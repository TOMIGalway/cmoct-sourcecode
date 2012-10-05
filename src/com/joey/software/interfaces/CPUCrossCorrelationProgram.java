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


import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.joey.software.DataToolkit.ThorLabs2DImageProducer;
import com.joey.software.DataToolkit.ThorLabs3DImageProducer;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.processors.CPUProcessor;
import com.joey.software.threadToolkit.Task;


public class CPUCrossCorrelationProgram extends JPanel
{
	FileSelectionField inputFile = new FileSelectionField();

	CPUProcessor cpuProcessor = new CPUProcessor();

	JSpinner keyFrame = new JSpinner();

	JSpinner corrNumber = new JSpinner();

	ThorLabs3DImageProducer inputLoader;

	int[] srcData;

	public CPUCrossCorrelationProgram()
	{
		createJPanel();
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());

	}

	public static void processVolume(ThorLabs2DImageProducer inputData, int corrNum, int threshold)
			throws IOException, InterruptedException
	{
		int frameSize = inputData.getSizeX() * inputData.getSizeY();
		int[] dataHolderA = new int[(corrNum + 1) * frameSize];
		int[] dataHolderB = new int[(corrNum + 1) * frameSize];

		float[] out = new float[frameSize*(corrNum)];
		float[] min = new float[frameSize];
		float[] max = new float[frameSize];
		float[] avg = new float[frameSize];

		float[][] viewer = new float[inputData.getSizeX()][inputData.getSizeY()];

		BufferedImage corImage = ImageOperations
				.getBi(inputData.getSizeX(), inputData.getSizeY());
		BufferedImage structImage = ImageOperations
				.getBi(inputData.getSizeX(), inputData.getSizeY());

		float minVal = -0.6f;
		float maxVal = 0.6f;

		DynamicRangeImage img = new DynamicRangeImage(viewer);
		FrameFactroy.getFrame(img);
		// for (int frame = 0; frame < inputData.getImageCount() - corrNum;
		// frame+=11)
		// {
		int frame = 700;
		{
			System.out.println(frame);
			int dataHold[];

			if (frame == 700)
			{
				// If the first frame just load all the data

				inputData.getImageData(0, dataHolderA, 0);
				for (int i = 0; i < corrNum; i++)
				{
					System.out.println("I : " + i);
					inputData.getImageData(frame + i + 1, dataHolderA, (i + 1)
							* frameSize);
				}
				dataHold = dataHolderA;
			} else
			{
				if (frame % 2 == 0)
				{
					System.out.println("B -> A");
					System
							.arraycopy(dataHolderB, frameSize, dataHolderA, 0, corrNum
									* frameSize);

					inputData
							.getImageData(frame + corrNum, dataHolderA, (corrNum)
									* frameSize);

					dataHold = dataHolderA;

				} else
				{
					System.out.println("A -> B");
					System
							.arraycopy(dataHolderA, frameSize, dataHolderB, 0, corrNum
									* frameSize);

					inputData
							.getImageData(frame + corrNum, dataHolderB, (corrNum)
									* frameSize);

					dataHold = dataHolderB;
				}

			}
		
			CPUProcessor.crossCorr(dataHold, inputData.getSizeX(), inputData
					.getSizeY(), 4, 4, corrNum, threshold, out);
			
			CPUProcessor.doStats(out, inputData.getSizeX(), inputData
					.getSizeY(), corrNum, avg, min, max);

			reshape(avg, viewer);
			img.setMinValue(-minVal);
			img.setMaxValue(-maxVal);
			img.updateImagePanel();
		}

	}

	public static void main(String input[]) throws IOException,
			InterruptedException
	{
		File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\good experiment\\1 40 later - topo_3D_000.IMG");
		ThorLabs2DImageProducer loader = new ThorLabs2DImageProducer(f);

		processVolume(loader, 2, 30);
	}

	public static void directInts(int[] src, int wide, int high, int num, int[][] dst)
	{
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				dst[x][y] = src[num * high * wide + high * x + y];
			}
		}
	}

	public static void placeImage(int[] data, BufferedImage image, int pos)
	{
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{

				data[pos * image.getWidth() * image.getHeight() + x
						* image.getHeight() + y] = ImageOperations
						.getGrayScale(image.getRGB(x, y));
			}
		}
	}

	public static void reshape(float[] data, float[][] rst)
	{
		float val = 0;
		for (int x = 0; x < rst.length; x++)
		{
			for (int y = 0; y < rst[x].length; y++)
			{
				val = data[x * rst[x].length + y];
				if (Float.isInfinite(val) || Float.isNaN(val))
				{
					rst[x][y] = 0;
				} else
				{
					rst[x][y] = val;
				}

			}
			// System.arraycopy(data, x * rst.length, rst[x], 0, rst.length);
		}
	}
}

class CorrTask implements Task
{
	@Override
	public void doTask()
	{
		// TODO Auto-generated method stub

	}
}
