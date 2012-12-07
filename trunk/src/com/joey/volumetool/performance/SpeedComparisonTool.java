package com.joey.volumetool.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.joey.volumetool.data.ByteDataset;
import com.joey.volumetool.data.Dataset;
import com.joey.volumetool.processing.blur.KernelBlurToolkit;
import com.joey.volumetool.processing.blur.KernelBlurProcessor;

public class SpeedComparisonTool {
	public static final int MEASURE_MILLI = 0;
	public static final int MEASURE_NANO = 1;
	
	int measureType  = MEASURE_MILLI;
	
	ArrayList<SpeedJob> jobs = new ArrayList<SpeedJob>();
	HashMap<SpeedJob, ArrayList<Long>> timeData = new HashMap<SpeedJob, ArrayList<Long>>();
	HashMap<SpeedJob, String> nameData = new HashMap<SpeedJob, String>();
	
	public void addJob(SpeedJob job, String name){
		jobs.add(job);
		timeData.put(job, new ArrayList<Long>());
		nameData.put(job, name);
	}
	
	public void resetTimes(){
		for(SpeedJob s : timeData.keySet()){
			timeData.get(s).clear();
		}
	}
	
	public void itterate(){
		long start;
		long diff = 0;
		
		for(SpeedJob s : jobs){
			System.out.print(".");
			s.prepairJob();
			
			start = getTime();
			s.processJob();
			diff = getTime()-start;
			timeData.get(s).add(diff);
		}
	}
	
	public void compareSpeed(int runs){
		for(int i = 0; i < runs; i++){
			System.out.printf("Running [%2d/%2d]",i+1,runs);
			itterate();
			System.out.println();
		}
		processResults();
	}
	
	public String getTimes(ArrayList<Long> time){
		StringBuffer rst = new StringBuffer();
		
		for(Long l : time){
			rst.append(String.format("%6d", l));
			rst.append(",");
		}
		
		return rst.toString();
	}
	
	public void processResults(){
		float[] statsHold = new float[3];
		
		float fastestTime= 0;
		SpeedJob bestJob = null;
		
		for(SpeedJob job : jobs){
			getStatistics(job, statsHold);
			if(bestJob == null){
				bestJob = job;
				fastestTime = statsHold[1];
			}else{
				if(fastestTime > statsHold[1]){
					bestJob = job;
					fastestTime = statsHold[1];
				}
			}
			
			
			System.out.printf("Job [%s] - Min[%6d] \tMax[%6d]\tAvg[%6d]\tTime:%s\n",nameData.get(job), Math.round(statsHold[1]), Math.round(statsHold[2]), Math.round(statsHold[0]),getTimes(timeData.get(job)));
		}
		
		System.out.println("Fastest :"+nameData.get(bestJob));
		
	}
	
	public long getTime(){
		if(measureType == MEASURE_NANO){
			return System.nanoTime();
		}else{
			return System.currentTimeMillis();
		}
	}
	
	public void getStatistics(SpeedJob job, float[] stats){
		ArrayList<Long> time = timeData.get(job);
		boolean first = true;
		for(Long l : time){
			if(first){
				stats[0] = l; //Avg
				stats[1] = l; //Min
				stats[2] = l; //Max
				first = false;
			}
			else{
				if(stats[1] > l){
					stats[1] = l;
				}
				if(stats[2] < l){
					stats[2] = l;
				}
				stats[0] += l;
			}
		}
		
		//None got 
		if(time.size() == 0){
			stats[0] = 0;
			stats[1] = 0;
			stats[2] = 0;
		}else{
			stats[0]/=time.size();
		}
	}
	
	public static void main(String args[]) {		
		SpeedComparisonTool speed = new SpeedComparisonTool();
		
		speed.addJob(new SpeedJob() {
			
			@Override
			public void processJob() {
				try {
					Thread.sleep((long) (10+100*Math.random()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void prepairJob() {
			}
		}, "10 Delay");
		
		speed.addJob(new SpeedJob() {	
			@Override
			public void processJob() {
				try {
					Thread.sleep((long) (50+100*Math.random()));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void prepairJob() {}
		}, "50 Delay");
		
		
		speed.compareSpeed(4);
	}

	public int getMeasureType() {
		return measureType;
	}

	public void setMeasureType(int measureType) {
		this.measureType = measureType;
	}
	
}


