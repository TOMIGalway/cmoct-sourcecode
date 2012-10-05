package com.joey.software.DataToolkit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.joey.software.timeingToolkit.EventTimer;


public class MultiThreadReader
{
	byte[] data;

	Object lock = new Object();

	ArrayList<ReaderThread> working = new ArrayList<ReaderThread>();

	ArrayList<ReaderThread> waiting = new ArrayList<ReaderThread>();

	int readSize = 1024;

	public static void createFile(File r, int size) throws IOException
	{
		FileOutputStream f = new FileOutputStream(r);
		BufferedOutputStream out = new BufferedOutputStream(f);

		for (int i = 0; i < size; i++)
		{
			out.write(12);
		}
	}

	public static void main(String input[]) throws IOException
	{
		File f = new File("c:\\test\\file");

		EventTimer t = new EventTimer();
		for (int mb = 1; mb < 512; mb += 32)
		{
			createFile(f, 1024 * 1024 * mb);
			MultiThreadReader read = new MultiThreadReader();
			read.data = new byte[(int) f.length()];

			t.mark("Single");
			read.loadSingle(f);
			t.tick("Single");

			t.mark("Multi");
			read.loadFile(f, 10);
			t.tick("Multi");

			if (mb == 1)
			{
				System.out.println("MB," + t.getTitle());
			}
			System.out.println(mb + "," + t.getData());
			t.clear();
		}
	}

	public void loadSingle(File f) throws IOException
	{

		FileInputStream in = new FileInputStream(f);
		in.read(data);
	}

	public void loadFile(File f, int threadNum) throws FileNotFoundException
	{
		for (int i = 0; i < threadNum; i++)
		{
			waiting.add(new ReaderThread(f, readSize));
		}

		int pos = 0;
		while (pos < data.length)
		{
			synchronized (lock)
			{

				while (!waiting.isEmpty() && pos < data.length)
				{
					ReaderThread r = waiting.get(0);
					waiting.remove(r);
					working.add(r);
					r
							.process(pos, Math
									.min(readSize, (data.length - pos)), this);
					pos += readSize;
				}

				try
				{
					lock.wait();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void reportFail(ReaderThread r, Exception e)
	{

	}

	public synchronized void reportSuccess(ReaderThread r)
	{
		System.arraycopy(r.data, 0, data, (int) r.pos, Math
				.min(r.data.length, this.data.length));
		synchronized (lock)
		{
			waiting.add(r);
			working.remove(r);
			lock.notify();
		}
	}
}

class ReaderThread implements Runnable
{
	MultiThreadReader owner;

	RandomAccessFile file;

	byte[] data;

	long pos;

	public ReaderThread(File f, int size) throws FileNotFoundException
	{
		file = new RandomAccessFile(f, "r");
		data = new byte[size];
	}

	public void readByte(long pos) throws IOException
	{
		this.pos = pos;
		file.seek(pos);
		file.read(data);
	}

	public void process(long pos, int size, MultiThreadReader owner)
	{
		if (data == null || data.length != size)
		{
			data = new byte[size];
		}
		this.owner = owner;
		this.pos = pos;
		run();
	}

	@Override
	public void run()
	{
		try
		{
			readByte(pos);
			owner.reportSuccess(this);
		} catch (IOException e)
		{
			e.printStackTrace();
			owner.reportFail(this, e);
		}

	}

}
