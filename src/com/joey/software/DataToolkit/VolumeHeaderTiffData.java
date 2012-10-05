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
package com.joey.software.DataToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VolumeHeaderTiffData extends VolumeHeaderData
{
	File tiff;

	public VolumeHeaderTiffData(File f) throws IOException
	{
		this.tiff = f;
		loadData();
	}

	public void loadData() throws IOException
	{
		TiffLoader load = new TiffLoader(tiff);
		sizeDataZ = load.getImageCount();

		BufferedImage img = load.getImage(0);
		sizeDataX = img.getWidth();
		sizeDataY = img.getHeight();
		img = null;
		load = null;
	}
}
