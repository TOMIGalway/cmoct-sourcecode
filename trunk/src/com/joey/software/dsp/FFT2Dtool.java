package com.joey.software.dsp;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;

public class FFT2Dtool
{
	int wide = 0; // Frame Wide

	int high = 0; // Frame High

	float[][] dataHolder = null;

	float[][] maskHold = null;

	FloatFFT_2D fft = null;

	float lastSigmaX = -1;
	float lastSigmaY = -1;
	boolean isFlipped = false;

	public static void main(String input[])
	{
		int wide = 512;
		int high = 512;

		int block = 30;

		BufferedImage img = ImageOperations.getGrayTestImage(wide, high, 20);

		ImageOperations.fillWithRandomColorSquares(32, 32, img);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.white);
		g.drawLine(0, 0, wide, high);
		g.drawLine(wide, 0, 0, high);

		float[][] srcData = new float[wide][high];
		float[][] rstData = new float[wide][high];
		float[][] fftData = new float[wide][high];
		float[][] mask = new float[wide][high];

		createGaussianMask(mask, block,block);
		getImage(img, srcData);

		FFT2Dtool tool = new FFT2Dtool(wide, high);
		tool.allocateMemory();
		tool.clearDataHolder();

		tool.setRealData(srcData);
		//
		// // FFT
		tool.fftData();
		tool.fftFlip();

		// Block
		tool.mull(mask);
		tool.getMagData(fftData);

		tool.fftFlip();

		// IFFT
		tool.ifftData(true);
		tool.getMagData(rstData);

		FrameFactroy.getFrame(srcData, mask, fftData, rstData);
	}

	public void mull(float[][] scale)
	{
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				dataHolder[x][2 * y] *= scale[x][y];
				dataHolder[x][2 * y + 1] *= scale[x][y];
			}
		}

	}

	public void blockRegion(Shape s, float real, float imag, boolean inside)
	{
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				if (inside)
				{
					if (s.contains(x, y))
					{
						dataHolder[x][2 * y] = real;
						dataHolder[x][2 * y + 1] = imag;
					}
				} else
				{
					if (!s.contains(x, y))
					{
						dataHolder[x][2 * y] = real;
						dataHolder[x][2 * y + 1] = imag;
					}
				}
			}
		}

	}

	public void blockRegion(Rectangle rec, float real, float imag, boolean inside)
	{
		if (inside)
		{
			for (int x = rec.x; x < rec.x + rec.width && x < wide; x++)
			{
				for (int y = rec.y; y < rec.y + rec.height && y < high; y++)
				{
					dataHolder[x][2 * y] = real;
					dataHolder[x][2 * y + 1] = imag;
				}
			}
		} else
		{
			// Top
			blockRegion(new Rectangle(0, 0, wide, rec.y), real, imag, true);

			// //Bottom
			blockRegion(new Rectangle(0, rec.y + rec.height, wide, high), real, imag, true);

			// //Left
			blockRegion(new Rectangle(0, 0, rec.x, high), real, imag, true);

			// //Right
			blockRegion(new Rectangle(rec.x + rec.width, 0, wide, high), real, imag, true);
		}
	}

	public FFT2Dtool(int wide, int high)
	{
		setSize(wide, high);
	}

	public void fftFlip()
	{
		isFlipped = !isFlipped;
		lastSigmaX = (float) (-1000001*Math.random());
		lastSigmaY = (float) (-1000001*Math.random());
		performFFTFlip(dataHolder);
	}

	public static void performFFTFlip(float[][] dataHolder)
	{

		for (int x = 0; x < dataHolder.length; x++)
		{
			float tmp = 0;
			for (int y = 0; y < dataHolder[0].length / 2; y++)
			{
				tmp = dataHolder[x][y];
				dataHolder[x][y] = dataHolder[x][y + dataHolder[0].length / 2];
				dataHolder[x][y + dataHolder[0].length / 2] = tmp;
			}
		}

		for (int x = 0; x < dataHolder.length / 2; x++)
		{
			float[] tmp = null;
			tmp = dataHolder[x];
			dataHolder[x] = dataHolder[x + dataHolder.length / 2];
			dataHolder[x + dataHolder.length / 2] = tmp;

		}
	}

	public static void getImage(BufferedImage img, float[][] hold)
	{
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				hold[x][y] = ImageOperations
						.getPlaneFromRGBA(img.getRGB(x, y), ImageOperations.PLANE_GRAY);
			}
		}
	}

	/**
	 * ....._________ <BR>
	 * .../......../| <BR>
	 * .Z/......../.| <Br>
	 * ./________/../ <Br>
	 * Y|........|./ <BR>
	 * .|________|/ <BR>
	 * .....X..............
	 * 
	 * @param sizeX
	 * @param sizeY
	 * @param sizeZ
	 */
	public void setSize(int wide, int high)
	{
		this.wide = wide;
		this.high = high;

		fft = new FloatFFT_2D(wide, high);
	}

	public void allocateMemory()
	{
		dataHolder = new float[wide][2 * high];
	}

	public void freeMemory()
	{
		dataHolder = null;
		maskHold = null;
		System.gc();
	}

	public void setRealData(float[][] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				dataHolder[x][2 * y] = data[x][y];
			}
		}
	}

	public void setImagData(float[][] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				dataHolder[x][2 * y + 1] = data[x][y];
			}
		}
	}

	public void clearDataHolder()
	{
		for (int x = 0; x < dataHolder.length; x++)
		{
			for (int y = 0; y < dataHolder[x].length; y++)
			{
				dataHolder[x][y] = 0;
			}
		}
	}

	public void getMagData(float[][] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				data[x][y] = (float) Math.sqrt(dataHolder[x][2 * y]
						* dataHolder[x][2 * y] + dataHolder[x][2 * y + 1]
						* dataHolder[x][2 * y + 1]);
			}
		}
	}

	public void getRealData(float[][] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				data[x][y] = dataHolder[x][2 * y];
			}
		}
	}

	public void getImagData(float[][] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				data[x][y] = dataHolder[x][2 * y + 1];
			}
		}
	}

	public void fftData()
	{
		fft.complexForward(dataHolder);
	}

	public void ifftData(boolean scale)
	{
		fft.complexInverse(dataHolder, scale);
	}

	/**
	 * This function will create a guassian mask centered around the middle of the image
	 * if scale is used it will be calculated between 0 and 1
	 * @param data
	 * @param sigma
	 * @param scale
	 */
	public static void createGaussianMask(float[][] data, float sigmaX, float sigmaY)
	{
		float termA = (2*sigmaX*sigmaX);
		float termB = (2*sigmaY*sigmaY);

		float xPos = 0;
		float yPos = 0;

		
		for (int x = 0; x < data.length; x++)
		{
			xPos = data.length / 2f - x;
			xPos = (xPos * xPos)/termA;
			
			for (int y = 0; y < data[x].length; y++)
			{
				yPos = data[x].length / 2f - y;
				yPos = (yPos * yPos)/termB;
				
				data[x][y] = (float) (Math.exp(-(xPos+yPos)));
			}
		}
	}

	public void gaussianMask(float sigma)
	{
		gaussianMask(sigma, sigma);
	}
	/**
	 * This function will overlay the current data with a gussian mask
	 * suitable for guasssan blur; 
	 * @param sigma
	 */
	public void gaussianMask(float sigmaX, float sigmaY)
	{
		
		if (lastSigmaX != sigmaX || lastSigmaY != sigmaY)
		{
			lastSigmaX = sigmaX;
			lastSigmaY = sigmaY;
			createGaussianMask(getMask(), sigmaX,sigmaY);
			if (!isFlipped)
			{
				performFFTFlip(getMask());
			}
		}
		mull(getMask());
	}

	/**
	 * This function will perform a guassian on the current data, 
	 * This is achieved by FFT-Mask-iFFT
	 * @param sigma
	 */
	public void gaussianBlur(float sigmaX, float sigmaY, boolean scale)
	{
		fftData();
		gaussianMask(sigmaX, sigmaY);
		ifftData(scale);
	}

	public void gaussianBlur(float sigma,boolean scale)
	{
		gaussianBlur(sigma,sigma, scale);
	}
	public float[][] getMask()
	{
		if (maskHold == null)
		{
			maskHold = new float[wide][high];
		}
		return maskHold;
	}

	public void gaussianBlur(float[][] data, float sigma, boolean scale)
	{
		setRealData(data);
		gaussianBlur(sigma, scale);
		getMagData(data);
	}
}
