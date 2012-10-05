package com.joey.software.Projections.surface;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;
import com.joey.software.toolkit.VolumeInputSelectorPanel;


public class VolumeProcessing
{
	public static final byte TYPE_PEAK_CLEAR = 0;

	public static final byte TYPE_PEAK_DATA = 127;

	public static final byte TYPE_PEAK_MARKED = (byte) 255;

	public static void main(String input[]) throws IOException
	{

		byte[][][] data = VolumeInputSelectorPanel.getUserVolumeData(null);
		byte[][][] result = new byte[data.length][data[0].length][data[0][0].length];

		OCTAnalysis analysis = new OCTAnalysis();
		FrameFactroy.getFrame(analysis);
		analysis.setData(data);
		analysis.setData(result);
		
		blurVolume(data, result, 3, 3, 3, null);

	}

	public static void grow(byte[][][] peaks, int px, int py, int pz, int size)
	{
		PositionHolder data = new PositionHolder();
		for (int x = px - size; x <= px + size; x++)
		{
			for (int y = py - size; y <= py + size; y++)
			{
				for (int z = pz - size; z <= pz + size; z++)
				{
					if (x >= 0 && y >= 0 && z >= 0 && x < peaks.length
							&& y < peaks[0].length && z < peaks[0][0].length)
					{
						data.push(x, y, z);
					}
				}
			}
		}
		while (data.hasDataLeft())
		{
			grow(peaks, data);
		}

	}

	/**
	 * This assumes that the data is in the form
	 * data[z][x][y]
	 * 
	 * @param in
	 * @param out
	 * @param kerX
	 * @param kerY
	 * @param kerZ
	 * @param status
	 */
	public static void blurVolume(byte[][][] in, byte[][][] out, int kerX, int kerY, int kerZ, StatusBarPanel status)
	{
		
		float data = 0;
		int count = 0;

		
		if(status != null)
		{
			status.setMaximum(in.length-1);
		}
		for (int xp = 0; xp < in[0].length; xp++)
		{
			if(status != null)
			{
				status.setValue(xp);
			}
			for (int yp = 0; yp < in[0][0].length; yp++)
			{
				for (int zp = 0; zp < in.length; zp++)
				{

					// Grab the data
					data = 0;
					count = 0;
					for (int x = xp - kerX; x <= xp + kerX; x++)
					{
						if (x >= 0 && x < in[0].length)
						{
							for (int y = yp - kerY; y <= yp + kerY; y++)
							{
								if (y >= 0 && y < in[0][0].length)
								{
									for (int z = zp - kerZ; z <= zp + kerZ; z++)
									{
										if (z >= 0 && z < in.length)
										{
											data += in[z][x][y];
											count++;
										}
									}
								}
							}
						}
					}

					
					if(count == 0)
					{
						count= 0;
					}
					data/= count;
					out[zp][xp][yp] = (byte)data;
				}
			}

		}

	}

	private static void clearBadData(byte[][][] data)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				for (int z = 0; z < data[x][y].length; z++)
				{
					if (data[x][y][z] != TYPE_PEAK_MARKED)
					{
						data[x][y][z] = TYPE_PEAK_CLEAR;
					}
				}
			}
		}
	}

	private static void grow(byte[][][] peaks, PositionHolder data)
	{
		if (!data.hasDataLeft())
		{
			return;
		}
		int[] val = data.pop();
		int px = val[0];
		int py = val[1];
		int pz = val[2];

		if (peaks[px][py][pz] == TYPE_PEAK_DATA)
		{
			peaks[px][py][pz] = TYPE_PEAK_MARKED;
		}
		data.remove(px, py, pz);

		for (int x = px - 1; x <= px + 1; x++)
		{
			for (int y = py - 1; y <= py + 1; y++)
			{
				for (int z = pz - 1; z <= pz + 1; z++)
				{
					if (!(x == px && y == py && z == pz))
					{
						if (x >= 0 && y >= 0 && z >= 0 && x < peaks.length
								&& y < peaks[0].length
								&& z < peaks[0][0].length)
						{
							if (peaks[x][y][z] == TYPE_PEAK_DATA)
							{
								data.push(x, y, z);
							}
						}
					}
				}
			}
		}
	}

	public static void findSurfaceFromUserSelection(final byte[][][] struct, final byte[][][] peaks)
	{
		final JSpinner slice = new JSpinner();

		final BufferedImage img = ImageOperations.getImage(struct[0]);

		final ROIPanel panel = new ROIPanel(false);
		panel.setImage(img);

		PolygonControler pol = new PolygonControler(panel);
		pol.setMaxPoints(1);

		panel.setControler(pol);

		FrameWaitForClose wait = new FrameWaitForClose(
				FrameFactroy.getFrame(slice, panel.getInPanel()));

		slice.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				int sliceVal = (Integer) slice.getValue();
				drawOverlay(struct[sliceVal], peaks[sliceVal], img);
				panel.repaint();
			}
		});

		slice.getChangeListeners()[0].stateChanged(null);

		wait.waitForClose();

		int px = (int) pol.points.get(0).x;
		int py = (int) pol.points.get(0).y;
		int pz = (Integer) slice.getValue();

		grow(peaks, pz, px, py, 1);
		// clearBadData(peaks);
	}

	public static BufferedImage drawOverlay(byte[][] data, byte[][] peaks)
	{
		BufferedImage img = ImageOperations.getBi(data.length, data[0].length);
		drawOverlay(data, peaks, img);
		return img;
	}

	public static void drawOverlay(byte[][] data, byte[][] peaks, BufferedImage img)
	{
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{

				if (peaks[x][y] != 0)
				{
					img.setRGB(x, y, Color.RED.getRGB());
				} else
				{
					img.setRGB(x, y, ImageOperations
							.getGrayRGB(b2i(data[x][y])));
				}
			}
		}
	}

	public static int b2i(byte val)
	{
		if (val >= 0)
		{
			return val;
		} else
		{
			return (val) + 256;
		}
	}
}

class PositionHolder
{
	Vector<Long> data = new Vector<Long>();

	Vector<Long> toBeRemoved = new Vector<Long>();

	public void push(int x, int y, int z)
	{
		long hash = getHash((short) x, (short) y, (short) z);
		if (!data.contains(hash))
		{
			data.add(hash);
		}
	}

	public void pop(int hold[])
	{
		long hash = data.remove(0);
		hold[0] = getX(hash);
		hold[1] = getY(hash);
		hold[2] = getZ(hash);
	}

	public int[] pop()
	{
		int[] hold = new int[3];
		pop(hold);
		return hold;
	}

	public void peek(int[] hold)
	{
		long hash = data.get(0);
		hold[0] = getX(hash);
		hold[1] = getY(hash);
		hold[2] = getZ(hash);
	}

	public int[] peek()
	{
		int[] hold = new int[3];
		peek(hold);
		return hold;
	}

	public void remove(int x, int y, int z)
	{
		long hash = getHash((short) x, (short) y, (short) z);
		if (data.contains(hash))
		{
			data.remove(hash);
		}
	}

	public boolean hasDataLeft()
	{
		return data.size() > 0;
	}

	public static long getHash(short x, short y, short z)
	{
		long value = x;
		value = value << 16 | y;
		value = value << 16 | z;
		return value;
	}

	public static short getZ(long hash)
	{
		return (short) (hash);
	}

	public static short getY(long hash)
	{
		return (short) ((hash >> 16) & 0xffff);
	}

	public static short getX(long hash)
	{
		return (short) ((hash >> 32) & 0xffff);
	}

}
