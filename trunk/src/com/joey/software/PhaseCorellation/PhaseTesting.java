package com.joey.software.PhaseCorellation;


import java.io.File;
import java.io.IOException;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.DataToolkit.thorlabs.ThorlabsFRGImageProducer;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;


public class PhaseTesting
{	
	public static void main(String inpt[]) throws IOException
	{
		File f= new File("D:\\Current Analysis\\Project Data\\Correlation\\for cross-correlation processing\\New Folder\\IMG1024palmarm_001.FRG");
		final ThorlabsFRGImageProducer loader = new ThorlabsFRGImageProducer(f);
		
		loader.loadSpecData(0);
		loader.processSpecData();
		final float[][] real = loader.getSpectrogramReal();
		final float[][] imag=loader.getSpectrogramImag();
		final float[][] mag = loader.getSpectrogramMag();
		final float[][] phase =loader.getSpectrogramPhase();
		
		final DynamicRangeImage realPan = new DynamicRangeImage(real);
		final DynamicRangeImage imagPan = new DynamicRangeImage(imag);
		final DynamicRangeImage magPan = new DynamicRangeImage(mag);
		final DynamicRangeImage phasePan = new DynamicRangeImage(phase);
		
		FrameFactroy.getFrameTabs(magPan, realPan,imagPan, phasePan);
		final JSpinner frame = new JSpinner(new SpinnerNumberModel(0,0,loader.getImageCount(),1));
		FrameFactroy.getFrame(frame);
		
		frame.addChangeListener(new ChangeListener()
		{
			
			@Override
			public void stateChanged(ChangeEvent e)
			{
				try
				{
					loader.loadSpecData((Integer)frame.getValue());
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				loader.processSpecData();
				
				loader.getSpectrogramReal(real);
				loader.getSpectrogramImag(imag);
				loader.getSpectrogramMag(mag);
				loader.getSpectrogramPhase(phase);
				
				realPan.updateImagePanel();
				imagPan.updateImagePanel();
				magPan.updateImagePanel();
				phasePan.updateImagePanel();
			}
		});
		
		
	}

}
