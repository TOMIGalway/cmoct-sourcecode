package com.joey.software.imageToolkit.AnalysisTools;


import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.mathsToolkit.NumberDimension;


public class ImageMeasurmentPanel
{
	JPanel toolPanel = new JPanel();

	ImagePanel image = new ImagePanel();

	JToolBar tools = new JToolBar();

	NumberDimension sizeX = new NumberDimension("pxl");

	NumberDimension sizeY = new NumberDimension("pxl");

	public ImageMeasurmentPanel()
	{

	}
}
