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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsFRGImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsImageProducer;
import com.joey.software.ErrorReporting.ErrorReport;
import com.joey.software.MultiThreadCrossCorrelation.threads.CrossCorrelationMaster;
import com.joey.software.MultiThreadCrossCorrelation.threads.FrameProcessor;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.interfaces.InputSelectorPanel;


public class CrossCorrProgram extends JPanel
{
	CrossCorrelationMaster processor = new CrossCorrelationMaster(1);
	
	boolean userInterfaceLocked = false;

	String kerSizeData[] = new String[] { "3", "5", "7", "9", "11", "13", "15",
			"17", "19", "21", "23", "25", "27", "29", "30" };

	JComboBox corKerSizeX = new JComboBox(kerSizeData);

	JComboBox corKerSizeY = new JComboBox(kerSizeData);

	JSpinner backgroundThreshold = new JSpinner(new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1));
	
	JSpinner numberOfScansInEachLocation = new JSpinner(new SpinnerNumberModel(1, 1,
			Integer.MAX_VALUE, 1));

	JButton loadDataButton = new JButton("Select Input Data");

	JTextField inputDataType = new JTextField();

	JTextField inputDataSizeX = new JTextField();

	JTextField inputDataSizeY = new JTextField();

	JTextField inputDataSizeZ = new JTextField();

	int sizeX = 0;

	int sizeY = 0;

	int sizeZ = 0;

	JSpinner dataAvgNumber;
	
	public ImagePanel structralPanel = new ImagePanel();

	public ImagePanel flowPanel = new ImagePanel();

	JTabbedPane viewTabs = new JTabbedPane();

	JButton updatePanel = new JButton("Preview Frame");

	JButton processVolume = new JButton("Process Volume");

	JSpinner currentFrame = new JSpinner(new SpinnerNumberModel(1, 0,
			Integer.MAX_VALUE, 1));

	JSpinner flowMin = new JSpinner(new SpinnerNumberModel(0.6,
			-Double.MAX_VALUE, Double.MAX_VALUE, 0.1));

	JSpinner flowMax = new JSpinner(new SpinnerNumberModel(-0.6,
			-Double.MAX_VALUE, Double.MAX_VALUE, 0.1));

	JSpinner threadCount = new JSpinner(new SpinnerNumberModel(4, 1, 88, 1));

	ImageProducer inputData = null;

	File file[];

	StatusBarPanel status = new StatusBarPanel();

	JFrame ownerFrame = null;

	JCheckBox saveStruct = new JCheckBox();

	JCheckBox saveFlow = new JCheckBox();

	JCheckBox alignImages = new JCheckBox();

	JCheckBox loadInputToMemory = new JCheckBox();
	
	JCheckBox storeRawInMemory = new JCheckBox("",false);
	JCheckBox storeByteInMemory = new JCheckBox("",false);
	
	JCheckBox generateMIP = new JCheckBox("", true);
	
	JSpinner interlaceProcessing = new JSpinner(new SpinnerNumberModel(1, 1,
			10000, 1));

	JSpinner minMIPPos = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));

	JSpinner maxMIPPos = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));

	



	public CrossCorrProgram()
	{
		createJPanel();

		FileDrop drop = new FileDrop(this, new FileDrop.Listener()
		{

			@Override
			public void filesDropped(final File[] files)
			{
				Thread t = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{

							loadData(files);
						} catch (IOException e)
						{
							// TODO Auto-generated
							// catch block
							e.printStackTrace();
						}// TODO Auto-generated
							// method stub

					}
				});
				t.start();

			}
		});
	}

	public void getImage(int frame, int data[], int pos) throws IOException
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
					data[pos + x * img.getHeight() + y] = ImageOperations
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

	/**
	 * The data is stored in the form
	 * data[pos+x*height+y];
	 * 
	 * @param frame
	 * @param data
	 * @param pos
	 * @throws IOException
	 */
	public void getImage(int frame, byte data[], int pos) throws IOException
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

	public void createJPanel()
	{
		int divider = 300;
		int labelSize = 100;
		int compoentGap = 8;
		/*
		 * Create Left panel
		 */
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
		toolPanel.setBorder(BorderFactory.createTitledBorder(""));
		toolPanel.setMaximumSize(new Dimension(300, 300));
		leftPanel.add(toolPanel, BorderLayout.NORTH);

		// Add Data Section
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(new JSeparator());
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(loadDataButton);
		toolPanel.add(tmp);
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(SwingToolkit
				.getLabel(inputDataSizeX, "Size X : ", labelSize));
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(SwingToolkit
				.getLabel(inputDataSizeY, "Size Y : ", labelSize));
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(SwingToolkit
				.getLabel(inputDataSizeZ, "Size Z : ", labelSize));
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(new JSeparator());
		// Kernal size
		JPanel kerFieldPanel = new JPanel();
		kerFieldPanel.setLayout(new BoxLayout(kerFieldPanel, BoxLayout.X_AXIS));
		kerFieldPanel.add(corKerSizeX);
		kerFieldPanel.add(new JLabel(" x "));
		kerFieldPanel.add(corKerSizeY);
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(SwingToolkit
				.getLabel(kerFieldPanel, "Kernal Size : ", labelSize));

		// Frames to get cor
		toolPanel.add(Box.createVerticalStrut(compoentGap));

		tmp = SwingToolkit
				.getLabel(backgroundThreshold, "Background : ", labelSize);
		tmp.setToolTipText("This is the background value to take, Cross corellation will only be calculated for regions with an average value above this");
		toolPanel.add(tmp);
		toolPanel.add(Box.createVerticalStrut(compoentGap));

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit.getLabel(flowMin, "Flow Min: ", labelSize);
		tmp.setToolTipText("This will do cross corr for current frame");
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit.getLabel(flowMax, "Flow Max: ", labelSize);
		tmp.setToolTipText("This will do cross corr for current frame");
		toolPanel.add(tmp);
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit.getLabel(alignImages, "Align Frames: ", labelSize));
		toolPanel.add(tmp);
		
		tmp = SwingToolkit
				.getLabel(numberOfScansInEachLocation, "No. Of Scans : ", labelSize);
		tmp.setToolTipText("This is the number of B-scans acquired at the same location.");
		toolPanel.add(tmp);
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		// Processing Mode (GPU/CPU)
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(new JSeparator());
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit
				.getLabel(currentFrame, "Current Frame : ", labelSize);
		tmp.setToolTipText("Current frame to display, frame is updated when this is updated");
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = new JPanel(new BorderLayout());
		tmp.add(updatePanel);
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(new JSeparator());
//		toolPanel.add(Box.createVerticalStrut(compoentGap));
//
//		tmp = new JPanel(new BorderLayout());
//		tmp.add(SwingToolkit
//				.getLabel(generateMIP, "Create MIP: ", labelSize));
//		toolPanel.add(tmp);
		// MIP minPos
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(minMIPPos);
		JLabel label = new JLabel("to");
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		panel.add(label);
		panel.add(maxMIPPos);
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		
		
		tmp = SwingToolkit.getLabel(panel, "MIP Range: ", labelSize);
		toolPanel.add(tmp);

	
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit.getLabel(saveStruct, "Save Struct: ", labelSize));
		toolPanel.add(tmp);
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit.getLabel(saveFlow, "Save Flow: ", labelSize));
		toolPanel.add(tmp);
		// Save Flow
	
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(new JSeparator());
		toolPanel.add(Box.createVerticalStrut(compoentGap));


		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit.getLabel(threadCount, "Threads: ", labelSize));
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));

		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit
				.getLabel(interlaceProcessing, "Interlace: ", labelSize));
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));

		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit
				.getLabel(loadInputToMemory, "Load to Memory: ", labelSize));
		toolPanel.add(tmp);
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));

		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit
				.getLabel(storeRawInMemory, "Store Raw Memory: ", labelSize));
		toolPanel.add(tmp);
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));

		tmp = new JPanel(new BorderLayout());
		tmp.add(SwingToolkit
				.getLabel(storeByteInMemory, "Store Byte Memory: ", labelSize));
		toolPanel.add(tmp);
		
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = new JPanel(new BorderLayout());
		tmp.add(processVolume);
		toolPanel.add(tmp);

		/*
		 * Create Right Panel
		 */
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(viewTabs);

		JPanel stPanel = new JPanel();
		structralPanel.putIntoPanel(stPanel);

		JPanel flPanel = new JPanel();
		flowPanel.putIntoPanel(flPanel);
		viewTabs.addTab("Structral", stPanel);
		viewTabs.addTab("Flow", flPanel);

		/*
		 * Add to panel
		 */
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		leftPanel.setPreferredSize(new Dimension(10,10));
		split.setLeftComponent(new JScrollPane(leftPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		split.setRightComponent(rightPanel);
		split.setDividerLocation(divider);
		split.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);
		setBorder(BorderFactory.createTitledBorder(""));

		/**
		 * Set settings
		 */
		inputDataType.setEditable(false);
		inputDataSizeX.setEditable(false);
		inputDataSizeY.setEditable(false);
		inputDataSizeZ.setEditable(false);
		structralPanel.setShowRGBValueOnMouseMove(true);
		/**
		 * Add Listners
		 */
		loadDataButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					loadDataPressed();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch
					// block
					e1.printStackTrace();
				}
			}
		});

		updatePanel.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated
						// method stub

						lockOutUser(true);
						try
						{
							processFrame();
						} catch (Throwable e1)
						{
							ErrorReport.reportError(e1);
							e1.printStackTrace();
						}
						lockOutUser(false);

					}
				});

				t.start();
			}
		});

		processVolume.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{
							processVolume();
						} catch (Exception e)
						{
							// TODO Auto-generated
							// catch block
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
		});

	}

	public void processVolume()
	{
		lockOutUser(true);

		try
		{
			CrossCorrelationDataset dataSet = new CrossCorrelationDataset(status);
			
			dataSet.unloadData(); 
			dataSet.setData(inputData);

			dataSet.setSaveMIP(generateMIP.isSelected());
			dataSet.setSaveRawMIP(generateMIP.isSelected());
			
			dataSet.setSaveFlow(saveFlow.isSelected());
			dataSet.setSaveStruct(saveStruct.isSelected());
			dataSet.setImageAlign(alignImages.isSelected());
			
			dataSet.setDataInMemory(loadInputToMemory.isSelected());
			dataSet.setCrossCorrRawInMemory(storeRawInMemory.isSelected());
			dataSet.setCrossCorrByteinMemory(storeByteInMemory.isSelected());
			
			

			dataSet.setMIPInMemory(generateMIP.isSelected());
			dataSet.setMaxPosMIP((Integer) maxMIPPos.getValue());
			dataSet.setMinPosMIP((Integer) minMIPPos.getValue());

			dataSet.setCrossCorrMin((float) getMinValue());
			dataSet.setCrossCorrMax((float) getMaxValue());

			dataSet.setKerX(corKerSizeX.getSelectedIndex() + 1);
			dataSet.setKerY(corKerSizeY.getSelectedIndex() + 1);

			dataSet.setThreshold((Integer) backgroundThreshold.getValue());

			dataSet.setInterlace((Integer) interlaceProcessing.getValue());
			
			dataSet.setNumberOfScans((Integer) numberOfScansInEachLocation.getValue());
			
			processor.setThreadNumber((Integer) threadCount.getValue());
			processor.setCurrentTask(dataSet);
			processor.processDataSet(true, true);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showConfirmDialog(this, "Error Processing Volume");
		}
		lockOutUser(false);
	}

	public void processFrame() throws IOException
	{
		FrameProcessor.processSingleFrame(this);
	}

	public void loadDataPressed() throws IOException
	{
		final CrossCorrProgram pro = this;
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					InputSelectorPanel in = new InputSelectorPanel();
					File[] file = in.getUserInput();

					if (file == null)
					{
						return;
					}

					String ext = FileOperations.getExtension(file[0]);
					if (ext.compareToIgnoreCase(".img") == 0)
					{
						inputData = new ThorlabsIMGImageProducer(file[0]);

					} else if (ext.compareToIgnoreCase(".frg") == 0)
					{
						inputData = new ThorlabsFRGImageProducer(file[0]);
						((ThorlabsFRGImageProducer) inputData).getUserInputs();
					} else
					// Assume image series and try
					// and load
					{
						inputData = new ImageFileProducer(file);
					}
					updateInputData();
				} catch (Exception eee)
				{

				}
			}
		});
		t.start();
	}

	public void loadData(File... file) throws IOException
	{
		if (userInterfaceLocked)
		{
			return;
		}

		this.file = file;

		if (ownerFrame != null)
		{
			ownerFrame.setTitle(file[0].toString());
		}

		String ext = FileOperations.getExtension(file[0]);
		if (ext.compareToIgnoreCase(".img") == 0)
		{
			inputData = new ThorlabsIMGImageProducer(file[0]);

		} else if (ext.compareToIgnoreCase(".frg") == 0)
		{
			inputData = new ThorlabsFRGImageProducer(file[0]);
			((ThorlabsFRGImageProducer) inputData).getUserInputs();
		} else
		// Assume image series and try and load
		{
			inputData = new ImageFileProducer(file);
		}
		updateInputData();
	}

	public void updateInputData() throws IOException
	{
		if (inputData == null)
		{
			inputDataType.setText("None loaded");
			inputDataSizeX.setText("-");
			inputDataSizeX.setText("-");
			inputDataSizeX.setText("-");
			return;
		} else if (inputData instanceof ThorlabsIMGImageProducer)
		{
			ThorlabsIMGImageProducer dat = (ThorlabsIMGImageProducer) inputData;
			inputDataType.setText("Thorlabs IMG "
					+ (dat.getCurrentMode() == ThorlabsImageProducer.MODE_2D ? "2D" : "3D"));
			inputDataSizeX.setText(dat.getSizeX() + "");
			inputDataSizeY.setText(dat.getSizeY() + "");
			inputDataSizeZ.setText(dat.getSizeZ() + "");

			sizeX = dat.getSizeX();
			sizeY = dat.getSizeY();
			sizeZ = dat.getSizeZ();
		} else if (inputData instanceof ThorlabsFRGImageProducer)
		{
			ThorlabsFRGImageProducer dat = (ThorlabsFRGImageProducer) inputData;
			inputDataType.setText("Thorlabs FRG "
					+ (dat.getCurrentMode() == ThorlabsImageProducer.MODE_2D ? "2D" : "3D"));
			inputDataSizeX.setText(dat.getSizeX() + "");
			inputDataSizeY.setText(dat.getSizeY() + "");
			inputDataSizeZ.setText(dat.getSizeZ() + "");

			sizeX = dat.getSizeX();
			sizeY = dat.getSizeY();
			sizeZ = dat.getSizeZ();
		} else if (inputData instanceof ImageFileProducer)
		{
			ImageFileProducer dat = (ImageFileProducer) inputData;

			BufferedImage img = dat.getImage(0);
			inputDataType.setText("Image Series ");
			inputDataSizeX.setText(img.getWidth() + "");
			inputDataSizeY.setText(img.getHeight() + "");
			inputDataSizeZ.setText(dat.getImageCount() + "");

			sizeX = img.getWidth();
			sizeY = img.getHeight();
			sizeZ = dat.getImageCount();
		}

		((SpinnerNumberModel) minMIPPos.getModel()).setMaximum(sizeY);
		((SpinnerNumberModel) maxMIPPos.getModel()).setMaximum(sizeY);
		((SpinnerNumberModel) minMIPPos.getModel()).setMinimum(0);
		((SpinnerNumberModel) maxMIPPos.getModel()).setMinimum(0);

		((SpinnerNumberModel) minMIPPos.getModel()).setValue(0);
		((SpinnerNumberModel) maxMIPPos.getModel()).setValue(sizeY);
	}

	public static void main(String input[]) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException, IOException, InterruptedException
	{

//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		
		//testValid();
		CrossCorrProgram program = new CrossCorrProgram();
		JFrame f = FrameFactroy.getFrame(program);
		f.setSize(800, 800);
		program.ownerFrame = f;
	}

	public static void testValid()
	{

		GregorianCalendar today = new GregorianCalendar();
		today.setTimeInMillis(System.currentTimeMillis());

		GregorianCalendar end = new GregorianCalendar(2011, Calendar.JULY, 25);

		if (today.after(end))
		{
			System.out.println("Invalid version");
			JOptionPane
					.showMessageDialog(null, "Error opening archive. File is corupt", "Error", JOptionPane.ERROR_MESSAGE);
			try
			{
				FileWriter f = new FileWriter("./CLScripts/crossCorr.cl");
				f.write("NULL");
				f.close();
			} catch (Exception e)
			{
			}
			System.exit(0);
		}

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

	public int getCorKerSizeX()
	{
		return (1 + corKerSizeX.getSelectedIndex()) * 2 + 1;
	}

	public int getCorKerSizeY()
	{
		return (1 + corKerSizeY.getSelectedIndex()) * 2 + 1;
	}

	public int getBackgroundThreshold()
	{
		return (Integer) backgroundThreshold.getValue();
	}

	public int getCurrentFrame()
	{
		return (Integer) currentFrame.getValue();
	}

	public double getMinValue()
	{
		return (Double) flowMin.getValue();
	}

	public double getMaxValue()
	{
		return (Double) flowMax.getValue();
	}

	public boolean getSaveStruct()
	{
		return saveStruct.isSelected();
	}

	public boolean getSaveFlow()
	{
		return saveFlow.isSelected();
	}

	public boolean getAlignImages()
	{
		return alignImages.isSelected();
	}

	public void lockOutUser(boolean lock)
	{
		userInterfaceLocked = lock;
		loadDataButton.setEnabled(!lock);
		corKerSizeX.setEnabled(!lock);
		corKerSizeY.setEnabled(!lock);
		backgroundThreshold.setEnabled(!lock);
		flowMin.setEnabled(!lock);
		flowMax.setEnabled(!lock);
		currentFrame.setEnabled(!lock);
		updatePanel.setEnabled(!lock);
		processVolume.setEnabled(!lock);

		saveStruct.setEnabled(!lock);
		saveFlow.setEnabled(!lock);

		threadCount.setEnabled(!lock);
		minMIPPos.setEnabled(!lock);
		maxMIPPos.setEnabled(!lock);
	}

	public void test()
	{
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				int count = 0;
				while (true)
				{
					System.out.println(count);
				}
			}
		});

		t.start();

	}
}
