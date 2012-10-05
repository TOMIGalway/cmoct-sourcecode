package com.joey.software.DataToolkit;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


@Deprecated
public class ThorLabs3DImageProducer extends ImageProducer
{
	String fileId;

	int totalFrames;

	int sizeZ;

	int sizeX;

	int sizeY;

	int num3D;

	int frmLenBytes;

	int frmPixels;

	byte[] pxlHolder = new byte[0];

	File f;

	public ThorLabs3DImageProducer(File f) throws IOException
	{
		setFile(f);
	}

	public void setFile(File f) throws IOException
	{
		this.f = f;
		byte[] data = new byte[36];
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		in.read(data, 0, 16);
		fileId = (byteToString(data, 16));

		in.read(data, 0, 4);
		sizeZ = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeX = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeY = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		totalFrames = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		num3D = BinaryToolkit.readFlippedInt(data, 0);

		in.skip(4 * 118);

		frmLenBytes = 40 + sizeX * sizeY; // 40 bytes is the length of sub
		// hearder of the image frame
		frmPixels = sizeX * sizeY;
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		in.skip(getFileOffset(pos));

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		in.skip(40);
		// Frame Info
		in.read(pxlHolder);
		return (getImage(pxlHolder, sizeY, sizeX));
	}

	public void getImageData(int frameNum, int data[], int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		in.skip(getFileOffset(frameNum));

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		in.skip(40);
		// Frame Info
		in.read(pxlHolder);
		
		for(int i =0; i < data.length; i++)
		{
			data[i+pos] = pxlHolder[i];
		}
	}
	private int getFileOffset(int pos)
	{
		int base = 512;
		return base+(pos) * frmLenBytes;
	}

	@Override
	public int getImageCount()
	{
		// TODO Auto-generated method stub
		return sizeZ;
	}

	public static void main(String input[]) throws IOException
	{

		// File f = new File("c:\\test\\data.img");

		
		File f = FileSelectionField.getUserFile(new String[]{"img:Thorlabs IMG File(.img)" });

		ThorLabs3DImageProducer prod = new ThorLabs3DImageProducer(f);
		
		
		String[] data = FileOperations.splitFile(f);
		String folder = data[0] + "\\" + data[1] + "\\";

		File newF = new File(data[0] + "\\" + data[1] + "\\");
		newF.getParentFile().mkdirs();
		System.out.println(newF);
		newF.mkdirs();
		
		String base = "struct";
		String format = "jpg";
		
		ImageFileSaver save = new ImageFileSaver(5, 8);
	
		for(int i = 0; i < prod.getImageCount(); i++)
		{
			System.out.println(i+" of "+(prod.getImageCount()-1));
			BufferedImage img =prod.getImage(i);
			img = ImageOperations.getRotatedImage(img, +1);
			ImageOperations.flipImage(img, ImageOperations.Y_AXIS);
			save.addData(new File(folder + "struct" + base
					+ StringOperations.getNumberString(5, i) + "."
					+ format), img);
			
		}
	}

	public static String byteToString(byte[] data, int count)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < count; i++)
		{
			rst.append((char) data[i]);
		}
		return rst.toString();
	}

	public static BufferedImage getImage(byte[] data, int wide, int high)
	{
		BufferedImage img = ImageOperations.getBi(wide, high);
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				img
						.setRGB(x, y, NativeDataSet.getByteToRGB(data[x + y
								* wide]));
			}
		}
		return img;
	}
}
