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
package com.joey.software.imageToolkit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;

import com.joey.software.timeingToolkit.EventTimer;


/**
 * 
 * @author ochafik
 */
public class ImageUtils
{
	public static void main(String input[])
	{
		EventTimer t = new EventTimer();

		int size = 2500;
		BufferedImage img = new BufferedImage(size, size,
				BufferedImage.TYPE_INT_ARGB);

		t.mark("mine");
		ImageOperations.imageToPixelsInts(img);
		t.tick("mine");

		t.mark("there : false");
		getImageIntPixels(img, false);
		t.tick("there : false");

		t.mark("there : true");
		getImageIntPixels(img, true);
		t.tick("there : true");

		t.printData();
	}

	public static int[] getImageIntPixels(Image image, boolean allowDeoptimizingDirectRead)
	{
		return getImageIntPixels(image, 0, 0, image.getWidth(null), image
				.getHeight(null), allowDeoptimizingDirectRead);
	}

	public static int[] getImageIntPixels(Image image, int x, int y, int width, int height, boolean allowDeoptimizingDirectRead)
	{
		if (image instanceof BufferedImage)
		{
			BufferedImage bim = (BufferedImage) image;
			WritableRaster raster = bim.getRaster();
			if (allowDeoptimizingDirectRead && raster.getParent() == null
					&& raster.getDataBuffer().getNumBanks() == 1)
			{
				DataBuffer b = bim.getRaster().getDataBuffer();
				if (b instanceof DataBufferInt)
				{
					int[] array = ((DataBufferInt) b).getData();
					return array;
				}
			}
			return bim.getRGB(x, y, width, height, null, 0, width);
		}
		PixelGrabber grabber = new PixelGrabber(image, x, y, width, height,
				true);
		try
		{
			grabber.grabPixels();
			return (int[]) grabber.getPixels();
		} catch (InterruptedException ex)
		{
			throw new RuntimeException("Pixel read operation was interrupted",
					ex);
		}
	}

	public static void setImageIntPixels(BufferedImage image, boolean allowDeoptimizingDirectRead, IntBuffer pixels)
	{
		setImageIntPixels(image, 0, 0, image.getWidth(null), image
				.getHeight(null), allowDeoptimizingDirectRead, pixels);
	}

	public static void setImageIntPixels(BufferedImage bim, int x, int y, int width, int height, boolean allowDeoptimizingDirectRead, IntBuffer pixels)
	{
		WritableRaster raster = bim.getRaster();
		if (allowDeoptimizingDirectRead && raster.getParent() == null
				&& raster.getDataBuffer().getNumBanks() == 1)
		{
			DataBuffer b = bim.getRaster().getDataBuffer();
			if (b instanceof DataBufferInt)
			{
				IntBuffer.wrap(((DataBufferInt) b).getData()).put(pixels);
				return;
			}
		}

		IntBuffer b = IntBuffer.allocate(width * height);
		b.put(pixels);
		b.rewind();
		int[] array = b.array();
		bim.setRGB(x, y, width, height, array, 0, width);
	}
}
