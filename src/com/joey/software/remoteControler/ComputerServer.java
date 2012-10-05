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
package com.joey.software.remoteControler;


import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.timeingToolkit.TickerTimer;


public class ComputerServer
{
	BufferedImage screenImage;

	BufferedImage lastScreen;

	BufferedImage overlay;

	Robot robot;

	Rectangle bounds = new Rectangle();

	int renderQuality = GraphicsToolkit.LOW_QUALITY;

	TickerTimer time = new TickerTimer();

	int imageModel = BufferedImage.TYPE_INT_ARGB;

	Dimension screenSize = new Dimension(1024, 800);

	public ComputerServer() throws AWTException
	{
		robot = new Robot();
		updateBounds();
	}

	public void updateBounds()
	{

		bounds.width = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDisplayMode().getWidth();
		bounds.height = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDisplayMode().getHeight();

		screenImage = new BufferedImage(screenSize.width, screenSize.height,
				imageModel);
		lastScreen = new BufferedImage(screenSize.width, screenSize.height,
				imageModel);
		overlay = new BufferedImage(screenSize.width, screenSize.height,
				imageModel);
	}

	public void grabScreen()
	{
		BufferedImage img = robot.createScreenCapture(bounds);
		Graphics2D g = screenImage.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, renderQuality);
		g.drawImage(img, 0, 0, screenSize.width, screenSize.height, null);
		g.drawImage(overlay, 0, 0, screenSize.width, screenSize.height, null);
		notifyScreenChanged();
	}

	public void notifyScreenChanged()
	{
		time.tick();

	}

	public static void main(String input[]) throws AWTException
	{
		final ComputerServer server = new ComputerServer();

		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				while (true)
				{
					server.grabScreen();
				}
			}
		};

		server.time.resetTicker();
		t.start();

		ImagePanel p = new ImagePanel(server.screenImage);
		FrameFactroy.getFrame(p);
		while (true)
		{
			p.repaint();
			System.out.println(server.time.getTickRate());
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class ConnectionListner implements Runnable
{

	ServerSocket server;

	Thread thread = new Thread(this);

	boolean allowConnections = true;

	boolean alive = true;

	Socket client = null;

	public ConnectionListner(int port) throws IOException
	{
		server = new ServerSocket(port);
	}

	@Override
	public void run()
	{
		while (alive)
		{
			try
			{
				Socket socket = server.accept();

				socket.setKeepAlive(true);
				testSocket(socket);
			} catch (IOException e)
			{
				System.out.println("Connection IOException  : ");
				e.printStackTrace();
			}
		}
	}

	private void testSocket(Socket socket)
	{

	}
}
