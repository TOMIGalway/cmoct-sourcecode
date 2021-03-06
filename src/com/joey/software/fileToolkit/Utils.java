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
package com.joey.software.fileToolkit;

import java.io.File;

import javax.swing.ImageIcon;

/**
 * http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html
 */
/* Utils.java is used by FileChooserDemo2.java. */
public class Utils
{
	public final static String jpeg = "jpeg";

	public final static String jpg = "jpg";

	public final static String gif = "gif";

	public final static String tiff = "tiff";

	public final static String tif = "tif";

	public final static String png = "png";

	public final static String bmp = "bmp";

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
		{
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path)
	{
		java.net.URL imgURL = Utils.class.getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL);
		} else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
