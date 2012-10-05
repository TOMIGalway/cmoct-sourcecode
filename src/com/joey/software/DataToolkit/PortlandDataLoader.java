package com.joey.software.DataToolkit;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


public class PortlandDataLoader extends ImageProducer
{
	File file = null;

	int totalNum = 0;

	int wide = 0;

	int high = 0;

	double max = 4;

	double min = 2.2;

	byte[] loader;

	double[][] frameData;

	public PortlandDataLoader(File f)
	{
		this.file = f;
		totalNum = 250;
		wide = 512;
		high = 2000;

		loader = new byte[8 * wide * high];

	}

	public int getRGB(double data)
	{
		float val = (float) ((data - min) / (max - min));
		if (val > 1)
		{
			val = 0;
		}
		if (val < 0)
		{
			val = 0;
		}

		Color c = new Color(val, val, val);
		return c.getRGB();
	}

	@Override
	public BufferedImage getImage(int pos)
	{
		return null;
	}

	public BufferedImage saveImages() throws IOException
	{

		FileInputStream read = new FileInputStream(file);
		BufferedInputStream input = new BufferedInputStream(read, wide * high
				* 8);
		DataInputStream in = new DataInputStream(input);

		BufferedImage img = ImageOperations.getBi(wide, high);
		for (int i = 0; i < totalNum; i++)
		{
			System.out.println(i);
			in.read(loader);
			for (int y = 0; y < high; y++)
			{

				for (int x = 0; x < wide; x++)
				{
					int loc = (x + y * wide) * 8;
					double data = BinaryToolkit.readFlippedDouble(loader, loc);
					img.setRGB(x, y, getRGB(data));
				}
			}

			System.out.println(i);

			File out = new File(
					"c:\\users\\joey.enfield\\desktop\\Portland\\image"
							+ StringOperations.getNumberString(3, i) + ".png");

			ImageIO.write(img, "PNG", out);
		}
		return null;
	}

	@Override
	public int getImageCount()
	{
		// TODO Auto-generated method stub
		return totalNum;
	}

	public static void main(String input[]) throws IOException
	{

		// "C:\Users\joey.enfield\Desktop\RawImage2.txt"
		File f = new File("c:\\users\\joey.enfield\\desktop\\RawImage2.txt");
		// BinaryToolkit.binaryFileTool(f);

		PortlandDataLoader loader = new PortlandDataLoader(f);

		loader.saveImages();

	}
}
