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

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.stringToolkit.StringOperations;
import com.joey.software.timeingToolkit.EventTimer;
import com.joey.software.toolkit.KernalIO;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLFloatBuffer;
import com.nativelibs4java.opencl.CLIntBuffer;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.util.NIOUtils;

public class SpeedTesting
{
	public static void main(String input[]) throws Exception
	{

		int background = 0;

		float minVal = 1;
		float maxVal = 0;

		int wide = 2048;
		int high = 512;

		int ker = 1;
		int frm = 6;
		int frameCount = 5;

		String data = "";
		String hold = "";
		String result = "";
		boolean runGPU = false;
		for (int ik = 1; ik < 8; ik++)
		{

			ker = ik;

			for (int ir = 8; ir < 13; ir++)
			{
				System.out.println(ik + " : " + ir);

				wide = (int) Math.pow(2, ir);

				hold = "\n\n" + wide + "x" + high + "," + (2 * ker + 1) + ",\n";

				ImageFileSaver imageSaver = new ImageFileSaver(2, 10);

				// Corelation Size
				int sizeX = ker;
				int sizeY = ker;

				int imageVarNumber = frm;
				int meanThreshold = background;

				float min = minVal;
				float max = maxVal;

				final int imageWide = wide;
				final int imageHigh = high;
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
							.replace(src, "####IMAGE_NUM####", imageVarNumber
									+ "");

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
				}

				gpu.clear();
				cpu.clear();
				for (int imgKey = 0; imgKey < frameCount; imgKey += 1)
				{
					gpu.mark("Total");
					cpu.mark("Total");

					int imageKeyFrame = imgKey;
					int imageVarStart = imageKeyFrame + 1;

					cpu.mark("Load Frames");
					gpu.mark("Load Frames");
					// Get Rest of Frames
					int frameSize = inX * inY;

					for (int i = 0; i < imageInput.length; i++)
					{
						imageInput[i] = (int) (3000 * Math.random());
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

						// Ask for execution of the kernel with global size
						// =
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
						FloatBuffer statsHolder = NIOUtils
								.directFloats(imageWide * imageHigh
										* imageVarNumber, ByteOrder
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

					gpu.tick("Total");
					cpu.tick("Total");

					if (runGPU)
					{
						if (imgKey == 0)
						{
							hold += (gpu.getTitle()) + "\n";
						}

						hold += (gpu.getData()) + "\n";

					} else
					{
						if (imgKey == 0)
						{
							hold += (cpu.getTitle()) + "\n";
						}

						hold += (cpu.getData()) + "\n";
					}

					gpu.clear();
					cpu.clear();
				}

				data = CSVFileToolkit.joinDataRight(data, hold);
			}

			result = result + "\n" + data;
			data = "";
		}

		System.out.println(result);
		System.exit(0);
	}
}
