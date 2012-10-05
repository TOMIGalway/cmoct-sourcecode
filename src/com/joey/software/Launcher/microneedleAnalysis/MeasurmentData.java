package com.joey.software.Launcher.microneedleAnalysis;

import java.awt.geom.Point2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.joey.software.mathsToolkit.NumberDimension;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.controlers.ImageProfileTool;


public class MeasurmentData implements Externalizable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String exp = "";

	String view = "";

	Point2D.Double skinA = new Point2D.Double();

	Point2D.Double skinB = new Point2D.Double();

	Point2D.Double poreA = new Point2D.Double();

	Point2D.Double poreB = new Point2D.Double();

	Point2D.Double poreSkinA = new Point2D.Double();

	Point2D.Double poreSkinB = new Point2D.Double();

	/**
	 * Surface Data
	 */
	ImageProfileTool surfaceData = new ImageProfileTool(new ROIPanel(false));

	ImageProfileTool needleData = new ImageProfileTool(new ROIPanel(false));

	ImageProfileTool interfaceData = new ImageProfileTool(new ROIPanel(false));

	ImageProfileTool freeData = new ImageProfileTool(new ROIPanel(false));

	int angle = 0;

	int length = 0;

	int projectNum = 0;

	int projectType = 0;

	int measureType = 0;

	boolean surface = false;

	boolean needle = false;

	boolean interBorder = false;

	boolean skin = false;

	boolean pore = false;

	boolean poreSkin = false;

	boolean freeSlice = false;

	double riSkin = 1;

	double riPore = 1;

	double riSkinPore = 1;

	NumberDimension poreDim = new NumberDimension("m");

	NumberDimension skinDim = new NumberDimension("m");

	NumberDimension skinPoreDim = new NumberDimension("m");

	public MeasurmentData()
	{

	}

	public void setData(MicroNeedleAnalysis data)
	{
		data.blockUpdate = true;
		data.expNames.setSelectedItem(exp);
		data.viewNames.setSelectedItem(view);
		data.tool.surfaceLine.setPanelData(surfaceData);
		data.tool.needleLine.setPanelData(needleData);
		data.tool.interfaceLine.setPanelData(interfaceData);
		data.tool.freeAScanTool.setPanelData(freeData);

		data.tool.skinMeasure.points.get(0).setLocation(skinA);
		data.tool.skinMeasure.points.get(1).setLocation(skinB);

		data.tool.poreMeasure.points.get(0).setLocation(poreA);
		data.tool.poreMeasure.points.get(1).setLocation(poreB);

		data.tool.poreSkinMeasure.points.get(0).setLocation(poreSkinA);
		data.tool.poreSkinMeasure.points.get(1).setLocation(poreSkinB);

		data.angle.setValue(angle);
		data.project.setValue(projectNum);
		data.length.setValue(length);

		data.projectType.setSelectedIndex(projectType);
		data.tool.measure.setSelectedIndex(measureType);

		data.tool.showSurface.setSelected(surface);
		data.tool.showNeedleLine.setSelected(needle);
		data.tool.showInterface.setSelected(interBorder);
		data.tool.showSkinMeasure.setSelected(skin);
		data.tool.showPoreMeasure.setSelected(pore);
		data.tool.showPoreSkinMeasure.setSelected(poreSkin);
		data.tool.showFreeMeasure.setSelected(freeSlice);

		data.tool.riPore.setValue(riPore);
		data.tool.riSkin.setValue(riSkin);
		data.tool.riPoreSkin.setValue(riSkinPore);

		data.blockUpdate = false;
		data.loadCurrentData();
		data.tool.updateMeasure();
	}

	public void grabData(MicroNeedleAnalysis data)
	{
		exp = data.expNames.getSelectedItem().toString();
		view = data.viewNames.getSelectedItem().toString();

		surfaceData.setPanelData(data.tool.surfaceLine);
		needleData.setPanelData(data.tool.needleLine);
		interfaceData.setPanelData(data.tool.interfaceLine);
		freeData.setPanelData(data.tool.freeAScanTool);

		skinA.setLocation(data.tool.skinMeasure.points.get(0));
		skinB.setLocation(data.tool.skinMeasure.points.get(1));

		poreA.setLocation(data.tool.poreMeasure.points.get(0));
		poreB.setLocation(data.tool.poreMeasure.points.get(1));

		poreSkinA.setLocation(data.tool.poreSkinMeasure.points.get(0));
		poreSkinB.setLocation(data.tool.poreSkinMeasure.points.get(1));

		angle = (Integer) data.angle.getValue();
		projectNum = (Integer) data.project.getValue();
		length = (Integer) data.length.getValue();

		projectType = data.projectType.getSelectedIndex();
		measureType = data.tool.measure.getSelectedIndex();

		surface = data.tool.showSurface.isSelected();
		needle = data.tool.showNeedleLine.isSelected();
		interBorder = data.tool.showInterface.isSelected();
		skin = data.tool.showSkinMeasure.isSelected();
		pore = data.tool.showPoreMeasure.isSelected();
		poreSkin = data.tool.showPoreSkinMeasure.isSelected();
		freeSlice = data.tool.showFreeMeasure.isSelected();

		riPore = (Double) data.tool.riPore.getValue();
		riSkin = (Double) data.tool.riSkin.getValue();
		riSkinPore = (Double) data.tool.riPoreSkin.getValue();

		poreDim.setValue(data.tool.poreData);
		skinDim.setValue(data.tool.skinData);
		skinPoreDim.setValue(data.tool.poreSkinData);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		// version
		in.readInt();
		exp = in.readUTF();
		view = in.readUTF();

		skinA = (java.awt.geom.Point2D.Double) in.readObject();

		skinB = (java.awt.geom.Point2D.Double) in.readObject();

		poreA = (java.awt.geom.Point2D.Double) in.readObject();

		poreB = (java.awt.geom.Point2D.Double) in.readObject();

		poreSkinA = (java.awt.geom.Point2D.Double) in.readObject();

		poreSkinB = (java.awt.geom.Point2D.Double) in.readObject();

		/**
		 * Surface Data
		 */
		surfaceData = (ImageProfileTool) in.readObject();

		needleData = (ImageProfileTool) in.readObject();

		interfaceData = (ImageProfileTool) in.readObject();

		freeData = (ImageProfileTool) in.readObject();

		angle = in.readInt();

		length = in.readInt();

		projectNum = in.readInt();

		projectType = in.readInt();

		measureType = in.readInt();

		surface = in.readBoolean();

		needle = in.readBoolean();

		interBorder = in.readBoolean();

		skin = in.readBoolean();

		pore = in.readBoolean();

		poreSkin = in.readBoolean();

		freeSlice = in.readBoolean();

		riSkin = in.readDouble();

		riPore = in.readDouble();

		riSkinPore = in.readDouble();

		poreDim = (NumberDimension) in.readObject();

		skinDim = (NumberDimension) in.readObject();

		skinPoreDim = (NumberDimension) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// Version
		out.writeInt(1);
		out.writeUTF(exp);

		out.writeUTF(view);

		out.writeObject(skinA);

		out.writeObject(skinB);

		out.writeObject(poreA);

		out.writeObject(poreB);

		out.writeObject(poreSkinA);

		out.writeObject(poreSkinB);

		/**
		 * Surface Data
		 */
		out.writeObject(surfaceData);

		out.writeObject(needleData);

		out.writeObject(interfaceData);

		out.writeObject(freeData);

		out.writeInt(angle);

		out.writeInt(length);

		out.writeInt(projectNum);

		out.writeInt(projectType);

		out.writeInt(measureType);

		out.writeBoolean(surface);

		out.writeBoolean(needle);

		out.writeBoolean(interBorder);

		out.writeBoolean(skin);

		out.writeBoolean(pore);

		out.writeBoolean(poreSkin);

		out.writeBoolean(freeSlice);

		out.writeDouble(riSkin);

		out.writeDouble(riPore);

		out.writeDouble(riSkinPore);

		out.writeObject(poreDim);

		out.writeObject(skinDim);

		out.writeObject(skinPoreDim);

	}

}