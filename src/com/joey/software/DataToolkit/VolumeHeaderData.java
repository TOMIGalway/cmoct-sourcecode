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

import java.io.IOException;
import java.io.RandomAccessFile;

public class VolumeHeaderData
{
	RandomAccessFile file;

	int version = 0;

	int volumeSize = 1;

	int numVolX = 1;

	int numVolY = 1;

	int numVolZ = 1;

	int sizeDataX = 1;

	int sizeDataY = 1;

	int sizeDataZ = 1;

	protected VolumeHeaderData()
	{
	}

	public VolumeHeaderData(RandomAccessFile file) throws IOException
	{
		setFile(file);
	}

	public byte[] getSuitableBuffer()
	{
		byte[] buffer = new byte[getVolumeSize() * getVolumeSize()
				* getVolumeSize()];
		return buffer;
	}

	public static String getHeaderDataString(RandomAccessFile file)
			throws IOException
	{
		StringBuilder out = new StringBuilder();

		out.append("\nVersion : ");
		out.append(getVersion(file));

		out.append("\nVolume Size : ");
		out.append(getVolumeSize(file));

		out.append("\nNum Volume X : ");
		out.append(getNumVolumeX(file));

		out.append("\nNum Volume Y : ");
		out.append(getNumVolumeY(file));

		out.append("\nNum Volume Z : ");
		out.append(getNumVolumeZ(file));

		out.append("\nSize Data X : ");
		out.append(getSizeDataX(file));

		out.append("\nSize Data Y : ");
		out.append(getSizeDataY(file));

		out.append("\nSize Data Z : ");
		out.append(getSizeDataZ(file));

		return out.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		try
		{
			result.append(getHeaderDataString(getFile()));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public int getVolumeSize()
	{
		return volumeSize;
	}

	public void setVolumeSize(int volumeSize)
	{
		this.volumeSize = volumeSize;
	}

	public int getNumVolX()
	{
		return numVolX;
	}

	public void setNumVolX(int numVolX)
	{
		this.numVolX = numVolX;
	}

	public int getNumVolY()
	{
		return numVolY;
	}

	public void setNumVolY(int numVolY)
	{
		this.numVolY = numVolY;
	}

	public int getNumVolZ()
	{
		return numVolZ;
	}

	public void setNumVolZ(int numVolZ)
	{
		this.numVolZ = numVolZ;
	}

	public int getSizeDataX()
	{
		return sizeDataX;
	}

	public void setSizeDataX(int sizeDataX)
	{
		this.sizeDataX = sizeDataX;
	}

	public int getSizeDataY()
	{
		return sizeDataY;
	}

	public void setSizeDataY(int sizeDataY)
	{
		this.sizeDataY = sizeDataY;
	}

	public int getSizeDataZ()
	{
		return sizeDataZ;
	}

	public void setSizeDataZ(int sizeDataZ)
	{
		this.sizeDataZ = sizeDataZ;
	}

	private RandomAccessFile getFile()
	{
		return file;
	}

	public void setFile(RandomAccessFile file) throws IOException
	{
		this.file = file;

		version = getVersion(file);
		volumeSize = getVolumeSize(file);
		numVolX = getNumVolumeX(file);
		numVolY = getNumVolumeY(file);
		numVolZ = getNumVolumeZ(file);

		sizeDataX = getSizeDataX(file);
		sizeDataY = getSizeDataY(file);
		sizeDataZ = getSizeDataZ(file);
	}

	/**
	 * This will extract the version of a data file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static int getVersion(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 0);
		return file.readInt();
	}

	/**
	 * This will extract the size of each volume
	 * 
	 * @param file
	 * @return
	 */
	public static int getVolumeSize(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 1);
		return file.readInt();
	}

	/**
	 * This will get the Number of volumes in X that are stored in the file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static int getNumVolumeX(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 2);
		return file.readInt();
	}

	public static int getNumVolumeY(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 3);
		return file.readInt();
	}

	public static int getNumVolumeZ(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 4);
		return file.readInt();
	}

	public static int getSizeDataX(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 5);
		return file.readInt();
	}

	public static int getSizeDataY(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 6);
		return file.readInt();
	}

	public static int getSizeDataZ(RandomAccessFile file) throws IOException
	{
		file.seek(4 * 7);
		return file.readInt();
	}

}
