package com.joey.software.volumeTools;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.drawingToolkit.DrawTools;
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
public class SliceSelectPanel extends JPanel implements ActionListener,
		Externalizable
{
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

	SliceControler controler;

	/**
	 * Position slider to change slice position
	 */
	JSlider pos = new JSlider();

	JButton leftButton = new JButton(new ImageIcon(ImageOperations
			.getRotatedImage(DrawTools.getMoveUPImage(15, 15), -1)));

	JButton rightButton = new JButton(new ImageIcon(ImageOperations
			.getRotatedImage(DrawTools.getMoveUPImage(15, 15), 1)));

	boolean sliderListen = true;

	/**
	 * Size in pxls of the cross
	 */
	int crossSize = 5;

	/**
	 * Color of the cross to be drawn
	 */
	Color borderColor = Color.RED;

	double xMinValue = 0;

	double xMaxValue = 1;

	double yMinValue = 0;

	double yMaxValue = 1;

	Color yMinColor = Color.RED;

	Color yMaxColor = Color.RED.brighter();

	Color xMinColor = Color.GREEN;

	Color xMaxColor = Color.GREEN.brighter();

	Color xCrossColor = Color.RED;

	Color yCrossColor = Color.BLUE;

	float xMinSize = 1;

	float xMaxSize = 1;

	float yMinSize = 1;

	float yMaxSize = 1;

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

	ImagePanelOld imgPanel = new ImagePanelOld(
			ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL)
	{
		@Override
		public void paintComponent(Graphics g1)
		{
			Graphics2D g = (Graphics2D) g1;
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
			super.paintComponent(g1);

			Dimension size = getDataImageSize();
			// int wide = (int) (getWidth()* scale);
			// int high = (int) (getHeight()* scale);

			int wide = (int) (size.getWidth() * scale);
			int high = (int) (size.getHeight() * scale);

			Point2D.Double p = new Point2D.Double(size.getWidth() * scale
					* crossX, size.getHeight() * scale * crossY);
			DrawTools.drawCross(g, p, crossSize, 0, xCrossColor, yCrossColor);

			g.setColor(xMinColor);
			g.setStroke(new BasicStroke(xMinSize));
			g
					.drawLine((int) (xMinValue * wide), 0, (int) (xMinValue * wide), high);

			g.setColor(xMaxColor);
			g.setStroke(new BasicStroke(xMaxSize));
			g
					.drawLine((int) (xMaxValue * wide), 0, (int) (xMaxValue * wide), high);

			g.setColor(yMinColor);
			g.setStroke(new BasicStroke(yMinSize));
			g
					.drawLine(0, (int) (yMinValue * high), wide, (int) (yMinValue * high));

			g.setColor(yMaxColor);
			g.setStroke(new BasicStroke(yMaxSize));
			g
					.drawLine(0, (int) (yMaxValue * high), wide, (int) (yMaxValue * high));
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

	public Dimension getDataImageSize()
	{
		return data.getPreviewSliceSize(sliceAxes);
	}

	public SliceSelectPanel()
	{
		this(50, 50);
	}

	/**
	 * This will create a new SlicePanel with a given width and height of the
	 * images
	 * 
	 * @param wide
	 * @param high
	 */
	public SliceSelectPanel(int wide, int high)
	{
		imgPanel.setImage(ImageOperations.getBi(wide, high));
		createJPanel();
		controler = new SliceControler(this);
	}

	public void setPosition(double val)
	{
		pos.setValue((int) (pos.getMaximum() * val));
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

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(pos, BorderLayout.CENTER);
		controlPanel.add(buttonPanel, BorderLayout.EAST);

		setLayout(new BorderLayout());
		add(imgPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

		rightButton.addActionListener(this);
		leftButton.addActionListener(this);
	}

	public void setOCTData(NativeDataSet data, int axes)
	{
		imgPanel.setImage(ImageOperations.getBi(1));
		this.data = data;
		this.sliceAxes = axes;

		int max = 0;

		if (axes == NativeDataSet.X_SLICE)
		{
			max = data.getSizeDataX() - 1;
			borderColor = X_AXIS_COLOR;
			yMinColor = Y_AXIS_COLOR;
			yMaxColor = Y_AXIS_COLOR.brighter();
			xMinColor = Z_AXIS_COLOR;
			xMaxColor = Z_AXIS_COLOR.brighter();

			yCrossColor = Z_AXIS_COLOR;
			xCrossColor = Y_AXIS_COLOR;

		} else if (axes == NativeDataSet.Y_SLICE)
		{
			max = data.getSizeDataY() - 1;
			borderColor = Y_AXIS_COLOR;
			yMinColor = Z_AXIS_COLOR;
			yMaxColor = Z_AXIS_COLOR.brighter();

			xMinColor = X_AXIS_COLOR;
			xMaxColor = X_AXIS_COLOR.brighter();

			yCrossColor = X_AXIS_COLOR;
			xCrossColor = Z_AXIS_COLOR;
		} else if (axes == NativeDataSet.Z_SLICE)
		{
			max = data.getSizeDataZ() - 1;
			borderColor = Z_AXIS_COLOR;
			yMinColor = Y_AXIS_COLOR;
			yMaxColor = Y_AXIS_COLOR.brighter();
			xMinColor = X_AXIS_COLOR;
			xMaxColor = X_AXIS_COLOR.brighter();

			yCrossColor = X_AXIS_COLOR;
			xCrossColor = Y_AXIS_COLOR;
		}

		
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
		}

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		// crossX = in.readDouble();
		// crossY = in.readDouble();
		// scale = in.readDouble();
		//
		// pos.setMaximum(in.readInt());
		// pos.setValue(in.readInt());
		//
		// crossSize = in.readInt();
		// xCrossColor = new Color(in.readInt());
		// yCrossColor = new Color(in.readInt());
		// borderColor = new Color(in.readInt());
		//
		// sliceAxes = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// out.writeDouble(crossX);
		// out.writeDouble(crossY);
		// out.writeDouble(scale);
		//
		// out.writeInt(pos.getMaximum());
		// out.writeInt(pos.getValue());
		//
		// out.writeInt(crossSize);
		// out.writeInt(xCrossColor.getRGB());
		// out.writeInt(yCrossColor.getRGB());
		// out.writeInt(borderColor.getRGB());
		// out.writeInt(sliceAxes);
	}

	public boolean isSliderListen()
	{
		return sliderListen;
	}

	public void setSliderListen(boolean sliderListen)
	{
		this.sliderListen = sliderListen;
	}
}
