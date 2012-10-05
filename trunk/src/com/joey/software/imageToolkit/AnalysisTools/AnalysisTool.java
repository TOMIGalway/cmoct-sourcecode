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
