package com.joey.software.framesToolkit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SwingToolkit
{
	public static void createPanel(String[] names, JComponent[] comp, int labelWide, int labelHigh, JPanel panel)
	{
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (int i = 0; i < names.length; i++)
		{
			JPanel p = new JPanel(new BorderLayout());

			JPanel labelPanel = new JPanel(new BorderLayout());
			JLabel label = new JLabel(names[i]);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			labelPanel.add(label);

			Dimension size = new Dimension(labelWide, labelHigh);
			labelPanel.setPreferredSize(size);
			labelPanel.setMaximumSize(size);
			labelPanel.setMinimumSize(size);

			p.add(labelPanel, BorderLayout.WEST);
			p.add(comp[i], BorderLayout.CENTER);

			panel.add(p);
		}
	}

	public static float getSliderPos(JSlider slide)
	{
		return ((float) slide.getValue() - slide.getMinimum())
				/ (slide.getMaximum() - slide.getMinimum());
	}

	public static void createPanel(String[] names, JComponent[] comp, int labelWide, JPanel panel)
	{
		panel.setLayout(new GridLayout(comp.length, 1));
		for (int i = 0; i < names.length; i++)
		{
			JPanel pan = new JPanel(new BorderLayout());
			JLabel label = new JLabel(names[i]);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setPreferredSize(new Dimension(labelWide, 0));

			pan.add(label, BorderLayout.WEST);
			pan.add(comp[i], BorderLayout.CENTER);

			panel.add(pan);
		}
	}

	public static JPanel getFileInputPanel(final JTextField textField, JButton button, String labelText, boolean folderOnly)
	{
		return getFileInputPanel(textField, button, labelText, folderOnly, -1, -1);
	}

	/**
	 * This will create a new JPanel text filed that can be used to enter file
	 * or folder locations
	 * 
	 * @param labelText
	 * @param initalText
	 * @param buttonText
	 * @param labelSize
	 * @param buttonSize
	 * @return
	 */
	public static JPanel getFileInputPanel(final JTextField textField, JButton button, String labelText, final boolean folder, int labelSize, int buttonSize)
	{
		JPanel filePanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(labelText);

		button.addActionListener(new ActionListener()
		{
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent e)
			{
				chooser.setMultiSelectionEnabled(false);
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				if (folder)
				{
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				} else
				{
					chooser
							.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				}
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					textField.setText(chooser.getSelectedFile().toString());
				}
			}
		});
		if (labelSize > 0)
		{

		}
		if (buttonSize > 0)
		{

		}
		JTextfieldTools.setPreventCharacters(textField, "/*?\"<>|");

		filePanel.add(label, BorderLayout.WEST);
		filePanel.add(textField, BorderLayout.CENTER);
		filePanel.add(button, BorderLayout.EAST);

		return filePanel;
	}

	public static JPanel getLabel(JComponent compoent, String title, int size)
	{
		JLabel lab = new JLabel(title);
		lab.setPreferredSize(new Dimension(size, 0));
		lab.setHorizontalAlignment(SwingConstants.RIGHT);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(lab, BorderLayout.WEST);
		panel.add(compoent, BorderLayout.CENTER);

		return panel;
	}
}
