package com.joey.software.imageToolkit.zoomTool;


import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class ZoomPanel extends JPanel
{
	JPanel childPanel;

	JPanel childPanelHolder = new JPanel();

	AffineTransform transform = new AffineTransform();

	public ZoomPanel()
	{
		setChildPanel(new JPanel());
		createJPanel();
	}

	public void createJPanel()
	{
		removeAll();
		setLayout(new BorderLayout());
		add(childPanelHolder, BorderLayout.CENTER);

	}

	public void setZoom(double x, double y)
	{
		transform.setToScale(x, y);
	}

	public double getZoomX()
	{
		return 1;
	}

	public double getZoomY()
	{
		return 1;
	}

	public Point2D.Double getZoom()
	{
		Point2D.Double p = new Point2D.Double(1, 1);
		transform.transform(p, p);
		return p;
	}

	public Point2D getTransformToChild(Point2D p)
	{
		Point2D result = (Point2D) p.clone();
		transform.transform(p, result);
		return result;
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		Graphics2D tG = (Graphics2D) g.create();

		// g.getTransform().concatenate(transform);
		super.paintComponent(g);
	}

	@Override
	protected void paintChildren(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		Graphics2D tG = (Graphics2D) g.create();
		tG.setTransform(transform);
		super.paintComponent(tG);
		super.paintChildren(tG);
	}

	public JPanel getChildPanel()
	{
		return childPanel;
	}

	public void setChildPanel(JPanel childPanel)
	{
		this.childPanel = childPanel;
		updateChildPanel();
	}

	public void updateChildPanel()
	{
		childPanelHolder.removeAll();
		childPanelHolder.setLayout(new BorderLayout());
		childPanelHolder.setBorder(BorderFactory.createTitledBorder(""));
		childPanelHolder.add(childPanel);
		childPanelHolder.repaint();
	}

	public static void main(String input[])
	{
		JPanel zoomHolder = new JPanel();
		ZoomPanel zoomPanel = new ZoomPanel();
		ImagePanel imagePanel = new ImagePanel();
		BufferedImage image = ImageOperations.getBi(100);
		ImageOperations.fillWithRandomColorSquares(3, 3, image);

		imagePanel.setImage(image);
		imagePanel.setPanelType(ImagePanel.TYPE_NORMAL);

		zoomPanel.setChildPanel(imagePanel);
		zoomPanel.setZoom(2, 1);

		zoomHolder.setLayout(new BorderLayout());
		zoomHolder.add(zoomPanel, BorderLayout.CENTER);

		JScrollPane zoomScroll = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		zoomScroll.setViewportView(zoomHolder);

		JPanel tmp = new JPanel();
		tmp.setLayout(new BorderLayout());
		tmp.add(zoomScroll);

		JFrame f = FrameFactroy.getFrame(tmp);
	}
}
