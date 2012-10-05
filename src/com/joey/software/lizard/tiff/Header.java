package com.joey.software.lizard.tiff;

import java.io.IOException;

import com.joey.software.lizard.util.MemoryFileInputFilter;
import com.joey.software.lizard.util.MotorolaIntelInputFilter;


class Header
{

	public static final int TIFF_VERSION = 42; // 0x2a

	public static final int TIFF_BIGENDIAN = 0x4d4d;

	public static final int TIFF_LITTLEENDIAN = 0x4949;

	public static final int SIZE = 8;

	public int byteOrder; // should be "II" (0x4949) or "MM" (0x4d4d)

	public int id; // should be 42

	public long offset; // 4 byte offset to first IFD

	public Header()
	{
		init();
	}

	void init()
	{
		byteOrder = TIFF_LITTLEENDIAN;
		id = TIFF_VERSION;
		offset = 10; // 8 bytes for the header size + 2 extra for breathing
		// space
	}

	void write(MotorolaIntelInputFilter out) throws IOException
	{
		out.writeShort(byteOrder);
		out.writeShort(id);
		out.writeInt((int) offset);
	}

	void read(MemoryFileInputFilter in)
	{
		byteOrder = in.readUnsignedShort();
		in.setByteOrder(byteOrder == Header.TIFF_LITTLEENDIAN);
		id = in.readUnsignedShort();
		offset = in.readInt();
	}

	@Override
	public String toString()
	{
		String s = "Byte Order: ";
		if (byteOrder == TIFF_BIGENDIAN)
			s += "Motorola";
		else
			s += "Intel";

		s += ", Version: " + id + ", Offset: " + Long.toHexString(offset);
		return s;
	}

}