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
package com.joey.software.fileToolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDifferenceFinder
{
	public static String findDifference(File f1, File f2) throws IOException
	{
		StringBuilder result = new StringBuilder();

		BufferedReader r1 = new BufferedReader(new FileReader(f1));
		BufferedReader r2 = new BufferedReader(new FileReader(f2));

		String l1;
		String l2;
		boolean finished = false;

		int lineCount = 0;
		while (!finished)
		{
			l1 = r1.readLine();
			l2 = r2.readLine();
			lineCount++;
			if (l1 == null && l2 == null)
			{
				finished = true;
			} else if (l1 == null)
			{
				finished = true;
			} else if (l2 == null)
			{
				finished = true;
			} else if (!l1.equalsIgnoreCase(l2) && lineCount < 250)
			{
				l1 = l1.substring(11);
				l2 = l2.substring(11);
				result.append("\nLine Number: ");
				result.append(lineCount);
				result.append("\n\tLine 1: ");
				result.append(l1);
				result.append("\n\tLine 2: ");
				result.append(l2);

			}
		}

		return result.toString();
	}

	public static void main(String arg[]) throws IOException
	{
		File f1 = new File("c:\\test\\before.js");
		File f2 = new File("c:\\test\\after.js");

		String result = findDifference(f1, f2);
		System.out.println(result);

	}
}
