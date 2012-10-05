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
package com.joey.software.MultiThreadCrossCorrelation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTest
{
	public static void main(String input[]) throws InterruptedException,
			IOException
	{
		BufferedImage imageA = ImageIO.read(new File("c:\\test\\FrameA.png"));
		BufferedImage imageB = ImageIO.read(new File("c:\\test\\FrameB.png"));
		
		

	}
}
