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
package com.joey.software.framesToolkit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class FrameWaitForClose extends WindowAdapter
{
	JFrame owner;

	public static void showWaitFrame(String title)
	{
		JFrame f = FrameFactroy.getFrame();
		f.setTitle(title);
		FrameWaitForClose w =new FrameWaitForClose(f);
		w.waitForClose();
	}
	
	public static void showWaitFrame()
	{
		showWaitFrame("");
	}
	public FrameWaitForClose(JFrame f)
	{
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.addWindowListener(this);
	}

	Object lock = new Object();

	@Override
	public synchronized void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub
		super.windowClosed(e);
		synchronized (lock)
		{
			lock.notifyAll();
		}
	}

	@Override
	public synchronized void windowClosing(WindowEvent e)
	{
		// TODO Auto-generated method stub
		super.windowClosing(e);
		synchronized (lock)
		{

			lock.notifyAll();
		}
	}

	public void waitForClose()
	{
		synchronized (lock)
		{
			try
			{
				lock.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Out");
	}

	public static void waitForFrame(JFrame frame)
	{
		FrameWaitForClose close = new FrameWaitForClose(frame);
		close.waitForClose();
	}
}
