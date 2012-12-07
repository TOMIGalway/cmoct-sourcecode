package com.joey.volumetool;

import com.joey.volumetool.data.ByteDataset;
import com.joey.volumetool.data.Dataset;
import com.joey.volumetool.data.FloatDataset;
import com.joey.volumetool.data.IntegerDataset;
import com.joey.volumetool.data.ShortDataset;
import com.joey.volumetool.performance.SpeedComparisonTool;
import com.joey.volumetool.performance.SpeedJob;
import com.joey.volumetool.processing.blur.KernelBlurProcessor;

public class ProcessSpeedComparison {
	public static void main(String args[]){
		
		int size = 256;  
		int sizeX = size;
		int sizeY = size;
		int sizeZ = size;
		int sizeT = 1;
		
		int ker = 2;
		
		final Dataset inputFloat = new FloatDataset();
		final Dataset inputInteger = new IntegerDataset();
		final Dataset inputShort = new ShortDataset();
		final Dataset inputByte= new ByteDataset();
		final Dataset outputFloat = new FloatDataset();
		final Dataset outputInteger = new IntegerDataset();
		final Dataset outputShort = new ShortDataset();
		final Dataset outputByte= new ByteDataset();
		
		inputFloat.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		inputInteger.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		inputShort.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		inputByte.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		
		outputFloat.ensureSize(inputFloat);
		outputInteger.ensureSize(inputFloat);
		outputShort.ensureSize(inputFloat);
		outputByte.ensureSize(inputFloat);
		
		inputFloat.fillWithRandomData(0, 256);
		inputInteger.fillWithRandomData(0, 256);
		inputShort.fillWithRandomData(0, 256);
		inputByte.fillWithRandomData(0, 256);
		
		final KernelBlurProcessor blur = new KernelBlurProcessor();
		blur.setKer(ker, ker, ker);
		
		SpeedJob floatJob = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processData();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputFloat);
				blur.setOutput(outputFloat);
			}
		};
		
		SpeedJob integerJob = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processData();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputInteger);
				blur.setOutput(outputInteger);
			}
		};
		SpeedJob shortJob = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processData();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputShort);
				blur.setOutput(outputShort);
			}
		};
		SpeedJob byteJob = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processData();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputByte);
				blur.setOutput(outputByte);
			}
		};
		SpeedJob genericJob = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processData();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputByte);
				blur.setInput(inputFloat);
			}
		};
		SpeedComparisonTool speed = new SpeedComparisonTool();
		speed.addJob(floatJob,   "Blur Float  ");
		speed.addJob(integerJob, "Blur Integer");
		speed.addJob(shortJob,   "Blur Short  ");
		speed.addJob(byteJob,    "Blur Byte   ");
		speed.addJob(genericJob, "Blur Generic");
		
		
		speed.compareSpeed(4);
		
		
	}
}
