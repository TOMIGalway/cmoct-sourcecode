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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;

import javax.swing.JOptionPane;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.mathsToolkit.NumberDimension;
import com.joey.software.userinterface.VersionManager;


public class NativeDataSet implements Externalizable
{
	private static final long serialVersionUID = VersionManager.VERSION_1;

	public static final int X_SLICE = 0;

	public static final int Y_SLICE = 1;

	public static final int Z_SLICE = 2;

	static int[] BYTE_TO_RGB = getByteToRGBData();

	File previewFile = null;

	File rawFile = null;

	RandomAccessFile rawData = null;

	VolumeHeaderData header = null;

	byte[][][] previewData = null;

	double previewScaleX = 1;

	double previewScaleY = 1;

	double previewScaleZ = 1;

	int previewSizeX = 0;

	int previewSizeY = 0;

	int previewSizeZ = 0;

	boolean loaded = true;

	double sizeDataX = 1;

	double sizeDataY = 1;

	double sizeDataZ = 1;

	String info = "";

	boolean loadFullDataAsPreview = false;

	/**
	 * These powers are just for use with a NumberDimensino panel!
	 * 
	 * These power prevexes are not normaly saved in the serilaziable due to
	 * back ward compatability.
	 * 
	 * To overcome this, when required manualy save after!
	 */
	int powerX = NumberDimension.POWER_UNITY;

	int powerY = NumberDimension.POWER_UNITY;

	int powerZ = NumberDimension.POWER_UNITY;

	boolean allowFullResolutin = true;

	/**
	 * This variable is used to prevent the data from fully loading the raw data
	 * when loading the data from the file.
	 * 
	 * It should never be set to true!!
	 */
	boolean blockPreviewLoad = false;

	public static void main(String input[]) throws IOException
	{
		File dataFile = new File("c:\\test\\file_raw.dat");
		File previewFile = new File("c:\\test\\file_prev.dat");

		NativeDataSet data = new NativeDataSet(dataFile, previewFile);

		BufferedImage test = ImageOperations.getBi(400, 400);

		data.getPreviewScaledSlice(0, 0, test);

		FrameFactroy.getFrame(test);
	}

	public NativeDataSet() throws IOException
	{
		this(null, null);
		previewData = new byte[][][]
		{
		{
		{ (byte) 1 } } };
	}

	public NativeDataSet(File dataFile, File previewFile) throws IOException
	{
		this(dataFile, previewFile, new StatusBarPanel());
	}

	public NativeDataSet(File dataFile, File previewFile, StatusBarPanel status)
			throws IOException
	{
		this(dataFile, previewFile, status, true);
	}

	public NativeDataSet(File dataFile, File previewFile, StatusBarPanel status, boolean blockLoad)
			throws IOException
	{
		if (blockLoad = true)
		{
			blockPreviewLoad = true;
		}
		if (!(dataFile == null && previewFile == null))
		{
			loadData(dataFile, previewFile, status);
		}
		blockPreviewLoad = false;
	}

	public static int[] getByteToRGBData()
	{
		int[] data = new int[256];
		for (int i = 0; i < 256; i++)
		{
			data[i] = new Color(i, i, i).getRGB();
		}
		return data;
	}

	public static int getByteToRGB(byte b)
	{
		return BYTE_TO_RGB[(b < 0 ? 256 + b : b)];
	}

	public static byte getRGBtoByte(int val)
	{
		Color c = new Color(val);
		val = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
		return (byte) (val);
	}

	public static int getByteToInt(byte b)
	{
		return (b < 0 ? 256 + b : b);
	}

	public void unloadData()
	{
		loaded = false;
		previewData = new byte[][][]
		{
		{
		{ (byte) 1 } } };
	}

	public void reloadData()
	{
		reloadData(new StatusBarPanel());
	}

	public void reloadData(StatusBarPanel status)
	{
		loaded = true;
		if (!(rawData == null && previewFile == null))
		{
			try
			{
				loadData(rawFile, previewFile, status);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This will set the data in rst to the nearest (rounded down always)
	 * position available in the preview data.
	 * 
	 * rst must be the correct size to fit all the data
	 * 
	 * @param axes
	 * @param pos
	 * @param rst
	 */
	public void getPreviewSlice(int axes, int pos, BufferedImage rst)
	{
		if (previewData == null)
		{
			return;
		}
		if (axes == X_SLICE)
		{
			getPreviewSliceX(pos, rst);
		} else if (axes == Y_SLICE)
		{
			getPreviewSliceY(pos, rst);
		} else if (axes == Z_SLICE)
		{
			getPreviewSliceZ(pos, rst);
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public void getPreviewSliceX(int pos, BufferedImage rst)
	{
		if (previewData == null)
		{
			return;
		}
		int x = (int) (pos * getPreviewScaleX());
		int y = 0;
		int z = 0;

		for (y = 0; y < getPreviewSizeY(); y++)
		{
			for (z = 0; z < getPreviewSizeZ(); z++)
			{
				rst.setRGB(z, y, getByteToRGB(previewData[x][y][z]));
			}

		}

	}

	public void getPreviewSliceY(int pos, BufferedImage rst)
	{
		if (previewData == null)
		{
			return;
		}
		int x = 0;
		int y = (int) (pos * getPreviewScaleY());
		int z = 0;
		for (x = 0; x < getPreviewSizeX(); x++)
		{
			for (z = 0; z < getPreviewSizeZ(); z++)
			{
				rst.setRGB(x, z, getByteToRGB(previewData[x][y][z]));
			}

		}
	}

	public void unBlockLoad()
	{
		blockPreviewLoad = false;
	}

	private void loadData(File dataFile, File previewFile, StatusBarPanel status)
			throws IOException
	{
		rawFile = dataFile;
		this.previewFile = previewFile;

		if (dataFile == null || previewFile == null)
		{
			return;
		}

		if (!dataFile.exists() && !previewFile.exists())
		{
			JOptionPane
					.showMessageDialog(null, "Could not find the Data Files\n Raw : "
							+ dataFile.toString()
							+ "\n Prv :"
							+ previewFile.toString(), "Error Loading DataSet", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!dataFile.exists())
		{
			JOptionPane
					.showMessageDialog(null, "Could not find the Raw Data File\n File : "
							+ dataFile.toString(), "Error Reading File", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!previewFile.exists())
		{
			JOptionPane
					.showMessageDialog(null, "Could not find the Preview Data File\n File :"
							+ previewFile.toString(), "Error Reading File", JOptionPane.ERROR_MESSAGE);
			return;
		}

		RandomAccessFile data = new RandomAccessFile(dataFile, "r");
		header = new VolumeHeaderData(data);
		/*
		 * This block has been added in an attempt to speed up the time it takes
		 * to load a series of files off the dis.
		 */

		System.gc();
		if (!blockPreviewLoad)
		{
			previewData = new byte[1][1][1];
			if (loadFullDataAsPreview)
			{
				setData(data, OCTDataGeneratingTool
						.readFullData(header, status));
			} else
			{
				setData(data, OCTDataGeneratingTool
						.readPreviewData(previewFile, status));
			}
		} else
		{
			setData(data, new byte[1][1][1]);
		}

	}

	public void getPreviewSliceZ(int pos, BufferedImage rst)
	{
		if (previewData == null)
		{
			return;
		}
		int x = 0;
		int y = 0;
		int z = (int) (pos * getPreviewScaleZ());
		for (x = 0; x < getPreviewSizeX(); x++)
		{
			for (y = 0; y < getPreviewSizeY(); y++)
			{
				rst.setRGB(x, y, getByteToRGB(previewData[x][y][z]));
			}
		}
	}

	public double getPreviewScaledSlice(int axes, int pos, BufferedImage rst)
	{
		if (previewData == null)
		{
			return 1;
		}
		if (axes == X_SLICE)
		{
			return getPreviewScaledSliceX(pos, rst);
		} else if (axes == Y_SLICE)
		{
			return getPreviewScaledSliceY(pos, rst);
		} else if (axes == Z_SLICE)
		{
			return getPreviewScaledSliceZ(pos, rst);
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public double getPreviewScaledSliceX(int pos, BufferedImage rst)
	{
		if (previewData == null || loaded == false)
		{
			return 1;
		}
		int outWide = rst.getWidth();
		int outHigh = rst.getHeight();

		double dataWide = getPreviewSizeZ();
		double dataHigh = getPreviewSizeY();

		/*
		 * Determine the scale to use
		 */
		double scale = DataAnalysisToolkit.getMind(outWide / dataWide, outHigh
				/ dataHigh);

		int val = 0;
		for (int x = 0; x < outWide && (int) (x / scale) < dataWide; x++)
		{
			for (int y = 0; y < outHigh && (int) (y / scale) < dataHigh; y++)
			{

				try
				{
					byte b = previewData[(int) (pos / getPreviewScaleX())][(int) (y / scale)][(int) (x / scale)];
					val = getByteToRGB(b);
					rst.setRGB(x, y, val);
				} catch (Exception e)
				{
					System.out
							.println("Error : OCTDataset image - Out of bounds [Not too serious]");
					System.out
							.printf("Slice X -\nOutput :[%d,%d]\nPos : [%d,%d]\nVol [%d,%d,%d] \n", outWide, outHigh, x, y, getPreviewSizeX(), getPreviewSizeY(), getPreviewSizeZ());
					System.out
							.printf("Trying [%d,%d,%d]\n", (int) (pos / getPreviewScaleX()), (int) (y / scale), (int) (x / scale));
				}
			}
		}

		return scale;

	}

	public boolean isAllowFullResolution()
	{
		return allowFullResolutin;
	}

	public double getPreviewScaledSliceY(int pos, BufferedImage rst)
	{
		if (previewData == null || loaded == false)
		{
			return 1;
		}
		int outWide = rst.getWidth();
		int outHigh = rst.getHeight();

		double dataWide = getPreviewSizeX();
		double dataHigh = getPreviewSizeZ();

		/*
		 * Determine the scale to use
		 */
		double scale = DataAnalysisToolkit.getMind(outWide / dataWide, outHigh
				/ dataHigh);

		int val = 0;
		for (int x = 0; x < outWide && (int) (x / scale) < dataWide; x++)
		{
			for (int y = 0; y < outHigh && (int) (y / scale) < dataHigh; y++)
			{
				try
				{
					byte b = previewData[(int) (x / scale)][(int) (pos / getPreviewScaleY())][(int) (y / scale)];
					val = getByteToRGB(b);
					rst.setRGB(x, y, val);
				} catch (Exception e)
				{
					System.out
							.println("Error : OCTDataset image - Out of bounds [Not too serious]");
					System.out
							.printf("Slice Y -\nOutput :[%d,%d]\nPos : [%d,%d]\nVol [%d,%d,%d] \n", outWide, outHigh, x, y, getPreviewSizeX(), getPreviewSizeY(), getPreviewSizeZ());
					System.out
							.printf("Trying [%d,%d,%d]\n", (int) (x / scale), (int) (pos / getPreviewScaleY()), (int) (y / scale));
				}
			}
		}

		return scale;
	}

	public double getPreviewScaledSliceZ(int pos, BufferedImage rst)
	{
		if (previewData == null || loaded == false)
		{
			return 1;
		}
		int outWide = rst.getWidth();
		int outHigh = rst.getHeight();

		double dataWide = getPreviewSizeX();
		double dataHigh = getPreviewSizeY();

		/*
		 * Determine the scale to use
		 */
		double scale = DataAnalysisToolkit.getMind(outWide / dataWide, outHigh
				/ dataHigh);

		int val = 0;
		for (int x = 0; x < outWide && (int) (x / scale) < dataWide; x++)
		{
			for (int y = 0; y < outHigh && (int) (y / scale) < dataHigh; y++)
			{
				try
				{
					byte b = previewData[(int) (x / scale)][(int) (y / scale)][(int) (pos / getPreviewScaleZ())];
					val = getByteToRGB(b);
					rst.setRGB(x, y, val);
				} catch (Exception e)
				{
					System.out
							.println("Error : OCTDataset image - Out of bounds [Not too serious]");
					System.out
							.printf("Slice Z -\nOutput :[%d,%d]\nPos : [%d,%d]\nVol [%d,%d,%d] \n", outWide, outHigh, x, y, getPreviewSizeX(), getPreviewSizeY(), getPreviewSizeZ());
					System.out
							.printf("Trying [%d,%d,%d]\n", (int) (x / scale), (int) (y / scale), (int) (pos / getPreviewScaleZ()));

				}
			}
		}

		return scale;
	}

	public int getPreviewSizeX()
	{
		if (loaded == false)
		{
			return 1;
		}
		return previewSizeX;
	}

	public int getPreviewSizeY()
	{
		if (loaded == false)
		{
			return 1;
		}

		return previewSizeY;
	}

	public int getPreviewSizeZ()
	{
		if (loaded == false)
		{
			return 1;
		}

		return previewSizeZ;
	}

	public int getRenderSizeX()
	{
		if (header == null || loaded == false)
		{
			return 1;
		}
		return header.getSizeDataX();
	}

	public int getRenderSizeY()
	{
		if (header == null || loaded == false)
		{
			return 1;
		}
		return header.getSizeDataY();
	}

	public int getRenderSizeZ()
	{
		if (header == null || loaded == false)
		{
			return 1;
		}
		return header.getSizeDataZ();
	}

	public double getPreviewScaleX()
	{
		return previewScaleX;
	}

	public double getPreviewScaleY()
	{
		return previewScaleY;
	}

	public double getPreviewScaleZ()
	{
		return previewScaleZ;
	}

	public Dimension getPreviewSliceSize(int axes)
	{
		if (header == null)
		{
			return new Dimension(1, 1);
		}
		if (axes == X_SLICE)
		{
			return getPreviewSliceSizeX();
		} else if (axes == Y_SLICE)
		{
			return getPreviewSliceSizeY();
		} else if (axes == Z_SLICE)
		{
			return getPreviewSliceSizeZ();
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public Dimension getPreviewSliceSizeX()
	{
		return new Dimension(getPreviewSizeZ(), getPreviewSizeY());
	}

	public Dimension getPreviewSliceSizeY()
	{
		return new Dimension(getPreviewSizeX(), getPreviewSizeZ());
	}

	public Dimension getPreviewSliceSizeZ()
	{
		return new Dimension(getPreviewSizeX(), getPreviewSizeY());
	}

	public Dimension getRenderSliceSize(int axes)
	{
		if (axes == X_SLICE)
		{
			return getRenderSliceSizeX();
		} else if (axes == Y_SLICE)
		{
			return getRenderSliceSizeY();
		} else if (axes == Z_SLICE)
		{
			return getRenderSliceSizeZ();
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public void getRenderSlice(int axes, int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		if (axes == X_SLICE)
		{
			getRenderSliceX(pos, rst, status);
		} else if (axes == Y_SLICE)
		{
			getRenderSliceY(pos, rst, status);
		} else if (axes == Z_SLICE)
		{
			getRenderSliceZ(pos, rst, status);
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public void getRenderSliceZ(int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		if (getRawData() == null)
		{
			return;
		}
		ImageSliceToolkit.getZSlice(getRawData(), pos, rst, status);
	}

	public void getRenderSliceX(int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		if (getRawData() == null)
		{
			return;
		}
		ImageSliceToolkit.getXSlice(getRawData(), pos, rst, status);
	}

	public void getRenderSliceY(int pos, BufferedImage rst, StatusBarPanel status)
			throws IOException
	{
		if (getRawData() == null)
		{
			return;
		}
		ImageSliceToolkit.getYSlice(getRawData(), pos, rst, status);
	}

	public Dimension getRenderSliceSizeX()
	{
		return new Dimension(getRenderSizeZ(), getRenderSizeY());
	}

	public Dimension getRenderSliceSizeY()
	{
		return new Dimension(getRenderSizeX(), getRenderSizeZ());
	}

	public Dimension getRenderSliceSizeZ()
	{
		return new Dimension(getRenderSizeX(), getRenderSizeY());
	}

	private void setData(RandomAccessFile file, byte[][][] previewData)
			throws IOException
	{
		System.out.println("NativeDataset:Set data");
		header = new VolumeHeaderData(file);
		rawData = file;

		double scaleX = header.getSizeDataX() / (double) previewData.length;
		double scaleY = header.getSizeDataY() / (double) previewData[0].length;
		double scaleZ = header.getSizeDataZ()
				/ (double) previewData[0][0].length;

		setPreviewData(previewData, scaleX, scaleY, scaleZ);
	}

	protected void setPreviewData(byte[][][] previewData, double scaleX, double scaleY, double scaleZ)
	{
		this.previewData = previewData;
		this.previewScaleX = scaleX;
		this.previewScaleY = scaleY;
		this.previewScaleZ = scaleZ;

		this.previewSizeX = previewData.length;
		this.previewSizeY = previewData[0].length;
		this.previewSizeZ = previewData[0][0].length;

	}

	public int getPreviewSizeData(int axes)
	{

		if (axes == X_SLICE)
		{
			return getPreviewSizeX();
		} else if (axes == Y_SLICE)
		{
			return getPreviewSizeY();
		} else if (axes == Z_SLICE)
		{
			return getPreviewSizeZ();
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public int getSizeData(int axes)
	{

		if (axes == X_SLICE)
		{
			return getSizeDataX();
		} else if (axes == Y_SLICE)
		{
			return getSizeDataY();
		} else if (axes == Z_SLICE)
		{
			return getSizeDataZ();
		} else
			throw new InvalidParameterException("Invalid Slice Axes Selected");
	}

	public int getSizeDataX()
	{
		if (header == null)
		{
			return 1;
		} else
		{
			return header.getSizeDataX();
		}
	}

	public double getPixelSizeX()
	{
		return sizeDataX / getRenderSizeX();
	}

	public double getPreviewPixelSizeX()
	{
		return sizeDataX / getPreviewSizeX();
	}

	public double getPixelSizeY()
	{
		return sizeDataY / getRenderSizeY();
	}

	public double getPreviewPixelSizeY()
	{
		return sizeDataY / getPreviewSizeY();
	}

	public double getPixelSizeZ()
	{
		return sizeDataZ / getRenderSizeZ();
	}

	public double getPreviewPixelSizeZ()
	{
		return sizeDataZ / getPreviewSizeZ();
	}

	public int getSizeDataY()
	{
		if (header == null)
		{
			return 1;
		} else
		{
			return header.getSizeDataY();
		}
	}

	public int getSizeDataZ()
	{
		if (header == null)
		{
		
			return 1;
		} else
		{
			return header.getSizeDataZ();
		}
	}

	public RandomAccessFile getRawData()
	{
		return rawData;
	}

	public VolumeHeaderData getHeader()
	{
		return header;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		File prv = (File) in.readObject();
		File raw = (File) in.readObject();
		sizeDataX = in.readDouble();
		sizeDataY = in.readDouble();
		sizeDataZ = in.readDouble();
		info = in.readUTF();

		blockPreviewLoad = true;
		loadData(raw, prv, new StatusBarPanel());
		blockPreviewLoad = false;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{

		out.writeObject(previewFile);
		out.writeObject(rawFile);
		out.writeDouble(sizeDataX);
		out.writeDouble(sizeDataY);
		out.writeDouble(sizeDataZ);
		out.writeUTF(info);

	}

	/**
	 * this function can be used to reduce the view port of a preview data set.
	 * The data will be stored into output in form [x][y][z]/
	 */
	public byte[][][] getPreviewData()
	{
		return previewData;
	}

	/**
	 * this function can be used to reduce the view port of a preview data set.
	 * The data will be stored into output in form [x][y][z]/
	 * 
	 * @param xMin
	 * @param yMin
	 * @param zMin
	 * @param xStep
	 * @param yStep
	 * @param zStep
	 * @param output
	 */
	public void getReducedPreviewData(int xMin, int yMin, int zMin, double xStep, double yStep, double zStep, byte[][][] output)
	{
		double xOut = 0;
		double yOut = 0;
		double zOut = 0;
		for (double x = xMin; (x < getPreviewSizeX()) && (xOut < output.length); x += xStep, xOut++)
		{
			yOut = 0;
			for (double y = yMin; (y < getPreviewSizeY())
					&& (yOut < output[0].length); y += yStep, yOut++)
			{
				zOut = 0;
				for (double z = zMin; (z < getPreviewSizeZ())
						&& (zOut < output[0][0].length); z += zStep, zOut++)
				{

					output[(int) xOut][(int) yOut][(int) zOut] = previewData[(int) x][(int) y][(int) z];
				}
				zOut = 0;
			}
			yOut = 0;
		}
		xOut = 0;
	}

	public void setPreviewData(byte[][][] previewData)
	{
		this.previewData = previewData;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public void setSizeDataX(double sizeDataX)
	{
		this.sizeDataX = sizeDataX;
	}

	public void setSizeDataY(double sizeDataY)
	{
		this.sizeDataY = sizeDataY;
	}

	public void setSizeDataZ(double sizeDataZ)
	{
		this.sizeDataZ = sizeDataZ;
	}

	public int getPowerX()
	{
		return powerX;
	}

	public void setPowerX(int powerX)
	{
		this.powerX = powerX;
	}

	public int getPowerY()
	{
		return powerY;
	}

	public void setPowerY(int powerY)
	{
		this.powerY = powerY;
	}

	public int getPowerZ()
	{
		return powerZ;
	}

	public void setPowerZ(int powerZ)
	{
		this.powerZ = powerZ;
	}

	public void setLoadFullDataAsPreview(boolean loadFullDataAsPreview)
	{
		this.loadFullDataAsPreview = loadFullDataAsPreview;
	}

}
