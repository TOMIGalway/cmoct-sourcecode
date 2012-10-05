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
package com.joey.software.gradient;


import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.Projections.surface.SurfaceFinderTool;
import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;


public class GradientTesting
{
	public static void main(String[] data) throws IOException
	{
		//seeDifferentStuff();
		findSurface();
	}
	public static void findSurface() throws IOException
	{

		int frameNumber = 100;
		float blurSigmaX = 20;
		float blurSigmaY = 20;
		
			File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\after_3D_000.IMG");
		ThorlabsIMGImageProducer loader = new ThorlabsIMGImageProducer(f, true);

		int wide = loader.getSizeX();
		int high = loader.getSizeY();
		byte[][] loadHolder = new byte[high][wide];
		float[][] frame = new float[wide][high];
		float[][] blured = new float[wide][high];
		byte[][] peak = new byte[wide][high];

		FFT2Dtool tool = new FFT2Dtool(wide, high);
		tool.allocateMemory();

		
		DynamicRangeImage framePanel = new DynamicRangeImage(frame);
		DynamicRangeImage blurPanel = new DynamicRangeImage(blured);
		DynamicRangeImage peakPanel = new DynamicRangeImage(peak);

		framePanel.setRange(0, 255);
		blurPanel.setRange(0, 255);
		FrameFactroy
				.getFrameTabs(framePanel, blurPanel, peakPanel);

		/**
		 * Load Each frame and then perform the
		 * blur
		 */

		 for (frameNumber = 0; frameNumber <
		 loader.getImageCount(); frameNumber++)
		{
			// Load frame
			loader.getImage(frameNumber, loadHolder);
			ArrayToolkit.transpose(loadHolder, frame);
			framePanel.updateImagePanel();

			// Blur and Edge fftframe B
			tool.setRealData(frame);
			tool.gaussianBlur(blurSigmaX,blurSigmaY, true);
			tool.getMagData(blured);
			
			blurPanel.updateImagePanel();

			ArrayToolkit.setValue(peak,(byte) 0);
			// For Each A-scan generate slope
			for (int x = 0; x < frame.length; x++)
			{
				findSurface(blured[x], peak[x], 1, 10);
			}
			peakPanel.updateImagePanel();
		}
	
	}
	public static void seeDifferentStuff() throws IOException
	{
		int frameNumber = 100;
		float blurSigma = 50;

		int blockX = (int) (blurSigma / 4);
		int blockY = (int) (blurSigma / 4);

		File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\after_3D_000.IMG");
		ThorlabsIMGImageProducer loader = new ThorlabsIMGImageProducer(f, true);

		int wide = loader.getSizeX();
		int high = loader.getSizeY();
		byte[][] loadHolder = new byte[high][wide];
		float[][] frame = new float[wide][high];
		float[][] blurEdge = new float[wide][high];

		float[][] slope = new float[wide][high];
		float[][] slopeSlope = new float[wide][high];
		byte[][] peak = new byte[wide][high];

		FFT2Dtool tool = new FFT2Dtool(wide, high);
		tool.allocateMemory();

		float[][] mask = ArrayToolkit.clone(tool.getMask());
		// Create MASK
		FFT2Dtool.createGaussianMask(mask, blurSigma,blurSigma);
		ArrayToolkit.setValue(mask, new Ellipse2D.Float(mask.length / 2
				- blockX, mask[0].length / 2 - blockY, 2 * blockX + 1,
				2 * blockY + 1), 0, true);
		FFT2Dtool.performFFTFlip(mask);

		DynamicRangeImage maskPanel = new DynamicRangeImage(mask);
		DynamicRangeImage framePanel = new DynamicRangeImage(frame);
		DynamicRangeImage blurEdgePanel = new DynamicRangeImage(blurEdge);
		DynamicRangeImage slopePanel = new DynamicRangeImage(slope);
		DynamicRangeImage slopeSlopePanel = new DynamicRangeImage(slopeSlope);
		DynamicRangeImage peakPanel = new DynamicRangeImage(peak);

		framePanel.setRange(0, 255);
		maskPanel.updateMaxMin();
		maskPanel.updateImagePanel();
		FrameFactroy
				.getFrameTabs(framePanel, maskPanel, blurEdgePanel, slopePanel, slopeSlopePanel, peakPanel);

		/**
		 * Load Each frame and then perform the
		 * blur
		 */

		 for (frameNumber = 0; frameNumber <
		 loader.getImageCount(); frameNumber++)
		{
			// Load frame
			loader.getImage(frameNumber, loadHolder);
			ArrayToolkit.transpose(loadHolder, frame);
			framePanel.updateImagePanel();

			// Blur and Edge fftframe B
			tool.setRealData(frame);
			tool.gaussianBlur(blurSigma, true);
			tool.fftData();
			//tool.mull(mask);
			tool.ifftData(true);
			tool.getMagData(blurEdge);
			blurEdgePanel.updateMaxMin();
			blurEdgePanel.updateImagePanel();

			ArrayToolkit.setValue(peak,(byte) 0);
			// For Each A-scan generate slope
			for (int x = 0; x < frame.length; x++)
			{
				// df
				DataAnalysisToolkit.getSlope(blurEdge[x], slope[x]);
				DataAnalysisToolkit.getSlope(slope[x], slopeSlope[x]);
				SurfaceFinderTool
						.determineCrossings(blurEdge[x],peak[x],1,10);
			}
			slopePanel.updateMaxMin();
			slopePanel.updateImagePanel();

			slopeSlopePanel.updateMaxMin();
			slopeSlopePanel.updateImagePanel();

			peakPanel.updateMaxMin();
			peakPanel.updateImagePanel();
		}
	}

	public static void findSurface(float[] aScan, byte[] peak, float delta, float threshold)
	{
		Vector<Integer> maxPos = new Vector<Integer>();
		Vector<Integer> minPos = new Vector<Integer>();
		
		DataAnalysisToolkit.findPeaks(aScan, delta, threshold, maxPos, minPos);
		
		for(Integer i : maxPos)
		{
			peak[i] = (byte)255;
		}
		
	}
	/**
	 * This function will perform a convolution on
	 * the given source data.
	 * 
	 * @param source
	 * @param kernal
	 * @param result
	 */
	public static void convolveData(float[][] source, float[][] kernal, float[][] result)
	{
		int sizeX = (kernal.length - 1) / 2;
		int sizeY = (kernal[0].length - 1) / 2;

		int x;
		int y;
		int xP;
		int yP;

		int kX = 0;
		int kY = 0;

		for (x = 0; x < source.length; x++)
		{
			for (y = 0; y < source[0].length; y++)
			{
				// At each location do convoluiont
				result[x][y] = 0;
				for (kX = 0; kX < kernal.length; kX++)
				{
					xP = x - sizeX + kX;
					if (xP >= 0 && xP < source.length)// Ensure
														// inside
														// kernal
					{
						for (kY = 0; kY < kernal[0].length; kY++)
						{
							yP = y - sizeY + kY;
							if (yP >= 0 && yP < source[0].length)
							{
								result[x][y] += source[xP][yP] * kernal[kX][kY];
							}
						}
					}
				}
			}
		}
	}
}
