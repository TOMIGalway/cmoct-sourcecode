package com.joey.software.geomertyToolkit;

import java.awt.Rectangle;

public class ScaleToolkit
{
	/**
	 * This is a simple method to create a scaled version of a rectange. it will
	 * return a rectangle with values multplied by scaleX and scaleY
	 * 
	 * @param orignal
	 * @param scaleX
	 * @param scaleY
	 * @return
	 */
	public static Rectangle getScaled(Rectangle orignal, double scaleX, double scaleY)
	{
		Rectangle result = new Rectangle();
		result.x = (int) (orignal.x * scaleX);
		result.y = (int) (orignal.y * scaleY);
		result.width = (int) (orignal.width * scaleX);
		result.height = (int) (orignal.height * scaleY);
		return result;
	}
}
