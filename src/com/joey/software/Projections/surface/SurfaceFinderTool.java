package com.joey.software.Projections.surface;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.dsp.FFTtool;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.toolkit.VolumeInputSelectorPanel;


public class SurfaceFinderTool
{
	public static final int TYPE_AVERAGE = 0;

	public static final int TYPE_MAX = 1;

	public static final int TYPE_MIN = 2;

	public static final int TYPE_SUM = 3;

	public static void main(String input[]) throws IOException
	{
		autoSurfaceFinder();
		//sliceSurfaceFinder();
		//volumeSurfaceFinder();
	}

	public static float[] b2f(byte[] data)
	{
		float[] result = new float[data.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = b2i(data[i]);
		}
		return result;
	}

	

	
	public static void removeTooLow(float[] data, float[] peaks, float val)
	{
		for (int i = 0; i < peaks.length; i++)
		{
			if (peaks[i] > 0)
			{
				if (data[i] < val)
				{
					peaks[i] = 0;
				}
			}
		}
	}

	public static void sliceSurfaceFinder() throws IOException
	{
		File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\before_3D_000.IMG");
		ThorlabsIMGImageProducer loader = new ThorlabsIMGImageProducer(f, true);

		byte[][] frameLoader = new byte[loader.getSizeY()][loader.getSizeX()];

		loader.getImage(100, frameLoader);
		byte[][] frame = ArrayToolkit.transpose(frameLoader);
		byte[][] peaks = new byte[frame.length][frame[0].length];

		float[] aScan = new float[frame[0].length];
		float[] aScanSmooth = new float[frame[0].length];
		float[] aScanGrad = new float[frame[0].length];
		float[] aScanGradGrad = new float[frame[0].length];

		FFTtool tool = new FFTtool(aScan.length);
		tool.allocateMemory();

		int kerX = 5;
		int count = 0;

		BufferedImage img = VolumeProcessing.drawOverlay(frame, peaks);
		ImagePanel panel = new ImagePanel(img);
		FrameFactroy.getFrame(panel.getInPanel());

		for (int i = 0; i < loader.getImageCount(); i++)
		{

			loader.getImage(i, frameLoader);
			ArrayToolkit.transpose(frameLoader, frame);
			for (int posX = 0; posX < frame.length; posX++)
			{

				// At each pos find ascan
				for (int y = 0; y < frame[0].length; y++)
				{
					aScan[y] = 0;
					count = 0;
					for (int x = posX - kerX; x <= posX + kerX; x++)
					{
						if (x >= 0 && x < frame.length)
						{
							aScan[y] += b2i(frame[x][y]);
							count++;
						}

					}
					if (count == 0)
					{
						count = 1;
					}
					aScan[y] /= count;
				}

				// Smooth A-scan
				tool.setRealData(aScan);
				tool.fftData();
				tool.gaussianBlur(200);
				tool.ifftData(true);
				tool.getMagData(aScanSmooth);

				determineCrossings(aScanSmooth, peaks[posX], 10,10);

			}
			VolumeProcessing.drawOverlay(frame, peaks, img);
			panel.repaint();
		}
	}

	

	public static void volumeSurfaceFinder() throws IOException
	{
		File f = new File(
		 "D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\after_3D_000.IMG");
		// "D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Comparison Regions\\before_3D_000.IMG");
		//		"D:\\Current Analysis\\Project Data\\Correlation\\reacitve hyperimea\\Good Day RH\\Attempt 3\\data_3D_001.IMG");
		ThorlabsIMGImageProducer loader = new ThorlabsIMGImageProducer(f, true);

		byte[][][] data = loader.createDataHolder();
		byte[][][] peak = loader.createDataHolder();
		float[] aScan = new float[data[0][0].length];
		float[] aScanSmooth = new float[data[0][0].length];
		float[] aScanGrad = new float[data[0][0].length];
		float[] aScanGradGrad = new float[data[0][0].length];

		FFTtool tool = new FFTtool(data[0][0].length);
		tool.allocateMemory();

		loader.getData(data, null);

		 OCTAnalysis analysis = new
		 OCTAnalysis();
		 analysis.setVideoMemory(256 * 1024 *
		 1024);
		 analysis.setData(data);
		 analysis.setData(peak);
		 FrameFactroy.getFrame(analysis).setSize(1024,
		 800);

		int kerX = 1;
		int kerZ = 1;

		int count = 0;
		for (int posX = 0; posX < data[0].length; posX++)
		{
			for (int posZ = 0; posZ < data.length; posZ++)
			{
				// At each pos find ascan
				for (int y = 0; y < data[0][0].length; y++)
				{
					aScan[y] = 0;
					count = 0;
					for (int x = posX - kerX; x <= posX + kerX; x++)
					{
						for (int z = posZ - kerZ; z <= posZ + kerZ; z++)
						{
							if (z >= 0 && x >= 0 && x < data[0].length
									&& z < data.length)
							{
								aScan[y] += b2i(data[z][x][y]);
								count++;
							}
						}
					}
					if (count == 0)
					{
						count = 1;
					}
					aScan[y] /= count;
				}

				// Smooth A-scan
				tool.setRealData(aScan);
				tool.fftData();
				tool.gaussianBlur(50);
				tool.ifftData(true);
				tool.getMagData(aScanSmooth);


				determineCrossings(aScanSmooth, peak[posZ][posX], 10,30);
			}
		}

		// Surface Found, now select it;
		VolumeProcessing.findSurfaceFromUserSelection(data, peak);
	}

	public static void determineCrossings(float[] aScan, byte crossing[], float delta, float threshold)
	{

			Vector<Integer> maxPos = new Vector<Integer>();
			Vector<Integer> minPos = new Vector<Integer>();
			
			DataAnalysisToolkit.findPeaks(aScan, delta,threshold, maxPos, minPos);
			
			for(Integer i : maxPos)
			{
				crossing[i] = (byte)255;
			}
			
			for(Integer i : minPos)
			{
				crossing[i] = (byte)128;
			}
	}
	public static void printArray(float[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			System.out.println(data[i]);
		}
	}

	public static void autoSurfaceFinder() throws IOException
	{
		StatusBarPanel status = new StatusBarPanel();

		byte[][][] struct = getVolumeData(status);
		byte[][][] flow;

		if (JOptionPane
				.showConfirmDialog(null, "Would you like to load a flow dataset?", "Please Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		{
			flow = getVolumeData(status);
		} else
		{
			flow = struct;
		}

		float[][] surface = null;

		if (JOptionPane.OK_OPTION == JOptionPane
				.showConfirmDialog(null, "Would you like to load a previous surface map"))
		{
			surface = loadSurfaceMap(FileSelectionField.getUserFile());
			if (surface.length != struct[0].length
					|| surface[0].length != struct.length)
			{
				JOptionPane
						.showMessageDialog(null, "The size of the surface map is not the same as the dataset. Epic FAIL!");
				surface = new float[struct[0].length][struct.length];
			}
		} else
		{
			surface = new float[struct[0].length][struct.length];
		}
		// Such a bad idea, using this to pass
		// variables between two functions
		// val[0] is
		//
		//
		int[] badProgrammingHolder = new int[3];
		createSurfaceMap(struct, surface, badProgrammingHolder, status);
		showDepthSlices(struct, flow, surface, badProgrammingHolder);

		struct = null;
		flow = null;

		System.gc();
		System.gc();
		System.gc();

		FrameWaitForClose.showWaitFrame();
		if (JOptionPane.OK_OPTION == JOptionPane
				.showConfirmDialog(null, "Would you like to save your surface map"))
		{
			saveSurfaceMap(FileSelectionField.getUserFile(), surface);
		}

		System.exit(0);

	}

	public static void saveSurfaceMap(File f, float[][] data)
			throws IOException
	{
		DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
		out.writeInt(data.length);
		out.writeInt(data[0].length);

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				out.writeDouble(data[x][y]);
			}
		}
		out.flush();
		out.close();
	}

	public static float[][] loadSurfaceMap(File f) throws IOException
	{
		float[][] data;

		DataInputStream input = new DataInputStream(new FileInputStream(f));
		int wide = input.readInt();
		int high = input.readInt();

		data = new float[wide][high];
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				data[x][y] = (float) input.readDouble();
			}
		}
		return data;
	}

	public static byte[][][] getIMGData(StatusBarPanel status)
			throws IOException
	{
		File f = new File(
				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\Joey Arm\\Left\\After Clearing\\1.00bh_3D_000.IMG");
		f = FileSelectionField.getUserFile();
		ThorlabsIMGImageProducer dataLoader = new ThorlabsIMGImageProducer(f);

		final byte[][][] data = dataLoader.createDataHolder();
		dataLoader.getData(data, status);

		return data;
	}

	public static byte[][][] getImageSeriesData(StatusBarPanel status)
			throws IOException
	{
		ImageFileProducer dataLoader = new ImageFileProducer(
				ImageFileSelectorPanel.getUserSelection());
		final byte[][][] data = dataLoader.createDataHolder();
		dataLoader.getData(data, status);
		return data;
	}

	public static byte[][][] getVolumeData(StatusBarPanel status)
			throws IOException
	{
		return VolumeInputSelectorPanel.getUserVolumeData(status);
	}

	/**
	 * DataHolder is used to link this function
	 * with and showDepthSlices with getDetph
	 * Slices (can be null) linker[0] is the slice
	 * number linker[1] is the average number
	 * 
	 * @param data
	 * @param surface
	 * @param dataHolder
	 * @param status
	 * @throws IOException
	 */
	public static void createSurfaceMap(final byte[][][] data, final float[][] surface, final int[] linker, final StatusBarPanel status)
			throws IOException
	{
		final float[][] mask = new float[data[0].length][data[0].length];

		final JCheckBox chagneFrame = new JCheckBox();
		final JCheckBox doFFT = new JCheckBox();
		final JSpinner threshold = new JSpinner(new SpinnerNumberModel(100, 0,
				255, 1));

		final JSpinner topSkip = new JSpinner(new SpinnerNumberModel(1, -512,
				512, 1));

		final JSpinner blur = new JSpinner(new SpinnerNumberModel(100, 0, 9000,
				1));
		final JSpinner frame = new JSpinner(new SpinnerNumberModel(0, 0,
				data.length, 1));
		final JSpinner avg = new JSpinner(new SpinnerNumberModel(0, 0,
				data.length, 1));
		final JSpinner delay = new JSpinner(new SpinnerNumberModel(0, 0,
				data.length, 1));

		final ImagePanel imgPanel = new ImagePanel();
		final BufferedImage image = new BufferedImage(data[0].length,
				data[0][0].length, BufferedImage.TYPE_INT_ARGB);
		final FFT2Dtool tool = new FFT2Dtool(data[0].length, data.length);

		FFT2Dtool.createGaussianMask(mask, 30,30);
		tool.allocateMemory();

		imgPanel.setImage(image);
		imgPanel.setHighQualityRenderingEnabled(true);

		JPanel controls = new JPanel(new GridLayout(3, 3, 5, 5));
		controls.add(SwingToolkit.getLabel(threshold, "Threshold : ", 90));
		controls.add(SwingToolkit.getLabel(topSkip, "Top Skip: ", 90));
		controls.add(new JPanel());

		controls.add(SwingToolkit.getLabel(doFFT, "FFT : ", 90));
		controls.add(SwingToolkit.getLabel(blur, "Blur : ", 90));
		controls.add(SwingToolkit.getLabel(avg, "Avg : ", 90));

		controls.add(SwingToolkit.getLabel(frame, "Frame : ", 90));
		controls.add(SwingToolkit.getLabel(chagneFrame, "Run : ", 90));
		controls.add(SwingToolkit.getLabel(delay, "Delay : ", 90));

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(status, BorderLayout.SOUTH);
		panel.add(imgPanel.getInPanel(), BorderLayout.CENTER);
		panel.add(controls, BorderLayout.NORTH);

		final JFrame frm = FrameFactroy.getFrame(panel);

		/*
		 * I use a boolean array so that within
		 * the run function i can access the final
		 * first term of the array to kill the
		 * thread
		 */
		final boolean alive[] = { true };
		final Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				float lastThreshold = -10000;
				boolean lastFFT = false;
				int lastBlur = -1000000;

				while (alive[0])
				{
					if ((Integer) threshold.getValue() != lastThreshold
							|| lastFFT != doFFT.isSelected()
							|| lastBlur != (Integer) blur.getValue())
					{
						synchronized (surface)
						{
							surfaceMapThreshold(data, ((Integer) threshold
									.getValue()), surface, (Integer) topSkip
									.getValue(), null);

							if (doFFT.isSelected())
							{
								tool.setRealData(surface);
								tool.fftData();
								tool.gaussianMask((Integer) blur.getValue());
								tool.ifftData(true);
								tool.getMagData(surface);
							}

							lastThreshold = (Integer) threshold.getValue();
							lastFFT = doFFT.isSelected();
							lastBlur = (Integer) blur.getValue();
						}
					}
					if (status != null)
					{
						status.setMaximum(data.length);
						status.setValue((Integer) frame.getValue());
					}
					frm.setTitle("Frame :" + frame.getValue());

					if (chagneFrame.isSelected())
					{
						if ((Integer) frame.getValue() < data.length - 1)
						{
							frame.setValue((Integer) frame.getValue() + 1);
						} else
						{
							frame.setValue(0);
						}
					}

					synchronized (surface)
					{
						int slice = 0;
						int sliceSize = 0;
						if (linker != null)
						{
							synchronized (linker)
							{
								slice = linker[0];
								sliceSize = linker[1];
							}
						}
						getSlice(image, data, surface, (Integer) frame
								.getValue(), (Integer) avg.getValue(), Color.RED
								.getRGB(), slice - sliceSize, slice + sliceSize);
					}

					imgPanel.repaint();

					if ((Integer) delay.getValue() != 0)
					{
						try
						{
							Thread.sleep((Integer) delay.getValue());
						} catch (InterruptedException e1)
						{
							// TODO Auto-generated
							// catch block
							e1.printStackTrace();
						}
					}

				}

				tool.freeMemory();

			}
		});
		t.setDaemon(true);
		t.start();

		Thread cleanUp = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				FrameWaitForClose close = new FrameWaitForClose(frm);
				close.waitForClose();
				alive[0] = false;

			}
		});
		cleanUp.setDaemon(false);
		cleanUp.start();
	}

	/**
	 * This function dataHolder[] is a link
	 * between this fuction and linker[0] is the
	 * slice number linker[1] is the average
	 * number
	 * 
	 * @param data
	 * @param surface
	 * @param dataHolder
	 */
	public static void showDepthSlices(final byte[][][] struct, final byte[][][] flow, final float[][] surface, final int[] linker)
	{
		final float[][] slice = new float[struct[0].length][struct.length];

		final DynamicRangeImage slicePanel = new DynamicRangeImage();
		JButton resetButton = new JButton("RESET");

		final JComboBox projectionType = new JComboBox(new String[] {
				"Average", "Max", "Min", "Sum" });
		final JCheckBox running = new JCheckBox("Running");

		final JSpinner blur = new JSpinner(new SpinnerNumberModel(100, 0, 512,
				1));
		final JCheckBox fft = new JCheckBox("FFT");
		final JSpinner slicePos = new JSpinner(new SpinnerNumberModel(0, -512,
				512, 1));
		final JSpinner runStep = new JSpinner(new SpinnerNumberModel(1, -512,
				512, 1));

		final JSpinner pro = new JSpinner(new SpinnerNumberModel(5, -512, 512,
				1));

		final JComboBox mode = new JComboBox(
				new String[] { "Structure", "Flow" });

		JPanel control = new JPanel(new GridLayout(2, 4));
		control.add(SwingToolkit.getLabel(mode, "Mode : ", 50));
		control.add(SwingToolkit.getLabel(pro, "Avg : ", 50));
		control.add(SwingToolkit.getLabel(fft, "FFT : ", 50));
		control.add(SwingToolkit.getLabel(blur, "Blur : ", 50));

		control.add(running);
		control.add(resetButton);
		control.add(SwingToolkit.getLabel(slicePos, "pos : ", 50));
		control.add(SwingToolkit.getLabel(runStep, "Step : ", 50));
		control.add(SwingToolkit.getLabel(projectionType, "Project : ", 50));

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(slicePanel, BorderLayout.CENTER);
		panel.add(control, BorderLayout.NORTH);

		final JFrame f = FrameFactroy.getFrame(panel);

		int pos = -20;
		slicePanel.setDataFloat(slice);
		slicePanel.setMaxValue(255);
		slicePanel.setMinValue(0);

		final boolean[] reset = { false };

		resetButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				reset[0] = true;
			}
		});

		final boolean alive[] = { true };

		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				FFT2Dtool tool = new FFT2Dtool(surface.length,
						surface[0].length);
				tool.allocateMemory();

				while (alive[0])
				{
					f.setTitle("Mip : " + slicePos.getValue());
					byte[][][] data = (mode.getSelectedIndex() == 0 ? struct
							: flow);
					getYProjectionSlice(data, surface, slice, (Integer) slicePos
							.getValue() - (Integer) pro.getValue(), (Integer) slicePos
							.getValue() + (Integer) pro.getValue(), projectionType
							.getSelectedIndex());

					if (fft.isSelected())
					{
						tool.setRealData(slice);
						tool.fftData();
						tool.gaussianMask((Integer) blur.getValue());
						tool.ifftData(true);
						tool.getMagData(slice);
					}
					slicePanel.updateImagePanel();
					if (running.isSelected())
					{
						slicePos.setValue((Integer) slicePos.getValue()
								+ (Integer) runStep.getValue());
					}
					synchronized (reset)
					{

						if (reset[0] == true)
						{
							reset[0] = false;

							slicePos.setValue(0);
						}
					}
					if (linker != null)
					{
						synchronized (linker)
						{
							linker[0] = (Integer) slicePos.getValue();
							linker[1] = (Integer) pro.getValue();
						}
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();

		// Tidy up function
		final FrameWaitForClose close = new FrameWaitForClose(f);
		Thread tidy = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				close.waitForClose();
				alive[0] = false;
			}
		});
		tidy.start();

	}

	// public static void getDepthSlice(byte[][][]
	// data, float[][] surface, int depth, int
	// avgker, float[][] out)
	// {
	// int y = 0;
	// int count = 0;
	// for (int x = 0; x < data[0].length; x++)
	// {
	// for (int z = 0; z < data.length; z++)
	// {
	// y = (int) (depth + surface[x][z]);
	// count = 0;
	// out[x][z] = 0;
	// for (int yP = y - avgker; yP <= y + avgker;
	// yP++)
	// {
	// if (yP > 0 && yP < data[0][0].length)
	// {
	// out[x][z] += b2i(data[z][x][yP]);
	// count++;
	// } else
	// {
	// out[x][z] = 0;
	// }
	// }
	// if (count == 0)
	// {
	// count = 1;
	// }
	// out[x][z] /= count;
	// }
	// }
	// }

	public static BufferedImage getSlice(byte[][][] data, float[][] surf, int frame, int frameAvg, int rgb, int posStart, int posEnd)
	{

		BufferedImage rst = new BufferedImage(data[0].length,
				data[0][0].length, BufferedImage.TYPE_INT_ARGB);
		getSlice(rst, data, surf, frame, frameAvg, rgb, posStart, posEnd);
		return rst;
	}

	public static void getSlice(BufferedImage rst, byte[][][] data, float[][] surf, int frame, int frameAvg, int rgb, int posStart, int posEnd)
	{
		int sizeZ = data.length;
		int sizeX = data[0].length;
		int sizeY = data[0][0].length;

		Graphics2D g = rst.createGraphics();
		float val = 0;

		int count = 0;
		for (int x = 0; x < sizeX; x++)
		{
			for (int y = 0; y < sizeY; y++)
			{
				val = 0;
				count = 0;
				for (int z = frame - frameAvg; z <= frame + frameAvg; z++)
				{
					if (z >= 0 && z < data.length)
					{
						val += b2i(data[z][x][y]);
						count++;
					}
				}
				if (count == 0)
				{
					count = 1;
				}
				rst.setRGB(x, y, ImageOperations
						.getGrayRGB((int) (val / count)));
			}
		}

		// Draw Color
		Graphics2D g1 = rst.createGraphics();
		g1.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		for (int x = 0; x < sizeX; x++)
		{
			int yP = (int) surf[x][frame];

			g1.setColor(new Color(rgb));
			g1.drawRect(x, yP + posStart, 1, (posEnd - posStart));
		}
	}

	/**
	 * This will perform Z projects over the given
	 * range posStart-posEnd surface data[z][x][y]
	 * result[x][z]; surf[x][z];
	 * 
	 * @param rst
	 * @param data
	 * @param surf
	 * @param posStart
	 * @param posEnd
	 * @param rgb
	 * @param slice
	 * @param sliceSize
	 */
	public static void getYProjectionSlice(byte[][][] data, float[][] surf, float[][] result, int posStart, int posEnd, int type)
	{

		if (posStart > posEnd)
		{
			int tmp = posEnd;
			posEnd = posStart;
			posStart = tmp;
		}
		float val = 0;
		int count = 0;
		boolean first = true;

		for (int x = 0; x < data[0].length; x++)
		{

			for (int z = 0; z < data.length; z++)
			{
				val = 0;
				count = 0;
				result[x][z] = 0;

				first = true;
				for (int y = (int) (surf[x][z] + posStart); y <= (int) (surf[x][z] + posEnd); y++)
				{

					if (y >= 0 && y < data[0][0].length)
					{
						val = b2i(data[z][x][y]);
						count++;

						if (first)// if first set
									// as first
									// value
						{
							first = false;
							result[x][z] = val;
						} else if (type == TYPE_MAX)
						{
							if (val > result[x][z])
							{
								result[x][z] = val;
							}
						} else if (type == TYPE_MIN)
						{
							if (val < result[x][z])
							{
								result[x][z] = val;
							}
						} else if (type == TYPE_AVERAGE || type == TYPE_SUM)
						{
							result[x][z] += val;
						}

					}
				}

				if (count == 0)
				{
					count = 1;
				}

				if (type == TYPE_AVERAGE)
				{
					result[x][z] /= count;
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

	public static void surfaceMapThreshold(byte[][][] data, int threshold, float[][] dist, int topSkip, StatusBarPanel status)
	{
		int sizeZ = data.length;
		int sizeX = data[0].length;
		int sizeY = data[0][0].length;

		for (int x = 0; x < sizeX; x++)
		{
			for (int z = 0; z < sizeZ; z++)
			{

				int peak = 0;
				int val = 0;
				for (int y = topSkip; y < sizeY; y++)
				{
					val = data[z][x][y] < 0 ? data[z][x][y] + 256
							: data[z][x][y];
					if (val > threshold)
					{
						dist[x][z] = y;
						y = sizeY;
					}
				}
			}
		}
	}

	public static void surfaceMapPeak(byte[][][] data, float[][] dist, StatusBarPanel status)
	{
		int sizeZ = data.length;
		int sizeX = data[0].length;
		int sizeY = data[0][0].length;

		for (int x = 0; x < sizeX; x++)
		{
			for (int z = 0; z < sizeZ; z++)
			{

				int peak = 0;
				int val = 0;
				for (int y = 0; y < sizeY; y++)
				{
					val = data[z][x][y] < 0 ? data[z][x][y] + 256
							: data[z][x][y];
					if (z == 0)
					{
						peak = val;
						dist[x][z] = y;
					} else
					{
						if (val > peak)
						{
							peak = val;
							dist[x][z] = y;
						}

					}
				}
			}
		}
	}
}
