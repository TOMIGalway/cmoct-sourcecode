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

public class FastFourierTransform2
{

	/*
	 * gets no. of points from the user, initialize the points and roots of
	 * unity lookup table and lets fft go. finally bit-reverses the results and
	 * outputs them into a file. n should be a power of 2.
	 */
	public static void main(String args[])
	{
		int n;
		int i;
		float A_re[], A_im[], W_re[], W_im[];

		n = 2048;

		A_re = new float[n];
		A_im = new float[n];
		W_re = new float[n / 2];
		W_im = new float[n / 2];

		FastFourierTransform2 fft = new FastFourierTransform2(n);
		// fft.init_array(n, A_re, A_im);
		// long start = 0;
		// long time = 0;
		//
		// for (int size = 1; size < 10000; size += 50)
		// {
		// start = System.currentTimeMillis();
		// for (i = 0; i < size; i++)
		// {
		// fft.compute_W(n, W_re, W_im);
		// fft.fft(n, A_re, A_im, W_re, W_im);
		// fft.permute_bitrev(n, A_re, A_im);
		// }
		// time = System.currentTimeMillis() - start;
		// System.out.println(size + "," + time);
		// }
	}

	int n = 1;

	float W_re[], W_im[];

	public FastFourierTransform2(int dataSize)
	{
		n = dataSize;
		W_re = new float[n / 2];
		W_im = new float[n / 2];
	}

	public void fft(float[] reIn, float[] reOut, float[] imOut)
	{
		fft(reIn, null, reOut, imOut, null, null);
	}

	public void fft(float[] reIn, float[] imIn, float[] reOut, float[] imOut)
	{
		fft(reIn, imIn, reOut, imOut, null, null);
	}

	public void fft(float[] reIn, float[] imIn, float[] reOut, float[] imOut, float[] mag, float[] phase)
	{
		for (int i = 0; i < reIn.length; i++)
		{
			reOut[i] = reIn[i];
			if (imIn != null)
			{
				imOut[i] = imIn[i];
			} else
			{
				imOut[i] = 0;
			}
		}

		compute_W(W_re, W_im);
		fft_Internal(reOut, imOut, W_re, W_im);
		permute_bitrev(reOut, imOut);

		if (mag == null && phase == null)
		{
			return;
		}

		// compute maths
		for (int i = 0; i < reIn.length / 2; i++)
		{
			if (mag != null)
			{
				mag[i] = 2
						* (float) (Math.sqrt(reOut[i] * reOut[i] + imOut[i]
								* imOut[i])) / n;
			}

			if (phase != null)
			{
				phase[i] = (float) Math.atan2(imOut[i], reOut[i]);
			}
		}
	}

	// public
	//
	// /* initializes the array with some function of n */
	// void init_array(float A_re[], float A_im[])
	// {
	// int NumPoints, i;
	// NumPoints = 0;
	//
	// for (i = 0; i < n * 2; i += 2)
	// {
	// A_re[NumPoints] = 1;// (float)input_buf[i];
	// A_im[NumPoints] = 0;// (float)input_buf[i+1];
	// /*
	// * printf("%d,%d -> %g,%g\n", input_buf[i], input_buf[i+1],
	// * A_re[NumPoints], A_im[NumPoints]);
	// */
	// /* printf("%g %g\n", A_re[NumPoints], A_im[NumPoints]); */
	// NumPoints++;
	// }
	//
	// for (i = 0; i < n; i++)
	// {
	// if (i == 1)
	// {
	// A_re[i] = 1.0f;
	// A_im[i] = 0.0f;
	// } else
	// {
	// A_re[i] = 0.0f;
	// A_im[i] = 0.0f;
	// }
	//
	// A_re[i] = (float) Math.sin((double) i * 2 * Math.PI / (double) n);
	// A_im[i] = (float) Math.sin((double) i * 2 * Math.PI / (double) n);
	//
	// }
	// // A_re[255] = 1.0;
	//
	// }

	/*
	 * W will contain roots of unity so that W[bitrev(i,log2n-1)] = e^(2*pi*i/n)
	 * n should be a power of 2 Note: W is bit-reversal permuted because fft(..)
	 * goes faster if this is done. see that function for more details on why we
	 * treat 'i' as a (log2n-1) bit number.
	 */
	void compute_W(float W_re[], float W_im[])
	{
		int i, br;
		int log2n = log_2(n);

		for (i = 0; i < (n / 2); i++)
		{
			br = bitrev(i, log2n - 1);
			W_re[br] = (float) Math.cos((double) i * 2 * Math.PI / n);
			W_im[br] = (float) Math.sin((double) i * 2 * Math.PI / n);
		}

		for (i = 0; i < (n / 2); i++)
		{
			br = i; // bitrev(i,log2n-1);
			// System.out.printf("(%g\t%g)\n", W_re[br], W_im[br]);
		}

	}

	/* permutes the array using a bit-reversal permutation */
	void permute_bitrev(float A_re[], float A_im[])
	{
		int i, bri, log2n;
		float t_re, t_im;

		log2n = log_2(n);

		for (i = 0; i < n; i++)
		{
			bri = bitrev(i, log2n);

			/* skip already swapped elements */
			if (bri <= i)
				continue;

			t_re = A_re[i];
			t_im = A_im[i];
			A_re[i] = A_re[bri];
			A_im[i] = A_im[bri];
			A_re[bri] = t_re;
			A_im[bri] = t_im;
		}
	}

	/*
	 * treats inp as a numbits number and bitreverses it. inp < 2^(numbits) for
	 * meaningful bit-reversal
	 */
	int bitrev(int inp, int numbits)
	{
		int i, rev = 0;
		for (i = 0; i < numbits; i++)
		{
			rev = (rev << 1) | (inp & 1);
			inp >>= 1;
		}
		return rev;
	}

	/* returns log n (to the base 2), if n is positive and power of 2 */
	int log_2(int n)
	{
		int res;
		for (res = 0; n >= 2; res++)
			n = n >> 1;
		return res;
	}

	/*
	 * fft on a set of n points given by A_re and A_im. Bit-reversal permuted
	 * roots-of-unity lookup table is given by W_re and W_im. More specifically,
	 * W is the array of first n/2 nth roots of unity stored in a permuted
	 * bitreversal order.
	 * 
	 * FFT - Decimation In Time FFT with input array in correct order and output
	 * array in bit-reversed order.
	 * 
	 * REQ: n should be a power of 2 to work.
	 * 
	 * Note: - See www.cs.berkeley.edu/~randit for her thesis on VIRAM FFTs and
	 * other details about VHALF section of the algo (thesis link -
	 * http://www.cs.berkeley.edu/~randit/papers/csd-00-1106.pdf) - See the
	 * foll. CS267 website for details of the Decimation In Time FFT implemented
	 * here. (www.cs.berkeley.edu/~demmel/cs267/lecture24/lecture24.html) -
	 * Also, look "Cormen Leicester Rivest [CLR] - Introduction to Algorithms"
	 * book for another variant of Iterative-FFT
	 */

	void fft_Internal(float A_re[], float A_im[], float W_re[], float W_im[])
	{
		float w_re, w_im, u_re, u_im, t_re, t_im;
		int m, g, b;
		int i, mt, k;

		/* for each stage */
		for (m = n; m >= 2; m = m >> 1)
		{
			/* m = n/2^s; mt = m/2; */
			mt = m >> 1;

			/* for each group of butterfly */
			for (g = 0, k = 0; g < n; g += m, k++)
			{
				/*
				 * each butterfly group uses only one root of unity. actually,
				 * it is the bitrev of this group's number k. BUT 'bitrev' it as
				 * a log2n-1 bit number because we are using a lookup array of
				 * nth root of unity and using cancellation lemma to scale nth
				 * root to n/2, n/4,... th root.
				 * 
				 * It turns out like the foll. w.re = W[bitrev(k, log2n-1)].re;
				 * w.im = W[bitrev(k, log2n-1)].im; Still, we just use k,
				 * because the lookup array itself is bit-reversal permuted.
				 */
				w_re = W_re[k];
				w_im = W_im[k];

				/* for each butterfly */
				for (b = g; b < (g + mt); b++)
				{
					/*
					 * printf("bf %d %d %d %f %f %f %f\n", m, g, b, A_re[b],
					 * A_im[b], A_re[b+mt], A_im[b+mt]);
					 */
					// printf("bf %d %d %d (u,t) %g %g %g %g (w) %g %g\n", m, g,
					// b, A_re[b], A_im[b], A_re[b+mt], A_im[b+mt], w_re, w_im);
					/* t = w * A[b+mt] */
					t_re = w_re * A_re[b + mt] - w_im * A_im[b + mt];
					t_im = w_re * A_im[b + mt] + w_im * A_re[b + mt];

					/* u = A[b]; in[b] = u + t; in[b+mt] = u - t; */
					u_re = A_re[b];
					u_im = A_im[b];
					A_re[b] = u_re + t_re;
					A_im[b] = u_im + t_im;
					A_re[b + mt] = u_re - t_re;
					A_im[b + mt] = u_im - t_im;

					/*
					 * printf("af %d %d %d %f %f %f %f\n", m, g, b, A_re[b],
					 * A_im[b], A_re[b+mt], A_im[b+mt]);
					 */
					// printf("af %d %d %d (u,t) %g %g %g %g (w) %g %g\n", m, g,
					// b, A_re[b], A_im[b], A_re[b+mt], A_im[b+mt], w_re, w_im);
				}
			}
		}
	}

}
