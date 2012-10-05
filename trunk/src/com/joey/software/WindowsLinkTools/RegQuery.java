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
package com.joey.software.WindowsLinkTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.joey.software.framesToolkit.FrameFactroy;


public class RegQuery
{

	private static final String REGQUERY_UTIL = "reg query ";

	private static final String REGSTR_TOKEN = "REG_SZ";

	private static final String REGDWORD_TOKEN = "REG_DWORD";

	private static final String REGBINARY_TOKEN = "REG_BINARY";

	private static final String PERSONAL_FOLDER_CMD = REGQUERY_UTIL
			+ "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
			+ "Explorer\\Shell Folders\" /v Personal";

	private static final String CPU_SPEED_CMD = REGQUERY_UTIL
			+ "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\""
			+ " /v ~MHz";

	private static final String CPU_NAME_CMD = REGQUERY_UTIL
			+ "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\""
			+ " /v ProcessorNameString";

	private static final String COMPUTER_WINDOWS_FAVORITES_FOLDER = REGQUERY_UTIL
			+ "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v Favorites";

	public static VideoCardData[] getVideoCardData()
	{
		JTabbedPane tab = new JTabbedPane();
		FrameFactroy.getFrame(tab);
		ArrayList<VideoCardData> rst = new ArrayList<VideoCardData>();
		int num = Integer.parseInt(getVideoNum());
		for (int i = 0; i < num; i++)
		{
			JTextArea text = new JTextArea();
			try
			{

				String path = "HKLM\\HARDWARE\\DEVICEMAP\\VIDEO";
				String key = "\\Device\\Video" + i;

				tab.addTab(key, new JScrollPane(text));
				String temp = "HKLM\\system"
						+ getValue(path, key, REGSTR_TOKEN).substring(24)
								.trim();
				if (temp != null)
				{
					text.append(getValue(temp));
					VideoCardData d = new VideoCardData();
					d.memory = Integer
							.parseInt(getValue(temp, "HardwareInformation.MemorySize", REGDWORD_TOKEN));
					d.name = hexToString(getValue(temp, "HardwareInformation.AdapterString", REGBINARY_TOKEN));

					rst.add(d);

					text.append("\n\n\n\n\nRAM Size" + d.memory);
				}

			} catch (Exception e)
			{
				text.append(e.toString());
			}
		}

		return rst.toArray(new VideoCardData[0]);
	}

	public static String hexToString(String hex)
	{
		byte[] bytes = new byte[hex.length() / 4];
		for (int i = 0; i < bytes.length; i += 1)
		{
			bytes[i] = (byte) Integer
					.parseInt(hex.substring(4 * i, 4 * i + 2), 16);
		}

		return new String(bytes);
	}

	public static String getValue(String path)
	{
		return getValue(path, null, null);
	}

	public static String getValue(String path, String key)
	{
		return getValue(path, key, null);
	}

	public static String getValue(String path, String key, String token)
	{
		int num = Integer.parseInt(getVideoNum());
		for (int i = 0; i < num; i++)
		{

			String QUERY;
			if (key == null)
			{
				QUERY = REGQUERY_UTIL + "\"" + path + "\"";
			} else
			{
				QUERY = REGQUERY_UTIL + "\"" + path + "\"" + " /v " + key;
			}
			try
			{
				Process process = Runtime.getRuntime().exec(QUERY);
				StreamReader reader = new StreamReader(process.getInputStream());
				reader.start();
				process.waitFor();
				reader.join();
				String result = reader.getResult();

				if (token == null)
				{
					return result;
				} else if (token == REGDWORD_TOKEN)
				{
					int p = result.indexOf(token);

					if (p == -1)
						return null;

					// Read Video Numberf
					String temp = result.substring(p + REGDWORD_TOKEN.length())
							.trim();
					return Integer.toString((Integer.parseInt(temp
							.substring("0x".length()), 16) + 1));
				} else if (token == REGBINARY_TOKEN)
				{
					int p = result.indexOf(token);

					if (p == -1)
						return null;

					// Read Video Numberf
					return result.substring(p + REGDWORD_TOKEN.length() + 1)
							.trim();
				} else
				{
					int p = result.indexOf(token);

					if (p == -1)
						return null;

					// Read Video Numberf
					return result.substring(p + REGDWORD_TOKEN.length()).trim();
				}
			} catch (Exception e)
			{
				return null;
			}
		}

		return "";
	}

	public static String getVideoNum()
	{
		String GPU_SIZE = REGQUERY_UTIL
				+ "\"HKLM\\HARDWARE\\DEVICEMAP\\VIDEO\""
				+ " /v MaxObjectNumber";
		try
		{
			Process process = Runtime.getRuntime().exec(GPU_SIZE);
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String result = reader.getResult();
			int p = result.indexOf(REGDWORD_TOKEN);

			if (p == -1)
				return null;

			// Read Video Numberf
			String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
			return Integer.toString((Integer.parseInt(temp.substring("0x"
					.length()), 16) + 1));
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String getCurrentPCFavorites()
	{
		try
		{
			Process process = Runtime.getRuntime()
					.exec(COMPUTER_WINDOWS_FAVORITES_FOLDER);
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);
			if (p == -1)
				return null;
			return result.substring(p + REGSTR_TOKEN.length()).trim();
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String getCurrentUserPersonalFolderPath()
	{
		try
		{
			Process process = Runtime.getRuntime().exec(PERSONAL_FOLDER_CMD);
			StreamReader reader = new StreamReader(process.getInputStream());

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);

			if (p == -1)
				return null;

			return result.substring(p + REGSTR_TOKEN.length()).trim();
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String getCPUSpeed()
	{
		try
		{
			Process process = Runtime.getRuntime().exec(CPU_SPEED_CMD);
			StreamReader reader = new StreamReader(process.getInputStream());

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			int p = result.indexOf(REGDWORD_TOKEN);

			if (p == -1)
				return null;

			// CPU speed in Mhz (minus 1) in HEX notation, convert it to DEC
			String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
			return Integer.toString((Integer.parseInt(temp.substring("0x"
					.length()), 16) + 1));
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String getCPUName()
	{
		try
		{
			Process process = Runtime.getRuntime().exec(CPU_NAME_CMD);
			StreamReader reader = new StreamReader(process.getInputStream());

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);

			if (p == -1)
				return null;

			return result.substring(p + REGSTR_TOKEN.length()).trim();
		} catch (Exception e)
		{
			return null;
		}
	}

	static class StreamReader extends Thread
	{
		private InputStream is;

		private StringWriter sw;

		StreamReader(InputStream is)
		{
			this.is = is;
			sw = new StringWriter();
		}

		@Override
		public void run()
		{
			try
			{
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e)
			{
				;
			}
		}

		String getResult()
		{
			return sw.toString();
		}
	}

	public static long getSystemMemorySize()
	{
		com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();
		return mxbean.getTotalPhysicalMemorySize();
	}

	public static long getSystemVideoMemorySize()
	{
		VideoCardData[] data = getVideoCardData();
		if (data != null && data.length > 0)
		{
			return data[0].memory;
		}
		return -1;
	}

	public static void main(String s[])
	{
		String ram = ("Ram : " + getSystemMemorySize() / 1024 / 1024);
		String video = ("Video : " + getSystemVideoMemorySize() / 1024 / 1024);

		JOptionPane.showMessageDialog(null, ram + "\n" + video);

	}
}
