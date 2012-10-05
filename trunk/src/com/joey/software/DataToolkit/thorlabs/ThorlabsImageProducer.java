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
package com.joey.software.DataToolkit.thorlabs;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.stringToolkit.StringOperations;


public abstract class ThorlabsImageProducer extends ImageProducer
{

	public static final int MODE_2D = 0;

	public static final int MODE_3D = 1;

	public static final int DATA_FRG = 0;

	public static final int DATA_IMG = 1;

	int currentData = -1;

	int currentMode = -1;

	File[] files; // Stores all file information
					// for a thorlabs dataset

	int[] fileFrameStart;// Stores first frame in
							// given file

	int sizeZ;

	int sizeX;

	int sizeY;

	String fileId;

	int num3D;

	int frmLenBytes;

	int frmPixels;

	public void setFile(File f) throws IOException
	{
		setFile(f, false);
	}

	public void setFile(File f, boolean forceSingleFile) throws IOException
	{
		if (!f.exists())
		{
			throw new FileNotFoundException(f.toString());
		}
		if (forceSingleFile)
		{
			files = new File[] { f };
		} else
		{
			files = getAllFiles(f);
		}

		// This will check current data type
		if (FileOperations.getExtension(files[0]).equalsIgnoreCase(".img"))
		{
			currentData = DATA_IMG;
		} else
		{
			currentData = DATA_FRG;
		}

		fileFrameStart = new int[files.length];
		sizeZ = 0;
		boolean not3Ddata = false; // This
									// variable
									// tells if 2d
									// or 3d data
		for (int i = 0; i < files.length; i++)
		{

			byte[] data = new byte[36];
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(f)));
			in.read(data, 0, 16);
			fileId = (byteToString(data, 16));

			in.read(data, 0, 4);
			int totalFrames = BinaryToolkit.readFlippedInt(data, 0);

			// Set starting frame using total
			// frames
			if (i < files.length - 1)
			{
				fileFrameStart[i + 1] = fileFrameStart[i] + totalFrames;
			}

			if (i == 0)
			{
				in.read(data, 0, 4);
				sizeX = BinaryToolkit.readFlippedInt(data, 0);

				in.read(data, 0, 4);
				sizeY = BinaryToolkit.readFlippedInt(data, 0);

				in.read(data, 0, 4);
				sizeZ = BinaryToolkit.readFlippedInt(data, 0);

				in.read(data, 0, 4);
				num3D = BinaryToolkit.readFlippedInt(data, 0);

			}
			if (sizeZ == 0)
			{
				currentMode = MODE_2D;
				not3Ddata = true;
			} else
			{
				currentMode = MODE_3D;
			}

			if (not3Ddata)
			{
				sizeZ += totalFrames;
			}
		}

		allocateMemory();

		// This bit here tests to make sure the
		// correct number of frames
		// exists in the final file
		boolean sizeZFixed = false;
		for (int i = sizeZ; i > 0 && !sizeZFixed; i--)
		{
			if (getFileOffset(i) < files[files.length - 1].length())
			{
				sizeZ = i;
				sizeZFixed = true;
			}
		}

		allocateMemory();

	}

	public File[] getData()
	{
		return files;
	}

	public int getCurrentMode()
	{
		return currentMode;
	}

	public int getCurrentData()
	{
		return currentData;
	}

	/**
	 * this function is called after the file is
	 * loaded to allocate memroy for loading
	 */
	protected abstract void allocateMemory();

	protected int getFileOffset(int pos)
	{
		// The 512 is the header
		return 512 + (getCorrectFrame(pos)) * frmLenBytes;
	}

	public String byteToString(byte[] data, int count)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < count; i++)
		{
			rst.append((char) data[i]);
		}
		return rst.toString();
	}

	@Override
	public int getImageCount()
	{
		// TODO Auto-generated method stub
		return sizeZ;
	}

	public int getSizeZ()
	{
		return sizeZ;
	}

	public void setSizeZ(int sizeZ)
	{
		this.sizeZ = sizeZ;
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(int sizeX)
	{
		this.sizeX = sizeX;
	}

	public int getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(int sizeY)
	{
		this.sizeY = sizeY;
	}

	/**
	 * This function will get the series of Files
	 * that are present in the file
	 * 
	 * @param f
	 * @return
	 */
	public static File[] getAllFiles(File f)
	{
		Vector<File> files = new Vector<File>();
		int count = 0;
		String[] data = FileOperations.splitFile(f);

		boolean stillFound = true;
		for (int i = 0; i < 999 && stillFound; i++)
		{
			String fileBase = data[1].substring(0, data[1].length() - 3);
			fileBase = fileBase + StringOperations.getNumberString(3, i);

			File file = new File(data[0] + fileBase + "." + data[2]);
			if (file.exists())
			{
				files.add(file);
			} else if (i == 0)
			{
				// Sometimes there is no 0 file.
			} else
			{
				stillFound = false;
				return files.toArray(new File[0]);
			}

		}
		return null;
	}

	protected int getCorrectFrame(int frame)
	{
		for (int i = 0; i < files.length - 1; i++)
		{
			if (fileFrameStart[i + 1] > frame)
			{
				return frame - fileFrameStart[i];
			}
		}
		return frame - fileFrameStart[files.length - 1];
	}

	/**
	 * When data is split across multiple files,
	 * this will get the correct file for a frame.
	 * 
	 * @param frame
	 * @return
	 */
	protected File getCorrectFile(int frame)
	{
		for (int i = 0; i < files.length - 1; i++)
		{
			if (fileFrameStart[i + 1] > frame)
			{
				return files[i];
			}
		}
		return files[files.length - 1];
	}

	public void getImage(int frame, byte[] data) throws IOException
	{
		getImage(frame, data, 0);
	}

	public abstract void getImage(int pos, BufferedImage img)
			throws IOException;

	public void getImage(int frame, int[] data) throws IOException
	{
		getImage(frame, data, 0);
	}

	public abstract void getImage(int frame, byte[] data, int pos)
			throws IOException;

	public abstract void getImage(int frame, int[] data, int pos)
			throws IOException;

	public abstract void getData(byte[][][] data, StatusBarPanel status)
			throws IOException;

	/**
	 * Return [z][x][y] (z is frames num y is
	 * depth)
	 * 
	 * @return
	 */
	public byte[][][] createDataHolder()
	{
		return new byte[getSizeZ()][getSizeX()][getSizeY()];
	}

	public void printSize()
	{
		System.out.println(getSizeX()+","+getSizeY()+","+getSizeZ());
	}
}
