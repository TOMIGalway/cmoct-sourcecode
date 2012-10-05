package com.joey.software.imageToolkit.volatileImage;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VolatileImageToolkit
{

	/**
	 * Returns a {@link VolatileImage} with a data layout and color model
	 * compatible with this <code>GraphicsConfiguration</code>. The returned
	 * <code>VolatileImage</code> may have data that is stored optimally for the
	 * underlying graphics device and may therefore benefit from
	 * platform-specific rendering acceleration.
	 * 
	 * @param width
	 *            the width of the returned <code>VolatileImage</code>
	 * @param height
	 *            the height of the returned <code>VolatileImage</code>
	 * @return a <code>VolatileImage</code> whose data layout and color model is
	 *         compatible with this <code>GraphicsConfiguration</code>.
	 * @see Component#createVolatileImage(int, int)
	 * @since 1.4
	 */
	public static VolatileImage createVolatileImage(int width, int height, int transparency)
	{
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice()
				.getDefaultConfiguration();
		VolatileImage image = null;

		image = gc.createCompatibleVolatileImage(width, height, transparency);

		int valid = image.validate(gc);

		if (valid == VolatileImage.IMAGE_INCOMPATIBLE)
		{
			image = createVolatileImage(width, height, transparency);
			return image;
		}

		return image;
	}

	/**
	 * This will create a new suitable volatile image for the current graphics
	 * envoirment. the transparancy is set to Transparenty.OPAQUE
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static VolatileImage createVolatileImage(int width, int height)
	{
		return createVolatileImage(width, height, Transparency.OPAQUE);
	}

	public static VolatileImage convert(BufferedImage bimage)
	{
		VolatileImage vimage = createVolatileImage(bimage.getWidth(), bimage
				.getHeight(), Transparency.TRANSLUCENT);
		Graphics2D g = null;

		try
		{
			g = vimage.createGraphics();

			g
					.drawImage(bimage, 0, 0, bimage.getWidth(), bimage
							.getHeight(), null);
		} finally
		{
			// It's always best to dispose of your Graphics objects.
			g.dispose();
		}
		return vimage;
	}

	/**
	 * This will load a bufferedimage from a given file.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static VolatileImage loadImage(File file) throws IOException
	{

		// Loads the image from a file using ImageIO.
		BufferedImage bimage = ImageIO.read(file);
		return convert(bimage);
	}

	/**
	 * This will load an image from the path defined in the passed string.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static VolatileImage loadImage(String fileName) throws IOException
	{
		return loadImage(new File(fileName));
	}
}
