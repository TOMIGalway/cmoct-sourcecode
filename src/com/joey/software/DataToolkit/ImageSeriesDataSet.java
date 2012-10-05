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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.imageio.ImageIO;

import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageFileLoader;
import com.joey.software.imageToolkit.ImageFileLoaderInterface;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.timeingToolkit.EventTimer;
import com.joey.software.userinterface.VersionManager;


public class ImageSeriesDataSet extends NativeDataSet
{

	private static final long serialVersionUID = VersionManager.VERSION_1;

	File imgFile[];

	ImageFileProducer loader = null;

	/**
	 * This is a variable to store the tiff
	 * header. This is important as the header
	 * from native data set can be reverted...
	 */
	VolumeHeaderImageSeriesData imgHeader;

	public ImageSeriesDataSet() throws IOException
	{
		super();
		allowFullResolutin = false;
	}

	public ImageSeriesDataSet(File img[]) throws IOException
	{
		this();
		setImageFiles(img);
	}

	public void setImageFiles(File img[]) throws IOException
	{
		this.imgFile = img;

		imgHeader = new VolumeHeaderImageSeriesData(img);
		loader = new ImageFileProducer(img);
		header = imgHeader;
	}

	@Override
	public VolumeHeaderData getHeader()
	{
		header = imgHeader;
		return super.getHeader();
	}

	@Override
	public void reloadData(StatusBarPanel status)
	{
		loaded = true;

		setPreviewData(new byte[0][0][0]);
		byte[][][] data = new byte[imgHeader.getSizeDataX()][imgHeader
				.getSizeDataY()][imgHeader.getSizeDataZ()];
		loadImageVolume(data, imgFile, status);
		previewSizeX = data.length;
		previewSizeY = data[0].length;
		previewSizeZ = data[0][0].length;
		setPreviewData(data, 1, 1, 1);

	}

	public static void main(String input[]) throws IOException
	{
		File f[] = ImageFileSelector.getUserImageFile(true);

		BufferedImage img = ImageIO.read(f[0]);
		int x = img.getWidth();
		int y = img.getHeight();
		int z = f.length;

		byte[][][] hold = new byte[x][y][z];
		EventTimer t = new EventTimer();

		t.printData();
	}

	public static byte[][][] loadImageVolume(final File[] f, final StatusBarPanel status)
	{
		BufferedImage img = ImageOperations.loadImage(f[0]);
		byte[][][] data = new byte[img.getWidth()][img.getHeight()][f.length];
		loadImageVolume(data, f, status);
		return data;
	}

	public static void loadImageVolume(final byte[][][] data, final File[] f, final StatusBarPanel status)
	{
		status.setMaximum(f.length);
		ImageFileLoader.loadImageFiles(f, new ImageFileLoaderInterface()
		{

			@Override
			public void imageLoaded(BufferedImage img, int index)
			{
				synchronized (status)
				{
					status.setValue(status.getValue() + 1);
				}
				for (int x = 0; x < img.getWidth(); x++)
				{
					for (int y = 0; y < img.getHeight(); y++)
					{
						data[x][y][index] = (byte) ImageOperations
								.getGrayScale(img.getRGB(x, y));
					}
				}

			}
		}, 10, true);
	}

	@Override
	public boolean isAllowFullResolution()
	{
		allowFullResolutin = false;
		return allowFullResolutin;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		System.out.println("Saving Tiff");
		out.writeObject(imgFile);
		super.writeExternal(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		imgFile = (File[]) in.readObject();
		super.readExternal(in);

		setImageFiles(imgFile);
	}

	public File[] getImageFiles()
	{
		return imgFile;
	}

}
