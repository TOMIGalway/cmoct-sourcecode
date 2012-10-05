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
package com.joey.software.Launcher.microneedleAnalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.mainProgram.OCTExperimentData;
import com.joey.software.mainProgram.OCTViewDataHolder;
import com.joey.software.mathsToolkit.NumberDimension;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.regionSelectionToolkit.controlers.ImageProfileTool;
import com.joey.software.regionSelectionToolkit.controlers.LineControler;
import com.joey.software.regionSelectionToolkit.controlers.ProfilePlotter;
import com.joey.software.regionSelectionToolkit.controlers.ROIControler;
import com.joey.software.sliceTools.OCTSliceViewer;
import com.joey.software.sliceTools.OCTSliceViewerDataHolder;


public class MicroNeedleAnalysis extends JPanel
{
	JComboBox expNames = new JComboBox();

	JComboBox viewNames = new JComboBox();

	LinkedHashMap<String, OCTExperimentData> expData = new LinkedHashMap<String, OCTExperimentData>();

	LinkedHashMap<OCTExperimentData, LinkedHashMap<String, OCTViewDataHolder>> viewData = new LinkedHashMap<OCTExperimentData, LinkedHashMap<String, OCTViewDataHolder>>();

	OCTSliceViewer viewer = new OCTSliceViewer();

	OCTExperimentData lastExp = null;

	OCTViewDataHolder latView = null;

	StatusBarPanel loadBar = null;

	BufferedImage data[] = null;

	ROIPanel viewPanel = new ROIPanel(false);

	JButton updateView = new JButton("Switch View");

	JButton saveImage = new JButton("Save View");

	JSpinner angle = new JSpinner(new SpinnerNumberModel(0, -360, 360, 1));

	JSpinner length = new JSpinner(new SpinnerNumberModel(20,
			Integer.MIN_VALUE / 100000, Integer.MAX_VALUE / 100000, 1));

	JSpinner project = new JSpinner(new SpinnerNumberModel(20,
			Integer.MIN_VALUE / 100000, Integer.MAX_VALUE / 100000, 1));

	JSlider alphaValue = new JSlider(0, 1000, 1000);

	JComboBox projectType = new JComboBox(new String[]
	{ "None", "Average", "Min", "Max" });

	AnalysisTool tool = new AnalysisTool(viewPanel);

	boolean blockUpdate = false;

	ArrayList<MeasurmentData> measures = new ArrayList<MeasurmentData>();

	MeasureTableModel model = new MeasureTableModel(this);

	JButton addMeasure = new JButton("Add Measure");

	JButton saveMeasure = new JButton("Save Measure");

	JButton delMeasure = new JButton("Del Measure");

	JTable table = new JTable(model);

	MeasurmentData currentMeasure = new MeasurmentData();

	FileSelectionField dataFile = new FileSelectionField();

	JMenuBar menuBar = new JMenuBar();

	JMenu fileMenu = new JMenu("File");

	JMenuItem open = new JMenuItem("Open");

	JMenuItem save = new JMenuItem("Save");

	JCheckBox plotReal = new JCheckBox("Graph Real");

	JFrame plotFrame = new JFrame("Data Plot");

	DataAnalysisViewer plotDataView = new DataAnalysisViewer(this);

	JButton showPlot = new JButton("Show Plot");

	public MicroNeedleAnalysis(OCTAnalysis program)
	{

		// FileSelectionField dataFile = new FileSelectionField();
		// FileSelectionField measureFile = new FileSelectionField();
		// dataFile
		// .setFieldText("c:\\users\\joey.enfield\\desktop\\forearm needle data.exp");
		//
		// dataFile.setLabelText("Data : ");
		// measureFile.setLabelText("Measure : ");
		//
		// JPanel hold = new JPanel(new GridLayout(2, 1));
		// hold.add(dataFile);
		// hold.add(measureFile);
		// program.getTopLevelAncestor().setVisible(false);
		// JOptionPane.showConfirmDialog(null, hold);

		// loadBar = program.getStatusBar();
		loadBar = new StatusBarPanel();
		tool.setOwner(this);

		File f = dataFile.getFile();
		// try
		// {
		// program.loadSet(f);
		// JFrame frame = FrameFactroy.getFrame(loadBar);
		// frame.setTitle("Loading Experiment Data");
		// frame.setSize(500, 150);
		// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//
		// while (VersionManager.loadingData == true)
		// {
		// try
		// {
		//
		// Thread.sleep(100);
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// frame.setVisible(false);
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }

		OCTSliceViewer viewer = new OCTSliceViewer();
		JPanel holder = new JPanel(new BorderLayout());
		holder.add(viewer, BorderLayout.CENTER);
		holder.add(loadBar, BorderLayout.SOUTH);
		viewer.setStatus(loadBar);

		ArrayList<OCTExperimentData> data = program.getExpData();
		for (int i = 0; i < data.size(); i++)
		{
			OCTExperimentData exp = data.get(i);

			expData.put(exp.getTitle(), exp);
			ArrayList<OCTViewDataHolder> viewData = exp.getViews();

			LinkedHashMap<String, OCTViewDataHolder> views = new LinkedHashMap<String, OCTViewDataHolder>();
			for (int v = 0; v < viewData.size(); v++)
			{

				views.put(viewData.get(v).getName(), viewData.get(v));
			}
			this.viewData.put(exp, views);
		}
		createJPanel();

		JFrame fra = FrameFactroy.getFrame(this);
		fra.setSize(1024, 800);
		fra.setJMenuBar(menuBar);
		// loadMeasure(measureFile.getFile());
	}

	public void loadMeasure()
	{
		loadMeasure(FileSelectionField.getUserOpenFile());
	}

	public void loadMeasure(File f)
	{
		measures.clear();
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			int num = in.readInt();
			for (int i = 0; i < num; i++)
			{
				System.out.println(i);
				MeasurmentData data = (MeasurmentData) in.readObject();
				model.addMeasure(data);
			}
			in.close();
		} catch (OptionalDataException e)
		{

			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error Loading");

		} catch (Exception e)
		{

			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error Loading");
			// createBlankData();
		}
	}

	public static String clearLabel(String label, String allowed)
	{
		StringBuilder rst = new StringBuilder();

		for (int i = 0; i < label.length(); i++)
		{
			for (int j = 0; j < allowed.length(); j++)
			{
				if (label.toLowerCase().charAt(i) == allowed.toLowerCase()
						.charAt(j))
				{
					rst.append(allowed.toLowerCase().charAt(j));
				}
			}
		}
		return rst.toString();
	}

	public void createBlankData()
	{
		if (true)
			return;
		String base = "c:\\users\\joey.enfield\\desktop\\FingerData\\";
		HashMap<String, File> outputFiles = new LinkedHashMap<String, File>();

		String[] arrayData = expData.keySet().toArray(new String[0]);
		for (int i = 0; i < arrayData.length; i++)
		{
			OCTExperimentData data = expData.get(arrayData[i]);
			ArrayList<OCTViewDataHolder> views = data.getViews();
			for (int j = 0; j < views.size(); j++)
			{
				OCTViewDataHolder view = views.get(j);

				String label = clearLabel(view.getName(), "abcdefghijklmnopqrstuvwzyz");
				File file = outputFiles.get(label);

				if (file == null)
				{
					String fileName = base + "fingerData[" + label
							+ "][crossed].dat";
					file = new File(fileName);
					measures.clear();
					outputFiles.put(label, file);
				} else
				{
					loadMeasure(file);
				}

				{
					MeasurmentData dataA = new MeasurmentData();
					dataA.exp = data.getTitle();
					dataA.view = view.getName();
					dataA.length = 100;
					dataA.projectNum = 20;
					dataA.angle = 45;
					dataA.surfaceData.setDataPoints(100);
					dataA.needleData.setDataPoints(200);
					dataA.riPore = 1;
					dataA.riSkin = 1;
					dataA.riSkinPore = 1;
					dataA.freeData.setDataPoints(200);
					model.addMeasure(dataA);
				}

				{
					MeasurmentData dataA = new MeasurmentData();
					dataA.exp = data.getTitle();
					dataA.view = view.getName();
					dataA.length = 100;
					dataA.projectNum = 20;
					dataA.angle = 135;
					dataA.surfaceData.setDataPoints(100);
					dataA.needleData.setDataPoints(200);
					dataA.riPore = 1;
					dataA.riSkin = 1;
					dataA.riSkinPore = 1;
					dataA.freeData.setDataPoints(200);
					model.addMeasure(dataA);
				}

				try
				{
					saveMeasure(file);
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public void saveMeasure() throws FileNotFoundException, IOException
	{
		saveMeasure(FileSelectionField.getUserSaveFile());
	}

	public void saveMeasure(File f) throws FileNotFoundException, IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeInt(measures.size());
		for (int i = 0; i < measures.size(); i++)
		{
			MeasurmentData data = measures.get(i);
			out.writeObject(data);
		}
		out.close();
	}

	public void updateMeasureData()
	{
		currentMeasure.grabData(this);
		model.updateCurrent();
		updatePlot();
	}

	public void createJPanel()
	{
		updateExpList();
		updateViewsList();
		tool.updateTool();

		/**
		 * Create menu Bar
		 */
		menuBar.add(fileMenu);
		fileMenu.add(open);
		fileMenu.add(save);

		open.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadMeasure();
			}
		});

		save.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					saveMeasure();
				} catch (FileNotFoundException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		/**
		 * Create Settings
		 */

		JPanel settingsPanel = new JPanel(new BorderLayout());
		int size = 90;
		int count = 0;
		settingsPanel.add(SwingToolkit.getLabel(angle, "Angle : ", size));
		count++;
		settingsPanel.add(SwingToolkit.getLabel(length, "Length : ", size));
		count++;
		settingsPanel.add(SwingToolkit.getLabel(project, "Number : ", size));
		count++;
		settingsPanel.add(SwingToolkit
				.getLabel(projectType, "Project : ", size));
		count++;
		settingsPanel.add(SwingToolkit.getLabel(updateView, "Update : ", size));
		count++;
		settingsPanel.add(SwingToolkit.getLabel(saveImage, "Save : ", size));
		count++;
		settingsPanel.add(SwingToolkit.getLabel(showPlot, "Plot : ", size));
		count++;
		settingsPanel.add(SwingToolkit.getLabel(plotReal, "Plot : ", size));
		count++;

		settingsPanel.setLayout(new GridLayout(count, 1));
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

		addMeasure.setEnabled(true);
		/**
		 * Table
		 */
		JPanel tableButtonPanel = new JPanel(new GridLayout(1, 2));
		tableButtonPanel.add(addMeasure);
		tableButtonPanel.add(delMeasure);

		JScrollPane scrollTable = new JScrollPane();
		scrollTable.setViewportView(table);

		JPanel tabHold = new JPanel(new BorderLayout());
		tabHold.add(table.getTableHeader(), BorderLayout.NORTH);
		tabHold.add(scrollTable, BorderLayout.CENTER);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tableButtonPanel, BorderLayout.NORTH);
		tablePanel.add(tabHold, BorderLayout.CENTER);
		/**
		 * Create View Controls
		 */
		JPanel viewControl = new JPanel(new FlowLayout(FlowLayout.CENTER));
		viewControl.add(SwingToolkit.getLabel(expNames, "Exps : ", 80));
		viewControl.add(SwingToolkit.getLabel(viewNames, "View : ", 80));

		JPanel toolHolder = new JPanel(new BorderLayout());
		toolHolder.add(alphaValue, BorderLayout.NORTH);
		toolHolder.add(tool.getControl(), BorderLayout.CENTER);
		toolHolder.setBorder(BorderFactory.createTitledBorder("Tool"));

		JPanel leftHolder = new JPanel(new BorderLayout());
		leftHolder.add(settingsPanel, BorderLayout.NORTH);
		leftHolder.add(toolHolder, BorderLayout.CENTER);

		/**
		 * Create Image Panel
		 */
		JPanel imageHolder = new JPanel();
		viewPanel.putIntoPanel(imageHolder);

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(leftHolder, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(holder);

		JSplitPane split = new JSplitPane();
		split.setLeftComponent(scroll);
		split.setRightComponent(imageHolder);
		split.setDividerLocation(430);

		JPanel main = new JPanel(new BorderLayout());
		main.add(viewControl, BorderLayout.NORTH);
		main.add(split, BorderLayout.CENTER);

		JSplitPane splitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitMain.setResizeWeight(1);
		splitMain.setDividerLocation(400);
		splitMain.setLeftComponent(main);
		splitMain.setRightComponent(tablePanel);

		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(loadBar, BorderLayout.SOUTH);
		tmp.add(splitMain, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(tmp, BorderLayout.CENTER);

		table.getSelectionModel().addListSelectionListener(model);
		table.setAutoCreateRowSorter(true);

		showPlot.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				showPlot();
			}

		});
		addMeasure.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				model.addNewMeasure();
			}
		});

		delMeasure.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				measures.remove(currentMeasure);
				model.fireTableDataChanged();
			}
		});
		saveImage.addActionListener(new ActionListener()
		{
			FileSelectionField output = new FileSelectionField();

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BufferedImage img = viewPanel.getImage();

					output
							.setFormat(FileSelectionField.FORMAT_IMAGE_FILES_SHOW_FORMAT);
					output.setType(FileSelectionField.TYPE_SAVE_FILE);

					if (output.getUserChoice())
					{
						File f = output.getFile();
						ImageIO.write(img, FileOperations.splitFile(f)[2], f);
					}
				} catch (Exception e1)
				{

				}
			}
		});
		alphaValue.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (!alphaValue.getValueIsAdjusting())
				{
					float val = (alphaValue.getValue() - alphaValue
							.getMinimum())
							/ ((float) alphaValue.getMaximum() - alphaValue
									.getMinimum());
					viewPanel.setAlpha(val);
				}
			}
		});

		angle.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				loadCurrentData();

			}
		});
		project.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{

				loadCurrentData();
			}
		});
		length.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{

				loadCurrentData();
			}
		});
		updateView.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadCurrentData();
			}
		});
		projectType.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateView();
			}
		});
		expNames.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateViewsList();
			}
		});
	}

	private void showPlot()
	{
		// TODO Auto-generated method stub
		plotFrame.getContentPane().setLayout(new BorderLayout());
		plotFrame.getContentPane().add(plotDataView, BorderLayout.CENTER);

		plotFrame.setVisible(true);
	}

	public void updatePlot()
	{
		if (plotReal.isSelected())
		{
			plotDataView.replotData();
		}
	}

	public void updateView()
	{

		if (blockUpdate)
		{
			return;
		}
		viewPanel.setImage(data[projectType.getSelectedIndex()]);
	}

	public void updateExpList()
	{
		((DefaultComboBoxModel) expNames.getModel()).removeAllElements();

		for (String s : expData.keySet())
		{
			((DefaultComboBoxModel) expNames.getModel()).addElement(s);
		}
	}

	public void updateViewsList()
	{

		((DefaultComboBoxModel) viewNames.getModel()).removeAllElements();

		HashMap<String, OCTViewDataHolder> dat = viewData
				.get(getCurrentExperimentData());
		for (String s : dat.keySet())
		{
			((DefaultComboBoxModel) viewNames.getModel()).addElement(s);
		}
	}

	public OCTExperimentData getCurrentExperimentData()
	{

		return expData.get(expNames.getSelectedItem());
	}

	public OCTViewDataHolder getCurrentViewDataHolder()
	{
		return viewData.get(getCurrentExperimentData()).get(viewNames
				.getSelectedItem());
	}

	public void loadCurrentData()
	{
		if (blockUpdate)
		{
			return;
		}
		OCTExperimentData exp = getCurrentExperimentData();
		OCTViewDataHolder view = getCurrentViewDataHolder();
		if (lastExp == null)
		{
			lastExp = exp;
			exp.getData().reloadData(loadBar);
		} else if (lastExp != exp)
		{
			lastExp.getData().unloadData();
			exp.getData().reloadData(loadBar);
			lastExp = exp;
		}

		OCTSliceViewerDataHolder slice = view.getSliceData();
		data = OCTSliceViewer
				.getImageData(viewer, view, exp.getData(), (Integer) project
						.getValue(), (Integer) length.getValue(), (Integer) angle
						.getValue());

		if (((Integer) angle.getValue()) == 90)
		{
			tool.currentExperiment.points.get(0).x = slice.p1z.y
					+ (Integer) length.getValue() - slice.getYSlicePlanePos();
			tool.currentExperiment.points.get(0).y = (slice.p1z.x);

			tool.currentExperiment.points.get(1).x = slice.p2z.y
					+ (Integer) length.getValue() - slice.getYSlicePlanePos();
			tool.currentExperiment.points.get(1).y = (slice.p2z.x);
		} else
		{
			tool.currentExperiment.points.get(0).x = slice.p1y.y
					+ (Integer) length.getValue() - slice.getZSlicePlanePos();
			tool.currentExperiment.points.get(0).y = (slice.p1y.x);

			tool.currentExperiment.points.get(1).x = slice.p2y.y
					+ (Integer) length.getValue() - slice.getZSlicePlanePos();
			tool.currentExperiment.points.get(1).y = (slice.p2y.x);

		}
		updatePixelSize();
		updateView();
		updateMeasureData();
	}

	public void updatePixelSize()
	{
		if (lastExp == null)
		{
			return;
		}

		tool.pixelSizeY
				.setValue(lastExp.getData().getPixelSizeX() / 1.33, true);
		tool.pixelSizeY.setPrefex(lastExp.getData().getPowerX(), false);

		tool.pixelSizeX
				.setValue(lastExp.getData().getPixelSizeX() / 1.33, true);
		tool.pixelSizeX.setPrefex(lastExp.getData().getPowerZ(), false);

	}

	public void setSetting()
	{
		JButton riButton = new JButton("Settings");
		{
			FrameFactroy.getFrame(riButton);
			riButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					setSetting();
				}
			});
		}
		for (MeasurmentData m : measures)
		{
			blockUpdate = true;
			m.setData(this);
			blockUpdate = false;
			loadCurrentData();
			m.poreA = tool.currentExperiment.points.get(0);
			m.poreB = tool.currentExperiment.points.get(1);
		}

	}

}

class AnalysisTool extends ROIControler implements ROIPanelListner
{
	public LineControler currentExperiment;

	public ImageProfileTool surfaceLine;

	public ImageProfileTool interfaceLine;

	public ImageProfileTool needleLine;

	public ImageProfileTool freeAScanTool;

	public LineControler skinMeasure;

	public LineControler poreMeasure;

	public LineControler poreSkinMeasure;

	public ProfilePlotter profileTool;

	JCheckBox showSurface = new JCheckBox("Surface");

	JCheckBox showInterface = new JCheckBox("Interface");

	JCheckBox showNeedleLine = new JCheckBox("Needle");

	JCheckBox showSkinMeasure = new JCheckBox("Skin");

	JCheckBox showPoreMeasure = new JCheckBox("Pore");

	JCheckBox showPoreSkinMeasure = new JCheckBox("Pore Skin");

	JCheckBox showFreeMeasure = new JCheckBox("Free");

	JCheckBox showCurrentExperiment = new JCheckBox("Exp Data");

	JCheckBox showProfileTool = new JCheckBox("Profile Tool");

	JComboBox measure = new JComboBox(new String[]
	{ "Surface", "Interface", "Needle Line", "Free Measure", "Skin Measure",
			"Pore Measure", "Pore Skin", "Orignal Exp", "Profile Tool" });

	NumberDimension pixelSizeX = new NumberDimension("m");

	NumberDimension pixelSizeY = new NumberDimension("m");

	JSpinner riPore = new JSpinner(new SpinnerNumberModel(1.000, 1.000, 10.000,
			0.01));

	JSpinner riSkin = new JSpinner(new SpinnerNumberModel(1.000, 1.000, 10.000,
			0.01));

	JSpinner riPoreSkin = new JSpinner(new SpinnerNumberModel(1.000, 1.000,
			10.000, 0.01));

	NumberDimension poreData = new NumberDimension("m");

	NumberDimension poreSkinData = new NumberDimension("m");

	NumberDimension skinData = new NumberDimension("m");

	JTextField poreDataPxl = new JTextField();

	JTextField poreSkinDataPxl = new JTextField();

	JTextField skinDataPxl = new JTextField();

	JPanel measurePanel = new JPanel();

	JPanel control = new JPanel();

	JPanel toolPanel = new JPanel();

	MicroNeedleAnalysis owner = null;

	public void setOwner(MicroNeedleAnalysis own)
	{
		this.owner = own;
	}

	public AnalysisTool(ROIPanel panel)
	{
		super(panel);
		panel.addROIPanelListner(this);
		surfaceLine = new ImageProfileTool(panel);
		interfaceLine = new ImageProfileTool(panel);
		needleLine = new ImageProfileTool(panel);
		freeAScanTool = new ImageProfileTool(panel);
		skinMeasure = new LineControler(panel);
		poreMeasure = new LineControler(panel);
		poreSkinMeasure = new LineControler(panel);
		currentExperiment = new LineControler(panel);
		profileTool = new ProfilePlotter(panel);

		surfaceLine.setPointColorSelected(Color.YELLOW);
		surfaceLine.setPointColorNotSelected(Color.PINK);
		surfaceLine.setOffsetColor(Color.YELLOW);

		surfaceLine.setAxis(ImageProfileTool.AXIS_Y);

		interfaceLine.setPointColorSelected(Color.CYAN);
		interfaceLine.setPointColorNotSelected(Color.BLUE);
		interfaceLine.setOffsetColor(Color.CYAN);
		interfaceLine.setAxis(ImageProfileTool.AXIS_Y);

		needleLine.setPointColorSelected(Color.GREEN);
		needleLine.setPointColorNotSelected(Color.MAGENTA);
		needleLine.setOffsetColor(Color.GREEN);
		needleLine.setAxis(ImageProfileTool.AXIS_Y);

		freeAScanTool.setAxis(ImageProfileTool.AXIS_Y);
		createPanel();
	}

	public void createPanel()
	{
		riPore.setEditor(new JSpinner.NumberEditor(riPore, "##.###"));
		riSkin.setEditor(new JSpinner.NumberEditor(riSkin, "##.###"));
		riPoreSkin.setEditor(new JSpinner.NumberEditor(riPoreSkin, "##.###"));

		skinData.setDigits(20);
		poreData.setDigits(20);
		poreSkinData.setDigits(20);

		skinData.setNumberformat("##.##");
		poreData.setNumberformat("##.##");
		poreSkinData.setNumberformat("##.##");

		poreData.setNumberEditable(false);
		skinData.setNumberEditable(false);
		poreSkinData.setNumberEditable(false);

		poreData.setIgnorePowerChange(false);
		skinData.setIgnorePowerChange(false);
		poreSkinData.setIgnorePowerChange(false);

		JPanel skinTemp = new JPanel(new GridLayout(1, 3));
		skinTemp.add(riSkin);
		skinTemp.add(skinDataPxl);
		JPanel skinMeasure = new JPanel(new BorderLayout());
		skinMeasure.add(skinData, BorderLayout.EAST);
		skinMeasure
				.add(SwingToolkit.getLabel(skinTemp, "Skin :", 50), BorderLayout.CENTER);

		JPanel poreTemp = new JPanel(new GridLayout(1, 2));
		poreTemp.add(riPore);
		poreTemp.add(poreDataPxl);

		JPanel poreMeasure = new JPanel(new BorderLayout());
		poreMeasure.add(poreData, BorderLayout.EAST);
		poreMeasure
				.add(SwingToolkit.getLabel(poreTemp, "Pore:", 50), BorderLayout.CENTER);

		JPanel poreSkinTemp = new JPanel(new GridLayout(1, 3));
		poreSkinTemp.add(riPoreSkin);
		poreSkinTemp.add(poreSkinDataPxl);

		JPanel poreSkinMeasure = new JPanel(new BorderLayout());
		poreSkinMeasure.add(poreSkinData, BorderLayout.EAST);
		poreSkinMeasure
				.add(SwingToolkit.getLabel(poreSkinTemp, "PoreS:", 50), BorderLayout.CENTER);

		JPanel measureData = new JPanel(new GridLayout(3, 1, 2, 0));
		measureData.add(skinMeasure);
		measureData.add(poreMeasure);
		measureData.add(poreSkinMeasure);

		JPanel hold = new JPanel(new BorderLayout());
		hold.add(measureData, BorderLayout.SOUTH);
		hold
				.add(SwingToolkit.getLabel(measure, "Measure :", 60), BorderLayout.CENTER);

		// skin
		JPanel views = new JPanel(new GridLayout(3, 3));
		views.add(showSurface);
		views.add(showNeedleLine);
		views.add(showInterface);
		views.add(showSkinMeasure);
		views.add(showPoreMeasure);
		views.add(showPoreSkinMeasure);
		views.add(showFreeMeasure);
		views.add(showCurrentExperiment);
		views.add(showProfileTool);

		toolPanel.setBorder(BorderFactory.createTitledBorder(""));
		views.setBorder(BorderFactory.createTitledBorder("Visible"));

		JPanel holder = new JPanel(new BorderLayout());
		holder.setLayout(new BorderLayout());
		holder.add(hold, BorderLayout.NORTH);
		holder.add(toolPanel, BorderLayout.SOUTH);
		holder.add(views, BorderLayout.CENTER);

		control.setLayout(new BorderLayout());
		control.add(holder, BorderLayout.NORTH);
		control.setBorder(BorderFactory.createTitledBorder("Controls"));
		ActionListener action = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateTool();
			}
		};

		measure.addActionListener(action);
		(showSurface).addActionListener(action);
		(showInterface).addActionListener(action);
		(showNeedleLine).addActionListener(action);
		(showSkinMeasure).addActionListener(action);
		showPoreSkinMeasure.addActionListener(action);
		(showPoreMeasure).addActionListener(action);
		showFreeMeasure.addActionListener(action);
		showCurrentExperiment.addActionListener(action);
		showProfileTool.addActionListener(action);
		riPore.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateMeasure();
			}
		});

		riSkin.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateMeasure();
			}
		});

		riPoreSkin.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateMeasure();
			}
		});
	}

	public JPanel getControl()
	{

		return control;
	}

	@Override
	public void draw(Graphics2D g)
	{

		if (showSurface.isSelected() || measure.getSelectedIndex() == 0)
		{
			surfaceLine.draw((Graphics2D) g.create());
		}

		if (showInterface.isSelected() || measure.getSelectedIndex() == 1)
		{
			interfaceLine.draw((Graphics2D) g.create());
		}

		if (showNeedleLine.isSelected() || measure.getSelectedIndex() == 2)
		{
			needleLine.draw((Graphics2D) g.create());
		}

		if (showFreeMeasure.isSelected() || measure.getSelectedIndex() == 3)
		{
			freeAScanTool.draw((Graphics2D) g.create());
		}

		if (showSkinMeasure.isSelected() || measure.getSelectedIndex() == 4)
		{
			skinMeasure.draw((Graphics2D) g.create());
		}

		if (showPoreMeasure.isSelected() || measure.getSelectedIndex() == 5)
		{
			poreMeasure.draw((Graphics2D) g.create());
		}

		if (showPoreSkinMeasure.isSelected() || measure.getSelectedIndex() == 6)
		{
			poreSkinMeasure.draw((Graphics2D) g.create());
		}

		if (showCurrentExperiment.isSelected()
				|| measure.getSelectedIndex() == 7)
		{
			currentExperiment.draw((Graphics2D) g.create());
		}

		if (measure.getSelectedIndex() == 8 || showProfileTool.isSelected())
		{
			profileTool.draw((Graphics2D) g.create());
		}
	}

	public void updateTool()
	{
		surfaceLine.setListening(false);
		interfaceLine.setListening(false);
		needleLine.setListening(false);
		skinMeasure.setListening(false);
		poreMeasure.setListening(false);
		freeAScanTool.setListening(false);
		poreSkinMeasure.setListening(false);
		currentExperiment.setListening(false);
		profileTool.setListening(false);
		toolPanel.removeAll();

		surfaceLine.setDrawCrosses(false);
		interfaceLine.setDrawCrosses(false);
		needleLine.setDrawCrosses(false);
		freeAScanTool.setDrawCrosses(false);

		if (measure.getSelectedIndex() == 0)
		{
			surfaceLine.setListening(true);
			surfaceLine.setDrawCrosses(true);
			toolPanel.setLayout(new BorderLayout());
			toolPanel.add(surfaceLine.getControls());
		} else if (measure.getSelectedIndex() == 1)
		{
			interfaceLine.setListening(true);
			interfaceLine.setDrawCrosses(true);
			toolPanel.setLayout(new BorderLayout());
			toolPanel.add(interfaceLine.getControls());
		} else if (measure.getSelectedIndex() == 2)
		{
			needleLine.setListening(true);
			needleLine.setDrawCrosses(true);
			toolPanel.setLayout(new BorderLayout());
			toolPanel.add(needleLine.getControls());
		} else if (measure.getSelectedIndex() == 3)
		{
			freeAScanTool.setListening(true);
			freeAScanTool.setDrawCrosses(true);
			toolPanel.setLayout(new BorderLayout());
			toolPanel.add(freeAScanTool.getControls());
		} else if (measure.getSelectedIndex() == 4)
		{
			skinMeasure.setListening(true);

		} else if (measure.getSelectedIndex() == 5)
		{
			poreMeasure.setListening(true);
		} else if (measure.getSelectedIndex() == 6)
		{
			poreSkinMeasure.setListening(true);
		} else if (measure.getSelectedIndex() == 7)
		{
			currentExperiment.setListening(true);
		} else if (measure.getSelectedIndex() == 8)
		{
			profileTool.setListening(true);

			toolPanel.setLayout(new BorderLayout());
			toolPanel.add(profileTool.getToolPanel());
		}

		getPanel().setControler(this);

		try
		{
			control.getTopLevelAncestor().validate();
		} catch (Exception e)
		{

		}
	}

	public void updateMeasure()
	{
		DecimalFormat format = new DecimalFormat("###.#");

		skinDataPxl.setText(format.format(getLength(skinMeasure, 1, 1)));
		int old = skinData.getPrefex();
		skinData
				.setValue(getLength(skinMeasure, pixelSizeX.getValue(), pixelSizeY
						.getValue())
						/ (Double) riSkin.getValue(), true);
		skinData.setPrefex(old, true);

		poreDataPxl.setText(format.format(getLength(poreMeasure, 1, 1)
				/ (Double) riPore.getValue()));
		old = poreData.getPrefex();
		poreData
				.setValue(getLength(poreMeasure, pixelSizeX.getValue(), pixelSizeY
						.getValue()
						/ (Double) riPore.getValue()), true);
		poreData.setPrefex(old, true);

		poreSkinDataPxl.setText(format.format(getLength(poreSkinMeasure, 1, 1)
				/ (Double) riPoreSkin.getValue()));
		old = poreSkinData.getPrefex();
		poreSkinData
				.setValue(getLength(poreSkinMeasure, pixelSizeX.getValue(), pixelSizeY
						.getValue()
						/ (Double) riPoreSkin.getValue()), true);
		poreSkinData.setPrefex(old, true);

		if (owner != null)
		{
			owner.updateMeasureData();
		}
	}

	public double getLength(LineControler dat, double pxlX, double pxlY)
	{
		double dx = dat.points.get(0).x - dat.points.get(1).x;
		double dy = dat.points.get(0).y - dat.points.get(1).y;

		dx *= pxlX;
		dy *= pxlY;

		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public void regionAdded(Shape region)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void regionChanged()
	{
		updateMeasure();
	}

	@Override
	public void regionRemoved(Shape region)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public JPanel getControlPanel()
	{
		// TODO Auto-generated method stub
		return getControl();
	}
}

class MeasureTableModel extends DefaultTableModel implements
		ListSelectionListener
{
	MicroNeedleAnalysis owner;

	public MeasureTableModel(MicroNeedleAnalysis owner)
	{
		this.owner = owner;
	}

	public void updateCurrent()
	{
		int row = getCurrentMeasureIndex();
		if (row < 0)
		{
			return;
		}
		for (int i = 0; i < getColumnCount(); i++)
			fireTableCellUpdated(row, i);
	}

	public int getCurrentMeasureIndex()
	{
		return owner.measures.indexOf(owner.currentMeasure);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			case 3:
				return Integer.class;
			case 4:
				return Integer.class;
			case 5:
				return Double.class;
			case 6:
				return Double.class;
			case 7:
				return Double.class;
			case 8:
				return Double.class;
		}
		return String.class;

	}

	@Override
	public int getColumnCount()
	{
		// TODO Auto-generated method stub
		return 9;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return "Experiment";
			case 1:
				return "View";
			case 2:
				return "Length";
			case 3:
				return "Project";
			case 4:
				return "Angle";
			case 5:
				return "Skin";
			case 6:
				return "Pore";
			case 7:
				return "Pore Skin";
			case 8:
				return "Total";
		}
		return "";
	}

	@Override
	public int getRowCount()
	{
		if (owner == null)
		{
			return 0;
		}
		// TODO Auto-generated method stub
		return owner.measures.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		MeasurmentData measure = owner.measures.get(rowIndex);
		switch (columnIndex)
		{
			case 0:
				return measure.exp;
			case 1:
				return measure.view;
			case 2:
				return measure.length;
			case 3:
				return measure.projectNum;
			case 4:
				return measure.angle;
			case 5:
				return measure.skinDim.getValue();
			case 6:
				return measure.poreDim.getValue();
			case 7:
				return measure.skinPoreDim.getValue();
			case 8:
				return measure.skinPoreDim.getValue()
						+ measure.poreDim.getValue();
		}
		return "";
	}

	public void addNewMeasure()
	{
		MeasurmentData measure = new MeasurmentData();
		measure.grabData(owner);
		addMeasure(measure);
	}

	public void addMeasure(MeasurmentData measure)
	{
		owner.measures.add(measure);
		fireTableRowsInserted(owner.measures.size() - 1, owner.measures.size() - 1);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		System.out
				.println("####################\n###############\n#############MicroNeedleAnalyssi : Value Changed############\n################\n##############");
		owner.currentMeasure.grabData(owner);

		if (owner.table.getSelectedRow() < 0)
		{
			return;
		}
		int index = owner.table.getRowSorter()
				.convertRowIndexToModel(owner.table.getSelectedRow());
		MeasurmentData newMeasure = owner.measures.get(index);
		owner.currentMeasure = new MeasurmentData();
		newMeasure.setData(owner);
		owner.currentMeasure = newMeasure;

	}

}
