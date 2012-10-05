package com.joey.software.DIC;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;


public class DICTesting
{
	public static void main(String input[]) throws IOException
	{
		singlePointTesting();
		//lineTesting();
	}

	public static void lineTesting() throws IOException
	{
		int frame = 100;
		// These set the max in x and y that
		// movement is tested for
		int maxX = 3;
		int maxY = 3;

		// This sets the number of locations
		// around the the point that should be
		// tested to get average
		int numX = 3;
		int numY = 3;

		// This is the size of the kernal that is
		// used
		int kerX = 10;
		int kerY = 10;

		File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\before_3D_000.IMG");
		ThorlabsIMGImageProducer loader = new ThorlabsIMGImageProducer(f, true);

		int wide = loader.getSizeX();
		int high = loader.getSizeY();
		byte[][] loadHolder = new byte[high][wide];
		byte[][] frameA = new byte[wide][high];
		byte[][] frameB = new byte[wide][high];
		float[][] hold = new float[2 * maxX + 1][2 * maxY + 1];
		float[][] dir = new float[2 * maxX + 1][2 * maxY + 1];

		BufferedImage imgA = ImageOperations.getImage(frameA);
		BufferedImage imgB = ImageOperations.getImage(frameB);
		ImagePanel panelA = new ImagePanel(imgA);
		ImagePanel panelB = new ImagePanel(imgB);
		FrameFactroy.getFrameTabs(panelA.getInPanel(), panelB.getInPanel());

		
		Point2D current[] = null;
		Point2D orignal[] = null;
		Point2D diff[] = null;
		GeneralPath path[] = null;

		boolean first = true;
		 for (frame = 0; frame <
		 1000000; frame++)
		 //loader.getImageCount() - 1; frame++)
		{

			// Load Frame A
			ArrayToolkit.clone(frameB, frameA);
			ArrayToolkit.setValue(frameB, (byte)0);
			
			if(first)
			{
				ArrayToolkit.clone(frameB,frameA);
				current = PolygonControler.getUserPath(imgA);
				orignal = clone(current);
				diff = clone(current);
				path = new GeneralPath[orignal.length];
			}
			
			for(int p = 0; p < current.length; p++)
			{
				int x = (int)(current[p].getX()+((0.5-Math.random()))*maxX);
				int y = (int)(current[p].getY()+((0.5-Math.random()))*maxY);
				if(x < 0)
				{
					x = maxX;
				}
				else if(x > frameB.length)
				{
					x = frameB.length-kerX;
				}
				
				if(y < 0)
				{
					y = maxY;
				}
				else if(y > frameB[0].length)
				{
					y = frameB[0].length-kerY;
				}
				drawShape(frameB, x,y, kerX/3, (byte)255);
			}
			
			if(first)
			{
				first = false;
				ArrayToolkit.clone(frameB, frameA);
			}
		
			ImageOperations.getImage(frameB, imgB);
			ImageOperations.getImage(frameA, imgA);
			for (int i = 0; i < current.length; i++)
			{
				// If no previous point get new
				// point
				if (path[i] == null)
				{
					path[i] = new GeneralPath();
					path[i].moveTo(current[i].getX(), current[i].getY());
				}
				path[i].lineTo(current[i].getX(), current[i].getY());

				int locX = (int) current[i].getX();
				int locY = (int) current[i].getY();

				// Test Corellation Change
				ArrayToolkit.setValue(hold, 0);
				for (int posX = locX - numX; posX <= locX + numX; posX++)
				{
					for (int posY = locY - numY; posY <= locY + numY; posY++)
					{
						determineCorellation(frameA, frameB, posX, posY, kerX, kerY, dir);
						ArrayToolkit.add(hold, dir);
					}
				}
				ArrayToolkit
						.scale(hold, 1 / ((2 * numX + 1f) * (2 * numY + 1)));

				Graphics2D g = imgA.createGraphics();
				g.setColor(Color.cyan);
				g.drawRect(locX - kerX, locY - kerY, (2 * kerX + 1), (2 * kerY + 1));
				g.setColor(Color.red);
				g.draw(path[i]);

				
				getMaxOffset(dir, diff[i]);
				current[i].setLocation(current[i].getX() + diff[i].getX(), current[i].getY() + diff[i].getY());
			}
			panelA.repaint();
			panelB.repaint();

		}

	}

	public static Point2D[] clone(Point2D[] data)
	{
		Point2D[] result = new Point2D[data.length];
		for (int i = 0; i < data.length; i++)
		{
			result[i] = (Point2D) data[i].clone();
		}
		return result;
	}

	public static GeneralPath getPath(Point2D data[])
	{
		GeneralPath path = new GeneralPath();
		path.moveTo(data[0].getX(), data[0].getY());
		for (int i = 0; i < data.length; i++)
		{
			path.lineTo(data[i].getX(), data[i].getY());
		}
		return path;
	}

	public static void singlePointTesting() throws IOException
	{
		// These set the max in x and y that
		// movement is tested for
		int maxX = 5;
		int maxY = 5;

		// This sets the number of locations
		// around the the point that should be
		// tested to get average
		int numX = 3;
		int numY = 3;

		// This is the size of the kernal that is
		// used
		int kerX = 12;
		int kerY = 12;

		File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\before_3D_000.IMG");
		ThorlabsIMGImageProducer loader = new ThorlabsIMGImageProducer(f, true);

		int wide = loader.getSizeX();
		int high = loader.getSizeY();
		byte[][] loadHolder = new byte[high][wide];
		byte[][] frameA = new byte[wide][high];
		byte[][] frameB = new byte[wide][high];
		float[][] hold = new float[2 * maxX + 1][2 * maxY + 1];
		float[][] dir = new float[2 * maxX + 1][2 * maxY + 1];

		BufferedImage imgA = ImageOperations.getImage(frameA);
		BufferedImage imgB = ImageOperations.getImage(frameB);
		ImagePanel panelA = new ImagePanel(imgA);
		ImagePanel panelB = new ImagePanel(imgB);
		FrameFactroy
				.getFrameTabs(panelA.getInPanel(), panelB.getInPanel(), new DynamicRangeImage(
						hold));

		GeneralPath path = new GeneralPath();

		Point2D p = null;
		Point2D.Double diff = new Point2D.Double();
		for (int frame = 0; frame < loader.getImageCount() - 1; frame++)
		{

			// Load Frame A
			loader.getImage(frame, loadHolder);
			ArrayToolkit.transpose(loadHolder, frameA);
			ImageOperations.getImage(frameA, imgA);

			loader.getImage(frame + 1, loadHolder);
			ArrayToolkit.transpose(loadHolder, frameB);
			ImageOperations.getImage(frameB, imgB);

			// If no previous point get new point
			if (p == null)
			{
				p = PolygonControler.getUserPoint(imgA);
				path.moveTo(p.getX(), p.getY());
			}
			path.lineTo(p.getX(), p.getY());

			int locX = (int) p.getX();
			int locY = (int) p.getY();

			// Test Corellation Change
			ArrayToolkit.setValue(hold, 0);
			for (int posX = locX - numX; posX <= locX + numX; posX++)
			{
				for (int posY = locY - numY; posY <= locY + numY; posY++)
				{
					determineCorellation(frameA, frameB, posX, posY, kerX, kerY, dir);
					ArrayToolkit.add(hold, dir);
				}
			}
			ArrayToolkit.scale(hold, 1 / ((2 * numX + 1f) * (2 * numY + 1)));

			Graphics2D g = imgA.createGraphics();
			g.setColor(Color.cyan);
			g.drawRect(locX - kerX, locY - kerY, (2 * kerX + 1), (2 * kerY + 1));
			g.setColor(Color.red);
			g.draw(path);

			panelA.repaint();
			panelB.repaint();

			getMaxOffset(dir, diff);
			p.setLocation(p.getX() + diff.x, p.getY() + diff.y);
		}

	}

	public static void getMaxOffset(float[][] dir, Point2D p)
	{

		int dx = (dir.length - 1) / 2;
		int dy = (dir[0].length - 1) / 2;

		float max = 0;
		for (int x = 0; x < dir.length; x++)
		{
			for (int y = 0; y < dir[0].length; y++)
			{
				if ((x == 0 && y == 0) || max < dir[x][y])
				{
					max = dir[x][y];
					p.setLocation(x - dx, y - dy);
				}
			}
		}
	}

	public static void setValue(float[][] data, float val)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				data[x][y] = val;
			}
		}
	}

	/**
	 * This function will check for corellation
	 * for a given kernal size in grid A to all
	 * ocations in the second grid
	 * 
	 * @param frameA
	 * @param frameB
	 * @param corr
	 * @param posX
	 * @param posY
	 * @param kerX
	 * @param kerY
	 */
	public static void determineFullCorellation(byte[][] frameA, byte[][] frameB, float[][] corr, int posX, int posY, int kerX, int kerY)
	{
		int kerSizeX = (2 * kerX + 1);
		int kerSizeY = (2 * kerY + 1);

		int[] gridA = new int[kerSizeX * kerSizeY];
		int[] gridB = new int[kerSizeX * kerSizeY];

		// Grab Kernal A
		grabKernal(frameA, gridA, posX, posY, kerX, kerY);

		for (int xP = 0; xP < frameB.length; xP++)
		{
			for (int yP = 0; yP < frameB[0].length; yP++)
			{
				// Grab Kernal B
				grabKernal(frameB, gridB, xP, yP, kerX, kerY);

				// Do Cross corellation
				corr[xP][yP] = getCrossCorr(gridA, gridB);
			}
		}
	}

	/**
	 * This function will check for corellation
	 * between two images for a given point in a
	 * series of directions directions are stored
	 * in directions[][]
	 * 
	 * number of directions depends on size of
	 * directions
	 * 
	 * @param frameA
	 * @param frameB
	 * @param corr
	 * @param posX
	 * @param posY
	 * @param kerX
	 * @param kerY
	 */
	public static void determineCorellation(byte[][] frameA, byte[][] frameB, int posX, int posY, int kerX, int kerY, float[][] direction)
	{
		int kerSizeX = (2 * kerX + 1);
		int kerSizeY = (2 * kerY + 1);

		int[] gridA = new int[kerSizeX * kerSizeY];
		int[] gridB = new int[kerSizeX * kerSizeY];

		// Grab Kernal A
		grabKernal(frameA, gridA, posX, posY, kerX, kerY);

		int dx = (direction.length - 1) / 2;
		int dy = (direction[0].length - 1) / 2;

		for (int xP = posX - dx; xP <= posX + dx; xP++)
		{
			for (int yP = posY - dy; yP <= posY + dy; yP++)
			{
				// Grab Kernal B
				grabKernal(frameB, gridB, xP, yP, kerX, kerY);

				// Do Cross corellation
				direction[xP + dx - posX][yP + dy - posY] = getCrossCorr(gridA, gridB);
			}
		}
	}

	public static float getCrossCorr(int[] gridA, int[] gridB)
	{

		float avgA = getAverage(gridA);
		float avgB = getAverage(gridB);

		int i;
		float tA = 0;
		float tB = 0;
		float tC = 0;
		float t1 = 0;
		float t2 = 0;
		int cal = 0;

		for (i = 0; i < gridA.length; i++)
		{

			t1 = gridA[i] - avgA;
			t2 = gridB[i] - avgB;
			tA += t1 * t2;
			tB += t1 * t1;
			tC += t2 * t2;
		}

		if (tB == 0 || tC == 0)
		{
			return 0;
		}
		return (float) (tA / Math.sqrt(tB * tC));
	}

	public static int getAverage(int data[])
	{
		if (data.length == 0)
		{
			return 0;
		}
		int value = 0;
		for (int i = 0; i < data.length; i++)
		{
			value += data[i];
		}
		value = value / data.length;
		return value;
	}

	public static void grabKernal(byte[][] data, int[] grid, int posX, int posY, int kerX, int kerY)
	{
		int pxlCount = 0;
		for (int x = posX - kerX; x <= posX + kerX; x++)
		{

			for (int y = posY - kerY; y <= posY + kerY; y++)
			{
				if (x < data.length && y < data[0].length && x >= 0 && y >= 0)
				{
					grid[pxlCount] = (data[x][y] < 0 ? data[x][y] + 256
							: data[x][y]);

				} else
				{
					grid[pxlCount] = 0;
				}
				pxlCount++;
			}
		}
	}

	public static void drawShape(byte[][] frame, int x, int y, int wide, byte value)
	{
		for (int xP = x - wide; xP <= x + wide; xP++)
		{
			for (int yP = y - wide; yP <= y + wide; yP++)
			{
				if (xP < frame.length && yP < frame[0].length && xP >= 0
						&& yP >= 0)
				{
					frame[xP][yP] = value;
				}
			}
		}
	}
}
