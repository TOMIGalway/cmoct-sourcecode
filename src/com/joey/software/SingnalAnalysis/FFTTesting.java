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
