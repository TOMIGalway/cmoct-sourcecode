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
package com.joey.software.userinterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.sliceTools.OCTSliceViewer;
import com.joey.software.volumeTools.OCTVolumeDivider;


public class OCTDataViewPanel extends JPanel implements Externalizable
{
	private static final long serialVersionUID = VersionManager
			.getCurrentVersion();

	String name;

	NativeDataSet data;

	public OCTVolumeDivider volumeView;

	public OCTSliceViewer sliceView;

	JTabbedPane tabPane = new JTabbedPane(SwingConstants.LEFT);

	static FileSelectionField loadRawPanel = new FileSelectionField();

	static FileSelectionField loadPrvPanel = new FileSelectionField();

	static final JTextField tabNameField = new JTextField(40);

	static JPanel loadOCTDataPanel = null;

	boolean loaded = true;

	public boolean isLoaded()
	{
		return loaded;
	}

	public OCTDataViewPanel() throws IOException
	{
		volumeView = new OCTVolumeDivider();
		sliceView = new OCTSliceViewer();

		createJPanel();
	}

	public void unloadAllData()
	{
		data.unloadData();
		sliceView.unloadData();
		volumeView.unloadData();
		loaded = false;
		System.gc();
	}

	public void reloadAllData()
	{
		System.gc();
		data.reloadData();
		sliceView.reloadData();
		volumeView.reloadData();
		loaded = true;
	}

	public OCTDataViewPanel(NativeDataSet data, String name) throws IOException
	{
		this();
		setOCTDataSet(data, name);
		volumeView.updateVolume(false);

		if (volumeView.getStatus() != null)
		{
			volumeView.getStatus().setStatusMessage("Loading Color Map");
		}
		volumeView.getVolumeViewer().getViewPanel().editCmap(false, volumeView
				.getStatus());
		volumeView.getVolumeViewer().getViewPanel().updateCmap(volumeView
				.getStatus());
	}

	public static JPanel getOCTLoadPanel()
	{
		if (loadOCTDataPanel == null)
		{
			loadOCTDataPanel = new JPanel();
			JPanel octNamePanel = new JPanel();

			SwingToolkit.createPanel(new String[]
			{ "Tab Name :" }, new JComponent[]
			{ tabNameField }, 60, 10, octNamePanel);

			loadRawPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
			loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);

			loadRawPanel.setLabelSize(60);
			loadPrvPanel.setLabelSize(60);

			loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);

			loadRawPanel.setLabelText("Raw : ");
			loadPrvPanel.setLabelText("Prv : ");

			loadOCTDataPanel.setLayout(new GridLayout(3, 1, 0, 2));
			loadOCTDataPanel.add(octNamePanel);
			loadOCTDataPanel.add(loadRawPanel);
			loadOCTDataPanel.add(loadPrvPanel);
		}
		return loadOCTDataPanel;
	}

	public static OCTDataViewPanel getUserSelection(Component owner)
	{
		OCTDataViewPanel result = null;
		int val = JOptionPane.showConfirmDialog(owner, getOCTLoadPanel());
		if (val == JOptionPane.OK_OPTION)
		{
			try
			{
				NativeDataSet data = new NativeDataSet(loadRawPanel.getFile(),
						loadPrvPanel.getFile());
				result = new OCTDataViewPanel(data, tabNameField.getText());
			} catch (IOException e)
			{
				JOptionPane
						.showMessageDialog(owner, "Error Loading File : "
								+ e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}

		}
		return result;
	}

	public void createJPanel()
	{
		removeAll();
		tabPane.removeAll();
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
		tabPane.addTab("2D Slice", sliceView);
		tabPane.addTab("3D Volume", volumeView);
	}

	public void setOCTDataSet(NativeDataSet data, String name)
			throws IOException
	{
		this.data = data;
		this.name = name;
		volumeView.setOCTData(data);
		sliceView.setOCTData(data);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		sliceView = (OCTSliceViewer) in.readObject();
		volumeView = (OCTVolumeDivider) in.readObject();
		String name = in.readUTF();
		setOCTDataSet(sliceView.getOCTData(), name);
		volumeView.getVolumeViewer().getViewPanel().editCmap(false, null);
		volumeView.getVolumeViewer().getViewPanel().updateCmap(null);
		createJPanel();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(sliceView);
		out.writeObject(volumeView);
		out.writeUTF(name);
	}
}
