/*******************************************************************************
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
package com.joey.software.interfaces;

import static com.nativelibs4java.opencl.JavaCL.createBestContext;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;
import com.joey.software.timeingToolkit.EventTimer;
import com.joey.software.toolkit.KernalIO;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLFloatBuffer;
import com.nativelibs4java.opencl.CLIntBuffer;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.util.NIOUtils;

public class CrossCorellationTool
{
	CrossCorrProgram owner = null;

	boolean gpuInUse = false;

	int[] imageInput;

	float[] statsOutput;

	int[] imageFlow;

	int[] imageStruct;

	float[] imageFlowFloat;

	public CrossCorellationTool(CrossCorrProgram owner)
	{
		this.owner = owner;
	}

	public void doFullVolume() throws Exception
	{
		owner.lockOutUser(true);
		if (owner.file == null)
		{
			owner.lockOutUser(false);
			return;
		}
		try
		{

			ImageFileSaver imageSaver = new ImageFileSaver(2, 10);

			String[] data = FileOperations.splitFile(owner.file[0]);

			// Corelation Size
			int sizeX = owner.getCorKerSizeX();
			int sizeY = owner.getCorKerSizeY();

			int imageVarNumber = owner.getCorFrameNum();
			int meanThreshold = owner.getBackgroundThreshold();

			boolean saveStruct = owner.getSaveStruct();
			boolean saveFlow = owner.getSaveFlow();

			// Create Output Location
			String folder = data[0] + "\\" + data[1] + "\\" + sizeX + "-"
					+ sizeY + "-" + imageVarNumber + "\\";
			File newF = new File(data[0] + "\\" + data[1] + "\\" + sizeX + "-"
					+ sizeY + "-" + imageVarNumber + "\\");
			newF.getParentFile().mkdirs();
			System.out.println(newF);
			newF.mkdirs();

			String base = "image";
			String format = "jpg";

			float min = (float) owner.getMinValue();
			float max = (float) owner.getMaxValue();

			ImageProducer imageIn = owner.inputData;

			BufferedImage tmp = imageIn.getImage(0);

			final int imageWide = tmp.getWidth();
			final int imageHigh = tmp.getHeight();
			int imageNum = imageVarNumber + 1;

			tmp = null;
			int inX = imageWide;
			int inY = imageHigh;
			int inZ = imageNum;

			final int[] imageInput = new int[inX * inY * inZ];

			float[] statsOutput = new float[imageWide * imageHigh
					* imageVarNumber];

			int[] imageFlow = new int[inY * inX];
			int[] imageStruct = new int[inY * inX];

			float[] imageFlowFloat = new float[inY * inX];

			boolean runGPU = owner.processingMode.getSelectedIndex() == 0;

			EventTimer gpu = new EventTimer();
			EventTimer cpu = new EventTimer();

			CLContext context = null;
			CLPlatform platform = null;
			CLProgram program = null;
			CLKernel kernelCrossCorr = null;
			CLIntBuffer in = null;
			CLFloatBuffer statsHold = null;
			CLQueue queue = null;

			if (runGPU)
			{
				gpu.mark("Create Kernal");
				context = createBestContext();
				platform = context.getPlatform();

				String src = KernalIO.readKernal("CLScripts/crossCorr.cl");
				src = StringOperations
						.replace(src, "####GRID_SIZE####", (2 * sizeX + 1)
								* (2 * sizeY + 1) + "");

				src = StringOperations
						.replace(src, "####IMAGE_NUM####", imageVarNumber + "");

				program = context.createProgram(src).build();
				queue = context.createDefaultQueue();

				kernelCrossCorr = program.createKernel("crossCorr");

				gpu.tick("Create Kernal");

				gpu.mark("Allocate gpu memory");
				// Allocate OpenCL-hosted memory for inputs and output
				in = context
						.createIntBuffer(CLMem.Usage.InputOutput, imageInput.length);

				statsHold = context
						.createFloatBuffer(CLMem.Usage.InputOutput, imageWide
								* imageHigh * imageVarNumber);
				gpu.tick("Allocate gpu memory");
				gpu.printData();
			}

			gpu.clear();
			cpu.clear();
			owner.status.setMaximum(imageIn.getImageCount());
			for (int imgKey = 0; imgKey < imageIn.getImageCount(); imgKey += 1)
			{
				gpu.mark("Total");
				cpu.mark("Total");
				owner.status.setValue(imgKey);
				int imageKeyFrame = imgKey;
				int imageVarStart = imageKeyFrame + 1;

				cpu.mark("Load Frames");
				gpu.mark("Load Frames");
				// Get Rest of Frames
				int frameSize = inX * inY;
				owner.getImage(imageKeyFrame, imageInput, 0);
				for (int i = 0; i < imageNum - 1; i++)
				{
					owner.getImage(i + imageVarStart, imageInput, (i + 1) * frameSize);
				}

				cpu.tick("Load Frames");
				gpu.tick("Load Frames");

				if (runGPU)
				{
					gpu.mark("Sending Data");
					IntBuffer inMap = in.map(queue, CLMem.MapFlags.Write);
					inMap.put(imageInput);
					in.unmap(queue, inMap);
					gpu.tick("Sending Data");

					// Ask for execution of the kernel with global size =
					// dataSize
					// and workgroup size = 1

					gpu.mark("Processing");
					kernelCrossCorr
							.setArgs(in, inX, inY, sizeX, sizeY, imageVarNumber, meanThreshold, statsHold);
					kernelCrossCorr.enqueueNDRange(queue, new int[]
					{ inX, inY, imageVarNumber }, new int[]
					{ 8, 8, 1 });
					queue.finish();
					gpu.tick("Processing");

					gpu.mark("Grab Data");
					FloatBuffer statsHolder = NIOUtils.directFloats(imageWide
							* imageHigh * imageVarNumber, ByteOrder
							.nativeOrder());
					statsHold.read(queue, statsHolder, true);
					statsHolder.get(statsOutput);
					gpu.tick("Grab Data");
				} else
				{
					cpu.mark("Processing");
					CPUVersion
							.crossCorr(imageInput, inX, inY, sizeX, sizeY, imageVarNumber, meanThreshold, statsOutput);
					cpu.tick("Processing");
				}
				int pixLoc = 0;
				for (int xP = 0; xP < inX; xP++)
				{
					for (int yP = 0; yP < inY; yP++)
					{
						pixLoc = yP * inX + xP;
						for (int i = 0; i < imageVarNumber; i++)
						{
							imageFlowFloat[pixLoc] += statsOutput[i * inX * inY
									+ xP * inY + yP]
									/ imageVarNumber;
						}

						imageFlowFloat[pixLoc] = (imageFlowFloat[pixLoc] - min)
								/ (max - min);

						if (imageFlowFloat[pixLoc] > 1)
						{
							imageFlowFloat[pixLoc] = 1;
						}

						if (imageFlowFloat[pixLoc] < 0)
						{
							imageFlowFloat[pixLoc] = 0;
						}

						imageFlow[pixLoc] = (int) (255 * imageFlowFloat[pixLoc]);
						imageFlow[pixLoc] = ImageOperations
								.getGrayRGB(imageFlow[pixLoc]);
						imageStruct[pixLoc] = ImageOperations
								.getGrayRGB(imageInput[pixLoc]);
					}
				}

				BufferedImage structImage = ImageOperations
						.getRotatedImage(ImageOperations
								.pixelsToImage(imageStruct, inY, inX), 1);
				ImageOperations.flipImage(structImage, ImageOperations.Y_AXIS);
				BufferedImage flowImage = ImageOperations
						.pixelsToImage(imageFlow, inX, inY);

				gpu.tick("Total");
				cpu.tick("Total");

				owner.structralPanel.setImage(structImage);
				owner.flowPanel.setImage(flowImage);

				cpu.mark("Save Images");
				gpu.mark("Save Images");
				if (saveStruct)
				{
					BufferedImage struct = new BufferedImage(inX, inY,
							BufferedImage.TYPE_INT_RGB);
					struct.createGraphics().drawImage(structImage, null, null);

					imageSaver.addData(new File(folder + "Struct_" + base
							+ StringOperations.getNumberString(5, imgKey) + "."
							+ format), struct);
				}

				if (saveFlow)
				{
					BufferedImage flow = new BufferedImage(inX, inY,
							BufferedImage.TYPE_INT_RGB);
					flow.createGraphics().drawImage(flowImage, null, null);

					imageSaver.addData(new File(folder + "Flow_" + base
							+ StringOperations.getNumberString(5, imgKey) + "."
							+ format), flow);
				}
				cpu.tick("Save Images");
				gpu.tick("Save Images");

				if (runGPU)
				{
					if (imgKey == 0)
					{
						System.out.println(gpu.getTitle());
					}

					System.out.println(gpu.getData());

				} else
				{
					if (imgKey == 0)
					{
						System.out.println(cpu.getTitle());
					}

					System.out.println(cpu.getData());
				}

				gpu.clear();
				cpu.clear();
			}
		} catch (Exception e)
		{
			owner.lockOutUser(false);
			throw e;
		}
		owner.lockOutUser(false);
	}

	public void doSingleFrame() throws IOException, CLBuildException
	{
		int imageVarNumber = owner.getCorFrameNum();
		int imageWide = owner.getSizeX();
		int imageHigh = owner.getSizeY();
		int imageNum = imageVarNumber + 1;

		int inX = imageWide;
		int inY = imageHigh;
		int inZ = imageNum;

		int sizeX = owner.getCorKerSizeX();
		int sizeY = owner.getCorKerSizeY();

		// Ensure Memory Size is correct
		if (imageInput == null || imageInput.length != inX * inY * inZ)
		{
			imageInput = new int[inX * inY * inZ];
		}
		if (statsOutput == null
				|| statsOutput.length != imageWide * imageHigh * imageVarNumber)
		{
			statsOutput = new float[imageWide * imageHigh * imageVarNumber];
		}
		if (imageFlow == null || imageFlow.length != inY * inX)
		{
			imageFlow = new int[inY * inX];
		}
		if (imageStruct == null || imageStruct.length != inY * inX)
		{
			imageStruct = new int[inY * inX];
		}
		if (imageFlowFloat == null || imageFlowFloat.length != inY * inX)
		{
			imageFlowFloat = new float[inY * inX];
		}

		EventTimer gpu = new EventTimer();
		EventTimer cpu = new EventTimer();

		int imageKeyFrame = owner.getCurrentFrame();
		int imageVarStart = imageKeyFrame + 1;

		gpu.mark("Loading Frames");
		cpu.mark("Loading Frames");
		// Get Rest of Frames
		int frameSize = inX * inY;
		owner.getImage(imageKeyFrame, imageInput, 0);
		for (int i = 0; i < imageNum - 1; i++)
		{
			owner.getImage(i + imageVarStart, imageInput, (i + 1) * frameSize);
		}
		gpu.tick("Loading Frames");
		cpu.tick("Loading Frames");

		boolean runGPU = owner.processingMode.getSelectedIndex() == 0;
		if (runGPU)
		{
			gpu.mark("Total");
			gpu.mark("Creating Kernal");
			CLContext context = createBestContext();
			String src = KernalIO.readKernal("CLScripts/crossCorr.cl");
			src = StringOperations
					.replace(src, "####GRID_SIZE####", (2 * sizeX + 1)
							* (2 * sizeY + 1) + "");

			src = StringOperations
					.replace(src, "####IMAGE_NUM####", imageVarNumber + "");

			CLProgram program = context.createProgram(src).build();
			CLQueue queue = context.createDefaultQueue();

			CLKernel kernelCrossCorr = program.createKernel("crossCorr");
			gpu.tick("Creating Kernal");

			gpu.mark("Allocating Memory");
			// Allocate OpenCL-hosted memory for inputs and output
			CLIntBuffer in = context
					.createIntBuffer(CLMem.Usage.InputOutput, imageInput.length);

			CLFloatBuffer statsHold = context
					.createFloatBuffer(CLMem.Usage.InputOutput, imageWide
							* imageHigh * imageVarNumber);
			gpu.tick("Allocating Memory");

			gpu.mark("Filling Memory");
			IntBuffer inMap = in.map(queue, CLMem.MapFlags.Write);
			inMap.put(imageInput);
			in.unmap(queue, inMap);
			gpu.tick("Filling Memory");

			// Ask for execution of the kernel with global size = dataSize
			// and workgroup size = 1
			gpu.mark("Processing");
			kernelCrossCorr
					.setArgs(in, inX, inY, sizeX, sizeY, imageVarNumber, owner
							.getBackgroundThreshold(), statsHold);
			kernelCrossCorr.enqueueNDRange(queue, new int[]
			{ inX, inY, imageVarNumber }, new int[]
			{ 8, 8, 1 });
			queue.finish();
			gpu.tick("Processing");

			gpu.mark("Grabbing Memory");
			FloatBuffer statsHolder = NIOUtils.directFloats(imageWide
					* imageHigh * imageVarNumber, ByteOrder.nativeOrder());
			statsHold.read(queue, statsHolder, true);
			statsHolder.get(statsOutput);
			gpu.tick("Grabbing Memory");
			gpu.tick("Total");
		} else
		{
			cpu.mark("Total");
			CPUVersion
					.crossCorr(imageInput, inX, inY, sizeX, sizeY, imageVarNumber, owner
							.getBackgroundThreshold(), statsOutput);
			cpu.tick("Total");
		}
		if (runGPU)
		{
			gpu.printData();
		} else
		{
			cpu.printData();
		}
		int pixLoc = 0;
		for (int xP = 0; xP < inX; xP++)
		{
			for (int yP = 0; yP < inY; yP++)
			{
				pixLoc = yP * inX + xP;
				imageFlowFloat[pixLoc] = 0;
				for (int i = 0; i < imageVarNumber; i++)
				{
					imageFlowFloat[pixLoc] += statsOutput[i * inX * inY + xP
							* inY + yP]
							/ imageVarNumber;
				}

				imageFlowFloat[pixLoc] = (float) ((imageFlowFloat[pixLoc] - owner
						.getMinValue()) / (owner.getMaxValue() - owner
						.getMinValue()));

				if (imageFlowFloat[pixLoc] > 1)
				{
					imageFlowFloat[pixLoc] = 1;
				}

				if (imageFlowFloat[pixLoc] < 0)
				{
					imageFlowFloat[pixLoc] = 0;
				}

				imageFlow[pixLoc] = (int) (255 * imageFlowFloat[pixLoc]);
				imageFlow[pixLoc] = ImageOperations
						.getGrayRGB(imageFlow[pixLoc]);
				imageStruct[pixLoc] = ImageOperations
						.getGrayRGB(imageInput[pixLoc]);
			}
		}

		BufferedImage structImage = ImageOperations
				.getRotatedImage(ImageOperations
						.pixelsToImage(imageStruct, inY, inX), 1);
		ImageOperations.flipImage(structImage, ImageOperations.Y_AXIS);
		BufferedImage flowImage = ImageOperations
				.pixelsToImage(imageFlow, inX, inY);

		owner.structralPanel.setImage(structImage);
		owner.flowPanel.setImage(flowImage);
	}
}

class CPUCorrelationTool
{
	public static int getAverage(int data[], int length)
	{
		if (length == 0)
		{
			return 0;
		}
		int value = 0;
		for (int i = 0; i < length; i++)
		{
			value += data[i];
		}
		value = value / length;
		return value;
	}

	public static float getMaxFloat(float[] data, int length)
	{
		if (length == 0)
		{
			return 0;
		}
		float val = data[0];
		for (int i = 0; i < length; i++)
		{
			if (data[i] > val)
			{
				val = data[i];
			}
		}
		return val;
	}

	public static float getMinFloat(float[] data, int length)
	{
		if (length == 0)
		{
			return 0;
		}
		float val = data[0];
		for (int i = 0; i < length; i++)
		{
			if (data[i] < val)
			{
				val = data[i];
			}
		}
		return val;

	}

	public static float getAverageFloat(float[] data, int length)
	{
		if (length == 0)
		{
			return 0;
		}

		float value = 0;
		for (int i = 0; i < length; i++)
		{
			value += data[i];
		}
		value = value / length;

		return value;
	}

	public static float getCrossCorr(int[] gridA, int[] gridB, int wide, int high, int threshold)
	{
		int x, y;
		float avgA = getAverage(gridA, wide * high);
		float avgB = getAverage(gridB, wide * high);

		if (avgA < threshold || avgB < threshold)
		{
			return 1;
		}

		float tA = 0;
		float tB = 0;
		float tC = 0;

		float t1 = 0;
		float t2 = 0;
		for (x = 0; x < wide; x++)
		{
			for (y = 0; y < high; y++)
			{
				t1 = gridA[x * high + y] - avgA;
				t2 = gridB[x * high + y] - avgB;
				tA += t1 * t2;
				tB += t1 * t1;
				tC += t2 * t2;
			}
		}

		if (tB == 0 || tC == 0)
		{
			return 1;
		}
		return (float) (tA / Math.sqrt(tB * tC));
	}

	public static void crossCorr(int[] inputData, int inX, int inY, int sizeX, int sizeY, int imgNum, int threshold, float[] out)
	{
		for (int xP = 0; xP < inX; xP++)
		{
			for (int yP = 0; yP < inY; yP++)
			{
				for (int img = 0; img < imgNum; img++)
				{

					int imgB = img + 1;
					int imgA = 0;

					int gridA[] = new int[(2 * sizeX + 1) * (2 * sizeY + 1)];
					int gridB[] = new int[(2 * sizeX + 1) * (2 * sizeY + 1)];

					int pxlCount;

					pxlCount = 0;
					for (int x = xP - sizeX; x <= xP + sizeX; x++)
					{
						for (int y = yP - sizeY; y <= yP + sizeY; y++)
						{
							if (x < inX && y < inY && x >= 0 && y >= 0)
							{
								gridA[pxlCount] = inputData[imgA * inX
										* inY + inY * x + y];
								gridB[pxlCount] = inputData[imgB * inX
										* inY + inY * x + y];
							} else
							{
								gridA[pxlCount] = 0;
								gridB[pxlCount] = 0;
							}
							pxlCount++;
						}
					}
					out[(imgB - 1) * inX * inY + inY * xP + yP] = getCrossCorr(gridA, gridB, 2 * sizeX + 1, 2 * sizeY + 1, threshold);
				}
			}
		}
	}

	public static void doStats(float[] inputData, int inX, int inY, int imgNum, float[] avg, float[] min, float[] max)
	{
		for (int xP = 0; xP < inX; xP++)
		{
			for (int yP = 0; yP < inY; yP++)
			{

				float avgVal = 0;
				float maxVal = 0;
				float minVal = 0;

				float value = 0;
				for (int i = 0; i < imgNum; i++)
				{
					value = inputData[i * inX * inY + inY * xP + yP];

					avgVal += value;
					if (i == 0)
					{
						maxVal = value;
						minVal = value;
					} else
					{
						if (value > maxVal)
						{
							maxVal = value;
						}

						if (value < minVal)
						{
							minVal = value;
						}
					}
				}

				avg[inY * xP + yP] = avgVal / imgNum;
				min[inY * xP + yP] = maxVal;
				max[inY * xP + yP] = minVal;
			}
		}
	}

}
