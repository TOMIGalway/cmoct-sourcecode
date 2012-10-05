package com.joey.software.DataToolkit;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.joey.software.VolumeToolkit.VolArrayFile;
import com.joey.software.VolumeToolkit.VolumeViewerPanel;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


public class TestingProcessing
{
	public static File[] createTestDataSet(String base, int sizeX, int sizeY, int sizeZ)
	{
		File[] file = new File[sizeZ];

		BufferedImage image;
		for (int z = 0; z < sizeZ; z++)
		{
			image = ImageOperations.getBi(sizeX, sizeY);
			for (int y = 0; y < sizeY; y++)
			{
				for (int x = 0; x < sizeX; x++)
				{
					int val = x;
					Color c = new Color(val, val, val);
					image.setRGB(x, y, c.getRGB());
				}
			}
			file[z] = new File(base + StringOperations.getNumberString(5, z)
					+ ".png");
			try
			{
				ImageIO.write(image, "png", file[z]);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}

	public static void main(String[] input) throws FileNotFoundException,
			IOException
	{
		File rawOut = new File("c:\\test\\micro\\raw.dat");
		File prvOut = new File("c:\\test\\micro\\prv.dat");

		// File rawOut = new File("c:\\test\\raw.dat");
		// File prvOut = new File("c:\\test\\prv.dat");

		// File[] file = createTestDataSet("c:\\test\\image\\image", 256, 256,
		// 256);
		//
		// OCTDataGeneratingTool
		// .createDataFiles(rawOut, prvOut, file, 1, new StatusBarPanel());
		// if (true)
		// {
		// return;
		// }

		VolumeHeaderData header = new VolumeHeaderData(new RandomAccessFile(
				rawOut.toString(), "r"));

		double gap = 0.3;
		double start = 0.3;

		int xMax = (int) (header.getSizeDataX() * (gap + start));
		int yMax = header.getSizeDataY();
		int zMax = header.getSizeDataZ();

		int xMin = (int) (header.getSizeDataX() * (start));
		int yMin = 0;
		int zMin = 0;

		int countX = 100;
		int countY = 512;
		int countZ = 512;

		byte[][][] output = new byte[countX][countY][countZ];
		ImageSliceToolkit
				.getVolumeRegionData(header, xMin, yMin, zMin, xMax, yMax, zMax, output);

		System.out.println();
		// System.out.println(header);
		for (int i = 0; i < output.length; i++)
		{
			int x = i;
			int y = 0;
			int z = 0;
			System.out.print(output[x][y][z] + ",");
		}

		VolumeViewerPanel view = new VolumeViewerPanel();
		// OCTDataSet data = new OCTDataSet(rawOut, prvOut);
		VolArrayFile file = new VolArrayFile(output, 1, 1, 1);
		view.getRender().setVolumeFile(file);
		//
		FrameFactroy.getFrame(new JPanel[]
		{ view, view.getControlPanel() }, 2, 1);
	}
}

class Testing1D
{
	public class Volume1D
	{
		int[] data;

		public Volume1D(int[] data)
		{
			this.data = data;
		}
	}

	public static int[] createRawData(int size)
	{
		int[] rawData = new int[size];

		for (int i = 0; i < size; i++)
		{
			rawData[i] = i;
		}
		return rawData;
	}

	public static Volume1D[] createVolumeData(int[] rawData, int size)
	{
		int count = (int) (Math.ceil(rawData.length / (double) size));
		Volume1D[] volData = new Volume1D[count];

		for (int i = 0; i < count; i++)
		{
			int[] dat = new int[size];
			for (int j = 0; j < size; j++)
			{
				if (i * size + j >= rawData.length)
				{
					dat[j] = 0;
				} else
				{
					dat[j] = rawData[i * size + j];
				}
			}
			volData[i] = (new Testing1D()).new Volume1D(dat);
		}
		return volData;
	}

	public static void printArrayData(int[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			if (i != 0)
			{
				System.out.printf(",");
			}
			System.out.printf("%2d", data[i]);
		}
	}

	public static void printVolData(Volume1D[] vol)
	{
		for (int i = 0; i < vol.length; i++)
		{
			printArrayData(vol[i].data);
			System.out.print("#");
		}

	}

	public static void getSubData(Volume1D[] volData, int minOut, int maxOut, int[] output)
	{
		int volSize = volData[0].data.length;

		double step = (double) (maxOut - minOut) / (double) (output.length - 1);

		int vMin = getVolSeg(volData, minOut);
		int vMax = getVolSeg(volData, maxOut);

		int count = 0;
		double d = getPosInVol(volData, minOut);

		for (int v = vMin; v <= vMax; v++)
		{
			Volume1D vol = volData[v];
			for (; d < volSize; d += step)
			{
				if (v * volSize + (int) d <= maxOut && count < output.length)
				{
					output[count++] = vol.data[(int) d];
				}
			}
			d = d - volSize;
		}
	}

	public static int getVolSeg(Volume1D[] volData, int pos)
	{
		return pos / volData[0].data.length;
	}

	public static int getPosInVol(Volume1D[] volData, int pos)
	{
		int seg = getVolSeg(volData, pos);
		return pos - seg * volData[0].data.length;
	}

	public static void main2(String[] input)
	{
		int dataSize = 1453;
		int volSize = 200;
		int outSize = 23;
		int outMin = 0;
		int outMax = 9;

		int[] rawData;
		Volume1D[] volData;
		int[] outData = new int[outSize];

		rawData = createRawData(dataSize);
		volData = createVolumeData(rawData, volSize);
		getSubData(volData, outMin, outMax, outData);

		System.out.println();
		printArrayData(rawData);
		System.out.println();
		printVolData(volData);
		System.out.println();
		printArrayData(outData);
	}

}