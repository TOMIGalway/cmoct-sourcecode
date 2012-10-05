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
package com.joey.software.Tools;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.DataToolkit.ThorLabs2DFRGImageProducer;
import com.joey.software.DataToolkit.ThorLabs2DImageProducer;
import com.joey.software.VideoToolkit.BufferedImageStreamToAvi;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;


public class IMGViewer
{
	public static void main(String input[]) throws IOException
	{
		ViewIMGdata();
	}

	public static void ViewFRGdata() throws IOException
	{
		File file = FileSelectionField.getUserFile();

		ThorLabs2DFRGImageProducer img = new ThorLabs2DFRGImageProducer(file);
		img.getUserInputs();
		ImagePanel panel = new ImagePanel();

		JPanel panelHold = new JPanel();
		panel.putIntoPanel(panelHold);
		JFrame f = FrameFactroy.getFrame(panelHold);

		BufferedImageStreamToAvi output = new BufferedImageStreamToAvi(
				256,256, 5, "c:\\test\\", "output.avi",
				true, true);
		for (int i = 0; i < img.getImageCount(); i++)
		{
			BufferedImage srcImage =(img.getImage(i));

			BufferedImage imgs = ImageOperations.cropImage(srcImage, new Rectangle(512,512));
			imgs = ImageOperations.getScaledImage(imgs, 0.5, true, false);
			
			panel.setImage(imgs);
			output.pushImage(imgs);
		}
		output.finaliseVideo();
	}

	public static void ViewIMGdata() throws IOException
	{
		File file = FileSelectionField.getUserFile();

		String[] parts = FileOperations.splitFile(file);

		ThorLabs2DImageProducer img = new ThorLabs2DImageProducer(file);

		BufferedImage imageA = new BufferedImage(img.getSizeY(),
				img.getSizeX(), BufferedImage.TYPE_BYTE_GRAY);
		BufferedImage imageB = new BufferedImage(img.getSizeY(),
				img.getSizeX(), BufferedImage.TYPE_BYTE_GRAY);

		BufferedImage imageViewA = new BufferedImage(img.getSizeX(), img
				.getSizeY(), BufferedImage.TYPE_BYTE_GRAY);
		BufferedImage imageViewB = new BufferedImage(img.getSizeX(), img
				.getSizeY(), BufferedImage.TYPE_BYTE_GRAY);

		byte dataA[] = ImageOperations.image_byte_data(imageA);
		byte dataB[] = ImageOperations.image_byte_data(imageB);

		int count = 0;
		boolean useA = true;

		BufferedImage image;
		BufferedImage imageView;
		byte data[];

		ImagePanel panel = new ImagePanel();

		JPanel panelHold = new JPanel();

		Rectangle rec = new Rectangle(256, 0, 512, 512);
		panel.putIntoPanel(panelHold);
		JFrame f = FrameFactroy.getFrame(panelHold);
		//BufferedImageStreamToAvi output = new BufferedImageStreamToAvi(
		//		rec.width / 2, rec.height / 2, 10, "c:\\test\\", "output.avi",
		//		true, true);
		while (count < img.getImageCount())
		{
			if(count > img.getImageCount()-2)
			{
				count= 0;
			}
			if (useA)
			{
				imageView = imageViewA;
				image = imageA;
				data = dataA;
			} else
			{
				imageView = imageViewB;
				image = imageB;
				data = dataB;
			}

			img.getImageData(count, data, 0);
			ImageOperations.getRotateRightByte(image, imageView);
			// panel.setImage(imageView);
			useA = !useA;
			count++;
			f.setTitle(file.toString() + " :: " + count);
			System.out.println(image.getWidth() + " : " + image.getHeight()
					+ " || " + rec);
			BufferedImage imgs = ImageOperations.cropImage(imageView, rec);
			imgs = ImageOperations.getScaledImage(imgs, 0.5, true, false);
			panel.setImage(imgs);
		//	output.pushImage(imgs);
		}
		//output.finaliseVideo();
	}
}
