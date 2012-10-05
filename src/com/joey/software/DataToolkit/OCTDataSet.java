package com.joey.software.DataToolkit;

import java.io.File;
import java.io.IOException;

import com.joey.software.framesToolkit.StatusBarPanel;


public class OCTDataSet extends NativeDataSet
{
	public static final int serialVersionUID = 1;

	/**
	 * @throws IOException
	 */
	public OCTDataSet() throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dataFile
	 * @param previewFile
	 * @param status
	 * @param blockLoad
	 * @throws IOException
	 */
	public OCTDataSet(File dataFile, File previewFile, StatusBarPanel status, boolean blockLoad)
			throws IOException
	{
		super(dataFile, previewFile, status, blockLoad);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dataFile
	 * @param previewFile
	 * @param status
	 * @throws IOException
	 */
	public OCTDataSet(File dataFile, File previewFile, StatusBarPanel status)
			throws IOException
	{
		super(dataFile, previewFile, status);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dataFile
	 * @param previewFile
	 * @throws IOException
	 */
	public OCTDataSet(File dataFile, File previewFile) throws IOException
	{
		super(dataFile, previewFile);
		// TODO Auto-generated constructor stub
	}

}
