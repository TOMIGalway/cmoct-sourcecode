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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayDeque;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;


public class FrameRateCounterPanel extends JPanel
{

	long last = -1, diff = 0;

	byte numAvg = 3;

	ArrayDeque<Integer> rates = new ArrayDeque<Integer>();

	public FrameRateCounterPanel()
	{
		for (int i = 0; i < numAvg; i++)
		{
			rates.push(new Integer(0));
		}
	}

	@Override
	protected void paintChildren(Graphics g)
	{
		// TODO Auto-generated method stub
		super.paintChildren(g);
		if (last != -1)
		{
			diff = System.currentTimeMillis() - last;
		} else
		{
			diff = 1000;
		}
		last = System.currentTimeMillis();
		if (diff == 0)
		{
			diff = 1;
		}
		setRate((int) (1000 / diff));
		g.setColor(Color.BLACK);
		g.drawString("FrameRate : " + getRate(), 10, 10);

	}

	public void setRate(int rate)
	{
		rates.push(rate);
		rates.removeLast();

	}

	public int getRate()
	{
		Integer average = 0;
		for (int num : rates)
		{
			average += num;
		}
		return average / rates.size();
	}
}

class testingRate
{
	public static void main(String input[])
	{
		FrameRateCounterPanel counter = new FrameRateCounterPanel();
		AnimatedPanel animPanel = new AnimatedPanel(60);

		animPanel.setLayout(new BorderLayout());
		animPanel.add(counter);

		JFrame f = FrameFactroy.getFrame(false, animPanel);
		f.setSize(800, 600);
		f.setVisible(true);
		animPanel.Start();

	}
}
