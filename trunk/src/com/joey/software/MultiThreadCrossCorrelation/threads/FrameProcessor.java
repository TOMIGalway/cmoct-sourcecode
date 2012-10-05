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
package com.joey.software.MultiThreadCrossCorrelation.threads;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.joey.software.MultiThreadCrossCorrelation.CrossCorrProgram;
import com.joey.software.MultiThreadCrossCorrelation.alignment.AlignmentTool;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;
import com.joey.software.timeingToolkit.EventTimer;


public class FrameProcessor
{
	static byte[] dataFrmA = null;

	static byte[] dataFrmB = null;

	static byte[] outFrameByte = null;

	static int[] gridA = null;

	static int[] gridB = null;

	static Date today = new Date(System.currentTimeMillis());

	static Date start = new Date(111, 5, 24);

	static Date end = new Date(111, 6, 20);



	public static void processSingleFrame(CrossCorrProgram program)
			throws IOException
	{

		EventTimer t = new EventTimer();

		int sizeX = program.getSizeX();
		int sizeY = program.getSizeY();
		int sizeZ = program.getSizeZ();

		int kerX = program.getCorKerSizeX();
		int kerY = program.getCorKerSizeY();

		int kerOrderX = (kerX - 1) / 2;
		int kerOrderY = (kerY - 1) / 2;
		t.mark("memory");
		if (dataFrmA == null || dataFrmA.length != sizeX * sizeY)
		{
			dataFrmA = new byte[sizeX * sizeY];
			dataFrmB = new byte[sizeX * sizeY];
			outFrameByte = new byte[sizeX * sizeY];
		}
		if (gridA == null || gridA.length != kerX * kerY)
		{
			gridA = new int[kerX * kerY];
			gridB = new int[kerX * kerY];
		}
		t.tick("memory");
		t.printData();

		
		
		t.mark("load");
		int pxlCount = 0;
		int high = sizeY;
		program.getImage(program.getCurrentFrame(), dataFrmA, 0);
		program.getImage(program.getCurrentFrame() + 1, dataFrmB, 0);
		t.tick("load");
		t.printData();

		t.mark("Align");
		if (program.getAlignImages())
		{
			AlignmentTool tool = new AlignmentTool(sizeX, sizeY);
			dataFrmB = tool.alignFrames(dataFrmA, dataFrmB);
			tool = null;
		}
		t.tick("Align");

		t.mark("Process");
		for (int xP = 0; xP < sizeX; xP++)
		{
			for (int yP = 0; yP < sizeY; yP++)
			{

				pxlCount = 0;
				// Grab the data and put into
				// linear arrays
				for (int x = xP - kerOrderX; x <= xP + kerOrderX; x++)
				{
					for (int y = yP - kerOrderY; y <= yP + kerOrderY; y++)
					{
						if (x < sizeX && y < sizeY && x >= 0 && y >= 0)
						{
							gridA[pxlCount] = (dataFrmA[x * high + y] < 0 ? dataFrmA[x
									* high + y] + 256
									: dataFrmA[x * high + y]);
							gridB[pxlCount] = (dataFrmB[x * high + y] < 0 ? dataFrmB[x
									* high + y] + 256
									: dataFrmB[x * high + y]);
						} else
						{
							gridA[pxlCount] = 0;
							gridB[pxlCount] = 0;
						}
						pxlCount++;
					}
				}
				// Calculate the cross coralation
				double crossCorr = CrossCorrelationWorker
						.getCrossCorr(gridA, gridB, program
								.getBackgroundThreshold());

				// Remap for output
				crossCorr = (crossCorr - program.getMinValue())
						/ (program.getMaxValue() - program.getMinValue());
				if (crossCorr > 1)
				{
					crossCorr = 1;
				}

				if (crossCorr < 0)
				{
					crossCorr = 0;
				}
				outFrameByte[yP * sizeX + xP] = (byte) (255 * crossCorr);
			}
		}
		t.tick("Process");
		t.printData();
		
		
		//Perform Summation

		t.mark("Make Struct");

		BufferedImage img = ImageOperations
				.getRotatedImage(ImageOperations
						.pixelsToImage(dataFrmA, program.getSizeY(), program
								.getSizeX()), 1);
		ImageOperations.flipImage(img, ImageOperations.Y_AXIS);
		program.structralPanel.setImage(img);
		t.tick("Make Struct");

		t.mark("Make flow");
		program.flowPanel.setImage(ImageOperations
				.pixelsToImage(outFrameByte, program.getSizeX(), program
						.getSizeY()));
		t.tick("Make flow");
		t.printData();
		program.repaint();

	}

	public static void processFrame(CrossCorrelationWorker worker)
	{

		byte[][] frmA = null;
		byte[][] frmB = null;
		try
		{
			frmA = worker.master.task.getFrame(worker.currentFrame);
			frmB = worker.master.task.getFrame(worker.currentFrame + 1);
		} catch (IOException e)
		{
			System.out.println("Error processing frame : "
					+ worker.currentFrame);
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (worker.master.task.isImageAlign())
		{
			frmB = worker.getAlignmentTool().alignFrames(frmA, frmB);
		}
		for (int xP = 0; xP < worker.master.task.getSizeX(); xP++)
		{
			for (int yP = 0; yP < worker.master.task.getSizeY(); yP++)
			{

				worker.pxlCount = 0;
				// Grab the data and put into
				// linear arrays
				for (int x = xP - worker.master.task.getKerX(); x <= xP
						+ worker.master.task.getKerX(); x++)
				{
					for (int y = yP - worker.master.task.getKerY(); y <= yP
							+ worker.master.task.getKerY(); y++)
					{
						if (x < worker.master.task.getSizeX()
								&& y < worker.master.task.getSizeY() && x >= 0
								&& y >= 0)
						{
							worker.gridA[worker.pxlCount] = (frmA[x][y] < 0 ? frmA[x][y] + 265
									: frmA[x][y]);
							worker.gridB[worker.pxlCount] = (frmB[x][y] < 0 ? frmB[x][y] + 265
									: frmB[x][y]);
						} else
						{
							worker.gridA[worker.pxlCount] = 0;
							worker.gridB[worker.pxlCount] = 0;
						}
						worker.pxlCount++;
					}
				}
				// Calculate the cross coralation

				worker.crossCorr = CrossCorrelationWorker
						.getCrossCorr(worker.gridA, worker.gridB, worker.master.task
								.getThreshold());

				// DO MIP (Min Corellation
				// Projection)
				if (worker.master.task.isMIPinMemory())
				{
					if (((worker.master.task.MIPData[xP][worker.currentFrame] > worker.crossCorr) && (yP >= worker.master.task
							.getMinPosMIP() && yP <= worker.master.task
							.getMaxPosMIP()))
							|| yP == worker.master.task.getMinPosMIP())
					{
						worker.master.task.MIPData[xP][worker.currentFrame] = worker.crossCorr;
						worker.master.task.MIPDepth[xP][worker.currentFrame] = yP;
					}
				}
				/*
				 * Storeing the raw cmOCT values
				 */
				if (worker.master.task.isCrossCorrRawInMemory())
				{
					worker.master.task.crossCorrRawData[worker.currentFrame][xP][yP] = (short) ((Short.MAX_VALUE) * worker.crossCorr);
				}

				// Remap for byte Output data
				if (worker.master.task.isCrossCorrByteInMemory()
						|| worker.master.task.isSaveFlow())
				{
					worker.crossCorr = (worker.crossCorr - worker.master.task
							.getCrossCorrMin())
							/ (worker.master.task.getCrossCorrMax() - worker.master.task
									.getCrossCorrMin());
					if (worker.crossCorr > 1)
					{
						worker.crossCorr = 1;
					}

					if (worker.crossCorr < 0)
					{
						worker.crossCorr = 0;
					}
				}

				if (worker.master.task.isSaveFlow())
				{
					worker.outFrameByte[yP * worker.master.task.getSizeX() + xP] = (byte) (255 * worker.crossCorr);
				}

				if (worker.master.task.isCrossCorrByteInMemory())
				{
					worker.master.task.crossCorrByteData[worker.currentFrame][xP][yP] = (byte) (255 * worker.crossCorr);
				}
			}

		}

		// Save the frame
		if (worker.master.task.isSaveStruct())
		{
			byte[] struct = new byte[worker.master.task.getSizeX()
					* worker.master.task.getSizeY()];
			for (int xS = 0; xS < worker.master.task.getSizeX(); xS++)
			{
				for (int yS = 0; yS < worker.master.task.getSizeY(); yS++)
				{
					struct[yS * worker.master.task.getSizeX() + xS] = frmA[xS][yS];
				}
			}

			worker.imageSave
					.addData(new File(worker.master.task.getSavePath()
							+ "\\structure\\image"
							+ StringOperations
									.getNumberString(4, worker.currentFrame)
							+ ".png"), ImageOperations
							.pixelsToImage(struct, worker.master.task
									.getSizeX(), worker.master.task.getSizeY()));
		}

		if (worker.master.task.isSaveFlow())
		{
			byte[] flow = worker.outFrameByte.clone();
			worker.imageSave
					.addData(new File(worker.master.task.getSavePath()
							+ "\\flow\\image"
							+ StringOperations
									.getNumberString(4, worker.currentFrame)
							+ ".png"), ImageOperations.pixelsToImage(flow, worker.master.task
							.getSizeX(), worker.master.task.getSizeY()));

		}

	}
}
