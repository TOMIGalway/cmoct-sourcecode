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
package com.joey.software.imageAlignment;

public class ImageReg
{

	// Correlation and AOI parameters
	static final double DISTANCE_EFFECT = .05;

	static final double[] VALID_THRESHOLD =
	{ .4, .4, .7, .7, .8, .9 };

	static final int[] AOI_W =
	{ 51, 5, 31, 9, 25, 15 };

	static final int[] AOI_H =
	{ 5, 51, 9, 31, 25, 15 };

	static final double[] AOI_WD =
	{ .8, .3, .8, .4, .6, .7 };

	static final double[] AOI_HD =
	{ .3, .8, .4, .8, .6, .7 };

	// Global data
	int iter; // Iteration count

	int[][] A, B, C; // The images

	boolean resampled; // Image C resampled after
						// last iteration. Yuck -
						// uggly!!

	int w, h; // Image size

	int min, max; // Minimum and maximum values

	int[][] aoi; // Rectangle coordinates of areas
					// of interest

	int[][] center; // Centers of areas of
					// interest

	int[][] offset; // Where in B the the A's
					// centers of AOI correlation
					// wants

	int[][] lsf; // Where least squares fit allows
					// the above to go

	double[][][] corrData; // The actual
							// correlation data

	double[] coeff1, coeff2; // Linear transform
								// coefficients

	// Constructor
	public ImageReg(int[][] A, int[][] B)
	{
		this.A = A;
		this.B = B;
		h = A.length;
		w = A[0].length;

		// Find min and max values in the original
		// image
		min = max = A[0][0];
		for (int i = 0; i < h; ++i)
			for (int j = 0; j < w; ++j)
			{
				if (min > A[i][j])
					min = A[i][j];
				if (min > B[i][j])
					min = B[i][j];
				if (max < A[i][j])
					max = A[i][j];
				if (max < B[i][j])
					max = B[i][j];
			}

		// Start with identity transform
		coeff2 = coeff1 = new double[6];
		coeff1[0] = coeff1[4] = 1;
		resample(coeff1);
		resampled = true;
	}

	// This constructs an object suitable only for
	// image display when the
	// transform is already known
	public ImageReg(int[][] A, int[][] B, double[] coeff, int iteration)
	{
		this(A, B);
		iter = iteration;
		coeff1 = coeff2 = coeff;
		resample(coeff1);
		resampled = true;
	}

	// Get iteration count
	public int getIteration()
	{
		return iter;
	}

	public int[][] getResult()
	{
		return C;
	}
	// Get image transform
	public double[] getTransform()
	{
		return coeff2;
	}

	// Linear transform
	static int getX(double[] coeff, int X, int Y)
	{
		return (int) Math.round(coeff[0] * X + coeff[1] * Y + coeff[2]);
	}

	static int getY(double[] coeff, int X, int Y)
	{
		return (int) Math.round(coeff[3] * X + coeff[4] * Y + coeff[5]);
	}

	int getPixel(double coeff[], int[][] B, int x, int y)
	{
		final int xx = getX(coeff, x, y), yy = getY(coeff, x, y);
		if (xx < 0 || xx >= B[0].length || yy < 0 || yy >= B.length)
			return min;
		else
			return B[yy][xx];
	}

	void resample(double coeff[])
	{
		int w = B[0].length, h = B.length;
		C = new int[h][w];
		for (int i = 0; i < h; ++i)
			for (int j = 0; j < w; ++j)
				C[i][j] = getPixel(coeff, B, j, i);
	}

	// Select areas of interest. Highly heuristic
	void AOI()
	{
		final int h = A.length; // Image height
		final int w = A[0].length; // Image width

		// The algorithm this implements is simply
		// to space AOI's evenly
		// across the image, without respect to
		// image data. Just make sure
		// there is enough space around the edges
		// to fill the ends for
		// correlation
		final int xmin = AOI_W[iter] / 2;
		final int xmax = w - AOI_W[iter] - AOI_W[iter] / 2;
		final int ymin = AOI_H[iter] / 2;
		final int ymax = h - AOI_H[iter] - AOI_H[iter] / 2;
		final int nx = (int) ((xmax - xmin) / (AOI_W[iter] / AOI_WD[iter])) + 1;
		final int ny = (int) ((ymax - ymin) / (AOI_H[iter] / AOI_HD[iter])) + 1;

		aoi = new int[nx * ny][4];
		center = new int[nx * ny][2];
		int k = 0;
		for (int i = 0; i < ny; ++i)
			for (int j = 0; j < nx; ++j)
			{
				aoi[k][0] = xmin + (xmax - xmin) * j / (nx - 1);
				aoi[k][1] = ymin + (ymax - ymin) * i / (ny - 1);
				aoi[k][2] = aoi[k][0] + AOI_W[iter];
				aoi[k][3] = aoi[k][1] + AOI_H[iter];
				center[k][0] = (aoi[k][0] + aoi[k][2]) / 2;
				center[k][1] = (aoi[k][1] + aoi[k][3]) / 2;
				++k;
			}
	}

	// Correlation. This is awkwardly slow. It
	// doesn't use FFT
	void correlate()
	{
		int I, J, i, j, k, imax, jmax;
		double SA, SB, SAB, SA2, SB2, max;

		// Correlate data
		offset = new int[aoi.length][2];
		corrData = new double[aoi.length][][];
		for (k = 0; k < aoi.length; ++k)
		{
			final int aoi_w = aoi[k][2] - aoi[k][0];
			final int aoi_h = aoi[k][3] - aoi[k][1];

			corrData[k] = new double[aoi_h][aoi_w];
			max = -1;
			jmax = imax = 0;
			for (I = 0; I < aoi_h; ++I)
				for (J = 0; J < aoi_w; ++J)
				{

					// Compute moments
					SA = SB = SAB = SA2 = SB2 = 0;
					for (i = aoi[k][1]; i < aoi[k][3]; ++i)
						for (j = aoi[k][0]; j < aoi[k][2]; ++j)
						{
							final double a = A[i][j];
							final double b = C[i + I - aoi_h / 2][j + J - aoi_w
									/ 2];
							SA += a;
							SB += b;
							SAB += a * b;
							SA2 += a * a;
							SB2 += b * b;
						}

					// Compute correlation
					final int N = aoi_w * aoi_h;
					final double num = SAB - SA * SB / N;
					final double den = Math.sqrt((SA2 - SA * SA / N)
							* (SB2 - SB * SB / N));
					final double corr = (den == 0 ? 0 : num / den)
							* (1 - DISTANCE_EFFECT
									* (Math.pow(I - aoi_h / 2, 2) + Math.pow(J
											- aoi_w / 2, 2))
									/ (Math.pow(aoi_h / 2, 2) + Math
											.pow(aoi_w / 2, 2)));
					if (max < corr)
					{
						max = corr;
						imax = I;
						jmax = J;
					}
					corrData[k][I][J] = corr;
				}
			offset[k][0] = aoi[k][0] + jmax;
			offset[k][1] = aoi[k][1] + imax;
		}
	}

	// Repack correlation data
	void repack()
	{
		int i, j;

		// Figure out number of valid correlation
		// squares
		for (i = j = 0; i < aoi.length; ++i)
			if (corrData[i][offset[i][1] - aoi[i][1]][offset[i][0] - aoi[i][0]] > VALID_THRESHOLD[iter])
				++j;

		// Allocate memory and copy data
		int[][] aoi1 = new int[j][];
		int[][] center1 = new int[j][];
		int[][] offset1 = new int[j][];
		double[][][] corrData1 = new double[j][][];
		for (i = j = 0; i < aoi.length; ++i)
			if (corrData[i][offset[i][1] - aoi[i][1]][offset[i][0] - aoi[i][0]] > VALID_THRESHOLD[iter])
			{
				aoi1[j] = aoi[i];
				center1[j] = center[i];
				offset1[j] = offset[i];
				corrData1[j] = corrData[i];
				++j;
			}
		aoi = aoi1;
		center = center1;
		offset = offset1;
		corrData = corrData1;
	}

	// Least squares fit
	boolean leastSquaresFit()
	{
		double A[][] = new double[6][6];
		double B[] = new double[6];

		// Compute coefficients for the linear
		// equations
		for (int i = 0; i < aoi.length; ++i)
		{
			final double x0 = center[i][0];
			final double y0 = center[i][1];
			final double x1 = getX(coeff1, offset[i][0], offset[i][1]);
			final double y1 = getY(coeff1, offset[i][0], offset[i][1]);

			A[0][0] += x0 * x0;
			A[0][1] += x0 * y0;
			A[0][2] += x0;
			A[1][0] += x0 * y0;
			A[1][1] += y0 * y0;
			A[1][2] += y0;
			A[2][0] += x0;
			A[2][1] += y0;
			A[2][2] += 1;
			A[3][3] += x0 * x0;
			A[3][4] += x0 * y0;
			A[3][5] += x0;
			A[4][3] += x0 * y0;
			A[4][4] += y0 * y0;
			A[4][5] += y0;
			A[5][3] += x0;
			A[5][4] += y0;
			A[5][5] += 1;

			B[0] += x1 * x0;
			B[1] += x1 * y0;
			B[2] += x1;
			B[3] += y1 * x0;
			B[4] += y1 * y0;
			B[5] += y1;
		}

		// Now solve the system of equations
		coeff2 = LinAlg.solve(A, B);

		if(coeff2 == null)
		{
			return false;
		}
		// To display markers, we need difference
		// transform
		double[][] D =
		{
		{ coeff1[0], coeff1[1], coeff1[2] },
		{ coeff1[3], coeff1[4], coeff1[5] },
		{ 0, 0, 1 } };
		double[][] E =
		{
		{ coeff2[0], coeff2[1], coeff2[2] },
		{ coeff2[3], coeff2[4], coeff2[5] },
		{ 0, 0, 1 } };
		double[][] F = LinAlg.mul(LinAlg.invert(D), E);
		double coeff3[] =
		{ F[0][0], F[0][1], F[0][2], F[1][0], F[1][1], F[1][2] };

		// Mark grid points
		lsf = new int[aoi.length][2];
		for (int i = 0; i < lsf.length; ++i)
		{
			lsf[i][0] = getX(coeff3, center[i][0], center[i][1]);
			lsf[i][1] = getY(coeff3, center[i][0], center[i][1]);
		}
		return true;
	}

	// Do one iteration's worth of processing
	public boolean iterate()
	{
		if (!resampled)
			resample(coeff2);
		coeff1 = coeff2;
		AOI();
		correlate();
		repack();
		leastSquaresFit();
		if (iter < AOI_W.length - 1)
			++iter;
		resampled = false;
		return true;
	}

	// Get graphical image for pretty display.
	// Just give it a graphics
	// buffer. Set corr to get picture with *old*
	// version of the second
	// image, and overlayed correlation results,
	// and grid points marked
	// up; unset corr to get picture with new,
	// resampled, version of B,
	// with only the two images, and no extraneous
	// marks. The first
	// version shows off the internal workings of
	// the algorithm, while
	// the second one allows to see easier how
	// good registration is. Don't ca
	public void getPicture(int[] pic, boolean corr)
	{

		// Resample the second image according to
		// the requested operation
		boolean gridMarks = corr && aoi != null;
		if (gridMarks == resampled)
			resample(gridMarks ? coeff1 : coeff2);
		resampled = !gridMarks;

		// Fill stupid alpha value
		for (int i = 0; i < h; ++i)
			for (int j = 0; j < w; ++j)
				pic[i * w + j] = 0xFF000000;

		// Insert original images
		for (int i = 0; i < h; ++i)
			for (int j = 0; j < w; ++j)
			{
				pic[i * w + j] += (A[i][j] - min) * 192 / (max - min) * 0x1
						+ (C[i][j] - min) * 192 / (max - min) * 0x10000;
			}

		if (gridMarks)
		{
			// Insert correlation data
			for (int k = 0; k < aoi.length; ++k)
				for (int i = aoi[k][1]; i < aoi[k][3]; ++i)
					for (int j = aoi[k][0]; j < aoi[k][2]; ++j)
						pic[i * w + j] += (int) ((corrData[k][i - aoi[k][1]][j
								- aoi[k][0]] + 1) / 2 * 192) * 0x100;

			// Insert maximum markers
			for (int k = 0; k < aoi.length; ++k)
			{
				try
				{
					pic[offset[k][1] * w + offset[k][0]] = 0xFFFFFF00;
					pic[offset[k][1] * w + offset[k][0] + 1] = 0xFFFFFF00;
					pic[offset[k][1] * w + offset[k][0] - 1] = 0xFFFFFF00;
					pic[offset[k][1] * w + offset[k][0] + w] = 0xFFFFFF00;
					pic[offset[k][1] * w + offset[k][0] - w] = 0xFFFFFF00;

					pic[lsf[k][1] * w + lsf[k][0]] = 0xFFFFFFFF;
					pic[lsf[k][1] * w + lsf[k][0] + 1] = 0xFFFFFFFF;
					pic[lsf[k][1] * w + lsf[k][0] - 1] = 0xFFFFFFFF;
					pic[lsf[k][1] * w + lsf[k][0] + w] = 0xFFFFFFFF;
					pic[lsf[k][1] * w + lsf[k][0] - w] = 0xFFFFFFFF;
				} catch (ArrayIndexOutOfBoundsException ex)
				{
				}
			}
		}
	}
}
