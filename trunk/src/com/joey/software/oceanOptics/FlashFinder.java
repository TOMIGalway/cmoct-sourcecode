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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.mathsToolkit.ArrayToolkit;


public class FlashFinder
{

	public static void main(String input[]) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		//			

		System.out.println("Starting");
		SpectrumFile[] src = getUserFiles();
		System.out.println("Files Loaded");
		SpectrumFile[] prc = find(src, 50, 2);
		System.out.println(prc.length);

		playUserData(src);
		showUserData(prc);

	}

	public static void ScaleData(String input[]) throws Exception
	{

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		File specFile = new File(
				"C:\\Users\\joey.enfield.UL\\Desktop\\G9 - Flash with polaroid.txt");

		File specFile2 = new File(
				"C:\\Users\\joey.enfield.UL\\Desktop\\G9 - Flash with polaroid.csv");

		File sensitivityFile = new File(
				"C:\\Users\\joey.enfield.UL\\Desktop\\spectrometer sensitivity.jpg.csv");

		double[][] senData = CSVFileToolkit.getDoubleData(sensitivityFile);
		ArrayToolkit.printToScreen(senData);
		SpectrumFile specInit = new SpectrumFile(specFile);
		SpectrumFile spec = new SpectrumFile(specFile);

		processData(senData, spec);

		JPanel before = new JPanel(new BorderLayout());
		before.setBorder(BorderFactory.createTitledBorder("Before"));
		before.add(specInit.getGraph());

		JPanel after = new JPanel(new BorderLayout());
		after.setBorder(BorderFactory.createTitledBorder("After"));
		after.add(spec.getGraph());

		JPanel[] data = new JPanel[]
		{ before, after };
		FrameFactroy.getFrame(data, 2, 1);

		spec.copyFile(specFile2);

	}

	public static void processData(double[][] senData, SpectrumFile spec)
	{
		for (int i = 0; i < spec.getPxlsInFile(); i++)
		{
			double scale = getWaveScale(senData, spec.getWaveData()[i]);
			if (scale == 0)
			{
				spec.getCountData()[i] = 0;

			} else
			{
				spec.getCountData()[i] = (float) (spec.getCountData()[i] / scale);

			}
		}

	}

	public static double getWaveScale(double[][] senData, double wave)
	{
		if (senData[0][0] > wave)
		{
			return 0;
		}
		for (int x = 0; x < senData.length; x++)
		{
			if (senData[x][0] > wave)
			{
				return senData[x][1];
			}
		}
		return 0;
	}

	public static void dataViewer() throws IOException
	{
		SpectrumFile[] src = getUserFiles();
		FrameFactroy.getFrame(src[0].getGraph());
	}

	public static void playUserData(final SpectrumFile[] data)
	{
		final int count = 0;
		final JPanel root = new JPanel();
		final JButton next = new JButton("Next");
		final JButton last = new JButton("Last");
		final JButton save = new JButton("Save");
		final JButton mode = new JButton("Video");
		JTextField frame = new JTextField(5);

		JPanel buttonHolder = new JPanel();
		buttonHolder.setLayout(new GridLayout(1, 4));
		buttonHolder.add(last);
		buttonHolder.add(mode);
		buttonHolder.add(next);
		buttonHolder.add(save);
		buttonHolder.add(frame);

		final JPanel holder = new JPanel(new BorderLayout());

		class Change implements Runnable, ActionListener
		{

			int count = 0;

			JPanel holder;

			SpectrumFile[] data;

			int delay;

			boolean playing = true;

			JTextField frame;

			FileSelectionField saveField = new FileSelectionField();

			public Change(JPanel holder, JTextField frame, SpectrumFile[] data, int delay)
			{
				this.holder = holder;
				this.data = data;
				this.delay = delay;
				this.frame = frame;
			}

			public void updatePanel()
			{
				holder.removeAll();
				holder.add(data[count].getGraph());
				holder.validate();
				holder.repaint();

				frame.setText(count + " of " + data.length);
			}

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == last)
				{
					count--;
					if (count < 0)
					{
						count = 0;
					}
				} else if (e.getSource() == next)
				{
					count++;
					if (count >= data.length)
					{
						count = data.length - 1;
					}
				} else if (e.getSource() == mode)
				{
					playing = !playing;
				} else if (e.getSource() == save)
				{
					saveCurrent();
				}
				updatePanel();
			}

			public void saveCurrent()
			{
				try
				{
					if (saveField.getUserChoice() == true)
					{
						File f = saveField.getFile();
						data[count].copyFile(f);
					}
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "Error Copying File"
							+ e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						if (playing)
						{
							count++;
							if (count > data.length)
							{
								count = 0;
							}
							updatePanel();
						}
						Thread.sleep(delay);
					} catch (Exception e)
					{

					}
				}
			}

		}
		;

		JPanel pan = new JPanel(new BorderLayout());
		pan.add(buttonHolder, BorderLayout.SOUTH);
		pan.add(holder, BorderLayout.CENTER);

		Change c = new Change(holder, frame, data, 10);

		last.addActionListener(c);
		next.addActionListener(c);
		mode.addActionListener(c);
		save.addActionListener(c);
		Thread t = new Thread(c);
		FrameFactroy.getFrame(pan);
		c.updatePanel();
		t.start();

	}

	public static void showUserData(final SpectrumFile[] data)
	{
		final int count = 0;
		final JPanel root = new JPanel();
		final JButton last = new JButton("Last");
		final JButton next = new JButton("Next");
		final JButton save = new JButton("Save");
		JPanel buttonHolder = new JPanel();
		buttonHolder.setLayout(new GridLayout(1, 2));
		buttonHolder.add(last);
		buttonHolder.add(next);
		buttonHolder.add(save);

		final JPanel holder = new JPanel(new BorderLayout());

		class Change implements ActionListener
		{
			FileSelectionField saveField = new FileSelectionField();

			int count = 0;

			JPanel holder;

			SpectrumFile[] data;

			public Change(JPanel holder, SpectrumFile[] data)
			{
				this.holder = holder;
				this.data = data;
			}

			public void updatePanel()
			{
				holder.removeAll();
				holder.add(data[count].getGraph());
				holder.validate();
				holder.repaint();

			}

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == last)
				{
					count--;
					if (count < 0)
					{
						count = 0;
					}
				} else if (e.getSource() == next)
				{
					count++;
					if (count >= data.length)
					{
						count = data.length - 1;
					}
				} else if (e.getSource() == save)
				{
					saveCurrent();
				}
				updatePanel();
			}

			public void saveCurrent()
			{
				try
				{
					if (saveField.getUserChoice() == true)
					{
						File f = saveField.getFile();
						data[count].copyFile(f);
					}
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "Error Copying File"
							+ e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		;

		Change change = new Change(holder, data);
		next.addActionListener(change);
		last.addActionListener(change);
		save.addActionListener(change);
		JPanel pan = new JPanel(new BorderLayout());
		pan.add(buttonHolder, BorderLayout.SOUTH);
		pan.add(holder, BorderLayout.CENTER);

		FrameFactroy.getFrame(pan);
		change.updatePanel();
	}

	public static SpectrumFile[] getUserFiles() throws IOException
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(null);

		File[] files = chooser.getSelectedFiles();
		SpectrumFile[] spec = new SpectrumFile[files.length];
		for (int i = 0; i < files.length; i++)
		{
			spec[i] = new SpectrumFile(files[i]);
		}
		return spec;
	}

	public static void getAverage(SpectrumFile[] data, float[] min, float[] max, float[] avg)
	{
		for (int wav = 0; wav < data[0].getPxlsInFile(); wav++)
		{
			for (int i = 0; i < data.length; i++)
			{
				if (i == 0)
				{
					avg[wav] = 0;
					min[wav] = data[i].getCountData()[wav];
					max[wav] = data[i].getCountData()[wav];
				}

				float val = data[i].getCountData()[wav];

				avg[wav] += val;
				if (val < min[wav])
				{
					min[wav] = val;
				}
				if (val > max[wav])
				{
					max[wav] = val;
				}
			}
			avg[wav] /= data.length;

		}
	}

	public static SpectrumFile[] find(SpectrumFile[] data, double minDiff, int around)
	{
		ArrayList<SpectrumFile> rst = new ArrayList<SpectrumFile>();

		int size = data[0].getCountData().length;
		float[] min = new float[size];
		float[] max = new float[size];
		float[] avg = new float[size];

		getAverage(data, min, max, avg);

		for (int i = 0; i < data.length; i++)
		{
			for (int wav = 0; wav < data[i].getPxlsInFile(); wav++)
			{
				double diff = (max[wav] - min[wav]);

				if (diff > minDiff)
				{

					float val = data[i].getCountData()[wav];
					if (val > min[wav] + diff / 2)
					{
						System.out.println("num : " + i + " and " + diff);
						for (int x = 0; x < around; x++)
						{
							try
							{
								rst.add(data[i + (int) (around / 2.0 + x)]);
							} catch (Exception e)
							{

							}
						}
						break;
					}
				}
			}

		}
		return rst.toArray(new SpectrumFile[0]);
	}
}
