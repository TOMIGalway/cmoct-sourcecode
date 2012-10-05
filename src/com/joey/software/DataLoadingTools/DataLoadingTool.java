package com.joey.software.DataLoadingTools;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.stringToolkit.StringOperations;


public class DataLoadingTool extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	File octFile = new File("");

	FileInputStream inOCT = null;

	short[][] dataS;

	int[][] dataI;

	byte[][] dataB;

	double[][] dataD;

	JTabbedPane tab = new JTabbedPane();

	JSpinner frameSkip = new JSpinner(new SpinnerNumberModel());

	JSpinner frameWide = new JSpinner(new SpinnerNumberModel(256, 0,
			Integer.MAX_VALUE, 1));

	JSpinner frameHigh = new JSpinner(new SpinnerNumberModel(256, 0,
			Integer.MAX_VALUE, 1));

	JSpinner headerSkip = new JSpinner(new SpinnerNumberModel(100, 0,
			Integer.MAX_VALUE, 1));

	JSpinner frameNum = new JSpinner(new SpinnerNumberModel());

	DynamicRangeImage octB = new DynamicRangeImage();

	DynamicRangeImage octS = new DynamicRangeImage();

	DynamicRangeImage octI = new DynamicRangeImage();

	DynamicRangeImage octD = new DynamicRangeImage();

	JButton exportSeries = new JButton("Export");

	public DataLoadingTool(File octFile) throws IOException
	{
		// BinaryToolkit.binaryFileTool(octFile);
		// if(true)return;
		inOCT = new FileInputStream(octFile);
		this.octFile = octFile;
		createJPanel();

		updateImage();

	}

	public void createJPanel()
	{

		tab.addTab("oct Byte", octB);
		tab.addTab("oct Short", octS);
		tab.addTab("oct Int", octI);
		tab.addTab("oct Double", octD);

		JPanel control = new JPanel();

		control.setLayout(new GridLayout(3, 4));

		control.add(new JLabel("Header Skip:"));
		control.add(headerSkip);

		control.add(new JLabel("Frame Wide :"));
		control.add(frameWide);

		control.add(new JLabel("Frame Skip :"));
		control.add(frameSkip);

		control.add(new JLabel("Frame High :"));
		control.add(frameHigh);

		control.add(new JLabel("Frame Number "));
		control.add(frameNum);

		setLayout(new BorderLayout());
		add(tab, BorderLayout.CENTER);
		add(control, BorderLayout.NORTH);
		add(exportSeries, BorderLayout.SOUTH);

		exportSeries.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread()
				{
					@Override
					public void run()
					{

						exportSeries();
					}
				};
				t.start();
			}
		});
		ChangeListener lis = new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				try
				{
					updateImage();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		frameSkip.addChangeListener(lis);
		frameWide.addChangeListener(lis);
		frameHigh.addChangeListener(lis);
		headerSkip.addChangeListener(lis);
		frameNum.addChangeListener(lis);
		tab.addChangeListener(lis);
	}

	public DynamicRangeImage getCurrentRangePanel()
	{
		if (tab.getSelectedIndex() == 0)
		{
			return octB;
		} else if (tab.getSelectedIndex() == 1)
		{
			return octS;
		} else if (tab.getSelectedIndex() == 2)
		{
			return octI;
		} else if(tab.getSelectedIndex() == 3)
		{
			return octD;
		}
		return null;
	}

	public void exportSeries()
	{

		/**
		 * Create start end index panel
		 */
		JSpinner startIndex = new JSpinner(new SpinnerNumberModel());
		JSpinner endIndex = new JSpinner(new SpinnerNumberModel());

		JLabel startLabel = new JLabel("Start Frame :");
		JLabel endLabel = new JLabel("Start Frame :");

		JPanel startPanel = new JPanel(new BorderLayout());
		JPanel endPanel = new JPanel(new BorderLayout());

		startLabel.setPreferredSize(new Dimension(100, 0));
		endLabel.setPreferredSize(new Dimension(100, 0));

		startLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		endLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		startPanel.add(startLabel, BorderLayout.WEST);
		endPanel.add(endLabel, BorderLayout.WEST);

		endPanel.add(endIndex, BorderLayout.CENTER);
		startPanel.add(startIndex, BorderLayout.CENTER);

		FileSelectionField output = new FileSelectionField();
		output.setFormat(FileSelectionField.FORMAT_IMAGE_FILES_SHOW_FORMAT);

		JPanel indexPanel = new JPanel(new GridLayout(3, 1));
		indexPanel.add(startPanel);
		indexPanel.add(endPanel);
		indexPanel.add(output);

		if (JOptionPane.showConfirmDialog(null, indexPanel) == JOptionPane.OK_OPTION)
		{

			int start = (Integer) startIndex.getValue();
			int end = (Integer) endIndex.getValue();

			if (end < start)
			{
				JOptionPane
						.showMessageDialog(null, "End cannot be before Start");
				return;
			}
			String[] part = FileOperations.splitFile(output.getFile());

			for (int i = start; i < end; i++)
			{
				try
				{
					frameNum.setValue(i);
					updateImage();

					BufferedImage img = (getCurrentRangePanel().getImage()
							.getImage());

					String num = StringOperations.getNumberString(4, i);
					System.out.println(part[2]);
					ImageIO.write(img, part[2], new File(part[0] + part[1]
							+ num + "." + part[2]));
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static double readDoubleLittleEndian(InputStream in) throws IOException
	{
		// get the 8 unsigned raw bytes, accumulate to a long and then
		// convert the 64-bit pattern to a double.
		long accum = 0;
		for (int shiftBy = 0; shiftBy < 64; shiftBy += 8)
		{
			// must cast to long or the shift would be done modulo 32
			accum |= ((long) ((byte) (in.read()) & 0xff)) << shiftBy;
		}
		return Double.longBitsToDouble(accum);

		// there is no such method as Double.reverseBytes( d );

	}

	public static void main(String args[]) throws IOException,
			InterruptedException
	{

		File input = new File(
				"\\\\crysis-pc\\c$\\Portland_data\\data1.txt");
		//FrameFactroy.getFrame(new DataLoadingTool(input));
		BinaryToolkit.binaryFileTool(input);
		if (true)
			return;

		// DataInputStream in = new DataInputStream(new FileInputStream(input));
		//		
		// for (int i = 0; i < 1000; i++)
		// {
		// System.out.println(readDoubleLittleEndian(in));
		//			
		// }
		// String input = "c:\\test\\images\\bostonteapot.raw";
		// String output = "c:\\test\\images\\output";
		// int offset = 0;
		// int wide = 128;
		// int high = 256;
		// int skip = 0;
		// int num = 256;
		// DynamicRangeImage img = new DynamicRangeImage();
		// FrameFactroy.getFrame(img);
		//
		// ImageFileSaver save = new ImageFileSaver(8, 16);
		// byte[][] data = new byte[wide][high];
		//
		// for (int i = 0; i < num; i++)
		// {
		// loadImage(new File(input),i, offset, skip, data);
		//			
		// BufferedImage image =ImageOperations.getImage(data);
		// img.setData(image);
		// File out = new
		// File(output+StringOperations.getNumberString(3,i)+".png");
		// //save.addData(out, image);
		// }
	}

	public static void loadImage(File input, int frameNum, int frameOffset, int frameHeader, short[][] output)
			throws IOException
	{
		int wide = output.length;
		int high = output[0].length;
		int offset = frameOffset;
		int header = frameHeader;
		FileInputStream in = new FileInputStream(input);

		BufferedInputStream inOCT = new BufferedInputStream(in,
				output[0].length * 5);

		// Set bytes per pixel
		int bytePerPxl = 2;

		// Skip the header
		inOCT.skip(header);

		// Skip to current Frame
		long frameSize = (offset + (wide * high) * bytePerPxl);
		inOCT.skip(offset + frameNum * frameSize);

		byte[] holder = new byte[high * bytePerPxl];
		for (int i = 0; i < wide; i++)
		{
			inOCT.read(holder);
			process(holder, output[i]);
		}
	}

	public static void loadImage(File input, int frameNum, int frameOffset, int frameHeader, byte[][] output)
			throws IOException
	{

		int wide = output.length;
		int high = output[0].length;
		int offset = frameOffset;
		int header = frameHeader;
		FileInputStream in = new FileInputStream(input);

		BufferedInputStream inOCT = new BufferedInputStream(in,
				output[0].length * 5);

		// Set bytes per pixel
		int bytePerPxl = 1;

		// Skip the header
		inOCT.skip(header);

		// Skip to current Frame
		long frameSize = (offset + (wide * high) * bytePerPxl);
		inOCT.skip(offset + frameNum * frameSize);

		byte[] holder = new byte[high * bytePerPxl];
		for (int i = 0; i < wide; i++)
		{
			inOCT.read(holder);
			process(holder, output[i]);
		}

	}

	public static void loadImage(File input, int frameNum, int frameOffset, int frameHeader, int[][] output)
			throws IOException
	{

		int wide = output.length;
		int high = output[0].length;
		int offset = frameOffset;
		int header = frameHeader;
		FileInputStream in = new FileInputStream(input);

		BufferedInputStream inOCT = new BufferedInputStream(in,
				output[0].length * 5);

		// Set bytes per pixel
		int bytePerPxl = 4;

		// Skip the header
		inOCT.skip(header);

		// Skip to current Frame
		long frameSize = (offset + (wide * high) * bytePerPxl);
		inOCT.skip(offset + frameNum * frameSize);

		byte[] holder = new byte[high * bytePerPxl];
		for (int i = 0; i < wide; i++)
		{
			inOCT.read(holder);
			process(holder, output[i]);
		}

	}

	public void updateImage() throws IOException
	{

		// BinaryToolkit.binaryFileTool(oct, "OCT");
		// BinaryToolkit.binaryFileTool(oct,"OCU");

		inOCT.close();

		inOCT = new FileInputStream(octFile);

		int wide = (Integer) frameWide.getValue();
		int high = (Integer) frameHigh.getValue();
		int header = (Integer) headerSkip.getValue();
		int offset = (Integer) frameSkip.getValue();
		int imageNum = (Integer) frameNum.getValue();

		int bytePerPxl = 1;
		// Load Byte Data
		if (tab.getSelectedIndex() == 0)
		{

			dataI = null;
			dataS = null;
			dataD = null;
			if (dataB == null || dataB.length != wide
					|| dataB[0].length != high)
			{
				dataB = new byte[wide][high];
			}
			// Set bytes per pixel
			bytePerPxl = 1;

			// Skip the header
			inOCT.skip(header);

			// Skip to current Frame
			long frameSize = (offset + (wide * high) * bytePerPxl);
			inOCT.skip(offset + imageNum * frameSize);

			byte[] holder = new byte[high * bytePerPxl];
			for (int i = 0; i < wide; i++)
			{
				inOCT.read(holder);
				process(holder, dataB[i]);
			}
			octB.setBlockUpdateRange(true);
			octB.setData(getImage(dataB));
			octB.setBlockUpdateRange(false);
		} else if (tab.getSelectedIndex() == 1)
		{
			dataI = null;
			dataB = null;
			dataD = null;
			if (dataS == null || dataS.length != wide
					|| dataS[0].length != high)
			{
				dataS = new short[wide][high];
			}
			// Set bytes per pixel
			bytePerPxl = 2;

			// Skip the header
			inOCT.skip(header);

			// Skip to current Frame
			long frameSize = (offset + (wide * high) * bytePerPxl);
			inOCT.skip(offset + imageNum * frameSize);

			byte[] holder = new byte[high * bytePerPxl];
			for (int i = 0; i < wide; i++)
			{
				inOCT.read(holder);
				process(holder, dataS[i]);
			}
			octS.setBlockUpdateRange(true);
			octS.setDataShort(dataS);
			octS.setBlockUpdateRange(false);

		} else if (tab.getSelectedIndex() == 2)
		{
			dataS = null;
			dataB = null;
			dataD = null;
			if (dataI == null || dataI.length != wide
					|| dataI[0].length != high)
			{
				dataI = new int[wide][high];
			}
			// Set bytes per pixel
			bytePerPxl = 4;

			// Skip the header
			inOCT.skip(header);

			// Skip to current Frame
			long frameSize = (offset + (wide * high) * bytePerPxl);
			inOCT.skip(offset + imageNum * frameSize);

			byte[] holder = new byte[high * bytePerPxl];
			for (int i = 0; i < wide; i++)
			{
				inOCT.read(holder);
				process(holder, dataI[i]);
			}
			octI.setBlockUpdateRange(true);
			octI.setDataInteger(dataI);
			octI.setBlockUpdateRange(false);

		} else if (tab.getSelectedIndex() == 3)
		{
			dataS = null;
			dataB = null;
			dataI = null;
			if (dataD == null || dataD.length != wide
					|| dataD[0].length != high)
			{
				dataD = new double[wide][high];
			}
			// Set bytes per pixel
			bytePerPxl = 8;

			// Skip the header
			inOCT.skip(header);

			// Skip to current Frame
			long frameSize = (offset + (wide * high) * bytePerPxl);
			inOCT.skip(offset + imageNum * frameSize);

			byte[] holder = new byte[high * bytePerPxl];
			for (int i = 0; i < wide; i++)
			{
				inOCT.read(holder);
				process(holder, dataD[i]);
			}
			octD.setBlockUpdateRange(true);
			octD.setDataDouble(dataD);
			octD.setBlockUpdateRange(false);

			//JOptionPane.showConfirmDialog(null, "This");
		}

		// inOCT.skip(offset);
		// if (dataI == null || dataS == null || dataB == null ||
		//
		// dataI.length != w || dataS.length != w || dataB.length != w ||
		//
		// dataI[0].length != h || dataS[0].length != h || dataB[0].length != h)
		// {
		// dataI = new int[w][h];
		// dataS = new short[w][h];
		// dataB = new byte[w][h];
		// }
		//
		// inOCT.skip(rs);
		// byte[] holder = new byte[h * 2];
		// for (int i = 0; i < w; i++)
		// {
		// inOCT.read(holder);
		// // process(holder, dataI[i]);
		// process(holder, dataS[i]);
		// process(holder, dataB[i]);
		// }
		//
		// octB.setData(getImage(dataB));
		// octS.setDataShort(dataS);
		// octI.setDataInteger(dataI);
	}

	public static void process(byte[] buffer, byte[] output)
	{
		for (int i = 0; i < output.length; i++)
		{
			output[i] = buffer[i];
		}
	}

	public static void process(byte[] buffer, short[] output)
	{
		for (int i = 0; i < output.length; i++)
		{
			output[i] = BinaryToolkit.readShort(buffer, i * 2);
		}
	}

	public static void process(byte[] buffer, double[] output)
	{
		for (int i = 0; i < output.length; i++)
		{
			output[i] = (short) BinaryToolkit.readFlippedDouble(buffer, i * 8);
		}
	}

	public static void process(byte[] buffer, int[] output)
	{
		for (int i = 0; i < output.length; i++)
		{
			output[i] = BinaryToolkit.readInt(buffer, i * 4);
		}
	}

	public static BufferedImage getImage(byte[][] data)
	{
		BufferedImage out = new BufferedImage(data.length, data[0].length,
				BufferedImage.TYPE_BYTE_GRAY);

		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				float val = ((128 + data[x][y]) / 255.0f);

				Color c = new Color(val, val, val);
				int rgb = c.getRGB();
				out.setRGB(x, y, rgb);
			}
		}

		return out;
	}

}
