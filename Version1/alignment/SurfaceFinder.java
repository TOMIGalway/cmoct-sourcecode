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
package com.joey.software.MultiThreadCrossCorrelation.alignment;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTabbedPane;

import com.joey.software.MultiThreadCrossCorrelation.threads.CrossCorrelationTool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;


public class SurfaceFinder
{

	public static void main(String input[]) throws IOException
	{

		BufferedImage imgA = ImageIO.read(new File("c:\\test\\frameA.png"));
		BufferedImage imgB = ImageIO.read(new File("c:\\test\\frameB.png"));
		BufferedImage rst = ImageOperations
				.cloneImage(imgA, BufferedImage.TYPE_INT_ARGB);

		imgA = ImageOperations.cloneImage(imgA, BufferedImage.TYPE_INT_ARGB);
		imgB = ImageOperations.cloneImage(imgB, BufferedImage.TYPE_INT_ARGB);

		float[] peakPosA = new float[imgA.getWidth()];
		float[] peakPosB = new float[imgB.getWidth()];
		float[] shift = new float[imgA.getWidth()];

		float surfThreshold = 230;
		if (false)
		{
			estimateSurfaceThreshold(imgA, peakPosA, surfThreshold);
			estimateSurfaceThreshold(imgB, peakPosB, surfThreshold);
		} else
		{
			estimateSurfacePeak(imgA, peakPosA);
			estimateSurfacePeak(imgB, peakPosB);
		}

		int smooth = 1;
		//drawSurface(imgA, peakPosA, 1, Color.RED);
		smoothMean(peakPosA, smooth);
		//drawSurface(imgA, peakPosA, 1, Color.CYAN);

		//drawSurface(imgB, peakPosB, 1, Color.RED);
		smoothMean(peakPosB, smooth);
		//drawSurface(imgB, peakPosB, 1, Color.CYAN);

		//getDiff(peakPosA, peakPosB, shift);
		shiftImage(imgB, rst, shift);

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Image A", new ImagePanel(imgA).getInPanel());
		tab.addTab("Image B", new ImagePanel(imgB).getInPanel());
		tab.addTab("Rst", new ImagePanel(rst).getInPanel());

		FrameFactroy.getFrame(tab);
		
		int ker = 3;
		int threshold = 50;
		byte[][] frameA = getImage(imgA);
		byte[][] frameB = getImage(rst);
		short[][] result = new short[frameA.length][frameA[0].length];
		CrossCorrelationTool.manualCrossCorr(frameA, frameB, ker, ker, threshold, result);
		DynamicRangeImage dri = new DynamicRangeImage(result);
		dri.setMinValue(Short.MAX_VALUE*0.6f);
		dri.setMaxValue(-Short.MAX_VALUE*0.6f);
		dri.updateImagePanel();
		
		
		FrameFactroy.getFrame(dri);
	}

	public static void smoothMean(float[] data, int ker)
	{
		float val = 0;
		int count = 0;
		float[] src = data.clone();
		for (int i = 0; i < data.length; i++)
		{
			count = 0;
			val = 0;

			for (int j = i - ker; j <= i + ker; j++)
			{
				if (j >= 0 && j < data.length)
				{
					val += src[j];
					count++;
				}
			}
			data[i] = val / count;
		}
	}

	/**
	 * Perform smoothing on the given dataset
	 * using a fft with a high freq block
	 * 
	 * @param data
	 * @param block
	 */
	public static void smoothFFT(float[] data, int block)
	{
		float[] hold = new float[data.length * 2];

		for (int i = 0; i < data.length; i++)
		{
			hold[2 * i] = data[i];
		}

		// FFT
		FloatFFT_1D fft = new FloatFFT_1D(data.length);
		fft.complexForward(hold);

		// Smooth

		for (int i = -2 * block; i <= 2 * block; i++)
		{
			hold[data.length + i] = 0;
		}

		// Ifft
		fft.complexInverse(hold, true);

		// Getback data
		for (int i = 0; i < data.length; i++)
		{
			data[i] = (float) Math.sqrt(hold[2 * i] * hold[2 * i]
					+ hold[2 * i + 1] * hold[2 * i + 1]);
		}
	}

	public static void getDiff(float[] dataA, float[] dataB, float[] rst)
	{
		for (int i = 0; i < dataA.length; i++)
		{
			rst[i] = dataB[i] - dataA[i];
		}
	}

	public static void shiftImage(BufferedImage src, BufferedImage rst, float[] shift)
	{
		int yP = 0;
		for (int x = 0; x < src.getWidth(); x++)
		{
			for (int y = 0; y < src.getHeight(); y++)
			{
				yP = (int) (y + shift[x]);
				if (yP < rst.getHeight() && yP >= 0)
				{
					rst.setRGB(x, y, src.getRGB(x, yP));
				} else
				{
					rst.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
	}

	public static void drawSurface(BufferedImage img, float[] peakPos, float wide, Color c)
	{
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = (int) (peakPos[x] - wide); y <= peakPos[x] + wide
					&& y < img.getHeight() && y >= 0; y++)
			{
				img.setRGB(x, y, c.getRGB());
			}
		}
	}

	public static void estimateSurfacePeak(BufferedImage img, float[] peakPos)
	{
		float max = 0;
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				if (y == 0)
				{
					max = ImageOperations.getGrayScale(img.getRGB(x, y));
				} else
				{
					if (ImageOperations.getGrayScale(img.getRGB(x, y)) > max)
					{
						max = ImageOperations.getGrayScale(img.getRGB(x, y));
						peakPos[x] = y;
					}
				}

			}
		}
	}

	public static void estimateSurfaceThreshold(BufferedImage img, float[] peakPos, float threshold)
	{
		float val = 0;
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				val = ImageOperations.getGrayScale(img.getRGB(x, y));

				if (val > threshold)
				{
					peakPos[x] = y;
					y = img.getHeight();// Break y
										// loop
				}
			}
		}
	}
	
	public static byte[][] getImage(BufferedImage img)
	{
		byte[][] file = new byte[img.getWidth()][img.getHeight()];
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				file[x][y] = (byte)ImageOperations.getGrayScale(img.getRGB(x, y));
			}
		}
		return file;
	}
}
