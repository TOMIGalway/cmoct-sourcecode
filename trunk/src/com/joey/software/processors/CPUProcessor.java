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
package com.joey.software.processors;

public class CPUProcessor
{
	public static int getAverage(int data[], int length)
	{
		if (length == 0)
		{
			return 0;
		}
		int value = 0;
		for (int i = 0; i < length; i++)
		{
			value += data[i];
		}
		value = value / length;
		return value;
	}

	public static float getMaxFloat(float[] data, int length)
	{
		if (length == 0)
		{
			return 0;
		}
		float val = data[0];
		for (int i = 0; i < length; i++)
		{
			if (data[i] > val)
			{
				val = data[i];
			}
		}
		return val;
	}

	public static float getMinFloat(float[] data, int length)
	{
		if (length == 0)
		{
			return 0;
		}
		float val = data[0];
		for (int i = 0; i < length; i++)
		{
			if (data[i] < val)
			{
				val = data[i];
			}
		}
		return val;

	}

	public static float getAverageFloat(float[] data, int length)
	{
		if (length == 0)
		{
			return 0;
		}

		float value = 0;
		for (int i = 0; i < length; i++)
		{
			value += data[i];
		}
		value = value / length;

		return value;
	}

	public static float getCrossCorr(int[] gridA, int[] gridB, int wide, int high, int threshold)
	{
		int x, y;
		float avgA = getAverage(gridA, wide * high);
		float avgB = getAverage(gridB, wide * high);

		if (avgA < threshold || avgB < threshold)
		{
			return 1;
		}

		float tA = 0;
		float tB = 0;
		float tC = 0;

		float t1 = 0;
		float t2 = 0;
		for (x = 0; x < wide; x++)
		{
			for (y = 0; y < high; y++)
			{
				t1 = gridA[x * high + y] - avgA;
				t2 = gridB[x * high + y] - avgB;
				tA += t1 * t2;
				tB += t1 * t1;
				tC += t2 * t2;
			}
		}

		if (tB == 0 || tC == 0)
		{
			return 1;
		}
		return (float) (tA / Math.sqrt(tB * tC));
	}

	public static void crossCorr(int[] inputData, int inX, int inY, int sizeX, int sizeY, int imgNum, int threshold, float[] out)
	{
		int imgB = 0;
		int imgA = 0;
		int gridA[] = new int[(2 * sizeX + 1) * (2 * sizeY + 1)];
		int gridB[] = new int[(2 * sizeX + 1) * (2 * sizeY + 1)];

		int pxlCount;
		for (int xP = 0; xP < inX; xP++)
		{
			for (int yP = 0; yP < inY; yP++)
			{
				for (int img = 0; img < imgNum; img++)
				{
					imgB = img + 1;
					imgA = 0;
					

					pxlCount = 0;
					for (int x = xP - sizeX; x <= xP + sizeX; x++)
					{
						for (int y = yP - sizeY; y <= yP + sizeY; y++)
						{
							if (x < inX && y < inY && x >= 0 && y >= 0)
							{
								gridA[pxlCount] = inputData[imgA * inX
										* inY + inY * x + y];
								gridB[pxlCount] = inputData[imgB * inX
										* inY + inY * x + y];
							} else
							{
								gridA[pxlCount] = 0;
								gridB[pxlCount] = 0;
							}
							pxlCount++;
						}
					}
					out[(imgB - 1) * inX * inY + inY * xP + yP] = getCrossCorr(gridA, gridB, 2 * sizeX + 1, 2 * sizeY + 1, threshold);
				}
			}
		}
	}

	public static void doStats(float[] inputData, int inX, int inY, int imgNum, float[] avg, float[] min, float[] max)
	{
		for (int xP = 0; xP < inX; xP++)
		{
			for (int yP = 0; yP < inY; yP++)
			{

				float avgVal = 0;
				float maxVal = 0;
				float minVal = 0;

				float value = 0;
				for (int i = 0; i < imgNum; i++)
				{
					value = inputData[i * inX * inY + inY * xP + yP];

					avgVal += value;
					if (i == 0)
					{
						maxVal = value;
						minVal = value;
					} else
					{
						if (value > maxVal)
						{
							maxVal = value;
						}

						if (value < minVal)
						{
							minVal = value;
						}
					}
				}

				avg[inY * xP + yP] = avgVal / imgNum;
				min[inY * xP + yP] = maxVal;
				max[inY * xP + yP] = minVal;
			}
		}
	}

}
