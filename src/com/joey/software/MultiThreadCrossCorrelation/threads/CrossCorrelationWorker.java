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
package com.joey.software.MultiThreadCrossCorrelation.threads;

import com.joey.software.MultiThreadCrossCorrelation.alignment.AlignmentTool;
import com.joey.software.imageToolkit.ImageFileSaver;


public class CrossCorrelationWorker implements Runnable
{
	CrossCorrelationMaster master;

	int currentFrame = -1;

	Thread thread = new Thread(this);

	Object lock = new Object();

	boolean alive = true;

	byte[] outFrameByte;

	AlignmentTool alignTool = null;

	ImageFileSaver imageSave = new ImageFileSaver(4, 8);

	int gridA[];

	int gridB[];

	int pxlCount;

	float crossCorr;

	float avgProcess = 0;

	float avgSave = 0;

	public CrossCorrelationWorker(CrossCorrelationMaster master)
	{
		this.master = master;
		thread.start();
		
	}

	public void setThreadPriority(int threadPrior)
	{
		thread.setPriority(threadPrior);
	}
	public void setCurrentFrame(int frame)
	{
		this.currentFrame = frame;
	}

	public void doTask()
	{
		synchronized (lock)
		{
			lock.notify();
		}
	}

	public AlignmentTool getAlignmentTool()
	{
		if (alignTool == null)
		{
			alignTool = new AlignmentTool(master.task.getSizeX(),
					master.task.getSizeY());
		}
		return alignTool;
	}

	public void allocateMemory()
	{
		// Ensure Allocation
		outFrameByte = new byte[master.task.getSizeX() * master.task.getSizeY()];
		gridA = new int[(2 * master.task.getKerX() + 1)
				* (2 * master.task.getKerY() + 1)];
		gridB = new int[(2 * master.task.getKerX() + 1)
				* (2 * master.task.getKerY() + 1)];
	}

	public static float getCrossCorr(int[] gridA, int[] gridB, int threshold)
	{
		
		float avgA = getAverage(gridA);
		float avgB = getAverage(gridB);
		if (avgA < threshold || avgB < threshold)
		{
			return 1;
		}

		int i;
		float tA = 0;
		float tB = 0;
		float tC = 0;
		float t1 = 0;
		float t2 = 0;

		for (i = 0; i < gridA.length; i++)
		{
			
				t1 = gridA[i] - avgA;
				t2 = gridB[i] - avgB;
				tA += t1 * t2;
				tB += t1 * t1;
				tC += t2 * t2;
		}

		if (tB == 0 || tC == 0)
		{
			return 1;
		}
		return (float) (tA / Math.sqrt(tB * tC));
	}

	public static int getAverage(int data[])
	{
		int value = 0;
		for (int i = 0; i < data.length; i++)
		{
			value += data[i];
		}
		value = value / data.length;
		return value;
	}

	public void processFrame()
	{
		FrameProcessor.processFrame(this);
	}

	@Override
	public void run()
	{
		while (alive)
		{
			synchronized (lock)
			{
				if (currentFrame >= 0)
				{
					processFrame();
					master.workerFinished(this);
				}
				try
				{
					lock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			}

		}
	}
}
