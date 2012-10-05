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
package com.joey.software.DataToolkit;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class DrgRawImageProducer extends ImageProducer
{
	public static final int LOAD_AXIS_X = 0;

	public static final int LOAD_AXIS_Y = 1;

	public static final int LOAD_AXIS_Z = 2;

	RandomAccessFile file = null;

	VolumeHeaderData head = null;

	int loadAxis = LOAD_AXIS_Z;

	public DrgRawImageProducer(File f) throws FileNotFoundException,
			IOException
	{
		this(new RandomAccessFile(f, "r"));
	}

	public DrgRawImageProducer(RandomAccessFile f) throws IOException
	{
		file = f;
		head = new VolumeHeaderData(file);
	}

	/**
	 * This will set the axis that will be loaded.
	 * 
	 * @param axis
	 */
	public void setLoadAxis(int axis)
	{
		loadAxis = axis;
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		return getImage(pos, loadAxis);
	}

	public BufferedImage getImage(int pos, int loadAxis) throws IOException
	{
		BufferedImage img = null;

		if (loadAxis == LOAD_AXIS_Z)
		{
			img = ImageOperations.getBi(ImageSliceToolkit

			.getZSliceSize(head));
			ImageSliceToolkit.getZSlice(file, pos, img, new StatusBarPanel());
			// img = ImageOperations.getRotatedImage(img, +1);
		} else if (loadAxis == LOAD_AXIS_X)
		{
			img = ImageOperations.getBi(ImageSliceToolkit

			.getXSliceSize(head));
			ImageSliceToolkit.getXSlice(file, pos, img, new StatusBarPanel());
			// img = ImageOperations.getRotatedImage(img, +1);
		} else if (loadAxis == LOAD_AXIS_Y)
		{
			img = ImageOperations.getBi(ImageSliceToolkit.getYSliceSize(head));
			ImageSliceToolkit.getYSlice(file, pos, img, new StatusBarPanel());
			// img = ImageOperations.getRotatedImage(img, +1);
		}
		return img;
	}

	public void getUserChoceAxis()
	{
		JRadioButton xOption = new JRadioButton("X - Axis :",
				loadAxis == LOAD_AXIS_X);
		JRadioButton yOption = new JRadioButton("Y - Axis :",
				loadAxis == LOAD_AXIS_Y);
		JRadioButton zOption = new JRadioButton("Z - Axis :",
				loadAxis == LOAD_AXIS_Z);

		ButtonGroup group = new ButtonGroup();
		group.add(xOption);
		group.add(yOption);
		group.add(zOption);

		final JSpinner xPos = new JSpinner(new SpinnerNumberModel(0, 0,
				getImageCount(LOAD_AXIS_X), 1));
		final JSpinner yPos = new JSpinner(new SpinnerNumberModel(0, 0,
				getImageCount(LOAD_AXIS_Y), 1));
		final JSpinner zPos = new JSpinner(new SpinnerNumberModel(0, 0,
				getImageCount(LOAD_AXIS_Z), 1));

		JPanel panel = new JPanel(new GridLayout(1, 3));
		final ImagePanel xAxis = new ImagePanel(
				ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		final ImagePanel yAxis = new ImagePanel(
				ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		final ImagePanel zAxis = new ImagePanel(
				ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

		JPanel xAxisPanel = new JPanel(new BorderLayout());
		JPanel yAxisPanel = new JPanel(new BorderLayout());
		JPanel zAxisPanel = new JPanel(new BorderLayout());

		xAxisPanel.add(xAxis, BorderLayout.CENTER);
		yAxisPanel.add(yAxis, BorderLayout.CENTER);
		zAxisPanel.add(zAxis, BorderLayout.CENTER);

		xAxisPanel.add(xOption, BorderLayout.SOUTH);
		yAxisPanel.add(yOption, BorderLayout.SOUTH);
		zAxisPanel.add(zOption, BorderLayout.SOUTH);

		xAxisPanel.add(xPos, BorderLayout.NORTH);
		yAxisPanel.add(yPos, BorderLayout.NORTH);
		zAxisPanel.add(zPos, BorderLayout.NORTH);
		xAxisPanel.setBorder(BorderFactory.createTitledBorder("X - Axis"));
		yAxisPanel.setBorder(BorderFactory.createTitledBorder("Y - Axis"));
		zAxisPanel.setBorder(BorderFactory.createTitledBorder("Z - Axis"));

		panel.setPreferredSize(new Dimension(600, 400));
		panel.add(xAxisPanel);
		panel.add(yAxisPanel);
		panel.add(zAxisPanel);

		/*
		 * Create the listners
		 */
		xPos.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				try
				{
					int pos = (Integer) xPos.getValue();
					xAxis.setImage(getImage(pos, LOAD_AXIS_X));
				} catch (Exception e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error loading image:\n"
									+ e1);
				}
			}
		});

		yPos.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				try
				{
					int pos = (Integer) yPos.getValue();
					yAxis.setImage(getImage(pos, LOAD_AXIS_Y));
				} catch (Exception e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error loading image:\n"
									+ e1);
				}
			}
		});

		zPos.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				try
				{
					int pos = (Integer) zPos.getValue();
					zAxis.setImage(getImage(pos, LOAD_AXIS_Z));
				} catch (Exception e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error loading image:\n"
									+ e1);
				}
			}
		});

		try
		{
			xAxis.setImage(getImage(0, LOAD_AXIS_X));
			yAxis.setImage(getImage(0, LOAD_AXIS_Y));
			zAxis.setImage(getImage(0, LOAD_AXIS_Z));
		} catch (IOException e1)
		{
			JOptionPane.showMessageDialog(null, "Error loading image:\n" + e1);
			e1.printStackTrace();
		}
		if (JOptionPane
				.showConfirmDialog(null, panel, "Select Prefered Axis", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
		{
			if (xOption.isSelected())
			{
				loadAxis = LOAD_AXIS_X;
			} else if (yOption.isSelected())
			{
				loadAxis = LOAD_AXIS_Y;
			} else if (zOption.isSelected())
			{
				loadAxis = LOAD_AXIS_Z;
			}
		}
	}

	public static void main(String input[]) throws FileNotFoundException,
			IOException
	{
		File f = new File(
				"C:\\Oct Data\\Micro Needles in Fore Arm\\Initial\\raw.dat");
		DrgRawImageProducer imgProd = new DrgRawImageProducer(f);
		imgProd.getUserChoceAxis();
	}

	@Override
	public int getImageCount()
	{
		return getImageCount(loadAxis);
	}

	public int getImageCount(int loadAxis)
	{
		if (file == null)
		{
			return 0;
		} else
		{
			if (loadAxis == LOAD_AXIS_Z)
			{
				return head.getSizeDataZ();
			} else if (loadAxis == LOAD_AXIS_X)
			{
				return head.getSizeDataX();
			} else if (loadAxis == LOAD_AXIS_Y)
			{
				return head.getSizeDataY();
			}
			return head.getSizeDataZ();
		}
	}

}
