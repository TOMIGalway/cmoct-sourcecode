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
package com.joey.software.regionSelectionToolkit.controlers;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;

import com.joey.software.regionSelectionToolkit.ROIPanel;


public abstract class ROIControler extends MouseAdapter implements KeyListener
{
	ROIPanel panel;

	public abstract void draw(Graphics2D g);

	public ROIControler(ROIPanel panel)
	{
		setPanel(panel);
	}

	public void setListening(boolean listening)
	{
		if (listening)
		{
			panel.addMouseListener(this);
			panel.addMouseMotionListener(this);
			panel.addMouseWheelListener(this);
			panel.addKeyListener(this);
		} else
		{
			panel.removeMouseListener(this);
			panel.removeMouseMotionListener(this);
			panel.removeMouseWheelListener(this);
			panel.removeKeyListener(this);
		}
	}

	public abstract JPanel getControlPanel();
	
	public ROIPanel getPanel()
	{
		return panel;
	}

	public void setPanel(ROIPanel panel)
	{
		this.panel = panel;
		panel.setControler(this);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

}
