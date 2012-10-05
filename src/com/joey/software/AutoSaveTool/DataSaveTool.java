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