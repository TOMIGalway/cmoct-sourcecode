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
package com.joey.software.Launcher;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.DataToolkit.ImageSliceToolkit;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.DataToolkit.OCTDataGeneratingTool;
import com.joey.software.LAFTools.EditableLAFControler;
import com.joey.software.Launcher.microneedleAnalysis.MicroNeedleAnalysis;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.mainProgram.OCTExperimentData;
import com.joey.software.stringToolkit.StringOperations;


public class MainLauncher
{
	public static final String PROGRAM_TITLE = "Dragon Volume Viewer - beta";

	public static void setLAF() throws IOException
	{

		EditableLAFControler laf = new EditableLAFControler(
				SettingsLauncher.getPath() + "\\LAF\\");
		laf.updateLAF(SettingsLauncher.readLAF(SettingsLauncher.getPath()
				+ "settings.dat"));
	}

	public static void processInputs(OCTAnalysis dat, String[] input)
	{
		// Check if any of the inputs is a file

		/**
		 * First assume that strings are passed
		 * files.. so try and load
		 */
		try
		{
			if (input.length >= 1)
			{
				File f = new File(input[0]);

				if (f.isFile())
				{
					String ext = FileOperations.getExtension(f);

					if (ext.toLowerCase().endsWith("drgwsp"))
					{

						dat.loadSet(f);
					}

					if (ext.toLowerCase().endsWith("drgprv"))
					{
						File prv = f;
						File raw = FileOperations.renameFileType(f, "drgraw");

						NativeDataSet data = new NativeDataSet(raw, prv);

						OCTExperimentData exp = new OCTExperimentData(dat,
								data, "DataSet");
						exp.addView();
						dat.addExperiment(exp);
					}

					if (ext.toLowerCase().endsWith("drgraw"))
					{
						File raw = f;
						File prv = FileOperations.renameFileType(f, "drgprv");

						NativeDataSet data = new NativeDataSet(raw, prv);

						OCTExperimentData exp = new OCTExperimentData(dat,
								data, "DataSet");
						exp.addView();

						dat.addExperiment(exp);

					}

				}
			}
		} catch (Exception e)
		{
			JOptionPane
					.showMessageDialog(null, "There was an error loading the file", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Trying to load files");
			e.printStackTrace();
		}

	}

	public static File[] createTestImageSet(boolean make) throws IOException
	{

		int xNum = 256;
		int yNum = 256;
		int zNum = 256;

		File[] rst = new File[zNum];
		for (int z = 0; z < zNum; z++)
		{
			BufferedImage img = null;
			if (make)
			{
				img = new BufferedImage(xNum, yNum, BufferedImage.TYPE_INT_ARGB);

				for (int x = 0; x < xNum; x++)
				{
					for (int y = 0; y < yNum; y++)
					{
						Color c;
						if (x >= z || y >= z)
						{
							c = new Color(0, 0, 0);
						} else
						{
							c = new Color(z, z, z);
						}
						img.setRGB(x, y, c.getRGB());
					}
				}
			}
			rst[z] = new File("c:\\test\\tst\\file"
					+ StringOperations.getNumberString(4, z) + ".png");
			if (make)
			{
				ImageIO.write(img, "png", rst[z]);
			}
			System.out.println(z + " of " + zNum);
		}
		return rst;
	}

	public static void testing(File raw, File prv) throws IOException
	{
		NativeDataSet natOld = new NativeDataSet(raw, prv);
		NativeDataSet natFull = new NativeDataSet(raw, prv);
		natFull.setLoadFullDataAsPreview(true);

		natOld.reloadData();
		natFull.reloadData();

		Dimension dOld = natOld.getRenderSliceSizeX();
		BufferedImage imgOld = ImageOperations.getBi(dOld);

		Dimension dFull = natFull.getRenderSliceSizeX();
		BufferedImage imgFull = ImageOperations.getBi(dFull);

		natOld.getRenderSliceX(0, imgOld, null);
		natFull.getRenderSliceX(0, imgFull, null);

		FrameFactroy.getFrame(imgOld, imgFull);
	}

	public static void main(String input[]) throws IOException,
			InterruptedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		if (input.length != 0)
		{
			if (input[0].equalsIgnoreCase("-setSettings"))
			{
				SettingsLauncher.main(input);
				return;
			}
		}

		if (false)
		{
			File raw = new File("c:\\test\\tst\\test.drgraw");
			File prv = new File("c:\\test\\tst\\test.drgprv");
			File file[] = createTestImageSet(false);
			if (true)
			{
				ImageSliceToolkit
						.writeVolumeDataToFile(raw, file, OCTDataGeneratingTool
								.getBUFFER_SIZE(), new StatusBarPanel());
			}
			testing(raw, prv);
			return;
		}
		SettingsLauncher.setActivePath(MainLauncher.class);

		// Set the look and feel
		try{
		setLAF();
		}catch(Exception e){
			
		}
		// Test if administrator access is needed
		if (!MainLauncher.checkReadWriteAccess())
		{
			JOptionPane
					.showMessageDialog(null, "The program was unable to Read/Write in the main directory\nPlease run the settings tool and enable Admin Launch");
			System.exit(0);
		}

		// SettingsLauncher.writeLicience("program.dat");
		// SettingsLauncher.testLicience("program.dat");
		// SettingsLauncher.writeLicience("program.dat");

		Date today = new Date(System.currentTimeMillis());
		if (today.after(new Date(2011, 6, 1)))
		{
			System.exit(0);
		}
		// Create program and menubar
		OCTAnalysis program = new OCTAnalysis();
		// program.logData(false);

		program.updateJMenuBar();

		// Read ram from settings file else set
		// size to 128 mb graphics card
		try
		{
			program.setVideoMemory(SettingsLauncher.readVRam(SettingsLauncher
					.getPath() + "settings.dat"));
		} catch (Exception e)
		{
			JOptionPane
					.showMessageDialog(null, "There was a problem reading the Video Ram\nPlease run the settings tool");
			program.setVideoMemory(128 * 1024 * 1024);
		}

		// Display program
		JFrame f = new JFrame();

		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(program);

		// Load the icon
		try
		{

			BufferedImage icon = OCTAnalysis.icon;
			f.setIconImage(icon);
			FrameFactroy.setDefaultIcon(icon);
			JOptionPane.getRootFrame().setIconImage(icon);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle(PROGRAM_TITLE);
		f.setJMenuBar(program.getJMenuBar());
		f.setSize(1023, 800);
		f.setVisible(true);

		// Process inputs
		processInputs(program, input);

		if (true)
		{
			return;
		}
		final OCTAnalysis ana = program;
		JButton launch = new JButton("Launch");

		FrameFactroy.getFrame(launch);
		launch.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub

				MicroNeedleAnalysis analysis = new MicroNeedleAnalysis(ana);
			}
		});
	}

	public static boolean checkReadWriteAccess()
	{
		File f = new File("temp.dat");
		try
		{
			f.createNewFile();
			f.delete();
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;

		}

		return true;
	}

	public static void switchToAdmin()
	{
		JOptionPane
				.showMessageDialog(null, "You are not an admin, about to switch");
	}
}
