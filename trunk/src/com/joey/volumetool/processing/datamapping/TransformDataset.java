package com.joey.volumetool.processing.datamapping;

import com.joey.volumetool.data.Dataset;
import com.joey.volumetool.processing.DataProcessor;

public class TransformDataset extends DataProcessor {

	@Override
	public void processData() {
		Dataset input = getInput();
		Dataset output = getOutput();
		for(int t = 0; t < input.getSizeT(); t++){
			for(int x = 0; x < input.getSizeX(); x++){
				for(int y = 0; y < input.getSizeY(); y++){
					for(int z = 0; z < input.getSizeZ(); z++){
						output.setValue(x, y, z, t, input.getValue(x, y, z, t));
					}
				}
			}
		}
	}

}
