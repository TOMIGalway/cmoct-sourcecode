package com.joey.software.imageToolkit.imageWarping;

import java.awt.image.BufferedImage;

public class ImageDataBuff extends ImageData
{
	BufferedImage srcData = null;

	public ImageDataBuff(BufferedImage img)
	{
		super();
		setImageBufferData(img);
	}

	public void setImageBufferData(BufferedImage img)
	{
		this.srcData = img;
	}

	@Override
	public void loadImageData()
	{

		if ((img == null)
				|| (img.getWidth() != srcData.getWidth() || img.getHeight() != srcData
						.getHeight()))
		{
			img = new BufferedImage(srcData.getWidth(), srcData.getHeight(),
					srcData.getType());
		}

		img.createGraphics().drawImage(srcData, 0, 0, null);
		loaded = true;
	}
}
