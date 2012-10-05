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
package com.joey.software.DataToolkit;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.Tools.FRG_Viewer;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.stringToolkit.StringOperations;


public class ThorLabsFRGImageProducer extends ImageProducer
{
	public static int WINDOW_GAUSSIAN = 0;

	public static int WINDOW_SUPER_GAUSSIAN = 1;

	public static int FFT_FULLDATA = -1;

	public static int FFT_REALDATA = -2;

	FRG_Viewer view = new FRG_Viewer();

	String fileId;

	int totalFrames;

	int sizeZ;

	int sizeX;

	int sizeY;

	int num3D;

	int frmLenBytes;

	int frmPixels;

	int fftSize;

	byte[] pxlHolder = new byte[0];

	public float[][] specData= new float[1][1];

	public float[][] mag= new float[1][1];

	public float[][] phase= new float[1][1];

	File file[];

	int frameNum[];

	public double[] range = new double[2];

	int preferedheight = 1;

	/**
	 * This sets that a reduced section should be used
	 */
	boolean useDifferentHeight = false;

	boolean useWindowing = false;

	int windowMethod = WINDOW_SUPER_GAUSSIAN;

	public boolean isUseWindowing()
	{
		return useWindowing;
	}

	public void setUseWindowing(boolean useWindowing)
	{
		this.useWindowing = useWindowing;
	}

	public void setWindowMethod(int windowMethod)
	{
		this.windowMethod = windowMethod;
	}

	public ThorLabsFRGImageProducer(File f) throws IOException
	{
		setFile(f);
	}

	public static String byteToString(byte[] data, int count)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < count; i++)
		{
			rst.append((char) data[i]);
		}
		return rst.toString();
	}

	public static File[] getMultipleFiles()
	{
		Vector<File> data = new Vector<File>();

		File f = null;
		do
		{
			FileSelectionField fie = new FileSelectionField();
			if (fie.getUserChoice())
			{
				f = fie.getFile();
			} else
			{
				f = null;
			}
			if (f != null)
			{
				data.add(f);
			}
		} while (f != null);

		return data.toArray(new File[0]);
	}

	public void setFile(File f) throws IOException
	{
		setFile(f, 2048);
	}

	/**
	 * This function will get the series of Files that are present in the file
	 * 
	 * @param f
	 * @return
	 */
	public static File[] getFRGFiles(File f)
	{
		Vector<File> files = new Vector<File>();
		int count = 0;
		String[] data = FileOperations.splitFile(f);

		boolean stillFound = true;
		for (int i = 0; i < 999 && stillFound; i++)
		{
			String fileBase = data[1].substring(0, data[1].length() - 3);
			fileBase = fileBase + StringOperations.getNumberString(3, i);

			File file = new File(data[0] + fileBase + "." + data[2]);
			if (file.exists())
			{
				files.add(file);
			} else
			{
				stillFound = false;
				return files.toArray(new File[0]);
			}

		}
		return null;
	}

	public void setFile(File fileIn, int fftOutLength) throws IOException
	{
		this.file = getFRGFiles(fileIn);
		this.frameNum = new int[file.length];

		byte[] data = new byte[36];

		for (int i = 0;i  < file.length; i++)
		{
			File f = file[i];
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(f)));
			in.read(data, 0, 16);
			fileId = (byteToString(data, 16));

			in.read(data, 0, 4);
			frameNum[i] = BinaryToolkit.readFlippedInt(data, 0);

			in.read(data, 0, 4);
			sizeX = BinaryToolkit.readFlippedInt(data, 0);

			in.read(data, 0, 4);
			sizeY = BinaryToolkit.readFlippedInt(data, 0);

			in.read(data, 0, 4);
			sizeZ = BinaryToolkit.readFlippedInt(data, 0);
			totalFrames = sizeZ;
			in.read(data, 0, 4);
			num3D = BinaryToolkit.readFlippedInt(data, 0);

			in.read(data, 0, 4);
			fftSize = BinaryToolkit.readFlippedInt(data, 0);
		}

		frmLenBytes = 40 + sizeX * fftSize * 2; // 40 bytes is the length of sub
		// hearder of the image frame
		frmPixels = sizeX * fftSize * 2;

		int size = 0;
		if (fftOutLength == FFT_FULLDATA)
		{
			size = fftSize;
		} else if (fftOutLength == FFT_REALDATA)
		{
			size = sizeZ;
		} else
		{
			size = fftOutLength;
		}
		specData = new float[sizeX][fftSize * 2];
		mag= new float[sizeX][fftSize * 2];
		phase= new float[sizeX][fftSize * 2];
		preferedheight = fftOutLength;
	}

	/**
	 * This will load the spectrum data from the file into the global variable
	 * specData.
	 * 
	 * @param pos
	 * @throws IOException
	 */
	public void loadSpecData(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file[0])));

		in.skip(getFileOffset(pos));

		if (pxlHolder.length != frmPixels)
		{
			pxlHolder = new byte[frmPixels];
		}

		in.skip(40);
		// Frame Info
		in.read(pxlHolder);

		reshape(pxlHolder, specData);

		// This is fixing some problem with the input data
		specData[0][238] = specData[0][239];
		specData[0][2] = specData[0][3];
		specData[0][4] = specData[0][5];
		specData[0][0] = specData[0][1];

		if (useWindowing)
		{
			float window_band = 0.3f * specData[0].length;

			for (int i = 0; i < specData.length; i++)
			{
				if (windowMethod == WINDOW_GAUSSIAN)
				{
					float val = ((i - specData.length / 2) / window_band);
					float window = (float) Math.exp(-val * val);
					for (int j = 0; j < specData[i].length; j++)
					{
						specData[i][j] *= window;
					}
				} else if (windowMethod == WINDOW_SUPER_GAUSSIAN)
				{
					float val = ((i - specData.length / 2) / window_band);
					float window = (float) Math.exp(-val * val * val * val);
					for (int j = 0; j < specData[i].length; j++)
					{
						specData[i][j] *= window;
					}
				}
			}
		}
	}

	public void processSpecData()
	{
	
	}

	public void getUserInputs() throws IOException
	{
		view.setFile(file[0]);

		JFrame f = new JFrame("Settings");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(view);

		FrameWaitForClose close = new FrameWaitForClose(f);
		f.setSize(600, 480);
		f.setVisible(true);
		close.waitForClose();

		JSpinner pixels = new JSpinner(new SpinnerNumberModel(getHeight(), 1,
				fftSize, 1));
		JOptionPane
				.showMessageDialog(null, pixels, "Select Image Pixels", JOptionPane.PLAIN_MESSAGE);

		int newValue = (Integer) pixels.getValue();

		if (newValue != getHeight())
		{
			setUseDifferentheight(true);
			setPreferedHeight(newValue);
		}

		range[1] = view.getMagPanel().getMaxValue();
		range[0] = view.getMagPanel().getMinValue();

		System.out.println(range[1]);
		System.out.println(range[0]);

		return;
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		BufferedImage img = ImageOperations.getBi(getWidth(), getHeight());
		getImage(pos, img);
		return img;
	}

	public void getImage(int pos, BufferedImage img)
	{

	}

	private int getFileOffset(int pos)
	{
		return 512+(pos) * frmLenBytes;
	}

	@Override
	public int getImageCount()
	{
		// TODO Auto-generated method stub
		return sizeZ;
	}

	public int getWidth()
	{
		return sizeX;
	}

	/**
	 * This allows you to reduce the size of the outputted data must also turn
	 * on
	 * 
	 * @param value
	 */
	public void setPreferedHeight(int value)
	{
		this.preferedheight = value;
	}

	public void setUseDifferentheight(boolean use)
	{
		this.useDifferentHeight = use;
	}

	public void setRange(float min, float max)
	{
		range[0] = min;
		range[1] = max;
	}

	public int getHeight()
	{
		if (useDifferentHeight == true)
		{
			return preferedheight;
		}
		return sizeY;
	}

	public static void reshape(byte[] data, float[][] out)
	{

		int high = out.length;
		int wide = out[0].length;

		for (int y = 0; y < high; y++)
		{
			for (int x = 0; x < wide; x++)
			{
				int pos = x * 2 + y * wide * 2;

				short val = BinaryToolkit.readFlippedShort(data, pos);
				out[y][x] = val;
			}
		}

	}

	public static BufferedImage getImage(float[][] data)
	{
		int wide = data.length;
		int high = data[0].length;
		BufferedImage img = ImageOperations.getBi(wide, high);

		getImage(data, img);
		return img;
	}

	public static void getImage(float[][] data, BufferedImage img)
	{
		int wide = data.length;
		int high = data[0].length;

		float[] range = DataAnalysisToolkit.getRangef(data);
		for (int y = 0; y < high; y++)
		{
			for (int x = 0; x < wide; x++)
			{
				int pos = x + y * wide;

				img.setRGB(x, y, getRGB(data[x][y], range[0], range[1]));
			}
		}

	}

	public static int getRGB(float value, float min, float max)
	{

		float pos = ((value) - min) / (max - min);
		if (pos > 1)
		{
			// System.out.println("Value : " + value);
			return 1;
		} else if (pos < 0)
		{
			return 0;
		}
		Color c = new Color(pos, pos, pos);
		return c.getRGB();

	}

	public byte[][][] getSuitableByteHolder()
	{
		return new byte[getImageCount()][getWidth()][getHeight()];
	}

	/**
	 * This will load a FRG volume into a byte array
	 * 
	 * @param f
	 * @param data
	 *            [data.imgcount][data.wide][data.high]
	 * @throws IOException
	 */
	public static byte[][][] loadFRGVolume(ThorLabsFRGImageProducer data, byte[][][] output)
			throws IOException
	{
		data.getUserInputs();
		if (output == null)
		{
			output = data.getSuitableByteHolder();
			System.out.println(output.length);
			System.out.println(output[0].length);
			System.out.println(output[0][0].length);

		}
		return output;
	}

	public static void main(String input[]) throws IOException
	{
		File f = FileSelectionField.getUserFile();

		ThorLabsFRGImageProducer data = new ThorLabsFRGImageProducer(f);
		byte[][][] output = loadFRGVolume(data, null);

	}
}
