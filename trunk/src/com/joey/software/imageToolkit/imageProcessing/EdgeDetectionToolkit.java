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
package com.joey.software.imageToolkit.imageProcessing;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;

public class EdgeDetectionToolkit
{
	public static void main(String input[]) throws IOException
	{
		File in = new File(
				"C:\\Oct Data\\Micro Needles in Fore Arm\\Initial\\Raw Images\\image00005.jpg");

		BufferedImage image = ImageIO.read(in);

		ImageOperations.blurImage(image);
		FrameFactroy.getFrame(image, sobelEdgeDetection(image));
	}

	public static BufferedImage sobelEdgeDetection(BufferedImage img)
	{
		BufferedImage edged = new BufferedImage(img.getWidth(),
				img.getHeight(), BufferedImage.TYPE_INT_RGB);

		float[] hx = new float[]
		{ -1, 0, 1, -2, 0, 2, -1, 0, 1 };

		float[] hy = new float[]
		{ -1, -2, -1, 0, 0, 0, 1, 2, 1 };

		int[] rgbX = new int[3];
		int[] rgbY = new int[3];

		// ignore border pixels strategy
		for (int x = 1; x < img.getWidth() - 1; x++)
			for (int y = 1; y < img.getHeight() - 1; y++)
			{
				convolvePixel(hx, 3, 3, img, x, y, rgbX);
				convolvePixel(hy, 3, 3, img, x, y, rgbY);

				// instead of using sqrt function for eculidean distance
				// just do an estimation
				int r = Math.abs(rgbX[0]) + Math.abs(rgbY[0]);
				int g = Math.abs(rgbX[1]) + Math.abs(rgbY[1]);
				int b = Math.abs(rgbX[2]) + Math.abs(rgbY[2]);

				// range check
				if (r > 255)
					r = 255;
				if (g > 255)
					g = 255;
				if (b > 255)
					b = 255;

				edged.setRGB(x, y, (r << 16) | (g << 8) | b);
			}
		return edged;
	}

	private static int[] convolvePixel(float[] kernel, int kernWidth, int kernHeight, BufferedImage src, int x, int y, int[] rgb)
	{
		if (rgb == null)
			rgb = new int[3];

		int halfWidth = kernWidth / 2;
		int halfHeight = kernHeight / 2;

		/*
		 * this algorithm pretends as though the kernel is indexed from
		 * -halfWidthto halfWidth horizontally and -halfHeight to halfHeight
		 * vertically.This makes the center pixel indexed at row 0, column 0.
		 */

		for (int component = 0; component < 3; component++)
		{
			float sum = 0;
			for (int i = 0; i < kernel.length; i++)
			{
				int row = (i / kernWidth) - halfWidth; // current row in kernel
				int column = (i - (kernWidth * row)) - halfHeight; // current
				// column in
				// kernel

				// range check
				if (x - row < 0 || x - row > src.getWidth())
					continue;
				if (y - column < 0 || y - column > src.getHeight())
					continue;

				int srcRGB = src.getRGB(x - row, y - column);
				sum = sum + kernel[i]
						* ((srcRGB >> (16 - 8 * component)) & 0xff);
			}
			rgb[component] = (int) sum;
		}
		return rgb;
	}

}
