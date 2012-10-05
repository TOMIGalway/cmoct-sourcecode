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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;


public class HistogramPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3566239829724858329L;

	public static final int TYPE_GRAY = 0;

	public static final int TYPE_RED = 1;

	public static final int TYPE_BLUE = 2;

	public static final int TYPE_GREEN = 3;

	public static final int TYPE_MAX_VALUE = 1;

	int[] data;

	/**
	 * This flag can be used to set the Max height as the average point
	 */
	boolean scaleHeight = true;

	int maxValue = 0;

	int type = TYPE_MAX_VALUE;

	Color backColor = Color.WHITE;

	Color rectColor = Color.BLACK;

	Color textColor = Color.CYAN;

	public HistogramPanel(int binCount)
	{
		data = new int[binCount];
	}

	public HistogramPanel()
	{
		this(256);
	}

	public void setRectangleColor(Color rectColor)
	{
		this.rectColor = rectColor;
	}

	public void setBackgroundColor(Color backColor)
	{
		this.backColor = backColor;
	}

	public Color getBackColor()
	{
		return backColor;
	}

	public Color getRectColor()
	{
		return rectColor;
	}

	public static void getHistogramData(float[][][] data, float min, float max, int out[])
	{

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					float pos = (data[x][y][z] - min) / (max - min);
					if (!(pos < 0 || pos > 1))
					{
						int val = (int) (pos * (out.length - 1));
						out[val] += 1;
					}
				}
			}
		}
	}

	public static void getHistogramData(float[][] data, float min, float max, int out[])
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				float pos = (data[x][y] - min) / (max - min);
				if (!(pos < 0 || pos > 1))
				{
					int val = (int) (pos * (out.length - 1));
					out[val] += 1;
				}
			}
		}
	}

	public static void getHistogramData(double[][] data, double min, double max, int out[])
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				double pos = (data[x][y] - min) / (max - min);
				if (!(pos < 0 || pos > 1))
				{
					int val = (int) (pos * (out.length - 1));
					out[val] += 1;
				}
			}
		}
	}

	public static void getHistogramData(int[][] data, int min, int max, int out[])
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				float pos = (data[x][y] - min) / (max - min);
				if (!(pos < 0 || pos > 1))
				{
					int val = (int) (pos * (out.length - 1));
					out[val] += 1;
				}
			}
		}
	}

	public static void getHistogramData(short[][] data, short min, short max, int out[])
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				float pos = (data[x][y] - min) / (max - min);
				if (!(pos < 0 || pos > 1))
				{
					int val = (int) (pos * (out.length - 1));
					out[val] += 1;
				}
			}
		}
	}

	public static void getHistogramData(byte[][] data, int min, int max, int out[])
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				float pos = (DataAnalysisToolkit.b2i(data[x][y]) - min) / (max - min);
				if (!(pos < 0 || pos > 1))
				{
					int val = (int) (pos * (out.length - 1));
					out[val] += 1;
				}
			}
		}
	}
	public static void getHistogramData(BufferedImage img, int[] result, int type)
	{
		int colorVal = 0;
		int pxlVal = 0;
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				colorVal = img.getRGB(x, y);
				if (type == TYPE_GRAY)
				{
					int red = (colorVal >>> 16) & 0xFF;
					int green = (colorVal >>> 8) & 0xFF;
					int blue = colorVal & 0xFF;
					pxlVal = (red + green + blue) / 3;
				} else if (type == TYPE_RED)
				{
					pxlVal = (colorVal >>> 16) & 0xFF;
				} else if (type == TYPE_GREEN)
				{
					pxlVal = (colorVal >>> 8) & 0xFF;
				} else if (type == TYPE_BLUE)
				{
					pxlVal = (colorVal & 0xFF);
				}

				result[pxlVal] += 1;
			}
		}
	}

	public void setData(float[][] array, float min, float max)
	{
		clearData();
		getHistogramData(array, min, max, data);
		workoutMaxValue();
		repaint();
	}

	public void setData(double[][] array, double min, double max)
	{
		clearData();
		getHistogramData(array, min, max, data);
		workoutMaxValue();
		repaint();
	}

	public void setData(short[][] array, short min, short max)
	{
		clearData();
		getHistogramData(array, min, max, data);
		workoutMaxValue();
		repaint();
	}
	
	public void setData(byte[][] array,int min, int max)
	{
		clearData();
		getHistogramData(array, min, max, data);
		workoutMaxValue();
		repaint();
	}

	public void setData(int[][] array, int min, int max)
	{
		clearData();
		getHistogramData(array, min, max, data);
		workoutMaxValue();
		repaint();
	}

	public void setImage(BufferedImage img, int type)
	{
		clearData();
		getHistogramData(img, data, type);
		workoutMaxValue();
		repaint();
	}

	public void setArrayData(byte[][][] volume, StatusBarPanel status)
	{
		clearData();
		getHistogramData(volume, data, status);
		workoutMaxValue();
		repaint();
	}

	public void workoutMaxValue()
	{
		switch (type)
		{
			case TYPE_MAX_VALUE:
				maxValue = getMaxVal(data);
				return;
		}

	}

	@Override
	protected void paintComponent(Graphics g1)
	{

		float binCount = data.length;
		// TODO Auto-generated method stub
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;

		Rectangle2D.Double rect = new Rectangle2D.Double();

		g.setColor(textColor);
		g.drawString("Max:" + maxValue, 10, 10);
		rect.height = getHeight();
		rect.width = (getWidth() / binCount);

		for (int i = 0; i < data.length; i++)
		{
			rect.y = getHeight() * (1 - data[i] / (double) maxValue);
			rect.x = (getWidth() / binCount) * i;

			g.setColor(rectColor);
			g.fill(rect);
		}

	}

	public void clearData()
	{
		for (int i = 0; i < data.length; i++)
		{
			data[i] = 0;
		}
	}

	public static void getHistogramData(byte[][][] data, int[] result, StatusBarPanel status)
	{

		for (int i = 0; i < result.length; i++)
		{
			result[i] = 0;
		}
		if (status != null)
		{
			status.setMaximum(data.length - 1);
		}
		for (int x = 0; x < data.length; x++)
		{
			if (status != null)
			{
				status.setValue(x);
			}
			for (int y = 0; y < data[0].length; y++)
			{
				for (int z = 0; z < data[0][0].length; z++)
				{
					int value = data[x][y][z];
					if (value < 0)
					{
						value = 256 + value;
					}
					result[value] += 1;
				}
			}
		}
	}

	public static int getMaxVal(int[] data)
	{
		int max = data[0];
		for (int i : data)
		{
			if (i > max)
			{
				max = i;
			}
		}
		return max;
	}

	public static void main(String input[]) throws IOException
	{
		int size = 800;
		BufferedImage img = ImageOperations.getBi(size, size);
		ImageOperations.fillWithRandomColors(img);
		JPanel m = new JPanel(new GridLayout(4, 1));

		ImagePanel imgPanel = new ImagePanel(img);
		imgPanel.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		imgPanel.setBorder(BorderFactory.createEtchedBorder());

		for (int i = 0; i < 4; i++)
		{
			HistogramPanel p = new HistogramPanel();
			p.setImage(img, i);
			p.setBorder(BorderFactory.createTitledBorder(""));

			m.add(p);
		}
		JFrame f = FrameFactroy.getFrame(m);
		FrameFactroy.getFrame(img);
		f.setSize(800, 600);
		f.setVisible(true);
	}

	public int[] getData()
	{
		return data;
	}

	public void setData(int[] data)
	{
		this.data = data;
		maxValue = getMaxVal(data);
		repaint();
	}

	
}
