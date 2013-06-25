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

import ij.ImagePlus;
import ij.process.ByteProcessor;

import java.io.IOException;

import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;


public class AlignmentTool
{
	byte[][] result;

	byte[] sourceData;

	byte[] targetData;

	ByteProcessor sourceProcessor;

	ByteProcessor targetProcessor;

	ImagePlus source;

	ImagePlus target;

	int wide;

	int high;

	ImageAlignTool tool;

	public AlignmentTool(int wide, int high)
	{
		this.wide = wide;
		this.high = high;
		tool = new ImageAlignTool();
		allocateMemory();
	}

	public void allocateMemory()
	{
		sourceData = new byte[wide * high];
		targetData = new byte[wide * high];
		result = new byte[wide][high];

		sourceProcessor = new ByteProcessor(wide, high);
		targetProcessor = new ByteProcessor(wide, high);

		sourceProcessor.setPixels(sourceData);
		targetProcessor.setPixels(targetData);

		source = new ImagePlus("Source", sourceProcessor);
		target = new ImagePlus("Target", targetProcessor);
	}

	/**
	 * This function will align frameA and frameB.
	 * It will adjust frameB to be similar to
	 * frameA and return the adujested frameB.
	 * 
	 * @param frameA
	 * @param frameB
	 * @return
	 */
	public byte[][] alignFrames(byte[][] frameA, byte[][] frameB)
	{
		// Copy Data to local Buffer
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				targetData[x + y * wide] = frameA[x][y];
				sourceData[x + y * wide] = frameB[x][y];
			}
		}

		// Align Images
		tool.process(source, target);

		// Grab back Data

		ByteProcessor processor = (ByteProcessor) tool.getTransformedImage()
				.getProcessor().convertToByte(false);

		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				result[x][y] = ((byte[]) processor.getPixels())[x + y * wide];
			}
		}

		tool.releaseTransformedImage();
		// Result
		return result;
	}

	public byte[] alignFrames(byte[] frameA, byte[] frameB)
	{
		// Copy Data to local Buffer
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				targetData[x + y * wide] = frameA[x + y * wide];
				sourceData[x + y * wide] = frameB[x + y * wide];
			}
		}

		// Align Images
		tool.process(source, target);

		// Grab back Data

		ByteProcessor processor = (ByteProcessor) tool.getTransformedImage()
				.getProcessor().convertToByte(false);

		return ((byte[]) processor.getPixels());
	}

	public static void main(String input[]) throws IOException
	{
		ThorlabsIMGImageProducer img = new ThorlabsIMGImageProducer(
				FileSelectionField.getUserFile());

		byte[][] frameA = new byte[img.getSizeY()][img.getSizeX()];
		byte[][] frameB = new byte[img.getSizeY()][img.getSizeX()];

		AlignmentTool tool = new AlignmentTool(img.getSizeY(), img.getSizeX());

		img.getImage(100, frameA);
		img.getImage(101, frameB);

		FrameFactroy.getFrame(b2f(frameA), b2f(frameB), b2f(tool
				.alignFrames(frameA, frameB)));

	}

	public static float[][] b2f(byte[][] data)
	{
		float[][] result = new float[data[0].length][data.length];

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				result[y][x] = (data[x][y] >= 0 ? data[x][y] : 255 + data[x][y]);
			}
		}

		return result;
	}
}
