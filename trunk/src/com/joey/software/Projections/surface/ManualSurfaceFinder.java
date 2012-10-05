package com.joey.software.Projections.surface;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.dsp.FFT2Dtool;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.controlers.ROIControler;
import com.joey.software.toolkit.VolumeInputSelectorPanel;


public class ManualSurfaceFinder extends ROIControler implements ChangeListener
{
	JComboBox sliceOrentation = new JComboBox(new String[] { "XY", "XZ" });

	JSlider slider = new JSlider(0, 1000, 1000);

	JButton copyNext = new JButton("Copy Next");

	JButton copyPrev = new JButton("Copy Previous");

	JButton loadSurf = new JButton("Load");

	JButton saveSurf = new JButton("Save");

	float drawTransparency = 1f;

	byte[] imageHolder;

	BufferedImage image;

	byte[][][] struct;

	byte[][][] flow;

	float[][] surface;

	FFT2Dtool tool;

	JSpinner slice = new JSpinner();

	//JComboBox view = new JComboBox(new String[] { "Structure", "Flow" });

	JCheckBox previewFFT = new JCheckBox();

	JButton updateFFT = new JButton("Update FFT");

	JButton commitFFT = new JButton("Commit FFT");

	JButton showSurfaceMaps = new JButton("Show Surface");

	JPanel control = null;

	JFrame surfaceFrame = null;

	DynamicRangeImage surfacePanel;

	public ManualSurfaceFinder(ROIPanel panel)
	{
		super(panel);

		final byte[][][] struct = new byte[2][2][2];
		final float[][] surface = new float[2][2];
		setData(struct, struct, surface);
		// TODO Auto-generated constructor stub
	}

	public void setData(byte[][][] struct, byte[][][] flow, float[][] surface)
	{
		this.surface = surface;

		this.flow = flow;
		this.struct = struct;

		imageHolder = new byte[struct[0].length * struct[0][0].length];
		image = ImageOperations
				.pixelsToImage(imageHolder, struct[0].length, struct[0][0].length);
		tool = new FFT2Dtool(surface.length, surface[0].length);
		tool.allocateMemory();

		((SpinnerNumberModel) slice.getModel()).setMaximum(struct.length);
		((SpinnerNumberModel) slice.getModel()).setMinimum(0);
		getPanel().setImage(image);
		updateImage();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		super.mouseClicked(e);
		processMouse(getPanel().panelToImageCoords(e.getPoint()));
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub
		super.mouseDragged(e);
		processMouse(getPanel().panelToImageCoords(e.getPoint()));
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		if (e.isControlDown())
		{
			processMouse(getPanel().panelToImageCoords(e.getPoint()));
		}
	};

	public void processMouse(Point p)
	{

		if (p.x >= 0 && p.x < getPanel().getImage().getWidth())
		{
			if (sliceOrentation.getSelectedIndex() == 0)
			{
				surface[p.x][getSlice()] = p.y;
			} else
			{
				surface[getSlice()][p.x] = p.y;
			}
		}
		getPanel().repaint();

	}

	public void showSurfaceMaps()
	{
		if (surfaceFrame == null)
		{
			surfacePanel = new DynamicRangeImage(surface);

			surfacePanel.setMinValue(0);
			surfacePanel.setMaxValue(struct[0][0].length);

			JTabbedPane tab = new JTabbedPane();
			tab.addTab("Surface", surfacePanel);

			surfaceFrame = FrameFactroy.getFrame(tab);
			surfaceFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}
		surfaceFrame.setVisible(true);
	}

	@Override
	public JPanel getControlPanel()
	{
		if (control == null)
		{

			int label = 100;

			JPanel holder = new JPanel(new GridLayout(8, 1));
			holder.add(sliceOrentation);
			holder.add(slider);
			holder.setBorder(BorderFactory.createTitledBorder("Controls"));
			holder.add(SwingToolkit.getLabel(slice, "Slice : ", label));
			holder.add(copyNext);
			holder.add(copyPrev);
			holder.add(loadSurf);
			holder.add(saveSurf);
			holder.add(showSurfaceMaps);

			control = new JPanel(new BorderLayout());
			control.add(holder, BorderLayout.NORTH);

			slice.addChangeListener(this);

			sliceOrentation.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updateImage();
					getPanel().repaint();
					
				}
			});
			slider.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					drawTransparency = (slider.getValue() / 1000f);
					// slider.getValue();
					getPanel().repaint();
				}
			});
			loadSurf.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						loadData();
					} catch (IOException e1)
					{
						// TODO Auto-generated
						// catch block
						e1.printStackTrace();
					}
				}
			});
			saveSurf.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						saveSurface();
					} catch (IOException e1)
					{
						// TODO Auto-generated
						// catch block
						e1.printStackTrace();
					}
				}
			});
			showSurfaceMaps.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					showSurfaceMaps();
				}
			});

			copyNext.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					copyFromNext();
				}
			});

			copyPrev.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					copyFromPrevious();
				}
			});
		}
		return control;
	}

	@Override
	public void draw(Graphics2D g)
	{

		GeneralPath pathA = new GeneralPath();

		int slice = getSlice();

		// XY slices
		if (sliceOrentation.getSelectedIndex() == 0)
		{
			for (int x = 0; x < surface.length; x++)
			{
				if (x == 0)
				{
					pathA.moveTo(x, surface[x][slice]);

				} else
				{
					pathA.lineTo(x, surface[x][slice]);

				}
			}
		} else
		// XZ slices
		{
			for (int x = 0; x < surface[slice].length; x++)
			{
				if (x == 0)
				{
					pathA.moveTo(x, surface[slice][x]);

				} else
				{
					pathA.lineTo(x, surface[slice][x]);

				}
			}
		}
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, drawTransparency));
		g.setColor(Color.RED);
		g.draw(pathA);

	}

	public static void main(String input[]) throws IOException
	{

		ROIPanel panel = new ROIPanel(false);
		ManualSurfaceFinder finder = new ManualSurfaceFinder(panel);

		panel.setControler(finder);

		FrameFactroy
				.getFrame(finder.getControlPanel(), panel.getInPanel(), 200);

	}

	public int getSlice()
	{
		int val = (Integer) slice.getValue();
		return (val < struct.length ? val : struct.length - 1);
	}

	public void updateImage()
	{
		int slice = getSlice();

		if (sliceOrentation.getSelectedIndex() == 0)
		{
			for (int x = 0; x < struct[0].length; x++)
			{
				for (int y = 0; y < struct[0][0].length; y++)
				{
					imageHolder[x + struct[0].length * y] = struct[slice][x][y];
				}
			}
		} else
		{
			for (int x = 0; x < struct.length; x++)
			{
				for (int y = 0; y < struct[0][0].length; y++)
				{
					imageHolder[x + struct[0].length * y] = struct[x][slice][y];
				}
			}
		}

	}

	public void copyFromPrevious()
	{
		int slice = getSlice();
		if (slice - 1 >= surface[0].length)
		{
			return;
		}

		for (int i = 0; i < surface.length; i++)
		{
			surface[i][slice] = surface[i][slice - 1];
		}
		getPanel().repaint();
	}

	public void copyFromNext()
	{
		int slice = getSlice();
		if (slice + 1 >= surface[0].length)
		{
			return;
		}

		for (int i = 0; i < surface.length; i++)
		{
			surface[i][slice] = surface[i][slice + 1];
		}
		getPanel().repaint();
	}

	public void loadData() throws IOException
	{
		final byte[][][] struct = VolumeInputSelectorPanel
				.getUserVolumeData(null);
		final float[][] surface = SurfaceFinderTool
				.loadSurfaceMap(FileSelectionField.getUserFile());
		setData(struct, struct, surface);

	}

	public void saveSurface() throws IOException
	{
		SurfaceFinderTool
				.saveSurfaceMap(FileSelectionField.getUserFile(), surface);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		updateImage();
		getPanel().repaint();
	}
}
