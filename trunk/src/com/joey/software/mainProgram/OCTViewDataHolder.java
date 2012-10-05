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
package com.joey.software.mainProgram;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import com.joey.software.sliceTools.OCTSliceViewer;
import com.joey.software.sliceTools.OCTSliceViewerDataHolder;
import com.joey.software.volumeTools.OCTVolumeDivider;
import com.joey.software.volumeTools.OCTVolumeDividerDataHolder;


public class OCTViewDataHolder
{
	private String name;

	OCTExperimentData expData;

	private OCTSliceViewerDataHolder sliceData;

	private OCTVolumeDividerDataHolder volumeData;

	DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(this);

	public OCTViewDataHolder(OCTExperimentData expData, String name, OCTSliceViewerDataHolder sliceData, OCTVolumeDividerDataHolder volumeData)
	{
		setName(name);
		setData(expData);
		setSliceDataHolder(sliceData);
		setVolumeDataHolder(volumeData);
	}

	public void resetViewData()
	{
		sliceData.initData(expData.getData());
		volumeData.initData(expData.getData());
	}

	public OCTViewDataHolder getCopy()
	{
		OCTSliceViewerDataHolder slice = new OCTSliceViewerDataHolder();
		slice.setData(sliceData);

		OCTVolumeDividerDataHolder volume = new OCTVolumeDividerDataHolder();
		volume.setData(volumeData);

		OCTViewDataHolder rst = new OCTViewDataHolder(expData, name + "- Copy",
				slice, volume);

		return rst;
	}

	public OCTViewDataHolder(OCTExperimentData expData, String name)
	{
		this(expData, name, new OCTSliceViewerDataHolder(),
				new OCTVolumeDividerDataHolder());
		resetViewData();
	}

	public OCTSliceViewerDataHolder getSliceDataHolder()
	{
		return getSliceData();
	}

	public void setSliceDataHolder(OCTSliceViewerDataHolder sliceData)
	{
		this.setSliceData(sliceData);
	}

	public OCTVolumeDividerDataHolder getVolumeDataHolder()
	{
		return getVolumeData();
	}

	public void setVolumeDataHolder(OCTVolumeDividerDataHolder volumeData)
	{
		this.setVolumeData(volumeData);
	}

	public DefaultMutableTreeNode getTreeNode()
	{
		return treeNode;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public void setName(String name)
	{
		this.name = name;
		try
		{
			expData.owner.getModel().reload(treeNode);
		} catch (Exception e)
		{

		}
	}

	public void setData(OCTExperimentData expData)
	{
		this.expData = expData;
	}

	public void getVolumeData(OCTVolumeDivider volumePanel)
	{
		volumeData.getData(volumePanel);
	}

	public void getSliceData(OCTSliceViewer slicePanel)
	{
		sliceData.getData(slicePanel);
	}

	public void setVolumeData(OCTVolumeDivider volumePanel) throws IOException
	{
		volumePanel.setOCTData(expData.getData());
		volumeData.setData(volumePanel);
	}

	public void setSliceData(OCTSliceViewer slicePanel)
	{
		slicePanel.setUpdateAllowed(false);
		slicePanel.setOCTData(expData.getData());
		slicePanel.setUpdateAllowed(true);
		sliceData.setData(slicePanel);
	}

	public String getName()
	{
		return name;
	}

	public void setSliceData(OCTSliceViewerDataHolder sliceData)
	{
		this.sliceData = sliceData;
	}

	public OCTSliceViewerDataHolder getSliceData()
	{
		return sliceData;
	}

	public void setVolumeData(OCTVolumeDividerDataHolder volumeData)
	{
		this.volumeData = volumeData;
	}

	public OCTVolumeDividerDataHolder getVolumeData()
	{
		return volumeData;
	}

	public void renameView()
	{
		JTextField field = new JTextField(name);
		if (JOptionPane
				.showConfirmDialog(null, field, "Enter new Name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			setName(field.getText());
		}

	}

}
