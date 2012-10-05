package com.joey.software.interfaces;


import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

import com.joey.software.DataToolkit.IMGDataSet;
import com.joey.software.DataToolkit.ImageSeriesDataSet;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;


public class SpeckleVarianceProgram
{
	public static void main(String input[]) throws IOException
	{

		NativeDataSet dataHold = null;
		if (false)
		{
			ImageFileSelectorPanel panel = new ImageFileSelectorPanel();

			JFrame f = FrameFactroy.getFrame(panel);
			FrameWaitForClose wait = new FrameWaitForClose(f);
			wait.waitForClose();

			dataHold = new ImageSeriesDataSet(panel.getFiles());
		} else
		{
			dataHold = new IMGDataSet(FileSelectionField.getUserFile());
		}

		dataHold.reloadData();

		int sizeX = dataHold.getPreviewSizeX();
		int sizeY = dataHold.getPreviewSizeY();
		int sizeZ = dataHold.getPreviewSizeZ();

		byte[][][] srcData = dataHold.getPreviewData();
		float[][][] rstData = new float[sizeX][sizeY][sizeZ];

		int ker = 0;

		int kerX =1;
		int kerY = 0;
		int kerZ = 0;

		float hold[] = new float[(2 * kerX + 1) * (2 * kerY + 1)
				* (2 * kerZ + 1)];
		int count = 0;

		int xP, yP, zP;
		for (int z = 0; z < sizeZ; z++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				for (int y = 0; y < sizeY; y++)
				{

					// Clear Kernal
					Arrays.fill(hold, 0, hold.length, 0);
					count = 0;

					// Fill Kernal
					for (int xK = -kerX; xK <= kerX; xK++)
					{
						for (int yK = -kerY; yK <= kerY; yK++)
						{
							for (int zK = -kerZ; zK <= kerZ; zK++)
							{
								xP = x + xK;
								yP = y + yK;
								zP = z + zK;

								// Grab Data
								if (xP >= 0 && yP >= 0 && zP >= 0 && xP < sizeX
										&& yP < sizeY && zP < sizeZ)
								{
									hold[count] = srcData[xP][yP][zP];
									count++;
								}

							}
						}
					}

					// Determine Average
					if (count == 0)
					{
						count = 1;
					}
					float avg = DataAnalysisToolkit.getAveragef(hold) / count;

					for (int i = 0; i < count; i++)
					{
						rstData[x][y][z] += (hold[i] - avg) * (hold[i] - avg);
					}
					rstData[x][y][z] /= count;
				}
			}
		}

		DynamicRangeImage img = new DynamicRangeImage();

		float[][] view = new float[sizeY][sizeZ];
		FrameFactroy.getFrame(img);
		int pos = 10;
		while (true)
		{

			if (pos >= sizeZ)
			{
				pos = 0;
			}

			for (int x = 0; x < sizeZ; x++)
			{
				for (int y = 0; y < sizeY; y++)
				{
					view[y][x] = rstData[pos][y][x];
				}
			}
			img.setDataFloat(view);

			pos++;
			FrameWaitForClose.showWaitFrame();
		}
	}
}
