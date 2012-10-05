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
package com.joey.software.OMAGTesting;
//package OMAGTesting;
//
///*File ImgMod31.java.java
// Copyright 2005, R.G.Baldwin
// The purpose of this program is to exercise and
// test the 2D Fourier Transform methods and the
// axis shifting method provided by the class named
// ImgMod30.
// The main method in this class reads a command-
// line parameter and uses it to select a specific
// case involving a particular kind of input data
// in the space domain.  The program then performs
// a 2D Fourier transform on that data followed by
// an inverse 2D Fourier transform.
// There are 14 cases built into the program with
// case numbers ranging from 0 to 13 inclusive.
// Each of the cases is designed such that the
// results should be known in advance by a person
// familiar with 2D Fourier analysis and the wave-
// number domain.  The cases are also designed to
// illustrate the impact of various space-domain
// characteristics on the wave-number spectrum.
// This information will be useful later when
// analyzing the results of performing 2D
// transforms on photographic images and other
// images as well.
// Each time the program is run, it produces a stack
// of six output images in the upper left corner of
// the screen.  The type of each image is listed
// below.  This list is in top-to-bottom order.  To
// view the images further down in the stack, you
// must  physically move those on top to get them
// out of the way.
// The top-to-bottom order of the output images is
// as follows:
// 1. Space-domain output of inverse Fourier
// transform.  Compare with original input in 6
// below.
// 2. Amplitude spectrum in wave-number domain with
// shifted origin.  Compare with 5 below.
// 3. Imaginary wave-number spectrum with shifted
// origin.
// 4. Real wave-number spectrum with shifted
// origin.
// 5. Amplitude spectrum in wave-number domain
// without shifted origin.  Compare with 2 above.
// 6. Space-domain input data.  Compare with 1
// above.
// In addition, the program produces some numeric
// output on the command-line screen that may be
// useful in confirming the validity of the inverse
// transform.  The following is an example:
// height = 41
// width = 41
// height = 41
// width = 41
// 2.0 1.9999999999999916
// 0.5000000000000002 0.49999999999999845
// 0.49999999999999956 0.4999999999999923
// 1.7071067811865475 1.7071067811865526
// 0.2071067811865478 0.20710678118654233
// 0.20710678118654713 0.20710678118655435
// 1.0 1.0000000000000064
// -0.4999999999999997 -0.49999999999999484
// -0.5000000000000003 -0.4999999999999965
// The first two lines above indicate the size of
// the spatial surface for the forward transform.
// The second two lines indicate the size of the
// wave-number surface for the inverse transform.
// The remaining nine lines indicate something
// about the quality of the inverse transform in
// terms of its ability to replicate the original
// spatial surface.  These lines also indicate
// something about the correctness or lack thereof
// of the overall scaling from original input to
// final output.  Each line contains a pair of
// values.  The first value is from the original
// spatial surface.  The second value is from the
// spatial surface produced by performing an inverse
// transform on the wave-number spectrum.  The two
// values in each pair of values should match.  If
// they match, this indicates the probability of a
// valid result.  Note however that this is
// a very small sampling of the values that make
// up the original and replicated spatial data and
// problems could arise in areas that are not
// included in this small sample.  The match is very
// good in the example shown above.  This example
// is from Case #12.
// Usage: java ImgMod31 CaseNumber DisplayType
// CaseNumber from 0 to 13 inclusive.
// If a case number is not provided, Case #2 will be
// run by default.  If a display type is not
// provided, display type 1 will be used by default.
// A description of each case is provided by the
// comments in this program.
// See ImgMod29 for a definition of DisplayType,
// which can have a value of 0, 1, or 2.
// You can terminate the program by clicking on the
// close button on any of the display frames
// produced by the program.
// Tested using J2SE 5.0 and WinXP
// ************************************************/
//import static java.lang.Math.*;
//
//class ImgMod31
//{
//
//	public static void main(String[] args)
//	{
//		// Get input parameters to select the case to
//		// be run and the displayType. See ImgMod29
//		// for a description of displayType. Use
//		// default case and displayType if the user
//		// fails to provide that information.
//		// If the user provides a non-numeric input
//		// parameter, an exception will be thrown.
//		int switchCase = 2;// default
//		int displayType = 1;// default
//		if (args.length == 1)
//		{
//			switchCase = Integer.parseInt(args[0]);
//		} else if (args.length == 2)
//		{
//			switchCase = Integer.parseInt(args[0]);
//			displayType = Integer.parseInt(args[1]);
//		} else
//		{
//			System.out.println("Usage: java ImgMod31 "
//					+ "CaseNumber DisplayType");
//			System.out.println("CaseNumber from 0 to 13 inclusive.");
//			System.out.println("DisplayType from 0 to 2 inclusive.");
//			System.out.println("Running case " + switchCase + " by default.");
//			System.out.println("Running DisplayType " + displayType
//					+ " by default.");
//		}// end else
//
//		// Create the array of test data.
//		int rows = 41;
//		int cols = 41;
//		// Get a test surface in the space domain.
//		double[][] spatialData = getSpatialData(switchCase, rows, cols);
//
//		// Display the spatial data. Don't display
//		// the axes.
//	//	new ImgMod29(spatialData, 3, false, displayType);
//		// Perform the forward transform from the
//		// space domain into the wave-number domain.
//		// First prepare some array objects to
//		// store the results.
//		double[][] realSpect = // Real part
//		new double[rows][cols];
//		double[][] imagSpect = // Imaginary part
//		new double[rows][cols];
//		double[][] amplitudeSpect = // Amplitude
//		new double[rows][cols];
//		// Now perform the transform
//		ImgMod30.xform2D(spatialData, realSpect, imagSpect, amplitudeSpect);
//
//		// Display the raw amplitude spectrum without
//		// shifting the origin first. Display the
//		// axes.
//		new ImgMod29(amplitudeSpect, 3, true, displayType);
//		// At this point, the wave-number spectrum is
//		// not in a format that is good for viewing.
//		// In particular, the origin is at the upper
//		// left corner. The horizontal Nyquist
//		// folding wave-number is near the
//		// horizontal center of the plot. The
//		// vertical Nyquist folding wave number is
//		// near the vertical center of the plot. It
//		// is much easier for most people to
//		// understand what is going on when the
//		// wave-number origin is shifted to the
//		// center of the plot with the Nyquist
//		// folding wave numbers at the edges of the
//		// plot. The method named shiftOrigin can be
//		// used to rearrange the data and to shift
//		// the orgin in that manner.
//		// Shift the origin and display the real part
//		// of the spectrum, the imaginary part of the
//		// spectrum, and the amplitude of the
//		// spectrum. Display the axes in all three
//		// cases.
//		double[][] shiftedRealSpect = ImgMod30.shiftOrigin(realSpect);
//		new ImgMod29(shiftedRealSpect, 3, true, displayType);
//
//		double[][] shiftedImagSpect = ImgMod30.shiftOrigin(imagSpect);
//		new ImgMod29(shiftedImagSpect, 3, true, displayType);
//
//		double[][] shiftedAmplitudeSpect = ImgMod30.shiftOrigin(amplitudeSpect);
//		new ImgMod29(shiftedAmplitudeSpect, 3, true, displayType);
//
//		// Now test the inverse transform by
//		// performing an inverse transform on the
//		// real and imaginary parts produced earlier
//		// by the forward transform.
//		// Begin by preparing an array object to store
//		// the results.
//		double[][] recoveredSpatialData = new double[rows][cols];
//		// Now perform the inverse transform.
//		ImgMod30.inverseXform2D(realSpect, imagSpect, recoveredSpatialData);
//
//		// Display the output from the inverse
//		// transform. It should compare favorably
//		// with the original spatial surface.
//		new ImgMod29(recoveredSpatialData, 3, false, displayType);
//
//		// Use the following code to confirm correct
//		// scaling. If the scaling is correct, the
//		// two values in each pair of values should
//		// match. Note that this is a very small
//		// subset of the total set of values that
//		// make up the original and recovered
//		// spatial data.
//		for (int row = 0; row < 3; row++)
//		{
//			for (int col = 0; col < 3; col++)
//			{
//				System.out.println(spatialData[row][col] + " "
//						+ recoveredSpatialData[row][col] + " ");
//			}// col
//		}// row
//	}// end main
//
//	// ===========================================//
//
//	// This method constructs and returns a 3D
//	// surface in a 2D array of type double
//	// according to the identification of a
//	// specific case received as an input
//	// parameter. There are 14 possible cases. A
//	// description of each case is provided in the
//	// comments. The other two input parameters
//	// specify the size of the surface in units of
//	// rows and columns.
//	private static double[][] getSpatialData(int switchCase, int rows, int cols)
//	{
//
//		// Create an array to hold the data. All
//		// elements are initialized to a value of
//		// zero.
//		double[][] spatialData = new double[rows][cols];
//
//		// Use a switch statement to select and
//		// create a specified case.
//		switch (switchCase)
//		{
//			case 0:
//				// This case places a single non-zero
//				// point at the origin in the space
//				// domain. The origin is at the upper
//				// left corner. In signal processing
//				// terminology, this point can be viewed
//				// as an impulse in space. This produces
//				// a flat spectrum in wave-number space.
//				spatialData[0][0] = 1;
//				break;
//
//			case 1:
//				// This case places a single non-zero
//				// point near but not at the origin in
//				// space. This produces a flat spectrum
//				// in wave-number space as in case 0.
//				// However, the real and imaginary parts
//				// of the transform are different from
//				// case 0 and the result is subject to
//				// arithmetic accuracy issues. The
//				// plotted flat spectrum doesn't look
//				// very good because the color switches
//				// back and forth between three values
//				// that are very close to together. This
//				// is the result of the display program
//				// normalizing the surface values based
//				// on the maximum and minimum values,
//				// which in this case are very close
//				// together.
//				spatialData[2][2] = 1;
//				break;
//
//			case 2:
//				// This case places a box on the diagonal
//				// near the origin. This produces a
//				// sin(x)/x shape to the spectrum with
//				// its peak at the origin in wave-number
//				// space.
//				spatialData[3][3] = 1;
//				spatialData[3][4] = 1;
//				spatialData[3][5] = 1;
//				spatialData[4][3] = 1;
//				spatialData[4][4] = 1;
//				spatialData[4][5] = 1;
//				spatialData[5][3] = 1;
//				spatialData[5][4] = 1;
//				spatialData[5][5] = 1;
//				break;
//
//			case 3:
//				// This case places a box at the top near
//				// the origin. This produces the same
//				// amplitude spectrum as case 2. However,
//				// the real and imaginary parts, (or the
//				// phase) is different from case 2 due to
//				// the difference in location of the box
//				// relative to the origin in space.
//				spatialData[0][3] = 1;
//				spatialData[0][4] = 1;
//				spatialData[0][5] = 1;
//				spatialData[1][3] = 1;
//				spatialData[1][4] = 1;
//				spatialData[1][5] = 1;
//				spatialData[2][3] = 1;
//				spatialData[2][4] = 1;
//				spatialData[2][5] = 1;
//				break;
//
//			case 4:
//				// This case draws a short line along the
//				// diagonal from upper left to lower
//				// right. This results in a spectrum with
//				// a sin(x)/x shape along that axis and a
//				// constant along the axis that is
//				// perpendicular to that axis
//				spatialData[0][0] = 1;
//				spatialData[1][1] = 1;
//				spatialData[2][2] = 1;
//				spatialData[3][3] = 1;
//				spatialData[4][4] = 1;
//				spatialData[5][5] = 1;
//				spatialData[6][6] = 1;
//				spatialData[7][7] = 1;
//				break;
//
//			case 5:
//				// This case draws a short line
//				// perpendicular to the diagonal from
//				// upper left to lower right. The
//				// spectral result is shifted 90 degrees
//				// relative to that shown for case 4
//				// where the line was along the diagonal.
//				// In addition, the line is shorter
//				// resulting in wider lobes in the
//				// spectrum.
//				spatialData[0][3] = 1;
//				spatialData[1][2] = 1;
//				spatialData[2][1] = 1;
//				spatialData[3][0] = 1;
//				break;
//
//			case 6:
//				// This case draws horizontal lines,
//				// vertical lines, and lines on both
//				// diagonals. The weights of the
//				// individual points is such that the
//				// average of all the weights is 0.
//				// The weight at the point where the
//				// lines intersect is also 0. This
//				// produces a spectrum that is
//				// symmetrical across the axes at 0,
//				// 45, and 90 degrees. The value of
//				// the spectrum at the origin is zero
//				// with major peaks at the folding
//				// wave-numbers on the 45-degree axes.
//				// In addition, there are minor peaks
//				// at various other points as well.
//				spatialData[0][0] = -1;
//				spatialData[1][1] = 1;
//				spatialData[2][2] = -1;
//				spatialData[3][3] = 0;
//				spatialData[4][4] = -1;
//				spatialData[5][5] = 1;
//				spatialData[6][6] = -1;
//
//				spatialData[6][0] = -1;
//				spatialData[5][1] = 1;
//				spatialData[4][2] = -1;
//				spatialData[3][3] = 0;
//				spatialData[2][4] = -1;
//				spatialData[1][5] = 1;
//				spatialData[0][6] = -1;
//
//				spatialData[3][0] = 1;
//				spatialData[3][1] = -1;
//				spatialData[3][2] = 1;
//				spatialData[3][3] = 0;
//				spatialData[3][4] = 1;
//				spatialData[3][5] = -1;
//				spatialData[3][6] = 1;
//
//				spatialData[0][3] = 1;
//				spatialData[1][3] = -1;
//				spatialData[2][3] = 1;
//				spatialData[3][3] = 0;
//				spatialData[4][3] = 1;
//				spatialData[5][3] = -1;
//				spatialData[6][3] = 1;
//				break;
//
//			case 7:
//				// This case draws a zero-frequency
//				// sinusoid (DC) on the surface with an
//				// infinite number of samples per cycle.
//				// This causes a single peak to appear in
//				// the spectrum at the wave-number
//				// origin. This origin is the upper left
//				// corner for the raw spectrum, and is
//				// at the center cross hairs after the
//				// origin has been shifted to the
//				// center for better viewing.
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = 1.0;
//					}// end inner loop
//				}// end outer loop
//				break;
//
//			case 8:
//				// This case draws a sinusoidal surface
//				// along the horizontal axis with one
//				// sample per cycle. This function is
//				// under-sampled by a factor of 2.
//				// This produces a single peak in the
//				// spectrum at the wave number origin.
//				// The result is the same as if the
//				// sinusoidal surface had zero frequency
//				// as in case 7..
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = cos(2 * PI * col / 1);
//					}// end inner loop
//				}// end outer loop
//				break;
//
//			case 9:
//				// This case draws a sinusoidal surface on
//				// the horizontal axis with 2 samples per
//				// cycle. This is the Nyquist folding
//				// wave number. This causes a single
//				// peak to appear in the spectrum at the
//				// negative folding wave number on the
//				// horizontal axis. A peak would also
//				// appear at the positive folding wave
//				// number if it were visible, but it is
//				// one unit outside the boundary of the
//				// plot.
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = cos(2 * PI * col / 2);
//					}// end inner loop
//				}// end outer loop
//				break;
//
//			case 10:
//				// This case draws a sinusoidal surface on
//				// the vertical axis with 2 samples per
//				// cycle. Again, this is the Nyquist
//				// folding wave number but the sinusoid
//				// appears along a different axis. This
//				// causes a single peak to appear in the
//				// spectrum at the negative folding wave
//				// number on the vertical axis. A peak
//				// would also appear at the positive
//				// folding wave number if it were
//				// visible, but it is one unit outside
//				// the boundary of the plot.
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = cos(2 * PI * row / 2);
//					}// end inner loop
//				}// end outer loop
//				break;
//
//			case 11:
//				// This case draws a sinusoidal surface on
//				// the horizontal axis with 8 samples per
//				// cycle. You might think of this surface
//				// as resembling a sheet of corrugated
//				// roofing material. This produces
//				// symmetrical peaks on the horizontal
//				// axis on either side of the wave-
//				// number origin.
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = cos(2 * PI * col / 8);
//					}// end inner loop
//				}// end outer loop
//				break;
//
//			case 12:
//				// This case draws a sinusoidal surface on
//				// the horizontal axis with 3 samples per
//				// cycle plus a sinusoidal surface on the
//				// vertical axis with 8 samples per
//				// cycle. This produces symmetrical peaks
//				// on the horizontal and vertical axes on
//				// all four sides of the wave number
//				// origin.
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = cos(2 * PI * row / 8)
//								+ cos(2 * PI * col / 3);
//					}// end inner loop
//				}// end outer loop
//				break;
//
//			case 13:
//				// This case draws a sinusoidal surface at
//				// an angle of approximately 45 degrees
//				// relative to the horizontal. This
//				// produces a pair of peaks in the
//				// wave-number spectrum that are
//				// symmetrical about the origin at
//				// approximately 45 degrees relative to
//				// the horizontal axis.
//				double phase = 0;
//				for (int row = 0; row < rows; row++)
//				{
//					for (int col = 0; col < cols; col++)
//					{
//						spatialData[row][col] = cos(2.0 * PI * col / 8 - phase);
//					}// end inner loop
//					// Increase phase for next row
//					phase += .8;
//				}// end outer loop
//				break;
//
//			default:
//				System.out.println("Case must be "
//						+ "between 0 and 13 inclusive.");
//				System.out.println("Terminating program.");
//				System.exit(0);
//		}// end switch statement
//
//		return spatialData;
//	}// end getSpatialData
//}// end class ImgMod31
