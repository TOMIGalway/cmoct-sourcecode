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
package com.joey.software.imageToolkit.noise;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/* portions of this code were adapted from the DitherTest applet */
/* noise synthesis code adapted from source code by Ken Perlin
 * and F. Kenton Musgrave to accompany:
 * Texturing and Modeling: A Procedural Approach
 * Ebert, D., Musgrave, K., Peachey, P., Perlin, K., and Worley, S.
 * AP Professional, September, 1994. ISBN 0-12-228760-6
 * Web site: http://www.cs.umbc.edu/~ebert/book/book.html
 */

public class NoiseSynthesisApplet extends Applet implements Runnable
{
	/***************************************************************************
	 * Boilerplate methods to handle painting and initialization
	 **************************************************************************/

	private int width = 256;

	private int height = 256;

	private double scalefactor = 1.0;

	private int[][] heightfield;

	private Image img;

	private boolean started;

	@Override
	public boolean handleEvent(Event evt)
	{
		if (evt.id == Event.WINDOW_DESTROY)
			System.exit(0);
		return super.handleEvent(evt);
	}

	@Override
	public boolean action(Event evt, Object arg)
	{
		return true;
	}

	String calcString = "Calculating...";

	String calcStyle = "planet";

	@Override
	public void paint(Graphics g)
	{
		int w = size().width;
		int h = size().height;
		if (img == null)
		{
			super.paint(g);
			g.setColor(Color.black);
			FontMetrics fm = g.getFontMetrics();
			int x = (w - fm.stringWidth(calcString)) / 2;
			int y = h / 2;
			g.drawString(calcString, x, y);
		} else
		{
			g.drawImage(img, 0, 0, w, h, this);
		}
	}

	@Override
	public void init()
	{
		int w = Integer.parseInt(getParameter("width"));
		if ((w <= 0) || (w > 256))
			w = width;
		width = w;
		height = w;
		scalefactor = 256 / width;
		started = false;
		calcStyle = getParameter("style");
		start();
	}

	synchronized void BuildImage()
	{
		/* build the image for display -- greyscale */
		int pixels[];
		int i, j, a, index = 0, min, max;
		// calculate range of values in heightfield
		min = heightfield[0][0];
		max = heightfield[0][0];
		for (i = 0; i < width; i++)
		{
			for (j = 0; j < width; j++)
			{
				if (heightfield[i][j] < min)
					min = heightfield[i][j];
				if (heightfield[i][j] > max)
					max = heightfield[i][j];
			}
		}
		scalefactor = 255.0 / (max - min);
		pixels = new int[width * height];
		for (i = 0; i < width; i++)
		{
			for (j = 0; j < width; j++)
			{
				a = (int) ((heightfield[i][j] - min) * scalefactor);
				if (a < 0)
					a = 0;
				if (a > 255)
					a = 255;
				/* if (a > 255) a=255; */
				pixels[index++] = (255 << 24) | (a << 16) | (a << 8) | a;
			}
		}

		img = createImage(new MemoryImageSource(width, height, ColorModel
				.getRGBdefault(), pixels, 0, width));
		repaint();
	}

	/***************************************************************************
	 * Thread methods to handle processing in the background
	 **************************************************************************/

	Thread kicker;

	@Override
	public/* synchronized */void start()
	{
		if (!started)
		{
			started = true;
			kicker = new Thread(this);
			kicker.start();
		} else if ((kicker != null) && (kicker.isAlive()))
			kicker.resume();
	}

	@Override
	public/* synchronized */void stop()
	{
		try
		{
			if ((kicker != null) && (kicker.isAlive()))
			{
				kicker.suspend();
			}
		} catch (Exception e)
		{
		}
	}

	public void restart()
	{
		try
		{
			if (kicker != null)
			{
				kicker.stop();
			}
		} catch (Exception e)
		{
		}
		kicker = null;
		img = null;
		started = false;
		start();
	}

	@Override
	public void run()
	{
		Thread me = Thread.currentThread();
		me.setPriority(4);
		if (calcStyle == null)
		{
			String[] opt =
			{ "basic", "multi", "hetro", "riged" };
			Object[] possibilities =
			{ "ham", "spam", "yam" };
			String s = (String) JOptionPane
					.showInputDialog(null, "Complete the sentence:\n"
							+ "\"Green eggs and...\"", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, new ImageIcon(), opt,

					opt[0]);
			calcStyle = s;
		}
		if (calcStyle.equals("basic"))
			DoBasicfBm(0.5, 2.0, 7.0);
		else if (calcStyle.equals("multi"))
			DoMultifractal(0.5, 2.0, 7.0, 1.0);
		else if (calcStyle.equals("hetero"))
			DoHeteroTerrain(0.5, 2.0, 7.0, 0.0);
		else if (calcStyle.equals("hybrid"))
			DoHybridMultifractal(0.25, 2.0, 7.0, 0.7);
		else if (calcStyle.equals("ridged"))
			DoRidgedMultifractal(1.0, 2.0, 7.0, 1.0, 2.0);
	}

	synchronized public void DoBasicfBm(double H, double lacunarity, double octaves)
	{
		int i, j;
		double point[] = new double[3];
		double buffer[][] = new double[width][width];
		double min, max;
		heightfield = new int[width][width];
		PerlinSolidNoiseGenerator psng = new PerlinSolidNoiseGenerator(H,
				lacunarity, octaves);
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				point[0] = ((double) i) / ((double) width) + 1.0;
				point[1] = ((double) j) / ((double) width) + 1.0;
				buffer[i][j] = psng.value(point[0], point[1], 0.5);
			}
		min = max = buffer[0][0];
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				if (min > buffer[i][j])
					min = buffer[i][j];
				if (max < buffer[i][j])
					max = buffer[i][j];
			}
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				heightfield[i][j] = (int) (((buffer[i][j] - min) / (max - min)) * 256);
			}
		BuildImage();
	}

	synchronized public void DoMultifractal(double H, double lacunarity, double octaves, double offset)
	{
		int i, j;
		double point[] = new double[3];
		double buffer[][] = new double[width][width];
		double min, max;
		heightfield = new int[width][width];
		PerlinSolidNoiseGenerator psng = new PerlinSolidNoiseGenerator(
				PerlinSolidNoiseGenerator.METHOD_MULTIFRACTAL, H, lacunarity,
				octaves, offset);
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				point[0] = ((double) i) / ((double) width) + 1.0;
				point[1] = ((double) j) / ((double) width) + 1.0;
				buffer[i][j] = psng.value(point[0], point[1], 0.5);
			}
		min = max = buffer[0][0];
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				if (min > buffer[i][j])
					min = buffer[i][j];
				if (max < buffer[i][j])
					max = buffer[i][j];
			}
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				heightfield[i][j] = (int) (((buffer[i][j] - min) / (max - min)) * 256);
			}
		BuildImage();
	}

	synchronized public void DoHeteroTerrain(double H, double lacunarity, double octaves, double offset)
	{
		int i, j;
		double point[] = new double[3];
		double buffer[][] = new double[width][width];
		double min, max;
		heightfield = new int[width][width];
		PerlinSolidNoiseGenerator psng = new PerlinSolidNoiseGenerator(
				PerlinSolidNoiseGenerator.METHOD_HETERO_TERRAIN, H, lacunarity,
				octaves, offset);
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				point[0] = ((double) i) / ((double) width) + 1.0;
				point[1] = ((double) j) / ((double) width) + 1.0;
				buffer[i][j] = psng.value(point[0], point[1], 0.5);
			}
		min = max = buffer[0][0];
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				if (min > buffer[i][j])
					min = buffer[i][j];
				if (max < buffer[i][j])
					max = buffer[i][j];
			}
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				heightfield[i][j] = (int) (((buffer[i][j] - min) / (max - min)) * 256);
			}
		BuildImage();
	}

	synchronized public void DoHybridMultifractal(double H, double lacunarity, double octaves, double offset)
	{
		int i, j;
		double point[] = new double[3];
		double buffer[][] = new double[width][width];
		double min, max;
		heightfield = new int[width][width];
		PerlinSolidNoiseGenerator psng = new PerlinSolidNoiseGenerator(
				PerlinSolidNoiseGenerator.METHOD_HYBRID_MULTIFRACTAL, H,
				lacunarity, octaves, offset);
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				point[0] = ((double) i) / ((double) width) + 1.0;
				point[1] = ((double) j) / ((double) width) + 1.0;
				buffer[i][j] = psng.value(point[0], point[1], 0.5);
			}
		min = max = buffer[0][0];
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				if (min > buffer[i][j])
					min = buffer[i][j];
				if (max < buffer[i][j])
					max = buffer[i][j];
			}
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				heightfield[i][j] = (int) (((buffer[i][j] - min) / (max - min)) * 256);
			}
		BuildImage();
	}

	synchronized public void DoRidgedMultifractal(double H, double lacunarity, double octaves, double offset, double gain)
	{
		int i, j;
		double point[] = new double[3];
		double buffer[][] = new double[width][width];
		double min, max;
		heightfield = new int[width][width];
		PerlinSolidNoiseGenerator psng = new PerlinSolidNoiseGenerator(H,
				lacunarity, octaves, offset, gain);
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				point[0] = ((double) i) / ((double) width) + 1.0;
				point[1] = ((double) j) / ((double) width) + 1.0;
				buffer[i][j] = psng.value(point[0], point[1], 0.5);
			}
		min = max = buffer[0][0];
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				if (min > buffer[i][j])
					min = buffer[i][j];
				if (max < buffer[i][j])
					max = buffer[i][j];
			}
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				heightfield[i][j] = (int) (((buffer[i][j] - min) / (max - min)) * 256);
			}
		BuildImage();
	}
}
