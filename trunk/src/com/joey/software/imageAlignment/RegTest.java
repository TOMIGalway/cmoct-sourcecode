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
package com.joey.software.imageAlignment;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.URL;

import javax.swing.JPanel;

import com.joey.software.framesToolkit.FrameFactroy;


public class RegTest extends JPanel implements ActionListener
{
	ImageReg ir;

	ImageCanvas ic;

	MemoryImageSource is;

	TextField tf1, tf2, tf3, tf4;

	int w, h;

	int[][] A, B;

	int[] pic;

	boolean lastMode;

	public RegTest()
	{
		ic = new ImageCanvas();
		createPanel();
	}
	// Initialization
	public void createPanel()
	{
		Panel pn1, pn2;
		Button bn;
		this.setLayout(new BorderLayout());
		add(ic , BorderLayout.CENTER);
		add(pn1 = new Panel(new GridLayout(4, 1)), BorderLayout.NORTH);

		pn1.add(pn2 = new Panel(new FlowLayout(FlowLayout.LEFT)));
		pn2.add(new Label("Image width:"));
		pn2.add(tf1 = new TextField("256"));
		pn2.add(new Label("Image height:"));
		pn2.add(tf2 = new TextField("256"));

		pn1.add(pn2 = new Panel(new FlowLayout(FlowLayout.LEFT)));
		pn2.add(new Label("Image A:"));
		pn2.add(tf3 = new TextField(
				"http://www.ccs.neu.edu/home/victor/imagereg/images/46hrs.dat"));
		pn1.add(pn2 = new Panel(new FlowLayout(FlowLayout.LEFT)));
		pn2.add(new Label("Image B:"));
		pn2.add(tf4 = new TextField(
				"http://www.ccs.neu.edu/home/victor/imagereg/images/68hrs.dat"));

		pn1.add(pn2 = new Panel());
		pn2.add(bn = new Button("Load images"));
		bn.addActionListener(this);
		pn2.add(bn = new Button("Iterate algorithm"));
		bn.addActionListener(this);
		pn2.add(bn = new Button("View offsets"));
		bn.addActionListener(this);
		pn2.add(bn = new Button("View result"));
		bn.addActionListener(this);
	}

	// Button press etc.
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		String label = ev.getActionCommand();
		if (label == "Load images")
		{
			setup(Integer.parseInt(tf1.getText()), Integer.parseInt(tf2
					.getText()), tf3.getText(), tf4.getText());
		} else if (label == "Iterate algorithm")
		{
			iterate();
		} else if (label == "View offsets")
		{
			viewOffsets();
		} else if (label == "View result")
		{
			viewResults();
		}
	}

	// Set up image registration
	public void setup(int w, int h, String nA, String nB)
	{
		showStatus("Loading image files...");
		this.w = w;
		this.h = h;
		A = new int[h][w];
		B = new int[h][w];
		try
		{
			DataInputStream fA = new DataInputStream(new BufferedInputStream(
					(new URL(nA)).openStream()));
			DataInputStream fB = new DataInputStream(new BufferedInputStream(
					(new URL(nB)).openStream()));
			for (int i = 0; i < A.length; ++i)
				for (int j = 0; j < A[0].length; ++j)
				{
					A[i][j] = fA.readShort();
					B[i][j] = fB.readShort();
				}
			fA.close();
			fB.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		pic = new int[w * h];
		is = new MemoryImageSource(w, h, pic, 0, w);
		is.setAnimated(true);
		ic.setImage(this.createImage(is));
		ir = new ImageReg(A, B);
		ir.getPicture(pic, false);
		is.newPixels();
		showStatus("Images loaded");
	}

	// Iterate registration algorithm
	public void iterate()
	{
		if (ir == null)
			return;
		showStatus("Registering image...");
		ir.iterate();
		ir.getPicture(pic, lastMode);
		is.newPixels();
		showStatus("Iteration " + ir.getIteration() + " complete");
	}

	// Display offsets
	public void viewOffsets()
	{
		if (ir == null)
			return;
		showStatus("Resampling image...");
		ir.getPicture(pic, lastMode = true);
		is.newPixels();
		showStatus("Iteration " + ir.getIteration() + " complete");
	}

	// Display results
	public void viewResults()
	{
		if (ir == null)
			return;
		showStatus("Resampling image...");
		ir.getPicture(pic, lastMode = false);
		is.newPixels();
		showStatus("Iteration " + ir.getIteration() + " complete");
	}
	
	public void showStatus(String string)
	{
		
	}
	
	
	public static void main(String input[])
	{
		FrameFactroy.getFrame(new RegTest());
	}
}
