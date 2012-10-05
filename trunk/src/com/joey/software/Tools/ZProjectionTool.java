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
package com.joey.software.Tools;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.joey.software.DataToolkit.ImageSeriesDataSet;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;


public class ZProjectionTool
{
	public static void main(String input[]) throws IOException
	{
		//doItImageSeries(input);
		viewIt(input);
	}

	public static void viewIt(String input[])
	{

		final DynamicRangeImage img = new DynamicRangeImage();
		img.setDataFloat(new float[1][1]);
		final JFrame f = FrameFactroy.getFrame(img);
		FileDrop drop = new FileDrop(img, new FileDrop.Listener()
		{

			@Override
			public void filesDropped(File[] files)
			{
				try
				{
					f.setTitle(files[0].toString());
					Dimension d = readDataSize(files[0]);
					float[][] data = img.getDataFloat();

					if (data.length != d.width || data[0].length != d.height)
					{
						data = new float[d.width][d.height];
						img.setDataFloat(data);
					}

					readData(data, files[0]);
					img.updateMaxMin();
					img.updateImagePanel();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	public static void doItDRGRAW(String input[]) throws IOException
	{

		Vector<File> inputFiles = new Vector<File>();
		File last = null;
		do
		{
			last = FileSelectionField.getUserFile();
			if (last != null)
			{
				inputFiles.add(last);
			}
		} while (last != null);
		File[] inputData = inputFiles.toArray(new File[0]);
		NativeDataSet data[] = new NativeDataSet[inputData.length];
		for (int i = 0; i < inputData.length; i++)
		{
			File f = inputData[i];
			boolean loadFull = true;
			File raw = null;
			File prv = null;

			String[] fileData = FileOperations.splitFile(f);

			if (fileData[2].equalsIgnoreCase("drgraw"))
			{
				loadFull = true;
				raw = new File(fileData[0] + fileData[1] + "." + fileData[2]);

				prv = new File(fileData[0] + fileData[1] + ".drgprv");
			} else
			{
				loadFull = false;
				raw = new File(fileData[0] + fileData[1] + ".drgraw");

				prv = new File(fileData[0] + fileData[1] + "." + fileData[2]);
			}
			data[i] = new NativeDataSet(raw, prv);
			data[i].setLoadFullDataAsPreview(loadFull);

		}

		doZProjection(inputData, data);
	}

	public static void doItImageSeries(String input[]) throws IOException
	{
		File[] name = new File[1];
		NativeDataSet[] data = new NativeDataSet[1];

		ImageFileSelectorPanel inData = new ImageFileSelectorPanel();
		inData.setPreferredSize(new Dimension(600, 480));
		if (JOptionPane.showConfirmDialog(null, inData) == JOptionPane.OK_OPTION)
		{
			data[0] = new ImageSeriesDataSet(inData.getFiles());
			name[0] = inData.getFiles()[0];
			doZProjection(name, data);
		}
	}

	public static void doZProjection(File name[], NativeDataSet dat[])
	{

		StatusBarPanel status = new StatusBarPanel();
		JFrame frame = FrameFactroy.getFrame(status);
		DynamicRangeImage avgView = new DynamicRangeImage();
		DynamicRangeImage minView = new DynamicRangeImage();
		DynamicRangeImage maxView = new DynamicRangeImage();

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Avg", avgView);
		tab.addTab("Max", maxView);
		tab.addTab("Min", minView);

		JPanel pan = new JPanel(new BorderLayout());
		pan.add(tab, BorderLayout.CENTER);
		pan.add(status, BorderLayout.SOUTH);

		frame.setVisible(false);
		FrameFactroy.getFrame(pan);

		for (int i = 0; i < dat.length; i++)
		{
			NativeDataSet data = dat[i];
			String[] fileData = FileOperations.splitFile(name[i]);
			data.reloadData(status);
			int sizeX = data.getPreviewSizeX();
			int sizeY = data.getPreviewSizeY();
			int sizeZ = data.getPreviewSizeZ();

			float[][] max = new float[sizeX][sizeZ];
			float[][] min = new float[sizeX][sizeZ];
			float[][] avg = new float[sizeX][sizeZ];

			int kerX = 5;
			int kerY = 5;

			minView.setDataFloat(min);
			maxView.setDataFloat(max);
			avgView.setDataFloat(avg);
			byte val;
			float avgVal = 0;
			int avgCount = 0;
			status.setMinimum(0);
			status.setMaximum(sizeY);

			for (int y = 0; y < sizeY; y++)
			{
				status.setValue(y);
				// Load Slice Data

				for (int z = 0; z < sizeZ; z++)
				{
					for (int x = 0; x < sizeX; x++)
					{
						avgVal = 0;
						avgCount = 0;
						for (int xP = -kerX; xP < kerX; xP++)
						{
							for (int zP = -kerY; zP < kerY; zP++)
							{
								if ((x + xP) >= 0 && (x + xP) < sizeX
										&& (z + zP) >= 0 && (z + zP) < sizeZ)
								{
									val = data.getPreviewData()[x + xP][y][z
											+ zP];
									if (val < 0)
									{
										avgVal += val + 256;
									} else
									{
										avgVal += val;
									}
									avgCount++;

								}
							}
						}

						avgVal /= avgCount;

						avg[x][z] += avgVal;

						if (y == 0)
						{
							max[x][z] = avgVal;
							min[x][z] = avgVal;
						} else
						{
							if (avgVal > max[x][z])
							{
								max[x][z] = avgVal;
							}

							if (avgVal < min[x][z])
							{
								min[x][z] = avgVal;
							}
						}
					}
				}
			}

			try
			{
				saveData(avg, new File(fileData[0] + fileData[1] + "_" + kerX
						+ "_avg.raw"));
				saveData(min, new File(fileData[0] + fileData[1] + "_" + kerX
						+ "_min.raw"));
				saveData(max, new File(fileData[0] + fileData[1] + "_" + kerX
						+ "_max.raw"));
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
	}

	public static Dimension readDataSize(File f) throws IOException
	{
		DataInputStream out = new DataInputStream(new FileInputStream(f));
		return new Dimension(out.readInt(), out.readInt());

	}

	public static void readData(float[][] data, File f) throws IOException
	{

		DataInputStream out = new DataInputStream(new FileInputStream(f));

		if (data.length != out.readInt() || out.readInt() != data[0].length)
		{
			throw new InvalidParameterException("Wrong size array");
		}

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				data[x][y] = out.readFloat();
			}
		}
	}

	public static void saveData(float[][] data, File f) throws IOException
	{

		DataOutputStream out = new DataOutputStream(new FileOutputStream(f));

		out.writeInt(data.length);
		out.writeInt(data[0].length);

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				out.writeFloat(data[x][y]);
			}
		}
	}
}
