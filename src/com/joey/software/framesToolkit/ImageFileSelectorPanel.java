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
package com.joey.software.framesToolkit;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.fileToolkit.InvalidFileSequenceException;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class ImageFileSelectorPanel extends JPanel implements ActionListener,
		ListSelectionListener
{
	static final Dimension previewPanelPrefSize = new Dimension(200, 200),
			imagePanelPrefSize = new Dimension(180, 180);

	JTable dataTable;

	Vector<Vector> rowData = new Vector<Vector>(); // This stores the data for

	// the rows in

	JTextField startFileTextField = new JTextField(24),
			endFileTextField = new JTextField(24);

	JButton startFileButton = new JButton("Select File"),
			endFileButton = new JButton("Select File"), goButton = new JButton(
					"Go"), addFileButton = new JButton("Add File"),
			removeFileButton = new JButton("Remove File");

	JButton upButton = new JButton(new ImageIcon(DrawTools
			.getMoveUPImage(10, 10)));

	JButton downButton = new JButton(new ImageIcon(DrawTools
			.getMoveDownImage(10, 10)));

	public ImagePanel previewImagePanel = new ImagePanel();

	JPanel mainPanel = new JPanel();

	JPanel previewPanel = new JPanel(new BorderLayout());

	File lastLoadedPriviewImage = new File(""); // This is to help reduce the

	// number of images loaded fro
	// teh prieview

	boolean includesROISelector = false;

	public ImageFileSelectorPanel()
	{
		createPanel();
		new FileDrop(null, this, /* dragBorder, */
		new FileDrop.Listener()
		{
			@Override
			public void filesDropped(java.io.File[] files)
			{
				for (int i = 0; i < files.length; i++)
				{
					try
					{
						addFile(files[i]);
					} // end try
					catch (Exception e)
					{
					}
				} // end for: through each dropped file
			} // end filesDropped
		}); // end FileDrop.Listener
	}

	public static File[] getUserSelection()
	{
		ImageFileSelectorPanel panel = new ImageFileSelectorPanel();
		JFrame f= FrameFactroy.getFrame(panel);
		f.setTitle("Select images then close to continue");
		FrameWaitForClose close = new FrameWaitForClose(f);
		close.waitForClose();
		return panel.getFiles();
	}
	public void setSelectedIndex(int index)
	{
		dataTable.setRowSelectionInterval(index, index);
	}

	public int getSelectedIndex()
	{
		return dataTable.getSelectedRow();
	}

	/**
	 * This will create a new file loader with a ROI selector
	 * 
	 * @param includedROISelector
	 */
	public ImageFileSelectorPanel(boolean includedROISelector)
	{
		this();
		setIncludesROISelector(includedROISelector);
	}

	/**
	 * This function will attempt to unload any memory in use by unselecting any
	 * currently selected images
	 * 
	 */
	public void unloadMemory()
	{
		dataTable.getSelectionModel().setSelectionInterval(-1, -1);
		repaint();
	}

	/**
	 * Not realy depricated, just saying that so not to mix up with getFiles
	 * 
	 * @return
	 */
	@Deprecated
	public File[] getUserFiles()
	{
		JFrame f = FrameFactroy.getFrame(this);
		FrameWaitForClose fc = new FrameWaitForClose(f);
		fc.waitForClose();

		return getFiles();
	}

	/**
	 * Provided that this image file loader is using a ROI slector it will
	 * return the seleced region of intread A *
	 * 
	 * @return
	 */
	public Rectangle getROI()
	{
		if (isIncludesROISelector())
		{
			ROIPanel pan = (ROIPanel) previewImagePanel;
			return (Rectangle) pan.getRegions().get(0);
		} else
		{
			return null;
		}
	}

	public void createPanel()
	{
		// Set up the dialog
		setLayout(new BorderLayout());
		// Create the data table
		Vector colNames = new Vector();
		colNames.add("file path");
		colNames.add("file Name");

		/**
		 * Create the table and prevent the cells from being edited by creating
		 * a stubb class
		 */
		dataTable = new JTable(rowData, colNames)
		{
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return false;
			}
		};
		dataTable.getSelectionModel().addListSelectionListener(this);
		dataTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// Create the main panel and add it to the dialog
		createMainPanel();
		add(mainPanel, BorderLayout.CENTER);

		// Register All buttons with the panel
		startFileButton.addActionListener(this);
		endFileButton.addActionListener(this);
		goButton.addActionListener(this);
		addFileButton.addActionListener(this);
		removeFileButton.addActionListener(this);
	}

	/**
	 * This will create the mainpanel of the application.
	 */
	public void createMainPanel()
	{
		mainPanel.removeAll();
		mainPanel.setLayout(new BorderLayout());

		/**
		 * The following section of code prepares the table panel
		 */
		JScrollPane tableScroll = new JScrollPane(dataTable);
		tableScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JPanel tableButtonPanel = new JPanel(new GridLayout(2, 2));
		tableButtonPanel.add(addFileButton);
		tableButtonPanel.add(removeFileButton);
		tableButtonPanel.add(upButton);
		tableButtonPanel.add(downButton);

		// Creating table panel
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(BorderFactory
				.createTitledBorder("Files to be loaded"));
		tablePanel.add(tableScroll, BorderLayout.CENTER);
		tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

		/***********************************************************************
		 * this will create the split plane for the prieview panel and the
		 * JTable
		 */
		createPreviewPanel();
		JSplitPane storePanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true, tablePanel, previewPanel);
		storePanel.setDividerLocation(200);

		// Add panels to the mainpanel

		mainPanel.add(storePanel, BorderLayout.CENTER);
	}

	/**
	 * This will create the preview Panel
	 */
	public void createPreviewPanel()
	{
		/**
		 * The following code prepares the preview panel
		 */
		previewImagePanel.putIntoPanel(previewPanel);
		previewImagePanel.setPanelType(ImagePanel.TYPE_NORMAL);
	}

	/**
	 * This will get the mainpanel of the dialog
	 */
	public JPanel getMainPanel()
	{
		return mainPanel;
	}

	/**
	 * This will return all the files in the rowData[].
	 * 
	 * @return File[] of all the chosen files.
	 */
	public File[] getFiles()
	{
		File[] result = new File[rowData.size()];

		for (int i = 0; i < rowData.size(); i++)
		{
			result[i] = (File) rowData.get(i).get(0);
		}

		return result;
	}

	/**
	 * This will add a files to the table.
	 * 
	 * @param file
	 */
	public void addFile(File file)
	{
		// Create a new Row
		Vector row = new Vector();
		row.add(file);
		row.add(file.getName());

		// Add new row to rowData
		rowData.add(row);

		// Update the table
		dataTable.addNotify();
	}

	/**
	 * This will remove a file from the list of files that are selected
	 * 
	 * @param index
	 */
	public void removeFile(int index)
	{
		try
		{
			rowData.remove(index);
			dataTable.addNotify();
		} catch (ArrayIndexOutOfBoundsException e)
		{

		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == startFileButton)
		{
			/**
			 * this will get the user to select a files for the starting file
			 */
			File userChoice = ImageFileSelector.getUserImageFile();
			if (userChoice != null)
			{
				if (userChoice.canRead() && userChoice.exists())
				{
					startFileTextField.setText(userChoice.getAbsoluteFile()
							+ "");
				} else
				{
					JOptionPane
							.showMessageDialog(this, "Error reading file : "
									+ userChoice.getAbsolutePath()
									+ userChoice.getName(), "Could not access file", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == endFileButton)
		{
			/**
			 * This will get the user to select and ending file
			 */
			File userChoice = ImageFileSelector.getUserImageFile();
			if (userChoice != null)
			{
				if (userChoice.canRead() && userChoice.exists())
				{
					endFileTextField.setText(userChoice.getAbsoluteFile() + "");
				} else
				{
					JOptionPane
							.showMessageDialog(this, "Error reading file : "
									+ userChoice.getAbsolutePath()
									+ userChoice.getName(), "Could not access file", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == goButton)
		{
			/**
			 * This will try and interpolate between the two files it will alert
			 * the user if it finds a problem
			 */
			File start = new File(startFileTextField.getText());
			File end = new File(endFileTextField.getText());
			Vector<File> result = new Vector<File>();

			try
			{
				FileOperations.createFileSequence(start, end, result);
				for (File fileData : result)
				{
					addFile(fileData);
				}
			} catch (InvalidFileSequenceException excp)
			{
				JOptionPane
						.showMessageDialog(this, "\nThere was an error while trying to sequence the files:\n"
								+ excp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getSource() == addFileButton)
		{
			File[] selectedFile = ImageFileSelector.getUserImageFile(true);
			if (selectedFile != null)
			{
				for (File userChoice : selectedFile)
				{
					if (userChoice != null)
					{
						if (userChoice.canRead() && userChoice.exists())
						{
							addFile(userChoice);
						} else
						{
							JOptionPane
									.showMessageDialog(this, "Error reading file : "
											+ userChoice, "Could not access file", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		} else if (e.getSource() == removeFileButton)
		{
			deleteSelectedFiles();
			updatePreviewImage();
		}
	}

	public void updatePreviewImage()
	{
		int selectedRow = dataTable.getSelectedRow();
		if (selectedRow < 0)
		{
			previewImagePanel.setImage(null);

		} else
		{

			try
			{
				// Check to see if the images has changed
				File imageFile = (File) rowData.get(selectedRow).get(0);
				if (!lastLoadedPriviewImage.equals(imageFile))
				{
					lastLoadedPriviewImage = imageFile;
					if (imageFile.canRead() && imageFile.exists())
					{
						try
						{
							previewImagePanel.setImage(ImageOperations
									.loadImage(imageFile));
						} catch (IllegalArgumentException excption)
						{
							JOptionPane
									.showMessageDialog(this, "Error reading file : "
											+ imageFile
											+ "\nThis could be beacuse the file was not a valid image", "Could not access file", JOptionPane.ERROR_MESSAGE);

						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException excp)
			{
				previewImagePanel.setImage(null);
			}

		}

	}

	public void deleteSelectedFiles()
	{
		int[] selectedRows = dataTable.getSelectedRows();
		for (int i = 0; i < selectedRows.length; i++)
		{
			removeFile(selectedRows[i]);

			/**
			 * The following loop adjust the row values as when a for is removed
			 * each successive row's index will drop by 1
			 */
			for (int j = i + 1; j < selectedRows.length; j++)
			{
				selectedRows[j] -= 1;
			}
		}
		dataTable.validate();
	}

	public static void main(String input[]) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager
				.getCrossPlatformLookAndFeelClassName());
		JFrame test = new JFrame();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImageFileSelectorPanel loader = new ImageFileSelectorPanel(true);
		test.getContentPane().setLayout(new BorderLayout());
		test.getContentPane().add(loader);

		test.pack();
		test.setVisible(true);
	}

	/**
	 * @return the includesROISelector
	 */
	public boolean isIncludesROISelector()
	{
		return includesROISelector;
	}

	/**
	 * @param includesROISelector
	 *            the includesROISelector to set
	 */
	public void setIncludesROISelector(boolean includesROISelector)
	{
		this.includesROISelector = includesROISelector;
		if (includesROISelector)
		{
			BufferedImage currentImage = previewImagePanel.getImage();
			previewImagePanel = new ROIPanel(false, ROIPanel.TYPE_RECTANGLE);
			((ROIPanel) previewImagePanel).setRegionColor(Color.RED);

			previewImagePanel.setImage(currentImage);
			createPreviewPanel();
		}
	}

	public File getSelectedFile()
	{
		int selectedRow = dataTable.getSelectedRow();
		if (selectedRow < 0)
		{
			throw new InvalidParameterException("No row is selected");
		}
		return getFiles()[selectedRow];
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		updatePreviewImage();
	}

}
