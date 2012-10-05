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
package com.joey.software.imageAlignment;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class ImageCanvas extends Canvas
{
	Image image;

	int width, height;

	public ImageCanvas()
	{
		this(new Dimension(100, 100));
	}

	public ImageCanvas(Dimension dim)
	{
		image = null;
		width = dim.width;
		height = dim.height;
	}

	public void setImage(Image image)
	{
		this.image = image;
		width = image.getWidth(this);
		height = image.getHeight(this);
		setSize(new Dimension(width, height));
	}

	@Override
	public synchronized Dimension getPreferredSize()
	{
		return new Dimension(width, height);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}

	@Override
	public void paint(Graphics g)
	{
		Dimension d = getSize();
		if (image != null)
			g.drawImage(image, 0, 0, this);
	}
}
