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
package com.joey.software.movementToolkit;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class ShapeMovementControler extends MovementPanelAdapter
{
	Shape shape = new Rectangle();

	@Override
	public void moveDownPressed(MovementPanel owner)
	{
		if (shape instanceof Rectangle)
		{
			Rectangle r = (Rectangle) shape;
			r.y += owner.getMovementAmount();
		}
	}

	@Override
	public void moveLeftPressed(MovementPanel owner)
	{
		if (shape instanceof Rectangle)
		{
			Rectangle r = (Rectangle) shape;
			r.x -= owner.getMovementAmount();
		}
	}

	@Override
	public void moveRightPressed(MovementPanel owner)
	{
		if (shape instanceof Rectangle)
		{
			Rectangle r = (Rectangle) shape;
			r.x += owner.getMovementAmount();
		}
	}

	@Override
	public void moveUpPressed(MovementPanel owner)
	{
		if (shape instanceof Rectangle)
		{
			Rectangle r = (Rectangle) shape;
			r.y -= owner.getMovementAmount();
		}
	}

	@Override
	public void scaleDownPressed(MovementPanel owner)
	{
		if (shape instanceof Rectangle)
		{
			Rectangle r = (Rectangle) shape;
			r.x += owner.getScaleIncrement();
			r.y += owner.getScaleIncrement();
			r.width -= 2 * owner.getScaleIncrement();
			r.height -= 2 * owner.getScaleIncrement();
		}
	}

	@Override
	public void scaleUpPressed(MovementPanel owner)
	{
		if (shape instanceof Rectangle)
		{
			Rectangle r = (Rectangle) shape;
			r.x -= owner.getScaleIncrement();
			r.y -= owner.getScaleIncrement();
			r.width += 2 * owner.getScaleIncrement();
			r.height += 2 * owner.getScaleIncrement();
		}
	}

	public static void main(String input[])
	{
		ShapeMovementControler controler = new ShapeMovementControler();
		Rectangle rect = new Rectangle(50, 50, 200, 200);
		controler.shape = rect;

		MovementPanel panel = new MovementPanel();
		panel.addListner(controler);

		ROIPanel p = new ROIPanel(true);
		p.addRegion(rect);

		JPanel holder = new JPanel(new FlowLayout());
		holder.add(panel);

		FrameFactroy.getFrame(holder);

	}

	public Shape getShape()
	{
		return shape;
	}

	public void setShape(Shape shape)
	{
		this.shape = shape;
	}
}
