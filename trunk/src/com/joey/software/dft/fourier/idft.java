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
package com.joey.software.dft.fourier;

import java.util.Vector;

import com.joey.software.dft.util.complex;


/**
 * Inverse discrete Fourier transform
 */
public class idft
{

	private int N;

	private Vector data;

	/**
	 * <p>
	 * Inverse DFT constructor
	 * </p>
	 * 
	 * <p>
	 * The inverse DFT constructor is passed a vector of complex objects (note
	 * that this differs from the <tt>dft</tt> constructor which is passed a
	 * vector of points).
	 * </p>
	 */
	public idft(Vector d)
	{
		N = 0;
		data = null;
		if (d != null)
		{
			int len = d.size();
			if (len > 0)
			{
				// check to see if its a Vector of "complex"
				complex cx = (complex) d.elementAt(0);
				// the Vector length is > 0 && the type is correct
				N = len;
				data = d;
			}
		}
	} // idft constructor

	/**
	 * Return the inverse DFT point at <i>m</i>
	 * 
	 * <p>
	 * The inverse DFT algorith is an N<sup>2</sup> algorithm. For a DFT point
	 * at <i>m</i>, there are O(N) calculations. The basic inverse DFT equation
	 * for calculating the inverse DFT point at <i>m</i> is shown below. In this
	 * equation the complex data values that resulted from a DFT transform are
	 * stored in the array "X". X[n] is one element of this array. "j" is the
	 * sqrt(-1) a.k.a the imaginary number i.
	 * </p>
	 * 
	 * <pre>
	 * N-1
	 *    __
	 *  1 \
	 * ---/   X[n](cos((2Pi*n*m)/N) + j*sin((2Pi*n*m)/N))
	 *  N --
	 *    n = 0
	 * </pre>
	 * 
	 * <p>
	 * At each step in the summation above the complex number X[n] is multiplied
	 * by the complex number
	 * </p>
	 * 
	 * <pre>
	 * cxDft = complex( cos((2Pi*n*m)/N), sin((2Pi*n*m)/N) )
	 * </pre>
	 */
	public complex iDftPoint(int m)
	{
		final double twoPi = 2 * Math.PI;
		complex cxRslt = new complex(0.0f, 0.0f);

		if (m >= 0 && m < N)
		{
			// At m == 0 the IDFT reduces to the sum of the data
			if (m == 0)
			{
				complex cx;
				for (int n = 0; n < N; n++)
				{
					cx = (complex) data.elementAt(n);
					cxRslt = cxRslt.add(cx);
				} // for
			} else
			{
				for (int n = 0; n < N; n++)
				{
					complex cx = (complex) data.elementAt(n);
					double scale = (twoPi * n * m) / N;
					float R = (float) Math.cos(scale);
					float I = (float) Math.sin(scale);
					complex cxDft = new complex(R, I);

					// cxRslt = cxRslt + cx * cxDft
					cxRslt = cxRslt.add(cx.mult(cxDft));
				} // for
				// cxRslt = cxRslt / N
				cxRslt = cxRslt.div(N);
			} // else
		}

		return cxRslt;
	} // dftPoint

} // class idft
