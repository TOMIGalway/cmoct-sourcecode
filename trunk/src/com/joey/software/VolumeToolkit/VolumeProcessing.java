package com.joey.software.VolumeToolkit;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;


public class VolumeProcessing
{
	public static void main(String[] input) throws IOException
	{
		byte min = 20;
		byte noise = 100;

		int sizeX = 100;
		int sizeY = 100;
		int sizeZ = 100;

		int size = 1;

		int zmin = 20;
		int zmax = 50;

		byte[][][] data = new byte[sizeX][sizeY][sizeZ];
		byte[][][] result = new byte[sizeX][sizeY][sizeZ];
		for (int x = 0; x < data[0].length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				for (int z = 0; z < data[0][0].length; z++)
				{
					if (z > zmin && z < zmax)
					{
						data[x][y][z] = (byte) 255;
					} else
					{
						data[x][y][z] = (byte) (min + Math.random() * noise);
					}
				}
			}
		}

		volumeSmooth(data, result, size, 1);

		VolumeViewerPanel orgView = new VolumeViewerPanel();
		VolumeViewerPanel rstView = new VolumeViewerPanel();

		JPanel orgPanel = new JPanel(new BorderLayout());
		JPanel rstPanel = new JPanel(new BorderLayout());

		orgPanel.setBorder(BorderFactory.createTitledBorder("Orignal Data"));
		rstPanel.setBorder(BorderFactory.createTitledBorder("Result Data"));

		orgPanel.add(orgView);
		rstPanel.add(rstView);

		orgView.getRender().setVolumeFile(new VolArrayFile(data, 1, 1, 1));
		rstView.getRender().setVolumeFile(new VolArrayFile(result, 1, 1, 1));

		FrameFactroy.getFrame(new JPanel[]
		{ orgPanel, rstPanel }, 2, 1);
	}

	/**
	 * This will smooth a 3D Volume. It will just get the average value of a
	 * kernal of side length 1+2*size
	 * 
	 * @param data
	 * @param result
	 * @param size
	 */
	public static void volumeSmooth(byte[][][] data, byte[][][] result, int size, double devThreshold)
	{
		/**
		 * data[x][y][z]
		 */
		byte[][][] kernal = new byte[1 + 2 * size][1 + 2 * size][1 + 2 * size];

		byte value = 0;
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				for (int z = 0; z < data[0][0].length; z++)
				{
					/*
					 * Fill kernal from orignal data set
					 */
					for (int kx = 0; kx < kernal.length; kx++)
					{
						for (int ky = 0; ky < kernal[0].length; ky++)
						{
							for (int kz = 0; kz < kernal[0].length; kz++)
							{
								try
								{
									kernal[kx][ky][kz] = data[x + kx - size][y
											+ ky - size][z + kz - size];
								} catch (Exception e)
								{
									kernal[kx][ky][kz] = 0;
								}
							}
						}
					}

					/*
					 * Determine Average Value
					 */
					double stat[] = getStatData(kernal);

					if (stat[1] > devThreshold)
					{
						result[x][y][z] = (byte) stat[0];
					} else
					{
						result[x][y][z] = 0;
					}

					/*
					 * Set output value
					 */

				}
			}
		}
	}

	public static double getAverage(byte[][][] kernal)
	{
		double result = 0;
		for (int x = 0; x < kernal.length; x++)
		{
			for (int y = 0; y < kernal[0].length; y++)
			{
				for (int z = 0; z < kernal[0][0].length; z++)
				{
					result += kernal[x][y][z];
				}
			}
		}

		return (result / (kernal.length * kernal[0].length * kernal[0][0].length));
	}

	/**
	 * This will return an array showing the average value and the deviation
	 * 
	 * double {average , deviation }
	 * 
	 * @param kernal
	 * @return
	 */
	public static double[] getStatData(byte kernal[][][])
	{

		double avg = getAverage(kernal);
		double varSqr = 0;

		for (int x = 0; x < kernal.length; x++)
		{
			for (int y = 0; y < kernal[0].length; y++)
			{
				for (int z = 0; z < kernal[0][0].length; z++)
				{
					varSqr += (kernal[x][y][z] - avg) * (kernal[x][y][z] - avg);
				}
			}
		}

		varSqr /= (kernal.length * kernal[0].length * kernal[0][0].length);

		return new double[]
		{ avg, Math.sqrt(varSqr) };
	}

}
