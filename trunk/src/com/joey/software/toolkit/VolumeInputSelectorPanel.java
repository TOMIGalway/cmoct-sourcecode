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
package com.joey.software.toolkit;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;


public class VolumeInputSelectorPanel extends JPanel
{
	JTabbedPane tabs = new JTabbedPane();

	FileSelectionField thorlabs = new FileSelectionField();
	JCheckBox forceSingle = new JCheckBox("Force Single", true);
	ImageFileSelectorPanel panel = new ImageFileSelectorPanel();

	public VolumeInputSelectorPanel()
	{
		createJPanel();
	}

	public void createJPanel()
	{
		JPanel holder = new JPanel(new BorderLayout());
		holder.add(thorlabs,BorderLayout.CENTER);
		holder.add(forceSingle, BorderLayout.EAST);
		
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(holder, BorderLayout.NORTH);
		
		tabs.addTab("Thorlabs Data", temp);
		tabs.addTab("Image Series", panel);

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	public File[] getFileData() throws IOException
	{
		if (JOptionPane
				.showConfirmDialog(null, this, "Select Input Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			if (tabs.getSelectedIndex() == 0)
			{
				return new File[] { (thorlabs.getFile()) };
			} else
			{
				return (panel.getFiles());
			}
		}
		return null;
	}

	public byte[][][] getVolumeData(StatusBarPanel status) throws IOException
	{
		if (JOptionPane
				.showConfirmDialog(null, this, "Select Input Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			if (tabs.getSelectedIndex() == 0)
			{
				File f = thorlabs.getFile();
				ThorlabsIMGImageProducer dataLoader = new ThorlabsIMGImageProducer(
						f, forceSingle.isSelected());

				final byte[][][] data = dataLoader.createDataHolder();
				dataLoader.getData(data, status);

				return data;
			} else
			{
				ImageFileProducer dataLoader = new ImageFileProducer(
						panel.getFiles());
				final byte[][][] data = dataLoader.createDataHolder();
				dataLoader.getData(data, status);
				return data;
			}
		}
		return null;
	}

	public static byte[][][] getUserVolumeData(StatusBarPanel status)
			throws IOException
	{
		VolumeInputSelectorPanel vol = new VolumeInputSelectorPanel();
		return vol.getVolumeData(status);
	}
}
