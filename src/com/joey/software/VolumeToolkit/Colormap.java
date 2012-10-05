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
package com.joey.software.VolumeToolkit;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;

import com.joey.software.binaryTools.BinaryToolkit;


/*
 *	@(#)Colormap.java 1.4 00/09/21 14:20:03
 *
 * Copyright (c) 1996-1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

abstract public class Colormap
{
	/**
	 * This represents grayscale alpha data that is generated using the
	 * histogram of pixel values
	 */
	public static int TYPE_HIST_MAP_GA = 0;

	/**
	 * This representds red,green blue and alpha that is generated using the
	 * histogram of pixel values
	 */
	public static int TYPE_HIST_MAP_RGBA = 1;

	/**
	 * This representds red,green blue and alpha that is generated using the
	 * histogram of pixel values
	 */
	public static int TYPE_GRAD_GA = 2;

	/**
	 * This representds red,green blue and alpha that is generated using the
	 * histogram of pixel values
	 */
	public static int TYPE_GRAD_RGBA = 3;

	int mapType = TYPE_HIST_MAP_RGBA;

	// color mapping tables
	int[] colorMapping = new int[256];

	int editId;

	int bytesPerVoxel = 4;

	int update()
	{
		return editId;
	}

	public ColorModel getColorModel()
	{
		if (mapType == TYPE_HIST_MAP_GA)
		{
			bytesPerVoxel = 4;
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			int[] nBits =
			{ 8 };
			return new ComponentColorModel(cs, nBits, false, false,
					Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		} else if (mapType == TYPE_HIST_MAP_RGBA)
		{
			bytesPerVoxel = 4;
			return ColorModel.getRGBdefault();
		} else if (mapType == TYPE_GRAD_GA)
		{
			bytesPerVoxel = 4;
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			int[] nBits =
			{ 8 };
			return new ComponentColorModel(cs, nBits, false, false,
					Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		} else if (mapType == TYPE_GRAD_RGBA)
		{
			bytesPerVoxel = 4;
			return ColorModel.getRGBdefault();
		}
		return null;
	}

	public int getRGBAGradColor(int val, int grad)
	{
		return Color.WHITE.getRed();
	}

	public void setRGBA(int mapIndex, int red, int green, int blue, int alpha)
	{
		colorMapping[mapIndex] = (alpha & 0xFF) << 24 | (red & 0xFF) << 16
				| (green & 0xFF) << 8 | (blue & 0xFF);
	}

	public void setGA(int mapIndex, int gray, int alpha)
	{
		colorMapping[mapIndex] = (alpha & 0xFF) << 24 | (gray & 0xFF) << 16
				| (gray & 0xFF) << 8 | (gray & 0xFF);
	}

	public int getRGBA(int mapIndex)
	{
		return colorMapping[mapIndex];
	}

	public byte getAlpha(int mapIndex)
	{
		return (byte) (colorMapping[mapIndex] >> 24);
	}

	public byte getRed(int mapIndex)
	{
		return (byte) (colorMapping[mapIndex] >> 16);
	}

	public byte getGreen(int mapIndex)
	{
		return (byte) (colorMapping[mapIndex] >> 16);
	}

	public static void main(String[] data)
	{
		byte rgb = 10;
		Colormap map = new Colormap()
		{
		};
		map.setRGBA(10, 11, 0, 0, 50);
		System.out.println(BinaryToolkit.getBinaryString(map.getRed(10))
				+ " : "
				+ BinaryToolkit
						.getBinaryString(map.getRGBA(10), Integer.SIZE));
		System.out.print("\t" + map.getAlpha(10));

	}

	public int getBytesPerVoxel()
	{
		return bytesPerVoxel;
	}
}
