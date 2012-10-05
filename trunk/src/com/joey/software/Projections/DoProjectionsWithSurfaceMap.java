package com.joey.software.Projections;


import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.MultiThreadCrossCorrelation.CrossCorrelationDataset;
import com.joey.software.Presentation.ReactiveHyperimeaTool;
import com.joey.software.Projections.surface.SurfaceFinderTool;
import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.stringToolkit.StringOperations;
import com.joey.software.toolkit.VolumeInputSelectorPanel;


public class DoProjectionsWithSurfaceMap
{
	public static void main(String ingput[]) throws Exception
	{
		//doProjectionWithSurface();
		
		//This function will create a depth coded batch of data
		batchProcessingDepthSlices();
	}
	
	public static void batchProcessingDepthSlices() throws IOException
	{
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = true; 
		
		int surfThreshold = 130;
		int topSkip = 5;
		int blur = 100;
		int posStart = 0;
		int posEnd = 200;
		
		int projectType = SurfaceFinderTool.TYPE_AVERAGE;
		int projectionSize = 3;
		
		
		
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

		byte[][][] struct= null;
		byte[][][] flow= null;

		int sizeZ= 1;
		int sizeX= 1;
		int sizeY= 1;

		float[][] surfaceMap = null;
		float[][] sliceFlow = null;
		ImageFileSaver save = new ImageFileSaver(4, 10);
		
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
			if(i == 0)
			{
				
				struct = imgLoader.createDataHolder();
				flow = new byte[ struct.length][struct[0].length][struct[0][0].length];// VolumeInputSelectorPanel.getUserVolumeData(status);

				sizeZ = struct.length;
				sizeX = struct[0].length;
				sizeY = struct[0][0].length;

				surfaceMap = new float[sizeX][sizeZ];
				sliceFlow = new float[sizeX][sizeZ];
			}
			
			imgLoader.getData(struct, null);

			System.out.println("Loading Data : " + file);
			ImageFileProducer imageLoader = new ImageFileProducer(
					ReactiveHyperimeaTool.getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned));
			imageLoader.getData(flow, null);

			/**
			 * Map the surface
			 */
			SurfaceFinderTool
					.surfaceMapThreshold(struct, surfThreshold, surfaceMap, topSkip, null);

			/**
			 * FFT Tool to blur surfacemap
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
			
			String dir = getFlowFolder(file, kerX, kerY, threshold, aligned);
			
			String dataDir = getDataFolder(file, kerX, kerY, threshold, aligned);
			//Save Surface MAP
			File f = new File(dataDir+"surface.smap");
			
			//This is to fix a problem where directory of surface.smap was moved a
			File fother = new File(dir+"surface.smap");
			FileOperations.ensureDirStruct(fother);
			CrossCorrelationDataset.saveMIPRawData(surfaceMap, f);
			for (int pos = posStart; pos < posEnd; pos++)
			{
				SurfaceFinderTool
						.getYProjectionSlice(flow, surfaceMap, sliceFlow, pos, pos
								+ projectionSize, projectType);
				save.addData(CrossCorrelationDataset.getMIPData(sliceFlow, 0, 255, ColorMap.getColorMap(ColorMap.TYPE_GRAY)), new File(dir+"image"+StringOperations.getNumberString(4, pos)+".png"));
				

			}
			System.out.println("Finished : " + (i+1)+" of "+dataHolder.size());
		}
		System.out.println("Done");
		
		
	}
	
	public static String getDataFolder(File srcIMG, int kerX, int kerY, int threshold, boolean aligned)
	{
		String[] data = FileOperations.splitFile(srcIMG);
		// Create Output Location
		String savePath = data[0] + "\\" + data[1] + "\\" + (2 * kerX + 1)
				+ "-" + (2 * kerY + 1) + "-" + threshold + " - "
				+ (aligned ? " " : "Not ") + "Aligned"
				+ "\\";
		
		return savePath;
	}
	
	public static String getFlowFolder(File srcIMG, int kerX, int kerY, int threshold, boolean aligned)
	{
		String[] data = FileOperations.splitFile(srcIMG);
		// Create Output Location
		String savePath = data[0] + "\\" + data[1] + "\\" + (2 * kerX + 1)
				+ "-" + (2 * kerY + 1) + "-" + threshold + " - "
				+ (aligned ? " " : "Not ") + "Aligned"
				+ "\\flow flat\\";
		
		return savePath;
	}
	public static void doProjectionWithSurface() throws Exception
	{
		int startPos = 0;
		int endPos = 255;

		int avgNum = 5;
		int offset = 0;
		
		
		
		byte[][][] data = VolumeInputSelectorPanel.getUserVolumeData(null);
		@SuppressWarnings("deprecation")
		float[][] map = SurfaceFinderTool.loadSurfaceMap(FileSelectionField
				.getUserFile());

		float[][] slice = new float[data[0].length][data.length];

		ImagePanel img = new ImagePanel();
		FrameFactroy.getFrame(img.getInPanel());
		
		FileSelectionField saveFolder = new FileSelectionField();
		saveFolder.setFormat(FileSelectionField.FORMAT_FOLDERS);
		saveFolder.setTitle("Please Select an output folder");
		saveFolder.getUserChoice();

		String[] parts = FileOperations.splitFile(saveFolder.getFile());
		ImageFileSaver save = new ImageFileSaver(8, 10);
		
		
		for (int pos = startPos; pos < endPos; pos++)
		{
			SurfaceFinderTool
					.getYProjectionSlice(data, map, slice, pos-offset, pos+avgNum, SurfaceFinderTool.TYPE_AVERAGE);
			
			//ArrayToolkit.smoothData(slice, sliceSmooth, 5, 5);
			img.setImage(ImageOperations.getImage(slice, 0, 255, ColorMap.getColorMap(ColorMap.TYPE_GRAY)));
			save.addData(img.getImage(), new File(parts[0]+"\\image"+StringOperations.getNumberString(5, pos-offset)+".png"));
		}
	}
	
	
}
