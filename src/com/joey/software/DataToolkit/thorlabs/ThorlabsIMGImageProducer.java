package com.joey.software.DataToolkit.thorlabs;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


public class ThorlabsIMGImageProducer extends ThorlabsImageProducer
{

	// This byte array is used to hold the pixels
	// when loading a frame
	byte[] pxlHolder = new byte[0];

	public ThorlabsIMGImageProducer(File fileSource) throws IOException
	{
		this(fileSource, false);
	}

	public ThorlabsIMGImageProducer(File fileSource, boolean forceSingle)
			throws IOException
	{
		setFile(fileSource, forceSingle);
	}

	@Override
	protected void allocateMemory()
	{
		frmLenBytes = 40 + sizeX * sizeY; // 40
											// bytes
											// is
											// the
											// length
											// of
											// sub
		// hearder of the image frame
		frmPixels = sizeX * sizeY;

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}
	}

	@Override
	public void getImage(int pos, BufferedImage img) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(getCorrectFile(pos))));

		in.skip(getFileOffset(pos) + 40);

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		// Frame Info
		in.read(pxlHolder);
		in.close();
		return (getImage(pxlHolder, sizeY, sizeX));
	}

	@Override
	public void getImage(int frameNum, byte data[], int pos) throws IOException
	{

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(getCorrectFile(pos))));

		in.skip(getFileOffset(frameNum) + 40);

		in.read(data, pos, frmPixels);
		in.close();
	}

	public static void main(String input[]) throws IOException
	{

		
		ThorlabsIMGImageProducer t = new ThorlabsIMGImageProducer(
				FileSelectionField.getUserFile(), true);

		byte[][] frame = new byte[t.getSizeX()][t.getSizeY()];
		
		DynamicRangeImage img = new DynamicRangeImage(frame);
		FrameFactroy.getFrame(img);
		for (int i = 0; i < 256; i++)
		{
			t.getImage(i, frame);
			System.out.println(t.getFrameTime(i));
			img.updateImagePanel();
		}

	}

	public static void ExportFrames() throws IOException
	{
		ThorlabsIMGImageProducer t = new ThorlabsIMGImageProducer(
				FileSelectionField.getUserFile(), true);

		FileSelectionField saveFolder = new FileSelectionField();
		saveFolder.setFormat(FileSelectionField.FORMAT_FOLDERS);
		saveFolder.getUserChoice();

		String[] loc = FileOperations.splitFile(saveFolder.getFile());

		ImageFileSaver imageSaver = new ImageFileSaver(4, 8);
		for (int i = 0; i < t.getImageCount(); i++)
		{
			BufferedImage img = t.getImage(i);
			File f = new File(loc[0] + "\\" + "image"
					+ StringOperations.getNumberString(4, i) + ".png");
			imageSaver.addData(img, f);
		}
		imageSaver.waitTillFinished();
	}

	public int getFrameTime(int frame) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(getCorrectFile(frame))));

		in.skip(getFileOffset(frame));

		byte[] data = new byte[4];
		in.read(data);
		in.close();
		return (BinaryToolkit.readFlippedInt(data, 0));

	}

	/**
	 * The data is stored in the form data[x*
	 * wide*high + high * x + y]
	 * 
	 * @param frameNum
	 * @param data
	 * @param pos
	 * @throws IOException
	 */
	@Override
	public void getImage(int frameNum, int data[], int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(getCorrectFile(pos))));

		in.skip(getFileOffset(frameNum) + 40);

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		// Frame Info
		in.read(pxlHolder);

		for (int i = 0; i < frmPixels; i++)
		{
			if (pxlHolder[i] < 0)
			{
				data[i + pos] = 256 + pxlHolder[i];
			} else
			{
				data[i + pos] = pxlHolder[i];
			}
		}
		in.close();
	}

	/**
	 * 
	 * @param pos
	 * @param data
	 *            data[X-Data][Y-data]
	 * @throws IOException
	 */
	@Override
	public synchronized void getImage(int pos, byte[][] data) throws IOException
	{

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(getCorrectFile(pos))));

		in.skip(getFileOffset(pos) + 40);

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		// Frame Info
		in.read(pxlHolder);
		for (int x = 0; x < data.length; x++)
		{
			System.arraycopy(pxlHolder, x* data[0].length, data[x], 0, data[0].length);
		}
		in.close();
	}

	private static void getImage(byte[] data, byte[][] out)
	{
		for (int x = 0; x < out.length; x++)
		{
			for (int y = 0; y < out[0].length; y++)
			{
				out[x][y] = data[x + y * out.length];
			}
		}
	}

	private static BufferedImage getImage(byte[] data, int wide, int high)
	{
		BufferedImage img = ImageOperations.getBi(high, wide);
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				img.setRGB(y, x, NativeDataSet.getByteToRGB(data[x + y * wide]));
			}
		}
		return img;
	}

	@Override
	public void getData(byte[][][] data, StatusBarPanel status)
			throws IOException
	{

		if (pxlHolder.length != data[0].length * data[0][0].length)
		{
			pxlHolder = new byte[data[0].length * data[0][0].length];
		}

		// Frame Info
		if (status != null)
		{
			status.setMaximum(data.length);
		}

		for (int x = 0; x < data.length; x++)
		{
			RandomAccessFile in = new RandomAccessFile(getCorrectFile(x), "r");
			in.seek(getFileOffset(x + 1) + 40);
			if (status != null)
			{
				status.setValue(x);
			}
			// Frame Info
			in.read(pxlHolder);
			for (int y = 0; y < data[0].length; y++)
			{
				System.arraycopy(pxlHolder, y * data[0][0].length, data[x][y], 0, data[0][0].length);
			}
			in.close();
		}
	}

}
