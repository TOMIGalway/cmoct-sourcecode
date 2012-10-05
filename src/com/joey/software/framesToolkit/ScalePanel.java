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
package com.joey.software.framesToolkit;


import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class ScalePanel extends JPanel
{
	AffineTransform trans = new AffineTransform();

	public ScalePanel()
	{

	}

	public void setScale(double x, double y)
	{
		trans.setToScale(x, y);
	}

	@Override
	protected void paintChildren(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		trans.concatenate(g.getTransform());
		g.setTransform(trans);
		super.paintChildren(g);
	}
}

class testing
{
	public static void main(String input[])
	{
		ScalePanel panel = new ScalePanel();
		ImagePanel imgPanel = new ImagePanel();
		BufferedImage image = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);

		ImageOperations.fillWithRandomColorSquares(3, 3, image);
		imgPanel.setImage(image);

		panel.setLayout(new BorderLayout());
		panel.add(imgPanel);
		panel.setScale(2, 2);

		JFrame f = FrameFactroy.getFrame(panel);
		f.setSize(800, 600);
		f.setVisible(true);
	}
}
