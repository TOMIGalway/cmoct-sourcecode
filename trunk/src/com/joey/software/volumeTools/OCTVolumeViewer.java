package com.joey.software.volumeTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import com.joey.software.DataToolkit.ImageSliceToolkit;
import com.joey.software.VolumeToolkit.Axis2DRenderer;
import com.joey.software.VolumeToolkit.VolArrayFile;
import com.joey.software.VolumeToolkit.VolumeViewerPanel;
import com.joey.software.userinterface.VersionManager;


public class OCTVolumeViewer extends JPanel implements Externalizable
{
	private static final long serialVersionUID = VersionManager
			.getCurrentVersion();

	boolean allowUpdate = true;

	OCTVolumeSizeControler volSize = new OCTVolumeSizeControler(this);

	OCTVolumeDivider owner;

	VolArrayFile volData;

	VolumeViewerPanel panel;

	boolean loaded = true;

	public OCTVolumeViewer() throws IOException
	{
		super();
		createJPanel();
	}

	public void setOwner(OCTVolumeDivider owner)
	{
		this.owner = owner;
	}

	public void setData(OCTVolumeViewer data)
	{
		volSize.setPanelModle(data.volSize.getPanelMode());

		volSize.setXSize(data.volSize.getXSize());
		volSize.setYSize(data.volSize.getYSize());
		volSize.setZSize(data.volSize.getZSize());

		volSize.setXScaleSliderValue(data.volSize.getXScaleSliderValue());
		volSize.setYScaleSliderValue(data.volSize.getYScaleSliderValue());
		volSize.setZScaleSliderValue(data.volSize.getZScaleSliderValue());

		volSize.setRenderQuality(data.volSize.getRenderQuality());

		getViewPanel().setData(data.getViewPanel());
	}

	public OCTVolumeViewer(OCTVolumeDivider owner) throws IOException
	{
		super();
		this.owner = owner;
		createJPanel();
		updateData(owner.renderHighRes.isSelected());
		updateRealSize(owner.renderHighRes.isSelected());
		getViewPanel().updateCmap(owner.status);
	}

	public void updateRealSize(boolean highRes)
	{

		int xSize = 1;
		int ySize = 1;
		int zSize = 1;

		if (highRes)
		{
			xSize = owner.data.getSizeDataX();
			ySize = owner.data.getSizeDataY();
			zSize = owner.data.getSizeDataZ();
		} else
		{
			xSize = owner.data.getPreviewSizeX();
			ySize = owner.data.getPreviewSizeY();
			zSize = owner.data.getPreviewSizeZ();
		}

		int xDataMin = (int) (Math
				.min(owner.sliceLinker.xMin, owner.sliceLinker.xMax) * xSize);
		int yDataMin = (int) (Math
				.min(owner.sliceLinker.yMin, owner.sliceLinker.yMax) * ySize);
		int zDataMin = (int) (Math
				.min(owner.sliceLinker.zMin, owner.sliceLinker.zMax) * zSize);

		int xDataMax = (int) (Math
				.max(owner.sliceLinker.xMin, owner.sliceLinker.xMax) * xSize);
		int yDataMax = (int) (Math
				.max(owner.sliceLinker.yMin, owner.sliceLinker.yMax) * ySize);
		int zDataMax = (int) (Math
				.max(owner.sliceLinker.zMin, owner.sliceLinker.zMax) * zSize);

		volSize.setXTrueSize(xDataMax - xDataMin);
		volSize.setYTrueSize(yDataMax - yDataMin);
		volSize.setZTrueSize(zDataMax - zDataMin);
	}

	public void updateVolumeData()
	{

		try
		{
			// Then update the image
			volSize.update();

//			System.out.println("OCTVolumeViewer - UpdateVolumeData ["
//					+ Thread.currentThread().getName() + "]");
			updateData(owner.renderHighRes.isSelected());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void unloadData()
	{
		loaded = false;
	}

	public void reloadData()
	{
		loaded = true;
	}

	public void createJPanel() throws IOException
	{
		setPreferredSize(new Dimension(300, 300));
		volData = new VolArrayFile();
		setViewPanel(new VolumeViewerPanel());
		getViewPanel().setPreferredSize(new Dimension(100, 100));

		JScrollPane scroll = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(getControlPanel(false));

		JSplitPane split = new JSplitPane();
		split.setResizeWeight(1);
		split.setRightComponent(scroll);
		split.setLeftComponent(getViewPanel());
		split.setContinuousLayout(false);
		split.setOneTouchExpandable(true);

		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);

	}

	public JPanel getControlPanel()
	{
		return getControlPanel(false);
	}

	public JPanel getControlPanel(boolean basicControls)
	{

		if (!basicControls)
		{
			// JPanel holder = new JPanel();
			// holder.setBorder(BorderFactory.createTitledBorder("Controls"));
			// holder.setLayout(new GridLayout(3, 1));
			// holder.add(panel.getControlPanel());
			// holder.add(scaleHolder);
			// holder.add(reduceHolder);
			//			
			// JPanel out = new JPanel(new BorderLayout());
			// out.add(holder, BorderLayout.NORTH);

			JPanel out = new JPanel(new BorderLayout());
			out.add(volSize, BorderLayout.NORTH);
			out.add(getViewPanel().getControlPanel(), BorderLayout.CENTER);
			return out;
		} else
		{

			JPanel out = new JPanel(new BorderLayout());
			out.add(volSize, BorderLayout.NORTH);

			return out;
		}
	}

	public void updateSlices()
	{
		volData.slicePosX = (float) ((owner.sliceLinker.xSlice - owner.sliceLinker.xMin) / (owner.sliceLinker.xMax - owner.sliceLinker.xMin));
		volData.slicePosY = (float) ((owner.sliceLinker.ySlice - owner.sliceLinker.yMin) / (owner.sliceLinker.yMax - owner.sliceLinker.yMin));
		volData.slicePosZ = (float) ((owner.sliceLinker.zSlice - owner.sliceLinker.zMin) / (owner.sliceLinker.zMax - owner.sliceLinker.zMin));

		Axis2DRenderer rend = (Axis2DRenderer) getViewPanel().getRender()
				.getRenderer();
		rend.updateSlices();
	}

	public void updateData(boolean highRes)
	{
		updateData(highRes, false);
	}
	public void updateData(boolean highRes, boolean waitForFinish)
	{

		try
		{
			System.gc();
			if (!isAllowUpdate())
			{
				return;
			}
//			System.out
//					.println("OCTVolumeView - UpdateData : About to update CPANEL["
//							+ Thread.currentThread().getName() + "]");
			getViewPanel().updateCmap(owner.status);

//			System.out
//					.println("OCTVolumeView - UpdateData :Updating Real Size["
//							+ Thread.currentThread().getName() + "]");
			updateRealSize(highRes);
			// Unload old data
			VolArrayFile file = new VolArrayFile();

//			System.out
//					.println("OCTVolumeView - UpdateData :Setting Volume File["
//							+ Thread.currentThread().getName() + "]");
			getViewPanel().getRender().setVolumeFile(file);
			volData.setData(new byte[][][]
			{
			{
			{ (byte) 1 } } }, volSize.getXScale(), volSize.getYScale(), volSize
					.getZScale());
			int xSize = 1;
			int ySize = 1;
			int zSize = 1;

			if (loaded == false)
			{
				xSize = 1;
				ySize = 1;
				zSize = 1;
			} else if (highRes)
			{
				xSize = owner.data.getSizeDataX();
				ySize = owner.data.getSizeDataY();
				zSize = owner.data.getSizeDataZ();
			} else
			{
				xSize = owner.data.getPreviewSizeX();
				ySize = owner.data.getPreviewSizeY();
				zSize = owner.data.getPreviewSizeZ();
			}

			int xDataMin = (int) (Math
					.min(owner.sliceLinker.xMin, owner.sliceLinker.xMax) * xSize);
			int yDataMin = (int) (Math
					.min(owner.sliceLinker.yMin, owner.sliceLinker.yMax) * ySize);
			int zDataMin = (int) (Math
					.min(owner.sliceLinker.zMin, owner.sliceLinker.zMax) * zSize);

			int xDataMax = (int) (Math
					.max(owner.sliceLinker.xMin, owner.sliceLinker.xMax) * xSize);
			int yDataMax = (int) (Math
					.max(owner.sliceLinker.yMin, owner.sliceLinker.yMax) * ySize);
			int zDataMax = (int) (Math
					.max(owner.sliceLinker.zMin, owner.sliceLinker.zMax) * zSize);

			int countX = volSize.getXReduce();
			int countY = volSize.getYReduce();
			int countZ = volSize.getZReduce();

			double stepX = (xDataMax - xDataMin) / (double) countX;
			double stepY = (yDataMax - yDataMin) / (double) countY;
			double stepZ = (zDataMax - zDataMin) / (double) countZ;

			updateSlices();
			// System.out
			// .printf("\nHighRes [%s] Data Size [%d,%d,%d] \n\tWindow[%d,%d,%d]
			// to [%d,%d,%d] step [%f,%f,%f]\n", highRes, xSize, ySize, zSize,
			// xDataMin, yDataMin, zDataMin, xDataMax, yDataMax, zDataMax,
			// stepX, stepY, stepZ);
			byte[][][] data = new byte[countX][countY][countZ];

			if (highRes)
			{
				ImageSliceToolkit
						.getVolumeRegionData(owner.data.getHeader(), xDataMin, yDataMin, zDataMin, xDataMax, yDataMax, zDataMax, data);
			} else
			{

				owner.data
						.getReducedPreviewData(xDataMin, yDataMin, zDataMin, stepX, stepY, stepZ, data);
			}

			// System.out.println("OCTVolumeView - UpdateData :setvolDatadata["
			// + Thread.currentThread().getName() + "]");
			volData
					.setData(data, volSize.getXScale(), volSize.getYScale(), volSize
							.getZScale());

			// System.out
			// .println("OCTVolumeView - UpdateData :setting voldata in render["
			// + Thread.currentThread().getName() + "]");
			getViewPanel().getRender().setVolumeFile(volData);

			// System.out.println("OCTVolumeView - UpdateData :UPdating cmap["
			// + Thread.currentThread().getName() + "]");
			getViewPanel().updateCmap(owner.status);

			// System.out.println("OCTVolumeView - UpdateData :Repainting["
			// + Thread.currentThread().getName() + "]");
			getViewPanel().repaint();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		if (VersionManager.getCurrentLoadingVersion() <= VersionManager.VERSION_3)
		{
			volSize.setXSize(in.readInt());
			volSize.setYSize(in.readInt());
			volSize.setZSize(in.readInt());

			volSize.setXScaleSliderValue(in.readInt());
			volSize.setYScaleSliderValue(in.readInt());
			volSize.setZScaleSliderValue(in.readInt());
		} else
		{
			volSize.setXSize(in.readInt());
			volSize.setYSize(in.readInt());
			volSize.setZSize(in.readInt());

			volSize.setXScaleSliderValue(in.readInt());
			volSize.setYScaleSliderValue(in.readInt());
			volSize.setZScaleSliderValue(in.readInt());

			volSize.setPanelModle(in.readInt());
			volSize.setRenderQuality(in.readInt());
		}
		getViewPanel().setData((VolumeViewerPanel) in.readObject());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		/**
		 * For backward compatilibyt wiht old files
		 * 
		 * this is the data that used to be saved
		 */
		if (VersionManager.getCurrentSavingVersion() <= VersionManager.VERSION_3)
		{
			out.writeInt(volSize.getXSize());
			out.writeInt(volSize.getYSize());
			out.writeInt(volSize.getZSize());

			out.writeInt(volSize.getXScaleSliderValue());
			out.writeInt(volSize.getYScaleSliderValue());
			out.writeInt(volSize.getZScaleSliderValue());
		} else
		{
			out.writeInt(volSize.getXSize());
			out.writeInt(volSize.getYSize());
			out.writeInt(volSize.getZSize());

			out.writeInt(volSize.getXScaleSliderValue());
			out.writeInt(volSize.getYScaleSliderValue());
			out.writeInt(volSize.getZScaleSliderValue());

			out.writeInt(volSize.getPanelMode());
			out.writeInt(volSize.getRenderQuality());
		}
		out.writeObject(getViewPanel());
	}

	public boolean isAllowUpdate()
	{
		return allowUpdate;
	}

	public void setAllowUpdate(boolean allowUpdate)
	{
		this.allowUpdate = allowUpdate;
	}

	public void setViewPanel(VolumeViewerPanel panel)
	{
		this.panel = panel;
	}

	public VolumeViewerPanel getViewPanel()
	{
		return panel;
	}

	public OCTVolumeSizeControler getVolSize()
	{
	
		return volSize;
	}
	
	public void setData(byte[][][] data)
	{
		volData.setData(data, 1, 1, 1);
	}

}
