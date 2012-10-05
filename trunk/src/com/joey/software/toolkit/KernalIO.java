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