package com.joey.software.ShapePanel;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Widget
{
	public abstract Rectangle getBounds();

	public abstract void drawWidget(Graphics2D g);

}
