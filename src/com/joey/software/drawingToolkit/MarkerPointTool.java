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
package com.joey.software.drawingToolkit;


import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImagePanel;

public class MarkerPointTool
{
	RadialGradientPaint radGrad = null;

	Point2D p = new Point2D.Float();

	float size = 1;

	public void main(String input[])
	{
		BufferedImage img = new BufferedImage(600, 600,
				BufferedImage.TYPE_INT_ARGB);
		MarkerPointTool tool = new MarkerPointTool();

		MarkerPointTool toolA = new MarkerPointTool();
		ImagePanel p = new ImagePanel(img);
		FrameFactroy.getFrame(p);

		while (true)
		{
			img = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

			tool.size = 20;
			toolA.size = 20;

			toolA.draw(img.createGraphics());
			tool.draw(img.createGraphics());

			p.setImage(img);

		}
	}

	public void draw(Graphics2D g)
	{

	}
}
