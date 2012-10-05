package com.joey.software.DataLoadingTools;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.joey.software.imageToolkit.ImageOperations;

public class BioptigenFileProcessor_PreSpeedUpdate
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

	boolean bHeader = false;

	boolean bWolfMontage = false;

	boolean bBatch = true;

	public void outputImageProcessorImage(ImageProcessor processor, String format, String fileName)
			throws IOException
	{
		Image oImg = processor.createImage();
		BufferedImage bImg = ImageOperations.toBufferedImage(oImg);
		ImageIO.write(bImg, format, new File(fileName));
	}

	public void loadFile(String octFile)
	{
		String folderBase = "c:\\test\\";
		String fileBase = "imageOut";
		int count = 0;
		try
		{
			FileInputStream inputFile = new FileInputStream(octFile);
			// DataInputStream inputData = new DataInputStream(inputFile);

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

					for (int i = 0; i < dataLength; i++)
					{
						Byte b = byteData[i];

						System.out
								.printf("byte[%d] = %d : \"%x\"\n", i, byteData[i], byteData[i]);
					}
					System.out.println("xMin : " + xMax);
					System.out.println("\n\n\n");
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

			// Dialog Box to Indicate Header Values
			// For Debugging Purposes
			if (bHeader)
			{ // only show if want header information only
				GenericDialog gd1 = new GenericDialog("Header Information");
				// gd1.addMessage("Magic Number: " + magic);
				// gd1.addMessage("Version Number: " + version);
				// gd1.addMessage("Key Value: " + key);
				// gd1.addMessage("Key Length: " + keyLen);
				gd1.addMessage("Target Key: " + strFrameCount);
				// gd1.addMessage("File Header Length: " + headerDataLen);
				gd1.addMessage("Frame Count: " + frameCount);
				gd1.addMessage("Line Count: " + lineCount);
				gd1.addMessage("Line Length: " + lineLength);
				// gd1.addMessage("Sample Format: " + sampleFormat);
				gd1.addMessage("Description: " + description);
				gd1.addMessage("Scan Type: " + scanType);
				gd1.addMessage("XMin: " + xMin);
				gd1.addMessage("XMax: " + xMax);
				gd1.addMessage("X Caption: " + xCaption);
				gd1.addMessage("YMin: " + yMin);
				gd1.addMessage("YMax: " + yMax);
				gd1.addMessage("Y Caption: " + yCaption);
				gd1.addMessage("Scan Depth: " + scanDepth);
				gd1.addMessage("Scan Length: " + Double.toString(scanLength));
				gd1.addMessage("Az Scan Length: "
						+ Double.toString(azScanLength));
				gd1.addMessage("El Scan Length: "
						+ Double.toString(elScanLength));
				gd1.addMessage("Scan Angle: " + scanAngle);
				gd1.addMessage("Doppler Flag: " + dopplerFlag);
				gd1.showDialog();
				if (gd1.wasCanceled())
					return;
			}

			if (!bHeader)
			{ // don't show if only want frame header information
				// Initialize Intensity Data

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

				while (currentFrame <= frameCount)
				{ // lower value of frameCount for debugging purposes
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
								Integer frameValueInt;
								short frameValueTemp;
								short frameValue;
								for (int i = 0; i < 8; i++)
								{

									// frameValueTemp = inputData.readShort();
									byte[] sData = new byte[2];
									byte[] shortData = new byte[]
									{ sData[1], sData[0] };
									inputData.read(shortData);
									frameValueTemp = byteToShort(shortData);

									frameValue = Short
											.reverseBytes(frameValueTemp);
									switch (i)
									{
										case 0:
											frameYear[sliceCount] = frameValue;
										case 1:
											frameMonth[sliceCount] = frameValue;
										case 2:
											frameDayOfWeek[sliceCount] = frameValue;
										case 3:
											frameDay[sliceCount] = frameValue;
										case 4:
											frameHour[sliceCount] = frameValue;
										case 5:
											frameMinute[sliceCount] = frameValue;
										case 6:
											frameSecond[sliceCount] = frameValue;
										case 7:
											frameMillisecond[sliceCount] = frameValue;
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

								String fileName = folderBase + fileBase + count
										+ ".jpg";
								outputImageProcessorImage(ipIntensity, "jpg", fileName);
								System.out.println("File : " + currentFrame
										+ " of " + frameCount);

								count++;
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
								String fileName = folderBase + fileBase + count
										+ "_D.jpg";
								outputImageProcessorImage(ipDoppler, "jpg", fileName);
							} // if key.equals(strDopplerSamples)
							else
							{
								frameFlag = 1;
							} // else for key.equals() for per-frame data
						} // while frameFlag == 0
					} // if key.equals(strFrameData)
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
						// GenericDialog gdTemp = new GenericDialog("debug");
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[0]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[1]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[2]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[3]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[4]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[5]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[6]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[7]);
						// gdTemp.addMessage("Frame Difference (ms): " +
						// frameDifference[8]);
						// gdTemp.addMessage("Frame Duration (ms): " +
						// frameDuration);
						// gdTemp.showDialog();
						// if (gdTemp.wasCanceled() ) { return; }
					} // frame duration calculated from last 2 frames
				} // while currentFrame < frameCount

				// Close references
				inputData.close(); // close data stream
				inputFile.close(); // close input file stream
				// impIntensity.close();
				// impDoppler.close();
				// currentFrame = 1; // reset currentFrame

				for (int i = 0; i < frameCount; i++)
				{
					System.out.println("Year :" + frameYear);
					System.out.println("Month : " + frameMonth);
					System.out.println("Day of Week: " + frameDayOfWeek);
					System.out.println("Day : " + frameDay);
					System.out.println("Hour : " + frameHour);
					System.out.println("Minute : " + frameMinute);
					System.out.println("Second :" + frameSecond);
					System.out.println("Millisecond : " + frameMillisecond);
					System.out.println("Difference : " + frameDifference);
				}
				// debug for frame date-time information
				// GenericDialog gd2 = new GenericDialog("FDT Info");
				// gd2.addMessage("Frame year = " + frameYear);
				// gd2.addMessage("Frame month = " + frameMonth);
				// gd2.addMessage("Frame day of week = " + frameDayOfWeek);
				// gd2.addMessage("Frame day = " + frameDay);
				// gd2.addMessage("Frame hour = " + frameHour);
				// gd2.addMessage("Frame minute = " + frameMinute);
				// gd2.addMessage("Frame second = " + frameSecond);
				// gd2.addMessage("Frame millisecond = " + frameMillisecond);
				// gd2.showDialog();
				// if (gd2.wasCanceled()) return;

			} // if (!bHeaderOnly)
		} catch (IOException e)
		{
			JOptionPane
					.showMessageDialog(null, "Error", "IO Error", JOptionPane.ERROR_MESSAGE);
		}
	} // Oct function definition

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
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		BioptigenFileProcessor_PreSpeedUpdate proc = new BioptigenFileProcessor_PreSpeedUpdate();
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(null);

		proc.loadFile(chooser.getSelectedFile().toString());

	}
}
