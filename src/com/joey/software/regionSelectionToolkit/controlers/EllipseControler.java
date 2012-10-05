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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import com.joey.software.regionSelectionToolkit.ROIPanel;


public class EllipseControler extends RectangleControler
{

	public EllipseControler(ROIPanel panel)
	{
		super(panel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Shape generateShape(Point2D start, Point2D end)
	{

		int x;
		int y;
		int wide = (int) (end.getX() - start.getX());
		int high = (int) (end.getY() - start.getY());

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
		return new Ellipse2D.Double(x, y, wide, high);
	}

}
