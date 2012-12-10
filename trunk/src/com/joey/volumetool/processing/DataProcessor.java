package com.joey.volumetool.processing;

import com.joey.volumetool.data.Dataset;
import com.joey.volumetool.data.Point4D;

public abstract class DataProcessor implements Runnable {
	public static final int PROCESSOR_WAITING = 0;
	public static final int PROCESSOR_RUNNING = 1;
	public static final int PROCESSOR_COMPLETE = 2;
	public static final int PROCESSOR_FAILED = 3;
	
	protected Dataset input;
	protected Dataset output;
	
	float progress = 0;
	long start = 0;
	long processingTime = 0;
	long estimatedRemainingTime = 0;
	int processStatus = PROCESSOR_WAITING;
	String statusMessage = "Waiting";
	
	public Dataset getInput() {
		return input;
	}

	public void setInput(Dataset input) {
		this.input = input;
	}

	public Dataset getOutput() {
		return output;
	}

	public void setOutput(Dataset output) {
		this.output = output;
	}

	public abstract void processDataValue(Point4D p);
	
	public void processData(){
		Point4D p = new Point4D();
		for(p.t = 0; p.t < input.getSizeT();p.t++){
			for(p.x = 0; p.x < input.getSizeX();p.x++){
				for(p.y = 0; p.y < input.getSizeY();p.y++){
					for(p.z = 0; p.z < input.getSizeZ();p.z++){
						processDataValue(p);
					}	
				}	
			}	
		}
	}
	
	public void updateProgress(float progress){
		this.progress = progress;
		this.estimatedRemainingTime = (long) ((System.currentTimeMillis()-start)/progress);
	}
	
	public void notifyStart(){
		start = System.currentTimeMillis();
		updateProgress(0);
		processStatus = PROCESSOR_RUNNING;
	}
	
	public void notifyFinish(){
		processingTime = System.currentTimeMillis()-start;
		updateProgress(1);
		processStatus = PROCESSOR_COMPLETE;
	}
	
	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public void notifyError(){
		processStatus = PROCESSOR_FAILED;
	}
	
	@Override
	public void run() {
		notifyStart();
		processData();
		notifyFinish();
	}
}
