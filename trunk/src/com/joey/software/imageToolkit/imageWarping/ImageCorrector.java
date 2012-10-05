package com.joey.software.imageToolkit.imageWarping;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.stringToolkit.StringOperations;


public class ImageCorrector extends JPanel implements ChangeListener,
		ROIPanelListner
{
	MenuControler menuControl = new MenuControler(this);

	ImageFileSelectorPanel panel = new ImageFileSelectorPanel();

	ArrayList<PositionSetter> data = new ArrayList<PositionSetter>();

	JSlider sourceSlider = new JSlider(0, 0, 0);

	JSlider resultSlider = new JSlider(0, 0, 0);

	JPanel sourcePanel = new JPanel();

	JPanel resultPanel = new JPanel();

	ImagePanel procSrcData = new ImagePanel();

	ROIPanel procRstData = new ROIPanel(false, ROIPanel.TYPE_RECTANGLE);

	double dataScale = 1;

	ExportSlicePanel exportPanel = new ExportSlicePanel(this);

	JColorChooser backgroundColor = new JColorChooser();

	JCheckBox realUpdate = new JCheckBox();

	JCheckBox showBack = new JCheckBox();

	JSlider dataAlpha = new JSlider(0, 1000);

	JSlider backAlpha = new JSlider(0, 1000);

	public ImageCorrector()
	{

		createJPanel();

		updateSourceSlider();
		updateResultSlider();

		updateSourceData();
		updateResultData();

		setListening(true);
	}

	public ImageCorrector(boolean realUpdate)
	{
		this();
		this.realUpdate.setSelected(realUpdate);
	}

	public int getImageCount()
	{
		return data.size();
	}

	public PositionSetter getPositionSetter(int index)
	{
		if (index < data.size())
		{

			return data.get(index);
		} else
		{
			return null;
		}
	}

	public PositionSetter getPositionSetter(File f)
	{
		for (int i = 0; i < data.size(); i++)
		{
			PositionSetter p = data.get(i);
			if (p.getData().file != null)
			{
				if (p.getData().file.toString().equalsIgnoreCase(f.toString()))
				{
					return p;
				}
			}
		}
		return null;
	}

	public static void createTestSet() throws IOException
	{
		for (int i = 0; i < 20; i++)
		{
			BufferedImage img = ImageOperations.getBi(600, 480);
			ImageOperations.setImage(Color.red, img);
			Graphics2D g = img.createGraphics();
			g.setColor(ImageOperations.getRandomColor());
			double random = Math.random();
			Rectangle2D.Double r = new Rectangle2D.Double(50 + 300 * random,
					50 + 300 * random, 50 + 300 * random, 50 + 300 * random);
			g.fill(r);
			ImageIO.write(img, "PNG", new File("c:\\test\\images\\image"
					+ StringOperations.getNumberString(4, i) + ".png"));
		}
	}

	public static void main(String input[]) throws IOException
	{
		createTestSet();

		ImageCorrector correct = new ImageCorrector();
		JFrame f = FrameFactroy.getFrame(correct);
		f.setJMenuBar(correct.getMenuBar());
		f.setSize(1024, 800);
		f.setTitle("Image Aligne Tool V1.0");
	}

	public void resizeViewData()
	{
		JSpinner scale = new JSpinner(new SpinnerNumberModel(dataScale, 0.001,
				10, 0.01));
		JPanel holder = new JPanel();
		holder.setLayout(new BorderLayout());
		holder.add(new JLabel("Scale : "), BorderLayout.WEST);
		holder.add(scale, BorderLayout.CENTER);

		if (JOptionPane.showConfirmDialog(this, holder) == JOptionPane.OK_OPTION)
		{
			setScaleSize((Double) scale.getModel().getValue());
		}
	}

	public void setScaleSize(double scale)
	{
		this.dataScale = scale;
		for (PositionSetter p : data)
		{
			ImageData d = p.getData();
			d.unloadImage();
			d.setReduced(true, scale);
			p.updateImage();
		}
		updateSourceData();
		updateResultData();
	}

	public void clearData()
	{
		data.clear();
		updateSourceSlider();
		updateResultSlider();

		updateSourceData();
		updateResultData();
	}

	public void addData(ImageData dat)
	{
		addData(dat, true);
	}

	public void addData(PositionSetter p)
	{
		data.add(p);
	}

	public void addData(ImageData dat, boolean update)
	{
		dat.unloadImage();
		dat.setReduced(true, dataScale);
		dat.getImg();
		PositionSetter p = new PositionSetter(dat);
		p.panel.addROIPanelListner(this);
		data.add(p);

		if (update)
		{
			updateSourceSlider();
			updateResultSlider();
		}
	}

	public void createJPanel()
	{
		JPanel control = new JPanel(new GridLayout(2, 4));
		control.setBorder(BorderFactory.createTitledBorder("Controls"));

		JLabel l = new JLabel("Real Update : ");
		l.setHorizontalAlignment(SwingConstants.RIGHT);

		control.add(l);
		control.add(realUpdate);

		l = new JLabel("Data Alphs: ");
		l.setHorizontalAlignment(SwingConstants.RIGHT);

		control.add(l);
		control.add(dataAlpha);

		l = new JLabel("Show Back: ");
		l.setHorizontalAlignment(SwingConstants.RIGHT);

		control.add(l);
		control.add(showBack);

		l = new JLabel("Data Alphs: ");
		l.setHorizontalAlignment(SwingConstants.RIGHT);
		control.add(l);
		control.add(backAlpha);

		JPanel left = new JPanel(new BorderLayout());
		left.add(new JScrollPane(sourcePanel), BorderLayout.CENTER);
		left.add(sourceSlider, BorderLayout.SOUTH);

		JPanel right = new JPanel(new BorderLayout());
		right.add(resultPanel, BorderLayout.CENTER);
		right.add(resultSlider, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane();
		split.setDividerLocation(512);
		split.setLeftComponent(left);
		split.setRightComponent(right);

		resultPanel.setLayout(new BorderLayout());

		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Result", new JScrollPane(procRstData));
		tab.addTab("Orignal", new JScrollPane(procSrcData));

		resultPanel.add(tab);

		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		add(control, BorderLayout.NORTH);

		sourceSlider.setValue(0);
		resultSlider.setValue(0);

		backAlpha.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (realUpdate.isSelected() || !backAlpha.getValueIsAdjusting())
				{
					updateResultData();
				}
			}
		});

		dataAlpha.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (realUpdate.isSelected() || !dataAlpha.getValueIsAdjusting())
				{
					updateResultData();
				}
			}
		});

	}

	public void setListening(boolean value)
	{
		if (value)
		{
			sourceSlider.addChangeListener(this);
			resultSlider.addChangeListener(this);
		} else
		{
			sourceSlider.removeChangeListener(this);
			resultSlider.removeChangeListener(this);
		}
	}

	public void addUserData()
	{
		JPanel holder = new JPanel(new BorderLayout());
		holder.setPreferredSize(new Dimension(1024, 800));
		holder.add(panel);
		if (JOptionPane
				.showConfirmDialog(this, holder, "Select Images", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			File[] fileData = panel.getFiles();
			for (File f : fileData)
			{
				ImageData d = new ImageData(f);

				addData(d, false);
			}
		}
		updateSourceSlider();
		updateResultSlider();

		updateSourceData();
		updateResultData();
	}

	public void setSourcePosition(int pos)
	{
		setSourcePosition(pos, false);
	}

	public void setSourcePosition(int pos, boolean fullSize)
	{
		if (pos < data.size() && pos >= 0)
		{
			sourcePanel.setLayout(new BorderLayout());
			sourcePanel.removeAll();
			PositionSetter p = data.get(pos);
			p.updateImage();

			if (fullSize)
			{
				JScrollPane scroll = new JScrollPane(
						ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scroll.setViewportView(p);
				sourcePanel.add(scroll, BorderLayout.CENTER);
			} else
			{
				sourcePanel.add(p, BorderLayout.CENTER);
			}

			setResultPosition(pos);
			validate();
		} else
		{
			sourcePanel.setLayout(new BorderLayout());
			sourcePanel.removeAll();
		}
		repaint();
	}

	public BufferedImage getProcessData(int pos, boolean fullSize)
	{
		float bGAlpha = getBackAlpha();
		if (!showBack.isSelected())
		{
			bGAlpha = 0;
		}
		return getProcessData(pos, fullSize, bGAlpha, getDataAlpha());
	}

	/**
	 * This will get the processed data for a given imiage
	 * 
	 * @param pos
	 * @param fullSize
	 * @param bgTra
	 *            -> This is the background transparance
	 * @param dataTra
	 *            -> This the the foregroudn/data transparance
	 * @return
	 */
	public BufferedImage getProcessData(int pos, boolean fullSize, float bgTra, float dataTra)
	{
		Color bg = backgroundColor.getColor();
		ImageData d1 = data.get(0).getData();
		ImageData d2 = data.get(pos).getData();

		if (fullSize)
		{
			Point2D.Double p1;
			Point2D.Double p2;

			p1 = new Point2D.Double();
			p1.x = d1.getP1().x / d1.getScale();
			p1.y = d1.getP1().y / d1.getScale();
			p2 = new Point2D.Double();
			p2.x = d1.getP2().x / d1.getScale();
			p2.y = d1.getP2().y / d1.getScale();

			if (!(d1 instanceof ImageDataBuff))
			{
				d1 = new ImageData(d1.getFile(), p1, p2);
			}
			p1 = new Point2D.Double();
			p1.x = d2.getP1().x / d2.getScale();
			p1.y = d2.getP1().y / d2.getScale();
			p2 = new Point2D.Double();
			p2.x = d2.getP2().x / d2.getScale();
			p2.y = d2.getP2().y / d2.getScale();
			if (!(d2 instanceof ImageDataBuff))
			{
				d2 = new ImageData(d2.getFile(), p1, p2);
			}
		}
		Point2D.Double p = new Point2D.Double();
		BufferedImage img = ImageShaperTool.getProcessedImage(d1, d2, p, bg);

		BufferedImage rst;
		if (bgTra < 0.00001)
		{
			rst = ImageOperations.getBi(d1.img.getWidth(), d1.img.getHeight());
			ImageOperations.setImage(getBackgroundcolor(), rst);
		} else
		{
			rst = ImageOperations
					.cloneImage(d1.img, getBackgroundcolor(), bgTra);
		}
		Graphics2D g = rst.createGraphics();
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, dataTra));
		g.drawImage(img, (int) p.x, (int) p.y, null);

		if (fullSize)
		{
			d1.unloadImage();
			d2.unloadImage();
		}

		return rst;
	}

	public Color getBackgroundcolor()
	{
		return backgroundColor.getColor();
	}

	public void setBackgroundColor(Color c)
	{
		backgroundColor.setColor(c);
	}

	public void setResultPosition(int pos)
	{
		setResultPosition(pos, false, backgroundColor.getColor());
	}

	public void setResultPosition(int pos, boolean fullSize, Color bg)
	{
		if (pos < data.size() && pos >= 0)
		{
			// try
			// {
			// PositionSetter p = null;
			// Component c = sourcePanel.getComponent(0);
			//
			// if (c instanceof JScrollPane)
			// {
			// JScrollPane scroll = (JScrollPane) sourcePanel
			// .getComponent(0);
			// p = (PositionSetter) scroll.getViewport().getView();
			// }
			// if (c instanceof PositionSetter)
			// {
			// p = (PositionSetter) sourcePanel.getComponent(0);
			// }
			//
			// p.getData().unloadImage();
			// } catch (Exception e)
			// {
			// }
			BufferedImage rst = getProcessData(pos, fullSize);
			procRstData.setImage(rst);
			procSrcData.setImage(data.get(0).getData().getImg());

		} else
		{
			procRstData.setImage(null);
			procSrcData.setImage(null);
		}
		repaint();
	}

	public void updateSourceSlider()
	{
		setListening(false);
		sourceSlider.setMinimum(0);
		sourceSlider.setMaximum(data.size() - 1);
		sourceSlider.setSnapToTicks(true);
		sourceSlider.setMinorTickSpacing(1);
		sourceSlider.setPaintTicks(true);
		setListening(true);
	}

	public int getSelectedIndex()
	{
		return resultSlider.getValue();
	}

	public void updateResultSlider()
	{
		setListening(false);
		resultSlider.setMinimum(0);
		resultSlider.setMaximum(data.size() - 1);
		resultSlider.setSnapToTicks(true);
		resultSlider.setMinorTickSpacing(1);
		resultSlider.setPaintTicks(true);
		setListening(true);
	}

	public BufferedImage getProcessedRegion(int pos, boolean fullSize)
	{
		BufferedImage result = getProcessData(pos, fullSize);

		if (procRstData.getRegions().size() > 0)
		{
			Rectangle r = (Rectangle) procRstData.getRegions().get(0);
			ImageData d = data.get(pos).getData();

			if (fullSize)
			{
				r = new Rectangle(r);
				r.x /= d.getScale();
				r.y /= d.getScale();
				r.width /= d.getScale();
				r.height /= d.getScale();
			}
			result = ImageOperations.cropImage(result, r);
		}

		return result;
	}

	public void exportCroppedData()
	{
		if (JOptionPane
				.showConfirmDialog(this, exportPanel, "Export Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			int zero = (Integer) exportPanel.zeroNum.getValue();
			for (int i = 0; i < data.size(); i++)
			{
				try
				{
					File src = data.get(i).getData().getFile();
					String fileName = FileOperations.renameFileType(src, "png")
							.getName();
					File f = new File(exportPanel.output.getFile().toString()
							+ fileName);
					BufferedImage img = getProcessedRegion(i, exportPanel.fullSize
							.isSelected());
					ImageIO.write(img, "png", f);
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "Error Opening File"
							+ e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public float getDataAlpha()
	{
		return ((float) dataAlpha.getValue() / dataAlpha.getMaximum());
	}

	public float getBackAlpha()
	{
		return ((float) backAlpha.getValue() / backAlpha.getMaximum());
	}

	public void updateSourceData()
	{
		setSourcePosition(sourceSlider.getValue());
	}

	public void updateResultData()
	{
		updateResultData(false);
	}

	public void updateResultData(boolean fullSize)
	{
		setResultPosition(resultSlider.getValue(), fullSize, backgroundColor
				.getColor());
	}

	public void setUserData()
	{
		data.clear();
		addUserData();
	}

	public JMenuBar getMenuBar()
	{
		return menuControl.getMenuBar();
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{

		if (e.getSource() == sourceSlider)
		{
			// if (!sourceSlider.getValueIsAdjusting())
			{
				updateSourceData();
			}
		}
		if (e.getSource() == resultSlider)
		{
			// if (resultSlider.getValueIsAdjusting())
			{
				updateResultData(false);
			}

		}

	}

	public void exitPressed()
	{
		System.exit(0);
	}

	@Override
	public void regionAdded(Shape region)
	{

	}

	@Override
	public void regionChanged()
	{
		if (realUpdate.isSelected())
		{
			updateResultData();
		}
	}

	@Override
	public void regionRemoved(Shape region)
	{

	}

}

class MenuControler
{
	JMenuBar menu = new JMenuBar();

	JMenu fileMenu = new JMenu("File");

	JMenu editMenu = new JMenu("Tool");

	ImageCorrector owner;

	// For File Menu
	JMenuItem loadData = new JMenuItem("Load Data");

	JMenuItem exitItem = new JMenuItem("Exit");

	JMenuItem exportData = new JMenuItem("Export Data");

	JMenuItem exportCurrent = new JMenuItem("Export Current");

	JMenuItem resizeItem = new JMenuItem("Resize Images");

	public MenuControler(ImageCorrector prog)
	{
		owner = prog;
		createMenu();
	}

	public void createMenu()
	{
		// File Menu Bar
		fileMenu.add(loadData);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitItem);

		// Edit Menu
		editMenu.add(resizeItem);
		editMenu.add(new JSeparator());
		// editMenu.add(exportCurrent);
		editMenu.add(exportData);

		resizeItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					owner.resizeViewData();
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(owner, "Error Resizing Data:"
							+ e1);
					e1.printStackTrace();
				}
			}
		});
		// Add Listners
		loadData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					owner.addUserData();
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(owner, "Error Opening File :"
							+ e1);
					e1.printStackTrace();
				}
			}
		});

		exportData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					owner.exportCroppedData();
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(owner, "Error Saving File :"
							+ e1);
					e1.printStackTrace();

				}
			}
		});

		exitItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.exitPressed();
			}
		});

	}

	public JMenuBar getMenuBar()
	{// Create PlaneMenu
		menu.removeAll();
		menu.add(fileMenu);
		menu.add(editMenu);
		return menu;
	}
}

class ExportSlicePanel extends JPanel
{
	ImageCorrector owner;

	JCheckBox fullSize = new JCheckBox("Full Size : ");

	FileSelectionField output = new FileSelectionField();

	JSpinner zeroNum = new JSpinner(new SpinnerNumberModel(3, 0, 10000, 1));

	public ExportSlicePanel(ImageCorrector owner)
	{
		fullSize.setSelected(true);
		this.owner = owner;
		createJPanel();
	}

	public void createJPanel()
	{
		JPanel panel = new JPanel();
		// panel.add(new JLabel("Zero Num : "), BorderLayout.WEST);
		// panel.add(zeroNum, BorderLayout.CENTER);
		panel.add(fullSize);

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(output, BorderLayout.NORTH);
		holder.add(panel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(holder, BorderLayout.CENTER);
	}

}