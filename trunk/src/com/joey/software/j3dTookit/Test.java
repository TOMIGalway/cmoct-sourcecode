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
package com.joey.software.j3dTookit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.Canvas3D;


import com.joey.software.VolumeToolkit.VolumeViewerPanel;
import com.joey.software.framesToolkit.FrameFactroy;
import com.sun.j3d.utils.universe.SimpleUniverse;


public class Test
{
	public static void main(String input[])
	{
		VolumeViewerPanel vol = new VolumeViewerPanel();
		FrameFactroy.getFrame(vol);
		// just after canvas and universe creation
		generateVideo(vol.getRender().getCanvas(), vol.getRender()
				.getUniverse(), 10000, new Alpha(), new File(
				"c:\\test\\movie.avi"), 30, 600, 480);

	}

	public static void generateVideo(Canvas3D canvas3D, SimpleUniverse universe, long animationTime, Alpha alpha, File outputFile, int fps, int width, int height)
	{

		/* Initializing the animation */
		long startTime = alpha.getStartTime();
		alpha.pause(startTime);

		/* Milliseconds it takes to change the frame. */
		long msFrame = (long) (((float) 1 / fps) * 1000);

		/* Frames you need to create the movie */
		long framesNeeded = animationTime / msFrame;

		/* Create an offscreen canvas from where you'll get the image */
		ScreenShot screenShot = new ScreenShot(canvas3D, universe, 1f);
		// canvas3D.getView().addCanvas3D(screenShot);

		/*
		 * CREATE FRAMES
		 */
		String tempDir = "c:\\test\\";
		String name = "img_";
		String fileType = "jpg";

		File fTempDir = new File(tempDir);

		Vector<File> vFiles = new Vector<File>();

		System.gc();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (long frameCount = 0; frameCount <= framesNeeded; frameCount++)
		{
			/* Stop the animation in the correct position */
			long time = startTime + frameCount * msFrame;
			alpha.pause(time);

			/* Get the renderized image at that moment */
			BufferedImage bi = screenShot.doRender(image);

			/* Create a JPEG file from the renderized image */
			String tempName = name + frameCount;
			File file = new File(fTempDir, tempName + "." + fileType);
			try
			{
				ImageIO.write(bi, fileType, file);
			} catch (IOException ex)
			{
				ex.printStackTrace();
				return;
			}

			vFiles.add(file);
		}

		// /*
		// * GENERATE VIDEO
		// */
		//
		// int frameRate = (int) ((framesNeeded + 1) / (animationTime / 1000));
		//
		// File[] files = vFiles.toArray(new File[0]);
		//
		// try
		// {
		// MovieMaker movieMaker = new MovieMaker(width, height, frameRate,
		// outputFile, files);
		//
		// movieMaker.makeMovie();
		// } catch (Exception ex)
		// {
		// ex.printStackTrace();
		// }
	}
}
