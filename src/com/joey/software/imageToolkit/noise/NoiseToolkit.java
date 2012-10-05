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

import java.awt.Color;
import java.awt.image.BufferedImage;

public class NoiseToolkit
{
	public static void addRandomNoise(BufferedImage image, int base, int maxAmp)
	{
		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				Color c = new Color(image.getRGB(x, y));

				int val = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

				val = val + base + (int) (Math.random() * maxAmp);

				if (val >= 256)
				{
					val = 255;
				}

				c = new Color(val, val, val);
				image.setRGB(x, y, c.getRGB());
			}
		}
	}
}
