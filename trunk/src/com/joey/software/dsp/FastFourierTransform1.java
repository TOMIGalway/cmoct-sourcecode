package com.joey.software.dsp;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.plottingToolkit.PlotingToolkit;


public final class FastFourierTransform1
{

	public static void main(String input[])
	{
		/*
		 * This sesction of code will attempt to determine the speed of FT
		 * operations
		 */

		float[] specData1;
		float[] real1;
		float[] imaginary1;
		float[] mag1;
		float[] phase1;

		float[] specData2;
		float[] real2;
		float[] imaginary2;
		float[] mag2;
		float[] phase2;

		float[] specDataDiff;
		float[] realDiff;
		float[] imaginaryDiff;
		float[] magDiff;
		float[] phaseDiff;

		int dataSize = 2048;
		int size = 1;
		int runs = 1;

		FastFourierTransform1 fft1 = new FastFourierTransform1();
		FastFourierTransform2 fft2 = new FastFourierTransform2(dataSize);

		specData1 = new float[dataSize];
		real1 = new float[dataSize];
		imaginary1 = new float[dataSize];
		mag1 = new float[dataSize];
		phase1 = new float[dataSize];

		specData2 = new float[dataSize];
		real2 = new float[dataSize];
		imaginary2 = new float[dataSize];
		mag2 = new float[dataSize];
		phase2 = new float[dataSize];

		specDataDiff = new float[dataSize];
		realDiff = new float[dataSize];
		imaginaryDiff = new float[dataSize];
		magDiff = new float[dataSize];
		phaseDiff = new float[dataSize];

		for (int i = 0; i < dataSize; i++)
		{
			specData1[i] = (float) Math.random() * 1000;
			specData2[i] = (float) Math.random() * 100990;
		}

		for (size = 1; size < 51; size += 50)
		{
			long time = System.currentTimeMillis();
			for (runs = 1; runs < size; runs += 1)
			{
				fft1.fft(specData1, real1, imaginary1, phase1, mag1);
			}
			long fft1Time = System.currentTimeMillis() - time;

			time = System.currentTimeMillis();
			for (runs = 1; runs < size; runs += 1)
			{
				fft2.fft(specData2, null, real2, imaginary2, phase2, mag2);
			}
			long fft2Time = System.currentTimeMillis() - time;

			for (int i = 0; i < specDataDiff.length; i++)
			{
				specDataDiff[i] = specData1[i] - specData2[i];
				realDiff[i] = real1[i] - real2[i];
				imaginaryDiff[i] = imaginary1[i] - imaginary2[i];
				magDiff[i] = mag1[i] - mag2[i];
				phaseDiff[i] = phase1[i] - phase2[i];
			}

			JPanel real = PlotingToolkit
					.getChartPanel(realDiff, "Real", "diff", "Diff");
			JPanel imaginary = PlotingToolkit
					.getChartPanel(imaginaryDiff, "Imaginary", "diff", "Diff");
			JPanel mag = PlotingToolkit
					.getChartPanel(magDiff, "Magnitude", "diff", "Diff");
			JPanel phase = PlotingToolkit
					.getChartPanel(phaseDiff, "Phase", "diff", "Diff");

			JTabbedPane tab = new JTabbedPane();
			tab.addTab("Real", real);
			tab.addTab("Imag", real);
			tab.addTab("Mag", mag);
			tab.addTab("Phase", phase);

			FrameFactroy.getFrame(tab);
			System.out.println(size + "," + fft1Time + "," + fft2Time);
		}
	}

	private int n, nu;

	private int bitrev(int j)
	{

		int j2;
		int j1 = j;
		int k = 0;
		for (int i = 1; i <= nu; i++)
		{
			j2 = j1 / 2;
			k = 2 * k + j1 - 2 * j2;
			j1 = j2;
		}
		return k;
	}

	/**
	 * This will return the fft data in an array containing float[0] -> real
	 * float[1] -> imag float[2] -> Mag float[3] -> phase
	 * 
	 * @param x
	 * @return
	 */
	public final float[][] fft(float[] reIn, float[] imIn)
	{
		// assume n is a power of 2
		n = reIn.length;
		float[] xre = new float[n];
		float[] xim = new float[n];
		float[] phase = new float[n];
		float[] mag = new float[n]; // used to be n2 but wanted to see what will
		// happen also changed down further

		fft(reIn, imIn, xre, xim, phase, mag);

		return new float[][]
		{ xre, xim, mag, phase };
	}

	/**
	 * This will return the fft data in an array containing float[0] -> real
	 * float[1] -> imag float[2] -> Mag float[3] -> phase
	 * 
	 * @param x
	 * @return
	 */
	public final float[][] fft(float[] reIn)
	{
		// assume n is a power of 2
		n = reIn.length;
		float[] xre = new float[n];
		float[] xim = new float[n];
		float[] phase = new float[n];
		float[] mag = new float[n]; // used to be n2 but wanted to see what will
		// happen also changed down further

		fft(reIn, null, xre, xim, phase, mag);

		return new float[][]
		{ xre, xim, mag, phase };
	}

	public void fft(float[] reIn, float[] reOut, float[] imOut, float[] phase, float[] mag)
	{
		fft(reIn, null, reOut, imOut, phase, mag);
	}

	public void fft(float[] reIn, float[] reOut, float[] imOut)
	{
		fft(reIn, null, reOut, imOut, null, null);
	}

	public void fft(float[] reIn, float[] imIn, float[] reOut, float[] imOut)
	{
		fft(reIn, imIn, reOut, imOut, null, null);
	}

	public void fft(float[] reIn, float[] imIn, float[] reOut, float[] imOut, float[] phase, float[] mag)
	{
		n = reIn.length;
		nu = (int) (Math.log(n) / Math.log(2));
		int n2 = n / 2;
		int nu1 = nu - 1;
		float tr, ti, p, arg, c, s;
		for (int i = 0; i < n; i++)
		{
			reOut[i] = reIn[i];
			if (imIn != null)
			{
				imOut[i] = imIn[i];
			} else
			{
				imOut[i] = 0f;
			}
		}
		int k = 0;

		// float sum = (float)(2*Math.PI/n);
		for (int l = 1; l <= nu; l++)
		{
			while (k < n)
			{
				for (int i = 1; i <= n2; i++)
				{
					p = bitrev(k >> nu1);
					arg = 2 * (float) Math.PI / n * p;
					c = (float) Math.cos(arg);
					s = (float) Math.sin(arg);
					tr = reOut[k + n2] * c + imOut[k + n2] * s;
					ti = imOut[k + n2] * c - reOut[k + n2] * s;
					reOut[k + n2] = reOut[k] - tr;
					imOut[k + n2] = imOut[k] - ti;
					reOut[k] += tr;
					imOut[k] += ti;
					k++;
				}
				k += n2;
			}
			k = 0;
			nu1--;
			n2 = n2 / 2;
		}
		k = 0;
		int r;
		while (k < n)
		{
			r = bitrev(k);
			if (r > k)
			{
				tr = reOut[k];
				ti = imOut[k];
				reOut[k] = reOut[r];
				imOut[k] = imOut[r];
				reOut[r] = tr;
				imOut[r] = ti;
			}
			k++;
		}

		boolean getMag = !(mag == null);
		boolean getPhase = !(phase == null);
		if (!getMag && !getPhase)
		{
			return;
		}

		if (getMag)
			mag[0] = (float) (Math.sqrt(reOut[0] * reOut[0] + imOut[0]
					* imOut[0]))
					/ n;

		if (getPhase)
			phase[0] = (float) Math.atan2(imOut[0], reOut[0]);
		for (int i = 1; i < n; i++)
		{
			if (getMag)
				mag[i] = 2
						* (float) (Math.sqrt(reOut[i] * reOut[i] + imOut[i]
								* imOut[i])) / n;

			if (getPhase)
				phase[i] = (float) Math.atan2(imOut[i], reOut[i]);
		}
	}
}
