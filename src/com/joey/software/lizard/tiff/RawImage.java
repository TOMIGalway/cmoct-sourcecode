package com.joey.software.lizard.tiff;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import com.joey.software.lizard.tiff.tag.PhotometricType;


class RawImage extends CodedImage
{
	public RawImage(IFD ifd)
	{
		super(ifd);
	}

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

		if (bitsPerSample == 1)
			Convert1To8Bits();
		if (bitsPerSample == 4)
			Convert4To8Bits();

		if (samplesPerPixel == 3)
		{
			int i, j, len = imageBytes.length / 3;
			int imagePixels[] = new int[len];
			for (i = 0, j = 0; i < len; i++, j += 3)
			{
				imagePixels[i] = -16777216
						| ((imageBytes[j] & 255) << 16
								| (imageBytes[j + 1] & 255) << 8 | imageBytes[j + 2] & 255);
			}
			ColorModel cm = new DirectColorModel(24, 16711680, 65280, 255);
			return new MemoryImageSource(imageWidth, imageHeight, cm,
					imagePixels, 0, imageWidth);
		} else if (bitsPerSample == 8)
		{
			ColorModel cm = null;
			if (photometric == PhotometricType.PALETTE)
			{
				cm = makeRGBColorModel();
			} else
				cm = makeColorModel();
			return new MemoryImageSource(imageWidth, imageHeight, cm,
					imageBytes, 0, imageWidth);
		}

		return null;
	}

	public void Convert1To8Bits()
	{
		byte b, w;

		System.out.println("Convert1To8Bits() using photometic:" + photometric);
		if (photometric == PhotometricType.MINISWHITE)
		{
			b = (byte) 0x00;
			w = (byte) 0xff;
		} else
		{
			b = (byte) 0xff;
			w = (byte) 0x00;
		}

		byte table[][] =
		{
		{ w, w, w, w, w, w, w, w },
		{ w, w, w, w, w, w, w, b },
		{ w, w, w, w, w, w, b, w },
		{ w, w, w, w, w, w, b, b }, // 3
				{ w, w, w, w, w, b, w, w },
				{ w, w, w, w, w, b, w, b },
				{ w, w, w, w, w, b, b, w },
				{ w, w, w, w, w, b, b, b }, // 7
				{ w, w, w, w, b, w, w, w },
				{ w, w, w, w, b, w, w, b },
				{ w, w, w, w, b, w, b, w },
				{ w, w, w, w, b, w, b, b }, // 11
				{ w, w, w, w, b, b, w, w },
				{ w, w, w, w, b, b, w, b },
				{ w, w, w, w, b, b, b, w },
				{ w, w, w, w, b, b, b, b }, // 15
				{ w, w, w, b, w, w, w, w },
				{ w, w, w, b, w, w, w, b },
				{ w, w, w, b, w, w, b, w },
				{ w, w, w, b, w, w, b, b }, // 19
				{ w, w, w, b, w, b, w, w },
				{ w, w, w, b, w, b, w, b },
				{ w, w, w, b, w, b, b, w },
				{ w, w, w, b, w, b, b, b }, // 23
				{ w, w, w, b, b, w, w, w },
				{ w, w, w, b, b, w, w, b },
				{ w, w, w, b, b, w, b, w },
				{ w, w, w, b, b, w, b, b }, // 27
				{ w, w, w, b, b, b, w, w },
				{ w, w, w, b, b, b, w, b },
				{ w, w, w, b, b, b, b, w },
				{ w, w, w, b, b, b, b, b }, // 31
				{ w, w, b, w, w, w, w, w },
				{ w, w, b, w, w, w, w, b },
				{ w, w, b, w, w, w, b, w },
				{ w, w, b, w, w, w, b, b }, // 35
				{ w, w, b, w, w, b, w, w },
				{ w, w, b, w, w, b, w, b },
				{ w, w, b, w, w, b, b, w },
				{ w, w, b, w, w, b, b, b }, // 39
				{ w, w, b, w, b, w, w, w },
				{ w, w, b, w, b, w, w, b },
				{ w, w, b, w, b, w, b, w },
				{ w, w, b, w, b, w, b, b }, // 43
				{ w, w, b, w, b, b, w, w },
				{ w, w, b, w, b, b, w, b },
				{ w, w, b, w, b, b, b, w },
				{ w, w, b, w, b, b, b, b }, // 47
				{ w, w, b, b, w, w, w, w },
				{ w, w, b, b, w, w, w, b },
				{ w, w, b, b, w, w, b, w },
				{ w, w, b, b, w, w, b, b }, // 51
				{ w, w, b, b, w, b, w, w },
				{ w, w, b, b, w, b, w, b },
				{ w, w, b, b, w, b, b, w },
				{ w, w, b, b, w, b, b, b }, // 55
				{ w, w, b, b, b, w, w, w },
				{ w, w, b, b, b, w, w, b },
				{ w, w, b, b, b, w, b, w },
				{ w, w, b, b, b, w, b, b }, // 59
				{ w, w, b, b, b, b, w, w },
				{ w, w, b, b, b, b, w, b },
				{ w, w, b, b, b, b, b, w },
				{ w, w, b, b, b, b, b, b }, // 63
				{ w, b, w, w, w, w, w, w },
				{ w, b, w, w, w, w, w, b },
				{ w, b, w, w, w, w, b, w },
				{ w, b, w, w, w, w, b, b }, // 67
				{ w, b, w, w, w, b, w, w },
				{ w, b, w, w, w, b, w, b },
				{ w, b, w, w, w, b, b, w },
				{ w, b, w, w, w, b, b, b }, // 71
				{ w, b, w, w, b, w, w, w },
				{ w, b, w, w, b, w, w, b },
				{ w, b, w, w, b, w, b, w },
				{ w, b, w, w, b, w, b, b }, // 75
				{ w, b, w, w, b, b, w, w },
				{ w, b, w, w, b, b, w, b },
				{ w, b, w, w, b, b, b, w },
				{ w, b, w, w, b, b, b, b }, // 79
				{ w, b, w, b, w, w, w, w },
				{ w, b, w, b, w, w, w, b },
				{ w, b, w, b, w, w, b, w },
				{ w, b, w, b, w, w, b, b }, // 83
				{ w, b, w, b, w, b, w, w },
				{ w, b, w, b, w, b, w, b },
				{ w, b, w, b, w, b, b, w },
				{ w, b, w, b, w, b, b, b }, // 87
				{ w, b, w, b, b, w, w, w },
				{ w, b, w, b, b, w, w, b },
				{ w, b, w, b, b, w, b, w },
				{ w, b, w, b, b, w, b, b }, // 91
				{ w, b, w, b, b, b, w, w },
				{ w, b, w, b, b, b, w, b },
				{ w, b, w, b, b, b, b, w },
				{ w, b, w, b, b, b, b, b }, // 95
				{ w, b, b, w, w, w, w, w },
				{ w, b, b, w, w, w, w, b },
				{ w, b, b, w, w, w, b, w },
				{ w, b, b, w, w, w, b, b }, // 99
				{ w, b, b, w, w, b, w, w },
				{ w, b, b, w, w, b, w, b },
				{ w, b, b, w, w, b, b, w },
				{ w, b, b, w, w, b, b, b }, // 103
				{ w, b, b, w, b, w, w, w },
				{ w, b, b, w, b, w, w, b },
				{ w, b, b, w, b, w, b, w },
				{ w, b, b, w, b, w, b, b }, // 107
				{ w, b, b, w, b, b, w, w },
				{ w, b, b, w, b, b, w, b },
				{ w, b, b, w, b, b, b, w },
				{ w, b, b, w, b, b, b, b }, // 111
				{ w, b, b, b, w, w, w, w },
				{ w, b, b, b, w, w, w, b },
				{ w, b, b, b, w, w, b, w },
				{ w, b, b, b, w, w, b, b }, // 115
				{ w, b, b, b, w, b, w, w },
				{ w, b, b, b, w, b, w, b },
				{ w, b, b, b, w, b, b, w },
				{ w, b, b, b, w, b, b, b }, // 119
				{ w, b, b, b, b, w, w, w },
				{ w, b, b, b, b, w, w, b },
				{ w, b, b, b, b, w, b, w },
				{ w, b, b, b, b, w, b, b }, // 123
				{ w, b, b, b, b, b, w, w },
				{ w, b, b, b, b, b, w, b },
				{ w, b, b, b, b, b, b, w },
				{ w, b, b, b, b, b, b, b }, // 127
				{ b, w, w, w, w, w, w, w },
				{ b, w, w, w, w, w, w, b },
				{ b, w, w, w, w, w, b, w },
				{ b, w, w, w, w, w, b, b }, // 131
				{ b, w, w, w, w, b, w, w },
				{ b, w, w, w, w, b, w, b },
				{ b, w, w, w, w, b, b, w },
				{ b, w, w, w, w, b, b, b }, // 135
				{ b, w, w, w, b, w, w, w },
				{ b, w, w, w, b, w, w, b },
				{ b, w, w, w, b, w, b, w },
				{ b, w, w, w, b, w, b, b }, // 139
				{ b, w, w, w, b, b, w, w },
				{ b, w, w, w, b, b, w, b },
				{ b, w, w, w, b, b, b, w },
				{ b, w, w, w, b, b, b, b }, // 143
				{ b, w, w, b, w, w, w, w },
				{ b, w, w, b, w, w, w, b },
				{ b, w, w, b, w, w, b, w },
				{ b, w, w, b, w, w, b, b }, // 147
				{ b, w, w, b, w, b, w, w },
				{ b, w, w, b, w, b, w, b },
				{ b, w, w, b, w, b, b, w },
				{ b, w, w, b, w, b, b, b }, // 151
				{ b, w, w, b, b, w, w, w },
				{ b, w, w, b, b, w, w, b },
				{ b, w, w, b, b, w, b, w },
				{ b, w, w, b, b, w, b, b }, // 155
				{ b, w, w, b, b, b, w, w },
				{ b, w, w, b, b, b, w, b },
				{ b, w, w, b, b, b, b, w },
				{ b, w, w, b, b, b, b, b }, // 159
				{ b, w, b, w, w, w, w, w },
				{ b, w, b, w, w, w, w, b },
				{ b, w, b, w, w, w, b, w },
				{ b, w, b, w, w, w, b, b }, // 163
				{ b, w, b, w, w, b, w, w },
				{ b, w, b, w, w, b, w, b },
				{ b, w, b, w, w, b, b, w },
				{ b, w, b, w, w, b, b, b }, // 167
				{ b, w, b, w, b, w, w, w },
				{ b, w, b, w, b, w, w, b },
				{ b, w, b, w, b, w, b, w },
				{ b, w, b, w, b, w, b, b }, // 171
				{ b, w, b, w, b, b, w, w },
				{ b, w, b, w, b, b, w, b },
				{ b, w, b, w, b, b, b, w },
				{ b, w, b, w, b, b, b, b }, // 175
				{ b, w, b, b, w, w, w, w },
				{ b, w, b, b, w, w, w, b },
				{ b, w, b, b, w, w, b, w },
				{ b, w, b, b, w, w, b, b }, // 179
				{ b, w, b, b, w, b, w, w },
				{ b, w, b, b, w, b, w, b },
				{ b, w, b, b, w, b, b, w },
				{ b, w, b, b, w, b, b, b }, // 183
				{ b, w, b, b, b, w, w, w },
				{ b, w, b, b, b, w, w, b },
				{ b, w, b, b, b, w, b, w },
				{ b, w, b, b, b, w, b, b }, // 187
				{ b, w, b, b, b, b, w, w },
				{ b, w, b, b, b, b, w, b },
				{ b, w, b, b, b, b, b, w },
				{ b, w, b, b, b, b, b, b }, // 191
				{ b, b, w, w, w, w, w, w },
				{ b, b, w, w, w, w, w, b },
				{ b, b, w, w, w, w, b, w },
				{ b, b, w, w, w, w, b, b }, // 195
				{ b, b, w, w, w, b, w, w },
				{ b, b, w, w, w, b, w, b },
				{ b, b, w, w, w, b, b, w },
				{ b, b, w, w, w, b, b, b }, // 199
				{ b, b, w, w, b, w, w, w },
				{ b, b, w, w, b, w, w, b },
				{ b, b, w, w, b, w, b, w },
				{ b, b, w, w, b, w, b, b }, // 203
				{ b, b, w, w, b, b, w, w },
				{ b, b, w, w, b, b, w, b },
				{ b, b, w, w, b, b, b, w },
				{ b, b, w, w, b, b, b, b }, // 207
				{ b, b, w, b, w, w, w, w },
				{ b, b, w, b, w, w, w, b },
				{ b, b, w, b, w, w, b, w },
				{ b, b, w, b, w, w, b, b }, // 211
				{ b, b, w, b, w, b, w, w },
				{ b, b, w, b, w, b, w, b },
				{ b, b, w, b, w, b, b, w },
				{ b, b, w, b, w, b, b, b }, // 215
				{ b, b, w, b, b, w, w, w },
				{ b, b, w, b, b, w, w, b },
				{ b, b, w, b, b, w, b, w },
				{ b, b, w, b, b, w, b, b }, // 219
				{ b, b, w, b, b, b, w, w },
				{ b, b, w, b, b, b, w, b },
				{ b, b, w, b, b, b, b, w },
				{ b, b, w, b, b, b, b, b }, // 223
				{ b, b, b, w, w, w, w, w },
				{ b, b, b, w, w, w, w, b },
				{ b, b, b, w, w, w, b, w },
				{ b, b, b, w, w, w, b, b }, // 227
				{ b, b, b, w, w, b, w, w },
				{ b, b, b, w, w, b, w, b },
				{ b, b, b, w, w, b, b, w },
				{ b, b, b, w, w, b, b, b }, // 231
				{ b, b, b, w, b, w, w, w },
				{ b, b, b, w, b, w, w, b },
				{ b, b, b, w, b, w, b, w },
				{ b, b, b, w, b, w, b, b }, // 235
				{ b, b, b, w, b, b, w, w },
				{ b, b, b, w, b, b, w, b },
				{ b, b, b, w, b, b, b, w },
				{ b, b, b, w, b, b, b, b }, // 239
				{ b, b, b, b, w, w, w, w },
				{ b, b, b, b, w, w, w, b },
				{ b, b, b, b, w, w, b, w },
				{ b, b, b, b, w, w, b, b }, // 243
				{ b, b, b, b, w, b, w, w },
				{ b, b, b, b, w, b, w, b },
				{ b, b, b, b, w, b, b, w },
				{ b, b, b, b, w, b, b, b }, // 247
				{ b, b, b, b, b, w, w, w },
				{ b, b, b, b, b, w, w, b },
				{ b, b, b, b, b, w, b, w },
				{ b, b, b, b, b, w, b, b }, // 251
				{ b, b, b, b, b, b, w, w },
				{ b, b, b, b, b, b, w, b },
				{ b, b, b, b, b, b, b, w },
				{ b, b, b, b, b, b, b, b }, // 255
		};

		/***********************************************************************
		 * gibberish * String bittable[] = { " ", " X", " X ", " XX", // 3 " X
		 * ", " X X", " XX ", " XXX", // 7 " X ", " X X", " X X ", " X
		 * XX", // 11 " XX ", " XX X", " XXX ", " XXXX", // 15 " X ", " X X", "
		 * X X ", " X XX", // 19 " X X ", " X X X", " X XX ", " X XXX", // 23
		 * " XX ", " XX X", " XX X ", " XX XX", // 27 " XXX ", " XXX X", " XXXX
		 * ", " XXXXX", // 31 " X ", " X X", " X X ", " X XX", // 35 " X X ", "
		 * X X X", " X XX ", " X XXX", // 39 " X X ", " X X X", " X X X ", " X X
		 * XX", // 43 " X XX ", " X XX X", " X XXX ", " X XXXX", // 47 " XX ", "
		 * XX X", " XX X ", " XX XX", // 51 " XX X ", " XX X X", " XX XX ", " XX
		 * XXX", // 55 " XXX ", " XXX X", " XXX X ", " XXX XX", // 59 " XXXX
		 * ", " XXXX X", " XXXXX ", " XXXXXX", // 63 " X ", " X X", " X X ", " X
		 * XX", // 67 " X X ", " X X X", " X XX ", " X XXX", // 71 " X X ",
		 * " X X X", " X X X ", " X X XX", // 75 " X XX ", " X XX X", " X XXX
		 * ", " X XXXX", // 79 " X X ", " X X X", " X X X ", " X X XX", // 83
		 * " X X X ", " X X X X", " X X XX ", " X X XXX", // 87 " X XX ", " X XX
		 * X", " X XX X ", " X XX XX", // 91 " X XXX ", " X XXX X", " X XXXX
		 * ", " X XXXXX", // 95 " XX ", " XX X", " XX X ", " XX XX", // 99 " XX
		 * X ", " XX X X", " XX XX ", " XX XXX", //103 " XX X ", " XX X X", " XX
		 * X X ", " XX X XX", //107 " XX XX ", " XX XX X", " XX XXX ", " XX
		 * XXXX", //111 " XXX ", " XXX X", " XXX X ", " XXX XX", //115 " XXX X
		 * ", " XXX X X", " XXX XX ", " XXX XXX", //119 " XXXX ", " XXXX X", "
		 * XXXX X ", " XXXX XX", //123 " XXXXX ", " XXXXX X", " XXXXXX ", "
		 * XXXXXXX", //127 "X ", "X X", "X X ", "X XX", //131 "X X ", "X X X",
		 * "X XX ", "X XXX", //135 "X X ", "X X X", "X X X ", "X X XX", //139 "X
		 * XX ", "X XX X", "X XXX ", "X XXXX", //143 "X X ", "X X X", "X X X ",
		 * "X X XX", //147 "X X X ", "X X X X", "X X XX ", "X X XXX", //151
		 * "X XX ", "X XX X", "X XX X ", "X XX XX", //155 "X XXX ", "X XXX X",
		 * "X XXXX ", "X XXXXX", //159 "X X ", "X X X", "X X X ", "X X
		 * XX", //163 "X X X ", "X X X X", "X X XX ", "X X XXX", //167 "X X X
		 * ", "X X X X", "X X X X ", "X X X XX", //171 "X X XX ", "X X XX X", "X
		 * X XXX ", "X X XXXX", //175 "X XX ", "X XX X", "X XX X ", "X XX
		 * XX", //179 "X XX X ", "X XX X X", "X XX XX ", "X XX XXX", //183 "X
		 * XXX ", "X XXX X", "X XXX X ", "X XXX XX", //187 "X XXXX ", "X XXXX
		 * X", "X XXXXX ", "X XXXXXX", //191 "XX ", "XX X", "XX X ", "XX
		 * XX", //195 "XX X ", "XX X X", "XX XX ", "XX XXX", //199 "XX X ", "XX
		 * X X", "XX X X ", "XX X XX", //203 "XX XX ", "XX XX X", "XX XXX ", "XX
		 * XXXX", //207 "XX X ", "XX X X", "XX X X ", "XX X XX", //211
		 * "XX X X ", "XX X X X", "XX X XX ", "XX X XXX", //215 "XX XX ",
		 * "XX XX X", "XX XX X ", "XX XX XX", //219 "XX XXX ", "XX XXX X", "XX
		 * XXXX ", "XX XXXXX", //223 "XXX ", "XXX X", "XXX X ", "XXX XX", //227
		 * "XXX X ", "XXX X X", "XXX XX ", "XXX XXX", //231 "XXX X ", "XXX X X",
		 * "XXX X X ", "XXX X XX", //235 "XXX XX ", "XXX XX X", "XXX XXX ", "XXX
		 * XXXX", //239 "XXXX ", "XXXX X", "XXXX X ", "XXXX XX", //243 "XXXX X
		 * ", "XXXX X X", "XXXX XX ", "XXXX XXX", //247 "XXXXX ", "XXXXX X",
		 * "XXXXX X ", "XXXXX XX", //251 "XXXXXX ", "XXXXXX X", "XXXXXXX
		 * ", "XXXXXXXX", //255 };
		 **********************************************************************/

		int len = imageBytes.length;
		byte compressedBytes[] = new byte[len];
		System.arraycopy(imageBytes, 0, compressedBytes, 0, len);
		imageBytes = new byte[(len * 8)
				+ (((imageWidth % 8) + 1) * imageHeight)]; // (imageWidth +
		// (imageWidth%8)) *
		// imageHeight];
		int i, x, y, n, scan, pad;

		int maxWidth = 8;

		scan = imageWidth / 8;

		pad = imageWidth % 8;
		if (pad > 0)
			scan++;

		for (y = 0, n = 0; y < compressedBytes.length; y += scan)
		{
			for (x = 0; x < scan; x++, n += 8)
			{
				if (n >= imageBytes.length)
					System.out.println("oops n=" + n);
				if ((x + y) >= compressedBytes.length)
					System.out.println("oops x+y=" + (x + y));
				System
						.arraycopy(table[(compressedBytes[x + y] & 0xff)], 0, imageBytes, n, 8);
			}

			n -= (pad > 0 ? (8 - pad) : 0);

		}

		bitsPerSample = 8;
	}

	public void Convert4To8Bits()
	{ // really to true color

		int i, x, y, n, scan, pad, b;
		int len = imageBytes.length;
		byte compressedBytes[] = new byte[len];
		System.arraycopy(imageBytes, 0, compressedBytes, 0, len);
		imageBytes = new byte[(len * 2)];

		scan = imageWidth / 2;

		System.out.println("size: " + imageWidth + "x" + imageHeight);
		System.out.println("real: " + imageWidth / 2 + "x" + imageHeight);
		System.out.println("len: " + compressedBytes.length);
		System.out.println("-->: " + imageBytes.length);

		pad = imageWidth % 2;
		if (pad > 0)
			scan++;

		for (y = 0, n = 0; y < compressedBytes.length; y += scan)
		{
			for (x = 0; x < scan; x++, n += 2)
			{

				if (n >= imageBytes.length)
					System.out.println("oops n=" + n);

				if (n + 1 >= imageBytes.length)
					System.out.println("oops n+1=" + n);

				if ((x + y) >= compressedBytes.length)
					System.out.println("oops x+y=" + (x + y));

				b = (compressedBytes[x + y] & 0xff);

				imageBytes[n] = (byte) (b >>> 4);
				imageBytes[n + 1] = (byte) (b & 0x0f);
			}
		}
		bitsPerSample = 8;
	}

}