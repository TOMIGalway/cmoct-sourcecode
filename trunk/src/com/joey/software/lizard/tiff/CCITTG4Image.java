/*******************************************************************************
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
package com.joey.software.lizard.tiff;

import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

import com.joey.software.lizard.util.Converter;


/* CCITT Group 4 fax encoding */
class CCITTG4Image extends RawImage
{

	byte WhiteRun[];

	byte BlackRun[];

	byte bytesArray[];

	byte rawImage[];

	boolean whiteRun = true;

	int lines = 0, nPixels = 0;

	int longHoriztonalRun = 0;

	boolean hiloBitOrder = false;

	int i, j, shift, count, a0, b1, ref[], cur[], refIndex, curIndex,
			runLength;

	byte b, tmp[];

	long start, stop;

	T4Node WhiteTree;

	T4Node BlackTree;

	T4Node ModeTree;

	T4Node node;

	T4Code code;

	public static final int P = 0;

	public static final int H = 1;

	public static final int V0 = 2;

	public static final int VR1 = 3;

	public static final int VR2 = 4;

	public static final int VR3 = 5;

	public static final int VL1 = 6;

	public static final int VL2 = 7;

	public static final int VL3 = 8;

	public static final int EXT2D = 9;

	public static final int EXT1D = 10;

	public static final int ModeCodes[][] =
	{
	{ 4, 0x1, P }, /* 0001 pass */
	{ 3, 0x1, H }, /* 001 horizontal */
	{ 1, 0x1, V0 }, /* 1 vert 0 */
	{ 3, 0x3, VR1 }, /* 011 vert r 1 */
	{ 6, 0x3, VR2 }, /* 000011 vert r 2 */
	{ 7, 0x3, VR3 }, /* 0000011 vert r 3 */
	{ 3, 0x2, VL1 }, /* 010 vert l 1 */
	{ 6, 0x2, VL2 }, /* 000010 vert l 2 */
	{ 7, 0x2, VL3 }, /* 0000010 vert l 3 */
	{ 10, 0xf, EXT2D }, /* 0000001111 */
	{ 12, 0xf, EXT1D }, /* 000000001111 */
	{ 12, 0x1, T4Code.EOL }, /* 000000000001 */
	};

	public CCITTG4Image(IFD ifd)
	{
		super(ifd);

		hiloBitOrder = (ifd.GetFieldValue(Tag.FILLORDER) == 2);
		init();
	}

	public CCITTG4Image(int imageWidth, int imageHeight, byte[] bytesArray, boolean hiloBitOrder)
	{
		super(new IFD());
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.imageBytes = bytesArray;
		this.hiloBitOrder = hiloBitOrder;
		init();
	}

	void init()
	{
		// setup trees
		WhiteTree = new T4Node();
		BlackTree = new T4Node();
		ModeTree = new T4Node();

		if (hiloBitOrder)
		{
			for (i = 0; i < ModeCodes.length; i++)
			{
				code = new T4Code(ModeCodes[i]);
				if (code.bitLength <= 8)
				{
					int lo = Converter.reverseByte(Converter
							.getLoByte(code.codeWord)); // & 0xff);
					code.codeWord = (lo >>> (8 - code.bitLength));
				} else
				{
					code.codeWord = (Converter.reverseInt(code.codeWord) >>> (16 - code.bitLength));
				}
				ModeTree.ReversedAdd(0, code);
			}

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
			for (i = 0; i < ModeCodes.length; i++)
			{
				code = new T4Code(ModeCodes[i]);
				ModeTree.Add(0, code);
			}

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
		// setup runs
		WhiteRun = new byte[imageWidth + 2];
		BlackRun = new byte[imageWidth + 2];

		for (i = 0; i < imageWidth + 1; i++)
		{
			WhiteRun[i] = (byte) 0xff;
			BlackRun[i] = 0;
		}
	}

	@Override
	public Object getImageProducer()
	{

		// DEBUG System.out.println("CCITTG4Image::getImage()");

		byte rawImage[] = getRawImage();
		if (bitsPerSample == 8 && rawImage != null)
		{
			ColorModel cm = makeColorModel();
			return new MemoryImageSource(imageWidth, imageHeight, cm,
					imageBytes, 0, imageWidth);
		}

		return null;
	}

	public byte[] getRawImage()
	{

		// DEBUG System.out.println("CCITTG4Image::getImage()");

		if (imageBytes == null && imageStrips != null)
		{
			int i, n, len, rows, lastRow;

			int rowsPerStrip = ifd.GetFieldValue(Tag.ROWSPERSTRIP);

			imageBytes = new byte[imageWidth * imageHeight];

			for (i = 0, n = 0, rows = 0, lastRow = imageStrips.length - 1; i < imageStrips.length; i++, rows += rowsPerStrip)
			{

				byte rawStrip[] = DecodeImageStrip(imageStrips[i], (i == lastRow ? (imageHeight - rows)
						: rowsPerStrip));

				System.arraycopy(rawStrip, 0, imageBytes, n, nPixels); // rawStrip.length);
				n += nPixels; // rawStrip.length;
				nPixels = 0;
			}
			imageStrips = null;
		} else if (imageBytes != null)
			imageBytes = DecodeImage();
		else
			return null;

		return imageBytes;
	}

	byte[] DecodeImage()
	{
		// DEBUG System.out.println("CCITTG4Image::DecodeImage()");
		return DecodeImageStrip(imageBytes, imageHeight);
	}

	byte[] DecodeImageStrip(byte[] imageStrip, int maxLines)
	{
		// DEBUG System.out.println("CCITTG4Image::DecodeImageStrip( strip, "
		// +maxLines+" )");

		bytesArray = imageStrip;

		int expectedCount = imageWidth * maxLines;
		rawImage = new byte[expectedCount + 1];
		tmp = new byte[2];
		ref = new int[imageWidth + 1];
		cur = new int[imageWidth + 1];

		ref[0] = imageWidth; // set initial reference line to all white
		ref[1] = 0;

		runLength = 0;
		a0 = 0;
		b1 = ref[0];
		refIndex = 1;
		curIndex = 0;

		nPixels = 0;
		lines = 0;
		longHoriztonalRun = 0;
		whiteRun = true;

		code = new T4Code();

		// go through the whole thing
		decodeLoop: for (i = 0, lines = 1, count = bytesArray.length * 8; lines <= maxLines
				&& i < count;)
		{

			i = readMode(i);

			switch (code.runLength)
			{
				case P:
				{
					// DEBUG System.out.print("mode: Pass,\t");
					decodePass();
					continue decodeLoop;
				}

				case H:
				{
					// DEBUG System.out.print("mode: Horizontal, ");
					i = decodeHorizontal(tmp, i);
					i = decodeHorizontal(tmp, i);
					detectB1();
					break;
				}

				case V0:
					// DEBUG System.out.print("mode: Vertital 0,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0 ==> "+b1+"-"+a0+"="+
					// (b1-a0) );
					addRun(b1 - a0);
					whiteRun = !whiteRun;

					b1 += ref[refIndex++];
					// DEBUG System.out.println("b1 += ref[refIndex++] ==>"+b1);

					break;

				case VR1:
					// DEBUG System.out.print("mode: Vertital R 1,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0+1 ==>
					// "+b1+"-"+a0+"+1="+ (b1-a0+1) );
					addRun(b1 - a0 + 1);
					whiteRun = !whiteRun;
					b1 += ref[refIndex++];
					break;

				case VR2:
					// DEBUG System.out.print("mode: Vertital R 2,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0+2 ==>
					// "+b1+"-"+a0+"+2="+ (b1-a0+2) );
					addRun(b1 - a0 + 2);
					whiteRun = !whiteRun;
					b1 += ref[refIndex++];
					break;

				case VR3:
					// DEBUG System.out.print("mode: Vertital R 3,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0+3 ==>
					// "+b1+"-"+a0+"+3="+ (b1-a0+3) );
					addRun(b1 - a0 + 3);
					whiteRun = !whiteRun;
					b1 += ref[refIndex++];
					break;

				case VL1:
					// DEBUG System.out.print("mode: Vertital L 1,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0-1 ==>
					// "+b1+"-"+a0+"-1="+ (b1-a0-1) );
					addRun(b1 - a0 - 1);
					whiteRun = !whiteRun;
					if (refIndex > 0)
						b1 -= ref[--refIndex];
					break;

				case VL2:
					// DEBUG System.out.print("mode: Vertital L 2,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0-2 ==>
					// "+b1+"-"+a0+"-2="+ (b1-a0-2) );
					addRun(b1 - a0 - 2);
					whiteRun = !whiteRun;
					if (refIndex > 0)
						b1 -= ref[--refIndex];
					break;

				case VL3:
					// DEBUG System.out.print("mode: Vertital L 3,\t");
					detectB1();
					// DEBUG System.out.println("\tb1-a0-3 ==>
					// "+b1+"-"+a0+"-3="+ (b1-a0-3) );
					addRun(b1 - a0 - 3);
					whiteRun = !whiteRun;
					if (refIndex > 0)
						b1 -= ref[--refIndex];
					break;

				case EXT2D:
				case EXT1D:
					System.out.println("NO NO I WON'T DO UNCOMPRESSED!");
					return null;

				case T4Code.EOL:
					System.out.println("End of the line");
					lines++;
					whiteRun = true;
					resetRuns();
					break;
			}

			if (runLength < 0)
			{
				System.out.println("negative runLength=" + runLength);
				return null;
			}

			// DEBUG System.out.println("a0 = "+a0);

			if (a0 >= imageWidth)
			{
				lines++;
				// DEBUG System.out.println( "End of the line due to a0="+a0+",
				// lines="+lines );
				whiteRun = true;
				resetRuns();
			}

		}

		bitsPerSample = 8;

		// DEBUG System.out.println("nPixels = "+nPixels+",
		// rawImage.length="+rawImage.length+", lines="+lines+",
		// maxLines="+maxLines+", imageWidth="+imageWidth);

		return (rawImage);
	}

	public int readMode(int i)
	{
		int j = i / 8;
		int shift = (i % 8);

		// DEBUG System.out.println("Loop start i="+i);

		copyBits(bytesArray, tmp, j, shift);

		node = ModeTree.Find(Converter.bytesToInt(tmp));
		if (node != null)
			code = node.code;

		i += code.bitLength;
		return i;
	}

	public void detectB1()
	{
		// DEBUG System.out.println("\n\tdetectB1\tcurIndex="+curIndex+",
		// refIndex="+refIndex+", a0="+a0+", b1="+b1+",
		// imageWidth="+imageWidth);
		if (curIndex != 0)
		{ // not at beginning of row
			while (b1 <= a0 && b1 < imageWidth)
			{
				// DEBUG System.out.println("\t\trefIndex="+refIndex+",
				// b1="+b1);
				int r = ref[refIndex] + ref[refIndex + 1];
				if (r == 0)
					b1 = imageWidth;
				b1 += r;
				if (refIndex + 2 < ref.length)
					refIndex += 2;
				else
					System.out.println("ERROR in detectB1, refIndex="
							+ refIndex + ", ref.length=" + ref.length);
			}
		}
	}

	public void copyBits(byte[] bytesArray, byte[] tmp, int j, int shift)
	{

		byte b1 = bytesArray[j];
		byte b2 = (j + 1 < bytesArray.length ? bytesArray[j + 1] : 0);
		byte b3 = (j + 2 < bytesArray.length ? bytesArray[j + 2] : 0);

		if (shift > 0)
		{

			if (hiloBitOrder)
			{
				tmp[1] = (byte) (((b1 & 0xff) >>> shift) + ((b2 & (0xff >>> 8 - shift)) << 8 - shift));
				tmp[0] = (byte) (((b2 & 0xff) >>> shift) + ((b3 & (0xff >>> 8 - shift)) << 8 - shift));
			} else
			{
				tmp[0] = (byte) (((b1 & 0xff) << shift) + ((b2 & 0xff) >> 8 - shift));
				tmp[1] = (byte) (((b2 & 0xff) << shift) + ((b3 & 0xff) >> 8 - shift));
			}

		} else
		{

			if (hiloBitOrder)
			{
				tmp[1] = b1;
				tmp[0] = b2;
			} else
			{
				tmp[0] = b1;
				tmp[1] = b2;
			}
		}
	}

	public void decodePass()
	{
		// DEBUG System.out.print("\ndecodePass()\ta0="+a0+", b1="+b1+",
		// refIndex="+refIndex);
		detectB1();
		b1 += ref[refIndex++];
		runLength += b1 - a0;
		a0 = b1;
		b1 += ref[refIndex++];
		// DEBUG System.out.println("==> a0="+a0+", b1="+b1+",
		// refIndex="+refIndex);
	}

	public int decodeHorizontal(byte[] tmp, int i)
	{

		do
		{
			int j = i / 8;
			int shift = (i % 8);

			copyBits(bytesArray, tmp, j, shift);

			if (whiteRun)
				node = WhiteTree.Find(Converter.bytesToInt(tmp));
			else
				node = BlackTree.Find(Converter.bytesToInt(tmp));
			if (node != null)
				code = node.code;

			if (code.runLength >= 0)
			{

				// DEBUG System.out.print( (whiteRun?"W":"B") + code.runLength
				// );

				if (code.runLength < 64)
				{
					addRun(code.runLength + longHoriztonalRun);
					whiteRun = !whiteRun;
					longHoriztonalRun = 0;
				} else
					longHoriztonalRun += code.runLength;

			} else if (code.runLength == T4Code.EOF)
			{
				lines--;
				System.out.print("---" + code);
				return T4Code.EOF; // reached the end of the image!
			} else
			{
				addRun(code.runLength);
				System.out.print(code + "~~");
				System.out.print((whiteRun ? "W" : "B") + code.runLength);
				System.out.print("~~");
			}

			i += code.bitLength;

			if (nPixels > 0 && code.runLength == T4Code.EOL) // end of the
			// line
			{
				whiteRun = true; // always start with white
				lines++;
				resetRuns();
			}

		} while (code.runLength >= 64);
		return i;
	}

	public void addRun(int x)
	{
		runLength += x;

		// DEBUG System.out.println("\naddRun("+x+"), runLength="+runLength+",
		// a0="+a0+", b1="+b1+", curIndex="+curIndex+", refIndex="+refIndex );

		if (runLength < 0 || curIndex + 1 >= cur.length
				|| nPixels + runLength >= rawImage.length
				|| runLength >= WhiteRun.length)
		{
			System.out.println("x=" + x + ", runLength=" + runLength
					+ ", curIndex=" + curIndex + ", nPixels=" + nPixels);
			System.out.println("cur.length=" + cur.length
					+ ", rawImage.length=" + rawImage.length
					+ ", WhiteRun.length=" + WhiteRun.length);
			System.out.flush();
			return;
		}

		cur[curIndex++] = runLength;
		a0 += x;

		if (runLength > 0)
		{
			// DEBUG System.out.println("runLength > 0 so create the pixels to
			// match");
			System
					.arraycopy((whiteRun ? WhiteRun : BlackRun), 0, rawImage, nPixels, runLength);
			nPixels += runLength;
		}

		runLength = 0;

		// DEBUG System.out.println("---->runLength="+runLength+", a0="+a0+",
		// b1="+b1+", curIndex="+curIndex+", refIndex="+refIndex );

	}

	public void resetRuns()
	{

		// DEBUG System.out.println("\nresetRuns");

		// DEBUG System.out.println("--------------------------- Current Line
		// #"+(lines-1)+"--------------------------");
		// DEBUG for (int i=0; i<curIndex; i++)
		// DEBUG System.out.print(cur[i]+ (i!=0 && i%8==0 ? "\n" : ", "));
		// DEBUG System.out.println("*\n");

		addRun(0);
		// DEBUG System.out.println("after addRun(0), a0="+a0);

		/* reset a0 **************************************** */
		if (a0 != imageWidth)
		{
			System.out.println((a0 < imageWidth ? "Premature EOL"
					: "Line length mismatch"));
			while (a0 > imageWidth)
				a0 -= cur[--curIndex];
			if (a0 < imageWidth)
			{
				if (a0 < 0)
					a0 = 0;
				if ((curIndex & 0x1) != 0)
					addRun(0);
				addRun(imageWidth - a0);// +(a0==0||curIndex==1?1:-a0));
			} else if (a0 > imageWidth)
			{
				addRun(imageWidth);
				addRun(0);
			}
		}
		/** ************************************************* */

		// DEBUG System.out.println("--------- Reference Line # "+lines+"
		// ---------");
		// DEBUG for (int i=0; i<curIndex; i++)
		// DEBUG System.out.print(cur[i]+ (i!=0 && i%8==0 ? "\n" : ", "));
		// DEBUG System.out.println("*\n");

		int tmp[] = ref;
		ref = cur;
		cur = tmp;
		// now zero out extra spots for runs
		for (int i = curIndex; i < imageWidth; i++)
			ref[i] = 0;
		for (int i = 0; i < imageWidth; i++)
			cur[i] = 0;
		runLength = 0;
		a0 = 0;
		b1 = ref[0];
		refIndex = 1;
		curIndex = 0;

	}

}
