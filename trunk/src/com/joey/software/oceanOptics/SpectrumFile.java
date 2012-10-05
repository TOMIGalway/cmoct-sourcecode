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
package com.joey.software.oceanOptics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class SpectrumFile
{
	File file;

	/*
	 * File Parameters
	 */
	DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy, HH:mm:ss");

	Date date = new Date();

	String user = "";

	String specSerial = "";

	String specChan = "";

	String specType = "";

	String adcType = "";

	String refChannel = "";

	String graphTitle = "";

	int intValue = 0; // Integration time ms

	int avgValue = 0; // Average vlue

	int boxValue = 0; // Boxcar

	int temp = 0;

	int pxlsInFile = 0;

	boolean darkCorrect = false;

	boolean timeNormal = false;

	boolean dualBeamRef = false;

	float[] waveData;

	float[] countData;

	JPanel chartPanel = null;

	public SpectrumFile(File f) throws IOException
	{
		setFile(f);
	}

	public int getPxlsInFile()
	{
		return pxlsInFile;
	}

	public float[] getWaveData()
	{
		return waveData;
	}

	public float[] getCountData()
	{
		return countData;
	}

	public void setFile(File f) throws IOException
	{
		this.file = f;
		reloadData();
	}

	public void copyFile(File f) throws IOException
	{
		FileOperations.copyFile(this.file, f);
	}

	public File getFile()
	{
		return file;
	}

	public void reloadData() throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(getFile()));

		String line;
		String[] data;
		in.readLine();
		in.readLine();

		/*
		 * Parse Date
		 */
		line = in.readLine();
		try
		{
			data = line.split(":", 2);
			date = dateFormat.parse(data[1]);
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Parse User
		 */
		line = in.readLine();
		data = line.split(":", 2);
		user = data[1].substring(1);

		/*
		 * Parse Spec Serial
		 */
		line = in.readLine();
		data = line.split(":", 2);
		specSerial = data[1].substring(1);

		/*
		 * parse channel
		 */
		line = in.readLine();
		data = line.split(":", 2);
		specChan = data[1].substring(1);

		/*
		 * Parse integration time
		 */
		line = in.readLine();
		data = line.split(":", 2);
		intValue = Integer.parseInt(data[1].substring(1));

		/*
		 * Parse spectra Averaged
		 */
		line = in.readLine();
		data = line.split(":", 2);
		avgValue = Integer.parseInt(data[1].substring(1));

		/*
		 * Parse Box Average Value
		 */
		line = in.readLine();
		data = line.split(":", 2);
		boxValue = Integer.parseInt(data[1].substring(1));

		line = in.readLine();
		/*
		 * Parse Box Average Value
		 */
		darkCorrect = line.endsWith("Enabled");

		line = in.readLine();
		/*
		 * Parse dual Beam Reference
		 */
		dualBeamRef = !line.endsWith("Disabled");

		line = in.readLine();
		/*
		 * Parse Time Normalized
		 */
		timeNormal = !line.endsWith("Disabled");

		/*
		 * Parse Reference Channel
		 */
		line = in.readLine();
		data = line.split(":", 2);
		refChannel = data[1].substring(1);

		/*
		 * Temperature
		 */
		line = in.readLine();

		if (line.endsWith("Not acquired"))
		{
			temp = -1;
		} else
		{
			data = line.split(":", 2);
			temp = Integer.parseInt(data[1].substring(1));
		}

		/*
		 * Spec type
		 */
		line = in.readLine();
		data = line.split(":", 2);
		specType = data[1].substring(1);

		/*
		 * ADC type
		 */
		line = in.readLine();
		data = line.split(":", 2);
		adcType = data[1].substring(1);

		/*
		 * Parse pixels
		 */
		line = in.readLine();
		data = line.split(":", 2);
		pxlsInFile = Integer.parseInt(data[1].substring(1));

		in.readLine();
		in.readLine();

		waveData = new float[pxlsInFile];
		countData = new float[pxlsInFile];

		Scanner s = new Scanner(in);
		for (int i = 0; i < pxlsInFile; i++)
		{
			waveData[i] = s.nextFloat();
			countData[i] = s.nextFloat();
		}
	}

	public void scaleCountData(float value)
	{

		for (int i = 0; i < pxlsInFile; i++)
		{
			countData[i] = countData[i] * value;
		}

	}

	public void shiftCountData(float value)
	{
		for (int i = 0; i < pxlsInFile; i++)
		{
			countData[i] = countData[i] + value;
		}
	}

	public String getHeaderData()
	{
		StringBuilder out = new StringBuilder();
		out.append("Data : ");
		out.append(date);
		out.append("\nUser : ");
		out.append(user);
		out.append("\nSpectrometer Serial Number: ");
		out.append(specSerial);
		out.append("\nSpectrometer Channel: ");
		out.append(specChan);
		out.append("\nIntegration Time (msec): ");
		out.append(intValue);
		out.append("\nSpectra Averaged: ");
		out.append(avgValue);
		out.append("\nBoxcar Smoothing: ");
		out.append(boxValue);
		out.append("\nCorrect for Electrical Dark: ");
		out.append(darkCorrect);
		out.append("\nTime Normalized:");
		out.append(timeNormal);
		out.append("\nDual-beam Reference: ");
		out.append(dualBeamRef);
		out.append("\nReference Channel: ");
		out.append(refChannel);
		out.append("\nTemperature: ");
		out.append(temp);
		out.append("\nSpectrometer Type: ");
		out.append(specType);
		out.append("\nADC Type: ");
		out.append(adcType);
		out.append("\nNumber of Pixels in File: ");
		out.append(pxlsInFile);

		return out.toString();
	}

	public void printSpectrumData()
	{
		System.out.printf("Wave\tCounts\n");
		for (int i = 0; i < pxlsInFile; i++)
		{
			System.out.printf("%.2f\t%.2f\n", waveData[i], countData[i]);
		}
	}

	public static float[] addCountData(SpectrumFile d1, SpectrumFile d2)
	{
		int length = Math.min(d1.pxlsInFile, d2.pxlsInFile);
		float[] rst = new float[length];

		for (int i = 0; i < length; i++)
		{
			rst[i] = d1.countData[i] + d2.countData[i];
		}

		return rst;
	}

	/**
	 * will return d1-d2
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static float[] subtractCountData(SpectrumFile d1, SpectrumFile d2)
	{
		int length = Math.min(d1.pxlsInFile, d2.pxlsInFile);
		float[] rst = new float[length];

		for (int i = 0; i < length; i++)
		{
			rst[i] = d1.countData[i] - d2.countData[i];
		}

		return rst;
	}

	public JPanel getGraph()
	{
		if (chartPanel == null)
		{
			chartPanel = PlotingToolkit
					.getChartPanel(waveData, countData, "Spectrum Data", "Wave Length", "Counts");
		}
		return chartPanel;
	}

	public void unloadGraph()
	{
		chartPanel = null;
	}

	public static void main(String[] input) throws IOException
	{
		FileSelectionField data = new FileSelectionField();
		data.setLabelText("Load");

		JOptionPane.showConfirmDialog(null, data);

		SpectrumFile f = new SpectrumFile(data.getFile());
		System.out.println(f.getHeaderData());

		f.printSpectrumData();

		FrameFactroy.getFrame(f.getGraph());
	}
}
