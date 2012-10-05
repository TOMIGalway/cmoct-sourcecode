package com.joey.software.DataToolkit;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageFileLoader;
import com.joey.software.imageToolkit.ImageFileLoaderInterface;
import com.joey.software.imageToolkit.ImageOperations;


public class ImageFileProducer extends ImageProducer
{
	File[] data;

	public ImageFileProducer(File[] data)
	{
		this.data = data;

	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		return ImageIO.read(data[pos]);
	}
	
	

	@Override
	public int getImageCount()
	{

		return data.length;
	}

	public File[] getData()
	{
		return data;
	}

	
	public byte[][][] createDataHolder() throws IOException
	{
		BufferedImage img = getImage(0);
		return new byte[getImageCount()][img.getWidth()][img.getHeight()];
	}
	/**
	 * This will load the frames (gray scale) into
	 * the volume data[frame][wide][high];
	 * 
	 * @param data
	 * @param status
	 */
	public void getData(final byte[][][] data, final StatusBarPanel status)
	{
		if (status != null)
		{
			status.setMaximum(data.length);
			status.setValue(0);
		}
		// TODO Auto-generated method stub
		ImageFileLoaderInterface loader = new ImageFileLoaderInterface()
		{

			@Override
			public void imageLoaded(BufferedImage img, int index)
			{
				if (status != null)
				{
					synchronized (status)
					{
						status.setValue(status.getValue()+1);
					}
				}
				for (int x = 0; x < img.getWidth(); x++)
				{
					for (int y = 0; y < img.getHeight(); y++)
					{
						data[index][x][y] = (byte) ImageOperations
								.getPlaneFromRGBA(img.getRGB(x, y), ImageOperations.PLANE_GRAY);
					}
				}

			}
		};

		ImageFileLoader.loadImageFiles(this.data, loader, 4, true);

	}
}
