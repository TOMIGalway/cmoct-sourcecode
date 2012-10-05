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
 * Container for complex numbers
 */
public class complex
{
	private float R; // real part

	private float I; // imaginary part

	/** Argumentless constructor */
	public complex()
	{
		R = 0.0f;
		I = 0.0f;
	}

	/** constructor with real and imaginary parts */
	public complex(float r, float i)
	{
		R = r;
		I = i;
	}

	/** constructor with a phasor argument */
	public complex(phasor ph)
	{
		R = ph.cx().R;
		I = ph.cx().I;
	}

	/** set the real part */
	public void real(float r)
	{
		R = r;
	}

	/** return the real part */
	public float real()
	{
		return R;
	}

	/** set the imaginary part */
	public void imag(float i)
	{
		I = i;
	}

	/** return the imaginary part */
	public float imag()
	{
		return I;
	}

	/**
	 * Return the magnitude of the complex number
	 * <p>
	 * The real and imaginary parts of a complex number are two vectors at right
	 * angles to each other. The magnitude of the complex number is the
	 * hypotenuse formed by these right angles. So the magnitude is the distance
	 * formula:
	 * </p>
	 * 
	 * <pre>
	 * M = sqrt( R<sup>2</sup> + I<sup>2</sup> )
	 * </pre>
	 */
	public float mag()
	{
		float Mag = (float) Math.sqrt((R * R) + (I * I));
		return Mag;
	}

	/**
	 * Return the angle of the "phasor", in degrees.
	 * 
	 * <p>
	 * The "phasor" is the vector formed from the real and the imaginary parts
	 * of the complex number. This vector has a magnitude (returned by mag()
	 * above) and an angle returned by this function.
	 * </p>
	 */
	public float angle()
	{
		// This result will be in radians
		float Angle = (float) Math.atan(I / R);
		// convert radians to degrees
		Angle = (Angle * 180.0f) / (float) Math.PI;

		return Angle;
	} // angle

	/** return the "phasor" equivalent to the complex number */
	public phasor phase()
	{
		phasor ph = new phasor(mag(), angle());
		return ph;
	}

	/**
	 * Add the rhs complex number to "this" complex number.
	 */
	public complex add(complex rhs)
	{
		float newR, newI;

		newR = R + rhs.R;
		newI = I + rhs.I;

		complex C = new complex(newR, newI);
		return C;
	}

	/**
	 * Subtract the rhs complex number from "this" complex number.
	 */
	public complex sub(complex rhs)
	{
		float newR, newI;

		newR = R - rhs.R;
		newI = I - rhs.I;

		complex C = new complex(newR, newI);
		return C;
	}

	public complex mult(complex rhs)
	{
		float newR, newI;

		newR = (R * rhs.R) - (I * rhs.I);
		newI = (R * rhs.I) + (rhs.R * I);

		complex C = new complex(newR, newI);
		return C;
	}

	public complex mult(float rhs)
	{
		float newR = R * rhs;
		float newI = rhs * I;

		complex C = new complex(newR, newI);
		return C;
	}

	/** Divide "this" by the divisor argument */
	public complex div(complex divisor)
	{
		float R2 = divisor.R;
		float I2 = divisor.I;
		float newR, newI;
		float div;

		div = (R2 * R2) + (I2 * I2);

		newR = ((R * R2) + (I * I2)) / div;
		newI = ((R2 * I) - (R * I2)) / div;

		complex C = new complex(newR, newI);
		return C;
	}

	public complex div(float divisor)
	{
		float R2 = divisor;
		float I2 = 0;
		float newR, newI;
		float div;

		div = R2 * R2;

		newR = (R * R2) / div;
		newI = (R2 * I) / div;

		complex C = new complex(newR, newI);
		return C;
	}

	/** compare rhs to "this". If rhs and this are equal, return true */
	public boolean eq(complex rhs)
	{
		return (R == rhs.R && I == rhs.I);
	}

}
