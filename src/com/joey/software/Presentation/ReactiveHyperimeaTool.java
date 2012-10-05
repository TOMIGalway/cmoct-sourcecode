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
package com.joey.software.Presentation;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.MultiThreadCrossCorrelation.CrossCorrelationDataset;
import com.joey.software.Projections.surface.SurfaceFinderTool;
import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageFileLoader;
import com.joey.software.imageToolkit.ImageFileLoaderInterface;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.plottingToolkit.PlotingToolkit;
import com.joey.software.stringToolkit.StringOperations;
import com.sun.media.jai.codec.TIFFEncodeParam;


public class ReactiveHyperimeaTool
{
	public static void main(String input[]) throws Exception
	{
		//normalDepthProjection();
		
		//copyMIPDataToFolder();
		// showAverageFromMIPImages();
		// MIPTool.BatchReProjectMIP();
		// displayDepthResolvedHyperimea();
		// convertDataSetToTiffStack();
		// generateDepthAverageValue();
		// generateDepthAverageValueWithSurface();

		// getTemporalAverage();

	}

	/**
	 * this tool will normalise the depth data
	 * between two ranges
	 */
	public static void normalDepthProjection()
	{
		final DynamicRangeImage before = new DynamicRangeImage();
		final DynamicRangeImage after = new DynamicRangeImage();

		final JSpinner maxStart = new JSpinner();
		final JSpinner maxEnd = new JSpinner();

		final JSpinner minStart = new JSpinner();
		final JSpinner minEnd = new JSpinner();

		final JSpinner threshold = new JSpinner(new SpinnerNumberModel(0.1,
				-Double.MAX_VALUE, +Double.MAX_VALUE, 1));

		JPanel holder = new JPanel(new GridLayout(5, 1));
		holder.add(SwingToolkit.getLabel(minStart, "Min Start : ", 100));
		holder.add(SwingToolkit.getLabel(minEnd, "Min End : ", 100));
		holder.add(SwingToolkit.getLabel(maxStart, "Max Start : ", 100));
		holder.add(SwingToolkit.getLabel(maxEnd, "Max End: ", 100));
		holder.add(SwingToolkit.getLabel(threshold, "Threshold: ", 100));
		FrameFactroy.getFrame(holder);

		FileDrop drop = new FileDrop(FrameFactroy.getFrameTabs(before, after),
				new FileDrop.Listener()
				{

					@Override
					public void filesDropped(File[] files)
					{
						try
						{
							float[][] data = CrossCorrelationDataset
									.loadMIPRawData(files[0]);
							float[][] norm = ArrayToolkit.clone(data);

							int posX, posY, wide;

							for (int y = 0; y < data[0].length; y++)
							{
								// Get Max
								posX = (Integer) maxStart.getValue();
								wide = (Integer) maxEnd.getValue();
								float max = 0;
								int count = 0;
								for (int x = posX; x < wide; x++)
								{
									max += data[x][y];
									count++;
								}
								max /= count;

								float min = 0;
								count = 0;
								posX = (Integer) minStart.getValue();
								wide = (Integer) minEnd.getValue();
								for (int x = posX; x < wide; x++)
								{
									min += data[x][y];
									count++;
								}
								min /= count;

								System.out.println(max + "," + min);

								for (int x = 0; x < data.length; x++)
								{
									if (max > (Double) threshold.getValue())
									{
										norm[x][y] = (data[x][y] - min)
												/ (max - min);
									} else
									{
										norm[x][y] = 0;
									}
								}

							}

							before.setDataFloat(data);
							after.setDataFloat(norm);

						} catch (Exception e)
						{

						}

					}
				});
	}

	

	public static void convertDataSetToTiffStack() throws IOException
	{
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = true;

		final Vector<File[]> dataHolder = new Vector<File[]>();

		ImageProducer inputData;
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

		for (int i = 0; i < dataHolder.size(); i++)
		{
			File file = dataHolder.get(i)[0];

			ThorlabsIMGImageProducer imgLoader = new ThorlabsIMGImageProducer(
					file, true);
			saveTiff(getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned), new File(
					"c:\\test\\data" + i + ".tif"));
		}
	}

	public static void saveTiff(final File[] files, File tiffFile)
			throws IOException
	{

		class Temp implements Iterator
		{
			File[] images;

			int last = 0;

			public Temp(File[] file)
			{
				this.images = file;
			}

			@Override
			public boolean hasNext()
			{
				// TODO Auto-generated method stub
				return last < images.length;
			}

			@Override
			public Object next()
			{
				try
				{
					BufferedImage img = ImageIO.read(images[last++]);
					return ImageOperations.cropImage(img, new Rectangle(0, 0,
							img.getWidth(), 200));
				} catch (IOException e)
				{
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void remove()
			{
				// TODO Auto-generated method stub

			}
		}

		Temp fileHolder = new Temp(files);

		TIFFEncodeParam param = new TIFFEncodeParam();
		param.setExtraImages(fileHolder);
		param.setCompression(TIFFEncodeParam.COMPRESSION_NONE);

		ParameterBlock pb = new ParameterBlock();
		OutputStream os = new FileOutputStream(tiffFile);

		pb.addSource(fileHolder.next());
		// pb.addSource ( JAI.create ( "fileload",
		// filename ) ) ;
		pb.add(os);
		pb.add("TIFF");
		pb.add(param);
		JAI.create("encode", pb);
		os.close();

	}

	public static void displayDepthResolvedHyperimea() throws IOException,
			InterruptedException
	{
		File f = FileSelectionField.getUserFile();

		float[][] sourceData = CrossCorrelationDataset.loadMIPRawData(f);

		FrameFactroy.getFrame(sourceData);
		float[][] data = DataAnalysisToolkit.copy(sourceData);
		// for (int y = 0; y < data[0].length;
		// y++)
		// {
		// float min = 0;
		// float max = 0;
		// for (int x = 0; x < data.length; x++)
		// {
		// if (x == 0)
		// {
		// min = data[x][y];
		// max = data[x][y];
		// }
		// if (min > data[x][y])
		// {
		// min = data[x][y];
		// }
		// if (max < data[x][y])
		// {
		// max = data[x][y];
		// }
		// }
		//
		// for (int x = 0; x < 10; x++)
		// {
		// if (x == 0)
		// {
		// max = data[x][y];
		// } else
		// {
		// max += data[x][y];
		// }
		// }
		// max /= 10;
		//
		// for (int x = 0; x < data.length; x++)
		// {
		// data[x][y] = (data[x][y] - min) / (max
		// - min);
		// }
		// }

		// FFT2Dtool tool = new
		// FFT2Dtool(data.length,data[0].length);
		// tool.allocateMemory();
		// tool.clearDataHolder();
		// tool.setRealData(data);
		// tool.fftData();
		// //tool.gaussianBlur(100);
		// tool.ifftData(true);
		// tool.getMagData(data);

		FrameFactroy.getFrame(data);

		CSVFileToolkit
				.writeCSVData(new File("c:\\test\\depthData.csv"), CSVFileToolkit
						.getCSVData(data));
		float[][] trans = DataAnalysisToolkit.transpose(data);

		JPanel hold = new JPanel(new BorderLayout());
		FrameFactroy.getFrame(hold);
		int i = 0;
		while (true)
		{
			hold.removeAll();
			hold.add(PlotingToolkit.getChartPanel(trans[i], "", "", ""), BorderLayout.CENTER);
			hold.updateUI();
			i++;
			if (i >= trans.length - 1)
			{
				i = 0;
			}
			Thread.sleep(100);
		}
	}

	/**
	 * This function will copy all processed MIP
	 * Data into a desired filder
	 * 
	 * @throws IOException
	 */
	public static void copyMIPDataToFolder() throws IOException
	{

		boolean aligned = true;
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;

		FileSelectionField saveFolder = new FileSelectionField();
		saveFolder.setFormat(FileSelectionField.FORMAT_FOLDERS);
		saveFolder.getUserChoice();
		final Vector<File[]> dataHolder = new Vector<File[]>();

		ImageProducer inputData;

		File[] fileData;

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

		for (int i = 0; i < dataHolder.size(); i++)
		{
			File file = dataHolder.get(i)[0];

			String[] data = FileOperations.splitFile(file);

			String[] loc = FileOperations.splitFile(saveFolder.getFile());
			File mip = getMIPFile(file, kerX, kerY, threshold, aligned, false);
			ImageIO.write(ImageIO.read(mip), "PNG", new File(loc[0] + "\\"
					+ data[1] + ".png"));
		}
	}

	public static File getMIPFile(File src, int kerX, int kerY, int threshold, boolean aligned, boolean raw)
	{
		String[] data = FileOperations.splitFile(src);
		// Create Output Location
		String savePath = data[0] + "\\" + data[1] + "\\" + (2 * kerX + 1)
				+ "-" + (2 * kerY + 1) + "-" + threshold + " - "
				+ (aligned ? " " : "Not ") + "Aligned"
				+ (raw ? "\\mip.raw" : "\\mip.png");
		System.out.println(savePath);
		return new File(savePath);
	}

	/**
	 * This function will grab the average data as
	 * a function of depth determined from the
	 * surface
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void generateDepthAverageValueWithSurface()
			throws IOException, InterruptedException
	{
		StatusBarPanel status = new StatusBarPanel();

		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = false;

		int surfThreshold = 130;
		int topSkip = 5;
		int blur = 100;
		int posStart = 0;
		int posEnd = 200;
		int steps = 2;

		final Vector<File[]> dataHolder = new Vector<File[]>();

		ImageProducer inputData;
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

		float[][] depthData = null;
		byte[][][] struct = null;
		byte[][][] flow = null;

		int sizeZ = 1;
		int sizeX = 1;
		int sizeY = 1;

		float[][] surfaceMap = null;
		float[][] sliceFlow = null;

		for (int i = 0; i < dataHolder.size(); i++)
		{
			File file = dataHolder.get(i)[0];

			/**
			 * Load Data
			 */
			ThorlabsIMGImageProducer imgLoader = new ThorlabsIMGImageProducer(
					file, true);

			/**
			 * On first load allocate memory
			 */
			if (i == 0)
			{
				depthData = new float[dataHolder.size()][(posEnd - posStart)
						/ steps];
				struct = imgLoader.createDataHolder();
				flow = new byte[struct.length][struct[0].length][struct[0][0].length];// VolumeInputSelectorPanel.getUserVolumeData(status);

				sizeZ = struct.length;
				sizeX = struct[0].length;
				sizeY = struct[0][0].length;

				surfaceMap = new float[sizeX][sizeZ];
				sliceFlow = new float[sizeX][sizeZ];

				FrameFactroy.getFrame(depthData);
				FrameFactroy.getFrame(surfaceMap);
			}

			imgLoader.getData(struct, status);

			System.out.println("Loading Data : " + file);
			ImageFileProducer imageLoader = new ImageFileProducer(
					getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned));
			imageLoader.getData(flow, status);

			/**
			 * Map the surface
			 */
			SurfaceFinderTool
					.surfaceMapThreshold(struct, surfThreshold, surfaceMap, topSkip, null);

			/**
			 * FFT Tool
			 */
			FFT2Dtool tool = new FFT2Dtool(sizeX, sizeZ);
			tool.allocateMemory();
			tool.clearDataHolder();
			tool.setRealData(surfaceMap);
			tool.fftData();
			tool.gaussianMask(blur);
			tool.ifftData(true);
			tool.getMagData(surfaceMap);

			/**
			 * 
			 */
			int count = 0;
			for (int pos = posStart; pos < posEnd
					&& count < depthData[i].length; pos += steps)
			{
				SurfaceFinderTool
						.getYProjectionSlice(flow, surfaceMap, sliceFlow, pos, pos
								+ steps, SurfaceFinderTool.TYPE_AVERAGE);

				depthData[i][count++] = DataAnalysisToolkit
						.getAveragef(sliceFlow);

			}
			System.out.println("Finished : " + i);
		}
		System.out.println("Done");
		String[] split = FileOperations.splitFile(dataHolder.get(0)[0]);
		CrossCorrelationDataset.saveMIPRawData(depthData, new File(split[0]
				+ "\\depthData" + steps + ".raw"));
	}

	public static void getTemporalAverage() throws IOException
	{
		StatusBarPanel status = new StatusBarPanel();

		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = true;

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

		float[] fractionAboveThreshold = null;
		float[] averageValue = null;
		byte[][][] flow = null;
		for (int i = 0; i < dataHolder.size(); i++)
		{
			File file = dataHolder.get(i)[0];

			/**
			 * Load Data
			 */
			ThorlabsIMGImageProducer imgLoader = new ThorlabsIMGImageProducer(
					file, true);

			ImageFileProducer imageLoader = new ImageFileProducer(
					getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned));

			if (i == 0)
			{
				fractionAboveThreshold = new float[dataHolder.size()];
				averageValue = new float[dataHolder.size()];
				flow = imageLoader.createDataHolder();
			}
			imageLoader.getData(flow, status);

			float[] dat = getStatics(flow, 10);

			averageValue[i] = dat[0];
			fractionAboveThreshold[i] = dat[1];
			System.out.println("Finished : " + i);
		}
		System.out.println("Done");

		for (int i = 0; i < averageValue.length; i++)
		{
			System.out.println(averageValue[i] + ","
					+ fractionAboveThreshold[i]);
		}
	}

	public static float[] getStatics(byte[][][] data, int threshold)
	{
		float[] rst = new float[2];
		rst[0] = 0;
		rst[1] = 0;
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[0].length; y++)
			{
				for (int z = 0; z < data[0][0].length; z++)
				{
					int val = b2i(data[x][y][z]);

					rst[0] += val;
					if (val > threshold)
					{
						rst[1]++;
					}

				}
			}
		}
		rst[0] /= data.length * data[0].length * data[1].length;
		rst[1] /= data.length * data[0].length * data[1].length;
		return rst;
	}

	/**
	 * This function will return an array of all
	 * the images files for the given parameters
	 * 
	 * @param src
	 * @param kerX
	 * @param kerY
	 * @param threshold
	 * @param aligned
	 * @return
	 */
	public static File[] getFlowImageFiles(ThorlabsIMGImageProducer src, int kerX, int kerY, int threshold, boolean aligned)
	{
		File[] files = new File[src.getImageCount() - 1];

		String[] data = FileOperations.splitFile(src.getData()[0]);

		// Create Output Location
		String savePath = data[0] + "\\" + data[1] + "\\" + (2 * kerX + 1)
				+ "-" + (2 * kerY + 1) + "-" + threshold + " - "
				+ (aligned ? " " : "Not ") + "Aligned" + "\\flow\\";

		for (int i = 0; i < files.length; i++)
		{
			files[i] = new File(savePath + "image"
					+ StringOperations.getNumberString(4, i) + ".png");
		}
		return files;
	}

	/**
	 * This function will show a graphicals and
	 * video display of average values of a series
	 * of MIP images files. It will also display a
	 * video of the data
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void showAverageFromMIPImages() throws InterruptedException,
			IOException
	{
		File[] data = ImageFileSelectorPanel.getUserSelection();
		final BufferedImage[] imgData = new BufferedImage[data.length];
		ImageFileLoader.loadImageFiles(data, new ImageFileLoaderInterface()
		{

			@Override
			public void imageLoaded(BufferedImage img, int index)
			{
				imgData[index] = img;
			}
		}, 2, true);

		Thread.sleep(10000);
		float[] ccData = new float[data.length];
		int pos = 0;
		int count = 0;
		for (BufferedImage i : imgData)
		{
			ccData[pos] = 0;
			for (int y = 0; y < i.getHeight(); y++)
			{
				for (int x = 0; x < i.getWidth(); x++)
				{
					ccData[pos] += ImageOperations.getGrayScale(i.getRGB(x, y));
				}

				// System.out.println(ccData[pos]);
			}
			ccData[pos] /= i.getWidth() * i.getHeight();
			pos++;
		}

		FrameFactroy.getFrame(PlotingToolkit.getChartPanel(ccData, "", "", ""));
		CSVFileToolkit.writeCSVData(new File("MIP_Time.csv"), CSVFileToolkit
				.getCSVColumn(ccData));

		DynamicRangeImage pan = new DynamicRangeImage();
		pan.getImage().setHighQualityRenderingEnabled(true);
		FrameFactroy.getFrame(pan);
		int i = 0;

		float[][] holder = null;
		FFT2Dtool tool = null;
		while (true)
		{

			if (i >= imgData.length)
			{
				i = 0;
			}
			if (holder == null || holder.length != imgData[i].getWidth()
					|| holder[0].length != imgData[i].getHeight())
			{
				holder = new float[imgData[i].getWidth()][imgData[i]
						.getHeight()];
				tool = new FFT2Dtool(imgData[i].getWidth(),
						imgData[i].getHeight());
				tool.allocateMemory();
			}
			ImageOperations
					.grabPxlData(imgData[i], holder, ImageOperations.PLANE_GRAY);
			tool.clearDataHolder();
			tool.setRealData(holder);

			tool.fftData();
			tool.gaussianMask(50);
			tool.ifftData(true);
			tool.getMagData(holder);
			pan.setDataFloat(holder);
			pan.setMinValue(0);
			pan.setMaxValue(255);
			pan.updateImagePanel();
			Thread.sleep(100);
			i++;
		}

	}

	public static void showAverageBScan() throws IOException,
			InterruptedException
	{
		File[] data = ImageFileSelectorPanel.getUserSelection();
		final BufferedImage[] imgData = new BufferedImage[data.length];
		final int[] high = new int[] { 0 };
		ImageFileLoader.loadImageFiles(data, new ImageFileLoaderInterface()
		{

			@Override
			public void imageLoaded(BufferedImage img, int index)
			{
				imgData[index] = img;
				high[0] += img.getHeight();
			}
		}, 2, true);

		Thread.sleep(10000);
		float[] ccData = new float[high[0]];
		int pos = 0;
		int count = 0;
		for (BufferedImage i : imgData)
		{
			for (int y = 0; y < i.getHeight(); y++)
			{
				for (int x = 0; x < i.getWidth(); x++)
				{
					ccData[pos] += ImageOperations.getGrayScale(i.getRGB(x, y));
				}
				ccData[pos] /= i.getWidth();
				// System.out.println(ccData[pos]);
				pos++;

			}
		}

		FrameFactroy.getFrame(PlotingToolkit.getChartPanel(ccData, "", "", ""));
	}

	/**
	 * This function will determine the average
	 * depth resolved value from a 3D array
	 * 
	 * @throws Exception
	 */
	public static void generateDepthAverageValue() throws Exception
	{
		StatusBarPanel status = new StatusBarPanel();
		FrameFactroy.getFrame(status);
		byte[][][] data = SurfaceFinderTool.getImageSeriesData(status);

		float[] avg = new float[data.length];

		getAverageValue(data, avg);

		FrameFactroy.getFrame(PlotingToolkit.getChartPanel(avg, "", "", ""));
		for (int i = 0; i < avg.length; i++)
		{
			System.out.println(avg[i]);
		}
	}

	public static void getAverageValue(byte[][][] data, float[] avg)
	{
		for (int z = 0; z < data.length; z++)
		{
			avg[z] = 0;
			for (int x = 0; x < data[z].length; x++)
			{
				for (int y = 0; y < data[z][x].length; y++)
				{
					avg[z] += b2i(data[z][x][y]);
				}
			}
			avg[z] /= data[0].length * data[0][0].length;
		}
	}

	public static int b2i(byte v)
	{
		return (v >= 0 ? v : v + 255);
	}
}
