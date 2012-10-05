package com.joey.software.DataToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VolumeHeaderImageSeriesData extends VolumeHeaderData
{
	File imgFiles[];

	public VolumeHeaderImageSeriesData(File f[]) throws IOException
	{
		this.imgFiles = f;
		loadData();
	}

	public void loadData() throws IOException
	{
		ImageFileProducer load = new ImageFileProducer(imgFiles);
		BufferedImage img = load.getImage(0);
		sizeDataX = img.getWidth();
		sizeDataY = img.getHeight();
		sizeDataZ = load.getImageCount();
		load = null;
		img = null;
	}
}
