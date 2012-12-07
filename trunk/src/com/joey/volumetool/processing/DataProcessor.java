package com.joey.volumetool.processing;

import com.joey.volumetool.data.Dataset;

public abstract class DataProcessor implements Runnable {
	public static final int PROCESSOR_WAITING = 0;
	public static final int PROCESSOR_RUNNING = 1;
	public static final int PROCESSOR_COMPLETE = 2;
	public static final int PROCESSOR_FAILED = 3;
	
	Dataset input;
	Dataset output;
	
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

	public abstract void processData();
	
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
