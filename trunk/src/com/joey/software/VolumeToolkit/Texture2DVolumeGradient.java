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

/*
 *	%Z%%M% %I% %E% %U%
 *
 * Copyright (c) 1996-1998 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.vecmath.Vector4f;

public class Texture2DVolumeGradient extends TextureVolume
{

	// sets of textures, one for each axis, sizes of the arrays are set
	// by the dimensions of the Volume
	Texture2D[] xTextures;

	Texture2D[] yTextures;

	Texture2D[] zTextures;

	TexCoordGeneration xTg = new TexCoordGeneration();

	TexCoordGeneration yTg = new TexCoordGeneration();

	TexCoordGeneration zTg = new TexCoordGeneration();

	ColorModel colorModel;

	WritableRaster raster;

	public Texture2DVolumeGradient(Context context, Volume volume)
	{
		super(context, volume);
	}

	@Override
	void loadTexture()
	{
		colorModel = cmap.getColorModel();

		long start = 0;
		if (timing)
		{
			start = System.currentTimeMillis();
		}
		xTextures = null;
		yTextures = null;
		zTextures = null;
		raster = null;

		loadAxis(Z_AXIS);

		loadAxis(Y_AXIS);

		loadAxis(X_AXIS);

		raster = null;
		if (timing)
		{
			long end = System.currentTimeMillis();
			double elapsed = (end - start) / 1000.0;
			System.out.println("Load took " + elapsed + " seconds");
		}
	}

	private void loadAxis(int axis)
	{
		int rSize = 0; // number of tex maps to create
		int sSize = 0; // s,t = size of texture map to create
		int tSize = 0;
		Texture2D[] textures = null;

		switch (axis)
		{
			case Z_AXIS:
				rSize = volume.zDim;
				sSize = volume.xTexSize;
				tSize = volume.yTexSize;
				textures = zTextures = new Texture2D[rSize];
				zTg = new TexCoordGeneration();
				zTg.setPlaneS(new Vector4f(volume.xTexGenScale, 0.0f, 0.0f,
						0.0f));
				zTg.setPlaneT(new Vector4f(0.0f, volume.yTexGenScale, 0.0f,
						0.0f));
				break;
			case Y_AXIS:
				rSize = volume.yDim;
				sSize = volume.xTexSize;
				tSize = volume.zTexSize;
				textures = yTextures = new Texture2D[rSize];
				yTg = new TexCoordGeneration();
				yTg.setPlaneS(new Vector4f(volume.xTexGenScale, 0.0f, 0.0f,
						0.0f));
				yTg.setPlaneT(new Vector4f(0.0f, 0.0f, volume.zTexGenScale,
						0.0f));
				break;
			case X_AXIS:
				rSize = volume.xDim;
				sSize = volume.yTexSize;
				tSize = volume.zTexSize;
				textures = xTextures = new Texture2D[rSize];
				xTg = new TexCoordGeneration();
				xTg.setPlaneS(new Vector4f(0.0f, volume.yTexGenScale, 0.0f,
						0.0f));
				xTg.setPlaneT(new Vector4f(0.0f, 0.0f, volume.zTexGenScale,
						0.0f));
				break;
		}

		raster = colorModel.createCompatibleWritableRaster(sSize, tSize);

		BufferedImage bImage = new BufferedImage(colorModel, raster, false,
				null);

		int[] intData = null;
		byte[] byteData = null;
		if (cmap.mapType == Colormap.TYPE_GRAD_RGBA)
		{
			intData = ((DataBufferInt) raster.getDataBuffer()).getData();
		} else
		{
			byteData = ((DataBufferByte) raster.getDataBuffer()).getData();
		}

		for (int i = 0; i < rSize; i++)
		{

			switch (axis)
			{
				case Z_AXIS:
					if (cmap.mapType == Colormap.TYPE_GRAD_RGBA)
					{
						volume.loadZRGBA(i, intData, cmap);

					} else
					{
						volume.loadZIntensity(i, byteData);
					}
					break;
				case Y_AXIS:
					if (cmap.mapType == Colormap.TYPE_GRAD_RGBA)
					{
						volume.loadYRGBA(i, intData, cmap);
					} else
					{
						volume.loadYIntensity(i, byteData);
					}
					break;
				case X_AXIS:
					if (cmap.mapType == Colormap.TYPE_GRAD_RGBA)
					{
						volume.loadXRGBA(i, intData, cmap);
					} else
					{
						volume.loadXIntensity(i, byteData);
					}
					break;
			}

			Texture2D tex;
			ImageComponent2D pArray;
			if (cmap.mapType == Colormap.TYPE_GRAD_RGBA)
			{

				tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, sSize,
						tSize);

				pArray = new ImageComponent2D(ImageComponent.FORMAT_RGBA,
						sSize, tSize);
			} else
			{
				tex = new Texture2D(Texture.BASE_LEVEL, Texture.INTENSITY,
						sSize, tSize);

				pArray = new ImageComponent2D(ImageComponent.FORMAT_CHANNEL8,
						sSize, tSize);
			}
			pArray.set(bImage);
			tex.setImage(0, pArray);
			tex.setEnable(true);
			tex.setMinFilter(Texture.NICEST);
			tex.setMagFilter(Texture.NICEST);

			// tex.setMinFilter(Texture.FASTEST);
			// tex.setMagFilter(Texture.FASTEST);

			// tex.setMinFilter(Texture.BASE_LEVEL_LINEAR);
			// tex.setMagFilter(Texture.BASE_LEVEL_LINEAR);
			tex.setBoundaryModeS(Texture.CLAMP);
			tex.setBoundaryModeT(Texture.CLAMP);

			textures[i] = tex;
		}

	}
}
