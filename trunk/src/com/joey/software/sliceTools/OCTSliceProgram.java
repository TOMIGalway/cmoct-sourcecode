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
package com.joey.software.sliceTools;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.fileToolkit.ImageFileFilter;
import com.joey.software.fileToolkit.Utils;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.memoryToolkit.MemoryUsagePanel;
import com.joey.software.userinterface.OCTDataCreatorPanel;


/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class OCTSliceProgram extends JFrame
{
	String programTitle = "OCT Slice Analysis 1.0";

	private JMenuBar mainMenuBar;

	private JMenuItem renameMenuItem;

	private JMenu editMenu;

	private JMenuItem exportToMeasureMenuItem;

	private JMenuItem exportImagesMenuItem;

	private JMenuItem saveMenuItem;

	private JMenuItem openSetMenuItem;

	private JSeparator jSeparator1;

	private JMenu toolMenu;

	private JMenuItem loadDataMenuItem;

	private JMenuItem dateGenerateToolMenuItem;

	private JMenuItem closeDataItem;

	private JMenuItem exitMenuItem;

	private JMenu fileMenu;

	private JTabbedPane tabPane = new JTabbedPane();

	FileSelectionField loadRawPanel = new FileSelectionField();

	FileSelectionField loadPrvPanel = new FileSelectionField();

	final JTextField tabNameField = new JTextField(40);

	JPanel loadOCTDataPanel = null;

	StatusBarPanel statusBar = new StatusBarPanel();

	JFileChooser fileLoader = new JFileChooser();
	{
		fileLoader.setFileFilter(new FileFilter()
		{

			// Accept all directories and all gif, jpg, tiff, or png files.
			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
				{
					return true;
				}

				String extension = Utils.getExtension(f);
				if (extension != null)
				{
					if (extension.equals("uid"))
					{
						return true;
					} else
					{
						return false;
					}
				}

				return false;
			}

			// The description of this filter
			@Override
			public String getDescription()
			{
				return "OCT File (.uid)";
			}
		});
	}

	public OCTSliceProgram()
	{
		initGUI();
		setTitle(programTitle);
		setSize(800, 600);
		setLocationByPlatform(true);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				super.windowClosing(e);
				exitPressed();
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				// TODO Auto-generated method stub
				super.windowClosed(e);
				exitPressed();
			}

		});
	}

	public void closePressed()
	{
		if (tabPane.getComponentCount() > 0)
		{
			removeSlicePanel((OCTSliceViewer) tabPane.getSelectedComponent());
		}
	}

	public void createDataToolPressed()
	{
		OCTDataCreatorPanel creator = new OCTDataCreatorPanel();
		creator.setVisible(true);
	}

	public void loadDataPressed()
	{
		try
		{
			if (loadOCTDataPanel == null)
			{
				loadOCTDataPanel = new JPanel();
				loadOCTDataPanel.setPreferredSize(new java.awt.Dimension(589,
						78));
				loadOCTDataPanel.setLayout(null);

				// JButton setRawButton = new JButton("SET");
				// JButton setPrvButton = new JButton("SET");
				//
				// setRawButton.addActionListener(new ActionListener()
				// {
				// JFileChooser chooser = new JFileChooser();
				//
				// @Override
				// public void actionPerformed(ActionEvent e)
				// {
				// chooser.setMultiSelectionEnabled(false);
				// chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				//
				// chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// if (chooser.showSaveDialog(null) ==
				// JFileChooser.APPROVE_OPTION)
				// {
				// rawInputField.setText(chooser.getSelectedFile()
				// .toString());
				// }
				// }
				// });
				//
				// setPrvButton.addActionListener(new ActionListener()
				// {
				// JFileChooser chooser = new JFileChooser();
				//
				// @Override
				// public void actionPerformed(ActionEvent e)
				// {
				// chooser.setMultiSelectionEnabled(false);
				// chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				//
				// chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// if (chooser.showSaveDialog(null) ==
				// JFileChooser.APPROVE_OPTION)
				// {
				// prvInputField.setText(chooser.getSelectedFile()
				// .toString());
				// }
				// }
				// });
				//
				// JPanel rawDataPanel = new JPanel();
				// rawDataPanel.setLayout(new BorderLayout());
				// rawDataPanel.add(rawInputField, BorderLayout.CENTER);
				// rawDataPanel.add(setRawButton, BorderLayout.EAST);
				//
				// JPanel prvDataPanel = new JPanel();
				// prvDataPanel.setLayout(new BorderLayout());
				// prvDataPanel.add(prvInputField, BorderLayout.CENTER);
				// prvDataPanel.add(setPrvButton, BorderLayout.EAST);

				JPanel octNamePanel = new JPanel();

				SwingToolkit.createPanel(new String[]
				{ "Tab Name :" }, new JComponent[]
				{ tabNameField }, 60, 10, octNamePanel);

				loadRawPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
				loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);

				loadRawPanel.setLabelSize(60);
				loadPrvPanel.setLabelSize(60);

				loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);

				loadRawPanel.setLabelText("Raw : ");
				loadPrvPanel.setLabelText("Prv : ");

				loadOCTDataPanel.setLayout(new GridLayout(3, 1, 0, 2));
				loadOCTDataPanel.add(octNamePanel);
				loadOCTDataPanel.add(loadRawPanel);
				loadOCTDataPanel.add(loadPrvPanel);

			}

			// JFrame f = new JFrame();
			// f.setResizable(false);
			// f.setSize(450,100);
			// f.getContentPane().add(loadOCTDataPanel, BorderLayout.CENTER);
			// f.setVisible(true);
			int returnVal = JOptionPane
					.showConfirmDialog(this, loadOCTDataPanel, "Select OCT Data Files", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (returnVal == JOptionPane.OK_OPTION)
			{

				File rawFile = loadRawPanel.getFile();
				File prvFile = loadPrvPanel.getFile();

				NativeDataSet data = new NativeDataSet(rawFile, prvFile,
						statusBar);

				OCTSliceViewer slice = new OCTSliceViewer();
				slice.setOCTData(data);
				slice.unloadData();
				addSlicePanel(slice, tabNameField.getText());
				statusBar.reset();
				reloadPlane();
			}
		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Error Opening File");

		}
	}

	public void addSlicePanel(OCTSliceViewer slice, String name)
	{
		tabPane.addTab(name, slice);
	}

	public void removeSlicePanel(OCTSliceViewer slice)
	{
		tabPane.remove(slice);
		slice.data.unloadData();
		slice.unloadData();

	}

	public void exitPressed()
	{
		System.exit(0);
	}

	public void savePressed()
	{
		fileLoader.setMultiSelectionEnabled(false);
		fileLoader.setDialogType(JFileChooser.SAVE_DIALOG);
		fileLoader.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileLoader.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File f = fileLoader.getSelectedFile();

			try
			{
				saveDataFile(f);
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void openPressed()
	{
		fileLoader.setMultiSelectionEnabled(false);
		fileLoader.setDialogType(JFileChooser.OPEN_DIALOG);
		fileLoader.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileLoader.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File f = fileLoader.getSelectedFile();
			loadDataFile(f);
		}

	}

	public void loadDataFile(File f)
	{

		tabPane.removeAll();
		statusBar.setStatusMessage("Loading Data");

		class Loader implements Runnable
		{
			File f;

			public Loader(File f)
			{
				this.f = f;
				Thread t = new Thread(this);
				t.setPriority(Thread.MAX_PRIORITY - 2);
				t.start();
			}

			@Override
			public void run()
			{
				try
				{
					ObjectInputStream in = new ObjectInputStream(
							new FileInputStream(f));
					int count = in.readInt();
					statusBar.setMaximum(count);
					for (int i = 0; i < count; i++)
					{
						statusBar.setValue(i);
						OCTSliceViewer view = (OCTSliceViewer) in.readObject();
						view.updatePreviewPanel(true);
						view.unloadData();
						String title = (String) in.readObject();
						addSlicePanel(view, title);
					}
					statusBar.setStatusMessage("Done");
					in.close();
					statusBar.reset();
					reloadPlane();
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		;

		new Loader(f);

	}

	public void saveDataFile(File f) throws FileNotFoundException, IOException
	{

		try
		{
			if (!Utils.getExtension(f).equalsIgnoreCase("uid"))
			{
				f = new File(f.toString() + ".uid");
			}
		} catch (Exception e)
		{
			f = new File(f.toString() + ".uid");
		}
		statusBar.setStatusMessage("Saving Data");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		out.writeInt(tabPane.getTabCount());

		statusBar.setMaximum(tabPane.getTabCount());
		for (int i = 0; i < tabPane.getTabCount(); i++)
		{
			statusBar.setValue(i);
			OCTSliceViewer view = (OCTSliceViewer) tabPane.getComponentAt(i);
			String title = tabPane.getTitleAt(i);

			out.writeObject(view);
			out.writeObject(title);
		}
		statusBar.setStatusMessage("Done");
		out.close();

		statusBar.reset();
	}

	public void renamePressed()
	{
		JTextField input = new JTextField();
		if (JOptionPane
				.showConfirmDialog(this, input, "Enter New Name", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
		{
			tabPane.setTitleAt(tabPane.getSelectedIndex(), input.getText());
		}
	}

	public void exportImagesPressed()
	{
		JFileChooser chooser = new JFileChooser();

		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileFilter(new ImageFileFilter());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{

			for (int i = 0; i < tabPane.getTabCount(); i++)
			{
				File f = new File(chooser.getSelectedFile().toString() + "\\"
						+ tabPane.getTitleAt(i) + ".png");
				OCTSliceViewer view = (OCTSliceViewer) tabPane
						.getComponentAt(i);
				try
				{
					view.exportCurrentFrame(f);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void runMeasureTool()
	{
		Runtime runtime = Runtime.getRuntime();
		// JOptionPane.showMessageDialog(this, "STill got to Do");
		runtime.gc();
		runtime.gc();
		long data = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Data : " + data / 1024 / 1024);
	}

	public void unloadAllData()
	{
		for (int i = 0; i < tabPane.getComponentCount(); i++)
		{
			if (i != tabPane.getSelectedIndex())
			{
				if (tabPane.getComponent(i) instanceof OCTSliceViewer)
				{
					OCTSliceViewer plane = (OCTSliceViewer) tabPane
							.getComponent(i);
					plane.data.unloadData();
					plane.unloadData();

				}
			}
		}
	}

	public void reloadPlane()
	{
		if (tabPane.getSelectedComponent() instanceof OCTSliceViewer)
		{
			OCTSliceViewer plane = (OCTSliceViewer) tabPane
					.getSelectedComponent();
			plane.reloadData();
			plane.updateUI();
		}
	}

	private void initGUI()
	{
		tabPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				unloadAllData();
				reloadPlane();
			}
		});
		try
		{
			{
				mainMenuBar = new JMenuBar();
				setJMenuBar(mainMenuBar);
				{
					fileMenu = new JMenu();
					mainMenuBar.add(fileMenu);
					fileMenu.setText("File");
					{
						openSetMenuItem = new JMenuItem();
						fileMenu.add(openSetMenuItem);
						openSetMenuItem.setText("Open");
						openSetMenuItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								openPressed();
							}
						});
					}
					{
						saveMenuItem = new JMenuItem();
						fileMenu.add(saveMenuItem);
						saveMenuItem.setText("Save");
						saveMenuItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								savePressed();
							}
						});
					}
					{
						jSeparator1 = new JSeparator();
						fileMenu.add(jSeparator1);
					}
					{
						loadDataMenuItem = new JMenuItem();
						fileMenu.add(loadDataMenuItem);

						loadDataMenuItem.setText("Load Data");
						loadDataMenuItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								loadDataPressed();

							}
						});
					}
					{
						closeDataItem = new JMenuItem();
						fileMenu.add(closeDataItem);
						closeDataItem.setText("Close Data");

						closeDataItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								closePressed();

							}
						});
					}
					{
						exitMenuItem = new JMenuItem();
						fileMenu.add(new JSeparator());
						fileMenu.add(exitMenuItem);
						exitMenuItem.setText("Exit");
						exitMenuItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								exitPressed();

							}
						});
					}
				}
				{
					editMenu = new JMenu();
					mainMenuBar.add(editMenu);
					editMenu.setText("Edit");
					{
						renameMenuItem = new JMenuItem();
						editMenu.add(renameMenuItem);
						renameMenuItem.setText("Rename");
						renameMenuItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								renamePressed();
							}
						});
					}
				}
				{
					toolMenu = new JMenu();
					mainMenuBar.add(toolMenu);
					toolMenu.setText("Tools");
					{
						dateGenerateToolMenuItem = new JMenuItem();
						toolMenu.add(dateGenerateToolMenuItem);
						dateGenerateToolMenuItem.setText("Create Data Tool");
						dateGenerateToolMenuItem
								.addActionListener(new ActionListener()
								{

									@Override
									public void actionPerformed(ActionEvent e)
									{
										createDataToolPressed();

									}
								});
					}
					{
						exportImagesMenuItem = new JMenuItem();
						toolMenu.add(exportImagesMenuItem);
						exportImagesMenuItem.setText("Export Images");
						exportImagesMenuItem
								.addActionListener(new ActionListener()
								{

									@Override
									public void actionPerformed(ActionEvent e)
									{
										exportImagesPressed();
									}
								});
					}
					{
						exportToMeasureMenuItem = new JMenuItem();
						toolMenu.add(exportToMeasureMenuItem);
						exportToMeasureMenuItem.setText("Measure Tool");
						exportToMeasureMenuItem
								.addActionListener(new ActionListener()
								{

									@Override
									public void actionPerformed(ActionEvent e)
									{
										runMeasureTool();
									}
								});
					}
				}
			}
			{
				this.setSize(317, 209);
				getContentPane().add(tabPane, BorderLayout.CENTER);
				getContentPane().add(statusBar, BorderLayout.SOUTH);

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String input[]) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		OCTSliceProgram program = new OCTSliceProgram();

		JFrame f = MemoryUsagePanel.getMemoryUsagePanel(500, 100);
		f.setVisible(true);
		program.setVisible(true);

		// program.loadDataFile(new
		// File("C:\\Users\\joey.enfield\\Desktop\\Evolution of Micro
		// Needles\\NeedleData.uid"));
		// C:\Users\joey.enfield\Desktop\Evolution of Micro
		// Needles\NeedleData.uid
	}
}
