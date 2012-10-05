package com.joey.software.DataToolkit;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.userinterface.VersionManager;


public class ArrayDataSet extends NativeDataSet
{

	private static final long serialVersionUID = VersionManager.VERSION_1;

	/**
	 * This is a variable to store the tiff header. This is important as the
	 * header from native data set can be reverted...
	 */
	VolumeHeaderArray arrayHeader;

	byte[][][] array = null;
	public ArrayDataSet(byte[][][] data) throws IOException
	{
		super();
		allowFullResolutin = false;
		arrayHeader = new VolumeHeaderArray(data);
		header = arrayHeader;
		setData(data);
		reloadData();
	}
	
	public void setData(byte[][][] data)
	{
		this.array = data;
		previewSizeX = data.length;
		previewSizeY = data[0].length;
		previewSizeZ = data[0][0].length;
		setPreviewData(data, 1, 1, 1);
	}

	@Override
	public VolumeHeaderData getHeader()
	{
		header = arrayHeader;
		return super.getHeader();
	}

	@Override
	public void reloadData(StatusBarPanel status)
	{
		loaded = true;
		setData(array);
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
		
		super.writeExternal(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
	
		super.readExternal(in);
	}

	public static void main(String input[]) throws IOException
	{
		
	}
}
