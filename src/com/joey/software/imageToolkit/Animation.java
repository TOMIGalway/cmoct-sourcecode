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
package com.joey.software.imageToolkit;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * This is a basic class to hold an animation.
 * 
 * @author user
 */
public class Animation extends BufferedImage
{
	String name = "";

	BufferedImage[] images; // The images involved in the animation

	int time; // The time each image is to be desplayed in ms

	int currentImageIndex = 0; // The index of the current image

	boolean//
			animationRunning = false,
			animationLooping = false;

	long lastTime; // System time of last update

	/**
	 * This will create a new AnimatedImage with a blank image
	 */
	public Animation(Dimension size)
	{
		super(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		images = new BufferedImage[1];
		time = 30;
		images[0] = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_4BYTE_ABGR);
	}

	/**
	 * This will create a new animation using the
	 * 
	 * @param images
	 * @param frameTime
	 *            the lenght of time in milisecond for each frame
	 */
	public Animation(BufferedImage images[], int frameTime)
	{
		this(new Dimension(images[0].getWidth(), images[0].getHeight()));
		setImages(images);
		setTime(frameTime);
		setCurrentImageIndex(0);
		updateImage();

	}

	public Animation(BufferedImage image[], int frameTime, String name)
	{
		this(image, frameTime);
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "Animation:" + name;
	}

	/**
	 * This will return the current image of the animation
	 * 
	 * @return current image of panel
	 */
	public BufferedImage getCurrentImage()
	{
		return images[currentImageIndex];
	}

	/**
	 * This will start the animation over again
	 */
	public void resetAnimation()
	{
		currentImageIndex = 0;
	}

	/**
	 * This will pause the animation
	 */
	public void pauseAnimation()
	{
		animationRunning = false;
	}

	/**
	 * This will restart the animation
	 */
	public void unPauseAnimation()
	{
		animationRunning = true;
		lastTime = System.currentTimeMillis();
	}

	/**
	 * This will return the index of the current image of the animation
	 * 
	 * @return Returns the index of current image.
	 */
	public int getCurrentImageIndex()
	{
		return currentImageIndex;
	}

	/**
	 * This will set the index of the current image.
	 * 
	 * @param currentImageIndex
	 *            The currentImageIndex to set.
	 */
	public void setCurrentImageIndex(int currentImageIndex)
	{
		this.currentImageIndex = currentImageIndex;
	}

	/**
	 * This will return all the frames in the current animation
	 * 
	 * @return Returns the image.
	 */
	public BufferedImage[] getImages()
	{
		return images;
	}

	/**
	 * This allows you to set all the frames in the current animation
	 * 
	 * @param image
	 *            The image to set.
	 */
	public void setImages(BufferedImage[] image)
	{
		this.images = image;
	}

	/**
	 * This returns the lenght of time that each frame should be displayed for
	 * 
	 * @return Returns the times that each image is displayed.
	 */
	public int getTime()
	{
		return time;
	}

	/**
	 * set the lenght of time that the images are to be displayed for
	 * 
	 * @param time
	 *            The time to set.
	 */
	public void setTime(int time)
	{
		this.time = time;
	}

	/**
	 * @return Returns the animationLooping.
	 */
	public boolean isAnimationLooping()
	{
		return animationLooping;
	}

	/**
	 * @param animationLooping
	 *            The animationLooping to set.
	 */
	public void setAnimationLooping(boolean animationLooping)
	{
		this.animationLooping = animationLooping;
	}

	public void updateImage()
	{
		try
		{
			setData(images[currentImageIndex].getData());
		} catch (OutOfMemoryError e)
		{
			System.gc();
			setData(images[currentImageIndex].getData());
		}
	}

	/**
	 * This method will update the entities image. It will check to see how much
	 * time has passed sence the last image was changed. If this is longer than
	 * the time the current frame is supposed to be displayed for. If enough
	 * time has not passed the current image will not be updated.
	 */
	public void update()
	{
		if (animationRunning)
		{
			float delta = System.currentTimeMillis() - lastTime;
			if (time < delta)
			{
				currentImageIndex += (int) (delta / time);
				if (currentImageIndex >= images.length)
				{
					currentImageIndex = 0;
				}

				updateImage();
				lastTime = System.currentTimeMillis();
			}
		}
	}
}
