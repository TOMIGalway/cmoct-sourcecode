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
package com.joey.software.regionSelectionToolkit;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.regionSelectionToolkit.controlers.EllipseControler;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;
import com.joey.software.regionSelectionToolkit.controlers.ROIControler;
import com.joey.software.regionSelectionToolkit.controlers.RectangleControler;


public class ROIPanel extends ImagePanel implements Serializable
{
	public static final int TYPE_RECTANGLE = 0;

	public static final int TYPE_OVAL = 1;

	public static final int TYPE_POLYGON = 2;

	Vector<Shape> regions = new Vector<Shape>();

	transient ROIControler controler;

	transient ROIControlPanel controlPanel;

	Vector<ROIPanelListner> listner = new Vector<ROIPanelListner>();

	boolean allowMultipleROI = true;

	Color regionColor = Color.BLACK;

	Color highlightColor = Color.RED;

	int highlightedRegion = 0;

	float alpha = 1f;

	boolean useAlpha = false;

	boolean useXORMode = false;

	public ROIPanel(boolean allowMultipleROI, int controlerType)
	{
		super();
		setAllowMultipleROI(allowMultipleROI);
		setControler(controlerType);
		setScale(1, 1);
	}

	public ROIPanel(boolean allowMultipeROI)
	{
		this(allowMultipeROI, TYPE_RECTANGLE);
	}

	public boolean isUseAlpha()
	{
		return useAlpha;
	}

	public void setUseAlpha(boolean useAlpha)
	{
		this.useAlpha = useAlpha;
	}

	public Shape getSelectedShape()
	{
		try
		{
			return regions.get(getHighlightedRegion());
		} catch (Exception e)
		{
			return null;
		}
	}

	public void addROIPanelListner(ROIPanelListner list)
	{
		listner.add(list);
	}

	public void removeROIPanelListner(ROIPanelListner list)
	{
		listner.remove(list);
	}

	@Override
	public void paintOverlay(Graphics2D g)
	{
		// TODO Auto-generated method stub

		if (useAlpha)
		{
			g.setComposite(AlphaComposite
					.getInstance(AlphaComposite.SRC_OVER, alpha));
		}

		if (useXORMode)
		{
			g.setXORMode(regionColor);
		}
		g.setColor(regionColor);

		for (Shape s : regions)
		{
			if (regions.indexOf(s) == highlightedRegion)
			{
				g.setColor(highlightColor);
			} else
			{
				g.setColor(regionColor);
			}

			g.draw(s);
		}
		try
		{
			if (controler != null)
			{
				controler.draw((Graphics2D) g.create());
			}
		} catch (Exception e)
		{
			System.out.println("Error Drawing in ROIPanel : "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public void updateControlPanel()
	{
		if (getControlPanel() != null)
		{
			controlPanel.validateData();
		}
	}

	public ROIControler getControler()
	{
		return controler;
	}

	public void setControler(int type)
	{
		if (controler != null)
		{
			controler.setListening(false);
		}

		if (type == TYPE_RECTANGLE)
		{
			new RectangleControler(this);
		} else if (type == TYPE_POLYGON)
		{
			new PolygonControler(this);
		} else if (type == TYPE_OVAL)
		{
			new EllipseControler(this);
		}

	}

	public void setControler(ROIControler controler)
	{
		if (this.controler != null)
		{

			this.controler.setListening(false);
		}

		this.controler = controler;
		if (this.controler != null)
		{

			this.controler.setListening(true);
		}
		updateControlPanel();
		resetPresed();
		repaint();
	}

	public Vector<Shape> getRegions()
	{
		return regions;
	}

	public void setRegions(Vector<Shape> regions)
	{
		this.regions = regions;
	}

	public void addRegion(Shape region)
	{

		if (allowMultipleROI == false)
		{
			getRegions().removeAllElements();
		}
		getRegions().add(region);
		for (ROIPanelListner list : listner)
		{
			list.regionAdded(region);
			list.regionChanged();

		}
	}

	public void removeRegion(Shape region)
	{
		getRegions().remove(region);
		for (ROIPanelListner list : listner)
		{
			list.regionRemoved(region);
			list.regionChanged();
		}
	}

	public void shapeChanged()
	{
		for (ROIPanelListner list : listner)
		{
			list.regionChanged();
		}
		repaint();
	}

	public boolean isAllowMultipleROI()
	{
		return allowMultipleROI;
	}

	public void setAllowMultipleROI(boolean allowMultipleROI)
	{
		this.allowMultipleROI = allowMultipleROI;
		updateControlPanel();
	}

	public Color getRegionColor()
	{
		return regionColor;
	}

	public void setRegionColor(Color regionColor)
	{
		this.regionColor = regionColor;
	}

	public ROIControlPanel getControlPanel()
	{
		return controlPanel;
	}

	public void setControlPanel(ROIControlPanel controlPanel)
	{
		this.controlPanel = controlPanel;
	}

	public Color getHighlightColor()
	{
		return highlightColor;
	}

	public void setHighlightColor(Color highlightColor)
	{
		this.highlightColor = highlightColor;
	}

	public int getHighlightedRegion()
	{
		return highlightedRegion;
	}

	public void setHighlightedRegion(int highlightedRegion)
	{
		this.highlightedRegion = highlightedRegion;
		repaint();
	}

	public float getAlpha()
	{
		return alpha;
	}

	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}

}

class test
{
	public static void main(String input[]) throws IOException
	{
		ROIPanel panel = new ROIPanel(true);
		BufferedImage img = ImageOperations.getBi(1000, 1000);

		panel.setControler(new PolygonControler(panel));
		ImageOperations.setImageColor(Color.WHITE, img);
		panel.setImage(img);
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(panel, BorderLayout.CENTER);
		tmp.add(new ROIControlPanel(panel), BorderLayout.WEST);
		JFrame f = FrameFactroy.getFrame(tmp);
		f.setSize(600, 480);
		f.setVisible(true);

	}
}
