package com.joey.software.LAFTools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.joey.software.framesToolkit.FrameFactroy;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;


public class EditableLAFControler extends JPanel
{
	HashMap<Integer, String> lafTitle = new HashMap<Integer, String>();

	JComboBox lafOptions;

	String lafFolder = null;

	JButton changeButton = new JButton("Change LAF");

	public JComponent view = null;

	public EditableLAFControler(String pathToLAFFolder)
	{
		this.lafFolder = pathToLAFFolder;
		createLAF();

		createJPanel();
	}

	public int getLAF()
	{
		return lafOptions.getSelectedIndex();
	}

	public void setLAF(int val)
	{
		lafOptions.setSelectedIndex(val);
	}

	public void createJPanel()
	{
		JPanel holder = new JPanel(new BorderLayout());
		holder.setLayout(new BorderLayout());
		holder.add(lafOptions, BorderLayout.CENTER);
		holder.add(changeButton, BorderLayout.EAST);
		holder.setBorder(BorderFactory.createTitledBorder(""));

		setLayout(new BorderLayout());
		add(holder, BorderLayout.NORTH);

		changeButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateLAF();

			}
		});

		lafOptions.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateLAF();

			}
		});
	}

	public void updateLAF()
	{
		updateLAF(lafOptions.getSelectedIndex());
	}

	public void updateLAF(int index)
	{

		try
		{
			LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();

			int choice = index;
			if (choice < lafs.length)
			{
				UIManager.setLookAndFeel(lafs[choice].getClassName());
			} else
			{
				choice -= lafs.length;
				if (choice == 0)
				{
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					UIManager.setLookAndFeel(laf);
				} else if (choice == 1)
				{
					NimRODTheme theme = new NimRODTheme(lafFolder
							+ "\\NimrodLAF\\Burdeos.theme");
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					NimRODLookAndFeel.setCurrentTheme(theme);
					UIManager.setLookAndFeel(laf);
				} else if (choice == 2)
				{
					NimRODTheme theme = new NimRODTheme(lafFolder
							+ "\\NimrodLAF\\DarkGrey.theme");
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					NimRODLookAndFeel.setCurrentTheme(theme);
					UIManager.setLookAndFeel(laf);
				} else if (choice == 3)
				{
					NimRODTheme theme = new NimRODTheme(lafFolder
							+ "\\NimrodLAF\\DarkTabaco.theme");
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					NimRODLookAndFeel.setCurrentTheme(theme);
					UIManager.setLookAndFeel(laf);
				} else if (choice == 4)
				{
					NimRODTheme theme = new NimRODTheme(lafFolder
							+ "\\NimrodLAF\\LightTabaco.theme");
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					NimRODLookAndFeel.setCurrentTheme(theme);
					UIManager.setLookAndFeel(laf);
				} else if (choice == 5)
				{
					NimRODTheme theme = new NimRODTheme(lafFolder
							+ "\\NimrodLAF\\Night.theme");
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					NimRODLookAndFeel.setCurrentTheme(theme);
					UIManager.setLookAndFeel(laf);
				} else if (choice == 6)
				{
					NimRODTheme theme = new NimRODTheme(lafFolder
							+ "\\NimrodLAF\\Snow.theme");
					NimRODLookAndFeel laf = new NimRODLookAndFeel();
					NimRODLookAndFeel.setCurrentTheme(theme);
					UIManager.setLookAndFeel(laf);
				}
			}

		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(this);
		if (view != null)
		{
			SwingUtilities.updateComponentTreeUI(view);

		}
	}

	public void changeLAF(JComponent comp)
	{
		this.view = comp;
		JFrame f = FrameFactroy.getFrame(this);
		f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		return;
	}

	public void createLAF()
	{
		int num = 0;

		LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();

		for (int i = 0; i < lafs.length; i++)
		{
			lafTitle.put(i, lafs[i].getName());
		}

		lafTitle.put(lafs.length + 0, "Nimrod");
		lafTitle.put(lafs.length + 1, "Nimrod - Burdeos");
		lafTitle.put(lafs.length + 2, "Nimrod - DarkGrey");
		lafTitle.put(lafs.length + 3, "Nimrod - DarkTabaco");
		lafTitle.put(lafs.length + 4, "Nimrod - LightTabaco");
		lafTitle.put(lafs.length + 5, "Nimrod - Night");
		lafTitle.put(lafs.length + 6, "Nimrod - Snow");

		lafOptions = new JComboBox(lafTitle.values().toArray(new String[0]));
	}
}
