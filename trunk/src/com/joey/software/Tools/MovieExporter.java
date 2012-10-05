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
package com.joey.software.Tools;


import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.VideoToolkit.BufferedImageStreamToAvi;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class MovieExporter
{
	public static void main(String inputArgs[]) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException
	{

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		final AScanViewerTool tool = new AScanViewerTool();
		tool.getImageViewPanel().setFile(FileSelectionField.getUserFile());
		// tool.getImageViewPanel().getFrg().setWindowMethod(ThorLabs2DFRGImageProducer.WINDOW_GAUSSIAN);
		// tool.getImageViewPanel().getFrg().setUseWindowing(true);
		JButton outputVideo = new JButton("Save Video");

		tool.getContentPane().add(outputVideo, BorderLayout.SOUTH);

		Rectangle r = new Rectangle(0, 0, 0, 1024);
		outputVideo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							JFrame f = new JFrame("Export Image");
							f.getContentPane().setLayout(new BorderLayout());
							f.setSize(800, 600);

							// Get Current image to allow user to chose cropped
							// region;
							DynamicRangeImage panel = (DynamicRangeImage) tool
									.getImageViewPanel().tabHolder
									.getSelectedComponent();

							ROIPanel regionPanel = new ROIPanel(false,
									ROIPanel.TYPE_RECTANGLE);
							regionPanel.setImage(panel.getImage().getImage());
							regionPanel
									.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

							f.getContentPane().add(regionPanel);

							FrameWaitForClose closeAdapter = new FrameWaitForClose(
									f);

							f.getContentPane().add(regionPanel);
							f.setSize(600, 480);
							f.setVisible(true);

							// Wait for the window to close
							closeAdapter.waitForClose();

							// Get Rectangle
							Rectangle r = (Rectangle) regionPanel.getRegions()
									.get(0);

							boolean useCropped = r.getWidth() > 1
									&& r.getHeight() > 1;

							if (r.x < 0)
							{
								r.width -= r.x;
								r.x = 0;
							}

							if (r.width % 2 != 0)
							{
								r.width += r.width % 2;
							}

							if (r.y < 0)
							{
								r.height += r.y;
								r.y = 0;
							}
							BufferedImageStreamToAvi output;

							BufferedImage img = panel.getImage().getImage();

							if (useCropped)
							{
								img = ImageOperations.cropImage(img, r);
							}

							// Get use fps

							JSpinner fpsValue = new JSpinner(
									new SpinnerNumberModel(10, 1, 1000, 1));
							JOptionPane
									.showMessageDialog(null, fpsValue, "Enter FPS", JOptionPane.INFORMATION_MESSAGE);

							output = new BufferedImageStreamToAvi(img
									.getWidth(), img.getHeight(), 25,
									"c:\\test\\", "file.avi", true, true);

							StatusBarPanel status = new StatusBarPanel();
							f.getContentPane().add(status, BorderLayout.CENTER);
							f.setSize(300, 20);
							f.setVisible(true);
							status.setMaximum(tool.getImageViewPanel()
									.getImageCount());
							for (int i = 0; i < tool.getImageViewPanel()
									.getImageCount(); i++)
							{
								status.setValue(i);
								try
								{
									tool.getImageViewPanel().setImagePos(i);
									img = panel.getImage().getImage();

									if (useCropped)
									{
										img = ImageOperations.cropImage(img, r);
									}

									output.pushImage(img);
								} catch (IOException e1)
								{
									status.setStatusMessage("Failed : " + i);
									e1.printStackTrace();
								}
							}
							output.finaliseVideo();
						} catch (Exception e)
						{

							JOptionPane
									.showMessageDialog(null, "Error : "
											+ e.getLocalizedMessage(), "Error Saving Video", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}

				};
				t.start();
			}
		});

		tool.setSize(600, 480);
		tool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tool.setVisible(true);
	}
}
