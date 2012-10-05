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


import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import com.joey.software.dsp.Complex;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class FrameFactroy
{

	static BufferedImage icon = null;

	public static JFrame getFrame()
	{
		return getFrame(true);
	}

	public static JFrame getFrame(boolean visible)
	{
		JFrame frame = new JFrame();
		if (icon != null)
		{
			frame.setIconImage(icon);
		}
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setVisible(visible);
		return frame;
	}

	public static JFrame getFrame(String name, boolean max, JComponent panel, boolean setVisible)
	{
		JFrame frame = getFrame();
		if (icon != null)
		{
			frame.setIconImage(icon);
		}
		if (max)
		{
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel);
		return frame;
	}

	public static JFrame getFrame(String name, JComponent panel)
	{
		return getFrame(name, false, panel);
	}

	public static JFrame getFrame(JComponent tool, JComponent main, int loc)
	{
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(tool);
		split.setRightComponent(main);
		split.setDividerLocation(loc);
		
		return getFrame(split);
	}
	public static JFrame getFrame(JComponent... panel)
	{
		if (panel.length == 1)
		{
			return getFrame("", panel[0]);
		}
		else
		{
			return getFrame(panel, panel.length,1);
		}
	}

	public static JFrame getFrameTabs(JComponent... panel)
	{
		if (panel.length == 1)
		{
			return getFrame("", panel[0]);
		}
		else
		{
			JTabbedPane tab = new JTabbedPane();
			for(JComponent c : panel)
			{
				tab.addTab("", c);
			}
			
			return getFrame(tab);
		}
	}
	public static JFrame getFrame(JComponent[] panel, int numX, int numY)
	{
		JPanel mainPanel = new JPanel(new GridLayout(numY, numX));
		for (int i = 0; i < panel.length; i++)
		{
			JPanel tmp = new JPanel(new BorderLayout());
			tmp.add(panel[i], BorderLayout.CENTER);

			mainPanel.add(tmp);
		}

		return getFrame(mainPanel);
	}

	public static JFrame getFrame(String name, boolean max, JComponent panel)
	{
		return getFrame(name, max, panel, true);
	}

	public static JFrame getFrame(boolean max, JComponent panel)
	{
		return getFrame("", max, panel);
	}

	public static JFrame getFrame(Image[] images, int numX, int numY)
	{
		return getFrame(null, images, numX, numY, ImagePanel.TYPE_NORMAL);
	}

	public static JFrame getFrame(Image[] images, int numX, int numY, int panelType)
	{
		return getFrame(null, images, numX, numY, panelType);
	}

	public static JFrame getFrame(HashMap<Image, String> labels, Image[] images, int numX, int numY)
	{
		return getFrame(labels, images, numX, numY, ImagePanel.TYPE_NORMAL);
	}

	public static JFrame getFrame(HashMap<Image, String> labels, Image[] images, int numX, int numY, int imagePanelType)
	{
		JScrollPane scroll = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel mainPanel = new JPanel(new GridLayout(numY, numX));
		for (int i = 0; i < images.length; i++)
		{

			ImagePanel panel = new ImagePanel();
			panel.setPanelType(imagePanelType);
			panel.setShowRGBValueOnMouseMove(true);
			panel.setImage(images[i]);

			JPanel tmp = new JPanel(new BorderLayout());
			String name = "Image  :" + i;
			if (labels != null)
			{
				if (labels.containsKey(images[i]))
				{
					name = labels.get(images[i]);
				}
			}
			panel.putIntoPanel(tmp);
			mainPanel.add(tmp);
		}

		scroll.setViewportView(mainPanel);
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(scroll);

		return getFrame(temp);
	}

	public static JFrame getFrame(HashMap<Image, String> labels, Image... images)
	{

		return getFrame(labels, images, images.length, 1);

	}

	public static JFrame getFrame(Image... images)
	{

		return getFrame(images, images.length, 1);

	}

	public static void setDefaultIcon(BufferedImage newIcon)
	{
		icon = newIcon;
	}

	public static JFrame getFrame(double[][]... data)
	{
		JPanel holder = new JPanel(new BorderLayout());
		JTabbedPane tab = new JTabbedPane();
		holder.add(tab, BorderLayout.CENTER);
		for (int i = 0; i < data.length; i++)
		{
			DynamicRangeImage view = new DynamicRangeImage(data[i]);
			tab.addTab("Tab :" + i, view);
		}
		return getFrame(holder);
	}

	public static JFrame getFrame(float[] hold)
	{
		return getFrame(PlotingToolkit.getChartPanel(hold, "", "X", "Y"));
	}

	public static JFrame getFrame(float[][]... data)
	{
		JPanel holder = new JPanel(new BorderLayout());
		JTabbedPane tab = new JTabbedPane();
		holder.add(tab, BorderLayout.CENTER);
		for (int i = 0; i < data.length; i++)
		{
			DynamicRangeImage view = new DynamicRangeImage(data[i]);
			tab.addTab("Tab :" + i, view);
		}
		return getFrame(holder);
	}

	public static JFrame getFrame(Complex[][] data)
	{
		double[][] real = new double[data.length][data[0].length];
		double[][] imag = new double[data.length][data[0].length];
		double[][] phase = new double[data.length][data[0].length];
		double[][] mag = new double[data.length][data[0].length];

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				real[x][y] = data[x][y].r;
				imag[x][y] = data[x][y].i;
				phase[x][y] = data[x][y].phs();
				mag[x][y] = data[x][y].mag();
			}
		}

		JPanel holder = new JPanel(new BorderLayout());
		JTabbedPane tab = new JTabbedPane();
		holder.add(tab, BorderLayout.CENTER);

		{
			DynamicRangeImage view = new DynamicRangeImage(real);
			tab.addTab("Real", view);
		}
		{
			DynamicRangeImage view = new DynamicRangeImage(imag);
			tab.addTab("Imaginary", view);
		}
		{
			DynamicRangeImage view = new DynamicRangeImage(phase);
			tab.addTab("Phase", view);
		}
		{
			DynamicRangeImage view = new DynamicRangeImage(mag);
			tab.addTab("Magnitude", view);
		}
		return getFrame(holder);

	}

	public static void getFrame(byte[][] frm)
	{
		getFrame(new DynamicRangeImage(frm));
		
	}

	public static void getFrame(short[][] map)
	{
		getFrame(new DynamicRangeImage(map));
	}
}
