package com.joey.software.DataToolkit;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.Tools.FRG_Viewer;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.dsp.FFTProcessingTool;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageProcessing.HilbertProcessingTool;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.stringToolkit.StringOperations;


@Deprecated
public class ThorLabs2DFRGImageProducer extends ImageProducer
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

	public float[][] specData;

	public float[][] real;

	public float[][] imaginary;

	public float[][] mag;

	public float[][] phase;

	public float[][] doppler;

	// public float[][] hilReal;
	//
	// public float[][] hilImag;
	//
	// public float[][] hilMag;
	//
	// public float[][] hilPhase;

	File file;

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

	public ThorLabs2DFRGImageProducer(File f) throws IOException
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

	public static void main(String input[]) throws IOException
	{

		ThorLabs2DFRGImageProducer prod = null;

		File[] files = getMultipleFiles();

		ImagePanel pan = new ImagePanel();

		StatusBarPanel sfile = new StatusBarPanel();
		StatusBarPanel sImage = new StatusBarPanel();

		ImageFileSaver save = new ImageFileSaver(5, 10);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(pan, BorderLayout.CENTER);
		panel.add(sfile, BorderLayout.NORTH);
		panel.add(sImage, BorderLayout.SOUTH);
		FrameFactroy.getFrame(panel);
		sfile.setMaximum(files.length - 1);
		for (int i = 0; i < files.length; i++)
		{
			sfile.setStatusMessage("File [" + i + " of " + files.length);
			sfile.setValue(i);

			boolean first = true;
			if (prod == null)
			{
				prod = new ThorLabs2DFRGImageProducer(files[i]);
				prod.setUseDifferentheight(true);
				prod.setPreferedHeight(512);
				first = true;
			} else
			{
				prod.setFile(files[i]);
			}

			if (first)
			{
				prod.getUserInputs();
				first = false;
			}
			sImage.setMaximum(prod.getImageCount() - 1);
			for (int j = 0; j < prod.getImageCount(); j++)
			{
				File saveFile = new File(files[i].toString() + "["
						+ StringOperations.getNumberString(4, j) + "].png");
				sImage.setStatusMessage("Image [" + j + " of "
						+ prod.getImageCount());
				sImage.setValue(j);
				BufferedImage img = prod.getImage(j);
				//save.addData(saveFile, img);
				pan.setImage(img);
			}
		}

	}

	public void setFile(File f) throws IOException
	{
		setFile(f, 2048);
	}

	public void setFile(File f, int fftOutLength) throws IOException
	{
		this.file = f;

		byte[] data = new byte[36];
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		in.read(data, 0, 16);
		fileId = (byteToString(data, 16));

		in.read(data, 0, 4);
		sizeZ = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeX = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeY = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		totalFrames = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		num3D = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		fftSize = BinaryToolkit.readFlippedInt(data, 0);

		frmLenBytes = 40 + sizeX * fftSize * 2; // 40 bytes is the length of sub
		// hearder of the image frame
		frmPixels = sizeX * fftSize * 2;

//		System.out.println("Thor3DFRG : setFile() :");
//		System.out.println("Size X : " + sizeX);
//		System.out.println("Size Y : " + sizeY);
//		System.out.println("Size Z : " + sizeZ);
//		System.out.println("FFT Size : " + fftSize);
//		System.out.println("Frame pixels: " + frmPixels);

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
		specData = new float[sizeX][fftSize];
		real = new float[specData.length][size];
		imaginary = new float[specData.length][size];
		mag = new float[specData.length][size];
		phase = new float[specData.length][size];
		doppler = new float[specData.length][size];

		// hilReal = new float[specData.length][specData[0].length];
		// hilImag = new float[specData.length][specData[0].length];
		// hilMag = new float[specData.length][specData[0].length];
		// hilPhase = new float[specData.length][specData[0].length];

		preferedheight = fftOutLength;
	}

	public static void processSpecDataHilbert(float[][] specData, float[][] real, float[][] imaginary, float[][] mag, float[][] phase)
	{
		HilbertProcessingTool process = new HilbertProcessingTool(4);
		process.processData(specData, real, imaginary, mag, phase);
	}

	/**
	 * This will process the given spectrum data into the relevant compoents.
	 * 
	 * @param specData
	 * @param real
	 * @param imaginary
	 * @param mag
	 * @param phase
	 */

	public static void processSpecData(float[][] specData, float[][] real, float[][] imaginary, float[][] mag, float[][] phase)
	{
		FFTProcessingTool process = new FFTProcessingTool(2);
		process.processData(specData, real, imaginary, mag, phase);
		// for (int x = 0; x < specData.length; x++)
		// {
		// FastFourierTransform fft = new FastFourierTransform();
		//		
		// // Do fft
		// fft.fft(specData[x], real[x], imaginary[x], phase[x], mag[x]);
		// }
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
		System.out.print("Loading Spectrum : ");
		long start = System.currentTimeMillis();

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));

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
		System.out.println(System.currentTimeMillis() - start);
	}

	public void processSpecData()
	{

		System.out.print("Starting Processing Spectrum : ");
		long start = System.currentTimeMillis();
		processSpecData(specData, real, imaginary, mag, phase);

		float t = 1 / 1000.f;
		float val = (float) (1 / (2 * Math.PI * t));

		for (int i = 0; i < phase.length; i++)
		{
			for (int j = 0; j < phase[i].length - 1; j++)
			{
				float re = real[i][j] * real[i][j + 1] + imaginary[i][j]
						* imaginary[i][j + 1];
				float im = real[i][j + 1] * imaginary[i][j] + real[i][j]
						* imaginary[i][j + 1];
				doppler[i][j] = (float) (val * Math.atan2(re, im));

				// System.out.printf("Re[%3.3f] Im[%3.3f] = D[%3.3f]\n",
				// re,im,doppler[i][j]);
			}
		}
		System.out.println(System.currentTimeMillis() - start);
	}

	public void processSpecDataHilbert()
	{
		System.out.print("Starting Processing Hilbert Spectrum : ");
		long start = System.currentTimeMillis();
		processSpecDataHilbert(specData, real, imaginary, mag, phase);
		System.out.println(System.currentTimeMillis() - start);
	}

	public void getUserInputs() throws IOException
	{
		view.setFile(file);

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
		view.setImagePos(pos);
		view.getMagPanel().setMaxValue((float)range[1]);
		view.getMagPanel().setMinValue((float)range[0]);

		BufferedImage img = view.getMagPanel().getImage().getImage();
		BufferedImage rst;
		if (useDifferentHeight)
		{
			Rectangle r = new Rectangle(img.getWidth(), preferedheight);
			rst = ImageOperations.cropImage(img, r);
		} else
		{
			rst = img;
		}
		return rst;
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

		float[] range = DataAnalysisToolkit.getRangef(data);
		for (int y = 0; y < high; y++)
		{
			for (int x = 0; x < wide; x++)
			{
				int pos = x + y * wide;

				img.setRGB(x, y, getRGB(data[x][y], range[0], range[1]));
			}
		}
		return img;
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

}
