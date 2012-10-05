package com.joey.software.MultiThreadCrossCorrelation;

import ij.IJ;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsFRGImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.MultiThreadCrossCorrelation.threads.CrossCorrelationMaster;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;


public class CrossCorrLauncher
{
	public static void main(String input[]) throws IOException
	{
		processBatchData();
		// viewCMOCTData();
	}

	/**
	 * This will allow real time sliceing through
	 * a cmOCT dataset and display a historgram at
	 * the end
	 * 
	 * @throws IOException
	 */
	public static void viewCMOCTData() throws IOException
	{
		File file = FileSelectionField.getUserFile();
		ThorlabsIMGImageProducer inputData = new ThorlabsIMGImageProducer(file,
				true);
		/**
		 * CrossCorrelatoin Processing
		 */

		IJ.freeMemory();
		CrossCorrelationMaster master = new CrossCorrelationMaster(4);
		CrossCorrelationDataset task = new CrossCorrelationDataset(null);

		task.setData(inputData);
		task.setSaveMIP(true);
		task.setSaveRawMIP(true);
		task.setSaveFlow(true);
		task.setSaveStruct(true);
		task.setCrossCorrRawInMemory(false);
		task.setCrossCorrByteinMemory(false);
		task.setDataInMemory(true);
		task.setImageAlign(false);

		task.setMaxPosMIP(512);
		task.setMinPosMIP(0);

		task.setCrossCorrMin(0.6f);
		task.setCrossCorrMax(-0.6f);

		task.setKerX(3);
		task.setKerY(3);
		task.setThreshold(25);
		task.setInterlace(2);
		master.setCurrentTask(task);

		task.allocateMemory();

		master.processDataSet(true, true);

		int[] histogram = new int[1000];
		float val = 0;

		float min = -1;
		float max = 1;
		for (int x = 0; x < task.getSizeX(); x++)
		{
			for (int y = 0; y < task.getSizeY(); y++)
			{
				for (int z = 0; z < task.getSizeZ(); z++)
				{
					val = (float) task.crossCorrRawData[z][x][y]
							/ (float) Short.MAX_VALUE;

					val = (val - min) / (max - min);
					if (val < 0)
					{
						val = 0;
					} else if (val > 1)
					{
						val = 1;
					}
					histogram[(int) (val * histogram.length - 1)]++;
				}
			}
		}

		String data = CSVFileToolkit.getCSVColumn(histogram);
		CSVFileToolkit.writeCSVData(new File("c:\\test\\histogram.csv"), data);

		System.gc();
	}

	public static void processBatchData() throws IOException
	{
		
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
					modelList.add(dataHolder.size(), f.toString());
					dataHolder.add(new File[] { f });
				}
			}
		});
		JFrame wait = FrameFactroy.getFrame(pan);
		FrameWaitForClose c = new FrameWaitForClose(wait);
		c.waitForClose();

		boolean display = JOptionPane.showConfirmDialog(null, "Would you like to Display the data")==JOptionPane.OK_OPTION;
		// Add Tabs
		HashMap<File[], DynamicRangeImage> images = new HashMap<File[], DynamicRangeImage>();
		JPanel panel = new JPanel(new BorderLayout());
		StatusBarPanel status = new StatusBarPanel();
		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panel.add(tabs, BorderLayout.CENTER);
		panel.add(status, BorderLayout.SOUTH);

		if (display)
		{
			FrameFactroy.getFrame(panel);
			for (int i = 0; i < dataHolder.size(); i++)
			{
				JPanel holder = new JPanel(new BorderLayout());
				DynamicRangeImage dynRanImg = new DynamicRangeImage();

				holder.add(dynRanImg, BorderLayout.CENTER);
				holder.setBorder(BorderFactory.createTitledBorder(dataHolder
						.get(i)[0].toString()));

				tabs.addTab("Waiting", holder);

				images.put(dataHolder.get(i), dynRanImg);
			}
		}
		
		for (int i = 0; i < dataHolder.size(); i++)
		{
			File[] file = dataHolder.get(i);
			String ext = FileOperations.getExtension(file[0]);
			if (ext.compareToIgnoreCase(".img") == 0)
			{
				inputData = new ThorlabsIMGImageProducer(file[0], true);

			} else if (ext.compareToIgnoreCase(".frg") == 0)
			{
				inputData = new ThorlabsFRGImageProducer(file[0]);
				((ThorlabsFRGImageProducer) inputData).getUserInputs();
			} else
			// Assume image series and try and
			// load
			{
				inputData = new ImageFileProducer(file);
			}

			/**
			 * CrossCorrelatoin Processing
			 */

			IJ.freeMemory();
			CrossCorrelationMaster master = new CrossCorrelationMaster(4);
			CrossCorrelationDataset task = new CrossCorrelationDataset(status);

			task.setData(inputData);
			task.setSaveMIP(true);
			task.setSaveRawMIP(true);
			task.setSaveFlow(true);
			task.setSaveStruct(true);
			task.setCrossCorrRawInMemory(false);
			task.setCrossCorrByteinMemory(false);
			task.setDataInMemory(true);
			task.setMIPInMemory(true);
			task.setImageAlign(false);

			task.setMaxPosMIP(512);
			task.setMinPosMIP(0);

			task.setCrossCorrMin(0.6f);
			task.setCrossCorrMax(-0.6f);

			task.setKerX(3);
			task.setKerY(3);
			task.setThreshold(25);
			task.setInterlace(2);
			master.setCurrentTask(task);

			task.allocateMemory();

			// Display Data
			if (display)
			{
				DynamicRangeImage range = images.get(file);
				range.setFitImage(true);
				range.setDataFloat(task.MIPData);
				range.setMinValue(task.getCrossCorrMin());
				range.setMaxValue(task.getCrossCorrMax());
				tabs.setTitleAt(i, "Processing");
			}
			System.out.println("Process ["+(i+1)+" of "+dataHolder.size()+"] : "+file[0].toString());
			
			System.out.println("Time : "+ master.processDataSet(true, false));

			// Display Dat
			if (display)
			{
				DynamicRangeImage range = images.get(file);
				tabs.setTitleAt(i, "Finished");
				range.updateImagePanel();
			}
			inputData = null;
			task = null;
			master = null;
			System.gc();
		}
	}
}
