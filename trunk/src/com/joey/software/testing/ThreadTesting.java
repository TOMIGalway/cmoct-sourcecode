package com.joey.software.testing;

public class ThreadTesting
{
	public static void main(String input[])
	{
		orignalClass oC = new orignalClass();
		newClass nC = new newClass();

		Thread thread1 = new Thread(oC);
		Thread thread2 = new Thread(nC);
		thread1.start();

		thread2.start();
	}

}

class orignalClass implements Runnable
{

	public String getName()
	{
		return "Orignal Class";
	}

	public void printName()
	{
		System.out.print("\nName:" + getName());
	}

	@Override
	public void run()
	{
		System.out.print("\n" + getName());
		printName();
	}

}

class newClass extends orignalClass
{
	@Override
	public String getName()
	{
		return "newClass";
	}

	@Override
	public void printName()
	{
		System.out.print("\nName:" + getName());
	}
}