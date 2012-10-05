package com.joey.software.threadToolkit.processing;


public class ProcessingTest
{
	public static void main(String input[])
	{
		ProcessingJob job = new ProcessingJob()
		{
			byte[][][] data = new byte[100][100][100];

			@Override
			public void process(int[] pos)
			{
				for (int i = 0; i < 1000; i++)
				{
					Math.sin(2);
				}
			}

			@Override
			public int getNDim()
			{
				// TODO Auto-generated method stub
				return 3;
			}

			@Override
			public int getSizeDim(int dim)
			{
				if(dim == 0)
				{
					return data.length;
				}
				else if(dim == 1)
				{
					return data[0].length;
				}
				else if(dim == 2)
				{
					return data[0][0].length;
				}
				else return 0;
			}
		};

		ProcessingMaster master = new ProcessingMaster(4);
		System.out.println("Setting Job");
		master.setJob(job);
		System.out.println("Starting Job");
		master.doJob(true);
		System.out.print("Finished Job");
	}
}
