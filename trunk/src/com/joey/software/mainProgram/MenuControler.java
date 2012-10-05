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
package com.joey.software.mainProgram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.DataToolkit.TiffDataSet;
import com.joey.software.Launcher.MainLauncher;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.memoryToolkit.MemoryUsagePanel;
import com.joey.software.userinterface.OCTDataCreatorPanel;


class MenuControler
{
	JMenu fileMenu = new JMenu("File");

	JMenu helpMenu = new JMenu("Help");

	JMenu toolMenu = new JMenu("Tools");

	OCTAnalysis owner;

	/**
	 * For Menu Bar
	 */
	// For file menu
	JMenuItem loadSetMenuItem = new JMenuItem("Load Workspace");

	JMenuItem saveSetMenuItem = new JMenuItem("Save Workspace");

	JMenuItem closeSetMenuItem = new JMenuItem("Close Workspace");

	JMenuItem addExpMenuItem = new JMenuItem("Add Dataset");

	JMenuItem changeToNative = new JMenuItem("Change To Native");

	JMenuItem toggleNavigationWindow = new JMenuItem(
			"Toggle 2D Navagation Window");

	JMenuItem toggleRenderQuality = new JMenuItem("Toggle 2D Render Quality");

	JMenuItem exitItem = new JMenuItem("Exit");

	// For Help Meun
	JMenuItem helpMenuItem = new JMenuItem("Help");

	JMenuItem showMemoryUsagePanel = new JMenuItem("Show Memory Usage");

	// Tool Menu
	JMenuItem saveMeasurementCSV = new JMenuItem("Save Measurment CSV");

	/**
	 * For Popup Bars
	 */
	JMenuItem loadSetItem = new JMenuItem("Load Workspace");

	JMenuItem saveSetItem = new JMenuItem("Save Workspace");

	JMenuItem closeSetItem = new JMenuItem("Close Workspace");

	JMenuItem addExpItem = new JMenuItem("Add Dataset");

	JMenuItem removeExpItem = new JMenuItem("Remove Dataset");

	JMenuItem renameExpItem = new JMenuItem("Rename Dataset");

	JMenuItem addViewItem = new JMenuItem("Add View");

	JMenuItem removeViewItem = new JMenuItem("Remove View");

	JMenuItem copyViewItem = new JMenuItem("Copy View");

	JMenuItem renameViewItem = new JMenuItem("Rename View");

	JMenuItem resetViewItem = new JMenuItem("Reset View");

	JMenuItem createNewDataSetItem = new JMenuItem("Import Data Tool");

	public MenuControler(OCTAnalysis prog)
	{
		owner = prog;
		createMenu();
	}

	public void createMenu()
	{
		// File Menu Bar
		fileMenu.add(loadSetMenuItem);
		fileMenu.add(saveSetMenuItem);
		fileMenu.add(closeSetMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(addExpMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitItem);

		// Tool Menu
		toolMenu.add(saveMeasurementCSV);

		// Help Menu Bar
		helpMenu.add(helpMenuItem);
		// helpMenu.add(showMemoryUsagePanel);

		toggleNavigationWindow.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.slicePanel.getPreviewPanel()
						.setNavigationImageEnabled(!owner.slicePanel
								.getPreviewPanel().isNavigationImageEnabled());

			}
		});

		toggleRenderQuality.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.slicePanel.getPreviewPanel()
						.setHighQualityRenderingEnabled(!owner.slicePanel
								.getPreviewPanel()
								.isHighQualityRenderingEnabled());

			}
		});
		helpMenuItem.addActionListener(new ActionListener()
		{

			ImageIcon icon = null;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String message = "<html><center><strong><big>"
							+ MainLauncher.PROGRAM_TITLE
							+ "</big></strong></center><br>";
					message += "This is currently a beta version of the software. If there are any issues while<BR>";
					message += "using the software please contact the author using the details below<br>";
					message += "<br><br><center><table>";
					message += "<tr><td align=right><b>Author :</b> </td><td align =left><i>Joey Enfield</i></td></tr>";
					message += "<tr><td align=right><b>Email : </b> </td><td  align =left><i>joey.enfield@gmail.com</i></td></tr>";
					message += "<tr><td align=right><b>Tel No. :</b> </td><td align =left>+353877548600</td></tr></table></center>";

					if (icon == null)
					{
						BufferedImage img = new BufferedImage(OCTAnalysis.icon
								.getWidth() / 2, OCTAnalysis.icon.getHeight() / 2,
								BufferedImage.TYPE_INT_ARGB);
						img.getGraphics().drawImage(OCTAnalysis.icon, 0, 0, img
								.getWidth(), img.getHeight(), null);
						icon = new ImageIcon(img);
					}

					JOptionPane
							.showMessageDialog(null, new JLabel(message), "Contact Info", JOptionPane.PLAIN_MESSAGE, icon);
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		// Add Listners
		saveMeasurementCSV.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					java.io.File f = FileSelectionField
							.getUserFile(new String[]
							{ "csv:CSV file (*.csv)" });
					owner.saveCSVExperimentMeasureData(f.toString());
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(null, "Problem Saving : "
							+ e1.getLocalizedMessage());
				}
			}
		});
		createNewDataSetItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread()
				{

					@Override
					public void run()
					{

						/**
						 * This will convert a new dataset and ask to add it to
						 * the current dataset
						 */
						OCTDataCreatorPanel data = new OCTDataCreatorPanel();
						data.setPanelIndex(0);

						FrameWaitForClose close = new FrameWaitForClose(data);

						data.setVisible(true);
						close.waitForClose();

						if (data.getLastWorked())
						{
							if (JOptionPane
									.showConfirmDialog(null, "Would you like to add the dataset to\nthe current workspace", "", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
							{
								try
								{
									NativeDataSet dataset = data
											.getLastDataSet();

									OCTExperimentData progData = new OCTExperimentData(
											owner, dataset, "Dataset");
									progData.addView();

									owner.addExperiment(progData);

								} catch (IOException e1)
								{
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				};
				t.start();

			}
		});
		showMemoryUsagePanel.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				MemoryUsagePanel.getMemoryUsagePanel(1000, 100);

			}
		});

		ActionListener addDataListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread load = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						owner.addExperiment();
					}
				});
				load.start();
			}
		};

		addExpItem.addActionListener(addDataListener);
		addExpMenuItem.addActionListener(addDataListener);

		ActionListener loadSetActionListner = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					owner.loadSetPressed();
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(owner, "Error Opening File :"
							+ e1);
					e1.printStackTrace();
				}
			}
		};

		loadSetItem.addActionListener(loadSetActionListner);
		loadSetMenuItem.addActionListener(loadSetActionListner);

		ActionListener saveSetActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					owner.saveSetPressed();
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(owner, "Error Saving File :"
							+ e1);
					e1.printStackTrace();

				}
			}
		};

		saveSetItem.addActionListener(saveSetActionListener);
		saveSetMenuItem.addActionListener(saveSetActionListener);

		exitItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.exitPressed();
			}
		});
	}

	public void getExperimentPopupMenu(JPopupMenu menu, final OCTExperimentData data)
	{
		getBasePopupMenu(menu);
		menu.add(removeExpItem);
		menu.add(renameExpItem);

		if (data.getData() instanceof TiffDataSet)
		{
			menu.add(changeToNative);
			ActionListener changeToNativeActionListener = new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					Thread t = new Thread()
					{
						@Override
						public void run()
						{
							owner.saveCurrentExperimentData();
							TiffDataSet dat = (TiffDataSet) data.getData();

							OCTDataCreatorPanel pan = new OCTDataCreatorPanel();
							pan.setInputData(dat);

							FrameWaitForClose wait = new FrameWaitForClose(pan);

							pan.setVisible(true);
							wait.waitForClose();

							if (pan.getLastWorked())
							{
								try
								{
									/*
									 * Check if this experiemnt is the selected
									 * experiment
									 * 
									 * if so - unload experiment
									 */
									if (owner.currentExperiment == data)
									{
										owner.unloadExperimentData();
									}
									NativeDataSet newDat = pan.getLastDataSet();
									TiffDataSet oldDat = dat;

									float scaleX = ((float) oldDat
											.getPreviewSizeX() / (float) newDat
											.getPreviewSizeX());
									float scaleY = ((float) oldDat
											.getPreviewSizeY() / (float) newDat
											.getPreviewSizeY());
									float scaleZ = ((float) oldDat
											.getPreviewSizeZ() / (float) newDat
											.getPreviewSizeZ());

									// System.out.printf("Scale [%3.3f,%3.3f,%3.3f]\n",
									// scaleX, scaleY, scaleZ);
									// System.out.println("View Data : \n");
									for (OCTViewDataHolder hold : data
											.getViews())
									{
										hold.getSliceData()
												.setRenderHighRes(true);
										// System.out.println(hold.getSliceData().p1x);
										// System.out.println(hold.getSliceData().p2x);
										// System.out.println(hold.getSliceData().p1y);
										// System.out.println(hold.getSliceData().p2y);
										// System.out.println(hold.getSliceData().p1z);
										// System.out.println(hold.getSliceData().p2z);
									}

									data.setData(newDat);
									/**
									 * Convert Measurments size
									 */

									if (owner.currentExperiment == data)
									{
										owner.reloadExperimentData();
										owner.updateView();
									}

								} catch (IOException e)
								{
									JOptionPane
											.showMessageDialog(null, "There was an error :\n"
													+ e);
									e.printStackTrace();
								}
							}
						}
					};
					t.start();

				}
			};

			for (ActionListener l : changeToNative.getActionListeners())
			{
				changeToNative.removeActionListener(l);
			}
			changeToNative.addActionListener(changeToNativeActionListener);

		}
		menu.add(new JSeparator());
		menu.add(addViewItem);

		// Add suitabel Listners
		ActionListener addViewActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				data.addView();
			}
		};

		for (ActionListener l : addViewItem.getActionListeners())
		{
			addViewItem.removeActionListener(l);
		}
		addViewItem.addActionListener(addViewActionListener);

		// Add suitabel Listners
		ActionListener removeDataActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.removeExperiment(data);
			}
		};

		for (ActionListener l : removeExpItem.getActionListeners())
		{
			removeExpItem.removeActionListener(l);
		}
		removeExpItem.addActionListener(removeDataActionListener);

		ActionListener renameDataListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				data.renameData();
			}
		};
		for (ActionListener l : renameExpItem.getActionListeners())
		{
			renameExpItem.removeActionListener(l);
		}
		renameExpItem.addActionListener(renameDataListener);

	}

	public void getViewPopupMenu(JPopupMenu menu, final OCTViewDataHolder view)
	{
		getExperimentPopupMenu(menu, view.expData);
		menu.add(removeViewItem);
		menu.add(copyViewItem);
		menu.add(renameViewItem);
		menu.add(resetViewItem);

		// // Add suitabel Listners
		ActionListener resetViewActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				view.resetViewData();
				if (view == owner.currentView)
				{
					try
					{
						owner.updateView();
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};
		for (ActionListener l : resetViewItem.getActionListeners())
		{
			resetViewItem.removeActionListener(l);
		}
		resetViewItem.addActionListener(resetViewActionListener);

		ActionListener copyViewActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				view.expData.addView(view.getCopy());
			}
		};
		for (ActionListener l : copyViewItem.getActionListeners())
		{
			copyViewItem.removeActionListener(l);
		}
		copyViewItem.addActionListener(copyViewActionListener);

		ActionListener removeViewActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				view.expData.removeView(view);
			}
		};
		for (ActionListener l : removeViewItem.getActionListeners())
		{
			removeViewItem.removeActionListener(l);
		}
		removeViewItem.addActionListener(removeViewActionListener);
		ActionListener renameViewListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				view.renameView();
			}
		};
		for (ActionListener l : renameViewItem.getActionListeners())
		{
			renameViewItem.removeActionListener(l);
		}
		renameViewItem.addActionListener(renameViewListener);

	}

	public void getBasicToolMenu(JMenu toolMenu)
	{
		toolMenu.add(createNewDataSetItem);
	}

	public void getSlicetoolMenu(JMenu toolMenu)
	{
		getBasicToolMenu(toolMenu);
		toolMenu.add(new JSeparator());
		toolMenu.add(toggleNavigationWindow);
		toolMenu.add(toggleRenderQuality);
	}

	public void getVolumeToolMenu(JMenu toolMenu)
	{
		getBasicToolMenu(toolMenu);
	}

	public void getBasePopupMenu(JPopupMenu menu)
	{
		menu.add(loadSetItem);
		menu.add(saveSetItem);
		menu.add(closeSetItem);
		menu.add(new JSeparator());
		menu.add(addExpItem);
	}

	public void getSliceMenuBar(JMenuBar menu)
	{
		// Create slice Menu
		menu.add(fileMenu);

		getSlicetoolMenu(toolMenu);
		menu.add(toolMenu);
		menu.add(helpMenu);
	}

	public void getVolumeMenuBar(JMenuBar menu)
	{
		// Create Volume menu
		menu.add(fileMenu);
		getVolumeToolMenu(toolMenu);
		menu.add(toolMenu);
		menu.add(helpMenu);
	}

	public void getPlaneMenuBar(JMenuBar menu)
	{// Create Plane Menu
		menu.add(fileMenu);
		getBasicToolMenu(toolMenu);
		menu.add(toolMenu);
		menu.add(helpMenu);
	}
}
