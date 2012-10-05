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
package com.joey.software.threadToolkit.processing;

public class ProcessingWorker implements Runnable
{
	Thread thread = new Thread(this);

	ProcessingMaster owner;

	int[] current;

	Object runningLock = new Object();

	boolean alive = true;

	boolean running = false;

	public ProcessingWorker(ProcessingMaster master)
	{
		this.owner = master;
		running = false;
		thread.start();
	}

	public void updateDims()
	{
		if (current == null || owner.getNDims() != current.length)
		{
			current = new int[owner.getNDims()];
		}
	}

	protected int[] getPositionHolder()
	{
		return current;
	}

	public void doProcessing()
	{
		synchronized (runningLock)
		{
			running = true;
			runningLock.notify();
		}
	}

	@Override
	public void run()
	{
		while (alive)
		{
			
			synchronized (runningLock)
			{	
				if (running)
				{
					owner.job.process(current);
					owner.workerFinished(this);
					running = false;
				}
				try
				{
					runningLock.wait();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
