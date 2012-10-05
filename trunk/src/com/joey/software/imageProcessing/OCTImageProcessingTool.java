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
