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
package com.joey.software.regionSelectionToolkit.controlers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class RectangleControler extends ROIControler
{
	Point startPoint = new Point();

	Point currentPoint = new Point();

	Color drawColor = Color.RED;

	boolean drawingActive = false;

	boolean mouseShiftPressed = false;

	public RectangleControler(ROIPanel panel)
	{
		super(panel);
	}

	public boolean isShiftPressed()
	{

		return mouseShiftPressed;
	}

	@Override
	public void draw(Graphics2D g)
	{
		if (drawingActive)
		{
			g.setColor(drawColor);
			// Scale Points by panel size
			// Point2D.Double scaleStart = new Point2D.Double();
			// Point2D.Double scaleCurrent = new Point2D.Double();
			// scaleStart.x = startPoint.x*panel.getXScale();
			// scaleStart.y = startPoint.y*panel.getYScale();
			//			
			// scaleCurrent.x = currentPoint.x*panel.getXScale();
			// scaleCurrent.y = currentPoint.y*panel.getYScale();
			// g.draw(generateShape(scaleStart, scaleCurrent));
			g.draw(generateShape(startPoint, currentPoint, isShiftPressed()));
		}
	}

	@Override
	public JPanel getControlPanel()
	{
		return new JPanel();
	}
	
	public Shape generateShape(Point2D start, Point2D end)
	{
		return this.generateShape(start, end, isShiftPressed());
	}

	public Shape generateShape(Point2D start, Point2D end, boolean square)
	{
		int x;
		int y;
		int wide = (int) (end.getX() - start.getX());
		int high = (int) (end.getY() - start.getY());

		if (square)
		{
			wide = DataAnalysisToolkit.getMin(wide, high);
			high = wide;
		}
		if (wide > 0)
		{
			x = (int) (start.getX());
		} else
		{
			x = (int) (end.getX());
			wide *= -1;
		}

		if (high > 0)
		{
			y = (int) (start.getY());
		} else
		{
			y = (int) (end.getY());
			high *= -1;
		}
		return new Rectangle(x, y, wide, high);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			startPoint = panel.panelToImageCoords(e.getPoint());
			currentPoint = panel.panelToImageCoords(e.getPoint());
			drawingActive = true;
			mouseShiftPressed = e.isShiftDown();
			panel.repaint();
		}

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		super.mouseMoved(e);
		if (drawingActive == true)
		{
			currentPoint = panel.panelToImageCoords(e.getPoint());
			mouseShiftPressed = e.isShiftDown();
			panel.repaint();
		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		if (e.getButton() == MouseEvent.BUTTON1)
		{

			drawingActive = false;
			currentPoint = panel.panelToImageCoords(e.getPoint());

			// Scale Points by panel size
			Point scaleStart = startPoint;
			Point scaleCurrent = currentPoint;

			panel
					.addRegion(generateShape(scaleStart, scaleCurrent, isShiftPressed()));
			mouseShiftPressed = false;
			panel.repaint();
		}

	}

}
