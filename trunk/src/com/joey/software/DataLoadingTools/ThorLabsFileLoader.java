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
package com.joey.software.DataLoadingTools;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;


/**
 * @deprecated old Data
 * @author Joey.Enfield
 * 
 */
@Deprecated
public class ThorLabsFileLoader
{
	static String fileId;

	static int totalFrames;

	static int sizeZ;

	static int sizeX;

	static int sizeY;

	static int num3D;

	public static void main(String input[]) throws IOException
	{
		String file = "C:\\Users\\joey.enfield.UL\\Desktop\\Daisy_001.img";
		ThorLabsFileLoader loader = new ThorLabsFileLoader();
		loader.load2DVideo(file);
	}

	public void load2DVideo(String file) throws IOException
	{
		BinaryToolkit.binaryFileTool(new File(file));

	}

	public static String byteToString(byte[] data, int count)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < count; i++)
		{
			rst.append((char) data[i]);
		}
		return rst.toString();
	}

	public static byte[][][] load3DOCTFile(File f) throws IOException
	{
		byte[] data = new byte[36];
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		in.read(data, 0, 16);
		fileId = (byteToString(data, 16));

		in.read(data, 0, 4);
		sizeZ = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeX = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeY = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		totalFrames = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		num3D = BinaryToolkit.readFlippedInt(data, 0);

		in.skip(4 * 118);

		int frmLenBytes = 40 + sizeX * sizeY; // 40 bytes is the length of sub
		// hearder of the image frame
		int frmPixels = sizeX * sizeY;

		byte[] pxlHolder = new byte[frmPixels];

		ImagePanel panel = new ImagePanel();
		FrameFactroy.getFrame(panel);

		for (int i = 0; i < sizeZ; i++)
		{
			// Elapsed_Time
			in.read(data, 0, 4);

			in.read(data, 0, 36);
			// Frame Info
			in.read(pxlHolder);
			panel.setImage(getImage(pxlHolder, sizeY, sizeX));
		}
		// Read in each frame

		return null;
	}

	public static BufferedImage getImage(byte[] data, int wide, int high)
	{
		BufferedImage img = ImageOperations.getBi(wide, high);
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				img
						.setRGB(x, y, NativeDataSet.getByteToRGB(data[x + y
								* wide]));
			}
		}
		return img;
	}

}
