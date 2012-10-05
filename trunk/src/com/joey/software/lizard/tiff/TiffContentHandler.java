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
package com.joey.software.lizard.tiff;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;

public class TiffContentHandler extends ContentHandler
{
	Tiff tiffImage;

	// returns an array of Image
	@Override
	public Object getContent(URLConnection uc)
	{

		tiffImage = new Tiff();

		try
		{
			tiffImage.readInputStream(uc.getInputStream());
		} catch (IOException e)
		{
		}
		;

		return tiffImage.getImageProducer(0);
	}

	/*
	 * public Object getImageProducer( int page ) { return
	 * tiffImage.getImageProducer(page); }
	 */

	@Override
	public String toString()
	{
		return tiffImage.toString();
	}
}
