package com.joey.volumetool.processing.blur;

import com.joey.volumetool.data.ByteDataset;
import com.joey.volumetool.data.FloatDataset;
import com.joey.volumetool.data.IntegerDataset;
import com.joey.volumetool.data.ShortDataset;
import com.joey.volumetool.processing.DataProcessor;

public class KernelBlurProcessor extends DataProcessor {

	int kerX = 1;
	int kerY = 1;
	int kerZ = 1;
	int kerT = 1;
	
	public int getKerX() {
		return kerX;
	}

	public void setKerX(int kerX) {
		this.kerX = kerX;
	}

	public int getKerY() {
		return kerY;
	}

	public void setKerY(int kerY) {
		this.kerY = kerY;
	}

	public int getKerZ() {
		return kerZ;
	}

	public void setKerZ(int kerZ) {
		this.kerZ = kerZ;
	}

	public int getKerT() {
		return kerT;
	}

	public void setKerT(int kerT) {
		this.kerT = kerT;
	}

	public void setKer(int kerX, int kerY, int kerZ){
		setKerX(kerX);
		setKerY(kerY);
		setKerZ(kerZ);
	}
	@Override
	public void processData() {
		if(!getInput().isSameSize(getOutput())){
			setStatusMessage("Output is not same size as input");
			notifyError();
		}
		
		if(getInput().getClass() == getOutput().getClass()){
			if(getInput() instanceof ByteDataset ){
				KernelBlurToolkit.blurData((ByteDataset)getInput(), (ByteDataset)getOutput(), kerX, kerY, kerZ, kerT);
			}else if(getInput() instanceof ShortDataset ){
				KernelBlurToolkit.blurData((ShortDataset)getInput(), (ShortDataset)getOutput(), kerX, kerY, kerZ, kerT);
			}else if(getInput() instanceof IntegerDataset ){
				KernelBlurToolkit.blurData((IntegerDataset)getInput(), (IntegerDataset)getOutput(), kerX, kerY, kerZ, kerT);
			}else if(getInput() instanceof FloatDataset ){
				KernelBlurToolkit.blurData((FloatDataset)getInput(), (FloatDataset)getOutput(), kerX, kerY, kerZ, kerT);
			}else{
				KernelBlurToolkit.blurData(getInput(), getOutput(), kerX, kerY, kerZ, kerT);	
			}
		}else{
			KernelBlurToolkit.blurData(getInput(), getOutput(), kerX, kerY, kerZ, kerT);
		}
	}

}
