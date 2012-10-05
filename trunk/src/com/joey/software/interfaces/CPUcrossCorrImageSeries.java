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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.processors.CPUProcessor;


public class CPUcrossCorrImageSeries
{
	public static void main(String input[]) throws IOException
	{
		ImageFileSelectorPanel sel = new ImageFileSelectorPanel();
		FrameWaitForClose c = new FrameWaitForClose(FrameFactroy.getFrame(sel));
		c.waitForClose();
		
		File[] data = sel.getFiles();
		BufferedImage srcImg = ImageIO.read(data[0]);
		int imageCount = data.length;
		int imageWide = srcImg.getWidth();
		int frameHigh = srcImg.getHeight();
		int corrNum = 1;
		int threshold = 40;
		int frameSize = imageWide * frameHigh;
		int[] dataHolder = new int[(corrNum + 1) * frameSize];
		

		float[] out = new float[frameSize * (corrNum)];
		float[] min = new float[frameSize];
		float[] max = new float[frameSize];
		float[] avg = new float[frameSize];

		float[][] viewer = new float[imageWide][frameHigh];

		float minVal = -0.6f;
		float maxVal = 0.6f;

		DynamicRangeImage img = new DynamicRangeImage(viewer);
		FrameFactroy.getFrame(img);

		for (int frame = 0; frame < imageCount - corrNum; frame++)
		{
			System.out.println(frame);
			int dataHold[];
			getImageData(data, frame, dataHolder, 0);
			for (int i = 0; i < corrNum; i++)
			{
				getImageData(data, frame + i + 1, dataHolder, (i + 1)
						* frameSize);
			}
			CPUProcessor
					.crossCorr(dataHolder, imageWide, frameHigh, 3, 3, corrNum, threshold, out);

			CPUProcessor
					.doStats(out, imageWide, frameHigh, corrNum, avg, min, max);

			reshape(avg, viewer);
			img.setMinValue(-minVal);
			img.setMaxValue(-maxVal);
			img.updateImagePanel();
			ImageIO.write(img.getImage().getImage(), "png", new File("C:\\Users\\joey.enfield\\Desktop\\Data\\before\\ccOCT\\image"+frame+".png"));
		}
	}
	
	public static void getImageData(File[] data, int frame, int[] dataHold, int pos) throws IOException
	{
		BufferedImage img = ImageIO.read(data[frame]);
		for(int x = 0; x < img.getWidth(); x++)
		{
			for(int y = 0; y < img.getHeight(); y++)
			{
				dataHold[pos+ img.getHeight() * x + y]=ImageOperations.getGrayScale(img.getRGB(x, y));
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
