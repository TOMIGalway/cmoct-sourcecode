package com.joey.software.examples;

import ij.ImagePlus;
import ij.ImageStack;
import ij3d.Content;
import ij3d.Image3DUniverse;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.Presentation.ReactiveHyperimeaTool;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;

import voltex.VoltexGroup;
import voltex.VoltexVolume;

public class DisplayVolume
{

	public static ImagePlus getVolumeHolder(int sizeX, int sizeY, int sizeZ)
	{

		ImageStack stack = new ImageStack(sizeX, sizeY, sizeZ);

		for (int i = 0; i < sizeZ; i++)
		{
			byte[] hold = new byte[sizeX * sizeY];
			ArrayToolkit.fillRandom(hold);
			stack.setPixels(hold, i + 1);
		}
		ImagePlus rst = new ImagePlus();
		rst.setStack(stack);
		return rst;
	}

	public static void fillRandom(ImagePlus plus)
	{
		ImageStack stack = plus.getStack();

		int size = stack.getSize();
		int pos = (int) (Math.random() * (size - 10));
		for (int i = 0; i < size; i++)
		{
			byte[] data = (byte[]) stack.getPixels(i + 1);
			if (i > pos && i < pos + 10)
			{
				ArrayToolkit.fillRandom(data);
			} else
			{
				ArrayToolkit.setValue(data, (byte) 0);
			}
		}
	}

	public static void fillRandom(VoltexVolume vol)
	{

		for (int z = 0; z < vol.zDim; z++)
		{
			if (Math.random() < 0.3)
				for (int x = 0; x < vol.xDim; x++)
				{

					for (int y = 0; y < vol.yDim; y++)
					{
						vol.setNoCheckNoUpdate(x, y, z, (byte) (255 * Math
								.random()));
					}
				}
		}
		vol.updateData();
	}

	public static void setVolume(VoltexVolume vol, byte[][][] data)
	{
		for (int z = 0; z < vol.zDim; z++)
		{
			for (int x = 0; x < data.length; x++)
			{

				for (int y = 0; y < vol.yDim; y++)
				{
					vol.setNoCheckNoUpdate(x, y, z, data[x][y][z]);
				}
			}
		}
		vol.updateData();
	}

	public static void setData(ImagePlus plus, byte[][][] data)
	{
		ImageStack stack = plus.getStack();

		int size = stack.getSize();

		for (int z = 0; z < size; z++)
		{
			byte[] frm = (byte[]) stack.getPixels(z + 1);

			for (int x = 0; x < stack.getWidth(); x++)
			{
				for (int y = 0; y < stack.getHeight(); y++)
				{
					frm[x * stack.getHeight() + y] = data[x][y][z];
				}
			}

		}
	}

	public static void main(String[] args) throws IOException
	{
		Time();
	}

	public static Vector<File[]> getUserFiles()
	{
		final Vector<File[]> dataHolder = new Vector<File[]>();

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

		return dataHolder;
	}

	public static void Time() throws IOException
	{
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = true;

		Vector<File[]> dataHolder = getUserFiles();

		// Create a universe and show it
		Image3DUniverse univ = new Image3DUniverse();
		// univ.getWindow().setTitle("Time Lapse");
		univ.show();

		byte[][][] data = null;

		Content conA = null;
		Content conB = null;

		for (int i = 0; i < dataHolder.size(); i++)
		{
			File file = dataHolder.get(i)[0];

			ThorlabsIMGImageProducer imgLoader = new ThorlabsIMGImageProducer(
					file, true);
			ImageFileProducer imageLoader = new ImageFileProducer(
					ReactiveHyperimeaTool
							.getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned));

			if (i == 0)
			{
				data = imageLoader.createDataHolder();
				imageLoader.getData(data, null);

				ImagePlus rawA = getVolumeHolder(data.length, data[0].length, data[0][0].length);
				rawA.setTitle("Data A");
				conA = univ.addVoltex(rawA);

				ImagePlus rawB = getVolumeHolder(data.length, data[0].length, data[0][0].length);
				rawA.setTitle("Data B");
				conB = univ.addVoltex(rawB);
				conB.setVisible(false);

				FrameWaitForClose.showWaitFrame();
			}

			imageLoader.getData(data, null);

			if (i % 2 == 0)
			{

				VoltexGroup vg = (VoltexGroup) conB.getContent();

				VoltexVolume vol = vg.getRenderer().getVolume();

				setVolume(vol, data);

				conB.setVisible(true);
				conA.setVisible(false);
			} else
			{

				VoltexGroup vg = (VoltexGroup) conA.getContent();

				VoltexVolume vol = vg.getRenderer().getVolume();
				setVolume(vol, data);

				conA.setVisible(true);
				conB.setVisible(false);
			}
		}
	}

	private static void sleep(int sec)
	{
		try
		{
			Thread.sleep(sec);
		} catch (InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
