package com.joey.software.imageToolkit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class ImageProcessing
{
	public static final int X_AXIS = 0;

	public static final int Y_AXIS = 1;

	public static void main(String[] arg) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		File f = ImageFileSelector.getUserImageFile();
		if (f == null)
		{
			return;
		}
		SmoothTest(f, 2);
	}

	public static void getByteData(BufferedImage src, byte[][] output)
	{

	}

	public static void transformImageToGray(BufferedImage img, float[] data)
	{
		int count = 0;
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				data[count++] = ImageOperations.getGrayScale(img.getRGB(x, y));
			}
		}
	}

	public static void transformImageToGray(BufferedImage img, double[] data)
	{
		int count = 0;
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				data[count++] = ImageOperations.getGrayScale(img.getRGB(x, y));
			}
		}
	}

	public static void transformImageToGray(BufferedImage img, float[][] data)
	{
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				data[x][y] = ImageOperations.getGrayScale(img.getRGB(x, y));
			}
		}
	}

	public static float[] reMakeImage(float[] data, BufferedImage img)
	{
		float min = (DataAnalysisToolkit.getMinf(data));
		float max = (DataAnalysisToolkit.getMaxf(data));
		if (min > max)
		{
			float f = max;
			max = min;
			min = f;
		}

		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				float val = (data[x * img.getHeight() + y] - min) / (max - min);
				if (val < 0)
				{
					val = 0;
				}
				if (val > 1)
				{
					val = 1;
				}
				img.setRGB(x, y, ImageOperations.getGrayRGB((int) (val * 256)));
			}
		}
		return new float[]
		{ min, max };

	}

	public static void reMakeImage(float[] data, float[][] holder)
	{
		for (int x = 0; x < holder.length; x++)
		{

			System
					.arraycopy(data, x * holder[0].length, holder[x], 0, holder[0].length);
		}
	}

	public static void MaskTest() throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final File[] files = ImageFileSelector.getUserImageFile(true);

		final JButton next = new JButton("Next");
		final JButton last = new JButton("Last");

		final JSpinner threshold = new JSpinner(new SpinnerNumberModel(1, 1,
				255, 1));
		final JSpinner size = new JSpinner(new SpinnerNumberModel(1, 0, 10000,
				1));
		final JSpinner count = new JSpinner(new SpinnerNumberModel(1, 1, 10000,
				1));
		final JSpinner rot = new JSpinner(new SpinnerNumberModel(0, 0, 4, 1));
		final JSpinner scale = new JSpinner(new SpinnerNumberModel(1, 0.001, 1,
				0.01));

		final ImagePanel srcPanel = new ImagePanel();
		final ImagePanel rstPanel = new ImagePanel();

		final JCheckBox realUpdate = new JCheckBox("Real time update");

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

		buttonPanel.setBorder(BorderFactory.createTitledBorder(""));
		buttonPanel.add(last);
		buttonPanel.add(next);

		class Action implements ActionListener
		{
			int current = 0;

			public void setImage(int num)
			{
				try
				{
					BufferedImage src = ImageIO.read(files[num]);
					src = ImageOperations.getScaledImage(src, (Double) scale
							.getValue());
					src = ImageOperations.getRotatedImage(src, (Integer) rot
							.getValue());
					BufferedImage rst = ImageOperations.getSameSizeImage(src);

					cleanImageTest(src, rst, (Integer) size.getValue(), (Integer) threshold
							.getValue(), (Integer) count.getValue());

					srcPanel.setImage(src);
					rstPanel.setImage(rst);
				} catch (Exception e)
				{

				}
			}

			public void updateImage()
			{
				if (realUpdate.isSelected() == true)
				{
					setImage(current);
				}
			}

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == next)
				{
					if (current < files.length - 1)
					{
						current++;
					} else
					{
						current = 0;
					}
				} else if (e.getSource() == last)
				{
					if (current > 0)
					{
						current--;
					} else
					{
						current = files.length - 1;
					}
				}

				updateImage();
			}
		}
		;

		final Action action = new Action();
		ChangeListener change = new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				action.updateImage();
			}

		};
		JPanel controlPanel = new JPanel();
		SwingToolkit
				.createPanel(new String[]
				{ "Real Update : ", "Scale : ", "Rotation : ", "Threshold : ",
						"Kernal Size : ", "Iter Count : " }, new JComponent[]
				{ realUpdate, scale, rot, threshold, size, count }, 100, 10, controlPanel);

		JScrollPane leftScroll = new JScrollPane(srcPanel);
		JScrollPane rightScroll = new JScrollPane(rstPanel);
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder("Orignal"));
		rightPanel.setBorder(BorderFactory.createTitledBorder("Processed"));
		leftPanel.add(leftScroll);
		rightPanel.add(rightScroll);

		JPanel imagePanel = new JPanel(new GridLayout(1, 2));
		imagePanel.add(leftPanel);
		imagePanel.add(rightPanel);

		JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		temp.add(controlPanel);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(imagePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.add(temp, BorderLayout.NORTH);
		JFrame frame = FrameFactroy.getFrame(mainPanel);
		frame.setVisible(true);

		next.addActionListener(action);
		last.addActionListener(action);

		threshold.addChangeListener(change);
		size.addChangeListener(change);
		count.addChangeListener(change);
		rot.addChangeListener(change);
		scale.addChangeListener(change);
	}

	public static void ProcessingTest() throws IOException
	{
		File file = new File("c:\\test\\image.jpg");
		HashMap<Image, String> labels = new HashMap<Image, String>();
		BufferedImage src = ImageIO.read(file);
		// src = ImageOperations.getScaledImage(src, 0.5);
		src = ImageOperations.getRotatedImage(src, 1);
		labels.put(src, "Orignal");

		BufferedImage smooth1 = ImageOperations.getSameSizeImage(src);
		removeNoise(src, smooth1, 1);
		labels.put(smooth1, "Smooth 1");

		BufferedImage blur = ImageOperations.getSameSizeImage(src);
		blur(src, blur);
		labels.put(blur, "Blurred");

		BufferedImage sharpen = ImageOperations.getSameSizeImage(src);
		sharpen(src, sharpen);
		labels.put(sharpen, "Sharpen");

		BufferedImage edge = ImageOperations.getSameSizeImage(src);
		edgeDetect(src, edge);
		labels.put(edge, "Edge");

		JFrame f = FrameFactroy.getFrame(labels, new Image[]
		{ src, smooth1, blur, sharpen, edge });
		f.setSize(1000, 900);
	}

	public static void lineTest(File f, int split) throws IOException
	{

		BufferedImage src = ImageIO.read(f);
		BufferedImage smooth1 = ImageOperations.getSameSizeImage(src);
		BufferedImage smooth2 = ImageOperations.getSameSizeImage(src);
		BufferedImage smooth3 = ImageOperations.getSameSizeImage(src);
		removeNoise(src, smooth1, 3);
		removeNoise(smooth1, smooth2, 3);
		removeNoise(smooth2, smooth3, 3);

		BufferedImage splits[] = ImageOperations
				.splitImage(smooth3, split, ImageOperations.X_AXIS);

		JTabbedPane tabs = new JTabbedPane();
		for (int i = 0; i < split; i++)
		{
			BufferedImage img = splits[i];
			double[] sd = new double[src.getWidth()];
			double[] avg = new double[src.getWidth()];
			for (int x = 0; x < src.getWidth(); x++)
			{
				int[] line = getLineData(img, x, Y_AXIS);
				sd[x] = DataAnalysisToolkit.getStdDev(line);
				avg[x] = DataAnalysisToolkit.getAverage(line);
			}
			JPanel sDPlotPanel = PlotingToolkit
					.getChartPanel(sd, "Standard Deviation", "Pos", "Pxls");
			JPanel avgPlotPanel = PlotingToolkit
					.getChartPanel(avg, "Average Value", "Pos", "Pxls");
			JPanel imagePanel = new ImagePanel(img,
					ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);

			JPanel plots = new JPanel(new GridLayout(2, 1));
			plots.add(sDPlotPanel);
			plots.add(avgPlotPanel);

			JPanel panel = new JPanel(new BorderLayout());
			panel.add(imagePanel, BorderLayout.NORTH);
			panel.add(plots, BorderLayout.CENTER);
			tabs.addTab("Sec " + i, panel);
		}

		JPanel p = new JPanel(new BorderLayout());
		p.add(tabs);
		JFrame frame = FrameFactroy.getFrame(p);
		frame.setVisible(true);

	}

	public static void SmoothTest(File f, int kernalSize) throws IOException
	{

		BufferedImage src = ImageIO.read(f);
		src = ImageOperations.getRotatedImage(src, 1);
		BufferedImage smooth1 = ImageOperations.getSameSizeImage(src);
		BufferedImage smooth2 = ImageOperations.getSameSizeImage(src);
		BufferedImage smooth3 = ImageOperations.getSameSizeImage(src);

		removeNoise(src, smooth1, kernalSize);
		removeNoise(smooth1, smooth2, kernalSize);
		removeNoise(smooth2, smooth3, kernalSize);
		JFrame frame = FrameFactroy.getFrame(src, smooth1, smooth2, smooth3);
		frame.setSize(1200, 1100);
	}

	public static void cleanImageTest(BufferedImage src, BufferedImage dst, int smooth, int threshold, int runCount)
			throws IOException
	{

		BufferedImage buff1 = ImageOperations.getSameSizeImage(src);
		BufferedImage buff2 = ImageOperations.getSameSizeImage(src);

		int size = smooth;

		// System.out.println("Src -> 1");
		smoothAndThreshold(src, buff1, size, threshold, 0);
		for (int i = 1; i < runCount; i++)
		{
			if (i % 2 == 1)
			{
				// System.out.println("1 -> 2");
				smoothAndThreshold(buff1, buff2, size, threshold, 0);
			} else if (i % 2 == 0)
			{
				// System.out.println("2 -> 1");
				smoothAndThreshold(buff2, buff1, size, threshold, 0);
			}

		}

		BufferedImage mask;
		if (runCount % 2 == 0)
		{
			// System.out.println("2 -> 1");
			smoothAndThreshold(buff2, buff1, size, threshold, 0, threshold + 1, 255);
			mask = buff1;
		} else
		{
			// System.out.println("1 -> 2");
			smoothAndThreshold(buff1, buff2, size, threshold, 0, threshold + 1, 255);
			mask = buff2;
		}

		maskImage(src, mask, dst, threshold);
	}

	public static void maskImage(BufferedImage src, BufferedImage mask, BufferedImage dst, int maskThreshold)
	{
		int wide = src.getWidth();
		int high = src.getHeight();

		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				int val = getPxlValue(mask, x, y);
				if (val > maskThreshold)
				{
					dst.setRGB(x, y, src.getRGB(x, y));
				}
			}
		}
	}

	public static void smoothAndThreshold(BufferedImage src, BufferedImage dst, int size, int minThreshold, int newMinValue)
	{
		smoothAndThreshold(src, dst, size, minThreshold, newMinValue, 255, 255);
	}

	public static void smoothAndThreshold(BufferedImage src, BufferedImage dst, int size, int minThreshold, int newMinValue, int maxThreshold, int newMaxValue)
	{
		int wide = src.getWidth();
		int high = src.getHeight();

		// Create grid to hold average values
		int[][] data = new int[2 * size + 1][2 * size + 1];

		// Random number to know when no value has been stored in data grid
		// this is for the case when the data is outside the pixel space
		int clearData = -222222;

		ArrayList<Integer> values = new ArrayList<Integer>();

		int val;
		int count;
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				// Clear Data kernal
				setIntArray(data, clearData);

				// Get Kernal data
				getPxlData(src, x, y, data, size);

				// Work out average Kernal Value
				// Work out average Kernal Value
				val = 0;
				count = 0;
				for (int i = 0; i < data.length; i++)
				{
					for (int j = 0; j < data[0].length; j++)
					{
						if (data[i][j] != clearData)
						{
							val += data[i][j];
							count++;
						}
					}
				}

				val /= count;
				if (val < minThreshold)
				{
					val = newMinValue;
				} else if (val > maxThreshold)
				{
					val = newMaxValue;
				}
				setPxlValue(dst, x, y, val);
			}
		}
	}

	public static int[] getLineData(BufferedImage img, int pos, int axes)
	{
		if (axes == X_AXIS)
		{
			int data[] = new int[img.getWidth()];
			for (int x = 0; x < img.getWidth(); x++)
			{
				data[x] = getPxlValue(img, x, pos);
			}
			return data;
		} else if (axes == Y_AXIS)
		{
			int data[] = new int[img.getHeight()];
			for (int y = 0; y < img.getHeight(); y++)
			{
				data[y] = getPxlValue(img, pos, y);
			}
			return data;
		} else
		{
			throw new InvalidParameterException("Not a valid axis");
		}
	}

	public static void removeNoise(BufferedImage src, BufferedImage rst, int size)
	{
		int wide = src.getWidth();
		int high = src.getHeight();

		// Create grid to hold average values
		int[][] data = new int[2 * size + 1][2 * size + 1];

		// Random number to know when no value has been stored in data grid
		// this is for the case when the data is outside the pixel space
		int clearData = -222222;

		int count;
		int val;

		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				// Clear Data kernal
				setIntArray(data, clearData);

				// Get Kernal data
				getPxlData(src, x, y, data, size);

				// Work out average Kernal Value
				val = 0;
				count = 0;
				for (int i = 0; i < data.length; i++)
				{
					for (int j = 0; j < data[0].length; j++)
					{
						if (data[i][j] != clearData)
						{
							val += data[i][j];
							count++;
						}
					}
				}

				val /= count;
				setPxlValue(rst, x, y, val);
			}
		}
	}

	/**
	 * this will get a data kernal from a buffered image.
	 * 
	 * @param img
	 * @param xPos
	 * @param yPos
	 * @param data
	 * @param size
	 */
	public static void getPxlData(BufferedImage img, int xPos, int yPos, int[][] data, int size)
	{
		int yD;
		int xD;

		for (int x = 0; x < data.length; x++)
		{
			xD = xPos - size + x;
			if (xD >= 0 && xD < img.getWidth())
			{
				for (int y = 0; y < data[0].length; y++)
				{
					yD = yPos - size + y;
					if (yD >= 0 && yD < img.getHeight())
					{
						data[x][y] = getPxlValue(img, xD, yD);
					}
				}
			}
		}
	}

	/**
	 * This will sharpen a given image
	 * 
	 * @param src
	 * @param dst
	 */
	public static void sharpen(BufferedImage src, BufferedImage dst)
	{
		float data[] =
		{ -1.0f, -1.0f, -1.0f, -1.0f, 9.0f, -1.0f, -1.0f, -1.0f, -1.0f };
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
				null);
		convolve.filter(src, dst);
	}

	/**
	 * This will blurr a given inage
	 * 
	 * @param src
	 * @param dst
	 */
	public static void blur(BufferedImage src, BufferedImage dst)
	{
		float data[] =
		{ 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f, 0.0625f, 0.125f,
				0.0625f };
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
				null);
		convolve.filter(src, dst);
	}

	/**
	 * This will perform an edge detection on a given image
	 * 
	 * @param src
	 * @param dst
	 */
	public static void edgeDetect(BufferedImage src, BufferedImage dst)
	{
		float data[] =
		{ 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f };

		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
				null);
		convolve.filter(src, dst);
	}

	public static int getPxlValue(BufferedImage img, int x, int y)
	{
		Color c = new Color(img.getRGB(x, y));
		return (c.getRed() + c.getGreen() + c.getBlue()) / 3;
	}

	public static void setPxlValue(BufferedImage img, int x, int y, int value)
	{
		Color c = new Color(value, value, value);
		img.setRGB(x, y, c.getRGB());
	}

	/**
	 * This function will set all the values in a given Int[][] to a given
	 * value.
	 * 
	 * @param data
	 * @param value
	 */
	public static void setIntArray(int[][] data, int value)
	{
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				data[x][y] = value;
			}
		}
	}
}
