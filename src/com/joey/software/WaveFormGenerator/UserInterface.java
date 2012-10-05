package com.joey.software.WaveFormGenerator;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class UserInterface extends JPanel
{
	ROIPanel shapePanel = new ROIPanel(false);

	JTextArea xOutputs = new JTextArea();

	JTextArea yOutputs = new JTextArea();

	JButton outputDataButton = new JButton("Grab Data");

	public UserInterface()
	{
		createJPanel();
		createImage();
		
		shapePanel.setControler(ROIPanel.TYPE_POLYGON);
	}

	/**
	 * this function will create the imge
	 */
	public void createImage()
	{
		int high = 1000;
		int wide = 1000;
		
		BufferedImage img = new BufferedImage(wide, high,
				BufferedImage.TYPE_BYTE_BINARY);
		ImageOperations.setImageColor(Color.WHITE, img);

		// Draw Main Border lines
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		
		Line2D.Double l = new Line2D.Double();
		
		g.setStroke(new BasicStroke(4));
		
		l.setLine(0, high/2, wide, high/2);
		g.draw(l);
		
		l.setLine(wide/2, 0, wide/2, high);
		g.draw(l);
		
		
		
		shapePanel.setImage(img);
		shapePanel.setHighQualityRenderingEnabled(true);
		shapePanel.setNavigationImageEnabled(true);
		
	}

	public void createJPanel()
	{
		JPanel outputDataPanel = new JPanel(new GridLayout(1, 2));
		outputDataPanel.add(new JScrollPane(xOutputs));
		outputDataPanel.add(new JScrollPane(yOutputs));

		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.add(outputDataPanel, BorderLayout.CENTER);
		outputPanel.add(outputDataButton, BorderLayout.NORTH);

		
		JPanel shapeHolder = new JPanel();
		shapePanel.putIntoPanel(shapeHolder);
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(300);
		split.setResizeWeight(1);
		split.setLeftComponent(shapeHolder);
		split.setRightComponent(outputPanel);

		setLayout(new BorderLayout());
		add(split);
	}

	public static void main(String input[])
	{
		FrameFactroy.getFrame(new UserInterface());

	}
}
