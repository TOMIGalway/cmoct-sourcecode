package com.joey.software.imageToolkit.noise;

/*
 * SolidNoiseGenerator.java  1.0  98/06/03  Carl Burke
 *
 * Simple interface to a solid noise generator.  Includes an interface
 * to a routine which interprets the noise value at a point as a color.
 *
 * Copyright (c) 1998 Carl Burke.
 */

public interface SolidNoiseGenerator
{
	/**
	 * Sets internal variables required for a selected magnification, image
	 * width, and image height.
	 */
	public void setScaling(double M, double W, double H);

	/**
	 * Calculates an intensity value in [0.0,1.0] at the specified point.
	 */
	public double value(double x, double y, double z);

	/**
	 * Returns an (alpha, red, green, blue) color value associated with the
	 * value() at the specified point.
	 */
	public int color(double x, double y, double z);

	/**
	 * Returns an (alpha, red, green, blue) color value associated with the
	 * background value in lieu of valid noise.
	 */
	public int background();
}
