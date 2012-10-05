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

import java.util.AbstractCollection;
import java.util.Vector;

import com.joey.software.dsp.FastFourierTransform1;


public class DataAnalysisToolkit
{

	public static int[] ListToArray(AbstractCollection<Integer> data)
	{
		int[] rst = new int[data.size()];
		int count = 0;
		for (Integer d : data)
		{
			rst[count++] = d;
		}
		return rst;
	}

	/**
	 * A point is considered a maximum peak if it
	 * has the maximal value, and was preceded (to
	 * the left) by a value lower by DELTA
	 * 
	 * @param values
	 * @param threshold
	 * @return
	 */
	public static void findPeaks(float[] values, float delta, float threshold, Vector<Integer> maxPos, Vector<Integer> minPos)
	{
		float mn = 0;// Inf;
		float mx = 0;// -Inf;
		int mnpos = 0;// NaN;
		int mxpos = 0;// NaN;

		float current = 0;

		int lookformax = 1;

		for (int i = 0; i < values.length; i++)
		{
			current = values[i];
			if (current > mx || i == 0)
			{
				mx = current;
				mxpos = i;
			}

			if (current < mn || i == 0)
			{
				mn = current;
				mnpos = i;
			}

			if (lookformax == 1)
			{
				if (current < mx - delta && current > threshold)
				{
					maxPos.add(mxpos);
					mn = current;
					mnpos = i;
					lookformax = 0;
				}
			} else
			{
				if (current > mn + delta && current > threshold)
				{
					minPos.add(mnpos);
					mx = current;
					mxpos = i;
					lookformax = 1;
				}
			}
		}
	}

	/**
	 * This function will get the gradient of the
	 * given data
	 * 
	 * @param data
	 * @param slope
	 */
	public static void getSlope(float[] data, float[] slope)
	{
		float diff = 0;
		for (int i = 1; i < data.length - 1; i++)
		{
			diff = data[i - 1] - data[i + 1];
			slope[i] = diff / 2;
		}
		slope[0] = slope[1];
		slope[data.length - 1] = slope[data.length - 2];
	}

	public static float[] getSlope(float[] data)
	{
		float[] slope = new float[data.length];
		getSlope(data, slope);
		return slope;
	}

	/**
	 * This will return the stats of an interger
	 * dataset stat[0] -> mean stat[1] -> min
	 * stat[2] -> max
	 * 
	 * @param data
	 * @param stat
	 */
	public static void getFullStatsInt(int[] data, int[] stat)
	{
		stat[0] = data[0];
		stat[1] = data[0];
		stat[2] = data[0];

		for (int i = 1; i < data.length; i++)
		{
			stat[0] += data[i];

			if (data[i] < stat[1])
			{
				stat[1] = data[i];
			}

			if (data[i] > stat[2])
			{
				stat[2] = data[i];
			}
		}

		stat[0] /= data.length;
	}

	/**
	 * This function will sort a given array
	 * assending
	 * 
	 * if saveorder is set this function will
	 * return an array that holds the orignal
	 * index of each data element else null
	 * 
	 * @param data
	 * @return
	 */
	public static int[] sortAssDataSet(int[] data, boolean saveOrder)
	{
		int[] rst = null;

		if (saveOrder)
		{
			rst = new int[data.length];
			for (int i = 0; i < data.length; i++)
			{
				rst[i] = i;
			}
		}
		int n = data.length;
		for (int pass = 1; pass < n; pass++)
		{ // count how many times
			// This next loop becomes shorter and
			// shorter
			for (int i = 0; i < n - pass; i++)
			{
				if (data[i] > data[i + 1])
				{
					// exchange elements
					int temp = data[i];
					data[i] = data[i + 1];
					data[i + 1] = temp;

					if (saveOrder)
					{
						temp = rst[i];
						rst[i] = rst[i + 1];
						rst[i + 1] = temp;
					}
				}
			}
		}

		return rst;
	}

	public static int getSumValues(int[] data)
	{
		int result = 0;
		for (int i : data)
		{
			result += i;
		}
		return result;
	}

	public static double getSumValues(double[] data)
	{
		double result = 0;
		for (double i : data)
		{
			result += i;
		}
		return result;
	}

	public static float getSumValues(float[] data)
	{
		float result = 0;
		for (float i : data)
		{
			result += i;
		}
		return result;
	}

	public static int getMaxFFTPos(float[] data)
	{
		FastFourierTransform1 tra = new FastFourierTransform1();
		float[] fft = tra.fft(data)[2];

		int maxPos = 0;
		float max = fft[0];

		for (int i = 0; i < fft.length; i++)
		{
			if (max < fft[i])
			{
				max = fft[i];
				maxPos = i;
			}
		}

		return maxPos;
	}

	public static double[] ListToArrayd(AbstractCollection<Double> data)
	{
		double[] rst = new double[data.size()];
		int count = 0;
		for (Double d : data)
		{
			rst[count++] = d;
		}
		return rst;
	}

	public static float[] ListToArrayf(AbstractCollection<Float> data)
	{
		float[] rst = new float[data.size()];
		int count = 0;
		for (Float d : data)
		{
			rst[count++] = d;
		}
		return rst;
	}

	public static double getStdDev(double... data)
	{
		double avg = getAverage(data);
		int varSqr = 0;

		for (double d : data)
		{
			varSqr += (d - avg) * (d - avg);
		}
		varSqr /= data.length;

		return Math.sqrt(varSqr);
	}

	public static double getStdDev(int... data)
	{
		double avg = getAverage(data);
		int varSqr = 0;

		for (double d : data)
		{
			varSqr += (d - avg) * (d - avg);
		}
		varSqr /= data.length;

		return Math.sqrt(varSqr);
	}

	public static double[] getProbDist(int[] data)
	{
		double[] rst = new double[data.length];
		double sum = getSumValues(data);
		for (int i = 0; i < rst.length; i++)
		{
			rst[i] = data[i] / sum;
		}
		return rst;
	}

	public static double[] getProbDist(double[] data)
	{
		double[] rst = new double[data.length];
		double sum = getSumValues(data);
		for (int i = 0; i < rst.length; i++)
		{
			rst[i] = data[i] / sum;
		}
		return rst;
	}

	public static float[] getProbDist(float[] data)
	{
		float[] rst = new float[data.length];
		float sum = getSumValues(data);
		for (int i = 0; i < rst.length; i++)
		{
			rst[i] = data[i] / sum;
		}
		return rst;
	}

	/**
	 * This will get the cumatitive distribution
	 * from a prob density distributeion
	 * 
	 * @return
	 */
	public static double[] getCumDist(double probDist[])
	{
		double[] result = new double[probDist.length];

		double sum = 0;
		for (int i = 0; i < result.length; i++)
		{
			sum += probDist[i];
			result[i] = sum;
		}
		return result;
	}

	public static float getStdDevf(float... data)
	{
		float avg = getAveragef(data);
		int varSqr = 0;

		for (float d : data)
		{
			varSqr += (d - avg) * (d - avg);
		}
		varSqr /= data.length;

		return (float) Math.sqrt(varSqr);
	}

	public static double getAverage(double... data)
	{
		double val = 0;
		for (double d : data)
		{
			val += d;
		}

		return val / data.length;
	}

	public static int getAverage(int... data)
	{
		double val = 0;
		for (int d : data)
		{
			val += d;
		}

		return (int) Math.round(val / data.length);
	}

	public static float getAveragef(float... data)
	{
		float val = 0;
		for (float d : data)
		{
			val += d;
		}

		return val / data.length;
	}

	public static double getMind(double... data)
	{
		double min = data[0];

		for (double d : data)
		{
			if (d < min)
			{
				min = d;
			}
		}

		return min;
	}

	public static double getMaxd(double... data)
	{
		double max = data[0];

		for (double d : data)
		{
			if (d > max)
			{
				max = d;
			}
		}

		return max;
	}

	/**
	 * This will get the range on a set of data.
	 * it will return the data in the form
	 * double[] -> [0] min [1] max
	 * 
	 * @param data
	 * @return
	 */
	public static double[] getRange(double... data)
	{
		double max = data[0];
		double min = data[0];

		for (double d : data)
		{
			if (d < min)
			{
				min = d;
			}
			if (d > max)
			{
				max = d;
			}
		}

		return new double[] { min, max };
	}

	public static int getMin(int... data)
	{
		int min = data[0];

		for (int d : data)
		{
			if (d < min)
			{
				min = d;
			}
		}

		return min;
	}

	public static int getMax(int... data)
	{
		int max = data[0];

		for (int d : data)
		{
			if (d > max)
			{
				max = d;
			}
		}

		return max;
	}

	public int getSum(int... data)
	{
		int sum = 0;
		for (int i : data)
		{
			sum += i;
		}
		return sum;
	}

	/**
	 * This will get the range on a set of data.
	 * it will return the data in the form int[]
	 * -> [0] min [1] max
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getRange(int... data)
	{
		int max = data[0];
		int min = data[0];

		for (int d : data)
		{
			if (d < min)
			{
				min = d;
			}
			if (d > max)
			{
				max = d;
			}
		}

		return new int[] { min, max };
	}

	public static float getMinf(float... data)
	{
		float min = data[0];

		for (float d : data)
		{
			if (d < min)
			{
				min = d;
			}
		}

		return min;
	}

	public static float getMaxf(float... data)
	{
		float max = data[0];

		for (float d : data)
		{
			if (d > max)
			{
				max = d;
			}
		}

		return max;
	}

	/**
	 * This will get the range on a set of data.
	 * it will return the data in the form float[]
	 * -> [0] min [1] max
	 * 
	 * @param data
	 * @return
	 */
	public static void getRangef(float range[], float... data)
	{
		range[0] = data[0];
		range[1] = data[0];

		for (float d : data)
		{
			if (d < range[0])
			{
				range[0] = d;
			}
			if (d > range[1])
			{
				range[1] = d;
			}
		}
	}

	public static float[] getRangef(float... data)
	{
		float[] range = new float[2];
		getRangef(range, data);
		return range;
	}

	public static short[] getRangeS(short... data)
	{
		short max = data[0];
		short min = data[0];

		for (short d : data)
		{
			if (d < min)
			{
				min = d;
			}
			if (d > max)
			{
				max = d;
			}
		}

		return new short[] { min, max };
	}

	public static double[] getRanged(double[][] data)
	{
		double max = data[0][0];
		double min = data[0][0];

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				double d = data[x][y];
				if (d < min)
				{
					min = d;
				}
				if (d > max)
				{
					max = d;
				}
			}
		}

		return new double[] { min, max };
	}


	public static float[] getRangef(float[][] data)
	{
		float max = data[0][0];
		float min = data[0][0];

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				float d = data[x][y];
				if (d < min)
				{
					min = d;
				}
				if (d > max)
				{
					max = d;
				}
			}
		}

		return new float[] { min, max };
	}

	public static short[] getRanges(short[][] data)
	{
		short max = data[0][0];
		short min = data[0][0];

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				short d = data[x][y];
				if (d < min)
				{
					min = d;
				}
				if (d > max)
				{
					max = d;
				}
			}
		}

		return new short[] { min, max };
	}

	/**
	 * This will output an array split between min
	 * and max with steps segments
	 * 
	 * @param min
	 * @param max
	 * @param steps
	 * @return
	 */
	public static double[] getData(double min, double max, int steps)
	{
		double[] result = new double[steps];
		for (int i = 0; i < steps; i++)
		{
			result[i] = (max - min) * ((double) i / (double) (steps - 1));

		}
		return result;
	}

	public static void main(String arg[])
	{
		int count = 10000;
		int run = 1000;
		int size = 100;

		int[] data = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		int[] stats = new int[3];

		getFullStatsInt(data, stats);

		System.out.println(stats[0] + "," + stats[1] + "," + stats[2]);

	}

	public static float getAveragef(float[][] sliceFlow)
	{
		float pos = 0;
		for (int x = 0; x < sliceFlow.length; x++)
		{
			for (int y = 0; y < sliceFlow[x].length; y++)
			{
				pos += sliceFlow[x][y];
			}
		}
		return pos / (sliceFlow.length * sliceFlow[0].length);
	}

	public static float[][] transpose(float[][] data)
	{
		float[][] result = new float[data[0].length][data.length];

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				result[y][x] = data[x][y];
			}
		}
		return result;
	}

	public static float[][] subSample(float[][] data, int newX, int newY)
	{
		float oldX = data.length;
		float oldY = data[0].length;

		float[][] result = new float[newX][newY];
		for (int x = 0; x < result.length; x++)
		{
			for (int y = 0; y < result[0].length; y++)
			{
				float xP = ((float) x / newX) * oldX;
				float yP = ((float) y / newY) * oldY;
				result[x][y] = data[(int) xP][(int) yP];
			}
		}
		return result;
	}

	public static float[][] copy(float[][] sourceData)
	{
		float[][] result = new float[sourceData.length][sourceData[0].length];
		for (int x = 0; x < sourceData.length; x++)
		{
			for (int y = 0; y < sourceData[0].length; y++)
			{
				result[x][y] = sourceData[x][y];
			}
		}
		return result;
	}

	public static int b2i(byte val)
	{
		return val >= 0 ? val : val + 256;
	}

	public static int[] getRangeb(byte[][] data)
	{
		int max = b2i(data[0][0]);
		int min = b2i(data[0][0]);

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				int d = b2i(data[x][y]);
				if (d < min)
				{
					min = d;
				}
				if (d > max)
				{
					max = d;
				}
			}
		}

		return new int[] { min, max };
	}

	public static void printData(byte[][] frm)
	{
		for (int x = 0; x < frm.length; x++)
		{
			for (int y = 0; y < frm[0].length; y++)
			{
				System.out.printf(frm[x][y] + "\t");
			}
			System.out.println();
		}

	}

	public static void printDataRow(float[] det)
	{
		for (int i = 0; i < det.length; i++)
		{
			System.out.print(det[i] + " , ");
		}
		System.out.println();
	}

	public static void printDataCol(float[] det)
	{
		for (int i = 0; i < det.length; i++)
		{
			System.out.println(det[i]);
		}
	}

	public static int getMul(int[] dims)
	{
		int result = 1;
		for (int i = 0; i < dims.length; i++)
		{
			result *= dims[i];
		}
		return result;
	}

	public static void printData(double[][] frm)
	{
		for (int x = 0; x < frm.length; x++)
		{
			for (int y = 0; y < frm[0].length; y++)
			{
				System.out.printf(frm[x][y] + "\t");
			}
			System.out.println();
		}

	}

	public static void fixBadNumber(double[][] frm, int i)
	{
		for (int x = 0; x < frm.length; x++)
		{
			for (int y = 0; y < frm[0].length; y++)
			{
				if (Double.isInfinite(frm[x][y]) || Double.isNaN(frm[x][y]))
				{
					frm[x][y] = 0;
				}
			}
		}
	}

	public static void fixBadNumber(float[][] frm, int i)
	{
		for (int x = 0; x < frm.length; x++)
		{
			for (int y = 0; y < frm[0].length; y++)
			{
				if (Float.isInfinite(frm[x][y]) || Float.isNaN(frm[x][y]))
				{
					frm[x][y] = 0;
				}
			}
		}
	}
	public static double[] getRanged(double[][][] data)
	{
		boolean first = false;

		double range[] = new double[2];
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					if (!Double.isInfinite(data[x][y][z])
							&& !Double.isNaN(data[x][y][z]))
					{
						if (first)
						{
							range[0] = data[x][y][z];
							range[1] = data[x][y][z];
						}

						if (data[x][y][z] < range[0])
						{
							range[0] = data[x][y][z];
						}

						if (data[x][y][z] > range[1])
						{
							range[1] = data[x][y][z];
						}
					}
				}
			}
		}

		return range;
	}

	public static float[] getRangef(float[][][] data)
	{
		boolean first = false;

		float range[] = new float[2];
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					if (!Float.isInfinite(data[x][y][z])
							&& !Float.isNaN(data[x][y][z]))
					{
						if (first)
						{
							range[0] = data[x][y][z];
							range[1] = data[x][y][z];
						}

						if (data[x][y][z] < range[0])
						{
							range[0] = data[x][y][z];
						}

						if (data[x][y][z] > range[1])
						{
							range[1] = data[x][y][z];
						}
					}
				}
			}
		}

		return range;
	}
	public static void printDataRow(double[] det)
	{
		for (int i = 0; i < det.length; i++)
		{
			System.out.print(det[i] + " , ");
		}
		System.out.println();
	}

	public static void printDataCol(double[] det)
	{
		for (int i = 0; i < det.length; i++)
		{
			System.out.println(det[i]);
		}
	}

	public static void fixBadNumber(double[][][] data, int val)
	{
		for(int i = 0; i < data.length; i++)
		{
			fixBadNumber(data[i], val);
		}
	}
	
	public static void fixBadNumber(float[][][] data, int val)
	{
		for(int i = 0; i < data.length; i++)
		{
			fixBadNumber(data[i], val);
		}
	}

	public static void normalise(float[] values)
	{
		float[] range = getRangef(values);
		
		
		for(int i = 0; i < values.length; i++)
		{
			//values[i] = (float) (Math.log10(100*values[i]+1)/Math.log10(100*range[1]+1));
			values[i]= (values[i]-range[0])/(range[1]-range[0]);
		}
		
	}

	public static int[] getRangei(int[][] data)
	{
		int max = (data[0][0]);
		int min = (data[0][0]);

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				int d = (data[x][y]);
				if (d < min)
				{
					min = d;
				}
				if (d > max)
				{
					max = d;
				}
			}
		}

		return new int[] { min, max };
	}
}
