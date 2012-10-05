package com.joey.software.plottingToolkit;


import java.awt.Color;
import java.security.InvalidParameterException;

import javax.swing.JPanel;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mathsToolkit.ArrayToolkit;

public class PlotingToolkit
{

	public static int[] getXData(int length)
	{
		int[] result = new int[length];
		for (int i = 0; i < length; i++)
		{
			result[i] = i;
		}
		return result;
	}

	public static double[] getXDataDouble(int length)
	{
		double[] result = new double[length];
		for (int i = 0; i < length; i++)
		{
			result[i] = i;
		}
		return result;
	}

	public static float[] getXDataFloat(int length)
	{
		float[] result = new float[length];
		for (int i = 0; i < length; i++)
		{
			result[i] = i;
		}
		return result;
	}

	public static float[] getStaticDataFloat(int length, float val)
	{
		float[] result = new float[length];
		for (int i = 0; i < length; i++)
		{
			result[i] = val;
		}
		return result;
	}

	public static XYSeriesCollection getCollection(double[] xData, double[] yData, String name)
	{
		if (xData.length != yData.length)
		{
			throw new InvalidParameterException("X and Y must be same length");
		}

		XYSeries series = new XYSeries(name);
		for (int i = 0; i < xData.length; i++)
		{
			series.add(xData[i], yData[i]);
		}

		XYSeriesCollection result = new XYSeriesCollection(series);
		return result;
	}

	public static XYSeriesCollection getCollection(float[] xData, float[] yData, String name)
	{
		if (xData.length != yData.length)
		{
			throw new InvalidParameterException("X and Y must be same length");
		}

		XYSeries series = new XYSeries(name);
		for (int i = 0; i < xData.length; i++)
		{
			series.add(xData[i], yData[i]);
		}

		XYSeriesCollection result = new XYSeriesCollection(series);
		return result;
	}

	public static JFreeChart getPlot(double[] xData, double[] yData, String title, String xlabel, String ylabel)
	{

		XYSeriesCollection datCol = getCollection(xData, yData, "Data");

		// Create the chart

		JFreeChart chart = ChartFactory.createXYLineChart(title, // Title
		xlabel, // X-Axis label
		ylabel, // Y-Axis label
		new XYSeriesCollection(), // Dataset
		PlotOrientation.VERTICAL, true, // Show legend
		true, true);

		// Add the series
		chart.getXYPlot().setDataset(0, datCol);

		// Set the rendering
		XYLineAndShapeRenderer rend1 = new XYLineAndShapeRenderer(true, true);
		rend1.setShapesVisible(false);
		chart.getXYPlot().setRenderer(0, rend1);

		return chart;
	}

	public static JFreeChart getPlot(double[] yData, String title, String xlabel, String ylabel)
	{
		double[] xData = getXDataDouble(yData.length);
		XYSeriesCollection datCol = getCollection(xData, yData, "Data");

		// Create the chart

		JFreeChart chart = ChartFactory.createXYLineChart(title, // Title
		xlabel, // X-Axis label
		ylabel, // Y-Axis label
		new XYSeriesCollection(), // Dataset
		PlotOrientation.VERTICAL, true, // Show legend
		true, true);

		// Add the series
		chart.getXYPlot().setDataset(0, datCol);

		// Set the rendering
		XYLineAndShapeRenderer rend1 = new XYLineAndShapeRenderer(true, true);

		chart.getXYPlot().setRenderer(0, rend1);

		return chart;
	}

	public static JFreeChart getPlot(float[] yData, String title, String xlabel, String ylabel)
	{
		float[] xData = getXDataFloat(yData.length);
		XYSeriesCollection datCol = getCollection(xData, yData, "Data");

		// Create the chart

		JFreeChart chart = ChartFactory.createXYLineChart(title, // Title
		xlabel, // X-Axis label
		ylabel, // Y-Axis label
		new XYSeriesCollection(), // Dataset
		PlotOrientation.VERTICAL, true, // Show legend
		true, true);

		// Add the series
		chart.getXYPlot().setDataset(0, datCol);

		// Set the rendering
		XYLineAndShapeRenderer rend1 = new XYLineAndShapeRenderer(true, true);

		chart.getXYPlot().setRenderer(0, rend1);

		return chart;
	}

	
	public static JPanel getChartPanel(float[][] data){
		String[] names = new String[data.length];
		for(int i = 0; i < data.length; i++){
			names[i] = "";
		}
		return getChartPanel(data, names, "", "", "", true, true);
	}
	public static JPanel getChartPanel(float[][] data, String[] names, String title, String xlabel, String ylabel){
		return getChartPanel(data, names, title, xlabel, ylabel, true, true);
	}
	
	public static JPanel getChartPanel(float[][] data, String[] names, String title, String xlabel, String ylabel, boolean showPoints, boolean showLines)
	{
		ChartPanel panel = new ChartPanel(getPlot(data, names, title, xlabel, ylabel, showPoints, showLines));
		return panel;
	}
	/**
	 * This will plot the data as series of data[1], data[2], data[3]....
	 * 
	 * @param data
	 * @param names
	 * @param title
	 * @param xlabel
	 * @param ylabel
	 * @return
	 */
	public static JFreeChart getPlot(float[][] data, String[] names, String title, String xlabel, String ylabel, boolean showPoints, boolean showLines)
	{

		// Create the chart

		JFreeChart chart = ChartFactory.createXYLineChart(title, // Title
		xlabel, // X-Axis label
		ylabel, // Y-Axis label
		new XYSeriesCollection(), // Dataset
		PlotOrientation.VERTICAL, true, // Show legend
		true, true);

		for (int i = 0; i < data.length; i++)
		{
			float[] xData = getXDataFloat(data[i].length);
			XYSeriesCollection datCol = getCollection(xData, data[i], ((names==null)||(names[i]==null)?"":names[i]));

			// Add the series
			chart.getXYPlot().setDataset(i, datCol);

			// Set the rendering
			XYLineAndShapeRenderer rend1 = new XYLineAndShapeRenderer(showLines,
					showPoints);
			rend1.setSeriesPaint(0, getPlotColor(i));
			chart.getXYPlot().setRenderer(i, rend1);
		}
		return chart;
	}

	public static Color getPlotColor(int number){
		switch(number){
			case 0:return Color.RED;
			case 1:return Color.GREEN;
			case 2:return Color.BLUE;
			case 3:return Color.CYAN;
			case 4:return Color.PINK;
			case 5:return Color.magenta;
			default:return ImageOperations.getRandomColor();
		}
	}
	public static XYSeriesCollection getCollection(int[] xData, int[] yData, String name)
	{
		if (xData.length != yData.length)
		{
			throw new InvalidParameterException("X and Y must be same length");
		}

		XYSeries series = new XYSeries(name);
		for (int i = 0; i < xData.length; i++)
		{
			series.add(xData[i], yData[i]);

		}

		XYSeriesCollection result = new XYSeriesCollection(series);
		return result;
	}

	public static JFreeChart getPlot(int[] xData, int[] yData, String title, String xlabel, String ylabel)
	{

		XYSeriesCollection datCol = getCollection(xData, yData, "Data");

		// Create the chart

		JFreeChart chart = ChartFactory.createXYLineChart(title, // Title
		xlabel, // X-Axis label
		ylabel, // Y-Axis label
		new XYSeriesCollection(), // Dataset
		PlotOrientation.VERTICAL, true, // Show legend
		true, true);

		// Add the series
		chart.getXYPlot().setDataset(0, datCol);

		// Set the rendering
		XYLineAndShapeRenderer rend1 = new XYLineAndShapeRenderer(true, true);

		chart.getXYPlot().setRenderer(0, rend1);

		return chart;
	}

	public static JFreeChart getPlot(int[] yData, String title, String xlabel, String ylabel)
	{
		int[] xData = getXData(yData.length);
		XYSeriesCollection datCol = getCollection(xData, yData, "Data");

		// Create the chart

		JFreeChart chart = ChartFactory.createXYLineChart(title, // Title
		xlabel, // X-Axis label
		ylabel, // Y-Axis label
		new XYSeriesCollection(), // Dataset
		PlotOrientation.VERTICAL, true, // Show legend
		true, true);

		// Add the series

		// Set the rendering
		XYLineAndShapeRenderer rend1 = new XYLineAndShapeRenderer(true, true);

		chart.getXYPlot().setRenderer(0, rend1);

		return chart;
	}

	public static JPanel getChartPanel(float[] xData, float[] yData, String title, String xlabel, String ylabel)
	{
		double[] xDataDouble = ArrayToolkit.convert(xData);
		double[] yDataDouble = ArrayToolkit.convert(yData);

		return getChartPanel(xDataDouble, yDataDouble, title, xlabel, ylabel);
	}

	public static JPanel getChartPanel(double[] xData, double[] yData, String title, String xlabel, String ylabel)
	{
		JFreeChart chart = getPlot(xData, yData, title, xlabel, ylabel);

		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}

	public static JPanel getChartPanel(double[] xData, double[] yData, String title, String xlabel, String ylabel, double xmin, double xmax, double ymin, double ymax)
	{
		JFreeChart chart = getPlot(xData, yData, title, xlabel, ylabel);
		ChartPanel panel = new ChartPanel(chart);
		NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
		NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();

		// xAxis.setLowerBound(xmin);
		// xAxis.setUpperBound(xmax);
		xAxis.setRange(xmin, xmax);
		yAxis.setRange(ymin, ymax);
		return panel;
	}

	public static JPanel getChartPanel(double[] yData, String title, String xlabel, String ylabel)
	{
		JFreeChart chart = getPlot(yData, title, xlabel, ylabel);
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}

	public static JPanel getChartPanel(float[] yData, String title, String xlabel, String ylabel)
	{
		JFreeChart chart = getPlot(yData, title, xlabel, ylabel);
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}

	public static JPanel getChartPanel(int[] xData, int[] yData, String title, String xlabel, String ylabel)
	{
		JFreeChart chart = getPlot(xData, yData, title, xlabel, ylabel);

		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}

	public static JPanel getChartPanel(int[] yData, String title, String xlabel, String ylabel)
	{
		JFreeChart chart = getPlot(yData, title, xlabel, ylabel);
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}

}
