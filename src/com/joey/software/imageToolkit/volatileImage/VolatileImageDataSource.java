package com.joey.software.imageToolkit.volatileImage;

import java.awt.image.VolatileImage;

/**
 * This is a interface that can be used for volatile images. It is usefull for
 * storeing the volatile image data for when the content of the volatile image
 * is lost
 * 
 * @author joey.enfield
 * 
 */
public interface VolatileImageDataSource
{
	public abstract VolatileImage getImageData();
}
