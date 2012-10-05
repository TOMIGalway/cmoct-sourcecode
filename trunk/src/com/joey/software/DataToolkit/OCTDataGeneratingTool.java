package com.joey.software.DataToolkit;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;


public class OCTDataGeneratingTool
{
	public static int BUFFER_SIZE = 30;

	public static int getBUFFER_SIZE()
	{
		return BUFFER_SIZE;
	}

	public static void setBUFFER_SIZE(int buffer_size)
	{
		BUFFER_SIZE = buffer_size;
	}

	public static void generateUserOCTDataFile()
	{

	}

	public static void main(String input[]) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException
	{
		File[] files = ImageFileSelector.getUserImageFile(true);

		JSpinner scale = new JSpinner(new SpinnerNumberModel(1., 0, 1., 0.01));
		JOptionPane.showConfirmDialog(null, scale);

		File dataFile = new File("c:\\test\\file_raw.dat");
		File previewFile = new File("c:\\test\\file_prev.dat");

		double scaleValue = (Double) scale.getValue();

		StatusBarPanel panel = new StatusBarPanel();

		FrameFactroy.getFrame(panel);
		try
		{
			createDataFiles(dataFile, previewFile, files, scaleValue, panel);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//
		// JTabbedPane tabbs = new JTabbedPane(JTabbedPane.TOP);
		// JPanel panel = new JPanel(new BorderLayout());
		// panel.add(tabbs, BorderLayout.CENTER);
		//
		// OCTSliceViewer slice = new OCTSliceViewer();
		// tabbs.addTab("Experiment ", slice);
		//
		// OCTDataSet dataSet = new OCTDataSet();
		// slice.setOCTData(dataSet);
		//
		// FrameFactroy.getFrame(panel);
	}

	public static void createDataFiles(File dataFile, File previewFile, File[] imageData, double scale, StatusBarPanel status)
			throws IOException
	{
		ImageProducer p = new ImageFileProducer(imageData);
		createDataFiles(dataFile, previewFile, p, scale, status);

	}

	/**
	 * This will create the data files. If the dataFile is null, not raw data
	 * will be saved only the preview data
	 * 
	 * @param dataFile
	 * @param previewFile
	 * @param imageData
	 * @param scale
	 * @param status
	 * @throws IOException
	 */
	public static void createDataFiles(File dataFile, File previewFile, ImageProducer imageData, double scale, StatusBarPanel status)
			throws IOException
	{
		if (dataFile != null)
		{

			ImageSliceToolkit
					.writeVolumeDataToFile(dataFile, imageData, getBUFFER_SIZE(), status);
		}
		byte[][][] data = createPreviewData(imageData, scale, status);

		writePreviewData(previewFile, data, status);

		data = null;
	}

	public NativeDataSet loadOCTDataSet(File dataFile, File previewFile)
	{
		NativeDataSet data = null;
		try
		{
			data = new NativeDataSet(dataFile, previewFile);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	public static void writePreviewData(File file, byte[][][] data, StatusBarPanel status)
			throws IOException
	{
		int sizeX = data.length;
		int sizeY = data[0].length;
		int sizeZ = data[0][0].length;

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
		out.writeInt(sizeX);
		out.writeInt(sizeY);
		out.writeInt(sizeZ);

		status.setMaximum(sizeX);
		status.setMinimum(0);
		status.setValue(0);
		status.setStatusMessage("Writing Preview Data To File..");

		for (int x = 0; x < sizeX; x++)
		{
			status.setValue(x);
			for (int y = 0; y < sizeY; y++)
			{
				for (int z = 0; z < sizeZ; z++)
				{
					out.writeByte(data[x][y][z]);
				}
				out.flush();
			}
		}
		out.close();
		status.setStatusMessage("Finished Writing Preview Data");
	}

	public static byte[][][] readPreviewData(File file, StatusBarPanel status)
			throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(file));

		int sizeX = in.readInt();
		int sizeY = in.readInt();
		int sizeZ = in.readInt();

		if (status != null)
		{
			status.setMaximum(sizeX);
			status.setValue(0);
			status.setStatusMessage("Allocating Memory");
		}
		byte[][][] data = new byte[sizeX][sizeY][sizeZ];

		if (status != null)
		{
			status.setStatusMessage("Finished");
			status.setStatusMessage("Reading Data From File");
		}
		for (int x = 0; x < sizeX; x++)
		{
			if (status != null)
			{
				status.setValue(x);
			}
			for (int y = 0; y < sizeY; y++)
			{
				in.read(data[x][y]);
				// for (int z = 0; z < sizeZ; z++)
				// {
				// data[x][y][z] = in.readByte();
				// }
			}
		}
		if (status != null)
		{
			status.setStatusMessage("Finished Reading Preview Data");
		}
		in.close();
		return data;
	}

	public static byte[][][] createPreviewData(File[] imgData, double scale, final StatusBarPanel status)
			throws IOException

	{
		ImageFileProducer prod = new ImageFileProducer(imgData);
		return createPreviewData(prod, scale, status);
	}

	public static byte[][][] createPreviewData(ImageProducer imgData, double scale, final StatusBarPanel status)
			throws IOException
	{
		status.setStatusMessage("Creating Preview Data");
		BufferedImage image = imgData.getImage(0);
		final int sizeX = (int) (image.getWidth() * scale);
		final int sizeY = (int) (image.getHeight() * scale);
		final int sizeZ = (int) (imgData.getImageCount() * scale);
		image = null;

		final byte[][][] octData = new byte[sizeX][sizeY][sizeZ];

		if (status != null)
		{
			status.setValue(0);
			status.setMinimum(0);
			status.setMaximum(sizeZ);
		}

		// Store the scale between image to load
		double imgScale = (double) imgData.getImageCount() / (double) sizeZ;

		for (int i = 0; i < sizeZ; i++)
		{
			
			BufferedImage img = imgData.getImage((int) (i * imgScale));
			if (status != null)
			{
				synchronized (status)
				{
					status.setValue(i);
					// prog.repaint();
				}
			}
			double pxlXScale = 0;
			int pxlX = 0;

			double pxlYScale = 0;
			int pxlY = 0;

			int sizeDataX = sizeX;
			int sizeDataY = sizeY;

			pxlXScale = (double) img.getWidth() / (double) sizeX;
			pxlYScale = (double) img.getHeight() / (double) sizeY;
			for (int x = 0; x < sizeDataX; x++)
			{
				for (int y = 0; y < sizeDataY; y++)
				{
					pxlX = (int) (x * pxlXScale);
					pxlY = (int) (y * pxlYScale);

					synchronized (octData)
					{
						octData[x][y][i] = NativeDataSet.getRGBtoByte(img
								.getRGB(pxlX, pxlY));
					}
				}
			}
			img = null;
			System.gc();
		}

		status.setStatusMessage("Finished Creating Preview Data");
		return octData;
	}

	public static byte[][][] readFullData(VolumeHeaderData header, StatusBarPanel status)
	{
		byte[][][] result;
		try
		{
			result = new byte[header.getSizeDataX()][header.getSizeDataY()][header
					.getSizeDataZ()];
		} catch (OutOfMemoryError e)
		{
			JOptionPane
					.showMessageDialog(null, "There was not enough Memory to load the data, \nPlease consult the user manual for methods to overcome this");
			return new byte[][][]
			{
			{
			{ (byte) 1 } } };
		}
		ImageSliceToolkit
				.getVolumeRegionData(header, 0, 0, 0, header.getSizeDataX(), header
						.getSizeDataY(), header.getSizeDataZ(), result, status);
		return result;
	}
}
