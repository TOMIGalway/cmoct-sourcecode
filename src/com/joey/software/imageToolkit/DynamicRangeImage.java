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
package com.joey.software.imageToolkit;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.imageToolkit.colorMapping.ColorMapChooser;
import com.joey.software.imageToolkit.histogram.HistogramPanel;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.controlers.ImageProfileToolDynamicRangePanel;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;


public class DynamicRangeImage extends JPanel implements ChangeListener
{
	public static final int PANEL_TYPE_FULL = 0;

	public static final int PANEL_TYPE_NONE = 1;

	public static final int PANEL_TYPE_BASIC = 2;

	static final int SLIDER_STEPS = 100000;

	int panelType = PANEL_TYPE_FULL;

	DecimalFormat displayFormat = new DecimalFormat();

	JSpinner maxValue = new JSpinner(new SpinnerNumberModel(0.0,
			-Double.MAX_VALUE, +Double.MAX_VALUE, 0.1));

	JSpinner minValue = new JSpinner(new SpinnerNumberModel(0.0,
			-Double.MAX_VALUE, +Double.MAX_VALUE, 0.1));

	double[] dataRange = new double[2];

	float[][] dataFloat = null;

	double[][] dataDouble = null;

	short[][] dataShort = null;

	int[][] dataInteger = null;

	byte[][] dataByte = null;

	float[] backgroundFloat = null;

	double[] backgroundDouble = null;

	short[] backgroundShort = null;

	int[] backgroundInteger = null;

	byte[] backgroundByte = null;

	boolean blockUpdateRange = false;

	JSlider maxJSlider = new JSlider(0, SLIDER_STEPS, SLIDER_STEPS);

	JSlider minJSlider = new JSlider(0, SLIDER_STEPS, 0);

	JTextField maxCurrentValueField = new JTextField(4);

	JTextField minCurrentValueField = new JTextField(4);

	JTextField minValueField = new JTextField(4);

	JTextField maxValueField = new JTextField(4);

	ROIPanel image = new ROIPanel(false, ROIPanel.TYPE_POLYGON);

	JButton saveData = new JButton("Save Image");

	JButton changeColorMap = new JButton("Change Map");

	JButton showHistogram = new JButton("Show Hist");

	JButton updateRange = new JButton("Update Range");

	JButton repaintPanel = new JButton("Repaint");

	JButton backgroundButton = new JButton("Background");

	JPanel mainPanel = new JPanel(new BorderLayout());

	JPanel imageHolderPanel = new JPanel(new BorderLayout());

	JPanel toolPanel = new JPanel(new BorderLayout());

	ColorMap map = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	ColorMapChooser colorChooser = new ColorMapChooser();

	JCheckBox useInterpolotion = new JCheckBox("Color Interpolation", false);

	JCheckBox realTimeHist = new JCheckBox("Update Histogram", false);

	JComboBox fitImage = new JComboBox(new String[] { "No", "Fit", "Scale" });

	JCheckBox useLogScaleing = new JCheckBox("Log Image", false);

	HistogramPanel histogram = new HistogramPanel(1000);

	JFrame histogramFrame = new JFrame("Histogram Panel");

	JSpinner posAvgNum = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

	PolygonControler pointPosition = new PolygonControler(this.getImage());

	ImageProfileToolDynamicRangePanel imageProfile = new ImageProfileToolDynamicRangePanel(
			this);

	public static final int TYPE_PLAIN_PANEL = 0;

	public static final int TYPE_POINT_PROFILE = 1;

	public static final int TYPE_MANUAL_PROFILE = 2;

	int panelMode = TYPE_PLAIN_PANEL;

	public boolean isUseLogScaleing()
	{
		return useLogScaleing.isSelected();
	}

	public JSlider getMaxJSlider()
	{
		return maxJSlider;
	}

	public JSlider getMinJSlider()
	{
		return minJSlider;
	}

	public void setUseLogScaleing(boolean useLogScaleing)
	{
		this.useLogScaleing.setSelected(useLogScaleing);
	}

	public DynamicRangeImage()
	{
		setDataFloat(new float[1][1]);
		new MouseLocationUpdater(this);
		image.setShowRGBValueOnMouseMove(true);
		maxJSlider.addChangeListener(this);
		minJSlider.addChangeListener(this);

		maxJSlider.setPaintTicks(true);
		minJSlider.setPaintTicks(true);

		maxJSlider.setMajorTickSpacing(SLIDER_STEPS / 2);
		minJSlider.setMajorTickSpacing(SLIDER_STEPS / 2);

		histogramFrame.getContentPane().setLayout(new BorderLayout());
		histogramFrame.getContentPane().add(histogram);
		histogramFrame.setSize(600, 480);
		createJPanel();
		createListners();
		updateImagePanel();
		setPanelMode(TYPE_PLAIN_PANEL);
		image.setRegionColor(Color.cyan);
	}

	private void createListners()
	{
		backgroundButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadBackground();
			}
		});
		saveData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				exportCurrentImage();

			}
		});
		changeColorMap.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				changeColorMap();
			}
		});

		repaintPanel.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				updateImagePanel();

			}
		});
		updateRange.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateMaxMin();
				updateImagePanel();
			}
		});

		useLogScaleing.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateMaxMin();
				updateImagePanel();

			}
		});
		showHistogram.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				showHistogram();

			}
		});

		fitImage.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (fitImage.getSelectedIndex() == 0)
				{
					image.setPanelType(ImagePanel.TYPE_NORMAL);
					if (image.getXScale() != image.getYScale())
					{
						image.setScale(image.getScale(), image.getScale(), true);
					}
				} else if (fitImage.getSelectedIndex() == 1)
				{
					image.setPanelType(ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);
				} else
				{
					image.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
				}

			}
		});

	}

	public int getPanelMode()
	{
		return panelMode;
	}

	public void setPanelMode(int panelMode)
	{
		this.panelMode = panelMode;
		pointPosition.setListening(false);
		imageProfile.setListening(false);

		if (panelMode == TYPE_PLAIN_PANEL)
		{
			image.setControler(null);

			toolPanel.removeAll();
		} else if (panelMode == TYPE_POINT_PROFILE)
		{
			image.setControler(pointPosition);
			pointPosition.setListening(true);

			JPanel southPanel = new JPanel(new BorderLayout());
			southPanel.add(posAvgNum, BorderLayout.EAST);

			toolPanel.removeAll();
			toolPanel.add(southPanel, BorderLayout.CENTER);
		} else if (panelMode == TYPE_MANUAL_PROFILE)
		{
			imageProfile.setListening(true);
			image.setControler(imageProfile);
			toolPanel.removeAll();
			toolPanel.add(imageProfile.getControls(), BorderLayout.CENTER);
		}
		try
		{
			getTopLevelAncestor().validate();
		} catch (Exception e)
		{

		}
		repaint();
	}

	/**
	 * this will return a int stating the posiion
	 * of the slider in the current image;
	 * 
	 * @return
	 */
	public int getPositionSelectorPos()
	{
		PolygonControler poly = pointPosition;

		Point2D.Double p1 = poly.points.get(0);

		return (int) p1.getX();
	}

	/**
	 * this will draw a line for the position
	 * selector on the current image.
	 * 
	 * @param c
	 */
	public void drawPositionSlider(Color c)
	{
		int pos = getPositionSelectorPos();
		Graphics2D g = image.getImage().createGraphics();
		g.setColor(c);
		g.drawLine(pos, 0, pos, image.getHeight());
	}

	public ROIPanel getImage()
	{
		return image;
	}

	public Point2D.Double getMarkerPosition()
	{
		if (panelMode == TYPE_POINT_PROFILE)
		{
			PolygonControler poly = pointPosition;
			return poly.points.get(0);
		} else if (panelMode == TYPE_MANUAL_PROFILE)
		{
			Point2D.Double p = new Point2D.Double(0, imageProfile.getOffset());
			return p;
		} else
			return new Point2D.Double(0, 0);
	}

	public void showHistogram()
	{
		if (dataShort != null)
		{
			histogram
					.setData(dataShort, (short) getMinSelection(), (short) getMaxSelection());
		} else if (dataInteger != null)
		{
			histogram
					.setData(dataInteger, (int) getMinSelection(), (int) getMaxSelection());
		} else if (dataFloat != null)
		{
			histogram
					.setData(dataFloat, (float) getMinSelection(), (float) getMaxSelection());
		} else if (dataDouble != null)
		{
			histogram.setData(dataDouble, getMinSelection(), getMaxSelection());
		} else if (dataByte != null)
		{
			histogram
					.setData(dataByte, (int) getMinSelection(), (int) getMaxSelection());
		}

		histogramFrame.setVisible(true);
	}

	public DynamicRangeImage(float[][] data)
	{
		this();
		setDataFloat(data);
	}

	public DynamicRangeImage(double[][] data)
	{
		this();
		setDataDouble(data);
	}

	public DynamicRangeImage(short[][] data)
	{
		this();
		setDataShort(data);
	}

	public DynamicRangeImage(int[][] data)
	{
		this();
		setDataInteger(data);
	}

	public DynamicRangeImage(byte[][] data)
	{
		this();
		setDataByte(data);
	}

	public DynamicRangeImage(BufferedImage img)
	{
		this();
		setData(img);
	}

	public void setDataByte(byte[][] data)
	{
		this.dataFloat = null;
		this.dataShort = null;
		this.dataInteger = null;
		this.dataDouble = null;
		this.dataByte = data;

		if (!isBlockUpdateRange())
		{
			try
			{
				maxValue.setValue((double) data[0][0]);
				minValue.setValue((double) data[0][0]);
			} catch (ArrayIndexOutOfBoundsException e)
			{
				maxValue.setValue((double) 0);
				minValue.setValue((double) 0);

			}

			updateMaxMin();
		}
		removeBackground();
		updateImagePanel();
	}

	public void setDataFloat(float[][] data)
	{
		this.dataFloat = data;
		this.dataShort = null;
		this.dataInteger = null;
		this.dataDouble = null;
		this.dataByte = null;

		if (!isBlockUpdateRange())
		{
			try
			{
				maxValue.setValue((double) data[0][0]);
				minValue.setValue((double) data[0][0]);
			} catch (ArrayIndexOutOfBoundsException e)
			{
				maxValue.setValue((double) 0);
				minValue.setValue((double) 0);

			}

			updateMaxMin();
		}
		removeBackground();
		updateImagePanel();
	}

	public void setDataDouble(double[][] data)
	{
		this.dataFloat = null;
		this.dataShort = null;
		this.dataInteger = null;
		this.dataDouble = data;
		this.dataByte = null;

		if (!isBlockUpdateRange())
		{
			try
			{
				maxValue.setValue(data[0][0]);
				minValue.setValue(data[0][0]);
			} catch (ArrayIndexOutOfBoundsException e)
			{
				maxValue.setValue((double) 0);
				minValue.setValue((double) 0);

			}

			updateMaxMin();
		}
		removeBackground();
		updateImagePanel();
	}

	public void setDataShort(short[][] data)
	{
		this.dataFloat = null;
		this.dataShort = data;
		this.dataInteger = null;
		this.dataDouble = null;
		this.dataByte = null;

		if (!isBlockUpdateRange())
		{
			try
			{
				maxValue.setValue((double) data[0][0]);
				minValue.setValue((double) data[0][0]);
			} catch (ArrayIndexOutOfBoundsException e)
			{
				maxValue.setValue((double) 0);
				minValue.setValue((double) 0);

			}

			updateMaxMin();
		}
		removeBackground();
		updateImagePanel();
	}

	public void setDataInteger(int[][] data)
	{
		this.dataFloat = null;
		this.dataShort = null;
		this.dataInteger = data;
		this.dataDouble = null;
		this.dataByte = null;

		if (!isBlockUpdateRange())
		{
			try
			{
				maxValue.setValue((double) data[0][0]);
				minValue.setValue((double) data[0][0]);
			} catch (ArrayIndexOutOfBoundsException e)
			{
				maxValue.setValue((double) 0);
				minValue.setValue((double) 0);

			}

			updateMaxMin();
		}
		removeBackground();
		updateImagePanel();
	}

	private void removeBackground()
	{
		if (dataFloat != null)
		{
			removeBackgroundFloat();
		} else if (dataDouble != null)
		{
			removeBackgroundDouble();
		} else if (dataShort != null)
		{
			removeBackgroundShort();
		} else if (dataInteger != null)
		{
			removeBackgroundInteger();
		} else if (dataByte != null)
		{
			removeBackgroundByte();
		}

	}

	public float[][] getDataFloat()
	{
		return dataFloat;
	}

	public double[][] getDataDouble()
	{
		return dataDouble;
	}

	public short[][] getDataShort()
	{
		return dataShort;
	}

	public int[][] getDataInteger()
	{
		return dataInteger;
	}

	public double getMaxValue()
	{
		return (Double) maxValue.getValue();
	}

	public void setMaxValue(float maxValue)
	{
		this.maxValue.setValue((double) maxValue);
	}

	public double getMinValue()
	{
		return (Double) minValue.getValue();
	}

	public void setMinValue(float minValue)
	{
		this.minValue.setValue((double) minValue);
	}

	public void updateMaxMin()
	{
		if (dataFloat != null)
		{
			updateMaxMinFloat();
		} else if (dataDouble != null)
		{
			updateMaxMinDouble();
		} else if (dataShort != null)
		{
			updateMaxMinShort();
		} else if (dataInteger != null)
		{
			updateMaxMinInteger();
		} else if (dataByte != null)
		{
			updateMaxMinByte();
		}

		if (getMinValue() > getMaxValue())
		{
			float min = (float) getMinValue();
			setMinValue((float) getMaxValue());
			setMaxValue(min);
		}
	}

	public void loadBackground()
	{
		if (dataFloat != null)
		{
			loadBackgroundFloat();
		} else if (dataDouble != null)
		{
			loadBackgroundDouble();
		} else if (dataShort != null)
		{
			loadBackgroundShort();
		} else if (dataInteger != null)
		{
			loadBackgroundInteger();
		} else if (dataByte != null)
		{
			loadBackgroundByte();
		}
	}

	public void createJPanel()
	{
		removeAll();
		setLayout(new BorderLayout());

		maxJSlider.setOrientation(SwingConstants.VERTICAL);
		minJSlider.setOrientation(SwingConstants.VERTICAL);

		setFormatString("#.######");
		JPanel buttonPanel = new JPanel();
		if (panelType == PANEL_TYPE_BASIC)
		{
			buttonPanel.setLayout(new GridLayout(5, 1));
			buttonPanel.add(repaintPanel);
			buttonPanel.add(changeColorMap);
			buttonPanel.add(updateRange);
			buttonPanel.add(saveData);
			buttonPanel.add(useLogScaleing);
		} else
		{
			buttonPanel.setLayout(new GridLayout(7, 1));
			buttonPanel.add(repaintPanel);
			buttonPanel.add(saveData);
			buttonPanel.add(changeColorMap);
			buttonPanel.add(showHistogram);
			buttonPanel.add(updateRange);
			buttonPanel.add(backgroundButton);
			buttonPanel.add(useLogScaleing);
		}

		// Min Panel
		JPanel minPanel = new JPanel(new BorderLayout());
		minPanel.add(minJSlider, BorderLayout.CENTER);
		minPanel.add(minCurrentValueField, BorderLayout.NORTH);
		minPanel.setBorder(BorderFactory.createTitledBorder("MIN"));

		// Max Panel
		JPanel maxPanel = new JPanel(new BorderLayout());
		maxPanel.add(maxJSlider, BorderLayout.CENTER);
		maxPanel.add(maxCurrentValueField, BorderLayout.NORTH);
		maxPanel.setBorder(BorderFactory.createTitledBorder("MAX"));

		// MINMAXHolder
		JPanel rangedScalePanel = new JPanel(new GridLayout(1, 2));
		rangedScalePanel.add(minPanel);
		rangedScalePanel.add(maxPanel);

		// RangePanel
		JPanel rangePanel = new JPanel(new BorderLayout());
		rangePanel.add(rangedScalePanel, BorderLayout.CENTER);
		rangePanel.add(minValue, BorderLayout.NORTH);
		rangePanel.add(maxValue, BorderLayout.SOUTH);
		rangePanel.setBorder(BorderFactory.createTitledBorder(""));

		JPanel checksPanel = new JPanel(new GridLayout(3, 1));
		checksPanel.add(useInterpolotion);
		checksPanel.add(realTimeHist);
		checksPanel.add(fitImage);

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

		controlPanel.add(rangePanel, BorderLayout.CENTER);
		controlPanel.add(buttonPanel, BorderLayout.SOUTH);
		if (panelType == PANEL_TYPE_FULL)
		{
			controlPanel.add(checksPanel, BorderLayout.NORTH);
		}
		image.putIntoPanel(imageHolderPanel);

		mainPanel.add(imageHolderPanel, BorderLayout.CENTER);

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(mainPanel);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(controlPanel, BorderLayout.CENTER);
		leftPanel.add(toolPanel, BorderLayout.SOUTH);

		leftPanel.setMinimumSize(new Dimension(40,0));
		holder.setMinimumSize(new Dimension(40,0));
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(leftPanel);
		split.setRightComponent(holder);

		split.setDividerLocation(150);
		split.setOneTouchExpandable(true);

		removeAll();

		if (panelType == PANEL_TYPE_NONE)
		{
			add(holder, BorderLayout.CENTER);
		} else
		{
			add(split, BorderLayout.CENTER);
		}

		pointPosition.setMaxPoints(1);
		pointPosition.addPoint(new Point2D.Double());
		pointPosition.addPoint(new Point2D.Double());
	}

	public float[] getScanDataFloat()
	{
		if (getPanelMode() == TYPE_PLAIN_PANEL)
		{
			return new float[0];
		} else if (getPanelMode() == TYPE_POINT_PROFILE)
		{
			int pos = getPositionSelectorPos();
			if (getPosAverageNum() > 1)
			{
				float[] data = new float[getDataFloat()[pos].length];

				int count = 0;

				for (int i = 0; i < getPosAverageNum(); i++)
				{
					for (int j = 0; j < data.length; j++)
					{
						data[j] += getDataFloat()[pos + i][j];
					}
				}

				// Get Average
				for (int i = 0; i < data.length; i++)
				{
					data[i] /= getPosAverageNum();
				}
				return data;
			} else
			{
				return getDataFloat()[pos];
			}
		} else if (getPanelMode() == TYPE_MANUAL_PROFILE)
		{
			imageProfile.setDataLength(getDataFloat()[0].length);
			imageProfile.updateAScan();
			return imageProfile.getProfileData();
		}

		return null;
	}

	public double[] getScanDataDouble()
	{
		if (getPanelMode() == TYPE_PLAIN_PANEL)
		{
			return new double[0];
		} else if (getPanelMode() == TYPE_POINT_PROFILE)
		{
			int pos = getPositionSelectorPos();
			if (getPosAverageNum() > 1)
			{
				double[] data = new double[getDataDouble()[pos].length];

				int count = 0;

				for (int i = 0; i < getPosAverageNum(); i++)
				{
					for (int j = 0; j < data.length; j++)
					{
						data[j] += getDataDouble()[pos + i][j];
					}
				}

				// Get Average
				for (int i = 0; i < data.length; i++)
				{
					data[i] /= getPosAverageNum();
				}
				return data;
			} else
			{
				return getDataDouble()[pos];
			}
		} else if (getPanelMode() == TYPE_MANUAL_PROFILE)
		{
			imageProfile.setDataLength(getDataDouble()[0].length);
			imageProfile.updateAScan();
			float[] dat = imageProfile.getProfileData();
			double[] rst = new double[dat.length];
			for (int i = 0; i < rst.length; i++)
			{
				rst[i] = dat[i];
			}
			return rst;
		}

		return null;
	}

	public int[] getScanDataInteger()
	{
		if (getPanelMode() == TYPE_PLAIN_PANEL)
		{
			return new int[0];
		} else if (getPanelMode() == TYPE_POINT_PROFILE)
		{
			int pos = getPositionSelectorPos();
			if (getPosAverageNum() > 1)
			{
				int[] data = new int[getDataInteger()[pos].length];

				int count = 0;

				for (int i = 0; i < getPosAverageNum(); i++)
				{
					for (int j = 0; j < data.length; j++)
					{
						data[j] += getDataInteger()[pos + i][j];
					}
				}

				// Get Average
				for (int i = 0; i < data.length; i++)
				{
					data[i] /= getPosAverageNum();
				}
				return data;
			} else
			{
				return getDataInteger()[pos];
			}
		} else if (getPanelMode() == TYPE_MANUAL_PROFILE)
		{
			imageProfile.setDataLength(getDataInteger()[0].length);
			imageProfile.updateAScan();

			float[] dat = imageProfile.getProfileData();
			int[] rst = new int[dat.length];
			for (int i = 0; i < rst.length; i++)
			{
				rst[i] = (int) dat[i];
			}
			return rst;

		}

		return null;
	}

	public short[] getScanDataShort()
	{
		if (getPanelMode() == TYPE_PLAIN_PANEL)
		{
			return new short[0];
		} else if (getPanelMode() == TYPE_POINT_PROFILE)
		{
			int pos = getPositionSelectorPos();
			if (getPosAverageNum() > 1)
			{
				short[] data = new short[getDataShort()[pos].length];

				int count = 0;

				for (int i = 0; i < getPosAverageNum(); i++)
				{
					for (int j = 0; j < data.length; j++)
					{
						data[j] += getDataShort()[pos + i][j];
					}
				}

				// Get Average
				for (int i = 0; i < data.length; i++)
				{
					data[i] /= getPosAverageNum();
				}
				return data;
			} else
			{
				return getDataShort()[pos];
			}
		} else if (getPanelMode() == TYPE_MANUAL_PROFILE)
		{
			imageProfile.setDataLength(getDataShort()[0].length);
			imageProfile.updateAScan();

			float[] dat = imageProfile.getProfileData();
			short[] rst = new short[dat.length];
			for (int i = 0; i < rst.length; i++)
			{
				rst[i] = (short) dat[i];
			}
			return rst;

		}

		return null;
	}

	private void exportCurrentImage()
	{

		File f = FileSelectionField.getUserFile();
		if (f != null)
		{
			BufferedImage img = getImage().getImage();
			try
			{
				ImageIO.write(img, "png", f);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public int getPosAverageNum()
	{
		return (Integer) posAvgNum.getValue();
	}

	public void removeBackgroundFloat()
	{

		if (backgroundFloat != null)
		{
			for (int i = 0; i < dataFloat.length; i++)
			{
				for (int j = 0; j < dataFloat[i].length; j++)
				{
					dataFloat[i][j] -= backgroundFloat[j];

				}
			}
		}
	}

	public void removeBackgroundByte()
	{

		if (backgroundByte != null)
		{
			for (int i = 0; i < dataByte.length; i++)
			{
				for (int j = 0; j < dataByte[i].length; j++)
				{
					dataByte[i][j] -= backgroundByte[j];

				}
			}
		}
	}

	public void removeBackgroundDouble()
	{

		if (backgroundDouble != null)
		{
			for (int i = 0; i < dataDouble.length; i++)
			{
				for (int j = 0; j < dataDouble[i].length; j++)
				{
					dataDouble[i][j] -= backgroundDouble[j];

				}
			}
		}
	}

	public void removeBackgroundShort()
	{

		if (backgroundShort != null)
		{
			for (int i = 0; i < dataShort.length; i++)
			{
				for (int j = 0; j < dataShort[i].length; j++)
				{
					dataShort[i][j] -= backgroundShort[j];

				}
			}
		}
	}

	public void removeBackgroundInteger()
	{

		if (backgroundInteger != null)
		{
			for (int i = 0; i < dataInteger.length; i++)
			{
				for (int j = 0; j < dataInteger[i].length; j++)
				{
					dataInteger[i][j] -= backgroundInteger[j];

				}
			}
		}
	}

	public void loadBackgroundFloat()
	{
		try
		{
			File f = FileSelectionField.getUserFile();

			double[][] data = CSVFileToolkit.getDoubleData(f);

			backgroundFloat = new float[data.length];
			for (int x = 0; x < data.length; x++)
			{
				backgroundFloat[x] = (float) data[x][1];
			}

			removeBackgroundFloat();
		} catch (Exception e)
		{
			e.printStackTrace();
			backgroundFloat = null;
		}
	}

	public void loadBackgroundByte()
	{
		try
		{
			File f = FileSelectionField.getUserFile();

			double[][] data = CSVFileToolkit.getDoubleData(f);

			backgroundByte = new byte[data.length];
			for (int x = 0; x < data.length; x++)
			{
				backgroundByte[x] = (byte) data[x][1];
			}

			removeBackgroundFloat();
		} catch (Exception e)
		{
			e.printStackTrace();
			backgroundByte = null;
		}
	}

	public void loadBackgroundDouble()
	{
		try
		{
			File f = FileSelectionField.getUserFile();

			double[][] data = CSVFileToolkit.getDoubleData(f);

			backgroundDouble = new double[data.length];
			for (int x = 0; x < data.length; x++)
			{
				backgroundDouble[x] = data[x][1];
			}

			removeBackgroundDouble();
		} catch (Exception e)
		{
			e.printStackTrace();
			backgroundDouble = null;
		}
	}

	public void loadBackgroundInteger()
	{
		try
		{
			File f = FileSelectionField.getUserFile();

			int[][] data = CSVFileToolkit.getIntegerData(f);

			backgroundInteger = new int[data.length];
			for (int x = 0; x < data.length; x++)
			{
				backgroundInteger[x] = data[x][1];
			}

			removeBackgroundInteger();
		} catch (Exception e)
		{
			e.printStackTrace();
			backgroundInteger = null;
		}
	}

	public void loadBackgroundShort()
	{
		try
		{
			File f = FileSelectionField.getUserFile();

			double[][] data = CSVFileToolkit.getDoubleData(f);

			backgroundShort = new short[data.length];
			for (int x = 0; x < data.length; x++)
			{
				backgroundShort[x] = (short) data[x][1];
			}

			removeBackgroundShort();
		} catch (Exception e)
		{
			e.printStackTrace();
			backgroundShort = null;
		}
	}

	public void updateMaxMinShort()
	{
		short[] rge = DataAnalysisToolkit.getRanges(dataShort);
		dataRange[0] = rge[0];
		dataRange[1] = rge[1];
		if (isBlockUpdateRange())
		{
			return;
		}
		maxValue.setValue((double) rge[1]);
		minValue.setValue((double) rge[0]);

		// if (isUseLogScaleing())
		// {
		// if ((Double) minValue.getValue() <
		// 0.001)
		// {
		// minValue.setValue((double) (Double)
		// minValue.getValue() + 0.001);
		// }
		// maxValue.setValue((double)
		// Math.log10((Double)
		// maxValue.getValue()));
		// minValue.setValue((double)
		// Math.log10((Double)
		// minValue.getValue()));
		// }
	}

	public void updateMaxMinInteger()
	{
		int[] rge = DataAnalysisToolkit.getRangei(dataInteger);
		dataRange[0] = rge[0];
		dataRange[1] = rge[1];
		if (isBlockUpdateRange())
		{
			return;
		}
		maxValue.setValue((double) rge[1]);
		minValue.setValue((double) rge[0]);

		// if (isUseLogScaleing())
		// {
		// if ((Double) minValue.getValue() <
		// 0.001)
		// {
		// minValue.setValue((double) (Double)
		// minValue.getValue() + 0.001);
		// }
		// maxValue.setValue((double)
		// Math.log10((Double)
		// maxValue.getValue()));
		// minValue.setValue((double)
		// Math.log10((Double)
		// minValue.getValue()));
		// }
	}

	public void updateMaxMinFloat()
	{
		float[] rge = DataAnalysisToolkit.getRangef(dataFloat);
		dataRange[0] = rge[0];
		dataRange[1] = rge[1];
		if (isBlockUpdateRange())
		{
			return;
		}
		maxValue.setValue((double) rge[1]);
		minValue.setValue((double) rge[0]);

		// if (isUseLogScaleing())
		// {
		// if ((Double) minValue.getValue() <
		// 0.001)
		// {
		// minValue.setValue((double) (Double)
		// minValue.getValue() + 0.001);
		// }
		// maxValue.setValue((double)
		// Math.log10((Double)
		// maxValue.getValue()));
		// minValue.setValue((double)
		// Math.log10((Double)
		// minValue.getValue()));
		// }
	}

	public void updateMaxMinByte()
	{
		int[] rge = DataAnalysisToolkit.getRangeb(dataByte);
		dataRange[0] = rge[0];
		dataRange[1] = rge[1];
		if (isBlockUpdateRange())
		{
			return;
		}
		maxValue.setValue((double) rge[1]);
		minValue.setValue((double) rge[0]);

		// if (isUseLogScaleing())
		// {
		// if ((Double) minValue.getValue() <
		// 0.001)
		// {
		// minValue.setValue((double) (Double)
		// minValue.getValue() + 0.001);
		// }
		// maxValue.setValue((double)
		// Math.log10((Double)
		// maxValue.getValue()));
		// minValue.setValue((double)
		// Math.log10((Double)
		// minValue.getValue()));
		// }
	}

	public void updateMaxMinDouble()
	{
		double[] rge = DataAnalysisToolkit.getRanged(dataDouble);
		dataRange[0] = rge[0];
		dataRange[1] = rge[1];
		if (isBlockUpdateRange())
		{
			return;
		}
		maxValue.setValue(rge[1]);
		minValue.setValue(rge[0]);
		//
		// if (isUseLogScaleing())
		// {
		// if ((Double) minValue.getValue() <
		// 0.001)
		// {
		// minValue.setValue((double) (Double)
		// minValue.getValue() + 0.001);
		// }
		// maxValue.setValue((double)
		// Math.log10((Double)
		// maxValue.getValue()));
		// minValue.setValue((double)
		// Math.log10((Double)
		// minValue.getValue()));
		// }
	}

	/**
	 * This will return the value for a given
	 * color standard is r+g+b/3
	 * 
	 * @param c
	 * @return
	 */
	public float getValue(Color c)
	{
		return (c.getRed() + c.getBlue() + c.getGreen()) / 3;
	}

	public int getDataSizeY()
	{
		if (dataFloat != null)
		{
			return dataFloat[0].length;
		} else if (dataDouble != null)
		{
			return dataDouble[0].length;
		} else if (dataShort != null)
		{
			return dataShort[0].length;
		} else if (dataInteger != null)
		{
			return dataInteger[0].length;
		} else if (dataByte != null)
		{
			return dataByte[0].length;
		}
		return 0;
	}
	public int getDataSizeX()
	{
		if (dataFloat != null)
		{
			return dataFloat.length;
		} else if (dataDouble != null)
		{
			return dataDouble.length;
		} else if (dataShort != null)
		{
			return dataShort.length;
		} else if (dataInteger != null)
		{
			return dataInteger.length;
		} else if (dataByte != null)
		{
			return dataByte.length;
		}
		return 0;
	}
	
	public double getValue(int x, int y)
	{
		if(x < 0 || y < 0 || x >= getDataSizeX() || y >= getDataSizeY())
		{
			return Double.NaN;
		}
		
		if (dataFloat != null)
		{
			return dataFloat[x][y];
		} else if (dataDouble != null)
		{
			return dataDouble[x][y];
		} else if (dataShort != null)
		{
			return dataShort[x][y];
		} else if (dataInteger != null)
		{
			return dataInteger[x][y];
		} else if (dataByte != null)
		{
			return dataByte[x][y];
		}
		return 0;
	}

	public void setData(BufferedImage img)
	{
		if (dataFloat == null || dataFloat.length < img.getWidth()
				|| dataFloat[0].length < img.getHeight())
		{
			dataFloat = new float[img.getWidth()][img.getHeight()];
		}

		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				Color c = new Color(img.getRGB(x, y));
				dataFloat[x][y] = getValue(c);
			}
		}
		setDataFloat(dataFloat);
	}

	public void changeColorMap()
	{
		if (JOptionPane
				.showConfirmDialog(null, colorChooser, "Select new map", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
		{
			map = colorChooser.getSelectedMap();
			updateImagePanel();
		}
	}

	public double getMinSelection()
	{
		try
		{
			return (Float) minValue.getValue()
					+ minJSlider.getValue()
					/ (float) SLIDER_STEPS
					* ((Float) maxValue.getValue() - (Float) minValue
							.getValue());
		} catch (Exception e)
		{
			return (Double) minValue.getValue()
					+ minJSlider.getValue()
					/ (float) SLIDER_STEPS
					* ((Double) maxValue.getValue() - (Double) minValue
							.getValue());
		}
	}

	public double getMaxSelection()
	{
		try
		{
			return (Float) minValue.getValue()
					+ maxJSlider.getValue()
					/ (float) SLIDER_STEPS
					* ((Float) maxValue.getValue() - (Float) minValue
							.getValue());
		} catch (Exception e)
		{
			return (Double) minValue.getValue()
					+ maxJSlider.getValue()
					/ (float) SLIDER_STEPS
					* ((Double) maxValue.getValue() - (Double) minValue
							.getValue());
		}
	}

	public void updateTextfields()
	{

		// System.out.printf("UPdate Text fiels in Dyanmicrange [%g, %g]\n",
		// getMinSelection(), getMaxSelection());
		minCurrentValueField.setText(displayFormat.format(getMinSelection()));
		maxCurrentValueField.setText(displayFormat.format(getMaxSelection()));

		maxValueField.setText(displayFormat.format(getMaxValue()));
		minValueField.setText(displayFormat.format(getMinValue()));
	}

	public void updateImagePanel()
	{
		if (realTimeHist.isSelected())
		{
			if (dataFloat != null)
			{
				histogram
						.setData(dataFloat, (float) getMinSelection(), (float) getMaxSelection());
			} else if (dataDouble != null)
			{
				histogram
						.setData(dataDouble, getMinSelection(), getMaxSelection());
			} else if (dataShort != null)
			{
				histogram
						.setData(dataShort, (short) getMinSelection(), (short) getMaxSelection());
			} else if (dataInteger != null)
			{
				histogram
						.setData(dataInteger, (int) getMinSelection(), (int) getMaxSelection());
			} else if (dataByte != null)
			{
				histogram
						.setData(dataByte, (int) getMinSelection(), (int) getMaxSelection());
			}

		}

		/*
		 * Work out size of images
		 */
		int numWide = 0;
		int numHigh = 0;
		if (dataFloat != null)
		{
			numWide = dataFloat.length;
			numHigh = dataFloat[0].length;
		} else if (dataDouble != null)
		{
			numWide = dataDouble.length;
			numHigh = dataDouble[0].length;
		} else if (dataShort != null)
		{
			numWide = dataShort.length;
			numHigh = dataShort[0].length;
		} else if (dataInteger != null)
		{
			numWide = dataInteger.length;
			numHigh = dataInteger[0].length;
		} else if (dataByte != null)
		{
			numWide = dataByte.length;
			numHigh = dataByte[0].length;
		}

		/**
		 * Make sure image is correct size.
		 */
		if (image.getImage().getWidth() != numWide
				|| image.getImage().getHeight() != numHigh)
		{
			image.setImage(ImageOperations.getBi(numWide, numHigh));
		}

		double minVal = getMinSelection();
		double maxVal = getMaxSelection();

		updateTextfields();

		for (int x = 0; x < numWide; x++)
		{
			for (int y = 0; y < numHigh; y++)
			{
				float value = 0;

				if (dataFloat != null)
				{
					value = dataFloat[x][y];
				} else if (dataDouble != null)
				{
					value = (float) dataDouble[x][y];
				} else if (dataShort != null)
				{
					value = dataShort[x][y];
				} else if (dataInteger != null)
				{
					value = dataInteger[x][y];
				} else if (dataByte != null)
				{
					value = DataAnalysisToolkit.b2i(dataByte[x][y]);
				}

				if (isUseLogScaleing())
				{
					// Get DB range
					value = (float) Math
							.log10(((value - dataRange[0]) / (dataRange[1] - dataRange[0]))
									+ Double.MIN_VALUE);
				}

				value = (float) ((value - minVal) / (maxVal - minVal));

				if (useInterpolotion.isSelected())
				{
					double pos = value;
					if (pos > 1)
					{
						// System.out.println("Value : "
						// + value);
						pos = 1;
					} else if (pos < 0)
					{
						pos = 0;
					}

					Color c = map.getColorInterpolate((float) pos);

					image.getImage().setRGB(x, y, c.getRGB());

				} else
				{
					int pos = (int) (255 * value);
					if (pos > 255)
					{
						// System.out.println("Value : "
						// + value);
						pos = 255;
					} else if (pos < 0)
					{
						pos = 0;
					}

					Color c = map.getColor(pos);

					image.getImage().setRGB(x, y, c.getRGB());
				}
			}
		}

		image.repaint();
		image.updateUI();

	}

	public void setFormatString(String s)
	{
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) minValue
				.getEditor();

		editor.getFormat().applyLocalizedPattern(s);
		editor = (JSpinner.NumberEditor) maxValue.getEditor();
		editor.getFormat().applyLocalizedPattern(s);

		displayFormat.applyLocalizedPattern(s);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (!maxJSlider.getValueIsAdjusting()
				&& !minJSlider.getValueIsAdjusting())
		{
			updateImagePanel();
		}
	}

	public void setPanelType(int panelType)
	{
		this.panelType = panelType;
		removeAll();
		createJPanel();
	}

	public void setFitImage(boolean set)
	{
		fitImage.setSelectedIndex((set ? 1 : 0));
	}

	public boolean isBlockUpdateRange()
	{
		return blockUpdateRange;
	}

	public void setBlockUpdateRange(boolean blockUpdateRange)
	{
		this.blockUpdateRange = blockUpdateRange;
	}

	public void setMap(ColorMap map)
	{
		this.map = map;
		updateImagePanel();
	}

	public void setRange(float min, float max)
	{
		setMinValue(min);
		setMaxValue(max);
	}

}

class MouseLocationUpdater extends GenericImagePanelMouseLocationInterface
{
	DynamicRangeImage image;

	public MouseLocationUpdater(DynamicRangeImage owner)
	{
		this.image = owner;
		owner.getImage().setMouseLocationUpdated(this);
	}

	@Override
	public void updateMouseLocationSting(ImagePanel panel, Point p)
	{
		// TODO Auto-generated method stub
		super.updateMouseLocationSting(panel, p);
		panel.rgbValue.setText(String.valueOf(image.getValue(p.x, p.y)));
	}
}
