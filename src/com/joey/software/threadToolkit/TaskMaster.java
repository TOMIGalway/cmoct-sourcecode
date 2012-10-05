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

import java.util.Vector;


public class TaskMaster implements Runnable
{
	int workerNumber = 0;

	int maxTaskNumber = 0;

	Thread thread = new Thread(this);

	boolean alive = true;

	boolean running = false;

	/**
	 * Task Holders
	 */
	Vector<Task> allTasks = new Vector<Task>();

	Vector<Task> waitingTasks = new Vector<Task>();

	Vector<Task> activeTasks = new Vector<Task>();

	/**
	 * Worker Tasks
	 */
	Vector<TaskWorker> waitingWorkers = new Vector<TaskWorker>();

	Vector<TaskWorker> activeWorkers = new Vector<TaskWorker>();

	// Locks
	Object workerLock = new Object();

	Object taskLock = new Object();

	/**
	 * Thsi will create a new task master. If max tasks has no limit it should
	 * be set to 0;
	 * 
	 * @param workerNumber
	 * @param maxTasks
	 */
	public TaskMaster(int workerNumber, int maxTasks)
	{
		this.workerNumber = workerNumber;
		this.maxTaskNumber = maxTasks;
		// Create Workers
		for (int i = 0; i < workerNumber; i++)
		{
			waitingWorkers.add(new TaskWorker(this));
		}

		// Make sure not running
		running = false;
		thread.start();
	}

	public void workerFinished(TaskWorker worker)
	{
		synchronized (workerLock)
		{
			synchronized (taskLock)
			{
				activeWorkers.remove(worker);
				waitingWorkers.add(worker);

				Task task = worker.getTask();
				activeTasks.remove(task);

				workerLock.notify();
				taskLock.notify();
			}
		}
	}

	public void start()
	{
		synchronized (workerLock)
		{
			synchronized (taskLock)
			{
				running = true;

				taskLock.notify();
				workerLock.notify();
			}
		}
	}

	public void pause()
	{
		synchronized (workerLock)
		{
			synchronized (taskLock)
			{
				running = false;
				taskLock.notify();
				workerLock.notify();
			}
		}
	}

	public synchronized void addTask(Task task)
	{

		synchronized (taskLock)
		{
			while (waitingTasks.size() > maxTaskNumber && maxTaskNumber != 0)
			{
				try
				{
					taskLock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			waitingTasks.add(task);
		}

		synchronized (workerLock)
		{
			workerLock.notify();
		}
	}

	public static void main(String input[])
	{
		TaskMaster master = new TaskMaster(2, 10);
		master.start();

		for(int i = 0; i < 10; i++)
		{

			final int num = i;
			master.addTask(new Task()
			{
				
				@Override
				public void doTask()
				{
					try
					{
						
						Thread.sleep(500);
						
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		
		master.waitForCompletion();
		
	}
	public void waitForCompletion()
	{
		while(running)
		{
			synchronized (taskLock)
			{
				if(waitingTasks.size() == 0 && activeTasks.size() == 0)
				{
					return;
				}
				try
				{
					taskLock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return;
	}
	@Override
	public void run()
	{
		while (alive)
		{
			synchronized (workerLock)
			{
				if (running)
				{
					// get Next Free worker (if available)
					if (waitingWorkers.size() > 0)
					{
						TaskWorker worker = waitingWorkers.get(0);

						// Get next Job (If available)
						if (waitingTasks.size() > 0)
						{
							// Get The Next Job
							synchronized (taskLock)
							{
								Task task = waitingTasks.get(0);

								// Remove from waiting tasks
								waitingTasks.remove(task);
								waitingWorkers.remove(worker);

								// Add to active tasks
								activeTasks.add(task);
								activeWorkers.add(worker);

								// Nofify task ready
								taskLock.notify();

								// Add to worker and launch
								worker.setTask(task);
								worker.doTask();
							}
						}
					}
				}

				if (waitingTasks.size() == 0 || waitingWorkers.size() == 0)
				{
					try
					{
						workerLock.wait();
					} catch (InterruptedException e)
					{
						System.out
								.println("Error while waiting with workerLocl");
						e.printStackTrace();
					}
				}
				
			}
		}

	}
}
