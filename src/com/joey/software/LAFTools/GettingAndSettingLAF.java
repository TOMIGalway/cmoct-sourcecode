package com.joey.software.LAFTools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GettingAndSettingLAF
{
	JFrame frame;

	JTextArea txtArea;

	public static void main(String args[])
	{
		GettingAndSettingLAF mc = new GettingAndSettingLAF();
	}

	public GettingAndSettingLAF()
	{
		frame = new JFrame("Change Look");
		UIManager.LookAndFeelInfo lookAndFeels[] = UIManager
				.getInstalledLookAndFeels();
		JPanel panel = new JPanel();
		JPanel panel1 = new JPanel();
		txtArea = new JTextArea(5, 15);
		JScrollPane sr = new JScrollPane(txtArea);
		panel1.add(sr);
		for (int i = 0; i < lookAndFeels.length; i++)
		{
			JButton button = new JButton(lookAndFeels[i].getName());
			button.addActionListener(new MyAction());
			panel.add(button);
		}
		frame.add(panel1, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		frame.setSize(300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public class MyAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			Object EventSource = ae.getSource();
			String lookAndFeelClassName = null;
			UIManager.LookAndFeelInfo looks[] = UIManager
					.getInstalledLookAndFeels();
			for (int i = 0; i < looks.length; i++)
			{
				if (ae.getActionCommand().equals(looks[i].getName()))
				{
					lookAndFeelClassName = looks[i].getClassName();
					break;
				}
			}
			try
			{
				UIManager.setLookAndFeel(lookAndFeelClassName);
				txtArea.setText(lookAndFeelClassName);
				SwingUtilities.updateComponentTreeUI(frame);
			} catch (Exception e)
			{
				JOptionPane
						.showMessageDialog(frame, "Can't change look and feel:"
								+ lookAndFeelClassName, "Invalid PLAF", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}