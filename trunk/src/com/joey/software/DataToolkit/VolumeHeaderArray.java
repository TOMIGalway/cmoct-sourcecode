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

import java.io.IOException;

public class VolumeHeaderArray extends VolumeHeaderData
{
	
	byte[][][] volData;
	public VolumeHeaderArray(byte[][][] data) throws IOException
	{
		volData = data;
		loadData();
	}

	public void loadData() throws IOException
	{
		sizeDataX = volData.length;
		sizeDataY = volData[0].length;
		sizeDataZ = volData[0][0].length;
	}
}
