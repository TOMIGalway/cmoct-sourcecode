package com.joey.volumetool.data;


public abstract class Dataset {
	
	int sizeX = 0;
	int sizeY = 0;
	int sizeZ = 0;
	int sizeT = 0;
	
	public abstract void allocateMemory(int x, int y, int z, int t);
	public abstract void updateSize();
	
	public void fillWithRandomData(float min, float max){
		for(int x = 0; x < getSizeX(); x++){
			for(int y = 0; y  <getSizeY(); y++){
				for(int z = 0; z < getSizeZ(); z++){
					for(int t = 0; t < getSizeT(); t++){
						setValue(x, y, z, t, (float)(min+Math.random()*(max-min)));
					}
				}
			}
		}
	}
	public float getValue(int x, int y, int z, int t){
		return getValuef(x, y, z, t);
	}
	
	public boolean isSameSize(Dataset set){
		return  set != null &&
				set.getSizeX()==getSizeX() &&
				set.getSizeY()==getSizeY() &&
				set.getSizeZ()==getSizeZ() &&
				set.getSizeT()==getSizeT() ;
	}
	
	public void ensureSize(Dataset data){
		allocateMemory(data.getSizeX(), data.getSizeY(), data.getSizeZ(), data.getSizeT());
	}
	public abstract float getValuef(int x, int y, int z, int t);
	public abstract int getValuei(int x, int y, int z, int t);
	public abstract short getValues(int x, int y, int z, int t);
	public abstract byte getValueb(int x, int y, int z, int t);
	
	public abstract void setValue(int x, int y, int z, int t, float val);
	public abstract void setValue(int x, int y, int z, int t, int val);
	public abstract void setValue(int x, int y, int z, int t, short val);
	public abstract void setValue(int x, int y, int z, int t, byte val);
		
	public int getSizeX(){return sizeX;};
	public int getSizeY(){return sizeY;};
	public int getSizeZ(){return sizeX;};
	public int getSizeT(){return sizeT;};
}
