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
package com.joey.software.imageToolkit.ROI;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.ImagePanelControler;

public class ImageROIPanel extends ImagePanel
{
	ROIControlPanel controlPanel = null;

	Vector<RegionOfIntreset> regions = new Vector<RegionOfIntreset>();

	public ImageROIPanel(BufferedImage image, int panelType)
	{
		super(image, panelType);
	}

	public void addROI(RegionOfIntreset roi)
	{
		regions.add(roi);
		getControlPanel().updatePanel();
	}

	public void removeROI(RegionOfIntreset roi)
	{
		regions.remove(roi);
	}

	@Override
	public void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		AffineTransform oldTrans = g.getTransform();
		Stroke oldStroke = g.getStroke();
		AffineTransform newTrans = new AffineTransform();

		newTrans.concatenate(oldTrans);
		newTrans.scale(getXScale(), getYScale());

		for (RegionOfIntreset roi : regions)
		{
			if (roi.isDisplay())
			{
				boolean selected = (getControlPanel().currentRegion == roi);

				g.setTransform(newTrans);

				g.setColor(roi.getDisplayColor());
				if (selected)
				{
					g.setStroke(oldStroke);
				} else
				{
					g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_BEVEL, 0, new float[]
							{ 12, 12 }, 0));
				}
				if (roi.regionType == RegionOfIntreset.TYPE_POINT)
				{
					newTrans.translate(roi.getPoint().x, roi.getPoint().x);
					int x = 10;
					int y = 10;
					g
							.drawLine(0, (int) (0.5 * y), (int) (0.4 * x), (int) (0.5 * y));
					g
							.drawLine((int) (0.5 * x), 0, (int) (0.5 * x), (int) (0.4 * y));
					g
							.drawLine((int) (0.6 * x), (int) (0.5 * y), x, (int) (0.5 * y));
					g
							.drawLine((int) (0.5 * x), (int) (0.6 * y), (int) (0.5 * x), y);
					newTrans.translate(-roi.getPoint().x, -roi.getPoint().x);
				} else
				{
					g.draw(roi.getRegion());
				}
			}
		}
		g.setTransform(oldTrans);
		g.setStroke(oldStroke);
	}

	public ROIControlPanel getControlPanel()
	{
		if (controlPanel == null)
		{
			controlPanel = new ROIControlPanel(this);
		}
		return controlPanel;
	}

	public static void main(String[] input)
	{
		BufferedImage image1 = null;

		image1 = new BufferedImage(1000, 1000, BufferedImage.TYPE_4BYTE_ABGR);

		ImageROIPanel image1Panel = new ImageROIPanel(image1,
				ImagePanel.TYPE_NORMAL);

		RegionOfIntreset roi1 = new RegionOfIntreset(
				RegionOfIntreset.TYPE_RECTANGLE, "Test Rectagle");
		roi1.setRegion(new Rectangle(10, 10, 300, 300));

		RegionOfIntreset roi2 = new RegionOfIntreset(
				RegionOfIntreset.TYPE_POINT, "Test Point");
		roi2.setPoint(new Point(300, 300));

		RegionOfIntreset roi3 = new RegionOfIntreset(
				RegionOfIntreset.TYPE_POLYGON, "Test Polygon");
		roi3.setRegion(new Rectangle(10, 10, 400, 400));

		RegionOfIntreset roi4 = new RegionOfIntreset(
				RegionOfIntreset.TYPE_OVAL, "Test Oval");

		roi4.setRegion(new Ellipse2D.Double(20, 20, 100, 100));

		image1Panel.addROI(roi1);
		image1Panel.addROI(roi2);
		image1Panel.addROI(roi3);
		image1Panel.addROI(roi4);
		image1Panel.setBorder(BorderFactory.createTitledBorder("Image 1"));

		JFrame frame = new JFrame("Image test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),
				BoxLayout.X_AXIS));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(image1Panel.getControlPanel());

		JScrollPane scroll = new JScrollPane(image1Panel);

		JPanel imageInfo = new JPanel(new BorderLayout());
		ImagePanelControler control = new ImagePanelControler(image1Panel);

		imageInfo.add(scroll, BorderLayout.CENTER);
		imageInfo.add(control, BorderLayout.NORTH);

		split.setRightComponent(imageInfo);
		split.setLeftComponent(image1Panel.getControlPanel());
		split.setDividerLocation(200);
		frame.getContentPane().add(split);

		frame.setVisible(true);

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RegionOfIntreset roi5 = new RegionOfIntreset(
				RegionOfIntreset.TYPE_RECTANGLE, "Test Rectagle New");
		roi5.setRegion(new Rectangle(50, 50, 100, 100));
		image1Panel.addROI(roi5);
	}

}

/**
 * @author Joey Enfield
 * 
 * @version 1.0
 * 
 * @discription This class is used to create all the required icons for this
 *              panel.
 */
class ROIImages
{

	static int imageSize = 10;

	static ImageIcon pointIcon = null;

	static ImageIcon polygonIcon = null;

	static ImageIcon ovalIcon = null;

	static ImageIcon rectangleIcon = null;

	public static ImageIcon getPointIcon()
	{
		if (pointIcon == null)
		{
			BufferedImage image = new BufferedImage(imageSize, imageSize,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLACK);
			int x = image.getWidth();
			int y = image.getHeight();

			g.drawLine(0, (int) (0.5 * y), (int) (0.4 * x), (int) (0.5 * y));
			g.drawLine((int) (0.5 * x), 0, (int) (0.5 * x), (int) (0.4 * y));
			g.drawLine((int) (0.6 * x), (int) (0.5 * y), x, (int) (0.5 * y));
			g.drawLine((int) (0.5 * x), (int) (0.6 * y), (int) (0.5 * x), y);

			pointIcon = new ImageIcon(image);
		}
		return pointIcon;
	}

	public static ImageIcon getPolygonIcon()
	{
		if (polygonIcon == null)
		{
			BufferedImage image = new BufferedImage(imageSize, imageSize,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLACK);
			GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD);
			path.moveTo(0.1 * imageSize, 0.1 * imageSize);
			path.lineTo(0.1 * imageSize, 0.9 * imageSize);
			path.lineTo(0.9 * imageSize, 0.9 * imageSize);
			path.lineTo(0.9 * imageSize, 0.1 * imageSize);
			path.lineTo(0.5 * imageSize, 0.3 * imageSize);
			path.closePath();
			g.draw(path);
			polygonIcon = new ImageIcon(image);
		}
		return polygonIcon;
	}

	public static ImageIcon getOvalIcon()
	{
		if (ovalIcon == null)
		{
			BufferedImage image = new BufferedImage(imageSize, imageSize,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLACK);
			g.fillOval(0, 0, image.getWidth(), image.getHeight());
			ovalIcon = new ImageIcon(image);
		}
		return ovalIcon;
	}

	public static ImageIcon getRectIcon()
	{
		if (rectangleIcon == null)
		{
			BufferedImage image = new BufferedImage(imageSize, imageSize,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			rectangleIcon = new ImageIcon(image);
		}
		return rectangleIcon;
	}
}

class ROIListModel extends DefaultListModel implements ListModel
{
	Vector<RegionOfIntreset> data;

	public ROIListModel(Vector<RegionOfIntreset> data)
	{
		this.data = data;
	}

	@Override
	public Object getElementAt(int index)
	{
		// TODO Auto-generated method stub
		return data.get(index);
	}

	@Override
	public int getSize()
	{
		// TODO Auto-generated method stub
		return data.size();
	}

	private Vector<RegionOfIntreset> getData()
	{
		return data;
	}

	private void setData(Vector<RegionOfIntreset> data)
	{
		this.data = data;
	}

}

class ROIListCellRender implements ListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JPanel p = new JPanel(new BorderLayout());

		if (isSelected)
		{
			p.setBackground(list.getSelectionBackground());
			p.setForeground(list.getSelectionForeground());
		} else
		{
			p.setBackground(list.getBackground());
			p.setForeground(list.getForeground());
		}
		RegionOfIntreset roi = (RegionOfIntreset) value;
		JLabel l = (new JLabel(roi.getName()));
		if (roi.getRegionType() == RegionOfIntreset.TYPE_OVAL)
		{
			l.setIcon(ROIImages.getOvalIcon());
		} else if (roi.getRegionType() == RegionOfIntreset.TYPE_POLYGON)
		{
			l.setIcon(ROIImages.getPolygonIcon());
		} else if (roi.getRegionType() == RegionOfIntreset.TYPE_POINT)
		{
			l.setIcon(ROIImages.getPointIcon());
		} else if (roi.getRegionType() == RegionOfIntreset.TYPE_RECTANGLE)
		{
			l.setIcon(ROIImages.getRectIcon());
		}
		p.add(l, BorderLayout.CENTER);
		JPanel tmp = new JPanel();
		tmp.setBackground(p.getBackground());
		p.add(tmp, BorderLayout.WEST);
		return p;
	}

}

class ROIControlPanel extends JPanel implements ListSelectionListener
{
	ImageROIPanel panel;

	RegionOfIntreset currentRegion = null;

	JList regionList;

	JScrollPane regionScroll;

	JPanel currentRegionPanel = new JPanel();

	JButton addRegion = new JButton("Add");

	JButton removeRegion = new JButton("Remove");

	public ROIControlPanel(ImageROIPanel panel)
	{
		setPanel(panel);
		createPanel();
	}

	public void createPanel()
	{
		regionList = new JList(new ROIListModel(panel.regions));
		regionList.setCellRenderer(new ROIListCellRender());
		regionList.addListSelectionListener(this);
		regionScroll = new JScrollPane(regionList);

		JPanel regionControlPanel = new JPanel();
		regionControlPanel.setLayout(new GridLayout(1, 2));
		regionControlPanel.add(addRegion);
		regionControlPanel.add(removeRegion);

		JPanel regionPanel = new JPanel(new BorderLayout());
		regionPanel.setBorder(BorderFactory.createTitledBorder("Regions"));
		regionPanel.add(regionScroll, BorderLayout.CENTER);
		regionPanel.add(regionControlPanel, BorderLayout.SOUTH);
		currentRegionPanel.setBorder(BorderFactory
				.createTitledBorder("Region Control"));

		setLayout(new BorderLayout());
		add(regionPanel, BorderLayout.NORTH);
		add(currentRegionPanel, BorderLayout.CENTER);
	}

	public void updatePanel()
	{
		((ROIListModel) regionList.getModel()).data = panel.regions;
	}

	public void createCurrentRegionPanel()
	{
		if (currentRegion != null)
		{

		}
	}

	public void setPanel(ImageROIPanel panel)
	{
		this.panel = panel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getSource() == regionList)
		{
			if (!e.getValueIsAdjusting())
			{
				currentRegion = panel.regions
						.get(regionList.getSelectedIndex());
				System.out.println("Region:" + currentRegion);
				createCurrentRegionPanel();
			} else
			{
				System.out.println("Hello");
			}
		}
		System.out.println("Hello");

	}

}

/**
 * 
 * @author Joey Enfield
 */
class RegionOfIntreset
{
	/**
	 * 
	 */
	public static int TYPE_POINT = 0;

	/**
	 * 
	 */
	public static int TYPE_POLYGON = 1;

	/**
	 * 
	 */
	public static int TYPE_RECTANGLE = 2;

	/**
	 * 
	 */
	public static int TYPE_OVAL = 3;

	/**
	 * 
	 */
	private static int counter = 0;

	/**
	 * 
	 */
	String name = "Region" + counter++;

	/**
	 * 
	 */
	int regionType = TYPE_POINT;

	/**
	 * 
	 */
	Point point = null;

	/**
	 * 
	 */
	Shape region = null;

	/**
	 * 
	 */
	boolean display = true;

	/**
	 * 
	 */
	Color displayColor = Color.BLACK;

	/**
	 * 
	 * @param type
	 * @param name
	 */
	public RegionOfIntreset(int type, String name)
	{
		setType(type);
		setName(name);
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(int type)
	{
		if (type > TYPE_OVAL)
		{
			throw new InvalidParameterException("Invalid Region Type");
		}
		regionType = type;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * @return the display
	 */
	public boolean isDisplay()
	{
		return display;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(boolean display)
	{
		this.display = display;
	}

	/**
	 * @return the displayColor
	 */
	public Color getDisplayColor()
	{
		return displayColor;
	}

	/**
	 * @param displayColor
	 *            the displayColor to set
	 */
	public void setDisplayColor(Color displayColor)
	{
		this.displayColor = displayColor;
	}

	/**
	 * @return the point
	 */
	public Point getPoint()
	{
		return point;
	}

	/**
	 * @param point
	 *            the point to set
	 */
	public void setPoint(Point point)
	{
		this.point = point;
	}

	/**
	 * @return the region
	 */
	public Shape getRegion()
	{
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(Shape region)
	{
		this.region = region;
	}

	/**
	 * @return the regionType
	 */
	public int getRegionType()
	{
		return regionType;
	}

	/**
	 * @param regionType
	 *            the regionType to set
	 */
	public void setRegionType(int regionType)
	{
		this.regionType = regionType;
	}
}
