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


import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.mathsToolkit.MathsToolkit;


public class RainbowStrokePaint
{
	Vector<Point2D> points = new Vector<Point2D>();

	Vector<Color> colors = new Vector<Color>();

	public static void main(String input[])
	{

	}

	public static void paintImage(BufferedImage image, Vector<Point2D> points, Vector<Double> posDat, ColorMap map)
	{
		Point2D.Double p = new Point2D.Double();
		for (int x = 0; x < image.getWidth(); x++)
		{
			p.x = x;
			for (int y = 0; y < image.getHeight(); y++)
			{
				p.y = y;

				double min = MathsToolkit
						.getLineSegmentDistance(points.get(0), points.get(1), p);
				double minIndex = 0;

				double val;

				for (int i = 1; i < points.size() - 1; i++)
				{
					val = MathsToolkit
							.getLineSegmentDistance(points.get(i), points
									.get(i + 1), p);
					if (val < min)
					{
						minIndex = i;
					}
				}

			}
		}

	}
}
