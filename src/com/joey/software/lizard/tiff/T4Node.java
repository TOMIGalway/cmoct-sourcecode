package com.joey.software.lizard.tiff;

import com.joey.software.lizard.util.Converter;

class T4Node
{
	public static final int NOMASK = 0xffff;

	int mask;

	T4Node zero;

	T4Node one;

	T4Code code;

	public T4Node()
	{
		mask = 0xffff;
		zero = null;
		one = null;
		code = null;
	}

	@Override
	public String toString()
	{
		StringBuffer sz = new StringBuffer();
		String szMask = Converter.intToBinaryString(mask, 16);
		int i = (mask != NOMASK ? szMask.indexOf("1") : code.bitLength);
		sz.append("\n");
		for (; i > 0; i--)
			sz.append(" ");
		sz.append("{");
		sz.append(szMask);
		sz.append(",");
		// if (zero==null) sz.append("null");
		// else sz.append( zero.toString() );
		sz.append(zero);
		sz.append(",");
		// if (one==null) sz.append("null");
		// else sz.append( one.toString() );
		sz.append(one);
		sz.append(",");
		sz.append(code);
		sz.append("}");
		return sz.toString();
	}

	public void Add(int d, T4Code c)
	{
		if (d == c.bitLength)
		{ // end of the line
			code = c;
			// System.out.println("Added T4Node for "+c);
		} else
		{
			d++;
			if (mask == NOMASK)
			{
				mask = 1 << (16 - d);
				// System.out.print("Added ");
			}

			int bits = c.codeWord << (16 - c.bitLength);

			// System.out.print("mask ("
			// + Converter.intToBinaryString(mask,16) + ") T4Node for "
			// + Converter.intToBinaryString(bits,16));

			if ((bits & mask) == 0)
			{
				// System.out.println(" --> add to zero side");
				if (zero == null)
					zero = new T4Node();
				zero.Add(d, c);
			} else
			{
				// System.out.println(" --> add to ones side");
				if (one == null)
					one = new T4Node();
				one.Add(d, c);
			}
		}
	}

	public void ReversedAdd(int d, T4Code c)
	{
		if (d == c.bitLength)
		{ // end of the line
			code = c;
			// DEBUG System.out.println("Added T4Node for "+c);
		} else
		{
			if (mask == NOMASK)
			{
				mask = 1 << (d);
				// DEBUG System.out.print("MASK ");
			}
			// DEBUG else System.out.print("mask ");
			d++;

			int bits = c.codeWord;

			// DEBUG System.out.print("("
			// DEBUG + Converter.intToBinaryString(mask,16) + ") T4Node for "
			// DEBUG + Converter.intToBinaryString(bits,16));

			if ((bits & mask) == 0)
			{
				// DEBUG System.out.println(" --> add to zero side");
				if (zero == null)
					zero = new T4Node();
				zero.ReversedAdd(d, c);
			} else
			{
				// DEBUG System.out.println(" --> add to ones side");
				if (one == null)
					one = new T4Node();
				one.ReversedAdd(d, c);
			}
		}
	}

	public T4Node Find(int i)
	{
		if (code != null)
		{
			// DEBUG System.out.println( "found it: "+code );
			return this;
		} else if (mask != NOMASK)
		{
			if ((mask & i) == 0)
			{
				// DEBUG System.out.println( "look on zero side");
				return (zero != null ? zero.Find(i) : null);
			} else
			{
				// DEBUG System.out.println( "look on ones side");
				return (one != null ? one.Find(i) : null);
			}
		} else
			return null;
	}

}