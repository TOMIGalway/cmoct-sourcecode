package com.joey.software.imageToolkit;


import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import com.joey.software.geomertyToolkit.ScaleToolkit;

/**
 * @author Joeys
 * 
 * @version 1.0
 * 
 * @discription
 * 
 *              This is a class that extends Imagepanel that will allow the user
 *              to select a region of intreast. To get the curretn Region of
 *              intrest use getSelectedRegion()
 */
public class ImageROISelectorPanel extends ImagePanel implements MouseListener,
		MouseMotionListener
{
	/**
	 * This is the current seleced region of intread
	 */
	Rectangle selectedRegion = new Rectangle();

	/**
	 * This is the current active region(region being drawn)
	 */
	Rectangle activeRegion = new Rectangle();

	/**
	 * 
	 */
	Point clickPoint = new Point(0, 0);

	/**
	 * 
	 */
	boolean clickStarted = false;

	/**
	 * 
	 */
	boolean regionSet = false;

	/**
	 * 
	 */
	public ImageROISelectorPanel()
	{
		super();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * @param image
	 * @param panelType
	 */
	public ImageROISelectorPanel(BufferedImage image, int panelType)
	{
		super(image, panelType);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * @param image
	 */
	public ImageROISelectorPanel(BufferedImage image)
	{
		super(image);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public ImageROISelectorPanel(int panelType)
	{
		super(panelType);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * This will get the current Region that is seleced by the user
	 */
	public Rectangle getSelectedRegion()
	{
		return selectedRegion;
	}

	/**
	 * This will draw the active region on the screen
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (regionSet)
		{
			g2.setColor(Color.RED);
			g2.draw(ScaleToolkit
					.getScaled(selectedRegion, getXScale(), getYScale()));
		}
		if (clickStarted)
		{
			g2.setColor(Color.BLUE);
			g2.draw(ScaleToolkit
					.getScaled(activeRegion, getXScale(), getYScale()));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

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

	/**
	 * This will set the inital point and flag that the click has been placed
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		clickStarted = true;
		clickPoint = e.getPoint();
		repaint();

	}

	/**
	 * This will update the selectedRegion. It will scale the selectedRegion
	 * appropatly
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{

		clickStarted = false;
		regionSet = true;
		selectedRegion.x = clickPoint.x;
		selectedRegion.y = clickPoint.y;
		selectedRegion.width = e.getPoint().x - clickPoint.x;
		selectedRegion.height = e.getPoint().y - clickPoint.y;

		// Scale to take into accoutn the images scale
		selectedRegion = ScaleToolkit
				.getScaled(selectedRegion, 1 / getXScale(), 1 / getYScale());

		// Take into account that the width and height might me -ve
		if (selectedRegion.width < 0)
		{
			selectedRegion.x += selectedRegion.width;
			selectedRegion.width *= -1;
		}
		if (selectedRegion.height < 0)
		{
			selectedRegion.y += selectedRegion.height;
			selectedRegion.height *= -1;
		}
		repaint();

	}

	/**
	 * This will update the active region
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{

		activeRegion.x = clickPoint.x;
		activeRegion.y = clickPoint.y;
		activeRegion.width = e.getPoint().x - clickPoint.x;
		activeRegion.height = e.getPoint().y - clickPoint.y;
		activeRegion = ScaleToolkit
				.getScaled(activeRegion, 1 / getXScale(), 1 / getYScale());
		if (activeRegion.width < 0)
		{
			activeRegion.x += activeRegion.width;
			activeRegion.width *= -1;
		}
		if (activeRegion.height < 0)
		{
			activeRegion.y += activeRegion.height;
			activeRegion.height *= -1;
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		BufferedImage image1 = new BufferedImage(1000, 1000,
				BufferedImage.TYPE_4BYTE_ABGR);
		BufferedImage image2 = new BufferedImage(1000, 1000,
				BufferedImage.TYPE_4BYTE_ABGR);
		BufferedImage image3 = new BufferedImage(1000, 1000,
				BufferedImage.TYPE_4BYTE_ABGR);

		ImageOperations.fillWithRandomColorSquares(3, 3, image1);
		ImageOperations.fillWithRandomColorSquares(3, 3, image2);
		ImageOperations.fillWithRandomColorSquares(3, 3, image3);

		ImageROISelectorPanel image1Panel = new ImageROISelectorPanel(image1,
				ImagePanel.TYPE_NORMAL);
		ImageROISelectorPanel image2Panel = new ImageROISelectorPanel(image2,
				ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);
		ImageROISelectorPanel image3Panel = new ImageROISelectorPanel(image3,
				ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

		image1Panel.setBorder(BorderFactory.createTitledBorder("Image 1"));
		image2Panel.setBorder(BorderFactory.createTitledBorder("Image 2"));
		image3Panel.setBorder(BorderFactory.createTitledBorder("Image 3"));

		JFrame frame = new JFrame("Image test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),
				BoxLayout.Y_AXIS));
		frame.getContentPane().add(image1Panel);
		frame.getContentPane().add(image2Panel);
		frame.getContentPane().add(image3Panel);

		frame.setVisible(true);

	}

}
