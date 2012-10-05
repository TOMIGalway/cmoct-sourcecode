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

import java.util.StringTokenizer;

/**
 * Converter does all kinds of neat conversion that java forgot. Copyright 1997
 * By BancTec. GNU Public license Applys.
 * 
 * @version 1.0, Aug 21, 1997
 * @author Liz Marks
 */

public final class Converter
{

	/*
	 * public static byte reverseByte( int oldBits ) { int i,d,bits; for
	 * (i=0,d=1,bits=0; i<8; i++,d=d<<1) { if ( (oldBits&d) != 0 ) { bits += 1
	 * << (7-i); } }
	 * 
	 * return (byte)( bits & 0xff); }
	 */

	public static int reverseByte(int oldBits)
	{
		int i, d, bits;
		for (i = 0, d = 1, bits = 0; i < 8; i++, d = d << 1)
		{
			if ((oldBits & d) != 0)
			{
				bits += 1 << (7 - i);
			}
		}

		return bits;
	}

	public static int reverseInt(int oldBits)
	{
		int i, d, bits;
		for (i = 0, d = 1, bits = 0; i < 16; i++, d = d << 1)
		{
			if ((oldBits & d) != 0)
			{
				bits += 1 << (15 - i);
			}
		}

		return bits;
	}

	public static byte getLoByte(int n)
	{
		return (byte) (n & 0xff);
	}

	public static byte getHiByte(int n)
	{
		return (byte) ((n & 0xff00) >> 8);
	}

	public static int bytesToInt(byte ba[])
	{
		if (ba.length < 2)
		{
			System.out
					.println("bytesToInt: byte array must be at least 2 bytes long");
			return 0;
		}
		int hi = ((ba[0] & 0xff) << 8);
		int lo = (ba[1] & 0xff);
		return (hi + lo);
	}

	public static String byteToBinaryString(byte b)
	{
		String szZeros = "00000000";
		String szBits = Integer.toString((b & 0xff), 2);
		String sz = szZeros.substring(szBits.length()) + szBits;
		// return sz.substring(0,4)+" "+sz.substring(4,8);
		return sz;
	}

	public static String byteToBinaryString(int i)
	{
		String szZeros = "00000000";
		String szBits = Integer.toString((i & 0xff), 2);
		String sz = szZeros.substring(szBits.length()) + szBits;
		// return sz.substring(0,4)+" "+sz.substring(4,8);
		return sz;
	}

	public static String byteToBinaryString(int i, int len)
	{
		return byteToBinaryString(i).substring(8 - len);
	}

	public static String intToBinaryString(int i, int len)
	{
		String szLo = byteToBinaryString(i & 0xff);
		String szHi = byteToBinaryString((i & 0xff00) >> 8);
		String szBits = szHi + szLo;

		return szBits.substring(16 - len);
	}

	public static byte getLoByteFromHexString(String szHex)
	{
		int n = Integer.parseInt(szHex, 16);
		return (byte) (n & 0xff);
	}

	public static byte getHiByteFromHexString(String szHex)
	{
		int n = Integer.parseInt(szHex, 16);
		return (byte) ((n & 0xff00) >> 8);
	}

	public static byte getByteFromHexString(String szHex)
	{
		return getLoByteFromHexString(szHex);
	}

	public static byte[] hexStringToBytes(String szHex)
	{
		StringTokenizer tokens = new StringTokenizer(szHex, ", ");
		int i, count = tokens.countTokens();
		byte bytes[] = new byte[count];
		for (i = 0; i < count && tokens.hasMoreTokens(); i++)
		{
			bytes[i] = getByteFromHexString(tokens.nextToken());
		}
		return bytes;
	}

	public static byte getByteFromBinaryString(String szBinary)
	{
		int n = Integer.parseInt(szBinary, 2);
		return (byte) (n & 0xff);
	}

	public static byte[] binaryStringToBytes(String szBinary)
	{
		StringTokenizer tokens = new StringTokenizer(szBinary, ", ");
		int i, count = tokens.countTokens();
		byte bytes[] = new byte[count];
		for (i = 0; i < count && tokens.hasMoreTokens(); i++)
		{
			bytes[i] = getByteFromBinaryString(tokens.nextToken());
		}
		return bytes;
	}

	public static int getBits(byte[] octet, int len)
	{
		System.out.println("\ngetBits (" + byteToBinaryString(octet[0]) + "_"
				+ byteToBinaryString(octet[1]) + ", " + len + ")");
		int bits = 0, hi, lo;
		switch (len)
		{
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				bits = (octet[0] & 0xff) >>> (8 - len);
				// System.out.println("\t"+byteToBinaryString(octet[0])+" >>> "
				// + (8-len) + " = " + byteToBinaryString(bits,len)+"\n");
				break;
			case 8:
				bits = octet[0];
				// System.out.println("\t"+byteToBinaryString(octet[0])+ " = " +
				// byteToBinaryString(bits,len)+"\n");
				break;
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				int shift = (8 - (len - 8));
				hi = ((octet[0] & 0xff) << 8);
				lo = (octet[1] & 0xff);
				bits = (hi + lo) >>> shift;

				// System.out.println("\t "+ intToBinaryString(hi,16));
				// System.out.println("\t+"+ intToBinaryString(lo,16));
				// System.out.println("\t="+ intToBinaryString(hi+lo,16) );
				// System.out.println("\t >>> " + shift);
				// System.out.println("\t\t==>" +
				// intToBinaryString(bits,len)+"\n");
				break;
			case 16:
				hi = ((octet[0] & 0xff) << 8);
				lo = (octet[1] & 0xff);
				bits = (hi + lo);
				// System.out.println("\t "+ intToBinaryString(hi,16));
				// System.out.println("\t+"+ intToBinaryString(lo,16));
				// System.out.println("\t="+ intToBinaryString(hi+lo,16) );
				break;
		}
		return bits;
	}

	public static int getBits(int number, int len)
	{
		System.out.println("\ngetBits (" + intToBinaryString(number, 16) + ", "
				+ len + ")");
		byte octet[] = new byte[2];
		octet[0] = getHiByte(number);
		octet[1] = getLoByte(number);
		return getBits(octet, len);
	}

}
