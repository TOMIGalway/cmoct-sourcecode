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
package com.joey.software.MultiThreadCrossCorrelation.alignment;

import ij.ImagePlus;
import ij.process.ByteProcessor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImagePanel;


public class MultithreadAlignTest
{
	public static void main(String input[])
	{
		for (int i = 0; i < 4; i++)
		{
			AlignThread thread1 = new AlignThread(1024, 512);
			FrameFactroy.getFrame(thread1);
		}

	}
}

class AlignThread extends JPanel implements Runnable
{
	ImageAlignTool tool = new ImageAlignTool();

	ImagePlus source;

	ImagePlus target;

	ImagePlus result;

	byte[] sourceData;

	byte[] resultData;

	byte[] targetData;

	ByteProcessor sourceProcessor;

	ByteProcessor targetProcessor;

	ByteProcessor resultProcessor;

	int wide = 0;

	int high = 0;

	ImagePanel sourcePanel = new ImagePanel();

	ImagePanel targetPanel = new ImagePanel();

	ImagePanel resultPanel = new ImagePanel();

	JTextField label = new JTextField();

	public AlignThread(int wide, int high)
	{
		this.wide = wide;
		this.high = high;
		allocate();
		createJPanel();

		Thread t = new Thread(this);
		t.start();

	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		add(label, BorderLayout.NORTH);

		sourcePanel.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		targetPanel.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		resultPanel.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("source", sourcePanel);
		tab.addTab("Target", targetPanel);
		tab.addTab("Result", resultPanel);

		add(tab, BorderLayout.CENTER);
	}

	public void allocate()
	{
		sourceData = new byte[wide * high];
		resultData = new byte[wide * high];
		targetData = new byte[wide * high];

		sourceProcessor = new ByteProcessor(wide, high);
		targetProcessor = new ByteProcessor(wide, high);
		resultProcessor = new ByteProcessor(wide, high);

		sourceProcessor.setPixels(sourceData);
		targetProcessor.setPixels(resultData);
		resultProcessor.setPixels(targetData);

		source = new ImagePlus("Source", sourceProcessor);
		target = new ImagePlus("Target", targetProcessor);
		result = new ImagePlus("Result", resultProcessor);
	}

	public void clearImage()
	{
		sourceProcessor.setColor(0);
		targetProcessor.setColor(0);
		resultProcessor.setColor(0);

		sourceProcessor.fill();
		targetProcessor.fill();
		resultProcessor.fill();
	}

	public void createRandomImage()
	{
		int border = 10;
		int size = (int) (50 + 50 * Math.random());

		int posX = (int) (wide / 2 + (0.5 - Math.random())
				* (wide / 2 - 2 * size - border));
		int posY = (int) (high / 2 + (0.5 - Math.random())
				* (high / 2 - 2 * size - border));

		int offsetX = (int) ((0.5 - Math.random()) * size);
		int offsetY = (int) ((0.5 - Math.random()) * size);

		sourceProcessor.setColor(255);
		targetProcessor.setColor(255);

		sourceProcessor.fillOval(posX, posY, size, size);
		sourceProcessor.drawLine(posX, posY, posX + size, posY + size);
		targetProcessor.fillOval(posX + offsetX, posY + offsetY, size, size);
	}

	public void alignImage()
	{
		result = tool.process(source, target);
	}

	public void reportImages()
	{
		result.setDisplayRange(0, 255);
		sourcePanel.setImage(source.getBufferedImage());
		targetPanel.setImage(target.getBufferedImage());
		resultPanel.setImage(result.getBufferedImage());
	}

	@Override
	public void run()
	{
		while (true)
		{
			clearImage();
			createRandomImage();
			alignImage();
			reportImages();
		}
	}

}
