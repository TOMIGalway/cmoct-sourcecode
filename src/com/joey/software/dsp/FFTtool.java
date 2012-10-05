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
package com.joey.software.dsp;

import com.joey.software.framesToolkit.FrameFactroy;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

public class FFTtool
{
	int points = 0; // Frame Wide

	float[] dataHolder = null;

	float[] maskHold = null;

	FloatFFT_1D fft = null;

	float lastSigma = -1;

	boolean isFlipped = false;

	public static void main(String input[])
	{
		int size = 512;
		float[] data = new float[size];
		
		
		//Fill
		int N = 3;
		int T = size/N;
		for(int i = 0;i < size;i++)
		{
			data[i] = 2+0.3f*(float)Math.random()+(float) Math.sin(2*Math.PI*i/T);
		}
		
		FrameFactroy.getFrame(data);
		FFTtool tool = new FFTtool(size);
		tool.allocateMemory();
		tool.clearDataHolder();
		tool.setRealData(data);
		tool.fftData();
		tool.gaussianBlur(100);
		tool.ifftData(true);
		tool.getMagData(data);
		FrameFactroy.getFrame(data);
	}
	public void mull(float[] scale)
	{
		for (int x = 0; x < points; x++)
		{
			dataHolder[2 * x] *= scale[x];
			dataHolder[2 * x + 1] *= scale[x];

		}

	}

	public FFTtool(int points)
	{
		setSize(points);
	}

	public void fftFlip()
	{
		isFlipped = !isFlipped;
		lastSigma = -1000001;
		performFFTFlip(dataHolder);
	}

	public static void performFFTFlip(float[] dataHolder)
	{

		for (int x = 0; x < dataHolder.length/2; x += 2)
		{
			float tmpR = 0;
			float tmpI = 0;
			tmpR = dataHolder[x];
			tmpI = dataHolder[x + 1];
			dataHolder[x] = dataHolder[x + dataHolder.length / 2];
			dataHolder[x + 1] = dataHolder[x + 1 + dataHolder.length / 2];

			dataHolder[x + dataHolder.length / 2] = tmpR;
			dataHolder[x + 1 + dataHolder.length / 2] = tmpI;
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
	public void setSize(int points)
	{
		this.points = points;
		fft = new FloatFFT_1D(points);
	}

	public void allocateMemory()
	{
		dataHolder = new float[2*points];
	}

	public void freeMemory()
	{
		dataHolder = null;
		maskHold = null;
		System.gc();
	}

	public void setRealData(float[] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			dataHolder[2 * x] = data[x];

		}
	}

	public void setImagData(float[] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			dataHolder[2 * x + 1] = data[x];
		}

	}

	public void clearDataHolder()
	{
		for (int x = 0; x < dataHolder.length; x++)
		{
			dataHolder[x] = 0;
		}
	}

	public void getMagData(float[] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{

			data[x] = (float) Math.sqrt(dataHolder[2 * x] * dataHolder[2 * x]
					+ dataHolder[2 * x + 1] * dataHolder[2 * x + 1]);

		}
	}

	public void getRealData(float[] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{
			data[x] = dataHolder[2 * x];
		}
	}

	public void getImagData(float[] data)
	{
		// Transfer to local holder
		for (int x = 0; x < data.length; x++)
		{

			data[x] = dataHolder[2 * x + 1];

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

	public static void createGaussianMask(float[] data, float sigma, boolean scale)
	{
		float termA = 2 * sigma;
		float termB = (float) (1 / (Math.PI * termA));

		float xPos = 0;
		float yPos = 0;

		float max = 0;
		for (int x = 0; x < data.length; x++)
		{
			xPos = data.length / 2f - x;
			xPos = xPos * xPos;

			data[x] = (float) (termB * Math.exp(-(xPos + yPos) / termA));
			if (x == 0)
			{
				max = data[x];
			} else
			{
				if (data[x] > max)
				{
					max = data[x];
				}
			}
		}

		if (scale)
		{
			for (int x = 0; x < data.length; x++)
			{
				data[x] /= max;
			}
		}
	}

	public void gaussianBlur(float sigma)
	{
		if (maskHold == null)
		{
			maskHold = new float[points];
		}

		if (lastSigma != sigma)
		{
			lastSigma = sigma;
			createGaussianMask(maskHold, sigma, true);
			if (!isFlipped)
			{
				performFFTFlip(maskHold);
			}
		}

		mull(maskHold);
	
	}

}
