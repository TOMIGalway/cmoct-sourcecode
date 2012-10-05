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
package com.joey.software.geomertyToolkit;

import java.awt.Shape;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

import com.joey.software.mathsToolkit.MathsToolkit;


public class GeomertyToolkit
{
	public static GeneralPath toPath(Vector<Point2D.Double> points)
	{
		GeneralPath path = new GeneralPath();
		boolean first = true;
		for (Point2D p : points)
		{
			if (first)
			{
				first = false;
				path.moveTo(p.getX(), p.getY());
			}
			path.lineTo(p.getX(), p.getY());
		}
		if (first)
		{
			path.moveTo(0, 0);
		}
		path.closePath();

		return path;
	}

	public static double getPathLength(Path2D paht)
	{
		return getPathLength(paht, 1, 1);
	}

	public static double getPathLength(Path2D path, double scaleX, double scaleY)
	{
		PathIterator outline = new FlatteningPathIterator(path
				.getPathIterator(null), 1e-4);
		double length = 0;

		double[] pointData = new double[6];
		Point2D.Double start = new Point2D.Double();
		Point2D.Double last = new Point2D.Double();
		Point2D.Double current = new Point2D.Double();

		double resultLength = 0;
		double segLength = 0;

		boolean first = true;
		double dx = 0;
		double dy = 0;
		outline.next();
		while (!outline.isDone())
		{
			if (outline.currentSegment(pointData) != PathIterator.SEG_CLOSE)
			{
				if (first)
				{
					first = false;
					start.x = pointData[0];
					start.y = pointData[1];
					current.x = pointData[0];
					current.y = pointData[1];
				} else
				{
					last.x = current.x;
					last.y = current.y;

					current.x = pointData[0];
					current.y = pointData[1];

					dx = (last.x - current.x) * scaleX;
					dy = (last.y - current.y) * scaleY;
					segLength = Math.sqrt(dx * dx + dy * dy);

					resultLength += segLength;

				}
			}
			outline.next();
		}

		return resultLength;
	}

	/**
	 * This will attempt to calculate the area inside a shape. if the path is
	 * not closed it will automatical assume a stright line close to the end.
	 * NOTE: This code approximates all shapes into straigh edged objects!. i.e
	 * curvature is ignored.
	 * 
	 * @param path
	 * @return
	 */
	public static double getArea(Shape shape)
	{
		return getArea(shape, 1, 1);
	}

	public static double getArea(Shape shape, double scaleX, double scaleY)
	{
		PathIterator outline = shape.getPathIterator(null, 1e-5);

		double[] pointData = new double[6];
		Point2D.Double start = new Point2D.Double();
		Point2D.Double last = new Point2D.Double();
		Point2D.Double current = new Point2D.Double();
		double area = 0;

		boolean first = true;
		outline.next();
		while (!outline.isDone())
		{
			if (outline.currentSegment(pointData) != PathIterator.SEG_CLOSE)
			{
				if (first)
				{
					first = false;
					start.x = pointData[0];
					start.y = pointData[1];
					current.x = pointData[0];
					current.y = pointData[1];
				} else
				{
					last.x = current.x;
					last.y = current.y;

					current.x = pointData[0];
					current.y = pointData[1];

					area += last.x * scaleX * current.y * scaleY - current.x
							* scaleX * last.y * scaleY;
				}
			}
			outline.next();
		}
		area += current.x * scaleX * start.y * scaleY - start.x * scaleX
				* current.y * scaleY;
		return Math.abs(area / 2);
	}

	/**
	 * This will return the maximum distance that the Path is from the given
	 * point.
	 * 
	 * @param outline
	 *            - GeneralPath - Path to check max distance from.
	 * @parm point - Point2D.Float - point to calculate max distanc from.
	 * @return float - The max distance that the outline lies from the given
	 *         point
	 */
	public static float workOutMaxDistance(GeneralPath outline, Point2D.Float point)
	{
		PathIterator i = outline.getPathIterator(null);
		double[] data = new double[6];
		double distance;
		double maxDist = 0;
		i.next();
		try
		{
			while (i.currentSegment(data) != 0)
			{
				distance = point.distance((int) data[0], (int) data[1]);
				if (distance > maxDist)
				{
					maxDist = distance;
				}
				i.next();
			}
		} catch (ArrayIndexOutOfBoundsException e)
		{

		}
		return (float) maxDist + 1;
	}

	/**
	 * This will calculate the moment of interia of any polygon method got from
	 * http://www.physicsforums.com/showthread.php?s=
	 * e251fddad79b926d003e2d4154799c14&t=25293&page=2&pp=15
	 * 
	 * @param s1
	 * @param mass
	 * @return
	 */
	public static float calculateInertia(Shape s1, Point2D.Float axis, float mass)
	{
		float[] data = new float[6];
		PathIterator outline = s1.getPathIterator(null);
		Point2D.Float p0 = new Point2D.Float(), p1 = new Point2D.Float();

		float denom = 0f;
		float numer = 0f;

		float a = 0;
		float b = 0;
		boolean finished = false;
		while (!finished)
		{
			outline.currentSegment(data);
			p0.x = axis.x - data[0];
			p0.y = axis.y - data[1];
			outline.next();
			if (outline.isDone())// if last point, close path by getting
			// first point again
			{
				outline = s1.getPathIterator(null);
				finished = true;
			}
			outline.currentSegment(data);
			p1.x = axis.x - data[0];
			p1.y = axis.y - data[1];

			a = (float)(MathsToolkit.getDotProcuct(p1, p1)
					+ MathsToolkit.getDotProcuct(p1, p0) + MathsToolkit
					.getDotProcuct(p0, p0));
			b = Math.abs((p0.x * p1.y) - (p0.y * p1.x));

			denom += (a * b);
			numer += a;
		}
		return (mass / 6.0f) * (denom / numer);

	}

	/**
	 * This will test to see if there is an intersection between two shapes. the
	 * flatenss is for the Pathiterator flatness. (lower -> more accurate,
	 * longer time)
	 * 
	 * @param s1
	 * @param s2
	 * @param flatness
	 * @return
	 */
	public static boolean testIntersection(Shape s1, Shape s2, ArrayList<Point2D> points, double flatness, boolean findOne)
	{
		boolean found = false;
		if (s1.intersects(s2.getBounds2D()))
		{
			double[] data = new double[6];

			/*
			 * Lines to test intersection
			 */
			Line2D.Double l1 = new Line2D.Double();
			Line2D.Double l2 = new Line2D.Double();

			/*
			 * Paths around the shapes
			 */
			PathIterator p1;
			PathIterator p2;

			/*
			 * Point to store found points
			 */
			Point2D.Double p = new Point2D.Double();

			if (flatness > 0)
			{
				p1 = s1.getPathIterator(null, flatness);
			} else
			{
				p1 = s1.getPathIterator(null);
			}
			// Get first point
			p1.currentSegment(data);
			l1.x1 = data[0];
			l1.y1 = data[1];
			p1.next();
			while (!p1.isDone())
			{
				p1.currentSegment(data);
				l1.x2 = data[0];
				l1.y2 = data[1];

				if (flatness > 0)
				{
					p2 = s2.getPathIterator(null, flatness);
				} else
				{
					p2 = s2.getPathIterator(null);
				}
				p2.currentSegment(data);
				l2.x1 = data[0];
				l2.y1 = data[1];
				p2.next();
				while (!p2.isDone())
				{
					p2.currentSegment(data);
					l2.x2 = data[0];
					l2.y2 = data[1];
					if (testIntersection(l1, l2, p))
					{
						found = true;
						points.add((Point2D) p.clone());
						if (findOne)
						{
							return true;
						}
					}

					l2.x1 = l2.x2;
					l2.y1 = l2.y2;
					p2.next();
				}

				l1.x1 = l1.x2;
				l1.y1 = l1.y1;
				p1.next();
			}

		}
		return found;

	}

	public static double getDistanceLineSegment(Line2D l, Point2D p)
	{
		return l.ptLineDist(p);
	}

	public static double getDistanceLineSegmentManual(final double x1, final double y1, final double x2, final double y2, final double px, final double py)
	{
		if ((x2 - x1) * (px - x2) + (y2 - y1) * (py - y2) > 0)
		{
			// Point is closest to point B of line AB
			return Math.sqrt((x2 - px) * (x2 - px) + (y2 - py) * (y2 - py));
		} else if ((x1 - x2) * (px - x1) + (y1 - y2) * (py - y1) > 0)
		{
			// Point is closest to point A of line AB
			return Math.sqrt((x1 - px) * (x1 - px) + (y1 - py) * (y1 - py));
		} else
		{
			double cross = (x2 - x1) * (px - x1) - (y2 - y1) * (py - y1);
			double lineLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1)
					* (y2 - y1));
			return Math.abs(cross / lineLength);
		}
	}

	public static double getSlope(Line2D line)
	{
		double t = line.getY2() - line.getY1();
		double b = line.getX2() - line.getX1();
		return t / b;
	}

	public static boolean testIntersection(Line2D l1, Line2D l2, Point2D p)
	{

		boolean result = getPointIntersection(l1, l2, p);

		System.out.println(result);
		System.out
				.printf("L1:[%3f,%3f],[%3f,%3f]\n", l1.getX1(), l1.getY1(), l1
						.getX2(), l1.getY2());
		System.out
				.printf("L2:[%3f,%3f],[%3f,%3f]\n", l2.getX1(), l2.getY1(), l2
						.getX2(), l2.getY2());
		System.out.println("I:" + getPointIntersection(l1, l2, p));
		System.out.println("P:" + p);
		return result;
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @param p
	 * @return
	 */
	public static boolean getPointIntersection(Line2D l1, Line2D l2, Point2D p)
	{
		Double s1 = getSlope(l1);
		Double s2 = getSlope(l2);

		Double x;
		Double y;

		if (Math.abs(s1 - s2) < 1e-100)
		{
			if (getSeperation(l1, l2) < 0.1)
			{
				return false;
			} else
			{
				x = 0.;
				y = 0.;
			}
		}
		if (s1.isNaN())
		{
			if (s2.isNaN())
			{
				return false;
			}
			x = l1.getX1();
			y = s2 * x + l2.getY1();
		} else if (s2.isNaN())
		{
			if (s1.isNaN())
			{
				return false;
			}
			x = l2.getX1();
			y = s1 * x + l1.getY1();
		} else
		{
			x = ((s1 * l1.getX1() - s2 * l2.getX2() - l1.getY1() + l2.getY2()) / (s1 - s2));
			y = (s1 * (x - l1.getX1()) + l1.getY1());
		}
		p.setLocation(x, y);
		return true;

	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static double getSeperation(Line2D l1, Line2D l2)
	{
		Double s1 = getSlope(l1);
		Double s2 = getSlope(l2);

		if (Math.abs(s1 - s2) < 1e-100)
		{
			if (s1.isNaN())
			{
				return Math.abs(l2.getY1() - l1.getY1());
			}
		}

		return 0;
	}

	public static double getLength(Line2D l)
	{
		double x = l.getX2() - l.getX1();
		double y = l.getY2() - l.getY1();

		return (Math.sqrt(x * x + y * y));
	}

	public static void main(String input[])
	{
		Vector<Point2D.Double> data = new Vector<Point2D.Double>();

		data.add(new Point2D.Double(0, 0));
		data.add(new Point2D.Double(1, 1));
		Path2D path = toPath(data);
		System.out
				.printf("%g , %g\n", getPathLength(path), getPathLength(path, 1, 2));

		// Line2D l1 = new Line2D.Double(0, 0, 100, 100);
		// Line2D l2 = new Line2D.Double(0, 0, 100, 100);
		// Point2D p = new Point2D.Double();
		//
		// System.out
		// .printf("L1:[%3f,%3f],[%3f,%3f]\n", l1.getX1(), l1.getY1(), l1
		// .getX2(), l1.getY2());
		// System.out
		// .printf("L2:[%3f,%3f],[%3f,%3f]\n", l2.getX1(), l2.getY1(), l2
		// .getX2(), l2.getY2());
		// System.out.println("I:" + getPointIntersection(l1, l2, p));
		// System.out.println("P:" + p);
	}

}
