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
package com.joey.software.Projections.surface;


import java.io.File;

import com.joey.software.MultiThreadCrossCorrelation.CrossCorrelationDataset;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;


public class SurfaceMapViewer
{
	public static void main(String input[])
	{
		final DynamicRangeImage image = new DynamicRangeImage();
		FrameFactroy.getFrame(image);
		
		FileDrop drop = new FileDrop(image, new FileDrop.Listener(){

			@Override
			public void filesDropped(File[] files)
			{
				try
				{
					image.setDataFloat(CrossCorrelationDataset.loadMIPRawData(files[0]));
				}
				catch(Exception e)
				{
					
				}
				
			}});
	}
}
