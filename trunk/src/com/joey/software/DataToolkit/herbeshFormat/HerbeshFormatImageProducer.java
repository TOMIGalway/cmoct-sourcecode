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


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.dsp.FFTtool;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMap;


public class HerbeshFormatImageProducer extends ImageProducer
{
	static boolean littleEndian = false;
	ColorMap colorMap = ColorMap.getColorMap(ColorMap.TYPE_GRAY);
	
	private File pramFile;
	private File dataFile;
	
	int aScansToAvgForBackground = 600;
	int aScansToAvgStartPos = 0;
	int aScanSize = 1024;
	int sizeX = 512;
	int sizeY = 512; 
	int sizeZ = 100;
	
	float W0=1240;
	float W1=0.12f;
	float W2=-1.77e-5f;
	float W3=2.1e-9f;
	float c=2.99795458E8f;
	float nm = 1E-9f;
	
	float[][] linearFrame;
	float[][] logFrame;
	
	float[] backgroundAScan;
	
	byte[] rawDataHolder; 
	float[] aScanHolder;
	
	float[] K_ES;
	float[] K;
	
	int bytePerAScan =1;
	FFTtool fft;
	DataInterpolator inter;
	

	float maxValue = 0;
	float dynamicRange = 40;
	
	public void readParamFile(){
		
	}
	
	public void readDataFile(){
		
	}
	
	public void readAScanBackgroundData() throws IOException{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(dataFile)));
		
		float[] temp = new float[aScanSize];
		ArrayToolkit.setValue(backgroundAScan, 0f);
		
		in.skip(aScansToAvgStartPos*aScanSize*bytePerAScan);
		for(int x = 0; x < aScansToAvgForBackground; x++){
			in.read(rawDataHolder);			
			HerbeshFormatImageProducer.convertByte2Float(rawDataHolder, temp);
			ArrayToolkit.add(temp, backgroundAScan, backgroundAScan);
		}
		ArrayToolkit.scale(backgroundAScan, 1f/aScansToAvgForBackground);
		in.close();
	}
	
	public static void convertByte2Float(byte[] rawDataHolder, float[] convertedData){
		if(littleEndian){
			for(int i = 0; i < convertedData.length; i++){
				convertedData[i] = BinaryToolkit.readShort(rawDataHolder, 2*i);
			}
		}else{
			for(int i = 0; i < convertedData.length; i++){
				convertedData[i] = BinaryToolkit.readFlippedShort(rawDataHolder, 2*i);
			}
		}
	}
	
	public void allocateMemory(){
		bytePerAScan =  (aScanSize*Short.SIZE/8);
		rawDataHolder = new byte[bytePerAScan];
		backgroundAScan = new float[aScanSize];
		linearFrame=new float[sizeX][sizeY];
		logFrame=new float[sizeX][sizeY];
		K = new float[aScanSize];
		K_ES = new float[aScanSize];
		aScanHolder = new float[aScanSize];
		fft = new FFTtool(aScanSize);
		fft.allocateMemory();
		
		
		//This 
		for(int i = 0; i < K.length; i++){
			K[aScanSize-1-i]=c/((W0+W1*(i)+W2*(i*i)+W3*(i*i*i))*nm);
		}
		float kstep = (K[aScanSize-1]-K[0])/(aScanSize-1);
		for(int i = 0; i < K.length; i++){
			K_ES[i] = K[0]+kstep*i;
		}
		inter = new DataInterpolator(K,K_ES);
	}

	@Override
	public void getImage(int pos, byte[][] data) throws IOException
	{
		loadFrame(pos);
	}
	
	public float getDBValue(int x, int y){
		return (float)(10*Math.log10(linearFrame[x][y]/maxValue));
	}
	@Override
	public BufferedImage getImage(int pos) throws IOException{
		BufferedImage img = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_BYTE_GRAY);
		loadFrame(pos);
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				float val = 1+getDBValue(x, y)/dynamicRange;
				
				val = val>1?1:val;
				val = val<0?0:val;
				img.setRGB(x, y, colorMap.getColor(val).getRGB());
			}
		}
		return img;
	}

	public void loadFrame(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(dataFile)));
		in.skip(bytePerAScan*sizeX*pos);
		for(int x = 0; x < sizeX; x++){
			in.read(rawDataHolder);	
			
			convertByte2Float(rawDataHolder, aScanHolder);
			ArrayToolkit.subtract(aScanHolder, backgroundAScan, aScanHolder);
			
			inter.y=aScanHolder;
			inter.yRescale=aScanHolder;
			inter.calc();
			fft.setRealData(aScanHolder);
			fft.ifftData(true);
			fft.getMagData(linearFrame[x]);
		}			
		in.close();
	}
	
	public void updateLogFrame(){
		for(int x = 0; x < logFrame.length; x++){
			for(int y = 0; y < logFrame[x].length; y++){
				logFrame[x][y] = getDBValue(x, y)/dynamicRange;
			}
		}
	}
	public void updateMaxValue(){
		maxValue = ArrayToolkit.getMax(linearFrame);
	}
	
	public File getPramFile()
	{
		return pramFile;
	}

	public void setPramFile(File pramFile)
	{
		this.pramFile = pramFile;
	}

	public File getDataFile()
	{
		return dataFile;
	}

	public void setDataFile(File dataFile)
	{
		this.dataFile = dataFile;
	}
	@Override
	public int getImageCount()
	{
		return sizeZ;
	}
	
	public void getUserInputs(){
		HerbeshDataFormatSettingsChooser chooser = new HerbeshDataFormatSettingsChooser(this);
		
		FrameWaitForClose.waitForFrame(FrameFactroy.getFrame(chooser));
	}
	public static void main(String input[]) throws IOException{
		String path = "C:\\Users\\joey.enfield\\Desktop\\Herbesh\\New Data\\";
		File parmFile = new File(path+"\\TEST1.prm");
		File dataFile = new File(path+"\\TEST1.dat");
		
		HerbeshFormatImageProducer data = new HerbeshFormatImageProducer();
		data.setPramFile(parmFile);
		data.setDataFile(dataFile);

		data.allocateMemory();
		data.readAScanBackgroundData();
		
		data.getUserInputs();
		
		ImagePanel panel = new ImagePanel();
		DynamicRangeImage linearImage=  new DynamicRangeImage(data.linearFrame);
		linearImage.setPanelType(DynamicRangeImage.PANEL_TYPE_BASIC);
		DynamicRangeImage logImage=  new DynamicRangeImage(data.logFrame);
		logImage.setPanelType(DynamicRangeImage.PANEL_TYPE_BASIC);
		
		FrameFactroy.getFrameTabs(linearImage,logImage, panel);
		
		
		int i =0;
		while(true){
			i++;
			try{
			System.out.println(i);
			panel.setImage(data.getImage(i));
			data.updateMaxValue();
			data.updateLogFrame();
			linearImage.updateImagePanel();
			logImage.updateImagePanel();
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getLocalizedMessage());
				i =0;
			}
			if(i>data.sizeZ){
				i = 0;
			}
			
		}
	}

}
