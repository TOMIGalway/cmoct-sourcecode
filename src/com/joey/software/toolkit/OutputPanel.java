package com.joey.software.toolkit;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.SwingToolkit;


public class OutputPanel extends JPanel
{
	JCheckBox saveStruct = new JCheckBox();

	JCheckBox saveFlow = new JCheckBox();
	
	JCheckBox saveProjection = new JCheckBox();

	JCheckBox saveAvg = new JCheckBox("Avg");

	JCheckBox saveMax = new JCheckBox("Max");

	JCheckBox saveMin = new JCheckBox("Min");

	JCheckBox rangeProjection = new JCheckBox();

	JSpinner rangeStart = new JSpinner();

	JSpinner rangeEnd = new JSpinner();

	JCheckBox stepProjection = new JCheckBox();

	JSpinner stepSize = new JSpinner();
	
	public OutputPanel()
	{
		createJPanel();
	}
	
	public void createJPanel()
	{
		int size = 80;
		
		JPanel basicPanel = new JPanel(new GridLayout(3,1));
		basicPanel.add(SwingToolkit.getLabel(saveStruct, "Structure :", size));
		basicPanel.add(SwingToolkit.getLabel(saveFlow, "Flow :", size));
		basicPanel.add(SwingToolkit.getLabel(saveProjection, "Projections :", size));
		
		
		
		
		JPanel projectListPanel =new JPanel(new GridLayout(1,3));
		projectListPanel.add(saveAvg);
		projectListPanel.add(saveMin);
		projectListPanel.add(saveMax);
		projectListPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		JPanel projectOptionsPanel = new JPanel(new GridLayout(3,1));
		projectOptionsPanel.add(SwingToolkit.getLabel(rangeProjection, "Range :", size));
		
		
		JPanel projectPanel = new JPanel(new BorderLayout());
		projectPanel.add(projectListPanel, BorderLayout.NORTH);
		projectPanel.add(projectOptionsPanel, BorderLayout.CENTER);
		
		JPanel projectSettingsPanel = new JPanel(new BorderLayout());
		projectSettingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		projectSettingsPanel.add(projectPanel, BorderLayout.NORTH);
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Output"));
		add(basicPanel, BorderLayout.NORTH);
		add(projectPanel, BorderLayout.CENTER);
	}
	
	public static void main(String input[])
	{
		OutputPanel panel = new OutputPanel();
		
		FrameFactroy.getFrame(panel);
	}
}
