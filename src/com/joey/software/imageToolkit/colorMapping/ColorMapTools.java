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
package com.joey.software.imageToolkit.colorMapping;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.joey.software.VolumeToolkit.UserChoiceColorMap;
import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;


public class ColorMapTools
{
	public static final int X_AXIS = 0;

	public static final int Y_AXIS = 1;

	public static void main(String[] inputs) throws IOException
	{
		CreateColorMapData(inputs);
		//showUserChoicePanel();
		// testColorMaps(inputs);
	}

	public static void testColorMaps(String[] input) throws IOException
	{
		File inputFile = ImageFileSelector.getUserImageFile();
		BufferedImage source = ImageIO.read(inputFile);
		BufferedImage gray = ImageOperations.getGrayScale(source);
		BufferedImage mapped[] = new BufferedImage[5];
		for (int i = 0; i < mapped.length; i++)
		{
			mapped[i] = getColorMappedImage(source, ColorMap.getColorMap(i));
		}
		BufferedImage[] images =
		{ source, mapped[0], mapped[1], mapped[2], mapped[3], mapped[4] };
		JFrame f = FrameFactroy
				.getFrame(images, 2, 3, ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		f.setSize(800, 600);
		f.setVisible(true);
	}

	/**
	 * This will get the data to create a
	 * 
	 * @param input
	 * @throws IOException
	 */
	public static void CreateColorMapData(String[] input) throws IOException
	{

		File imageFile = FileSelectionField.getUserFile();
		BufferedImage orignal = ImageIO.read(imageFile);

		ArrayList<Color> colors = getColorVector(orignal, Y_AXIS);
		ColorMap colorMap = getColorMap(colors, "Testing");

		ColorMapChooser chooser = new ColorMapChooser();
		chooser.maps[0] = colorMap;
		chooser.selectedIndex = 0;
		
		UserChoiceColorMap map = new UserChoiceColorMap();
		map.mapChanged(chooser);
		System.out.println(map.getTextDataColorMap(false));
		
		JPanel panel = map.getUserSelectionPanel();

		panel.setPreferredSize(new Dimension(800, 600));
		JOptionPane.showConfirmDialog(null, panel);

		ColorMap newMap = new ColorMap(map.getTextDataColorMap(true));

		BufferedImage grayed = ImageOperations.getGrayTestImage(800, 600, 1);
		BufferedImage mapped = ImageOperations.getSameSizeImage(grayed);
		ColorMapTools.setColorMap(grayed, mapped, newMap);

		BufferedImage[] images =
		{ orignal, getImage(colors, 10, orignal.getHeight()), grayed, mapped };
		JFrame f = FrameFactroy.getFrame(images, 2, 2);
		f.setSize(800, 600);
		f.setVisible(true);

		System.out.println(map.getTextDataColorMap(false));
	}

	/**
	 * This will produce a color map from a vector of colors. it will set the
	 * name of the map to name. maxValue is the number of colors to create in
	 * the map .ie maxValue = 10 -> map of 10 colors
	 * 
	 * @param colors
	 * @param name
	 * @param maxValue
	 * @return
	 * 
	 */
	protected static ColorMap getColorMap(ArrayList<Color> colors, String name, int maxValue)
	{
		ColorMap map = new ColorMap();
		map.setMapName(name);
		int size = colors.size();
		for (int i = 0; i < maxValue; i++)
		{
			double d = (double) i / maxValue;
			int val = (int) (d * size);

			map.setColor(i, colors.get(val));
			// System.out.printf("Map Colours Index[%d], C[%d,%d,%d]\n", i,
			// map.getColor(i).getRed(), map.getColor(i).getGreen(),
			// map.getColor(i)
			// .getBlue());
		}

		return map;
	}

	public static ColorMap getColorMap(ArrayList<Color> colors, String name)
	{
		return getColorMap(colors, name, 256);
	}

	public static BufferedImage getImage(ArrayList<Color> col, int sizeColor, int high)
	{
		int wide = sizeColor * col.size();
		BufferedImage img = new BufferedImage(wide, high,
				BufferedImage.TYPE_INT_ARGB);
		Rectangle rect = new Rectangle(0, 0, sizeColor, high);
		Graphics2D g = img.createGraphics();
		for (int i = 0; i < col.size(); i++)
		{
			// System.out.printf("Array Image Index[%d], C[%d,%d,%d]\n", i, col
			// .get(i).getRed(), col.get(i).getGreen(), col.get(i)
			// .getBlue());
			rect.x = i * sizeColor;
			g.setColor(col.get(i));
			g.fill(rect);
		}

		return img;
	}

	public static BufferedImage getColorMappedImage(BufferedImage source, ColorMap map)
	{
		BufferedImage result = ImageOperations.getSameSizeImage(source);
		setColorMap(source, result, map);
		return result;

	}

	public static void setLinearMap(BufferedImage image, ColorMap map)
	{
		for (int x = 0; x < image.getWidth(); x++)
		{
			Color c = map.getColorInterpolate(x / (image.getWidth() - 1f));
			for (int y = 0; y < image.getHeight(); y++)
			{
				image.setRGB(x, y, c.getRGB());
			}
		}
	}

	public static BufferedImage getSampleMap(ColorMap m, int wide, int high)
	{
		BufferedImage mapped = ImageOperations.getBi(wide, high);
		setLinearMap(mapped, m);
		return mapped;

	}

	public static void setColorMap(BufferedImage source, BufferedImage result, ColorMap map)
	{
		if (source.getWidth() != result.getWidth()
				|| source.getHeight() != result.getHeight())
		{
			throw new InvalidParameterException(
					"Images must have same dimensions");
		}
		for (int x = 0; x < source.getWidth(); x++)
		{
			for (int y = 0; y < source.getHeight(); y++)
			{
				Color c = new Color(source.getRGB(x, y));
				int pxl = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

				Color newColor = map.getColor(pxl);

				// System.out
				// .printf("Set Map\tOld[%d,%d,%d] -> %d -> New[%d,%d,%d]\n", c
				// .getRed(), c.getGreen(), c.getBlue(), pxl, newColor
				// .getRed(), newColor.getGreen(), newColor
				// .getBlue());
				if (newColor == null)
				{
					System.out.println("here");
					newColor = map.getColor(0);
				}
				result.setRGB(x, y, newColor.getRGB());

			}
		}
	}

	/*
	 * This function will scan across an image and retrive a vectors of the
	 * colors from the image. It will scan through one axis(i.e x) and take the
	 * average value of the pixels on the other axis(i.e y). ###############
	 * #0#1#1#2#3#5#4# ############### #1#1#2#3#4#5#6# ############### Avg : 1 1
	 * 1 2 3 5 5
	 */
	public static ArrayList<Color> getColorVector(BufferedImage image, int axes)
	{
		ArrayList<Color> colors = new ArrayList<Color>();
		int red = 0;
		int green = 0;
		int blue = 0;
		int loopCount = 0;

		for (int x = 0; x < (axes == Y_AXIS ? image.getWidth() : image
				.getHeight()); x++)
		{
			loopCount = 0;
			red = 0;
			green = 0;
			blue = 0;
			for (int y = 0; y < (axes == Y_AXIS ? image.getHeight() : image
					.getWidth()); y++)
			{
				loopCount++;
				try
				{
					Color c;
					if (axes == Y_AXIS)
					{
						c = new Color(image.getRGB(x, y));
					} else
					{
						c = new Color(image.getRGB(y, x));
					}

					red += c.getRed();
					green += c.getGreen();
					blue += c.getBlue();

				} catch (Exception e)
				{
					System.out
							.printf("\nError Point[%d,%d] ImageSize(%d,%d)", x, y, image
									.getWidth(), image.getHeight());
					e.printStackTrace();
				}
			}
			if (loopCount == 0)
			{
				loopCount = 1;
			}
			red /= loopCount;
			green /= loopCount;
			blue /= loopCount;
			try
			{
				colors.add(new Color(red, green, blue));
			} catch (Exception e)
			{
				System.out.printf("\nC[%d,%d,%d]", red, green, blue);
			}

		}
		return colors;
	}

	public static ColorMap showUserChoicePanel()
	{
		JButton setButton = new JButton("Set");
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(setButton);

		final JDialog dialog = new JDialog((JFrame) null, true);
		dialog.setLocationByPlatform(true);
		dialog.setTitle("Select Color Scheme");
		ColorMapChooser choser = new ColorMapChooser();
		dialog.getContentPane().add(choser, BorderLayout.CENTER);
		dialog.getContentPane().add(p, BorderLayout.SOUTH);

		setButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialog.setVisible(false);

			}
		});
		dialog.pack();
		dialog.setVisible(true);
		return choser.getSelectedMap();
	}

}
