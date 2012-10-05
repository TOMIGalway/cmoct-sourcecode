package com.joey.software.interfaces;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;


public class InputSelectorPanel extends JPanel
{
	JTabbedPane tabs = new JTabbedPane();

	FileSelectionField thorlabs = new FileSelectionField();

	ImageFileSelectorPanel panel = new ImageFileSelectorPanel();

	public InputSelectorPanel()
	{
		createJPanel();
	}

	public void createJPanel()
	{
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(thorlabs, BorderLayout.NORTH);
		tabs.addTab("Thorlabs Data", temp);
		tabs.addTab("Image Series", panel);

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	public void getUserInput(CrossCorrProgram program) throws IOException
	{
		if (JOptionPane
				.showConfirmDialog(program, this, "Select Input Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			if (tabs.getSelectedIndex() == 0)
			{
				program.loadData(thorlabs.getFile());
			} else
			{
				program.loadData(panel.getFiles());
			}
		}
	}
	
	public File[] getUserInput() throws IOException
	{
		if (JOptionPane
				.showConfirmDialog(null, this, "Select Input Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			if (tabs.getSelectedIndex() == 0)
			{
				return new File[]{ (thorlabs.getFile())};
			} else
			{
				return (panel.getFiles());
			}
		}
		return null;
	}

}
