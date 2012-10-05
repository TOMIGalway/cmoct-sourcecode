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
package com.joey.software.VolumeToolkit;

import java.awt.Color;

public class ColormapToolkit
{
	public void getColorMap(int minValue, int maxValue)
	{

	}

	public static int getColor(int index)
	{
		Color c = new Color(index, index, index);
		return c.getRGB();
	}
}

class AlphaColorMap extends Colormap
{
	public AlphaColorMap(int minAlpha, int maxAlpha)
	{
		LinearColormap c = new LinearColormap();
		colorMapping = c.colorMapping;
		for (int i = minAlpha; i < maxAlpha; i++)
		{

			double fraction = i / 256.0;

			double value = 0;

			int redMapping;
			int greenMapping;
			int blueMapping;
			int alphaMapping;

			// value = 255.0 * Math.abs(Math.sin(fraction * Math.PI / 2.0));

			redMapping = (int) value;

			// value = 200.0 * Math.abs(Math.sin(fraction * Math.PI));
			// if (value > 175.0)
			// {
			// value = 175.0;
			// }
			greenMapping = (int) value;

			// value = 255.0 * Math.abs(Math.cos(fraction * Math.PI / 2.0));
			// if (i == 0)
			// {
			// value = 0;
			// }
			blueMapping = (int) value;

			value = 255;
			if (i == 0)
			{
				value = 0;
			} else if (i < minAlpha)
			{
				value = 255 * 0.9;
			} else if (i < maxAlpha)
			{
				value = 1; // just a touch
			} else
			{
				value = 255 * 0.9;
			}
			alphaMapping = (int) value;

			colorMapping[i] = (alphaMapping & 0xFF) << 24
					| (redMapping & 0xFF) << 16 | (greenMapping & 0xFF) << 8
					| (blueMapping & 0xFF);
		}
	}

	public void AlphadColorMap(int min, int max)
	{
		int alphaMapping = 255;
		int redMapping = 0;
		int greenMapping = 0;
		int blueMapping = 0;

		for (int i = 0; i < colorMapping.length; i++)
		{
			if (i < min)
			{
				colorMapping[i] = colorMapping[i] = (alphaMapping & 0xFF) << 24
						| (redMapping & 0xFF) << 16
						| (greenMapping & 0xFF) << 8 | (blueMapping & 0xFF);
				;
			} else if (i > max)
			{
				colorMapping[i] = colorMapping[i] = (alphaMapping & 0xFF) << 24
						| (redMapping & 0xFF) << 16
						| (greenMapping & 0xFF) << 8 | (blueMapping & 0xFF);
			} else
			{
				colorMapping[i] = ColormapToolkit.getColor(i);
			}
		}
	}
}

class RangedColorMap extends Colormap
{
	public RangedColorMap(int min, int minSet, int max, int maxSet)
	{
		for (int i = 0; i < colorMapping.length; i++)
		{
			if (i < min)
			{
				colorMapping[i] = minSet;
			} else if (i > max)
			{
				colorMapping[i] = maxSet;
			}
			{
				colorMapping[i] = (int) (256. / (max - min) * i);
			}
		}
	}
}
