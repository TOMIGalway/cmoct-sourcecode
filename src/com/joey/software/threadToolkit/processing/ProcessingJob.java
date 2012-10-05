package com.joey.software.threadToolkit.processing;

public interface ProcessingJob
{
	public void process(int[] pos);
	
	public int getNDim();
	
	public int getSizeDim(int dim);
}
