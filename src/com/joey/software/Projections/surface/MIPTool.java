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
package com.joey.software.Projections.surface;


import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.MultiThreadCrossCorrelation.CrossCorrelationDataset;
import com.joey.software.Presentation.ReactiveHyperimeaTool;
import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.stringToolkit.StringOperations;


public class MIPTool
{
	public static void main(String input[]) throws IOException, InterruptedException
	{
		//BatchReProjectMIP()
		
		
		StatusBarPanel status = new StatusBarPanel();
		FrameFactroy.getFrame(status);
		byte[][][] data = SurfaceFinderTool.getImageSeriesData(status);

		float[][] mip = new float[data[0].length][data.length];

		DynamicRangeImage img = new DynamicRangeImage();
		int start = 0;
		int step = 3;
		img.setDataFloat(mip);
		FrameFactroy.getFrame(img);
		FFT2Dtool tool = new FFT2Dtool(mip.length, mip[0].length);
		tool.allocateMemory();
		
		while (true)
		{
			mipDataY(data, mip, start, start+step);
			tool.setRealData(mip);
			tool.fftData();
			tool.gaussianMask(100f);
			tool.ifftData(true);
			tool.getMagData(mip);		
			start+= 1;
			if(start+step > data[0][0].length)
			{
				start = 0;
			}
			img.updateImagePanel();
			System.out.println("now"+start);
		}
	}

	
	public static void BatchReProjectMIP() throws IOException
	{
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = true; 
		int proStart = 30;
		int proEnd = 512;
		
		final Vector<File[]> dataHolder = new Vector<File[]>();

		ImageProducer inputData;
		JPanel pan = new JPanel(new BorderLayout());

		final DefaultListModel modelList = new DefaultListModel();
		final JList fileList = new JList(modelList);

		pan.add(new JScrollPane(fileList));
		FileDrop drop = new FileDrop(pan, new FileDrop.Listener()
		{

			@Override
			public void filesDropped(File[] files)
			{
				for (File f : files)
				{
					modelList.add(0, f.toString());
					dataHolder.add(new File[] { f });
				}
			}
		});
		JFrame wait = FrameFactroy.getFrame(pan);
		FrameWaitForClose c = new FrameWaitForClose(wait);
		c.waitForClose();

	
		float[][] MIP = null;
		float[][] MIPsmooth = null;
		byte[][][] data = null;
		
		
		for (int i = 0; i < dataHolder.size(); i++)
		{
			System.out.println("Processing : "+(i+1)+" of "+dataHolder.size());
			File file = dataHolder.get(i)[0];

			ThorlabsIMGImageProducer imgLoader = new ThorlabsIMGImageProducer(
					file, true);
			ImageFileProducer imageLoader = new ImageFileProducer(
					ReactiveHyperimeaTool.getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned));
			
			MIP = new float[imgLoader.getSizeX()][imgLoader.getSizeZ()];
			MIPsmooth = new float[imgLoader.getSizeX()][imgLoader.getSizeZ()];
			data = imageLoader.createDataHolder();
			imageLoader.getData(data, null);
			
			MIPTool.mipDataY(data, MIP, proStart, proEnd);
			//ArrayToolkit.smoothData(MIP, MIPsmooth, 1, 1);
			BufferedImage img = CrossCorrelationDataset.getMIPData(MIP,50,255,ColorMap.getColorMap(ColorMap.TYPE_GLOW));
			ImageOperations.saveImage(img, "c:\\test\\image"+StringOperations.getNumberString(3, i)+".png");
		}
	}
	
	public static void mipDataY(byte[][][] data, float[][] mip)
	{
		mipDataY(data, mip, 0, data.length);
	}

	public static void mipDataY(byte[][][] data, float[][] mip, int startY, int endY)
	{
		mipData(data, 0, data[0].length, startY, endY, 0, data.length, null, mip, null);
	}

	/**
	 * This function will perform MIP for the
	 * given volume stored in data. This will can
	 * be passed null for non required axis
	 * 
	 * @param data
	 *            [z][x][y]
	 * @param xStart
	 * @param xEnd
	 * @param yStart
	 * @param yEnd
	 * @param zStart
	 * @param zEnd
	 * @param mipX
	 *            [y][z]
	 * @param mipY
	 *            [x][z]
	 * @param mipZ
	 *            [x][y]
	 */
	public static void mipData(byte[][][] data, int xStart, int xEnd, int yStart, int yEnd, int zStart, int zEnd, float[][] mipX, float[][] mipY, float[][] mipZ)
	{

		int val = 0;
		for (int z = zStart; z < zEnd && z < data.length; z++)
		{
			for (int x = xStart; x < xEnd && x < data[z].length; x++)
			{
				for (int y = yStart; y < yEnd && y < data[z][x].length; y++)
				{
					val = b2i(data[z][x][y]);

					// Y mip
					if (mipY != null && ((mipY[x][z] < val) || (y == yStart)))
					{
						mipY[x][z] = val;
					}

					// X mip
					if (mipX != null && ((mipX[y][z] < val) || (x == xStart)))
					{
						mipX[y][z] = val;
					}

					// Z mip
					if (mipZ != null && ((mipZ[x][y] < val) || (z == zStart)))
					{
						mipZ[x][y] = val;
					}
				}
			}
		}
	}

	public static int b2i(byte v)
	{
		return (v >= 0 ? v : v + 255);
	}
}
