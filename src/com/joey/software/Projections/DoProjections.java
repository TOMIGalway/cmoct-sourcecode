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
package com.joey.software.Projections;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.joey.software.DataToolkit.ImageSeriesDataSet;
import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;


public class DoProjections
{

	public static int b2i(byte val)
	{
		return val < 0 ? 256 + val : val;
	}

	public static void main(String input[]) throws IOException
	{
		doProjections(input);
		//convertMIPToSlices();

		//makeMovie();
		// showSmoothStruct();
	}

	public static void convertMIPToSlices() throws IOException
	{
		ImageFileSelectorPanel images = new ImageFileSelectorPanel();

		File[] files = images.getUserFiles();
		ImageSeriesDataSet data = new ImageSeriesDataSet(files);

		ImagePanel imgPan = new ImagePanel();
		JPanel panel = new JPanel(new BorderLayout());
		StatusBarPanel status = new StatusBarPanel();
		panel.add(status, BorderLayout.SOUTH);
		panel.add(imgPan, BorderLayout.CENTER);

		FrameFactroy.getFrame(panel);

		data.reloadData(status);

		int wide = data.getPreviewSizeX();
		int high = data.getPreviewSizeZ();

		ImageFileSaver save = new ImageFileSaver(4, 8);

		String[] fileSplit = FileOperations.splitFile(files[0]);

		for (int y = 0; y < data.getPreviewSizeY(); y++)
		{
			byte[] dataHold = new byte[wide * high];
			for (int x = 0; x < wide; x++)
			{
				for (int z = 0; z < high; z++)
				{
					dataHold[z * wide + x] = data.getPreviewData()[x][y][z];
				}
			}
			File f = new File(fileSplit[0] + "reslice\\frame" + y + "."
					+ fileSplit[2]);
			FileOperations.ensureDirStruct(f);
			BufferedImage img = ImageOperations
					.pixelsToImage(dataHold, wide, high);
			imgPan.setImage(img);
			save.addData(img, f);
		}

	}

	public static void showSmoothStruct() throws IOException
	{
		BufferedImage struct = ImageOperations
				.getAverageImage(ImageFileSelectorPanel.getUserSelection());
		FrameFactroy.getFrame(struct);
	}

	public static void makeMovie() throws IOException
	{
		BufferedImage struct = ImageOperations
				.getAverageImage(ImageFileSelectorPanel.getUserSelection());
		File[] data = ImageFileSelectorPanel.getUserSelection();

		BufferedImage img = ImageIO.read(data[0]);
		int wide = img.getWidth();
		int high = img.getHeight()+512;
		int frameRate = 10;
		String fileDir = "c:\\test\\";
		String fileName = "avg.avi";
		//BufferedImageStreamToAvi out = new BufferedImageStreamToAvi(wide, high,
			//	frameRate, fileDir, fileName, true, true);

		img = ImageOperations.getBi(1024, 1024 + 512);
		int start = 0;
		int step = 7;
		Graphics2D g = img.createGraphics();
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		ImagePanel panel = new ImagePanel(img);
		// panel.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		FrameFactroy.getFrame(panel.getInPanel());
		for (int i = 0; i < data.length; i++)
		{
			BufferedImage slice = ImageIO.read(data[i]);
			ImageOperations.setImage(Color.white, img);
			g.drawImage(slice, 0, 512, 1024, 1024, null);
			g.drawImage(struct, null, 0, 0);
			g.setColor(Color.red);
			g.fillRect(0, i, struct.getWidth(), 2);
			g.fillRect(0, i+step-2, struct.getWidth(), 2);

			System.out.println("Processed : " + (i + 1) + " of " + data.length);
			panel.repaint();
			//out.pushImage(img);
			ImageIO.write(img, "png", new File("c:\\test\\movie\\frame" + i
					+ ".png"));
		}
		//out.finaliseVideo();
	}

	public static void doProjections(String input[]) throws IOException
	{
		ImageFileSelectorPanel images = new ImageFileSelectorPanel();

		File[] files = images.getUserFiles();
		ImageSeriesDataSet data = new ImageSeriesDataSet(files);

		float[][] mip = new float[data.getSizeDataX()][data.getSizeDataZ()];
		float[][] avg = new float[data.getSizeDataX()][data.getSizeDataZ()];

		DynamicRangeImage mipImg = new DynamicRangeImage(mip);
		DynamicRangeImage avgImg = new DynamicRangeImage(avg);

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("AVG", avgImg);
		tab.addTab("MIP", mipImg);

		StatusBarPanel status = new StatusBarPanel();

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(status, BorderLayout.SOUTH);
		panel.add(tab, BorderLayout.CENTER);

		FrameFactroy.getFrame(panel);

		data.reloadData(status);
		status.setMaximum(data.getSizeDataX());

		int ker = 2;
		int step = 7;
		String[] fileData = FileOperations.splitFile(files[0]);
		// Create Output Location
		String folder = fileData[0] + "\\MIP\\Ker " + +ker + "\\ Step " + step
				+ " (1)\\";
		File newF = new File(folder);
		newF.getParentFile().mkdirs();
		System.out.println(newF);
		newF.mkdirs();

		// for (int ker = 1; ker < 5; ker++)
		// for (int step = 5; step < 25; step +=
		// 5)
		for (int yStart = 0; yStart < data.getSizeDataY(); yStart += 1)
		{
			int hold = 0;
			int count = 0;

			int kerX = ker;
			int kerZ = ker;
			int xV = 0;
			int zV = 0;

			int yEnd = yStart + step;
			for (int x = 0; x < mip.length; x++)
			{
				for (int y = 0; y < mip[x].length; y++)
				{
					mip[x][y] = 0;
					avg[x][y] = 0;
				}
			}

			for (int x = 0; x < data.getSizeDataX(); x++)
			{
				status.setValue(x);
				for (int z = 0; z < data.getSizeDataZ(); z++)
				{
					for (int y = yStart; y < yEnd && y < data.getSizeDataY(); y++)
					{
						hold = 0;
						count = 0;
						for (int xP = -kerX; xP <= kerX; xP++)
						{
							for (int zP = -kerZ; zP <= kerZ; zP++)
							{
								xV = x + xP;
								zV = z + zP;
								if (xV < data.getSizeDataX()
										&& zV < data.getSizeDataZ() && xV >= 0
										&& zV >= 0)
								{
									hold += b2i(data.getPreviewData()[xV][y][zV]);
									count++;
								}
							}
						}
						if (count == 0)
						{
							count = 1;
						}
						hold /= count;

						if (mip[x][z] < hold || y == 0)
						{
							mip[x][z] = hold;
						}

						avg[x][z] += hold / step;
					}
				}
			}
			// mipImg.setMap(ColorMap.getColorMap(ColorMap.TYPE_FIRE_BALL));
			mipImg.setMaxValue(255);
			mipImg.setMinValue(0);
			mipImg.updateImagePanel();
			// avgImg.setMap(ColorMap.getColorMap(ColorMap.TYPE_FIRE_BALL));
			avgImg.setMaxValue(255);
			avgImg.setMinValue(0);
			avgImg.updateImagePanel();

			File mipFile = new File(folder + "\\MIP[" + yStart + "-" + yEnd
					+ "].png");
			File avgFile = new File(folder + "\\AVG[" + yStart + "-" + yEnd
					+ "].png");

			FileOperations.ensureDirStruct(mipFile);
			FileOperations.ensureDirStruct(avgFile);

			ImageIO.write(mipImg.getImage().getImage(), "png", mipFile);
			ImageIO.write(avgImg.getImage().getImage(), "png", avgFile);

		}

	}
}
