package com.joey.software.sliceTools;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.DataToolkit.OCTDataSetPanel;
import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.Utils;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.geomertyToolkit.GeomertyToolkit;
import com.joey.software.imageProcessing.OCTImageProcessingTool;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMapTools;
import com.joey.software.mainProgram.OCTViewDataHolder;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.mathsToolkit.MathsToolkit;
import com.joey.software.mathsToolkit.NumberDimension;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.regionSelectionToolkit.controlers.ImageProfileTool;
import com.joey.software.regionSelectionToolkit.controlers.LineControler;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;


/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class OCTSliceViewer extends JPanel implements MouseMotionListener,
		MouseListener, ChangeListener, ActionListener, Externalizable,
		ROIPanelListner
{

	public static void main(String[] input) throws IOException
	{
		File dataFile = new File("c:\\test\\micro\\raw.dat");
		File previewFile = new File("c:\\test\\micro\\prv.dat");

		NativeDataSet octData = new NativeDataSet(dataFile, previewFile);

		final OCTSliceViewer panel = new OCTSliceViewer();
		panel.setOCTData(octData);

		FrameFactroy.getFrame(panel);
		// final MemoryUsagePanel u = new MemoryUsagePanel(500, 100);
		// JFrame f = new JFrame("Memory Usage");
		// f.setLayout(new BorderLayout());
		// f.getContentPane().add(u, BorderLayout.CENTER);
		// f.setSize(800, 400);
		// f.setVisible(true);
		// JButton clear = new JButton("Clear");
		// clear.addActionListener(new ActionListener()
		// {
		//
		// boolean loaded = true;
		//
		// @Override
		// public void actionPerformed(ActionEvent e)
		// {
		// if (loaded)
		// {
		// try
		// {
		// loaded = false;
		// panel.unloadData();
		// } catch (IOException e1)
		// {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// } else
		// {
		// loaded = true;
		// panel.reloadData();
		// }
		// }
		// });
		//
		// JButton garbage = new JButton("Running");
		// garbage.addActionListener(new ActionListener()
		// {
		//
		// boolean running = false;
		//
		// @Override
		// public void actionPerformed(ActionEvent e)
		// {
		// if (running)
		// {
		// running = false;
		// u.stopUpdating();
		// } else
		// {
		// running = true;
		// u.startUpdating();
		// }
		// }
		// });
		//
		// JPanel temp = new JPanel(new GridLayout(1, 2));
		// temp.add(clear);
		// temp.add(garbage);
		//
		// f.getContentPane().add(temp, BorderLayout.SOUTH);
		// UserChoiceColorMap user = new UserChoiceColorMap();
		// user.updateVolumeData(octData.getPreviewData());
		// FrameFactroy.getFrame(user.getUserSelectionPanel());

	}

	NumberFormat numformat = NumberFormat.getInstance();
	{
		numformat.setGroupingUsed(false);
	}

	private static final long serialVersionUID = 1L;

	NativeDataSet data;

	RandomAccessFile file;

	SlicePanel xSlicePanel;

	SlicePanel ySlicePanel;

	SlicePanel zSlicePanel;

	JButton colorMapButton = new JButton("Change");

	JButton exportFrame = new JButton("Export Frame");

	JButton showFreeSlice = new JButton("Show Slice");

	JButton showProjection = new JButton("Show");

	JTabbedPane measureTab = new JTabbedPane();

	JTabbedPane toolTab = new JTabbedPane();

	JTabbedPane controlTabPanel = new JTabbedPane();

	JSpinner projectNumber = new JSpinner(new SpinnerNumberModel(0, -1000, 1000,
			1));

	boolean allowProjection = false;

	JComboBox projectType = new JComboBox(new String[]
	{ "None", "Avg", "Min", "Max" });

	boolean mouseDragging = false;

	boolean updateAllowed = true;

	BufferedImage avgData[];

	/*
	 * The next set of variables are to deal with the preview panel
	 */
	int previewAxes;

	int previewPos;

	ROIPanel previewPanel = new ROIPanel(false);

	LineControler noMeasureOverlay = new LineControler(previewPanel);

	/**
	 * Profile tools for each axis
	 */
	JPanel profileControlHolder = new JPanel();// This holds the current axis

	// profile panel

	ImageProfileTool xProfileTool = new ImageProfileTool(previewPanel);

	ImageProfileTool yProfileTool = new ImageProfileTool(previewPanel);

	ImageProfileTool zProfileTool = new ImageProfileTool(previewPanel);

	/**
	 * Measure between two points
	 */
	LineControler xLineMeasure = new LineControler(previewPanel);

	LineControler yLineMeasure = new LineControler(previewPanel);

	LineControler zLineMeasure = new LineControler(previewPanel);

	PolygonControler xPathMeasure = new PolygonControler(previewPanel);

	PolygonControler yPathMeasure = new PolygonControler(previewPanel);

	PolygonControler zPathMeasure = new PolygonControler(previewPanel);

	PolygonControler xAreaMeasure = new PolygonControler(previewPanel);

	PolygonControler yAreaMeasure = new PolygonControler(previewPanel);

	PolygonControler zAreaMeasure = new PolygonControler(previewPanel);

	OCTImageProcessingTool imageProcesser = new OCTImageProcessingTool();

	LineControler xFreeSlice = new LineControler(previewPanel);

	LineControler yFreeSlice = new LineControler(previewPanel);

	LineControler zFreeSlice = new LineControler(previewPanel);

	boolean blockFreeSliceUpdate = false;

	JSpinner freeSliceDepthStart = new JSpinner(new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1));

	JSpinner freeSliceDepthEnd = new JSpinner(new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1));

	JFrame freeSliceFrame = null;

	ImagePanel freeSlicePanel = new ImagePanel();

	BufferedImage[] freeSliceData = new BufferedImage[4];

	JCheckBox freeSliceRealTimeUpdate = new JCheckBox("Real Update");

	JSpinner freeSliceProjectNumber = new JSpinner(new SpinnerNumberModel(0,
			-100, 100, 1));

	JComboBox freeSliceProjectType = new JComboBox(new String[]
	{ "Avg", "Max", "Min", "Custom" });

	JButton saveFreeSliceProjectImage = new JButton("Save Image");

	/**
	 * 
	 * This is if the slider should update every image or just when the slice
	 * sliders are set in position
	 */
	JCheckBox continousSliderUpdate = new JCheckBox();

	JCheckBox renderHighRes = new JCheckBox();

	JCheckBox showPositionLines = new JCheckBox();

	StatusBarPanel status = null;

	JPanel slicePanel = new JPanel();

	OCTDataSetPanel dataSetPanel = null;

	JPanel lineMeasurePanel = new JPanel();

	JPanel pathMeasurePanel = new JPanel();

	JPanel areaMeasurePanel = new JPanel();

	JTextField xPathPxlField = new JTextField(10);

	JTextField yPathPxlField = new JTextField(10);

	JTextField zPathPxlField = new JTextField(10);

	JTextField xPathSizeField = new JTextField(10);

	JTextField yPathSizeField = new JTextField(10);

	JTextField zPathSizeField = new JTextField(10);

	JTextField xAreaPxlField = new JTextField(10);

	JTextField yAreaPxlField = new JTextField(10);

	JTextField zAreaPxlField = new JTextField(10);

	JTextField xAreaSizeField = new JTextField(10);

	JTextField yAreaSizeField = new JTextField(10);

	JTextField zAreaSizeField = new JTextField(10);

	JTextField xSizeField = new JTextField(10);

	JTextField xDxField = new JTextField(10);

	JTextField xDyField = new JTextField(10);

	JTextField ySizeField = new JTextField(10);

	JTextField yDxField = new JTextField(10);

	JTextField yDyField = new JTextField(10);

	JTextField zSizeField = new JTextField(10);

	JTextField zDxField = new JTextField(10);

	JTextField zDyField = new JTextField(10);

	JButton updateField = new JButton("Update Measure");

	JSlider lineTransparency = new JSlider(0, 100, 100);

	JSlider measureTransparency = new JSlider(0, 100, 100);

	boolean blockUpdate = false;

	public OCTSliceViewer()
	{

		super();

		xAreaMeasure.setDrawClosePath(true);
		yAreaMeasure.setDrawClosePath(true);
		zAreaMeasure.setDrawClosePath(true);

		previewPanel.setNavigationImageEnabled(true);
		xSlicePanel = new SlicePanel(300, 500, this);
		ySlicePanel = new SlicePanel(300, 500, this);
		zSlicePanel = new SlicePanel(300, 500, this);
		previewPanel.setPanelType(ImagePanel.TYPE_CUSTOM_SCALE);
		previewPanel.setUseAlpha(true);

		setPreviewAxes(NativeDataSet.X_SLICE);
		// Set up measure Tool

		noMeasureOverlay.setVisible(false);

		previewPanel.setHighQualityRenderingEnabled(true);
		try
		{
			NativeDataSet data = new NativeDataSet();
			setOCTData(data);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		createJPanel();

	}

	public void setDataSetPanel(OCTDataSetPanel data)
	{
		this.dataSetPanel = data;
	}

	public void unloadData()
	{
		previewPanel.setImage(ImageOperations.getBi(1));
		setVisible(false);
	}

	public void reloadData()
	{
		updatePreviewPanel(renderHighRes.isSelected());
		setVisible(true);
		repaint();
	}

	public LineControler getLineMeasureTool()
	{
		if (getPreviewAxes() == NativeDataSet.X_SLICE)
		{
			return xLineMeasure;
		} else if (getPreviewAxes() == NativeDataSet.Y_SLICE)
		{
			return yLineMeasure;
		} else if (getPreviewAxes() == NativeDataSet.Z_SLICE)
		{
			return zLineMeasure;
		} else
		{
			throw new InvalidParameterException("Error- not real axis");
		}
	}

	public void createJPanel()
	{
		/**
		 * Create the slice selector
		 */

		// xSlicePanel.setBorder(BorderFactory
		// .createTitledBorder("X Slice - ZY Plane"));
		// ySlicePanel.setBorder(BorderFactory
		// .createTitledBorder("Y Slice - XZ Plane"));
		// zSlicePanel.setBorder(BorderFactory
		// .createTitledBorder("Z Slice - XY Plane"));
		// slicePanel.setLayout(new BoxLayout(slicePanel, BoxLayout.Y_AXIS));
		slicePanel.setLayout(new GridLayout(3, 1));
		slicePanel.setBorder(BorderFactory.createTitledBorder(""));
		slicePanel.add(xSlicePanel);
		slicePanel.add(ySlicePanel);
		slicePanel.add(zSlicePanel);

		JPanel lineTool = new JPanel(new BorderLayout());
		lineTool.add(showPositionLines, BorderLayout.WEST);
		lineTool.add(lineTransparency, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(""));

		SwingToolkit.createPanel(new String[]
		{ "Show Lines : ", "Continous :", "High Res : ", "Color Map :",
				"Export Frame :", }, new JComponent[]
		{ lineTool, continousSliderUpdate, renderHighRes, colorMapButton,
				exportFrame }, 80, controlPanel);

		JPanel contPanel = new JPanel(new BorderLayout());
		contPanel.add(controlPanel, BorderLayout.NORTH);

		/**
		 * Create the line measure panel
		 */
		{
			JPanel measureLabels = new JPanel(new GridLayout(4, 1));
			measureLabels.add(new JPanel());

			JLabel xField = new JLabel("X Plane");
			JLabel yField = new JLabel("Y Plane");
			JLabel zField = new JLabel("Z Plane");
			xField.setText("X: ");
			yField.setText("Y: ");
			zField.setText("Z: ");
			xField.setForeground(SlicePanel.X_AXIS_COLOR);
			yField.setForeground(SlicePanel.Y_AXIS_COLOR);
			zField.setForeground(SlicePanel.Z_AXIS_COLOR);
			measureLabels.add(xField);
			measureLabels.add(yField);
			measureLabels.add(zField);

			JLabel diffXLabel = new JLabel("Diff X");
			JLabel diffYLabel = new JLabel("Diff Y");
			JLabel lengthLabel = new JLabel("Length");

			diffXLabel.setHorizontalAlignment(SwingConstants.CENTER);
			diffYLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lengthLabel.setHorizontalAlignment(SwingConstants.CENTER);

			JPanel sizeFields = new JPanel(new GridLayout(4, 3));
			sizeFields.add(diffXLabel);
			sizeFields.add(diffYLabel);
			sizeFields.add(lengthLabel);
			sizeFields.add(xDxField);
			sizeFields.add(xDyField);
			sizeFields.add(xSizeField);
			sizeFields.add(yDxField);
			sizeFields.add(yDyField);
			sizeFields.add(ySizeField);
			sizeFields.add(zDxField);
			sizeFields.add(zDyField);
			sizeFields.add(zSizeField);

			JPanel dataField = new JPanel();
			dataField.setLayout(new BorderLayout());
			dataField.add(measureLabels, BorderLayout.WEST);
			dataField.add(sizeFields, BorderLayout.CENTER);

			lineMeasurePanel.setLayout(new BorderLayout());
			lineMeasurePanel.add(dataField, BorderLayout.NORTH);
			lineMeasurePanel.setBorder(BorderFactory.createTitledBorder(""));
		}

		{
			JPanel measureLabels = new JPanel(new GridLayout(4, 1));
			measureLabels.add(new JPanel());

			JLabel xField = new JLabel("X Plane");
			JLabel yField = new JLabel("Y Plane");
			JLabel zField = new JLabel("Z Plane");
			xField.setText("X: ");
			yField.setText("Y: ");
			zField.setText("Z: ");
			xField.setForeground(SlicePanel.X_AXIS_COLOR);
			yField.setForeground(SlicePanel.Y_AXIS_COLOR);
			zField.setForeground(SlicePanel.Z_AXIS_COLOR);
			measureLabels.add(xField);
			measureLabels.add(yField);
			measureLabels.add(zField);

			JLabel pxlsLabel = new JLabel("Pixels");
			JLabel sizeLabel = new JLabel("Size");

			pxlsLabel.setHorizontalAlignment(SwingConstants.CENTER);
			sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);

			JPanel sizeFields = new JPanel(new GridLayout(4, 2));
			sizeFields.add(pxlsLabel);
			sizeFields.add(sizeLabel);
			sizeFields.add(xPathPxlField);
			sizeFields.add(xPathSizeField);
			sizeFields.add(yPathPxlField);
			sizeFields.add(yPathSizeField);
			sizeFields.add(zPathPxlField);
			sizeFields.add(zPathSizeField);

			JPanel dataField = new JPanel();
			dataField.setLayout(new BorderLayout());
			dataField.add(measureLabels, BorderLayout.WEST);
			dataField.add(sizeFields, BorderLayout.CENTER);

			pathMeasurePanel.setLayout(new BorderLayout());
			pathMeasurePanel.add(dataField, BorderLayout.NORTH);
			pathMeasurePanel.setBorder(BorderFactory.createTitledBorder(""));
		}

		{
			JPanel measureLabels = new JPanel(new GridLayout(4, 1));
			measureLabels.add(new JPanel());

			JLabel xField = new JLabel("X Plane");
			JLabel yField = new JLabel("Y Plane");
			JLabel zField = new JLabel("Z Plane");
			xField.setText("X: ");
			yField.setText("Y: ");
			zField.setText("Z: ");
			xField.setForeground(SlicePanel.X_AXIS_COLOR);
			yField.setForeground(SlicePanel.Y_AXIS_COLOR);
			zField.setForeground(SlicePanel.Z_AXIS_COLOR);
			measureLabels.add(xField);
			measureLabels.add(yField);
			measureLabels.add(zField);

			JLabel pxlsLabel = new JLabel("Pixels");
			JLabel sizeLabel = new JLabel("Size");

			pxlsLabel.setHorizontalAlignment(SwingConstants.CENTER);
			sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);

			JPanel sizeFields = new JPanel(new GridLayout(4, 2));
			sizeFields.add(pxlsLabel);
			sizeFields.add(sizeLabel);
			sizeFields.add(xAreaPxlField);
			sizeFields.add(xAreaSizeField);
			sizeFields.add(yAreaPxlField);
			sizeFields.add(yAreaSizeField);
			sizeFields.add(zAreaPxlField);
			sizeFields.add(zAreaSizeField);

			JPanel dataField = new JPanel();
			dataField.setLayout(new BorderLayout());
			dataField.add(measureLabels, BorderLayout.WEST);
			dataField.add(sizeFields, BorderLayout.CENTER);

			areaMeasurePanel.setLayout(new BorderLayout());
			areaMeasurePanel.add(dataField, BorderLayout.NORTH);
			areaMeasurePanel.setBorder(BorderFactory.createTitledBorder(""));
		}
		measureTab.addTab("Line", lineMeasurePanel);
		measureTab.addTab("Path", pathMeasurePanel);
		measureTab.addTab("Area", areaMeasurePanel);

		JPanel measureTransparancy = new JPanel(new BorderLayout());
		measureTransparancy.add(new JLabel("Opacity :"), BorderLayout.WEST);
		measureTransparancy.add(measureTransparency, BorderLayout.CENTER);

		JPanel measurePanel = new JPanel(new BorderLayout());
		measurePanel.add(measureTab, BorderLayout.CENTER);
		measurePanel.add(measureTransparancy, BorderLayout.NORTH);
		measurePanel.add(updateField, BorderLayout.SOUTH);
		measurePanel.setBorder(BorderFactory.createTitledBorder(""));

		JLabel projectNumberLabel = new JLabel("Number : ");
		projectNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		projectNumberLabel.setPreferredSize(new Dimension(70, 0));
		JPanel projectNumberPanel = new JPanel(new BorderLayout());
		projectNumberPanel.add(projectNumberLabel, BorderLayout.WEST);
		projectNumberPanel.add(projectNumber, BorderLayout.CENTER);

		JLabel projectTypeLabel = new JLabel("Type : ");
		projectTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		projectTypeLabel.setPreferredSize(new Dimension(70, 0));
		JPanel projectTypePanel = new JPanel(new BorderLayout());
		projectTypePanel.add(projectTypeLabel, BorderLayout.WEST);
		projectTypePanel.add(projectType, BorderLayout.CENTER);

		JPanel projectPanel = new JPanel(new GridLayout(3, 1));
		projectPanel.add(projectNumberPanel);
		projectPanel.add(projectTypePanel);
		projectPanel.add(showProjection);

		JPanel projectHolder = new JPanel(new BorderLayout());
		projectHolder.add(projectPanel, BorderLayout.NORTH);

		/**
		 * Free Slice Panel
		 */

		JPanel freeSlicePanel = new JPanel(new GridLayout(6, 1));
		freeSlicePanel.add(SwingToolkit
				.getLabel(freeSliceDepthStart, "Start :", 50));
		freeSlicePanel.add(SwingToolkit
				.getLabel(freeSliceDepthEnd, "End :", 50));
		freeSlicePanel.add(SwingToolkit
				.getLabel(freeSliceProjectType, "Type :", 50));
		freeSlicePanel.add(SwingToolkit
				.getLabel(freeSliceProjectNumber, "Num :", 50));
		freeSlicePanel.add(SwingToolkit
				.getLabel(saveFreeSliceProjectImage, "Save :", 50));
		freeSlicePanel.add(SwingToolkit.getLabel(showFreeSlice, "Show :", 50));
		JPanel freeSlicePanelHolder = new JPanel(new BorderLayout());
		freeSlicePanelHolder.add(freeSlicePanel, BorderLayout.NORTH);

		toolTab.addTab("Profile", profileControlHolder);
		toolTab.addTab("Projections", projectHolder);
		toolTab.addTab("Free Slice", freeSlicePanelHolder);

		controlTabPanel.addTab("Main", contPanel);
		controlTabPanel.addTab("Measure", measurePanel);
		controlTabPanel.addTab("Tool", toolTab);

		saveFreeSliceProjectImage.addActionListener(new ActionListener()
		{
			FileSelectionField save = new FileSelectionField();

			@Override
			public void actionPerformed(ActionEvent e)
			{

				save.setType(FileSelectionField.TYPE_SAVE_FILE);
				save
						.setFormat(FileSelectionField.FORMAT_IMAGE_FILES_SHOW_FORMAT);

				if (save.getUserChoice())
				{
					try
					{
						File f = save.getFile();

						ImageIO.write(OCTSliceViewer.this.freeSlicePanel
								.getImage(), FileOperations.splitFile(f)[2], f);
					} catch (Exception ee)
					{
						JOptionPane
								.showMessageDialog(null, "Error Saving Image : "
										+ ee.getLocalizedMessage());
					}
				}

			}
		});
		ChangeListener freeSliceChange = new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateFreeSlice();
			}
		};
		freeSliceDepthEnd.addChangeListener(freeSliceChange);
		freeSliceDepthStart.addChangeListener(freeSliceChange);
		freeSliceProjectNumber.addChangeListener(freeSliceChange);
		freeSliceProjectType.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				OCTSliceViewer.this.freeSlicePanel
						.setImage(freeSliceData[freeSliceProjectType
								.getSelectedIndex()]);

			}
		});

		showFreeSlice.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateFreeSlice();

			}
		});
		showProjection.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updatePreviewPanel(renderHighRes.isSelected());

			}
		});
		/*
		 * 
		 * This is were the tools are switched
		 */
		ChangeListener change = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateToolOverlay();
			}
		};

		controlTabPanel.addChangeListener(change);
		toolTab.addChangeListener(change);
		measureTab.addChangeListener(change);

		JSplitPane toolSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		toolSplit.add(controlTabPanel);
		toolSplit.add(slicePanel);
		toolSplit.setOneTouchExpandable(true);
		toolSplit.setContinuousLayout(false);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder(""));
		leftPanel.add(toolSplit, BorderLayout.CENTER);

		JPanel previewScroll = new JPanel(new BorderLayout());
		previewScroll.add(previewPanel, BorderLayout.CENTER);
		previewScroll.add(previewPanel.getHorScroll(), BorderLayout.SOUTH);
		previewScroll.add(previewPanel.getVerScroll(), BorderLayout.EAST);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel
				.add(previewPanel.getImageInformationPanel(), BorderLayout.SOUTH);
		rightPanel.add(previewScroll, BorderLayout.CENTER);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setLeftComponent(leftPanel);
		splitPanel.setRightComponent(rightPanel);
		splitPanel.setDividerLocation(260);
		splitPanel.setOneTouchExpandable(true);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(splitPanel, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		renderHighRes.setRolloverEnabled(false);
		showPositionLines.setRolloverEnabled(false);

		/**
		 * add all the listners
		 */
		xSlicePanel.imgPanel.addMouseListener(this);
		xSlicePanel.imgPanel.addMouseMotionListener(this);
		xSlicePanel.pos.addChangeListener(this);

		ySlicePanel.imgPanel.addMouseListener(this);
		ySlicePanel.imgPanel.addMouseMotionListener(this);
		ySlicePanel.pos.addChangeListener(this);

		zSlicePanel.imgPanel.addMouseListener(this);
		zSlicePanel.imgPanel.addMouseMotionListener(this);
		zSlicePanel.pos.addChangeListener(this);

		colorMapButton.addActionListener(this);

		previewPanel.addROIPanelListner(this);

		lineTransparency.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updatePreviewPanel(renderHighRes.isSelected());
			}
		});

		measureTransparency.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				previewPanel.setAlpha((float) measureTransparency.getValue()
						/ (float) measureTransparency.getMaximum());
				updatePreviewPanel(renderHighRes.isSelected());
			}
		});

		renderHighRes.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (isUpdateAllowed())
				{

					// Correct the scale points when change happends
					double scaleX = 0;
					double scaleY = 0;
					if (getPreviewAxes() == NativeDataSet.X_SLICE)
					{
						scaleX = data.getPreviewScaleZ();
						scaleY = data.getPreviewScaleY();

					} else if (getPreviewAxes() == NativeDataSet.Y_SLICE)
					{
						scaleX = data.getPreviewScaleX();
						scaleY = data.getPreviewScaleZ();
					} else if (getPreviewAxes() == NativeDataSet.Z_SLICE)
					{
						scaleX = data.getPreviewScaleZ();
						scaleY = data.getPreviewScaleY();
					}

					if (!renderHighRes.isSelected())
					{
						scaleX = 1 / scaleX;
						scaleY = 1 / scaleY;
					}

					for (Point2D.Double p : xLineMeasure.points)
					{
						p.x *= scaleX;
						p.y *= scaleY;
					}
					for (Point2D.Double p : yLineMeasure.points)
					{
						p.x *= scaleX;
						p.y *= scaleY;
					}
					for (Point2D.Double p : zLineMeasure.points)
					{
						p.x *= scaleX;
						p.y *= scaleY;
					}

					updatePreviewPanel(renderHighRes.isSelected());
				}
			}
		});

		showPositionLines.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updatePreviewPanel(renderHighRes.isSelected());
			}
		});
		exportFrame.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				exportCurrentFrame();

			}
		});

		updateField.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateMeasurement(true);
			}
		});
	}

	/**
	 * This will return the size that is measured in the x slice plane
	 * 
	 * @return
	 */

	public double getLineMeasureX()
	{
		return getLineMeasureX(false);
	}

	public double getLineMeasureX(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeZ();
			scaleY = data.getPixelSizeY();
		} else
		{
			scaleX = data.getPreviewPixelSizeZ();
			scaleY = data.getPreviewPixelSizeY();
		}

		double dx = xLineMeasure.points.get(0).x - xLineMeasure.points.get(1).x;
		double dy = xLineMeasure.points.get(0).y - xLineMeasure.points.get(1).y;

		dx *= scaleX;
		dy *= scaleY;
		double val = Math.sqrt(dx * dx + dy * dy);

		int power = data.getPowerX();

		if (update)
		{
			// xDxField.setText(numformat.format(Math.abs(dx)));
			// xDyField.setText(numformat.format(Math.abs(dy)));
			// xSizeField.setText(numformat.format(val));

			xDxField.setText(NumberDimension.getValue(dx, power, "m"));
			xDyField.setText(NumberDimension.getValue(dy, power, "m"));
			xSizeField.setText(NumberDimension.getValue(val, power, "m"));

		}
		return val;
	}

	public double getLineMeasureY()
	{
		return getLineMeasureY(false);
	}

	public double getLineMeasureY(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeX();
			scaleY = data.getPixelSizeZ();
		} else
		{
			scaleX = data.getPreviewPixelSizeX();
			scaleY = data.getPreviewPixelSizeZ();
		}

		double dx = yLineMeasure.points.get(0).x - yLineMeasure.points.get(1).x;
		double dy = yLineMeasure.points.get(0).y - yLineMeasure.points.get(1).y;

		dx *= scaleX;
		dy *= scaleY;
		double val = Math.sqrt(dx * dx + dy * dy);

		int power = data.getPowerY();

		if (update)
		{
			yDxField.setText(NumberDimension.getValue(dx, power, "m"));
			yDyField.setText(NumberDimension.getValue(dy, power, "m"));
			ySizeField.setText(NumberDimension.getValue(val, power, "m"));
		}
		return val;
	}

	public double getLineMeasureZ()
	{
		return getLineMeasureZ(false);
	}

	public double getPathMeasureX(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeZ();
			scaleY = data.getPixelSizeY();
		} else
		{
			scaleX = data.getPreviewPixelSizeZ();
			scaleY = data.getPreviewPixelSizeY();
		}

		Path2D path = GeomertyToolkit.toPath(xPathMeasure.points);
		double pxl = GeomertyToolkit.getPathLength(path);
		double size = GeomertyToolkit.getPathLength(path, scaleX, scaleY);

		int power = data.getPowerX();
		if (update)
		{
			xPathPxlField.setText(NumberDimension
					.getValue(pxl, NumberDimension.POWER_UNITY, "pxl"));
			xPathSizeField.setText(NumberDimension.getValue(size, power, "m"));
		}
		return size;
	}

	public double getPathMeasureY(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeX();
			scaleY = data.getPixelSizeZ();
		} else
		{
			scaleX = data.getPreviewPixelSizeX();
			scaleY = data.getPreviewPixelSizeZ();
		}

		Path2D path = GeomertyToolkit.toPath(yPathMeasure.points);
		double pxl = GeomertyToolkit.getPathLength(path);
		double size = GeomertyToolkit.getPathLength(path, scaleX, scaleY);

		int power = data.getPowerY();
		if (update)
		{
			yPathPxlField.setText(NumberDimension
					.getValue(pxl, NumberDimension.POWER_UNITY, "pxl"));
			yPathSizeField.setText(NumberDimension.getValue(size, power, "m"));
		}
		return size;
	}

	public double getPathMeasureZ(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeX();
			scaleY = data.getPixelSizeY();
		} else
		{
			scaleX = data.getPreviewPixelSizeX();
			scaleY = data.getPreviewPixelSizeY();
		}

		Path2D path = GeomertyToolkit.toPath(zPathMeasure.points);
		double pxl = GeomertyToolkit.getPathLength(path);
		double size = GeomertyToolkit.getPathLength(path, scaleX, scaleY);

		int power = data.getPowerZ();
		if (update)
		{
			zPathPxlField.setText(NumberDimension
					.getValue(pxl, NumberDimension.POWER_UNITY, "pxl"));
			zPathSizeField.setText(NumberDimension.getValue(size, power, "m"));
		}
		return size;
	}

	public double getAreaMeasureX(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeZ();
			scaleY = data.getPixelSizeY();
		} else
		{
			scaleX = data.getPreviewPixelSizeZ();
			scaleY = data.getPreviewPixelSizeY();
		}

		Path2D path = GeomertyToolkit.toPath(xAreaMeasure.points);
		double pxl = GeomertyToolkit.getArea(path);
		double size = GeomertyToolkit.getArea(path, scaleX, scaleY);

		int power = data.getPowerX();
		if (update)
		{
			xAreaPxlField.setText(NumberDimension
					.getValue(pxl, NumberDimension.POWER_UNITY, "pxl\u00b2"));
			xAreaSizeField.setText(NumberDimension
					.getValue(size, power, "m\u00b2"));
		}
		return size;
	}

	public double getAreaMeasureY(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeX();
			scaleY = data.getPixelSizeZ();
		} else
		{
			scaleX = data.getPreviewPixelSizeX();
			scaleY = data.getPreviewPixelSizeZ();
		}

		Path2D path = GeomertyToolkit.toPath(yAreaMeasure.points);
		double pxl = GeomertyToolkit.getArea(path);
		double size = GeomertyToolkit.getArea(path, scaleX, scaleY);

		int power = data.getPowerY();
		if (update)
		{
			yAreaPxlField.setText(NumberDimension
					.getValue(pxl, NumberDimension.POWER_UNITY, "pxl\u00b2"));
			yAreaSizeField.setText(NumberDimension
					.getValue(size, power, "m\u00b2"));
		}
		return size;
	}

	public double getAreaMeasureZ(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeX();
			scaleY = data.getPixelSizeY();
		} else
		{
			scaleX = data.getPreviewPixelSizeX();
			scaleY = data.getPreviewPixelSizeY();
		}

		Path2D path = GeomertyToolkit.toPath(zAreaMeasure.points);
		double pxl = GeomertyToolkit.getArea(path);
		double size = GeomertyToolkit.getArea(path, scaleX, scaleY);

		int power = data.getPowerZ();
		if (update)
		{
			zAreaPxlField.setText(NumberDimension
					.getValue(pxl, NumberDimension.POWER_UNITY, "pxl\u00b2"));
			zAreaSizeField.setText(NumberDimension
					.getValue(size, power, "m\u00b2"));
		}
		return size;
	}

	public double getLineMeasureZ(boolean update)
	{
		double scaleX = 1;
		double scaleY = 1;
		if (renderHighRes.isSelected())
		{
			scaleX = data.getPixelSizeX();
			scaleY = data.getPixelSizeY();
		} else
		{
			scaleX = data.getPreviewPixelSizeX();
			scaleY = data.getPreviewPixelSizeY();
		}

		double dx = zLineMeasure.points.get(0).x - zLineMeasure.points.get(1).x;
		double dy = zLineMeasure.points.get(0).y - zLineMeasure.points.get(1).y;

		dx *= scaleX;
		dy *= scaleY;

		double val = Math.sqrt(dx * dx + dy * dy);

		int power = data.getPowerZ();
		if (update)
		{
			zDxField.setText(NumberDimension.getValue(dx, power, "m"));
			zDyField.setText(NumberDimension.getValue(dy, power, "m"));
			zSizeField.setText(NumberDimension.getValue(val, power, "m"));
		}
		return val;
	}

	public void updateMeasurement()
	{
		updateMeasurement(false);
	}

	public void updateMeasurement(boolean updateAll)
	{
		try
		{
			if (updateAll)
			{
				getLineMeasureX(true);
				getLineMeasureY(true);
				getLineMeasureZ(true);
				getLineMeasureX(true);
				getLineMeasureY(true);
				getLineMeasureZ(true);
				getAreaMeasureX(true);
				getAreaMeasureY(true);
				getAreaMeasureZ(true);
			} else if (controlTabPanel.getSelectedIndex() == 0)
			{// No Overlay

			} else if (controlTabPanel.getSelectedIndex() == 1)
			{
				if (measureTab.getSelectedIndex() == 0)
				{// Line Measure
					getLineMeasureX(true);
					getLineMeasureY(true);
					getLineMeasureZ(true);
				} else if (measureTab.getSelectedIndex() == 1)
				{// Path Measure
					getPathMeasureX(true);
					getPathMeasureY(true);
					getPathMeasureZ(true);
				} else if (measureTab.getSelectedIndex() == 2)
				{// Area Measure
					getAreaMeasureX(true);
					getAreaMeasureY(true);
					getAreaMeasureZ(true);

				}
			} else if (controlTabPanel.getSelectedIndex() == 2)
			{
				// Free slice panel
				if (toolTab.getSelectedIndex() == 2)
				{
					if (freeSliceRealTimeUpdate.isSelected())
					{
						updateFreeSlice();
					}
				}
			}

		} catch (Exception e)
		{
			System.out.println("There was an error updating measure");
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public boolean isUpdateAllowed()
	{
		return updateAllowed;
	}

	public void setUpdateAllowed(boolean allow)
	{
		this.updateAllowed = allow;
	}

	FileSelectionField exportCurrentFrame = new FileSelectionField();

	public void exportCurrentFrame()
	{
		try
		{

			exportCurrentFrame.setLabelSize(50);
			exportCurrentFrame
					.setFormat(FileSelectionField.FORMAT_IMAGE_FILES_SHOW_FORMAT);
			if (exportCurrentFrame.getUserChoice())
			{

				if (JOptionPane
						.showConfirmDialog(null, "The file already exist, Overwrite?", "Confirm Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION)
				{
					return;
				}
				exportCurrentFrame(exportCurrentFrame.getFile());
			}
		} catch (IOException e1)
		{
			JOptionPane
					.showMessageDialog(null, "There was an error saving the frame : \n"
							+ e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}

	}

	public void exportCurrentFrame(File f) throws IOException
	{
		exportCurrentFrame(f, false);
	}

	public void exportCurrentFrame(File f, boolean showOverlay)
			throws IOException
	{
		ImageIO.write(previewPanel.getImage(), Utils.getExtension(f), f);
	}

	public void setOCTData(NativeDataSet data)
	{
		this.data = data;
		xSlicePanel.setOCTData(data, NativeDataSet.X_SLICE);
		ySlicePanel.setOCTData(data, NativeDataSet.Y_SLICE);
		zSlicePanel.setOCTData(data, NativeDataSet.Z_SLICE);

		setPreviewAxes(NativeDataSet.X_SLICE);
		previewPos = 0;

		if (data.isAllowFullResolution())
		{
			renderHighRes.setEnabled(true);
		} else
		{
			renderHighRes.setSelected(false);
			renderHighRes.setEnabled(false);
		}
		updatePreviewPanel(true);
	}

	/**
	 * This function will draw the current slice and display the frame
	 */
	public void updateFreeSlice()
	{
		if (blockFreeSliceUpdate)
		{
			return;
		}
		Color maxCol = new Color(1f, 0, 0);
		Color minCol = new Color(0, 0, 1f);
		int sPos = DataAnalysisToolkit.getMin((Integer) freeSliceDepthStart
				.getValue(), (Integer) freeSliceDepthEnd.getValue());
		int ePos = DataAnalysisToolkit.getMax((Integer) freeSliceDepthStart
				.getValue(), (Integer) freeSliceDepthEnd.getValue());

		int avgNum = (Integer) freeSliceProjectNumber.getValue();
		if (freeSliceFrame == null)
		{
			freeSliceFrame = new JFrame();
			JPanel controls = new JPanel(new FlowLayout());
			controls.add(freeSliceRealTimeUpdate);

			freeSliceFrame.getContentPane().removeAll();
			freeSliceFrame.getContentPane().setLayout(new BorderLayout());
			freeSliceFrame.getContentPane()
					.add(freeSlicePanel, BorderLayout.CENTER);

			freeSliceFrame.getContentPane().add(controls, BorderLayout.NORTH);
		}
		Point2D.Double pA = new Point2D.Double();
		Point2D.Double pB = new Point2D.Double();

		int sizeX = 0;
		int sizeY = 0;

		/*
		 * Get the point data from the data
		 */
		if (this.previewAxes == NativeDataSet.X_SLICE)
		{
			pA = xFreeSlice.points.get(0);
			pB = xFreeSlice.points.get(1);
		} else if (this.previewAxes == NativeDataSet.Y_SLICE)
		{
			pA = yFreeSlice.points.get(0);
			pB = yFreeSlice.points.get(1);
		} else if (this.previewAxes == NativeDataSet.Z_SLICE)
		{
			pA = zFreeSlice.points.get(0);
			pB = zFreeSlice.points.get(1);
		}

		sizeX = (int) (pA.distance(pB));
		sizeY = (ePos - sPos);
		if (sizeY < 1)
		{
			sizeY = 1;
		}
		if (sizeX < 1)
		{
			sizeX = 1;
		}
		BufferedImage img = freeSliceData[0];
		if (img == null || img.getWidth() != sizeX || img.getHeight() != sizeY)
		{
			freeSliceData[0] = new BufferedImage(sizeX, sizeY,
					BufferedImage.TYPE_4BYTE_ABGR);
			freeSliceData[1] = new BufferedImage(sizeX, sizeY,
					BufferedImage.TYPE_4BYTE_ABGR);
			freeSliceData[2] = new BufferedImage(sizeX, sizeY,
					BufferedImage.TYPE_4BYTE_ABGR);
			freeSliceData[3] = new BufferedImage(sizeX, sizeY,
					BufferedImage.TYPE_4BYTE_ABGR);
		}

		int vx = 0;
		int vy = 0;
		int vz = 0;
		float pos;
		Point p = new Point();

		// These points are for perpendicular averageing
		Point2D.Double avgDir = new Point2D.Double();
		Point2D.Double datDir = new Point2D.Double(pB.x - pA.x, pB.y - pA.y);

		MathsToolkit.normalise(datDir);
		MathsToolkit.getPerpendicular(datDir, avgDir);

		Point2D.Double avgPos = new Point2D.Double();

		int datHold[] = new int[2 * avgNum + 1];
		int[] stats = new int[3];
		for (int x = 0; x < sizeX; x++)
		{
			pos = (x / (float) (sizeX - 1));

			p.x = (int) (pA.x + pos * (pB.x - pA.x));
			p.y = (int) (pA.y + pos * (pB.y - pA.y));

			for (int y = 0; y < (ePos - sPos); y++)
			{
				/**
				 * This code should was removed when the data was to be loaded
				 * into 4 different images for max, min, avg, none.
				 * 
				 * Memory Performance can be enhanced
				 */
				// if (avgNum == 0)
				// {
				// if (this.previewAxes == NativeDataSet.X_SLICE)
				// {
				// vx = y + sPos;
				// vy = p.y;
				// vz = p.x;
				// } else if (this.previewAxes == NativeDataSet.Y_SLICE)
				// {
				// vy = y + sPos;
				// vx = p.x;
				// vz = p.y;
				// } else if (this.previewAxes == NativeDataSet.Z_SLICE)
				// {
				// vz = y + sPos;
				// vx = p.x;
				// vy = p.y;
				// }
				//
				// boolean valid = true;
				//
				// valid = (vx >= 0) && (vy >= 0) && (vz >= 0)
				// && (vx < data.getPreviewSizeX())
				// && (vy < data.getPreviewSizeY())
				// && (vz < data.getPreviewSizeZ());
				//
				// if (valid)
				// {
				// img.setRGB(x, y, data.getByteToRGB(data
				// .getPreviewData()[vx][vy][vz]));
				// } else
				// {
				// img.setRGB(x, y, new Color(1f, 0, 0, 1f).getRGB());
				// }
				//
				// } else
				{
					int trueVal = 0;
					int count = 0;
					for (int avg = -avgNum; avg <= avgNum; avg++)
					{

						MathsToolkit.setLength(avgDir, avg, avgPos);
						if (this.previewAxes == NativeDataSet.X_SLICE)
						{
							vx = y + sPos;
							vy = (int) (p.y + avgPos.y);
							vz = (int) (p.x + avgPos.x);
						} else if (this.previewAxes == NativeDataSet.Y_SLICE)
						{
							vy = y + sPos;
							vx = (int) (p.x + avgPos.x);
							vz = (int) (p.y + avgPos.y);
						} else if (this.previewAxes == NativeDataSet.Z_SLICE)
						{
							vz = y + sPos;
							vx = (int) (p.x + avgPos.x);
							vy = (int) (p.y + avgPos.y);
						}

						boolean valid = true;

						valid = (vx >= 0) && (vy >= 0) && (vz >= 0)
								&& (vx < data.getPreviewSizeX())
								&& (vy < data.getPreviewSizeY())
								&& (vz < data.getPreviewSizeZ());

						if (valid)
						{

							datHold[count++] = NativeDataSet.getByteToInt(data
									.getPreviewData()[vx][vy][vz]);
							if (avg == 0)
							{
								trueVal = NativeDataSet.getByteToInt(data
										.getPreviewData()[vx][vy][vz]);
							}
						} else
						{
							datHold[count++] = 0;
						}
					}

					int val = 0;
					/**
					 * this was added to calculate all measurments at the same
					 * time.
					 */
					if (true)
					{
						DataAnalysisToolkit.getFullStatsInt(datHold, stats);
						// Remove Posavg too;
						freeSliceData[0].setRGB(x, y, NativeDataSet
								.getByteToRGB((byte) trueVal));
						freeSliceData[1].setRGB(x, y, NativeDataSet
								.getByteToRGB((byte) stats[0]));
						freeSliceData[2].setRGB(x, y, NativeDataSet
								.getByteToRGB((byte) stats[1]));
						freeSliceData[3].setRGB(x, y, NativeDataSet
								.getByteToRGB((byte) stats[2]));
					}
					/**
					 * Removed when array of images added to allow all to be
					 * done at the same time
					 */
					// else if (freeSliceProjectType.getSelectedIndex() == 0)
					// {
					// val = DataAnalysisToolkit.getAverage(datHold);
					// img.setRGB(x, y, data.getByteToRGB((byte) val));
					// } else if (freeSliceProjectType.getSelectedIndex() == 1)
					// {
					// val = DataAnalysisToolkit.getMax(datHold);
					// img.setRGB(x, y, data.getByteToRGB((byte) val));
					// } else if (freeSliceProjectType.getSelectedIndex() == 2)
					// {
					// val = DataAnalysisToolkit.getMin(datHold);
					// img.setRGB(x, y, data.getByteToRGB((byte) val));
					// } else if (freeSliceProjectType.getSelectedIndex() == 3)
					// {
					// val = DataAnalysisToolkit.getAverage(datHold);
					//
					// float min = DataAnalysisToolkit.getMin(datHold) / 256f;
					// float max = DataAnalysisToolkit.getMax(datHold) / 256f;
					//
					// img.setRGB(x, y, data.getByteToRGB((byte) val));
					//
					// Graphics2D g = img.createGraphics();
					// g.setComposite(AlphaComposite
					// .getInstance(AlphaComposite.SRC_OVER, 0.2f));
					// g.setColor(new Color(1 - min, max, 0));
					// g.fillRect(x, y, 1, 1);
					// }

				}
			}
		}

		freeSlicePanel.setImage(freeSliceData[freeSliceProjectType
				.getSelectedIndex()]);
		freeSliceFrame.setVisible(true);
	}

	/**
	 * This will update the current too Overlay and block all updates
	 */
	public void updateToolOverlay()
	{
		blockUpdate = true;

		allowProjection = false;
		xLineMeasure.setListening(false);
		yLineMeasure.setListening(false);
		zLineMeasure.setListening(false);

		xProfileTool.setListening(false);
		yProfileTool.setListening(false);
		zProfileTool.setListening(false);

		xPathMeasure.setListening(false);
		yPathMeasure.setListening(false);
		zPathMeasure.setListening(false);

		xAreaMeasure.setListening(false);
		yAreaMeasure.setListening(false);
		zAreaMeasure.setListening(false);

		xFreeSlice.setListening(false);
		yFreeSlice.setListening(false);
		zFreeSlice.setListening(false);

		noMeasureOverlay.setListening(true);
		noMeasureOverlay.setPanel(previewPanel);
		// Update Preview Panel overlay
		profileControlHolder.removeAll();
		profileControlHolder.setLayout(new BorderLayout());
		if (this.previewAxes == NativeDataSet.X_SLICE)
		{
			profileControlHolder.add(xProfileTool.getControls());
		} else if (this.previewAxes == NativeDataSet.Y_SLICE)
		{
			profileControlHolder.add(yProfileTool.getControls());
		} else if (this.previewAxes == NativeDataSet.Z_SLICE)
		{
			profileControlHolder.add(zProfileTool.getControls());
		}

		if (controlTabPanel.getSelectedIndex() == 0)
		{// No Overlay

		} else if (controlTabPanel.getSelectedIndex() == 1)
		{
			if (measureTab.getSelectedIndex() == 0)
			{// Line Measure
				if (this.previewAxes == NativeDataSet.X_SLICE)
				{
					xLineMeasure.setListening(true);
					xLineMeasure.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Y_SLICE)
				{
					yLineMeasure.setListening(true);
					yLineMeasure.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Z_SLICE)
				{
					zLineMeasure.setListening(true);
					zLineMeasure.setPanel(previewPanel);
				}
			} else if (measureTab.getSelectedIndex() == 1)
			{// Path Measure
				if (this.previewAxes == NativeDataSet.X_SLICE)
				{
					xPathMeasure.setListening(true);
					xPathMeasure.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Y_SLICE)
				{
					yPathMeasure.setListening(true);
					yPathMeasure.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Z_SLICE)
				{
					zPathMeasure.setListening(true);
					zPathMeasure.setPanel(previewPanel);
				}
			} else if (measureTab.getSelectedIndex() == 2)
			{// Area Measure
				if (this.previewAxes == NativeDataSet.X_SLICE)
				{
					xAreaMeasure.setListening(true);
					xAreaMeasure.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Y_SLICE)
				{
					yAreaMeasure.setListening(true);
					yAreaMeasure.setPanel(previewPanel);

				} else if (this.previewAxes == NativeDataSet.Z_SLICE)
				{
					zAreaMeasure.setListening(true);
					zAreaMeasure.setPanel(previewPanel);
				}
			}
		} else if (controlTabPanel.getSelectedIndex() == 2)
		{
			if (toolTab.getSelectedIndex() == 0)
			{// Profile Tool
				if (this.previewAxes == NativeDataSet.X_SLICE)
				{
					xProfileTool.setListening(true);
					xProfileTool.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Y_SLICE)
				{
					yProfileTool.setListening(true);
					yProfileTool.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Z_SLICE)
				{
					zProfileTool.setListening(true);
					zProfileTool.setPanel(previewPanel);
				}
			} else if (toolTab.getSelectedIndex() == 1)
			{
				// Prevent Continous Update
				continousSliderUpdate.setSelected(false);
				allowProjection = true;
			} else if (toolTab.getSelectedIndex() == 2)
			{
				if (this.previewAxes == NativeDataSet.X_SLICE)
				{
					xFreeSlice.setListening(true);
					xFreeSlice.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Y_SLICE)
				{
					yFreeSlice.setListening(true);
					yFreeSlice.setPanel(previewPanel);
				} else if (this.previewAxes == NativeDataSet.Z_SLICE)
				{
					zFreeSlice.setListening(true);
					zFreeSlice.setPanel(previewPanel);
				}
			}
		}

		blockUpdate = false;
	}

	public void updatePreviewPanel(final boolean highRes)
	{
		if (blockUpdate)
		{
			return;
		}
		updateToolOverlay();

		BufferedImage img = previewPanel.getImage();
		Dimension d;

		if (renderHighRes.isSelected())
		{
			d = data.getRenderSliceSize(getPreviewAxes());
		} else
		{
			d = data.getPreviewSliceSize(getPreviewAxes());
		}
		if (img.getWidth() != d.width || img.getHeight() != d.height)
		{
			img = ImageOperations.getBi(d.width, d.height);
			avgData = null;
			previewPanel.setImage(img);
		}

		try
		{
			/**
			 * Check if data is to be average and if averaging is allowed
			 */

			int avgNum = (Integer) projectNumber.getValue();

			if (!allowProjection)
			{
				avgNum = 0;
			}

			if (avgNum == 0)
			{// If not just get current image
				if (highRes && renderHighRes.isSelected())
				{
					data
							.getRenderSlice(getPreviewAxes(), previewPos, img, status);
				} else
				{
					data
							.getPreviewScaledSlice(getPreviewAxes(), previewPos, img);
				}
			} else
			{// If yes load all data
				int pos = previewPos;
				if (avgNum < 0)
				{
					avgNum *= -1;
					pos -= avgNum;
				}

				if (pos < 0)
				{
					avgNum += pos;
					pos = 0;

				}

				if (pos > data.getSizeData(getPreviewAxes()))
				{
					avgNum += (data.getSizeData(getPreviewAxes()) - pos);
					pos = data.getSizeData(getPreviewAxes());
				}

				if (avgNum < 1)
				{
					avgNum = 1;
				}
				// Load the images
				if (avgData == null || avgData.length != avgNum)
				{
					avgData = new BufferedImage[avgNum];
					for (int i = 0; i < avgNum; i++)
					{
						avgData[i] = ImageOperations.getSameSizeImage(img);
					}
				}
				for (int i = 0; i < avgNum; i++)
				{
					if (status != null)
					{
						status.setStatusMessage("Loading Image [" + i + " of "
								+ (avgNum - 1) + "]");
					}
					if (highRes && renderHighRes.isSelected())
					{
						data
								.getRenderSlice(getPreviewAxes(), pos + i, avgData[i], null);
					} else
					{
						data
								.getPreviewScaledSlice(getPreviewAxes(), pos
										+ i, avgData[i]);
					}
				}
				// Project the data
				int project = 0;
				if (projectType.getSelectedIndex() == 0)
				{
					project = ImageOperations.PROJECT_TYPE_AVERAGE;
				} else if (projectType.getSelectedIndex() == 1)
				{
					project = ImageOperations.PROJECT_TYPE_MAX;
				} else if (projectType.getSelectedIndex() == 2)
				{
					project = ImageOperations.PROJECT_TYPE_MIN;
				}
				if (status != null)
				{
					status.setStatusMessage("Starting Projection");
				}
				ImageOperations
						.getImageProjection(project, ImageOperations.PLANE_GRAY, null, img, avgData);

			}
			if (status != null)
			{
				status.setStatusMessage("Doing Image Processing ");
			}
			imageProcesser.processImage(previewPanel.getImage());
			// previewPanel.setScale(getViewScale(), getViewScale());

			// Draw Lines if needed
			if (showPositionLines.isSelected())
			{
				drawPlaneLines(img, (float) lineTransparency.getValue()
						/ (float) lineTransparency.getMaximum());
			}
			if (status != null)
			{
				status.reset();
			}

		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "There was and error :\n" + e);
			e.printStackTrace();
		}
		// previewScroll.revalidate();
		// previewPanel.revalidate();
		revalidate();
		repaint();
	}

	public void drawPlaneLines(BufferedImage img, float alpha)
	{
		double x = 1;
		double y = 1;

		Color xCol = Color.RED;
		Color yCol = Color.red;

		if (getPreviewAxes() == xSlicePanel.sliceAxes)
		{

			y = (double) ySlicePanel.pos.getValue()
					/ ySlicePanel.pos.getMaximum();
			x = (double) zSlicePanel.pos.getValue()
					/ zSlicePanel.pos.getMaximum();
			xCol = SlicePanel.Z_AXIS_COLOR;
			yCol = SlicePanel.Y_AXIS_COLOR;

		} else if (getPreviewAxes() == ySlicePanel.sliceAxes)
		{
			x = (double) xSlicePanel.pos.getValue()
					/ xSlicePanel.pos.getMaximum();
			y = (double) zSlicePanel.pos.getValue()
					/ zSlicePanel.pos.getMaximum();
			xCol = SlicePanel.X_AXIS_COLOR;
			yCol = SlicePanel.Z_AXIS_COLOR;
		} else if (getPreviewAxes() == zSlicePanel.sliceAxes)
		{
			x = (double) xSlicePanel.pos.getValue()
					/ xSlicePanel.pos.getMaximum();
			y = (double) ySlicePanel.pos.getValue()
					/ ySlicePanel.pos.getMaximum();

			xCol = SlicePanel.X_AXIS_COLOR;
			yCol = SlicePanel.Y_AXIS_COLOR;
		}
		/*
		 * Validate the positoin to ensure they are with 0 -> 1
		 */
		x = x > 1 ? 1 : x;
		x = x < 0 ? 0 : x;

		y = y > 1 ? 1 : y;
		y = y < 0 ? 0 : y;

		x *= img.getWidth();
		y *= img.getHeight();

		Graphics2D g = img.createGraphics();
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, alpha));
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		g.setColor(xCol);
		g.drawLine((int) x, 0, (int) x, img.getHeight());

		g.setColor(yCol);
		g.drawLine(0, (int) y, img.getWidth(), (int) y);

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{

		if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK)
		{
			mouseDragging = true;
			double x = 1;
			double y = 1;
			double z = 1;
			if (e.getSource() == xSlicePanel.imgPanel)
			{

				Point img = new Point();
				xSlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) xSlicePanel.pos.getValue()
						/ xSlicePanel.pos.getMaximum();
				y = (double) img.y / data.getPreviewSizeY();
				z = (double) img.x / data.getPreviewSizeZ();

			} else if (e.getSource() == ySlicePanel.imgPanel)
			{

				Point img = new Point();
				ySlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) img.x / data.getPreviewSizeX();
				y = (double) ySlicePanel.pos.getValue()
						/ ySlicePanel.pos.getMaximum();
				z = (double) img.y / data.getPreviewSizeZ();

			} else if (e.getSource() == zSlicePanel.imgPanel)
			{

				Point img = new Point();
				zSlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) img.x / data.getPreviewSizeX();
				y = (double) img.y / data.getPreviewSizeY();
				z = (double) zSlicePanel.pos.getValue()
						/ zSlicePanel.pos.getMaximum();

			}

			/*
			 * Validate the positoin to ensure they are with 0 -> 1 This will
			 * help sort out if a user has click outside an image
			 */
			x = x > 1 ? 1 : x;
			x = x < 0 ? 0 : x;

			y = y > 1 ? 1 : y;
			y = y < 0 ? 0 : y;

			z = z > 1 ? 1 : z;
			z = z < 0 ? 0 : z;

			xSlicePanel.setPosition(x);
			xSlicePanel.crossX = z;
			xSlicePanel.crossY = y;

			ySlicePanel.setPosition(y);
			ySlicePanel.crossX = x;
			ySlicePanel.crossY = z;

			zSlicePanel.setPosition(z);
			zSlicePanel.crossX = x;
			zSlicePanel.crossY = y;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			if (e.getSource() == xSlicePanel.imgPanel)
			{
				setPreviewAxes(NativeDataSet.X_SLICE);
				previewPos = xSlicePanel.getPosition();
			} else if (e.getSource() == ySlicePanel.imgPanel)
			{
				setPreviewAxes(NativeDataSet.Y_SLICE);
				previewPos = ySlicePanel.getPosition();
			} else if (e.getSource() == zSlicePanel.imgPanel)
			{
				setPreviewAxes(NativeDataSet.Z_SLICE);
				previewPos = zSlicePanel.getPosition();
			}

			updatePreviewPanel(true);
		}
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			mouseDragging = true;
			double x = 1;
			double y = 1;
			double z = 1;
			if (e.getSource() == xSlicePanel.imgPanel)
			{

				Point img = new Point();
				xSlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) xSlicePanel.pos.getValue()
						/ xSlicePanel.pos.getMaximum();
				y = (double) img.y / data.getPreviewSizeY();
				z = (double) img.x / data.getPreviewSizeZ();

			} else if (e.getSource() == ySlicePanel.imgPanel)
			{

				Point img = new Point();
				ySlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) img.x / data.getPreviewSizeX();
				y = (double) ySlicePanel.pos.getValue()
						/ ySlicePanel.pos.getMaximum();
				z = (double) img.y / data.getPreviewSizeZ();

			} else if (e.getSource() == zSlicePanel.imgPanel)
			{

				Point img = new Point();
				zSlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) img.x / data.getPreviewSizeX();
				y = (double) img.y / data.getPreviewSizeY();
				z = (double) zSlicePanel.pos.getValue()
						/ zSlicePanel.pos.getMaximum();

			}
			/*
			 * Validate the positoin to ensure they are with 0 -> 1
			 */
			x = x > 1 ? 1 : x;
			x = x < 0 ? 0 : x;

			y = y > 1 ? 1 : y;
			y = y < 0 ? 0 : y;

			z = z > 1 ? 1 : z;
			z = z < 0 ? 0 : z;

			xSlicePanel.setPosition(x);
			xSlicePanel.crossX = z;
			xSlicePanel.crossY = y;

			ySlicePanel.setPosition(y);
			ySlicePanel.crossX = x;
			ySlicePanel.crossY = z;

			zSlicePanel.setPosition(z);
			zSlicePanel.crossX = x;
			zSlicePanel.crossY = y;

			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (mouseDragging)
		{
			mouseDragging = false;
			updatePreviewPanel(true);
		}

		if (e.getButton() == MouseEvent.BUTTON1)
		{
			double x = 1;
			double y = 1;
			double z = 1;
			if (e.getSource() == xSlicePanel.imgPanel)
			{

				Point img = new Point();
				xSlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) xSlicePanel.pos.getValue()
						/ xSlicePanel.pos.getMaximum();
				y = (double) img.y / data.getPreviewSizeY();
				z = (double) img.x / data.getPreviewSizeZ();

			} else if (e.getSource() == ySlicePanel.imgPanel)
			{

				Point img = new Point();
				ySlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) img.x / data.getPreviewSizeX();
				y = (double) ySlicePanel.pos.getValue()
						/ ySlicePanel.pos.getMaximum();
				z = (double) img.y / data.getPreviewSizeZ();

			} else if (e.getSource() == zSlicePanel.imgPanel)
			{

				Point img = new Point();
				zSlicePanel.getPaneltoData(e.getPoint(), img);

				x = (double) img.x / data.getPreviewSizeX();
				y = (double) img.y / data.getPreviewSizeY();
				z = (double) zSlicePanel.pos.getValue()
						/ zSlicePanel.pos.getMaximum();

			}
			/*
			 * Validate the positoin to ensure they are with 0 -> 1
			 */
			x = x > 1 ? 1 : x;
			x = x < 0 ? 0 : x;

			y = y > 1 ? 1 : y;
			y = y < 0 ? 0 : y;

			z = z > 1 ? 1 : z;
			z = z < 0 ? 0 : z;

			xSlicePanel.setPosition(x);
			xSlicePanel.crossX = z;
			xSlicePanel.crossY = y;

			ySlicePanel.setPosition(y);
			ySlicePanel.crossX = x;
			ySlicePanel.crossY = z;

			zSlicePanel.setPosition(z);
			zSlicePanel.crossX = x;
			zSlicePanel.crossY = y;
			repaint();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (!isUpdateAllowed())
		{
			return;
		}
		if (e.getSource() instanceof JSlider)
		{
			JSlider slide = (JSlider) e.getSource();

			{
				double x = (double) xSlicePanel.pos.getValue()
						/ xSlicePanel.pos.getMaximum();
				double y = (double) ySlicePanel.pos.getValue()
						/ ySlicePanel.pos.getMaximum();
				double z = (double) zSlicePanel.pos.getValue()
						/ zSlicePanel.pos.getMaximum();

				/*
				 * Validate the positoin to ensure they are with 0 -> 1
				 */
				x = x > 1 ? 1 : x;
				x = x < 0 ? 0 : x;

				y = y > 1 ? 1 : y;
				y = y < 0 ? 0 : y;

				z = z > 1 ? 1 : z;
				z = z < 0 ? 0 : z;

				xSlicePanel.setPosition(x);
				xSlicePanel.crossX = z;
				xSlicePanel.crossY = y;

				ySlicePanel.setPosition(y);
				ySlicePanel.crossX = x;
				ySlicePanel.crossY = z;

				zSlicePanel.setPosition(z);
				zSlicePanel.crossX = x;
				zSlicePanel.crossY = y;

				if (slide == xSlicePanel.pos
						&& xSlicePanel.sliceAxes == getPreviewAxes())
				{
					// setPreviewAxes(OCTDataSet.X_SLICE);
					previewPos = slide.getValue();
				} else if (slide == ySlicePanel.pos
						&& ySlicePanel.sliceAxes == getPreviewAxes())
				{
					// setPreviewAxes(OCTDataSet.Y_SLICE);
					previewPos = slide.getValue();
				} else if (slide == zSlicePanel.pos
						&& zSlicePanel.sliceAxes == getPreviewAxes())
				{
					// setPreviewAxes(OCTDataSet.Z_SLICE);
					previewPos = slide.getValue();
				}
				repaint();

				if (!slide.getValueIsAdjusting()
						|| continousSliderUpdate.isSelected())
				{
					if (!mouseDragging)
					{
						updatePreviewPanel(true);
					}

				}

			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == colorMapButton)
		{
			imageProcesser.setColorMap(ColorMapTools.showUserChoicePanel());
			updatePreviewPanel(true);
		}

	}

	public StatusBarPanel getStatus()
	{
		return status;
	}

	public void setStatus(StatusBarPanel status)
	{
		this.status = status;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		// Remove OLD Listners
		xSlicePanel.imgPanel.removeMouseListener(this);
		xSlicePanel.imgPanel.removeMouseMotionListener(this);
		xSlicePanel.pos.removeChangeListener(this);

		ySlicePanel.imgPanel.removeMouseListener(this);
		ySlicePanel.imgPanel.removeMouseMotionListener(this);
		ySlicePanel.pos.removeChangeListener(this);

		zSlicePanel.imgPanel.removeMouseListener(this);
		zSlicePanel.imgPanel.removeMouseMotionListener(this);
		zSlicePanel.pos.removeChangeListener(this);

		// Add New Data
		data = (NativeDataSet) in.readObject();
		xSlicePanel = (SlicePanel) in.readObject();
		ySlicePanel = (SlicePanel) in.readObject();
		zSlicePanel = (SlicePanel) in.readObject();

		slicePanel.removeAll();
		slicePanel.add(xSlicePanel);
		slicePanel.add(ySlicePanel);
		slicePanel.add(zSlicePanel);

		/**
		 * add all the listners
		 */
		xSlicePanel.imgPanel.addMouseListener(this);
		xSlicePanel.imgPanel.addMouseMotionListener(this);
		xSlicePanel.pos.addChangeListener(this);

		ySlicePanel.imgPanel.addMouseListener(this);
		ySlicePanel.imgPanel.addMouseMotionListener(this);
		ySlicePanel.pos.addChangeListener(this);

		zSlicePanel.imgPanel.addMouseListener(this);
		zSlicePanel.imgPanel.addMouseMotionListener(this);
		zSlicePanel.pos.addChangeListener(this);

		xSlicePanel.setOCTData(data, NativeDataSet.X_SLICE);
		ySlicePanel.setOCTData(data, NativeDataSet.Y_SLICE);
		zSlicePanel.setOCTData(data, NativeDataSet.Z_SLICE);

		setPreviewAxes(in.readInt());
		previewPos = in.readInt();

		/**
		 * Removed
		 */
		// scaleSlider.setValue(in.readInt());
		in.readInt();

		renderHighRes.setSelected(in.readBoolean());
		continousSliderUpdate.setSelected(in.readBoolean());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(data);

		out.writeObject(xSlicePanel);
		out.writeObject(ySlicePanel);
		out.writeObject(zSlicePanel);

		out.writeInt(getPreviewAxes());
		out.writeInt(previewPos);

		/**
		 * Removed
		 */
		// out.writeInt(scaleSlider.getValue());
		out.writeInt(1);

		out.writeBoolean(renderHighRes.isSelected());
		out.writeBoolean(continousSliderUpdate.isSelected());
	}

	public int getPreviewAxes()
	{
		return previewAxes;
	}

	public void setPreviewAxes(int previewAxes)
	{
		this.previewAxes = previewAxes;

		blockUpdate = true;
		updateToolOverlay();
		blockUpdate = false;
	}

	public int getPreviewPos()
	{
		return previewPos;
	}

	public void setPreviewPos(int previewPos)
	{
		this.previewPos = previewPos;
	}

	public SlicePanel getXSlicePanel()
	{
		return xSlicePanel;
	}

	public SlicePanel getYSlicePanel()
	{
		return ySlicePanel;
	}

	public SlicePanel getZSlicePanel()
	{
		return zSlicePanel;
	}

	public JButton getColorMapButton()
	{
		return colorMapButton;
	}

	public ImagePanel getPreviewPanel()
	{
		return previewPanel;
	}

	public JCheckBox getContinousSliderUpdate()
	{
		return continousSliderUpdate;
	}

	public JCheckBox getRenderHighRes()
	{
		return renderHighRes;
	}

	public OCTImageProcessingTool getImageProcesser()
	{
		return imageProcesser;
	}

	public NativeDataSet getOCTData()
	{
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public void regionAdded(Shape region)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void regionChanged()
	{
		updateMeasurement();
	}

	@Override
	public void regionRemoved(Shape region)
	{
		// TODO Auto-generated method stub

	}

	public JTextField getXDxField()
	{
		return xDxField;
	}

	public JTextField getXDyField()
	{
		return xDyField;
	}

	public JTextField getYDxField()
	{
		return yDxField;
	}

	public JTextField getYDyField()
	{
		return yDyField;
	}

	public JTextField getZDxField()
	{
		return zDxField;
	}

	public JTextField getZDyField()
	{
		return zDyField;
	}

	/**
	 * this will get the projected slices around the current position in the oct
	 * slice viewer
	 * 
	 * @param viewer
	 * @param view
	 * @param data
	 * @param projectNum
	 * @param scanLength
	 * @param angle
	 * @return
	 */
	public static BufferedImage[] getImageData(OCTSliceViewer viewer, OCTViewDataHolder view, NativeDataSet data, int projectNum, int scanLength, int angle)
	{
		viewer.blockFreeSliceUpdate = true;
		BufferedImage[] result = new BufferedImage[4];

		view.setSliceData(viewer);

		viewer.setPreviewAxes(NativeDataSet.X_SLICE);
		viewer.updatePreviewPanel(false);
		// X Slice
		viewer.freeSliceDepthStart.setValue(0);
		viewer.freeSliceDepthEnd.setValue(data.getPreviewSizeX());

		double y = (double) viewer.ySlicePanel.pos.getValue()
				/ viewer.ySlicePanel.pos.getMaximum();
		double x = (double) viewer.zSlicePanel.pos.getValue()
				/ viewer.zSlicePanel.pos.getMaximum();

		int points = scanLength;

		viewer.xFreeSlice.points.get(0).x = x * data.getPreviewSizeZ() - points
				* Math.cos(Math.toRadians(angle));
		viewer.xFreeSlice.points.get(0).y = y * data.getPreviewSizeY() - points
				* Math.sin(Math.toRadians(angle));

		viewer.xFreeSlice.points.get(1).x = x * data.getPreviewSizeZ() - points
				* Math.cos(Math.toRadians(angle + 180));
		viewer.xFreeSlice.points.get(1).y = y * data.getPreviewSizeY() - points
				* Math.sin(Math.toRadians(angle + 180));

		// viewer.xFreeSlice.points.set(0,
		// viewer.previewPanel.imageToPanelCoords(viewer.xFreeSlice.points.get(0)));
		// viewer.xFreeSlice.points.set(1,
		// viewer.previewPanel.imageToPanelCoords(viewer.xFreeSlice.points.get(1)));

		viewer.freeSliceProjectNumber.setValue(projectNum);

		viewer.blockFreeSliceUpdate = false;
		viewer.updateFreeSlice();
		result = viewer.freeSliceData;
		viewer.freeSliceFrame.setVisible(false);
		return result;
	}

}
