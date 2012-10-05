package com.joey.software.OMAGTesting;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.joey.software.imageToolkit.ImageOperations;

public class fftTesting
{

	public static void main(String input[]) throws IOException,
			InterruptedException
	{
		int wide = 2000;
		int high = 1024;

		int[] data = new int[wide * high];

		File f = new File(
				"c:\\users\\joey.enfield\\desktop\\New Folder\\data1.txt");

		BufferedImage img = ImageOperations.getGrayTestImage(wide, high, 20);

		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				data[y + high * x] = ImageOperations.getGrayScale(img
						.getRGB(x, y));
			}
		}

		FFT fft = new FFT(data, wide, high);

	}

	public static void getData(File f, double[][] output, int pos)
			throws IOException
	{
		RandomAccessFile in = new RandomAccessFile(f, "r");
		in.seek(pos * output.length * output[0].length * 2 + 1);

		byte[] holder = null;
		if (holder == null || holder.length != output[0].length * 2)
		{
			holder = new byte[output[0].length * 2];
		}

		for (int i = 0; i < output.length; i++)
		{
			in.read(holder);
			for (int j = 0; j < output[i].length; j++)
			{
				output[i][j] = (short) (((holder[j * 2] & 0xff) << 8) | (holder[j * 2 + 1] & 0xff));
			}
		}
	}
}
