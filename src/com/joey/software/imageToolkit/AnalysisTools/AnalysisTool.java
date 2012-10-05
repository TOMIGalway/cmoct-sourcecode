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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public interface AnalysisTool
{
	public String getToolDescritpion();

	public BufferedImage getToolImage();

	public void paintTool(Graphics2D g);

	public void createToolPanel();

	public JPanel getToolPanel();

	public void updateData();
}
