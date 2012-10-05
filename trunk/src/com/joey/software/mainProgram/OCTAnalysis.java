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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.joey.software.AutoSaveTool.DataSaveTool;
import com.joey.software.DataToolkit.ArrayDataSet;
import com.joey.software.DataToolkit.IMGDataSet;
import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.DataToolkit.OCTDataSetPanel;
import com.joey.software.DataToolkit.TiffDataSet;
import com.joey.software.DivertSystemOut.RedirectedFrame;
import com.joey.software.LAFTools.EditableLAFControler;
import com.joey.software.Launcher.SettingsLauncher;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.sliceTools.OCTSliceViewer;
import com.joey.software.sliceTools.OCTSliceViewerDataHolder;
import com.joey.software.userinterface.LoadOCTDatasetPanel;
import com.joey.software.userinterface.VersionManager;
import com.joey.software.volumeTools.OCTVolumeDivider;
import com.joey.software.volumeTools.OCTVolumeDividerDataHolder;


public class OCTAnalysis extends JPanel implements TreeSelectionListener,
		FileDrop.Listener
{
	RedirectedFrame redirect;

	OCTVolumeDivider volumePanel = new OCTVolumeDivider();

	OCTSliceViewer slicePanel = new OCTSliceViewer();

	OCTDataSetPanel octDataSetPanel = new OCTDataSetPanel();

	JTabbedPane viewTabs = new JTabbedPane(SwingConstants.TOP);

	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Experiments");

	DefaultTreeModel model = new DefaultTreeModel(root);

	JTree expTree = new JTree(model);

	TreeMouseListner treeMouseListner = new TreeMouseListner(this);

	private ArrayList<OCTExperimentData> expData = new ArrayList<OCTExperimentData>();

	FileSelectionField dataSetPanel = new FileSelectionField();

	LoadOCTDatasetPanel loadOCTDataPanel = null;

	private StatusBarPanel status = new StatusBarPanel();

	OCTExperimentData currentExperiment = null;

	OCTViewDataHolder currentView = null;

	JMenuBar menu = new JMenuBar();

	MenuControler menuControl = new MenuControler(this);

	DataSaveTool autoSaveTool = new DataSaveTool(this, 5, 60 * 1000 * 3);

	public static BufferedImage icon = null;
	{
		try
		{
			loadIcon();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long vRam = 0;

	EditableLAFControler cont = null;

	// JButton addViewButton = new JButton("+ View");

	// JButton removeViewButton = new JButton("- View");

	public static void createTestSet(String raw, String prv, int sizeX, int sizeY, int sizeZ, int boxX, int boxY, int boxZ)
	{

	}

	public void changeLAF() throws IOException
	{
		if (cont == null)
		{
			cont = new EditableLAFControler(SettingsLauncher.getPath()
					+ "LAF\\");
		}
		cont.changeLAF(this);
	}

	public void logData(boolean show)
	{
		if (redirect == null)
		{
			redirect = new RedirectedFrame(true, true, SettingsLauncher
					.getPath()
					+ "errorLog.txt", 300, 300, WindowConstants.HIDE_ON_CLOSE);

		}
		redirect.setVisible(show);
	}

	public OCTAnalysis()
	{
		createJPanel();
		try
		{
			loadIcon();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileDrop dropper = new FileDrop(this, this);
	}

	@Override
	public void filesDropped(File[] files)
	{

		for (int i = 0; i < files.length; i++)
		{
			try
			{
				File f = files[i];
				String[] parts = FileOperations.splitFile(f);

				if (parts[2].equalsIgnoreCase("drgwsp"))
				{
					try
					{
						loadSet(f);
					} catch (Exception e)
					{
						JOptionPane
								.showMessageDialog(this, "There was an error loading the dropped workspace : \n"
										+ e);
						e.printStackTrace();
					}
				} else if (parts[2].equalsIgnoreCase("drgprv"))
				{
					File prv = f;
					File raw = FileOperations.renameFileType(f, "drgraw");

					NativeDataSet data = new NativeDataSet(raw, prv);

					OCTExperimentData exp = new OCTExperimentData(this, data,
							parts[1]);
					exp.addView();
					addExperiment(exp);
				}
				else if (parts[2].equalsIgnoreCase("drgraw"))
				{
					File raw = f;
					File prv = FileOperations.renameFileType(f, "drgprv");

					NativeDataSet data = new NativeDataSet(raw, prv);

					OCTExperimentData exp = new OCTExperimentData(this, data,
							parts[1]);
					exp.addView();

					this.addExperiment(exp);

				}
				else if (parts[2].equalsIgnoreCase("img"))
				{
					IMGDataSet data = new IMGDataSet(f);
					OCTExperimentData exp = new OCTExperimentData(this, data,
							parts[1]);
					exp.addView();
					this.addExperiment(exp);
				}
			} catch (Exception e)
			{
				JOptionPane
						.showMessageDialog(null, "There was an error loading the file", "Error", JOptionPane.ERROR_MESSAGE);
				System.out.println("Trying to load files");
				e.printStackTrace();
			}
		}
	}

	public void setVideoMemory(long vram)
	{
		this.vRam = vram;
		volumePanel.getVolumeViewer().getVolSize().setVideoMemorySize(vram);
	}

	public static void loadIcon() throws IOException
	{
		icon = ImageIO.read(new File(SettingsLauncher.getPath()
				+ "icon\\icon-mainprogram.png"));
	}

	public void createJPanel()
	{
		// addViewButton.addActionListener(new ActionListener()
		// {
		//
		// @Override
		// public void actionPerformed(ActionEvent e)
		// {
		// if (currentExperiment != null)
		// {
		// currentExperiment.addView();
		// }
		//
		// }
		// });
		//
		// JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		// buttonPanel.add(addViewButton);
		// buttonPanel.add(removeViewButton);

		JPanel viewHolder = new JPanel(new BorderLayout());

		viewTabs.addTab("2D", slicePanel);
		viewTabs.addTab("3D", volumePanel);
		viewTabs.addTab("Experiment Details", octDataSetPanel);

		// Where the tree is initialized:
		expTree.getSelectionModel()
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		expTree.setCellRenderer(new TreeRender(this));
		expTree.setCellEditor(new TreeEditor(expTree));
		expTree.setEditable(true);
		// Listen for when the selection changes.
		expTree.addTreeSelectionListener(this);
		expTree.addMouseListener(treeMouseListner);
		expTree.expandRow(0);

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(expTree);
		add(scroll, BorderLayout.WEST);

		JPanel expHolder = new JPanel(new BorderLayout());
		expHolder.add(scroll, BorderLayout.CENTER);
		expHolder.setMinimumSize(new Dimension(135, 0));
		// expHolder.add(buttonPanel, BorderLayout.SOUTH);

		viewHolder.add(viewTabs);
		viewHolder.setBorder(BorderFactory.createTitledBorder(""));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(135);
		split.setOneTouchExpandable(true);
		split.setLeftComponent(expHolder);
		split.setRightComponent(viewHolder);

		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		add(getStatusBar(), BorderLayout.SOUTH);

		slicePanel.setStatus(status);
		volumePanel.setStatus(status);
	}

	public void saveCSVExperimentMeasureData(final String fileOut)
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					unloadExperimentData();
					System.out.println("Starting saving CSV Data");
					String output = "";

					getStatusBar().setStatusMessage("Saving Data");
					getStatusBar().setMaximum(expData.size());
					getStatusBar().setValue(0);

					int expCount = expData.size();
					for (OCTExperimentData exp : expData)
					{
						exp.getData().reloadData();
						getStatusBar().setValue(getStatusBar().getValue() + 1);
						expCount--;

						String expOut = "Title :," + exp.getTitle() + "\n";
						expOut += "Size X : ,"
								+ (exp.getData().getPixelSizeX() * exp
										.getData().getSizeDataX()) + "\n";
						expOut += "Size Y : ,"
								+ (exp.getData().getPixelSizeY() * exp
										.getData().getSizeDataY()) + "\n";
						expOut += "Size Z : ,"
								+ (exp.getData().getPixelSizeZ() * exp
										.getData().getSizeDataZ()) + "\n";

						int viewCount = exp.getViews().size();

						for (OCTViewDataHolder view : exp.getViews())
						{
							viewCount--;
							getStatusBar().setStatusMessage("Saving Exp["
									+ (expData.size() - expCount) + " of "
									+ expData.size() + "] View["
									+ (exp.getViews().size() - viewCount)
									+ " of " + exp.getViews().size() + "]");

							expOut += "\n#############################################\n";
							expOut += "View Name : ," + view.getName() + "\n";

							Point2D.Double p1;
							Point2D.Double p2;
							double scaleX;
							double scaleY;
							double dx;
							double dy;
							double length;
							// X data
							{
								p1 = view.getSliceData().p1x;
								p2 = view.getSliceData().p2x;

								if (view.getSliceData().isRenderHighRes())
								{
									scaleX = exp.getData().getPixelSizeZ();
									scaleY = exp.getData().getPixelSizeY();
								} else
								{
									scaleX = exp.getData()
											.getPreviewPixelSizeZ();
									scaleY = exp.getData()
											.getPreviewPixelSizeY();
								}

								dx = p1.x - p2.x;
								dy = p1.y - p2.y;

								dx *= scaleX;
								dy *= scaleY;

								dx = Math.abs(dx);
								dy = Math.abs(dy);

								length = Math.sqrt(dx * dx + dy * dy);
								expOut += "X Plane\n";
								expOut += "DX : ," + dx + "\n";
								expOut += "DY : ," + dy + "\n";
								expOut += "Length : ," + length + "\n";
							}

							{
								p1 = view.getSliceData().p1y;
								p2 = view.getSliceData().p2y;

								if (view.getSliceData().isRenderHighRes())
								{
									scaleX = exp.getData().getPixelSizeX();
									scaleY = exp.getData().getPixelSizeZ();
								} else
								{
									scaleX = exp.getData()
											.getPreviewPixelSizeX();
									scaleY = exp.getData()
											.getPreviewPixelSizeZ();
								}

								dx = p1.x - p2.x;
								dy = p1.y - p2.y;

								dx *= scaleX;
								dy *= scaleY;

								dx = Math.abs(dx);
								dy = Math.abs(dy);

								length = Math.sqrt(dx * dx + dy * dy);
								expOut += "Y Plane\n";
								expOut += "DX : ," + dx + "\n";
								expOut += "DY : ," + dy + "\n";
								expOut += "Length : ," + length + "\n";
							}

							{
								p1 = view.getSliceData().p1z;
								p2 = view.getSliceData().p2z;

								if (view.getSliceData().isRenderHighRes())
								{
									scaleX = exp.getData().getPixelSizeX();
									scaleY = exp.getData().getPixelSizeY();
								} else
								{
									scaleX = exp.getData()
											.getPreviewPixelSizeX();
									scaleY = exp.getData()
											.getPreviewPixelSizeY();
								}

								dx = p1.x - p2.x;
								dy = p1.y - p2.y;

								dx *= scaleX;
								dy *= scaleY;

								dx = Math.abs(dx);
								dy = Math.abs(dy);

								length = Math.sqrt(dx * dx + dy * dy);
								expOut += "Z Plane\n";
								expOut += "DX : ," + dx + "\n";
								expOut += "DY : ," + dy + "\n";
								expOut += "Length : ," + length + "\n";
							}
						}

						expOut = CSVFileToolkit.padData(expOut);
						expOut = CSVFileToolkit
								.addAfterLines(expOut, ",#####,");
						output = CSVFileToolkit.joinDataRight(output, expOut);
						exp.getData().unloadData();
					}

					getStatusBar().setStatusMessage("Saving to file");
					PrintWriter out = new PrintWriter(fileOut);
					output = CSVFileToolkit.getTrimmedData(output);
					out.write(output);
					out.close();

					getStatusBar().reset();
					// CSVFileToolkit.printCSVData(output);

				} catch (Exception e)
				{

				}
				reloadExperimentData();
			}
		});
		t.start();
	}

	public JMenuBar getJMenuBar()
	{
		return menu;
	}

	public void updateJMenuBar()
	{
		menu.removeAll();
		if (viewTabs.getSelectedIndex() == 0)
		{
			menuControl.getSliceMenuBar(menu);
		} else if (viewTabs.getSelectedIndex() == 1)
		{
			menuControl.getVolumeMenuBar(menu);
		}
		menuControl.getPlaneMenuBar(menu);
	}

	public void updateView() throws IOException
	{
		if (currentView != null)
		{

			currentView.setSliceData(slicePanel);

			currentView.setVolumeData(volumePanel);

			octDataSetPanel.setOCTData(currentView.expData.getData());

		} else
		{

			(new OCTVolumeDividerDataHolder()).setData(volumePanel);

			(new OCTSliceViewerDataHolder()).setData(slicePanel);

		}
		viewTabs.validate();
		slicePanel.updateMeasurement();

	}
	
	public void setData(byte[][][] data)
	{
		setData(data, "Data");
	}
	public void setData(byte[][][] data, String name)
	{
		try
		{
			OCTExperimentData exp = new OCTExperimentData(this, new ArrayDataSet(data), name);
			exp.addView();
			addExperiment(exp);
			
			currentExperiment = exp;
			currentView = exp.getViews().get(0);
			updateView();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addExperiment()
	{

		try
		{
			if (loadOCTDataPanel == null)
			{
				loadOCTDataPanel = new LoadOCTDatasetPanel();
			}

			loadOCTDataPanel.resetFields();
			int returnVal = JOptionPane
					.showConfirmDialog(this, loadOCTDataPanel, "Select OCT Data Files", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (returnVal == JOptionPane.OK_OPTION)
			{
				addExperiment(loadOCTDataPanel.getOCTExperiment(this));
				getStatusBar().reset();
			}
		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Error Opening File");
			e.printStackTrace();

		}

	}

	public void addExperiment(OCTExperimentData data)
	{
		getExpData().add(data);
		getModel().insertNodeInto(data.baseNode, root, root.getChildCount());
		expTree.expandRow(0);
	}

	public void removeExperiment(OCTExperimentData data)
	{
		expData.remove(data);
		getModel().removeNodeFromParent(data.baseNode);
		try
		{
			if (currentExperiment == data)
			{
				currentExperiment = null;
				currentView = null;
				updateView();
			}
			if (expData.size() == 0)
			{
				currentExperiment = null;
				currentView = null;
				updateView();
			}
		} catch (Exception e)
		{

		}

	}

	public DefaultTreeModel getModel()
	{
		return model;
	}

	public void reloadTree()
	{
		root.removeAllChildren();
		for (OCTExperimentData p : getExpData())
		{
			p.setNodes(root);
		}
		model.reload(root);

		expTree.validate();
	}

	public void saveCurrentExperimentData()
	{
		if (currentView == null)
		{
			return;
		}
		currentView.getSliceData(slicePanel);
		currentView.getVolumeData(volumePanel);
	}

	public void unloadExperimentData()
	{
		if (currentExperiment != null)
		{
			currentExperiment.getData().unloadData();
			slicePanel.unloadData();
			volumePanel.unloadData();
			System.gc();
		}
	}

	public void reloadExperimentData()
	{
		try
		{
			if (currentExperiment != null)
			{
				currentExperiment.getData().reloadData(getStatusBar());

				// System.out.println("Current Data Size (OCTAnalysis : reloadExperiment)");
				// System.out.println(currentExperiment.getData().getSizeDataX());
				// System.out.println(currentExperiment.getData().getSizeDataY());
				// System.out.println(currentExperiment.getData().getSizeDataZ());
				// System.out.println("Current Preview Size");
				// System.out.println(currentExperiment.getData().getPreviewSizeX());
				// System.out.println(currentExperiment.getData().getPreviewSizeY());
				// System.out.println(currentExperiment.getData().getPreviewSizeZ());

				volumePanel.reloadData();

				// slicePanel.reloadData();
				slicePanel.updateMeasurement();
			}
		} catch (OutOfMemoryError e)
		{
			String message = "";

			if (currentExperiment.getData() instanceof TiffDataSet)
			{
				message = "There was not enough memory to fully load the dataset.\n";
				message += "Please convert the data to 'Native' format to reduce memory requirments";
			} else
			{
				message = "There was not enough memory to fully load the dataset.\n";
				message += "Please reduce the preview file size to reduce memory requirments";
			}
			JOptionPane
					.showMessageDialog(null, message, "System has run out of memory", JOptionPane.ERROR_MESSAGE);
			System.out.println("error reloading dataset");
			e.printStackTrace();
		} catch (Exception e)
		{
			JOptionPane
					.showMessageDialog(null, "There has been an internal error loading the data", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("error reloading dataset");
			e.printStackTrace();
		}
	}

	public void exitPressed()
	{
		System.exit(0);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{

		// Returns the last path element of the selection.
		// This method is useful only when the selection model allows a
		// single
		// selection.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) expTree
				.getLastSelectedPathComponent();

		expTree.repaint();
		if (node == null)
			// Nothing is selected.
			return;

		Object nodeData = node.getUserObject();

		if (currentView != null)
		{
			saveCurrentExperimentData();
		}

		if (nodeData instanceof OCTExperimentData)
		{
			if (currentExperiment != ((OCTExperimentData) nodeData))
			{
				unloadExperimentData();
				currentExperiment = (OCTExperimentData) nodeData;
				currentView = currentExperiment.getViews().get(0);
				reloadExperimentData();
			}

		} else if (nodeData instanceof OCTViewDataHolder)
		{
			boolean reload = false;
			currentView = (OCTViewDataHolder) nodeData;
			if (currentExperiment != currentView.expData)
			{
				unloadExperimentData();
				reload = true;
			}

			currentExperiment = currentView.expData;
			if (reload)
			{

				reloadExperimentData();
			}
		}

		try
		{
			updateView();
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void saveSetPressed() throws FileNotFoundException, IOException
	{
		saveCurrentExperimentData();
		dataSetPanel.setType(FileSelectionField.TYPE_SAVE_FILE);
		dataSetPanel.setExtensions(new String[]
		{ "drgWsp:Dragon Workspace(.drgWsp)" }, true, true);
		if (!dataSetPanel.getUserChoice())
		{
			return;
		}

		if (dataSetPanel.getFile().exists())
		{
			if (JOptionPane
					.showConfirmDialog(null, "The file already exist, Overwrite?", "Confirm Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION)
			{
				return;
			}
		}
		VersionManager.saveData(this, dataSetPanel.getFile(), getStatusBar());
	}

	public void loadSetPressed() throws FileNotFoundException, IOException,
			ClassNotFoundException
	{
		dataSetPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
		dataSetPanel.setExtensions(new String[]
		{ "drgWsp:Dragon Workspace(.drgWsp)" }, true, true);
		if (!dataSetPanel.getUserChoice())
		{
			return;
		}
		loadSet(dataSetPanel.getFile());
	}

	public void loadSet(File f) throws FileNotFoundException, IOException,
			ClassNotFoundException
	{
		while (expData.size() > 0)
		{
			removeExperiment(expData.get(0));
		}
		currentExperiment = null;
		currentView = null;
		updateView();

		VersionManager.loadData(this, f, getStatusBar());

		getTopLevelAncestor().validate();
	}

	public void setExpData(ArrayList<OCTExperimentData> expData)
	{
		this.expData = expData;
	}

	public ArrayList<OCTExperimentData> getExpData()
	{

		return expData;
	}

	public OCTViewDataHolder getCurrentView()
	{
		return currentView;
	}

	public StatusBarPanel getStatusBar()
	{
		return status;
	}

	public void clearAllData() throws IOException
	{
		for(OCTExperimentData data : expData)
		{
			data.getData().unloadData();
			data.setData(new ArrayDataSet(new byte[1][1][1]));
		}
		expData.clear();
		currentExperiment = null;
		currentView = null;
	}

}

class TreeRender extends DefaultTreeCellRenderer
{
	Color expUnSelColor = Color.RED;

	Color expSelColor = Color.PINK;

	Color viewUnSelColor = Color.GREEN;

	Color viewSelColor = Color.CYAN;

	Color normUnSelColor;

	Color normSelColor;

	OCTAnalysis owner;

	public TreeRender(OCTAnalysis owner)
	{
		this.owner = owner;
		normUnSelColor = getBackgroundNonSelectionColor();
		normSelColor = getBackgroundSelectionColor();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{

		if (value instanceof DefaultMutableTreeNode)
		{
			Object dat = ((DefaultMutableTreeNode) value).getUserObject();
			if (dat == owner.currentExperiment)
			{
				setBackgroundNonSelectionColor(expUnSelColor);
				setBackgroundSelectionColor(expSelColor);
			} else

			if (dat == owner.currentView)
			{
				setBackgroundNonSelectionColor(viewUnSelColor);
				setBackgroundSelectionColor(viewSelColor);
			} else
			{
				setBackgroundNonSelectionColor(normUnSelColor);
				setBackgroundSelectionColor(normSelColor);
			}
		} else
		{
			setBackgroundNonSelectionColor(normUnSelColor);
			setBackgroundSelectionColor(normSelColor);
		}
		Component comp = super
				.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		return comp;
	}
}

class TreeEditor extends AbstractCellEditor implements TreeCellEditor
{
	JTree tree;

	public TreeEditor(JTree tree)
	{
		this.tree = tree;
	}

	@Override
	public Component getTreeCellEditorComponent(final JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
	{

		if (value instanceof DefaultMutableTreeNode)
		{
			Object obj = ((DefaultMutableTreeNode) value).getUserObject();
			if (obj instanceof OCTViewDataHolder)
			{

				final OCTViewDataHolder data = (OCTViewDataHolder) obj;

				JPanel p = new JPanel(new BorderLayout());

				final JTextField field = new JTextField(10);
				field.setText(data.getName());
				final String org = data.getName();
				field.addFocusListener(new FocusListener()
				{

					@Override
					public void focusGained(FocusEvent e)
					{

					}

					@Override
					public void focusLost(FocusEvent e)
					{
						data.setName(field.getText());

					}
				});

				p.add(field);
				return p;
			}
		}
		return new JPanel();
	}

	@Override
	public boolean isCellEditable(EventObject event)
	{
		if (event instanceof MouseEvent)
		{
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree
					.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());

			if (path != null)
			{
				// Only allow to edit on second click.
				Object node = path.getLastPathComponent();
				if (tree.getSelectionPath() != null)
				{
					if (tree.getSelectionPath().getLastPathComponent() != node)
					{
						return false;
					}
				}
				if ((node != null) && (node instanceof DefaultMutableTreeNode))
				{

					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
					if (treeNode.getUserObject() instanceof OCTViewDataHolder)
					{
						return true;
					} else if (treeNode.getUserObject() instanceof OCTExperimentData)
					{
						return false;
					}
				}

			}
		}
		return false;
	}

	@Override
	public Object getCellEditorValue()
	{
		return null;
	}
}

class TreeMouseListner extends MouseAdapter
{
	OCTAnalysis owner;

	JPopupMenu menu = new JPopupMenu("Menu");

	public TreeMouseListner(OCTAnalysis owner)
	{
		this.owner = owner;

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			JTree expTree = owner.expTree;
			if (e.getSource() == expTree)
			{
				TreePath path = expTree.getPathForLocation(e.getPoint().x, e
						.getPoint().y);

				JPopupMenu menu = getMenu(path);
				if (menu == null)
				{
					return;
				}
				menu.show(expTree, e.getPoint().x, e.getPoint().y);

			}
		}

	}

	public JPopupMenu getMenu(TreePath path)
	{
		menu.removeAll();

		if (path == null)
		{
			owner.menuControl.getBasePopupMenu(menu);
		} else
		{
			if (path.getLastPathComponent() instanceof DefaultMutableTreeNode)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();

				if (node.getUserObject() instanceof OCTExperimentData)
				{
					owner.menuControl
							.getExperimentPopupMenu(menu, (OCTExperimentData) node
									.getUserObject());
				} else if (node.getUserObject() instanceof OCTViewDataHolder)
				{
					owner.menuControl
							.getViewPopupMenu(menu, (OCTViewDataHolder) node
									.getUserObject());
				}
			}
		}

		return menu;
	}
}
