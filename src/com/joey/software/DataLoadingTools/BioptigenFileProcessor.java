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
package com.joey.software.DataLoadingTools;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


public class BioptigenFileProcessor
{
	/** Define constants * */
	String directory, name, octFile;

	String batchFileName = "";

	String currentSlice = "";

	String description = "";

	String xCaption = "";

	String yCaption = "";

	String key = "";

	String config = "";

	int headerLen = 0;

	int frameCount = 0;

	int lineCount = 0;

	int lineLength = 0;

	int sampleFormat = 0;

	int dataLength = 0;

	int headerDataLen = 0;

	int keyLen = 0;

	int magic = 0;

	int skipLength = 0;

	int dopplerFlag = 0;

	int frameDataLength = 0;

	int scanType = 0;

	int framesPerVolume = 0;

	int subFrames = 0;

	int subFrameLines = 0;

	int subFrameOffsets = 0;

	int subFrameRadii = 0;

	int version = 0;

	int headerFlag = 0;

	int frameFlag = 0;

	int currentFrame = 1;

	int frameLines = 0;

	int sliceCount = 0;

	int framesPerScan = 0;

	int scans = 0;

	int frames = 0;

	double xScalePixels = 0;

	double yScalePixels = 0;

	double xScaleMax = 0;

	double yScaleMax = 0;

	double xMin = 0;

	double xMax = 0;

	double yMin = 0;

	double yMax = 0;

	double objectDistance = 0;

	double scanLength = 0;

	double azScanLength = 0;

	double elScanLength = 0;

	double scanDepth = 0;

	double scanAngle = 0;

	byte[] frameDateTime = new byte[32];

	byte[] byteData = new byte[32];

	short pixel;

	String statusString = ""; // Extra status info passed to loadOctFile(...)

	// batch processing constants
	String sBatchText = "0";

	int iBatchCount = 0;

	int iCurrentBatch = 0;

	int nameLength = 0;

	String batchFilename = "";

	String rootFilename = "";

	// debugging constants
	int frameDateTimeFlag = 0;

	int frameTimeStampFlag = 0;

	int frameLinesFlag = 0;

	int frameSamplesFlag = 0;

	int dopplerSamplesFlag = 0;

	// frame key names
	String strFrameHeader = "FRAMEHEADER";

	String strFrameCount = "FRAMECOUNT";

	String strLineCount = "LINECOUNT";

	String strLineLength = "LINELENGTH";

	String strSampleFormat = "SAMPLEFORMAT";

	String strDescription = "DESCRIPTION";

	String strXMin = "XMIN";

	String strXMax = "XMAX";

	String strXCaption = "XCAPTION";

	String strYMin = "YMIN";

	String strYMax = "YMAX";

	String strYCaption = "YCAPTION";

	String strScanType = "SCANTYPE";

	String strScanDepth = "SCANDEPTH";

	String strScanLength = "SCANLENGTH";

	String strAzScanLength = "AZSCANLENGTH";

	String strElScanLength = "ELSCANLENGTH";

	String strObjectDistance = "OBJECTDISTANCE";

	String strScanAngle = "SCANANGLE";

	String strFramesPerVolume = "FRAMESPERVOLUME";

	String strScans = "SCANS"; // v1.7 version of FRAMESPERVOLUME

	String strFrames = "FRAMES"; // v1.7 update to include multiple

	// frames/scan

	String strDopplerFlag = "DOPPLERFLAG";

	String strSubFramesFlag = "SUBFRAMESFLAG";

	String strSubFrames = "SUBFRAMES";

	String strSubFrameLines = "SUBFRAMELINES";

	String strSubFrameOffsets = "SUBFRAMEOFFSETS";

	String strSubFrameRadii = "SUBFRAMERADII";

	String strConfig = "CONFIG"; // "Obfuscated ASCII String"

	// frame header keynames
	String strFrameData = "FRAMEDATA";

	String strFrameDateTime = "FRAMEDATETIME";

	String strFrameTimeStamp = "FRAMETIMESTAMP";

	String strFrameLines = "FRAMELINES";

	String strFrameSamples = "FRAMESAMPLES";

	String strDopplerSamples = "DOPPLERSAMPLES";

	// oct reader information
	String strOctReaderVersion = "1.0";

	String strInVivoVueVersion = "1.7";

	// GUI for processing selection
	/** Constants * */
	boolean bCrop = false;

	boolean bSVP = false;

	boolean bRegister = false;

	boolean bMontage = false;

	boolean bScaleBar = false;

	boolean bHeader = true;

	boolean bWolfMontage = false;

	boolean bBatch = true;

	public void outputImageProcessorImage(ImageProcessor processor, String format, String fileName)
			throws IOException
	{
		File f = new File(fileName);
		if (!f.exists())
		{
			f.mkdirs();
			f.createNewFile();
		}
		Image oImg = processor.createImage();
		BufferedImage bImg = ImageOperations.toBufferedImage(oImg);
		ImageIO.write(bImg, format, new File(fileName));
	}

	public void loadFile(BioptigenSettingsPanel settings)
	{

		String octFile = settings.getInputFile();
		String outputFolder = settings.getImageOutputFolder();
		String fileBase = settings.getImageOutputName();
		int numberDigits = settings.getImageOutputDigits();

		loadFile(octFile, outputFolder, fileBase, numberDigits, settings
				.getInitialIndex(), settings.isOutputHeader(), new StatusBarPanel());
	}

	public String loadFile(String octFile) throws IOException
	{
		String result = "";
		// ProgressMonitor prog = new ProgressMonitor(null, "Loading OCT File :
		// "+octFile, "Reading Header File", 0, 10 );

		int count = 0;

		FileInputStream inputFile = new FileInputStream(octFile);

		BufferedInputStream inputData = new BufferedInputStream(inputFile);
		// Magic Number
		inputData.read(byteData, 0, 4);
		magic = byteToInt(byteData);

		// Software Version
		inputData.read(byteData, 0, 2);
		version = byteToShort(byteData);

		inputData.read(byteData, 0, 4);
		keyLen = byteToInt(byteData);
		inputData.read(byteData, 0, keyLen);
		key = new String(byteData);
		key = key.trim();
		inputData.read(byteData, 0, 4);

		if (!key.equals(strFrameHeader))
		{
			JOptionPane
					.showMessageDialog(null, "Error Loading File", "There was an error with the file header", JOptionPane.ERROR_MESSAGE);
		}

		headerFlag = 0;
		while (headerFlag == 0)
		{

			byteData = new byte[32]; // Clear array
			inputData.read(byteData, 0, 4);
			keyLen = byteToInt(byteData);
			inputData.read(byteData, 0, keyLen);
			key = new String(byteData);
			key = key.trim();

			// Check format for if/else if and string compare in Java
			if (key.equals(strFrameCount))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				frameCount = byteToInt(byteData);
			} else if (key.equals(strLineCount))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				lineCount = byteToInt(byteData);
			} else if (key.equals(strLineLength))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				lineLength = byteToInt(byteData);
			} else if (key.equals(strSampleFormat))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				sampleFormat = byteToInt(byteData);
			} else if (key.equals(strDescription))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				description = new String(byteData);
				description = description.trim();
			} else if (key.equals(strXMin))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				xMin = byteToDouble(byteData);
			} else if (key.equals(strXMax))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				xMax = byteToDouble(byteData);
			} else if (key.equals(strXCaption))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				xCaption = new String(byteData);
				xCaption = xCaption.trim();
			} else if (key.equals(strYMin))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				yMin = byteToDouble(byteData);
			} else if (key.equals(strYMax))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				yMax = byteToDouble(byteData);
			} else if (key.equals(strYCaption))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				yCaption = new String(byteData);
				yCaption = yCaption.trim();
			} else if (key.equals(strScanType))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				scanType = byteToInt(byteData);
			} else if (key.equals(strScanDepth))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				scanDepth = byteToDouble(byteData);
			} else if (key.equals(strScanLength))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				scanLength = byteToDouble(byteData);
			} else if (key.equals(strAzScanLength))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				azScanLength = byteToDouble(byteData);
			} else if (key.equals(strElScanLength))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				elScanLength = byteToDouble(byteData);
			} else if (key.equals(strObjectDistance))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				objectDistance = byteToDouble(byteData);
			} else if (key.equals(strScanAngle))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				scanAngle = byteToDouble(byteData);
			} else if (key.equals(strFramesPerVolume))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				framesPerVolume = byteToInt(byteData);
			} else if (key.equals(strScans))
			{ // v1.7 and later
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				scans = byteToInt(byteData);
			} else if (key.equals(strFrames))
			{ // v1.7 and later
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				frames = byteToInt(byteData);
			} else if (key.equals(strDopplerFlag))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				dopplerFlag = byteToInt(byteData);
			} else if (key.equals(strConfig))
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				config = new String(byteData);
				config = config.trim();
			} else if (key.equals(strSubFrames) && version == 105)
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				subFrames = byteToInt(byteData);
			} else if (key.equals(strSubFrameLines) && version == 105)
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, 4);
				subFrameLines = byteToInt(byteData);
			} else if (key.equals(strSubFrameOffsets) && version == 105)
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				subFrameOffsets = byteToInt(byteData);
			} else if (key.equals(strSubFrameRadii) && version == 105)
			{
				inputData.read(byteData, 0, 4);
				dataLength = byteToInt(byteData);
				inputData.read(byteData, 0, dataLength);
				subFrameRadii = byteToInt(byteData);
			} else
			{
				headerFlag = 1;
			}
		}
		return result;
	}

	public void loadFile(String octFile, String outputFolder, String fileBase, int numberDigits, int startIndex, boolean outputHeader, StatusBarPanel status)
	{

		// ProgressMonitor prog = new ProgressMonitor(null, "Loading OCT File :
		// "+octFile, "Reading Header File", 0, 10 );

		status.setStatusMessage("Reading Header");
		status.setMaximum(1);
		status.setValue(0);
		if (!outputFolder.endsWith("\\"))
		{
			outputFolder = outputFolder + "\\";
		}
		int count = 0;
		try
		{
			FileInputStream inputFile = new FileInputStream(octFile);

			BufferedInputStream inputData = new BufferedInputStream(inputFile);
			// Magic Number
			inputData.read(byteData, 0, 4);
			magic = byteToInt(byteData);

			// Software Version
			inputData.read(byteData, 0, 2);
			version = byteToShort(byteData);

			inputData.read(byteData, 0, 4);
			keyLen = byteToInt(byteData);
			inputData.read(byteData, 0, keyLen);
			key = new String(byteData);
			key = key.trim();
			inputData.read(byteData, 0, 4);

			if (!key.equals(strFrameHeader))
			{
				JOptionPane
						.showMessageDialog(null, "Error Loading File", "There was an error with the file header", JOptionPane.ERROR_MESSAGE);
			}

			headerFlag = 0;
			while (headerFlag == 0)
			{
				byteData = new byte[32]; // Clear array
				inputData.read(byteData, 0, 4);
				keyLen = byteToInt(byteData);
				inputData.read(byteData, 0, keyLen);
				key = new String(byteData);
				key = key.trim();

				// Check format for if/else if and string compare in Java
				if (key.equals(strFrameCount))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					frameCount = byteToInt(byteData);
				} else if (key.equals(strLineCount))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					lineCount = byteToInt(byteData);
				} else if (key.equals(strLineLength))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					lineLength = byteToInt(byteData);
				} else if (key.equals(strSampleFormat))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					sampleFormat = byteToInt(byteData);
				} else if (key.equals(strDescription))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					description = new String(byteData);
					description = description.trim();
				} else if (key.equals(strXMin))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					xMin = byteToDouble(byteData);
				} else if (key.equals(strXMax))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					xMax = byteToDouble(byteData);
				} else if (key.equals(strXCaption))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					xCaption = new String(byteData);
					xCaption = xCaption.trim();
				} else if (key.equals(strYMin))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					yMin = byteToDouble(byteData);
				} else if (key.equals(strYMax))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					yMax = byteToDouble(byteData);
				} else if (key.equals(strYCaption))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					yCaption = new String(byteData);
					yCaption = yCaption.trim();
				} else if (key.equals(strScanType))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					scanType = byteToInt(byteData);
				} else if (key.equals(strScanDepth))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					scanDepth = byteToDouble(byteData);
				} else if (key.equals(strScanLength))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					scanLength = byteToDouble(byteData);
				} else if (key.equals(strAzScanLength))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					azScanLength = byteToDouble(byteData);
				} else if (key.equals(strElScanLength))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					elScanLength = byteToDouble(byteData);
				} else if (key.equals(strObjectDistance))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					objectDistance = byteToDouble(byteData);
				} else if (key.equals(strScanAngle))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					scanAngle = byteToDouble(byteData);
				} else if (key.equals(strFramesPerVolume))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					framesPerVolume = byteToInt(byteData);
				} else if (key.equals(strScans))
				{ // v1.7 and later
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					scans = byteToInt(byteData);
				} else if (key.equals(strFrames))
				{ // v1.7 and later
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					frames = byteToInt(byteData);
				} else if (key.equals(strDopplerFlag))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					dopplerFlag = byteToInt(byteData);
				} else if (key.equals(strConfig))
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					config = new String(byteData);
					config = config.trim();
				} else if (key.equals(strSubFrames) && version == 105)
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					subFrames = byteToInt(byteData);
				} else if (key.equals(strSubFrameLines) && version == 105)
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, 4);
					subFrameLines = byteToInt(byteData);
				} else if (key.equals(strSubFrameOffsets) && version == 105)
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					subFrameOffsets = byteToInt(byteData);
				} else if (key.equals(strSubFrameRadii) && version == 105)
				{
					inputData.read(byteData, 0, 4);
					dataLength = byteToInt(byteData);
					inputData.read(byteData, 0, dataLength);
					subFrameRadii = byteToInt(byteData);
				} else
				{
					headerFlag = 1;
				}
			}

			status.setStatusMessage("Reading Header Finished");
			status.setMaximum(frameCount + 1);

			// prog.setNote("Reading Header File Complete");
			// prog.setMaximum(frameCount+1);
			// try
			// {
			// Thread.sleep(1000);
			// } catch (InterruptedException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			status.setStatusMessage("Processing Images...");

			ImagePlus impIntensity = NewImage
					.createByteImage("OCT Intensity Image", lineLength, lineCount, 1, NewImage.FILL_BLACK);
			ImageProcessor ipIntensity = impIntensity.getProcessor();

			// Initialize Doppler Data
			ImagePlus impDoppler = NewImage
					.createByteImage("OCT Doppler Image", lineLength, lineCount, 1, NewImage.FILL_BLACK);
			ImageProcessor ipDoppler = impDoppler.getProcessor();

			// frameDateTime information
			short[] frameYear = new short[frameCount];
			short[] frameMonth = new short[frameCount];
			short[] frameDayOfWeek = new short[frameCount];
			short[] frameDay = new short[frameCount];
			short[] frameHour = new short[frameCount];
			short[] frameMinute = new short[frameCount];
			short[] frameSecond = new short[frameCount];
			short[] frameMillisecond = new short[frameCount];
			int[] frameDifference = new int[frameCount];
			int frameDuration = 0; // time difference between frames in
			// milliseconds

			while (currentFrame <= frameCount)// && !prog.isCanceled())
			{ // lower value of frameCount for debugging purposes
				switch (currentFrame % 5)
				{
					case 0:
						status.setStatusMessage("Processing Images.");
						break;
					case 1:
						status.setStatusMessage("Processing Images..");
						break;
					case 2:
						status.setStatusMessage("Processing Images...");
						break;
					case 3:
						status.setStatusMessage("Processing Images....");
						break;
					case 4:
						status.setStatusMessage("Processing Images.....");
						break;
				}
				status.setValue(currentFrame);

				frameFlag = 0; // reset frameFlag for next frame read
				impIntensity.setSlice(1);
				impDoppler.setSlice(1);
				sliceCount = currentFrame - 1;
				currentSlice = Integer.toString(sliceCount);

				byteData = new byte[32]; // Clear array
				inputData.read(byteData, 0, 4);
				keyLen = byteToInt(byteData);
				byteData = new byte[32]; // Clear array
				inputData.read(byteData, 0, keyLen);
				key = new String(byteData);
				key = key.trim();
				byteData = new byte[32]; // Clear array
				inputData.read(byteData, 0, 4);

				if (key.equals(strFrameData))
				{
					while (frameFlag == 0)
					{
						byteData = new byte[32]; // Clear array
						inputData.read(byteData, 0, 4);
						keyLen = byteToInt(byteData);
						inputData.read(byteData, 0, keyLen);
						key = new String(byteData);
						key = key.trim();

						if (key.equals(strFrameDateTime))
						{

							frameDateTimeFlag = 1;
							byteData = new byte[32];
							inputData.read(byteData, 0, 4);
							dataLength = byteToInt(byteData);
							short frameValue;

							for (int i = 0; i < 8; i++)
							{
								byte[] shortData = new byte[2];
								inputData.read(shortData);

								byte tmp = shortData[0];
								shortData[0] = shortData[1];
								shortData[1] = tmp;

								frameValue = BinaryToolkit
										.readShort(shortData, 0);

								switch (i)
								{
									case 0:
										frameYear[sliceCount] = frameValue;
										break;
									case 1:
										frameMonth[sliceCount] = frameValue;
										break;
									case 2:
										frameDayOfWeek[sliceCount] = frameValue;
										break;
									case 3:
										frameDay[sliceCount] = frameValue;
										break;
									case 4:
										frameHour[sliceCount] = frameValue;
										break;
									case 5:
										frameMinute[sliceCount] = frameValue;
										break;
									case 6:
										frameSecond[sliceCount] = frameValue;
										break;
									case 7:
										frameMillisecond[sliceCount] = frameValue;
										break;
								} // switch
							} // for 8 WORDS in SYSTEMTIME structure
							inputData.skip(dataLength - 16);
						} // if key.equals(strFrameDateTime)
						else if (key.equals(strFrameTimeStamp))
						{
							frameTimeStampFlag = 1;
							byteData = new byte[32]; // Clear array
							inputData.read(byteData, 0, 4);
							dataLength = byteToInt(byteData);
							inputData.skip(dataLength);
						} // if key.equals(strFrameTimeStamp)
						else if (key.equals(strFrameLines))
						{
							frameLinesFlag = 1;
							byteData = new byte[32]; // Clear array
							inputData.read(byteData, 0, 4);
							dataLength = byteToInt(byteData);
							byteData = new byte[32]; // Clear array
							inputData.read(byteData, 0, 4);
							frameLines = byteToInt(byteData);
						} // if key.equals(strFrameLines)
						else if (key.equals(strFrameSamples))
						{
							frameSamplesFlag = 1;
							byteData = new byte[32]; // Clear array
							inputData.read(byteData, 0, 4);
							dataLength = byteToInt(byteData);
							// inputData.skip(dataLength); // read frame
							// data
							for (int j = 0; j < lineCount; j++)
							{
								for (int k = 0; k < lineLength; k++)
								{
									inputData.read(byteData, 0, 2);
									pixel = byteToShort(byteData);
									ipIntensity.putPixel(k, j, pixel);
								} // for k = ... lineLength
								// inputData.skip(2*(lineLength/2));
							} // for j = ... lineCount
							// stackIntensity
							// .addSlice(currentSlice, ipIntensity
							// .rotateLeft(), sliceCount);
						} // if key.equals(strFrameSamples)
						else if (key.equals(strDopplerSamples))
						{
							dopplerSamplesFlag = 1;
							byteData = new byte[32]; // Clear array
							inputData.read(byteData, 0, 4);
							dataLength = byteToInt(byteData);
							// inputData.skip(dataLength); // read Doppler
							// data
							for (int j = 0; j < lineCount; j++)
							{
								for (int k = 0; k < lineLength; k++)
								{
									inputData.read(byteData, 1, 2);
									pixel = byteToShort(byteData);
									ipDoppler.putPixel(k, j, pixel);
								}
								// inputData.skip(2*(lineLength/2));
							}

							// outputImageProcessorImage(ipDoppler, "jpg",
							// fileName);
						} // if key.equals(strDopplerSamples)
						else
						{
							frameFlag = 1;
						} // else for key.equals() for per-frame data
					} // while frameFlag == 0
				} // if key.equals(strFrameData)

				/**
				 * Output Data
				 */
				String fileName = outputFolder
						+ fileBase
						+ StringOperations
								.getNumberString(numberDigits, currentFrame
										+ startIndex) + ".jpg";
				outputImageProcessorImage(ipIntensity, "jpg", fileName);

				currentFrame++;
				if (sliceCount > 0)
				{
					if (frameMillisecond[sliceCount] < frameMillisecond[sliceCount - 1])
					{
						frameDifference[sliceCount - 1] = 1000 - frameMillisecond[sliceCount - 1];
					} // if millisecond value rolls over into seconds
					else
					{
						frameDifference[sliceCount - 1] = frameMillisecond[sliceCount]
								- frameMillisecond[sliceCount - 1];
					}
				} // if currentFrame > 2, assumes frame duration is under
				// 1 s
				if (currentFrame > frameCount)
				{
					int temp = 0;
					for (int i = 0; i < frameCount; i++)
					{
						temp += frameDifference[i];
					} // sum of differences
					frameDuration = temp / (frameCount - 1);

				} // frame duration calculated from last 2 frames
			} // while currentFrame < frameCount

			// Close references
			inputData.close(); // close data stream
			inputFile.close(); // close input file stream

			/**
			 * for (int i = 0; i < frameCount; i++) { System.out
			 * .println(getDateStampString(frameYear[i], frameMonth[i],
			 * frameDayOfWeek[i], frameDay[i], frameHour[i], frameMinute[i],
			 * frameSecond[i], frameMillisecond[i])); }
			 */
			if (outputHeader)
			{
				System.out.println("Outputting header");
				status.setStatusMessage("Creating Header File...");
				status.setValue(0);

				File headerOut = new File(outputFolder + "header.csv");
				PrintWriter out = new PrintWriter(headerOut);
				out.printf("Frame Count,%d\n", frameCount);
				out.printf("Line Count,%d\n", lineCount);
				out.printf("Line Length,%d\n", lineLength);
				out.printf("Scan Depth,%f\n", scanDepth);
				out.printf("Scan Width,%f\n", azScanLength);
				out.printf("Scan Length,%f\n", elScanLength);

				out
						.printf("Year,month,day,hour, minute, second, millisecond\n");
				for (int i = 0; i < frameYear.length; i++)
				{
					status.setValue(i);
					// out.printf("%d,%d,%d,%d,%d,%d,%d\n", frameYear[i],
					// frameMonth[i],
					// frameDay[i], frameHour[i], frameMinute[i],
					// frameSecond[i],
					// frameMillisecond[i]);
					out
							.println(getDateStampString(frameYear[i], frameMonth[i], frameDayOfWeek[i], frameDay[i], frameHour[i], frameMinute[i], frameSecond[i], frameMillisecond[i]));
				}

				out.close();
				status.setStatusMessage("Finished");
			}
		} catch (IOException e)
		{
			JOptionPane
					.showMessageDialog(null, "Error", "IO Error", JOptionPane.ERROR_MESSAGE);
		}
		status.setStatusMessage("Finished");

	} // Oct function definition

	public String getDateStampString(short year, short month, short dayWeek, short day, short hour, short minute, short second, short millisecond)
	{
		StringBuilder out = new StringBuilder();

		out.append(year);
		out.append(",");
		out.append(month);
		out.append(",");
		out.append(day);
		out.append(",");
		out.append(hour);
		out.append(",");
		out.append(minute);
		out.append(",");
		out.append(second);
		out.append(",");
		out.append(millisecond);

		return out.toString();
	}

	// Supplementary function definitions
	// byteToDouble uses the same algorithm as below for byteToInt
	public double byteToDouble(byte[] bytes)
	{

		long val = ((0xff & bytes[0]) | (0xff & bytes[1]) << 8
				| (0xff & bytes[2]) << 16 | (long) (0xff & bytes[3]) << 24
				| (long) (0xff & bytes[4]) << 32
				| (long) (0xff & bytes[5]) << 40
				| (long) (0xff & bytes[6]) << 48 | (long) (0xff & bytes[7]) << 56);
		return Double.longBitsToDouble(val);
	}

	public int byteToInt(byte[] byteArray)
	{
		byte b1 = byteArray[0];
		byte b2 = byteArray[1];
		byte b3 = byteArray[2];
		byte b4 = byteArray[3];

		return (((b4 & 0xff) << 24) | ((b3 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b1 & 0xff));
	}

	public short byteToShort(byte[] byteArray)
	{
		byte b1 = byteArray[0];
		byte b2 = byteArray[1];

		return ((short) ((b1 << 8 & 0xff) | (b2 & 0xff)));
	}

	public static void main(String input[]) throws Exception
	{
		BioptigenFileProcessor tst = new BioptigenFileProcessor();
		File f = FileSelectionField.getUserFile();
		StatusBarPanel status = new StatusBarPanel();
		FrameFactroy.getFrame(status);
		tst.loadFile(f.toString(), "C:\\text\\", "test", 3, 1, true, status);

	}

	public static void something() throws Exception
	{
		// UIManager.setLoo1kAndFeel(UIManager.getSystemLookAndFeelClassName());

		final JFileChooser c = new JFileChooser();

		final StatusBarPanel status = new StatusBarPanel();
		final JTextArea currentNumber = new JTextArea();
		currentNumber.setEditable(false);

		JScrollPane scroll = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setViewportView(currentNumber);

		final JButton runButton = new JButton("Run Export");

		class Action implements ActionListener, Runnable
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(this);
				t.start();
			}

			@Override
			public void run()
			{

				runButton.setEnabled(false);
				// TODO Auto-generated method stub

				try
				{
					c.setMultiSelectionEnabled(true);
					c.showOpenDialog(null);
					File[] files = c.getSelectedFiles();
					for (int i = 0; i < files.length; i++)
					{
						BioptigenFileProcessor proc = new BioptigenFileProcessor();
						String octFile = files[i].toString();
						String outputFolder = octFile.substring(0, octFile
								.length() - 4);
						String fileBase = "image";
						int numberDigits = 5;

						currentNumber
								.append(String
										.format("Loading [ %3d of %3d ]", i, files.length));
						long start = System.currentTimeMillis();
						proc
								.loadFile(octFile, outputFolder, fileBase, numberDigits, 0, true, status);
						long time = System.currentTimeMillis() - start;
						currentNumber.append(String
								.format("Done. it took %f ms\n", (double) time
										/ (double) 1000));
					}
				} catch (Exception e)
				{
					currentNumber.append("Error : " + e);
				}
				runButton.setEnabled(true);
			}

		}
		runButton.addActionListener(new Action());

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(currentNumber, BorderLayout.CENTER);
		mainPanel.add(status, BorderLayout.SOUTH);
		mainPanel.add(runButton, BorderLayout.NORTH);

		JFrame f = FrameFactroy.getFrame(mainPanel);
	}

	public static void dmain(String input[]) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		BioptigenSettingsPanel settings = new BioptigenSettingsPanel();
		BioptigenFileProcessor processor = new BioptigenFileProcessor();
		int val = JOptionPane
				.showConfirmDialog(null, settings, "Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (val == JOptionPane.OK_OPTION)
		{
			processor.loadFile(settings);
		}
	}
}
