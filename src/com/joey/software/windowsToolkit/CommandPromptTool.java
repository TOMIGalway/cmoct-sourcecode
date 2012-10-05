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
package com.joey.software.windowsToolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextArea;

public class CommandPromptTool
{
	public static String sendCMD(String[] command) throws IOException
	{
		return sendCMD(command, true);
	}

	public static void sendCMD(String[] command, JTextArea output, boolean waitFor)
			throws IOException
	{
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);
		StreamToTextArea outGob = new StreamToTextArea(proc.getInputStream(),
				output);
		StreamToTextArea errGob = new StreamToTextArea(proc.getErrorStream(),
				output);
		outGob.setPriority(Thread.NORM_PRIORITY + 2);
		errGob.setPriority(Thread.NORM_PRIORITY + 2);
		outGob.start();
		errGob.start();
		try
		{
			if (waitFor)
			{
				proc.waitFor();
				outGob.join();
				errGob.join();
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outGob = null;
		errGob = null;

	}

	public static String sendCMD(String[] command, boolean waitFor)
			throws IOException
	{
		StringBuilder result = new StringBuilder();
		StringBuilder error = new StringBuilder();
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);
		StreamToString outGob = new StreamToString(proc.getInputStream(),
				result);
		StreamToString errGob = new StreamToString(proc.getErrorStream(), error);
		outGob.start();
		errGob.start();
		try
		{
			if (waitFor)
			{
				proc.waitFor();
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outGob = null;
		errGob = null;
		return result.toString();
	}

	public static void main(String arg[]) throws IOException
	{
		String[] commands =
		// { "taskkill", "/f", "/im", "firefox.exe" };
		{ "tasklist" };
		String result = sendCMD(commands);
		System.out.println(result);

	}
}

class StreamToString extends Thread
{
	InputStream is;

	StringBuilder result;

	StreamToString(InputStream is, StringBuilder result)
	{
		this.is = is;
		this.result = result;
	}

	@Override
	public void run()
	{

		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				result.append(line);
				result.append("\n");
			}
			isr.close();
			br.close();
			is.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

	}

	public StringBuilder getResult()
	{
		return result;
	}

	public void setResult(StringBuilder result)
	{
		this.result = result;
	}

}

class StreamToTextArea extends Thread
{
	InputStream is;

	JTextArea result;

	StreamToTextArea(InputStream is, JTextArea result)
	{
		this.is = is;
		this.result = result;
	}

	@Override
	public void run()
	{

		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				result.append(line);
				result.append("\n");
			}
			isr.close();
			br.close();
			is.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

	}

	public JTextArea getResult()
	{
		return result;
	}

	public void setResult(JTextArea result)
	{
		this.result = result;
	}
}
