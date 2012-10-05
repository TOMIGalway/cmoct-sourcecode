package com.joey.software.SwingTools;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.joey.software.imageToolkit.ImagePanel;

public class ActivityPanel extends JPanel implements Runnable
{
	boolean running = false;

	boolean alive = true;

	private int size = 15;

	BufferedImage highImage = new BufferedImage(size, size,
			BufferedImage.TYPE_INT_ARGB);

	BufferedImage lowImage = new BufferedImage(size, size,
			BufferedImage.TYPE_INT_ARGB);

	BufferedImage staticImage = new BufferedImage(size, size,
			BufferedImage.TYPE_INT_ARGB);

	ImagePanel imgPanel = new ImagePanel(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

	Color highColor = Color.RED;

	Color lowColor = Color.GREEN;

	Color staticColor = Color.BLUE;

	String title;

	long sleepTime = 100;

	Thread owner = new Thread(this);

	public ActivityPanel(String title, long sleepTime)
	{
		this.title = title;
		this.sleepTime = sleepTime;
		createJPanel();
		createImages();
		owner.start();
		imgPanel.setImage(staticImage);
	}

	public void setActive(boolean state)
	{
		running = state;
		if (running == true)
		{
			synchronized (this)
			{
				notifyAll();
			}
		}
	}

	public void setImageSize(int size)
	{
		this.size = size;
		createImages();
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		add(new JLabel(title), BorderLayout.WEST);

		add(imgPanel, BorderLayout.CENTER);
	}

	public void createImages()
	{
		BufferedImage imgs[] = new BufferedImage[]
		{ highImage, lowImage, staticImage };
		Color[] color = new Color[]
		{ highColor, lowColor, staticColor };
		for (int i = 0; i < 3; i++)
		{
			BufferedImage img = imgs[i];
			Graphics2D g = img.createGraphics();
			g.setColor(new Color(255, 255, 255, 0));
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.setColor(color[i]);
			g.fillOval(0, 0, img.getWidth(), img.getHeight());
		}
	}

	@Override
	public void run()
	{
		int count = 0;
		while (alive)
		{
			if (running)
			{
				if (count++ == 1)
				{
					count = 0;
					imgPanel.setImage(highImage);
				} else
				{
					imgPanel.setImage(lowImage);
				}
				try
				{
					Thread.sleep(sleepTime);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				imgPanel.setImage(staticImage);
				try
				{
					synchronized (this)
					{
						wait();
					}
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}