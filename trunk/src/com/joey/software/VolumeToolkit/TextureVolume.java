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

import javax.swing.JOptionPane;

public abstract class TextureVolume implements VolRendConstants
{

	// Attrs we care about
	ColormapChoiceAttr colorModeAttr;

	ToggleAttr texColorMapAttr;

	Volume volume;

	Colormap cmap = null;

	boolean useCmap = false;;

	boolean useTextureColorTable = false;

	boolean texColorMapEnable = false;

	boolean texColorMapAvailable = true;

	int[][] texColorMap;

	static final int RELOAD_NONE = 0;

	static final int RELOAD_VOLUME = 1;

	static final int RELOAD_CMAP = 2;

	static final int RED = 0;

	static final int GREEN = 1;

	static final int BLUE = 2;

	static final int ALPHA = 3;

	// cache for update()
	int volEditId = -1;

	int cmapEditId = -1;

	boolean timing = false;

	// NumberFormat numFormatter = null;

	boolean volumeReloadNeeded = true;

	boolean tctReloadNeeded = true;

	public TextureVolume(Context context, Volume volume)
	{
		// timing = Boolean.getBoolean("timing");
		this.volume = volume;
		colorModeAttr = (ColormapChoiceAttr) context.getAttr("Color Mode");
		texColorMapAttr = (ToggleAttr) context.getAttr("Tex Color Map");
		texColorMapEnable = texColorMapAttr.getValue();
		texColorMap = new int[4][];
		texColorMap[0] = new int[256];
		texColorMap[1] = new int[256];
		texColorMap[2] = new int[256];
		texColorMap[3] = new int[256];
	}

	Object lock = new Object();

	int update()
	{
		synchronized (lock)
		{

			//System.out.println("TextureVolume : Update() : ["
			//		+ Thread.currentThread().getName() + "]");
			// Exception e = new Exception();
			// e.printStackTrace();
			int newVolEditId = -1;
			if ((newVolEditId = volume.update()) != volEditId)
			{
				volEditId = newVolEditId;
				volumeReloadNeeded = true;
			}
			boolean newTexColorMapEnable = texColorMapAttr.getValue();
			int newCmapEditId = -1;
			if (colorModeAttr.getColormap() != cmap)
			{
				cmap = colorModeAttr.getColormap();
				if (cmap != null)
				{
					cmapEditId = cmap.update();
				} else
				{
					cmapEditId = -1;
				}
				if (texColorMapAvailable && texColorMapEnable)
				{
					tctReloadNeeded = true;
					useCmap = false;
					useTextureColorTable = true;
				} else
				{
					if (cmap != null)
					{
						useCmap = true;
					} else
					{
						useCmap = false;
					}
					useTextureColorTable = false;
					volumeReloadNeeded = true;
				}
			} else if ((cmap != null)
					&& (((newCmapEditId = cmap.update()) != cmapEditId) || (texColorMapEnable != newTexColorMapEnable)))
			{
				if (texColorMapAvailable && newTexColorMapEnable)
				{
					tctReloadNeeded = true;
					useCmap = false;
					useTextureColorTable = true;
					if (!texColorMapEnable)
					{
						/*
						 * previously loaded with color table, need to reload
						 * w/o color table
						 */
						volumeReloadNeeded = true;
					}
				} else
				{
					useCmap = true;
					useTextureColorTable = false;
					volumeReloadNeeded = true;
				}
				cmapEditId = newCmapEditId;
				texColorMapEnable = newTexColorMapEnable;
			}

			if (volumeReloadNeeded)
			{
				// If there volume fails to load just return
				if (!volumeReload())
				{
					return RELOAD_NONE;
				}
				tctReload();
				return RELOAD_VOLUME;
			} else if (tctReloadNeeded)
			{
				tctReload();
				return RELOAD_CMAP;
			} else
			{
				return RELOAD_NONE;
			}
		}
	}

	boolean volumeReload()
	{
		//System.out.println("VolumeReload:TextureVolume : ["
		//		+ Thread.currentThread() + "]");
		try
		{
			synchronized (lock)
			{

				if (volume.hasData())
				{
					long start = 0;
					if (timing)
					{
						start = System.currentTimeMillis();
					}

					loadTexture();

					if (timing)
					{
						long end = System.currentTimeMillis();
						double elapsed = (end - start) / 1000.0;
						System.out.println("Texture load took " +
						/* numFormat */(elapsed) + " seconds");
					}
				}
			}
		} catch (OutOfMemoryError e)
		{
			e.printStackTrace();

			Thread t = new Thread()
			{
				@Override
				public void run()
				{
					String message = "There has been an error while attemping to render the current viewport\n"
							+ "This error is due to insufficient memory to render the viewport\n"
							+ "If this error persists :\n"
							+ "(1) - Reduce Render Quality\n"
							+ "(2) - Reduce the size of the dataset\n"
							+ "(3) - Increase the amount of ram in the Settings Tool\n"
							+ "(4) - Decrease Video Ram amount in the Settings Tool";

					JOptionPane
							.showMessageDialog(null, message, "Out of Memory during Render Process", JOptionPane.ERROR_MESSAGE);
				}
			};
			t.start();

			volumeReloadNeeded = false;
			return false;

			// JOptionPane
			// .showMessageDialog(null, "Out of Memory!",
			// "Error loading texture", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e)
		{
			e.printStackTrace();

			System.out.println("Error: Texture Volume : loading Texture() ");
		}
		volumeReloadNeeded = false;
		//System.out.println("Finished : VolumeReload:TextureVolume : ["
		//		+ Thread.currentThread() + "]");
		return true;
	}

	int byteToInt(int byteValue)
	{
		return (int) ((byteValue / 255.0f) * Integer.MAX_VALUE);
	}

	void tctReload()
	{
		synchronized (lock)
		{

			if (((cmap != null) && texColorMapEnable))
			{
				for (int i = 0; i < 256; i++)
				{
					texColorMap[RED][i] = byteToInt((cmap.colorMapping[i] & 0x00ff0000) >> 16);
					texColorMap[GREEN][i] = byteToInt((cmap.colorMapping[i] & 0x0000ff00) >> 8);
					texColorMap[BLUE][i] = byteToInt((cmap.colorMapping[i] & 0x000000ff));
					texColorMap[ALPHA][i] = byteToInt((cmap.colorMapping[i] & 0xff000000) >> 24);
				}
				useTextureColorTable = true;
			} else
			{
				useTextureColorTable = false;
			}
			tctReloadNeeded = false;
		}
	}

	abstract void loadTexture();
}
