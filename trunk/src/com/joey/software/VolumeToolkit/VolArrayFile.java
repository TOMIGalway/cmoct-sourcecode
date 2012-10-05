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

import java.io.IOException;

public class VolArrayFile extends VolFile
{
	public VolArrayFile()
	{
		this(new byte[][][]
		{
		{
		{ (byte) 1 } } }, 1, 1, 1);
	}

	/**
	 * This should pass the byte data in the form byte[z][y][x]
	 * 
	 * @param data
	 * @throws IOException
	 */
	public VolArrayFile(byte[][][] data, float xScale, float yScale, float zScale)

	{
		setData(data, xScale, yScale, zScale);
	}

	public byte[][][] getData()
	{
		return fileData;
	}

	public void setData(byte[][][] data, float xScale, float yScale, float zScale)
	{
		if (data.length == 0 || data[0].length == 0 || data[0][0].length == 0)
		{
			xDim = 0;
			yDim = 0;
			zDim = 0;
		} else
		{
			xDim = data[0][0].length;
			yDim = data[0].length;
			zDim = data.length;
		}
		xSpace = zScale;
		ySpace = yScale;
		zSpace = xScale;

		fileData = data;

	}

}
