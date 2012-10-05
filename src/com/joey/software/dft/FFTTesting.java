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
package com.joey.software.dft;

import com.joey.software.SingnalAnalysis.FastFourierTransform;
import com.joey.software.dsp.FastFourierTransform1;
import com.joey.software.dsp.FastFourierTransform2;
import com.joey.software.dsp.FastFourierTransform3;


public class FFTTesting
{
	public static void main(String input[])
	{
		int num= (int)Math.pow(2, 10);
		float[] reInA = new float[num*2];
		float[] reInB = new float[num];
		float[] reInC = new float[num];
		float[] reInD = new float[num];
		
		for (int i = 0; i < num; i++)
		{
			float val = (float) Math.sin(Math.toRadians(i)*10);
			 reInA[i*2] = val;
			 reInB[i] = val;
			 reInC[i] = val;
			 reInD[i] = val;
		}
		
		
		
		FastFourierTransform fftA = new FastFourierTransform();
		FastFourierTransform1 fftB = new FastFourierTransform1();
		FastFourierTransform2 fftC = new FastFourierTransform2(num);
		FastFourierTransform3 fftD = new FastFourierTransform3(num);
		
		
		float[] magA = fftA.fftMag(reInA);
		float[] magB = fftB.fft(reInB)[2];
		float[] magC = new float[num];
		fftC.fft(reInC, new float[num], new float[num], new float[num], magC, new float[num]);
		
		float[] magD = new float[num];
		fftD.fft(reInD, new float[num], new float[num], new float[num], magD, new float[num]);
		
		for(int i=0; i < num; i++)
		{
			System.out.println(reInB[i]+","+magA[i]+","+magB[i]+","+magC[i]+","+magD[i]);
		}
	}
}
