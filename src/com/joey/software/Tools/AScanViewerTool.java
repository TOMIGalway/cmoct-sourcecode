package com.joey.software.Tools;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.joey.software.LAFTools.EditableLAFControler;
import com.joey.software.Launcher.SettingsLauncher;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class AScanViewerTool extends JFrame implements FRG_ViewerListner
{
	FRG_Viewer imageViewPanel = new FRG_Viewer();

	JFreeChart previewPlot = PlotingToolkit
			.getPlot(new float[1], "A Scan", "", "");

	JFreeChart dataPlot = PlotingToolkit
			.getPlot(new float[1], "Log10(A Scan)", "", "");

	JButton saveCSVData = new JButton("Save Data");

	ChartPanel previewPanel = new ChartPanel(previewPlot);

	ChartPanel dataPanel = new ChartPanel(dataPlot);

	HashMap<DynamicRangeImage, Integer> aScanLength = new HashMap<DynamicRangeImage, Integer>();

	HashMap<DynamicRangeImage, Double[]> realLength = new HashMap<DynamicRangeImage, Double[]>();

	float xDataMin = 0;

	float xDataMax = 1;

	float[] xData = new float[0];

	float[] aScanData = new float[0];

	float[] logData = new float[0];

	float[] minData = new float[0];

	float[] maxData = new float[0];

	int marker = 0;

	JComboBox aScanType = new JComboBox(new String[]
	{ "None", "Point", "Manual" });

	JSlider diffMarker = new JSlider();

	public AScanViewerTool()
	{
		createJPanel();
		imageViewPanel.setListner(this);

		// aScanLength.put(imageViewPanel.realPanel, 1024);
		// aScanLength.put(imageViewPanel.imaginaryPanel, 1024);
		// aScanLength.put(imageViewPanel.phasePanel, 1024);
		// aScanLength.put(imageViewPanel.magPanel, 1024);
		//
		// realLength.put(imageViewPanel.realPanel, new Double[]
		// { 0., 6. });
		// realLength.put(imageViewPanel.imaginaryPanel, new Double[]
		// { 0., 6. });
		// realLength.put(imageViewPanel.phasePanel, new Double[]
		// { 0., 6. });
		// realLength.put(imageViewPanel.magPanel, new Double[]
		// { 0., 6. });
	}

	public void setFile(File f) throws IOException
	{
		imageViewPanel.setFile(f);

	}

	public void saveAScanData(File f) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(f);

		out.println("Min Window :," + minData[0]);
		out.println("Max Window :," + maxData[0]);
		out.println("Pos,Value, log10(value)");

		for (int i = 0; i < xData.length; i++)
		{
			out.println(xData[i] + "," + aScanData[i] + "," + logData[i]);
		}
		out.flush();
		out.close();
	}

	public void createJPanel()
	{
		JSplitPane graphSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		graphSplit.setLeftComponent(previewPanel);
		graphSplit.setRightComponent(dataPanel);
		graphSplit.setOneTouchExpandable(true);
		graphSplit.setDividerLocation(400);

		JPanel graphHolder = new JPanel(new BorderLayout());
		graphHolder.add(graphSplit, BorderLayout.CENTER);
		graphHolder.setBorder(BorderFactory.createTitledBorder(""));

		JPanel tool = new JPanel(new BorderLayout());
		tool.add(saveCSVData, BorderLayout.SOUTH);
		tool.add(aScanType, BorderLayout.CENTER);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(graphHolder, BorderLayout.CENTER);
		leftPanel.add(tool, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setOneTouchExpandable(true);
		split.setRightComponent(imageViewPanel);
		split.setLeftComponent(leftPanel);
		split.setDividerLocation(600);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(split, BorderLayout.CENTER);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel);

		aScanType.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				imageViewPanel.setViewType(aScanType.getSelectedIndex());

			}
		});
		saveCSVData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File f = FileSelectionField.getUserFile();
					f = FileOperations.renameFileType(f, "csv");
					saveAScanData(f);
				} catch (Exception e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error : "
									+ e1.getLocalizedMessage(), "Error Saving Data", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
	}

	public void saveCSVData()
	{

	}

	public static void main(String input[]) throws IOException
	{

		SettingsLauncher.setActivePath(AScanViewerTool.class);
		EditableLAFControler laf = new EditableLAFControler(SettingsLauncher
				.getPath()
				+ "\\LAF\\");
		laf.setLAF(7);

		AScanViewerTool view = new AScanViewerTool();

		view.setSize(1200, 800);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);

		// view.setFile(new File("C:\\test\\test2D.FRG"));
	}

	@Override
	public void AScanChanged(DynamicRangeImage src, float[] dataF, int marker)
	{
		if (dataF == null || dataF.length == 0)
		{
			return;
		}
		this.marker = marker;
		float[] data;
		if (aScanLength.containsKey(src))
		{
			int length = aScanLength.get(src);
			data = new float[length];
			for (int i = 0; i < length; i++)
			{
				data[i] = dataF[i];
			}

		} else
		{
			data = dataF;
		}

		if (realLength.containsKey(src))
		{
			xDataMin = realLength.get(src)[0].floatValue();
			xDataMax = realLength.get(src)[1].floatValue();
		} else
		{
			xDataMax = data.length;
			xDataMin = 0;
		}

		setAScan(data, marker);
		setMax((float)src.getMaxSelection());
		setMin((float)src.getMinSelection());

	}

	public FRG_Viewer getImageViewPanel()
	{
		return imageViewPanel;
	}

	public int getAScanLength()
	{

		if (aScanLength.containsKey(getImageViewPanel().getCurrentView()))
		{
			return aScanLength.get(getImageViewPanel().getCurrentView());
		} else
		{
			return getImageViewPanel().getCurrentView().getImage().getImage()
					.getHeight();
		}
	}

	public double[] getDataRange()
	{
		if (realLength.containsKey(getImageViewPanel().getCurrentView()))
		{
			return new double[]
			{ realLength.get(getImageViewPanel().getCurrentView())[0],
					realLength.get(getImageViewPanel().getCurrentView())[1] };
		} else
		{
			return new double[]
			{ 0, getAScanLength() };
		}
	}
	
	public boolean getLogScaleing()
	{
		return imageViewPanel.magPanel.isUseLogScaleing();
	}

	public float getDisplayMaxValue()
	{
		return (float)imageViewPanel.magPanel.getMaxSelection();
	}
	
	public float getDisplayMinValue()
	{
		return (float)imageViewPanel.magPanel.getMinSelection();
	}
	public void estimateSurface()
	{

	}

	public void setAScan(float[] aData, int pos)
	{
		{

			if (aData.length != aScanData.length)
			{
				xData = new float[aData.length];
				maxData = new float[aData.length];
				minData = new float[aData.length];
			}

			for (int i = 0; i < xData.length; i++)
			{
				xData[i] = xDataMin + (xDataMax - xDataMin)
						* (i / ((float) xData.length - 1));
			}

			aScanData = aData;

			XYSeriesCollection datCol1 = PlotingToolkit
					.getCollection(xData, aScanData, "Data");

			XYSeriesCollection datCol2 = PlotingToolkit
					.getCollection(new float[]
					{ xData[pos] }, new float[]
					{ aScanData[pos] }, "Data");

			previewPlot.getXYPlot().setDataset(0, datCol1);
			previewPlot.getXYPlot().setDataset(3, datCol2);

			XYLineAndShapeRenderer dataRender1 = new XYLineAndShapeRenderer(
					true, false);
			XYLineAndShapeRenderer dataRender2 = new XYLineAndShapeRenderer(
					false, true);

			dataRender1.setSeriesPaint(0, Color.CYAN);
			dataRender2.setSeriesPaint(0, Color.RED);

			previewPlot.getXYPlot().setRenderer(0, dataRender1);
			previewPlot.getXYPlot().setRenderer(3, dataRender2);
		}

		/*
		 * Log Data
		 */
		{
			logData = aScanData.clone();

			for (int i = 0; i < logData.length; i++)
			{
				if (logData[i] < 0.0001f)
				{
					logData[i] += 0.0001f;
				}

				logData[i] = (float) Math.log(logData[i]);
			}

			XYSeriesCollection logCol = PlotingToolkit
					.getCollection(xData, logData, "Data");
			XYSeriesCollection datCol2 = PlotingToolkit
					.getCollection(new float[]
					{ xData[pos] }, new float[]
					{ logData[pos] }, "Data");

			dataPlot.getXYPlot().setDataset(0, logCol);
			dataPlot.getXYPlot().setDataset(3, datCol2);
			// Set the rendering
			XYLineAndShapeRenderer dataRender = new XYLineAndShapeRenderer(
					true, false);
			XYLineAndShapeRenderer dataRender2 = new XYLineAndShapeRenderer(
					false, true);
			dataRender2.setSeriesPaint(0, Color.RED);
			dataRender.setSeriesPaint(0, Color.CYAN);
			dataPlot.getXYPlot().setRenderer(0, dataRender);
			dataPlot.getXYPlot().setRenderer(3, dataRender2);
		}
	}

	public void setMax(float max)
	{
		for (int i = 0; i < maxData.length; i++)
		{
			maxData[i] = max;
		}

		XYSeriesCollection maxCol = PlotingToolkit
				.getCollection(xData, maxData, "Max");

		XYLineAndShapeRenderer maxRender = new XYLineAndShapeRenderer(true,
				false);
		maxRender.setSeriesPaint(0, Color.MAGENTA);

		previewPlot.getXYPlot().setRenderer(2, maxRender);
		dataPlot.getXYPlot().setRenderer(2, maxRender);

		dataPlot.getXYPlot().setDataset(2, maxCol);
		previewPlot.getXYPlot().setDataset(2, maxCol);
	}

	public void setMin(float min)
	{
		for (int i = 0; i < minData.length; i++)
		{
			minData[i] = min;
		}

		XYSeriesCollection minCol = PlotingToolkit
				.getCollection(xData, minData, "Min");

		XYLineAndShapeRenderer minRender = new XYLineAndShapeRenderer(true,
				false);
		minRender.setSeriesPaint(0, Color.RED);

		previewPlot.getXYPlot().setRenderer(1, minRender);
		dataPlot.getXYPlot().setRenderer(1, minRender);

		dataPlot.getXYPlot().setDataset(1, minCol);
		previewPlot.getXYPlot().setDataset(1, minCol);
	}

}
