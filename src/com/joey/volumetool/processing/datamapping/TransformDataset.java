package com.joey.volumetool.processing.datamapping;

import com.joey.volumetool.data.Dataset;
import com.joey.volumetool.data.Point4D;
import com.joey.volumetool.processing.DataProcessor;

public class TransformDataset extends DataProcessor {

	@Override
	public void processDataValue(Point4D p) {
		output.setValue(p.x, p.y, p.z, p.t, input.getValue(p.x, p.y, p.z, p.t));
	}

}
