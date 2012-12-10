package com.joey.programs;

public class TestDataGenerator {
	public static void main(String input[]){
		/*
		 * This will create a test dataset consisting of a 3d sphere with varying color towards
		 * the center and simulate a set of vessels in the middle of the sphere
		 */
		
		int sizeX=256;
		int sizeY=256;
		int sizeZ=256;
		
		byte[][][] data = new byte[sizeX][sizeY][sizeZ];
		
		
		for(int x = 0; x < sizeX; x++){
			for(int y= 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					data[x][y][z] = 0;
					
					//determine distance
					float dist = 
				}
			}
		}
		
	}
}
