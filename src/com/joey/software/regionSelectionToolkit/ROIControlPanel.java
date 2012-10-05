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
package com.joey.software.regionSelectionToolkit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.movementToolkit.MovementPanel;
import com.joey.software.regionSelectionToolkit.controlers.EllipseControler;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;
import com.joey.software.regionSelectionToolkit.controlers.ROIControler;
import com.joey.software.regionSelectionToolkit.controlers.RectangleControler;


public class ROIControlPanel extends JPanel implements ActionListener
{
	ROIPanel panel;

	String[] options =
	{ "Rectangle", "Oval", "Polygon" };

	JComboBox regionTypes = new JComboBox(options);

	JButton loadImageButton = new JButton("Load Image");

	JButton allowMultipleButton = new JButton("");

	JButton nextShapeButton = new JButton("Next");

	JButton lastShapeButton = new JButton("Last");

	MovementPanel movement = new MovementPanel();

	RegionShapeMovementControler shapeControler;

	/**
	 * @param panel
	 */
	public ROIControlPanel(ROIPanel panel)
	{
		super();
		setPanel(panel);
		createJPanel();
		regionTypes.addActionListener(this);
		loadImageButton.addActionListener(this);
		allowMultipleButton.addActionListener(this);
		validateData();
	}

	public void validateData()
	{
		if (panel.isAllowMultipleROI())
		{
			allowMultipleButton.setText("True");
		} else
		{
			allowMultipleButton.setText("False");
		}

		ROIControler cont = panel.getControler();
		if (cont instanceof PolygonControler)
		{
			regionTypes.setSelectedIndex(2);
		} else if (cont instanceof EllipseControler)
		{
			regionTypes.setSelectedIndex(1);
		} else if (cont instanceof RectangleControler)
		{
			regionTypes.setSelectedIndex(0);
		}
	}

	public void createJPanel()
	{
		JPanel labelsPanel = new JPanel(new GridLayout(3, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(3, 1));

		labelsPanel.add(new JLabel("Background:"));
		fieldPanel.add(loadImageButton);

		labelsPanel.add(new JLabel("Region:"));
		fieldPanel.add(regionTypes);

		labelsPanel.add(new JLabel("Allow Multiple:"));
		fieldPanel.add(allowMultipleButton);

		JPanel movementControlPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT));
		movementControlPanel.setPreferredSize(new Dimension(200, 250));
		movementControlPanel.add(movement);

		shapeControler = new RegionShapeMovementControler(this);
		movement.addListner(shapeControler);

		JPanel fields = new JPanel(new BorderLayout());
		fields.add(labelsPanel, BorderLayout.WEST);
		fields.add(fieldPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.add(lastShapeButton);
		buttonPanel.add(nextShapeButton);

		JPanel controls = new JPanel(new BorderLayout());
		controls.add(fields, BorderLayout.CENTER);
		controls.add(buttonPanel, BorderLayout.SOUTH);

		JPanel main = new JPanel(new BorderLayout());
		main.add(controls, BorderLayout.NORTH);
		main.add(movementControlPanel, BorderLayout.CENTER);

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(main);

		nextShapeButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				nextShape();
			}
		});

		lastShapeButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				lastShape();
			}
		});
	}

	public void nextShape()
	{
		int region = panel.getHighlightedRegion();
		region++;
		panel.setHighlightedRegion(region);
		updateShape();
	}

	public void lastShape()
	{
		int region = panel.getHighlightedRegion();

		region--;
		panel.setHighlightedRegion(region);
		updateShape();
	}

	public void removeCurrent()
	{
		Shape s = panel.getSelectedShape();
		if (s != null)
		{
			panel.removeRegion(s);
			lastShape();
		}
	}

	public void updateShape()
	{
		int region = panel.getHighlightedRegion();

		if (region >= panel.getRegions().size())
		{
			region = 0;
			panel.setHighlightedRegion(region);
		} else if (region < 0)
		{
			region = panel.getRegions().size() - 1;
			panel.setHighlightedRegion(region);
		}

		Shape s = panel.getSelectedShape();
		if (s != null)
		{
			shapeControler.setShape(s);
		}
	}

	public void setControlerEditable(boolean edit)
	{
		regionTypes.setEditable(edit);
	}

	public ROIPanel getPanel()
	{
		return panel;
	}

	public void setPanel(ROIPanel panel)
	{
		this.panel = panel;
		panel.setControlPanel(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == regionTypes)
		{
			int val = regionTypes.getSelectedIndex();
			if (val == 0)
			{
				panel.setControler(ROIPanel.TYPE_RECTANGLE);
			} else if (val == 1)
			{
				panel.setControler(ROIPanel.TYPE_OVAL);
			} else if (val == 2)
			{
				panel.setControler(ROIPanel.TYPE_POLYGON);
			}
		} else if (e.getSource() == allowMultipleButton)
		{
			panel.setAllowMultipleROI(!panel.isAllowMultipleROI());
		} else if (e.getSource() == loadImageButton)
		{
			File input = ImageFileSelector.getUserImageFile();
			if (input != null)
			{
				try
				{
					panel.setImage(ImageIO.read(input));
				} catch (IOException e1)
				{
					JOptionPane
							.showMessageDialog(null, "There was an Error loading the file");
					e1.printStackTrace();
				}
			}
		}

	}

}
