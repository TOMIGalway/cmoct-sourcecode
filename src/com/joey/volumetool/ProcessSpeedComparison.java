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
		
		int size = 300;  
		int sizeX = 512;
		int sizeY = 512;
		int sizeZ = 1024;
		int sizeT = 1;
		
		int ker = 5;
		
		final Dataset inputFloat = new FloatDataset();
		final Dataset inputInteger = new IntegerDataset();
		final Dataset inputShort = new ShortDataset();
		final Dataset inputByte= new ByteDataset();




		inputFloat.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		inputInteger.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		inputShort.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		inputByte.allocateMemory(sizeX, sizeY, sizeZ, sizeT);
		
		
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
				blur.setOutput(inputFloat);
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
				blur.setOutput(inputInteger);
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
				blur.setOutput(inputShort);
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
				blur.setOutput(inputByte);
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
		
		SpeedJob floatJobOld = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processDataOld();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputFloat);
				blur.setOutput(inputFloat);
			}
		};
		
		SpeedJob integerJobOld = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processDataOld();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputInteger);
				blur.setOutput(inputInteger);
			}
		};
		SpeedJob shortJobOld = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processDataOld();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputShort);
				blur.setOutput(inputShort);
			}
		};
		SpeedJob byteJobOld = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processDataOld();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputByte);
				blur.setOutput(inputByte);
			}
		};
		SpeedJob genericJobOld = new SpeedJob(){
			@Override
			public void processJob() {
				blur.processDataOld();	
			}
			@Override
			public void prepairJob() {
				blur.setInput(inputByte);
				blur.setInput(inputFloat);
			}
		};
		SpeedComparisonTool speed = new SpeedComparisonTool();
		speed.addJob(floatJob,      "Blur Float      ");
		speed.addJob(integerJob,    "Blur Integer    ");
		speed.addJob(shortJob,      "Blur Short      ");
		speed.addJob(byteJob,       "Blur Byte       ");
		speed.addJob(genericJob,    "Blur Generic    ");
		speed.addJob(floatJobOld,   "Blur Float   Old");
		speed.addJob(integerJobOld, "Blur Integer Old");
		speed.addJob(shortJobOld,   "Blur Short   Old");
		speed.addJob(byteJobOld,    "Blur Byte    Old");
		speed.addJob(genericJobOld, "Blur Generic Old");		
		
		speed.compareSpeed(50);
		
		
	}
}
