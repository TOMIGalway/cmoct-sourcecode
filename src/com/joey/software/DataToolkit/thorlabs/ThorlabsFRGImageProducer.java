package com.joey.software.DataToolkit.thorlabs;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import com.joey.software.Tools.AScanViewerTool;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.colorMapping.ColorMap;


public class ThorlabsFRGImageProducer extends ThorlabsImageProducer
{
	public static ColorMap GRAY = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	public static int WINDOW_GAUSSIAN = 0;

	public static int WINDOW_SUPER_GAUSSIAN = 1;

	public static int FFT_FULLDATA = -1;

	public static int FFT_REALDATA = -2;

	public static int FFT_REALDATA_HALF = -3;

	int fftType = FFT_REALDATA_HALF;

	int fftSize = 2048;

	byte[] pxlHolder = new byte[0];

	public float[][] specData;

	float dataMax = 0;

	float dataMin = 0;

	float displayMax = 0;

	float displayMin = 0;

	boolean logData = true;

	public float[][] magnitude;

	public double[] range = new double[2];

	boolean useWindowing = false;

	int windowMethod = WINDOW_SUPER_GAUSSIAN;

	public static void main(String input[]) throws IOException
	{
		File f = FileSelectionField.getUserFile();

		DynamicRangeImage img = new DynamicRangeImage();
		FrameFactroy.getFrame(img);
		
		ThorlabsFRGImageProducer data = new ThorlabsFRGImageProducer(f);
		
		
		data.loadSpecData(0);
		img.setDataFloat(data.specData);
		FrameWaitForClose.showWaitFrame();
	}

	public ThorlabsFRGImageProducer(File f, boolean single) throws IOException
	{
		setFile(f, single);
	}

	public ThorlabsFRGImageProducer(File f) throws IOException
	{
		setFile(f);
	}

	public void resetMaxMinDisplay()
	{
		displayMax = dataMax;
		displayMin = dataMin;
	}

	public void setSettings(boolean logData, float min, float max)
	{
		this.logData = logData;
		this.displayMin = min;
		this.displayMax = max;

		if (logData)
		{
			displayMin = (float) Math.exp(displayMin);
			displayMax = (float) Math.exp(displayMax);
		}
	}

	public void getUserInputs() throws IOException
	{
		System.out.println("Here");
		AScanViewerTool tool = new AScanViewerTool();
		tool.setSize(1024, 600);
		tool.setFile(getCorrectFile(0));
		tool.setVisible(true);
		FrameWaitForClose close = new FrameWaitForClose((tool));
		close.waitForClose();

		logData = tool.getLogScaleing();
		displayMin = tool.getDisplayMinValue();
		displayMax = tool.getDisplayMaxValue();

		if (logData)
		{
			displayMin = (float) Math.exp(displayMin);
			displayMax = (float) Math.exp(displayMax);
		}
	}

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

	public void setFile(File f, int fftOutLength) throws IOException
	{
		if (fftOutLength != FFT_FULLDATA || fftOutLength != FFT_REALDATA)
		{
			throw new InvalidParameterException(
					"FFT must be FFT_FULLDATA or FFT_REALDATA");
		}
		this.fftSize = fftOutLength;
		setFile(f);
	}

	public void setDisplayMax(float value)
	{
		displayMax = value;
	}

	public void setDisplayMin(float value)
	{
		displayMin = value;
	}

	@Override
	public void allocateMemory()
	{
		frmLenBytes = 40 + sizeX * fftSize * 2; // 40
												// bytes
												// is
												// the
												// length
												// of
												// sub
		// hearder of the image frame
		frmPixels = sizeX * fftSize * 2;

		int size = 0;
		if (fftType == FFT_FULLDATA)
		{
			size = fftSize;
		} else if (fftType == FFT_REALDATA)
		{
			size = fftSize / 2;
		} else if (fftType == FFT_REALDATA_HALF)
		{
			size = fftSize / 4;
		}

		specData = new float[sizeX][fftSize * 2];
		magnitude = new float[specData.length][size];
	}

	/**
	 * This function will return only the real
	 * part of the spectrogram
	 * 
	 * @return
	 */
	public void getSpectrogramReal(float[][] data)
	{
		for (int x = 0; x < specData.length; x++)
		{
			for (int y = 0; y < specData[0].length / 2; y++)
			{
				data[x][y] = specData[x][2 * y];
			}
		}
	}

	/**
	 * This function will return only the real
	 * part of the spectral Data
	 * 
	 * @return
	 */
	public float[][] getSpectrogramReal()
	{
		float[][] holder = new float[specData.length][specData[0].length / 2];
		getSpectrogramReal(holder);
		return holder;
	}

	/**
	 * This function will return only the
	 * imaginary part of the spectral Data
	 * 
	 * @return
	 */
	public void getSpectrogramImag(float[][] data)
	{
		for (int x = 0; x < specData.length; x++)
		{
			for (int y = 0; y < specData[0].length / 2; y++)
			{
				data[x][y] = specData[x][2 * y + 1];
			}
		}
	}

	/**
	 * This function will return only the
	 * imaginary part of the spectral Data
	 * 
	 * @return
	 */
	public float[][] getSpectrogramImag()
	{
		float[][] holder = new float[specData.length][specData[0].length / 2];
		getSpectrogramImag(holder);
		return holder;
	}

	/**
	 * This function will return only the
	 * magnitude of the spectral Data
	 * 
	 * @return
	 */
	public void getSpectrogramPhase(float[][] data)
	{
		for (int x = 0; x < specData.length; x++)
		{
			for (int y = 0; y < specData[0].length / 2; y++)
			{
				data[x][y] = (float) Math
						.atan2(specData[x][2 * y + 1], specData[x][2 * y]);
			}
		}
	}

	/**
	 * This function will return only the
	 * magnitude of the spectral Data
	 * 
	 * @return
	 */
	public float[][] getSpectrogramPhase()
	{
		float[][] holder = new float[specData.length][specData[0].length / 2];
		getSpectrogramPhase(holder);
		return holder;
	}

	/**
	 * This function will return only the
	 * magnitude of the spectral Data
	 * 
	 * @return
	 */
	public void getSpectrogramMag(float[][] data)
	{
		for (int x = 0; x < specData.length; x++)
		{
			for (int y = 0; y < specData[0].length / 2; y++)
			{
				data[x][y] = (float) Math.sqrt(specData[x][2 * y + 1]
						* specData[x][2 * y + 1] + specData[x][2 * y]
						* specData[x][2 * y]);
			}
		}
	}

	public void getDBMagnitude(float[][] data)
	{
		for (int x = 0; x < specData.length; x++)
		{
			for (int y = 0; y < specData[0].length / 2; y++)
			{
				data[x][y] = (float) (20 * Math.log10(Math
						.sqrt(specData[x][2 * y + 1] * specData[x][2 * y + 1]
								+ specData[x][2 * y] * specData[x][2 * y])));
			}
		}
	}

	public float[][] getSpectrogramHolder()
	{
		return new float[specData.length][specData[0].length / 2];
	}

	public float[][] getMagnitudeHolder()
	{
		return new float[magnitude.length][magnitude[0].length];
	}

	/**
	 * This function will return only the
	 * magnitude of the spectral Data
	 * 
	 * @return
	 */
	public float[][] getSpectrogramMag()
	{
		float[][] holder = getSpectrogramHolder();
		getSpectrogramMag(holder);
		return holder;
	}

	public void processSpecData()
	{
		FloatFFT_1D fft = new FloatFFT_1D(specData[0].length / 2);
		for (int pos = 0; pos < specData.length; pos++)
		{

			fft.complexInverse(specData[pos], false);
			for (int i = 0; i < magnitude[pos].length; i++)
			{
				magnitude[pos][i] = (float) Math.sqrt(specData[pos][2 * i]
						* specData[pos][2 * i] + specData[pos][2 * i + 1]
						* specData[pos][2 * i + 1]);

				if (magnitude[pos][i] > dataMax)
				{
					dataMax = magnitude[pos][i];
				}

				if (magnitude[pos][i] < dataMin)
				{
					dataMin = magnitude[pos][i];
				}
			}
		}
	}

	/**
	 * This will load the spectrum data from the
	 * file into the global variable specData.
	 * 
	 * @param pos
	 * @throws IOException
	 */
	public void loadSpecData(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(getCorrectFile(pos))));

		in.skip(getFileOffset(pos));

		if (pxlHolder.length != frmPixels)
		{
			pxlHolder = new byte[frmPixels];
		}

		in.skip(40);
		// Frame Info
		in.read(pxlHolder);

		reshape(pxlHolder, specData);

		// This is fixing some problem with the
		// input data
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

	public void setRange(float min, float max)
	{
		displayMin = min;
		displayMax = max;
	}

	public static void reshape(byte[] data, float[][] out)
	{

		int high = out.length;
		int wide = out[0].length / 2;

		for (int y = 0; y < high; y++)
		{
			for (int x = 0; x < wide; x++)
			{
				int pos = 2 * x + y * wide * 2;

				short val = BinaryToolkit.readFlippedShort(data, pos);
				out[y][2 * x] = val;
				out[y][2 * x + 1] = 0;
			}
		}

	}

	public static void getImage(float[][] data, float max, float min, boolean logData, BufferedImage img)
	{
		int wide = data.length;
		int high = data[0].length;

		for (int y = 0; y < high; y++)
		{
			for (int x = 0; x < wide; x++)
			{
				img.setRGB(x, y, getRGB(data[x][y], min, max, logData));
			}
		}
	}

	public static BufferedImage getImage(float[][] data, float max, float min, boolean logData)
	{
		BufferedImage img = ImageOperations.getBi(data.length, data[0].length);
		getImage(data, max, min, logData, img);
		return img;
	}

	public static int getRGB(float value, float min, float max, boolean logData)
	{
		return getRGB(value, min, max, logData, null);
	}

	public static int getRGB(float value, float min, float max, boolean logData, ColorMap map)
	{
		float pos = 0;

		if (logData)
		{
			pos = (float) (((Math.log(value)) - Math.log(min + 0.001)) / (Math

			.log(max + 0.001) - Math.log(min + 0.001)));
		} else
		{
			pos = ((((value)) - (min)) / ((max) - (min)));
		}
		if (pos > 1)
		{
			pos = 1;
		} else if (pos < 0)
		{
			pos = 0;
		}

		if (map == null)
		{
			return GRAY.getColor(pos).getRGB();
		} else
		{
			return map.getColor(pos).getRGB();
		}
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		loadSpecData(pos);
		processSpecData();
		return getImage(magnitude, displayMax, displayMin, logData);
	}

	@Override
	public void getImage(int pos, byte[][] data) throws IOException
	{
		loadSpecData(pos);
		processSpecData();

		for (int x = 0; x < getSizeX(); x++)
		{
			for (int y = 0; y < getSizeY(); y++)
			{
				data[x][y] = (byte) getRGB(magnitude[x][y], displayMax, displayMin, logData);
			}
		}
	}

	@Override
	public void getImage(int pos, BufferedImage img) throws IOException
	{
		loadSpecData(pos);
		processSpecData();
		getImage(magnitude, displayMax, displayMin, logData, img);
	}

	@Override
	public void getData(byte[][][] data, StatusBarPanel status)
			throws IOException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * This function will load a given imagedata
	 * set into the current byte[] at the given
	 * position The data is stored in the form
	 * data[x* wide*high + high * x + y]
	 */
	@Override
	public void getImage(int frame, byte[] data, int pos) throws IOException
	{
		loadSpecData(frame);
		processSpecData();

		for (int x = 0; x < magnitude.length; x++)
		{
			for (int y = 0; y < magnitude[x].length; y++)
			{
				float loc = 0;

				if (logData)
				{
					loc = (float) (((Math.log(magnitude[x][y])) - Math
							.log(displayMin + 0.001)) / (Math

					.log(displayMax + 0.001) - Math.log(displayMin + 0.001)));
				} else
				{
					loc = ((((magnitude[x][y])) - (displayMin)) / ((displayMax) - (displayMin)));
				}

				if (loc > 1)
				{
					loc = 1;
				}
				if (loc < 0)
				{
					loc = 0;
				}
				data[pos + magnitude[0].length * x + y] = (byte) (255 * loc);
			}
		}

	}

	/**
	 * This function will load a given imagedata
	 * set into the current int[] at the given
	 * position
	 */
	@Override
	public void getImage(int frame, int[] data, int pos) throws IOException
	{
		loadSpecData(frame);
		processSpecData();

		for (int x = 0; x < magnitude.length; x++)
		{
			for (int y = 0; y < magnitude[x].length; y++)
			{
				float loc = 0;

				if (logData)
				{
					loc = (float) (((Math.log(magnitude[x][y])) - Math
							.log(displayMin + 0.001)) / (Math

					.log(displayMax + 0.001) - Math.log(displayMin + 0.001)));
				} else
				{
					loc = ((((magnitude[x][y])) - (displayMin)) / ((displayMax) - (displayMin)));
				}
				if (loc > 1)
				{
					loc = 1;
				}
				if (loc < 0)
				{
					loc = 0;
				}
				data[pos + magnitude[0].length * x + y] = (int) (255 * loc);
			}
		}

	}

}
