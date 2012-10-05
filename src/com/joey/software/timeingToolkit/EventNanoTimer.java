package com.joey.software.timeingToolkit;

import java.util.Vector;

public class EventNanoTimer
{
	public Vector<String> name = new Vector<String>();

	public Vector<long[]> time = new Vector<long[]>();

	public void tick(String id)
	{
		time.get(name.indexOf((id)))[1] = System.nanoTime();
	}

	public void mark(String id)
	{
		if (time.contains(id))
		{
			long[] dat = time.get(name.indexOf((id)));
			dat[0] = System.nanoTime();
			dat[1] = 0;
		} else
		{
			time.add(new long[]
			{ System.nanoTime(), 0 });
			name.add(id);
		}
	}

	public float getTime(String id)
	{

		long[] dat = time.get(name.indexOf((id)));
		if (dat == null)
		{
			return -1;
		}
		if (dat[1] == 0)
		{
			return (System.nanoTime() - dat[0])/1e6f;
		} else
		{
			return (dat[1] - dat[0])/1e6f;
		}
	}

	public void printData()
	{

		for (int i = 0; i < name.size(); i++)
		{
			System.out.println(name.get(i) + "\t:"
					+ ((time.get(i)[1] - time.get(i)[0])/1e6));
		}
	}

	public String getTitle()
	{
		String titleOut = "";
		for (int i = 0; i < name.size(); i++)
		{
			titleOut += name.get(i) + ",";
		}

		return titleOut;
	}

	public String getData()
	{

		String timeOut = "";
		for (int i = 0; i < name.size(); i++)
		{
			timeOut += (time.get(i)[1] - time.get(i)[0])/1e6 + ",";
		}
		return timeOut;
	}

	public void clear()
	{
		time.clear();
		name.clear();
	}
	
	public static void main(String input[])
	{
		EventNanoTimer time = new EventNanoTimer();
		
		time.mark("Test");
		for(int i = 0; i < 100000; i++);
		time.tick("Test");
		
		time.printData();
	}
}
