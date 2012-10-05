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
package com.joey.software.imageProcessing;


import java.awt.image.BufferedImage;

import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.imageToolkit.colorMapping.ColorMapTools;

public class OCTImageProcessingTool
{
	ColorMap colorMap = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	boolean useColorMap = true;

	boolean useSmooth = true;

	public boolean isUseColorMap()
	{
		return useColorMap;
	}

	public void setUseColorMap(boolean useColorMap)
	{
		this.useColorMap = useColorMap;
	}

	public void processImage(BufferedImage img)
	{

		if (isUseColorMap())
		{
			ColorMapTools.setColorMap(img, img, getColorMap());
		}
	}

	public ColorMap getColorMap()
	{
		if (colorMap == null)
		{
			return ColorMap.getColorMap(ColorMap.TYPE_GRAY);
		}
		return colorMap;
	}

	public void setColorMap(ColorMap colorMap)
	{
		this.colorMap = colorMap;
	}
}
