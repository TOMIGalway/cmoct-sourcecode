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

import java.util.Vector;

public class ProcessingMaster implements Runnable
{
	ProcessingJob job;

	boolean taskComplete = true;

	int[] taskSize = new int[0];

	int[] currentTask = new int[0];

	/**
	 * Worker Tasks
	 */
	Vector<ProcessingWorker> waitingWorkers = new Vector<ProcessingWorker>();

	Vector<ProcessingWorker> activeWorkers = new Vector<ProcessingWorker>();

	// Locks for workers waiting
	Object workerLock = new Object();

	// Lock locked when job completed
	Object taskFinishLock = new Object();

	Object processingLock = new Object();

	boolean alive = true;

	Thread t = new Thread(this);

	public ProcessingMaster(int threads)
	{
		setThreads(threads);
		t.start();
	}

	/**
	 * Can have a bug is active threads are
	 * available
	 * 
	 * @param num
	 */
	public void setThreads(int num)
	{
		// Bug as only waiting threads are checked
		// System.out.println("Setting Thread ProcessingMaster could have bugs!! Fix");

		int count = waitingWorkers.size();
		if (count > num)
		{
			for (int i = 0; i < (count - num); i++)
			{
				waitingWorkers.remove(0);
			}
		}
		if (count < num)
		{
			for (int i = 0; i < (num - count); i++)
			{
				waitingWorkers.add(new ProcessingWorker(this));
			}
		}
		updateWorkersDims();
	}

	private void setNDims(int dims)
	{
		if (taskSize == null || taskSize.length != dims)
		{
			taskSize = new int[dims];
			currentTask = new int[dims];
		}
		for (int i = 0; i < dims; i++)
		{
			taskSize[i] = 0;
			currentTask[i] = 0;
		}
		updateWorkersDims();
	}

	public void updateWorkersDims()
	{
		// Bug as only waiting threads are checked
		// System.out.println("Updating Workers ProcessingMaster could have bugs!! Fix");
		for (ProcessingWorker w : waitingWorkers)
		{
			w.updateDims();
		}
	}

	private void setDimSize(int dim, int size)
	{
		taskSize[dim] = size;
	}

	public int getNDims()
	{
		return taskSize.length;
	}

	public void workerFinished(ProcessingWorker worker)
	{
		synchronized (workerLock)
		{
			activeWorkers.remove(worker);
			waitingWorkers.add(worker);
			workerLock.notify();
		}

	}

	public void setJob(ProcessingJob job)
	{
		this.job = job;
		taskComplete = false;// Set completion to
								// 0
		setNDims(job.getNDim());
		for (int i = 0; i < job.getNDim(); i++)
		{
			setDimSize(i, job.getSizeDim(i));
		}
	}

	/**
	 * This will set the current position in the
	 * array
	 * 
	 * @param position
	 */
	private void getPosition(int[] position)
	{
		for (int i = 0; i < currentTask.length; i++)
		{
			position[i] = currentTask[i];
		}
	}

	public void incrementPosition()
	{
		int depth = 0;
		while (depth < taskSize.length)
		{
			currentTask[depth]++;
			if (currentTask[depth] >= taskSize[depth])
			{
				currentTask[depth] = 0;
				depth++;
			} else
			{
				return;
			}
		}
		taskComplete = true;
	}

	public boolean isProcessingRemaining()
	{
		return !taskComplete;
	}

	public boolean isActiveWorkers()
	{
		boolean act;
		synchronized (workerLock)
		{
			act = activeWorkers.size() == 0;
		}
		return act;
	}

	public int getSizeDim(int dim)
	{
		return taskSize[dim];
	}

	@Override
	public String toString()
	{
		StringBuilder rst = new StringBuilder();
		rst.append(" [ ");
		for (int i = 0; i < getNDims(); i++)
		{

			rst.append(currentTask[i]);
			rst.append("-");
			rst.append(taskSize[i]);
			rst.append(",");
		}
		rst.append(" ]\t\t");
		rst.append(super.toString());
		return rst.toString();
	}

	public void doJob(boolean waitForCompletion)
	{
		// Active the job
		synchronized (processingLock)
		{
			processingLock.notify();
		}

		if (waitForCompletion)
		{
			// wait for job to be completed
			synchronized (taskFinishLock)
			{
				try
				{
					taskFinishLock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void run()
	{
		while (alive)
		{
			synchronized (workerLock)
			{

				while (isProcessingRemaining())
				{
					if (waitingWorkers.size() > 0)
					{
						ProcessingWorker worker = waitingWorkers.get(0);
						int[] loc = worker.getPositionHolder();
						getPosition(loc);
						incrementPosition();
						waitingWorkers.remove(worker);
						activeWorkers.add(worker);
						worker.doProcessing();
					} else
					{
						try
						{
							workerLock.wait();
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			// This means job is complete, but
			// need to wait for workers to finish
			synchronized (workerLock)
			{
				if (activeWorkers.size() != 0)
				{
					try
					{
						workerLock.wait();
					} catch (InterruptedException e)
					{
						// TODO Auto-generated
						// catch block
						e.printStackTrace();
					}
				}
			}

			// Notify Task has been completed
			synchronized (taskFinishLock)
			{
				taskFinishLock.notify();
			}

			// Lock and wait for next processing
			synchronized (processingLock)
			{
				try
				{
					processingLock.wait();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

		}
	}

}
