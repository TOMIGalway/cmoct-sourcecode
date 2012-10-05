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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.InvalidParameterException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.imageToolkit.colorMapping.ColorMapTools;

public class SimpleColorChooser extends JPanel
{
	ColorPanel primaryColor = new ColorPanel(this);

	ColorPanel secondaryColor = new ColorPanel(this);

	boolean useSecondary;

	int numWide;

	int numHigh;

	ColorPanel[][] colors;

	public SimpleColorChooser(int wide, int high, Color primaryColor, Color secondaryColor, boolean useSecondary)
	{
		setPrimaryColor(primaryColor);
		setSecondaryColor(secondaryColor);
		setUseSecondary(useSecondary);
		setNumWide(wide);
		setNumHigh(high);
		createJPanel();
	}

	public void setColorPanelSize(int x, int y)
	{
		for (int i = 0; i < getNumWide(); i++)
		{
			for (int j = 0; j < getNumHigh(); j++)
			{
				colors[i][j].setSize(x, y);
			}
		}
		repaint();
	}

	public void createJPanel()
	{
		removeAll();
		setLayout(new BorderLayout());

		JPanel colorGridPanel = new JPanel(new GridLayout(getNumWide(),
				getNumHigh()));
		colors = new ColorPanel[getNumWide()][getNumHigh()];
		for (int i = 0; i < getNumWide(); i++)
		{
			for (int j = 0; j < getNumHigh(); j++)
			{
				colors[i][j] = new ColorPanel(this);
				JPanel tmp = new JPanel(new BorderLayout());
				tmp.setBorder(BorderFactory.createEtchedBorder());
				tmp.add(colors[i][j]);
				colorGridPanel.add(tmp);
			}
		}

		ColorSetter.setColors(colors, ColorSetter.TYPE_NORMAL);

		add(colorGridPanel, BorderLayout.CENTER);

	}

	public Color getPrimaryColor()
	{
		return primaryColor.getColor();
	}

	public void setPrimaryColor(Color color)
	{
		primaryColor.setColor(color);
	}

	public Color getSecondaryColor()
	{
		return secondaryColor.getColor();
	}

	public void setSecondaryColor(Color color)
	{
		secondaryColor.setColor(color);
	}

	public boolean isUseSecondary()
	{
		return useSecondary;
	}

	public void setUseSecondary(boolean useSecondary)
	{
		this.useSecondary = useSecondary;
	}

	public int getNumHigh()
	{
		return numHigh;
	}

	public void setNumHigh(int numHigh)
	{
		this.numHigh = numHigh;
	}

	public int getNumWide()
	{
		return numWide;
	}

	public void setNumWide(int numWide)
	{
		this.numWide = numWide;
	}

}

/**
 * This class is used to display a single color. It will allow the user to
 * select the color. if the user clicks int the panel
 * 
 * @author joey.enfield
 * 
 */
class ColorPanel extends JPanel implements MouseListener
{
	Dimension size = new Dimension(20, 20);

	SimpleColorChooser owner;

	Color color;

	public ColorPanel(SimpleColorChooser owner, Color color)
	{
		setOwner(owner);
		setColor(color);
		addMouseListener(this);
		setSize(size);
	}

	public ColorPanel(SimpleColorChooser owner)
	{
		this(owner, Color.BLACK);
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		setBackground(color);
		super.paintComponent(g1);
	}

	public void setOwner(SimpleColorChooser owner)
	{
		this.owner = owner;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getSource() == owner)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				getOwner().setPrimaryColor(getColor());
			} else if (e.getButton() == MouseEvent.BUTTON3)
			{
				getOwner().setSecondaryColor(getColor());
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
		repaint();
	}

	public SimpleColorChooser getOwner()
	{
		return owner;
	}

	@Override
	public Dimension getSize()
	{
		return size;
	}

	@Override
	public void setSize(Dimension size)
	{
		this.size = size;
		setMaximumSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
	}
}

/*******************************************************************************
 * This class is a helper class that can be used to set the color of current
 * color panel.
 * 
 * @author joey.enfield
 * 
 */
class ColorSetter
{
	public static final int TYPE_NORMAL = 0;

	public static void setColors(ColorPanel[][] colors, int type)
	{
		if (type == TYPE_NORMAL)
		{
			ColorMap map = ColorMap.getColorMap(ColorMap.TYPE_1);
			int size = colors.length * colors[0].length;
			if (size > 256)
			{
				throw new InvalidParameterException(
						"Error:maxium colors is 256");
			}
			int count = 0;
			for (int i = 0; i < colors.length; i++)
			{
				for (int j = 0; j < colors[0].length; j++)
				{
					int val = (int) ((((double) size - count++) / size) * 256);
					System.out.println(val);
					System.out.println(count);
					System.out.println(size);
					colors[i][j].setColor(map.getColor(val));
				}
			}

		}
	}
}

class testing
{
	public static void main(String input[])
	{
		SimpleColorChooser color = new SimpleColorChooser(2, 5, Color.BLACK,
				Color.WHITE, true);
		ImagePanel p = new ImagePanel(ColorMapTools
				.getColorMappedImage(ImageOperations
						.getGrayTestImage(100, 100, 1), ColorMap
						.getColorMap(ColorMap.TYPE_1)));
		JPanel c = new JPanel(new FlowLayout(FlowLayout.LEFT));
		c.add(color);
		c.add(p);
		JFrame f = FrameFactroy.getFrame(c);
		f.setSize(600, 480);
		f.setVisible(true);
	}

}
