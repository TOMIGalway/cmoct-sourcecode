package com.joey.volumetool.processing.blur;

import com.joey.volumetool.data.ByteDataset;
import com.joey.volumetool.data.FloatDataset;
import com.joey.volumetool.data.IntegerDataset;
import com.joey.volumetool.data.Point4D;
import com.joey.volumetool.data.ShortDataset;
import com.joey.volumetool.processing.DataProcessor;

public class KernelBlurProcessor extends DataProcessor {

	Point4D ker = new Point4D();
	
	public int getKerX() {
		return ker.x;
	}

	public void setKerX(int kerX) {
		this.ker.x = kerX;
	}

	public int getKerY() {
		return ker.y;
	}

	public void setKerY(int kerY) {
		this.ker.y = kerY;
	}

	public int getKerZ() {
		return ker.z;
	}

	public void setKerZ(int kerZ) {
		this.ker.z = kerZ;
	}

	public int getKerT() {
		return ker.t;
	}

	public void setKerT(int kerT) {
		this.ker.t = kerT;
	}

	public void setKer(int kerX, int kerY, int kerZ){
		setKerX(kerX);
		setKerY(kerY);
		setKerZ(kerZ);
	}
	
	public void processDataValue(Point4D p) {
		if(getInput().getClass() == getOutput().getClass()){
			if(getInput() instanceof ByteDataset ){
				((ByteDataset)getOutput()).data[p.t][p.z][p.x][p.y]=KernelBlurToolkit.getBlurData((ByteDataset)getInput(), ker,p);
			}else if(getInput() instanceof ShortDataset ){
				((ShortDataset)getOutput()).data[p.t][p.z][p.x][p.y]=KernelBlurToolkit.getBlurData((ShortDataset)getInput(), ker,p);
			}else if(getInput() instanceof IntegerDataset ){
				((IntegerDataset)getOutput()).data[p.t][p.z][p.x][p.y]=KernelBlurToolkit.getBlurData((IntegerDataset)getInput(), ker,p);
			}else if(getInput() instanceof FloatDataset ){
				((FloatDataset)getOutput()).data[p.t][p.z][p.x][p.y]=KernelBlurToolkit.getBlurData((FloatDataset)getInput(), ker,p);
			}else{
				getOutput().setValue(p.x, p.y, p.z, p.t, KernelBlurToolkit.getBlurData(getInput(), ker,p));	
			}
		}else{
			getOutput().setValue(p.x, p.y, p.z, p.t, KernelBlurToolkit.getBlurData(getInput(), ker,p));
		}
	}
	
	public void processDataOld() {
		if(!getInput().isSameSize(getOutput())){
			setStatusMessage("Output is not same size as input");
			notifyError();
		}
		
		if(getInput().getClass() == getOutput().getClass()){
			if(getInput() instanceof ByteDataset ){
				KernelBlurToolkit.blurData((ByteDataset)getInput(), (ByteDataset)getOutput(), ker.x, ker.y, ker.z, ker.t);
			}else if(getInput() instanceof ShortDataset ){
				KernelBlurToolkit.blurData((ShortDataset)getInput(), (ShortDataset)getOutput(),ker.x, ker.y, ker.z, ker.t);
			}else if(getInput() instanceof IntegerDataset ){
				KernelBlurToolkit.blurData((IntegerDataset)getInput(), (IntegerDataset)getOutput(), ker.x, ker.y, ker.z, ker.t);
			}else if(getInput() instanceof FloatDataset ){
				KernelBlurToolkit.blurData((FloatDataset)getInput(), (FloatDataset)getOutput(), ker.x, ker.y, ker.z, ker.t);
			}else{
				KernelBlurToolkit.blurData(getInput(), getOutput(), ker.x, ker.y, ker.z, ker.t);	
			}
		}else{
			KernelBlurToolkit.blurData(getInput(), getOutput(), ker.x, ker.y, ker.z, ker.t);
		}
	}

}
