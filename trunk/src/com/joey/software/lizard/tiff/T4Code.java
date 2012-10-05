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

import com.joey.software.lizard.util.Converter;

/*
 * CCITT T.4 1D Huffman runlength codes and
 * related definitions.  Given the small sizes
 * of these tables it does not seem
 * worthwhile to make code & length 8 bits.
 */
class T4Code
{

	/* status values returned instead of a run bitLength */
	public static final int EOL = -1; /* NB: ACT_EOL - ACT_WRUNT */

	public static final int INVALID = -2; /* NB: ACT_INVALID - ACT_WRUNT */

	public static final int EOF = -3; /* end of input data */

	public static final int INCOMP = -4; /* incomplete run code */

	public int bitLength; /* bit bitLength of g3 code */

	public int codeWord; /* g3 codeWord */

	public int runLength; /* run length in bits */

	public T4Code()
	{
		bitLength = 0;
		codeWord = 0;
		runLength = 0;
	}

	public T4Code(T4Code c)
	{
		if (c != null)
		{
			bitLength = c.bitLength;
			codeWord = c.codeWord;
			runLength = c.runLength;
		} else
		{
			bitLength = 0;
			codeWord = 0;
			runLength = 0;
		}
	}

	public T4Code(int l, int c, int r)
	{
		bitLength = l;
		codeWord = c;
		runLength = r;
	}

	public T4Code(int codeArray[])
	{
		if (codeArray.length == 3)
		{
			bitLength = codeArray[0];
			codeWord = codeArray[1];
			runLength = codeArray[2];
		} else
		{
			bitLength = 0;
			codeWord = 0;
			runLength = 0;
		}
	}

	public T4Code(byte[] octet, boolean whiteRun)
	{
		this();
		T4Code c = getCode(octet, whiteRun);
		if (c != null)
		{
			bitLength = c.bitLength;
			codeWord = c.codeWord;
			runLength = c.runLength;
		}
	}

	public T4Code getCode(byte[] octet, boolean whiteRun)
	{

		T4Code c = null;
		int table[][];// = (whiteRun ? T4Tables.WhiteCodes :
		// T4Tables.BlackCodes);
		int len = 0;
		int i, ca[];
		int bits = 0;
		boolean found = false;

		if (whiteRun)
		{
			table = T4Tables.WhiteCodes;
			i = (octet[0] == 0 ? 92 : 0);
		} else
		{
			table = T4Tables.BlackCodes;
			i = (octet[0] == 0 ? 15 : 0);
		}
		for (; !found && i < table.length; i++)
		{

			ca = table[i];
			if (len != ca[0])
			{
				len = ca[0];
				bits = Converter.getBits(octet, len);
			}

			// System.out.println("test " + c
			// + " --- bits = " + Converter.intToBinaryString(bits,len));

			if (bits == ca[1])
			{
				c = new T4Code(ca);
				found = true;
			}

		}

		// if (found) {
		// System.out.println();
		// System.out.println("found: " + c);
		// }
		return c;
	}

	@Override
	public String toString()
	{
		StringBuffer sz = new StringBuffer();
		// sz.append( "run len=" );
		// sz.append( runLength );
		// sz.append( ", bits=" );
		// sz.append( bitLength );
		// sz.append( ", code=" );
		// sz.append( Converter.intToBinaryString(codeWord,bitLength) );

		sz.append("{");
		sz.append(bitLength);
		sz.append(",");
		sz.append(runLength);
		sz.append(",");
		sz.append(Converter.intToBinaryString(codeWord, bitLength));
		sz.append("}");

		return sz.toString();
	}
}
