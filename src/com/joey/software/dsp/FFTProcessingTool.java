package com.joey.software.dsp;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImageProcessing;
import com.joey.software.timeingToolkit.EventTimer;


public class FFTProcessingTool
{
	float[][] specData;

	float[][] real;

	float[][] imaginary;

	float[][] mag;

	float[][] phase;

	int threads;

	int workingIndex = 0;

	Object lock = new Object();

	Vector<FFTWorkerTool> activeThreads = new Vector<FFTWorkerTool>();

	Vector<FFTWorkerTool> staticThreads = new Vector<FFTWorkerTool>();

	public static void main(String input[]) throws IOException
	{
		int powHor = 11;
		int powVer = 10;

		int wide = (int) Math.pow(2, powHor);
		int high = (int) Math.pow(2, powVer);

//		BufferedImage img = ImageIO.read(new File(
//				"C:\\Users\\joey.enfield\\Desktop\\IMG_3190.JPG"));
		BufferedImage inputImage = ImageOperations
				.getGrayTestImage(wide, high, (int) (0.5 * high), ImageOperations.X_AXIS);

		//ImageOperations.getResizeImage(img, inputImage);
		EventTimer t = new EventTimer();

		float[][] data = new float[wide][high];
		float[][] real = new float[wide][high];
		float[][] imag = new float[wide][high];
		float[][] mag = new float[wide][];
		float[][] phase = new float[wide][];

		t.mark("Image Buffer Grab");
		ImageProcessing.transformImageToGray(inputImage, data);
		t.tick("Image Buffer Grab");

		FFTProcessingTool tool = new FFTProcessingTool(2);

		t.mark("Process");
		tool.processData(data, real, imag, mag, phase);
		t.tick("Process");
		t.printData();
	}

	public FFTProcessingTool(int threads)
	{
		this.threads = threads;
	}

	public void processData(float[][] specData, float[][] real, float[][] imaginary, float[][] mag, float[][] phase)
	{
		this.specData = specData;
		this.real = real;
		this.imaginary = imaginary;
		this.mag = mag;
		this.phase = phase;

		if (staticThreads.size() != threads)
		{
			staticThreads.removeAllElements();
			for (int i = 0; i < threads; i++)
			{
				staticThreads.add(new FFTWorkerTool(this));
			}
		}
		workingIndex = specData.length - 1;
		while (true)
		{
			FFTWorkerTool worker = getFreeWorker();

			if (worker == null)
			{

				return;
			}

			worker.setWorkingIndex(workingIndex);
			workingIndex--;

			synchronized (activeThreads)
			{
				activeThreads.add(worker);
			}

			synchronized (staticThreads)
			{
				staticThreads.remove(worker);
			}
			Thread t = new Thread(worker);
			t.start();
		}
	}

	public FFTWorkerTool getFreeWorker()
	{
		while (true)
		{
			if (workingIndex < 0)
			{
				return null;
			}
			if (staticThreads.size() > 0)

			{
				return staticThreads.firstElement();
			} else
			{
				try
				{
					synchronized (lock)
					{
						lock.wait();
					}
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void notifyFinished(FFTWorkerTool worker)
	{
		synchronized (activeThreads)
		{
			activeThreads.remove(worker);
		}

		synchronized (staticThreads)
		{
			staticThreads.add(worker);
		}

		synchronized (lock)
		{
			lock.notify();
		}
	}
}

class FFTWorkerTool implements Runnable
{
	int workingIndex;

	FFTProcessingTool tool;

	FastFourierTransform3 fft;

	public FFTWorkerTool(FFTProcessingTool tool)
	{
		this.tool = tool;
		fft = new FastFourierTransform3(tool.specData[workingIndex].length);
	}

	public void setWorkingIndex(int id)
	{
		this.workingIndex = id;
	}

	@Override
	public void run()
	{

		fft
				.fft(tool.specData[workingIndex], null, tool.real[workingIndex], tool.imaginary[workingIndex], tool.mag[workingIndex], tool.phase[workingIndex]);

		tool.notifyFinished(this);

	}
}
