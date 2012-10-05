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
package com.joey.software.interfaces;


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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsFRGImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsImageProducer;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.nativelibs4java.opencl.CLBuildException;

public class CrossCorrProgram extends JPanel
{
	boolean userInterfaceLocked = false;

	CrossCorellationToolNew crossCorrTool = new CrossCorellationToolNew(this);

	String kerSizeData[] = new String[]
	{ "3", "5", "7", "9", "11", "13", "15", "17", "19", "21", "23", "25", "27",
			"29", "30" };

	JComboBox corKerSizeX = new JComboBox(kerSizeData);

	JComboBox corKerSizeY = new JComboBox(kerSizeData);

	JComboBox processingMode = new JComboBox(new String[]
	{ "GPU", "CPU" });

	JSpinner corFrameNum = new JSpinner(new SpinnerNumberModel(1, 0,
			Integer.MAX_VALUE, 1));

	JSpinner backgroundThreshold = new JSpinner(new SpinnerNumberModel(0, 0,
			Integer.MAX_VALUE, 1));

	JButton loadDataButton = new JButton("Load");

	JTextField inputDataType = new JTextField();

	JTextField inputDataSizeX = new JTextField();

	JTextField inputDataSizeY = new JTextField();

	JTextField inputDataSizeZ = new JTextField();

	int sizeX = 0;

	int sizeY = 0;

	int sizeZ = 0;

	ImagePanel structralPanel = new ImagePanel();

	ImagePanel flowPanel = new ImagePanel();

	JTabbedPane viewTabs = new JTabbedPane();

	JButton showMerged = new JButton("Get Merged Image");

	JButton updatePanel = new JButton("Update Data");

	JButton processVolume = new JButton("Run");

	JSpinner currentFrame = new JSpinner(new SpinnerNumberModel(1, 0,
			Integer.MAX_VALUE, 1));

	JSpinner flowMin = new JSpinner(new SpinnerNumberModel(0.6,
			-Double.MAX_VALUE, Double.MAX_VALUE, 0.1));

	JSpinner flowMax = new JSpinner(new SpinnerNumberModel(-0.6,
			-Double.MAX_VALUE, Double.MAX_VALUE, 0.1));

	ImageProducer inputData = null;

	File file[];

	StatusBarPanel status = new StatusBarPanel();

	JFrame ownerFrame = null;

	ColorMap flowMap = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	JButton setMap = new JButton("Set Map");

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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}// TODO Auto-generated method stub

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
					data[pos + x * img.getHeight() + y] = (byte)ImageOperations
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
		int labelSize = 80;
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
		toolPanel.add(SwingToolkit
				.getLabel(loadDataButton, "Input Data : ", labelSize));
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel
				.add(SwingToolkit.getLabel(setMap, "Input Type : ", labelSize));
		toolPanel.add(SwingToolkit
				.getLabel(inputDataType, "Input Type : ", labelSize));
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

		JPanel tmp = SwingToolkit
				.getLabel(corFrameNum, "Frame Num : ", labelSize);
		tmp
				.setToolTipText("This is the number of frame to determine the cross corellation across");
		toolPanel.add(tmp);
		toolPanel.add(Box.createVerticalStrut(compoentGap));

		// Background threshold for OpenCL processing
		tmp = SwingToolkit
				.getLabel(backgroundThreshold, "Background : ", labelSize);
		tmp
				.setToolTipText("This is the background value to take, Cross corellation will only be calculated for regions with an average value above this");
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
		// Processing Mode (GPU/CPU)
		toolPanel.add(SwingToolkit
				.getLabel(processingMode, "Processing : ", labelSize));
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		toolPanel.add(new JSeparator());
		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit
				.getLabel(currentFrame, "Current Frame : ", labelSize);
		tmp
				.setToolTipText("Current frame to display, frame is updated when this is updated");
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit.getLabel(updatePanel, "Update Frame: ", labelSize);
		tmp.setToolTipText("This will do cross corr for current frame");
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit.getLabel(showMerged, "Sow Merged : ", labelSize);
		tmp
				.setToolTipText("This will merge the src frame and the current displayed flow frame (max, min, avg) if no frame is open it will ask");
		toolPanel.add(tmp);

		toolPanel.add(Box.createVerticalStrut(compoentGap));
		tmp = SwingToolkit
				.getLabel(processVolume, "Process volume: ", labelSize);
		tmp
				.setToolTipText("This will merge the src frame and the current displayed flow frame (max, min, avg) if no frame is open it will ask");
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

		split.setLeftComponent(leftPanel);
		split.setRightComponent(rightPanel);
		split.setDividerLocation(250);
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		updatePanel.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					crossCorrTool.doSingleFrame();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CLBuildException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
							crossCorrTool.doFullVolume();
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
		});

	}

	public void loadDataPressed() throws IOException
	{
		final CrossCorrProgram pro = this;
		Thread t = new Thread(new Runnable(){

			@Override
			public void run()
			{
				InputSelectorPanel inputSelector = new InputSelectorPanel();
				try
				{
					inputSelector.getUserInput(pro);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}});
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
	}

	
	public static void main(String input[]) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		
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

		GregorianCalendar end = new GregorianCalendar(2010, Calendar.NOVEMBER,
				25);

		if (today.after(end))
		{
			JOptionPane
					.showMessageDialog(null, "Error opening clscript. File is corupt", "Error", JOptionPane.ERROR_MESSAGE);
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

	public int getCorFrameNum()
	{
		return (Integer) corFrameNum.getValue();
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
		return true;
	}

	public boolean getSaveFlow()
	{
		return true;
	}

	public void lockOutUser(boolean lock)
	{
		userInterfaceLocked = lock;
		loadDataButton.setEnabled(!lock);
		corKerSizeX.setEnabled(!lock);
		corKerSizeY.setEnabled(!lock);
		corFrameNum.setEnabled(!lock);
		backgroundThreshold.setEnabled(!lock);
		flowMin.setEnabled(!lock);
		flowMax.setEnabled(!lock);
		processingMode.setEnabled(!lock);
		currentFrame.setEnabled(!lock);
		updatePanel.setEnabled(!lock);
		showMerged.setEnabled(!lock);
		processVolume.setEnabled(!lock);
	}
}
