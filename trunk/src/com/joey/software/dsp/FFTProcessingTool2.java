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
package com.joey.software.dsp;

import com.joey.software.threadToolkit.Task;
import com.joey.software.threadToolkit.TaskMaster;
import com.joey.software.timeingToolkit.EventTimer;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

public class FFTProcessingTool2
{
	TaskMaster master;

	int threadNum = 5;

	public static void main(String input[])
	{
		int fftNum = 1024;
		int fftSize = 2048;

		float[][] spec = new float[fftNum][fftSize * 2];
		float[][] phase = new float[fftNum][fftSize];
		float[][] mag = new float[fftNum][fftSize];

		EventTimer t = new EventTimer();

		FFTProcessingTool2 fft2 = new FFTProcessingTool2();
		// for (int i = 1; i < fft2.taskNum; i += 1)
		// {
		fft2.threadNum = 10;
		t.mark("New");
		fft2.processData(spec, mag, phase);
		t.tick("New");
		// }
		t.mark("Shit");
		fft2.processDataLinear(spec, mag);
		t.tick("Shit");

		float[][] specA = new float[fftNum][fftSize];
		float[][] realA = new float[fftNum][fftSize];
		float[][] imagA = new float[fftNum][fftSize];
		float[][] phaseA = new float[fftNum][fftSize];
		float[][] magA = new float[fftNum][fftSize];
		FFTProcessingTool fft = new FFTProcessingTool(fft2.threadNum);

		t.mark("Old");
		fft.processData(specA, realA, imagA, magA, phaseA);
		t.tick("Old");

		t.printData();

		System.exit(0);
	}

	public FFTProcessingTool2()
	{
		master = new TaskMaster(threadNum, threadNum*2);
		master.start();
	}

	public void processData(float[][] spec, float[][] mag, float[][] phase)
	{

		for (int i = 0; i < spec.length; i++)
		{
			master.addTask(new FFTprocessor(spec, mag, phase, i));
		}
		master.waitForCompletion();

	}

	public void processDataLinear(float[][] spec, float[][] mag)
	{
		FloatFFT_1D fft = new FloatFFT_1D(spec[0].length / 2);
		for (int pos = 0; pos < spec.length; pos++)
		{
			fft.complexForward(spec[pos]);
			for (int i = 0; i < spec[pos].length / 2; i++)
			{
				mag[pos][i] = (float) Math.sqrt(spec[pos][i] * spec[pos][i]
						+ spec[pos][i + 1] * spec[pos][i + 1]);
			}
		}
	}
}

class FFTprocessor implements Task
{
	int pos = 0;

	float[][] spec;

	float[][] mag;

	float[][] phase;

	FloatFFT_1D fft;

	public FFTprocessor(float[][] spec, float[][] mag, float[][] phase, int pos)
	{
		this.pos = pos;
		fft = new FloatFFT_1D(spec[pos].length / 2);
		this.spec = spec;
		this.mag = mag;
		this.phase = phase;
	}

	@Override
	public void doTask()
	{
		fft.complexForward(spec[pos]);
		fft = null;
		for (int i = 0; i < spec[pos].length / 2; i++)
		{
			mag[pos][i] = (float) Math.sqrt(spec[pos][i] * spec[pos][i]
					+ spec[pos][i + 1] * spec[pos][i + 1]);
		//	phase[pos][i] = (float) Math.atan2(spec[pos][i + 1], spec[pos][i]);
		}
	}

}
