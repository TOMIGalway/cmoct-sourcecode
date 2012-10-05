package com.joey.software.MultiThreadCrossCorrelation.threads;


import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.joey.software.MultiThreadCrossCorrelation.CrossCorrelationDataset;
import com.joey.software.MultiThreadCrossCorrelation.ccVolumeSlicer;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.mainProgram.OCTAnalysis;


public class CrossCorrelationMaster implements Runnable
{
	CrossCorrelationDataset task = null;

	int workerNumber = -1;

	ThreadGroup group = new ThreadGroup("Processing Group");

	Thread thread = new Thread(this);

	boolean alive = true;

	boolean running = false;

	/**
	 * Worker Tasks
	 */
	Vector<CrossCorrelationWorker> waitingWorkers = new Vector<CrossCorrelationWorker>();

	Vector<CrossCorrelationWorker> activeWorkers = new Vector<CrossCorrelationWorker>();

	// Locks
	Object workerLock = new Object();

	Object taskLock = new Object();

	// Job Finished Lock
	Object jobFinishedLock = new Object();

	/**
	 * Thsi will create a new task master. If max
	 * tasks has no limit it should be set to 0;
	 * 
	 * @param workerNumber
	 * @param maxTasks
	 */
	public CrossCorrelationMaster(int workerNumber)
	{
		setThreadNumber(workerNumber);
		// Make sure not running
		running = false;
		thread.start();
	}

	public void setThreadNumber(int workerNumber)
	{
		if (this.workerNumber == workerNumber)
		{
			return;
		}
		this.workerNumber = workerNumber;

		synchronized (workerLock)
		{
			synchronized (taskLock)
			{
				// If Too many remove then
				if (waitingWorkers.size() + activeWorkers.size() > workerNumber)
				{
					// System.out.println("Too Many Threads");
					waitingWorkers.setSize(workerNumber - activeWorkers.size());
					activeWorkers.setSize(workerNumber - waitingWorkers.size());
				} else
				{
					// System.out.println("Making Threads");
					// Create Workers
					int num = activeWorkers.size() + waitingWorkers.size();
					for (int i = 0; i < workerNumber - num; i++)
					{
						CrossCorrelationWorker wrk = new CrossCorrelationWorker(
								this);
						wrk.setThreadPriority(Thread.MIN_PRIORITY);
						waitingWorkers.add(wrk);

					}
				}
			}
		}
	}

	public int getThreadNumber()
	{
		return activeWorkers.size() + waitingWorkers.size();
	}

	public void workerFinished(CrossCorrelationWorker worker)
	{
		synchronized (workerLock)
		{
			synchronized (taskLock)
			{
				activeWorkers.remove(worker);
				waitingWorkers.add(worker);

				if (task != null && !task.hasFramesRemaining()
						&& activeWorkers.size() == 0)
				{
					notifyTaskComplete(task);
				}
				workerLock.notify();
				taskLock.notify();
			}
		}
	}

	private void waitForJobToFinish()
	{
		synchronized (jobFinishedLock)
		{
			try
			{
				jobFinishedLock.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void start()
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

	public void setCurrentTask(CrossCorrelationDataset task)
	{
		task.createFrameProcessing((task.getInterlace()));
		synchronized (workerLock)
		{
			synchronized (taskLock)
			{

				this.task = task;

				for (CrossCorrelationWorker w : waitingWorkers)
				{
					w.allocateMemory();
				}

				for (CrossCorrelationWorker w : activeWorkers)
				{
					w.allocateMemory();
				}
			}
		}
	}

	public void notifyTaskComplete(CrossCorrelationDataset task)
	{
		try
		{
			task.saveMIP(false);
			task.unloadData();
			synchronized (jobFinishedLock)
			{

				jobFinishedLock.notify();
			}
			System.gc();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long processDataSet(boolean waitForFinish, boolean displayData)
			throws IOException
	{
		return processDataSet(waitForFinish, displayData, false);
	}

	public long processDataSet(boolean waitForFinish, boolean displayData, boolean displayTimes)
			throws IOException
	{
		return processDataSet(waitForFinish, displayData, displayTimes, null);
	}

	public long processDataSet(boolean waitForFinish, boolean displayData, boolean displayTimes, float[] times)
			throws IOException
	{

		if (displayTimes)
		{
			//Allocate Memory
			long time = System.nanoTime();
			task.allocateMemory();
			if(times != null)
			{
				times[0] = (System.nanoTime() - time)/ 1e6f; 
			}
			System.out.println("Allocate Memory :" + (System.nanoTime() - time)
					/ 1e6 + " ms");

			//Load Data To memory
			time = System.nanoTime();
			task.loadDataToMemory();
			if(times != null)
			{
				times[1] = (System.nanoTime() - time)/ 1e6f; 
			}
			System.out.println("Load Data To Memory:"
					+ (System.nanoTime() - time) / 1e6 + " ms");

			//Create Directories
			time = System.nanoTime();
			task.createDirectories();
			if(times != null)
			{
				times[2] = (System.nanoTime() - time)/ 1e6f; 
			}
			System.out.println("Create Directories :"
					+ (System.nanoTime() - time) / 1e6 + " ms");

		} else
		{
			task.allocateMemory();
			task.loadDataToMemory();
			task.createDirectories();
		}
		long start = System.currentTimeMillis();
		start();
		if (!waitForFinish)
		{
			return -1;
		}

		final DynamicRangeImage imgMIPData;
		final DynamicRangeImage imgMIPDepth;

		if (displayData)
		{
			imgMIPData = new DynamicRangeImage(task.MIPData);
			imgMIPDepth = new DynamicRangeImage(task.MIPDepth);
			imgMIPData.setPanelType(DynamicRangeImage.PANEL_TYPE_BASIC);
			imgMIPDepth.setPanelType(DynamicRangeImage.PANEL_TYPE_BASIC);
			JTabbedPane tab = new JTabbedPane();
			tab.addTab("MIP Data", imgMIPData);
			tab.addTab("MIP Depth", imgMIPDepth);

			final JFrame f = FrameFactroy.getFrame(tab);

			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			imgMIPData.setMinValue(task.getCrossCorrMin());
			imgMIPData.setMaxValue(task.getCrossCorrMax());

			imgMIPDepth.setRange(0, 512);

			Thread updateThread = new Thread()
			{
				@Override
				public void run()
				{
					while (f.isVisible())
					{
						imgMIPData.updateImagePanel();
						imgMIPDepth.updateImagePanel();
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated
							// catch block
							e.printStackTrace();
						}
					}
				};
			};
			updateThread.start();

			if (task.isCrossCorrRawInMemory())
			{
				ccVolumeSlicer sliceViewer = new ccVolumeSlicer();
				sliceViewer.setData(task);
				FrameFactroy.getFrame(sliceViewer)
						.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}

			if (task.isCrossCorrByteInMemory())
			{
				OCTAnalysis analysis = new OCTAnalysis();

				if (task.isDataInMemory())
				{
					analysis.setData(task.structData, "Struct");
				}
				analysis.setData(task.crossCorrByteData, "Flow");

				FrameFactroy.getFrame(analysis)
						.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}

			waitForJobToFinish();

			updateThread.stop();
			imgMIPData.updateImagePanel();
			imgMIPDepth.updateImagePanel();
		} else
		// Not Waiting for job to finish
		{
			waitForJobToFinish();
		}

		if (displayTimes)
		{
			if(times != null)
			{
				times[3] = (System.currentTimeMillis() - start); 
			}
			System.out.println("Processing Time : "
					+ (System.currentTimeMillis() - start) + " ms");
		}

		return System.currentTimeMillis() - start;
		// while (running)
		// {
		// synchronized (taskLock)
		// {
		// if (task == null
		// || (!task.hasFramesRemaining() &&
		// activeWorkers.size() == 0))
		// {
		// notifyTaskComplete(task);
		// return System.currentTimeMillis() -
		// start;
		// }
		// try
		// {
		// taskLock.wait();
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch
		// // block
		// e.printStackTrace();
		// }
		// }
		// }
		// notifyTaskComplete(task);
		// return System.currentTimeMillis() -
		// start;
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

					// get Next Free worker
					// (if available)
					if (waitingWorkers.size() > 0)
					{
						CrossCorrelationWorker worker = waitingWorkers.get(0);

						// Get next Job (If
						// available)
						if (task != null && task.hasFramesRemaining())
						{
							// Get The Next Job
							synchronized (taskLock)
							{
								int pos = task.getNextSlice();

								// Remove from
								// waiting tasks
								waitingWorkers.remove(worker);
								worker.setCurrentFrame(pos);
								// Add to active
								// tasks
								activeWorkers.add(worker);

								// Nofify task
								// ready
								taskLock.notify();

								// Add to worker
								// and launch

								worker.doTask();
							}
						}
					}
				}

				if (task == null
						|| (!task.hasFramesRemaining() || waitingWorkers.size() == 0))
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
