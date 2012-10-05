package com.joey.software.Launcher.microneedleAnalysis;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class DataAnalysisViewer extends JPanel
{
	public static final int MODE_All_FOREARM = 0;

	public static final int MODE_AVG_FOREARM = 1;

	public static final int MODE_All_FINGER = 2;

	public static final int MODE_AVG_FINGER = 3;

	JComboBox modeBox = new JComboBox(new String[]
	{ "All Forearm", "AVG Forearm", "All Finger Vs Time" });

	MicroNeedleAnalysis owner = null;

	ChartPanel chartHolder = new ChartPanel(PlotingToolkit.getPlot(new int[]
	{ 1, 2, 3 }, "", "", ""));

	JButton replot = new JButton("Replot");

	JTextField expFilter = new JTextField();

	JTextField viewFilter = new JTextField();

	JCheckBox useSkin = new JCheckBox("Skin", true);

	JCheckBox usePore = new JCheckBox("Pore", true);

	JCheckBox usePoreSkin = new JCheckBox("Pore Skin", true);

	JCheckBox useTotal = new JCheckBox("Total", true);

	JButton debugButton = new JButton("Debug");

	public DataAnalysisViewer(MicroNeedleAnalysis analysis)
	{
		owner = analysis;
		createJPanel();
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());

		JPanel pan = new JPanel(new GridLayout(4, 1));
		pan.add(SwingToolkit.getLabel(modeBox, "Mode : ", 100));
		pan.add(SwingToolkit.getLabel(expFilter, "Exp : ", 100));
		pan.add(SwingToolkit.getLabel(viewFilter, "View : ", 100));
		pan.add(debugButton);

		JPanel showPanel = new JPanel(new GridLayout(1, 3));
		showPanel.add(useSkin);
		showPanel.add(usePore);
		showPanel.add(usePoreSkin);
		showPanel.add(useTotal);

		JPanel temp = new JPanel(new BorderLayout());
		temp.add(pan, BorderLayout.CENTER);
		temp.add(showPanel, BorderLayout.SOUTH);

		add(temp, BorderLayout.NORTH);
		add(chartHolder, BorderLayout.CENTER);
		add(replot, BorderLayout.SOUTH);

		debugButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveDataAsCSV();
			}
		});
		ActionListener action = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				replotData();
			}
		};

		useSkin.addActionListener(action);
		usePore.addActionListener(action);
		usePoreSkin.addActionListener(action);
		useTotal.addActionListener(action);
		modeBox.addActionListener(action);

		expFilter.getDocument().addDocumentListener(new DocumentListener()
		{

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				replotData();
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				replotData();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				replotData();
			}
		});

		viewFilter.getDocument().addDocumentListener(new DocumentListener()
		{

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				replotData();
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				replotData();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				replotData();
			}
		});

		replot.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				replotData();

			}
		});
	}

	public void saveDataAsCSV()
	{
		System.out.println("Debug");

	}

	public void replotData()
	{

		if (modeBox.getSelectedIndex() == MODE_All_FOREARM)
		{
			CategoryDataset data = null;
			data = getAllForeArmData();
			chartHolder
					.setChart(ChartFactory
							.createBarChart("Experiment", "Cat", "Value", data, PlotOrientation.VERTICAL, true, false, false));
		} else if (modeBox.getSelectedIndex() == MODE_AVG_FOREARM)
		{
			CategoryDataset data = null;
			data = getAVGForeArmData();
			chartHolder
					.setChart(ChartFactory
							.createBarChart("Experiment", "Cat", "Value", data, PlotOrientation.VERTICAL, true, false, false));
		} else if (modeBox.getSelectedIndex() == MODE_All_FINGER)
		{
			XYSeriesCollection collection = getAllFingerData();
			chartHolder
					.setChart(ChartFactory
							.createScatterPlot("Data", "Time (min)", "Dim", collection, PlotOrientation.VERTICAL, true, false, false));
		}

	}

	public boolean validMeasurment(MeasurmentData measure)
	{
		boolean eOk = false;
		boolean vOk = false;

		String exp = expFilter.getText();
		String view = viewFilter.getText();

		if (exp.length() > 0)
		{
			eOk = eOk || isOk(measure.exp, exp);
		} else
		{
			eOk = eOk || true;
		}
		if (view.length() > 0)
		{
			vOk = vOk || isOk(measure.view, view);
		} else
		{
			vOk = vOk || true;
		}

		return (eOk && vOk);
	}

	public static boolean isOk(String data, String opt)
	{
		boolean rst = false;
		String expOpt[] = opt.toLowerCase().split("\\|");

		for (String s : expOpt)
		{
			rst = rst || data.toLowerCase().startsWith(s);
		}
		return rst;
	}

	public static void main(String input[])
	{
		String data = "data";
		String opt = "";

		System.out.println(isOk(data, opt));
	}

	public XYSeriesCollection getAllFingerData()
	{
		XYSeriesCollection collection = new XYSeriesCollection();

		LinkedHashMap<String, XYSeries> skinData = new LinkedHashMap<String, XYSeries>();
		LinkedHashMap<String, XYSeries> poreData = new LinkedHashMap<String, XYSeries>();
		LinkedHashMap<String, XYSeries> poreSkinData = new LinkedHashMap<String, XYSeries>();
		LinkedHashMap<String, XYSeries> totalData = new LinkedHashMap<String, XYSeries>();
		ArrayList<MeasurmentData> measureData = owner.measures;
		for (MeasurmentData measure : measureData)
		{
			if (validMeasurment(measure))
			{
				String key = MicroNeedleAnalysis
						.clearLabel(measure.view, "abcdefghijklmnopqrstuvwxyz0123456789");

				XYSeries skin = skinData.get(key);
				XYSeries pore = poreData.get(key);
				XYSeries poreSkin = poreSkinData.get(key);
				XYSeries total = totalData.get(key);

				if (skin == null)
				{
					skin = new XYSeries(key + " Skin");
					skinData.put(key, skin);
				}

				if (pore == null)
				{
					pore = new XYSeries(key + " Pore");
					poreData.put(key, pore);
				}

				if (poreSkin == null)
				{
					poreSkin = new XYSeries(key + " Pore Skin");
					poreSkinData.put(key, poreSkin);
				}

				if (total == null)
				{
					total = new XYSeries(key + " Total");
					totalData.put(key, total);
				}

				int time;
				try
				{
					time = Integer.parseInt(MicroNeedleAnalysis
							.clearLabel(measure.exp, "0123456789"));
				} catch (Exception e)
				{
					time = -10;
				}

				skin.add(time, measure.skinDim.getValue());
				pore.add(time, measure.poreDim.getValue());
				poreSkin.add(time, measure.skinPoreDim.getValue());
				total
						.add(time, (measure.poreDim.getValue() + measure.skinPoreDim
								.getValue()));
			}
		}

		String[] keys = skinData.keySet().toArray(new String[0]);
		for (String key : keys)
		{
			XYSeries skin = skinData.get(key);
			XYSeries pore = poreData.get(key);
			XYSeries poreSkin = poreSkinData.get(key);
			XYSeries total = totalData.get(key);
			if (useSkin.isSelected())
			{
				collection.addSeries(skin);
			}

			if (usePore.isSelected())
			{
				collection.addSeries(pore);
			}

			if (usePoreSkin.isSelected())
			{
				collection.addSeries(poreSkin);
			}

			if (useTotal.isSelected())
			{
				collection.addSeries(total);
			}
		}
		return collection;
	}

	public CategoryDataset getAllForeArmData()
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		ArrayList<MeasurmentData> measureData = owner.measures;
		for (MeasurmentData measure : measureData)
		{
			if (validMeasurment(measure))
			{
				if (useSkin.isSelected())
				{
					dataset
							.addValue(measure.skinDim.getValue(), "Skin", measure.view
									+ "." + measure.angle);
				}

				if (usePore.isSelected())
				{
					dataset
							.addValue(measure.poreDim.getValue(), "Pore", measure.view
									+ "." + measure.angle);
				}

				if (usePoreSkin.isSelected())
				{
					dataset
							.addValue(measure.skinPoreDim.getValue(), "Skin Pore", measure.view
									+ "." + measure.angle);
				}

				if (useTotal.isSelected())
				{
					dataset.addValue(measure.skinPoreDim.getValue()
							+ measure.poreDim.getValue(), "Total", measure.view
							+ "." + measure.angle);
				}
			}
		}
		return dataset;
		// column keys...
	}

	public CategoryDataset getAVGForeArmData()
	{

		LinkedHashMap<String, ArrayList<Double>> skinData = new LinkedHashMap<String, ArrayList<Double>>();
		LinkedHashMap<String, ArrayList<Double>> poreData = new LinkedHashMap<String, ArrayList<Double>>();
		LinkedHashMap<String, ArrayList<Double>> poreSkinData = new LinkedHashMap<String, ArrayList<Double>>();

		ArrayList<MeasurmentData> measureData = owner.measures;
		for (MeasurmentData measure : measureData)
		{
			if (validMeasurment(measure))
			{

				ArrayList<Double> holdSkin = skinData.get(measure.view);
				if (holdSkin == null)
				{
					holdSkin = new ArrayList<Double>();
					skinData.put(measure.view, holdSkin);
				}
				holdSkin.add(measure.skinDim.getValue());

				ArrayList<Double> holdPore = poreData.get(measure.view);
				if (holdPore == null)
				{
					holdPore = new ArrayList<Double>();
					poreData.put(measure.view, holdPore);
				}
				holdPore.add(measure.poreDim.getValue());

				ArrayList<Double> holdPoreSkin = poreSkinData.get(measure.view);
				if (holdPoreSkin == null)
				{
					holdPoreSkin = new ArrayList<Double>();
					poreSkinData.put(measure.view, holdPoreSkin);
				}
				holdPoreSkin.add(measure.skinPoreDim.getValue());

			}
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		{
			String[] keys = skinData.keySet().toArray(new String[0]);

			for (String key : keys)
			{
				ArrayList<Double> skin = skinData.get(key);

				if (useSkin.isSelected())
				{
					dataset
							.addValue(DataAnalysisToolkit
									.getAverage(DataAnalysisToolkit
											.ListToArrayd(skinData.get(key))), "Skin", key);
				}

				if (usePore.isSelected())
				{
					dataset
							.addValue(DataAnalysisToolkit
									.getAverage(DataAnalysisToolkit
											.ListToArrayd(poreData.get(key))), "Pore", key);
				}

				if (usePoreSkin.isSelected())
				{
					dataset
							.addValue(DataAnalysisToolkit
									.getAverage(DataAnalysisToolkit
											.ListToArrayd(poreSkinData.get(key))), "SkinPore", key);
				}

				if (useTotal.isSelected())
				{
					dataset
							.addValue(DataAnalysisToolkit
									.getAverage(DataAnalysisToolkit
											.ListToArrayd(skinData.get(key)))
									+ DataAnalysisToolkit
											.getAverage(DataAnalysisToolkit
													.ListToArrayd(poreSkinData
															.get(key))), "Total", key);
				}
			}
		}
		return dataset;
	}

}
