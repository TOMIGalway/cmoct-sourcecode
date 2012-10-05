package com.joey.software.dft.fourier;

import java.util.Vector;

import com.joey.software.dft.util.complex;
import com.joey.software.dft.util.point;


/**
 * <p>
 * Class supporting the discrete Fourier transform
 * </p>
 */
public class dft
{
	private int N; // number of "points" in the DFT

	private Vector data; // Data on which the DFT is performed,

	// set up in constructor

	/**
	 * Discreat Fourier Transform constructor
	 * <p>
	 * The dft constructor is initialized with a <i>Vector</i> of Java point
	 * objects. There are a number of ways to regard the <i>x</i> and <i>y</i>
	 * values in the point object. In the context of signal processing, the
	 * <i>x</i> values would be time, and the <i>y</i> values would be the
	 * magnitude of the signal at that time. If a synthetic function, like
	 * <tt>sin(x)</tt> is sampled, the <i>x</i> component is the argument to the
	 * function and the <i>y</i> component is the result.
	 * </p>
	 */
	public dft(Vector d)
	{
		N = 0;
		data = null;
		if (d != null)
		{
			int len = d.size();
			if (len > 0)
			{
				// check to see if its a Vector of "point"
				point p = (point) d.elementAt(0);
				// the Vector length is > 0 && the type is correct
				N = len;
				data = d;
			}
		}
	} // dft constructor

	/**
	 * Return the dft point at <i>m</i>
	 * 
	 * <p>
	 * The DFT algorith is an N<sup>2</sup> algorithm. For a DFT point at
	 * <i>m</i>, there are O(N) calculations. The basic DFT equation for
	 * calculating the DFT point <i>m</i> is shown below. In this equation our
	 * data values are stored in the array "x". X[n] is one element of this
	 * array. "j" is the sqrt(-1) a.k.a the imaginary number i.
	 * </p>
	 * 
	 * <pre>
	 * N-1
	 *    __
	 *    \
	 *    /   x[n](cos((2Pi*n*m)/N) - j*sin((2Pi*n*m)/N))
	 *    --
	 *    n= 0
	 * </pre>
	 * 
	 * <p>
	 * This calculation returns a complex value, with a real part calculated
	 * from the cosine part of the equation and the imaginary part calculated
	 * from the sine part of the equation.
	 * </p>
	 */
	public complex dftPoint(int m)
	{
		final double twoPi = 2 * Math.PI;
		complex cx = new complex(0.0f, 0.0f);

		if (m >= 0 && m < N)
		{
			double R = 0.0;
			double I = 0.0;

			// At m == 0 the DFT reduces to the sum of the data
			if (m == 0)
			{
				point p;
				for (int n = 0; n < N; n++)
				{
					p = (point) data.elementAt(n);
					R = R + p.y;
				} // for
			} else
			{
				double x;
				double scale;
				point p;

				for (int n = 0; n < N; n++)
				{
					p = (point) data.elementAt(n);
					x = p.y;
					scale = (twoPi * n * m) / N;
					R = R + x * Math.cos(scale);
					I = I - x * Math.sin(scale);
				} // for
			} // else
			cx.real((float) R);
			cx.imag((float) I);
		}
		return cx;
	} // dftPoint

} // class dft
