package com.joey.software.regionSelectionToolkit.controlers;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.plottingToolkit.PlotingToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class ImageProfileToolDynamicRangePanel extends ROIControler
{
	private static final long serialVersionUID = 1L;

	public static int AXIS_X = 0;

	public static int AXIS_Y = 1;

	int dataPoints = 20;

	// This is the data in the user points
	float value[] = new float[dataPoints];

	// this is the real Data
	float selectionData[] = new float[256];

	boolean useData[] = new boolean[dataPoints];

	int pointSize = 1;

	Color crossColor = Color.RED;

	Color pointColorSelected = Color.CYAN;

	Color pointColorNotSelected = Color.ORANGE;

	Color offsetColor = Color.green;

	DynamicRangeImage view;

	JFreeChart dataPlot = PlotingToolkit
			.getPlot(new float[1], "Log10(A Scan)", "", "");

	ChartPanel dataPanel = new ChartPanel(dataPlot);

	JPanel chartHolderPanel = null;

	float[] xData = new float[0];

	float[] aScan = new float[0];

	float[] xRange = new float[2];

	JButton updatePoints = new JButton("Set");

	JSpinner numPoints = new JSpinner(new SpinnerNumberModel(10, 2, 10000, 1));

	JButton saveData = new JButton("Save");

	JButton moveUpData = new JButton(new ImageIcon(DrawTools
			.getMoveUPImage(20, 40)));

	JCheckBox circularShift = new JCheckBox("Circular", false);

	int delay = 100;

	Timer moveUPTimer;

	Timer moveDownTimer;

	JButton moveDownData = new JButton(new ImageIcon(DrawTools
			.getMoveDownImage(20, 40)));

	JButton showFlattenedButton = new JButton("Show");

	JPanel controls = null;

	int dataLength = 10;

	JCheckBox showOffset = new JCheckBox("Show Offset");

	JSpinner offset = new JSpinner(
			new SpinnerNumberModel(0., -10000, +10000, 1));

	JSlider transparance = new JSlider(0, 1000);

	int axis = AXIS_Y;

	public ImageProfileToolDynamicRangePanel(DynamicRangeImage viewPanel)
	{
		super(viewPanel.getImage());

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
		JFrame f = new JFrame("Flattened Image");
		JButton save = new JButton("Save");

		final DynamicRangeImage img = getFlattenedImage();

		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(img, BorderLayout.CENTER);
		f.getContentPane().add(save, BorderLayout.SOUTH);

		f.setSize(600, 480);
		f.setVisible(true);
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
						ImageIO.write(img.getImage().getImage(), "png", f);
					}
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

	}

	public DynamicRangeImage getFlattenedImage()
	{
		DynamicRangeImage img = view;

		ROIPanel pan = img.getImage();
		int wide = 0;
		for (int i = 0; i < img.getImage().getImage().getWidth(); i++)
		{
			if (getUseData(i))
			{
				wide++;
			}
		}

		float[][] rst = new float[wide][img.getImage().getImage().getHeight()];

		// This will get the smoothed out aScan from the Dynamic?Range panel
		int posX = 0;
		int posY = 0;

		for (int i = 0; i < img.getImage().getImage().getWidth(); i++)
		{
			posY = 0;
			if (getUseData(i))
			{
				int start = (int) (img.getImage().getImage().getHeight() * (1 - getSelectionValue(i)));

				if (!this.circularShift.isSelected())
				{
					for (int j = start; j < img.getImage().getImage()
							.getHeight(); j++)
					{
						if (img.getDataShort() != null)
						{
							rst[posX][posY++] += img.getDataShort()[i][j];
						} else if (img.getDataInteger() != null)
						{
							rst[posX][posY++] += img.getDataInteger()[i][j];
						} else if (img.getDataFloat() != null)
						{
							rst[posX][posY++] += img.getDataFloat()[i][j];
						} else if (img.getDataDouble() != null)
						{
							rst[posX][posY++] += img.getDataDouble()[i][j];
						} else
						{
							rst[posX][posY++] += 0;
						}

					}

				} else
				{

					for (int j = 0; j < img.getImage().getImage().getHeight(); j++)
					{
						int y = j + start;
						if (y >= img.getImage().getImage().getHeight())
						{
							y -= img.getImage().getImage().getHeight();
						}

						if (img.getDataShort() != null)
						{
							rst[posX][j] += img.getDataShort()[i][y];
						} else if (img.getDataInteger() != null)
						{
							rst[posX][j] += img.getDataInteger()[i][y];
						} else if (img.getDataFloat() != null)
						{
							rst[posX][j] += img.getDataFloat()[i][y];
						} else if (img.getDataDouble() != null)
						{
							rst[posX][j] += img.getDataDouble()[i][y];
						} else
						{
							rst[posX][j] += 0;
						}
					}
				}
				posX++;
			}

		}

		return new DynamicRangeImage(rst);
	}

	private void setView(DynamicRangeImage view)
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

			JPanel pointsPanel = new JPanel(new BorderLayout());
			pointsPanel.add(saveData, BorderLayout.NORTH);
			pointsPanel.add(numPoints, BorderLayout.CENTER);
			pointsPanel.add(updatePoints, BorderLayout.EAST);
			pointsPanel.add(showFlattenedButton, BorderLayout.SOUTH);

			JPanel offsetPane = new JPanel(new BorderLayout());
			offsetPane.add(showOffset, BorderLayout.WEST);
			offsetPane.add(offset, BorderLayout.CENTER);

			JPanel temp = new JPanel(new BorderLayout());
			temp.add(offsetPane, BorderLayout.NORTH);
			temp.add(transparance, BorderLayout.CENTER);

			JPanel toolPanel = new JPanel(new BorderLayout());
			toolPanel.add(dirButton, BorderLayout.NORTH);
			toolPanel.add(pointsPanel, BorderLayout.CENTER);
			toolPanel.add(temp, BorderLayout.SOUTH);

			controls.add(toolPanel, BorderLayout.NORTH);

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
					addValue(1.0f / view.getImage().getImage().getHeight());
				}
			});

			moveDownTimer = new Timer(delay, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					addValue(-1.0f / view.getImage().getImage().getHeight());
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
					addValue(1.0f / view.getImage().getImage().getHeight());

				}
			});

			moveDownData.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					addValue(-1.0f / view.getImage().getImage().getHeight());

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
	}

	public void estimateSurface()
	{
		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] = 0.9f;
		}
		for (int i = 0; i < value.length; i++)
		{
			value[i] = 0.9f;
		}
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
		return dataLength;
	}

	public void updatePlotPanel()
	{
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
					* (i / (xData.length - 1)));
		}

		updateAScan();

		XYSeriesCollection datCol1 = PlotingToolkit
				.getCollection(xData, aScan, "Data");

		XYLineAndShapeRenderer dataRender1 = new XYLineAndShapeRenderer(true,
				false);

		dataRender1.setSeriesPaint(0, Color.CYAN);
		dataPlot.getXYPlot().setRenderer(0, dataRender1);
		dataPlot.getXYPlot().setDataset(0, datCol1);
	}

	public void updateAScan()
	{
		DynamicRangeImage img = view;
		float[] rst = new float[img.getImage().getImage().getHeight()];
		int[] count = new int[img.getImage().getImage().getHeight()];

		// This will get the smoothed out aScan from the Dynamic?Range panel
		int pos = 0;

		for (int i = 0; i < img.getImage().getImage().getWidth(); i++)
		{
			pos = 0;
			if (getUseData(i))
			{

				int start = (int) (img.getImage().getImage().getHeight() * (1 - getSelectionValue(i)));

				for (int j = start; j < img.getImage().getImage().getHeight(); j++)
				{
					count[pos]++;
					if (img.getDataShort() != null)
					{
						rst[pos++] += img.getDataShort()[i][j];
					} else if (img.getDataInteger() != null)
					{
						rst[pos++] += img.getDataInteger()[i][j];
					} else if (img.getDataFloat() != null)
					{
						rst[pos++] += img.getDataFloat()[i][j];
					} else if (img.getDataDouble() != null)
					{
						rst[pos++] += img.getDataDouble()[i][j];
					} else
					{
						rst[pos++] += 0;
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
				System.out.println("Bad");
				aScan[i] = 0;
			}
		}
	}

	public DynamicRangeImage getView()
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

	/**
	 * dont forget to updateData
	 * 
	 * @return
	 */
	public float[] getSelctionData()
	{
		return selectionData;
	}

	/**
	 * This will update the data from the values chosen by the use
	 */
	public void updateSelectionData()
	{
		if (selectionData.length != panel.getImage().getWidth())
		{
			selectionData = new float[panel.getImage().getWidth()];
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

	public boolean getUseData(int pos)
	{
		float x = pos * (float) (value.length - 1) / (selectionData.length - 1);
		int x0 = (int) Math.floor(x);
		int x1 = (int) Math.ceil(x);

		if (x0 == x1)
		{
			return useData[x0];
		}

		if (useData[x0] == useData[x1])
		{
			return useData[x0];
		}

		if (x - x0 > 0.5)
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
			DynamicRangeImage img = new DynamicRangeImage(ImageOperations
					.getGrayTestImage(600, 600, 3));

			JPanel p = new JPanel(new BorderLayout());
			p.add(img, BorderLayout.CENTER);

			img.setPanelMode(DynamicRangeImage.TYPE_MANUAL_PROFILE);
			FrameFactroy.getFrame(img);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics2D g)
	{

		updateSelectionData();

		float tra = (transparance.getValue() / (float) (transparance
				.getMaximum()));

		Composite oldcomp = g.getComposite();
		g
				.setComposite(AlphaComposite
						.getInstance(AlphaComposite.SRC_OVER, tra));
		Line2D.Double line = new Line2D.Double();
		for (int i = 0; i < selectionData.length - 1; i++)
		{
			double x1 = view.getImage().getImage().getWidth()
					/ (double) (selectionData.length - 1) * i;
			double y1 = view.getImage().getImage().getHeight()
					* (1 - getSelectionValue(i));

			double x2 = view.getImage().getImage().getWidth()
					/ (double) (selectionData.length - 1) * (i + 1);
			double y2 = view.getImage().getImage().getHeight()
					* (1 - getSelectionValue(i + 1));

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
					line.y1 += offset;
					line.y2 += offset;
					g.draw(line);
				}
			}
		}

		// Draw each point

		for (int i = 0; i < value.length; i++)
		{
			g.setColor(getCrossColor());
			double x = view.getImage().getImage().getWidth()
					/ (double) (value.length - 1) * i;
			double y = view.getImage().getImage().getHeight() * (1 - value[i]);
			DrawTools.drawCross(g, new Point2D.Double(x, y), pointSize * 2);

		}

		g.setComposite(oldcomp);
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

		pos = Math
				.round((p.x / (float) view.getImage().getImage().getWidth() * (value.length - 1)));
		val = 1 - p.y / (float) view.getImage().getImage().getHeight();

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

	public void setPanelData(ImageProfileToolDynamicRangePanel p)
	{
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

}
