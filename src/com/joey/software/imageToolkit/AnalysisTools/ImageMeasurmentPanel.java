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
