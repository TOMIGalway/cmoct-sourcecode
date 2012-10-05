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
package com.joey.software.dft;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class GraphPlot extends Canvas
{
	// JDK 1.02 version

	public static final int SIGNAL = 1;

	public static final int SPECTRUM = 2;

	Color plotColor = Color.yellow;

	Color axisColor = Color.black;

	Color gridColor = Color.black;

	Color bgColor = Color.blue;

	int plotStyle = SIGNAL;

	boolean tracePlot = true;

	boolean logScale = false;

	int vertSpace = 20;

	int horzSpace = 20;

	int vertIntervals = 8;

	int horzIntervals = 10;

	int nPoints = 0;

	float xmax = 0.0f;

	float ymax = 0.0f;

	float xScale, yScale;

	private float[] plotValues;

	public GraphPlot()
	{
	}

	public void setPlotColor(Color c)
	{
		if (c != null)
			plotColor = c;
	}

	public Color getPlotColor()
	{
		return plotColor;
	}

	public void setAxisColor(Color c)
	{
		if (c != null)
			axisColor = c;
	}

	public Color getAxisColor()
	{
		return axisColor;
	}

	public void setGridColor(Color c)
	{
		if (c != null)
			gridColor = c;
	}

	public Color getGridColor()
	{
		return gridColor;
	}

	public void setBgColor(Color c)
	{
		if (c != null)
			bgColor = c;
	}

	public Color getBgColor()
	{
		return bgColor;
	}

	public void setPlotStyle(int pst)
	{
		plotStyle = pst;
	}

	public int getPlotStyle()
	{
		return plotStyle;
	}

	public void setTracePlot(boolean b)
	{
		tracePlot = b;
	}

	public boolean isTracePlot()
	{
		return tracePlot;
	}

	public void setLogScale(boolean b)
	{
		logScale = b;
	}

	public boolean isLogScale()
	{
		return logScale;
	}

	public void setVertSpace(int v)
	{
		vertSpace = v;
	}

	public int getVertSpace()
	{
		return vertSpace;
	}

	public void setHorzSpace(int h)
	{
		horzSpace = h;
	}

	public int getHorzSpace()
	{
		return horzSpace;
	}

	public int getVertIntervals()
	{
		return vertIntervals;
	}

	public void setVertIntervals(int i)
	{
		vertIntervals = i;
	}

	public int getHorzIntervals()
	{
		return horzIntervals;
	}

	public void setHorzIntervals(int i)
	{
		horzIntervals = i;
	}

	public void setYmax(float m)
	{
		ymax = m;
	}

	public float getYmax()
	{
		return ymax;
	}

	public void setPlotValues(float[] values)
	{
		nPoints = values.length;
		plotValues = new float[nPoints];
		plotValues = values;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{

		int x, y;
		int top = vertSpace;
		int bottom = size().height - vertSpace;
		int left = horzSpace;
		int right = size().width - horzSpace;
		int width = right - left;
		int fullHeight = bottom - top;
		int centre = (top + bottom) / 2;
		int xAxisPos = centre;
		int yHeight = fullHeight / 2;
		if (plotStyle == SPECTRUM)
		{
			xAxisPos = bottom;
			yHeight = fullHeight;
		}
		this.setBackground(bgColor);
		if (logScale)
		{
			xAxisPos = top;
			g.setColor(gridColor);
			// vertical grid lines
			for (int i = 0; i <= vertIntervals; i++)
			{
				x = left + i * width / vertIntervals;
				g.drawLine(x, top, x, bottom);
			}
			// horizontal grid lines
			for (int i = 0; i <= horzIntervals; i++)
			{
				y = top + i * fullHeight / horzIntervals;
				g.drawLine(left, y, right, y);
			}
		}
		g.setColor(axisColor);
		g.drawLine(left, top, left, bottom); // vertical axis
		g.drawLine(left, xAxisPos, right, xAxisPos); // horizontal axis

		if (nPoints != 0)
		{
			g.setColor(plotColor);
			// horizontal scale factor:
			xScale = width / (float) (nPoints - 1);
			// vertical scale factor:
			yScale = yHeight / ymax;
			int[] xCoords = new int[nPoints];
			int[] yCoords = new int[nPoints];
			for (int i = 0; i < nPoints; i++)
			{
				xCoords[i] = left + Math.round(i * xScale);
				yCoords[i] = xAxisPos - Math.round(plotValues[i] * yScale);
			}
			if (tracePlot)
				for (int i = 0; i < nPoints - 1; i++)
					g
							.drawLine(xCoords[i], yCoords[i], xCoords[i + 1], yCoords[i + 1]);
			else
			{
				for (int i = 0; i < nPoints; i++)
					g.drawLine(xCoords[i], xAxisPos, xCoords[i], yCoords[i]);
			}
		}
	}
}
