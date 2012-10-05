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
package com.joey.software.VolumeToolkit.Transferfunctions;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;


public class TransferFunctionPanel extends JPanel
{
	BufferedImage data = new BufferedImage(256, 256,
			BufferedImage.TYPE_INT_ARGB);

	ImagePanel previewPanel = new ImagePanel(data);

	DynamicRangeImage editPanel = new DynamicRangeImage(ImageOperations
			.getBi(256));

	DefaultListModel windowListModel = new DefaultListModel();

	JList windowList = new JList(windowListModel);

	JPanel windowControl = new JPanel();

	TransferWindow lastWindow = null;

	JButton removeView = new JButton("Remove");

	JButton addView = new JButton("Add");

	public TransferFunctionPanel()
	{
		createPanel();
	}

	public int getARGB(int posX, int posY)
	{
		return data.getRGB(posX, posY);
	}

	public void setBackgroundImage(int[][] data)
	{
		editPanel.setDataInteger(data);
		editPanel.updateMaxMin();
	}

	public void addTransferWindow(TransferWindow window)
	{
		windowListModel.addElement(window);
	}

	public void updateSelectedValue()
	{
		if (windowList.getSelectedIndex() >= 0)
		{
			setSelectedWindow((TransferWindow) windowListModel.get(windowList
					.getSelectedIndex()));
		} else
		{
			setSelectedWindow(null);
		}
	}

	public void setSelectedWindow(TransferWindow window)
	{
		// If last window is not null set as not showing
		if (lastWindow != null)
		{
			lastWindow.showing = false;
		}

		windowControl.removeAll();
		if (window == null)
		{
			PolygonControler cont = new PolygonControler(editPanel.getImage());
			cont.setMaxPoints(1);
		} else
		{
			window.showing = true;
			window.setPanel(editPanel.getImage());
			windowControl.setLayout(new BorderLayout());
			windowControl.add(window.getControlPanel());
			windowControl.getParent().validate();
			windowControl.getParent().repaint();
		}

		lastWindow = window;
	}

	public void createPanel()
	{
		editPanel.setPanelType(DynamicRangeImage.PANEL_TYPE_BASIC);
		JPanel previewHolder = new JPanel(new BorderLayout());
		JPanel editHolder = new JPanel(new BorderLayout());

		editHolder
				.setBorder(BorderFactory.createTitledBorder("Current Region"));
		previewHolder.setBorder(BorderFactory
				.createTitledBorder("Transfer Function"));

		editHolder.add(editPanel);
		previewHolder.add(previewPanel);

		JPanel viewHolder = new JPanel(new GridLayout(1, 2));
		viewHolder.add(editHolder);
		viewHolder.add(previewHolder);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.add(addView);
		buttonPanel.add(removeView);

		JPanel listHolder = new JPanel(new BorderLayout());
		listHolder.add(new JScrollPane(windowList), BorderLayout.CENTER);
		listHolder.add(buttonPanel, BorderLayout.SOUTH);
		listHolder.setPreferredSize(new Dimension(200, 100));

		JPanel windowHolder = new JPanel(new BorderLayout());
		windowHolder.add(listHolder, BorderLayout.WEST);
		windowHolder.add(windowControl, BorderLayout.CENTER);
		windowHolder.setBorder(BorderFactory.createTitledBorder("Windows"));

		setLayout(new BorderLayout());
		add(viewHolder, BorderLayout.CENTER);
		add(windowHolder, BorderLayout.SOUTH);

		removeView.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeSelectedTransferWindow();
			}
		});
		addView.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				addTransferWindow();
			}
		});
		windowList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				updateSelectedValue();
			}
		});
		editPanel.getImage().addROIPanelListner(new ROIPanelListner()
		{

			@Override
			public void regionAdded(Shape region)
			{
				updateTransferData();
			}

			@Override
			public void regionChanged()
			{
				updateTransferData();
			}

			@Override
			public void regionRemoved(Shape region)
			{
				updateTransferData();
			}
		});
	}

	public void updateTransferData()
	{

		BufferedImage holdImage = ImageOperations.getSameSizeImage(data);
		// Set Transparance
		ImageOperations.setImageColor(new Color(0, 0, 0, 0f), data);
		for (Object obj : windowListModel.toArray())
		{
			TransferWindow w = (TransferWindow) obj;
			if (!w.visible.isSelected())
			{
				ImageOperations
						.setImageColor(new Color(0, 0, 0, 0f), holdImage);

				Graphics2D gHold = holdImage.createGraphics();
				GraphicsToolkit
						.setRenderingQuality(gHold, GraphicsToolkit.HIGH_QUALITY);

				w.drawPath(gHold);

				Graphics2D gRst = data.createGraphics();
				GraphicsToolkit
						.setRenderingQuality(gRst, GraphicsToolkit.HIGH_QUALITY);
				gRst.setComposite(AlphaComposite
						.getInstance(AlphaComposite.SRC_OVER, w.getAlpha()));
				gRst.drawImage(holdImage, null, null);
				gRst.dispose();
			}
		}
		previewPanel.repaint();
	}

	public void showView()
	{
		updateTransferData();
		FrameFactroy.getFrame(data)
				.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public void removeSelectedTransferWindow()
	{
		if (windowList.getSelectedIndex() < 0)
		{
			return;
		} else
		{
			TransferWindow window = (TransferWindow) windowListModel
					.get(windowList.getSelectedIndex());
			if (lastWindow == window)
			{
				setSelectedWindow(null);
			}
			windowListModel.removeElement(window);
		}
		updateTransferData();
	}

	public void addTransferWindow()
	{
		TransferWindow window = new TransferWindow(editPanel.getImage());
		addTransferWindow(window);
		// Make sure that last selected is reselected
		setSelectedWindow(lastWindow);
	}

	public static void main(String input[])
	{
		final TransferFunctionPanel owner = new TransferFunctionPanel();

		byte[][][] volData = GradientToolkit.getData(256, 256, 128);
		int[][] traFun = GradientToolkit.getGradHistHolder();

		GradientToolkit.getGradientFunctionFAST(volData, traFun);
		owner.setBackgroundImage(traFun);
		JPanel main = new JPanel(new BorderLayout());
		main.add(owner, BorderLayout.CENTER);

		// panel.setPanelType(ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		FrameFactroy.getFrame(main);

	}
}
