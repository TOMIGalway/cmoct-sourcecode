package com.joey.software.volumeTools;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.joey.software.userinterface.VersionManager;


/**
 * this is a hub to update all the panels when one is changed
 * 
 * @author Joey.Enfield
 * 
 */
public class SliceLinker implements Externalizable
{
	private static final long serialVersionUID = VersionManager
			.getCurrentVersion();

	double xMin;

	double xMax;

	double yMin;

	double yMax;

	double zMin;

	double zMax;

	double xSlice;

	double ySlice;

	double zSlice;

	SliceControler xSliceControl;

	SliceControler ySliceControl;

	SliceControler zSliceControl;

	OCTVolumeDivider owner;

	public SliceLinker()
	{
		SliceSelectPanel xPanel = new SliceSelectPanel();
		SliceSelectPanel yPanel = new SliceSelectPanel();
		SliceSelectPanel zPanel = new SliceSelectPanel();

		xSliceControl = xPanel.controler;
		ySliceControl = yPanel.controler;
		zSliceControl = zPanel.controler;
	}

	public void setData(SliceLinker link)
	{
		owner = link.owner;
		xMax = link.xMax;
		xMin = link.xMin;
		yMax = link.yMax;
		yMin = link.yMin;
		zMax = link.zMax;
		zMin = link.zMin;
		xSlice = link.xSlice;
		ySlice = link.ySlice;
		zSlice = link.zSlice;
		updateSlices();
	}

	public SliceLinker(OCTVolumeDivider owner, SliceControler xcont, SliceControler ycont, SliceControler zcont)
	{
		this.owner = owner;
		xSliceControl = xcont;
		ySliceControl = ycont;
		zSliceControl = zcont;

		xcont.setLinker(this);
		ycont.setLinker(this);
		zcont.setLinker(this);

		readValues();
		updateSlices();
	}

	public void readValues()
	{
		xMin = zSliceControl.panel.xMinValue;
		xMax = zSliceControl.panel.xMaxValue;
		yMin = zSliceControl.panel.yMinValue;
		yMax = zSliceControl.panel.yMaxValue;

		zMin = xSliceControl.panel.xMinValue;
		zMax = xSliceControl.panel.xMaxValue;
		yMin = xSliceControl.panel.yMinValue;
		yMax = xSliceControl.panel.yMaxValue;

		xMin = ySliceControl.panel.xMinValue;
		xMax = ySliceControl.panel.xMaxValue;
		zMin = ySliceControl.panel.yMinValue;
		zMax = ySliceControl.panel.yMaxValue;

		ySlice = zSliceControl.panel.crossY;
		xSlice = zSliceControl.panel.crossX;
		zSlice = ySliceControl.panel.crossY;
		xSlice = ySliceControl.panel.crossX;
		ySlice = xSliceControl.panel.crossY;
		zSlice = xSliceControl.panel.crossX;
	}

	public void updateSlices()
	{
		zSliceControl.panel.xMinValue = xMin;
		zSliceControl.panel.xMaxValue = xMax;
		zSliceControl.panel.yMinValue = yMin;
		zSliceControl.panel.yMaxValue = yMax;

		xSliceControl.panel.xMinValue = zMin;
		xSliceControl.panel.xMaxValue = zMax;
		xSliceControl.panel.yMinValue = yMin;
		xSliceControl.panel.yMaxValue = yMax;

		ySliceControl.panel.xMinValue = xMin;
		ySliceControl.panel.xMaxValue = xMax;
		ySliceControl.panel.yMinValue = zMin;
		ySliceControl.panel.yMaxValue = zMax;

		xSliceControl.panel.setSliderListen(false);
		xSliceControl.panel.setPosition(xSlice);
		xSliceControl.panel.crossX = zSlice;
		xSliceControl.panel.crossY = ySlice;
		xSliceControl.panel.setSliderListen(true);

		ySliceControl.panel.setSliderListen(false);
		ySliceControl.panel.setPosition(ySlice);
		ySliceControl.panel.crossX = xSlice;
		ySliceControl.panel.crossY = zSlice;
		ySliceControl.panel.setSliderListen(true);

		zSliceControl.panel.setSliderListen(false);
		zSliceControl.panel.setPosition(zSlice);
		zSliceControl.panel.crossX = xSlice;
		zSliceControl.panel.crossY = ySlice;
		zSliceControl.panel.setSliderListen(true);
		xSliceControl.panel.repaint();
		ySliceControl.panel.repaint();
		zSliceControl.panel.repaint();
		try
		{
			owner.getVolumeViewer().updateRealSize(owner.renderHighRes
					.isSelected());
			owner.getVolumeViewer().updateSlices();
		} catch (Exception e)
		{

		}
	}

	public void valueChanged(SliceControler src)
	{
		if (src == xSliceControl)
		{
			yMin = xSliceControl.panel.yMinValue;
			yMax = xSliceControl.panel.yMaxValue;
			zMin = xSliceControl.panel.xMinValue;
			zMax = xSliceControl.panel.xMaxValue;

			ySlice = xSliceControl.panel.crossY;
			zSlice = xSliceControl.panel.crossX;
		} else if (src == ySliceControl)
		{
			xMin = ySliceControl.panel.xMinValue;
			xMax = ySliceControl.panel.xMaxValue;
			zMin = ySliceControl.panel.yMinValue;
			zMax = ySliceControl.panel.yMaxValue;

			zSlice = ySliceControl.panel.crossY;
			xSlice = ySliceControl.panel.crossX;
		} else if (src == zSliceControl)
		{
			xMin = zSliceControl.panel.xMinValue;
			xMax = zSliceControl.panel.xMaxValue;
			yMin = zSliceControl.panel.yMinValue;
			yMax = zSliceControl.panel.yMaxValue;

			ySlice = zSliceControl.panel.crossY;
			xSlice = zSliceControl.panel.crossX;
		}
		updateSlices();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		xMin = in.readDouble();
		xMax = in.readDouble();
		xSlice = in.readDouble();

		yMin = in.readDouble();
		yMax = in.readDouble();
		ySlice = in.readDouble();

		zMin = in.readDouble();
		zMax = in.readDouble();
		zSlice = in.readDouble();

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeDouble(xMin);
		out.writeDouble(xMax);
		out.writeDouble(xSlice);

		out.writeDouble(yMin);
		out.writeDouble(yMax);
		out.writeDouble(ySlice);

		out.writeDouble(zMin);
		out.writeDouble(zMax);
		out.writeDouble(zSlice);

	}
}
