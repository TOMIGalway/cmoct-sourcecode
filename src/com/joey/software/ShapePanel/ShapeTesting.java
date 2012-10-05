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
package com.joey.software.ShapePanel;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;


public class ShapeTesting extends Widget
{
	Rectangle rec = new Rectangle(50, 50, 100, 100);

	Rectangle2D.Float topLeft = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recB = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recC = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recD = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recE = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recF = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recG = new Rectangle2D.Float(20, 20, 100, 100);

	Rectangle2D.Float recH = new Rectangle2D.Float(20, 20, 100, 100);

	Point mouse = new Point();

	@Override
	public void drawWidget(Graphics2D g)
	{
		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		// Draw Boundary
		drawOuterBounds(g, getBounds());
		drawNodes(g);
	}

	public void drawNodes(Graphics2D g)
	{
		drawPoint(g, rec.x, rec.y, 5);
		drawPoint(g, rec.x + rec.width, rec.y, 5);
		drawPoint(g, rec.x, rec.y + rec.height, 5);
		drawPoint(g, rec.x + rec.width, rec.y + rec.height, 5);
	}

	public static void drawPoint(Graphics2D g, float x, float y, float rad)
	{
		Ellipse2D.Float cir = new Ellipse2D.Float(x - rad, y - rad, 2 * rad,
				2 * rad);

		g.setColor(Color.WHITE);
		g.fill(cir);
		g.setColor(Color.BLACK);
		g.draw(cir);

	}

	public void drawOuterBounds(Graphics2D g, Rectangle r)
	{
		int size = 20;

		g.setColor(Color.pink);
		topLeft.setRect(r.x - size, r.y - size, size, size);
		recB.setRect(r.x, r.y - size, r.width, size);
		recC.setRect(r.x + r.width, r.y + r.height, size, size);
		recD.setRect(r.x - size, r.y, size, r.height);
		recE.setRect(r.x, r.y - size, r.width, size);
		recF.setRect(r.x - size, r.y + r.height, size, size);
		// recG.setRect(r.x+r.width, r.y, size, r.height);
		recH.setRect(r.x + r.height, r.y - size, size, size);

		g.draw(topLeft);
		g.draw(recB);
		g.draw(recC);
		g.draw(recD);
		g.draw(recE);
		g.draw(recF);
		g.draw(recG);
		g.draw(recH);

		g.setColor(Color.red);
		g.draw(r);

	}

	@Override
	public Rectangle getBounds()
	{
		// TODO Auto-generated method stub
		return rec;
	}

	public static void main(String input[])
	{
		BufferedImage img = ImageOperations.getBi(600);
		ImageOperations.setImage(Color.WHITE, img);
		Graphics2D g = img.createGraphics();

		ShapeTesting test = new ShapeTesting();
		test.drawWidget(g);

		FrameFactroy.getFrame(img);

	}
}
