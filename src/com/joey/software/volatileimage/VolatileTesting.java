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
package com.joey.software.volatileimage;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Vector;

import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class VolatileTesting
{

	public static void main(String input[])
	{
		int imgNum = 25;
		int wide = 2048;
		int high = 2048;
		Vector<Vector<VolatileImage>> hold = new Vector<Vector<VolatileImage>>();
		for (GraphicsDevice gd : GraphicsEnvironment
				.getLocalGraphicsEnvironment().getScreenDevices())
		{
			System.out.println(gd.toString());

			Vector<VolatileImage> data = new Vector<VolatileImage>();
			boolean ok = true;
			int count = 0;
			long startMemory = -gd.getAvailableAcceleratedMemory();
			while (ok)
			{

				VolatileImage img = gd.getDefaultConfiguration()
						.createCompatibleVolatileImage(wide, high);
				Graphics2D g = img.createGraphics();
				g.setColor(ImageOperations.getRandomColor());
				g.fillRect(0, 0, wide, high);

				ImageOperations.fillWithRandomColorSquares(50, 50, img);
				data.add(img);
				long diff = -gd.getAvailableAcceleratedMemory() - startMemory;
				System.out.println("Num : " + (count++) + " Memory :"
						+ (diff / 1024 / 1024f));

				if (count > imgNum)
				{
					ok = false;
				}
				try
				{
					Thread.sleep(300);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			hold.add(data);

		}

		BufferedImage img = ImageOperations.getBi(wide, high);
		ImagePanel pan = new ImagePanel(img);

		JPanel holder = new JPanel();
		pan.putIntoPanel(holder);
		FrameFactroy.getFrame(holder);
		while (true)
		{
			for (Vector<VolatileImage> dd : hold)
			{
				for (VolatileImage v : dd)
				{

					ImageOperations.setImageColor(Color.WHITE, img);
					Graphics2D g = img.createGraphics();
					g.drawImage(v.getSnapshot(), null, 0, 0);
					if (v.contentsLost())
					{
						g.setColor(Color.GREEN);
					} else
					{
						g.setColor(Color.RED);
					}
					g.drawRect(0, 0, 200, 200);
					pan.repaint();
				}
			}
		}

	}
}
