package com.joey.software.threadToolkit;


public class TestingTaskMaster
{
	public static void main(String input[])
	{
		TaskMaster manager = new TaskMaster(4, 0);
		manager.start();

		for (int i = 0; i < 100; i++)
		{
			System.out.println("Adding : " + i);
			manager.addTask(new PrintTask(i));
		}
	}
}

class PrintTask implements Task
{
	int val = 0;

	public PrintTask(int value)
	{
		this.val = value;
	}

	@Override
	public void doTask()
	{
		System.out.println("Finished : " + val);
		for (int i = 0; i < 10000000; i++)
		{

			Math.random();

		}
	}
}

class ImageProcessing implements Task
{

	int[][] src;

	int[][] dst;

	int kerX = 1;

	int kerY = 1;

	public ImageProcessing(int[][] src, int[][] dst, int kerX, int kerY)
	{
		this.src = src;
		this.dst = dst;

		this.kerX = kerX;
		this.kerY = kerY;
	}

	@Override
	public void doTask()
	{
		int x = 0;
		int y = 0;

		int wide = src.length;
		int high = src[0].length;

		for (int xP = 0; xP < wide; xP++)
		{
			for (int yP = 0; yP < high; yP++)
			{

				for (int xL = -kerX; xL < kerX; xL++)
				{
					for (int yL = -kerY; yL < kerY; yL++)
					{
						x = xP + kerX;
						y = yP + kerY;

					}
				}

			}
		}
	}

}
