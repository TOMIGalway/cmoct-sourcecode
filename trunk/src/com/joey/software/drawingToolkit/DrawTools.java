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
package com.joey.software.drawingToolkit;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;

public class DrawTools
{
	static Line2D.Double x1 = new Line2D.Double();

	static Line2D.Double x2 = new Line2D.Double();

	static Line2D.Double y1 = new Line2D.Double();

	static Line2D.Double y2 = new Line2D.Double();

	public static void drawCross(Graphics2D g, Point2D p, float size)
	{
		drawCross(g, p, size, 0);
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap)
	{
		drawCross(g, p, size, gap, g.getColor());
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap, Color color)
	{
		drawCross(g, p, size, gap, color, 1);
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap, Color color, float lineSize)
	{
		drawCross(g, p, size, gap, color, new BasicStroke(lineSize));
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap, Color color, BasicStroke stroke)
	{
		drawCross(g, p, size, gap, color, color, stroke);
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap, Color xColor, Color yColor, float lineSize)
	{
		drawCross(g, p, size, gap, xColor, yColor, new BasicStroke(lineSize));
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap, Color xColor, Color yColor)
	{
		drawCross(g, p, size, gap, xColor, yColor, 1);
	}

	public static void drawCross(Graphics2D g, Point2D p, float size, float gap, Color xColor, Color yColor, BasicStroke stroke)
	{
		//
		g.setStroke(stroke);

		x1.x1 = (p.getX() + gap);
		x1.y1 = (p.getY());
		x1.x2 = (p.getX() + gap + size);
		x1.y2 = (p.getY());

		x2.x1 = (p.getX() - gap);
		x2.y1 = (p.getY());
		x2.x2 = (p.getX() - gap - size);
		x2.y2 = (p.getY());

		y1.y1 = (p.getY() + gap);
		y1.x1 = (p.getX());
		y1.y2 = (p.getY() + gap + size);
		y1.x2 = (p.getX());

		y2.y1 = (p.getY() - gap);
		y2.x1 = (p.getX());
		y2.y2 = (p.getY() - gap - size);
		y2.x2 = (p.getX());

		g.setColor(xColor);
		g.draw(x1);
		g.draw(x2);
		g.setColor(yColor);
		g.draw(y1);
		g.draw(y2);

	}

	public static void drawLine(Graphics2D g, Point2D p1, Point2D p2)
	{

		drawLine(g, p1, p2, g.getColor());
	}

	public static void drawLine(Graphics2D g, Point2D p1, Point2D p2, Color c)
	{
		drawLine(g, p1, p2, c, 1f);
	}

	public static void drawLine(Graphics2D g, Point2D p1, Point2D p2, Color c, float lineSize)
	{
		drawLine(g, p1, p2, c, new BasicStroke(lineSize));
	}

	public static void drawLine(Graphics2D g, Point2D p1, Point2D p2, Color c, BasicStroke stroke)
	{
		g.setColor(c);
		g.setStroke(stroke);
		Line2D.Double line = new Line2D.Double(p1, p2);
		g.draw(line);
	}

	public static BufferedImage getMoveDownImage(int imageWidth, int imageHeight)
	{
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		GeneralPath path = new GeneralPath();
		path.moveTo(0.3 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.5 * imageWidth, imageHeight);
		path.lineTo(0.9 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.1 * imageHeight);
		g.setColor(Color.black);
		g.fill(path);
		return image;
	}

	public static BufferedImage getMoveUPImage(int imageWidth, int imageHeight)
	{
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		GeneralPath path = new GeneralPath();
		path.moveTo(0.3 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.5 * imageWidth, 0);
		path.lineTo(0.9 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.9 * imageHeight);
		g.setColor(Color.black);
		g.fill(path);
		return image;
	}

	public static BufferedImage getMoveLeftImage(int imageWidth, int imageHeight)
	{
		BufferedImage img = getMoveUPImage(imageHeight, imageWidth);
		return ImageOperations.getRotatedImage(img, -1);
	}

	public static BufferedImage getMoveRightImage(int imageWidth, int imageHeight)
	{
		BufferedImage img = getMoveUPImage(imageHeight, imageWidth);
		return ImageOperations.getRotatedImage(img, 1);
	}

	public static BufferedImage getDeleteImage(int imageWidth, int imageHeight)
	{
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		GeneralPath path = new GeneralPath();
		path.moveTo(0.1 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.5 * imageWidth, 0.3 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.9 * imageWidth, 0.1 * imageHeight);
		//
		path.lineTo(0.9 * imageWidth, 0.3 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.5 * imageHeight);
		//
		path.lineTo(0.9 * imageWidth, 0.7 * imageHeight);
		path.lineTo(0.9 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.7 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.5 * imageWidth, 0.7 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.9 * imageHeight);
		//
		path.lineTo(0.1 * imageWidth, 0.7 * imageHeight);
		path.lineTo(0.3 * imageWidth, 0.5 * imageHeight);
		//
		path.lineTo(0.1 * imageWidth, 0.3 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.1 * imageHeight);

		g.setColor(Color.black);
		g.fill(path);
		return image;
	}

	public static BufferedImage getAddImage(int imageWidth, int imageHeight)
	{
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		GeneralPath path = new GeneralPath();
		path.moveTo(0.4 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.6 * imageWidth, 0.1 * imageHeight);
		path.lineTo(0.6 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.9 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.9 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.6 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.6 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.4 * imageWidth, 0.9 * imageHeight);
		path.lineTo(0.4 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.4 * imageWidth, 0.4 * imageHeight);

		g.setColor(Color.black);
		g.fill(path);
		return image;
	}

	public static BufferedImage getRemoveImage(int imageWidth, int imageHeight)
	{
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		GeneralPath path = new GeneralPath();
		path.moveTo(0.9 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.9 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.6 * imageHeight);
		path.lineTo(0.1 * imageWidth, 0.4 * imageHeight);
		path.lineTo(0.4 * imageWidth, 0.4 * imageHeight);

		g.setColor(Color.black);
		g.fill(path);
		return image;
	}

	public static GeneralPath toGeneralPath(ArrayList<Point> points, boolean close)
	{
		GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
		boolean first = true;
		for (Point p : points)
		{
			if (first)
			{
				path.moveTo(p.x, p.y);
				first = false;
			} else
			{
				path.lineTo(p.x, p.y);
			}
		}

		if (close)
		{
			path.closePath();
		}

		return path;
	}

	public static GeneralPath toGeneralPath(Vector<Point2D.Double> points, boolean close)
	{
		GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
		boolean first = true;
		for (Point2D.Double p : points)
		{
			if (first)
			{
				path.moveTo(p.x, p.y);
				first = false;
			} else
			{
				path.lineTo(p.x, p.y);
			}
		}

		if (close)
		{
			path.closePath();
		}

		return path;
	}

	public static void drawGradientCircle(Graphics2D g, Point2D p, float size)
	{
		Ellipse2D.Float f = new Ellipse2D.Float((float) (p.getX()), (float) (p
				.getY()), size, size);

		f.setFrameFromCenter(p.getX(), p.getY(), p.getX() + size, p.getY()
				+ size);
		g.fill(f);
	}

	public static void main(String input[])
	{
		int imageWidth = 500;
		int imageHeight = 500;

		System.out.print("Making Image:");
		BufferedImage image = ImageOperations.getBi(imageWidth, imageHeight);

		drawCross(image.createGraphics(), new Point2D.Double(10, 10), 10, 0, Color.red, Color.blue);
		JFrame f = FrameFactroy.getFrame(image);
		f.setSize(800, 600);
		f.setVisible(true);
	}
}
