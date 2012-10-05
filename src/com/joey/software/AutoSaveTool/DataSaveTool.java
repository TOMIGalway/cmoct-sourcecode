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
package com.joey.software.AutoSaveTool;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.userinterface.VersionManager;


public class DataSaveTool extends TimerTask
{
	long delay;

	int fileCount;

	Timer time = new Timer();

	int currentCount = 0;

	OCTAnalysis program;

	boolean active = true;

	public DataSaveTool(OCTAnalysis program, int fileCount, long delay)
	{
		this.delay = delay;
		this.fileCount = fileCount;
		this.program = program;
		time.scheduleAtFixedRate(this, 0, delay);
	}

	public void start()
	{
		active = true;
	}

	public void pause()
	{
		active = false;
	}

	@Override
	public void run()
	{
		try
		{
			if (active)
			{
				currentCount++;
				if (currentCount > fileCount)
				{
					currentCount = 0;
				}

				VersionManager.saveData(program, new File("autobackup\\temp_"
						+ currentCount + ".tmp"), program.getStatusBar());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
