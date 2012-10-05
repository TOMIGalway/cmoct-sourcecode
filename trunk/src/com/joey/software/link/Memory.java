package com.joey.software.link;

import java.io.IOException;

import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.MultiThreadCrossCorrelation.CrossCorrProgram;


public class Memory
{
	public static void main(String input[])
	{
		allocateMemory();
	}
	public static void allocateMemory()
	{
		try
		{
			CrossCorrProgram.main(new String[]{""});
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
