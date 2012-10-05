package com.joey.software.MultiThreadCrossCorrelation.threads;

public class CrossCorrelationTool
{

	public static void main(String input[])
	{

		int wide = 1024;
		int high = 512;
		int kerX = 3;
		int kerY = 3;
		int threshold = -100;

		byte frameA[][] = new byte[wide][high];
		byte frameB[][] = new byte[wide][high];
		short result[][] = new short[wide][high];

		for (int ker = 1; ker < 10; ker++)
		{
			// System.out.printf("Ker ( %d x %d )\t: ",2
			// * ker + 1, 2 * ker + 1);
			for (int i = 0; i < 5; i++)
			{
				long start = System.currentTimeMillis();
				manualCrossCorr(frameA, frameB, ker, ker, threshold, result);
				System.out.print("" + (System.currentTimeMillis() - start)
						+ " , ");
			}
			System.out.println();
		}
	}

	/**
	 * 
	 * @param frameA
	 *            - Source Frame
	 * @param frameB
	 *            - Second Frame
	 * @param kerX
	 *            - Kernal Order X
	 * @param kerY
	 *            - Kernal Order Y
	 * @param threshold
	 * @param result
	 */
	public static void manualCrossCorr(byte[][] frameA, byte[][] frameB, int kerX, int kerY, int threshold, short[][] result)
	{
		int[] gridA = new int[(2 * kerX + 1) * (2 * kerY + 1)];
		int[] gridB = new int[(2 * kerX + 1) * (2 * kerY + 1)];
		int pxlCount = 0;
		int wide = frameA.length;
		int high = frameA[0].length;

		for (int xP = 0; xP < wide; xP++)
		{
			for (int yP = 0; yP < high; yP++)
			{
				pxlCount = 0;
				for (int x = xP - kerX; x <= xP + kerX; x++)
				{

					for (int y = yP - kerY; y <= yP + kerY; y++)
					{
						if (x < wide && y < high && x >= 0 && y >= 0)
						{
							gridA[pxlCount] = (frameA[x][y] < 0 ? frameA[x][y] + 256
									: frameA[x][y]);
							gridB[pxlCount] = (frameB[x][y] < 0 ? frameB[x][y] + 256
									: frameB[x][y]);
						} else
						{
							gridA[pxlCount] = 0;
							gridB[pxlCount] = 0;
						}
						pxlCount++;
					}
				}
				// Calculate the cross coralation
				double crossCorr = CrossCorrelationTool
						.getCrossCorr(gridA, gridB, threshold);

				result[xP][yP] = (short) ((crossCorr) * Short.MAX_VALUE);

			}
		}

	}
	
	public static float getVariance(int[] data, int threshold)
	{
		float avg = getAverage(data);
		if(avg < threshold)
		{
			return 0;
		}
		float rst = 0;
		for(int i = 0; i < data.length; i++)
		{
			rst +=(data[i]-avg)*(data[i]-avg);
		}
		return rst/data.length;
	}

	public static float getCrossCorr(int[] gridA, int[] gridB,int threshold)
	{
		float avgA = getAverage(gridA);
		float avgB = getAverage(gridB);
		if (avgA < threshold || avgB < threshold)
		{
			return 1;
		}

		float tA = 0;
		float tB = 0;
		float tC = 0;
		float t1 = 0;
		float t2 = 0;

		for (int i = 0; i < gridA.length; i++)
		{
			t1 = gridA[i] - avgA;
			t2 = gridB[i] - avgB;
			tA += t1 * t2;
			tB += t1 * t1;
			tC += t2 * t2;
		}

		if (tB == 0 || tC == 0)
		{
			return 1;
		}
		return (float) (tA / Math.sqrt(tB * tC));
	}

	public static int getAverage(int data[])
	{
		int value = 0;
		for (int i : data)
		{
			value += i;
		}
		return value / data.length;
	}

	public static void manualCrossCorr(byte[][] frameA, byte[][] frameB, int kerX, int kerY, int threshold, float[][] result)
	{
		int[] gridA = new int[(2 * kerX + 1) * (2 * kerY + 1)];
		int[] gridB = new int[(2 * kerX + 1) * (2 * kerY + 1)];
		int pxlCount = 0;
		int wide = frameA.length;
		int high = frameA[0].length;

		for (int xP = 0; xP < wide; xP++)
		{
			for (int yP = 0; yP < high; yP++)
			{
				pxlCount = 0;
				for (int x = xP - kerX; x <= xP + kerX; x++)
				{

					for (int y = yP - kerY; y <= yP + kerY; y++)
					{
						if (x < wide && y < high && x >= 0 && y >= 0)
						{
							gridA[pxlCount] = (frameA[x][y] < 0 ? frameA[x][y] + 256
									: frameA[x][y]);
							gridB[pxlCount] = (frameB[x][y] < 0 ? frameB[x][y] + 256
									: frameB[x][y]);
						} else
						{
							gridA[pxlCount] = 0;
							gridB[pxlCount] = 0;
						}
						pxlCount++;
					}
				}
				// Calculate the cross coralation
				result[xP][yP] = CrossCorrelationTool
						.getCrossCorr(gridA, gridB, threshold);

				

			}
		}
		
	}

}
