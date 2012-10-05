package com.joey.software.imageToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Stack;

import javax.imageio.ImageIO;

public class ImageFileLoader implements Runnable
{

	Object loadingFinishedWait = new Object();

	boolean finishedRunning = false;

	/**
	 * 
	 */
	HashMap<Integer, File> files = new HashMap<Integer, File>();

	/**
	 * This is notified when am imagloading failes
	 */
	ImageFileLoaderInterface output;

	/**
	 * This stores the static threads
	 */
	Stack<LoaderThread> staticThreads = new Stack<LoaderThread>();

	/**
	 * This stores the activeThreads
	 */
	Stack<LoaderThread> activeThreads = new Stack<LoaderThread>();

	/**
	 * 
	 */
	Object threadLock = new Object();

	/**
	 * 
	 * @param images
	 * @param output
	 * @param threadCount
	 */
	private ImageFileLoader(File[] images, ImageFileLoaderInterface output, int threadCount)
	{
		this.output = output;

		// Add files to the hashmap
		for (int i = 0; i < images.length; i++)
		{
			files.put(i, images[i]);
		}

		// Add the static threads
		for (int i = 0; i < threadCount; i++)
		{
			staticThreads.push(new LoaderThread(this));
		}
	}

	/**
	 * This function will allo multiple images to
	 * be loaded using a multithread system. The
	 * function will return once all the images
	 * have been loaded.
	 * 
	 * @param images
	 * @param output
	 * @param threadCount
	 */
	public static void loadImageFiles(File[] images, ImageFileLoaderInterface output, int threadCount, boolean waitFor)
	{
		ImageFileLoader loader = new ImageFileLoader(images, output,
				threadCount);
		Thread t = new Thread(loader);
		t.start();

		if (waitFor)
		{
			try
			{
				t.join();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void imageLoadComplete(LoaderThread thread)
	{
		try
		{
			output.imageLoaded(thread.img, thread.fileID);
		} catch (Exception e)
		{
			System.out.println("There was an error loading the image");
			e.printStackTrace();
		}
		tidyThread(thread);
	}

	public void imageLoadFaled(LoaderThread thread)
	{
		tidyThread(thread);
	}

	public void tidyThread(LoaderThread thread)
	{
		thread.img = null;

		/*
		 * Once Finished pass to static thread
		 * list and remove from the active thread
		 * list
		 */
		synchronized (staticThreads)
		{
			staticThreads.add(thread);
		}

		synchronized (activeThreads)
		{
			activeThreads.remove(thread);
			if (finishedRunning)
			{
				if (activeThreads.size() == 0)
				{
					synchronized (loadingFinishedWait)
					{
						loadingFinishedWait.notify();
					}

				}
			}
		}

		// Notify if any images are waiting to be
		// loaded
		synchronized (threadLock)
		{
			threadLock.notify();
		}
	}

	/**
	 * This will retrieve the next thread that is
	 * free. if there is no thread that is
	 * available it will wait untill threadLock is
	 * notifed.
	 * 
	 * @return
	 */
	public LoaderThread getNextLoaderThread()
	{
		do
		{
			// Attempt to return a free thread
			synchronized (staticThreads)
			{

				if (staticThreads.size() > 0)
				{
					activeThreads.add(staticThreads.peek());
					return staticThreads.pop();
				}
			}

			// If no thread is available wait
			// untill threadLock is
			// notified
			synchronized (threadLock)
			{
				try
				{
					threadLock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			}
		} while (true);

	}

	@Override
	public void run()
	{
		// While files are not available
		while (files.size() > 0)
		{
			Integer fileId = files.keySet().toArray(new Integer[0])[0];
			File file = files.get(fileId);
			files.remove(fileId);

			LoaderThread loader = getNextLoaderThread();
			loader.setData(file, fileId);

			Thread t = new Thread(loader);
			t.start();
		}

		/*
		 * This is to ensure the the program waits
		 * untill all of the threads have finished
		 * running before exiting
		 */
		finishedRunning = true;
		synchronized (loadingFinishedWait)
		{
			try
			{
				loadingFinishedWait.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

/**
 * This is the loader thread that handles loading
 * the image file from the drive
 * 
 * @author joey.enfield
 * 
 */
class LoaderThread implements Runnable
{
	int fileID;

	File file;

	BufferedImage img;

	ImageFileLoader loader;

	public LoaderThread(ImageFileLoader loader)
	{
		this.loader = loader;
	}

	public void setData(File file, int fileId)
	{
		this.file = file;
		this.fileID = fileId;
	}

	@Override
	public void run()
	{
		try
		{
			img = ImageIO.read(file);
			loader.imageLoadComplete(this);
		} catch (Throwable e)
		{
			System.out.println("Failed to load : " + file);
			e.printStackTrace();
			loader.imageLoadFaled(this);
		}

	}

}
