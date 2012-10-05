package com.joey.software.regionSelectionToolkit.controlers;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.ImagePanelControler;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.plottingToolkit.PlotingToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class ImageProfileTool extends ROIControler implements Externalizable
{
	private static final long serialVersionUID = 1L;

	public static int AXIS_X = 0;

	public static int AXIS_Y = 1;

	int plane = ImageOperations.PLANE_GRAY;

	int dataPoints = 20;

	JTabbedPane tabPanel;

	// This is the data in the user points
	float value[] = new float[dataPoints];

	// this is the real Data
	float selectionData[] = new float[256];

	boolean useData[] = new boolean[dataPoints];

	double pointSize = 6;

	Color crossColor = Color.RED;

	Color pointColorSelected = Color.CYAN;

	Color pointColorNotSelected = Color.ORANGE;

	Color offsetColor = Color.green;

	ROIPanel view;

	JFreeChart dataPlot = PlotingToolkit
			.getPlot(new float[1], "AScan Data", "", "");

	ChartPanel dataPanel = new ChartPanel(dataPlot);

	JPanel chartHolderPanel = null;

	float[] xData = new float[0];

	float[] aScan = new float[0];

	float[] xRange = new float[]
	{ 0, 1 };

	JButton updatePoints = new JButton("Set");

	JSpinner numPoints = new JSpinner(new SpinnerNumberModel(10, 2, 10000, 1));

	JButton saveData = new JButton("Save CSV");

	JButton estimageSurface = new JButton("Est Surf");

	JButton moveUpData = new JButton(new ImageIcon(DrawTools
			.getMoveUPImage(15, 20)));

	int delay = 100;

	Timer moveUPTimer;

	Timer moveDownTimer;

	JButton moveDownData = new JButton(new ImageIcon(DrawTools
			.getMoveDownImage(15, 20)));

	JButton showFlattenedButton = new JButton("Image");

	JPanel controls = null;

	int dataLength = 10;

	JCheckBox showImageOverlay = new JCheckBox("Overlay ");

	JCheckBox realTimeImage = new JCheckBox("RealTimeImage ");

	JCheckBox showOffset = new JCheckBox("Offset");

	JSpinner offset = new JSpinner(
			new SpinnerNumberModel(0., -10000, +10000, 1));

	JSlider transparance = new JSlider(0, 1000);

	int axis = AXIS_X;

	JButton axisToggle = new JButton("X-Axis");

	JFrame chartHolder = null;

	JButton showAScanButton = new JButton("A-Scan");

	ImagePanel previewPanel = new ImagePanel();

	JFrame imageHolder = null;

	boolean drawCrosses = true;

	boolean blockUpdate = false;

	public ImageProfileTool()
	{
		this(new ROIPanel(false));
	}

	public ImageProfileTool(ROIPanel viewPanel)
	{
		super(viewPanel);

		setView(viewPanel);

		selectionData = new float[viewPanel.getImage().getWidth()];

		estimateSurface();
	}

	public float[] getProfileData()
	{
		return aScan;
	}

	public void showFlattenedImage()
	{
		if (imageHolder == null)
		{
			imageHolder = new JFrame("Flattened Image");
			JButton save = new JButton("Save");

			ImagePanelControler control = new ImagePanelControler(previewPanel);
			// FrameFactroy.getFrame(control);
			JPanel panel = new JPanel(new FlowLayout());
			panel.add(showImageOverlay);
			panel.add(realTimeImage);

			JPanel pan = new JPanel();
			previewPanel.putIntoPanel(pan);

			imageHolder.getContentPane().setLayout(new BorderLayout());
			imageHolder.getContentPane().add(pan, BorderLayout.CENTER);
			imageHolder.getContentPane().add(save, BorderLayout.SOUTH);
			imageHolder.getContentPane().add(panel, BorderLayout.NORTH);

			imageHolder.setSize(600, 480);
			imageHolder.setVisible(true);

			showImageOverlay.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					previewPanel.setImage(getFlattenedImage(showImageOverlay
							.isSelected()));
				}
			});

			imageHolder.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			save.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						File f = FileSelectionField.getUserFile();
						if (f != null)
						{
							ImageIO.write(previewPanel.getImage(), "png", f);
						}
					} catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			});
		}

		previewPanel.setImage(getFlattenedImage(showImageOverlay.isSelected()));
		imageHolder.setVisible(true);

	}

	public BufferedImage getFlattenedImage()
	{
		return getFlattenedImage(showImageOverlay.isSelected());
	}

	public BufferedImage getFlattenedImage(boolean overlayAscan)
	{

		ROIPanel pan = view;
		BufferedImage rst = previewPanel.getImage();
		if (axis == AXIS_Y)
		{
			int wide = 0;
			for (int i = 0; i < pan.getImage().getWidth(); i++)
			{
				if (getUseData(i))
				{
					wide++;
				}
			}

			if (rst == null || rst.getWidth() != wide
					|| rst.getHeight() != pan.getImage().getHeight())
			{
				rst = ImageOperations.getBi(wide, pan.getImage().getHeight());
			} else
			{
				ImageOperations.setImage(Color.BLACK, rst);
			}
			// This will get the smoothed out aScan from the Dynamic?Range panel
			int posX = 0;
			int posY = 0;

			for (int i = 0; i < pan.getImage().getWidth(); i++)
			{
				posY = 0;

				if (getUseData(i))
				{
					int start = (int) (pan.getImage().getHeight() * (1 - getSelectionValue(i)));
					for (int j = start; j < pan.getImage().getHeight(); j++)
					{
						if (j > 0 && i > 0)
						{
							try
							{
								rst.setRGB(posX, posY, pan.getImage()
										.getRGB(i, j));
							} catch (Exception e)
							{
								System.out.println("Org [" + i + "," + j
										+ "], Pos :[" + posX + "," + posY);
							}
						}
						posY++;
					}
					posX++;
				}

			}
		} else if (axis == AXIS_X)
		{
			int high = 0;
			for (int i = 0; i < pan.getImage().getHeight(); i++)
			{
				if (getUseData(i))
				{
					high++;
				}
			}

			if (rst == null || rst.getHeight() != high
					|| rst.getWidth() != pan.getImage().getWidth())
			{
				rst = ImageOperations.getBi(pan.getImage().getWidth(), high);
			} else
			{
				ImageOperations.setImage(Color.BLACK, rst);
			}
			// This will get the smoothed out aScan from the Dynamic?Range panel
			int posX = 0;
			int posY = 0;

			for (int i = 0; i < pan.getImage().getHeight(); i++)
			{
				posX = 0;

				if (getUseData(i))
				{
					int start = (int) (pan.getImage().getWidth() * (1 - getSelectionValue(i)));
					for (int j = start; j < pan.getImage().getWidth(); j++)
					{
						if (j > 0 && i > 0)
						{
							try
							{
								rst.setRGB(posX, posY, pan.getImage()
										.getRGB(j, i));
							} catch (Exception e)
							{
								System.out.println("Org [" + i + "," + j
										+ "], Pos :[" + posX + "," + posY);
							}
						}
						posX++;
					}
					posY++;
				}

			}
		}

		if (overlayAscan)
		{
			Graphics2D g = rst.createGraphics();
			g.setColor(Color.cyan);
			GraphicsToolkit
					.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
			float max = DataAnalysisToolkit.getMaxf(aScan);
			float min = DataAnalysisToolkit.getMinf(aScan);
			Line2D.Double line = new Line2D.Double();

			GeneralPath path = new GeneralPath();
			for (int i = 0; i < aScan.length; i++)
			{
				int xP = 0;
				int yP = 0;

				float p1 = ((aScan[i] - min) / (max - min));

				double x = 0;
				double y = 0;

				if (axis == AXIS_Y)
				{

					y = rst.getHeight() / (double) (aScan.length - 1) * i;
					x = rst.getWidth() * (1 - p1);

				} else if (axis == AXIS_X)
				{
					x = rst.getWidth() / (double) (aScan.length - 1) * i;
					y = rst.getHeight() * (1 - p1);

				}

				if (i == 0)
				{
					path.moveTo(x, y);
				} else
				{
					path.lineTo(x, y);
				}

			}
			g.draw(path);
		}
		return rst;
	}

	private void setView(ROIPanel view)
	{
		this.view = view;
	}

	public JPanel getChartHolderPanel()
	{
		if (chartHolderPanel == null)
		{
			chartHolderPanel = new JPanel(new BorderLayout());
			chartHolderPanel.add(dataPanel);
		}
		return chartHolderPanel;
	}

	public void showAScanPanel()
	{
		if (chartHolder == null)
		{
			chartHolder = new JFrame("A - Scan");
			chartHolder.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			chartHolder.getContentPane().setLayout(new BorderLayout());
			chartHolder.getContentPane()
					.add(getChartHolderPanel(), BorderLayout.CENTER);
			chartHolder.setSize(600, 480);
		}

		chartHolder.setVisible(true);
	}

	@Override
	public JPanel getControlPanel()
	{
		return getControls();
	}
	public JPanel getControls()
	{
		if (controls == null)
		{
			controls = new JPanel(new BorderLayout());

			JPanel dirButton = new JPanel(new GridLayout(1, 2));
			dirButton.add(moveUpData);
			dirButton.add(moveDownData);

			JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
			buttonPanel.add(showFlattenedButton);
			buttonPanel.add(showAScanButton);
			buttonPanel.add(estimageSurface);

			JPanel pointsPanel = new JPanel(new BorderLayout());
			pointsPanel.add(saveData, BorderLayout.SOUTH);
			pointsPanel.add(axisToggle, BorderLayout.WEST);
			pointsPanel.add(numPoints, BorderLayout.CENTER);
			pointsPanel.add(updatePoints, BorderLayout.EAST);
			pointsPanel.add(buttonPanel, BorderLayout.NORTH);

			JPanel offsetPane = new JPanel(new BorderLayout());
			offsetPane.add(showOffset, BorderLayout.WEST);
			offsetPane.add(offset, BorderLayout.CENTER);

			JPanel temp = new JPanel(new BorderLayout());
			temp.add(offsetPane, BorderLayout.SOUTH);
			temp.add(transparance, BorderLayout.NORTH);

			JPanel toolPanel = new JPanel(new BorderLayout());
			toolPanel.add(temp, BorderLayout.NORTH);
			toolPanel.add(pointsPanel, BorderLayout.CENTER);
			toolPanel.add(dirButton, BorderLayout.SOUTH);

			controls.add(toolPanel, BorderLayout.NORTH);

			estimageSurface.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					estimateSurface();

				}
			});
			showAScanButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					showAScanPanel();
				}
			});
			axisToggle.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (axis == AXIS_X)
					{
						setAxis(AXIS_Y);
						axisToggle.setText("Y Axis");
					} else
					{
						setAxis(AXIS_X);
						axisToggle.setText("X Axis");
					}
					panel.repaint();
				}
			});
			transparance.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					panel.repaint();

				}
			});
			showOffset.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					panel.repaint();
				}
			});

			offset.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					panel.shapeChanged();
					panel.repaint();
					updatePlotPanel();
				}

			});
			showFlattenedButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					showFlattenedImage();
				}
			});
			updatePoints.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					setDataPoints((Integer) numPoints.getValue());
				}
			});

			moveUPTimer = new Timer(delay, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					addValue(1.0f / view.getImage().getHeight());
				}
			});

			moveDownTimer = new Timer(delay, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					addValue(-1.0f / view.getImage().getHeight());
				}
			});

			moveUpData.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					JButton btn2 = (JButton) e.getSource();
					ButtonModel model = btn2.getModel();
					if (model.isPressed() && !moveUPTimer.isRunning())
					{
						moveUPTimer.start();
					} else if (!model.isPressed() && moveUPTimer.isRunning())
					{
						moveUPTimer.stop();
					}

				}
			});

			moveDownData.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					JButton btn2 = (JButton) e.getSource();
					ButtonModel model = btn2.getModel();
					if (model.isPressed() && !moveDownTimer.isRunning())
					{
						moveDownTimer.start();
					} else if (!model.isPressed() && moveDownTimer.isRunning())
					{
						moveDownTimer.stop();
					}

				}
			});
			moveUpData.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					addValue(1.0f / view.getImage().getHeight());

				}
			});

			moveDownData.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					addValue(-1.0f / view.getImage().getHeight());

				}
			});

			saveData.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						saveData();
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}

		return controls;
	}

	public void saveData() throws IOException
	{
		File f = FileSelectionField.getUserFile();

		String data = CSVFileToolkit.getCSVDataRow(aScan);
		CSVFileToolkit.writeCSVData(f, data);
	}

	public void addValue(float num)
	{
		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] += num;
		}

		for (int i = 0; i < value.length; i++)
		{
			value[i] += num;
		}

		getPanel().repaint();
		panel.shapeChanged();
		updatePlotPanel();

	}

	public void estimateSurface()
	{
		if (true)
		{
			return;
		}
		int threshold = Integer.parseInt(JOptionPane
				.showInputDialog("Number :"));
		boolean found = true;

		int val = 0;
		int rgb = 0;

		int[] pos;

		if (axis == AXIS_Y)
		{
			pos = new int[panel.getImage().getWidth()];
			for (int x = 0; x < panel.getImage().getWidth(); x++)
			{
				for (int y = 0; y < panel.getImage().getHeight(); y++)
				{
					rgb = panel.getImage().getRGB(x, y);
					val = ImageOperations
							.getPlaneFromRGBA(rgb, ImageOperations.PLANE_GRAY);
					if (val > threshold)
					{
						pos[x] = y;
						y = panel.getImage().getHeight();
					}
				}
			}

		} else if (axis == AXIS_X)
		{

		}
		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] = 0.9f;
		}
		for (int i = 0; i < value.length; i++)
		{
			value[i] = 0.9f;
		}

		panel.repaint();
	}

	public void setDataRange(float[] range)
	{
		this.xRange[0] = range[0];
		this.xRange[1] = range[1];
	}

	public void setDataLength(int length)
	{
		this.dataLength = length;
		if (aScan.length != dataLength)
		{
			aScan = new float[dataLength];
		}
	}

	public int getDataLength()
	{
		if (axis == AXIS_X)
		{
			return panel.getImage().getWidth();
		} else if (axis == AXIS_Y)
		{
			return panel.getImage().getHeight();
		}
		return 0;
	}

	public boolean updatePlotPanel()
	{

		if (isBlockUpdate())
		{
			return false;
		}

		if (realTimeImage.isSelected())
		{
			previewPanel.setImage(getFlattenedImage());
		}
		// Chack data size of
		int length = getDataLength();

		if (xData.length != length)
		{
			xData = new float[length];
		}

		if (aScan.length != length)
		{
			aScan = new float[length];
		}

		// Update X Range
		float[] range = xRange;
		for (int i = 0; i < xData.length; i++)
		{
			xData[i] = (range[0] + (range[1] - range[0])
					* (i / (xData.length - 1.f)));
		}

		xData = PlotingToolkit.getXDataFloat(aScan.length);
		updateAScan();

		int pos = (int) getOffset();
		if (pos < 0)
		{
			pos = 0;
		}
		XYSeriesCollection datCol1 = PlotingToolkit
				.getCollection(xData, aScan, "Data");

		XYSeriesCollection datCol2 = PlotingToolkit.getCollection(new float[]
		{ xData[pos] }, new float[]
		{ aScan[pos] }, "Data");

		dataPlot.getXYPlot().setDataset(0, datCol1);
		dataPlot.getXYPlot().setDataset(1, datCol2);

		XYLineAndShapeRenderer dataRender1 = new XYLineAndShapeRenderer(true,
				false);
		XYLineAndShapeRenderer dataRender2 = new XYLineAndShapeRenderer(false,
				true);

		dataRender1.setSeriesPaint(0, Color.CYAN);
		dataRender2.setSeriesPaint(0, Color.RED);

		dataPlot.getXYPlot().setRenderer(0, dataRender1);
		dataPlot.getXYPlot().setRenderer(1, dataRender2);

		// XYSeriesCollection datCol1 = PlotingToolkit
		// .getCollection(xData, aScan, "Data");
		//
		// XYLineAndShapeRenderer dataRender1 = new XYLineAndShapeRenderer(true,
		// false);
		//
		// dataRender1.setSeriesPaint(0, Color.CYAN);
		// dataPlot.getXYPlot().setRenderer(0, dataRender1);
		// dataPlot.getXYPlot().setDataset(0, datCol1);

		dataPanel.repaint();
		return true;
	}

	public void updateAScan()
	{
		if (isBlockUpdate())
		{
			return;
		}
		ROIPanel img = view;
		float[] rst = null;
		int[] count = null;

		if (axis == AXIS_X)
		{
			rst = new float[img.getImage().getWidth()];
			count = new int[img.getImage().getWidth()];
		} else if (axis == AXIS_Y)
		{
			rst = new float[img.getImage().getHeight()];
			count = new int[img.getImage().getHeight()];
		}
		// This will get the smoothed out aScan from the Dynamic?Range panel
		int pos = 0;

		int data = 0;
		int average = 0;

		if (axis == AXIS_Y)
		{
			average = img.getImage().getWidth();
			data = img.getImage().getHeight();
		} else if (axis == AXIS_X)
		{
			average = img.getImage().getHeight();
			data = img.getImage().getWidth();
		}
		for (int i = 0; i < average; i++)
		{
			pos = 0;
			if (getUseData(i))
			{
				int start = 0;

				start = (int) (data * (1 - getSelectionValue(i)));

				for (int j = start; j < data; j++)
				{
					int rgb = 0;

					if (axis == AXIS_Y)
					{
						if (i > 0 && j > 0 && i < img.getImage().getWidth()
								&& j < img.getImage().getHeight())
						{

							rgb = img.getImage().getRGB(i, j);
							int val = ImageOperations
									.getPlaneFromRGBA(rgb, plane);
							count[pos]++;
							rst[pos++] += val;
						}
					} else if (axis == AXIS_X)
					{
						if (i > 0 && j > 0 && i < img.getImage().getHeight()
								&& j < img.getImage().getWidth())
						{
							rgb = img.getImage().getRGB(j, i);
							int val = ImageOperations
									.getPlaneFromRGBA(rgb, plane);
							count[pos]++;
							rst[pos++] += val;
						}
					}

				}
			}
		}

		for (int i = 0; i < aScan.length; i++)
		{

			if (i < rst.length)
			{
				aScan[i] = rst[i] / count[i];
			} else
			{

				aScan[i] = 0;
			}

		}
	}

	public ROIPanel getView()
	{
		return view;
	}

	// public void setView(DynamicRangeImage view)
	// {
	// this.view = view;
	// updateImage();
	// }

	public void setCrossColor(Color c)
	{
		crossColor = c;
	}

	public void setDataPoints(int size)
	{
		updateSelectionData();
		this.dataPoints = size;

		float[] newValue = new float[dataPoints];
		boolean[] newUseData = new boolean[dataPoints];

		double scale = (double) (selectionData.length - 1)
				/ (newValue.length - 1);

		for (int i = 0; i < newValue.length; i++)
		{
			double pos = i * scale;
			newValue[i] = getSelectionValue((int) Math.round(pos));
			newUseData[i] = getUseData((int) Math.round(pos));
		}
		value = newValue;
		useData = newUseData;
		getPanel().repaint();

	}

	public void setSelectionData(float[] data)
	{
		this.selectionData = data;
	}

	/**
	 * dont forget to updateData
	 * 
	 * @return
	 */
	public float[] getSelctionData()
	{
		return selectionData;
	}

	public boolean[] getUseData()
	{
		return useData;
	}

	public float[] getValue()
	{
		return value;
	}

	/**
	 * This will update the data from the values chosen by the use
	 */
	public void updateSelectionData()
	{
		int total = 0;

		if (axis == AXIS_Y)
		{
			total = panel.getImage().getWidth();
		} else if (axis == AXIS_X)
		{
			total = panel.getImage().getHeight();
		}
		if (selectionData.length != total)
		{
			selectionData = new float[total];
		}

		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] = getSelectionValue(i);
		}
	}

	/**
	 * This will get a data point from a given value (value is reduced data set)
	 * data point pepresent full data
	 * 
	 * @param pos
	 * @return
	 */
	public float getSelectionValue(int pos)
	{
		float x = pos * (float) (value.length - 1) / (selectionData.length - 1);
		int x0 = (int) Math.floor(x);
		int x1 = (int) Math.ceil(x);
		if (x0 == x1)
		{
			return value[x0];
		}
		return (value[x0] + ((x - x0) / (x1 - x0)) * (value[x1] - value[x0]));
	}

	public void setAxis(int axis)
	{
		if (axis == AXIS_Y)
		{
			this.axis = axis;
		} else if (axis == AXIS_X)
		{
			this.axis = axis;
		}
		updateSelectionData();
		updateAScan();
		updatePlotPanel();
	}

	public boolean getUseData(int pos)
	{
		float val = 0;

		if (axis == AXIS_Y)
		{
			val = pos * (float) (value.length - 1) / (selectionData.length - 1);
		} else if (axis == AXIS_X)
		{
			val = pos * (float) (value.length - 1) / (selectionData.length - 1);
		}
		int x0 = (int) Math.floor(val);
		int x1 = (int) Math.ceil(val);

		if (x0 == x1)
		{
			return useData[x0];
		}

		if (useData[x0] == useData[x1])
		{
			return useData[x0];
		}

		if (val - x0 > 0.5)
		{
			return useData[x1];
		} else
		{
			return useData[x0];
		}

	}

	public int getDataPoints()
	{
		return dataPoints;
	}

	public static void main(String[] input)
	{
		try
		{
			ROIPanel pan = new ROIPanel(false);

			// BufferedImage img = ImageOperations.getBi(600);

			BufferedImage img = ImageOperations.getGrayTestImage(600, 600, 3);
			// ImageOperations.fillWithRandomColorSquares(5, 5, img);
			// ImageOperations.fillWithRandomColors(img);
			pan.setImage(img);

			ImageProfileTool tool = new ImageProfileTool(pan);

			tool.setListening(true);
			tool.setPanel(pan);

			tool.setDataLength(600);
			tool.setDataRange(new float[]
			{ 0, 600 });

			JPanel holder = new JPanel(new GridLayout(3, 1));
			holder.add(pan);
			holder.add(tool.getChartHolderPanel());
			holder.add(tool.getControls());
			FrameFactroy.getFrame(holder);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics2D g)
	{
		if (isBlockUpdate())
		{
			return;
		}
		updateSelectionData();

		float tra = (transparance.getValue() / (float) (transparance
				.getMaximum()));

		float lineSize = 1f / (float) panel.getScale();
		float crossSize = (float) (pointSize / panel.getScale());

		RenderingHints oldHinds = g.getRenderingHints();
		Composite oldcomp = g.getComposite();
		Stroke oldStroke = g.getStroke();
		g
				.setComposite(AlphaComposite
						.getInstance(AlphaComposite.SRC_OVER, tra));
		g.setStroke(new BasicStroke(lineSize));
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		Line2D.Double line = new Line2D.Double();

		double x1 = 0;
		double x2 = 0;
		double y1 = 0;
		double y2 = 0;
		for (int i = 0; i < selectionData.length - 1; i++)
		{
			if (axis == AXIS_X)
			{
				y1 = view.getImage().getHeight()
						/ (double) (selectionData.length - 1) * i;
				x1 = view.getImage().getWidth() * (1 - getSelectionValue(i));

				y2 = view.getImage().getHeight()
						/ (double) (selectionData.length - 1) * (i + 1);
				x2 = view.getImage().getWidth()
						* (1 - getSelectionValue(i + 1));
			} else if (axis == AXIS_Y)
			{
				x1 = view.getImage().getWidth()
						/ (double) (selectionData.length - 1) * i;
				y1 = view.getImage().getHeight() * (1 - getSelectionValue(i));

				x2 = view.getImage().getWidth()
						/ (double) (selectionData.length - 1) * (i + 1);
				y2 = view.getImage().getHeight()
						* (1 - getSelectionValue(i + 1));
			}

			if (getUseData(i))
			{
				g.setColor(getPointColorSelected());
			} else
			{
				g.setColor(getPointColorNotSelected());
			}
			line.x1 = x1;
			line.x2 = x2;
			line.y1 = y1;
			line.y2 = y2;

			g.draw(line);

			if (showOffset.isSelected())
			{
				if (getUseData(i))
				{
					g.setColor(getOffsetColor());
					double offset = (Double) this.offset.getValue();

					if (axis == AXIS_X)
					{
						line.x1 += offset;
						line.x2 += offset;

					} else if (axis == AXIS_Y)
					{
						line.y1 += offset;
						line.y2 += offset;
					}
					g.draw(line);
				}
			}
		}

		// Draw each point
		if (drawCrosses)
		{
			double x = 0;
			double y = 0;
			for (int i = 0; i < value.length; i++)
			{

				if (axis == AXIS_X)
				{
					y = view.getImage().getHeight()
							/ (double) (value.length - 1) * i;
					x = view.getImage().getWidth() * (1 - value[i]);
				} else if (axis == AXIS_Y)
				{
					x = view.getImage().getWidth()
							/ (double) (value.length - 1) * i;
					y = view.getImage().getHeight() * (1 - value[i]);
				}
				DrawTools
						.drawCross(g, new Point2D.Double(x, y), crossSize, 0, getCrossColor(), lineSize);

			}
		}
		g.setComposite(oldcomp);
		g.setStroke(oldStroke);
		g.setRenderingHints(oldHinds);
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

		processMouse(e.getPoint(), e.getButton());

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

		processMouse(e.getPoint(), e.getButton());

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{

		int button = 0;

		if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK)
		{
			button = 1;
		} else if (e.getModifiersEx() == InputEvent.BUTTON2_DOWN_MASK)
		{
			button = 2;
		} else if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK)
		{
			button = 3;
		}

		processMouse(e.getPoint(), button);

	}

	public void processMouse(Point p1, int button)
	{
		int pos = 0;
		float val = 0;

		if (button == 2)
		{
			return;
		}
		Point p = panel.panelToImageCoords(p1);
		if (axis == AXIS_Y)
		{
			pos = Math
					.round((p.x / (float) view.getImage().getWidth() * (value.length - 1)));
			val = 1 - p.y / (float) view.getImage().getHeight();
		} else if (axis == AXIS_X)
		{
			pos = Math
					.round((p.y / (float) view.getImage().getHeight() * (value.length - 1)));
			val = 1 - p.x / (float) view.getImage().getWidth();
		}
		if (pos < 0 || pos > value.length - 1)
		{
			return;
		}
		if (val > 1)
		{
			val = 1;
		}
		if (val < 0)
		{
			val = 0;
		}

		if (button == 3)
		{
			useData[pos] = false;
		} else
		{
			useData[pos] = true;
		}

		value[pos] = val;
		view.repaint();

		panel.shapeChanged();
		updatePlotPanel();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void setValues(float data[])
	{
		for (int i = 0; i < 256; i++)
		{
			selectionData[i] = data[i];
		}

		for (int i = 0; i < value.length; i++)
		{
			int pos = (int) (255 * i / (value.length - 1.));
			value[i] = selectionData[pos];
		}

		updateSelectionData();
		view.repaint();
	}

	public void setPanelData(ImageProfileTool p)
	{
		boolean preBlockP = p.isBlockUpdate();
		boolean preBlockThis = isBlockUpdate();

		p.setBlockUpdate(true);
		setBlockUpdate(true);

		dataPoints = p.dataPoints;
		crossColor = new Color(p.crossColor.getRGB());
		pointSize = p.pointSize;
		value = new float[p.value.length];
		for (int i = 0; i < value.length; i++)
		{
			value[i] = p.value[i];
		}

		selectionData = new float[p.selectionData.length];
		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] = p.selectionData[i];
		}

		useData = new boolean[p.useData.length];
		for (int i = 0; i < useData.length; i++)
		{
			useData[i] = p.useData[i];
		}

		offset.setValue(p.offset.getValue());
		showOffset.setSelected(p.showOffset.isSelected());

		numPoints.setValue(dataPoints);

		p.setBlockUpdate(false || preBlockP);
		setBlockUpdate(false || preBlockThis);
	}

	public Color getPointColorSelected()
	{
		return pointColorSelected;
	}

	public void setPointColorSelected(Color pointColorSelected)
	{
		this.pointColorSelected = pointColorSelected;
	}

	public Color getPointColorNotSelected()
	{
		return pointColorNotSelected;
	}

	public void setPointColorNotSelected(Color pointColorNotSelected)
	{
		this.pointColorNotSelected = pointColorNotSelected;
	}

	public Color getOffsetColor()
	{
		return offsetColor;
	}

	public void setOffsetColor(Color offsetColor)
	{
		this.offsetColor = offsetColor;
	}

	public Color getCrossColor()
	{
		return crossColor;
	}

	public double getOffset()
	{
		return (Double) offset.getValue();
	}

	public boolean isDrawCrosses()
	{
		return drawCrosses;
	}

	public void setDrawCrosses(boolean drawCrosses)
	{
		this.drawCrosses = drawCrosses;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		setBlockUpdate(true);
		int version = in.readInt();

		dataPoints = in.readInt();
		crossColor = new Color(in.readInt());
		pointSize = in.readDouble();

		value = new float[in.readInt()];
		for (int i = 0; i < value.length; i++)
		{
			value[i] = in.readFloat();
		}

		selectionData = new float[in.readInt()];
		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] = in.readFloat();
		}

		useData = new boolean[in.readInt()];
		for (int i = 0; i < useData.length; i++)
		{
			useData[i] = in.readBoolean();
		}

		offset.setValue(in.readDouble());
		showOffset.setSelected(in.readBoolean());

		setBlockUpdate(false);
		// dataPoints = p.dataPoints;
		// crossColor = new Color(p.crossColor.getRGB());
		// pointSize = p.pointSize;
		// value = new float[p.value.length];
		// for (int i = 0; i < value.length; i++)
		// {
		// value[i] = p.value[i];
		// }
		//
		// selectionData = new float[p.selectionData.length];
		// for (int i = 0; i < selectionData.length; i++)
		// {
		// selectionData[i] = p.selectionData[i];
		// }
		//
		// useData = new boolean[p.useData.length];
		// for (int i = 0; i < useData.length; i++)
		// {
		// useData[i] = p.useData[i];
		// }
		//
		// offset.setValue(p.offset.getValue());
		// showOffset.setSelected(p.showOffset.isSelected());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		int version = 1;
		out.writeInt(version);

		out.writeInt(dataPoints);
		out.writeInt(crossColor.getRGB());
		out.writeDouble(pointSize);

		out.writeInt(value.length);
		for (int i = 0; i < value.length; i++)
		{
			out.writeFloat(value[i]);
		}

		out.writeInt(selectionData.length);
		for (int i = 0; i < selectionData.length; i++)
		{
			out.writeFloat(selectionData[i]);
		}

		out.writeInt(useData.length);
		for (int i = 0; i < useData.length; i++)
		{
			out.writeBoolean(useData[i]);
		}

		out.writeDouble((Double) offset.getValue());
		out.writeBoolean(showOffset.isSelected());

	}

	public boolean isBlockUpdate()
	{
		return blockUpdate;
	}

	public void setBlockUpdate(boolean blockUpdate)
	{
		// System.out.println(this+" - "+blockUpdate+" ["+Thread.currentThread().getName()+"]");
		// new Exception().printStackTrace(System.out);
		// System.out.println("\n\n");
		this.blockUpdate = blockUpdate;
	}

}
