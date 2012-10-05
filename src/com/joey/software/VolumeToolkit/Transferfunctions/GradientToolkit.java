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
package com.joey.software.VolumeToolkit.Transferfunctions;


import java.io.IOException;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImagePanel;

public class GradientToolkit
{
	/**
	 * this will get the gradient from the 6 surrounding pixels
	 * (up/dw/lf/rg/ft/bk)
	 */
	public static final int GRADIENT_FAST = 0;

	/**
	 * This will get the gradient from the 8 surrounding pixels
	 */
	public static final int GRADIENT_FULL = 0;

	public static void main(String input[]) throws IOException
	{
		// byte[][][] data =
		// getData("c:\\users\\joey.enfield\\desktop\\Pat_MDL 001_Cosmetic examples_Neck.1_Scan.1_09-Sep-2009_12.43.23_OCT [old blade].tif");
		byte[][][] data = getData(512, 512, 128);
		// byte[][][] data = getData(10,10,10);
		int[][] grad = getGradHistHolder();

		grad[0][0] = 100;
		grad[255][255] = 100;
		getGradientFunctionFAST(data, grad);

		DynamicRangeImage img = new DynamicRangeImage(grad);
		img.getImage().setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		FrameFactroy.getFrame(img);
	}

	public static int[][] getGradHistHolder()
	{
		return new int[256][256];
	}

	public static byte[][][] getData(int wide, int high, int deep)
	{
		byte[][][] data = new byte[wide][high][deep];

		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				for (int z = 0; z < deep; z++)
				{
					data[x][y][z] = (byte) (256 * Math.random());
				}
			}
		}

		return data;
	}

	public static int getGradientFAST(byte[][][] data, int x, int y, int z)
	{
		try
		{
			int p1 = 0; // up
			int p2 = 0; // Down
			int p3 = 0; //
			int p4 = 0;
			int p5 = 0;
			int p6 = 0;

			// Move all data up if at 0
			if (x == 0)
			{
				x = 1;
			}
			if (y == 0)
			{
				y = 1;
			}
			if (z == 0)
			{
				z = 1;
			}

			// Move all data down
			if (x == data.length - 1)
			{
				x -= 2;
			}
			if (y == data[0].length - 1)
			{
				y -= 2;
			}
			if (z == data[0][0].length - 1)
			{
				z -= 2;
			}
			int grad = 0;
			int val = 0;

			val = data[x][y][z] < 0 ? data[x][y][z] + 256 : data[x][y][z];

			p1 = data[x + 1][y][z] < 0 ? data[x + 1][y][z] + 256
					: data[x + 1][y][z];
			p2 = data[x - 1][y][z] < 0 ? data[x - 1][y][z] + 256
					: data[x - 1][y][z];
			p3 = data[x][y + 1][z] < 0 ? data[x][y + 1][z] + 256
					: data[x][y + 1][z];
			p4 = data[x][y - 1][z] < 0 ? data[x][y - 1][z] + 256
					: data[x][y - 1][z];
			p5 = data[x][y][z + 1] < 0 ? data[x][y][z + 1] + 256
					: data[x][y][z + 1];
			p6 = data[x][y][z - 1] < 0 ? data[x][y][z - 1] + 256
					: data[x][y][z - 1];

			grad = ((p1 + p2 + p3 + p4 + p5 + p6) / 6) - val;

			if (grad < 0)
			{
				grad *= -1;
			}
			// System.out.printf("Data[%d] -> val [%d]\n",data[x][y][z],
			// val);

			return grad;
		} catch (Exception e)
		{
			System.out
					.printf("Size[%d,%d,%d]\tPos[%d,%d,%d]\n", data.length, data[0].length, data[0][0].length, x, y, z);
			e.printStackTrace();
			return 0;
		}
	}

	public static void getGradientFunctionFAST(byte[][][] data, int[][] out)
	{
		for (int x = 0; x < out.length; x++)
		{
			for (int y = 0; y < out[x].length; y++)
			{
				out[x][y] = 0;
			}
		}
		int p1 = 0; // up
		int p2 = 0; // Down
		int p3 = 0; //
		int p4 = 0;
		int p5 = 0;
		int p6 = 0;

		int grad = 0;
		int val = 0;
		for (int x = 1; x < data.length - 1; x++)
		{
			for (int y = 1; y < data[x].length - 1; y++)
			{
				for (int z = 1; z < data[x][y].length - 1; z++)
				{
					val = data[x][y][z] < 0 ? data[x][y][z] + 256
							: data[x][y][z];

					p1 = data[x + 1][y][z] < 0 ? data[x + 1][y][z] + 256
							: data[x + 1][y][z];
					p2 = data[x - 1][y][z] < 0 ? data[x - 1][y][z] + 256
							: data[x - 1][y][z];
					p3 = data[x][y + 1][z] < 0 ? data[x][y + 1][z] + 256
							: data[x][y + 1][z];
					p4 = data[x][y - 1][z] < 0 ? data[x][y - 1][z] + 256
							: data[x][y - 1][z];
					p5 = data[x][y][z + 1] < 0 ? data[x][y][z + 1] + 256
							: data[x][y][z + 1];
					p6 = data[x][y][z - 1] < 0 ? data[x][y][z - 1] + 256
							: data[x][y][z - 1];

					grad = ((p1 + p2 + p3 + p4 + p5 + p6) / 6) - val;
					if (grad < 0)
					{
						grad *= -1;
					}
					// System.out.printf("Data[%d] -> val [%d]\n",data[x][y][z],
					// val);
					out[val][grad]++;

				}
			}
		}
	}
}
