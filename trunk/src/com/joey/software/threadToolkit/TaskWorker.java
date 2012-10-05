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
package com.joey.software.threadToolkit;


public class TaskWorker implements Runnable
{
	TaskMaster master;

	Thread thread = new Thread(this);

	Object lock = new Object();

	boolean alive = true;

	Task task = null;

	public TaskWorker(TaskMaster master)
	{
		this.master = master;
		thread.start();
	}

	public void doTask()
	{
		synchronized (lock)
		{
			lock.notify();
		}
	}

	public void setTask(Task task)
	{
		synchronized (lock)
		{
			this.task = task;
		}
	}

	public Task getTask()
	{
		return task;
	}

	@Override
	public void run()
	{
		while (alive)
		{
			synchronized (lock)
			{
				if (task != null)
				{
					
					task.doTask();
					
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
