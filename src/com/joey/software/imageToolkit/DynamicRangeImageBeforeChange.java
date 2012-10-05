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
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.imageToolkit.colorMapping.ColorMapChooser;
import com.joey.software.imageToolkit.histogram.HistogramPanel;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class DynamicRangeImageBeforeChange extends JPanel implements
		ChangeListener
{
	static final int SLIDER_STEPS = 10000;

	float maxValue = 0;

	float minValue = 0;

	float[][] data;

	JSlider maxJSlider = new JSlider(0, SLIDER_STEPS, SLIDER_STEPS);

	JSlider minJSlider = new JSlider(0, SLIDER_STEPS, 0);

	public JSlider getMaxJSlider()
	{
		return maxJSlider;
	}

	public JSlider getMinJSlider()
	{
		return minJSlider;
	}

	ROIPanel image = new ROIPanel(false);

	JScrollPane scroll = new JScrollPane(image);

	JButton saveData = new JButton("Save Image");

	JButton changeColorMap = new JButton("Change Map");

	JButton showHistogram = new JButton("Show Hist");

	JButton updateRange = new JButton("Update Range");

	JPanel mainPanel = new JPanel(new BorderLayout());

	JPanel imageHolderPanel = new JPanel(new BorderLayout());

	ColorMap map = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	ColorMapChooser colorChooser = new ColorMapChooser();

	JCheckBox useInterpolotion = new JCheckBox("Color Interpolation", false);

	JCheckBox realTimeHist = new JCheckBox("Update Histogram", false);

	JCheckBox fitImage = new JCheckBox("Fit Image", false);

	JCheckBox useLogScaleing = new JCheckBox("Log Image", false);

	HistogramPanel histogram = new HistogramPanel(1000);

	JFrame histogramFrame = new JFrame("Histogram Panel");

	JSlider positionSelector = new JSlider(0, 2000, 0);

	JSpinner posAvgNum = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

	boolean usePositionSlider = false;

	public boolean isUseLogScaleing()
	{
		return useLogScaleing.isSelected();
	}

	public void setUseLogScaleing(boolean useLogScaleing)
	{
		this.useLogScaleing.setSelected(useLogScaleing);
	}

	public DynamicRangeImageBeforeChange()
	{
		setData(new float[1][1]);

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
		updateImagePanel();
	}

	public boolean isUsePositionSlider()
	{
		return usePositionSlider;
	}

	public void setUsePositionSlider(boolean usePositionSlider)
	{
		this.usePositionSlider = usePositionSlider;

		mainPanel.removeAll();
		mainPanel.setLayout(new BorderLayout());

		if (usePositionSlider)
		{

			JPanel southPanel = new JPanel(new BorderLayout());
			southPanel.add(positionSelector, BorderLayout.CENTER);
			southPanel.add(posAvgNum, BorderLayout.EAST);

			mainPanel.add(southPanel, BorderLayout.SOUTH);
			mainPanel.add(imageHolderPanel, BorderLayout.CENTER);
		} else
		{
			mainPanel.add(imageHolderPanel, BorderLayout.CENTER);
		}
	}

	public JSlider getPositionSelector()
	{
		return positionSelector;
	}

	/**
	 * this will return a int stating the posiion of the slider in the current
	 * image;
	 * 
	 * @return
	 */
	public int getPositionSelectorPos()
	{
		return (int) ((image.getWidth() - 1)
				* (float) (positionSelector.getValue() - positionSelector
						.getMinimum()) / (positionSelector.getMaximum() - positionSelector
				.getMinimum()));
	}

	/**
	 * this will draw a line for the position selector on the current image.
	 * 
	 * @param c
	 */
	public void drawPositionSlider(Color c)
	{
		if (!usePositionSlider)
		{
			return;
		}

		int pos = getPositionSelectorPos();
		Graphics2D g = image.getImage().createGraphics();
		g.setColor(c);
		g.drawLine(pos, 0, pos, image.getHeight());
	}

	public ImagePanel getImage()
	{
		return image;
	}

	public void showHistogram()
	{
		histogram.setData(data, getMinSelection(), getMaxSelection());
		histogramFrame.setVisible(true);
	}

	public DynamicRangeImageBeforeChange(float[][] data)
	{
		this();
		setData(data);
	}

	public DynamicRangeImageBeforeChange(BufferedImage img)
	{
		this();
		setData(img);
	}

	public float[][] getData()
	{
		return data;
	}

	public float getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(float maxValue)
	{
		this.maxValue = maxValue;
	}

	public float getMinValue()
	{
		return minValue;
	}

	public void setMinValue(float minValue)
	{
		this.minValue = minValue;
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		maxJSlider.setOrientation(SwingConstants.VERTICAL);
		minJSlider.setOrientation(SwingConstants.VERTICAL);

		JPanel buttonPanel = new JPanel(new GridLayout(5, 1));
		buttonPanel.add(saveData);
		buttonPanel.add(changeColorMap);
		buttonPanel.add(showHistogram);
		buttonPanel.add(updateRange);
		buttonPanel.add(useLogScaleing);

		JPanel minPanel = new JPanel(new BorderLayout());
		minPanel.add(minJSlider);
		minPanel.setBorder(BorderFactory.createTitledBorder("MIN"));

		JPanel maxPanel = new JPanel(new BorderLayout());
		maxPanel.add(maxJSlider);
		maxPanel.setBorder(BorderFactory.createTitledBorder("MAX"));

		JPanel rangePanel = new JPanel(new GridLayout(1, 2));
		rangePanel.add(minPanel);
		rangePanel.add(maxPanel);

		JPanel checksPanel = new JPanel(new GridLayout(3, 1));
		checksPanel.add(useInterpolotion);
		checksPanel.add(realTimeHist);
		checksPanel.add(fitImage);

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(rangePanel, BorderLayout.CENTER);
		controlPanel.add(buttonPanel, BorderLayout.SOUTH);
		controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
		controlPanel.add(checksPanel, BorderLayout.NORTH);

		imageHolderPanel.setLayout(new BorderLayout());
		imageHolderPanel.add(scroll, BorderLayout.CENTER);

		mainPanel.add(imageHolderPanel, BorderLayout.CENTER);

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(mainPanel);

		add(controlPanel, BorderLayout.WEST);
		add(holder, BorderLayout.CENTER);

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

		updateRange.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateMaxMin();
				updateImagePanel();
				System.out.println(maxValue);
				System.out.println(minValue);
			}
		});

		useLogScaleing.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
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

		fitImage.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (fitImage.isSelected())
				{
					imageHolderPanel.remove(scroll);
					scroll.remove(image);

					imageHolderPanel.add(image, BorderLayout.CENTER);
					imageHolderPanel.updateUI();

					image.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

				} else
				{
					imageHolderPanel.remove(image);
					image.setPanelType(ImagePanel.TYPE_NORMAL);
					scroll.setViewportView(image);
					imageHolderPanel.add(scroll, BorderLayout.CENTER);
				}

			}
		});

		positionSelector.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (usePositionSlider)
				{
					updateImagePanel();
				}
			}
		});

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

	public void setData(float[][] data)
	{
		this.data = data;
		maxValue = data[0][0];
		minValue = data[0][0];

		updateMaxMin();

		updateImagePanel();
	}

	public void updateMaxMin()
	{
		maxValue = data[0][0];
		minValue = data[0][0];
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{

				if (maxValue < data[x][y])
				{
					maxValue = data[x][y];
				}

				if (minValue > data[x][y])
				{
					minValue = data[x][y];
				}
			}

		}

		if (isUseLogScaleing())
		{
			if (minValue < 0.001)
			{
				minValue += 0.001;
			}
			maxValue = (float) Math.log(maxValue);
			minValue = (float) Math.log(minValue);
		}
	}

	/**
	 * This will return the value for a given color standard is r+g+b/3
	 * 
	 * @param c
	 * @return
	 */
	public float getValue(Color c)
	{
		return (c.getRed() + c.getBlue() + c.getGreen()) / 3;
	}

	public void setData(BufferedImage img)
	{
		float[][] data = new float[img.getWidth()][img.getHeight()];

		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				Color c = new Color(img.getRGB(x, y));
				data[x][y] = getValue(c);
			}
		}
		setData(data);
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

	public float getMinSelection()
	{
		return minValue + minJSlider.getValue() / (float) SLIDER_STEPS
				* (maxValue - minValue);
	}

	public float getMaxSelection()
	{
		return minValue + maxJSlider.getValue() / (float) SLIDER_STEPS
				* (maxValue - minValue);
	}

	public void updateImagePanel()
	{
		if (realTimeHist.isSelected())
		{
			histogram.setData(data, getMinSelection(), getMaxSelection());
		}

		if (image.getImage().getWidth() < data.length
				|| image.getImage().getHeight() < data[0].length)
		{
			image.setImage(ImageOperations.getBi(data.length, data[0].length));
		}

		float minVal = getMinSelection();
		float maxVal = getMaxSelection();

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				float value = data[x][y];

				if (isUseLogScaleing())
				{
					value = (float) Math.log(value);
				}
				if (useInterpolotion.isSelected())
				{
					float pos = (((value) - minVal) / (maxVal - minVal));
					if (pos > 1)
					{
						// System.out.println("Value : " + value);
						pos = 1;
					} else if (pos < 0)
					{
						pos = 0;
					}

					Color c = map.getColorInterpolate(pos);

					image.getImage().setRGB(x, y, c.getRGB());

				} else
				{
					int pos = (int) (255 * ((value) - minVal) / (maxVal - minVal));
					if (pos > 255)
					{
						// System.out.println("Value : " + value);
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
		drawPositionSlider(Color.cyan);
		image.repaint();
		image.updateUI();

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

}
