package com.joey.software.imageToolkit;

import java.awt.Adjustable;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.mathsToolkit.NumberDimension;


/**
 * <p>
 * <code>NavigableImagePanel</code> is a
 * lightweight container displaying an image that
 * can be zoomed in and out and panned with ease
 * and simplicity, using an adaptive rendering for
 * high quality display and satisfactory
 * performance.
 * </p>
 * <p>
 * <h3>Image</h3>
 * <p>
 * An image is loaded either via a constructor:
 * </p>
 * 
 * <pre>
 * NavigableImagePanel panel = new NavigableImagePanel(image);
 * </pre>
 * 
 * or using a setter:
 * 
 * <pre>
 * NavigableImagePanel panel = new NavigableImagePanel();
 * panel.setImage(image);
 * </pre>
 * 
 * When an image is set, it is initially painted
 * centered in the component, at the largest
 * possible size, fully visible, with its aspect
 * ratio is preserved. This is defined as 100% of
 * the image size and its corresponding zoom level
 * is 1.0. </p> <h3>Zooming</h3>
 * <p>
 * Zooming can be controlled interactively, using
 * either the mouse scroll wheel (default) or the
 * mouse two buttons, or programmatically,
 * allowing the programmer to implement other
 * custom zooming methods. If the mouse does not
 * have a scroll wheel, set the zooming device to
 * mouse buttons:
 * 
 * <pre>
 * panel.setZoomDevice(ZoomDevice.MOUSE_BUTTON);
 * </pre>
 * 
 * The left mouse button works as a toggle switch
 * between zooming in and zooming out modes, and
 * the right button zooms an image by one
 * increment (default is 20%). You can change the
 * zoom increment value by:
 * 
 * <pre>
 * panel.setZoomIncrement(newZoomIncrement);
 * </pre>
 * 
 * If you intend to provide programmatic zoom
 * control, set the zoom device to none to disable
 * both the mouse wheel and buttons for zooming
 * purposes:
 * 
 * <pre>
 * panel.setZoomDevice(ZoomDevice.NONE);
 * </pre>
 * 
 * and use <code>setZoom()</code> to change the
 * zoom level.
 * </p>
 * <p>
 * Zooming is always around the point the mouse
 * pointer is currently at, so that this point
 * (called a zooming center) remains stationary
 * ensuring that the area of an image we are
 * zooming into does not disappear off the screen.
 * The zooming center stays at the same location
 * on the screen and all other points move
 * radially away from it (when zooming in), or
 * towards it (when zooming out). For
 * programmatically controlled zooming the zooming
 * center is either specified when
 * <code>setZoom()</code> is called:
 * 
 * <pre>
 * panel.setZoom(newZoomLevel, newZoomingCenter);
 * </pre>
 * 
 * or assumed to be the point of an image which is
 * the closest to the center of the panel, if no
 * zooming center is specified:
 * 
 * <pre>
 * panel.setZoom(newZoomLevel);
 * </pre>
 * 
 * </p>
 * <p>
 * There are no lower or upper zoom level limits.
 * </p>
 * <h3>Navigation</h3>
 * <p>
 * <code>NavigableImagePanel</code> does not use
 * scroll bars for navigation, but relies on a
 * navigation image located in the upper left
 * corner of the panel. The navigation image is a
 * small replica of the image displayed in the
 * panel. When you click on any point of the
 * navigation image that part of the image is
 * displayed in the panel, centered. The
 * navigation image can also be zoomed in the same
 * way as the main image.
 * </p>
 * <p>
 * In order to adjust the position of an image in
 * the panel, it can be dragged with the mouse,
 * using the left button.
 * </p>
 * <p>
 * For programmatic image navigation, disable the
 * navigation image:
 * 
 * <pre>
 * panel.setNavigationImageEnabled(false)
 * </pre>
 * 
 * and use <code>getImageOrigin()</code> and
 * <code>setImageOrigin()</code> to move the image
 * around the panel.
 * </p>
 * <h3>Rendering</h3>
 * <p>
 * <code>NavigableImagePanel</code> uses the
 * Nearest Neighbor interpolation for image
 * rendering (default in Java). When the scaled
 * image becomes larger than the original image,
 * the Bilinear interpolation is applied, but only
 * to the part of the image which is displayed in
 * the panel. This interpolation change threshold
 * can be controlled by adjusting the value of
 * <code>HIGH_QUALITY_RENDERING_SCALE_THRESHOLD</code>
 * .
 * </p>
 */
public class ImagePanel extends JPanel implements MouseMotionListener
{

	// This will draw the image to a 1:1 scale
	public static final int TYPE_NORMAL = 0;

	// This will fit the image into the panel. It
	// will not distort the panel
	public static final int TYPE_SCALE_IMAGE_TO_PANEL = 1;

	// This will distort the image to fit in into
	// the exact size of the panel
	public static final int TYPE_FIT_IMAGE_TO_PANEL = 2;

	// This allows the current scale to be vaired
	public static final int TYPE_CUSTOM_SCALE = 3;

	// This is the current mode the panel is
	// running in
	int panelType = TYPE_NORMAL;

	int quality = GraphicsToolkit.HIGH_QUALITY;

	boolean fixedRun = false;

	boolean currentlyRunningFix = false; // This
											// is
											// a
											// fix
											// that
											// displays
											// resize

	boolean showRGBValueOnMouseMove = false;

	/**
	 * <p>
	 * Identifies a change to the zoom level.
	 * </p>
	 */
	public static final String ZOOM_LEVEL_CHANGED_PROPERTY = "zoomLevel";

	/**
	 * <p>
	 * Identifies a change to the zoom increment.
	 * </p>
	 */
	public static final String ZOOM_INCREMENT_CHANGED_PROPERTY = "zoomIncrement";

	/**
	 * <p>
	 * Identifies that the image in the panel has
	 * changed.
	 * </p>
	 */
	public static final String IMAGE_CHANGED_PROPERTY = "image";

	private static final double SCREEN_NAV_IMAGE_FACTOR = 0.15; // 15%
																// of

	// panel's width

	private static final double NAV_IMAGE_FACTOR = 0.3; // 30%
														// of
														// panel's
														// width

	private static final double HIGH_QUALITY_RENDERING_SCALE_THRESHOLD = 1.0;

	private static final Object INTERPOLATION_TYPE = RenderingHints.VALUE_INTERPOLATION_BILINEAR;

	private double zoomIncrement = 0.1;

	private double zoomFactor = 1.0 + zoomIncrement;

	private double navZoomFactor = 1.0 + zoomIncrement;

	BufferedImage image;

	private BufferedImage navigationImage;

	private double initialScale = 0.0;

	private double scaleX = 1.0;

	private double scaleY = 1.0;

	private double navScale = 0.0;

	private Rectangle navBounds = new Rectangle(20, 20, 20, 20);

	private int originX = 0;

	private int originY = 0;

	private Point mousePosition;

	private Dimension previousPanelSize;

	private boolean navigationImageEnabled = false;

	private boolean highQualityRenderingEnabled = false;

	private boolean lockDataToImage = true;

	private WheelZoomDevice wheelZoomDevice = null;

	private ButtonZoomDevice buttonZoomDevice = null;

	JPopupMenu scalePopup = new JPopupMenu("Scale Settings");

	JPopupMenu copyPopup = new JPopupMenu("Copy Popup");

	JPopupMenu fixingPopup = new JPopupMenu("Fixing Popup");

	JPanel informationPanel = new JPanel(new BorderLayout());

	JLabel scaleData = new JLabel("1 : 1");

	JSpinner scaleSpinner = new JSpinner(new SpinnerNumberModel(0.0,
			-Double.MAX_VALUE, +Double.MAX_VALUE, 0.1));

	JButton resetScale = new JButton("Reset");

	JButton setScale = new JButton("Set");

	JButton toClipButton = new JButton("To Clipboard");

	JButton toFileButton = new JButton("To File");

	JLabel mouseLocationData = new JLabel();

	JLabel rgbValue = new JLabel();

	boolean blockScrollBarUpdate = false;

	JScrollBar verScroll = null;

	JScrollBar horScroll = null;

	int overlayGraphicsQuality = GraphicsToolkit.HIGH_QUALITY;

	boolean useDimensions = false;

	NumberDimension imageDimensionWide = new NumberDimension();

	NumberDimension imageDimensionHigh = new NumberDimension();

	ImagePanelMouseLocationInterface imagePanelMouseLocationInterface = new GenericImagePanelMouseLocationInterface();

	/**
	 * <p>
	 * Defines zoom devices.
	 * </p>
	 */
	public static class ZoomDevice
	{
		/**
		 * <p>
		 * Identifies that the panel does not
		 * implement zooming, but the component
		 * using the panel does (programmatic
		 * zooming method).
		 * </p>
		 */
		public static final ZoomDevice NONE = new ZoomDevice("none");

		/**
		 * <p>
		 * Identifies the left and right mouse
		 * buttons as the zooming device.
		 * </p>
		 */
		public static final ZoomDevice MOUSE_BUTTON = new ZoomDevice(
				"mouseButton");

		/**
		 * <p>
		 * Identifies the mouse scroll wheel as
		 * the zooming device.
		 * </p>
		 */
		public static final ZoomDevice MOUSE_WHEEL = new ZoomDevice(
				"mouseWheel");

		private String zoomDevice;

		private ZoomDevice(String zoomDevice)
		{
			this.zoomDevice = zoomDevice;
		}

		@Override
		public String toString()
		{
			return zoomDevice;
		}
	}

	private class WheelZoomDevice implements MouseWheelListener
	{
		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			Point p = e.getPoint();
			boolean zoomIn = (e.getWheelRotation() < 0);
			if (isInNavigationImage(p))
			{
				if (zoomIn)
				{
					navZoomFactor = 1.0 + zoomIncrement;
				} else
				{
					navZoomFactor = 1.0 - zoomIncrement;
				}
				zoomNavigationImage();
			} else if (isInImage(p))
			{
				if (zoomIn)
				{
					zoomFactor = 1.0 + zoomIncrement;
				} else
				{
					zoomFactor = 1.0 - zoomIncrement;
				}
				zoomImage();
			}
		}
	}

	private class ButtonZoomDevice extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			Point p = e.getPoint();
			if (SwingUtilities.isRightMouseButton(e))
			{
				if (isInNavigationImage(p))
				{
					navZoomFactor = 1.0 - zoomIncrement;
					zoomNavigationImage();
				} else if (isInImage(p))
				{
					zoomFactor = 1.0 - zoomIncrement;
					zoomImage();
				}
			} else
			{
				if (isInNavigationImage(p))
				{
					navZoomFactor = 1.0 + zoomIncrement;
					zoomNavigationImage();
				} else if (isInImage(p))
				{
					zoomFactor = 1.0 + zoomIncrement;
					zoomImage();
				}
			}
		}
	}

	/**
	 * <p>
	 * Creates a new navigable image panel with no
	 * default image and the mouse scroll wheel as
	 * the zooming device.
	 * </p>
	 */
	public ImagePanel()
	{
		super(true);
		createScrollBars();
		image = ImageOperations.getBi(1);
		setOpaque(false);

		addMouseMotionListener(this);
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				fixedRun = false;
				if (scaleX > 0.0 || scaleY > 0.0)
				{
					if (isFullImageInPanel())
					{
						centerImage();
					} else if (isImageEdgeInPanel())
					{
						scaleOrigin();
					}
					if (isNavigationImageEnabled())
					{
						createNavigationImage();
					}
					repaint();
				}

				previousPanelSize = getSize();
			}
		});

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isMiddleMouseButton(e))
				{
					if (isInNavigationImage(e.getPoint()))
					{
						Point p = e.getPoint();
						displayImageAt(p);
					}
				}
			}
		});

		addMouseMotionListener(new MouseMotionListener()
		{

			@Override
			public void mouseDragged(MouseEvent e)
			{
				int mask = InputEvent.CTRL_DOWN_MASK
						| InputEvent.BUTTON3_DOWN_MASK;

				if (isInNavigationImage(e.getPoint())
						&& ((e.getModifiersEx() & mask) == mask))
				{
					navBounds.x = (int) (e.getPoint().x - navBounds.width
							* navScale / 2);
					navBounds.y = (int) (e.getPoint().y - navBounds.height
							* navScale / 2);

					repaint();
				} else if (SwingUtilities.isMiddleMouseButton(e))
				{
					if (isInNavigationImage(e.getPoint()))
					{
						Point p = e.getPoint();
						displayImageAt(p);
					} else
					{
						Point p = e.getPoint();
						moveImage(p);
					}
				}

			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				// we need the mouse position so
				// that after zooming
				// that position of the image is
				// maintained
				mousePosition = e.getPoint();
			}
		});

		setZoomDevice(ZoomDevice.MOUSE_WHEEL);

		createChildPanels();
	}

	public void createChildPanels()
	{
		informationPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JLabel copyLabel = new JLabel("Save");

		informationPanel.add(new JSeparator(SwingConstants.VERTICAL));
		informationPanel.add(copyLabel);
		informationPanel.add(new JSeparator(SwingConstants.VERTICAL));
		informationPanel.add(scaleData);
		informationPanel.add(new JSeparator());
		informationPanel.add(mouseLocationData);
		informationPanel.add(new JSeparator());
		informationPanel.add(rgbValue);

		JPanel hold = new JPanel(new BorderLayout());
		hold.add(scaleSpinner, BorderLayout.CENTER);
		hold.add(setScale, BorderLayout.EAST);

		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.setPreferredSize(new Dimension(140, 50));
		panel.add(hold);
		panel.add(resetScale);

		copyPopup.add(toClipButton);
		copyPopup.add(toFileButton);
		copyLabel.setComponentPopupMenu(copyPopup);
		toClipButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				ImageOperations.sendImageToClipBoard(image);
			}
		});
		toFileButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				File f = FileSelectionField.getUserFile();
				if (f != null)
				{
					BufferedImage img = getImage();
					try
					{
						ImageIO.write(img, "png", f);
					} catch (IOException edd)
					{
						// TODO Auto-generated
						// catch block
						edd.printStackTrace();
					}
				}

			}
		});

		scalePopup.add(panel);
		scalePopup.addPopupMenuListener(new PopupMenuListener()
		{

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				scaleSpinner.setValue(getScale());
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		scaleData.setComponentPopupMenu(scalePopup);

		setScale.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setScale((Double) scaleSpinner.getValue(), (Double) scaleSpinner
						.getValue());
				resetPresed();
			}
		});

		resetScale.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setScale(1, 1);
				resetPresed();
			}
		});

	}

	public void resetPresed()
	{
		setScale(getXScale(), getYScale());
		repaint();
	}

	/**
	 * For some unknow reason this fixes the
	 * program
	 */
	public void doFixThing()
	{
		informationPanel.setComponentPopupMenu(fixingPopup);
		fixingPopup.removeAll();
		fixingPopup.add(new JLabel("Resize Panel"));
		fixingPopup.setLocation(informationPanel.getLocationOnScreen());
		fixingPopup.setVisible(true);
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fixingPopup.setVisible(false);
	}

	public void createScrollBars()
	{
		if (verScroll == null)
		{
			verScroll = new JScrollBar(Adjustable.VERTICAL);
			verScroll.addAdjustmentListener(new AdjustmentListener()
			{
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e)
				{
					readScrollBarValues();
				}

			});
		}

		if (horScroll == null)
		{
			horScroll = new JScrollBar(Adjustable.HORIZONTAL);
			horScroll.addAdjustmentListener(new AdjustmentListener()
			{
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e)
				{
					readScrollBarValues();
				}

			});
		}
	}

	/**
	 * <p>
	 * Creates a new navigable image panel with
	 * the specified image and the mouse scroll
	 * wheel as the zooming device.
	 * </p>
	 */
	public ImagePanel(BufferedImage image)
	{
		this();
		setImage(image);
	}

	public ImagePanel(BufferedImage image, int panelType)
	{
		this();
		setImage(image);
		setPanelType(panelType);
	}

	public ImagePanel(Image image)
	{
		this(ImageOperations.toBufferedImage(image));
	}

	public ImagePanel(Image image, int panelType)
	{
		this(ImageOperations.toBufferedImage(image), panelType);
	}

	public ImagePanel(int paneltype)
	{
		this();
		setPanelType(paneltype);
	}

	private void addWheelZoomDevice()
	{
		if (wheelZoomDevice == null)
		{
			wheelZoomDevice = new WheelZoomDevice();
			addMouseWheelListener(wheelZoomDevice);
		}
	}

	private void addButtonZoomDevice()
	{
		if (buttonZoomDevice == null)
		{
			buttonZoomDevice = new ButtonZoomDevice();
			addMouseListener(buttonZoomDevice);
		}
	}

	private void removeWheelZoomDevice()
	{
		if (wheelZoomDevice != null)
		{
			removeMouseWheelListener(wheelZoomDevice);
			wheelZoomDevice = null;
		}
	}

	private void removeButtonZoomDevice()
	{
		if (buttonZoomDevice != null)
		{
			removeMouseListener(buttonZoomDevice);
			buttonZoomDevice = null;
		}
	}

	/**
	 * <p>
	 * Sets a new zoom device.
	 * </p>
	 * 
	 * @param newZoomDevice
	 *            specifies the type of a new zoom
	 *            device.
	 */
	public void setZoomDevice(ZoomDevice newZoomDevice)
	{
		if (newZoomDevice == ZoomDevice.NONE)
		{
			removeWheelZoomDevice();
			removeButtonZoomDevice();
		} else if (newZoomDevice == ZoomDevice.MOUSE_BUTTON)
		{
			removeWheelZoomDevice();
			addButtonZoomDevice();
		} else if (newZoomDevice == ZoomDevice.MOUSE_WHEEL)
		{
			removeButtonZoomDevice();
			addWheelZoomDevice();
		}
	}

	/**
	 * <p>
	 * Gets the current zoom device.
	 * </p>
	 */
	public ZoomDevice getZoomDevice()
	{
		if (buttonZoomDevice != null)
		{
			return ZoomDevice.MOUSE_BUTTON;
		} else if (wheelZoomDevice != null)
		{
			return ZoomDevice.MOUSE_WHEEL;
		} else
		{
			return ZoomDevice.NONE;
		}
	}

	// Called from paintComponent() when a new
	// image is set.
	private void initializeParams()
	{
		if (image == null)
		{
			return;
		}
		double xScale = (double) getWidth() / image.getWidth();
		double yScale = (double) getHeight() / image.getHeight();
		initialScale = Math.min(xScale, yScale);
		scaleX = initialScale;
		scaleY = initialScale;
		// An image is initially centered
		centerImage();
		if (isNavigationImageEnabled())
		{
			createNavigationImage();
		}
	}

	// Centers the current image in the panel.
	private void centerImage()
	{
		originX = (getWidth() - getScreenImageWidth()) / 2;
		originY = (getHeight() - getScreenImageHeight()) / 2;
	}

	public void putIntoPanel(JPanel pan)
	{
		pan.setLayout(new BorderLayout());

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(this, BorderLayout.CENTER);
		holder.add(getHorScroll(), BorderLayout.SOUTH);
		holder.add(getVerScroll(), BorderLayout.EAST);

		pan.add(holder, BorderLayout.CENTER);
		pan.add(getImageInformationPanel(), BorderLayout.SOUTH);
	}

	// Creates and renders the navigation image in
	// the upper let corner of the
	// panel.
	private void createNavigationImage()
	{
		if (!isNavigationImageEnabled())
		{
			return;
		}
		// We keep the original navigation image
		// larger than initially
		// displayed to allow for zooming into it
		// without pixellation effect.
		navBounds.width = (int) (getWidth() * NAV_IMAGE_FACTOR);
		navBounds.height = navBounds.width * image.getHeight()
				/ image.getWidth();
		int scrNavImageWidth = (int) (getWidth() * SCREEN_NAV_IMAGE_FACTOR);
		int scrNavImageHeight = scrNavImageWidth * image.getHeight()
				/ image.getWidth();
		if (navBounds.width == 0)
		{
			navBounds.width = 1;
		}
		if (navBounds.height == 0)
		{
			navBounds.height = 1;
		}
		navScale = (double) scrNavImageWidth / navBounds.width;
		if (navigationImage == null
				|| navigationImage.getWidth() != navBounds.width
				|| navigationImage.getHeight() != navBounds.height)
		{
			navigationImage = new BufferedImage(navBounds.width,
					navBounds.height, image.getType());
		}
		Graphics g = navigationImage.getGraphics();
		g.drawImage(image, 0, 0, navBounds.width, navBounds.height, null);
	}

	public void setSizeToImage()
	{
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * <p>
	 * Sets an image for display in the panel.
	 * </p>
	 * 
	 * @param image
	 *            an image to be set in the panel
	 */
	public void setImage(BufferedImage image, boolean resetParam)
	{

		BufferedImage oldImage = this.image;
		this.image = image;
		// Reset scale so that
		// initializeParameters() is called in
		// paintComponent()
		// for the new image.

		double oldX = scaleX;
		double oldY = scaleY;

		int oldOriginX = originX;
		int oldOriginY = originY;
		scaleX = 0;
		scaleY = 0;
		initializeParams();
		if (!resetParam)
		{
			scaleX = oldX;
			scaleY = oldY;
			originX = oldOriginX;
			originY = oldOriginY;

		}

		firePropertyChange(IMAGE_CHANGED_PROPERTY, oldImage, image);
		repaint();
	}

	public void setImage(BufferedImage image)
	{
		setImage(image, false);
	}

	public void setImage(Image image)
	{
		setImage(ImageOperations.toBufferedImage(image));
	}

	/**
	 * <p>
	 * Tests whether an image uses the standard
	 * RGB color space.
	 * </p>
	 */
	public static boolean isStandardRGBImage(BufferedImage bImage)
	{
		return bImage.getColorModel().getColorSpace().isCS_sRGB();
	}

	// Converts this panel's coordinates into the
	// original image coordinates
	public Point2D.Double panelToImageCoords(Point2D p)
	{
		return new Point2D.Double((p.getX() - originX) / scaleX,
				(p.getY() - originY) / scaleY);
	}

	// Converts the original image coordinates
	// into this panel's coordinates
	public Point2D.Double imageToPanelCoords(Point2D p)
	{
		return new Point2D.Double((p.getX() * scaleX) + originX,
				(p.getY() * scaleY) + originY);
	}

	// Converts this panel's coordinates into the
	// original image coordinates
	public Point panelToImageCoords(Point p)
	{
		return new Point((int) ((p.getX() - originX) / scaleX),
				(int) ((p.getY() - originY) / scaleY));
	}

	// Converts the original image coordinates
	// into this panel's coordinates
	public Point imageToPanelCoords(Point p)
	{
		return new Point((int) ((p.getX() * scaleX) + originX),
				(int) ((p.getY() * scaleY) + originY));
	}

	/**
	 * This will return the panel space of the
	 * imagebounds
	 * 
	 * @return
	 */
	public Rectangle getImagePanelBounds()
	{
		Point pOrigin = new Point(0, 0);
		Point pEnd = new Point(image.getWidth(), image.getHeight());

		Rectangle r = new Rectangle();
		r.setFrameFromDiagonal(imageToPanelCoords(pOrigin), imageToPanelCoords(pEnd));
		return r;
	}

	public Rectangle getImageBounds()
	{
		return new Rectangle(0, 0, image.getWidth(), image.getHeight());
	}

	// Converts the navigation image coordinates
	// into the zoomed image
	// coordinates
	private Point navToZoomedImageCoords(Point p)
	{
		int x = (p.x - navBounds.x) * getScreenImageWidth()
				/ getScreenNavImageWidth();
		int y = (p.y - navBounds.y) * getScreenImageHeight()
				/ getScreenNavImageHeight();
		return new Point(x, y);
	}

	// The user clicked within the navigation
	// image and this part of the image
	// is displayed in the panel.
	// The clicked point of the image is centered
	// in the panel.
	private void displayImageAt(Point p)
	{
		Point scrImagePoint = navToZoomedImageCoords(p);
		originX = -(scrImagePoint.x - getWidth() / 2);
		originY = -(scrImagePoint.y - getHeight() / 2);
		repaint();
	}

	// Tests whether a given point in the panel
	// falls within the image
	// boundaries.
	private boolean isInImage(Point p)
	{
		Point coords = panelToImageCoords(p);
		int x = (int) coords.getX();
		int y = (int) coords.getY();
		return (x >= 0 && x < image.getWidth() && y >= 0 && y < image
				.getHeight());
	}

	// Tests whether a given point in the panel
	// falls within the navigation
	// image
	// boundaries.
	private boolean isInNavigationImage(Point p)
	{
		Rectangle r = new Rectangle(navBounds.x, navBounds.y,
				(int) (navBounds.width * navScale),
				(int) (navBounds.height * navScale));

		return (isNavigationImageEnabled() && r.contains(p));
	}

	// Used when the image is resized.
	private boolean isImageEdgeInPanel()
	{
		if (previousPanelSize == null)
		{
			return false;
		}

		return (originX > 0 && originX < previousPanelSize.width || originY > 0
				&& originY < previousPanelSize.height);
	}

	// Tests whether the image is displayed in its
	// entirety in the panel.
	private boolean isFullImageInPanel()
	{
		return (originX >= 0 && (originX + getScreenImageWidth()) < getWidth()
				&& originY >= 0 && (originY + getScreenImageHeight()) < getHeight());
	}

	/**
	 * <p>
	 * Indicates whether the high quality
	 * rendering feature is enabled.
	 * </p>
	 * 
	 * @return true if high quality rendering is
	 *         enabled, false otherwise.
	 */
	public boolean isHighQualityRenderingEnabled()
	{
		return highQualityRenderingEnabled;
	}

	public void setUseDimensions(boolean use)
	{
		this.useDimensions = true;
		repaint();
	}

	/**
	 * <p>
	 * Enables/disables high quality rendering.
	 * </p>
	 * 
	 * @param enabled
	 *            enables/disables high quality
	 *            rendering
	 */
	public void setHighQualityRenderingEnabled(boolean enabled)
	{
		highQualityRenderingEnabled = enabled;
	}

	// High quality rendering kicks in when when a
	// scaled image is larger
	// than the original image. In other words,
	// when image decimation stops and
	// interpolation starts.
	private boolean isHighQualityRendering()
	{
		return (highQualityRenderingEnabled && Math.sqrt(scaleX * scaleX
				+ scaleY * scaleY) > HIGH_QUALITY_RENDERING_SCALE_THRESHOLD);
	}

	/**
	 * <p>
	 * Indicates whether navigation image is
	 * enabled.
	 * <p>
	 * 
	 * @return true when navigation image is
	 *         enabled, false otherwise.
	 */
	public boolean isNavigationImageEnabled()
	{
		return navigationImageEnabled;
	}

	/**
	 * <p>
	 * Enables/disables navigation with the
	 * navigation image.
	 * </p>
	 * <p>
	 * Navigation image should be disabled when
	 * custom, programmatic navigation is
	 * implemented.
	 * </p>
	 * 
	 * @param enabled
	 *            true when navigation image is
	 *            enabled, false otherwise.
	 */
	public void setNavigationImageEnabled(boolean enabled)
	{
		navigationImageEnabled = enabled;
		repaint();
	}

	// Used when the panel is resized
	private void scaleOrigin()
	{
		if (previousPanelSize.width == 0)
		{
			previousPanelSize.width = 1;
		}

		if (previousPanelSize.height == 0)
		{
			previousPanelSize.height = 1;
		}

		originX = originX * getWidth() / previousPanelSize.width;
		originY = originY * getHeight() / previousPanelSize.height;
		repaint();
	}

	// Converts the specified zoom level to scale.
	private double zoomToScale(double zoom)
	{
		return initialScale * zoom;
	}

	/**
	 * <p>
	 * Gets the current zoom level.
	 * </p>
	 * 
	 * @return the current zoom level
	 */
	public double getScale()
	{
		return (scaleX + scaleY) / 2;
	}

	/**
	 * <p>
	 * Sets the zoom level used to display the
	 * image.
	 * </p>
	 * <p>
	 * This method is used in programmatic
	 * zooming. The zooming center is the point of
	 * the image closest to the center of the
	 * panel. After a new zoom level is set the
	 * image is repainted.
	 * </p>
	 * 
	 * @param newZoom
	 *            the zoom level used to display
	 *            this panel's image.
	 */
	public void setZoom(double newZoom)
	{
		Point zoomingCenter = new Point(getWidth() / 2, getHeight() / 2);
		setZoom(newZoom, zoomingCenter);
	}

	/**
	 * <p>
	 * Sets the zoom level used to display the
	 * image, and the zooming center, around which
	 * zooming is done.
	 * </p>
	 * <p>
	 * This method is used in programmatic
	 * zooming. After a new zoom level is set the
	 * image is repainted.
	 * </p>
	 * 
	 * @param newZoom
	 *            the zoom level used to display
	 *            this panel's image.
	 */
	public void setZoom(double newZoom, Point zoomingCenter)
	{
		Point2D.Double imageP = panelToImageCoords(new Point2D.Double(
				zoomingCenter.x, zoomingCenter.y));
		if (imageP.x < 0.0)
		{
			imageP.x = 0.0;
		}
		if (imageP.y < 0.0)
		{
			imageP.y = 0.0;
		}
		if (imageP.x >= image.getWidth())
		{
			imageP.x = image.getWidth() - 1.0;
		}
		if (imageP.y >= image.getHeight())
		{
			imageP.y = image.getHeight() - 1.0;
		}

		Point2D.Double correctedP = imageToPanelCoords(imageP);
		double oldZoom = getScale();
		scaleX = zoomToScale(newZoom);
		scaleY = zoomToScale(newZoom);
		Point2D.Double panelP = imageToPanelCoords(imageP);

		originX += (correctedP.getX() - (int) panelP.x);
		originY += (correctedP.getY() - (int) panelP.y);

		firePropertyChange(ZOOM_LEVEL_CHANGED_PROPERTY, new Double(oldZoom), new Double(
				getScale()));

		repaint();
	}

	/**
	 * <p>
	 * Gets the current zoom increment.
	 * </p>
	 * 
	 * @return the current zoom increment
	 */
	public double getZoomIncrement()
	{
		return zoomIncrement;
	}

	/**
	 * <p>
	 * Sets a new zoom increment value.
	 * </p>
	 * 
	 * @param newZoomIncrement
	 *            new zoom increment value
	 */
	public void setZoomIncrement(double newZoomIncrement)
	{
		double oldZoomIncrement = zoomIncrement;
		zoomIncrement = newZoomIncrement;
		firePropertyChange(ZOOM_INCREMENT_CHANGED_PROPERTY, new Double(
				oldZoomIncrement), new Double(zoomIncrement));
	}

	// Zooms an image in the panel by repainting
	// it at the new zoom level.
	// The current mouse position is the zooming
	// center.
	private void zoomImage()
	{
		Point imageP = panelToImageCoords(mousePosition);
		double oldZoom = getScale();
		scaleX *= zoomFactor;
		scaleY *= zoomFactor;
		Point panelP = imageToPanelCoords(imageP);

		originX += (mousePosition.x - panelP.x);
		originY += (mousePosition.y - panelP.y);

		firePropertyChange(ZOOM_LEVEL_CHANGED_PROPERTY, new Double(oldZoom), new Double(
				getScale()));

		repaint();
	}

	// Zooms the navigation image
	private void zoomNavigationImage()
	{
		navScale *= navZoomFactor;
		repaint();
	}

	/**
	 * <p>
	 * Gets the image origin.
	 * </p>
	 * <p>
	 * Image origin is defined as the upper, left
	 * corner of the image in the panel's
	 * coordinate system.
	 * </p>
	 * 
	 * @return the point of the upper, left corner
	 *         of the image in the panel's
	 *         coordinates system.
	 */
	public Point getImageOrigin()
	{
		return new Point(originX, originY);
	}

	/**
	 * <p>
	 * Sets the image origin.
	 * </p>
	 * <p>
	 * Image origin is defined as the upper, left
	 * corner of the image in the panel's
	 * coordinate system. After a new origin is
	 * set, the image is repainted. This method is
	 * used for programmatic image navigation.
	 * </p>
	 * 
	 * @param x
	 *            the x coordinate of the new
	 *            image origin
	 * @param y
	 *            the y coordinate of the new
	 *            image origin
	 */
	public void setImageOrigin(int x, int y)
	{
		setImageOrigin(new Point(x, y));
	}

	/**
	 * <p>
	 * Sets the image origin.
	 * </p>
	 * <p>
	 * Image origin is defined as the upper, left
	 * corner of the image in the panel's
	 * coordinate system. After a new origin is
	 * set, the image is repainted. This method is
	 * used for programmatic image navigation.
	 * </p>
	 * 
	 * @param newOrigin
	 *            the value of a new image origin
	 */
	public void setImageOrigin(Point newOrigin)
	{
		originX = newOrigin.x;
		originY = newOrigin.y;
		repaint();
	}

	// Moves te image (by dragging with the mouse)
	// to a new mouse position p.
	private void moveImage(Point p)
	{
		int xDelta = p.x - mousePosition.x;
		int yDelta = p.y - mousePosition.y;
		originX += xDelta;
		originY += yDelta;

		mousePosition = p;
		repaint();
	}

	// Gets the bounds of the image area currently
	// displayed in the panel (in
	// image
	// coordinates).
	private Rectangle getImageClipBounds()
	{
		Point startCoords = panelToImageCoords(new Point(0, 0));
		Point endCoords = panelToImageCoords(new Point(getWidth() - 1,
				getHeight() - 1));
		int panelX1 = (int) startCoords.getX();
		int panelY1 = (int) startCoords.getY();
		int panelX2 = (int) endCoords.getX();
		int panelY2 = (int) endCoords.getY();
		// No intersection?
		if (panelX1 >= image.getWidth() || panelX2 < 0
				|| panelY1 >= image.getHeight() || panelY2 < 0)
		{
			return null;
		}

		int x1 = (panelX1 < 0) ? 0 : panelX1;
		int y1 = (panelY1 < 0) ? 0 : panelY1;
		int x2 = (panelX2 >= image.getWidth()) ? image.getWidth() - 1 : panelX2;
		int y2 = (panelY2 >= image.getHeight()) ? image.getHeight() - 1
				: panelY2;
		return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	public void paintOverlay(Graphics2D g)
	{

	}

	public Component getImageInformationPanel()
	{
		return informationPanel;
	}

	/**
	 * This function will test to see if a parent
	 * is a scroll panel, if so it will notify the
	 * panel of the current changes
	 */
	public void updateScrollBars()
	{
		blockScrollBarUpdate = true;
		horScroll
				.getModel()
				.setRangeProperties(-originX, getWidth(), 0, getScreenImageWidth(), false);
		verScroll
				.getModel()
				.setRangeProperties(-originY, getHeight(), 0, getScreenImageHeight(), false);
		blockScrollBarUpdate = false;
	}

	private void readScrollBarValues()
	{
		if (!blockScrollBarUpdate)
		{
			originX = -horScroll.getValue();
			originY = -verScroll.getValue();
			repaint();
		}
	}

	/**
	 * Paints the panel and its image at the
	 * current zoom level, location, and
	 * interpolation method dependent on the image
	 * scale.</p>
	 * 
	 * @param g
	 *            the <code>Graphics</code>
	 *            context for painting
	 */
	@Override
	public synchronized void paintComponent(Graphics g1)
	{

		if (!fixedRun && isVisible())
		{
			if (currentlyRunningFix == false)
			{
				Thread t = new Thread()
				{
					@Override
					public void run()
					{
						currentlyRunningFix = true;
						try
						{
							doFixThing();
						} catch (Exception e)
						{

						}
						currentlyRunningFix = false;

					}
				};
				t.start();
			}
			fixedRun = true;

		}
		Graphics2D g = (Graphics2D) g1;

		createNavigationImage();

		if (image == null)
		{
			image = ImageOperations.getBi(1);
		}

		/**
		 * Old functions for ImagePanel
		 */
		if (panelType == TYPE_SCALE_IMAGE_TO_PANEL)
		{

			/**
			 * This will determine which scale(X
			 * or Y) is the smallest and draw the
			 * image with the scale
			 */

			scaleX = (1 + (getWidth() - getImage().getWidth())
					/ (double) getImage().getWidth());
			scaleY = (1 + (getHeight() - getImage().getHeight())
					/ (double) getImage().getHeight());

			if (scaleX < scaleY)
			{
				scaleY = scaleX;
			} else
			{
				scaleX = scaleY;
			}
		} else if (panelType == TYPE_FIT_IMAGE_TO_PANEL)
		{

			scaleX = getWidth() / (double) getImage().getWidth();
			scaleY = getHeight() / (double) getImage().getHeight();
		}
		/**
		 * Make sure that nav is inside the window
		 */
		if (true)
		{
			Rectangle imageBounds = new Rectangle(navBounds.x, navBounds.y,
					getScreenNavImageWidth(), getScreenNavImageHeight());
			Rectangle panelBounds = new Rectangle(getBounds().x, getBounds().y,
					getBounds().width, getBounds().height);

			/**
			 * Test if image is smaller than
			 * penel, if so center
			 */

			int navEndX = imageBounds.x + imageBounds.width;
			int navEndY = imageBounds.y + imageBounds.height;

			if (navEndX > panelBounds.width)
			{
				navBounds.x += panelBounds.width - navEndX;
			}

			if (navEndY > panelBounds.height)
			{
				navBounds.y += panelBounds.height - navEndY;
			}

			if (navBounds.x < 0)
			{
				navBounds.x = 0;
			}

			if (navBounds.y < 0)
			{
				navBounds.y = 0;
			}
		}
		/**
		 * Force the image to be inside the bounds
		 * of the image
		 */
		if (lockDataToImage)
		{
			Rectangle imageBounds = new Rectangle(originX, originY,
					getScreenImageWidth(), getScreenImageHeight());
			Rectangle panelBounds = new Rectangle(getBounds().x, getBounds().y,
					getBounds().width, getBounds().height);

			/**
			 * Test if image is smaller than
			 * penel, if so center
			 */
			if (panelBounds.width > imageBounds.width)
			{
				originX = (panelBounds.width - imageBounds.width) / 2;
			} else
			{
				if (originX > 0)
				{
					originX = 0;
				} else
				{
					int end = imageBounds.x + imageBounds.width;
					if (end < panelBounds.width)
					{
						originX -= end - panelBounds.width;
					}
				}
			}

			if (panelBounds.height > imageBounds.height)
			{
				originY = (panelBounds.height - imageBounds.height) / 2;
			} else
			{
				if (originY > 0)
				{
					originY = 0;
				} else
				{

					int end = imageBounds.y + imageBounds.height;
					if (end < panelBounds.height)
					{
						originY -= end - panelBounds.height;
					}

				}
			}
		}

		if (isHighQualityRendering())
		{
			Rectangle rect = getImageClipBounds();
			if (rect == null || rect.width == 0 || rect.height == 0)
			{ // no part of image is displayed in
				// the panel
				return;
			}

			BufferedImage subimage = image
					.getSubimage(rect.x, rect.y, rect.width, rect.height);
			Graphics2D g2 = g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, INTERPOLATION_TYPE);
			g2.drawImage(subimage, Math.max(0, originX), Math.max(0, originY), Math
					.min((int) (subimage.getWidth() * scaleX), getWidth()), Math
					.min((int) (subimage.getHeight() * scaleY), getHeight()), null);
		} else
		{
			g.drawImage(image, originX, originY, getScreenImageWidth(), getScreenImageHeight(), null);
		}

		// Draw navigation image

		AffineTransform old = g.getTransform();
		AffineTransform newTra = new AffineTransform(old);
		RenderingHints oldHints = g.getRenderingHints();
		newTra.translate(originX, originY);
		newTra.scale(scaleX, scaleY);

		GraphicsToolkit.setRenderingQuality(g, overlayGraphicsQuality);
		g.setTransform(newTra);
		paintOverlay((Graphics2D) g.create());
		g.setTransform(old);

		g.setRenderingHints(oldHints);
		if (isNavigationImageEnabled())
		{
			g.drawImage(navigationImage, navBounds.x, navBounds.y, (int) (navBounds.width * navScale), (int) (navBounds.height * navScale), null);
			drawZoomAreaOutline(g);

		}

		updateScaleLabel();
		updateScrollBars();

	}

	public void updateScaleLabel()
	{

		double scale = getScale();

		if (Math.abs(scale - 1) < 0.0001)
		{
			scaleData.setText("1 : 1");
		} else
		{
			DecimalFormat format = new DecimalFormat("#.###");

			scaleData.setText("1 : " + format.format(scale));
		}
	}

	// Paints a white outline over the navigation
	// image indicating
	// the area of the image currently displayed
	// in the panel.
	private void drawZoomAreaOutline(Graphics g1)
	{

		Graphics2D g = (Graphics2D) g1;

		Shape oldClip = g.getClip();
		Composite oldComp = g.getComposite();

		Rectangle cropRegion = new Rectangle();
		cropRegion.x = -originX * getScreenNavImageWidth()
				/ getScreenImageWidth() + navBounds.x;
		cropRegion.y = -originY * getScreenNavImageHeight()
				/ getScreenImageHeight() + navBounds.y;
		cropRegion.width = getWidth() * getScreenNavImageWidth()
				/ getScreenImageWidth();
		cropRegion.height = getHeight() * getScreenNavImageHeight()
				/ getScreenImageHeight();

		Rectangle navRect = new Rectangle(navBounds.x, navBounds.y,
				getScreenNavImageWidth(), getScreenNavImageHeight());
		Area a = new Area(navRect);
		a.subtract(new Area(cropRegion));

		cropRegion.x -= 1;
		cropRegion.y -= 1;
		cropRegion.width += 1;
		cropRegion.height += 1;

		g.setClip(navRect);
		g.setColor(Color.WHITE);
		g.draw(cropRegion);
		g.setColor(Color.BLACK);
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.fill(a);

		g.setClip(oldClip);
		g.setComposite(oldComp);

		g.setXORMode(Color.WHITE);
		// Draw Rectangle
		g.setColor(Color.BLACK);
		g.drawRect(navRect.x - 1, navRect.y - 1, navRect.width + 1, navRect.height + 1);

	}

	private int getScreenImageWidth()
	{
		int val = (int) (scaleX * image.getWidth());
		if (val < 1)
		{
			val = 1;
		}
		return val;
	}

	private int getScreenImageHeight()
	{
		int val = (int) (scaleY * image.getHeight());

		if (val < 1)
		{
			val = 1;
		}
		return val;
	}

	private int getScreenNavImageWidth()
	{
		int val = (int) (navScale * navBounds.width);
		if (val < 1)
		{
			val = 1;
		}
		return val;
	}

	private int getScreenNavImageHeight()
	{
		int val = (int) (navScale * navBounds.height);
		if (val < 1)
		{
			val = 1;
		}
		return val;
	}

	private static String[] getImageFormatExtensions()
	{
		String[] names = ImageIO.getReaderFormatNames();
		for (int i = 0; i < names.length; i++)
		{
			names[i] = names[i].toLowerCase();
		}
		Arrays.sort(names);
		return names;
	}

	private static boolean endsWithImageFormatExtension(String name)
	{
		int dotIndex = name.lastIndexOf(".");
		if (dotIndex == -1)
		{
			return false;
		}

		String extension = name.substring(dotIndex + 1).toLowerCase();
		return (Arrays.binarySearch(getImageFormatExtensions(), extension) >= 0);
	}

	public static void main(String[] args)
	{

		ImagePanel panel = new ImagePanel();
		panel.setUseDimensions(true);
		panel.imageDimensionWide.setUnit("Km");
		panel.imageDimensionWide.setValue(1, false);

		panel.imageDimensionHigh.setUnit("Hm");
		panel.imageDimensionHigh.setValue(10, false);

		final BufferedImage image = new BufferedImage(600, 480,
				BufferedImage.TYPE_INT_ARGB);
		ImageOperations.fillWithRandomColors(image);
		panel.setImage(image);
		panel.setNavigationImageEnabled(true);
		JPanel holder = new JPanel(new BorderLayout());

		panel.putIntoPanel(holder);
		FrameFactroy.getFrame(holder);
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public double getXScale()
	{
		return scaleX;
	}

	public double getYScale()
	{
		return scaleY;
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
	 * This will set the scale the the image is to
	 * be drawn at, NOTE: dont forget to set the
	 * panel type to Type custom scale first
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
		if (scaleX != x || scaleY != y)
		{
			scaleX = x;
			scaleY = y;
		}

		Dimension size = new Dimension((int) (getImage().getWidth() * scaleX),
				(int) (getImage().getHeight() * scaleY));
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

	public JScrollBar getVerScroll()
	{
		return verScroll;
	}

	public JScrollBar getHorScroll()
	{
		return horScroll;
	}

	public void updateMouseLocation(Point p)
	{
		imagePanelMouseLocationInterface.updateMouseLocationSting(this, p);
	}

	public ImagePanelMouseLocationInterface getMouseLocationUpdated()
	{
		return imagePanelMouseLocationInterface;
	}

	public void setMouseLocationUpdated(ImagePanelMouseLocationInterface i)
	{
		imagePanelMouseLocationInterface = i;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub
		updateMouseLocation(panelToImageCoords(e.getPoint()));
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		updateMouseLocation(panelToImageCoords(e.getPoint()));
	}

	public boolean isShowRGBValueOnMouseMove()
	{
		return showRGBValueOnMouseMove;
	}

	public void setShowRGBValueOnMouseMove(boolean showRGBValueOnMouseMove)
	{
		this.showRGBValueOnMouseMove = showRGBValueOnMouseMove;
	}

	public JPanel getInPanel()
	{
		JPanel panel = new JPanel();
		putIntoPanel(panel);
		return panel;
	}

}
