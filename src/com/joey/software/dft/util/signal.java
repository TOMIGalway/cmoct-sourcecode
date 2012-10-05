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
package com.joey.software.dft.util;

/**
 * This class provides a signal generator for testing Fourier transform code.
 */
public class signal
{

	/** current x */
	private float x;

	/** number of samples for a signal phase */
	private float rate;

	/** "step" (or increment) between samples */
	private float step;

	/**
	 * coeficients scaling the result of the <i>sin</i> function (e.g.,
	 * coef[i]*sin(x)
	 */
	private float coef[];

	/**
	 * scaling factor for the argument to the <i>sin</i> function (e.g., sin(
	 * scale[i]*x )
	 */
	private float scale[];

	/**
	 * offset factor for the argument to the <i>sin</i> function (e.g., sin(x +
	 * offset[i])
	 */
	private float offset[];

	/**
	 * <p>
	 * The constructor is passed:
	 * </p>
	 * 
	 * @param r
	 *            The number of samples for one cycle of the generated signal.
	 * @param c
	 *            An array of coefficients for the result of the <i>sin</i>
	 *            function (for example: for c = {1.5f, 3.0f}, 1.5sin(x) +
	 *            3.0sin(x))
	 * @param s
	 *            An array of scaling factors for the argument to the <i>sin</i>
	 *            function (for example: for s = {0.5f, 0.25f}, sin(x/2) +
	 *            sin(x/4))
	 * @param o
	 *            An array of soffset factors for the argument to the <i>sin</i>
	 *            function (for example: for o = {(float)Math.PI/4.0f}, sin( x +
	 *            pi/4 ))
	 * 
	 *            <p>
	 *            The signal produced by nextVal() is the sum of a set of
	 *            <i>sin</i>(x) values. The number of <i>sin</i>(x) values is
	 *            <tt>len = max(c.length, s.length)</tt>.
	 *            </p>
	 * 
	 *            <pre>
	 * .             len
	 *   .             ---
	 *   signal(x) =   \
	 *   .             /    c&lt;sub&gt;i&lt;/sub&gt; * sin( s&lt;sub&gt;i&lt;/sub&gt; * x + o&lt;sub&gt;i&lt;/sub&gt;)
	 *   .             ---
	 *   .             i=0
	 * </pre>
	 *            <p>
	 *            Here c<sub>i</sub> scales the result of the sin(x) function,
	 *            s<sub>i</sub> scales the argument to sin(x) and o<sub>i</sub>
	 *            is an offset to the argument of sin(x).
	 *            </p>
	 * 
	 *            <p>
	 *            Example:
	 *            </p>
	 * 
	 *            <pre>
	 * float sampleRate = 16;
	 *     float coef[] = new float[] {1.0f};
	 *     float scale[] = new float[] {2.0f, 1.0f, 0.5f};
	 *     float offset[] = new float[] { 0.0f, (float)Math.PI/4.0f }
	 * 
	 *     signal sig = new signal( sampleRate, coef, scale, offset );
	 * </pre>
	 * 
	 *            <p>
	 *            This will result in the signal
	 *            </p>
	 * 
	 *            <pre>
	 * f(x) = sin( 2x ) + sin( x + pi/4 ) + sin(x/2);
	 * </pre>
	 * 
	 *            <p>
	 *            Any or all of the <tt>coef</tt>, <tt>scale</tt> and
	 *            <tt>offset</tt> arguments may be <tt>null</tt>. If they are
	 *            supplied, they do not have to be the same size. The shorter
	 *            array will be padded with appropriate values by the signal
	 *            object.
	 *            </p>
	 * 
	 *            <p>
	 *            If <tt>coef</tt>, <tt>scale</tt> and <tt>offset</tt> arguments
	 *            are <tt>null</tt> the signal will be sin(x).
	 *            </p>
	 * 
	 *            <p>
	 *            The signal that would be generated from the <tt>coef</tt>,
	 *            <tt>scale</tt> and <tt>offset</tt> arguments shown above is
	 *            shown below.
	 *            </p>
	 * 
	 *            <p>
	 *            <img src="/misl/misl_tech/signal/util/signal_example.jpg"
	 *            border=0, align=center>
	 *            </p>
	 * 
	 *            <p>
	 *            The loop below is used to generate the points in the graph
	 *            from the range 0...6Pi.
	 *            </p>
	 * 
	 *            <pre>
	 * float range = 3.0f * (2.0f * (float) Math.PI);
	 * 
	 * point p;
	 * float x;
	 * 
	 * do
	 * {
	 * 	p = sig.nextVal();
	 * 	data.addElement(p);
	 * 	x = p.x;
	 * 	System.out.println(&quot; &quot; + p.x + &quot;  &quot; + p.y);
	 * } while (x &lt;= range);
	 * </pre>
	 */
	public signal(float r, float c[], float s[], float o[])
	{
		x = (float) 0.0;
		rate = r;
		if (c == null)
		{
			coef = new float[]
			{ 1.0f };
		} else
		{
			coef = c;
		}

		if (s == null)
		{
			scale = new float[]
			{ 1.0f };
		} else
		{
			scale = s;
		}

		if (o == null)
		{
			offset = new float[]
			{ 0.0f };
		} else
		{
			offset = o;
		}

		int maxLen;
		maxLen = Math.max(coef.length, scale.length);
		maxLen = Math.max(offset.length, maxLen);

		if (coef.length < maxLen)
		{
			float newCoef[] = new float[maxLen];
			int i;
			for (i = 0; i < coef.length; i++)
			{
				newCoef[i] = coef[i];
			}
			for (i = coef.length; i < maxLen; i++)
			{
				newCoef[i] = 1.0f;
			}
			coef = newCoef;
		}

		if (scale.length < maxLen)
		{
			float newScale[] = new float[maxLen];
			int i;
			for (i = 0; i < scale.length; i++)
			{
				newScale[i] = scale[i];
			}
			for (i = scale.length; i < maxLen; i++)
			{
				newScale[i] = 1.0f;
			}
			scale = newScale;
		}

		if (offset.length < maxLen)
		{
			float newOffset[] = new float[maxLen];
			int i;
			for (i = 0; i < offset.length; i++)
			{
				newOffset[i] = offset[i];
			}
			for (i = offset.length; i < maxLen; i++)
			{
				newOffset[i] = 0.0f;
			}
			offset = newOffset;
		}

		//
		// Adjust the sample rate for the scaling of sin(x)
		//
		float maxScale = scale[0];
		for (int i = 1; i < scale.length; i++)
		{
			maxScale = Math.max(maxScale, scale[i]);
		}

		rate = rate * maxScale;

		step = (float) ((2.0 * Math.PI) / rate);
	} // signal

	/**
	 * Get the next signal value
	 * 
	 * <p>
	 * This function returns the next signal value and then increments the
	 * counter by step.
	 * </p>
	 */
	public point nextVal()
	{
		point p = new point(x, 0.0f);

		double scaleX;
		for (int i = 0; i < coef.length; i++)
		{
			scaleX = scale[i] * x;
			p.y = p.y + coef[i] * (float) Math.sin(scaleX + offset[i]);
		}
		x = x + step;
		return p;
	} // next_t

}
