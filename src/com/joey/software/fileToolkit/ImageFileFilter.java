package com.joey.software.fileToolkit;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/* ImageFilter.java is used by FileChooserDemo2.java. */
/**
 * http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html
 */
public class ImageFileFilter extends FileFilter
{

	// Accept all directories and all gif, jpg, tiff, or png files.
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}

		String extension = Utils.getExtension(f);
		if (extension != null)
		{
			if (extension.equals(Utils.tiff) || extension.equals(Utils.tif)
					|| extension.equals(Utils.gif)
					|| extension.equals(Utils.jpeg)
					|| extension.equals(Utils.jpg)
					|| extension.equals(Utils.png)
					|| extension.equals(Utils.bmp))
			{
				return true;
			} else
			{
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	@Override
	public String getDescription()
	{
		return "Just Images";
	}
}