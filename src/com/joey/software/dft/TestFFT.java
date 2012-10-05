package com.joey.software.dft;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.joey.software.dft.fourier.dft;
import com.joey.software.dft.util.complex;
import com.joey.software.dft.util.point;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class TestFFT
{
	public static void main(String input[])
	{
		Vector<point> data = new Vector<point>();

		int peroids = 5;

		float length = (float) (peroids * 2 * Math.PI);
		int points = 2048;
		for (float x = 0; x <= length; x += length / points)
		{
			System.out.println(x);
			double fx = Math.sin(x) + Math.sin(2 * x);
			point p = new point(x, (float) fx);
			data.add(p);
		}
		showPlot(data);

		dft t = new dft(data);

		Vector<complex> complex = new Vector<complex>();
		for (int i = 0; i < data.size(); i++)
		{
			complex.add(t.dftPoint(i));
		}
		showPlot(complex);
	}

	public static float[][] getDataPoints(Vector<point> data)
	{
		float[][] rst = new float[2][data.size()];
		for (int i = 0; i < data.size(); i++)
		{
			rst[0][i] = data.get(i).x;
			rst[1][i] = data.get(i).y;
		}
		return rst;
	}

	/***
	 * returns an array of the data stored in the complex vector. This is stored
	 * in a float[element][data];
	 * 
	 * float[0] -> Real float[1] -> Imaginary float[2] -> mag float[3] -> angle
	 * 
	 * @param data
	 * @return
	 */
	public static float[][] getDataComplex(Vector<complex> data)
	{
		float[][] rst = new float[4][data.size()];
		for (int i = 0; i < data.size(); i++)
		{
			rst[0][i] = data.get(i).real();
			rst[1][i] = data.get(i).imag();
			rst[2][i] = data.get(i).mag();
			rst[3][i] = data.get(i).angle();
		}
		return rst;
	}

	public static void showPlot(Vector data)
	{
		if (data.get(0) instanceof point)
		{
			float[][] points = getDataPoints(data);
			FrameFactroy
					.getFrame(PlotingToolkit
							.getChartPanel(points[0], points[1], "Testing", "X Data", "Y Data"));
		} else
		{
			float[][] points = getDataComplex(data);

			JPanel fullData = PlotingToolkit
					.getChartPanel(points[0], points[1], "Full Data", "Real Value", "Imaginary");
			JPanel real = PlotingToolkit
					.getChartPanel(points[0], "Read Data", "Index", "Value");
			JPanel complex = PlotingToolkit
					.getChartPanel(points[1], "Complex Data", "Index", "Value");
			JPanel mag = PlotingToolkit
					.getChartPanel(points[2], "Mag Data", "Index", "Value");
			JPanel angle = PlotingToolkit
					.getChartPanel(points[3], "Angle Data", "Index", "Value");

			FrameFactroy.getFrame(new JComponent[]
			{ fullData, real, complex, mag, angle }, 3, 2);
		}
	}
}
