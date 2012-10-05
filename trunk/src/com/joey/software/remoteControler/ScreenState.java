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
package com.joey.software.remoteControler;


import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import com.joey.software.imageToolkit.ImageOperations;

public class ScreenState
{
	BufferedImage lastScreen;

	BufferedImage currentScreen;

	public static void main(String input[]) throws UnknownHostException,
			IOException
	{
		BufferedImage imgA = ImageOperations.getBi(1024, 800);
		BufferedImage imgB = ImageOperations.getBi(1024, 800);

		ImageOperations
				.fillRegion(imgB, new Rectangle(20, 20, 50, 50), Color.pink);
		Vector<Point> data = new Vector<Point>();
		int size = 5;

		long start = System.currentTimeMillis();
		checkImages(imgA, imgB, data, size);
		System.out.println(System.currentTimeMillis() - start);
		System.out.println("Size : " + data.size());

		Socket soc = new Socket("192.168.0.164", 4444);
		PrintWriter out = new PrintWriter(soc.getOutputStream());

		out.print("hello");
		out.flush();
		soc.close();
	}

	public static void checkImages(BufferedImage last, BufferedImage current, Vector<Point> diff, int size)
	{
		double numX = (last.getWidth() / (double) size);
		double numY = (last.getHeight() / (double) size);

		for (int xP = 0; xP < (int) numX; xP++)
		{
			for (int yP = 0; yP < (int) numY; yP++)
			{
				for (int x = 0; x < size; x++)
				{
					for (int y = 0; y < size; y++)
					{
						int xV = size * xP + x;
						int yV = size * yP + y;
						if (xV < last.getWidth() && yV < last.getHeight())
						{
							if (last.getRGB(xV, yV) != current.getRGB(xV, yV))
							{
								diff.add(new Point(xP, yP));
								x = size;
								y = size;

							}
						}
					}
				}

			}
		}
	}
}
