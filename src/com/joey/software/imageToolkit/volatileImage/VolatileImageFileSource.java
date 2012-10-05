package com.joey.software.imageToolkit.volatileImage;

import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

public class VolatileImageFileSource implements VolatileImageDataSource
{
	File source;

	public VolatileImageFileSource(File source)
	{
		setSource(source);
	}

	@Override
	public VolatileImage getImageData()
	{
		// TODO Auto-generated method stub
		try
		{
			return VolatileImageToolkit.loadImage(getSource());
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public File getSource()
	{
		return source;
	}

	public void setSource(File source)
	{
		this.source = source;
	}

}
