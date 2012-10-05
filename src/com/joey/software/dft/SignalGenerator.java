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

import java.util.Random;

class SignalGenerator
{

	private String wform = "None";

	private float ampl = 1.0f;

	private float rate = 8000.0f;

	private float freq = 1000.0f;

	private float dcLevel = 0.0f;

	private float noise = 0.0f;

	private int nSamples = 256;

	private boolean addDCLevel = false;

	private boolean addNoise = false;

	public void setWaveform(String w)
	{
		wform = w;
	}

	public void setAmplitude(float a)
	{
		ampl = a;
	}

	public void setFrequency(float f)
	{
		freq = f;
	}

	public void setSamplingRate(float r)
	{
		rate = r;
	}

	public void setSamples(int s)
	{
		nSamples = s;
	}

	public void setDCLevel(float dc)
	{
		dcLevel = dc;
	}

	public void setNoise(float n)
	{
		noise = n;
	}

	public void setDCLevelState(boolean s)
	{
		addDCLevel = s;
	}

	public void setNoiseState(boolean s)
	{
		addNoise = s;
	}

	public float[] generate()
	{

		float[] values = new float[nSamples];

		if (wform.equals("Sine"))
		{ // sine wave
			float theta = 2.0f * (float) Math.PI * freq / rate;
			for (int i = 0; i < nSamples; i++)
				values[i] = ampl * (float) Math.sin(i * theta);
		}

		if (wform.equals("Cosine"))
		{ // cosine wave
			float theta = 2.0f * (float) Math.PI * freq / rate;
			for (int i = 0; i < nSamples; i++)
				values[i] = ampl * (float) Math.cos(i * theta);
		}

		if (wform.equals("Square"))
		{ // square wave
			float p = 2.0f * freq / rate;
			for (int i = 0; i < nSamples; i++)
				values[i] = Math.round(i * p) % 2 == 0 ? ampl : -ampl;
		}

		if (wform.equals("Triangular"))
		{ // triangular wave
			float p = 2.0f * freq / rate;
			for (int i = 0; i < nSamples; i++)
			{
				int ip = Math.round(i * p);
				values[i] = 2.0f * ampl * (1 - 2 * (ip % 2)) * (i * p - ip);
			}
		}

		if (wform.equals("Sawtooth"))
		{ // sawtooth wave
			for (int i = 0; i < nSamples; i++)
			{
				float q = i * freq / rate;
				values[i] = 2.0f * ampl * (q - Math.round(q));
			}
		}

		if (addDCLevel)
		{ // add constant DC level to signal
			for (int i = 0; i < nSamples; i++)
				values[i] += dcLevel;
		}

		if (addNoise)
		{ // add random "noise" to signal
			Random r = new Random();
			for (int i = 0; i < nSamples; i++)
				values[i] += noise * r.nextGaussian();
		}

		return values;
	}
}
