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
package com.joey.software.userinterface;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.DataToolkit.DrgRawImageProducer;
import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.DataToolkit.OCTDataGeneratingTool;
import com.joey.software.DataToolkit.RochesterDataLoader;
import com.joey.software.DataToolkit.ThorLabs2DFRGImageProducer;
import com.joey.software.DataToolkit.ThorLabs3DImageProducer;
import com.joey.software.DataToolkit.TiffDataSet;
import com.joey.software.DataToolkit.TiffLoader;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;


/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class OCTDataCreatorPanel extends JFrame
{
	int labelSize = 200;

	ImageProducer imageData;

	private JTextField previewOutZField;

	private JTextField previewOutYField;

	private JTextField previewOutXField;

	private JLabel previewOutputSizeLabel;

	private JTextField previewInputZField;

	private JTextField previewInputYField;

	private JTextField previewInputXField;

	private JLabel previewInputLabel;

	private JLabel scaleLabel;

	private JSlider scaleSizeSlider;

	private JLabel previewCheckLabel;

	// private JCheckBox createRawDataCheck;
	// private JCheckBox createPreviewCheck;
	private JLabel rawDataCheckLabel;

	private JPanel previewDataPanel;

	private JPanel rawDataPanel;

	File rawDataFile;

	File previewFile;

	File infoFile;

	int panelIndex = 0;

	JButton nextButton = new JButton("Next");

	JButton prevButton = new JButton("Prev");

	JButton cancelButton = new JButton("Cancel");

	JButton confirmButton = new JButton("Confirm");

	private JPanel outputPanel = new JPanel();

	StatusBarPanel status = new StatusBarPanel();

	private JScrollPane outputPanelScroll = new JScrollPane(
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

	private JPanel controlPanel = new JPanel();

	JPanel mainPanel = new JPanel();

	JPanel inputDataPanel = new JPanel(new BorderLayout());

	ImageFileSelectorPanel fileInputPanel = new ImageFileSelectorPanel();

	FileSelectionField thorLabsIMGFileField = new FileSelectionField();

	FileSelectionField thorLabsFRGFileField = new FileSelectionField();

	FileSelectionField rawOutputField = new FileSelectionField();

	FileSelectionField prvOutputField = new FileSelectionField();

	FileSelectionField tiffInputField = new FileSelectionField();

	FileSelectionField drgRawInputField = new FileSelectionField();

	{
		tiffInputField.setExtensions(new String[]
		{ "tif:Tiff Image Stack (*.tif)" }, true, true);

		rawOutputField.setExtensions(new String[]
		{ "drgraw:Raw Data File (.drgraw)" }, true, true);
		prvOutputField.setExtensions(new String[]
		{ "drgprv:Prv Data File (.drgprv)" }, true, true);

		drgRawInputField.setExtensions(new String[]
		{ "drgraw:Raw Data File (.drgraw)" }, true, true);
	}

	private JPanel jPanel2;

	private JTextField memoryField;

	private JLabel ramLabel;

	private JPanel jPanel1;

	JFileChooser rochesterChooser = new JFileChooser();

	JButton showRochester = new JButton("Set Files");

	File lastImageLoader = new File("");

	JTabbedPane inputTab = new JTabbedPane(SwingConstants.TOP);

	boolean lastWorked = false;

	static boolean RESEARCH = true;

	public OCTDataCreatorPanel()
	{
		setSize(680, 480);
		setMinimumSize(new Dimension(300, 240));
		createJPanel();
		setPanelIndex(0);
		updateData();
		setTitle("Data Import Tool");
	}

	public void setInputData(TiffDataSet dataSet)
	{
		File tiff = dataSet.getTiffFile();
		File raw = FileOperations.renameFileType(tiff, "drgraw");
		File prv = FileOperations.renameFileType(tiff, "drgprv");

		tiffInputField.setFieldText(tiff.toString());
		rawOutputField.setFieldText(raw.toString());
		prvOutputField.setFieldText(prv.toString());

		inputTab.setSelectedIndex(1);
		nextPressed();
	}

	public void confirmPressed()
	{

		final ImageProducer files = imageData;
		final double scale = getScaleSizeSlider().getValue() / 100.;
		final File previewFile = prvOutputField.getFile();
		final File rawFile;

		/*
		 * This section of code was added to allow the user to create different
		 * sized preview datasets.
		 */
		if (files instanceof DrgRawImageProducer)
		{
			rawFile = null;
		} else
		{
			rawFile = rawOutputField.getFile();
		}
		final JFrame owner = this;

		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				if (previewFile.exists())
				{
					if (JOptionPane
							.showConfirmDialog(null, "The file already exist, Overwrite?", "Confirm Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION)
					{
						return;
					}
				}

				if (rawFile != null)
				{
					if (rawFile.exists())
					{
						if (JOptionPane
								.showConfirmDialog(null, "The file already exist, Overwrite?", "Confirm Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION)
						{
							return;
						}
					}
				}
				cancelButton.setEnabled(false);
				nextButton.setEnabled(false);
				prevButton.setEnabled(false);
				owner.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

				try
				{
					OCTDataGeneratingTool
							.createDataFiles(rawFile, previewFile, files, scale, status);
				} catch (Exception e)
				{
					JOptionPane
							.showMessageDialog(owner, "Error : "
									+ e.getLocalizedMessage(), "Error Creating Data Files", JOptionPane.ERROR_MESSAGE);
				}

				cancelButton.setEnabled(true);
				nextButton.setEnabled(true);
				prevButton.setEnabled(true);

				lastWorked = true;
				owner.setVisible(false);
				owner.dispose();
			}
		};
		t.start();

	}

	public boolean getLastWorked()
	{
		return lastWorked;
	}

	public void resetLastWorked()
	{
		lastWorked = false;
	}

	public NativeDataSet getLastDataSet() throws IOException
	{
		if (inputTab.getSelectedIndex() == 2)
		{
			File raw = drgRawInputField.getFile();
			File prv = prvOutputField.getFile();

			NativeDataSet dataSet = new NativeDataSet(raw, prv);
			dataSet.unloadData();
			return dataSet;
		} else
		{
			File raw = rawOutputField.getFile();
			File prv = prvOutputField.getFile();

			NativeDataSet dataSet = new NativeDataSet(raw, prv);
			dataSet.unloadData();
			return dataSet;
		}
	}

	@Override
	public void setVisible(boolean b)
	{
		if (b == true)
		{
			resetLastWorked();
		}

		super.setVisible(b);
	}

	public void setPanelIndex(final int index)
	{
		Thread thr = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				nextButton.setText("Next");
				if (index < 0 || index > 2)
				{
					return;
				}
				panelIndex = index;
				if (index == 0)
				{
					mainPanel.removeAll();
					mainPanel.add(inputDataPanel, BorderLayout.CENTER);
				} else if (index == 1)
				{
					if (inputTab.getSelectedIndex() == 0)
					{
						if (fileInputPanel.getFiles().length < 1)
						{
							JOptionPane
									.showMessageDialog(null, "No Images Selected", "Error", JOptionPane.ERROR_MESSAGE);
							panelIndex = 0;
							return;
						} else
						{
							try
							{
								imageData = new ImageFileProducer(
										fileInputPanel.getFiles());
							} catch (Exception e)
							{
								JOptionPane
										.showMessageDialog(null, "Error With Images", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					} else if (inputTab.getSelectedIndex() == 1)
					{
						try
						{
							imageData = new TiffLoader(tiffInputField.getFile());
						} catch (Exception e)
						{
							JOptionPane
									.showMessageDialog(null, "Error Loading File"
											+ e, "Error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					} else if (inputTab.getSelectedIndex() == 2)
					{

						try
						{
							imageData = new DrgRawImageProducer(
									drgRawInputField.getFile());
						} catch (Exception e)
						{
							JOptionPane
									.showMessageDialog(null, "Error Loading File"
											+ e, "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else if (inputTab.getSelectedIndex() == 3)
					{

						try
						{
							imageData = new ThorLabs3DImageProducer(
									thorLabsIMGFileField.getFile());
						} catch (Exception e)
						{
							JOptionPane
									.showMessageDialog(null, "Error Loading File"
											+ e, "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else if (inputTab.getSelectedIndex() == 4)
					{

						try
						{
							imageData = new ThorLabs2DFRGImageProducer(
									thorLabsFRGFileField.getFile());

							((ThorLabs2DFRGImageProducer) imageData)
									.getUserInputs();
						} catch (Exception e)
						{
							JOptionPane
									.showMessageDialog(null, "Error Loading File"
											+ e, "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else if (inputTab.getSelectedIndex() == 5)
					{
						try
						{
							imageData = new RochesterDataLoader(
									rochesterChooser.getSelectedFiles());

							((RochesterDataLoader) imageData).getUserInputs();
						} catch (Exception e)
						{
							JOptionPane
									.showMessageDialog(null, "Error Loading File"
											+ e, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					mainPanel.removeAll();
					mainPanel.add(outputPanelScroll, BorderLayout.CENTER);
					nextButton.setText("Finish");
				} else if (index == 2)
				{
					nextButton.setText("Finished");
					if (JOptionPane
							.showConfirmDialog(null, "Are you happy with the settings?", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
					{
						confirmPressed();
					} else
					{
						prevPressed();
					}

				}
				updateData();
				validateTree();
				repaint();
			}
		});

		thr.start();
	}

	public void updateData()
	{
		int xSize = 0;
		int ySize = 0;
		int zSize = 0;

		try
		{
			outputPanel.removeAll();
			if (imageData instanceof DrgRawImageProducer)
			{
				outputPanel.setLayout(new BorderLayout());
				outputPanel.setPreferredSize(new java.awt.Dimension(400, 240));
				// outputPanel.add(getRawDataPanel(), BorderLayout.NORTH);
				outputPanel.add(getPreviewDataPanel(), BorderLayout.CENTER);
			} else
			{
				outputPanel.setLayout(new BorderLayout());
				outputPanel.setPreferredSize(new java.awt.Dimension(400, 240));
				outputPanel.add(getRawDataPanel(), BorderLayout.NORTH);
				outputPanel.add(getPreviewDataPanel(), BorderLayout.CENTER);
			}

			BufferedImage img;

			if (imageData == null)
			{
				img = ImageOperations.getBi(1);
				zSize = 1;
			} else
			{
				img = imageData.getImage(0);
				zSize = imageData.getImageCount();
			}

			xSize = img.getWidth();
			ySize = img.getHeight();

			getPreviewInputXField().setText(xSize + "");
			getPreviewInputYField().setText(ySize + "");
			getPreviewInputZField().setText(zSize + "");

			//	
			//
			// xSize = Integer.parseInt(getPreviewInputXField().getText());
			// ySize = Integer.parseInt(getPreviewInputYField().getText());
			// zSize = Integer.parseInt(getPreviewInputZField().getText());

			double scale = getScaleSizeSlider().getValue() / 100.;
			int pXSize = (int) (xSize * scale);
			int pYSize = (int) (ySize * scale);
			int pZSize = (int) (zSize * scale);

			getPreviewOutXField().setText(pXSize + "");
			getPreviewOutYField().setText(pYSize + "");
			getPreviewOutZField().setText(pZSize + "");

			float size = pXSize * pYSize * pZSize / 1024.f / 1024.f;

			NumberFormat formatter = new DecimalFormat("#0.00");

			getMemoryField().setText(formatter.format(size) + " Mb");
		} catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public void nextPressed()
	{
		setPanelIndex(getPanelIndex() + 1);
	}

	public void prevPressed()
	{
		setPanelIndex(getPanelIndex() - 1);
	}

	public void cancelPressed()
	{
		this.setVisible(false);

	}

	public int getPanelIndex()
	{
		return panelIndex;
	}

	public static void main(String input[]) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		OCTDataCreatorPanel data = new OCTDataCreatorPanel();

		data.setPanelIndex(0);
		data.setVisible(true);
	}

	private void createJPanel()
	{
		try
		{
			inputDataPanel.setLayout(new BorderLayout());
			inputDataPanel.add(inputTab);

			JPanel holder1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			holder1.add(thorLabsIMGFileField);

			JPanel holder2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			holder2.add(thorLabsFRGFileField);

			JPanel holder3 = new JPanel(new BorderLayout());
			holder3.add(showRochester);

			JPanel holder4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
			holder4.add(drgRawInputField);

			showRochester.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					rochesterChooser.setMultiSelectionEnabled(true);
					rochesterChooser.showOpenDialog(null);
				}
			});

			JPanel tiffHolder = new JPanel(new BorderLayout());
			tiffHolder.add(tiffInputField, BorderLayout.NORTH);
			tiffHolder.setBorder(BorderFactory.createTitledBorder("Tiff File"));

			inputTab.addTab("Image File Series", fileInputPanel);
			inputTab.addTab("Tiff Stack", tiffHolder);
			inputTab.addTab("Drg Raw File", holder4);
			if (RESEARCH)
			{
				inputTab.addTab("Thorlabs IMG", holder1);
				inputTab.addTab("Thorlab FRG", holder2);
				inputTab.addTab("Rochester", holder3);
			}
			JPanel rootPanel = new JPanel(new BorderLayout());
			BorderLayout thisLayout = new BorderLayout();
			rootPanel.setLayout(thisLayout);
			rootPanel.setPreferredSize(new java.awt.Dimension(442, 238));
			rootPanel.add(mainPanel, BorderLayout.CENTER);
			rootPanel.setBorder(BorderFactory.createTitledBorder(""));
			mainPanel.setLayout(new BorderLayout());

			FlowLayout controlPanelLayout = new FlowLayout();
			controlPanelLayout.setAlignment(FlowLayout.RIGHT);
			controlPanel.setLayout(controlPanelLayout);
			// controlPanel.setPreferredSize(new java.awt.Dimension(442, 34));
			controlPanel.setBorder(BorderFactory.createTitledBorder(""));
			controlPanel.add(prevButton);
			controlPanel.add(nextButton);
			controlPanel.add(cancelButton);

			rootPanel.add(controlPanel, BorderLayout.SOUTH);
			{

				outputPanel.setLayout(new BorderLayout());
				outputPanel.setPreferredSize(new java.awt.Dimension(400, 240));
				outputPanel.add(getRawDataPanel(), BorderLayout.NORTH);
				outputPanel.add(getPreviewDataPanel(), BorderLayout.CENTER);

				outputPanelScroll.setViewportView(outputPanel);
			}

			this.getContentPane().add(rootPanel, BorderLayout.CENTER);
			this.getContentPane().add(status, BorderLayout.SOUTH);

			// Add the listners
			prevButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					prevPressed();
				}
			});

			nextButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					nextPressed();

				}
			});

			confirmButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					confirmPressed();

				}
			});

			cancelButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					cancelPressed();

				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private JPanel getRawDataPanel()
	{
		if (rawDataPanel == null)
		{
			rawDataPanel = new JPanel();
			rawDataPanel
					.setBorder(BorderFactory.createTitledBorder("Raw Data"));

			rawDataPanel.setLayout(new BorderLayout());

			JPanel holder = new JPanel(new BorderLayout());
			rawOutputField.setLabelSize(labelSize);
			holder.add(rawOutputField, BorderLayout.NORTH);
			rawDataPanel.add(holder);
			// rawDataPanel.add(getCreateRawDataCheck());
		}
		return rawDataPanel;
	}

	private JPanel getPreviewDataPanel()
	{
		if (previewDataPanel == null)
		{

			previewDataPanel = new JPanel();
			previewDataPanel.setPreferredSize(new java.awt.Dimension(588, 183));
			previewDataPanel.setBorder(BorderFactory
					.createTitledBorder("Preview Data"));
			previewDataPanel.setLayout(new BorderLayout());
			// previewDataPanel.add(getCreatePreviewCheck());

			prvOutputField.setLabelSize(labelSize);
			getPreviewInputLabel()
					.setPreferredSize(new Dimension(labelSize, -1));
			getPreviewOutputSizeLabel().setPreferredSize(new Dimension(
					labelSize, -1));
			getScaleLabel().setPreferredSize(new Dimension(labelSize, -1));

			JPanel inputSizeHolder = new JPanel(new GridLayout(1, 3, 1, 5));
			inputSizeHolder.add(getPreviewInputXField());
			inputSizeHolder.add(getPreviewInputYField());
			inputSizeHolder.add(getPreviewInputZField());
			inputSizeHolder.setMaximumSize(new Dimension(250, -1));
			inputSizeHolder.setPreferredSize(new Dimension(250, -1));

			JPanel inputSizePanel = new JPanel(new BorderLayout());
			inputSizePanel.add(getPreviewInputLabel(), BorderLayout.WEST);
			inputSizePanel.add(inputSizeHolder, BorderLayout.CENTER);
			inputSizePanel.add(new JPanel(), BorderLayout.EAST);

			JPanel scalePanel = new JPanel(new BorderLayout());
			scalePanel.add(getScaleLabel(), BorderLayout.WEST);
			scalePanel.add(getScaleSizeSlider(), BorderLayout.CENTER);

			JPanel outputSizeHolder = new JPanel(new GridLayout(1, 3, 1, 5));
			outputSizeHolder.add(getPreviewOutXField());
			outputSizeHolder.add(getPreviewOutYField());
			outputSizeHolder.add(getPreviewOutZField());
			outputSizeHolder.setMaximumSize(new Dimension(250, -1));
			outputSizeHolder.setPreferredSize(new Dimension(250, -1));

			JPanel outputSizePanel = new JPanel(new BorderLayout());
			outputSizePanel.add(getPreviewOutputSizeLabel(), BorderLayout.WEST);
			outputSizePanel.add(outputSizeHolder, BorderLayout.CENTER);

			JPanel holderInput = new JPanel(new BorderLayout());
			holderInput.add(inputSizePanel, BorderLayout.WEST);

			JPanel holderOutput = new JPanel(new BorderLayout());
			holderOutput.add(outputSizePanel, BorderLayout.WEST);

			JPanel mainHolder = new JPanel(new GridLayout(4, 1, 5, 5));
			GridLayout mainHolderLayout = new GridLayout(5, 1);
			mainHolder.setLayout(mainHolderLayout);
			mainHolder.add(prvOutputField);
			mainHolder.add(holderInput);
			mainHolder.add(scalePanel);
			mainHolder.add(holderOutput);
			mainHolder.add(getJPanel2());

			previewDataPanel.add(mainHolder, BorderLayout.NORTH);
			mainHolderLayout.setRows(5);
			mainHolderLayout.setColumns(1);
			mainHolderLayout.setHgap(5);
			mainHolderLayout.setVgap(5);
		}
		return previewDataPanel;
	}

	private JLabel getRawDataCheckLabel()
	{
		if (rawDataCheckLabel == null)
		{
			rawDataCheckLabel = new JLabel();
			rawDataCheckLabel.setText("Create Raw Data :");
			rawDataCheckLabel.setPreferredSize(new java.awt.Dimension(105, 18));
			rawDataCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			rawDataCheckLabel.setBounds(22, 20, 106, 18);
		}
		return rawDataCheckLabel;
	}

	// private JCheckBox getCreateRawDataCheck()
	// {
	// if (createRawDataCheck == null)
	// {
	// createRawDataCheck = new JCheckBox();
	// createRawDataCheck.setBounds(130, 20, 26, 21);
	// }
	// return createRawDataCheck;
	// }

	// private JCheckBox getCreatePreviewCheck()
	// {
	// if (createPreviewCheck == null)
	// {
	// createPreviewCheck = new JCheckBox();
	// createPreviewCheck.setBounds(133, 22, 20, 20);
	// }
	// return createPreviewCheck;
	// }

	private JLabel getPreviewCheckLabel()
	{
		if (previewCheckLabel == null)
		{
			previewCheckLabel = new JLabel();
			previewCheckLabel.setText("Create Preview File :");
			previewCheckLabel.setPreferredSize(new java.awt.Dimension(110, 12));
			previewCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			previewCheckLabel.setBounds(19, 24, 110, 12);
		}
		return previewCheckLabel;
	}

	private JSlider getScaleSizeSlider()
	{
		if (scaleSizeSlider == null)
		{
			scaleSizeSlider = new JSlider();
			scaleSizeSlider.setRequestFocusEnabled(false);
			scaleSizeSlider.setValue(50);
			scaleSizeSlider.setMaximum(100);
			scaleSizeSlider.setMajorTickSpacing(10);
			scaleSizeSlider.setPaintTicks(true);
			scaleSizeSlider.setMinorTickSpacing(5);
			scaleSizeSlider.setPreferredSize(new java.awt.Dimension(226, 29));
			scaleSizeSlider.setBounds(133, 106, 226, 29);
			scaleSizeSlider.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					updateData();
				}
			});
		}
		return scaleSizeSlider;
	}

	private JLabel getScaleLabel()
	{
		if (scaleLabel == null)
		{
			scaleLabel = new JLabel();
			scaleLabel.setText("Scale : ");
			scaleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			scaleLabel.setBounds(72, 106, 60, 14);
		}
		return scaleLabel;
	}

	private JLabel getPreviewInputLabel()
	{
		if (previewInputLabel == null)
		{
			previewInputLabel = new JLabel();
			previewInputLabel.setText("Input Size : ");
			previewInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			previewInputLabel.setBounds(62, 74, 70, 14);
		}
		return previewInputLabel;
	}

	private JTextField getPreviewInputXField()
	{
		if (previewInputXField == null)
		{
			previewInputXField = new JTextField();
			previewInputXField.setText("0");
			previewInputXField.setBounds(138, 71, 64, 20);
			previewInputXField.setBorder(new LineBorder(new java.awt.Color(171,
					173, 179), 1, false));
			previewInputXField.setEditable(false);
		}
		return previewInputXField;
	}

	private JTextField getPreviewInputYField()
	{
		if (previewInputYField == null)
		{
			previewInputYField = new JTextField();
			previewInputYField.setText("0");
			previewInputYField.setBounds(217, 71, 65, 20);
			previewInputYField.setBorder(new LineBorder(new java.awt.Color(171,
					173, 179), 1, false));
			previewInputYField.setEditable(false);
		}
		return previewInputYField;
	}

	private JTextField getPreviewInputZField()
	{
		if (previewInputZField == null)
		{
			previewInputZField = new JTextField();
			previewInputZField.setText("0");
			previewInputZField.setBounds(292, 71, 66, 20);
			previewInputZField.setBorder(new LineBorder(new java.awt.Color(171,
					173, 179), 1, false));
			previewInputZField.setEditable(false);
		}
		return previewInputZField;
	}

	private JLabel getPreviewOutputSizeLabel()
	{
		if (previewOutputSizeLabel == null)
		{
			previewOutputSizeLabel = new JLabel();
			previewOutputSizeLabel.setText("Output Size : ");
			previewOutputSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			previewOutputSizeLabel.setBounds(65, 147, 67, 14);
		}
		return previewOutputSizeLabel;
	}

	private JTextField getPreviewOutXField()
	{
		if (previewOutXField == null)
		{
			previewOutXField = new JTextField();
			previewOutXField.setText("Out X");
			previewOutXField.setBounds(138, 145, 64, 20);
			previewOutXField.setBorder(new LineBorder(new java.awt.Color(171,
					173, 179), 1, false));
			previewOutXField.setEditable(false);
		}
		return previewOutXField;
	}

	private JTextField getPreviewOutYField()
	{
		if (previewOutYField == null)
		{
			previewOutYField = new JTextField();
			previewOutYField.setText("Out Y");
			previewOutYField.setBounds(217, 145, 65, 20);
			previewOutYField.setBorder(new LineBorder(new java.awt.Color(171,
					173, 179), 1, false));
			previewOutYField.setEditable(false);
		}
		return previewOutYField;
	}

	private JTextField getPreviewOutZField()
	{
		if (previewOutZField == null)
		{
			previewOutZField = new JTextField();
			previewOutZField.setText("out Z");
			previewOutZField.setBounds(292, 145, 66, 20);
			previewOutZField.setBorder(new LineBorder(new java.awt.Color(171,
					173, 179), 1, false));
			previewOutZField.setEditable(false);
		}
		return previewOutZField;
	}

	private JPanel getJPanel1()
	{
		if (jPanel1 == null)
		{
			jPanel1 = new JPanel();
			BorderLayout jPanel1Layout = new BorderLayout();
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.add(getRamLabel(), BorderLayout.WEST);
			jPanel1.add(getMemoryField(), BorderLayout.CENTER);
		}
		return jPanel1;
	}

	private JLabel getRamLabel()
	{
		if (ramLabel == null)
		{
			ramLabel = new JLabel();
			ramLabel.setText("Memory : ");
			ramLabel.setPreferredSize(new java.awt.Dimension(labelSize, 0));
			ramLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return ramLabel;
	}

	private JTextField getMemoryField()
	{
		if (memoryField == null)
		{
			memoryField = new JTextField();
			memoryField.setPreferredSize(new Dimension(100, 0));

		}
		return memoryField;
	}

	private JPanel getJPanel2()
	{
		if (jPanel2 == null)
		{
			jPanel2 = new JPanel();
			BorderLayout jPanel2Layout = new BorderLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setPreferredSize(new java.awt.Dimension(388, 30));
			jPanel2.add(getJPanel1(), BorderLayout.WEST);
		}
		return jPanel2;
	}

}
