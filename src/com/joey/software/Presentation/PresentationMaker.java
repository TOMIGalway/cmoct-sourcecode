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
package com.joey.software.Presentation;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;


public class PresentationMaker
{
	public static void main(String input[])
	{
		
		
		FrameFactroy.getFrame(new DynamicRangeImage(ImageOperations.getGrayTestImage(100, 512, 1,ImageOperations.Y_AXIS)));
	}
	
	
	public static void maind(String input[]) throws IOException
	{
		File f = FileSelectionField.getUserFile();
		BufferedImage img = ImageIO.read(f);
		BufferedImage rst = ImageOperations.cloneImage(img);
		
		int threshold = 20;
		int k = 5;
		for (int y = 0; y < img.getHeight(); y++)
		{
			for (int x = 0; x < img.getWidth(); x++)
			{
				float val = 0;
				int count = 0;
				for(int xP = -k; xP < k; xP++)
				{
					for(int yP = -k; yP < k; yP++)
					{
						int xL = x+xP;
						int yL = y+yP;
						if(xL >= 0 && yL >= 0 && xL < img.getWidth() && yL < img.getHeight())
						{
							count++;
							val += ImageOperations.getGrayScale(img.getRGB(xL, yL));
						}
					}
				}
				
				val /= count;
				
				
				if(val < threshold)
				{
					rst.setRGB(x, y, Color.BLACK.getRGB());
				}
				else
				{
					rst.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
			
			
		}
		
		FrameFactroy.getFrame(rst);

	}
}
