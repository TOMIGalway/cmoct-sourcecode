package com.joey.software.j3dTookit;

public class J3DTool
{

	public static boolean NPOTAvailable = true;

	public static boolean isNPOTAvailable()
	{
		return NPOTAvailable;
	}

	public static int getNPOT(long num)
	{
		int val = 8;
		while (val <= num)
		{
			val *= 2;
		}

		return val;
	}

	public static long getEstimatedMemory2D(long sizeX, long sizeY, long sizeZ, int bytesPerVoxel)
	{
		if (J3DTool.isNPOTAvailable())
		{
			return sizeX * sizeY * sizeZ * 3 * bytesPerVoxel;
		} else
		{
			long xImages = sizeX * J3DTool.getNPOT(sizeY)
					* J3DTool.getNPOT(sizeZ);
			long yImages = sizeY * J3DTool.getNPOT(sizeX)
					* J3DTool.getNPOT(sizeZ);
			long zImages = sizeZ * J3DTool.getNPOT(sizeX)
					* J3DTool.getNPOT(sizeY);

			return (xImages + yImages + zImages) * bytesPerVoxel;
		}
	}

	public static long getBestTextureSize2D(long[] inSize, long[] outSize, long memByte, int numBytesPerPixel)
	{

		long xH = inSize[0];
		long xL = 4;
		long x = xH;

		long yH = inSize[1];
		long yL = 4;
		long y = yH;

		long zH = inSize[2];
		long zL = 4;
		long z = zH;

		long memory = memByte;
		long memMid = 0;

		boolean found = false;
		while (!found)
		{
			// System.out
			// .printf("X[%d,%d,%d] Y[%d,%d,%d] Z[%d,%d,%d] - Memory : [%d]\tAvailable : [%d]\n",xL,
			// x,xH, yL,y,yH,zL, z,zH, getEstimatedMemory2D(x, y, z), memory);
			x = xL + (xH - xL) / 2;
			y = yL + (yH - yL) / 2;
			z = zL + (zH - zL) / 2;

			memMid = getEstimatedMemory2D(x, y, z, numBytesPerPixel);

			if (memMid > memory)
			{
				xH = x;
				yH = y;
				zH = z;
			} else
			{
				xL = x;
				yL = y;
				zL = z;
			}

			if (xH - xL < 2)
			{
				found = true;
			}
		}

		outSize[0] = x - 2;
		outSize[1] = y - 2;
		outSize[2] = z - 2;
		System.out.println(numBytesPerPixel);
		return getEstimatedMemory2D(x, y, z, numBytesPerPixel);
	}

	public static void main(String input[])
	{

		long sizeX = 512;
		long sizeY = 256;
		long sizeZ = 512;

		long[] inSize = new long[]
		{ sizeX, sizeY, sizeZ };
		long[] outSize = new long[]
		{ sizeX, sizeY, sizeZ };

		long memory = 128 * 1024 * 1024;

		long m = 1;// getBestTextureSize2D(inSize, outSize, memory);

		System.out.println(getEstimatedMemory2D(512, 256, 512, 4));

		System.out
				.printf("In[%d,%d,%d] - Out[%d,%d,%d] - Scale [%2.3f,%2.3f,%2.3f]\t", inSize[0], inSize[1], inSize[2], outSize[0], outSize[1], outSize[2], ((float) outSize[0] / inSize[0]), ((float) outSize[1] / inSize[1]), ((float) outSize[2] / inSize[2]));
		System.out.println("Memory Used : " + ((float) m / (float) memory));
	}
}
