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

public class LinAlg
{

	// Generate identity matrix
	public static double[][] I(int N)
	{
		double[][] r = new double[N][N];
		for (int i = 0; i < N; ++i)
			r[i][i] = 1;
		return r;
	}

	// Clone matrix
	public static double[][] clone(double[][] A)
	{
		int M = A[0].length, N = A.length;
		double[][] r = new double[N][M];
		for (int i = 0; i < N; ++i)
			for (int j = 0; j < M; ++j)
				r[i][j] = A[i][j];
		return r;
	}

	// Transpose matrix
	public static double[][] transpose(double[][] A)
	{
		int M = A[0].length, N = A.length;
		double[][] r = new double[M][N];
		for (int i = 0; i < M; ++i)
			for (int j = 0; j < N; ++j)
				r[i][j] = A[j][i];
		return r;
	}

	// Multiply vector and matrix
	public static double[] mul(double[] B, double[][] A)
	{
		int M = A[0].length, N = A.length;
		double[] r = new double[M];
		for (int j = 0; j < M; ++j)
			for (int i = 0; i < N; ++i)
				r[j] += B[i] * A[i][j];
		return r;
	}

	// Multiply matrix and vector
	public static double[] mul(double[][] A, double[] B)
	{
		int M = A[0].length, N = A.length;
		double[] r = new double[N];
		for (int i = 0; i < N; ++i)
			for (int j = 0; j < M; ++j)
				r[i] += A[i][j] * B[j];
		return r;
	}

	// Multiply two matrices
	public static double[][] mul(double[][] A, double[][] B)
	{
		int M = A[0].length, N = A.length, K = B[0].length;
		double[][] r = new double[N][K];
		;
		for (int i = 0; i < N; ++i)
			for (int j = 0; j < K; ++j)
				for (int k = 0; k < M; ++k)
					r[i][j] += A[i][k] * B[k][j];
		return r;
	}

	// Solve system of linear equations
	public static double[][] solve(double[][] A, double[][] B)
	{
		A = invert(A);
		if (A == null)
			return null;
		return mul(A, B);
	}

	// Solve system of linear equations
	public static double[] solve(double[][] A, double[] B)
	{
		A = invert(A);
		if (A == null)
			return null;
		return mul(A, B);
	}

	// Borrowed from "Numerical Recipes in C".
	// Returns true if matrix A
	// is singular. Returns result vector if right
	// hand side is given, or
	// inverted matrix otherwise
	public static double[][] invert(double[][] A)
	{
		int N = A.length;
		A = clone(A);

		boolean[] pivot = new boolean[N];
		int[] indcol = new int[N];
		int[] indrow = new int[N];

		double val;
		int i, j, k, col, row;

		for (i = 0; i < N; ++i)
		{ // Main loop over the columns to be
			// reduced
			val = 0;
			col = row = -1;
			for (j = 0; j < N; ++j)
				// Outer loop, search for pivot
				// element
				if (!pivot[j])
					for (k = 0; k < N; ++k)
					{
						if (!pivot[k])
							if (Math.abs(A[j][k]) > val)
							{
								val = Math.abs(A[j][k]);
								row = j;
								col = k;
							}
					}
			if (col == -1)
				return null;
			pivot[col] = true;

			if (row != col) // Swap rows to put
							// the pivot element
							// on the diagonal
				for (j = 0; j < N; ++j)
				{
					val = A[row][j];
					A[row][j] = A[col][j];
					A[col][j] = val;
				}
			indrow[i] = row; // Remember column of
								// each pivot
								// element
			indcol[i] = col;

			val = A[col][col]; // Divide pivot row
								// by pivot
								// element
			A[col][col] = 1;
			for (j = 0; j < N; ++j)
				A[col][j] /= val;
			for (k = 0; k < N; ++k)
				if (k != col)
				{
					val = A[k][col];
					A[k][col] = 0;
					for (j = 0; j < N; ++j)
						A[k][j] -= A[col][j] * val;
				}
		}

		for (j = N - 1; j >= 0; --j)
			// Unscramble the inverted matrix
			if (indrow[j] != indcol[j])
				for (k = 0; k < N; ++k)
				{
					val = A[k][indrow[j]];
					A[k][indrow[j]] = A[k][indcol[j]];
					A[k][indcol[j]] = val;
				}
		return A;
	}

	// Test routine
	public static void main(String argv[])
	{
		double[][] A =
		{
		{ 1, 2, 3 },
		{ 4, 5, 6 },
		{ 7, 8, 10 } };
		double[][] B = I(3);
		double[][] C = solve(A, B);
		if (C == null)
			System.out.println("Singular");
		else
		{
			for (int i = 0; i < 3; ++i)
				for (int j = 0; j < 3; ++j)
					System.out.print((float) C[i][j] + " ");
			System.out.println();
		}
	}
}
