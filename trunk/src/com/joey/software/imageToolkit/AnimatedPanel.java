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

import javax.swing.JPanel;

/*******************************************************************************
 * This is a simple panael to handle all animations. I will constantly redraw
 * itself and try to mantain the chosen refresh rate. it can be made to stop
 * redrawing itself by calling its stop animation and started again by calling
 * its run animation. Once created the panel will not start running until its
 * unPause() method is called.
 * 
 * @author user
 */

public class AnimatedPanel extends JPanel implements Runnable
{

	int frameRate = 100; // wanted frame rate of panel.

	double realFrameRate = 0; // the frame reate that is being achieved

	Thread animationThread = null; // Thread for animation

	boolean running = false; // if the thread is running still

	boolean alive = true;

	public AnimatedPanel()
	{
		super(true);
		setIgnoreRepaint(true);
		if (animationThread == null)
		{
			animationThread = new Thread(this);
		}
	}

	public AnimatedPanel(int frameRate)
	{
		this();
		setFrameRate(frameRate);
	}

	/***************************************************************************
	 * This will start the panel updating itself or unpause
	 */
	public synchronized void Start()
	{
		animationThread.start();
		running = true;

		notifyAll();
	}

	public synchronized void unPause()
	{
		running = true;

		notifyAll();
	}

	/***************************************************************************
	 * This will stop the panel from updating itself and send it to sleep.
	 */
	public void pause()
	{
		running = false;
	}

	/***************************************************************************
	 * This will destroy the panel
	 */

	public void kill()
	{
		alive = false;
	}

	/***************************************************************************
	 * This is the controler of the thread it will run the animatino while it is
	 * still needed
	 */
	@Override
	public synchronized void run()
	{
		float sleepTime = 1000f / (frameRate);

		while (alive)
		{
			try
			{
				if (running)
				{// This is where the updating is done from.
					// if no graphics are available getGraphics() should return
					// a
					// null pointer
					try
					{
						paintImmediately(getBounds());
					} catch (NullPointerException e)
					{

					}

					Thread.sleep((long) sleepTime);

				} else
				{
					wait();
				}
			} catch (InterruptedException e)
			{
			}
		}
	}

	/**
	 * @return Returns the frameRate.
	 */
	public int getFrameRate()
	{
		return frameRate;
	}

	/**
	 * @param frameRate
	 *            The frameRate to set.
	 */
	private void setFrameRate(int frameRate)
	{
		this.frameRate = frameRate;
	}

	/**
	 * @return Returns the running.
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * @return the animationThread
	 */
	public Thread getAnimationThread()
	{
		return animationThread;
	}

}
