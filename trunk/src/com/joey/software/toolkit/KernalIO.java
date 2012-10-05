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
package com.joey.software.toolkit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class KernalIO
{

	public static String readKernal(String file) throws IOException
	{
		StringBuilder rst = new StringBuilder();

		FileReader input = new FileReader(file);
		BufferedReader in = new BufferedReader(input);

		String data = in.readLine();
		while (data != null)
		{
			rst.append(data + "\n");
			data = in.readLine();
		}

		return rst.toString();
	}
}
