package com.joey.software.imageToolkit.volatileImage;

import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

public class VolatileImageBufferedSource implements VolatileImageDataSource
{
	BufferedImage image;

	public VolatileImageBufferedSource(BufferedImage img)
	{
		setImage(img);
	}

	@Override
	public VolatileImage getImageData()
	{
		// TODO Auto-generated method stub
		return VolatileImageToolkit.convert(image);
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public void setImage(BufferedImage image)
	{
		this.image = image;
	}

}
