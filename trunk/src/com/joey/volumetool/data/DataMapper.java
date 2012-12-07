package com.joey.volumetool.data;

public class DataMapper {
	
	public static double b2d(byte b){
		return ((int)b & 0xFF);
	}
	
	public static float b2f(byte b){
		return ((int)b & 0xFF);
	}
	
	public static int b2i(byte b){
		return ((int)b & 0xFF);
	}
	
	public static short b2s(byte b){
		return (short)(b&0xFF);
	}
}
