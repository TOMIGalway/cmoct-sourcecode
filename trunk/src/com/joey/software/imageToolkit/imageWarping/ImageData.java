package com.joey.software.imageToolkit.imageWarping;


import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.imageToolkit.ImageOperations;


public class ImageData
{
	boolean loaded = false;

	File file;

	BufferedImage img;

	Point2D.Double p1;

	Point2D.Double p2;

	boolean reduce = false;

	double scale = 1;

	public ImageData()
	{
		this(null);
	}

	public ImageData(File f)
	{
		this(f, new Point2D.Double(0, 1), new Point2D.Double(0, 1));
	}

	public ImageData(File f, Point2D.Double p1, Point2D.Double p2)
	{
		file = f;
		this.p1 = p1;
		this.p2 = p2;
	}

	public void setReduced(boolean reduced, double scale)
	{
		reduce = reduced;
		if (reduce)
		{
			// Convert to full size positon
			p1.x /= this.scale;
			p1.y /= this.scale;

			p2.x /= this.scale;
			p2.y /= this.scale;

			this.scale = scale;

			// Conver to reduced images size
			p1.x *= scale;
			p1.y *= scale;

			p2.x *= scale;
			p2.y *= scale;
		} else
		{

			p1.x /= this.scale;
			p1.y /= this.scale;

			p2.x /= this.scale;
			p2.y /= this.scale;

			this.scale = 1;
		}
	}

	public double getLength()
	{
		return p1.distance(p2);
	}

	public synchronized void unloadImage()
	{
		img = ImageOperations.getBi(1);
		loaded = false;
		System.gc();
	}

	public synchronized BufferedImage getImg()
	{
		return getImg(false);
	}

	public void loadImageData()
	{
		loaded = true;
		if (file == null)
		{
			loaded = false;
			img = ImageOperations.getBi(1);
			return;
		}

		try
		{
			img = ImageIO.read(file);
		} catch (IOException e)
		{
			loaded = false;
			img = ImageOperations.getBi(1);
			e.printStackTrace();
		}
	}

	public synchronized BufferedImage getImg(boolean showMarks)
	{

		if (loaded)
		{
			return img;
		} else
		{
			loadImageData();
			if (showMarks)
			{
				DrawTools.drawCross(img.createGraphics(), p1, 5, 0, Color.CYAN);
				DrawTools.drawCross(img.createGraphics(), p2, 5, 0, Color.CYAN);
			}
			if (reduce)
			{
				img = ImageOperations.getScaledImage(img, scale);
			} else
			{

			}

			return img;
		}

	}

	public void setFile(File f)
	{
		this.file = f;
	}

	public Point2D.Double getP1()
	{
		return p1;
	}

	public void setP1(Point2D.Double p1)
	{
		this.p1 = p1;
	}

	public Point2D.Double getP2()
	{
		return p2;
	}

	public void setP2(Point2D.Double p2)
	{
		this.p2 = p2;
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public File getFile()
	{
		return file;
	}

	public boolean isReduce()
	{
		return reduce;
	}

	public double getScale()
	{
		return scale;
	}
}