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
import java.nio.IntBuffer;


import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;
import com.joey.software.timeingToolkit.EventTimer;
import com.joey.software.toolkit.KernalIO;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLIntBuffer;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.util.NIOUtils;

public class CrossCorellationToolNew
{
	public static final String CL_SCRIPT = "CLScripts/crossCorrSingleFrame.cl";
	CrossCorrProgram owner = null;

	boolean gpuInUse = false;

	int[] imageInput;

	int[] imageFlow;

	byte[] imageStruct;

	float[] imageFlowFloat;

	public CrossCorellationToolNew(CrossCorrProgram owner)
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
			int imageWide = owner.getSizeX();
			int imageHigh = owner.getSizeY();

			int inX = imageWide;
			int inY = imageHigh;
			int inZ = 2;

			float min = (float) owner.getMinValue();
			float max = (float) owner.getMaxValue();

			int sizeX = owner.getCorKerSizeX();
			int sizeY = owner.getCorKerSizeY();
			// Ensure Memory Size is correct
			if (imageInput == null || imageInput.length != inX * inY * inZ)
			{
				imageInput = new int[inX * inY * inZ];
			}
			if (imageFlow == null || imageFlow.length != inY * inX)
			{
				imageFlow = new int[inY * inX];
			}
			if (imageStruct == null || imageStruct.length != inY * inX)
			{
				imageStruct = new byte[inY * inX];
			}
			if (imageFlowFloat == null || imageFlowFloat.length != inY * inX)
			{
				imageFlowFloat = new float[inY * inX];
			}

			int colorData[] = owner.flowMap.getRGBValues();
			
			EventTimer gpu = new EventTimer();
			EventTimer cpu = new EventTimer();

		
			
			ImageFileSaver imageSaver = new ImageFileSaver(2, 10);
			String[] data = FileOperations.splitFile(owner.file[0]);
	
			
			boolean saveStruct = owner.getSaveStruct();
			boolean saveFlow = owner.getSaveFlow();

			// Create Output Location
			String folder = data[0] + "\\" + data[1] + "\\" + sizeX + "-"
					+ sizeY + "-" + owner.getBackgroundThreshold() + "\\";
			File newF = new File(data[0] + "\\" + data[1] + "\\" + sizeX + "-"
					+ sizeY + "-" + owner.getBackgroundThreshold()+ "\\");
			newF.getParentFile().mkdirs();
			System.out.println(newF);
			newF.mkdirs();

			String base = "image";
			String format = "jpg";

			boolean runGPU = owner.processingMode.getSelectedIndex() == 0;

			CLContext context = null;
			CLPlatform platform = null;
			CLProgram program = null;
			CLKernel kernelCrossCorr = null;
			CLIntBuffer in = null;
			CLIntBuffer colorCL = null;
			CLIntBuffer resultCL = null;
			CLQueue queue = null;

			if (runGPU)
			{
				gpu.mark("Creating Kernal");
				 context = createBestContext();
				String src = KernalIO
						.readKernal(CL_SCRIPT);
				src = StringOperations
						.replace(src, "####GRID_SIZE####", (2 * sizeX + 1)
								* (2 * sizeY + 1) + "");

				src = StringOperations.replace(src, "####IMAGE_NUM####", 1 + "");

				 program = context.createProgram(src).build();
				 queue = context.createDefaultQueue();

				 kernelCrossCorr = program.createKernel("crossCorr");
				gpu.tick("Creating Kernal");

				gpu.mark("Allocating Memory");
				// Allocate OpenCL-hosted memory for inputs and output
				 in = context
						.createIntBuffer(CLMem.Usage.InputOutput, imageInput.length);

				 colorCL = context
						.createIntBuffer(CLMem.Usage.Input, colorData.length);

				 resultCL = context
						.createIntBuffer(CLMem.Usage.Output, imageWide * imageHigh);
				 
				//Sending the full color map
					IntBuffer colorMap = colorCL.map(queue, CLMem.MapFlags.Write);
					colorMap.put(colorData);
					colorCL.unmap(queue, colorMap);
				gpu.tick("Allocating Memory");
				gpu.printData();
			}

			
			
			
			gpu.clear();
			cpu.clear();
			owner.status.setMaximum(owner.inputData.getImageCount());
			for (int imgKey = 0; imgKey < owner.inputData.getImageCount(); imgKey += 1)
			{
				gpu.mark("Total");
				cpu.mark("Total");
				owner.status.setValue(imgKey);
				int imageKeyFrame = imgKey;
				int imageVarStart = imageKeyFrame + 1;

				// Get Rest of Frames
				int frameSize = inX * inY;
				gpu.mark("Loading Frames");
				cpu.mark("Loading Frames");
				owner.getImage(imageKeyFrame, imageInput, 0);
				owner.getImage(imageKeyFrame + 1, imageInput, inX * inY);
				gpu.tick("Loading Frames");
				cpu.tick("Loading Frames");

				gpu.mark("Processing");
				cpu.mark("Processing");
				if (runGPU)
				{
					gpu.mark("Filling Memory");
					IntBuffer inMap = in.map(queue, CLMem.MapFlags.Write);
					inMap.put(imageInput);
					in.unmap(queue, inMap);

					gpu.tick("Filling Memory");

					// Ask for execution of the kernel with global size =
					// dataSize
					// and workgroup size = 1

					gpu.mark("Kernal Running");
					kernelCrossCorr
							.setArgs(in, inX, inY, sizeX, sizeY, owner
									.getBackgroundThreshold(), colorCL, colorData.length, min, max, resultCL);
					kernelCrossCorr.enqueueNDRange(queue, new int[]
					{ inX, inY }, new int[]
					{ 8, 8 });
					queue.finish();
					gpu.tick("Kernal Running");

					gpu.mark("Grabbing Result");
					IntBuffer rstHolder = NIOUtils
							.directInts(imageWide * imageHigh, ByteOrder.nativeOrder());
					resultCL.read(queue, rstHolder, true);
					rstHolder.get(imageFlow);
					gpu.tick("Grabbing Result");
				} else
				{
					CPUCorrelationToolNew.crossCorr(imageInput, inX, inY, sizeX, sizeY, owner
							.getBackgroundThreshold(), colorData, colorData.length, min, max, imageFlow);
				}
				gpu.tick("Processing");
				cpu.tick("Processing");
				
				gpu.mark("Create Frame");
				cpu.mark("Create Frame");
				// Reload Frame
				owner.getImage(imageKeyFrame, imageStruct, 0);
				BufferedImage structImage = ImageOperations
						.getRotatedImage(ImageOperations
								.pixelsToImage(imageStruct, inY, inX), 1);
				
				ImageOperations.flipImage(structImage, ImageOperations.Y_AXIS);
				BufferedImage flowImage = ImageOperations
						.getRotatedImage(ImageOperations
								.pixelsToImage(imageFlow, inY, inX), 1);

				owner.structralPanel.setImage(structImage);
				owner.flowPanel.setImage(flowImage);
				gpu.tick("Create Frame");
				cpu.tick("Create Frame");
				
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

				gpu.mark("Total");
				cpu.mark("Total");
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

		int imageWide = owner.getSizeX();
		int imageHigh = owner.getSizeY();

		int inX = imageWide;
		int inY = imageHigh;
		int inZ = 2;

		float min = (float) owner.getMinValue();
		float max = (float) owner.getMaxValue();

		int sizeX = owner.getCorKerSizeX();
		int sizeY = owner.getCorKerSizeY();

		// Ensure Memory Size is correct
		if (imageInput == null || imageInput.length != inX * inY * inZ)
		{
			imageInput = new int[inX * inY * inZ];
		}

		if (imageFlow == null || imageFlow.length != inY * inX)
		{
			imageFlow = new int[inY * inX];
		}
		if (imageStruct == null || imageStruct.length != inY * inX)
		{
			imageStruct = new byte[inY * inX];
		}
		if (imageFlowFloat == null || imageFlowFloat.length != inY * inX)
		{
			imageFlowFloat = new float[inY * inX];
		}

		EventTimer gpu = new EventTimer();
		EventTimer cpu = new EventTimer();

		
		int imageKeyFrame = owner.getCurrentFrame();

		gpu.mark("Loading Frames");
		cpu.mark("Loading Frames");
		owner.getImage(imageKeyFrame, imageInput, 0);
		owner.getImage(imageKeyFrame + 1, imageInput, inX * inY);
		gpu.tick("Loading Frames");
		cpu.tick("Loading Frames");

		int colorData[] = owner.flowMap.getRGBValues();

		gpu.mark("Processing");
		cpu.mark("Processing");
		boolean runGPU = owner.processingMode.getSelectedIndex() == 0;
		if (runGPU)
		{
			gpu.mark("Creating Kernal");
			CLContext context = createBestContext();
			String src = KernalIO
					.readKernal(CL_SCRIPT);
			src = StringOperations
					.replace(src, "####GRID_SIZE####", (2 * sizeX + 1)
							* (2 * sizeY + 1) + "");

			src = StringOperations.replace(src, "####IMAGE_NUM####", 1 + "");

			CLProgram program = context.createProgram(src).build();
			CLQueue queue = context.createDefaultQueue();

			CLKernel kernelCrossCorr = program.createKernel("crossCorr");
			gpu.tick("Creating Kernal");

			gpu.mark("Allocating Memory");
			// Allocate OpenCL-hosted memory for inputs and output
			CLIntBuffer in = context
					.createIntBuffer(CLMem.Usage.InputOutput, imageInput.length);

			CLIntBuffer colorCL = context
					.createIntBuffer(CLMem.Usage.Input, colorData.length);

			CLIntBuffer resultCL = context
					.createIntBuffer(CLMem.Usage.Output, imageWide * imageHigh);
			gpu.tick("Allocating Memory");

			gpu.mark("Filling Memory");
			IntBuffer inMap = in.map(queue, CLMem.MapFlags.Write);
			inMap.put(imageInput);
			in.unmap(queue, inMap);

			IntBuffer colorMap = colorCL.map(queue, CLMem.MapFlags.Write);
			colorMap.put(colorData);
			colorCL.unmap(queue, colorMap);
			gpu.tick("Filling Memory");

			// Ask for execution of the kernel with global size = dataSize
			// and workgroup size = 1
			gpu.mark("Kernal Running");
			kernelCrossCorr
					.setArgs(in, inX, inY, sizeX, sizeY, owner
							.getBackgroundThreshold(), colorCL, colorData.length, min, max, resultCL);
			kernelCrossCorr.enqueueNDRange(queue, new int[]
			{ inX, inY }, new int[]
			{ 8, 8 });
			queue.finish();
			gpu.tick("Kernal Running");

			gpu.mark("Grabbing Result");
			IntBuffer rstHolder = NIOUtils
					.directInts(imageWide * imageHigh, ByteOrder.nativeOrder());
			resultCL.read(queue, rstHolder, true);
			rstHolder.get(imageFlow);
			gpu.tick("Grabbing Result");
		}
		else
		{
			CPUCorrelationToolNew.crossCorr(imageInput, inX, inY, sizeX, sizeY, owner
					.getBackgroundThreshold(), colorData, colorData.length, min, max, imageFlow);
		}
		gpu.tick("Processing");
		cpu.tick("Processing");
		
		if(runGPU)
		{
			gpu.printData();	
		}
		else
		{
			cpu.printData();	
		}
		
		
		
		// Reload Frame

		owner.getImage(imageKeyFrame, imageStruct, 0);
		BufferedImage structImage = ImageOperations
				.getRotatedImage(ImageOperations
						.pixelsToImage(imageStruct, inY, inX), 1);
		
		ImageOperations.flipImage(structImage, ImageOperations.Y_AXIS);
		BufferedImage flowImage = ImageOperations
				.getRotatedImage(ImageOperations
						.pixelsToImage(imageFlow, inY, inX), 1);

		owner.structralPanel.setImage(structImage);
		owner.flowPanel.setImage(flowImage);
	}

}
