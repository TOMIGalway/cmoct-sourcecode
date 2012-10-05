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
package com.joey.software.imageAlignment;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTabbedPane;

import com.joey.software.MultiThreadCrossCorrelation.threads.CrossCorrelationTool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


public class AlignmentTest
{
	public static void main(String args[]) throws IOException
	{

		int iterations = 10;
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		float min = 0.6f;
		float max = -0.6f;

		int wide = 1024;
		int high = 512;
		int[][] intA = new int[wide][high];
		int[][] intB = new int[wide][high];
		int[][] intC = null;

		byte[][] byteA = new byte[wide][high];
		byte[][] byteB = new byte[wide][high];
		byte[][] byteC = new byte[wide][high];

		short[][] before = new short[wide][high];
		short[][] after = new short[wide][high];

		
		
		DynamicRangeImage beforeImg = new DynamicRangeImage(before);
		DynamicRangeImage afterImg = new DynamicRangeImage(after);

		beforeImg.setMinValue(min * Short.MAX_VALUE);
		beforeImg.setMaxValue(max * Short.MAX_VALUE);

		afterImg.setMinValue(min * Short.MAX_VALUE);
		afterImg.setMaxValue(max * Short.MAX_VALUE);

		beforeImg.updateImagePanel();
		afterImg.updateImagePanel();
		JTabbedPane data = new JTabbedPane();
		data.addTab("Before", beforeImg);
		data.addTab("After", afterImg);

		FrameFactroy.getFrame(data);
		for (int frm = 1; frm < 1022; frm++)
		{
			File imgAFile = new File(
					"C:\\Users\\joey.enfield\\Desktop\\Image Alignment\\Data\\Before\\Struct_image"+StringOperations.getNumberString(5, frm)+".jpg");
			File imgBFile = new File(
					"C:\\Users\\joey.enfield\\Desktop\\Image Alignment\\Data\\Before\\Struct_image"+StringOperations.getNumberString(5, frm+1)+".jpg");

			BufferedImage imgA = ImageIO.read(imgAFile);
			BufferedImage imgB = ImageIO.read(imgBFile);

			for (int x = 0; x < imgA.getWidth(); x++)
			{
				for (int y = 0; y < imgA.getHeight(); y++)
				{
					intA[x][y] = ImageOperations
							.getGrayScale(imgA.getRGB(x, y));
					intB[x][y] = ImageOperations
							.getGrayScale(imgB.getRGB(x, y));
				}
			}

			ImageReg reg = new ImageReg(intA, intB);
			for (int i = 0; i < iterations; i++)
			{
				reg.iterate();
			}
			intC = reg.getResult();

			Ints2Bytes(intA, byteA);
			Ints2Bytes(intB, byteB);
			Ints2Bytes(intC, byteC);

			CrossCorrelationTool
					.manualCrossCorr(byteA, byteB, kerX, kerY, threshold, before);
			CrossCorrelationTool
					.manualCrossCorr(byteA, byteC, kerX, kerY, threshold, after);
			

			beforeImg.updateImagePanel();
			afterImg.updateImagePanel();
		}
	}

	public static void Ints2Bytes(int[][] data, byte[][] rst)
	{

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				rst[x][y] = (byte) data[x][y];
			}
		}
	}

	public static void Bytes2Ints(byte[][] data, int[][] rst)
	{

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				rst[x][y] = (data[x][y] < 0 ? data[x][y] + 256 : data[x][y]);
			}
		}
	}
}
