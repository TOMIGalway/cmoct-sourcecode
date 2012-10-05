package com.joey.software.stringToolkit;

import java.util.ArrayList;

public class StringOperations
{
	public static String reverseString(String data)
	{
		StringBuffer result = new StringBuffer();
		for (int i = data.length() - 1; i >= 0; i--)
		{
			result.append(data.charAt(i));
		}
		return result.toString();
	}

	/*
	 * Replace all instances of a String in a String. @param s String to alter.
	 * 
	 * @param f String to look for. @param r String to replace it with, or null
	 * to just remove it.
	 */

	public static String replace(String s, String f, String r)
	{
		if (s == null)
			return s;
		if (f == null)
			return s;
		if (r == null)
			r = "";

		int index01 = s.indexOf(f);
		while (index01 != -1)
		{
			s = s.substring(0, index01) + r + s.substring(index01 + f.length());
			index01 += r.length();
			index01 = s.indexOf(f, index01);
		}
		return s;
	}

	public static String removeNonNumber(String src)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < src.length(); i++)
		{
			if (Character.isDigit(src.charAt(i)) || src.charAt(i) == '.')
			{
				rst.append(src.charAt(i));
			}

		}
		return rst.toString();
	}

	public static String removeChar(String src, char c)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < src.length(); i++)
		{
			if (src.charAt(i) != c)
			{
				rst.append(src.charAt(i));
			}
		}
		return rst.toString();
	}

	/**
	 * This will return a string with a given number of digits i.e if digits is
	 * 8 and number is 4 Result -> 00000004
	 * 
	 * @param zero
	 * @param number
	 * @return
	 */
	public static String getNumberString(int digits, int number)
	{
		StringBuilder numString = new StringBuilder();
		numString.append(number);

		if (numString.length() > digits)
		{
			return numString.toString();
		}

		StringBuilder result = new StringBuilder();
		char c;
		for (int i = 0; i < digits; i++)
		{
			if (i >= (digits - numString.length()))
			{
				c = numString.charAt(i - (digits - numString.length()));
			} else
			{
				c = '0';
			}
			result.append(c);
		}

		return result.toString();

	}

	public static synchronized String getNextChar(String input)
	{
		return getNextChar(input, (char) 0, (char) 255);
	}

	/**
	 * This function is used to get the next string in a sequence. It operates
	 * by increasing the characters by 1. .ie
	 * a,b,c,....,aa,ab,ac,.....aaa,aab,aac The output string is counted between
	 * min and max, however if the input string has a character below min, it
	 * will count from the character below .ie if min = 'c', max = 'f' input =
	 * 'ffa' input -> ffb, ffc, ffd, ffe, fff, cccc
	 * 
	 * @param input
	 *            - The String to get the next logical string of
	 * @param minChar
	 *            - The min character to use
	 * @param maxChar
	 *            - The max character to use
	 * @return The next logical string after the inputed string
	 */
	public static synchronized String getNextChar(String input, char minChar, char maxChar)
	{
		char[] data = input.toCharArray();
		boolean incNext = true;
		for (int i = data.length - 1; incNext && i >= 0; i--)
		{
			if (incNext)
			{
				data[i] += 1;
			}
			if (data[i] > maxChar)
			{
				data[i] = minChar;
				incNext = true;
			} else
			{
				incNext = false;
			}
		}
		if (incNext == true)
		{
			StringBuilder result = new StringBuilder(data.length + 1);
			result.append(minChar);
			for (int i = 0; i < data.length; i++)
			{
				result.append(minChar);
			}
			return result.toString();
		}

		return new String(data);
	}

	/**
	 * This will split a string into a string[]
	 * 
	 * @param data
	 * @param split
	 * @return
	 */
	public static String[] splitString(String data, char split)
	{
		ArrayList<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.length(); i++)
		{
			if (data.charAt(i) == split)
			{
				result.add(builder.toString());
				builder.setLength(0);
			} else
			{
				builder.append(data.charAt(i));
			}
		}
		result.add(builder.toString());
		return result.toArray(new String[0]);
	}

	/**
	 * this will print out a full character map
	 */
	public static void printCharacterMap()
	{
		for (int i = 0; i < 256; i++)
		{
			if (i == 9)
			{
				System.out.printf("%d - %s\t", i, "TAB");
			} else
			{
				System.out.printf("%d - %s\t", i, Character.toString((char) i));
			}
			if (i % 5 == 0 && i != 0)
			{
				System.out.println();
			}
		}
	}

	public static void main(String input[])
	{
		int digits = 3;

		for (int i = 0; i < 2000; i += 14)
		{
			System.out.println(getNumberString(digits, i));
		}
	}
}
