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
package com.joey.software.VideoToolkit;


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;


public class CompoentRecorderControler extends JPanel
{
	CompoentRecorder recorder;

	JButton startRecording = new JButton("Start");

	JButton stopRecording = new JButton("Stop");

	JButton snapCompoent = new JButton("Snap Still");
	
	JToggleButton pauseButton = new JToggleButton("Paused");

	FileSelectionField videoOutput = new FileSelectionField();

	FileSelectionField snapOutput = new FileSelectionField();

	JSpinner frameRate = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));

	public static void main(String input[]) throws AWTException
	{
		ImagePanel panel = new ImagePanel();

		CompoentRecorder recorder = new CompoentRecorder(panel);
		CompoentRecorderControler control = new CompoentRecorderControler(
				recorder);

		panel.setImage(ImageOperations.getGrayTestImage(300, 300, 1));

		FrameFactroy.getFrame(panel);
		FrameFactroy.getFrame(control);
	}

	public CompoentRecorderControler(CompoentRecorder recorder)
	{
		this.recorder = recorder;
		createJPanel();
		recorder.setControler(this);

		videoOutput.setExtensions(new String[]
		{ "avi:AVI Video File" }, false, true);
	}

	public void saveSnapImage()
	{
		BufferedImage snap = recorder.snapImage();
		snapOutput.setType(FileSelectionField.TYPE_SAVE_FILE);
		snapOutput.setFormat(FileSelectionField.FORMAT_IMAGE_FILES_SHOW_FORMAT);

		ImagePanel img = new ImagePanel(snap);

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(img.getInPanel(), BorderLayout.CENTER);
		holder.add(snapOutput, BorderLayout.SOUTH);
		holder.setPreferredSize(new Dimension(600, 480));
		if (JOptionPane
				.showConfirmDialog(null, holder, "Enter Path to save Image", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
		{
			try
			{
				System.out.println(FileOperations.getExtension(snapOutput
						.getFile()));
				System.out.println(snapOutput.getFile());
				if (!ImageIO.write(snap, FileOperations.getExtension(snapOutput
						.getFile()).substring(1), snapOutput.getFile()))
				{
					JOptionPane
							.showMessageDialog(null, "Unsupported format: "
									+ FileOperations.getExtension(snapOutput
											.getFile()));
				}
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "Error Saving Image : "
						+ e.getLocalizedMessage());
			}

		}
	}

	private void createJPanel()
	{
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
		buttonPanel.add(startRecording);
		buttonPanel.add(pauseButton);
		buttonPanel.add(snapCompoent);
		buttonPanel.add(stopRecording);

		setBorder(BorderFactory.createTitledBorder("Controls"));

		JPanel frameRatePanel = new JPanel(new BorderLayout());
		JPanel realRatePanel = new JPanel(new BorderLayout());

		videoOutput.setLabelSize(100);
		JLabel lab = new JLabel("Frame Rate :");
		lab.setHorizontalAlignment(SwingConstants.RIGHT);
		lab.setPreferredSize(new Dimension(100, 20));
		frameRatePanel.add(lab, BorderLayout.WEST);
		frameRatePanel.add(frameRate, BorderLayout.CENTER);

		lab = new JLabel("Real Rate :");
		lab.setHorizontalAlignment(SwingConstants.RIGHT);
		lab.setPreferredSize(new Dimension(100, 20));
		realRatePanel.add(lab, BorderLayout.WEST);
		realRatePanel.add(recorder.recordeRate, BorderLayout.CENTER);

		JPanel rateHolder = new JPanel(new GridLayout(1, 2));
		rateHolder.add(frameRatePanel);
		rateHolder.add(realRatePanel);

		JPanel mainPanel = new JPanel(new GridLayout(2, 1, 3, 3));
		mainPanel.add(videoOutput);
		mainPanel.add(rateHolder);

		JPanel holder = new JPanel(new BorderLayout());
		holder.add(mainPanel, BorderLayout.NORTH);
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.SOUTH);
		add(holder, BorderLayout.CENTER);

		pauseButton.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				recorder.setPause(pauseButton.isSelected());
			}
		});
		snapCompoent.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveSnapImage();
			}
		});
		startRecording.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					recorder
							.startRecording(videoOutput.getFile().getParent()
									+ "\\", videoOutput.getFile().getName(), (Integer) frameRate
									.getValue());
				} catch (IOException e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error : "
									+ e1.getLocalizedMessage(), "Recording Failed", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});

		stopRecording.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					recorder.stopRecording();
				} catch (IOException e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error : "
									+ e1.getLocalizedMessage(), "Stopping Failed", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
	}

	public void statusChanged()
	{
		if (recorder.isRecording())
		{
			startRecording.setEnabled(false);
			stopRecording.setEnabled(true);
		} else
		{
			startRecording.setEnabled(true);
			stopRecording.setEnabled(false);
		}
	}

}
