package com.joey.software.imageToolkit.noise;

/*
 * PerlinSolidNoiseGenerator.java  1.0  98/06/16  Carl Burke
 *
 * Encapsulates Perlin's method for solid noise generation.
 *
 * Copyright (c) 1998 Carl Burke.
 *
 * Adapted from copyrighted source code by Ken Perlin
 * and F. Kenton Musgrave to accompany:
 * Texturing and Modeling: A Procedural Approach
 * Ebert, D., Musgrave, K., Peachey, P., Perlin, K., and Worley, S.
 * AP Professional, September, 1994. ISBN 0-12-228760-6
 * Web site: http://www.cs.umbc.edu/~ebert/book/book.html
 */


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;

public class PerlinSolidNoiseGenerator implements SolidNoiseGenerator
{
	// *** METHODS OF TERRAIN GENERATION CURRENTLY SUPPORTED

	static public final int METHOD_BASIC = 1;

	static public final int METHOD_MULTIFRACTAL = 2;

	static public final int METHOD_HETERO_TERRAIN = 3;

	static public final int METHOD_HYBRID_MULTIFRACTAL = 4;

	static public final int METHOD_RIDGED_MULTIFRACTAL = 5;

	// *** PRIVATE DATA TO DRIVE TERRAIN CALCULATIONS

	private int method;

	private double H;

	private double lacunarity;

	private double octaves;

	private double offset;

	private double gain;

	private double[] point;

	private Random rgen;

	public PerlinSolidNoiseGenerator()
	{
		rgen = new Random();
		init_noise();
		point = new double[3];
		method = METHOD_BASIC;
		H = 0.5;
		lacunarity = 2.0;
		octaves = 7.0;
	}

	public PerlinSolidNoiseGenerator(double hIn, double lacIn, double octIn)
	{
		rgen = new Random();
		init_noise();
		point = new double[3];
		method = METHOD_BASIC;
		H = hIn;
		lacunarity = lacIn;
		octaves = octIn;
	}

	public PerlinSolidNoiseGenerator(int methIn, double hIn, double lacIn, double octIn, double offIn)
	{
		rgen = new Random();
		init_noise();
		point = new double[3];
		switch (methIn)
		{
			case METHOD_MULTIFRACTAL:
				method = METHOD_MULTIFRACTAL;
				H = hIn;
				lacunarity = lacIn;
				octaves = octIn;
				offset = offIn;
				break;
			case METHOD_HETERO_TERRAIN:
				method = METHOD_HETERO_TERRAIN;
				H = hIn;
				lacunarity = lacIn;
				octaves = octIn;
				offset = offIn;
				break;
			case METHOD_HYBRID_MULTIFRACTAL:
				method = METHOD_HYBRID_MULTIFRACTAL;
				H = hIn;
				lacunarity = lacIn;
				octaves = octIn;
				offset = offIn;
				break;
			default: // don't know which method, so do basic
				method = METHOD_BASIC;
				H = hIn;
				lacunarity = lacIn;
				octaves = octIn;
		}
	}

	public PerlinSolidNoiseGenerator(double hIn, double lacIn, double octIn, double offIn, double gainIn)
	{
		rgen = new Random();
		init_noise();
		point = new double[3];
		method = METHOD_RIDGED_MULTIFRACTAL;
		H = hIn;
		lacunarity = lacIn;
		octaves = octIn;
		offset = offIn;
		gain = gainIn;
	}

	// /** RANDOM NUMBER INITIALIZATION AND GENERATION ***

	private double rseed;

	public void setSeed(double s)
	{
		rseed = s;
		rgen.setSeed(Double.doubleToLongBits(rseed));
		init_noise();
	}

	public double getSeed()
	{
		return rseed;
	}

	/***************************************************************************
	 * Methods specific to noise synthetic terrain generation
	 **************************************************************************/
	/***************************************************************************
	 * Supporting/filtering methods
	 **************************************************************************/

	public double bias(double a, double b)
	{
		return Math.pow(a, Math.log(b) / Math.log(0.5));
	}

	public double gain(double a, double b)
	{
		double p = Math.log(1. - b) / Math.log(0.5);

		if (a < .001)
			return 0.;
		else if (a > .999)
			return 1.;
		if (a < 0.5)
			return Math.pow(2 * a, p) / 2;
		else
			return 1. - Math.pow(2 * (1. - a), p) / 2;
	}

	private double vec[];

	public double turbulence(double v[], double freq)
	{
		double t;

		if (vec == null)
			vec = new double[3];
		for (t = 0.; freq >= 1.; freq /= 2)
		{
			vec[0] = freq * v[0];
			vec[1] = freq * v[1];
			vec[2] = freq * v[2];
			t += Math.abs(noise3(vec)) / freq;
		}
		return t;
	}

	/***************************************************************************
	 * Initialization
	 **************************************************************************/

	private void normalize2(double v[]) // v.length == 2
	{
		double s;

		s = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		v[0] = v[0] / s;
		v[1] = v[1] / s;
	}

	private void normalize3(double v[]) // v.length == 3
	{
		double s;

		s = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		v[0] = v[0] / s;
		v[1] = v[1] / s;
		v[2] = v[2] / s;
	}

	private void init_noise()
	{
		int i, j, k;

		p = new int[B + B + 2];
		g3 = new double[B + B + 2][3];
		g2 = new double[B + B + 2][2];
		g1 = new double[B + B + 2];

		for (i = 0; i < B; i++)
		{
			p[i] = i;

			g1[i] = rgen.nextDouble() * 2.0 - 1.0; // -1.0 to 1.0

			for (j = 0; j < 2; j++)
				g2[i][j] = rgen.nextDouble() * 2.0 - 1.0; // -1.0 to 1.0
			normalize2(g2[i]);

			for (j = 0; j < 3; j++)
				g3[i][j] = rgen.nextDouble() * 2.0 - 1.0; // -1.0 to 1.0
			normalize3(g3[i]);
		}

		while ((--i) > 0)
		{
			j = (int) (rgen.nextDouble() * B);
			k = p[i];
			p[i] = p[j];
			p[j] = k;
		}

		for (i = 0; i < B + 2; i++)
		{
			p[B + i] = p[i];
			g1[B + i] = g1[i];
			for (j = 0; j < 2; j++)
				g2[B + i][j] = g2[i][j];
			for (j = 0; j < 3; j++)
				g3[B + i][j] = g3[i][j];
		}
	}

	/***************************************************************************
	 * Noise generation (interpolation) over 1,2, and 3 dimensions
	 **************************************************************************/

	/* noise functions over 1, 2, and 3 dimensions */

	static final int B = 0x100;

	static final int BM = 0xff;

	static final int N = 0x1000;

	static final int NP = 12; /* 2^N */

	static final int NM = 0xfff;

	private int p[];

	private double g3[][];

	private double g2[][];

	private double g1[];

	public double s_curve(double t)
	{
		return t * t * (3. - 2. * t);
	}

	public double lerp(double t, double a, double b)
	{
		return a + t * (b - a);
	}

	/*
	 * #define setup(i,b0,b1,r0,r1)\ t = vec[i] + N;\ b0 = ((int)t) & BM;\ b1 =
	 * (b0+1) & BM;\ r0 = t - (int)t;\ r1 = r0 - 1.;\
	 */

	public double noise1(double arg)
	{
		int bx0, bx1;
		double rx0, rx1, sx, t, u, v;

		/* setup(0, bx0,bx1, rx0,rx1) */
		t = arg + N;
		bx0 = ((int) t) & BM;
		bx1 = (bx0 + 1) & BM;
		rx0 = t - (int) t;
		rx1 = rx0 - 1.;
		/***/

		sx = s_curve(rx0);

		u = rx0 * g1[p[bx0]];
		v = rx1 * g1[p[bx1]];

		return lerp(sx, u, v);
	}

	public double noise2(double vec[]) // vec.length == 2
	{
		int bx0, bx1, by0, by1, b00, b10, b01, b11;
		double rx0, rx1, ry0, ry1, q[], sx, sy, a, b, t, u, v;
		int i, j;

		/* setup(0, bx0,bx1, rx0,rx1) */
		t = vec[0] + N;
		bx0 = ((int) t) & BM;
		bx1 = (bx0 + 1) & BM;
		rx0 = t - (int) t;
		rx1 = rx0 - 1.;
		/***/
		/* setup(1, by0,by1, ry0,ry1) */
		t = vec[1] + N;
		by0 = ((int) t) & BM;
		by1 = (by0 + 1) & BM;
		ry0 = t - (int) t;
		ry1 = ry0 - 1.;
		/***/

		i = p[bx0];
		j = p[bx1];

		b00 = p[i + by0];
		b10 = p[j + by0];
		b01 = p[i + by1];
		b11 = p[j + by1];

		sx = s_curve(rx0);
		sy = s_curve(ry0);

		q = g2[b00];
		u = (rx0 * q[0] + ry0 * q[1]);
		q = g2[b10];
		v = (rx1 * q[0] + ry0 * q[1]);
		a = lerp(sx, u, v);

		q = g2[b01];
		u = (rx0 * q[0] + ry1 * q[1]);
		q = g2[b11];
		v = (rx1 * q[0] + ry1 * q[1]);
		b = lerp(sx, u, v);

		return lerp(sy, a, b);
	}

	public double noise3(double vec[]) // vec.length == 3
	{
		int bx0, bx1, by0, by1, bz0, bz1, b00, b10, b01, b11;
		double rx0, rx1, ry0, ry1, rz0, rz1, q[], sy, sz, a, b, c, d, t, u, v;
		int i, j;

		/* setup(0, bx0,bx1, rx0,rx1) */
		t = vec[0] + N;
		bx0 = ((int) t) & BM;
		bx1 = (bx0 + 1) & BM;
		rx0 = t - (int) t;
		rx1 = rx0 - 1.;
		/***/
		/* setup(1, by0,by1, ry0,ry1) */
		t = vec[1] + N;
		by0 = ((int) t) & BM;
		by1 = (by0 + 1) & BM;
		ry0 = t - (int) t;
		ry1 = ry0 - 1.;
		/***/
		/* setup(2, bz0,bz1, rz0,rz1) */
		t = vec[2] + N;
		bz0 = ((int) t) & BM;
		bz1 = (bz0 + 1) & BM;
		rz0 = t - (int) t;
		rz1 = rz0 - 1.;
		/***/

		i = p[bx0];
		j = p[bx1];

		b00 = p[i + by0];
		b10 = p[j + by0];
		b01 = p[i + by1];
		b11 = p[j + by1];

		t = s_curve(rx0);
		sy = s_curve(ry0);
		sz = s_curve(rz0);

		q = g3[b00 + bz0];
		u = (rx0 * q[0] + ry0 * q[1] + rz0 * q[2]);
		q = g3[b10 + bz0];
		v = (rx1 * q[0] + ry0 * q[1] + rz0 * q[2]);
		a = lerp(t, u, v);

		q = g3[b01 + bz0];
		u = (rx0 * q[0] + ry1 * q[1] + rz0 * q[2]);
		q = g3[b11 + bz0];
		v = (rx1 * q[0] + ry1 * q[1] + rz0 * q[2]);
		b = lerp(t, u, v);

		c = lerp(sy, a, b);

		q = g3[b00 + bz1];
		u = (rx0 * q[0] + ry0 * q[1] + rz1 * q[2]);
		q = g3[b10 + bz1];
		v = (rx1 * q[0] + ry0 * q[1] + rz1 * q[2]);
		a = lerp(t, u, v);

		q = g3[b01 + bz1];
		u = (rx0 * q[0] + ry1 * q[1] + rz1 * q[2]);
		q = g3[b11 + bz1];
		v = (rx1 * q[0] + ry1 * q[1] + rz1 * q[2]);
		b = lerp(t, u, v);

		d = lerp(sy, a, b);

		return lerp(sz, c, d);
	}

	public double noise(double vec[], int len)
	{
		switch (len)
		{
			case 0:
				return 0.;
			case 1:
				return noise1(vec[0]);
			case 2:
				return noise2(vec);
			default:
				return noise3(vec);
		}
	}

	/***************************************************************************
	 * Methods that use the noise functions to generate height fields Adapted
	 * from code written by F. Kenton Musgrave
	 **************************************************************************/
	/*
	 * Procedural fBm evaluated at "point"; returns value stored in "value".
	 * 
	 * Copyright 1994 F. Kenton Musgrave
	 * 
	 * Parameters: ``H'' is the fractal increment parameter ``lacunarity'' is
	 * the gap between successive frequencies ``octaves'' is the number of
	 * frequencies in the fBm
	 * 
	 * 'point' must be a double[3]
	 */
	private boolean first_fBm = true;

	private double exponent_array[];

	public double fBm(double point[], double H, double lacunarity, double octaves)
	{
		double value, frequency, remainder;
		int i;

		/* precompute and store spectral weights */
		if (first_fBm)
		{
			/* seize required memory for exponent_array */
			exponent_array = new double[(int) octaves + 1];
			frequency = 1.0;
			for (i = 0; i <= octaves; i++)
			{
				/* compute weight for each frequency */
				exponent_array[i] = Math.pow(frequency, -H);
				frequency *= lacunarity;
			}
			first_fBm = false;
		}

		value = 0.0; /* initialize vars to proper values */
		frequency = 1.0;

		/* inner loop of spectral construction */
		for (i = 0; i < octaves; i++)
		{
			value += noise3(point) * exponent_array[i];
			point[0] *= lacunarity;
			point[1] *= lacunarity;
			point[2] *= lacunarity;
		}

		remainder = octaves - (int) octaves;
		if (remainder != 0.0)
		{
			/* add in ``octaves'' remainder */
			/* ``i'' and spatial freq. are preset in loop above */
			value += remainder * noise3(point) * exponent_array[i];
		}
		return (value);
	}

	/*
	 * Procedural multifractal evaluated at "point"; returns value stored in
	 * "value".
	 * 
	 * Copyright 1994 F. Kenton Musgrave
	 * 
	 * Parameters: ``H'' determines the highest fractal dimension ``lacunarity''
	 * is gap between successive frequencies ``octaves'' is the number of
	 * frequencies in the fBm ``offset'' is the zero offset, which determines
	 * multifractality
	 * 
	 * Note: this tends to yield very small values, so the results need to be
	 * scaled appropriately.
	 */
	public double multifractal(double point[], double H, double lacunarity, double octaves, double offset)
	{
		double value, frequency, remainder;
		int i;

		/* precompute and store spectral weights */
		if (first_fBm)
		{
			/* seize required memory for exponent_array */
			exponent_array = new double[(int) octaves + 1];
			frequency = 1.0;
			for (i = 0; i <= octaves; i++)
			{
				/* compute weight for each frequency */
				exponent_array[i] = Math.pow(frequency, -H);
				frequency *= lacunarity;
			}
			first_fBm = false;
		}

		value = 1.0; /* initialize vars to proper values */
		frequency = 1.0;

		/* inner loop of multifractal construction */
		for (i = 0; i < octaves; i++)
		{
			value *= offset * frequency * noise3(point);
			point[0] *= lacunarity;
			point[1] *= lacunarity;
			point[2] *= lacunarity;
		}

		remainder = octaves - (int) octaves;
		if (remainder != 0.0)
		{
			/* add in ``octaves'' remainder */
			/* ``i'' and spatial freq. are preset in loop above */
			value += remainder * noise3(point) * exponent_array[i];
		}
		return value;
	}

	/*
	 * Heterogeneous procedural terrain function: stats by altitude method.
	 * Evaluated at "point"; returns value stored in "value".
	 * 
	 * Copyright 1994 F. Kenton Musgrave
	 * 
	 * Parameters: ``H'' determines the fractal dimension of the roughest areas
	 * ``lacunarity'' is the gap between successive frequencies ``octaves'' is
	 * the number of frequencies in the fBm ``offset'' raises the terrain from
	 * `sea level'
	 */
	public double Hetero_Terrain(double point[], double H, double lacunarity, double octaves, double offset)
	{
		double value, increment, frequency, remainder;
		int i;

		/* precompute and store spectral weights */
		if (first_fBm)
		{
			/* seize required memory for exponent_array */
			exponent_array = new double[(int) octaves + 1];
			frequency = 1.0;
			for (i = 0; i <= octaves; i++)
			{
				/* compute weight for each frequency */
				exponent_array[i] = Math.pow(frequency, -H);
				frequency *= lacunarity;
			}
			first_fBm = false;
		}

		/* first unscaled octave of function; later octaves are scaled */
		value = offset + noise3(point);
		point[0] *= lacunarity;
		point[1] *= lacunarity;
		point[2] *= lacunarity;

		/* spectral construction inner loop, where the fractal is built */
		for (i = 1; i < octaves; i++)
		{
			/* obtain displaced noise value */
			increment = noise3(point) + offset;
			/* scale amplitude appropriately for this frequency */
			increment *= exponent_array[i];
			/* scale increment by current `altitude' of function */
			increment *= value;
			/* add increment to ``value'' */
			value += increment;
			/* raise spatial frequency */
			point[0] *= lacunarity;
			point[1] *= lacunarity;
			point[2] *= lacunarity;
		} /* for */

		/* take care of remainder in ``octaves'' */
		remainder = octaves - (int) octaves;
		if (remainder != 0.0)
		{
			/* ``i'' and spatial freq. are preset in loop above */
			/* note that the main loop code is made shorter here */
			/* you may want to that loop more like this */
			increment = (noise3(point) + offset) * exponent_array[i];
			value += remainder * increment * value;
		}

		return (value);
	}

	/*
	 * Hybrid additive/multiplicative multifractal terrain model.
	 * 
	 * Copyright 1994 F. Kenton Musgrave
	 * 
	 * Some good parameter values to start with:
	 * 
	 * H: 0.25 offset: 0.7
	 */
	public double HybridMultifractal(double point[], double H, double lacunarity, double octaves, double offset)
	{
		double frequency, result, signal, weight, remainder;
		int i;

		/* precompute and store spectral weights */
		if (first_fBm)
		{
			/* seize required memory for exponent_array */
			exponent_array = new double[(int) octaves + 1];
			frequency = 1.0;
			for (i = 0; i <= octaves; i++)
			{
				/* compute weight for each frequency */
				exponent_array[i] = Math.pow(frequency, -H);
				frequency *= lacunarity;
			}
			first_fBm = false;
		}

		/* get first octave of function */
		result = (noise3(point) + offset) * exponent_array[0];
		weight = result;
		/* increase frequency */
		point[0] *= lacunarity;
		point[1] *= lacunarity;
		point[2] *= lacunarity;

		/* spectral construction inner loop, where the fractal is built */
		for (i = 1; i < octaves; i++)
		{
			/* prevent divergence */
			if (weight > 1.0)
				weight = 1.0;

			/* get next higher frequency */
			signal = (noise3(point) + offset) * exponent_array[i];
			/* add it in, weighted by previous freq's local value */
			result += weight * signal;
			/* update the (monotonically decreasing) weighting value */
			/* (this is why H must specify a high fractal dimension) */
			weight *= signal;

			/* increase frequency */
			point[0] *= lacunarity;
			point[1] *= lacunarity;
			point[2] *= lacunarity;
		} /* for */

		/* take care of remainder in ``octaves'' */
		remainder = octaves - (int) octaves;
		if (remainder != 0.0)
		{
			/* ``i'' and spatial freq. are preset in loop above */
			result += remainder * noise3(point) * exponent_array[i];
		}

		return (result / 2.0 - 1.0);
	}

	/*
	 * Ridged multifractal terrain model.
	 * 
	 * Copyright 1994 F. Kenton Musgrave
	 * 
	 * Some good parameter values to start with:
	 * 
	 * H: 1.0 offset: 1.0 gain: 2.0
	 */
	public double RidgedMultifractal(double point[], double H, double lacunarity, double octaves, double offset, double gain)
	{
		double result, frequency, signal, weight;
		int i;

		/* precompute and store spectral weights */
		if (first_fBm)
		{
			/* seize required memory for exponent_array */
			exponent_array = new double[(int) octaves + 1];
			frequency = 1.0;
			for (i = 0; i <= octaves; i++)
			{
				/* compute weight for each frequency */
				exponent_array[i] = Math.pow(frequency, -H);
				frequency *= lacunarity;
			}
			first_fBm = false;
		}

		/* get first octave */
		signal = noise3(point);
		/* get absolute value of signal (this creates the ridges) */
		if (signal < 0.0)
			signal = -signal;
		/* invert and translate (note that "offset" should be ~= 1.0) */
		signal = offset - signal;
		/* square the signal, to increase "sharpness" of ridges */
		signal *= signal;
		/* assign initial values */
		result = signal;
		weight = 1.0;

		for (i = 1; i < octaves; i++)
		{
			/* increase the frequency */
			point[0] *= lacunarity;
			point[1] *= lacunarity;
			point[2] *= lacunarity;

			/* weight successive contributions by previous signal */
			weight = signal * gain;
			if (weight > 1.0)
				weight = 1.0;
			if (weight < 0.0)
				weight = 0.0;
			signal = noise3(point);
			if (signal < 0.0)
				signal = -signal;
			signal = offset - signal;
			signal *= signal;
			/* weight the contribution */
			signal *= weight;
			result += signal * exponent_array[i];
		}

		return ((result - 1.0) / 2.0);
	}

	// /** COLOR INDEX CONSTANTS ***

	public static final int BLACK = 0;

	public static final int BLUE0 = 1;

	public static final int BLUE1 = 9;

	public static final int LAND0 = 10;

	public static final int LAND1 = 18;

	public static final int WHITE = 19;

	static int[] rtable =
	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 32, 48, 64, 80, 96, 112, 128, 255 };

	static int[] gtable =
	{ 0, 0, 16, 32, 48, 64, 80, 96, 112, 128, 255, 240, 224, 208, 192, 176,
			160, 144, 128, 255 };

	static int[] btable =
	{ 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 4, 8, 12, 16, 20, 24,
			28, 32, 255 };

	public boolean latic; // flag for latitude based colour

	public double land; // percentage of surface covered by land

	public double water;// percentage of surface covered by water

	// /*** METHODS REQUIRED BY INTERFACE

	/**
	 * Sets internal variables required for a selected magnification, image
	 * width, and image height.
	 */
	@Override
	public void setScaling(double M, double W, double H)
	{
	}

	/**
	 * Calculates an intensity value in [0.0,1.0] at the specified point.
	 */
	@Override
	public double value(double x, double y, double z)
	{
		point[0] = x;
		point[1] = y;
		point[2] = z;
		switch (method)
		{
			case METHOD_BASIC:
				return fBm(point, H, lacunarity, octaves);
			case METHOD_MULTIFRACTAL:
				return multifractal(point, H, lacunarity, octaves, offset);
			case METHOD_HETERO_TERRAIN:
				return Hetero_Terrain(point, H, lacunarity, octaves, offset);
			case METHOD_HYBRID_MULTIFRACTAL:
				return HybridMultifractal(point, H, lacunarity, octaves, offset);
			case METHOD_RIDGED_MULTIFRACTAL:
				return RidgedMultifractal(point, H, lacunarity, octaves, offset, gain);
		}
		return 0.0;
	}

	/**
	 * Returns an (alpha, red, green, blue) color value associated with the
	 * value() at the specified point.
	 */
	@Override
	public int color(double x, double y, double z)
	{
		double alt = value(x, y, z);
		int colour;

		// calculate colour
		if (alt <= 0.) // if below sea level then
		{
			water++;
			if (latic && y * y + alt >= 0.90)
			{ // white if close to poles
				colour = WHITE;
			} else
			{ // blue scale otherwise
				colour = BLUE1 + (int) ((BLUE1 - BLUE0 + 1) * (2 * alt));
				if (colour < BLUE0)
					colour = BLUE0;
			}
		} else
		{
			land++;
			if (latic)
				alt += 0.10204 * y * y; // altitude adjusted with latitude
			if (alt >= 0.5) // arbitrary, but not too bad
			{ // if high then white
				colour = WHITE;
			} else
			{ // else green to brown scale
				colour = LAND0 + (int) ((LAND1 - LAND0 + 1) * (2 * alt));
			}
		}
		if (colour < 0)
			colour = 0;
		if (colour > 19)
			colour = 19;
		return (255 << 24 | rtable[colour] << 16 | gtable[colour] << 8 | btable[colour]);
	}

	/**
	 * Returns an (alpha, red, green, blue) color value associated with the
	 * background value in lieu of valid noise.
	 */
	@Override
	public int background()
	{
		return 0xFF000000;
	}

	public static void test(BufferedImage img)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		int i, j;
		double point[] = new double[3];
		double buffer[][] = new double[width][width];
		double min, max;
		int[][] heightfield = new int[width][width];

		double H = 0;
		double lacunarity = 2;
		double octaves = 9;
		double offset = .1;

		PerlinSolidNoiseGenerator psng = new PerlinSolidNoiseGenerator(H,
				lacunarity, octaves);
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				point[0] = ((double) i) / ((double) width) + 1.0;
				point[1] = ((double) j) / ((double) width) + 1.0;
				buffer[i][j] = psng.value(point[0], point[1], 0.5);
			}
		min = max = buffer[0][0];
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				if (min > buffer[i][j])
					min = buffer[i][j];
				if (max < buffer[i][j])
					max = buffer[i][j];
			}
		for (i = 0; i < width; i++)
			for (j = 0; j < width; j++)
			{
				heightfield[i][j] = (int) (((buffer[i][j] - min) / (max - min)) * 256);
			}
		BuildImage(heightfield, img);
	}

	public static void BuildImage(int[][] heightfield, BufferedImage img)
	{
		/* build the image for display -- greyscale */
		int pixels[];
		int i, j, a, index = 0, min, max;
		// calculate range of values in heightfield
		min = heightfield[0][0];
		max = heightfield[0][0];
		int width = img.getWidth();
		int height = img.getHeight();

		for (i = 0; i < width; i++)
		{
			for (j = 0; j < width; j++)
			{
				if (heightfield[i][j] < min)
					min = heightfield[i][j];
				if (heightfield[i][j] > max)
					max = heightfield[i][j];
			}
		}
		double scalefactor = 255.0 / (max - min);
		for (i = 0; i < width; i++)
		{
			for (j = 0; j < height; j++)
			{
				a = (int) ((heightfield[i][j] - min) * scalefactor);
				if (a < 0)
					a = 0;
				if (a > 255)
					a = 255;
				/* if (a > 255) a=255; */
				Color c = new Color((255 << 24) | (a << 16) | (a << 8) | a);
				img.setRGB(i, j, c.getRGB());
			}
		}
	}

	public static void main(String input[])
	{
		BufferedImage img = ImageOperations.getBi(600);
		test(img);
		JFrame f = FrameFactroy.getFrame(img);
		f.setSize(600, 400);

	}
}
