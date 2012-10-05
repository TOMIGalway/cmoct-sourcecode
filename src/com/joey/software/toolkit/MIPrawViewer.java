/*******************************************************************************
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
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
