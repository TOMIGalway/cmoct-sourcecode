package com.joey.software.toolkit;


import java.io.IOException;

import com.joey.software.MultiThreadCrossCorrelation.CrossCorrelationDataset;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;


public class MIPrawViewer
{
	public static void main(String input[]) throws IOException
	{
		
		FrameFactroy.getFrame(new DynamicRangeImage(CrossCorrelationDataset.loadMIPRawData(FileSelectionField.getUserFile())));
	}
}
