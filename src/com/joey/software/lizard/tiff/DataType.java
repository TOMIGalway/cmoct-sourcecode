package com.joey.software.lizard.tiff;

import java.io.IOException;

import com.joey.software.lizard.util.MemoryFileInputFilter;
import com.joey.software.lizard.util.MotorolaIntelInputFilter;


/*
 * Tag data type information.
 *
 * Note: RATIONALs are the ratio of two 32-bit integer values.
 */
class DataType
{

	public int type;

	public static final int MINIMUM = 0;

	public static final int NOTYPE = 0; /* placeholder */

	public static final int BYTE = 1; /* 8-bit unsigned integer */

	public static final int ASCII = 2; /* 8-bit bytes w/ last byte null */

	public static final int SHORT = 3; /* 16-bit unsigned integer */

	public static final int LONG = 4; /* 32-bit unsigned integer */

	public static final int RATIONAL = 5; /* 64-bit unsigned fraction */

	public static final int SBYTE = 6; /* !8-bit signed integer */

	public static final int UNDEFINED = 7; /* !8-bit untyped data */

	public static final int SSHORT = 8; /* !16-bit signed integer */

	public static final int SLONG = 9; /* !32-bit signed integer */

	public static final int SRATIONAL = 10; /* !64-bit signed fraction */

	public static final int FLOAT = 11; /* !32-bit IEEE floating point */

	public static final int DOUBLE = 12; /* !64-bit IEEE floating point */

	public static final int MAXIMUM = 12;

	public DataType()
	{
		type = NOTYPE;
	}

	public DataType(int n)
	{

		type = n;
		if (n < MINIMUM || n > MAXIMUM)
		{
			System.out.println("WARNING: Unknown Data Type " + n);
		}
	}

	public int size()
	{
		return size(type);
	}

	public int size(int n)
	{
		int size = 0;
		switch (n)
		{
			case BYTE:
			case ASCII:
			case SBYTE:
			case UNDEFINED:
				size = 1;
				break;
			case SHORT:
			case SSHORT:
				size = 2;
				break;
			case LONG:
			case SLONG:
			case FLOAT:
				size = 4;
				break;
			case RATIONAL:
			case SRATIONAL:
			case DOUBLE:
				size = 8;
				break;
		}
		return size;
	}

	public boolean isAscii()
	{
		return type == ASCII;
	}

	public boolean isRational()
	{
		return type == RATIONAL || type == SRATIONAL;
	}

	public boolean isShort()
	{
		return type == SHORT || type == SSHORT;
	}

	public boolean isLong()
	{
		return type == LONG || type == SLONG;
	}

	public boolean equals(int n)
	{
		return type == n;
	}

	public void write(MotorolaIntelInputFilter out) throws IOException
	{
		out.writeShort((short) type);
	}

	public void read(MemoryFileInputFilter in)
	{
		type = in.readUnsignedShort();
	}

	@Override
	public String toString()
	{
		return toString(type);
	}

	public String toString(int n)
	{
		String s = "...";
		switch (n)
		{
			case NOTYPE:
				s = "NOTYPE ";
				break;
			case BYTE:
				s = "BYTE   ";
				break;
			case ASCII:
				s = "ASCII  ";
				break;
			case SBYTE:
				s = "SBYTE  ";
				break;
			case UNDEFINED:
				s = "UNDEFINED ";
				break;
			case SHORT:
				s = "SHORT  ";
				break;
			case SSHORT:
				s = "SSHORT ";
				break;
			case LONG:
				s = "LONG   ";
				break;
			case SLONG:
				s = "SLONG  ";
				break;
			case FLOAT:
				s = "FLOAT  ";
				break;
			case RATIONAL:
				s = "RATIONAL ";
				break;
			case SRATIONAL:
				s = "SRATIONAL ";
				break;
			case DOUBLE:
				s = "DOUBLE ";
				break;
		}
		return s;
	}
}