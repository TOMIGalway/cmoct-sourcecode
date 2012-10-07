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

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Widget
{
	public abstract Rectangle getBounds();

	public abstract void drawWidget(Graphics2D g);

}