package com.joey.software.interfaces;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JTabbedPane;

import com.joey.software.DataToolkit.ThorLabs2DImageProducer;
import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;


/**
 * This program will generate a comparison between cross correlation and speckle
 * Variance
 * 
 * @author joey.enfield
 * 
 */
public class SpeckleCompareCrossCorr
{
	public static void main(String input[]) throws IOException
	{
		File file = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\for cross-correlation processing\\ZeroComma6mlperhours\\OCT\\OCTat10degreesflowat0comma6mlperhour_001.IMG");
		ThorLabs2DImageProducer data = new ThorLabs2DImageProducer(file);

		int wide = data.getImage(0).getWidth();
		int high = data.getImage(0).getHeight();

		Rectangle statRegion = new Rectangle(77, 40, 442, 80);
		Rectangle flowRegion = new Rectangle(870, 80, 400, 40);
		Rectangle backRegion = new Rectangle(100, 300, 400, 100);
		
		DynamicRangeImage stImg = new DynamicRangeImage();
		DynamicRangeImage svImg = new DynamicRangeImage();
		DynamicRangeImage ccImg = new DynamicRangeImage();
		
		stImg.getImage().setAllowMultipleROI(true);
		stImg.getImage().setRegionColor(Color.red);
		stImg.getImage().addRegion((Rectangle)statRegion.clone());
		stImg.getImage().addRegion((Rectangle)flowRegion.clone());
		stImg.getImage().addRegion((Rectangle)backRegion.clone());
		

		svImg.getImage().setAllowMultipleROI(true);
		svImg.getImage().setRegionColor(Color.red);
		svImg.getImage().addRegion((Rectangle)statRegion.clone());
		svImg.getImage().addRegion((Rectangle)flowRegion.clone());
		svImg.getImage().addRegion((Rectangle)backRegion.clone());
		
		ccImg.getImage().setAllowMultipleROI(true);
		ccImg.getImage().setRegionColor(Color.red);
		ccImg.getImage().addRegion((Rectangle)statRegion.clone());
		ccImg.getImage().addRegion((Rectangle)flowRegion.clone());
		ccImg.getImage().addRegion((Rectangle)backRegion.clone());
		
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("ST", stImg);
		tabs.addTab("SV", svImg);
		tabs.addTab("CC", ccImg);
		FrameFactroy.getFrame(tabs);
		
		Rectangle[] rec =
		{ statRegion, flowRegion, backRegion };
		String[] out =
		{ "Static Region\nFrame Num,SV Avg, SV Max,SV Min, CC Avg,CC Max,CC Min,\n", "Flow region\nFrame Num,SV Avg, SV Max,SV Min, CC Avg,CC Max,CC Min,\n", "Background Region\nFrame Num,SV Avg, SV Max,SV Min, CC Avg,CC Max,CC Min,\n" };
		int ccKer = 1;
		int ccNum = 1;
		boolean runCC = true;

		int svNum = 20;
		boolean runSV = true;

		// int frame = 10;
		for (int frame = 0; frame < data.getImageCount() - svNum; frame++)
		{
			stImg.setData(data.getImage(frame));
			
			System.out.println("\n\n\n\n\n##########################################");
			System.out.println("##########################################");
			System.out.println("##########################################");
			System.out.println("##########################################");
			System.out.println("##########################################");
			System.out.println(CSVFileToolkit.joinDataRight(CSVFileToolkit
					.joinDataRight(out[0], out[1]), out[2]));
			System.out.println("##########################################");
			System.out.println("##########################################");
			float[][] ccOCT = new float[wide][high];
			if (runCC)
			{
				/**
				 * Cross Correlation
				 */

				int ccKerX = ccKer;
				int ccKerY = ccKer;

				int ccMeanThreshold = 0;
				int ccImageNum = ccNum + 1;
				final int[] ccImageInput = new int[wide * high * ccImageNum];
				float[] ccStatsOutput = new float[wide * high * ccNum];

				/*
				 * Load the frame data
				 */

				for (int i = 0; i < ccImageNum; i++)
				{
					data.getImageData(frame + i, ccImageInput, i
							* (wide * high));
				}

				// Do Cross Correlation
				CPUVersion
						.crossCorr(ccImageInput, wide, high, ccKerX, ccKerY, ccNum, ccMeanThreshold, ccStatsOutput);

				// Get the average correlation
				for (int xP = 0; xP < wide; xP++)
				{
					for (int yP = 0; yP < high; yP++)
					{
						for (int i = 0; i < ccNum; i++)
						{
							ccOCT[xP][yP] += ccStatsOutput[i * wide * high + xP
									* high + yP]
									/ ccNum;
						}
					}
				}

			}

			float[][] svOCT = new float[wide][high];
			if (runSV)
			{
				/**
				 * Speckle Variance Data
				 */
				int[] svData = new int[svNum * wide * high];

				float svHold[] = new float[svNum];

				// Load Data
				for (int i = 0; i < svNum; i++)
				{
					data.getImageData(frame + i, svData, i * (wide * high));
				}

				for (int xP = 0; xP < wide; xP++)
				{
					for (int yP = 0; yP < high; yP++)
					{

						Arrays.fill(svHold, 0);
						for (int zP = 0; zP < svNum; zP++)
						{
							svHold[zP] = svData[zP * wide * high + high * xP
									+ yP];
						}

						float avg = DataAnalysisToolkit.getAveragef(svHold);

						for (int zP = 0; zP < svNum; zP++)
						{
							svOCT[xP][yP] = (svHold[zP] - avg)
									* (svHold[zP] - avg) / svNum;
						}
					}
				}

			}
			
			ccImg.setDataFloat(ccOCT);
			svImg.setDataFloat(svOCT);
		
			// Determin regions

			for (int r = 0; r < rec.length; r++)
			{
				float avgCC = 0;
				float maxCC = ccOCT[rec[r].x][rec[r].y];
				float minCC = ccOCT[rec[r].x][rec[r].y];

				float avgSV = 0;
				float maxSV = svOCT[rec[r].x][rec[r].y];
				float minSV = svOCT[rec[r].x][rec[r].y];
				float count = 0;
				for (int x = rec[r].x; x < rec[r].x + rec[r].width; x++)
				{
					for (int y = rec[r].y; y < rec[r].y + rec[r].height; y++)
					{
						count++;

						avgSV += svOCT[x][y];
						avgCC += ccOCT[x][y];

						if (maxCC < ccOCT[x][y])
						{
							maxCC = ccOCT[x][y];
						}
						if (maxSV < svOCT[x][y])
						{
							maxSV = svOCT[x][y];
						}

						if (minCC > ccOCT[x][y])
						{
							minCC = ccOCT[x][y];
						}
						if (minSV > svOCT[x][y])
						{
							minSV = svOCT[x][y];
						}
					}
				}

				avgSV /= count;
				avgCC /= count;

				out[r] += (frame + "," + avgSV + "," + maxSV + "," + minSV
						+ "," + avgCC + "," + maxCC + "," + minCC + "\n");

			}
		}
	}

	public static void generateSpeckleVsN(String input[]) throws IOException
	{
		File file = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\for cross-correlation processing\\ZeroComma6mlperhours\\OCT\\OCTat10degreesflowat0comma6mlperhour_001.IMG");
		ThorLabs2DImageProducer data = new ThorLabs2DImageProducer(file);

		int wide = data.getImage(0).getWidth();
		int high = data.getImage(0).getHeight();

		int frame = 10;

		float max = 0;
		float min = 0;

		float globalMax = 4000.26f;
		float globalMin = 0f;

		DynamicRangeImage img = new DynamicRangeImage();
		FrameFactroy.getFrame(img);
		for (int svNum = 1; svNum < 50; svNum++)
		{
			float fmax = 0;
			float fmin = 0;

			System.out.print("\n" + svNum);
			/**
			 * Speckle Variance Data
			 */
			int[] svData = new int[svNum * wide * high];
			float[][] svOCT = new float[wide][high];
			float svHold[] = new float[svNum];

			// Load Data
			for (int i = 0; i < svNum; i++)
			{
				data.getImageData(frame + i, svData, i * (wide * high));
			}

			for (int xP = 0; xP < wide; xP++)
			{
				for (int yP = 0; yP < high; yP++)
				{

					Arrays.fill(svHold, 0);
					for (int zP = 0; zP < svNum; zP++)
					{
						svHold[zP] = svData[zP * wide * high + high * xP + yP];
					}

					float avg = DataAnalysisToolkit.getAveragef(svHold);

					for (int zP = 0; zP < svNum; zP++)
					{
						svOCT[xP][yP] = (svHold[zP] - avg) * (svHold[zP] - avg)
								/ svNum;
					}

					if (svOCT[xP][yP] > fmax)
					{
						fmax = svOCT[xP][yP];
					}

					if (svOCT[xP][yP] < min)
					{
						fmin = svOCT[xP][yP];
					}
				}
			}

			if (max < fmax)
			{
				max = fmax;
			}

			if (min > fmin)
			{
				min = fmin;
			}
			System.out.print(" : [" + fmin + "," + fmax + "] - {" + max + ":"
					+ min + "}");

			img.setDataFloat(svOCT);
			img.updateMaxMin();
			img.updateImagePanel();
			ImageIO
					.write(img.getImage().getImage(), "png", new File(
							String
									.format("c:\\test\\sv\\sv_localRange_%d_[%1.0f-%1.0f].png", svNum, img
											.getMinSelection(), img
											.getMaxSelection())));
			img.setMaxValue(globalMax);
			img.setMinValue(globalMin);
			img.updateImagePanel();
			ImageIO
					.write(img.getImage().getImage(), "png", new File(
							String
									.format("c:\\test\\sv\\sv_globalRange_%d_[%1.0f-%1.0f].png", svNum, img
											.getMinSelection(), img
											.getMaxSelection())));
			
		}
	}

	public static void GenerateCCKerVsAvgNumGrid(String input[])
			throws IOException
	{
		File file = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\for cross-correlation processing\\ZeroComma6mlperhours\\OCT\\OCTat10degreesflowat0comma6mlperhour_001.IMG");
		ThorLabs2DImageProducer data = new ThorLabs2DImageProducer(file);

		int wide = data.getImage(0).getWidth();
		int high = data.getImage(0).getHeight();

		int wideOut = 512;
		int highOut = 512;

		int xNum = 6;
		int yNum = 6;

		DynamicRangeImage preview = new DynamicRangeImage();

		BufferedImage output = ImageOperations.getBi(xNum * wideOut, yNum
				* highOut);

		FrameFactroy.getFrame(output);
		// FrameFactroy.getFrame(preview);

		for (int x = 0; x < xNum; x++)
		{
			for (int y = 0; y < yNum; y++)
			{
				System.out.println(x + " : " + y);
				// Cross Correlation data
				int ker = x + 1;
				int ccKerX = ker;
				int ccKerY = ker;
				int ccNum = y + 1;

				int meanThreshold = 30;
				int imageNum = ccNum + 1;
				final int[] imageInput = new int[wide * high * imageNum];
				float[] statsOutput = new float[wide * high * ccNum];
				float[][] ccOCT = new float[wide][high];

				/*
				 * Load the frame data
				 */
				int frame = 10;
				for (int i = 0; i < imageNum; i++)
				{
					data.getImageData(frame + i, imageInput, i * (wide * high));
				}

				// Do Cross Correlation
				CPUVersion
						.crossCorr(imageInput, wide, high, ccKerX, ccKerY, ccNum, meanThreshold, statsOutput);

				// Get the average correlation
				for (int xP = 0; xP < wide; xP++)
				{
					for (int yP = 0; yP < high; yP++)
					{
						for (int i = 0; i < ccNum; i++)
						{
							ccOCT[xP][yP] += statsOutput[i * wide * high + xP
									* high + yP]
									/ ccNum;
						}
					}
				}

				preview.setDataFloat(ccOCT);
				preview.setMinValue(0.5f);
				preview.setMaxValue(0);
				preview.updateImagePanel();

				Graphics2D g = output.createGraphics();
				GraphicsToolkit
						.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
				g.drawImage(preview.getImage().getImage(), x * wideOut, y
						* highOut, wideOut, highOut, null);
			}
		}
	}
}
