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
package com.joey.software.volumeTools;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class SliceControler implements MouseMotionListener, MouseListener,
		MouseWheelListener
{
	SliceSelectPanel panel;

	SliceLinker link;

	Point lastDragPoint;

	public SliceControler(SliceSelectPanel panel)
	{
		this.panel = panel;
		panel.imgPanel.addMouseListener(this);
		panel.imgPanel.addMouseMotionListener(this);
		panel.imgPanel.addMouseWheelListener(this);
	}

	public void setLinker(SliceLinker link)
	{
		this.link = link;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{

		Point pImg = new Point();
		panel.getPaneltoData(e.getPoint(), pImg);

		Dimension d = panel.getDataImageSize();

		double x = pImg.getX() / d.getWidth();
		double y = pImg.getY() / d.getHeight();

		if (x <= 1 && x >= 0 && y <= 1 && y >= 0)
		{
			if (e.getModifiersEx() == (InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
			{
				panel.yMinValue = y;
				notifyChange();
			} else if (e.getModifiersEx() == (InputEvent.BUTTON3_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK))
			{
				panel.yMaxValue = y;
				notifyChange();
			} else if (e.getModifiersEx() == (InputEvent.BUTTON1_DOWN_MASK))
			{
				panel.xMinValue = x;
				notifyChange();
			} else if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK)
			{
				panel.xMaxValue = x;
				notifyChange();
			} else if (e.getModifiersEx() == InputEvent.BUTTON2_DOWN_MASK)
			{
				panel.crossX = x;
				panel.crossY = y;
				notifyChange();
			} else if ((e.getModifiersEx() == (InputEvent.BUTTON2_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)))
			{
				double dx = (panel.xMaxValue - panel.xMinValue) / 2;
				double dy = (panel.yMaxValue - panel.yMinValue) / 2;

				double xMinValue = x - dx;
				double xMaxValue = x + dx;

				double yMinValue = y - dy;
				double yMaxValue = y + dy;

				// Make sure bounds are ok.
				if ((xMinValue >= 0) && (xMaxValue <= 1))
				{
					panel.xMaxValue = xMaxValue;
					panel.xMinValue = xMinValue;
				}

				if ((yMinValue >= 0) && (yMaxValue <= 1))
				{
					panel.yMinValue = yMinValue;
					panel.yMaxValue = yMaxValue;
				}
				notifyChange();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void notifyChange()
	{
		link.valueChanged(this);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Point pImg = new Point();
		panel.getPaneltoData(e.getPoint(), pImg);

		Dimension d = panel.getDataImageSize();

		double x = pImg.getX() / d.getWidth();
		double y = pImg.getY() / d.getHeight();

		if (x <= 1 && x >= 0 && y <= 1 && x >= 0)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK)
				{
					panel.yMinValue = y;
					notifyChange();
				} else
				{
					panel.xMinValue = x;
					notifyChange();
				}
			} else if (e.getButton() == MouseEvent.BUTTON3)
			{
				if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK)
				{
					panel.yMaxValue = y;
					notifyChange();
				} else
				{
					panel.xMaxValue = x;
					notifyChange();
				}
			}
			if (e.getButton() == MouseEvent.BUTTON2)
			{
				panel.crossX = x;
				panel.crossY = y;
				notifyChange();
			}
		}

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
	public void mouseWheelMoved(MouseWheelEvent e)
	{

		if ((e.getModifiersEx() == (InputEvent.SHIFT_DOWN_MASK)))
		{
			double dx = 0.001;
			double dy = 0.001;
			if (e.getWheelRotation() < 0)
			{
				dx *= -1;
				dy *= -1;
			}

			double xMinValue = panel.xMinValue + dx;
			double xMaxValue = panel.xMaxValue - dx;

			double yMinValue = panel.yMinValue + dy;
			double yMaxValue = panel.yMaxValue - dy;

			// Make sure bounds are ok.
			if ((xMinValue >= 0) && (xMaxValue <= 1))
			{
				panel.xMaxValue = xMaxValue;
				panel.xMinValue = xMinValue;
			}

			if ((yMinValue >= 0) && (yMaxValue <= 1))
			{
				panel.yMinValue = yMinValue;
				panel.yMaxValue = yMaxValue;
			}
			notifyChange();
		}

	}

}
