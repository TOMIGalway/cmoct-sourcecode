package com.joey.software.binaryTools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileCompare
{
	public static void findDifference(File file1, File file2, int bufferSize)
			throws IOException
	{
		RandomAccessFile fileA = new RandomAccessFile(file1, "r");
		RandomAccessFile fileB = new RandomAccessFile(file2, "r");

		byte dataHolderA[] = new byte[bufferSize];
		byte dataHolderB[] = new byte[bufferSize];

		long length = file1.length();
		if (file2.length() < length)
		{
			length = file2.length();
		}

		for (long i = 0; i < length / bufferSize; i++)
		{
			fileA.read(dataHolderA);
			fileB.read(dataHolderB);

			for (int j = 0; j < bufferSize; j++)
			{
				if (i * bufferSize < length)
				{
					if (dataHolderA[j] != dataHolderB[j])
					{
						System.out
								.println("Difference : " + i * bufferSize + j);
					}
				}
			}

		}

		fileA.close();
		fileB.close();
	}
}
