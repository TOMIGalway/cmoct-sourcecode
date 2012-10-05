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
package com.joey.software.OMAGTesting;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;

public class FastLoader
{
	float[][] output;

	Vector<LoadThread> threadActive = new Vector<LoadThread>();

	int wide = 0;

	int high = 0;

	int loadPos = 0;

	int frame = 0;

	int thread = 2;

	File file;

	Object finishLock = new Object();

	public FastLoader(File f, int wide, int high, int thread)
	{
		file = f;
		this.wide = wide;
		this.high = high;
		this.thread = thread;
		output = new float[wide][high];
	}

	public void loadFrame(int frame) throws InterruptedException, IOException
	{
		this.frame = frame;
		loadData();
		waitForFinish();
	}

	public synchronized void loadData() throws InterruptedException,
			IOException
	{
		loadPos = 0;

		// Remove Old threads

		// Add New Loaders
		for (int i = 0; i < thread; i++)
		{
			LoadThread loadThread = new LoadThread(this);
			loadThread.aScan = loadPos;
			loadPos++;
			threadActive.add(loadThread);
			loadThread.initLoader();
			loadThread.loadNext();
		}
	}

	private void waitForFinish()
	{
		synchronized (finishLock)
		{
			if (threadActive.size() == 0)
			{
				return;
			}
			try
			{
				finishLock.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void notifyLoaded(LoadThread t)
	{
		byte[] data = t.holder;
		for (int j = 0; j < output[t.aScan].length; j++)
		{
			output[t.aScan][j] = (short) (((data[j * 2] & 0xff) << 8) | (data[j * 2 + 1] & 0xff));
		}

		loadPos++;
		if (loadPos < wide)
		{
			t.aScan = loadPos;
		} else
		{
			threadActive.remove(t);
			t.active = false;
			t.alive = false;

			if (threadActive.size() == 0)
			{
				synchronized (finishLock)
				{
					finishLock.notifyAll();
				}
			}
			t.active = false;
		}
	}

	public static void main(String input[]) throws InterruptedException,
			IOException
	{
		File f = new File(
				"c:\\users\\joey.enfield\\desktop\\New Folder\\data1.txt");
		int wide = 2000;
		int high = 1024;

		FastLoader load = new FastLoader(f, wide, high, 10);

		int frame = 0;
		long start;
		while (frame++ < 220)
		{
			start = System.currentTimeMillis();
			load.loadFrame(frame);
			System.out.println(System.currentTimeMillis() - start);
		}

		DynamicRangeImage img = new DynamicRangeImage(load.output);
		FrameFactroy.getFrame(img);
	}
}

class LoadThread implements Runnable
{
	byte[] holder;

	FastLoader owner;

	Thread thread = new Thread(this);

	Object lock = new Object();

	boolean alive = true;

	boolean active = false;

	RandomAccessFile in;

	int aScan = 0;

	public LoadThread(FastLoader owner)
	{
		this.owner = owner;
		thread.start();
	}

	public void loadNext()
	{
		synchronized (lock)
		{
			active = true;
			lock.notifyAll();
		}
	}

	public void initLoader() throws IOException
	{
		in = new RandomAccessFile(owner.file, "r");
		holder = new byte[owner.high * 2];
	}

	public void loadData() throws IOException
	{
		in.seek(owner.frame * owner.wide * owner.high * 2 + 1 + aScan
				* owner.high * 2);
		in.read(holder);
	}

	@Override
	public void run()
	{
		try
		{
			while (alive)
			{
				synchronized (lock)
				{
					if (active)
					{
						loadData();
						owner.notifyLoaded(this);
					} else
					{
						lock.wait();
					}
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
