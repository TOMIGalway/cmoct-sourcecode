package com.joey.software.mathsToolkit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.vecmath.Point3f;

public class MathsToolkit
{

	public static void getCenter(Shape s, Point2D.Float result)
			throws Exception
	{
		result.x = (float) s.getBounds().getCenterX();
		result.y = (float) s.getBounds().getCenterY();

	}

	public static Point2D.Double getLinePointIntersection(Point2D line1, Point2D line2, Point2D p)
	{
		Point2D.Double result = new Point2D.Double();

		double cx = p.getX();
		double cy = p.getY();

		double ax = line1.getX();
		double ay = line1.getY();
		double bx = line2.getX();
		double by = line2.getY();
		double distanceSegment = 0, distanceLine = 0;

		double r_numerator = (cx - ax) * (bx - ax) + (cy - ay) * (by - ay);
		double r_denomenator = (bx - ax) * (bx - ax) + (by - ay) * (by - ay);
		double r = r_numerator / r_denomenator;
		//
		result.x = ax + r * (bx - ax);
		result.y = ay + r * (by - ay);

		return result;
	}

	/**
	 * 
	 * find the distance from the point (cx,cy) to the line determined by the
	 * points (ax,ay) and (bx,by)
	 * 
	 * distanceSegment = distance from the point to the line segment
	 * distanceLine = distance from the point to the line (assuming infinite
	 * extent in both directions
	 * 
	 * 
	 * 
	 * 
	 * Subject 1.02: How do I find the distance from a point to a line?
	 * 
	 * 
	 * Let the point be C (Cx,Cy) and the line be AB (Ax,Ay) to (Bx,By). Let P
	 * be the point of perpendicular projection of C on AB. The parameter r,
	 * which indicates P's position along AB, is computed by the dot product of
	 * AC and AB divided by the square of the length of AB:
	 * 
	 * (1) AC dot AB r = --------- ||AB||^2
	 * 
	 * r has the following meaning:
	 * 
	 * r=0 P = A r=1 P = B r<0 P is on the backward extension of AB r>1 P is on
	 * the forward extension of AB 0<r<1 P is interior to AB
	 * 
	 * The length of a line segment in d dimensions, AB is computed by:
	 * 
	 * L = sqrt( (Bx-Ax)^2 + (By-Ay)^2 + ... + (Bd-Ad)^2)
	 * 
	 * so in 2D:
	 * 
	 * L = sqrt( (Bx-Ax)^2 + (By-Ay)^2 )
	 * 
	 * and the dot product of two vectors in d dimensions, U dot V is computed:
	 * 
	 * D = (Ux * Vx) + (Uy * Vy) + ... + (Ud * Vd)
	 * 
	 * so in 2D:
	 * 
	 * D = (Ux * Vx) + (Uy * Vy)
	 * 
	 * So (1) expands to:
	 * 
	 * (Cx-Ax)(Bx-Ax) + (Cy-Ay)(By-Ay) r = ------------------------------- L^2
	 * 
	 * The point P can then be found:
	 * 
	 * Px = Ax + r(Bx-Ax) Py = Ay + r(By-Ay)
	 * 
	 * And the distance from A to P = r*L.
	 * 
	 * Use another parameter s to indicate the location along PC, with the
	 * following meaning: s<0 C is left of AB s>0 C is right of AB s=0 C is on
	 * AB
	 * 
	 * Compute s as follows:
	 * 
	 * (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay) s = ----------------------------- L^2
	 * 
	 * 
	 * Then the distance from C to P = |s|*L.
	 * 
	 */
	public static double getLineSegmentDistance(Point2D line1, Point2D line2, Point2D p)
	{
		double cx = p.getX();
		double cy = p.getY();

		double ax = line1.getX();
		double ay = line1.getY();
		double bx = line2.getX();
		double by = line2.getY();
		double distanceSegment = 0, distanceLine = 0;

		double r_numerator = (cx - ax) * (bx - ax) + (cy - ay) * (by - ay);
		double r_denomenator = (bx - ax) * (bx - ax) + (by - ay) * (by - ay);
		double r = r_numerator / r_denomenator;
		//
		double px = ax + r * (bx - ax);
		double py = ay + r * (by - ay);
		//
		double s = ((ay - cy) * (bx - ax) - (ax - cx) * (by - ay))
				/ r_denomenator;

		distanceLine = Math.abs(s) * Math.sqrt(r_denomenator);

		//
		// (xx,yy) is the point on the lineSegment closest to (cx,cy)
		//
		double xx = px;
		double yy = py;

		if ((r >= 0) && (r <= 1))
		{
			distanceSegment = distanceLine;
		} else
		{

			double dist1 = (cx - ax) * (cx - ax) + (cy - ay) * (cy - ay);
			double dist2 = (cx - bx) * (cx - bx) + (cy - by) * (cy - by);
			if (dist1 < dist2)
			{
				xx = ax;
				yy = ay;
				distanceSegment = Math.sqrt(dist1);

			} else
			{
				xx = bx;
				yy = by;
				distanceSegment = Math.sqrt(dist2);
			}

		}

		return distanceSegment;
	}

	/**
	 * This will get the distance between the line represented by l1 and l2 and
	 * the point p, it assumes that it is an infinite line
	 * 
	 * @param l1
	 * @param l2
	 * @param p
	 */
	public static double getLineDistance(Point2D l1, Point2D l2, Point2D p)
	{
		double A = p.getX() - l1.getX();
		double B = p.getY() - l1.getY();
		double C = l2.getX() - l1.getX();
		double D = l2.getY() - l1.getY();
		return Math.abs(A * D - C * B) / Math.sqrt(C * C + D * D);
	}

	/**
	 * this will return a point representing the lenght of the line
	 */
	public static void getSize(Line2D line, Point2D p)
	{
		p.setLocation(line.getX2() - line.getX1(), line.getY2() - line.getY1());
	}

	public static Point2D.Float getSize(Line2D line)
	{
		Point2D.Float p = new Point2D.Float();
		getSize(line, p);
		return p;
	}

	/**
	 * @param lineA
	 * @param lineB
	 * @param p
	 * @return
	 */
	public static boolean testInterset(Line2D lineA, Line2D lineB, Point2D p)
	{
		if (lineA.intersectsLine(lineB))
		{
			double mA = getSlope(lineA);
			double mB = getSlope(lineB);

			double cA = getYIntercept(lineA);
			double cB = getYIntercept(lineB);

			double x = (cB - cA) / (mA - mB);
			double y = mA * x + cA;

			double b1 = -1;
			double b2 = -1;

			// Compute the inverse of the determinate ..
			// first makes sure that it does not go to inf

			double det_inv = (mA * b2 - mB * b1);
			if (Math.abs(det_inv) < 0.0001)
			{
				return false;
			}
			det_inv = 1 / det_inv;

			// use Kramers rule to compute xi and yi

			x = ((b1 * cB - b2 * cA) * det_inv);
			y = ((mB * cA - mA * cB) * det_inv);

			p.setLocation(x, y);
			return true;
		} else
		{
			return false;
		}

	}

	/**
	 * This will get the postion on a line using a line segment i.e p = p1 +
	 * r(p2-p1)
	 * 
	 * @param p1
	 * @param p2
	 * @param r
	 * @return
	 */
	public static Point2D.Double getLinePoint(Point2D p1, Point2D p2, double r)
	{
		Point2D.Double result = new Point2D.Double();

		result.x = p1.getX() + r * (p2.getX() - p1.getX());
		result.y = p1.getY() + r * (p2.getY() - p1.getY());
		return result;
	}

	/**
	 * This will return the slope of the given line.
	 * 
	 * @param line
	 *            Line2D to get slope of
	 * @return the slope of the line
	 */
	public static double getSlope(Line2D line)
	{
		double y = (line.getY2() - line.getY1());
		double x = (line.getX2() - line.getX1());

		if (Math.abs(x) < 0.0001)
		{
			return 1e+10;
		}
		return y / x;
	}

	/**
	 * This will get the y intercept i.e y = mx+c .. c
	 * 
	 * @param line
	 *            line2D to get C of
	 * @return c
	 */
	public static double getYIntercept(Line2D line)
	{
		return (line.getY1() - getSlope(line) * line.getX1());
	}

	public static double PARELLEL_TOLERANCE = 0.1;

	public static boolean checkParallel(Line2D line1, Line2D line2)
	{
		double slope1 = getSlope(line1), slope2 = getSlope(line2);
		if (Math.abs(slope1 - slope2) < PARELLEL_TOLERANCE)
		{
			return true;
		} else
		{
			return false;
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		Line2D.Double line1 = new Line2D.Double(0, 0, 100, 105), line2 = new Line2D.Double(
				90, 90, -1, -1);
		JFrame f = new JFrame("test");
		f.setSize(100, 100);
		f.setIgnoreRepaint(true);
		f.setVisible(true);
		int i = 1000;
		System.out.print("\nParallel Lines [" + checkParallel(line1, line2)
				+ "]");
		while (i-- > 0)
		{
			Graphics2D g2 = (Graphics2D) f.getGraphics();
			g2.setColor(Color.BLACK);
			g2.draw(line1);
			g2.draw(line2);
			Thread.sleep(100);
		}

	}

	/**
	 * Returns a Point that has a size of the difference between the two points
	 * 
	 * @param p1
	 * @param p2
	 * @param result
	 *            The difference between the two points
	 */
	public static void getDifference(Point2D.Float p1, Point2D.Float p2, Point2D.Float result)
	{
		result.setLocation(p2.getX() - p1.getX(), p2.getY() - p1.getY());
	}

	public static float sqr(float num)
	{
		return num * num;
	}

	/**
	 * returns the difference between the two positns
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Point2D.Float getDifference(Point2D.Float p1, Point2D.Float p2)
	{
		Point2D.Float p = new Point2D.Float();
		getDifference(p1, p2, p);
		return p;
	}

	public static Point2D.Float pointToPoint2D(Point p)
	{
		Point2D.Float point = new Point2D.Float();
		point.setLocation(p);
		return point;
	}

	public static double getDotProcuct(Point2D v1, Point2D v2)
	{
		return v1.getX() * v2.getX() + v1.getY() * v2.getY();
	}

	public static double getLength(Point2D v)
	{
		return v.distance(0, 0);
	}

	/**
	 * This will rotate the object by 90 degrees .ie p(x,y) -> p(-y,x);
	 * 
	 * @param p
	 */
	public static void makePerpendicular(Point2D p)
	{
		p.setLocation(-p.getY(), p.getX());
	}

	public static void getPerpendicular(Point2D pS, Point2D pD)
	{
		pD.setLocation(-pS.getY(), pS.getX());
	}

	/**
	 * Will add the two points
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Point2D.Float add(Point2D.Float p1, Point2D.Float p2)
	{
		return new Point2D.Float(p1.x + p2.x, p1.y + p2.y);
	}

	/**
	 * This will add the two vectors p1 and p2 into result
	 * 
	 * @param p1
	 * @param p2
	 * @param result
	 */
	public static void add(Point2D.Float p1, Point2D.Float p2, Point2D.Float result)
	{
		result.x = p1.x + p2.x;
		result.y = p1.y + p1.y;
	}

	public static void setLength(Point2D pSource, float length, Point2D pDest)
	{
		pDest.setLocation(pSource);
		normalise(pDest);
		scale(pDest, length);

	}

	/**
	 * This will return the vector perpinduclar to p
	 * 
	 * @param p
	 * @return
	 */
	public static Point2D.Float getPerpenducular(Point2D.Float p)
	{
		Point2D.Float pnew = (Point2D.Float) p.clone();
		makePerpendicular(pnew);
		return pnew;
	}

	/**
	 * This will return the vector perpinduclar to p
	 * 
	 * @param p
	 * @return
	 */
	public static Point2D.Double getPerpenducular(Point2D.Double p, Point2D.Double around)
	{
		Point2D.Double pnew = (Point2D.Double) p.clone();
		pnew.x -= around.x;
		pnew.y -= around.y;
		makePerpendicular(pnew);
		pnew.x += around.x;
		pnew.y += around.y;
		return pnew;
	}

	/**
	 * This will make the point have a length of 1
	 * 
	 * @param p
	 */
	public static void normalise(Point2D p)
	{
		double lenght = Math.abs(p.distance(0, 0));
		p.setLocation(p.getX() / lenght, p.getY() / lenght);
	}

	public static Point2D getNormalise(Point2D p)
	{
		Point2D result = (Point2D) p.clone();
		normalise(result);
		return result;
	}

	/**
	 * This will scale the point p by num p(x,y) -> p(x*num, y*num)
	 */
	public static void scale(Point2D p, double num)
	{
		p.setLocation(p.getX() * num, p.getY() * num);
	}

	/**
	 * will returned a new point that is the valu of p multiplied by num
	 * 
	 * @param p
	 * @param num
	 * @return
	 */

	public static Point2D.Float scale(Point2D.Float p, float num)
	{
		return new Point2D.Float(p.x * num, p.y * num);
	}

	public static Point2D.Double scale(Point2D.Double p, double num)
	{
		return new Point2D.Double(p.x * num, p.y * num);
	}

	public static void getCrossProduct(Point3f result, Point3f a, Point3f b)
	{
		result.x = a.y*b.z-b.y*a.z;
		result.y = a.z*b.x-b.z*a.x;
		result.z = a.x*b.y-b.x*a.y;
	}

	public static float getDotProcuct(Point3f a, Point3f b)
	{
		return (a.x*b.x)+(a.y*b.y)+(a.z*b.z);
	}

}
