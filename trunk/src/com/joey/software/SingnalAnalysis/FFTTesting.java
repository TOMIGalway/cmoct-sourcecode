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
package com.joey.software.SingnalAnalysis;

import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class FFTTesting
{
	public static void main(String input[])
	{
		FastFourierTransform fft = new FastFourierTransform();

		float[] data = new float[1024];
		for (int i = 0; i < data.length; i++)
		{
			// data[i] = (float) Math.sin(Math.toRadians(i*5));
			data[i] = (float) Math.random();
		}

		JPanel source = PlotingToolkit
				.getChartPanel(data, "Source", "test", "test");

		float[] rst = fft.fftMag(data);

		JPanel result = PlotingToolkit
				.getChartPanel(rst, "rst", "test", "test");
		FrameFactroy.getFrame(new JPanel[]
		{ source, result }, 2, 1);
	}
}
