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
package com.joey.software.sliceTools;


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
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.VideoToolkit.BufferedImageStreamToAvi;
import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.ImagePanelOld;


/**
 * This simple class will handle drawing a Slice Panel. It will draw the Image
 * panel which will draw a cross and it will also draw the slider
 * 
 * @author joey.enfield
 * 
 */
public class SlicePanel extends JPanel implements ActionListener,
		Externalizable
{
	private static final long serialVersionUID = 1L;

	static final Color X_AXIS_COLOR = Color.BLUE;

	static final Color Y_AXIS_COLOR = Color.GREEN;

	static final Color Z_AXIS_COLOR = Color.RED;

	/**
	 * Fractional X position of the X Cross
	 */
	double crossX = 0.5;

	/**
	 * Fractional Y position of the Y Cross
	 */
	double crossY = 0.5;

	/**
	 * Scale of Image data
	 */
	double scale = 1;

	/**
	 * Position slider to change slice position
	 */
	JSlider pos = new JSlider();

	JButton leftButton = new JButton(new ImageIcon(ImageOperations
			.getRotatedImage(DrawTools.getMoveUPImage(15, 15), -1)));

	JButton rightButton = new JButton(new ImageIcon(ImageOperations
			.getRotatedImage(DrawTools.getMoveUPImage(15, 15), 1)));

	JButton showAsView = new JButton("Show");

	JButton saveFlyThrough = new JButton("Save");

	/**
	 * Size in pxls of the cross
	 */
	int crossSize = 5;

	/**
	 * Color of the cross to be drawn
	 */
	Color borderColor = Color.RED;

	Color xCrossColor = Color.RED;

	Color yCrossColor = Color.BLUE;

	/**
	 * OCT image data used for drawing the current slice
	 */
	NativeDataSet data;
	{
		try
		{
			data = new NativeDataSet();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The Oct slice axes
	 * 
	 * @see {@link NativeDataSet.X_SLICE}
	 */
	int sliceAxes = NativeDataSet.X_SLICE;

	/**
	 * This is an image panel that will draw a cross at a given position on the
	 * image panel
	 */

	OCTSliceViewer owner;

	ImagePanelOld imgPanel = new ImagePanelOld(
			ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL)
	{
		@Override
		public void paintComponent(java.awt.Graphics g)
		{
			if (getImage().getWidth() != getWidth()
					|| getImage().getHeight() != getHeight())
			{
				setImage(ImageOperations.getBi(getWidth(), getHeight()));
				ImageOperations.setImage(getBackground(), getImage());
			}

			/*
			 * Set the image data
			 */
			scale = data
					.getPreviewScaledSlice(sliceAxes, getPosition(), imgPanel
							.getImage());

			ImageOperations.addColorBorder(getImage(), 1, borderColor);
			/*
			 * Draw images
			 */
			super.paintComponent(g);

			/*
			 * Draw the cross of the image
			 */

			Dimension size = data.getPreviewSliceSize(sliceAxes);
			// work out cross position
			Point2D.Double p = new Point2D.Double(size.getWidth() * scale
					* crossX, size.getHeight() * scale * crossY);
			DrawTools
					.drawCross((Graphics2D) g, p, crossSize, 0, xCrossColor, yCrossColor);

		}
	};

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public void getPaneltoData(Point panelSpace, Point imgSpace)
	{
		imgSpace.x = (int) (panelSpace.x / scale);
		imgSpace.y = (int) (panelSpace.y / scale);
	}

	public SlicePanel(OCTSliceViewer owner)
	{
		this(50, 50, owner);
	}

	/**
	 * This will create a new SlicePanel with a given width and height of the
	 * images
	 * 
	 * @param wide
	 * @param high
	 */
	public SlicePanel(int wide, int high, OCTSliceViewer owner)
	{
		this.owner = owner;
		imgPanel.setImage(ImageOperations.getBi(wide, high));
		createJPanel();
	}

	public void setPosition(double val)
	{
		int p = (int) (pos.getMaximum() * val);
		if (p >= pos.getMaximum())
		{
			p -= 1;
			if (p < 1)
			{
				p = 1;
			}
		}
		pos.setValue(p);
		// System.out.println("Val : "+pos.getValue());
		// System.out.println("Max : "+pos.getMaximum());
	}

	public int getPosition()
	{
		return pos.getValue();
	}

	public void createJPanel()
	{
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.add(leftButton);
		buttonPanel.add(rightButton);

		JPanel buttonControlPanel = new JPanel(new GridLayout(1, 2));
		buttonControlPanel.add(showAsView);
		buttonControlPanel.add(saveFlyThrough);

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(pos, BorderLayout.CENTER);
		controlPanel.add(buttonPanel, BorderLayout.EAST);
		controlPanel.add(buttonControlPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(imgPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		rightButton.addActionListener(this);
		leftButton.addActionListener(this);
		showAsView.addActionListener(this);
		saveFlyThrough.addActionListener(this);
	}

	public void saveFlyThrough()
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					// Create Panel to get user Inputs
					JPanel inputPanel = new JPanel();
					FileSelectionField output = new FileSelectionField();
					JSpinner frameRate = new JSpinner(new SpinnerNumberModel(
							10, 1, 100, 1));
					JCheckBox fullResolution = new JCheckBox(
							"Use Full Resolution");

					JLabel rateLabel = new JLabel("Frame Rate : ");
					rateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
					JPanel frameRatePanel = new JPanel(new BorderLayout());
					frameRatePanel.add(rateLabel, BorderLayout.WEST);
					frameRatePanel.add(frameRate, BorderLayout.CENTER);

					rateLabel.setPreferredSize(new Dimension(80, -1));
					output.setLabelSize(80);

					JPanel mainPanel = new JPanel(new GridLayout(2, 1));
					mainPanel.add(output);
					mainPanel.add(frameRatePanel);

					inputPanel.setLayout(new BorderLayout());
					inputPanel.add(mainPanel);

					// show confirmation panel

					if (JOptionPane
							.showConfirmDialog(null, inputPanel, "Enter output file", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
					{
						StatusBarPanel status = new StatusBarPanel();
						int count = data.getPreviewSizeData(sliceAxes);
						status.setMaximum(count);

						JFrame f = new JFrame();
						f.getContentPane().setLayout(new BorderLayout());
						f.getContentPane().add(status, BorderLayout.CENTER);
						f.setSize(300, 80);
						f.setVisible(true);

						Dimension d = data.getPreviewSliceSize(sliceAxes);

						int fRate = (Integer) frameRate.getValue();

						status.setStatusMessage("Preparing Output File");
						BufferedImageStreamToAvi videoOut = new BufferedImageStreamToAvi(
								d.width, d.height, fRate, output.getFile()
										.getParent()
										+ "\\", output.getFile().getName(),
								true, false);

						BufferedImage outputImage = ImageOperations
								.getBi(videoOut.getXDim(), videoOut.getYDim());
						for (int i = 0; i < count; i++)
						{
							status.setStatusMessage("Loading : " + i + " of "
									+ (count - 1));
							try
							{
								data
										.getPreviewScaledSlice(sliceAxes, i, outputImage);
								videoOut.pushImage(outputImage);
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}
						status.setStatusMessage("Finalising Video");
						videoOut.finaliseVideo();
						status.setStatusMessage("Complete");
						f.setVisible(false);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		t.start();

	}

	public void setOCTData(NativeDataSet data, int axes)
	{
		this.data = data;
		this.sliceAxes = axes;

		int max = 0;

		if (axes == NativeDataSet.X_SLICE)
		{
			max = data.getSizeDataX();
			borderColor = X_AXIS_COLOR;
			yCrossColor = Z_AXIS_COLOR;
			xCrossColor = Y_AXIS_COLOR;
		} else if (axes == NativeDataSet.Y_SLICE)
		{
			max = data.getSizeDataY();
			borderColor = Y_AXIS_COLOR;
			yCrossColor = X_AXIS_COLOR;
			xCrossColor = Z_AXIS_COLOR;
		} else if (axes == NativeDataSet.Z_SLICE)
		{
			max = data.getSizeDataZ();
			borderColor = Z_AXIS_COLOR;
			yCrossColor = X_AXIS_COLOR;
			xCrossColor = Y_AXIS_COLOR;
		}

		max -= 1;
		if (max < 1)
		{
			max = 1;
		}

		pos.setMaximum(max);
		validateTree();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == rightButton)
		{
			pos.setValue(pos.getValue() + 1);
		} else if (e.getSource() == leftButton)
		{
			pos.setValue(pos.getValue() - 1);
		} else if (e.getSource() == showAsView)
		{
			owner.setPreviewAxes(sliceAxes);
			owner.setPreviewPos(pos.getValue());
			owner.updatePreviewPanel(owner.renderHighRes.isSelected());
		} else if (e.getSource() == saveFlyThrough)
		{

			saveFlyThrough();
		}

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		crossX = in.readDouble();
		crossY = in.readDouble();
		scale = in.readDouble();

		pos.setMaximum(in.readInt());
		pos.setValue(in.readInt());

		crossSize = in.readInt();
		xCrossColor = new Color(in.readInt());
		yCrossColor = new Color(in.readInt());
		borderColor = new Color(in.readInt());

		sliceAxes = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeDouble(crossX);
		out.writeDouble(crossY);
		out.writeDouble(scale);

		out.writeInt(pos.getMaximum());
		out.writeInt(pos.getValue());

		out.writeInt(crossSize);
		out.writeInt(xCrossColor.getRGB());
		out.writeInt(yCrossColor.getRGB());
		out.writeInt(borderColor.getRGB());
		out.writeInt(sliceAxes);
	}

	public double getCrossX()
	{
		return crossX;
	}

	public void setCrossX(double crossX)
	{
		this.crossX = crossX;
	}

	public double getCrossY()
	{
		return crossY;
	}

	public void setCrossY(double crossY)
	{
		this.crossY = crossY;
	}

	public JSlider getPos()
	{
		return pos;
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
	}

	public Color getXCrossColor()
	{
		return xCrossColor;
	}

	public void setXCrossColor(Color crossColor)
	{
		xCrossColor = crossColor;
	}

	public Color getYCrossColor()
	{
		return yCrossColor;
	}

	public void setYCrossColor(Color crossColor)
	{
		yCrossColor = crossColor;
	}

}
