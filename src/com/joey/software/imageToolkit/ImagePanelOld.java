package com.joey.software.imageToolkit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JViewport;

import com.joey.software.drawingToolkit.GraphicsToolkit;


/**
 * @author Joey Enfield
 * 
 * @version alpha
 * 
 *          This is a simple panel that will draw a image as its backround.
 * 
 * 
 */
public class ImagePanelOld extends JPanel implements MouseWheelListener
{
	// This will draw the image to a 1:1 scale
	public static final int TYPE_NORMAL = 0;

	// This will fit the image into the panel. It will not distort the panel
	public static final int TYPE_SCALE_IMAGE_TO_PANEL = 1;

	// This will distort the image to fit in into the exact size of the panel
	public static final int TYPE_FIT_IMAGE_TO_PANEL = 2;

	// This allows the current scale to be vaired
	public static final int TYPE_CUSTOM_SCALE = 3;

	// The buffered Image to be displayed
	transient BufferedImage image = null;

	// This is the current mode the panel is running in
	int panelType = TYPE_NORMAL;

	int quality = GraphicsToolkit.HIGH_QUALITY;

	boolean mouseWheelZoomEnabled = false;

	/**
	 * These store the scale in each direction. They are determined in the
	 * PaintCompent
	 */
	double xScale = 1, yScale = 1;

	double xScaleWheelInc = 0.1, yScaleWheelInc = 0.1;

	boolean highRes = false;

	public ImagePanelOld()
	{
		super(true);
		setImage(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
	}

	/**
	 * This will creata a new ImagePanel with the given image as the backround.
	 * The image is displayed as a normal scaled image. i.e scale in x, y = 1.
	 * If the given image that is passed is null no image will be displayed
	 * 
	 * @param image
	 *            - The image to be displayed(If null no image will be
	 *            displayed)
	 */
	public ImagePanelOld(BufferedImage image)
	{
		this();
		// Set the image
		setImage(image);
	}

	/**
	 * 
	 * @param image
	 * @param panelType
	 */
	public ImagePanelOld(BufferedImage image, int panelType)
	{
		this(image);
		setPanelType(panelType);

	}

	public ImagePanelOld(int type)
	{
		this();
		setPanelType(type);
	}

	public void setAllowMouseControl()
	{

	}

	/**
	 * This will set the image that is to be displayed. it also sets the size of
	 * the panel to the size of the image.
	 * 
	 * @param image
	 */
	public void setImage(BufferedImage image)
	{
		if (image == null)
		{
			image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
		this.image = image;
		if (panelType == TYPE_NORMAL)
		{
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

		} else
		{
			setScale(xScale, yScale);
		}
		revalidate();
		repaint();
	}

	/**
	 * This will set the scale the the image is to be drawn at, NOTE: dont
	 * forget to set the panel type to Type custom scale first
	 * 
	 * @param x
	 * @param y
	 */

	public void setScale(double x, double y)
	{
		setScale(x, y, true);
	}

	public void setScale(double x, double y, boolean update)
	{
		// Check if the value of scale is changed
		if (xScale != x || yScale != y)
		{
			xScale = x;
			yScale = y;
		}

		Dimension size = new Dimension((int) (getImage().getWidth() * xScale),
				(int) (getImage().getHeight() * yScale));
		setPreferredSize(size);
		// setSize(size);

		if (update)
		{
			validate();
			try
			{
				((JComponent) this).revalidate();
			} catch (Exception e)
			{

			}
		}
	}

	/**
	 * This function will draw the image onto the graphics compoent
	 */
	@Override
	public void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		GraphicsToolkit.setRenderingQuality(g, getQuality());

		super.paintComponent(g);

		g.setClip(getBounds());

		if (panelType == TYPE_NORMAL)
		{
			g.drawImage(getImage(), 0, 0, this);
			xScale = 1;
			yScale = 1;
		} else if (panelType == TYPE_SCALE_IMAGE_TO_PANEL)
		{

			/**
			 * This will determine which scale(X or Y) is the smallest and draw
			 * the image with the scale
			 */

			xScale = (1 + (getWidth() - getImage().getWidth())
					/ (double) getImage().getWidth());
			yScale = (1 + (getHeight() - getImage().getHeight())
					/ (double) getImage().getHeight());

			if (xScale < yScale)
			{
				g
						.drawImage(getImage(), 0, 0, (int) (getImage()
								.getWidth() * xScale), (int) (getImage()
								.getHeight() * xScale), this);
				yScale = xScale;
			} else
			{
				g
						.drawImage(getImage(), 0, 0, (int) (getImage()
								.getWidth() * yScale), (int) (getImage()
								.getHeight() * yScale), this);
				xScale = yScale;
			}
		} else if (panelType == TYPE_FIT_IMAGE_TO_PANEL)
		{
			g.drawImage(getImage(), 0, 0, getWidth(), getHeight(), this);
			xScale = getWidth() / (double) getImage().getWidth();
			yScale = getHeight() / (double) getImage().getHeight();
		} else if (panelType == TYPE_CUSTOM_SCALE)
		{
			AffineTransform old = g.getTransform();
			g.setTransform(AffineTransform.getScaleInstance(xScale, yScale));
			g.drawImage(getImage(), null, null);

			g.setTransform(old);
		}

		setScale(xScale, yScale, false);

	}

	public final BufferedImage getImage()
	{
		return image;
	}

	public static void main(String[] args)
	{
		BufferedImage image1 = new BufferedImage(100, 100,
				BufferedImage.TYPE_4BYTE_ABGR);
		ImageOperations.fillWithRandomColorSquares(100, 100, image1);
		ImagePanel image1Panel = new ImagePanel(image1);
		// image1Panel.setMouseWheelZoomEnabled(true);

		// image1Panel.setBorder(BorderFactory.createTitledBorder("Image 1"));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(image1Panel);
		JFrame frame = new JFrame("Image test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

		frame.setVisible(true);

	}

	public int getPanelType()
	{
		return panelType;
	}

	public void setPanelType(int panelType)
	{
		this.panelType = panelType;
		if (panelType == TYPE_NORMAL)
		{
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		}

		try
		{
			getTopLevelAncestor().validate();
			getTopLevelAncestor().repaint();
		} catch (Exception e)
		{

		}
	}

	/**
	 * @return the xScale
	 */
	public double getXScale()
	{
		return xScale;
	}

	/**
	 * @return the yScale
	 */
	public double getYScale()
	{
		return yScale;
	}

	public boolean isOwnerScrollPanel()
	{
		if (getParent() instanceof JViewport)
		{
			return true;
		}
		return false;
	}

	/**
	 * This will process the mouse wheel movement.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getSource() == this)
		{
			if (getParent() instanceof JViewport)
			{
				JViewport view = (JViewport) getParent();
			}

			if (e.getWheelRotation() > 0)
			{
				setScale((1 - getXScaleWheelInc()) * getXScale(), (1 - getYScaleWheelInc())
						* getYScale());
			} else
			{
				setScale((1 + getXScaleWheelInc()) * getXScale(), (1 + getYScaleWheelInc())
						* getYScale());
			}
			repaint();
		}
	}

	public boolean isMouseWheelZoomEnabled()
	{
		return mouseWheelZoomEnabled;
	}

	public void setMouseWheelZoomEnabled(boolean mouseWheelZoomEnabled)
	{
		this.mouseWheelZoomEnabled = mouseWheelZoomEnabled;
		if (mouseWheelZoomEnabled)
		{
			setPanelType(ImagePanel.TYPE_CUSTOM_SCALE);
			addMouseWheelListener(this);
		} else
		{
			removeMouseWheelListener(this);
		}
	}

	private double getXScaleWheelInc()
	{
		return xScaleWheelInc;
	}

	private void setXScaleWheelInc(double scaleWheelInc)
	{
		xScaleWheelInc = scaleWheelInc;
	}

	private double getYScaleWheelInc()
	{
		return yScaleWheelInc;
	}

	private void setYScaleWheelInc(double scaleWheelInc)
	{
		yScaleWheelInc = scaleWheelInc;
	}

	public int getQuality()
	{
		return quality;
	}

	/**
	 * see GraphicsToolkit
	 * 
	 * @param quality
	 */
	public void setQuality(int quality)
	{
		this.quality = quality;
	}

}
