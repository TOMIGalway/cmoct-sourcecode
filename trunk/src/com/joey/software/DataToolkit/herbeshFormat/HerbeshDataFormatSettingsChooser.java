package com.joey.software.DataToolkit.herbeshFormat;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.ImagePanel;

public class HerbeshDataFormatSettingsChooser extends JPanel
{
	JButton updateMax;
	JSpinner maxValue;
	JSpinner dynamicRange;
	JSpinner currentFrame;
	JButton update;
	
	HerbeshFormatImageProducer data;
	ImagePanel previewPanel = new ImagePanel();
	public HerbeshDataFormatSettingsChooser(HerbeshFormatImageProducer input){
		this.data = input;
		createJPanel();
	}
	
	public void createJPanel(){
		updateMax = new JButton("Calculate Max");
		maxValue = new JSpinner(new SpinnerNumberModel(data.maxValue, 0, Double.MAX_VALUE, 1));
		dynamicRange=new JSpinner(new SpinnerNumberModel(data.dynamicRange, 0, Double.MAX_VALUE, 1));
		currentFrame = new JSpinner(new SpinnerNumberModel(0,0,data.sizeZ, 1));
		update = new JButton("Update");
		
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new GridLayout(6,1));
		int labelSize = 80;
		toolPanel.add(SwingToolkit.getLabel(currentFrame, "Frame", labelSize));
		toolPanel.add(SwingToolkit.getLabel(maxValue, "Max Value:", labelSize));
		toolPanel.add(updateMax);
		toolPanel.add(SwingToolkit.getLabel(dynamicRange, "Dynamic Range", labelSize));
		toolPanel.add(new JSeparator());
		toolPanel.add(update);
		
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(toolPanel, BorderLayout.NORTH);
		
		JSplitPane split = new JSplitPane();
		split.setLeftComponent(temp);
		split.setRightComponent(previewPanel);
		split.setDividerLocation(200);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		
		ChangeListener change = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				repaintImage();
			}
		};
		maxValue.addChangeListener(change);
		dynamicRange.addChangeListener(change);
		currentFrame.addChangeListener(change);
		updateMax.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				data.updateMaxValue();
				maxValue.setValue(data.maxValue);
			}
		});
		
		update.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				repaintImage();
			}
		});
	}
	
	public float getMaxValue(){
		return ((Number)maxValue.getValue()).floatValue();
	}
	
	public float getDynamicRange(){
		return((Number)dynamicRange.getValue()).floatValue();
	}
	
	public int getCurrentFrame(){
		return((Number)currentFrame.getValue()).intValue();
	}
	
	public void repaintImage(){
		data.maxValue = getMaxValue();
		data.dynamicRange = getDynamicRange();
		try
		{
			previewPanel.setImage(data.getImage(getCurrentFrame()));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
