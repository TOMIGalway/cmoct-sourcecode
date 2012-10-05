package com.joey.software.DataToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;


public class TiffVolumeProducer
{
	public static void main(String input[]) throws IOException
	{
		File f = new File(
				"C:\\Users\\joey.enfield\\Desktop\\Pat_708_New_BCC Biopsy_ScanA.2_06-Aug-2009_11.15.54_OCT.tif");

		TiffVolumeProducer vol = new TiffVolumeProducer();

		StatusBarPanel stat = new StatusBarPanel();
		FrameFactroy.getFrame(stat);
		byte[][][] data = vol.getData(f, stat);
	}

	public synchronized byte[][][] getData(File tiffFile, StatusBarPanel status)
			throws IOException
	{
		FileLoader load = new FileLoader(tiffFile);

		status.setStatusMessage("Allocating Memory");
		byte[][][] result = new byte[load.getSizeX()][load.getSizeY()][load
				.getSizeZ()];

		status.setStatusMessage("Loading Data");
		status.setMaximum(load.getSizeZ());

		BufferedImage img = load.getNextImage();
		int pos = 0;
		while (img != null)
		{
			status.setValue(pos);
			for (int x = 0; x < load.getSizeX(); x++)
			{
				for (int y = 0; y < load.getSizeY(); y++)
				{
					result[x][y][pos] = NativeDataSet.getRGBtoByte(img
							.getRGB(x, y));
				}
			}
			pos++;
			img = load.getNextImage();
		}

		status.setStatusMessage("Data Loaded");
		return result;
	}
}

class FileLoader implements Runnable
{
	int pos = 0;

	TiffLoader loader;

	boolean first = true;

	Object loadingLock = new Object();

	BufferedImage img;

	int sizeX = 0;

	int sizeY = 0;

	int sizeZ = 0;

	public FileLoader(File f) throws IOException
	{
		loader = new TiffLoader(f);
		img = loader.getImage(pos);

		sizeZ = loader.getImageCount();
		sizeX = img.getWidth();
		sizeY = img.getHeight();
	}

	public BufferedImage getNextImage()
	{
		BufferedImage rst = null;

		// Get next image
		synchronized (loadingLock)
		{
			rst = img;
		}

		pos++;
		if (pos > loader.getImageCount())
		{
			return null;
		}
		Thread t = new Thread(this);
		t.start();
		// Start next Image loading
		return rst;
	}

	public BufferedImage getImage(int pos) throws IOException
	{
		return loader.getImage(pos);
	}

	@Override
	public void run()
	{
		synchronized (loadingLock)
		{
			try
			{
				img = getImage(pos);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public int getSizeY()
	{
		return sizeY;
	}

	public int getSizeZ()
	{
		return sizeZ;
	}
}
