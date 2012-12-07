package com.joey.volumetool.data;

public class ByteDataset extends Dataset {

	public byte[][][][] data;
	
	@Override
	public void updateSize() {
		if(data != null){
			sizeT = data.length;
			sizeZ = data[0].length;
			sizeX = data[0][0].length;
			sizeY = data[0][0][0].length;
		}else{
			sizeT = -1;
			sizeZ = -1;
			sizeX = -1;
			sizeY = -1;
		}
	}

	@Override
	public void setValue(int x, int y, int z, int t, float val) {
		data[t][z][x][y] = (byte) val;
	}

	@Override
	public void setValue(int x, int y, int z, int t, int val) {
		data[t][z][x][y] = (byte) val;
	}

	@Override
	public void setValue(int x, int y, int z, int t, short val) {
		data[t][z][x][y] = (byte) val;
	}

	@Override
	public void setValue(int x, int y, int z, int t, byte val) {
		data[t][z][x][y] = val;
	}

	@Override
	public void allocateMemory(int x, int y, int z, int t) {
		data = new byte[t][z][x][y];
		updateSize();
	}

	@Override
	public float getValuef(int x, int y, int z, int t) {
		return data[t][z][x][y];
	}

	@Override
	public int getValuei(int x, int y, int z, int t) {
		return data[t][z][x][y];	
	}

	@Override
	public short getValues(int x, int y, int z, int t) {
		return data[t][z][x][y];
	}

	@Override
	public byte getValueb(int x, int y, int z, int t) {
		return data[t][z][x][y];
	}
}
