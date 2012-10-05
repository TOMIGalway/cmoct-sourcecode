package com.joey.software.framesToolkit;

import java.awt.GridBagConstraints;

public class GBag
{
	public static void setPos(GridBagConstraints c, int x, int y)
	{
		c.gridx = x;
		c.gridy = y;
	}

	public static void setFill(GridBagConstraints c, int fill)
	{
		c.fill = fill;
	}

	public static void setAnchor(GridBagConstraints c, int anchor)
	{
		c.anchor = anchor;
	}

	public static void setSize(GridBagConstraints c, int wide, int height)
	{
		c.gridwidth = wide;
		c.gridheight = height;
	}

	public static void setWeight(GridBagConstraints c, double x, double y)
	{
		c.weightx = x;
		c.weighty = y;
	}

	public static void setPad(GridBagConstraints c, int x, int y)
	{
		c.ipadx = x;
		c.ipady = y;
	}

	public static void reset(GridBagConstraints c)
	{
		GridBagConstraints n = new GridBagConstraints();
		c.anchor = n.anchor;
		c.fill = n.fill;
		c.gridheight = n.gridheight;
		c.gridwidth = n.gridwidth;
		c.gridx = n.gridx;
		c.gridy = n.gridy;
		c.insets = n.insets;
		c.ipadx = n.ipadx;
		c.ipady = n.ipady;
		c.weightx = n.weightx;
		c.weighty = n.weighty;
	}

}
