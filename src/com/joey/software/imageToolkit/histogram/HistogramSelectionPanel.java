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
package com.joey.software.imageToolkit.histogram;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;


public class HistogramSelectionPanel extends HistogramPanel implements
		MouseListener, MouseMotionListener, Externalizable
{
	private static final long serialVersionUID = 1L;

	int dataPoints = 20;

	// This is the data in the user points
	float value[] = new float[dataPoints];

	// this is the real Data
	float selectionData[] = new float[256];

	int pointSize = 1;

	Color crossColor = Color.RED;

	HistogramSelectionPanelListener listner = null;

	public HistogramSelectionPanel()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		for (int i = 0; i < selectionData.length; i++)
		{
			selectionData[i] = (float) i / (selectionData.length - 1);
		}
		for (int i = 0; i < value.length; i++)
		{
			value[i] = (float) i / (value.length - 1);
		}
	}

	public void setCrossColor(Color c)
	{
		crossColor = c;
	}

	public void setDataPoints(int size)
	{
		updateSelectionData();
		this.dataPoints = size;

		float[] newValue = new float[dataPoints];

		double scale = (double) (selectionData.length - 1)
				/ (newValue.length - 1);

		for (int i = 0; i < newValue.length; i++)
		{
			double pos = i * scale;
			newValue[i] = getSelectionValue((int) Math.round(pos));
		}
		value = newValue;
		repaint();

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

	public int getDataPoints()
	{
		return dataPoints;
	}

	public static void main(String[] input)
	{
		int size = 200;
		BufferedImage img = ImageOperations.getGrayTestImage(size, size, 2);
		ImageOperations.fillWithRandomColors(img);
		final HistogramSelectionPanel p = new HistogramSelectionPanel();
		p.setImage(img, HistogramPanel.TYPE_GRAY);
		JFrame f = FrameFactroy.getFrame(p);

		final JSpinner num = new JSpinner(new SpinnerNumberModel(p.dataPoints,
				1, 256, 5));
		num.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				p.setDataPoints((Integer) num.getValue());

			}
		});

		JPanel pan = new JPanel(new BorderLayout());
		pan.add(num);
		FrameFactroy.getFrame(pan).pack();

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		float[] data = new float[256];
		for (int i = 0; i < data.length; i++)
		{
			data[i] = ((data.length - 1 - i) / (float) (data.length - 1.));
			System.out.println(data[i]);
		}
		p.setValues(data);
		System.out.println("Done");
	}

	@Override
	public void paintComponent(Graphics g1)
	{
		// TODO Auto-generated method stub
		super.paintComponent(g1);

		Graphics2D g = (Graphics2D) g1;

		for (int i = 0; i < selectionData.length; i++)
		{
			double x = getWidth() / (double) (selectionData.length - 1) * i;
			double y = getHeight() * (1 - getSelectionValue(i));
			g.setColor(Color.CYAN);
			DrawTools.drawCross(g, new Point2D.Double(x, y), pointSize);
		}
		// Draw each point
		g.setColor(crossColor);
		for (int i = 0; i < value.length; i++)
		{
			double x = getWidth() / (double) (value.length - 1) * i;
			double y = getHeight() * (1 - value[i]);
			DrawTools.drawCross(g, new Point2D.Double(x, y), pointSize * 2);
		}
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
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			processMouse(e.getPoint());
		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			processMouse(e.getPoint());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK)
		{
			processMouse(e.getPoint());
		}

	}

	public void notifyHistogramChanged()
	{
		if (listner == null)
		{
			return;
		}
		listner.histogramChanged();
	}

	public void setHistogramListner(HistogramSelectionPanelListener listner)
	{
		this.listner = listner;
	}

	public void processMouse(Point p)
	{
		int x = Math.round((p.x / (float) getWidth() * (value.length - 1)));
		float y = 1 - p.y / (float) getHeight();

		if (x < 0 || x > value.length - 1)
		{
			return;
		}
		if (y > 1)
		{
			y = 1;
		}
		if (y < 0)
		{
			y = 0;
		}
		value[x] = y;
		repaint();
		notifyHistogramChanged();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		dataPoints = in.readInt();
		pointSize = in.readInt();
		crossColor = new Color(in.readInt());

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
		repaint();
	}

	public void setPanelData(HistogramSelectionPanel p)
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
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(dataPoints);
		out.writeInt(pointSize);
		out.writeInt(crossColor.getRGB());

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

	}
}
