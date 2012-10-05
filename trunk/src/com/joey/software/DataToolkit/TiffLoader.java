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
package com.joey.software.DataToolkit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;


public class TiffLoader extends ImageProducer
{

	File tiffFile = null;

	ImageDecoder dec;

	SeekableStream file;

	int imageCount = 0;

	public TiffLoader(File tiff) throws IOException
	{
		setFile(tiff);
	}

	public void setFile(File tiff) throws IOException
	{
		this.tiffFile = tiff;

		file = new FileSeekableStream(tiff);

		TIFFDecodeParam param = null;

		dec = ImageCodec.createImageDecoder("tiff", file, param);

		// TODO Auto-generated method stub
		try
		{
			imageCount = dec.getNumPages();
		} catch (IOException e)
		{
			JOptionPane
					.showMessageDialog(null, "There was an error reading Image Stack: "
							+ e.getLocalizedMessage());

			e.printStackTrace();
		} catch (RuntimeException e)
		{
			// Ok this second attempt is to see what happens when we try this a
			// second time. Not sure if this will work

			try
			{
				imageCount = dec.getNumPages();
			} catch (RuntimeException e1)
			{
				JOptionPane
						.showMessageDialog(null, "There was an error reading Image Stack: "
								+ e.getLocalizedMessage());

				e.printStackTrace();
			} catch (IOException e2)
			{
				JOptionPane
						.showMessageDialog(null, "There was an error reading Image Stack: "
								+ e.getLocalizedMessage());

				e.printStackTrace();

			}

		}

	}

	public static void main(String input[]) throws IOException
	{
//		File f = new File(
//				"C:\\Users\\joey.enfield\\Desktop\\Pat_708_New_BCC Biopsy_ScanA.2_06-Aug-2009_11.15.54_OCT.tif");// Pat_MDL
		File f = new File("C:\\Users\\joey.enfield\\Desktop\\Mucosa000.tif");
		TiffLoader tiff = new TiffLoader(f);
		System.out.println(tiff.getImageCount());

		StatusBarPanel fileStat = new StatusBarPanel();

		fileStat.setStatusMessage("File Prog");

		JPanel p = new JPanel(new BorderLayout());
		p.add(fileStat);

		FrameFactroy.getFrame(p);
		fileStat.setMaximum(tiff.getImageCount());

		byte valb = 0;
		byte[][][] data = null;
		for (int i = 0; i < tiff.getImageCount(); i++)
		{
			BufferedImage img = tiff.getImage(i);
			FrameFactroy.getFrame(img);
			if (i == 0)
			{
				data = new byte[tiff.getImageCount()][img.getWidth()][img
						.getHeight()];
			}
			fileStat.setValue(i);
			long stat = System.currentTimeMillis();
			for (int x = 0; x < img.getWidth(); x++)
			{
				for (int y = 0; y < img.getHeight(); y++)
				{
					Color c = new Color(img.getRGB(x, y));

					valb = (byte) (((c.getRed() + c.getGreen() + c.getBlue()) / 3.0f) * 255);
					data[i][x][y] = valb;
				}
			}
			System.out.println("PROC" + (System.currentTimeMillis() - stat));
		}
	}

	public BufferedImage convertRenderedImage(RenderedImage img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm
				.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable properties = new Hashtable();
		String[] keys = img.getPropertyNames();
		if (keys != null)
		{
			for (int i = 0; i < keys.length; i++)
			{
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster,
				isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		if (pos < getImageCount())
		{
			RenderedImage op = new NullOpImage(dec.decodeAsRenderedImage(pos),
					null, OpImage.OP_IO_BOUND, null);
			return convertRenderedImage(op);
		}
		return null;
	}

	@Override
	public int getImageCount()
	{
		return imageCount;
	}
}
