package com.joey.software.Arrays;


import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class ArrayToolkit
{
	public static void main(String input[])
	{
		int wide = 2048;
		int high = 2048;
		byte[][] data = new byte[wide][high];

		fillRandom(data);
		BufferedImage img = ImageOperations.getImage(data);

		ImagePanel pan = new ImagePanel(img);
		FrameFactroy.getFrame(pan.getInPanel());
		int dx = -1;
		int dy = -1;

		while (true)
		{
			shift(data, dx, dy);
			ImageOperations.getImage(data, img);
			pan.repaint();
		}

	}

	/*
	 * ****************************************************************
	 * Start BYTE Functions
	 * ***********************
	 * ***************************************
	 */

	public static byte[][] transpose(byte[][] data)
	{
		byte[][] result = new byte[data[0].length][data.length];

		transpose(data, result);
		return result;
	}

	public static void transpose(byte[][] data, byte[][] result)
	{
		for (int x = 0; x < result.length; x++)
		{
			for (int y = 0; y < result[0].length; y++)
			{
				result[x][y] = data[y][x];
			}
		}
	}

	public static void transpose(byte[][] data, float[][] result)
	{
		for (int x = 0; x < result.length; x++)
		{
			for (int y = 0; y < result[0].length; y++)
			{
				result[x][y] = b2i(data[y][x]);
			}
		}
	}

	public static int b2i(byte val)
	{
		return (val >= 0 ? val : val + 256);
	}

	public static byte[][] clone(byte[][] data)
	{
		byte[][] result = new byte[data.length][data[0].length];
		clone(data, result);
		return result;
	}

	public static void clone(byte[][] data, byte[][] result)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				result[x][y] = data[x][y];
			}
		}

	}

	public static void fillRandom(byte[][] data)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				data[x][y] = (byte) (255 * Math.random());
			}
		}
	}

	public static void shift(byte[][] data, int dx, int dy)
	{
		if (dx > 0)
		{
			for (int x = 0; x < dx; x++)
			{
				shiftRight(data);
			}
		} else if (dx < 0)
		{
			for (int x = 0; x < -dx; x++)
			{
				shiftLeft(data);
			}
		}

		if (dy > 0)
		{
			for (int y = 0; y < dy; y++)
			{
				shiftUp(data);
			}
		} else if (dy < 0)
		{
			for (int y = 0; y < -dy; y++)
			{
				shiftDown(data);
			}
		}

	}

	public static void shiftUp(byte[][] data)
	{
		byte temp;

		for (int x = 0; x < data.length; x++)
		{
			temp = data[x][0];
			for (int y = 0; y < data[0].length - 1; y++)
			{

				data[x][y] = data[x][y + 1];
			}
			data[x][data[0].length - 1] = temp;
		}
	}

	public static void shiftDown(byte[][] data)
	{
		byte temp;

		for (int x = 0; x < data.length; x++)
		{
			temp = data[x][data[0].length - 1];
			for (int y = data[0].length - 1; y > 0; y--)
			{
				data[x][y] = data[x][y - 1];
			}
			data[x][0] = temp;
		}
	}

	public static void shiftLeft(byte[][] data)
	{
		byte temp;
		for (int y = 0; y < data[0].length; y++)
		{
			temp = data[0][y];
			for (int x = 0; x < data.length - 1; x++)
			{
				data[x][y] = data[x + 1][y];
			}
			data[data.length - 1][y] = temp;
		}
	}

	public static void shiftRight(byte[][] data)
	{
		byte temp;
		for (int y = 0; y < data[0].length; y++)
		{
			temp = data[data.length - 1][y];
			for (int x = data.length - 1; x > 0; x--)
			{
				data[x][y] = data[x - 1][y];
			}
			data[0][y] = temp;
		}
	}

	public static void setValue(byte[][] data, byte val)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				data[x][y] = val;
			}
		}
	}

	public static void addValue(byte[][] data, byte val)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				data[x][y] += val;
			}
		}
	}

	public static void printArray(byte[][] data)
	{

		for (int y = 0; y < data[0].length; y++)
		{
			for (int x = 0; x < data.length; x++)
			{
				System.out.print(data[x][y] + ",");
			}
			System.out.println();
		}
	}

	/*
	 * *****************************************************
	 * Float Functions
	 * ****************************
	 * *************************
	 */

	public static void printArray(float[][] data)
	{
		for (int y = 0; y < data[0].length; y++)
		{
			for (int x = 0; x < data.length; x++)
			{
				System.out.print(data[x][y] + ",");
			}
			System.out.println();
		}
	}

	public static void add(float[][] data, float[][] values)
	{
		for (int y = 0; y < data[0].length; y++)
		{
			for (int x = 0; x < data.length; x++)
			{
				data[x][y] += values[x][y];
			}
		}
	}

	public static void setValue(float[][] data, float val)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				data[x][y] = val;
			}
		}
	}

	/**
	 * This will smooth data using a kernal blur
	 * 
	 * @param data
	 * @param rst
	 * @param kerX
	 * @param kerY
	 */
	public static void smoothData(float[][] data, float[][] rst, int kerX, int kerY)
	{
		int count = 0;
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				count = 0;
				rst[x][y] = 0;

				for (int xP = x - kerX; xP <= x + kerX; xP++)
				{
					for (int yP = y - kerY; yP <= y + kerY; yP++)
					{
						if (xP >= 0 && yP >= 0 && xP < data.length
								&& yP < data[0].length)
							rst[x][y] += data[xP][yP];
						count++;
					}
				}
				if (count == 0)
				{
					count = 1;
				}
				rst[x][y] /= count;

			}
		}
	}

	public static void scale(float[][] data, float values)
	{
		for (int y = 0; y < data[0].length; y++)
		{
			for (int x = 0; x < data.length; x++)
			{
				data[x][y] *= values;
			}
		}
	}

	public static float getAverage(float[][] data)
	{
		float avg = 0;
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				avg += data[x][y];
			}
		}
		return avg / (data.length * data[0].length);
	}

	public static float getSum(float[][] data)
	{
		float sum = 0;
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				sum += data[x][y];
			}
		}
		return sum;
	}

	public static void setValue(float[][] data, Shape s, float value, boolean inside)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				if (s.contains(x, y) == inside)
				{
					data[x][y] = value;
				}
			}
		}
	}

	public static void setValue(float[][] data, Rectangle rec, float value, boolean inside)
	{
		if (inside)
		{
			for (int x = rec.x; x < rec.x + rec.width && x < data.length; x++)
			{
				for (int y = rec.y; y < rec.y + rec.height
						&& y < data[0].length; y++)
				{
					data[x][y] = value;
				}
			}
		} else
		{
			// Top
			setValue(data, new Rectangle(0, 0, data.length, rec.y), value, true);

			// //Bottom
			setValue(data, new Rectangle(0, rec.y + rec.height, data.length,
					data[0].length), value, true);

			// //Left
			setValue(data, new Rectangle(0, 0, rec.x, data[0].length), value, true);

			// //Right
			setValue(data, new Rectangle(rec.x + rec.width, 0, data.length,
					data[0].length), value, true);
		}
	}

	public static float[][] clone(float[][] data)
	{
		float[][] result = new float[data.length][data[0].length];
		clone(data, result);
		return result;
	}

	public static void clone(float[][] data, float[][] result)
	{
		// TODO Auto-generated method stub
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				result[x][y] = data[x][y];
			}
		}
	}

	public static void fillRandom(byte[][][] data)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					data[x][y][z] = (byte) (256 * Math.random());
				}
			}
		}

	}

	/**
	 * This will get the average value from the
	 * array [x][y] from the region [x,y] to
	 * [x+wide][y+high] (it only averages data
	 * inside bounds of the input array
	 * 
	 * @param data
	 * @param x
	 * @param numX
	 * @param y
	 * @param numY
	 */
	public static float getAverage(float[][] data, int xPos, int wide, int yPos, int high)
	{
		float avg = 0;
		int count = 0;
		for (int x = xPos; x < xPos + wide; x++)
		{
			for (int y = yPos; y < yPos + high; y++)
			{
				if (x >= 0 && y >= 0 && x < data.length && y < data[x].length)
				{
					count++;
					avg += data[x][y];
				}
			}
		}

		if (count == 0)
		{
			count++;
		}
		return avg / count;
	}

	public static void clone(byte[][][] data, byte[][][] result)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					result[x][y][z] = data[x][y][z];
				}
			}
		}

	}

	public static void fillRandom(byte[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			data[i] = (byte) (255 * Math.random());
		}
	}

	public static void setValue(byte[] data, byte val)
	{
		for (int i = 0; i < data.length; i++)
		{
			data[i] = val;
		}
	}

	public static void setValue(byte[][][] data, byte val)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					data[x][y][z] = val;
				}
			}
		}
	}

	/**
	 * Data[x][y][z];
	 * @param data
	 * @param sX
	 * @param eX
	 * @param sY
	 * @param eY
	 * @param sZ
	 * @param eZ
	 * @param val
	 */
	public static void setValue(byte[][][] data, int sX, int eX, int sY, int eY, int sZ, int eZ, byte val)
	{
		for (int x = sX; x < data.length && x <= eX && x >= 0; x++)
		{
			for (int y = sY; y < data[x].length && y <= eY && y >= 0; y++)
			{
				for (int z = sZ; z < data[x][y].length && z <= eZ && z >= 0; z++)
				{
					data[x][y][z] = val;
				}
			}
		}
	}
	
	public static void setValueX(byte[][][] data, int start, int end, byte val)
	{
		setValue(data, start, end, 0, data[0].length, 0, data[0][0].length, val);
	}
	
	public static void setValueY(byte[][][] data,  int start, int end, byte val)
	{
		setValue(data, 0,data.length,start, end, 0, data[0][0].length, val);
	}
	
	public static void setValueZ(byte[][][] data,  int start, int end, byte val)
	{
		setValue(data, 0,data.length,0, data[0].length, start,end,val);
	}

	public static void fillRandom(float[] values)
	{
		for(int i = 0; i < values.length; i++)
		{
			values[i] = (float)(Math.random());
		}
		
	}

	public static void add(float[] valA, float[] valB, float[] rst)
	{
		for(int i = 0; i < valA.length; i++){
			rst[i] = valA[i]+valB[i];
		}
		
	}

	public static void setValue(float[] meanAScan, float val)
	{
		Arrays.fill(meanAScan, val);
	}

	public static void scale(float[] meanAScan, float values)
	{
		for(int i = 0; i < meanAScan.length; i++){
			meanAScan[i]*=values;
		}
		
	}

	/**
	 * rst = valA-valB;
	 * @param valA
	 * @param valB
	 * @param rst
	 */
	public static void subtract(float[] valA, float[] valB, float[] rst)
	{
		for(int i = 0; i < valA.length; i++){
			rst[i] = valA[i]-valB[i];
		}
	}

	public static float getMax(float[][] rawFrame)
	{
		float max = rawFrame[0][0];
		for(int x= 0; x < rawFrame.length; x++){
			for(int y = 0; y < rawFrame[x].length; y++){
				if(max<rawFrame[x][y]){
					max = rawFrame[x][y];
				}
			}
		}
		return max;
	}
}
