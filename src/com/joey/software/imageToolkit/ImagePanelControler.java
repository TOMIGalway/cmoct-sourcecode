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
package com.joey.software.imageToolkit;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImagePanelControler extends JPanel implements ActionListener,
		ChangeListener
{
	static String[] options =
	{ "Actual Size", "Scale Image", "Fit Image", "Custom Scale" };

	JComboBox panelType = new JComboBox(options);

	JSlider zoomControl = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 5);

	ImagePanel panel;

	JPanel panelTypeControl = new JPanel();

	JPanel zoomControlPanel = new JPanel();

	JPanel mouseZoomPanel = new JPanel();

	JButton mouseZoomButton = new JButton();;

	private JPanel getPanelTypeControl()
	{
		return panelTypeControl;
	}

	private void setPanelTypeControl(JPanel panelTypeControl)
	{
		this.panelTypeControl = panelTypeControl;
	}

	private JPanel getZoomControlPanel()
	{
		return zoomControlPanel;
	}

	private void setZoomControlPanel(JPanel zoomControlPanel)
	{
		this.zoomControlPanel = zoomControlPanel;
	}

	public ImagePanelControler(ImagePanel panel)
	{
		setPanel(panel);
		createPanel();

	}

	public void createPanel()
	{
		// Create the panel type
		panelTypeControl.setLayout(new BorderLayout());
		panelTypeControl.add(panelType);

		// Create the Zoom Control
		zoomControlPanel.setLayout(new BorderLayout());
		zoomControlPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		zoomControlPanel.add(zoomControl);
		zoomControl.setMajorTickSpacing(10);
		zoomControl.setMinorTickSpacing(1);
		zoomControl.setPaintTicks(true);
		zoomControl.setPaintLabels(true);

		// Add panel parts
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createTitledBorder("Controls"));

		add(panelTypeControl);
		add(zoomControlPanel);

		// Add listners
		panelType.addActionListener(this);
		mouseZoomButton.addActionListener(this);
		zoomControl.addChangeListener(this);
	}

	public static void main(String input[])
	{
		// Create the image
		BufferedImage image = new BufferedImage(300, 300,
				BufferedImage.TYPE_4BYTE_ABGR);
		ImageOperations.fillWithRandomColorSquares(4, 4, image);

		// Create the image panel
		ImagePanel imagePanel = new ImagePanel(image);
		imagePanel.setBorder(BorderFactory.createTitledBorder("Image"));
		// Create the image control panel
		ImagePanelControler controlPanel = new ImagePanelControler(imagePanel);

		// Create the mainPanel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createTitledBorder("Application"));
		mainPanel.add(imagePanel, BorderLayout.CENTER);
		mainPanel.add(controlPanel, BorderLayout.NORTH);
		// Create the frame
		JFrame f = new JFrame("Test");
		f.setSize(600, 800);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(mainPanel, BorderLayout.CENTER);
		f.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == panelType)
		{
			panel.setPanelType(panelType.getSelectedIndex());
			if (panelType.getSelectedIndex() == ImagePanel.TYPE_CUSTOM_SCALE)
			{
				zoomControlPanel.setVisible(true);
			} else
			{
				zoomControlPanel.setVisible(false);
			}
		}

	}

	private ImagePanel getPanel()
	{
		return panel;
	}

	private void setPanel(ImagePanel panel)
	{
		this.panel = panel;
		panelType.setSelectedIndex(panel.getPanelType());
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == zoomControl)
		{
			switch (zoomControl.getValue())
			{
				case 0:
					panel.setScale(0.1, 0.1);
					break;
				case 1:
					panel.setScale(0.25, 0.25);
					break;
				case 2:
					panel.setScale(0.5, 0.5);
					break;
				case 3:
					panel.setScale(0.7, 0.7);
					break;
				case 4:
					panel.setScale(0.9, 0.9);
					break;
				case 5:
					panel.setScale(1, 1);
					break;
				case 6:
					panel.setScale(2, 2);
					break;
				case 7:
					panel.setScale(4, 4);
					break;
				case 8:
					panel.setScale(8, 8);
					break;
				case 9:
					panel.setScale(10, 10);
					break;
				case 10:
					panel.setScale(20, 20);
					break;
			}
			panel.repaint();
			panel.validate();
		}

	}
}
