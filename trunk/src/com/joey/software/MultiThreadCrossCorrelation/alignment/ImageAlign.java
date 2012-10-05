package com.joey.software.MultiThreadCrossCorrelation.alignment;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTabbedPane;

import com.joey.software.ImageJLinker.MIJ;
import com.joey.software.MultiThreadCrossCorrelation.threads.CrossCorrelationTool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.timeingToolkit.EventTimer;


public class ImageAlign
{

	public static void initIJ(String path)
	{
		MIJ.start(path + "plugins/");
		MIJ.imagej.setVisible(false);
	}

	public static void main(String input[]) throws InterruptedException
	{
		//initIJ("C:\\Users\\joey.enfield\\programming\\Java Plugins\\Image J\\ij141b-src\\");
		//
		int wide = 1024;
		int high = 512;
		int num = 2;
		byte[] pixelsA = new byte[wide * high];
		byte[] pixelsB = new byte[wide * high];

		ImageStack stackSrc = new ImageStack(wide, high, num);
		stackSrc.setPixels(pixelsA, 1);
		stackSrc.setPixels(pixelsB, 2);

		ImagePlus source = new ImagePlus("Src", stackSrc.getProcessor(1));
		ImagePlus target = new ImagePlus("Dst", stackSrc.getProcessor(2));
		ImagePlus result = null;
		
		if(true)
		{
			ImageProcessor p1 = stackSrc.getProcessor(1);
			ImageProcessor p2 = stackSrc.getProcessor(2);
			
			p1.setColor(255);
			p2.setColor(255);
			
			p1.fillOval(100, 100, 50, 50);
			p2.fillOval(140, 140, 50, 50);
			
			p2.drawLine(200, 200, 210, 210);
		}
		
		ImageAlignTool reg = new ImageAlignTool();
		

		EventTimer t = new EventTimer();
		t.mark("p");
		reg.process(source, target);
		t.tick("p");
		t.printData();
		result = reg.getTransformedImage();
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Source", new ImagePanel(source.getBufferedImage()).getInPanel());
		tabs.addTab("Target", new ImagePanel(target.getBufferedImage()).getInPanel());
		tabs.addTab("Result", new ImagePanel(result.getBufferedImage()).getInPanel());
		
		FrameFactroy.getFrame(tabs);
	}

	public static void copy2Dto1D(byte[][] src, byte[] dst)
	{
		for(int x = 0; x < src.length; x++)
		{
			for(int y=  0; y < src[x].length; y++)
			{
				dst[x+y*src.length] = src[x][y];
			}
		}
	}
	
	public static void copy1Dto2D(byte[] src, byte[][] dst)
	{
		for(int x = 0; x < dst.length; x++)
		{
			for(int y=  0; y < dst[x].length; y++)
			{
				dst[x][y] = src[x+y*src.length];
			}
		}
	}
	
	public static void mainORG(String input[]) throws IOException
	{
		initIJ("C:\\Users\\joey.enfield\\programming\\Java Plugins\\Image J\\ij141b-src\\");

		byte[][] frameDataAfterA = new byte[1024][512];
		byte[][] frameDataAfterB = new byte[1024][512];

		short[][] resultAfter = new short[1024][512];

		int threshold = 50;
		int kerX = 3;
		int kerY = 3;

		DynamicRangeImage resultAfterPan = new DynamicRangeImage(resultAfter);
		resultAfterPan.setMinValue(Short.MAX_VALUE * 0.6f);
		resultAfterPan.setMaxValue(-Short.MAX_VALUE * 0.6f);

		FrameFactroy.getFrame(resultAfterPan);
		String path = "D:\\Current Analysis\\Project Data\\Correlation\\ORAL MUCOSA\\oraL555_3D_000\\7-7-40\\structure\\";
		// int i = 100;
		for (int i = 0; i < 1024; i++)
		{
			System.out.println("num" + i);

			int imageNum = i;
			IJ.run("Image Sequence...", "open=[" + path
					+ "image0000.png] number=2 starting=" + imageNum
					+ " increment=1 scale=100 file=[] or=[] sort");

			// IJ.run("Image Sequence...",
			// "open=[D:\\Current Analysis\\Project Data\\Correlation\\ORAL MUCOSA\\oraL3X3 B_3D_000\\7-7-30\\structure\\image0081.png] number=2 starting=100 increment=1 scale=100 file=[] or=[] sort");
			ImagePlus img = IJ.getImage();
			// ByteProcessor b = new
			// ByteProcessor(10, 10);
			// b.setIntArray(a);

			IJ.run("StackReg ", "transformation=[Rigid Body]");
			img = IJ.getImage();
			getFrame(img.getImageStack().getProcessor(1), frameDataAfterA);
			getFrame(img.getImageStack().getProcessor(2), frameDataAfterB);
			IJ.run("Close");
			CrossCorrelationTool
					.manualCrossCorr(frameDataAfterA, frameDataAfterB, kerX, kerY, threshold, resultAfter);
			resultAfterPan.updateImagePanel();

			ImageIO.write(resultAfterPan.getImage().getImage(), "png", new File(
					path + "Aligned\\image" + i + ".png"));
		}
	}

	public static void getDiff(short[][] A, short[][] B, short[][] diff)
	{
		for (int x = 0; x < A.length; x++)
		{
			for (int y = 0; y < A[x].length; y++)
			{
				diff[x][y] = (short) (B[x][y] - A[x][y]);
			}
		}
	}

	public static void getFrame(ImageProcessor img, byte[][] hold)
	{

		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{

				hold[x][y] = (byte) (img.getPixel(x, y));
			}
		}
	}
}
