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
package com.joey.software.dsp;

public class FFTSpeedTest
{
	public static void main(String input[])
	{
		float error = 1e-3f;
		int dataSize = 1024;

		float[] reIn1 = getRandomData(dataSize);
		float[] imIn1 = getStaticData(dataSize, 0);
		float[] reOut1 = getRandomData(dataSize);
		float[] imOut1 = getStaticData(dataSize, 0);

		float[] reIn2 = reIn1.clone();// getRandomData(dataSize);
		float[] imIn2 = getStaticData(dataSize, 0);
		float[] reOut2 = getRandomData(dataSize);
		float[] imOut2 = getStaticData(dataSize, 0);

		float[] reIn3 = reIn1.clone();// getRandomData(dataSize);
		float[] imIn3 = getStaticData(dataSize, 0);
		float[] reOut3 = getRandomData(dataSize);
		float[] imOut3 = getStaticData(dataSize, 0);

		int runs = 256;
		//for (int runs = 2048; runs < 2050; runs += 150)
		{
			long time1 = method1FFT(reIn1, imIn1, reOut1, imOut1, runs);
			long time2 = method2FFT(reIn2, imIn2, reOut2, imOut2, runs);
			long time3 = method3FFT(reIn3, imIn3, reOut3, imOut3, runs);

			System.out.println(runs + "," + time1 + "," + time2 + "," + time3
					+ ", : ");
			// + (compareResult(reOut1, imOut1, reOut3, imOut3, error) &&
			// compareResult(reOut1, imOut1, reOut2, imOut2, error)));
		}
	}

	public static long method1FFT(float[] reIn, float[] imIn, float[] reOut, float[] imOut, int runs)
	{
		FastFourierTransform1 fft = new FastFourierTransform1();

		long start = 0;
		long total = 0;
		for (int i = 0; i < runs; i++)
		{
			start = System.currentTimeMillis();
			fft.fft(reIn, imIn, reOut, imOut);
			total += System.currentTimeMillis() - start;
		}
		return total;
	}

	public static long method2FFT(float[] reIn, float[] imIn, float[] reOut, float[] imOut, int runs)
	{
		FastFourierTransform2 fft = new FastFourierTransform2(reIn.length);

		long start = 0;
		long total = 0;
		for (int i = 0; i < runs; i++)
		{
			start = System.currentTimeMillis();
			fft.fft(reIn, imIn, reOut, imOut);
			total += System.currentTimeMillis() - start;
		}
		return total;
	}

	public static long method3FFT(float[] reIn, float[] imIn, float[] reOut, float[] imOut, int runs)
	{
		FastFourierTransform3 fft = new FastFourierTransform3(reIn.length);

		long start = 0;
		long total = 0;
		for (int i = 0; i < runs; i++)
		{
			start = System.currentTimeMillis();
			fft.fft(reIn, imIn, reOut, imOut);
			total += System.currentTimeMillis() - start;
		}
		return total;
	}

	public static float[] getRandomData(int length)
	{
		float[] result = new float[length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = (float) Math.random();
		}
		return result;
	}

	public static float[] getSinData(int length, float peroids)
	{
		float[] result = new float[length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = (float) Math.sin(i * 2 * Math.PI * peroids
					/ result.length);
		}
		return result;
	}

	public static float[] getStaticData(int length, float value)
	{
		float[] result = new float[length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = value;
		}
		return result;
	}

	public static Complex[] getComplexData(float[] real, float[] imag)
	{
		Complex[] data = new Complex[real.length];
		for (int i = 0; i < real.length; i++)
		{
			data[i] = new Complex(real[i], imag[i]);
		}
		return data;
	}

	public static boolean compareResult(float[] re1, float[] im1, float[] re2, float[] im2, float error)
	{
		for (int i = 0; i < re1.length; i++)
		{
			if (Math.abs((re1[i] - re2[i])) > error)
			{
				// System.out.println("Real [" + i + "] = " + re1[i] + " : "
				// + re2[i]);
				return false;
			}

			if (Math.abs((im1[i] - im2[i])) > error)
			{
				// System.out.println("Img [" + i + "] = " + im1[i] + " : "
				// + im2[i]);
				return false;
			}
		}
		return false;
	}
}
