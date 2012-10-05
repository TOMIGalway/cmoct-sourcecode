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
package com.joey.software.mathsToolkit;

public class ArrayToolkit
{
	public static void printToScreen(double[][] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < data[0].length; j++)
			{
				if (j != 0)
				{
					System.out.print(" , ");
				}
				System.out.print(data[i][j]);
			}
			System.out.println();
		}
	}

	public static int[] conver(Integer[] data)
	{
		int[] rst = new int[data.length];
		for (int i = 0; i < data.length; i++)
		{
			rst[i] = data[i];
		}
		return rst;
	}

	public static void main(String[] dd)
	{
		float[] input = new float[]
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		float[] output = new float[5];

		resizeArray(input, output);

		for (int i = 0; i < input.length; i++)
		{
			System.out.print(input[i] + ",");
		}

		System.out.println();

		for (int i = 0; i < output.length; i++)
		{
			System.out.print(output[i] + ",");
		}
	}

	public static void resizeArray(float[] input, float[] output)
	{
		float step = (float) (input.length - 1) / (output.length - 1);

		float val;
		float low;
		float high;

		float pos;
		float delta;

		for (int i = 0; i < output.length; i++)
		{
			pos = i * step;
			low = input[(int) Math.floor(pos)];
			high = input[(int) Math.ceil(pos)];
			delta = pos - low;

			val = low + (high - low) * delta;

			output[i] = val;
		}
	}

	public static int[] makeLinear(int[][] data)
	{

		int[] result = new int[data.length * data[0].length];

		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < data[0].length; j++)
			{
				result[data.length * i + j] = data[i][j];
			}
		}

		return result;
	}

	public static double[] convert(float[] data)
	{
		double[] rst = new double[data.length];

		for (int i = 0; i < data.length; i++)
		{
			rst[i] = data[i];
		}
		return rst;
	}
}
