package com.joey.software.DataToolkit;

import java.io.IOException;

public class VolumeHeaderArray extends VolumeHeaderData
{
	
	byte[][][] volData;
	public VolumeHeaderArray(byte[][][] data) throws IOException
	{
		volData = data;
		loadData();
	}

	public void loadData() throws IOException
	{
		sizeDataX = volData.length;
		sizeDataY = volData[0].length;
		sizeDataZ = volData[0][0].length;
	}
}
