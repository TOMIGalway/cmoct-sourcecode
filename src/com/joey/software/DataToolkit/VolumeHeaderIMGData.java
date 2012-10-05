package com.joey.software.DataToolkit;

import java.io.File;
import java.io.IOException;

public class VolumeHeaderIMGData extends VolumeHeaderData
{
	File imgFile;

	public VolumeHeaderIMGData(File f) throws IOException
	{
		this.imgFile = f;
		loadData();
	}

	public void loadData() throws IOException
	{
		ThorLabs2DImageProducer load = new ThorLabs2DImageProducer(imgFile);
		sizeDataX = load.getSizeZ();
		sizeDataY = load.getSizeX();
		sizeDataZ = load.getSizeY();
		load = null;
	}
}
