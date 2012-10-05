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

//
//import java.awt.BorderLayout;
//import java.awt.GridLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JSeparator;
//import javax.swing.JTabbedPane;
//import javax.swing.JTextField;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//import sliceTools.OCTSliceViewer;
//import userinterface.OCTDataViewPanel;
//import userinterface.VersionManager;
//import volumeTools.OCTVolumeDivider;
//
//import DataToolkit.OCTDataSet;
//import framesToolkit.FileSelectionField;
//import framesToolkit.FrameFactroy;
//import framesToolkit.StatusBarPanel;
//import framesToolkit.SwingToolkit;
//
public class OCTMain
{
}
// private static final long serialVersionUID = VersionManager
// .getCurrentVersion();
//
// OwnerInterface owner = null;
//
// public static final String TITLE = "OCT Analysis 1.0";
//
// JTabbedPane panelData = new JTabbedPane();
//
// OCTVolumeDivider volume = new OCTVolumeDivider();
//
// OCTSliceViewer slice = new OCTSliceViewer();
//
// MenuControler control = new MenuControler(this);
//
// StatusBarPanel status = new StatusBarPanel();
//
// FileSelectionField loadRawPanel = new FileSelectionField();
//
// FileSelectionField loadPrvPanel = new FileSelectionField();
//
// FileSelectionField dataSetPanel = new FileSelectionField();
//
// JTextField tabNameField = new JTextField(40);
//
// JPanel loadOCTDataPanel = null;
//
// int lastIndex = -1;
//
// PanelListner viewListner = new PanelListner(this);
//
// public static void main(String input[]) throws Exception
// {
// // MemoryUsagePanel.getMemoryUsagePanel(1000, 100).setVisible(true);
// final JFrame f = FrameFactroy.getFrame(false);
//
// OwnerInterface owner = new OwnerInterface()
// {
//
// @Override
// public void setJMenuBar(JMenuBar menu)
// {
// f.setJMenuBar(menu);
// }
//
// };
//
// OCTMain program = new OCTMain(owner);
//
// f.setTitle(program.TITLE);
// f.getContentPane().setLayout(new BorderLayout());
// f.getContentPane().add(program, BorderLayout.CENTER);
// f.setSize(1024, 800);
// f.setVisible(true);
//
// program.addDataSet();
// }
//
// public OCTMain(OwnerInterface owner)
// {
// setOwner(owner);
// createJPanel();
// updatePanel();
// }
//
// public void addDataSet()
// {
// FileSelectionField loadRawPanel = new FileSelectionField();
//
// FileSelectionField loadPrvPanel = new FileSelectionField();
//
// FileSelectionField dataSetPanel = new FileSelectionField();
//
// JTextField tabNameField = new JTextField(40);
//
// JPanel loadOCTDataPanel = null;
// try
// {
// if (loadOCTDataPanel == null)
// {
// loadOCTDataPanel = new JPanel();
// loadOCTDataPanel.setPreferredSize(new java.awt.Dimension(589,
// 78));
// loadOCTDataPanel.setLayout(null);
//
// JPanel octNamePanel = new JPanel();
//
// SwingToolkit.createPanel(new String[]
// { "Tab Name :" }, new JComponent[]
// { tabNameField }, 60, 10, octNamePanel);
//
// loadRawPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
// loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
//
// loadRawPanel.setLabelSize(60);
// loadPrvPanel.setLabelSize(60);
//
// loadPrvPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
//
// loadRawPanel.setLabelText("Raw : ");
// loadPrvPanel.setLabelText("Prv : ");
//
// loadOCTDataPanel.setLayout(new GridLayout(3, 1, 0, 2));
// loadOCTDataPanel.add(octNamePanel);
// loadOCTDataPanel.add(loadRawPanel);
// loadOCTDataPanel.add(loadPrvPanel);
//
// }
//
// // JFrame f = new JFrame();
// // f.setResizable(false);
// // f.setSize(450,100);
// // f.getContentPane().add(loadOCTDataPanel, BorderLayout.CENTER);
// // f.setVisible(true);
// int returnVal = JOptionPane
// .showConfirmDialog(this, loadOCTDataPanel, "Select OCT Data Files",
// JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//
// if (returnVal == JOptionPane.OK_OPTION)
// {
//
// File rawFile = loadRawPanel.getFile();
// File prvFile = loadPrvPanel.getFile();
//
// OCTDataSet data = new OCTDataSet(rawFile, prvFile, status);
// addDataSet(data, tabNameField.getText());
// status.reset();
// updatePanel();
// }
// } catch (Exception e)
// {
// JOptionPane.showMessageDialog(this, "Error Opening File");
// e.printStackTrace();
//
// }
// }
//
// public void createJPanel()
// {
// setLayout(new BorderLayout());
// add(panelData, BorderLayout.CENTER);
// add(status, BorderLayout.SOUTH);
//
// panelData.addChangeListener(new ChangeListener()
// {
//
// @Override
// public void stateChanged(ChangeEvent e)
// {
// updatePanel();
// }
// });
// }
//
// public void addDataSet(OCTViewDataHolder panel)
// {
// panel.setView(slice, volume);
// panelData.addTab(panel.name, panel);
//	
// }
//
// public void addDataSet(OCTDataSet data, String name) throws IOException
// {
// OCTViewDataHolder panel = new OCTViewDataHolder(slice, volume, data);
// panel.setName(name);
// addDataSet(panel);
// }
//
// public void updatePanel()
// {
//
// try
// {
// if (panelData.getSelectedIndex() == -1)
// {
// lastIndex = -1;
// setJMenuBar(control.getPlaneMenuBar());
// } else
// {
// ((OCTViewDataHolder) panelData.getSelectedComponent())
// .updatePanel();
// }
// } catch (Exception e)
// {
// e.printStackTrace();
// }
//
// }
//
// public void setOwner(OwnerInterface owner)
// {
// this.owner = owner;
// }
//
// public void updateJMenuBar()
// {
// OCTViewDataHolder newPanel = ((OCTViewDataHolder) panelData
// .getSelectedComponent());
// if (newPanel.tabPane.getSelectedIndex() == 0)
// {
// setJMenuBar(control.getSliceMenuBar());
// } else
// {
// setJMenuBar(control.getVolumeMenuBar());
// }
// }
//
// public void setJMenuBar(JMenuBar menu)
// {
// if (owner != null)
// {
//
// owner.setJMenuBar(menu);
// }
// }
//
// public void exitPressed()
// {
// System.exit(0);
// }
//
// public void saveSetPressed() throws FileNotFoundException, IOException
// {
// dataSetPanel.setType(FileSelectionField.TYPE_SAVE_FILE);
// if (!dataSetPanel.getUserChoice())
// {
// return;
// }
// ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
// dataSetPanel.getFile()));
//
// out.writeInt(panelData.getSelectedIndex());
// out.writeInt(panelData.getTabCount());
//
// for (int i = 0; i < panelData.getTabCount(); i++)
// {
// OCTViewDataHolder p = (OCTViewDataHolder) panelData.getComponentAt(i);
// out.writeObject(p);
// }
// out.close();
// }
//
// public void loadSetPressed() throws FileNotFoundException, IOException,
// ClassNotFoundException
// {
// dataSetPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
// if (!dataSetPanel.getUserChoice())
// {
// return;
// }
//
// ObjectInputStream in = new ObjectInputStream(new FileInputStream(
// dataSetPanel.getFile()));
// int index = in.readInt();
// int size = in.readInt();
//
// for (int i = 0; i < size; i++)
// {
//
// OCTViewDataHolder p = (OCTViewDataHolder) in.readObject();
// addDataSet(p);
// }
//
// panelData.setSelectedIndex(index);
// updatePanel();
// }
//
// }
//
// /**
// * This class is used to ensure that the correct menu bar will be displayed in
// * the oct program
// *
// * @author Joey.Enfield
// *
// */
// class PanelListner implements ChangeListener
// {
// OCTMain owner;
//
// OCTViewDataHolder child = null;
//
// public PanelListner(OCTMain owner)
// {
// this.owner = owner;
// }
//
// public void setView(OCTViewDataHolder view)
// {
//
// if (child != null)
// {
// child.tabPane.removeChangeListener(this);
// }
// child = view;
// child.tabPane.addChangeListener(this);
// }
//
// @Override
// public void stateChanged(ChangeEvent e)
// {
// owner.updateJMenuBar();
// }
// }
//
// interface OwnerInterface
// {
// public abstract void setJMenuBar(JMenuBar menu);
// }
