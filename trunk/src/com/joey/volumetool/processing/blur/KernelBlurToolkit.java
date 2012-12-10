package com.joey.volumetool.processing.blur;

import com.joey.volumetool.data.ByteDataset;
import com.joey.volumetool.data.Dataset;
import com.joey.volumetool.data.FloatDataset;
import com.joey.volumetool.data.IntegerDataset;
import com.joey.volumetool.data.Point4D;
import com.joey.volumetool.data.ShortDataset;

public class KernelBlurToolkit{

	public static float getBlurData(Dataset input, Point4D ker,Point4D p ){
		float val = 0;
		int count = 0;
		
		//Perform Calculation
		for(int tP = -ker.t; tP < ker.t; tP++){
			for (int xP = -ker.x; xP < ker.x; xP++) {
				for (int yP = -ker.y; yP < ker.y; yP++) {
					for (int zP = -ker.z; zP < ker.z; zP++) {
						if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
							val += input.getValue(xP, yP, zP, tP);
							count++;
						}
					}
				}
			}
		}
		
		if(count != 0){
			val/=count;
		}
		return val;
	}
	
	public static float getBlurData(FloatDataset input, Point4D ker,Point4D p ){
		float val = 0;
		int count = 0;
		
		//Perform Calculation
		for(int tP = -ker.t; tP < ker.t; tP++){
			for (int xP = -ker.x; xP < ker.x; xP++) {
				for (int yP = -ker.y; yP < ker.y; yP++) {
					for (int zP = -ker.z; zP < ker.z; zP++) {
						if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
							val += input.data[tP][zP][xP][zP];
							count++;
						}
					}
				}
			}
		}
		
		if(count != 0){
			val/=count;
		}
		return val;
	}
	
	public static int getBlurData(IntegerDataset input, Point4D ker,Point4D p){
		float val = 0;
		int count = 0;
		
		//Perform Calculation
		for(int tP = -ker.t; tP < ker.t; tP++){
			for (int xP = -ker.x; xP < ker.x; xP++) {
				for (int yP = -ker.y; yP < ker.y; yP++) {
					for (int zP = -ker.z; zP < ker.z; zP++) {
						if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
							val += input.data[tP][zP][xP][zP];
							count++;
						}
					}
				}
			}
		}
		
		if(count != 0){
			val/=count;
		}
		return (int)val;
	}
	
	public static short getBlurData(ShortDataset input, Point4D ker,Point4D p ){
		float val = 0;
		int count = 0;
		
		//Perform Calculation
		for(int tP = -ker.t; tP < ker.t; tP++){
			for (int xP = -ker.x; xP < ker.x; xP++) {
				for (int yP = -ker.y; yP < ker.y; yP++) {
					for (int zP = -ker.z; zP < ker.z; zP++) {
						if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
							val += input.data[tP][zP][xP][zP];
							count++;
						}
					}
				}
			}
		}
		
		if(count != 0){
			val/=count;
		}
		return (short)val;
	}
	
	public static byte getBlurData(ByteDataset input, Point4D ker,Point4D p ){
		float val = 0;
		int count = 0;
		
		//Perform Calculation
		for(int tP = -ker.t; tP < ker.t; tP++){
			for (int xP = -ker.x; xP < ker.x; xP++) {
				for (int yP = -ker.y; yP < ker.y; yP++) {
					for (int zP = -ker.z; zP < ker.z; zP++) {
						if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
							val += input.data[tP][zP][xP][zP];
							count++;
						}
					}
				}
			}
		}
		
		if(count != 0){
			val/=count;
		}
		return (byte)val;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void blurData(Dataset input, Dataset output, int kerX, int kerY, int kerZ, int kerT) { 	
		float val = 0;
		int xP,yP,zP,tP,count = 0;
		
		for(int t = 0; t < input.getSizeT(); t++){
			for(int x = 0; x < input.getSizeX(); x++){
				for(int y = 0; y < input.getSizeY(); y++){
					for(int z = 0; z < input.getSizeZ(); z++){
						
						//Perform Calculation
						for(tP = -kerT; tP < kerT; tP++){
							for (xP = -kerX; xP < kerX; xP++) {
								for (yP = -kerY; yP < kerY; yP++) {
									for (zP = -kerZ; zP < kerZ; zP++) {
										if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
											val += input.getValue(xP, yP, zP, tP);
											count++;
										}
									}
								}
							}
						}
						
						if(count != 0){
							val/=count;
						}
						output.setValue(x, y, z, t, val);
					}	
				}	
			}
		}
	}

	public static void blurData(ByteDataset input, ByteDataset output, int kerX, int kerY, int kerZ, int kerT) {
		float val = 0;
		int xP,yP,zP,tP,count = 0;
		
		for(int t = 0; t < input.getSizeT(); t++){
			for(int x = 0; x < input.getSizeX(); x++){
				for(int y = 0; y < input.getSizeY(); y++){
					for(int z = 0; z < input.getSizeZ(); z++){
						
						//Perform Calculation
						for(tP = -kerT; tP < kerT; tP++){
							for (xP = -kerX; xP < kerX; xP++) {
								for (yP = -kerY; yP < kerY; yP++) {
									for (zP = -kerZ; zP < kerZ; zP++) {
										if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
											val += (input.data[tP][zP][xP][yP] & 0xff);
											count++;
										}
									}
								}
							}
						}
						
						if(count != 0){
							val/=count;
						}
						output.data[t][z][x][y]= (byte)val;
					}	
				}	
			}
		}
	}

	public static void blurData(ShortDataset input, ShortDataset output, int kerX, int kerY, int kerZ, int kerT) {
		float val = 0;
		int xP,yP,zP,tP,count = 0;
		
		for(int t = 0; t < input.getSizeT(); t++){
			for(int x = 0; x < input.getSizeX(); x++){
				for(int y = 0; y < input.getSizeY(); y++){
					for(int z = 0; z < input.getSizeZ(); z++){
						
						//Perform Calculation
						for(tP = -kerT; tP < kerT; tP++){
							for (xP = -kerX; xP < kerX; xP++) {
								for (yP = -kerY; yP < kerY; yP++) {
									for (zP = -kerZ; zP < kerZ; zP++) {
										if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
											val += (input.data[tP][zP][xP][yP]);
											count++;
										}
									}
								}
							}
						}
						
						if(count != 0){
							val/=count;
						}
						output.data[t][z][x][y]= (short)val;
					}	
				}	
			}
		}
	}
	
	public static void blurData(IntegerDataset input, IntegerDataset output, int kerX, int kerY, int kerZ, int kerT) {	
		float val = 0;
		int xP,yP,zP,tP,count = 0;
		
		for(int t = 0; t < input.getSizeT(); t++){
			for(int x = 0; x < input.getSizeX(); x++){
				for(int y = 0; y < input.getSizeY(); y++){
					for(int z = 0; z < input.getSizeZ(); z++){
						
						//Perform Calculation
						for(tP = -kerT; tP < kerT; tP++){
							for (xP = -kerX; xP < kerX; xP++) {
								for (yP = -kerY; yP < kerY; yP++) {
									for (zP = -kerZ; zP < kerZ; zP++) {
										if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
											val += (input.data[tP][zP][xP][yP]);
											count++;
										}
									}
								}
							}
						}
						
						if(count != 0){
							val/=count;
						}
						output.data[t][z][x][y]= (int)val;
					}	
				}	
			}
		}
	}
	
	public static void blurData(FloatDataset input, FloatDataset output, int kerX, int kerY, int kerZ, int kerT) {	
		float val = 0;
		int xP,yP,zP,tP,count = 0;
		
		for(int t = 0; t < input.getSizeT(); t++){
			for(int x = 0; x < input.getSizeX(); x++){
				for(int y = 0; y < input.getSizeY(); y++){
					for(int z = 0; z < input.getSizeZ(); z++){
						
						//Perform Calculation
						for(tP = -kerT; tP < kerT; tP++){
							for (xP = -kerX; xP < kerX; xP++) {
								for (yP = -kerY; yP < kerY; yP++) {
									for (zP = -kerZ; zP < kerZ; zP++) {
										if (xP >= 0 && yP >= 0 && zP >= 0 && tP >= 0 && tP < input.getSizeT() && xP < input.getSizeX() && yP < input.getSizeY() && zP < input.getSizeZ()) {
											val += (input.data[tP][zP][xP][yP]);
											count++;
										}
									}
								}
							}
						}
						
						if(count != 0){
							val/=count;
						}
						output.data[t][z][x][y]= val;
					}	
				}	
			}
		}
	}
}
