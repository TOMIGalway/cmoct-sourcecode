package com.joey.software.lizard.tiff;

import java.awt.Image;
import java.awt.Toolkit;

class JPEGImage extends CodedImage
{
	public JPEGImage(IFD ifd)
	{
		super(ifd);
	}

	/*
	 * public Image getImage() {
	 * 
	 * if (!CanDecodeImage()) return null;
	 * 
	 * Image img = null; if (imageBytes == null && imageStrips != null) { int i,
	 * n, len; for (i=0,len=0; i<imageStrips.length; i++)
	 * len+=imageStrips[i].length; imageBytes = new byte [len]; for (i=0,n=0;
	 * i<imageStrips.length; i++) { System.arraycopy(imageStrips[i], 0,
	 * imageBytes, n, imageStrips[i].length); n += imageStrips[i].length; }
	 * imageStrips = null; } img =
	 * Toolkit.getDefaultToolkit().createImage(imageBytes); return img; }
	 */
	@Override
	public Object getImageProducer()
	{

		if (!CanDecodeImage())
			return null;

		Image img = null;
		if (imageBytes == null && imageStrips != null)
		{
			int i, n, len;
			for (i = 0, len = 0; i < imageStrips.length; i++)
				len += imageStrips[i].length;
			imageBytes = new byte[len];
			for (i = 0, n = 0; i < imageStrips.length; i++)
			{
				System
						.arraycopy(imageStrips[i], 0, imageBytes, n, imageStrips[i].length);
				n += imageStrips[i].length;
			}
			imageStrips = null;
		}

		img = Toolkit.getDefaultToolkit().createImage(imageBytes);

		return img.getSource();
	}
}