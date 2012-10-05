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
package com.joey.software.DataToolkit.herbeshFormat;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.dsp.FFTtool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class HerbeshFormatBackgroundCreator extends JPanel
{
	int sizeY = 0;
	File dataFile;
	float backgroundAScan[];
	byte[] rawDataHolder;
	
	public void readAScanBackgroundData(int avgNum, boolean smooth, float sigma) throws IOException{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(dataFile)));
		
		float[] temp = new float[sizeY];
		
		
		ArrayToolkit.setValue(backgroundAScan, 0f);
		for(int x = 0; x < avgNum; x++){
			in.read(rawDataHolder);			
			HerbeshFormatImageProducer.convertByte2Float(rawDataHolder, temp);
			
			ArrayToolkit.add(temp, backgroundAScan, backgroundAScan);
		}
		ArrayToolkit.scale(backgroundAScan, 1f/avgNum);
		
		if(smooth){
			FFTtool tool = new FFTtool(sizeY);
			tool.allocateMemory();
			tool.setRealData(backgroundAScan);
			tool.fftData();
			tool.gaussianBlur(sigma);
			tool.ifftData(true);
			tool.getMagData(backgroundAScan);
		}
		in.close();
	}
	
	
	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(dataFile)));


		float[] aScan = new float[sizeY];
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JFrame f= FrameFactroy.getFrame(panel);
		
		
		for(int x = 0; x < sizeX; x++){
			in.read(rawDataHolder);	
			convertByte2Float(rawDataHolder, aScan);
			panel.removeAll();
			panel.add(PlotingToolkit.getChartPanel(new float[][]{backgroundAScan, aScan},	new String[]{"Avg","Scan"}, "","","", false, true), BorderLayout.CENTER);
			f.validate();
			System.out.println(x);
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}	
		return null;
	}
}
