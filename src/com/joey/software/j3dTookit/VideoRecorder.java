package com.joey.software.j3dTookit;


import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Raster;
import javax.media.j3d.Screen3D;
import javax.vecmath.Point3f;


import com.joey.software.VolumeToolkit.VolumeViewerPanel;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImagePanel;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class VideoRecorder extends Canvas3D
{
	int frameRate = 25;

	protected Canvas3D canvas;

	protected SimpleUniverse universe;

	FrameProducer producer;

	FrameConsumer consumer;

	ImagePanel panel = new ImagePanel();

	public static void main(String input[]) throws InterruptedException
	{
		final VolumeViewerPanel panel = new VolumeViewerPanel();
		final VideoRecorder shoot = new VideoRecorder(panel.getRender()
				.getCanvas(), panel.getRender().getUniverse(), 1, 1000, 800,
				600);

		// FrameFactroy.getFrame(new JPanel[]{panel, shoot.panel}, 1, 2);
		FrameFactroy.getFrame(panel);
		Thread.sleep(1000);
		FrameFactroy.getFrame(shoot.panel);
		Thread.sleep(5000);
		shoot.startRecording();

		// OR you can also put the
		// control in a floating Frame;
		// Frame f = new Frame("ScreenShot Control");
		// f.setSize(240, 200);
		// f.add(shoot.getControlPanel());
		// f.pack();
		// f.setVisible(true);
		//
		// FrameFactroy.getFrame(panel);
	}

	public VideoRecorder(Canvas3D canvas, SimpleUniverse universe, float scale, int frameRate, int wide, int high)
	{
		super(canvas.getGraphicsConfiguration(), true);
		this.canvas = canvas;
		this.universe = universe;
		this.frameRate = frameRate;
		producer = new FrameProducer(this, frameRate, wide, high);
		consumer = new FrameConsumer();

		universe.getViewer().getView().addCanvas3D(this);
		setFrameRate(frameRate);
	}

	public void setFrameRate(int rate)
	{
		this.frameRate = rate;
	}

	public void startRecording()
	{
		producer.setFrameRate(frameRate);
		producer.startProducing();
	}

	public void stopRecording()
	{

	}
}

class FrameProducer implements Runnable
{

	VideoRecorder owner;

	int frameRate;

	Dimension outSize = new Dimension();

	Thread t = new Thread(this);

	long delay;

	boolean running = false;

	boolean alive = true;

	public FrameProducer(VideoRecorder owner, int frameRate, int wide, int high)
	{
		this.owner = owner;
		setFrameRate(frameRate);
		setOutputSize(wide, high);
		t.start();
	}

	public void setFrameRate(int rate)
	{
		frameRate = rate;
		delay = (long) (1.0 / frameRate * 1000);
	}

	public synchronized void startProducing()
	{
		System.out.println("Starting");
		running = true;
		notifyAll();
	}

	public void stopProducing()
	{
		running = false;
	}

	public void setOutputSize(int width, int height)
	{
		Screen3D sOff = owner.getScreen3D();
		Screen3D sOn = owner.canvas.getScreen3D();

		outSize.width = width;
		outSize.height = height;

		sOff.setSize(outSize);
		sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth() / 2);
		sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight() / 2);
	}

	@Override
	public synchronized void run()
	{
		Point3f p = new Point3f(-1.0f, -1.0f, -1.0f);

		ImageComponent2D ic = new ImageComponent2D(ImageComponent.FORMAT_RGB,
				new BufferedImage(outSize.width, outSize.height,
						BufferedImage.TYPE_INT_RGB));
		Raster ras = new Raster(p, Raster.RASTER_COLOR, 0, 0, owner.getWidth(),
				owner.getHeight(), ic, null);

		while (alive)
		{
			System.out.print("Start Loop : ");
			if (running)
			{
				System.out.print("Processing - ");
				try
				{
					// buffer.setYUp(true);
					// owner.renderOffScreenBuffer();
					// owner.waitForOffScreenRendering();
					// owner.panel.setImage(owner.getOffScreenBuffer().getImage());
					// owner.panel.repaint();

					// Robot r = new Robot();
					// owner.panel.setImage(r.createScreenCapture(owner.getBounds()));

					GraphicsContext3D ctx = owner.canvas.getGraphicsContext3D();
					// The raster components need all be set!
					ctx.readRaster(ras);

					owner.panel.setImage(ras.getImage().getImage());
					System.out.println("Success - Delay : " + delay);
					Thread.sleep(delay);
				} catch (Exception e)
				{
					System.out.println("Failed");
					e.printStackTrace();
				}
			} else
			{
				System.out.println("Waiting");
				try
				{
					wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}

class FrameConsumer implements Runnable
{

	@Override
	public void run()
	{
		// TODO Auto-generated method stub

	}

}