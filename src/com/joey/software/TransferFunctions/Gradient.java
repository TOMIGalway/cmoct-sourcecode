package com.joey.software.TransferFunctions;

import javax.vecmath.Vector3f;

public class Gradient
{
	int mDepth = 0;

	int mHeight = 0;

	int mWidth = 0;

	int[] mGradients = null;

	// / <summary>
	// / Generates gradients using a central differences scheme.
	// / </summary>
	// / <param name="sampleSize">The size/radius of the sample to take.</param>
	private void generateGradients(int sampleSize)
	{
		int n = sampleSize;
		Vector3f normal = new Vector3f();
		Vector3f s1 = null, s2 = null;

		int index = 0;
		for (int z = 0; z < mDepth; z++)
		{
			for (int y = 0; y < mHeight; y++)
			{
				for (int x = 0; x < mWidth; x++)
				{
					s1.x = sampleVolume(x - n, y, z);
					s2.x = sampleVolume(x + n, y, z);
					s1.y = sampleVolume(x, y - n, z);
					s2.y = sampleVolume(x, y + n, z);
					s1.z = sampleVolume(x, y, z - n);
					s2.z = sampleVolume(x, y, z + n);

					s2.sub(s1);
					s2.normalize();
					// mGradients[index++] = Vector3f.Normalize(s2 - s1);

				}
			}
		}
	}

	public float sampleVolume(int x, int y, int z)
	{
		return 1;
	}

	// / <summary>
	// / Applies an NxNxN filter to the gradients.
	// / Should be an odd number of samples. 3 used by default.
	// / </summary>
	// / <param name="n"></param>
	private void filterNxNxN(int n)
	{
		int index = 0;
		for (int z = 0; z < mDepth; z++)
		{
			for (int y = 0; y < mHeight; y++)
			{
				for (int x = 0; x < mWidth; x++)
				{
					// mGradients[index++] = sampleNxNxN(x, y, z, n);
				}
			}
		}
	}

	// / <summary>
	// / Samples the sub-volume graident volume and returns the average.
	// / Should be an odd number of samples.
	// / </summary>
	// / <param name="x"></param>
	// / <param name="y"></param>
	// / <param name="z"></param>
	// / <param name="n"></param>
	// / <returns></returns>
	private Vector3f sampleNxNxN(int x, int y, int z, int n)
	{
		n = (n - 1) / 2;

		Vector3f average = new Vector3f();
		int num = 0;

		// for (int k = z - n; k <= z + n; k++)
		// {
		// for (int j = y - n; j <= y + n; j++)
		// {
		// for (int i = x - n; i <= x + n; i++)
		// {
		// if (isInBounds(i, j, k))
		// {
		// average += sampleGradients(i, j, k);
		// num++;
		// }
		// }
		// }
		// }
		//
		// average /= (float)num;
		// if (average.X != 0.0f && average.Y != 0.0f && average.Z != 0.0f)
		// average.Normalize();

		return average;
	}
}
