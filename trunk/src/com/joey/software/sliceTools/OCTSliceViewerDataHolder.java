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
package com.joey.software.sliceTools;


import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Vector;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.colorMapping.ColorMap;


public class OCTSliceViewerDataHolder implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3439135368738545377L;

	// X Slice Plane
	int xSlicePlanePos;

	int xSlicePlanePosMax;

	double xSliceCrossX;

	double xSliceCrossY;

	// Y Slice Plane
	int ySlicePlanePos;

	int ySlicePlanePosMax;

	double ySliceCrossX;

	double ySliceCrossY;

	// Z Slice Plane
	int zSlicePlanePos;

	int zSlicePlanePosMax;

	double zSliceCrossX;

	double zSliceCrossY;

	int previewAxes;

	int previewPos;

	int scalePos;

	boolean renderHighRes;

	boolean continousUpdate;

	ColorMap color;

	public volatile Point2D.Double p1x = new Point2D.Double(); // this if

	// volatile for
	// backwards
	// compability
	// (version 2
	// added this)

	public volatile Point2D.Double p2x = new Point2D.Double(); // this is

	// volatile for
	// same reason

	public volatile Point2D.Double p1y = new Point2D.Double(); // this if

	// volatile for
	// backwards
	// compability
	// (version 2
	// added this)

	public volatile Point2D.Double p2y = new Point2D.Double(); // this is

	// volatile for
	// same reason

	public volatile Point2D.Double p1z = new Point2D.Double(); // this if

	// volatile for
	// backwards
	// compability
	// (version 2
	// added this)

	public volatile Point2D.Double p2z = new Point2D.Double(); // this is

	// volatile for
	// same reason

	public transient Vector<Point2D.Double> xPath = new Vector<Point2D.Double>();

	public transient Vector<Point2D.Double> yPath = new Vector<Point2D.Double>();

	public transient Vector<Point2D.Double> zPath = new Vector<Point2D.Double>();

	public transient Vector<Point2D.Double> xArea = new Vector<Point2D.Double>();

	public transient Vector<Point2D.Double> yArea = new Vector<Point2D.Double>();

	public transient Vector<Point2D.Double> zArea = new Vector<Point2D.Double>();

	public void setData(OCTSliceViewerDataHolder data)
	{
		// X Slice Plane
		xSlicePlanePos = data.xSlicePlanePos;

		xSlicePlanePosMax = data.xSlicePlanePosMax;

		xSliceCrossX = data.xSliceCrossX;

		xSliceCrossY = data.xSliceCrossY;

		// Y Slice Plane
		ySlicePlanePos = data.ySlicePlanePos;

		ySlicePlanePosMax = data.ySlicePlanePosMax;

		ySliceCrossX = data.ySliceCrossX;

		ySliceCrossY = data.ySliceCrossY;

		// Z Slice Plane
		// X Slice Plane
		zSlicePlanePos = data.zSlicePlanePos;

		zSlicePlanePosMax = data.zSlicePlanePosMax;

		zSliceCrossX = data.zSliceCrossX;

		zSliceCrossY = data.zSliceCrossY;

		previewAxes = data.previewAxes;

		previewPos = data.previewPos;

		scalePos = data.scalePos;

		renderHighRes = data.renderHighRes;

		continousUpdate = data.continousUpdate;

		ColorMap color = data.color;

		p1x = data.p1x;
		p2x = data.p2x;

		p1z = data.p1z;
		p2z = data.p2z;

		p1y = data.p1y;
		p2y = data.p2y;

		xArea.clear();
		yArea.clear();
		zArea.clear();
		xPath.clear();
		yPath.clear();
		zPath.clear();

		xArea.addAll(data.xArea);
		yArea.addAll(data.yArea);
		zArea.addAll(data.zArea);

		xPath.addAll(data.xPath);
		yPath.addAll(data.xPath);
		zPath.addAll(data.xPath);
	}

	public void initData(NativeDataSet data)
	{
		// X Slice Plane
		xSlicePlanePos = 0;

		xSlicePlanePosMax = data.getSizeDataX();

		xSliceCrossX = 0;

		xSliceCrossY = 0;

		// Y Slice Plane
		ySlicePlanePos = 0;

		ySlicePlanePosMax = data.getSizeDataY();

		ySliceCrossX = 0;

		ySliceCrossY = 0;

		// Z Slice Plane
		// X Slice Plane
		zSlicePlanePos = 0;

		zSlicePlanePosMax = data.getSizeDataZ();

		zSliceCrossX = 0;

		zSliceCrossY = 0;

		previewAxes = NativeDataSet.X_SLICE;

		previewPos = 0;

		scalePos = 10;

		renderHighRes = false;

		continousUpdate = false;

		ColorMap color = ColorMap.getColorMap(ColorMap.TYPE_GRAY);

	}

	public void setData(OCTSliceViewer p)
	{
		p.setUpdateAllowed(false);
		p.getImageProcesser().setColorMap(color);
		p.getXSlicePanel().getPos().setMaximum(xSlicePlanePosMax);
		p.getXSlicePanel().getPos().setValue(xSlicePlanePos);

		p.getXSlicePanel().setCrossX(xSliceCrossX);
		p.getXSlicePanel().setCrossY(xSliceCrossY);

		p.getYSlicePanel().getPos().setMaximum(ySlicePlanePosMax);
		p.getYSlicePanel().getPos().setValue(ySlicePlanePos);

		p.getYSlicePanel().setCrossX(ySliceCrossX);
		p.getYSlicePanel().setCrossY(ySliceCrossY);

		p.getZSlicePanel().getPos().setMaximum(zSlicePlanePosMax);
		p.getZSlicePanel().getPos().setValue(zSlicePlanePos);

		p.getZSlicePanel().setCrossX(zSliceCrossX);
		p.getZSlicePanel().setCrossY(zSliceCrossY);

		p.setPreviewAxes(previewAxes);
		p.setPreviewPos(previewPos);

		// p.getScaleSlider().setValue(scalePos);
		p.getRenderHighRes().setSelected(renderHighRes);
		p.getContinousSliderUpdate().setSelected(continousUpdate);

		p.setUpdateAllowed(true);
		p.xSlicePanel.imgPanel.setImage(ImageOperations.getBi(1));
		p.ySlicePanel.imgPanel.setImage(ImageOperations.getBi(1));
		p.zSlicePanel.imgPanel.setImage(ImageOperations.getBi(1));
		p.updatePreviewPanel(p.getRenderHighRes().isSelected());

		p.xLineMeasure.points.set(0, p1x);
		p.xLineMeasure.points.set(1, p2x);

		p.yLineMeasure.points.set(0, p1y);
		p.yLineMeasure.points.set(1, p2y);

		p.zLineMeasure.points.set(0, p1z);
		p.zLineMeasure.points.set(1, p2z);

		System.out
				.println("##########\tSet Data\t##################################");
		System.out.println(xArea);
		System.out.println("############################################");

		p.xAreaMeasure.setData(xArea);
		p.yAreaMeasure.setData(yArea);
		p.zAreaMeasure.setData(zArea);

		p.xPathMeasure.setData(xPath);
		p.yPathMeasure.setData(yPath);
		p.zPathMeasure.setData(zPath);
		System.out
				.println("##########\tSet Data\t##################################");
		System.out.println(xArea);
		System.out.println("############################################");

	}

	public void getData(OCTSliceViewer p)
	{
		color = p.getImageProcesser().getColorMap();
		// Slice Plane Information
		xSlicePlanePos = p.getXSlicePanel().getPos().getValue();
		xSlicePlanePosMax = p.getXSlicePanel().getPos().getMaximum();
		xSliceCrossX = p.getXSlicePanel().getCrossX();
		xSliceCrossY = p.getXSlicePanel().getCrossY();

		ySlicePlanePos = p.getYSlicePanel().getPos().getValue();
		ySlicePlanePosMax = p.getYSlicePanel().getPos().getMaximum();
		ySliceCrossX = p.getYSlicePanel().getCrossX();
		ySliceCrossY = p.getYSlicePanel().getCrossY();

		zSlicePlanePos = p.getZSlicePanel().getPos().getValue();
		zSlicePlanePosMax = p.getZSlicePanel().getPos().getMaximum();
		zSliceCrossX = p.getZSlicePanel().getCrossX();
		zSliceCrossY = p.getZSlicePanel().getCrossY();

		previewAxes = p.getPreviewAxes();
		previewPos = p.getPreviewPos();

		// scalePos = p.getScaleSlider().getValue();
		renderHighRes = p.getRenderHighRes().isSelected();
		continousUpdate = p.getContinousSliderUpdate().isSelected();

		p1x = p.xLineMeasure.points.get(0);
		p2x = p.xLineMeasure.points.get(1);

		p1y = p.yLineMeasure.points.get(0);
		p2y = p.yLineMeasure.points.get(1);

		p1z = p.zLineMeasure.points.get(0);
		p2z = p.zLineMeasure.points.get(1);

		if (xArea == null)
		{
			xArea = new Vector<Point2D.Double>();
		}
		if (yArea == null)
		{
			yArea = new Vector<Point2D.Double>();
		}
		if (zArea == null)
		{
			zArea = new Vector<Point2D.Double>();
		}

		if (xPath == null)
		{
			xPath = new Vector<Point2D.Double>();
		}
		if (yPath == null)
		{
			yPath = new Vector<Point2D.Double>();
		}
		if (zPath == null)
		{
			zPath = new Vector<Point2D.Double>();
		}
		xArea.clear();
		yArea.clear();
		zArea.clear();
		xPath.clear();
		yPath.clear();
		zPath.clear();

		xArea.addAll(p.xAreaMeasure.points);
		yArea.addAll(p.yAreaMeasure.points);
		zArea.addAll(p.zAreaMeasure.points);

		xPath.addAll(p.xPathMeasure.points);
		yPath.addAll(p.yPathMeasure.points);
		zPath.addAll(p.zPathMeasure.points);

		System.out
				.println("##########\tGet Data\t##################################");
		System.out.println(xArea);
		System.out.println("############################################");
	}

	public int getXSlicePlanePos()
	{
		return xSlicePlanePos;
	}

	public int getXSlicePlanePosMax()
	{
		return xSlicePlanePosMax;
	}

	public double getXSliceCrossX()
	{
		return xSliceCrossX;
	}

	public double getXSliceCrossY()
	{
		return xSliceCrossY;
	}

	public int getYSlicePlanePos()
	{
		return ySlicePlanePos;
	}

	public int getYSlicePlanePosMax()
	{
		return ySlicePlanePosMax;
	}

	public double getYSliceCrossX()
	{
		return ySliceCrossX;
	}

	public double getYSliceCrossY()
	{
		return ySliceCrossY;
	}

	public int getZSlicePlanePos()
	{
		return zSlicePlanePos;
	}

	public int getZSlicePlanePosMax()
	{
		return zSlicePlanePosMax;
	}

	public double getZSliceCrossX()
	{
		return zSliceCrossX;
	}

	public double getZSliceCrossY()
	{
		return zSliceCrossY;
	}

	public int getPreviewAxes()
	{
		return previewAxes;
	}

	public int getPreviewPos()
	{
		return previewPos;
	}

	public int getScalePos()
	{
		return scalePos;
	}

	public boolean isRenderHighRes()
	{
		return renderHighRes;
	}

	public boolean isContinousUpdate()
	{
		return continousUpdate;
	}

	public ColorMap getColor()
	{
		return color;
	}

	public Point2D.Double getP1x()
	{
		return p1x;
	}

	public Point2D.Double getP2x()
	{
		return p2x;
	}

	public Point2D.Double getP1y()
	{
		return p1y;
	}

	public Point2D.Double getP2y()
	{
		return p2y;
	}

	public Point2D.Double getP1z()
	{
		return p1z;
	}

	public Point2D.Double getP2z()
	{
		return p2z;
	}

	public void setRenderHighRes(boolean renderHighRes)
	{
		this.renderHighRes = renderHighRes;
	}
}
