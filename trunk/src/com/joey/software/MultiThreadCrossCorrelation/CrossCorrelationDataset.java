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
package com.joey.software.MultiThreadCrossCorrelation;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsImageProducer;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.colorMapping.ColorMap;


/**
 * The data must be stored in the format
 * data[z][x][y].
 * 
 * @author joey.enfield
 * 
 */
public class CrossCorrelationDataset
{
	int sizeX = -1;

	int sizeY = -1;

	int sizeZ = -1;

	private int kerX = -1;

	private int kerY = -1;

	private int interlace = 1;

	private int threshold = -1;

	private float crossCorrMin = -1;

	private float crossCorrMax = -1f;

	private int minPosMIP = -1;

	private int maxPosMIP = -1;

	String savePath = "";

	private boolean saveStruct = false;

	private boolean saveFlow = false;

	private boolean saveMIP = false;

	private boolean saveRawMIP = false;

	private boolean imageAlign = true;

	private boolean crossCorrRawInMemory = false;

	private boolean crossCorrByteInMemory = false;

	private boolean dataInMemory = false;

	private boolean mipInMemory = false;

	public byte[][][] structData = null;

	public short[][][] crossCorrRawData = null;

	public byte[][][] crossCorrByteData = null;

	public float[][] MIPData = null;

	public float[][] MIPDepth = null;

	Vector<Integer> remainingFrames = new Vector<Integer>();

	ColorMap mipOutMap = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	ImageProducer inputData = null;

	StatusBarPanel statusbar = null;

	public CrossCorrelationDataset(StatusBarPanel status)
	{
		setStatusBar(status);
	}

	public void setStatusBar(StatusBarPanel status)
	{
		this.statusbar = status;
	}

	public static void main(String input[])
	{
		CrossCorrelationDataset test = new CrossCorrelationDataset(
				new StatusBarPanel());
		test.sizeX = 10;
		test.sizeY = 10;
		test.sizeZ = 10;

		test.createFrameProcessing(3);
		while (test.hasFramesRemaining())
		{
			System.out.println(test.getNextSlice());
		}
	}

	public void createFrameProcessing(int interlace)
	{
		remainingFrames.clear();

		for (int i = 0; i < interlace; i++)
		{
			int frame = i;
			while (frame < sizeZ - 1)
			{
				remainingFrames.add(frame);
				frame += interlace;
			}
		}
	}

	public void setData(ImageProducer data) throws IOException
	{
		this.inputData = data;

		BufferedImage img = inputData.getImage(0);
		this.sizeZ = inputData.getImageCount();
		this.sizeX = img.getWidth();
		this.sizeY = img.getHeight();
	}

	public void allocateMemory()
	{
		// Ensure available Memoryu
		if (isDataInMemory())
		{
			if (structData == null || structData.length != sizeZ
					|| structData[0].length != sizeX
					|| structData[0][0].length != sizeY)
			{
				structData = new byte[sizeZ][sizeX][sizeY];
			}
		}

		if (isCrossCorrRawInMemory())
		{
			if (crossCorrRawData == null || crossCorrRawData.length != sizeZ
					|| crossCorrRawData[0].length != sizeX
					|| crossCorrRawData[0][0].length != sizeY)
			{
				crossCorrRawData = new short[sizeZ][sizeX][sizeY];
			}
		}

		if(isCrossCorrByteInMemory())
		{
			if (crossCorrRawData == null || crossCorrRawData.length != sizeZ
					|| crossCorrRawData[0].length != sizeX
					|| crossCorrRawData[0][0].length != sizeY)
			{
				crossCorrByteData = new byte[sizeZ][sizeX][sizeY];
			}
		}
		if (isMIPinMemory())
		{
			if (MIPData == null || MIPDepth == null || MIPData.length != sizeX
					|| MIPData[0].length != sizeZ || MIPDepth.length != sizeX
					|| MIPDepth[0].length != sizeZ)
			{
				MIPData = new float[sizeX][sizeZ];
				MIPDepth = new float[sizeX][sizeZ];
			}
		}
	}

	public void loadDataToMemory() throws IOException
	{
		if (isDataInMemory())
		{
			load(inputData, structData, statusbar);
		}
	}

	/**
	 * This will return the file the current
	 * dataset is from
	 * 
	 * @return
	 */
	public File getDataFile()
	{
		if (inputData instanceof ImageFileProducer)
		{
			// Load the image Manualy
			ImageFileProducer imgProd = (ImageFileProducer) inputData;
			return imgProd.getData()[0];
		} else
		// Must Be Thorlabs
		{
			ThorlabsImageProducer imgProd = (ThorlabsImageProducer) inputData;
			return imgProd.getData()[0];
		}
	}

	public void createDirectories()
	{
		createDirectories(false, false, false);
	}

	public void createDirectories(boolean forceBase, boolean forceFlow, boolean forceStruct)
	{
		String[] data = FileOperations.splitFile(getDataFile());

		// Create Output Location
		savePath = data[0] + "\\" + data[1] + "\\" + (2 * getKerX() + 1) + "-"
				+ (2 * getKerY() + 1) + "-" + getThreshold() + " - "
				+ (isImageAlign() ? " " : "Not ") + "Aligned" + "\\";

		if (isSaveMIP() || isSaveRawMIP() || forceBase)
		{
			File f = new File(savePath + "mip.png");
			FileOperations.ensureDirStruct(f);
		}

		if (isSaveStruct() || forceStruct)
		{
			File f = new File(savePath + "structure\\image.img");
			FileOperations.ensureDirStruct(f);
		}
		if (isSaveFlow() || forceFlow)
		{
			File f = new File(savePath + "flow\\image.img");
			FileOperations.ensureDirStruct(f);
		}

	}

	public static void load(ImageProducer inputData, byte[][][] data, StatusBarPanel status)
			throws IOException
	{
		long start = 0;
		long time = 0;

		float avg = 0;
		BufferedImage img = inputData.getImage(0);
		byte[] frameHold = new byte[img.getWidth() * img.getHeight()];

		if (status != null)
		{
			status.setStatusMessage("Loading Data");
			status.setMaximum(inputData.getImageCount() - 1);
		}
		for (int frame = 0; frame < inputData.getImageCount(); frame++)
		{
			start = System.currentTimeMillis();
			if (status != null)
			{
				status.setValue(frame);
			}
			// Load the image Manualy
			getImage(inputData, frame, frameHold, 0);
			for (int x = 0; x < img.getWidth(); x++)
			{
				for (int y = 0; y < img.getHeight(); y++)
				{
					data[frame][x][y] = frameHold[x * img.getHeight() + y];
				}
			}
			time = System.currentTimeMillis() - start;
			if (frame == 0)
			{
				avg = (int) time;
			} else
			{
				avg = avg + (time - avg) / 5;
			}

			if (status != null)
			{
				status.setStatusMessage("Loading Data.    Time Remaining :"
						+ (int) ((inputData.getImageCount() - frame - 1.0f)
								* avg / 1000) + " s");
			}
		}

		if (status != null)
		{
			status.setStatusMessage("Loading Complete");
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			status.reset();
		}

	}

	public static void getImage(ImageProducer inputData, int frame, byte data[], int pos)
			throws IOException
	{
		if (inputData instanceof ImageFileProducer)
		{
			// Load the image Manualy
			ImageFileProducer imgProd = (ImageFileProducer) inputData;
			BufferedImage img = imgProd.getImage(frame);
			for (int x = 0; x < img.getWidth(); x++)
			{
				for (int y = 0; y < img.getHeight(); y++)
				{
					data[pos + x * img.getHeight() + y] = (byte) ImageOperations
							.getGrayScale(img.getRGB(x, y));
				}
			}
		} else
		// Must Be Thorlabs
		{
			ThorlabsImageProducer imgProd = (ThorlabsImageProducer) inputData;
			imgProd.getImage(frame, data, pos);
		}
	}

	public boolean hasFramesRemaining()
	{
		return !remainingFrames.isEmpty();
	}

	public int getNextSlice()
	{
		if (hasFramesRemaining())
		{
			int frame = remainingFrames.get(0);
			remainingFrames.remove(0);
			return frame;
		}
		return 0;
	}

	public boolean isImageAlign()
	{
		return imageAlign;
	}

	/**
	 * This function can be very inefficient if
	 * the data is not loaded into memory. use
	 * getFrame(frame, byte[][] data)
	 * 
	 * @param frame
	 * @return
	 * @throws IOException
	 */
	public byte[][] getFrame(int frame) throws IOException
	{

		if (isDataInMemory())
		{
			return structData[frame];
		} else
		{
			if (frame == 0)
			{
				System.out
						.println("CrossCorrelationDataset.getFrame() is not using an optimised "
								+ "as data is not in memory meaning that a new byte[][] is created each call");
			}
			byte[][] data = new byte[sizeX][sizeY];
			getFrame(frame, data);
			return data;
		}

	}

	public static void saveMIPData(CrossCorrelationDataset data, File f)
			throws IOException
	{
		BufferedImage out = getMIPData(data.MIPData, data.getCrossCorrMin(), data
				.getCrossCorrMax(), data.mipOutMap);
		ImageIO.write(out, "png", f);
	}

	public static BufferedImage getMIPData(float[][] MIPData, float min, float max, ColorMap mipOutMap)
	{
		BufferedImage out = new BufferedImage(MIPData.length,
				MIPData[0].length, BufferedImage.TYPE_INT_ARGB);
		getMIPData(MIPData, min, max, mipOutMap, out);
		return out;

	}

	public static void getMIPData(float[][] MIPData, float min, float max, ColorMap mipOutMap, BufferedImage out)
	{

		float val = 0;
		for (int x = 0; x < out.getWidth(); x++)
		{
			for (int y = 0; y < out.getHeight(); y++)
			{
				val = (MIPData[x][y] - min) / (max - min);
				if (val < 0)
				{
					val = 0;
				} else if (val > 1)
				{
					val = 1;
				}

				out.setRGB(x, y, mipOutMap.getColor(val).getRGB());
			}
		}
	}

	public static void saveMIPRawData(float[][] MIP, File f) throws IOException
	{
		DataOutputStream out = new DataOutputStream(new FileOutputStream(f));

		out.writeInt(MIP.length);
		out.writeInt(MIP[0].length);

		for (int x = 0; x < MIP.length; x++)
		{
			for (int y = 0; y < MIP[0].length; y++)
			{
				out.writeFloat(MIP[x][y]);
			}
		}
		out.flush();
		out.close();
	}

	public static float[][] loadMIPRawData(File f) throws IOException
	{
		float[][] data;

		DataInputStream r = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		int sizeX = r.readInt();
		int sizeY = r.readInt();

		data = new float[sizeX][sizeY];

		for (int x = 0; x < sizeX; x++)
		{
			for (int y = 0; y < sizeY; y++)
			{
				data[x][y] = r.readFloat();
			}
		}
		r.close();
		return data;
	}

	public void getFrame(int frame, byte[][] data) throws IOException
	{
		inputData.getImage(frame, data);
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public int getSizeY()
	{
		return sizeY;
	}

	public int getSizeZ()
	{
		return sizeZ;
	}

	public String getSavePath()
	{
		return savePath;
	}

	public boolean isSaveStruct()
	{
		return saveStruct;
	}

	public boolean isSaveFlow()
	{
		return saveFlow;
	}

	public boolean isDataInMemory()
	{
		return dataInMemory;
	}

	public boolean isCrossCorrRawInMemory()
	{
		return crossCorrRawInMemory;
	}

	public boolean isCrossCorrByteInMemory()
	{
		return crossCorrByteInMemory;
	}

	public boolean isMIPinMemory()
	{
		return mipInMemory;
	}

	public int getKerX()
	{
		return kerX;
	}

	public int getKerY()
	{
		return kerY;
	}

	public int getThreshold()
	{
		return threshold;
	}

	public float getCrossCorrMin()
	{
		return crossCorrMin;
	}

	public float getCrossCorrMax()
	{
		return crossCorrMax;
	}

	public int getMinPosMIP()
	{
		return minPosMIP;
	}

	public int getMaxPosMIP()
	{
		return maxPosMIP;
	}

	public boolean isSaveMIP()
	{
		return saveMIP;
	}

	public boolean isSaveRawMIP()
	{
		return saveRawMIP;
	}

	public void getBlurredMIP(int kerX, int kerY, float[][] result)
	{
		int count = 0;

		for (int xP = 0; xP < MIPData.length; xP++)
		{

			for (int yP = 0; yP < MIPData[xP].length; yP++)
			{
				result[xP][yP] = 0;
				count = 0;
				for (int x = xP - kerX; x <= xP + kerX; x++)
				{
					for (int y = yP - kerY; y <= yP + kerY; y++)
					{
						if (x >= 0 && y >= 0 && x < MIPData.length
								&& y < MIPData[0].length)
						{
							result[xP][yP] += MIPData[x][y];
							count++;
						}
					}

				}

				result[xP][yP] /= count;

			}
		}

		return;

	}

	public void unloadData()
	{
		structData = null;
		crossCorrRawData = null;
		System.gc();
	}

	public int getInterlace()
	{
		// TODO Auto-generated method stub
		return interlace;
	}

	public void saveMIP(boolean forceSave) throws IOException
	{
		createDirectories(true, false, false);
		if (isSaveMIP() || forceSave)
		{
			saveMIPData(this, new File(savePath + "MIP.png"));
		}

		if (isSaveRawMIP() || forceSave)
		{
			saveMIPRawData(MIPData, new File(savePath + "mip.raw"));
		}
	}

	public void setSaveMIP(boolean saveMIP)
	{
		this.saveMIP = saveMIP;
	}

	public void setSaveRawMIP(boolean saveRawMIP)
	{
		this.saveRawMIP = saveRawMIP;
	}

	public void setSaveFlow(boolean saveFlow)
	{
		this.saveFlow = saveFlow;
	}

	public void setSaveStruct(boolean saveStruct)
	{
		this.saveStruct = saveStruct;
	}

	public void setCrossCorrRawInMemory(boolean crossCorrRawInMemory)
	{
		this.crossCorrRawInMemory = crossCorrRawInMemory;
	}

	public void setCrossCorrByteinMemory(boolean crossCorrByteInMemory)
	{
		this.crossCorrByteInMemory = crossCorrByteInMemory;
	}

	public void setDataInMemory(boolean dataInMemory)
	{
		this.dataInMemory = dataInMemory;
	}

	public void setImageAlign(boolean imageAlign)
	{
		this.imageAlign = imageAlign;
	}

	public void setMaxPosMIP(int maxPosMIP)
	{
		this.maxPosMIP = maxPosMIP;
	}

	public void setMinPosMIP(int minPosMIP)
	{
		this.minPosMIP = minPosMIP;
	}

	public void setCrossCorrMin(float crossCorrMin)
	{
		this.crossCorrMin = crossCorrMin;
	}

	public void setCrossCorrMax(float crossCorrMax)
	{
		this.crossCorrMax = crossCorrMax;
	}

	public void setKerX(int kerX)
	{
		this.kerX = kerX;
	}

	public void setKerY(int kerY)
	{
		this.kerY = kerY;
	}

	public void setThreshold(int threshold)
	{
		this.threshold = threshold;
	}

	public void setInterlace(int interlace)
	{
		this.interlace = interlace;
	}

	public void setMIPInMemory(boolean selected)
	{
		this.mipInMemory = selected;
	}
}
