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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.mathsToolkit.MathsToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class LineControler extends PolygonControler
{
	boolean drawLines = true;

	boolean visible = true;

	int width = 20;

	public LineControler(ROIPanel panel)
	{

		super(panel);
		setMaxPoints(2);
		addPoint(new Point2D.Double(0, 0));
		addPoint(new Point2D.Double(0, 0));
	}

	/**
	 * This is going to draw a line perpendicular to the<br>
	 * line between p1 and p2 <BR>
	 * (P1A - P1B) and (P2A - P2B) withing the image<BR>
	 * We say that the image is made up of line L1,L2,L3,L4<BR>
	 * IA-----P1A----L1---P2A------------IB <BR>
	 * |......#............#.............| <BR>
	 * |......#............#.............| <BR>
	 * |......P1***********P2............| <BR>
	 * L2.....#............#...........L3 <BR>
	 * |......#............#.............| <BR>
	 * |......#............#.............| <BR>
	 * |......#............#.............| <BR>
	 * IC-----P1B---L4----P2B-----------ID <BR>
	 */
	@Override
	public void draw(Graphics2D g)
	{
		if (!visible)
		{
			return;
		}
		if (drawLines)
		{
			if (points.size() == 2)
			{

				Shape oringalClip = g.getClip();
				Stroke oldStroke = g.getStroke();
				RenderingHints oldHints = g.getRenderingHints();

				GraphicsToolkit
						.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
				g.setClip(0, 0, panel.getImage().getWidth(), panel.getImage()
						.getHeight());
				g.setStroke(new BasicStroke((float) (1f / panel.getScale())));
				double Length = width / panel.getScale();
				Point2D.Double p1 = (Double) points.get(0).clone();
				Point2D.Double p2 = (Double) points.get(1).clone();

				g.setColor(panel.getHighlightColor());
				Point2D.Double dP1 = new Point2D.Double(p1.y - p2.y, p2.x
						- p1.x);// Looks funny but correct.
				MathsToolkit.normalise(dP1);
				dP1 = MathsToolkit.scale(dP1, Length);
				Line2D.Double l1 = new Line2D.Double((p1.x + dP1.x),
						(p1.y + dP1.y), (p1.x - dP1.x), (p1.y - dP1.y));
				g.draw(l1);

				g.setColor(panel.getHighlightColor());
				Point2D.Double dP2 = new Point2D.Double(p2.y - p1.y, p1.x
						- p2.x);// Looks funny but correct.
				MathsToolkit.normalise(dP2);
				dP2 = MathsToolkit.scale(dP2, Length);
				l1 = new Line2D.Double((p2.x + dP2.x), (p2.y + dP2.y),
						(p2.x - dP2.x), (p2.y - dP2.y));
				g.draw(l1);

				g.setClip(oringalClip);
				g.setStroke(oldStroke);
				g.setRenderingHints(oldHints);
			}

		}

		super.draw(g);

	}

	public double getSize()
	{
		if (points.size() == 2)
		{
			Point2D.Double p1 = points.get(0);
			Point2D.Double p2 = points.get(1);

			return p1.distance(p2);
		}
		return 0;
	}

	public boolean isDrawLines()
	{
		return drawLines;
	}

	public void setDrawLines(boolean drawLines)
	{
		this.drawLines = drawLines;
		panel.repaint();
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
		panel.repaint();
	}

}
