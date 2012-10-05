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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

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
public class BioptigenSettingsPanel extends JPanel implements ActionListener
{
	static private JPanel mainPanel;

	static private JButton setInputButton;

	static private JPanel OutputPanel;

	static private JPanel headerOutput;

	private JSpinner initialDigit;

	private JLabel timeStampLabel;

	private JSpinner numberDigits;

	private JLabel digitsLabel;

	private JTextField outputFileName;

	private JLabel jLabel2;

	private JButton setImageFolderOutput;

	static private JCheckBox headerOuputCheck;

	private JTextField imageOutputFolder;

	private JLabel jLabel1;

	private JPanel ImageInformation;

	private JLabel OCTFileFieldLabel;

	static private JCheckBox imageOuputCheck;

	static private JPanel ImageOutputPanel;

	static private JTextField InputField;

	static private JPanel InputPanel;

	public BioptigenSettingsPanel()
	{
		super();
		initGUI();
	}

	private void initGUI()
	{
		{
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(459, 400));
		}

		mainPanel = new JPanel();
		BorderLayout mainPanelLayout = new BorderLayout();
		this.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(mainPanelLayout);
		{
			InputPanel = new JPanel();
			mainPanel.add(InputPanel, BorderLayout.NORTH);
			InputPanel
					.setBorder(BorderFactory.createTitledBorder("Input Data"));
			InputPanel.setLayout(null);
			InputPanel.setPreferredSize(new java.awt.Dimension(449, 54));
			{
				InputField = new JTextField();
				InputPanel.add(InputField);
				InputField.setBounds(76, 20, 299, 22);
			}
			{
				setInputButton = new JButton();
				InputPanel.add(setInputButton);
				setInputButton.setText("SET");
				setInputButton.setBounds(385, 20, 52, 22);
				setInputButton.addActionListener(this);
			}
			{
				OCTFileFieldLabel = new JLabel();
				InputPanel.add(OCTFileFieldLabel);
				OCTFileFieldLabel.setText("OCT Input : ");
				OCTFileFieldLabel.setBounds(16, 20, 60, 22);
			}
		}
		{
			OutputPanel = new JPanel();
			BorderLayout OutputPanelLayout = new BorderLayout();
			mainPanel.add(OutputPanel, BorderLayout.CENTER);
			OutputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
			OutputPanel.setLayout(OutputPanelLayout);
			OutputPanel.setPreferredSize(new java.awt.Dimension(449, 313));
			{
				headerOutput = new JPanel();
				OutputPanel.add(headerOutput, BorderLayout.SOUTH);
				headerOutput.setBorder(BorderFactory
						.createTitledBorder("Header Output"));
				headerOutput.setLayout(null);
				headerOutput.setPreferredSize(new java.awt.Dimension(437, 89));
				{
					headerOuputCheck = new JCheckBox();
					headerOutput.add(headerOuputCheck);
					headerOuputCheck.setText("Export Header File");
					headerOuputCheck.setBounds(17, 21, 123, 20);
				}
			}
			{
				ImageOutputPanel = new JPanel();
				OutputPanel.add(ImageOutputPanel, BorderLayout.CENTER);
				ImageOutputPanel.setBorder(BorderFactory
						.createTitledBorder("Image Sequence Output"));
				ImageOutputPanel.setLayout(null);
				ImageOutputPanel.setPreferredSize(new java.awt.Dimension(447,
						193));
				{
					imageOuputCheck = new JCheckBox();
					ImageOutputPanel.add(imageOuputCheck);
					imageOuputCheck.setText("Export Image Sequence");
					imageOuputCheck.setBounds(17, 21, 141, 20);
					imageOuputCheck.addActionListener(this);
				}
				{
					ImageInformation = new JPanel();
					ImageOutputPanel.add(ImageInformation);
					ImageInformation.setBounds(27, 60, 399, 135);
					ImageInformation.setBorder(BorderFactory
							.createEtchedBorder(BevelBorder.LOWERED));
					ImageInformation.setLayout(null);
					{

						numberDigits = new JSpinner();
						ImageInformation.add(numberDigits);
						numberDigits.setModel(new SpinnerNumberModel(5, 0,
								10000, 1));
						numberDigits.setBounds(84, 70, 87, 22);
						numberDigits
								.getEditor()
								.setPreferredSize(new java.awt.Dimension(27, 18));
					}
					{
						digitsLabel = new JLabel();
						ImageInformation.add(digitsLabel);
						digitsLabel.setText("Digits :");
						digitsLabel.setBounds(17, 73, 63, 16);
						digitsLabel
								.setHorizontalAlignment(SwingConstants.RIGHT);
					}
					{
						jLabel1 = new JLabel();
						ImageInformation.add(jLabel1);
						jLabel1.setText("Folder :");
						jLabel1.setBounds(31, 13, 49, 16);
						jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
					}
					{
						imageOutputFolder = new JTextField();
						ImageInformation.add(imageOutputFolder);
						imageOutputFolder.setBounds(84, 10, 244, 22);
					}
					{
						setImageFolderOutput = new JButton();
						ImageInformation.add(setImageFolderOutput);
						setImageFolderOutput.setText("SET");
						setImageFolderOutput.setBounds(338, 11, 53, 22);
						setImageFolderOutput.addActionListener(this);
					}
					{
						jLabel2 = new JLabel();
						ImageInformation.add(jLabel2);
						jLabel2.setText("File Name :");
						jLabel2.setBounds(10, 45, 70, 16);
						jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
					}
					{
						outputFileName = new JTextField();
						ImageInformation.add(outputFileName);
						outputFileName.setBounds(84, 42, 307, 22);
					}
					{
						timeStampLabel = new JLabel();
						ImageInformation.add(timeStampLabel);
						timeStampLabel.setText("Initial Index :");
						timeStampLabel.setBounds(12, 100, 68, 18);
						timeStampLabel
								.setHorizontalAlignment(SwingConstants.RIGHT);
					}
					{
						initialDigit = new JSpinner();
						ImageInformation.add(numberDigits);
						numberDigits.setModel(new SpinnerNumberModel(5, 0,
								Integer.MAX_VALUE - 1, 1));
						ImageInformation.add(initialDigit);
						initialDigit.setBounds(84, 98, 87, 20);
					}
				}
			}
		}

	}

	public boolean isOutputHeader()
	{
		return headerOuputCheck.isSelected();
	}

	public void showInputFileDialog()
	{
		JFileChooser c = new JFileChooser();
		if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File f = c.getSelectedFile();
			InputField.setText(f.toString());
		}
	}

	public void showImageFolderDialog()
	{
		JFileChooser c = new JFileChooser();
		c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File f = c.getSelectedFile();
			imageOutputFolder.setText(f.toString());
		}
	}

	public static void main(String input[]) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JOptionPane.showConfirmDialog(null, new BioptigenSettingsPanel());
	}

	public void setImageExportFeatures(boolean available)
	{

	}

	public void setHeaderExportFeatures(boolean avaliable)
	{

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == setInputButton)
		{
			showInputFileDialog();
		} else if (e.getSource() == setImageFolderOutput)
		{
			showImageFolderDialog();
		} else if (e.getSource() == imageOuputCheck)
		{
			setImageExportFeatures(imageOuputCheck.isSelected());
		} else if (e.getSource() == headerOuputCheck)
		{
			setHeaderExportFeatures(headerOuputCheck.isSelected());
		}

	}

	public String getInputFile()
	{
		return InputField.getText();
	}

	public String getImageOutputFolder()
	{
		return imageOutputFolder.getText();
	}

	public String getImageOutputName()
	{
		return outputFileName.getText();
	}

	public int getImageOutputDigits()
	{
		return (Integer) numberDigits.getValue();
	}

	public int getInitialIndex()
	{
		return (Integer) initialDigit.getValue();
	}

}
