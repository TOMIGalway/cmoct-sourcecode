package com.joey.software.regionSelectionToolkit.controlers;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.geomertyToolkit.GeomertyToolkit;
import com.joey.software.mathsToolkit.MathsToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class PolygonControler extends ROIControler
{
	protected double mouseDistance = 7;

	protected Color crossStart = Color.GREEN;

	protected Color crossNormal = Color.MAGENTA;

	protected Color crossHigh = Color.CYAN;

	protected Color lineHigh = Color.BLUE;

	protected Color lineLow = Color.GREEN;

	public Vector<Point2D.Double> points = new Vector<Point2D.Double>();

	protected boolean lineNear = false;

	protected boolean pointNear = false;

	protected boolean dragging = false;

	protected boolean drawCross = true;

	protected int lineNearIndex = -1;

	protected int pointNearIndex = -1;

	boolean bindPointsInImage = false;

	protected Point2D.Double lastMouse = new Point2D.Double();

	protected int maxSize = Integer.MAX_VALUE;

	boolean drawClosePath = false;

	public PolygonControler(ROIPanel panel)
	{
		super(panel);
	}

	/**
	 * ~This will attempt to find points near to the given point and return its
	 * index in the vector that is used to store all the current points. if no
	 * point is found it will return -1;
	 * 
	 * @param point
	 * @param distance
	 * @return
	 */
	public int findNearbyPoint(Point point, double distance)
	{
		int count = 0;
		for (Point2D.Double p : points)
		{
			if (p.distance(point) < distance)
			{
				return count;
			}
			count++;
		}
		return -1;
	}

	public void updatePoints()
	{
		while (points.size() > maxSize)
		{
			points.remove(points.get(0));
		}
	}

	public void setMaxPoints(int size)
	{
		maxSize = size;

	}

	/**
	 * This will convert a point from panel space to image space
	 * 
	 * @param src
	 * @param rst
	 */
	public void transformPanelToImage(Point2D.Double src, Point2D.Double rst)
	{
		rst.setLocation(panel.panelToImageCoords(src));
	}

	public void transformImageToPanel(Point2D.Double src, Point2D.Double rst)
	{
		rst.setLocation(panel.imageToPanelCoords(src));
	}

	public double getArea()
	{
		return getArea(1, 1);
	}

	@Override
	public JPanel getControlPanel()
	{
		return new JPanel();
	}
	
	public double getArea(float scaleX, float scaleY)
	{
		try
		{
			return GeomertyToolkit
					.getArea(GeomertyToolkit.toPath(points), scaleX, scaleY);
		} catch (IllegalPathStateException e)
		{
			return 0;
		}

	}
	
	public double getLength(float scaleX, float scaleY)
	{
		try
		{
			return GeomertyToolkit.getPathLength(GeomertyToolkit.toPath(points), scaleX, scaleY);
		}
		catch(IllegalPathStateException e)
		{
			return 0;
		}
	}
	
	public double getLength()
	{
		return getLength(1,1);
	}

	/**
	 * This add points from Panel Space and converts it to image space before
	 * adding to the points
	 * 
	 * @param pos
	 * @param p
	 */
	public void setPoint(int pos, Point2D.Double p)
	{
		Point2D.Double rst = new Point2D.Double();
		transformPanelToImage(p, rst);
		points.set(pos, rst);
	}

	/**
	 * This add points from Panel Space and converts it to image space before
	 * adding to the points
	 * 
	 * @param pos
	 * @param p
	 */
	public void addPoint(int pos, Point2D.Double p)
	{
		Point2D.Double rst = new Point2D.Double();
		transformPanelToImage(p, rst);
		points.add(pos, rst);
	}

	public void setData(Vector<Point2D.Double> data)
	{
		points.removeAllElements();
		if (data != null)
		{
			points.addAll(data);
		}
	}

	/**
	 * This add points from Panel Space and converts it to image space before
	 * adding to the points
	 * 
	 * @param pos
	 * @param p
	 */
	public void addPoint(Point2D.Double p)
	{
		addPoint(points.size(), p);
	}

	public void addListner(JComponent comp)
	{
		comp.addMouseListener(this);
		comp.addMouseWheelListener(this);
		comp.addMouseMotionListener(this);
	}

	public void removeListner(JComponent comp)
	{
		comp.removeMouseListener(this);
		comp.removeMouseWheelListener(this);
		comp.removeMouseMotionListener(this);
	}

	public void updateContacts()
	{
		updatePoints();
		Point2D.Double last = null;
		int pxlCount = -1;
		int lineCount = -1;

		boolean redraw = pointNear || lineNear;

		pointNear = false;
		lineNear = false;

		pointNearIndex = -1;
		lineNearIndex = -1;

		for (Point2D.Double p1 : points)
		{
			Point2D.Double p = new Point2D.Double();
			transformImageToPanel(p1, p);
			pxlCount++;
			if (p.distance(lastMouse) < mouseDistance)
			{
				pointNear = true;
				pointNearIndex = pxlCount;
				panel.repaint();
				return;
			} else if (lineNear)
			{
				return;
			} else if (last != null)
			{
				lineCount++;
				if (MathsToolkit.getLineSegmentDistance(p, last, lastMouse) < mouseDistance)
				{
					lineNear = true;
					lineNearIndex = lineCount;
					panel.repaint();
				}
			}
			last = p;
		}

		if (redraw)
		{
			panel.repaint();
		}
	}

	private void dataChanged()
	{
		updatePoints();
		panel.shapeChanged();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		getPanel().resetPresed();
		Rectangle rect = panel.getImagePanelBounds();
		if (!bindPointsInImage || rect.contains(e.getPoint()))
		{

			lastMouse.setLocation(e.getPoint());
			updateContacts();

			if (e.getButton() == MouseEvent.BUTTON1)
			{
				if (lineNear)
				{
					addPoint(lineNearIndex + 1, (Double) lastMouse.clone());
				} else if (!pointNear)
				{
					addPoint((Double) lastMouse.clone());
				}
				dataChanged();
			} else if (e.getButton() == MouseEvent.BUTTON3)
			{
				if (pointNear)
				{
					points.remove(pointNearIndex);
				} else if (lineNear)
				{
					points.remove(lineNearIndex);
					points.remove(lineNearIndex);
				}
				dataChanged();
			}
		}
		updateContacts();
		panel.repaint();

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
		lastMouse.setLocation(e.getPoint());
		updateContacts();

		if (lineNear || pointNear)
		{
			dragging = true;

			if (pointNear)
			{
				setPoint(pointNearIndex, lastMouse);
			}
			dataChanged();
		}

		panel.repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		dragging = false;
		lastMouse.setLocation(e.getPoint());
		updateContacts();
		panel.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Rectangle rect = panel.getImagePanelBounds();
		if (dragging)
		{

			double dx = e.getPoint().x - lastMouse.getX();
			double dy = e.getPoint().y - lastMouse.getY();
			if (!bindPointsInImage || rect.contains(e.getPoint()))
			{
				if (pointNear)
				{
					Point2D.Double p1 = new Point2D.Double();
					transformImageToPanel(points.get(pointNearIndex), p1);
					p1.x += dx;
					p1.y += dy;

					if (!bindPointsInImage || rect.contains(p1))
					{
						setPoint(pointNearIndex, p1);
					}

				} else
				{
					Point2D.Double p1 = new Point2D.Double();
					Point2D.Double p2 = new Point2D.Double();

					transformImageToPanel(points.get(lineNearIndex), p1);
					transformImageToPanel(points.get(lineNearIndex + 1), p2);
					p1.x += dx;
					p1.y += dy;
					p2.x += dx;
					p2.y += dy;

					if (!bindPointsInImage
							|| (rect.contains(p1) && rect.contains(p2)))
					{
						setPoint(lineNearIndex, p1);
						setPoint(lineNearIndex + 1, p2);
					}
				}
			}
			lastMouse.setLocation(e.getPoint());
			dataChanged();
		} else
		{

			lastMouse.setLocation(e.getPoint());
			updateContacts();

		}
		panel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

		lastMouse.setLocation(e.getPoint());
		updateContacts();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g)
	{
		Stroke old = g.getStroke();

		int pxlCount = -1;
		int lineCount = -1;

		Point2D.Double last = null;

		float crossSize = (float) (mouseDistance / panel.getScale());
		float crossLine = (float) (1 / panel.getScale());
		float lineUnselSize = (float) (1 / panel.getScale());
		float lineSelSize = (float) (1 / panel.getScale());

		BasicStroke crossStroke = new BasicStroke(crossLine);
		BasicStroke lineStroke = new BasicStroke(lineUnselSize);
		BasicStroke lineUnStroke = new BasicStroke(lineUnselSize);
		Line2D.Double line = new Line2D.Double();
		boolean first = true;

		for (Point2D.Double p1 : points)
		{
			Point2D.Double p = p1;

			pxlCount++;

			if (last != null)
			{
				lineCount++;
				if (lineNearIndex == lineCount)
				{
					g.setStroke(lineStroke);
					g.setColor(lineHigh);
				} else
				{
					g.setStroke(lineUnStroke);
					g.setColor(lineLow);
				}
				line.setLine(last, p);
				g.draw(line);
			}

			Color c = (pointNearIndex == pxlCount) ? crossHigh : crossNormal;
			if (first)
			{
				c = (pointNearIndex == pxlCount) ? crossHigh : crossStart;
				first = false;
			}
			if (drawCross)
			{
				DrawTools.drawCross(g, p, crossSize, 0, c, crossStroke);
			}
			last = p;
		}

		if (isDrawClosePath())
		{
			try
			{

				DrawTools.drawLine(g, points.get(0), last, lineLow,

				new BasicStroke((lineNearIndex == lineCount) ? lineSelSize / 2
						: lineUnselSize / 2, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, new float[]
						{ 10.0f }, 0.0f));

			} catch (Exception e)
			{

			}
		}

		g.setStroke(old);
	}

	public boolean isDrawClosePath()
	{
		return drawClosePath;
	}

	public void setDrawClosePath(boolean drawClosePath)
	{
		this.drawClosePath = drawClosePath;
	}

	public boolean isBindPointsInImage()
	{
		return bindPointsInImage;
	}

	public void setBindPointsInImage(boolean bindPointsInImage)
	{
		this.bindPointsInImage = bindPointsInImage;
	}

	public static Point2D getUserPoint(BufferedImage imgA)
	{
		final ROIPanel panel = new ROIPanel(false);
		panel.setImage(imgA);

		PolygonControler pol = new PolygonControler(panel);
		pol.setMaxPoints(1);

		panel.setControler(pol);

		FrameWaitForClose wait = new FrameWaitForClose(
				FrameFactroy.getFrame(panel.getInPanel()));

		wait.waitForClose();
		return pol.points.get(0);
	}
	
	public static Point2D[] getUserPath(BufferedImage imgA)
	{
		final ROIPanel panel = new ROIPanel(false);
		panel.setImage(imgA);

		PolygonControler pol = new PolygonControler(panel);

		panel.setControler(pol);

		FrameWaitForClose wait = new FrameWaitForClose(
				FrameFactroy.getFrame(panel.getInPanel()));

		wait.waitForClose();
		return pol.points.toArray(new Point2D.Double[0]);
	}
}
