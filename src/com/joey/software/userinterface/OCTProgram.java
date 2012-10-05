package com.joey.software.userinterface;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;


public class OCTProgram extends JPanel
{
	private static final long serialVersionUID = VersionManager
			.getCurrentVersion();

	OwnerInterface owner = null;

	public static final String TITLE = "OCT Analysis 1.0";

	JTabbedPane panelData = new JTabbedPane();

	MenuControler control = new MenuControler(this);

	StatusBarPanel status = new StatusBarPanel();

	FileSelectionField loadRawPanel = new FileSelectionField();

	FileSelectionField loadPrvPanel = new FileSelectionField();

	FileSelectionField dataSetPanel = new FileSelectionField();

	JTextField tabNameField = new JTextField(40);

	JPanel loadOCTDataPanel = null;

	int lastIndex = -1;

	PanelListner viewListner = new PanelListner(this);

	// DataSaveTool tool = new DataSaveTool(this, 5, 1*60*1000);

	public OCTProgram(OwnerInterface owner)
	{
		setOwner(owner);
		createJPanel();
		updatePanel();

	}

	public void addDataSet()
	{
		try
		{
			if (loadOCTDataPanel == null)
			{
				loadOCTDataPanel = new JPanel();
				loadOCTDataPanel.setPreferredSize(new java.awt.Dimension(589,
						78));
				loadOCTDataPanel.setLayout(null);

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

				NativeDataSet data = new NativeDataSet(rawFile, prvFile, status);
				addDataSet(data, tabNameField.getText());
				status.reset();
				updatePanel();
			}
		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Error Opening File");
			e.printStackTrace();

		}
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		add(panelData, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);

		panelData.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updatePanel();
			}
		});
	}

	public void addDataSet(OCTDataViewPanel panel)
	{
		panelData.addTab(panel.name, panel);
		panel.unloadAllData();
	}

	public void addDataSet(NativeDataSet data, String name) throws IOException
	{
		OCTDataViewPanel panel = new OCTDataViewPanel(data, name);
		addDataSet(panel);
	}

	public void updatePanel()
	{

		if (panelData.getSelectedIndex() == -1)
		{
			lastIndex = -1;
			setJMenuBar(control.getPlaneMenuBar());

		} else
		{
			if (lastIndex != -1)
			{
				// Process old compoent
				OCTDataViewPanel oldPanel = (OCTDataViewPanel) panelData
						.getComponent(lastIndex);
				oldPanel.unloadAllData();
			}

			// Add New Compoent
			lastIndex = panelData.getSelectedIndex();
			OCTDataViewPanel newPanel = ((OCTDataViewPanel) panelData
					.getSelectedComponent());
			newPanel.reloadAllData();

			int oldVal;
			int newVal;
			if (newPanel.tabPane.getSelectedIndex() == 0)
			{
				oldVal = 0;
				newVal = 1;
			} else
			{
				oldVal = 1;
				newVal = 0;
			}

			newPanel.tabPane.setSelectedIndex(newVal);
			newPanel.repaint();
			newPanel.tabPane.setSelectedIndex(oldVal);
			newPanel.repaint();

			viewListner.setView(newPanel);
			updateJMenuBar();

		}

	}

	public void setOwner(OwnerInterface owner)
	{
		this.owner = owner;
	}

	public void updateJMenuBar()
	{
		OCTDataViewPanel newPanel = ((OCTDataViewPanel) panelData
				.getSelectedComponent());
		if (newPanel.tabPane.getSelectedIndex() == 0)
		{
			setJMenuBar(control.getSliceMenuBar());
		} else
		{
			setJMenuBar(control.getVolumeMenuBar());
		}
	}

	public void setJMenuBar(JMenuBar menu)
	{
		if (owner != null)
		{

			owner.setJMenuBar(menu);
		}
	}

	public void exitPressed()
	{
		System.exit(0);
	}

	public void saveSetPressed() throws FileNotFoundException, IOException
	{
		dataSetPanel.setType(FileSelectionField.TYPE_SAVE_FILE);
		if (!dataSetPanel.getUserChoice())
		{
			return;
		}
		saveData(dataSetPanel.getFile());
	}

	public void saveData(File f) throws FileNotFoundException, IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		out.writeInt(panelData.getSelectedIndex());
		out.writeInt(panelData.getTabCount());

		for (int i = 0; i < panelData.getTabCount(); i++)
		{
			OCTDataViewPanel p = (OCTDataViewPanel) panelData.getComponentAt(i);
			out.writeObject(p);
		}
		out.close();
	}

	public void loadSetPressed() throws FileNotFoundException, IOException,
			ClassNotFoundException
	{
		dataSetPanel.setType(FileSelectionField.TYPE_OPEN_FILE);
		if (!dataSetPanel.getUserChoice())
		{
			return;
		}

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				dataSetPanel.getFile()));
		int index = in.readInt();
		int size = in.readInt();

		for (int i = 0; i < size; i++)
		{

			OCTDataViewPanel p = (OCTDataViewPanel) in.readObject();
			addDataSet(p);
		}

		panelData.setSelectedIndex(index);
		updatePanel();
	}

}

class MenuControler
{
	JMenuBar sliceMenuBar = new JMenuBar();

	JMenuBar volumeMenuBar = new JMenuBar();

	JMenuBar planeMenuBar = new JMenuBar();

	JMenu fileMenu = new JMenu("File");

	JMenu helpMenu = new JMenu("Help");

	JMenu volumeToolMenu = new JMenu("Tools - V");

	JMenu sliceToolMenu = new JMenu("Tools - S");

	OCTProgram owner;

	// For File Menu
	JMenuItem loadSetItem = new JMenuItem("Load Set");

	JMenuItem saveSetItem = new JMenuItem("Save Set");

	JMenuItem closeSetItem = new JMenuItem("Close Set");

	JMenuItem addDataItem = new JMenuItem("Add Data");

	JMenuItem removeDataItem = new JMenuItem("Remove Data");

	JMenuItem exitItem = new JMenuItem("Exit");

	// For Help Meun
	JMenuItem helpMenuItem = new JMenuItem("Help");

	public MenuControler(OCTProgram prog)
	{
		owner = prog;
		createMenu();
	}

	public void createMenu()
	{
		// File Menu Bar
		fileMenu.add(loadSetItem);
		fileMenu.add(saveSetItem);
		fileMenu.add(closeSetItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(addDataItem);
		fileMenu.add(removeDataItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitItem);

		// Help Menu Bar
		helpMenu.add(helpMenuItem);

		// Add Listners
		addDataItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.addDataSet();
			}
		});

		loadSetItem.addActionListener(new ActionListener()
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
		});

		saveSetItem.addActionListener(new ActionListener()
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
		});

		exitItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				owner.exitPressed();
			}
		});

	}

	public JMenuBar getSliceMenuBar()
	{
		// Create slice Menu
		sliceMenuBar.add(fileMenu);
		sliceMenuBar.add(sliceToolMenu);
		sliceMenuBar.add(helpMenu);
		return sliceMenuBar;
	}

	public JMenuBar getVolumeMenuBar()
	{
		// Create Volume menu
		volumeMenuBar.add(fileMenu);
		volumeMenuBar.add(volumeToolMenu);
		volumeMenuBar.add(helpMenu);
		return volumeMenuBar;
	}

	public JMenuBar getPlaneMenuBar()
	{// Create Plane Menu
		planeMenuBar.add(fileMenu);

		planeMenuBar.add(helpMenu);
		return planeMenuBar;
	}
}

/**
 * This class is used to ensure that the correct menu bar will be displayed in
 * the oct program
 * 
 * @author Joey.Enfield
 * 
 */
class PanelListner implements ChangeListener
{
	OCTProgram owner;

	OCTDataViewPanel child = null;

	public PanelListner(OCTProgram owner)
	{
		this.owner = owner;
	}

	public void setView(OCTDataViewPanel view)
	{

		if (child != null)
		{
			child.tabPane.removeChangeListener(this);
		}
		child = view;
		child.tabPane.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		owner.updateJMenuBar();
	}
}

interface OwnerInterface
{
	public abstract void setJMenuBar(JMenuBar menu);
}
