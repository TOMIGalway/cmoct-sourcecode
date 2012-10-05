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
package com.joey.software.lizard.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * MotorolaIntelInputFilter is basically RandomAccessFile, but can be told which
 * byte order system to use when reading and writing files. Copyright 1997 By
 * BancTec. GNU Public license Applys.
 * 
 * @version 1.0, Aug 21, 1997
 * @author Liz Marks
 * @see RandomAccessFile
 */

public class MotorolaIntelInputFilter
{
	private RandomAccessFile raf;

	private boolean isIntel;

	public MotorolaIntelInputFilter(RandomAccessFile raf, boolean isIntel)
	{
		this.raf = raf;
		this.isIntel = isIntel;
	}

	public boolean isIntel()
	{
		return isIntel;
	}

	public short readByte() throws IOException
	{
		short b = (short) raf.readUnsignedByte();
		return b;
	}

	public void writeByte(int v) throws IOException
	{
		raf.writeByte(v);
	}

	public int readShort() throws IOException
	{
		int s = 0;
		if (isIntel)
		{
			int l = 0, h = 0;
			l = raf.readUnsignedByte();
			h = raf.readUnsignedByte();
			s = (((h & 0xffff) << 8) + (l & 0xffff));
		} else
		{
			s = raf.readUnsignedShort();
		}
		return s;
	}

	public int readUnsignedShort() throws IOException
	{
		int s = 0;
		if (isIntel)
		{
			int l = 0, h = 0;
			l = raf.readUnsignedByte();
			h = raf.readUnsignedByte();
			s = (((h & 0xffff) << 8) + (l & 0xffff));
		} else
		{
			s = raf.readUnsignedShort();
		}
		return s;
	}

	public void writeShort(int v) throws IOException
	{
		if (isIntel)
		{
			int b1 = 0, b2 = 0;

			b1 = ((v & 0x0000ff00) >> 8);
			b2 = (v & 0x000000ff);

			v = ((b2 & 0xff) << 8) + (b1 & 0xff);
		}
		raf.writeShort(v);
	}

	public long readInt() throws IOException
	{
		long i;
		int b1 = 0, b2 = 0, b3 = 0, b4 = 0;

		i = raf.readInt();

		if (isIntel)
		{
			b1 = (int) ((i & 0xff000000) >> 24);
			b2 = (int) ((i & 0x00ff0000) >> 16);
			b3 = (int) ((i & 0x0000ff00) >> 8);
			b4 = (int) (i & 0x000000ff);
			i = (((b4 & 0xff) << 24) + ((b3 & 0xff) << 16) + ((b2 & 0xff) << 8) + (b1 & 0xff));
		}
		return i;
	}

	public void writeInt(int v) throws IOException
	{
		int b1 = 0, b2 = 0, b3 = 0, b4 = 0;

		if (isIntel)
		{
			b1 = ((v & 0xff000000) >> 24);
			b2 = ((v & 0x00ff0000) >> 16);
			b3 = ((v & 0x0000ff00) >> 8);
			b4 = (v & 0x000000ff);
			v = (((b4 & 0xff) << 24) + ((b3 & 0xff) << 16) + ((b2 & 0xff) << 8) + (b1 & 0xff));
		}
		raf.writeInt(v);
	}

	public void readFully(byte[] b) throws IOException
	{
		raf.readFully(b, 0, b.length);
	}

	public void write(byte[] b) throws IOException
	{
		raf.write(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException
	{
		// may want to change this to flip the bytes as we read them raf.
		// System.out.println("readFully( bytes, "+off+", "+len+" )");
		int n = 0;
		while (n < len)
		{
			int count = raf.read(b, off + n, len - n);
			if (count < 0)
				throw new EOFException();
			n += count;
		}
	}

	public void write(byte[] b, int off, int len) throws IOException
	{
		raf.write(b, off, len);
	}

	public void readProperly(byte[] bytes, int typeSize, int len)
			throws IOException
	{
		if (!isIntel || typeSize < 2)
			readFully(bytes, 0, len);
		else
		{
			int n = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0, b7 = 0, b8 = 0;
			while (n < len)
			{
				switch (typeSize)
				{
					case 2:
					{
						b1 = raf.readUnsignedByte();
						b2 = raf.readUnsignedByte();
						bytes[n] = (byte) b2;
						bytes[n + 1] = (byte) b1;
						// System.out.println( "Read a short
						// "+bytes[n]+bytes[n+1]);
						break;
					}
					case 4:
					{
						b1 = raf.readUnsignedByte();
						b2 = raf.readUnsignedByte();
						b3 = raf.readUnsignedByte();
						b4 = raf.readUnsignedByte();
						bytes[n] = (byte) b4;
						bytes[n + 1] = (byte) b3;
						bytes[n + 2] = (byte) b2;
						bytes[n + 3] = (byte) b1;
						break;
					}
					case 8:
					{
						b1 = raf.readUnsignedByte();
						b2 = raf.readUnsignedByte();
						b3 = raf.readUnsignedByte();
						b4 = raf.readUnsignedByte();
						b5 = raf.readUnsignedByte();
						b6 = raf.readUnsignedByte();
						b7 = raf.readUnsignedByte();
						b8 = raf.readUnsignedByte();
						bytes[n] = (byte) b8;
						bytes[n + 1] = (byte) b7;
						bytes[n + 2] = (byte) b6;
						bytes[n + 3] = (byte) b5;
						bytes[n + 4] = (byte) b4;
						bytes[n + 5] = (byte) b3;
						bytes[n + 6] = (byte) b2;
						bytes[n + 7] = (byte) b1;
						break;
					}
				}// switch
				n += typeSize;
			}// while
		}// else
	}// readProperly

	public void writeProperly(byte[] bytes, int typeSize, int len)
			throws IOException
	{
		if (!isIntel || typeSize < 2)
			write(bytes, 0, len);
		else
		{
			int n = 0, v, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0, b7 = 0, b8 = 0;
			while (n < len)
			{
				switch (typeSize)
				{
					case 2:
					{
						b2 = bytes[n];
						b1 = bytes[n + 1];
						v = ((b2 & 0xff) << 8) + (b1 & 0xff);
						raf.writeShort(v);
						break;
					}
					case 4:
					{
						b4 = bytes[n];
						b3 = bytes[n + 1];
						b2 = bytes[n + 2];
						b1 = bytes[n + 3];
						v = (((b4 & 0xff) << 24) + ((b3 & 0xff) << 16)
								+ ((b2 & 0xff) << 8) + (b1 & 0xff));
						raf.writeInt(v);
						break;
					}
					case 8:
					{
						b8 = bytes[n];
						b7 = bytes[n + 1];
						b6 = bytes[n + 2];
						b5 = bytes[n + 3];
						b4 = bytes[n + 4];
						b3 = bytes[n + 5];
						b2 = bytes[n + 6];
						b1 = bytes[n + 7];

						long lv;
						lv = (((b8 & 0xff) << 56) + ((b7 & 0xff) << 48)
								+ ((b6 & 0xff) << 40) + ((b5 & 0xff) << 32)
								+ ((b4 & 0xff) << 24) + ((b3 & 0xff) << 16)
								+ ((b2 & 0xff) << 8) + (b1 & 0xff));
						raf.writeLong(lv);
						break;
					}
				}// switch
				n += typeSize;
			}// while
		}// else
	}// writeProperly

	public void seek(long pos) throws IOException
	{
		raf.seek(pos);
	}

	public long getFilePointer() throws IOException
	{
		return raf.getFilePointer();
	}

	public long length() throws IOException
	{
		return raf.length();
	}

	public void close() throws IOException
	{
		raf.close();
	}
}
