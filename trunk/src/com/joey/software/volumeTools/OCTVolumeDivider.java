package com.joey.software.volumeTools;


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.DataToolkit.ArrayDataSet;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageProcessing.OCTImageProcessingTool;
import com.joey.software.userinterface.VersionManager;


public class OCTVolumeDivider extends JPanel implements ChangeListener,
		ActionListener, Externalizable
{

	private static final long serialVersionUID = VersionManager
			.getCurrentVersion();

	NativeDataSet data;

	SliceSelectPanel xSlicePanel;

	SliceSelectPanel ySlicePanel;

	SliceSelectPanel zSlicePanel;

	public SliceLinker sliceLinker;

	private OCTVolumeViewer volumeViewer;

	JButton updateScreen = new JButton("Update Screen");

	OCTImageProcessingTool imageProcesser = new OCTImageProcessingTool();

	boolean mouseDragging = false;

	JCheckBox renderHighRes = new JCheckBox();

	StatusBarPanel status = null;

	JPanel slicePanel = new JPanel();

	boolean loaded = true;

	public static void main(String input[]) throws IOException
	{
		File dataFile = new File("c:\\test\\micro\\raw.dat");
		File previewFile = new File("c:\\test\\micro\\prv.dat");

		NativeDataSet octData = new NativeDataSet(dataFile, previewFile);

		OCTVolumeDivider divide = new OCTVolumeDivider();
		divide.setOCTData(octData);

		JFrame f = FrameFactroy.getFrame(divide);
		f.setSize(800, 600);

	}

	public OCTVolumeDivider()
	{
		super();
		xSlicePanel = new SliceSelectPanel(300, 500);
		ySlicePanel = new SliceSelectPanel(300, 500);
		zSlicePanel = new SliceSelectPanel(300, 500);

		sliceLinker = new SliceLinker(this, xSlicePanel.controler,
				ySlicePanel.controler, zSlicePanel.controler);

		try
		{
			NativeDataSet data = new NativeDataSet();
			setOCTData(data);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		createJPanel();

	}

	public void unloadData()
	{
		loaded = false;
		getVolumeViewer().unloadData();
		updateVolume(false);
	}

	public void reloadData()
	{
		getVolumeViewer().reloadData();
		loaded = true;
		updateVolume(renderHighRes.isSelected());
	}

	public void createJPanel()
	{
		/**
		 * Create the slice selector
		 */

		slicePanel.setLayout(new GridLayout(3, 1));
		slicePanel.setBorder(BorderFactory.createTitledBorder(""));
		slicePanel.add(xSlicePanel);
		slicePanel.add(ySlicePanel);
		slicePanel.add(zSlicePanel);

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(""));
		SwingToolkit.createPanel(new String[]
		{ "High Res : ", "Update :" }, new JComponent[]
		{ renderHighRes, updateScreen }, 60, 20, controlPanel);

		renderHighRes.addChangeListener(new ChangeListener()
		{
			boolean last = renderHighRes.isSelected();

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (renderHighRes.isSelected() != last)
				{
					last = renderHighRes.isSelected();
					updateVolume(renderHighRes.isSelected());
				}
			}
		});

		updateScreen.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateVolume(renderHighRes.isSelected());
			}
		});

		JSplitPane toolSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		toolSplit.add(controlPanel);
		toolSplit.add(slicePanel);
		toolSplit.setOneTouchExpandable(true);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder(""));
		leftPanel.add(toolSplit, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(getVolumeViewer(), BorderLayout.CENTER);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setOneTouchExpandable(true);
		splitPanel.setLeftComponent(leftPanel);
		splitPanel.setRightComponent(rightPanel);
		splitPanel.setDividerLocation(250);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(splitPanel, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		/**
		 * add all the listners
		 */
		xSlicePanel.pos.addChangeListener(this);
		ySlicePanel.pos.addChangeListener(this);
		zSlicePanel.pos.addChangeListener(this);

	}

	public void updateVolume(boolean highRes)
	{

		if (!data.isAllowFullResolution())
		{
			renderHighRes.setEnabled(false);
			renderHighRes.setSelected(false);
		} else
		{
			renderHighRes.setEnabled(true);
		}
		getVolumeViewer().updateRealSize(highRes);
		getVolumeViewer().volSize.update();
		getVolumeViewer().updateData(highRes);
	}

	public void setOCTData(NativeDataSet data) throws IOException
	{
		this.data = data;
		xSlicePanel.setOCTData(data, NativeDataSet.X_SLICE);
		ySlicePanel.setOCTData(data, NativeDataSet.Y_SLICE);
		zSlicePanel.setOCTData(data, NativeDataSet.Z_SLICE);

		if (getVolumeViewer() == null)
		{
			setVolumeViewer(new OCTVolumeViewer(this));
		}
		if (getVolumeViewer().owner != this)
		{
			getVolumeViewer().setOwner(this);
		}
		getVolumeViewer().updateData(renderHighRes.isSelected());
		getVolumeViewer().updateRealSize(renderHighRes.isSelected());

	}

	public void updateData()
	{
		getVolumeViewer().panel.getRender().startWaitForUpdate();
		getVolumeViewer().updateData(renderHighRes.isSelected());
		getVolumeViewer().updateRealSize(renderHighRes.isSelected());	
		getVolumeViewer().panel.getRender().waitForUpdate();
	}
	public void setData(byte[][][] data)
	{
		try
		{
			ArrayDataSet dat = new ArrayDataSet(data);
			dat.reloadData();
			setOCTData(dat);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() instanceof JSlider)
		{

			JSlider slide = (JSlider) e.getSource();

			if (slide == xSlicePanel.pos)
			{
				if (!xSlicePanel.isSliderListen())
				{
					return;
				}
			}
			if (slide == ySlicePanel.pos)
			{
				if (!ySlicePanel.isSliderListen())
				{
					return;
				}
			}
			if (slide == zSlicePanel.pos)
			{
				if (!zSlicePanel.isSliderListen())
				{
					return;
				}
			}
			double x = (double) xSlicePanel.pos.getValue()
					/ xSlicePanel.pos.getMaximum();
			double y = (double) ySlicePanel.pos.getValue()
					/ ySlicePanel.pos.getMaximum();
			double z = (double) zSlicePanel.pos.getValue()
					/ zSlicePanel.pos.getMaximum();

			/*
			 * Validate the positoin to ensure they are with 0 -> 1
			 */
			x = x > 1 ? 1 : x;
			x = x < 0 ? 0 : x;

			y = y > 1 ? 1 : y;
			y = y < 0 ? 0 : y;

			z = z > 1 ? 1 : z;
			z = z < 0 ? 0 : z;

			xSlicePanel.setPosition(x);
			xSlicePanel.crossX = z;
			xSlicePanel.crossY = y;

			ySlicePanel.setPosition(y);
			ySlicePanel.crossX = x;
			ySlicePanel.crossY = z;

			zSlicePanel.setPosition(z);
			zSlicePanel.crossX = x;
			zSlicePanel.crossY = y;

			zSlicePanel.controler.link.readValues();
			repaint();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{

	}

	public StatusBarPanel getStatus()
	{
		return status;
	}

	public void setStatus(StatusBarPanel status)
	{
		this.status = status;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		sliceLinker.setData((SliceLinker) in.readObject());
		getVolumeViewer().setData((OCTVolumeViewer) in.readObject());
		renderHighRes.setSelected(in.readBoolean());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// Linker
		out.writeObject(sliceLinker);

		//
		out.writeObject(getVolumeViewer());
		out.writeBoolean(renderHighRes.isSelected());
	}

	public void setVolumeViewer(OCTVolumeViewer volumeViewer)
	{
		this.volumeViewer = volumeViewer;
	}

	public OCTVolumeViewer getVolumeViewer()
	{
		return volumeViewer;
	}

	public BufferedImage getSnapshot() throws AWTException
	{
		// TODO Auto-generated method stub
		return volumeViewer.getViewPanel().getRecorder().snapImage();
	}
}
