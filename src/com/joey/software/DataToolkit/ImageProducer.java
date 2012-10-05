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
package com.joey.software.DataToolkit;


import java.awt.image.BufferedImage;
import java.io.IOException;

import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;

public abstract class ImageProducer
{
	
	public void getImage(int pos, byte[][] data) throws IOException
	{
		BufferedImage img = getImage(pos);
		
		ImageOperations.grabPxlData(img, data, ImageOperations.PLANE_GRAY);
	}
	
	public abstract BufferedImage getImage(int pos) throws IOException;

	public BufferedImage getImageProject(int pos, int num, int projectType, int plane, StatusBarPanel status)
			throws IOException
	{

		// This is for the case when num is -ve,
		if (num < 0)
		{
			pos = pos + num;
			num *= -1;
		}

		BufferedImage data[] = new BufferedImage[num];
		if (status != null)
		{
			status.setStatusMessage("Loading Image Data");
			status.setMaximum(num - 1);
		}

		int j;
		for (int i = 0; i < num; i++)
		{
			if (status != null)
			{
				status.setValue(i);
			}
			data[i] = getImage(i + pos);
		}
		return ImageOperations
				.getImageProjection(projectType, plane, status, data);
	}

	public abstract int getImageCount();

}
