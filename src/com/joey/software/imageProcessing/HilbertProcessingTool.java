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
package com.joey.software.imageProcessing;

import java.util.Vector;

import com.joey.software.dsp.HilbertTransform;


public class HilbertProcessingTool
{
	float[][] specData;

	float[][] real;

	float[][] imaginary;

	float[][] mag;

	float[][] phase;

	int threads;

	int workingIndex = 0;

	Object lock = new Object();

	Vector<HilbertWorkerTool> activeThreads = new Vector<HilbertWorkerTool>();

	Vector<HilbertWorkerTool> staticThreads = new Vector<HilbertWorkerTool>();

	public HilbertProcessingTool(int threads)
	{
		this.threads = threads;
		for (int i = 0; i < threads; i++)
		{
			staticThreads.add(new HilbertWorkerTool(this));
		}
	}

	public void processData(float[][] specData, float[][] real, float[][] imaginary, float[][] mag, float[][] phase)
	{
		this.specData = specData;
		this.real = real;
		this.imaginary = imaginary;
		this.mag = mag;
		this.phase = phase;

		workingIndex = specData.length - 1;
		while (true)
		{
			HilbertWorkerTool worker = getFreeWorker();

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

	public HilbertWorkerTool getFreeWorker()
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

	public void notifyFinished(HilbertWorkerTool worker)
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

class HilbertWorkerTool implements Runnable
{
	int workingIndex;

	HilbertProcessingTool tool;

	HilbertTransform fft;

	public HilbertWorkerTool(HilbertProcessingTool tool)
	{
		this.tool = tool;
	}

	public void setWorkingIndex(int id)
	{
		this.workingIndex = id;
	}

	@Override
	public void run()
	{
		fft = new HilbertTransform();
		HilbertTransform
				.getHilbert(tool.specData[workingIndex], null, tool.real[workingIndex], tool.imaginary[workingIndex]);

		for (int i = 0; i < tool.specData[workingIndex].length; i++)
		{
			float reOut = tool.real[workingIndex][i];
			float imOut = tool.imaginary[workingIndex][i];

			tool.mag[workingIndex][i] = 2
					* (float) (Math.sqrt(reOut * reOut + imOut * imOut))
					/ tool.real[workingIndex].length;
			tool.phase[workingIndex][i] = (float) Math.atan2(imOut, reOut);

		}
		System.out.println(workingIndex);
		tool.notifyFinished(this);
	}
}
