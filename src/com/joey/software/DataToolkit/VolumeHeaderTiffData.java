package com.joey.software.DataToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VolumeHeaderTiffData extends VolumeHeaderData
{
	File tiff;

	public VolumeHeaderTiffData(File f) throws IOException
	{
		this.tiff = f;
		loadData();
	}

	public void loadData() throws IOException
	{
		TiffLoader load = new TiffLoader(tiff);
		sizeDataZ = load.getImageCount();

		BufferedImage img = load.getImage(0);
		sizeDataX = img.getWidth();
		sizeDataY = img.getHeight();
		img = null;
		load = null;
	}
}
