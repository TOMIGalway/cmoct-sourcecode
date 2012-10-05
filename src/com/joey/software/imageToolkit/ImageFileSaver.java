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
package com.joey.software.imageToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.ImageFileSelector;


public class ImageFileSaver
{
	int maxThreadCount = 4;

	int maxDataCount = 5;

	Stack<Job> jobs = new Stack<Job>();

	Object threadLock = new Object();

	Object dataLock = new Object();

	Object finishedLock = new Object();

	boolean alive = true;

	public static void main(String input[]) throws InterruptedException, IOException
	{
		ImageFileSaver saver = new ImageFileSaver(4, 10);
		File[] files = ImageFileSelector.getUserImageFile(true);
		int count = 0;
		for (File f : files)
		{
			saver
					.addData(f, ImageIO.read(f));
			System.out.println("Added [" + (count++) + "]");
		}

		System.out.println("About to wait for finish");
		saver.waitTillFinished(true);
		System.out.println("Done");

	}

	Stack<WorkerThread> workers = new Stack<WorkerThread>();

	public ImageFileSaver(int maxThreadcount, int maxDataCount)
	{
		setMaxDataCount(maxDataCount);
		createWorkers(maxThreadcount);
	}

	public void createWorkers(int maxThreadcount)
	{
		this.maxThreadCount = maxThreadcount;
		for (int i = 0; i < maxThreadcount; i++)
		{
			WorkerThread worker = new WorkerThread(this);
			Thread t = new Thread(worker);
			t.start();
		}
	}

	public void setMaxDataCount(int maxDataCount)
	{
		this.maxDataCount = maxDataCount;
	}

	public void addData(BufferedImage img,File f )
	{
		addData(f, img);
	}
	public void addData(File f, BufferedImage img)
	{
		if (jobs.size() > maxDataCount)
		{
			synchronized (dataLock)
			{
				try
				{
					dataLock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		String[] data = FileOperations.splitFile(f);

		Job j = new Job(img, f, data[2]);
		jobs.add(j);
		synchronized (threadLock)
		{
			threadLock.notify();
		}
	}

	public void kill()
	{
		alive = false;

		synchronized (threadLock)
		{
			threadLock.notifyAll();

		}
		synchronized (dataLock)
		{
			dataLock.notifyAll();
		}
	}

	public void NofifyWorkerStarted(WorkerThread t)
	{
		synchronized (workers)
		{
			workers.add(t);
		}
	}

	public void waitTillFinished()
	{
		waitTillFinished(true);
	}

	public void waitTillFinished(boolean kill)
	{
		synchronized (finishedLock)
		{
			try
			{
				finishedLock.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (kill)
		{
			kill();
		}
	}

	public void NotifyWorkerEnded(WorkerThread t)
	{
		synchronized (workers)
		{
			workers.remove(t);
		}
	}

	protected synchronized Job getJob(WorkerThread thread)
			throws InterruptedException
	{
		synchronized (dataLock)
		{
			dataLock.notify();
		}
		if (jobs.size() == 0)
		{
			synchronized (finishedLock)
			{
				finishedLock.notifyAll();
			}
		}
		do
		{
			if (jobs.size() > 0)
			{
				Job job = jobs.pop();
				return job;
			} else
			{
				synchronized (threadLock)
				{
					threadLock.wait();
				}
			}

			if (!alive)
			{
				return null;
			}

		} while (true);

	}
}

class Job
{
	static int num = 0;

	int count = num++;

	BufferedImage img;

	File f;

	String format;

	public Job(BufferedImage img, File f, String format)
	{
		this.img = img;
		this.f = f;
		this.format = format;
	}

	public boolean doJob()
	{
		try
		{
			ImageIO.write(img, format, f);
			return true;
		} catch (Exception e)
		{
			return false;
		}

	}
}

class WorkerThread implements Runnable
{
	static int num = 0;

	static int count = num++;

	boolean running = true;

	ImageFileSaver owner;

	public WorkerThread(ImageFileSaver owner)
	{
		this.owner = owner;
	}

	public void pauseThread()
	{
		running = false;

	}

	public synchronized void startThread()
	{
		running = true;
		notifyAll();
	}

	public String getDetails()
	{
		return "Worker [" + count + "]";
	}

	@Override
	public synchronized void run()
	{

		Job currentJob;
		owner.NofifyWorkerStarted(this);
		while (owner.alive)
		{
			if (running)
			{
				try
				{
					currentJob = owner.getJob(this);
					if (currentJob != null)
					{
						currentJob.doJob();
					}
				} catch (Exception e)
				{
					e.printStackTrace();
					System.out.println(e);
				}
			} else
			{
				try
				{
					wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		owner.NotifyWorkerEnded(this);
	}

}
