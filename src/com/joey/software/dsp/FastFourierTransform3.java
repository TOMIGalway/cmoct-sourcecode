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

public class FastFourierTransform3
{

	private static final float PI = (float) Math.PI;

	private static final float TWOPI = (float) (2.0 * Math.PI);

	float[] fdata;

	public FastFourierTransform3(int dataSize)
	{
		// find power of two greater than num of points in fdata
		int nPointsPow2 = 1;
		while (nPointsPow2 < dataSize)
			nPointsPow2 *= 2;
		fdata = new float[nPointsPow2 * 2];
	}

	public void fft(float[] reIn, float[] imIn, float[] reOut, float[] imOut)
	{
		fft(reIn, imIn, reOut, imOut, null, null);
	}

	public void fft(float[] reIn, float[] reOut, float[] imOut)
	{
		fft(reIn, null, reOut, imOut, null, null);
	}

	/** Static processing methods */
	/** Forward Fast Fourier Transform */
	public void fft(float[] reIn, float[] imIn, float[] reOut, float[] imOut, float[] mag, float[] phase)
	{
		// load float data to double array
		int i, j;
		for (i = 0, j = 0; i < reIn.length; i++)
		{
			fdata[j++] = reIn[i];
			if (imIn != null)
			{
				fdata[j++] = imIn[i];
			} else
			{
				fdata[j++] = 0;
			}
		}
		for (; j < fdata.length; j++)
			fdata[j] = 0.0f;
		// apply forward FFT
		four1(fdata, 1);

		for (i = 0, j = 0; i < reOut.length; i++)
		{
			reOut[i] = fdata[j++];
			imOut[i] = fdata[j++];

			if (mag != null)
			{
				mag[i] = 2
						* (float) (Math.sqrt(reOut[i] * reOut[i] + imOut[i]
								* imOut[i])) / reIn.length;
			}

			if (phase != null)
			{
				phase[i] = (float) Math.atan2(imOut[i], reOut[i]);
			}
		}
	}

	/**
	 * Fast Fourier Transform (adapted from Numerical Recipies in C) isign = 1
	 * replace data by FFT = -1 replace data by inverse FFT data pseudo-complex
	 * array of length nn input as real pairs nn integer power of 2
	 */
	public void four1(float[] data, int isign)
	{
		int nn = data.length / 2;
		int n, m, j;
		float temp;
		n = nn << 1;
		j = 0;
		for (int i = 0; i < n; i += 2)
		{
			if (j > i)
			{
				// swap(data[j], data[i]);
				temp = data[j];
				data[j] = data[i];
				data[i] = temp;
				// swap(data[j + 1], data[i + 1]);
				temp = data[j + 1];
				data[j + 1] = data[i + 1];
				data[i + 1] = temp;
			}
			m = n >> 1;
			while (m >= 2 && j > m - 1)
			{
				j -= m;
				m >>= 1;
			}
			j += m;
		}
		int i, istep;
		float wtemp, wr, wpr, wpi, wi, theta;
		float tempr, tempi;
		int mmax = 2;
		while (n > mmax)
		{
			istep = 2 * mmax;
			theta = TWOPI / (isign * mmax);
			wtemp = (float) Math.sin(0.5 * theta);
			wpr = (float) (-2.0 * wtemp * wtemp);
			wpi = (float) Math.sin(theta);
			wr = 1.0f;
			wi = 0.0f;
			for (m = 0; m < mmax; m += 2)
			{
				for (i = m; i < n; i += istep)
				{
					j = i + mmax;
					tempr = wr * data[j] - wi * data[j + 1];
					tempi = wr * data[j + 1] + wi * data[j];
					data[j] = data[i] - tempr;
					data[j + 1] = data[i + 1] - tempi;
					data[i] += tempr;
					data[i + 1] += tempi;
				}
				wr = (wtemp = wr) * wpr - wi * wpi + wr;
				wi = wi * wpr + wtemp * wpi + wi;
			}
			mmax = istep;
		}
		if (isign == -1)
			for (m = 0; m < 2 * nn; m += 2)
				data[m] /= nn;

	}

	/**
	 * Fast Fourier Transform (adapted from Numerical Recipies in C) isign = 1
	 * replace data by FFT = -1 replace data by inverse FFT data pseudo-complex
	 * array of length nn input as real pairs nn integer power of 2
	 */
	public void four1(double[] data, int isign)
	{
		int nn = data.length / 2;
		int n, m, j;
		double temp;
		n = nn << 1;
		j = 0;
		for (int i = 0; i < n; i += 2)
		{
			if (j > i)
			{
				// swap(data[j], data[i]);
				temp = data[j];
				data[j] = data[i];
				data[i] = temp;
				// swap(data[j + 1], data[i + 1]);
				temp = data[j + 1];
				data[j + 1] = data[i + 1];
				data[i + 1] = temp;
			}
			m = n >> 1;
			while (m >= 2 && j > m - 1)
			{
				j -= m;
				m >>= 1;
			}
			j += m;
		}
		int i, istep;
		double wtemp, wr, wpr, wpi, wi, theta;
		double tempr, tempi;
		int mmax = 2;
		while (n > mmax)
		{
			istep = 2 * mmax;
			theta = TWOPI / (isign * mmax);
			wtemp = Math.sin(0.5 * theta);
			wpr = (-2.0 * wtemp * wtemp);
			wpi = Math.sin(theta);
			wr = 1.0f;
			wi = 0.0f;
			for (m = 0; m < mmax; m += 2)
			{
				for (i = m; i < n; i += istep)
				{
					j = i + mmax;
					tempr = wr * data[j] - wi * data[j + 1];
					tempi = wr * data[j + 1] + wi * data[j];
					data[j] = data[i] - tempr;
					data[j + 1] = data[i + 1] - tempi;
					data[i] += tempr;
					data[i + 1] += tempi;
				}
				wr = (wtemp = wr) * wpr - wi * wpi + wr;
				wi = wi * wpr + wtemp * wpi + wi;
			}
			mmax = istep;
		}
		if (isign == -1)
			for (m = 0; m < 2 * nn; m += 2)
				data[m] /= nn;

	}

}
