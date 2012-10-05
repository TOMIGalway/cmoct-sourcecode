package com.joey.software.lizard.tiff;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

import com.joey.software.lizard.util.Converter;


/* CCITT Group 3 fax encoding */
class CCITTG3Image extends RawImage
{

	T4Node WhiteTree;

	T4Node BlackTree;

	byte WhiteRun[];

	byte BlackRun[];

	boolean hiloBitOrder;

	int nPixels = 0;

	public CCITTG3Image(IFD ifd)
	{
		super(ifd);

		int i;
		T4Code code;

		WhiteRun = new byte[2560];
		BlackRun = new byte[2560];

		for (i = 0; i < 2560; i++)
		{
			WhiteRun[i] = (byte) 0xff;
			BlackRun[i] = 0;
		}

		hiloBitOrder = (ifd.GetFieldValue(Tag.FILLORDER) == 2);
		WhiteTree = new T4Node();
		BlackTree = new T4Node();

		// create the code lookup trees
		if (hiloBitOrder)
		{ // thus reverse codeWords and do a reversed add to make masks
			// correctly
			for (i = 0; i < T4Tables.WhiteCodes.length; i++)
			{
				code = new T4Code(T4Tables.WhiteCodes[i]);
				if (code.bitLength <= 8)
				{
					int lo = Converter.reverseByte(Converter
							.getLoByte(code.codeWord)); // & 0xff);
					code.codeWord = (lo >>> (8 - code.bitLength));
				} else
				{
					code.codeWord = (Converter.reverseInt(code.codeWord) >>> (16 - code.bitLength));
				}
				WhiteTree.ReversedAdd(0, code);
			}

			for (i = 0; i < T4Tables.BlackCodes.length; i++)
			{
				code = new T4Code(T4Tables.BlackCodes[i]);
				if (code.bitLength <= 8)
				{
					int lo = Converter.reverseByte(Converter
							.getLoByte(code.codeWord)); // & 0xff);
					code.codeWord = (lo >>> (8 - code.bitLength));
				} else
				{
					code.codeWord = (Converter.reverseInt(code.codeWord) >>> (16 - code.bitLength));
				}
				BlackTree.ReversedAdd(0, code);
			}
		} else
		{
			for (i = 0; i < T4Tables.WhiteCodes.length; i++)
			{
				code = new T4Code(T4Tables.WhiteCodes[i]);
				WhiteTree.Add(0, code);
			}

			for (i = 0; i < T4Tables.BlackCodes.length; i++)
			{
				code = new T4Code(T4Tables.BlackCodes[i]);
				BlackTree.Add(0, code);
			}
		}

	}

	/**/
	@Override
	public Image getImage()
	{

		if (imageBytes == null && imageStrips != null)
		{
			int i, n, len, rows, lastRow;

			int rowsPerStrip = ifd.GetFieldValue(Tag.ROWSPERSTRIP);

			imageBytes = new byte[imageWidth * imageHeight];

			for (i = 0, n = 0, rows = 0, lastRow = imageStrips.length - 1; i < imageStrips.length; i++, rows += rowsPerStrip)
			{ // imageStrips.length

				byte rawStrip[] = DecodeImageStrip(imageStrips[i], (i == lastRow ? (imageHeight - rows)
						: rowsPerStrip), true); // (i==0));

				System.arraycopy(rawStrip, 0, imageBytes, n, nPixels);
				n += nPixels;
				nPixels = 0;
			}
			imageStrips = null;
		} else if (imageBytes != null)
			imageBytes = DecodeImage();
		else
			return null;

		Image img = null;

		if (bitsPerSample == 8 && imageBytes != null)
		{
			ColorModel cm = makeColorModel();
			img = Toolkit.getDefaultToolkit()
					.createImage(new MemoryImageSource(imageWidth, imageHeight,
							cm, imageBytes, 0, imageWidth));
		}

		return img;
	}

	/**/

	@Override
	public Object getImageProducer()
	{

		if (imageBytes == null && imageStrips != null)
		{
			int i, n, len, rows, lastRow;

			int rowsPerStrip = ifd.GetFieldValue(Tag.ROWSPERSTRIP);

			imageBytes = new byte[imageWidth * imageHeight];

			for (i = 0, n = 0, rows = 0, lastRow = imageStrips.length - 1; i < imageStrips.length; i++, rows += rowsPerStrip)
			{ // imageStrips.length

				byte rawStrip[] = DecodeImageStrip(imageStrips[i], (i == lastRow ? (imageHeight - rows)
						: rowsPerStrip), true); // (i==0));

				System.arraycopy(rawStrip, 0, imageBytes, n, nPixels);
				n += nPixels;
				nPixels = 0;
			}
			imageStrips = null;
		} else if (imageBytes != null)
			imageBytes = DecodeImage();
		else
			return null;

		if (bitsPerSample == 8 && imageBytes != null)
		{
			int[] imagePixels = new int[imageBytes.length];
			for (int i = 0; i < imageBytes.length; i++)
				imagePixels[i] = imageBytes[i];
			return new MemoryImageSource(imageWidth, imageHeight, imagePixels,
					0, imageWidth);
		}

		return null;
	}

	byte[] DecodeImage()
	{
		System.out.println("CCITTG3Image::DecodeImage()");
		return DecodeImageStrip(imageBytes, imageHeight, true);
	}

	byte[] DecodeImageStrip(byte[] imageStrip, int maxLines, boolean firstStrip)
	{
		System.out.println("CCITTG3Image::DecodeImageStrip( strip, " + maxLines
				+ " )");

		int i, j, shift, count;
		byte bytesArray[] = imageStrip;
		byte b1, b2, b3, tmp[] = new byte[2];
		boolean whiteRun = true;
		int lines = 0;

		T4Code c, code = new T4Code();
		T4Node node;

		int expectedCount = imageWidth * maxLines;
		byte rawImage[] = new byte[expectedCount + 1];

		nPixels = 0;

		if (firstStrip)
		{

			// for (i=0; i<8; i++)
			// System.out.print( " " + Converter.byteToBinaryString(
			// bytesArray[i] ) );
			// System.out.println();

			// skip over first eol which starts the encoded image
			for (i = 0, j = 0; bytesArray[i] == 0; i++)
			{
				;
			}
			j = i;

			if (hiloBitOrder)
			{
				int b = Converter.reverseByte(bytesArray[j]);
				for (i = 0; (b >> i) != 1; i++)
				{
					;
				}
			} else
			{
				for (i = 0; (bytesArray[j] >> i) != 1; i++)
				{
					;
				}
			}
			i += j * 8;
		} else
		{
			i = 0;
		}

		// go through the whole thing
		for (lines = 1, count = bytesArray.length * 8; lines < maxLines
				&& i < count;)
		{

			// i is the bit index, j is the byte index, shift is amount to shift
			// over in byte j
			j = i / 8;
			shift = (i % 8);

			// grab the next few bytes to get bytes to search on
			b1 = bytesArray[j];
			b2 = (j + 1 < bytesArray.length ? bytesArray[j + 1] : 0);
			b3 = (j + 2 < bytesArray.length ? bytesArray[j + 2] : 0);

			// shift bytes if necessary
			if (hiloBitOrder)
			{
				if (shift > 0)
				{
					tmp[1] = (byte) (((b1 & 0xff) >>> shift) + ((b2 & (0xff >>> 8 - shift)) << 8 - shift));
					tmp[0] = (byte) (((b2 & 0xff) >>> shift) + ((b3 & (0xff >>> 8 - shift)) << 8 - shift));
				} else
				{
					tmp[1] = b1;
					tmp[0] = b2;
				}
			} else
			{
				if (shift > 0)
				{
					tmp[0] = (byte) (((b1 & 0xff) << shift) + ((b2 & 0xff) >> 8 - shift));
					tmp[1] = (byte) (((b2 & 0xff) << shift) + ((b3 & 0xff) >> 8 - shift));
				} else
				{
					tmp[0] = b1;
					tmp[1] = b2;
				}
			}
			// find the code using our code trees
			if (whiteRun)
				node = WhiteTree.Find(Converter.bytesToInt(tmp));
			else
				node = BlackTree.Find(Converter.bytesToInt(tmp));
			if (node != null)
				code = node.code;

			// set the image bytes based on the code's runlength
			if (code.runLength > 0)
			{

				if (nPixels >= rawImage.length
						|| nPixels + code.runLength >= rawImage.length
						|| code.runLength >= WhiteRun.length
						|| code.runLength >= BlackRun.length)
				{
					System.out.println("nPixels=" + nPixels
							+ ", code.runLength=" + code.runLength
							+ ", rawImage.length=" + rawImage.length
							+ ", WhiteRun.length=" + WhiteRun.length
							+ ", BlackRun.length=" + BlackRun.length);
					System.out.flush();
				}
				System
						.arraycopy((whiteRun ? WhiteRun : BlackRun), 0, rawImage, nPixels, code.runLength);
				nPixels += code.runLength;
			}

			// increment our bit index
			i += code.bitLength;

			// if at end of file, decrement line count (remember, skipped over
			// first line (just eol code))
			if (code.runLength == T4Code.EOF)
			{
				lines--;
				break; // reached the end of the image!
			}

			// DEBUG System.out.print((whiteRun ? " W" : " B") +
			// code.runLength);
			// DEBUG if (code.runLength<0)
			// DEBUG System.out.println(code);

			// if at the end of the line
			if (nPixels > 0 && code.runLength == T4Code.EOL) // end of the
			// line
			{
				whiteRun = true; // always start with a white run
				lines++;

				// DEBUG System.out.println();
			} else if (code.runLength < 64) // if terminating code
				whiteRun = !whiteRun; // switch run type (white/black)

		}

		// DEBUG System.out.println("nPixels = "+nPixels+",
		// rawImage.length="+rawImage.length+", lines="+lines+",
		// maxLines="+maxLines+", imageWidth="+imageWidth);

		bitsPerSample = 8;
		return (rawImage);
	}

}