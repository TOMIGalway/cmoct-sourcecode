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

import java.io.File;
import java.io.IOException;

public class VolumeHeaderIMGData extends VolumeHeaderData
{
	File imgFile;

	public VolumeHeaderIMGData(File f) throws IOException
	{
		this.imgFile = f;
		loadData();
	}

	public void loadData() throws IOException
	{
		ThorLabs2DImageProducer load = new ThorLabs2DImageProducer(imgFile);
		sizeDataX = load.getSizeZ();
		sizeDataY = load.getSizeX();
		sizeDataZ = load.getSizeY();
		load = null;
	}
}
