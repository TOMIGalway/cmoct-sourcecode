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
package com.joey.software.Presentation;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.VideoToolkit.BufferedImageStreamToAvi;
import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;


public class SmoothDepthMovieMaker
{
	

	public static void movieWithSmooth() throws IOException
	{
		File[] images = ImageFileSelectorPanel.getUserSelection();

		BufferedImage img = ImageIO.read(images[0]);

		float[][] data = new float[img.getWidth()][img.getHeight()];

		ImagePanel panel = new ImagePanel();
		FrameFactroy.getFrame(panel.getInPanel());

		float scale = 0.45f;
		BufferedImage rst = ImageOperations.getScaledImage(img, scale);
		Graphics2D g = rst.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);

		File[] avgFrames = new File[5];

		BufferedImageStreamToAvi out = new BufferedImageStreamToAvi(
				rst.getWidth(), rst.getHeight(), 10, "c:\\test\\",
				"amovie.avi", true, true);
		for (int i = 0; i < images.length - avgFrames.length; i++)
		{
			for (int j = 0; j < avgFrames.length; j++)
			{
				avgFrames[j] = images[i + j];
			}
			img = ImageOperations.getAverageImage(avgFrames);
			ImageOperations.grabPxlData(img, data, ImageOperations.PLANE_GRAY);
			FFT2Dtool tool = new FFT2Dtool(img.getWidth(), img.getHeight());
			tool.allocateMemory();
			tool.setRealData(data);
			tool.fftData();
			tool.fftFlip();
			tool.gaussianMask(120);
			tool.fftFlip();
			tool.ifftData(true);
			tool.getMagData(data);

			ImageOperations.getImage(data, 3, 230, ColorMap
					.getColorMap(ColorMap.TYPE_GLOW), img);

			g.drawImage(img, 0, 0, rst.getWidth(), rst.getHeight(), null);

			panel.setImage(rst);
			out.pushImage(rst);

			System.out.println(i);

		}

		out.finaliseVideo();
	}
}
