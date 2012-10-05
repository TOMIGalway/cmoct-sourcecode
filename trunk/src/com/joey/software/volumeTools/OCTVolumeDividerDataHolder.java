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
package com.joey.software.volumeTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.VolumeToolkit.UserChoiceColorMap;
import com.joey.software.framesToolkit.FrameFactroy;


public class OCTVolumeDividerDataHolder implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5744883881056645053L;

	double xMax = 1;

	double xMin = 0;

	double xSlice = 0;

	double yMax = 1;

	double yMin = 0;

	double ySlice = 0;

	double zMax = 1;

	double zMin = 0;

	double zSlice = 0;

	boolean highRes = false;

	int xSize = 30;

	int ySize = 30;

	int zSize = 30;

	int xScale = 1000;

	int yScale = 1000;

	int zScale = 1000;

	public Quat4d rot = new Quat4d();;

	double scale = 1;

	Vector3d trans = new Vector3d();

	boolean annot = true;

	boolean persp = false;

	boolean coord = true;

	int rendAtt = 0;

	boolean texColMap = false;

	int colorModeAtt = 0;

	UserChoiceColorMap cmap = new UserChoiceColorMap();

	public void setData(OCTVolumeDividerDataHolder data)
	{

		xMax = data.xMax;

		xMin = data.xMin;

		xSlice = data.xSlice;

		yMax = data.yMax;

		yMin = data.yMin;

		ySlice = data.ySlice;

		zMax = data.zMax;

		zMin = data.zMin;

		zSlice = data.zSlice;

		highRes = data.highRes;

		xSize = data.xSize;

		ySize = data.ySize;

		zSize = data.zSize;

		xScale = data.xScale;

		yScale = data.zScale;

		zScale = data.zScale;

		rot.set(data.rot);

		scale = data.scale;

		trans.set(data.trans);

		annot = data.annot;

		persp = data.persp;

		coord = data.coord;

		rendAtt = data.rendAtt;

		texColMap = data.texColMap;

		colorModeAtt = data.colorModeAtt;

		cmap.setData(data.cmap);
	}

	public void initData(NativeDataSet data)
	{
		xMax = 1;

		xMin = 0;

		xSlice = 0;

		yMax = 1;

		yMin = 0;

		ySlice = 0;

		zMax = 1;

		zMin = 0;

		zSlice = 0;

		highRes = false;

		xSize = 30;

		ySize = 30;

		zSize = 30;

		xScale = 1000;

		yScale = 1000;

		zScale = 1000;

		rot = new Quat4d();
		;

		scale = 1;

		trans = new Vector3d();

		annot = true;

		persp = false;

		coord = true;

		rendAtt = 0;

		texColMap = false;

		colorModeAtt = 0;

		cmap = new UserChoiceColorMap();
	}

	public static void main(String input[]) throws IOException,
			ClassNotFoundException
	{

		if (false)
		{
			testSave(input);
			return;
		}
		File dataFile = new File("c:\\test\\micro\\raw.dat");
		File previewFile = new File("c:\\test\\micro\\prv.dat");

		final NativeDataSet octData = new NativeDataSet(dataFile, previewFile);

		final OCTVolumeDivider divide = new OCTVolumeDivider();

		System.out.println("Setup Done");
		FrameFactroy.getFrame(divide);
		System.out.println("Setting Data");

		System.out.println("Testing Rotation:");
		divide.setOCTData(octData);

	}

	public static void testSave(String input[]) throws IOException,
			ClassNotFoundException
	{
		File dataFile = new File("c:\\test\\prob.raw");
		File previewFile = new File("c:\\test\\prob.prv");

		final NativeDataSet octData = new NativeDataSet(dataFile, previewFile);

		final OCTVolumeDivider divide1 = new OCTVolumeDivider();
		final OCTVolumeDivider divide2 = new OCTVolumeDivider();
		divide1.setOCTData(octData);
		divide2.setOCTData(octData);

		JPanel p1 = new JPanel(new BorderLayout());
		p1.setPreferredSize(new Dimension(1024, 800));
		p1.add(divide1);

		JFrame f = new JFrame();
		f.add(p1);
		f.setSize(1024, 800);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					System.out.println("here");

					OCTVolumeDividerDataHolder hold = new OCTVolumeDividerDataHolder();
					hold.getData(divide1);

					System.out.println(hold.rot);
					ObjectOutputStream out = new ObjectOutputStream(
							new FileOutputStream("c:\\test\\test.dat"));
					out.writeObject(hold);
					out.close();

					ObjectInputStream in = new ObjectInputStream(
							new FileInputStream("c:\\test\\test.dat"));
					hold = (OCTVolumeDividerDataHolder) in.readObject();
					hold.setData(divide2);

					JPanel p2 = new JPanel(new BorderLayout());
					p2.setPreferredSize(new Dimension(1024, 800));
					p2.add(divide2);
					JOptionPane.showConfirmDialog(null, p2);
					System.exit(0);
				} catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
		});
		f.setVisible(true);

	}

	public void setData(OCTVolumeDivider p)
	{

		p.sliceLinker.xMin = xMin;
		p.sliceLinker.xMax = xMax;
		p.sliceLinker.xSlice = xSlice;

		p.sliceLinker.yMin = yMin;
		p.sliceLinker.yMax = yMax;
		p.sliceLinker.ySlice = ySlice;

		p.sliceLinker.zMin = zMin;
		p.sliceLinker.zMax = zMax;
		p.sliceLinker.zSlice = zSlice;

		p.renderHighRes.setSelected(highRes);

		p.getVolumeViewer().volSize.setXSize(xSize);
		p.getVolumeViewer().volSize.setYSize(ySize);
		p.getVolumeViewer().volSize.setZSize(zSize);

		p.getVolumeViewer().volSize.setXScaleSliderValue(xScale);
		p.getVolumeViewer().volSize.setYScaleSliderValue(yScale);
		p.getVolumeViewer().volSize.setZScaleSliderValue(zScale);

		p.getVolumeViewer().getViewPanel().getRender().getRotationAttr()
				.set(rot);
		p.getVolumeViewer().getViewPanel().getRender().getScaleAttr()
				.set(scale);
		p.getVolumeViewer().getViewPanel().getRender().getTranslationAttr()
				.set(trans);

		// Copy CMAP data
		p.getVolumeViewer().getViewPanel().getRender().getColorModeAttr().colormaps[0] = cmap;

		p.getVolumeViewer().getViewPanel().getRender().getAnnotationsAttr()
				.set(annot);
		p.getVolumeViewer().getViewPanel().getRender().perspectiveAttr
				.set(persp);
		p.getVolumeViewer().getViewPanel().getRender().coordSysAttr.set(coord);

		p.getVolumeViewer().getViewPanel().getRender().rendererAttr.value = rendAtt;

		p.getVolumeViewer().getViewPanel().getRender().texColorMapAttr.value = texColMap;

		p.getVolumeViewer().getViewPanel().getRender().colorModeAttr.value = colorModeAtt;

		p.sliceLinker.updateSlices();
		p.getVolumeViewer().getViewPanel().reloadColorMap(null);
		p.getVolumeViewer().getViewPanel().getRender().restoreXform();
		p.updateVolume(highRes);

	}

	public void getData(OCTVolumeDivider p)
	{
		xMin = p.sliceLinker.xMin;
		xMax = p.sliceLinker.xMax;
		xSlice = p.sliceLinker.xSlice;

		yMin = p.sliceLinker.yMin;
		yMax = p.sliceLinker.yMax;
		ySlice = p.sliceLinker.ySlice;

		zMin = p.sliceLinker.zMin;
		zMax = p.sliceLinker.zMax;
		zSlice = p.sliceLinker.zSlice;

		xSize = p.getVolumeViewer().volSize.getXSize();
		ySize = p.getVolumeViewer().volSize.getYSize();
		zSize = p.getVolumeViewer().volSize.getZSize();

		xScale = p.getVolumeViewer().volSize.getXScaleSliderValue();
		yScale = p.getVolumeViewer().volSize.getYScaleSliderValue();
		zScale = p.getVolumeViewer().volSize.getZScaleSliderValue();

		highRes = p.renderHighRes.isSelected();

		rot = p.getVolumeViewer().getViewPanel().getRender().getRotationAttr()
				.getValue();
		scale = p.getVolumeViewer().getViewPanel().getRender().getScaleAttr()
				.getValue();
		trans = p.getVolumeViewer().getViewPanel().getRender()
				.getTranslationAttr().getValue();

		// Copy CMAP data
		cmap = (UserChoiceColorMap) p.getVolumeViewer().getViewPanel()
				.getRender().getColorModeAttr().colormaps[0];

		annot = p.getVolumeViewer().getViewPanel().getRender()
				.getAnnotationsAttr().value;
		persp = p.getVolumeViewer().getViewPanel().getRender().perspectiveAttr.value;
		coord = p.getVolumeViewer().getViewPanel().getRender().coordSysAttr.value;

		rendAtt = p.getVolumeViewer().getViewPanel().getRender().rendererAttr.value;

		texColMap = p.getVolumeViewer().getViewPanel().getRender().texColorMapAttr.value;
		colorModeAtt = p.getVolumeViewer().getViewPanel().getRender().colorModeAttr.value;
	}
}
