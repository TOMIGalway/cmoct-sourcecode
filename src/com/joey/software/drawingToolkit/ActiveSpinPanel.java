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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;


public class ActiveSpinPanel extends JPanel
{
	public static void main(String input[])
	{
		FrameFactroy.getFrame(new ActiveSpinPanel());
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);

		Graphics2D g = (Graphics2D) g1;

		int wide = getWidth();
		int high = getHeight();

		int dW = (int) (wide / 6f);
		int dH = (int) (high / 6f);

		Ellipse2D.Double cir = new Ellipse2D.Double(0, 0, dW, dH);
		cir.setFrameFromCenter(dW, dH, dW, dH);

		g.setColor(Color.BLUE);
		g.fill(cir);
	}
}
