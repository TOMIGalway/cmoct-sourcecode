package com.joey.software.lizard.tiff;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

import com.joey.software.lizard.tiff.tag.CompressionType;
import com.joey.software.lizard.tiff.tag.PhotometricType;
import com.joey.software.lizard.tiff.tag.PlanarConfigType;


class CodedImage
{

	public byte imageBytes[];

	public byte imageStrips[][];

	public long compType;

	public int bitsPerSample, samplesPerPixel, extraSamples, photometric;

	public int imageWidth, imageHeight;

	IFD ifd;

	public CodedImage(IFD ifd)
	{
		this.ifd = ifd;

		imageBytes = null;
		imageStrips = null;
		compType = CompressionType.NONE;
		imageWidth = 0;
		imageHeight = 0;
		bitsPerSample = 0;
		// samplesPerPixel=0;
		samplesPerPixel = 1;
		extraSamples = -1;
		photometric = 0;

		// short redcmap[]; /* colormap pallete */
		// short greencmap[];
		// short bluecmap[];

		// TIFFRGBValue* Map; /* sample mapping array */
		// uint32** BWmap; /* black&white map */
		// uint32** PALmap; /* palette image map */
		// TIFFYCbCrToRGB* ycbcr; /* YCbCr conversion state */

		for (int i = 0; i < ifd.count; i++)
		{

			if (ifd.entries[i].tag.equals(Tag.IMAGEWIDTH))
			{
				imageWidth = (int) ifd.entries[i].value;
			} else if (ifd.entries[i].tag.equals(Tag.IMAGELENGTH))
			{
				imageHeight = (int) ifd.entries[i].value;
			} else if (ifd.entries[i].tag.equals(Tag.COMPRESSION))
			{
				compType = ifd.entries[i].value;
			} else if (ifd.entries[i].tag.equals(Tag.BITSPERSAMPLE))
			{
				if (ifd.entries[i].isOffset())
				{
					if (ifd.entries[i].type.isShort())
						bitsPerSample = ((ifd.entries[i].dataArray[0] & 0xff) << 8)
								+ (ifd.entries[i].dataArray[1] & 0xff);
					else
						bitsPerSample = ((ifd.entries[i].dataArray[0] & 0xff) << 24)
								+ ((ifd.entries[i].dataArray[1] & 0xff) << 16)
								+ ((ifd.entries[i].dataArray[2] & 0xff) << 8)
								+ (ifd.entries[i].dataArray[3] & 0xff);
				} else
					bitsPerSample = (int) ifd.entries[i].value;
			} else if (ifd.entries[i].tag.equals(Tag.SAMPLESPERPIXEL))
			{
				samplesPerPixel = (int) ifd.entries[i].value;
			} else if (ifd.entries[i].tag.equals(Tag.EXTRASAMPLES))
			{
				extraSamples = (int) ifd.entries[i].value;
			} else if (ifd.entries[i].tag.equals(Tag.PHOTOMETRIC))
			{
				photometric = (int) ifd.entries[i].value;
			}
		}
	}

	boolean isRaw()
	{
		return (compType == CompressionType.NONE);
	}

	boolean isJPEG()
	{
		return (compType == CompressionType.JPEG || compType == CompressionType.OJPEG);
	}

	boolean isCCITT()
	{
		return (compType == CompressionType.CCITTFAX3
				|| compType == CompressionType.CCITTFAX4
				|| compType == CompressionType.CCITTRLE || compType == CompressionType.CCITTRLEW);
	}

	ColorModel makeColorModel()
	{
		byte[] rLUT, gLUT, bLUT;

		rLUT = new byte[256];
		gLUT = new byte[256];
		bLUT = new byte[256];
		for (int i = 0; i < 256; i++)
		{
			rLUT[i] = (byte) (i & 0xff);
			gLUT[i] = (byte) (i & 0xff);
			bLUT[i] = (byte) (i & 0xff);
		}
		return (new IndexColorModel(8, 256, rLUT, gLUT, bLUT));
	}

	ColorModel makeRGBColorModel()
	{
		byte[] rLUT, gLUT, bLUT;
		byte[] map = ifd.GetEntry(Tag.COLORMAP).dataArray;
		int i, j, n, r, g, b;
		n = map.length / 6; // 2bytes (1 ignored) * 3
		rLUT = new byte[256];
		gLUT = new byte[256];
		bLUT = new byte[256];

		if (ifd.GetEntry(Tag.PHOTOMETRIC).value == 3)
		{ // planar format ie rrrrrrrrrggggggggggbbbbbbbbb

			r = 0;
			g = n * 2;
			b = n * 4;

			System.out.println("----------------- PALETTE ------------------");
			for (i = 0, j = 0; i < n; i++, j += 2)
			{
				rLUT[i] = (byte) (map[j + r] & 0xff);
				gLUT[i] = (byte) (map[j + g] & 0xff);
				bLUT[i] = (byte) (map[j + b] & 0xff);
				System.out.println("#" + i + " = (" + (rLUT[i] & 0xff) + ","
						+ (gLUT[i] & 0xff) + "," + (bLUT[i] & 0xff) + "), ");
			}
		} else
		{ // chunky rgb format ie rgbrgbrgb

			r = 0;
			g = 2;
			b = 4;
			System.out.println("----------------- PALETTE ------------------");
			for (i = 0, j = 0; i < n; i++, j += 6)
			{
				rLUT[i] = (byte) (map[j + r] & 0xff);
				gLUT[i] = (byte) (map[j + g] & 0xff);
				bLUT[i] = (byte) (map[j + b] & 0xff);
				System.out.println("#" + i + " = (" + (rLUT[i] & 0xff) + ","
						+ (gLUT[i] & 0xff) + "," + (bLUT[i] & 0xff) + "), ");
			}
		}

		for (i = n; i < 256; i++)
		{
			rLUT[i] = 0;
			gLUT[i] = 0;
			bLUT[i] = 0;
		}

		return (new IndexColorModel(bitsPerSample, n, rLUT, gLUT, bLUT));
	}

	/*
	 * Macros for extracting components from the packed ABGR form returned by
	 * TIFFReadRGBAImage.
	 */
	int GetR(int abgr)
	{
		return ((abgr) & 0xff);
	}

	int GetG(int abgr)
	{
		return (((abgr) >> 8) & 0xff);
	}

	int GetB(int abgr)
	{
		return (((abgr) >> 16) & 0xff);
	}

	int GetA(int abgr)
	{
		return (((abgr) >> 24) & 0xff);
	}

	public Image getImage()
	{
		Image img = null;
		ImageProducer ip = (ImageProducer) getImageProducer();
		if (ip != null)
		{
			img = Toolkit.getDefaultToolkit().createImage(ip);
		}
		return img;
	}

	public Object getImageProducer()
	{

		ColorModel cm = makeColorModel();
		int[] pixels;
		imageWidth = 256;
		imageHeight = 256;

		pixels = new int[imageWidth * imageHeight];
		for (int y = 0; y < pixels.length; y += imageWidth)
		{
			for (int x = 0; x < imageWidth; x++)
			{
				pixels[x + y] = (x & 0xff);
			}
		}

		return new MemoryImageSource(imageWidth, imageHeight, pixels, 0,
				imageWidth);
	}

	/*
	 * Construct any mapping table used by the associated put routine.
	 */

	/*
	 * Check the image to see if TIFFReadRGBAImage can deal with it. 1/0 is
	 * returned according to whether or not the image can be handled. If 0 is
	 * returned, emsg contains the reason why it is being rejected.
	 */
	boolean CanDecodeImage()
	{
		String emsg;
		int colorChannels;
		int planarConfig;

		switch (bitsPerSample)
		{
			case 1:
			case 2:
			case 4:
			case 8:
			case 16:
				break;
			default:
				emsg = "Sorry, can not handle images with " + bitsPerSample
						+ "-bit samples";
				System.out.println(emsg);
				return (false);
		}

		if (extraSamples == -1) // tag not set in IFD
			colorChannels = samplesPerPixel; // normally would default to 0
		else
			colorChannels = samplesPerPixel - extraSamples;

		System.out.println("samplesPerPixel:" + samplesPerPixel
				+ ", extraSamples:" + extraSamples + ", colorChannels:"
				+ colorChannels + ", photometric:" + photometric);

		if (photometric == 0 && extraSamples != -1)
		{
			switch (colorChannels)
			{
				case 1:
					photometric = PhotometricType.MINISBLACK;
					break;
				case 3:
					photometric = PhotometricType.RGB;
					break;
				default:
					emsg = "Missing needed PHOTOMETRIC tag";
					System.out.println(emsg);
					return (false);
			}
		}

		switch (photometric)
		{
			case PhotometricType.MINISWHITE:
			case PhotometricType.MINISBLACK:
			case PhotometricType.PALETTE:
				planarConfig = ifd.GetFieldValue(Tag.PLANARCONFIG);
				if (planarConfig == PlanarConfigType.CONTIG
						&& samplesPerPixel != 1)
				{
					emsg = "Sorry, can not handle contiguous data with PHOTOMETRIC="
							+ photometric
							+ ", and Samples per Pixel="
							+ samplesPerPixel;
					System.out.println(emsg);
					return (false);
				}
				break;
			case PhotometricType.YCBCR:
				planarConfig = ifd.GetFieldValue(Tag.PLANARCONFIG);
				if (planarConfig != PlanarConfigType.CONTIG)
				{
					emsg = "Sorry, can not handle YCbCr images with Planarconfiguration="
							+ planarConfig;
					System.out.println(emsg);
					return (false);
				}
				break;
			case PhotometricType.RGB:
				if (colorChannels < 3)
				{
					emsg = "Sorry, can not handle RGB image with Color channels="
							+ colorChannels;
					System.out.println(emsg);
					return (false);
				}
				break;
			default:
				emsg = "Sorry, can not handle image with Photometric="
						+ photometric;
				System.out.println(emsg);
				return (false);
		}
		return (true);
	}

}