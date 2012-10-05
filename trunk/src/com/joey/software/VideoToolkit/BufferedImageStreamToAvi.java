package com.joey.software.VideoToolkit;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;
import java.util.HashMap;

import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.imageToolkit.ImageOperations;


/**
 * This plugin saves stacks in AVI format. It is based on the FileAvi class
 * written by William Gandler. The FileAvi class is part of Matthew J.
 * McAuliffe's MIPAV program, available from http://mipav.cit.nih.gov/.
 */
public class BufferedImageStreamToAvi
{

	private RandomAccessFile raFile;

	private int bytesPerPixel;

	private File file;

	private int bufferFactor;

	private int xDim, yDim, zDimVar, tDimVar;

	private int lutBufferRemapped[] = null;

	private int microSecPerFrame;

	private int xPad;

	private byte[] bufferWrite;

	private int bufferSize;

	private int indexA, indexB;

	private float opacityPrime;

	private int bufferAdr;

	private byte[] lutWrite = null;

	private HashMap<Integer, Integer> dcLengthMAP = null;

	HashMap<Integer, Long> savedbLengthMap = new HashMap<Integer, Long>();

	HashMap<Integer, Long> savedcLengthMap = new HashMap<Integer, Long>();;

	byte[] dataSig;

	long saveLISTSize2;

	long totalFramePos;

	long playTimePos;

	int frameCount = 0;

	long savemoviVar;

	long saveFileSizeVar; // location of file size in bytes not counting first

	// 8 bytes

	int frameRateVar;

	boolean allowResize = false;

	BufferedImage resizeHolder = null; // This holds resized frames (if needed

	// and allowed)

	public static void resetFrameRate(File f, int rate) throws IOException
	{
		int v = rate;
		RandomAccessFile raFile = new RandomAccessFile(f, "rw");
		raFile.seek(132);
		raFile.write(v & 0xFF);
		raFile.write((v >>> 8) & 0xFF);
		raFile.write((v >>> 16) & 0xFF);
		raFile.write((v >>> 24) & 0xFF);
		raFile.close();
	}

	public void setFrameRate(int rate) throws IOException
	{
		int v = rate;
		long oldPos = raFile.getFilePointer();
		raFile.seek(132);
		raFile.write(v & 0xFF);
		raFile.write((v >>> 8) & 0xFF);
		raFile.write((v >>> 16) & 0xFF);
		raFile.write((v >>> 24) & 0xFF);
		raFile.seek(oldPos);
	}

	public static void main(String input[]) throws IOException
	{

		if (true)
		{
			return;
		}
		int v = 15;

		RandomAccessFile raFile = new RandomAccessFile(FileSelectionField
				.getUserFile(), "rw");
		raFile.seek(132);
		raFile.write(v & 0xFF);
		raFile.write((v >>> 8) & 0xFF);
		raFile.write((v >>> 16) & 0xFF);
		raFile.write((v >>> 24) & 0xFF);

		if (true)
		{
			return;
		}
		int frameRate = 170;
		int total = 3 * frameRate;
		int wide = 4700;
		int high = 2070;

		String fileName = "testing1.avi";
		String path = "c:\\test\\";

		System.out.println("Hello");
		BufferedImageStreamToAvi output1 = new BufferedImageStreamToAvi(wide,
				high, frameRate, path, fileName, true, true);
		BufferedImage data = ImageOperations.getBi(wide, high);
		for (int i = 0; i < total; i++)
		{
			System.out.println(i + " of " + total);

			ImageOperations.fillWithRandomColors(data);
			output1.pushImage(data);
		}
		output1.finaliseVideo();

	}

	/**
	 * NOTE When you start the avi recording the x dimension of your video can
	 * change. This occours as the video seems to need to be a mod 4.
	 * 
	 * To solve this you have two choices copy the vale of xdim as your prefered
	 * image size. This can be a real problem...
	 * 
	 * OR
	 * 
	 * set allowResize. This will cause the program to create a bufferedImage of
	 * suitable size to
	 * 
	 * 
	 * 
	 * @param wide
	 * @param high
	 * @param frameRate
	 * @param fileDir
	 * @param fileName
	 * @param color
	 * @throws IOException
	 */
	public BufferedImageStreamToAvi(int wide, int high, int frameRate, String fileDir, String fileName, boolean color, boolean allowResize)
			throws IOException
	{
		// Start writing

		this.allowResize = allowResize;
		initateFile(wide, high, frameRate, fileDir, fileName, color);

		// Create BufferedImage holder here as only now is xdim set
		if (allowResize)
		{
			resizeHolder = ImageOperations.getBi(xDim, yDim);
		}
	}

	public synchronized void finaliseVideo() throws IOException
	{
		/*
		 * First write the footer to finish the program. But from what I can
		 * make out from AVI desine, we need to set rewrite the header once the
		 * true lenght of the file is know.
		 */
		writeFooter();

		// Go back and write total frames
		raFile.seek(totalFramePos);
		writeInt(tDimVar * frameCount);

		raFile.seek(playTimePos);
		writeInt(tDimVar * frameCount);
		raFile.close();

	}

	private void initateFile(int wide, int high, int frameRate, String fileDir, String fileName, boolean color)
			throws IOException
	{
		file = new File(fileDir + fileName);
		raFile = new RandomAccessFile(file, "rw");

		if (color)
			bytesPerPixel = 3;
		else
			bytesPerPixel = 1;

		dataSig = new byte[4];
		dataSig[0] = 48; // 0
		dataSig[1] = 48; // 0
		dataSig[2] = 100; // d
		dataSig[3] = 98; // b

		frameRateVar = frameRate;

		writeHeader(frameCount, wide, high, frameRate);

		System.out.println("Bu Im St 2 Avi initilize -> input [" + wide + ","
				+ high + "] takenAs [" + xDim + "," + yDim + "]");
	}

	/**
	 * This funciton will write the header
	 * 
	 * @param frameCount
	 * @param frameWidth
	 * @param frameHeight
	 * @param frameRate
	 * @return
	 * @throws IOException
	 */
	private void writeHeader(int frameCount, int frameWidth, int frameHeight, int frameRate)
			throws IOException
	{

		long saveLIST1Size; // location of length of CHUNK with first LIST - not
		// including
		// first 8 bytes with LIST and size. JUNK follows the end of
		// this CHUNK
		int[] extents;
		long saveLIST1subSize; // location of length of CHUNK with second LIST -
		// not including
		// first 8 bytes with LIST and size. Note that saveLIST1subSize =
		// saveLIST1Size + 76, and that the length size written to
		// saveLIST2Size is 76 less than that written to saveLIST1Size.
		// JUNK follows the end of this CHUNK.
		long savestrfSize; // location of lenght of strf CHUNK - not including
		// the first
		// 8 bytes with strf and size. strn follows the end of this
		// CHUNK.
		int resXUnit = 0;
		int resYUnit = 0;
		float xResol = 0.0f; // in distance per pixel
		float yResol = 0.0f; // in distance per pixel
		long biXPelsPerMeter = 0L;
		long biYPelsPerMeter = 0L;
		byte[] strnSignature;
		byte[] text;
		long savestrnPos;
		byte[] JUNKsignature;
		long saveJUNKsignature;
		int paddingBytes;
		int i;

		byte[] idx1Signature;

		int t, z;

		int xMod;

		lutBufferRemapped = new int[1];

		raFile.seek(0);

		writeString("RIFF"); // signature
		saveFileSizeVar = raFile.getFilePointer();
		// Bytes 4 thru 7 contain the length of the file. This length does
		// not include bytes 0 thru 7.
		writeInt(0); // for now write 0 in the file size location
		writeString("AVI "); // RIFF type
		// Write the first LIST chunk, which contains information on data
		// decoding
		writeString("LIST"); // CHUNK signature
		// Write the length of the LIST CHUNK not including the first 8 bytes
		// with LIST and
		// size. Note that the end of the LIST CHUNK is followed by JUNK.
		saveLIST1Size = raFile.getFilePointer();
		writeInt(0); // for now write 0 in avih sub-CHUNK size location
		writeString("hdrl"); // CHUNK type
		writeString("avih"); // Write the avih sub-CHUNK

		writeInt(0x38); // Write the length of the avih sub-CHUNK (38H) not
		// including the
		// the first 8 bytes for avihSignature and the length
		microSecPerFrame = (int) ((1.0 / frameRate) * 1.0e6);
		// IJ.write("microSecPerFrame: "+microSecPerFrame);
		writeInt(microSecPerFrame); // dwMicroSecPerFrame - Write the
		// microseconds per frame

		writeInt(0); // dwMaxBytesPerSec
		// Write the maximum data rate of the file in bytes per second
		writeInt(0); // dwReserved1 - Reserved1 field set to zero
		writeInt(0x10); // dwFlags - just set the bit for AVIF_HASINDEX
		// 10H AVIF_HASINDEX: The AVI file has an idx1 chunk containing
		// an index at the end of the file. For good performance, all
		// AVI files should contain an index.
		// 20H AVIF_MUSTUSEINDEX: Index CHUNK, rather than the physical
		// ordering of the chunks in the file, must be used to determine the
		// order of the frames.
		// 100H AVIF_ISINTERLEAVED: Indicates that the AVI file is interleaved.
		// This is used to read data from a CD-ROM more efficiently.
		// 800H AVIF_TRUSTCKTYPE: USE CKType to find key frames
		// 10000H AVIF_WASCAPTUREFILE: The AVI file is used for capturing
		// real-time video. Applications should warn the user before
		// writing over a file with this fla set because the user
		// probably defragmented this file.
		// 20000H AVIF_COPYRIGHTED: The AVI file contains copyrighted data
		// and software. When, this flag is used, software should not
		// permit the data to be duplicated.

		tDimVar = 1;
		zDimVar = frameCount;// getStackSize();
		yDim = frameHeight;
		xDim = frameWidth;
		xPad = 0;
		xMod = xDim % 4;
		if (xMod != 0)
		{
			xPad = 4 - xMod;
			xDim = xDim + xPad;
			xPad = 0;
		}

		totalFramePos = raFile.getFilePointer();
		writeInt(0); // dwTotalFrames - total frame number
		writeInt(0); // dwInitialFrames -Initial frame for interleaved files.
		// Noninterleaved files should specify 0.
		writeInt(1); // dwStreams - number of streams in the file - here 1 video
		// and zero audio.
		writeInt(0); // dwSuggestedBufferSize - Suggested buffer size for
		// reading the file.
		// Generally, this size should be large enough to contain the largest
		// chunk in the file.
		writeInt(xDim - xPad); // dwWidth - image width in pixels
		writeInt(yDim); // dwHeight - image height in pixels
		// dwReserved[4] - Microsoft says to set the following 4 values to 0.
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(0);

		// Write the Stream line header CHUNK
		writeString("LIST");
		// Write the size of the first LIST subCHUNK not including the first 8
		// bytes with
		// LIST and size. Note that saveLIST1subSize = saveLIST1Size + 76, and
		// that
		// the length written to saveLIST1subSize is 76 less than the length
		// written to saveLIST1Size.
		// The end of the first LIST subCHUNK is followed by JUNK.
		saveLIST1subSize = raFile.getFilePointer();
		writeInt(0); // for now write 0 in CHUNK size location
		writeString("strl"); // Write the chunk type
		writeString("strh"); // Write the strh sub-CHUNK
		writeInt(56); // Write the length of the strh sub-CHUNK
		writeString("vids"); // fccType - Write the type of data stream - here
		// vids for video stream
		// Write DIB for Microsoft Device Independent Bitmap. Note:
		// Unfortunately,
		// at least 3 other four character codes are sometimes used for
		// uncompressed
		// AVI videos: 'RGB ', 'RAW ', 0x00000000
		writeString("DIB ");
		writeInt(0); // dwFlags
		// 0x00000001 AVISF_DISABLED The stram data should be rendered only when
		// explicitly enabled.
		// 0x00010000 AVISF_VIDEO_PALCHANGES Indicates that a palette change is
		// included
		// in the AVI file. This flag warns the playback software that it
		// will need to animate the palette.
		writeInt(0); // dwPriority - priority of a stream type. For example, in
		// a file with
		// multiple audio streams, the one with the highest priority might be
		// the
		// default one.
		writeInt(0); // dwInitialFrames - Specifies how far audio data is skewed
		// ahead of video
		// frames in interleaved files. Typically, this is about 0.75 seconds.
		// In
		// interleaved files specify the number of frames in the file prior
		// to the initial frame of the AVI sequence.
		// Noninterleaved files should use zero.
		// rate/scale = samples/second
		writeInt(1); // dwScale

		System.out.println(raFile.getFilePointer());
		writeInt(frameRate); // dwRate - frame rate for video streams
		writeInt(0); // dwStart - this field is usually set to zero

		playTimePos = raFile.getFilePointer();
		writeInt(0); // dwLength - playing time of AVI file as defined
		// by scale and rate
		// Set equal to the number of frames
		writeInt(0); // dwSuggestedBufferSize - Suggested buffer size for
		// reading the stream.
		// Typically, this contains a value corresponding to the largest chunk
		// in a stream.
		writeInt(-1); // dwQuality - encoding quality given by an integer
		// between
		// 0 and 10,000. If set to -1, drivers use the default
		// quality value.
		writeInt(0); // dwSampleSize #
		// 0 if the video frames may or may not vary in size
		// If 0, each sample of data(such as a video frame) must
		// be in a separate chunk.
		// If nonzero, then multiple samples of data can be grouped into
		// a single chunk within the file.
		// rcFrame - Specifies the destination rectangle for a text or video
		// stream within the movie
		// rectangle specified by the dwWidth and dwHeight members of the AVI
		// main header structure.
		// The rcFrame member is typically used in support of multiple video
		// streams. Set this
		// rectangle to the coordinates corresponding to the movie rectangle to
		// update the whole
		// movie rectangle. Units for this member are pixels. The upper-left
		// corner of the destination
		// rectangle is relative to the upper-left corner of the movie
		// rectangle.
		writeShort((short) 0); // left
		writeShort((short) 0); // top
		writeShort((short) 0); // right
		writeShort((short) 0); // bottom
		writeString("strf"); // Write the stream format chunk
		// Write the size of the stream format CHUNK not including the first 8
		// bytes for
		// strf and the size. Note that the end of the stream format CHUNK is
		// followed by
		// strn.
		savestrfSize = raFile.getFilePointer();
		writeInt(0); // for now write 0 in the strf CHUNK size location
		writeInt(40); // biSize - Write header size of BITMAPINFO header
		// structure
		// Applications should use this size to determine which BITMAPINFO
		// header structure is
		// being used. This size includes this biSize field.
		writeInt(xDim - xPad); // biWidth - image width in pixels
		writeInt(yDim); // biHeight - image height in pixels. If height is
		// positive,
		// the bitmap is a bottom up DIB and its origin is in the lower left
		// corner. If
		// height is negative, the bitmap is a top-down DIB and its origin is
		// the upper
		// left corner. This negative sign feature is supported by the Windows
		// Media Player, but it is not
		// supported by PowerPoint.
		writeShort(1); // biPlanes - number of color planes in which the data is
		// stored
		// This must be set to 1.
		int bitsPerPixel = (bytesPerPixel == 3) ? 24 : 8;
		writeShort((short) bitsPerPixel); // biBitCount - number of bits per
		// pixel #
		// 0L for BI_RGB, uncompressed data as bitmap
		// writeInt(bytesPerPixel*xDim*yDim*zDim*tDim); // biSizeImage #
		writeInt(0); // biSizeImage #
		writeInt(0); // biCompression - type of compression used
		writeInt(0); // biXPelsPerMeter - horizontal resolution in pixels
		writeInt(0); // biYPelsPerMeter - vertical resolution in pixels
		// per meter
		if (bitsPerPixel == 8)
			writeInt(256); // biClrUsed
		else
			writeInt(0); // biClrUsed
		writeInt(0); // biClrImportant - specifies that the first x colors of
		// the color table
		// are important to the DIB. If the rest of the colors are not
		// available,
		// the image still retains its meaning in an acceptable manner. When
		// this
		// field is set to zero, all the colors are important, or, rather, their
		// relative importance has not been computed.
		// Write the LUTa.getExtents()[1] color table entries here. They are
		// written:
		// blue byte, green byte, red byte, 0 byte
		if (bytesPerPixel == 1)
		{
			createLUT();
			raFile.write(lutWrite);
		}

		// Use strn to provide a zero terminated text string describing the
		// stream
		savestrnPos = raFile.getFilePointer();
		raFile.seek(savestrfSize);
		writeInt((int) (savestrnPos - (savestrfSize + 4)));
		raFile.seek(savestrnPos);
		writeString("strn");
		writeInt(16); // Write the length of the strn sub-CHUNK
		text = new byte[16];
		text[0] = 70; // F
		text[1] = 105; // i
		text[2] = 108; // l
		text[3] = 101; // e
		text[4] = 65; // A
		text[5] = 118; // v
		text[6] = 105; // i
		text[7] = 32; // space
		text[8] = 119; // w
		text[9] = 114; // r
		text[10] = 105; // i
		text[11] = 116; // t
		text[12] = 101; // e
		text[13] = 32; // space
		text[14] = 32; // space
		text[15] = 0; // termination byte
		raFile.write(text);
		// write a JUNK CHUNK for padding
		saveJUNKsignature = raFile.getFilePointer();
		raFile.seek(saveLIST1Size);
		writeInt((int) (saveJUNKsignature - (saveLIST1Size + 4)));
		raFile.seek(saveLIST1subSize);
		writeInt((int) (saveJUNKsignature - (saveLIST1subSize + 4)));
		raFile.seek(saveJUNKsignature);
		writeString("JUNK");
		paddingBytes = (int) (4084 - (saveJUNKsignature + 8));
		writeInt(paddingBytes);
		for (i = 0; i < (paddingBytes / 2); i++)
		{
			writeShort((short) 0);
		}

		// Write the second LIST chunk, which contains the actual data
		writeString("LIST");
		// Write the length of the LIST CHUNK not including the first 8 bytes
		// with LIST and
		// size. The end of the second LIST CHUNK is followed by idx1.
		saveLISTSize2 = raFile.getFilePointer();
		writeInt(0); // For now write 0
		savemoviVar = raFile.getFilePointer();
		writeString("movi"); // Write CHUNK type 'movi'

		// savedbLength = new long[tDim * zDim];
		// savedcLength = new long[tDim * zDim];
		// dcLength = new int[tDim * zDim];

		// Write the data. Each 3-byte triplet in the bitmap array represents
		// the relative intensities
		// of blue, green, and red, respectively, for a pixel. The color bytes
		// are in reverse order
		// from the Windows convention.
		bufferWrite = new byte[bytesPerPixel * xDim * yDim];
	}

	public int getXDim()
	{
		return xDim;
	}

	public int getYDim()
	{
		return yDim;
	}

	private synchronized void writeFooter() throws IOException
	{
		long endPos;
		long idx1Pos;
		long saveidx1Length;

		// Write the idx1 CHUNK
		// Write the 'idx1' signature
		idx1Pos = raFile.getFilePointer();
		raFile.seek(saveLISTSize2);
		writeInt((int) (idx1Pos - (saveLISTSize2 + 4)));
		raFile.seek(idx1Pos);
		writeString("idx1");
		// Write the length of the idx1 CHUNK not including the idx1 signature
		// and the 4 length
		// bytes. Write 0 for now.
		saveidx1Length = raFile.getFilePointer();
		writeInt(0);
		for (int z = 0; z < frameCount; z++)
		{
			// In the ckid field write the 4 character code to identify the
			// chunk 00db or 00dc
			raFile.write(dataSig);
			if (z == 0)
			{
				writeInt(0x10); // Write the flags - select AVIIF_KEYFRAME
			} else
			{
				writeInt(0x00);
			}
			// AVIIF_KEYFRAME 0x00000010L
			// The flag indicates key frames in the video sequence.
			// Key frames do not need previous video information to be
			// decompressed.
			// AVIIF_NOTIME 0x00000100L The CHUNK does not influence video
			// timing(for
			// example a palette change CHUNK).
			// AVIIF_LIST 0x00000001L Marks a LIST CHUNK.
			// AVIIF_TWOCC 2L
			// AVIIF_COMPUSE 0x0FFF0000L These bits are for compressor use.
			writeInt((int) (savedbLengthMap.get(z) - 4 - savemoviVar));
			// Write the offset (relative to the 'movi' field) to the relevant
			// CHUNK
			writeInt(bytesPerPixel * xDim * yDim); // Write the length of the
			// relevant
			// CHUNK. Note that this length is
			// also written at savedbLength
		} // for (z = 0; z < zDim; z++)
		endPos = raFile.getFilePointer();
		raFile.seek(saveFileSizeVar);
		writeInt((int) (endPos - (saveFileSizeVar + 4)));
		raFile.seek(saveidx1Length);
		writeInt((int) (endPos - (saveidx1Length + 4)));

	}

	public synchronized void pushImage(BufferedImage img) throws IOException
	{
		// if (img.getWidth() != xDim || img.getHeight() != yDim)
		// {
		// if (allowResize)
		// {
		//
		// ImageOperations.getResizeImage(img, resizeHolder, true, false);
		// img = resizeHolder;
		// } else
		// {
		// throw new InvalidParameterException(
		// "BufferedImageStreamToAVI: Error Pushing image onto AVI file : The image size has changed");
		// }
		// }

		raFile.write(dataSig);
		savedbLengthMap.put(frameCount++, raFile.getFilePointer());

		writeInt(bytesPerPixel * xDim * yDim); // Write the data length

		// System.out
		// .println("BISTAVI - pushImage() -> Wants " + xDim + "," + yDim
		// + " Getting : " + img.getWidth() + ","
		// + img.getHeight());
		if (bytesPerPixel == 1)
			writeByteFrame(img);
		else
			writeRGBFrame(img);
	}

	private void writeByteFrame(BufferedImage img) throws IOException
	{// int slice) throws IOException {
		// ImageProcessor ip = imp.getStack().getProcessor(slice);
		// ip = ip.convertToByte(true);
		// byte[] pixels = (byte[])ip.getPixels();
		// int width = imp.getWidth();
		// int height = imp.getHeight();
		// int c, offset, index = 0;
		// for (int y=height-1; y>=0; y--) {
		// offset = y*width;
		// for (int x=0; x<width; x++)
		// bufferWrite[index++] = pixels[offset++];
		// for (int i = 0; i<xPad; i++)
		// bufferWrite[index++] = (byte)0;
		// }
		// raFile.write(bufferWrite);
		throw new InvalidParameterException(
				"This function is not working yet : AVIWrite:writebyteframe");
	}

	private void writeRGBFrame(BufferedImage img) throws IOException
	{
		int width = img.getWidth();
		int height = img.getHeight();
		int c, offset, index = 0;

		for (int y = height - 1; y >= 0; y--)
		{
			for (int x = 0; x < width; x++)
			{
				c = img.getRGB(x, y);
				bufferWrite[index++] = (byte) (c & 0xff); // blue
				bufferWrite[index++] = (byte) ((c & 0xff00) >> 8); // green
				bufferWrite[index++] = (byte) ((c & 0xff0000) >> 16); // red
			}
			for (int i = 0; i < xPad; i++)
			{
				bufferWrite[index++] = (byte) 0;
				bufferWrite[index++] = (byte) 0;
				bufferWrite[index++] = (byte) 0;
			}
		}
		raFile.write(bufferWrite);
	}

	private void createLUT()
	{
		// LookUpTable lut = imp.createLut();
		// IndexColorModel cm = (IndexColorModel)lut.getColorModel();
		// int mapSize = cm.getMapSize();
		lutWrite = new byte[4 * 256];
		for (int i = 0; i < 256; i++)
		{
			// if (i<mapSize) {
			// lutWrite[4*i] = (byte)cm.getBlue(i);
			// lutWrite[4*i+1] = (byte)cm.getGreen(i);
			// lutWrite[4*i+2] = (byte)cm.getRed(i);
			// lutWrite[4*i+3] = (byte)0;
			// }

			lutWrite[4 * i] = (byte) 255;
			lutWrite[4 * i + 1] = (byte) 255;
			lutWrite[4 * i + 2] = (byte) 255;
			lutWrite[4 * i + 3] = (byte) 0;

		}
	}

	final void writeString(String s) throws IOException
	{
		byte[] bytes = s.getBytes("UTF-8");
		raFile.write(bytes);
	}

	final void writeInt(int v) throws IOException
	{
		raFile.write(v & 0xFF);
		raFile.write((v >>> 8) & 0xFF);
		raFile.write((v >>> 16) & 0xFF);
		raFile.write((v >>> 24) & 0xFF);
	}

	final void writeShort(int v) throws IOException
	{
		raFile.write(v & 0xFF);
		raFile.write((v >>> 8) & 0xFF);
	}

}
