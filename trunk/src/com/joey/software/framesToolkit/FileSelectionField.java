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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.ImageFileFilter;
import com.joey.software.fileToolkit.ImagePreview;


public class FileSelectionField extends JPanel implements ActionListener,
		DropTextFieldListner
{

	static FileSelectionField fieldLoader = new FileSelectionField();

	public static final int TYPE_SAVE_FILE = 0;

	public static final int TYPE_OPEN_FILE = 1;

	public static final int FORMAT_ALL_FILES = 0;

	public static final int FORMAT_IMAGE_FILES = 1;

	public static final int FORMAT_CUSTOM_FILES = 2;

	public static final int FORMAT_IMAGE_FILES_SHOW_FORMAT = 3;

	public static final int FORMAT_FOLDERS = 4;

	JButton button = new JButton("SET");

	JLabel label = new JLabel("File : ", SwingConstants.RIGHT);

	DropTextField field = new DropTextField();

	JFileChooser chooser = new JFileChooser();

	int height = 30;

	int buttonSize = 70;

	int fieldSize = 200;

	int labelSize = 70;

	int type = TYPE_OPEN_FILE;

	int format = FORMAT_ALL_FILES;

	String title = "Select File";

	String[] extensionData = null;

	/**
	 * This should contain all the files that
	 * should be allowed to use wiht the filter.
	 * data should be in the format
	 * "extension:descripton" String[] data =
	 * {"JPG: jpg image file .jpg", }
	 * 
	 * @param extension
	 */
	public void setExtensions(String[] extension, boolean allFiles, boolean directory)
	{
		this.extensionData = extension;

		chooser.setAcceptAllFileFilterUsed(allFiles);

		for (int i = 0; i < extension.length; i++)
		{
			CustomFileFilter filter = new CustomFileFilter(extension[i]);
			chooser.addChoosableFileFilter(filter);
			if (i == 0)
			{
				chooser.setFileFilter(filter);
			}
		}
	}

	public FileSelectionField()
	{
		createJPanel();
	}

	public void setButtonText(String data)
	{
		button.setText(data);
	}

	public void setLabelText(String data)
	{
		label.setText(data);
	}

	public void setFieldText(String data)
	{
		field.setText(data);
	}

	public void setButtonSize(int size)
	{
		this.buttonSize = size;
		button.setPreferredSize(new java.awt.Dimension(buttonSize, -1));
		updateUI();
	}

	public void setLabelSize(int size)
	{
		this.labelSize = size;
		label.setPreferredSize(new java.awt.Dimension(labelSize, -1));
		updateUI();

	}

	public void setFieldSize(int size)
	{
		fieldSize = size;
		field.setPreferredSize(new java.awt.Dimension(fieldSize, height));
		updateUI();
	}

	public void setCompoentHeight(int value)
	{
		height = value;
		button.setPreferredSize(new java.awt.Dimension(buttonSize, height));
		label.setPreferredSize(new java.awt.Dimension(labelSize, height));
		field.setPreferredSize(new java.awt.Dimension(fieldSize, height));
		updateUI();
	}

	public void setCompoentSize(int labelWide, int fieldWide, int buttonWide, int height)
	{
		setCompoentHeight(height);
		setLabelSize(labelWide);
		setFieldSize(fieldWide);
		setButtonSize(buttonWide);
	}

	/**
	 * this will create a file from the given text
	 * field. it will NOT test to see if it is a
	 * file
	 * 
	 * @return
	 */
	public File getFile()
	{
		return new File(field.getText());
	}

	/**
	 * this will show a JOptionpane and get the
	 * user to select a file. If the user choses
	 * cancle this function will return false.
	 * 
	 * @return
	 */
	public boolean getUserChoice()
	{
		if (JOptionPane
				.showConfirmDialog(null, this, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param type
	 *            -TYPE_SAVE/TYPE_OPEN
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	private void createJPanel()
	{
		try
		{
			
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);

			this.removeAll();

			this.add(field, BorderLayout.CENTER);
			field.setPreferredSize(new java.awt.Dimension(fieldSize, height));
			field.addListner(this);

			this.add(button, BorderLayout.EAST);
			button.setPreferredSize(new java.awt.Dimension(buttonSize, height));

			this.add(label, BorderLayout.WEST);
			label.setPreferredSize(new java.awt.Dimension(labelSize, height));

			button.addActionListener(this);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String input[]) throws InterruptedException
	{
		FileSelectionField.getUserOpenImageFile();
		FileSelectionField.getUserSaveImageFile();

		FileSelectionField.getUserOpenFolder();
		FileSelectionField.getUserSaveFolder();

		FileSelectionField
				.getUserOpenFile(new String[] { "drgraw:Dragon Raw File (.drgRaw)" });
		FileSelectionField
				.getUserSaveFile(new String[] { "drgprv:Dragon Raw File (.drgPrv)" });

		FileSelectionField.getUserOpenFile();
		FileSelectionField.getUserSaveFile();
	}

	public static File getUserFile(final String fileTypes[])
	{
		if (fileTypes != null)
		{
			fieldLoader.setExtensions(fileTypes, true, true);
		}

		if (fieldLoader.getUserChoice())
		{
			return fieldLoader.getFile();
		} else
		{
			return null;
		}
	}

	/**
	 * @deprecated dont use this anymore, see
	 *             getUserOpenFile
	 * @return
	 */
	@Deprecated
	public static File getUserFile()
	{
		return getUserFile(null);
	}

	public static File getUserOpenFolder()
	{
		fieldLoader.setType(TYPE_OPEN_FILE);
		fieldLoader.setFormat(FORMAT_FOLDERS);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public static File getUserSaveFolder()
	{
		fieldLoader.setType(TYPE_SAVE_FILE);
		fieldLoader.setFormat(FORMAT_FOLDERS);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public static File getUserOpenFile(String[] formats)
	{
		fieldLoader.setType(TYPE_OPEN_FILE);
		fieldLoader.setFormat(FORMAT_CUSTOM_FILES);
		fieldLoader.setExtensions(formats, true, true);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public static File getUserSaveFile(String[] formats)
	{

		fieldLoader.setType(TYPE_SAVE_FILE);
		fieldLoader.setFormat(FORMAT_CUSTOM_FILES);
		fieldLoader.setExtensions(formats, true, true);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public void setFile(File f)
	{
		field.setText(f.getAbsolutePath());
	}

	public static File getUserOpenFile()
	{
		fieldLoader.setType(TYPE_OPEN_FILE);
		fieldLoader.setFormat(FORMAT_ALL_FILES);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public static File getUserSaveFile()
	{
		fieldLoader.setType(TYPE_SAVE_FILE);
		fieldLoader.setFormat(FORMAT_ALL_FILES);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public static File getUserOpenImageFile()
	{
		fieldLoader.setType(TYPE_OPEN_FILE);
		fieldLoader.setFormat(FORMAT_IMAGE_FILES);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	public static File getUserSaveImageFile()
	{
		fieldLoader.setType(TYPE_SAVE_FILE);
		fieldLoader.setFormat(FORMAT_IMAGE_FILES);
		fieldLoader.getUserChoice();
		return fieldLoader.getFile();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == button)
		{
			int returnValue;

			if (getType() == TYPE_SAVE_FILE)
			{
				System.out.println("Saving");
				returnValue = getFileChooser().showSaveDialog(this);
			} else
			{
				System.out.println("Open");
				returnValue = getFileChooser().showOpenDialog(this);
			}

			if (returnValue == JFileChooser.APPROVE_OPTION)
			{

				getFileChooser().setSelectedFile(checkApproved(getFileChooser()
						.getSelectedFile()));
				field.setText(getFileChooser().getSelectedFile()
						.getAbsolutePath());
			}
		}

	}

	public File checkApproved(File in)
	{
		if (getFileChooser().getFileFilter() instanceof CustomFileFilter)
		{
			CustomFileFilter filter = (CustomFileFilter) getFileChooser()
					.getFileFilter();
			return FileOperations.renameFileType(in, filter.extension);
		}

		return in;
	}

	public JFileChooser getFileChooser()
	{

		return chooser;
	}

	public void setFileChooser(JFileChooser chooser)
	{
		this.chooser = chooser;
	}

	public int getType()
	{
		return type;
	}

	public int getFormat()
	{
		return format;
	}

	public void setFormat(int format)
	{

		this.format = format;
		chooser.setAccessory(null);
		for (FileFilter f : chooser.getChoosableFileFilters())
		{
			chooser.removeChoosableFileFilter(f);
		}

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		title = "Select File";

		if (format == FORMAT_IMAGE_FILES)
		{
			ImageFileFilter filter = new ImageFileFilter();
			chooser.setFileFilter(filter);
			chooser.addChoosableFileFilter(filter);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setMultiSelectionEnabled(false);
			// Add the preview pane.
			chooser.setAccessory(new ImagePreview(chooser));
			chooser.setFileFilter(filter);
		} else if (format == FORMAT_IMAGE_FILES_SHOW_FORMAT)
		{
			String[] extension = new String[] { "tiff: Tiff files (*.tiff)",
					"tif: Tif file (*.tif)", "jpeg: JPEG file (*.jpeg)",
					"jpg: JPG file (*.jpg)", "bmp: BMP file (*.BMP)",
					"png: PNG file (*.png)" };

			setExtensions(extension, true, true);
			// Add the preview pane.
			chooser.setAccessory(new ImagePreview(chooser));
		} else if (format == FORMAT_ALL_FILES)
		{
			chooser.setAcceptAllFileFilterUsed(true);
		} else if (format == FORMAT_CUSTOM_FILES)
		{
			chooser.setAcceptAllFileFilterUsed(false);

		} else if (format == FORMAT_FOLDERS)
		{
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			title = "Select Folder";
		} else
		{
			chooser = new JFileChooser();
		}
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public boolean acceptFile(File f)
	{
		return (this.format == FORMAT_FOLDERS) || chooser.accept(f);
	}

	@Override
	public File getSuitableFile(File f)
	{
		if (this.format == FORMAT_FOLDERS)
			return f.isDirectory() ? f : f.getParentFile();
		else
			return f;
	}
}

class CustomFileFilter extends FileFilter
{
	boolean directory = true;

	boolean files = true;

	String extension;

	String description;

	public CustomFileFilter(String data)
	{
		String[] rst = data.split(":", 2);
		extension = rst[0];
		description = rst[1];
	}

	public CustomFileFilter(String extension, String description)
	{
		this.extension = extension;
		this.description = description;
	}

	@Override
	public boolean accept(File f)
	{
		try
		{
			if (f.isDirectory())

			{
				return directory;
			}

			int index = f.toString().lastIndexOf('.');
			if (index == -1)
			{
				return false;
			}

			String ext = FileOperations.getExtension(f);
			ext = ext.substring(1);

			if (extension.equalsIgnoreCase(ext.toLowerCase()))
			{
				return true;
			}

			return false;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return description;
	}

}
