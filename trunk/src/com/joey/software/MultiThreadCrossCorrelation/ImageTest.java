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
