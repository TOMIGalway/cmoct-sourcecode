package com.joey.software.threadToolkit;


public abstract interface Task
{
	/**
	 * This function should be called when the job is running
	 * 
	 * @return
	 */
	public abstract void doTask();
}
