package com.joey.software.VolumeToolkit.Transferfunctions;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;
import com.joey.software.imageToolkit.colorMapping.ColorMapTools;
import com.joey.software.mathsToolkit.MathsToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;


public class TransferWindow extends PolygonControler
{

	JTextField name = new JTextField(15);

	ColorMap colorMap = ColorMap.getColorMap(ColorMap.TYPE_FIRE_BALL);

	JSpinner size = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));

	JCheckBox visible = new JCheckBox("Hidden");

	JSlider alphaValue = new JSlider(0, 1000);

	JButton changeMap = new JButton("MAP");

	JPanel controlPanel = new JPanel();

	ImagePanel mapPreview = new ImagePanel(ImageOperations.getBi(256, 2));

	// Speed up image drawing
	Point2D.Double pA = new Point2D.Double();

	Point2D.Double pB = new Point2D.Double();

	Point2D.Double pCA = new Point2D.Double();

	Point2D.Double pCB = new Point2D.Double();

	boolean showing = false;

	static int regionCount = 1;

	public TransferWindow(ROIPanel panel)
	{
		super(panel);
		name.setText("Region :" + regionCount++);
		createControls();
	}

	public void updateMapPreview()
	{
		ColorMapTools.setLinearMap(mapPreview.getImage(), colorMap);
		mapPreview.repaint();
		getPanel().shapeChanged();
	}

	@Override
	public String toString()
	{
		return name.getText();
	}

	public void createControls()
	{
		if (controlPanel == null)
		{
			controlPanel = new JPanel();
		}

		mapPreview.setPanelType(ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);
		mapPreview.setPreferredSize(new Dimension(255, 10));
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(name);
		controlPanel.add(size);
		controlPanel.add(visible);
		controlPanel.add(alphaValue);
		controlPanel.add(mapPreview);
		controlPanel.add(changeMap);

		size.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				getPanel().shapeChanged();
			}
		});

		visible.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				getPanel().shapeChanged();
			}
		});

		alphaValue.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (alphaValue.getValueIsAdjusting())
				{
					getPanel().shapeChanged();
				}

			}
		});
		changeMap.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				colorMap = ColorMapTools.showUserChoicePanel();
				updateMapPreview();
			}
		});
	}

	@Override
	public JPanel getControlPanel()
	{
		return controlPanel;
	}

	@Override
	public void draw(Graphics2D g)
	{
		int pxlCount = -1;
		int lineCount = -1;
		Point2D.Double last = null;
		boolean first = true;

		drawPath(g);

		if (showing)
		{
			super.draw(g);
		}
	}

	public void drawPath(Graphics2D g)
	{
		Path2D.Double path = new Path2D.Double();

		// Get orignal Settings
		Stroke old = g.getStroke();
		Paint paint = g.getPaint();
		Composite comp = g.getComposite();
		RenderingHints hints = g.getRenderingHints();

		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);
		/**
		 * Work out the position for each point along the path
		 */
		float[] pos = new float[points.size()];
		for (int i = 0; i < points.size() - 1; i++)
		{
			if (i == 0)
			{
				pos[0] = 0;
			}
			pos[i + 1] = pos[i]
					+ (float) points.get(i).distance(points.get(i + 1));
		}

		// Work out position of each point along the line
		for (int i = 0; i < points.size(); i++)
		{
			pos[i] = pos[i] / pos[points.size() - 1];
		}

		// Get linesize and then convert to panel space
		double lineSize = (Integer) size.getValue();

		// Draw points on image
		for (int i = 0; i < points.size() - 1; i++)
		{
			path = new Path2D.Double();

			pA.setLocation(points.get(i));
			pB.setLocation(points.get(i + 1));
			// transformImageToPanel(points.get(i), pA);
			// transformImageToPanel(points.get(i + 1), pB);

			if (i == 0)
			{
				double r = lineSize / pA.distance(pB);
				pCA = MathsToolkit.getLinePoint(pA, pB, -r / 2);

				if (i == points.size() - 2)
				{

					pCB = MathsToolkit.getLinePoint(pB, pA, -r / 2);
				} else
				{
					pCB.setLocation(pB);
				}
			} else if (i == points.size() - 2)
			{
				double r = lineSize / pA.distance(pB);
				pCA.setLocation(pA);
				pCB = MathsToolkit.getLinePoint(pB, pA, -r / 2);
			} else
			{
				pCA.setLocation(pA);
				pCB.setLocation(pB);
			}
			int steps = (int) ((pos[i + 1] - pos[i]) * 256);
			if (steps < 2)
			{
				steps = 2;
			}
			Color[] colDat = new Color[steps];
			float[] colPos = new float[steps];

			// Work out Colors to add
			colDat[0] = colorMap.getColorInterpolate(pos[i]);
			colPos[0] = 0;

			colDat[steps - 1] = colorMap.getColorInterpolate(pos[i + 1]);
			colPos[steps - 1] = 1;
			for (int j = 1; j < steps - 1; j++)
			{
				colPos[j] = j / (steps - 1f);
				colDat[j] = colorMap.getColorInterpolate(pos[i] + colPos[j]
						* (pos[i + 1] - pos[i]));
			}

			if (pA.distance(pB) < 1)
			{
				pB.x += 0.1;
			}
			g.setPaint(new LinearGradientPaint(pCA, pCB, colPos, colDat));

			g.setStroke(new BasicStroke((float) lineSize,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			path = new Path2D.Double();

			path.moveTo(pA.x, pA.y);
			path.lineTo(pB.x, pB.y);

			g.draw(path);

			g.setComposite(comp);
			g.setStroke(old);
			g.setPaint(paint);
			g.setRenderingHints(hints);

		}
	}

	public float getAlpha()
	{
		return (alphaValue.getValue()) / ((float) alphaValue.getMaximum());
	}

}