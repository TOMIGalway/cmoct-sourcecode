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

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import com.joey.software.lizard.util.MemoryFileInputFilter;
import com.joey.software.lizard.util.MotorolaIntelInputFilter;


/**
 * Tiff decoder class. It can decompress raw, packbits, Group 3 1D, Group4, and
 * JPEG
 * 
 * Copyright 1997 By BancTec. GNU Public license Applys.
 * 
 * @version 1.0, Aug 21, 1997
 * @author Liz Marks
 */

public class Tiff
{
	Header header;

	IFD pages[];

	int pageCount;

	/**
	 * Create empty tiff.
	 */
	public Tiff()
	{
		header = new Header();
		pages = null;
		pageCount = 0;
	}

	/**
	 * Returns the number of pages in the tiff file
	 */
	public int getPageCount()
	{
		return pageCount;
	}

	/**
	 * Returns an Image for the given page
	 */
	public Image getImage(int page)
	{
		if (pageCount == 0 || page > pageCount)
			return null;
		return pages[page].getImage();
	}

	/**
	 * Returns an ImageProducer for the given page
	 */
	public Object getImageProducer(int page)
	{

		if (pageCount == 0 || page > pageCount)
			return null;
		return pages[page].getImageProducer();
	}

	/**
	 * Write the tiff file out using RandomAccessFile
	 */
	public void write(java.io.RandomAccessFile raf) throws IOException
	{

		int i;
		long offset = header.offset;
		MotorolaIntelInputFilter out = new MotorolaIntelInputFilter(raf,
				(header.byteOrder == Header.TIFF_LITTLEENDIAN));

		if (pageCount == 0)
			return;

		header.write(out);

		offset = header.offset;

		for (i = 0; i < pageCount; i++)
		{
			out.seek(offset);
			pages[i].write(out);
			offset = pages[i].offset;
		}
	}

	/**
	 * Reads the tiff file from the given input stream
	 */
	public void readInputStream(InputStream input) throws IOException
	{

		int CHUNK = 10240;
		int i, count, max = 10 * CHUNK;
		byte tmp[] = new byte[CHUNK], bytesArray[] = new byte[max];

		// read it all into a byte array
		for (i = 0, count = 0; (count = input.read(tmp)) != -1; i += count)
		{
			if (i + count > max)
			{
				byte ba[] = new byte[i + count + CHUNK];
				System.arraycopy(bytesArray, 0, ba, 0, i);
				max = i + count + CHUNK;
				bytesArray = ba;
			}
			System.arraycopy(tmp, 0, bytesArray, i, count);
		}
		tmp = null;

		// now reduce the size of bytesArray to its actual size
		tmp = new byte[i];
		System.arraycopy(bytesArray, 0, tmp, 0, i);
		bytesArray = tmp;

		read(bytesArray);
	}

	/**
	 * Reads the tiff file from the given byte array
	 */
	public void read(byte[] bytesArray) throws IOException
	{

		MemoryFileInputFilter in = new MemoryFileInputFilter(bytesArray);
		pageCount = 0;
		pages = new IFD[10]; // cheater!!!!

		// read the header and set the byte order
		header.read(in);

		// read all the pages

		long offset = header.offset;

		while (offset != 0)
		{
			in.seek(offset);
			pages[pageCount] = new IFD();
			pages[pageCount].read(in);

			offset = pages[pageCount].offset;
			pageCount++;
			if (pageCount >= 10 && offset != 0)
			{
				System.out
						.println("Oop, I was cheating and only allowed for 10 pages in a tif file");
				break; // can't allow them to go any farther
			}
		}

		in.close();
	}

	/**
	 * Returns a string with all the tags for all the pages
	 */
	@Override
	public String toString()
	{
		String s;
		int i;

		s = "Tiff has " + pageCount + " pages\n\n";
		s += header.toString() + "\n";
		for (i = 0; i < pageCount; i++)
			s += "Page #" + i + "\n" + pages[i].toString();
		return s;
	}
}
