package com.joey.software.VideoToolkit;


import java.awt.AWTException;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextField;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.imageWarping.ImageRotatePanel;
import com.joey.software.timeingToolkit.TickerTimer;


public class CompoentRecorder
{
	boolean pause = false;
	
	Component compoent;

	BufferedImageStreamToAvi outputVideo;

	Rectangle bounds = new Rectangle();

	Robot robot;

	Timer timer = new Timer();

	boolean isRecording = false;

	CompoentRecorderControler controler = null;

	TickerTimer t = new TickerTimer();

	JTextField recordeRate = new JTextField();

	DecimalFormat formater = new DecimalFormat("#.##");

	public CompoentRecorder(Component compoent) throws AWTException
	{
		robot = new Robot();
		this.compoent = compoent;
	}

	public CompoentRecorderControler getControler()
	{
		if (controler == null)
		{
			controler = new CompoentRecorderControler(this);
		}
		return controler;
	}

	public void setControler(CompoentRecorderControler controler)
	{
		this.controler = controler;
	}

	public boolean isRecording()
	{
		return isRecording;
	}

	public void setRecording(boolean isRecording)
	{
		this.isRecording = isRecording;
		if (controler != null)
		{
			controler.statusChanged();
		}
	}

	/**
	 * This will start the video recording.
	 * 
	 * 
	 * 
	 * @param path
	 * @param fileName
	 * @param frameRate
	 * @throws IOException
	 */
	public void startRecording(final String path, final String fileName, int frameRate)
			throws IOException
	{

		bounds.width = compoent.getWidth();
		bounds.height = compoent.getHeight();

		outputVideo = new BufferedImageStreamToAvi(bounds.width, bounds.height,
				frameRate, path, fileName, true, true);

		bounds.width = outputVideo.getXDim();

		timer.scheduleAtFixedRate(new TimerTask()
		{

			@Override
			public void run()
			{

				try
				{
					if (!(recordFrame() && isRecording()))
					{
						cancel();
						int rate = (int) Math.round(t.getTickRate());
						if (rate <= 0)
						{
							rate = 1;
						}
						stopRecording();
						outputVideo.setFrameRate(rate);
						outputVideo.finaliseVideo();

					}

				} catch (Exception e)
				{
					cancel();

					/*
					 * This is for the case when an error is thrown in
					 * recordFrame. This will then ensure that the video is
					 * closed
					 */
					try
					{
						outputVideo.finaliseVideo();
					} catch (Exception e1)
					{

					}
					e.printStackTrace();
				}

			}
		}, 0, (long) (1.0 / frameRate * 1000));
		setRecording(true);

	}

	public static void main(String input[]) throws AWTException,
			InterruptedException, IOException
	{
		ImageRotatePanel rot = new ImageRotatePanel();
		rot.setImage(ImageOperations.getGrayTestImage(800, 600, 5));
		
	
		FrameFactroy.getFrame(rot);
		
		CompoentRecorder recorder = new CompoentRecorder(rot);
		CompoentRecorderControler cont = new CompoentRecorderControler(recorder);
		FrameFactroy.getFrame(cont);
	

		while (true)
		{
			System.out.println(recorder.t.getTickRate());
			Thread.sleep(500);
		}

	}

	/**
	 * This function will attempt to save a copy of the compoent to the avi
	 * stream.
	 * 
	 * It will also ensure that there has been no change to the frame size. If
	 * this occours the video will be resized to a suitable size by scaleing the
	 * new image into the old
	 * 
	 * If this fails this function will return false; This should induce the
	 * frame to stop recording.
	 * 
	 * @return
	 * @throws IOException
	 */
	private synchronized boolean recordFrame() throws IOException
	{

		t.tick();
		recordeRate.setText(formater.format(t.getTickRate()));
		if(!compoent.isShowing() || isPaused())
		{
			return true;
		}
		bounds.x = compoent.getLocationOnScreen().x;
		bounds.y = compoent.getLocationOnScreen().y;
		outputVideo.pushImage(robot.createScreenCapture(bounds));
		return true;
	}

	public synchronized boolean isPaused()
	{
		return pause;
	}
	
	public synchronized void setPause(boolean pause)
	{
		this.pause = pause;
	}
	public BufferedImage snapImage()
	{
		Rectangle bounds = new Rectangle();
		bounds.x = compoent.getLocationOnScreen().x;
		bounds.y = compoent.getLocationOnScreen().y;
		bounds.width = compoent.getWidth();
		bounds.height = compoent.getHeight();
		return robot.createScreenCapture(bounds);
	}

	public synchronized void stopRecording() throws IOException
	{
		setRecording(false);
	}
}
