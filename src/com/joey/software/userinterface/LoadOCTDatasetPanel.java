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
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.joey.software.DataToolkit.IMGDataSet;
import com.joey.software.DataToolkit.ImageSeriesDataSet;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.DataToolkit.TiffDataSet;
import com.joey.software.Launcher.MainLauncher;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.mainProgram.OCTExperimentData;
import com.joey.software.mathsToolkit.NumberDimension;


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
public class LoadOCTDatasetPanel extends JPanel
{
	FileSelectionField loadRawPanel = new FileSelectionField();

	private JScrollPane jScrollPane1;

	private JPanel jPanel7;

	private JPanel jPanel6;

	private JPanel jPanel5;

	private JPanel jPanel4;

	private JPanel jPanel3;

	private JLabel jLabel2;

	private JLabel jLabel1;

	private NumberDimension xSize;

	private NumberDimension ySize;

	private NumberDimension zSize;

	private JLabel sizeDataXLabel;

	private JPanel jPanel1;

	private JTextArea infomation;

	private JPanel jPanel2;

	FileSelectionField loadPrvPanel = new FileSelectionField();

	FileSelectionField loadTiffPanel = new FileSelectionField();

	FileSelectionField loadIMGPanel = new FileSelectionField();
	
	ImageFileSelectorPanel loadImageSeriesPanel = new ImageFileSelectorPanel();
	
	JTextField dataSetNameField = new JTextField();

	JTabbedPane dataTabPanel = new JTabbedPane();

	public LoadOCTDatasetPanel()
	{
		createJPanel();
	}

	public void createJPanel()
	{

		setPreferredSize(new java.awt.Dimension(800, 600));
		setLayout(null);

		int labelSize = 50;

		// Create Name Panel
		JLabel nameLabel = new JLabel("Dataset Name :");
		nameLabel.setPreferredSize(new Dimension(100, 0));
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JPanel octNamePanel = new JPanel(new BorderLayout());
		octNamePanel.add(nameLabel, BorderLayout.WEST);
		octNamePanel.add(dataSetNameField, BorderLayout.CENTER);

		JPanel detailsPanel = new JPanel(new BorderLayout());
		BorderLayout detailsPanelLayout = new BorderLayout();
		detailsPanel.setLayout(detailsPanelLayout);

		{
			jPanel3 = new JPanel();
			BorderLayout jPanel3Layout = new BorderLayout();
			jPanel3.setLayout(jPanel3Layout);
			detailsPanel.add(jPanel3, BorderLayout.CENTER);
			{
				jPanel1 = new JPanel();
				jPanel3.add(jPanel1, BorderLayout.WEST);
				BorderLayout jPanel1Layout = new BorderLayout();
				jPanel1.setBorder(BorderFactory
						.createTitledBorder("Data Dimensions"));
				jPanel1.setLayout(jPanel1Layout);
				jPanel1.setPreferredSize(new java.awt.Dimension(250, 191));
				{
					jPanel7 = new JPanel();
					GridLayout jPanel7Layout = new GridLayout(3, 1);
					jPanel7Layout.setColumns(1);
					jPanel7Layout.setHgap(5);
					jPanel7Layout.setVgap(5);
					jPanel7Layout.setRows(3);
					jPanel7.setLayout(jPanel7Layout);
					jPanel1.add(jPanel7, BorderLayout.NORTH);
					{
						jPanel4 = new JPanel();
						jPanel7.add(jPanel4);
						BorderLayout jPanel4Layout = new BorderLayout();
						jPanel4.setLayout(jPanel4Layout);
						jPanel4
								.setPreferredSize(new java.awt.Dimension(208,
										29));
						{
							sizeDataXLabel = new JLabel();
							jPanel4.add(sizeDataXLabel, BorderLayout.WEST);
							sizeDataXLabel.setText("Dim X :");
							sizeDataXLabel
									.setHorizontalAlignment(SwingConstants.RIGHT);
							sizeDataXLabel
									.setHorizontalTextPosition(SwingConstants.CENTER);
							sizeDataXLabel
									.setPreferredSize(new java.awt.Dimension(
											49, 14));
						}
						{
							xSize = new NumberDimension("m");
							jPanel4.add(xSize, BorderLayout.CENTER);

							xSize.setPreferredSize(new java.awt.Dimension(155,
									20));

						}
					}
					{
						jPanel6 = new JPanel();
						jPanel7.add(jPanel6);
						BorderLayout jPanel6Layout = new BorderLayout();
						jPanel6.setLayout(jPanel6Layout);
						jPanel6
								.setPreferredSize(new java.awt.Dimension(209,
										29));
						{
							ySize = new NumberDimension("m");
							jPanel6.add(ySize, BorderLayout.CENTER);

							ySize.setPreferredSize(new java.awt.Dimension(152,
									20));

						}
						{
							jLabel1 = new JLabel();
							jPanel6.add(jLabel1, BorderLayout.WEST);
							jLabel1.setText("Dim Y :");
							jLabel1
									.setHorizontalAlignment(SwingConstants.RIGHT);
							jLabel1
									.setHorizontalTextPosition(SwingConstants.CENTER);
							jLabel1.setPreferredSize(new java.awt.Dimension(49,
									15));
						}
					}
					{
						jPanel5 = new JPanel();
						jPanel7.add(jPanel5);
						BorderLayout jPanel5Layout = new BorderLayout();
						jPanel5.setLayout(jPanel5Layout);
						jPanel5
								.setPreferredSize(new java.awt.Dimension(216,
										29));
						{
							zSize = new NumberDimension("m");
							jPanel5.add(zSize, BorderLayout.CENTER);

							zSize.setPreferredSize(new java.awt.Dimension(152,
									20));

						}
						{
							jLabel2 = new JLabel();
							jPanel5.add(jLabel2, BorderLayout.WEST);
							jLabel2.setText("Dim Z :");
							jLabel2
									.setHorizontalAlignment(SwingConstants.RIGHT);
							jLabel2
									.setHorizontalTextPosition(SwingConstants.CENTER);
							jLabel2.setPreferredSize(new java.awt.Dimension(58,
									14));
						}
					}
				}
			}
			{
				jPanel2 = new JPanel();
				jPanel3.add(jPanel2, BorderLayout.CENTER);
				BorderLayout jPanel2Layout = new BorderLayout();
				jPanel2.setBorder(BorderFactory
						.createTitledBorder("Information"));
				jPanel2.setLayout(jPanel2Layout);
				{
					jScrollPane1 = new JScrollPane();
					jPanel2.add(jScrollPane1, BorderLayout.CENTER);
					{
						infomation = new JTextArea();
						jScrollPane1.setViewportView(infomation);
						infomation.setText("");
					}
				}
			}
		}
		detailsPanel.add(octNamePanel, BorderLayout.NORTH);

		octNamePanel.setBorder(BorderFactory
				.createTitledBorder("Dataset Title"));
		octNamePanel.setPreferredSize(new java.awt.Dimension(653, 52));

		// Create Native Input Field
		loadRawPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
		loadRawPanel.setExtensions(new String[]
		{ "drgraw:Raw Data File (.drgraw)" }, true, true);

		loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
		loadPrvPanel.setExtensions(new String[]
		{ "drgprv:Prv Data File (.drgprv)" }, true, true);

		loadRawPanel.setLabelSize(labelSize);
		loadPrvPanel.setLabelSize(labelSize);

		loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);

		loadRawPanel.setLabelText("Raw File : ");
		loadPrvPanel.setLabelText("Prv File : ");

		JPanel nativeFormat = new JPanel(new GridLayout(2, 1, 10, 10));
		nativeFormat.add(loadRawPanel);
		nativeFormat.add(loadPrvPanel);

		JPanel nativeHolder = new JPanel(new BorderLayout());
		nativeHolder.setBorder(BorderFactory.createTitledBorder(""));
		nativeHolder.add(nativeFormat, BorderLayout.NORTH);

		// Create Tiff Loader

		loadTiffPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
		loadTiffPanel.setExtensions(new String[]
		{ "tif:Tiff Image Stack (.tif)" }, true, true);
		loadTiffPanel.setLabelSize(labelSize);
		loadTiffPanel.setLabelText("Tiff File : ");

		JPanel tiffHolder = new JPanel(new GridLayout(2, 1, 10, 10));
		tiffHolder.add(loadTiffPanel);
		tiffHolder.add(new JPanel());

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(tiffHolder, BorderLayout.NORTH);
		holder.setBorder(BorderFactory.createTitledBorder(""));

		
		JPanel IMGloadHolder = new JPanel(new BorderLayout());
		IMGloadHolder.add(loadIMGPanel, BorderLayout.NORTH);
		
		dataTabPanel.addTab("Tiff Stack", holder);
		dataTabPanel.addTab("Native Format", nativeHolder);
		dataTabPanel.addTab("IMG", IMGloadHolder);
		dataTabPanel.addTab("Image Series", loadImageSeriesPanel);
		JPanel datasetFile = new JPanel(new BorderLayout());
		datasetFile.setBorder(BorderFactory.createTitledBorder("Data File"));
		datasetFile.add(dataTabPanel, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(detailsPanel, BorderLayout.NORTH);
		detailsPanel.setPreferredSize(new java.awt.Dimension(653, 180));
		
		add(datasetFile, BorderLayout.CENTER);

		// Set data dimensions size
		sizeDataXLabel.setPreferredSize(new Dimension(50, 0));
		jLabel1.setPreferredSize(new Dimension(50, 0));
		jLabel2.setPreferredSize(new Dimension(50, 0));

	}

	public OCTExperimentData getOCTExperiment(OCTAnalysis owner)
			throws IOException
	{
		if (dataTabPanel.getSelectedIndex() == 0)
		{
			File in = loadTiffPanel.getFile();
			TiffDataSet data = new TiffDataSet(in);
			data.unloadData();

			data.setSizeDataX(xSize.getValue());
			data.setSizeDataY(ySize.getValue());
			data.setSizeDataZ(zSize.getValue());

			data.setPowerX(xSize.getPrefex());
			data.setPowerY(ySize.getPrefex());
			data.setPowerZ(zSize.getPrefex());

			data.setInfo(infomation.getText());

			OCTExperimentData expField = new OCTExperimentData(owner, data,
					dataSetNameField.getText());
			expField.addView();
			return expField;
		} else if (dataTabPanel.getSelectedIndex() == 1)
		{
			File rawFile = loadRawPanel.getFile();
			File prvFile = loadPrvPanel.getFile();

			NativeDataSet data = new NativeDataSet(rawFile, prvFile, owner
					.getStatusBar(), false);
			data.unloadData();

			data.setSizeDataX(xSize.getValue());
			data.setSizeDataY(ySize.getValue());
			data.setSizeDataZ(zSize.getValue());

			data.setPowerX(xSize.getPrefex());
			data.setPowerY(ySize.getPrefex());
			data.setPowerZ(zSize.getPrefex());

			data.setInfo(infomation.getText());

			OCTExperimentData expField = new OCTExperimentData(owner, data,
					dataSetNameField.getText());

			expField.addView();
			return expField;
		} else if (dataTabPanel.getSelectedIndex() == 2)
		{
			File in = loadIMGPanel.getFile();
			IMGDataSet data = new IMGDataSet(in);
			data.unloadData();

			data.setSizeDataX(xSize.getValue());
			data.setSizeDataY(ySize.getValue());
			data.setSizeDataZ(zSize.getValue());

			data.setPowerX(xSize.getPrefex());
			data.setPowerY(ySize.getPrefex());
			data.setPowerZ(zSize.getPrefex());

			data.setInfo(infomation.getText());

			OCTExperimentData expField = new OCTExperimentData(owner, data,
					dataSetNameField.getText());
			expField.addView();
			return expField;
		} else if(dataTabPanel.getSelectedIndex() ==3)
		{
			File in[] = loadImageSeriesPanel.getFiles();
			ImageSeriesDataSet data = new ImageSeriesDataSet(in);
			data.unloadData();

			data.setSizeDataX(xSize.getValue());
			data.setSizeDataY(ySize.getValue());
			data.setSizeDataZ(zSize.getValue());

			data.setPowerX(xSize.getPrefex());
			data.setPowerY(ySize.getPrefex());
			data.setPowerZ(zSize.getPrefex());

			data.setInfo(infomation.getText());

			OCTExperimentData expField = new OCTExperimentData(owner, data,
					dataSetNameField.getText());
			expField.addView();
			return expField;
		}
		else
		{
			return null;
		}
	}

	public static void main(String input[]) throws IOException
	{
		MainLauncher.setLAF();
		JOptionPane.showConfirmDialog(null, new LoadOCTDatasetPanel());
		System.exit(0);
	}

	public void resetFields()
	{
		loadPrvPanel.setFieldText("");
		loadRawPanel.setFieldText("");
		loadTiffPanel.setFieldText("");

		dataTabPanel.setSelectedIndex(0);
		xSize.setValue(1, true);
		ySize.setValue(1, true);
		zSize.setValue(1, true);

		infomation.setText("");

	}
}
