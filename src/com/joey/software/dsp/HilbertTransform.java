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

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class HilbertTransform
{

	public static void main(String input[])
	{
		int length = 5000;

		float[] reInput = FFTSpeedTest.getSinData(length, 1000);
		float[] imInput = FFTSpeedTest.getStaticData(length, 0f);

		float[] reOutput = new float[length];
		float[] imOutput = new float[length];

		FastFourierTransform3 fft = new FastFourierTransform3(length);
		fft.fft(reInput, imInput, reOutput, imOutput);

		HilbertTransform ht = new HilbertTransform();
		getHilbert(reInput, imInput, reOutput, imOutput);

		JFreeChart inputPlot = PlotingToolkit.getPlot(new float[][]
		{ reInput, imInput }, new String[]
		{ "Real", "Imaginary" }, "Input Data", "X Data", "Y Data");

		JFreeChart outputPlot = PlotingToolkit.getPlot(new float[][]
		{ reOutput, imOutput }, new String[]
		{ "Real", "Imaginary" }, "Output Data", "X Data", "Y Data");

		ChartPanel p1 = new ChartPanel(inputPlot);
		ChartPanel p2 = new ChartPanel(outputPlot);

		JPanel p = new JPanel(new GridLayout(1, 2));
		p.add(p1);
		p.add(p2);

		FrameFactroy.getFrame(p);
	}

	static float c, c2, h[];

	static int n, nt;

	static void hilbert_init(int nt1 /* transform length */, int n1 /*
																	 * trace
																	 * length
																	 */, float c1 /*
																				 * filter
																				 * parameter
																				 */)
	/* < initialize > */
	{
		n = n1;
		nt = nt1;
		c = (float) (1. / (2 * Math.sqrt(c1)));
		c2 = c * c;
		h = new float[nt];
	}

	public static void getHilbert(float[] reIn, float[] imIn, float[] reOut, float[] imOut)
	{
		float[] fftRe = new float[reIn.length];
		float[] fftIm = new float[reIn.length];

		FastFourierTransform3 fft = new FastFourierTransform3(reIn.length);
		fft.fft(reIn, imIn, fftRe, fftIm);

		for (int n = 0; n < reIn.length; n++)
		{
			for (int k = 0; k < reIn.length; k++)
			{
				double val = 2 * Math.PI * k * n / reIn.length;
				reOut[k] += fftRe[k] * Math.sin(val) / reIn.length;
				imOut[k] += fftIm[k] * Math.cos(val) / reIn.length;
			}
		}

	}

	static void hilbert(float[] trace, float[] trace2)
	/* < transform > */
	{
		int i, it;

		for (it = 0; it < nt; it++)
		{
			h[it] = trace[it];
		}

		for (i = n; i >= 1; i--)
		{
			trace2[0] = h[0] + (h[2] - 2 * h[1] + h[0]) * c2;
			trace2[1] = h[1] + (h[3] - 3 * h[1] + 2 * h[0]) * c2;
			for (it = 2; it < nt - 2; it++)
			{
				trace2[it] = (float) (h[it] + (h[it + 2] - 2. * h[it] + h[it - 2])
						* c2);
			}
			trace2[nt - 2] = h[nt - 2]
					+ (h[nt - 4] - 3 * h[nt - 2] + 2 * h[nt - 1]) * c2;
			trace2[nt - 1] = h[nt - 1]
					+ (h[nt - 3] - 2 * h[nt - 2] + h[nt - 1]) * c2;

			for (it = 0; it < nt; it++)
			{
				h[it] = trace[it] + trace2[it] * (2 * i - 1) / (2 * i);
			}
		}

		trace2[0] = (float) (2. * (h[0] - h[1]) * c);
		for (it = 1; it < nt - 1; it++)
		{
			trace2[it] = (h[it - 1] - h[it + 1]) * c;
		}
		trace2[nt - 1] = (float) (2. * (h[nt - 2] - h[nt - 1]) * c);
	}

	static void hilbert4(float[] trace, float[] trace2)
	/* < transform - kind 4 filter > */
	{
		int i, it;

		for (it = 0; it < nt; it++)
		{
			h[it] = trace[it];
		}

		for (i = n; i >= 1; i--)
		{
			for (it = 1; it < nt - 1; it++)
			{
				trace2[it] = (float) (h[it] + (h[it + 1] - 2. * h[it] + h[it - 1])
						* c2);
			}
			trace2[0] = trace2[1];
			trace2[nt - 1] = trace2[nt - 2];

			for (it = 0; it < nt; it++)
			{
				h[it] = trace[it] + trace2[it] * (2 * i - 1) / (2 * i);
			}
		}

		for (it = 0; it < nt - 1; it++)
		{
			trace2[it] = (h[it] - h[it + 1]) * c;
		}
		trace2[nt - 1] = trace2[nt - 2];
	}

	static void deriv(float[] trace, float[] trace2)
	/* < derivative operator > */
	{
		int i, it;

		for (it = 0; it < nt; it++)
		{
			h[it] = trace[it];
		}

		for (i = n; i >= 1; i--)
		{
			for (it = 1; it < nt - 1; it++)
			{
				trace2[it] = (float) (h[it] - 0.5 * (h[it + 1] + h[it - 1]));
			}
			trace2[0] = trace2[1];
			trace2[nt - 1] = trace2[nt - 2];

			for (it = 0; it < nt; it++)
			{
				h[it] = trace[it] + trace2[it] * i / (2 * i + 1);
			}
		}

		trace2[0] = h[1] - h[0];
		for (it = 1; it < nt - 1; it++)
		{
			trace2[it] = (float) (0.5 * (h[it + 1] - h[it - 1]));
		}
		trace2[nt - 1] = h[nt - 1] - h[nt - 2];
	}

}
