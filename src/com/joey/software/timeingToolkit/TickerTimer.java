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
package com.joey.software.timeingToolkit;

public class TickerTimer
{
	/**
	 * This was the system time of the last tick
	 */
	long lastTick = 0;

	/**
	 * Last different time
	 */
	long lastDiff = 0;

	/**
	 * Average Diff time
	 */
	double avgDiff = 0;

	/**
	 * Used to notify the first tick
	 */
	boolean firstTick = true;

	int avgCount = 10;

	public TickerTimer()
	{
		resetTicker();
	}

	public void setAvgCount(int val)
	{
		avgCount = val;
	}

	public void resetTicker()
	{
		firstTick = true;
	}

	public void tick()
	{
		if (firstTick)
		{
			lastTick = System.currentTimeMillis();
			lastDiff = 0;
			avgDiff = 0;
			firstTick = false;
		} else
		{
			lastDiff = System.currentTimeMillis() - lastTick;
			lastTick = System.currentTimeMillis();
			avgDiff = (avgDiff * avgCount + lastDiff) / (avgCount + 1);
		}
	}

	/**
	 * This will return the average tick rate per second
	 * 
	 * @return
	 */
	public double getTickRate()
	{
		return 1 / avgDiff * 1000;
	}

	public double getTickDiff()
	{
		return avgDiff;
	}

	public static void main(String input[]) throws InterruptedException
	{
		TickerTimer t = new TickerTimer();

		for (int i = 0; i < 100; i++)
		{
			t.tick();
			Thread.sleep((long) (10 + 20 * Math.random()));
			System.out.println(t.getTickDiff());
		}

		System.out.println(t.getTickDiff());
	}
}
