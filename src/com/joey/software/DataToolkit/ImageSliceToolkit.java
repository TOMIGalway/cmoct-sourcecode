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


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;

import javax.swing.JOptionPane;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;


/**
 * This toolkit can be used to store a series of images into a data file that
 * allows planes to be quickly sliced out of the data
 * 
 * @author joey.enfield
 * 
 */
public class ImageSliceToolkit
{
	static final int VERSION_1 = 1;

	static final int VERSION = VERSION_1;

	public static void printFileData(RandomAccessFile file) throws IOException
	{
		int buffer = 100;

		long size = file.length();
		byte[] data = new byte[size < buffer ? (int) size : buffer];

		System.out.printf("Raw Data\n");

		for (int i = 0; i < size; i += buffer)
		{
			file.seek(i);
			file.read(data);
			System.out.print(BinaryToolkit.printData(data, 4,0));
		}

	}

	/**
	 * This will return a given slice in the Z
	 * 
	 * @param file
	 * @param pos
	 * @param rst
	 * @throws IOException
	 */
	public static void getZSlice(RandomAccessFile file, int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		// status.setStatusMessage("Generating Slice");
		VolumeHeaderData header = new VolumeHeaderData(file);
		byte[] buffer = header.getSuitableBuffer();

		int zVol = getDataVolumeNumberZ(header, pos);// Volume for slice

		int zPos = pos % header.getVolumeSize(); // Position with the slice
		int xP;
		int yP;

		if (status != null)
		{
			status.setMaximum(header.getNumVolX());
		}
		for (int xVol = 0; xVol < header.getNumVolX(); xVol++)
		{
			if (status != null)
			{
				status.setValue(xVol);
			}
			for (int yVol = 0; yVol < header.getNumVolY(); yVol++)
			{
				// Get the volume
				getVolumeData(header, xVol, yVol, zVol, buffer);

				for (int y = 0; y < header.getVolumeSize(); y++)
				{

					for (int x = 0; x < header.getVolumeSize(); x++)
					{
						xP = xVol * header.getVolumeSize() + x;
						yP = yVol * header.getVolumeSize() + y;

						if (xP < header.getSizeDataX()
								&& yP < header.getSizeDataY())
						{
							rst
									.setRGB(xP, yP, getByteToRGB(getByteDataFromVolume(buffer, header
											.getVolumeSize(), x, y, zPos)));
						}

					}
				}
			}
		}
	}

	/**
	 * This will return a given slice in the Y
	 * 
	 * @param file
	 * @param pos
	 * @param rst
	 * @throws IOException
	 */
	public static void getYSlice(RandomAccessFile file, int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		// status.setStatusMessage("Generating Slice");
		VolumeHeaderData header = new VolumeHeaderData(file);
		byte[] buffer = header.getSuitableBuffer();

		int yVol = getDataVolumeNumberY(header, pos);// Volume for slice

		int yPos = pos % header.getVolumeSize(); // Position with the slice

		int xP;
		int zP;

		if (status != null)
		{
			status.setMaximum(header.getNumVolX());
		}
		for (int xVol = 0; xVol < header.getNumVolX(); xVol++)
		{
			if (status != null)
			{
				status.setValue(xVol);
			}
			for (int zVol = 0; zVol < header.getNumVolZ(); zVol++)
			{
				// Get the volume
				getVolumeData(header, xVol, yVol, zVol, buffer);

				for (int z = 0; z < header.getVolumeSize(); z++)
				{

					for (int x = 0; x < header.getVolumeSize(); x++)
					{

						xP = xVol * header.getVolumeSize() + x;
						zP = zVol * header.getVolumeSize() + z;

						if (xP < header.getSizeDataX()
								&& zP < header.getSizeDataZ())
						{
							rst
									.setRGB(xP, zP, getByteToRGB(getByteDataFromVolume(buffer, header
											.getVolumeSize(), x, yPos, z)));
						}

					}
				}
			}
		}
	}

	/**
	 * This will return a given slice in the X
	 * 
	 * @param file
	 * @param pos
	 * @param rst
	 * @throws IOException
	 */
	public static void getXSlice(RandomAccessFile file, int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		// status.setStatusMessage("Generating Slice");

		VolumeHeaderData header = new VolumeHeaderData(file);
		byte[] buffer = header.getSuitableBuffer();

		int xVol = getDataVolumeNumberX(header, pos);// Volume for slice
		int xPos = pos % header.getVolumeSize(); // Position with the slice

		int yP;
		int zP;
		if (status != null)
		{
			status.setMaximum(header.getNumVolZ());
		}
		for (int zVol = 0; zVol < header.getNumVolZ(); zVol++)
		{
			if (status != null)
			{
				status.setValue(zVol);
			}
			for (int yVol = 0; yVol < header.getNumVolY(); yVol++)
			{
				// Get the volume
				getVolumeData(header, xVol, yVol, zVol, buffer);

				// System.out.println();
				// System.out.println(zVol+" , "+yVol);
				for (int y = 0; y < header.getVolumeSize(); y++)
				{

					for (int z = 0; z < header.getVolumeSize(); z++)
					{

						yP = yVol * header.getVolumeSize() + y;
						zP = zVol * header.getVolumeSize() + z;

						if (zP < header.getSizeDataZ()
								&& yP < header.getSizeDataY())
						{
							rst
									.setRGB(zP, yP, getByteToRGB(getByteDataFromVolume(buffer, header
											.getVolumeSize(), xPos, y, z)));

						} else
						{

						}

					}

				}
			}

		}

	}

	/**
	 * This function will work out what volume slice a given data piont is in
	 * 
	 * @param value
	 * @return
	 */
	public static int getDataVolumeNumberX(VolumeHeaderData header, int value)
	{
		return value / header.getVolumeSize();
	}

	/**
	 * This function will get the position offset that a data point will be in a
	 * volume.
	 * 
	 * @param header
	 * @param pos
	 * @return
	 */
	public static int getPosInVolumeX(VolumeHeaderData header, int pos)
	{
		int seg = getDataVolumeNumberX(header, pos);
		return pos - seg * header.getVolumeSize();
	}

	public static int getPosInVolumeY(VolumeHeaderData header, int pos)
	{
		int seg = getDataVolumeNumberY(header, pos);
		return pos - seg * header.getVolumeSize();
	}

	public static int getPosInVolumeZ(VolumeHeaderData header, int pos)
	{
		int seg = getDataVolumeNumberZ(header, pos);
		return pos - seg * header.getVolumeSize();
	}

	public static int getDataVolumeNumberY(VolumeHeaderData header, int value)
	{
		return value / header.getVolumeSize();
	}

	public static int getDataVolumeNumberZ(VolumeHeaderData header, int value)
	{
		return value / header.getVolumeSize();
	}

	public static Dimension getXSliceSize(VolumeHeaderData data)
	{
		return new Dimension(data.getSizeDataZ(), data.getSizeDataY());
	}

	public static Dimension getYSliceSize(VolumeHeaderData data)
	{
		return new Dimension(data.getSizeDataX(), data.getSizeDataZ());
	}

	public static Dimension getZSliceSize(VolumeHeaderData data)
	{
		return new Dimension(data.getSizeDataX(), data.getSizeDataY());
	}

	/**
	 * This return the offset for a header file. This is implemented for future
	 * version of the software for backward compatiability
	 * 
	 * @param version
	 * @return
	 */
	public static long getHeaderOffset(VolumeHeaderData data)
	{
		switch (data.getVersion())
		{
			case VERSION_1:
				return 8 * 4;

		}
		throw new InvalidParameterException("Unknow Version");
	}

	public static BufferedImage[] generateVolumeImages(RandomAccessFile file, int xNum, int yNum, int zNum)
			throws Throwable
	{

		VolumeHeaderData header = new VolumeHeaderData(file);
		BufferedImage[] result = new BufferedImage[header.getVolumeSize()];
		byte[] buffer = header.getSuitableBuffer();
		getVolumeData(header, xNum, yNum, zNum, buffer);
		for (int z = 0; z < header.getVolumeSize(); z++)
		{
			result[z] = ImageOperations.getBi(header.getVolumeSize());
			for (int x = 0; x < header.getVolumeSize(); x++)
			{
				for (int y = 0; y < header.getVolumeSize(); y++)
				{
					result[z]
							.setRGB(x, y, getByteToRGB(getByteDataFromVolume(buffer, header
									.getVolumeSize(), x, y, z)));
				}
			}
		}

		return result;
	}

	/**
	 * This will load a volume of data from a given Volume file. The output will
	 * be stored in the format byte[x][y][z]
	 */
	public static void getVolumeRegionData(VolumeHeaderData header, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, byte[][][] output)
	{
		getVolumeRegionData(header, xMin, yMin, zMin, xMax, yMax, zMax, output, null);
	}

	public static void getVolumeRegionData(VolumeHeaderData header, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, byte[][][] output, StatusBarPanel status)
	{
		byte[] buffer = header.getSuitableBuffer();

		int numX = output.length;
		int numY = output[0].length;
		int numZ = output[0][0].length;

		double xStep = (xMax - xMin) / (double) numX;
		double yStep = (yMax - yMin) / (double) numY;
		double zStep = (zMax - zMin) / numZ;

		// Min Volume Index
		int volMinX = getDataVolumeNumberX(header, xMin);
		int volMinY = getDataVolumeNumberY(header, yMin);
		int volMinZ = getDataVolumeNumberZ(header, zMin);

		// Max volume index
		int volMaxX = getDataVolumeNumberX(header, (int) (xMin + numX * xStep));
		int volMaxY = getDataVolumeNumberY(header, (int) (yMin + numY * yStep));
		int volMaxZ = getDataVolumeNumberZ(header, (int) (zMin + numZ * zStep));

		double outX = 0;
		double outY = 0;
		double outZ = 0;

		int startX = 0;
		int startY = 0;
		int startZ = 0;

		boolean reloadVolume = true;

		int statusCount = 0;
		if (status != null)
		{
			status.setMaximum((volMaxX - volMinX) * (volMaxY - volMinY));
		}
		for (int vX = volMinX; vX <= volMaxX; vX++)
		{

			if (vX == volMinX)
			{
				startX = getPosInVolumeX(header, xMin);
			} else
			{
				startX = 0;
			}

			for (int vY = volMinY; vY <= volMaxY; vY++)
			{
				if (status != null)
				{
					status.setValue(statusCount++);
				}
				if (vY == volMinY)
				{
					startY = getPosInVolumeY(header, yMin);
				} else
				{
					startY = 0;
				}

				for (int vZ = volMinZ; vZ <= volMaxZ; vZ++)
				{
					if (vZ == volMinZ)
					{
						startZ = getPosInVolumeZ(header, zMin);
					} else
					{
						startZ = 0;
					}
					try
					{
						reloadVolume = true;

						// Process to Output Data
						if (vZ == volMinZ)
						{
							outZ = (vZ - volMinZ) * header.getVolumeSize();
						} else
						{
							outZ = (vZ) * header.getVolumeSize() - zMin;
						}
						outZ = outZ / zStep;

						for (double z = startZ; z < header.getVolumeSize()
								&& outZ < numZ; z += zStep)
						{
							if (vY == volMinY)
							{
								outY = (vY - volMinY) * header.getVolumeSize();
							} else
							{
								outY = (vY) * header.getVolumeSize() - yMin;
							}
							outY = outY / yStep;

							for (double y = startY; y < header.getVolumeSize()
									&& outY < numY; y += yStep)
							{
								// Set X \Out Range
								if (vX == volMinX)
								{
									outX = (vX - volMinX)
											* header.getVolumeSize();
								} else
								{
									outX = (vX) * header.getVolumeSize() - xMin;
								}
								outX = outX / xStep;

								// Load Data
								for (double x = startX; x < header
										.getVolumeSize()
										&& outX < numX; x += xStep)
								{
									if (reloadVolume)
									{
										// Load Volume Data
										getVolumeData(header, vX, vY, vZ, buffer);
										reloadVolume = false;
									}
									// System.out.printf("Vol[%d,%d,%d]
									// out[%d,%d,%d] Data[%d,%d,%d]\n",
									// vX,vY,vZ, (int)outX, (int)outY,
									// (int)outZ, (int)x,(int)y,(int)z);
									output[(int) outX][(int) outY][(int) outZ] = getByteDataFromVolume(buffer, header
											.getVolumeSize(), (int) x, (int) y, (int) z);
									outX++;
								}
								outY++;
							}
							outZ++;
						}
					} catch (IOException e)
					{
						System.out
								.println("There was an error reading from the full volume data");
						e.printStackTrace();
					}
				}
			}
		}

	}

	public static byte[][][] readFullData(VolumeHeaderData header, StatusBarPanel status)
	{
		byte[][][] result;
		try
		{
			result = new byte[header.getSizeDataX()][header.getSizeDataY()][header
					.getSizeDataZ()];
		} catch (OutOfMemoryError e)
		{
			JOptionPane
					.showMessageDialog(null, "There was not enough Memory to load the data, \nPlease consult the user manual for methods to overcome this");
			return new byte[][][]
			{
			{
			{ (byte) 1 } } };
		}
		return null;
	}

	public static void main(String[] input) throws FileNotFoundException,
			IOException
	{
		VolumeHeaderData header = new VolumeHeaderData(new RandomAccessFile(
				"c:\\test\\raw.dat", "r"));

		BufferedImage image = new BufferedImage(getXSliceSize(header).width,
				getXSliceSize(header).height, BufferedImage.TYPE_INT_RGB);

		getXSlice(header.file, 10, image, new StatusBarPanel());
		FrameFactroy.getFrame(image);

	}

	/**
	 * This will get the offset for any given volume data set location
	 * 
	 * @param data
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static long getVolumeOffset(VolumeHeaderData data, int x, int y, int z)
	{
		long volumeSize = data.getVolumeSize() * data.getVolumeSize()
				* data.getVolumeSize();
		long offset = (x + y * data.getNumVolX() + z * data.getNumVolX()
				* data.getNumVolY())
				* volumeSize;
		return offset + getHeaderOffset(data);
	}

	/**
	 * This function will write a series of image files into volume data file.
	 * The data is stores as series of sub volumes. Each volume will have a size
	 * of bufferSize X bufferSize X bufferSize.
	 * 
	 * When processing the images if the Volume goes outside the data set the
	 * data is just set to 0. i.e if you buffer was 100X100X100,and your image
	 * set is 200X200X10, all data points will be added to time image but data
	 * outside the point is compleley removed.
	 * 
	 * @param outputFile
	 *            - This is the file where the volume sets will be stored in.
	 * @param imageFiles
	 *            - The source files for the volume data.
	 * @param bufferSize
	 *            - The size of the buffer to use.
	 * @throws IOException
	 *             - Thrown if there is a problem with reading or writing the
	 *             files
	 */
	public static void writeVolumeDataToFile(File outputFile, File[] imageFiles, int bufferSize, StatusBarPanel status)
			throws IOException
	{
		ImageFileProducer prod = new ImageFileProducer(imageFiles);
		writeVolumeDataToFile(outputFile, prod, bufferSize, status);
	}

	public static void writeVolumeDataToFile(File outputFile, ImageProducer imageData, int bufferSize, StatusBarPanel status)
			throws IOException
	{

		status.setStatusMessage("Creating Data File");
		status.setValue(0);
		status.setMaximum(imageData.getImageCount());
		status.repaint();

		BufferedImage[] imageBuffer = new BufferedImage[bufferSize];
		byte[] buffer = new byte[bufferSize * bufferSize * bufferSize];
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(outputFile)));

		/**
		 * Write Header information
		 */

		// Do Calculations
		BufferedImage tmpImage = imageData.getImage(0);
		int dataX = tmpImage.getWidth();
		int dataY = tmpImage.getHeight();
		int dataZ = imageData.getImageCount();

		int volX = (int) Math.ceil((double) dataX / (double) bufferSize);
		int volY = (int) Math.ceil((double) dataY / (double) bufferSize);
		int volZ = (int) Math.ceil((double) dataZ / (double) bufferSize);

		// Write Data
		out.writeInt(VERSION); // Version
		out.writeInt(bufferSize); // Size of buffer
		out.writeInt(volX);
		out.writeInt(volY);
		out.writeInt(volZ);
		out.writeInt(dataX);
		out.writeInt(dataY);
		out.writeInt(dataZ);

		/**
		 * Write the volume Data
		 */
		int count = bufferSize;

		for (int i = 0; i < imageData.getImageCount(); i++)
		{
			System.gc();
			System.out.println("ImageSliceToolkit - writeVolumeDataToFile"+i);
			// for (File file : imageFiles)
			// {
			status.setValue(status.getValue() + 1);
			status.repaint();
			// Add images to the buffer
			int pos = bufferSize - count--;
			
			// imageBuffer[pos] = ImageIO.read(file);
			System.out.println("ImageSliceToolkit - starting Get Image");
			imageBuffer[pos] = imageData.getImage(i);
			System.out.println("ImageSliceToolkit - Loaded");
			if (i == imageData.getImageCount() - 1)
			{
				while (count > 0)
				{
					pos = bufferSize - count--;
					imageBuffer[pos] = ImageOperations.getBi(dataX, dataY);// imageData.getImage(i);
				}
			}
			// When buffer is full output the raw data to file
			if (count == 0)
			{
				System.out.println("Writing Data");
				// Step over the data to get a series of volumes
				for (int z = 0; z < imageBuffer.length; z += bufferSize)
				{
					for (int y = 0; y < imageBuffer[z].getHeight(); y += bufferSize)
					{
						for (int x = 0; x < imageBuffer[z].getWidth(); x += bufferSize)
						{
							// Read the volume data into the buffer and write it
							// to the file
							generateVolumeData(imageBuffer, z, x, y, bufferSize, buffer);
							out.write(buffer);
						}
					}
				}
				// Reset the counter
				count = bufferSize;
			}
		}
		// Flush and close output file
		out.flush();
		out.close();
		status.setStatusMessage("Data File Sucessfuly Created");
		status.repaint();
	}

	/**
	 * This function will extract a volume data set from a raw data file. The X
	 * Y Z represents the index of the volume set.
	 * 
	 * @param file
	 * @param x
	 * @param y
	 * @param z
	 * @param dst
	 * @throws IOException
	 * @@Deprecated use getVolData(VolumeHeaderData, x, y, z, dst)...
	 */
	public static void getVolumeData(RandomAccessFile file, int x, int y, int z, byte[] dst)
			throws IOException
	{
		VolumeHeaderData headerData = new VolumeHeaderData(file);
		long offset = getVolumeOffset(headerData, x, y, z);

		file.seek(offset);
		file.read(dst);
	}

	public static void getVolumeData(VolumeHeaderData header, int x, int y, int z, byte[] dst)
			throws IOException
	{
		long offset = getVolumeOffset(header, x, y, z);
		header.file.seek(offset);
		header.file.read(dst);
	}

	/**
	 * This function will crop out a volume of a given size from an image
	 * series. The volume data is stored as a 1D array of bytes[].
	 * 
	 * If the data is a 3D array of Pxls[X][Y][z] where (x,y) -> pixel in image
	 * z. The data is returned as a linerar array that has been generated in the
	 * form for Z, for Y, for X } } }
	 * 
	 * 
	 * 
	 * @param imgs
	 * @param imgIndex
	 * @param posX
	 * @param posY
	 * @param size
	 * @param dst
	 */
	public static void generateVolumeData(BufferedImage[] imgs, int imgIndex, int posX, int posY, int size, byte[] dst)
	{
		if (dst.length < size * size * size)
		{
			throw new InvalidParameterException(
					"Destation Buffer is not large enough");
		}

		int count = 0;
		for (int z = 0; z < size; z++)
		{
			for (int y = 0; y < size; y++)
			{
				for (int x = 0; x < size; x++)
				{
					// Make sure that there is data at the given point
					if (z >= imgs.length
							|| y + posY >= imgs[z + imgIndex].getHeight()
							|| x + posX >= imgs[z + imgIndex].getWidth())
					{// if not set 0
						dst[count++] = 0;
					} else
					{// if there is set point
						dst[count++] = getRGBToByte(imgs[z + imgIndex].getRGB(x
								+ posX, y + posY));
					}
				}
			}
		}
	}

	/**
	 * This will extract a specific byte at location (x,y,z) from with a volume
	 * data set given by a byte[]
	 * 
	 * i.e byte[x][y][z] = getByteDataFromVolume(data[],size, x, y, z) where
	 * data -> 1D array
	 * 
	 * @param data
	 * @param size
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static byte getByteDataFromVolume(byte[] data, int size, int x, int y, int z)
	{
		return data[x + y * size + z * size * size];
	}

	public static byte getRGBToByte(int rgb)
	{
		return NativeDataSet.getRGBtoByte(rgb);
	}

	public static int getByteToRGB(byte b)
	{
		return NativeDataSet.getByteToRGB(b);
	}
}
