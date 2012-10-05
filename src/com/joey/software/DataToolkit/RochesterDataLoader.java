package com.joey.software.DataToolkit;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;


public class RochesterDataLoader extends ImageProducer
{
	File[] data;

	int wide = 500;

	int high = 1000;

	public RochesterDataLoader(File[] data)
	{
		this.data = data;
	}

	public static void main(String input[]) throws IOException
	{
		if (true)
		{
			rawToPNG();
			return;
		}
		File f = new File(
				"C:\\Users\\joey.enfield\\Desktop\\Spectrum data\\spect_Joey.bin");
		// FileSelectionField.getUserFile();
		// BinaryToolkit.binaryFileTool(f);
		RandomAccessFile in = new RandomAccessFile(f, "r");

		byte[] headByte = new byte[4];
		in.read(headByte);
		int headValue = BinaryToolkit.readInt(headByte, 0);
		System.out.println("Head Value : " + headValue);

		byte[] channelListByte = new byte[4];
		in.read(channelListByte);
		int channelListValue = BinaryToolkit.readInt(channelListByte, 0);
		System.out.println("Channel List Value : " + channelListValue);

		byte[] channelList = new byte[channelListValue];
		in.read(channelList);

		byte[] groupByte = new byte[4];
		in.read(groupByte);
		int groupLength = BinaryToolkit.readInt(groupByte, 0);
		System.out.println(groupLength);

		// byte[] dat = new byte[4];
		// in.read(dat);
		// System.out.println(BinaryToolkit.readInt(dat, 0));
		// System.out.println(in.readInt());
		// System.out.println(in.readInt());
		for (int i = 0; i < 10; i++)
		{
			System.out.println(in.readFloat());
		}
	}

	public void determineSize() throws IOException
	{

		BufferedReader in = new BufferedReader(new FileReader(data[0]));

		int totWide = 0;
		int totHigh = -10;

		BufferedImage img = ImageOperations.getBi(high, wide);
		String lineDat = in.readLine();
		while (lineDat != null)
		{
			totWide++;
			if (totHigh == -10)
			{
				totHigh = lineDat.split("\t").length;
			}
			lineDat = in.readLine();
		}

		high = totWide;
		wide = totHigh;
	}

	public void getUserInputs()
	{
		try
		{
			determineSize();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSpinner wideSpin = new JSpinner(new SpinnerNumberModel(wide, 0,
				100000, 1));
		JSpinner highSpin = new JSpinner(new SpinnerNumberModel(high, 0,
				100000, 1));

		JPanel widePan = new JPanel(new BorderLayout());
		widePan.add(new JLabel("High : "), BorderLayout.WEST);
		widePan.add(wideSpin, BorderLayout.CENTER);

		JPanel highPan = new JPanel(new BorderLayout());
		highPan.add(new JLabel("Wide : "), BorderLayout.WEST);
		highPan.add(highSpin, BorderLayout.CENTER);

		JPanel holder = new JPanel(new GridLayout(2, 1));
		holder.add(widePan);
		holder.add(highPan);

		JOptionPane.showInputDialog(null, holder);
		wide = (Integer) wideSpin.getValue();
		high = (Integer) highSpin.getValue();
	}

	public static void rawToPNG() throws IOException
	{
		JFileChooser chooser = new JFileChooser();

		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(null);

		File[] files = chooser.getSelectedFiles();

		StatusBarPanel stat = new StatusBarPanel();
		stat.setMaximum(files.length);

		FrameFactroy.getFrame(stat).setSize(300, 100);

		int wide = 500;
		int high = 500;

		long start;
		long diff = 0;

		BufferedImage img = ImageOperations.getBi(high, wide);

		for (int k = 0; k < files.length; k++)
		{
			start = System.currentTimeMillis();
			stat.setValue(k);
			BufferedReader in = new BufferedReader(new FileReader(files[k]));

			for (int i = 0; i < high; i++)
			{
				String lineDat = in.readLine();
				String[] dat = lineDat.split("\t");
				for (int j = 0; j < dat.length; j++)
				{
					int val = Integer.parseInt(dat[j]);
					Color c = new Color(val, val, val);
					img.setRGB(i, dat.length - j - 1, c.getRGB());
				}
			}

			// ImageIO.write(img, "PNG", new File(files[k].toString() +
			// ".png"));
			FrameFactroy.getFrame(img);
			diff = (System.currentTimeMillis() - start) / 1000;
			stat.setStatusMessage("Last Time [" + diff + " ms] Remain : "
					+ ((files.length - k) * diff) + " s");
		}
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(data[pos]));

		BufferedImage img = ImageOperations.getBi(high, wide);
		for (int i = 0; i < high; i++)
		{
			String lineDat = in.readLine();
			String[] dat = lineDat.split("\t");
			for (int j = 0; j < dat.length; j++)
			{
				int val = Integer.parseInt(dat[j]);
				Color c = new Color(val, val, val);
				img.setRGB(i, dat.length - j - 1, c.getRGB());
			}
		}

		return img;
	}

	@Override
	public int getImageCount()
	{
		// TODO Auto-generated method stub
		return data.length;
	}
}