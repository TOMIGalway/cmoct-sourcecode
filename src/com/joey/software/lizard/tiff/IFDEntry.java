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

import com.joey.software.lizard.tiff.tag.CompressionType;
import com.joey.software.lizard.util.MemoryFileInputFilter;
import com.joey.software.lizard.util.MotorolaIntelInputFilter;


class IFDEntry
{

	public static final int SIZE = 12;

	public Tag tag; // tag that identifies the field

	public DataType type; // field type

	public long count; // count of values of the indicated type

	public long value; // if count > 4 then it is an offset to the value

	public byte dataArray[]; // used if value is bigger than 4 bytes

	public IFDEntry()
	{
		init();
	}

	public IFDEntry(int newTag, int newType, long newCount, long newValue)
	{
		tag = new Tag(newTag);
		type = new DataType(newType);
		count = newCount;
		value = newValue;
		dataArray = null;
	}

	void init()
	{
		tag = new Tag();
		type = new DataType();
		count = 0;
		value = 0;
		dataArray = null;
	}

	public boolean isOffset()
	{
		return (sizeOfData() > 4);
	}

	public int sizeOfData()
	{
		// get size of the data type then multiply it by the count
		return ((int) count) * type.size();
	}

	void setOffset(long n)
	{
		value = n;
	}

	public void writeEntry(MotorolaIntelInputFilter out) throws IOException
	{
		tag.write(out);
		type.write(out);
		out.writeInt((int) count);
		out.writeInt((int) value);
	}

	public void writeData(MotorolaIntelInputFilter out) throws IOException
	{
		out.seek(value);

		if (type.isRational())
		{
			// switch it around here
			byte b[] = new byte[dataArray.length];
			// System.arraycopy( dataArray, 0, b, 0, dataArray.length );
			b[0] = dataArray[3];
			b[1] = dataArray[2];
			b[2] = dataArray[1];
			b[3] = dataArray[0];
			b[4] = dataArray[7];
			b[5] = dataArray[6];
			b[6] = dataArray[5];
			b[7] = dataArray[4];

			out.write(b);
		} else
			out.writeProperly(dataArray, type.size(), sizeOfData());
	}

	public void read(MemoryFileInputFilter in)
	{
		read(in, 0);
	}

	public void read(MemoryFileInputFilter in, long ifdOffset)
	{
		tag.read(in);
		type.read(in);
		count = in.readInt();
		value = in.readInt();

		if (isOffset())
		{
			int len = sizeOfData();
			long currentOffset = in.getFilePointer();
			dataArray = new byte[len];
			in.seek(value + ifdOffset);
			if (type.isAscii())
				in.readFully(dataArray);
			else if (type.isRational())
			{
				DataType t = new DataType(DataType.LONG);
				byte b[] = new byte[4]; // two longs
				if (in.isIntel())
				{
					in.readProperly(dataArray, t.size(), 4);
					in.readProperly(b, t.size(), 4);
					System.arraycopy(b, 0, dataArray, 4, 4);
				} else
				{
					in.readFully(dataArray);
				}
			} else
			{
				in.readProperly(dataArray, type.size(), len);
			}
			in.seek(currentOffset);
		}
		if (type.isShort() && !in.isIntel())
			value = (int) ((value >> 16) & 0xffff);
	}

	@Override
	public String toString()
	{
		String s;
		s = "Tag: " + tag.toString() + ", Type: " + type.toString()
				+ ", Count: " + count;

		if (tag.equals(Tag.COMPRESSION))
			s += ", " + CompressionType.toString((int) value);
		else if (isOffset())
		{
			int i;
			int len = Math.min(dataArray.length, 256);
			s += ", Offset: 0x" + Long.toHexString(value) + ", " + sizeOfData()
					+ "bytes";

			if (type.isAscii())
			{
				String sz; // = new String (dataArray); //jdk1.1 allows
				// String(byte[])
				sz = new String(dataArray, 0, 0, dataArray.length); // jdk1.02
				// allows
				// String(byte
				// ascii[],
				// int
				// hibyte,
				// int
				// offset,
				// int
				// count)
				s += "\n\t\t" + sz + "\n";
			} else if (type.isRational())
			{
				int n = ((dataArray[0] & 0xff) << 24)
						+ ((dataArray[1] & 0xff) << 16)
						+ ((dataArray[2] & 0xff) << 8) + (dataArray[3] & 0xff);
				int d = ((dataArray[4] & 0xff) << 24)
						+ ((dataArray[5] & 0xff) << 16)
						+ ((dataArray[6] & 0xff) << 8) + (dataArray[7] & 0xff);
				s += "\n\t\t" + n + "/" + d + "\n";
			} else if (tag.equals(Tag.COLORMAP))
			{
				int skip = dataArray.length / 3;
				for (i = 0; i < dataArray.length; i++)
				{
					if (i % skip == 0)
						s += "\n\n\t\t";
					if (i % 16 == 0)
						s += "\n\t\t";
					if (i % 2 == 0)
						s += "|";
					else
						s += " ";
					// s += dataArray[i];

					// int d = (((int)dataArray[i])&0xff) +
					// ((((int)dataArray[i+1])&0xff)<<8);
					// int d = ((((int)dataArray[i])&0xff)<<8) +
					// (((int)dataArray[i+1])&0xff);
					int d = ((dataArray[i]) & 0xff);
					// s += d;

					s += (d == 0 ? "00" : Integer.toHexString(d));

				}

				s += "\n";
			} else if (type.equals(DataType.SHORT))
			{
				for (i = 0; i < dataArray.length; i += 2)
				{
					if (i % 16 == 0)
						s += "\n\t\t";
					else
						s += ", ";
					// s += dataArray[i];

					int d = (((dataArray[i]) & 0xff) << 8)
							+ ((dataArray[i + 1]) & 0xff);
					// int d = (((int)dataArray[i])&0xff);
					s += d;
					// s += Integer.toHexString( d );
				}

				s += "\n";
			} else if (type.equals(DataType.LONG))
			{
				for (i = 0; i < len; i += 4)
				{
					int n = ((dataArray[i] & 0xff) << 24)
							+ ((dataArray[i + 1] & 0xff) << 16)
							+ ((dataArray[i + 2] & 0xff) << 8)
							+ (dataArray[i + 3] & 0xff);
					if (i % 16 == 0)
						s += "\n\t\t";
					else
						s += ", ";
					s += n;
				}
				s += "\n";
			}
			/*******************************************************************
			 * gets ugly************ else if (type.equals(DataType.BYTE)) { for
			 * (i=0; i<len; i++) { if (i%16==0) s += "\n\t\t"; else s += ", "; s
			 * += Integer.toHexString( dataArray[i]&0xff ); } s += "\n"; }
			 ******************************************************************/
		} else
			s += ", Value: " + value;
		return s;
	}
}
