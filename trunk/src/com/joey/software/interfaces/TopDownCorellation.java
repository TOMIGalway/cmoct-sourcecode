package com.joey.software.interfaces;

import static com.nativelibs4java.opencl.JavaCL.createBestContext;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;


import com.joey.software.DataToolkit.IMGDataSet;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.stringToolkit.StringOperations;
import com.joey.software.threadToolkit.Task;
import com.joey.software.threadToolkit.TaskMaster;
import com.joey.software.toolkit.KernalIO;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLFloatBuffer;
import com.nativelibs4java.opencl.CLIntBuffer;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.util.NIOUtils;


public class TopDownCorellation
{
	public static void main(String input[])
	{

		TaskMaster master = new TaskMaster(1, 10);

		Vector<File> inputFiles = new Vector<File>();
		File last = null;
		do
		{
			last = FileSelectionField.getUserFile();
			if (last != null)
			{
				inputFiles.add(last);
			}
		} while (last != null);
		File[] inputData = inputFiles.toArray(new File[0]);

		master.start();
		for (File f : inputData)
		{
			master.addTask(new ProcessData(f));
		}
	}
}


class ProcessData implements Task
{

	File f = null;

	public ProcessData(File f)
	{
		this.f = f;
	}

	@Override
	public void doTask()
	{
		try
		{
			ImageFileSaver imageSaver = new ImageFileSaver(2, 10);
			int size = 5;

			String[] data = FileOperations.splitFile(f);

			// Corelation Size
			int sizeX = size;
			int sizeY = size;

			int imageVarNumber = 1;
			int meanThreshold = 20;

			boolean saveStruct = true;
			boolean saveFlow = true;

			// Create Output Location
			String folder = data[0] + "\\" + data[1] + "\\" + size + "\\";
			File newF = new File(data[0] + "\\" + data[1] + "\\" + size + "\\");
			newF.getParentFile().mkdirs();
			System.out.println(newF);
			newF.mkdirs();

			String base = "TOP_SCAN";
			String format = "jpg";

			float min = 1f;
			float max = -1f;

			CLContext context = createBestContext();
			CLPlatform platform = context.getPlatform();
			CLDevice[] devices = platform.listAllDevices(true);

			IMGDataSet srcData = new IMGDataSet(f);
			srcData.reloadData();
			
			final int imageWide = srcData.getSizeDataX();
			final int imageHigh = srcData.getSizeDataY();
			int imageNum = imageVarNumber + 1;

			int inX = imageWide;
			int inY = imageHigh;
			int inZ = imageNum;

			final int[] imageInput = new int[inX * inY * inZ];

			float[] statsOutput = new float[imageWide * imageHigh
					* imageVarNumber];

			int[] imageFlow = new int[inY * inX];
			int[] imageStruct = new int[inY * inX];

			float[] imageFlowFloat = new float[inY * inX];

			String src = KernalIO.readKernal("CLScripts/crossCorr.cl");
			src = StringOperations
					.replace(src, "####GRID_SIZE####", (2 * sizeX + 1)
							* (2 * sizeY + 1) + "");

			src = StringOperations
					.replace(src, "####IMAGE_NUM####", imageVarNumber + "");

			CLProgram program = context.createProgram(src).build();
			CLQueue queue = context.createDefaultQueue();

			CLKernel kernelCrossCorr = program.createKernel("crossCorr");

			// Allocate OpenCL-hosted memory for inputs and output
			CLIntBuffer in = context
					.createIntBuffer(CLMem.Usage.InputOutput, imageInput.length);

			CLFloatBuffer statsHold = context
					.createFloatBuffer(CLMem.Usage.InputOutput, imageWide
							* imageHigh * imageVarNumber);

			ImagePanel structPanel = new ImagePanel();
			ImagePanel flowPanel = new ImagePanel();

			JTabbedPane tab = new JTabbedPane();
			tab.addTab("Struct", structPanel);
			tab.addTab("Flow", flowPanel);

			JFrame frame = FrameFactroy.getFrame(tab);
			frame.setTitle(f.toString());
			for (int imgKey = 180; imgKey < srcData.getSizeDataZ(); imgKey += 1)
			{
				int imageKeyFrame = imgKey;
				int imageVarStart = imageKeyFrame + 1;

				// Get Rest of Frames
				int frameSize = inX * inY;
				
				byte value = 0;
				int z = imageKeyFrame;
				for(int x= 0; x < imageWide; x++)
				{
					for(int y = 0; y < imageHigh; y++)
					{
						value = srcData.getPreviewData()[x][y][z];
						if(value < 0)
						{
							imageInput[x*imageHigh+y] = value+256;
						}
						else
						{
							imageInput[x*imageHigh+y] = value;
						}
					}
				}
				
				for (int i = 0; i < imageNum - 1; i++)
				{
					z = imageVarStart+i;
					for(int x= 0; x < imageWide; x++)
					{
						for(int y = 0; y < imageHigh; y++)
						{
							value = srcData.getPreviewData()[x][y][z];
							if(value < 0)
							{
								imageInput[(i+1)*frameSize+x*imageHigh+y] = value+256;
							}
							else
							{
								imageInput[(i+1)*frameSize+x*imageHigh+y] = value;
							}
							
						}
					}
				}

				IntBuffer inMap = in.map(queue, CLMem.MapFlags.Write);
				inMap.put(imageInput);
				in.unmap(queue, inMap);

				// Ask for execution of the kernel with global size = dataSize
				// and workgroup size = 1
				kernelCrossCorr
						.setArgs(in, inX, inY, sizeX, sizeY, imageVarNumber, meanThreshold, statsHold);
				kernelCrossCorr.enqueueNDRange(queue, new int[]
				{ inX, inY, imageVarNumber }, new int[]
				{ 8, 8, 1 });
				queue.finish();

				FloatBuffer statsHolder = NIOUtils.directFloats(imageWide
						* imageHigh * imageVarNumber, ByteOrder.nativeOrder());
				statsHold.read(queue, statsHolder, true);
				statsHolder.get(statsOutput);

				// CPUVersion.crossCorr(imageInput, inX, inY, sizeX, sizeY,
				// imageVarNumber, meanThreshold, statsOutput);
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

				structPanel.setImage(structImage);
				flowPanel.setImage(flowImage);

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

			}
			frame.setVisible(false);
			frame.dispose();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}