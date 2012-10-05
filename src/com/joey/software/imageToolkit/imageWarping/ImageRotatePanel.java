package com.joey.software.imageToolkit.imageWarping;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class ImageRotatePanel extends JPanel
{
	int sliderAccuracy = 100;

	JCheckBox realTimeUpdate = new JCheckBox("Real Time");

	ImagePanel orignalPanel = new ImagePanel();

	ImagePanel resultPanel = new ImagePanel();

	JSlider position = new JSlider(JSlider.VERTICAL, 0, 360 * sliderAccuracy, 0);

	JColorChooser backgroundColor = new JColorChooser();

	JButton changeBackgroundButton = new JButton("Color");

	public ImageRotatePanel()
	{
		createJPanel();
		backgroundColor.setColor(Color.LIGHT_GRAY);
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(realTimeUpdate, BorderLayout.NORTH);
		controlPanel.add(position, BorderLayout.CENTER);
		controlPanel.add(changeBackgroundButton, BorderLayout.SOUTH);

		JTabbedPane imageTab = new JTabbedPane();
		imageTab.add("Processed", new JScrollPane(resultPanel));
		imageTab.addTab("Orignal", new JScrollPane(orignalPanel));

		JSplitPane mainSplit = new JSplitPane();
		mainSplit.setLeftComponent(controlPanel);
		mainSplit.setRightComponent(imageTab);

		setLayout(new BorderLayout());
		add(mainSplit, BorderLayout.CENTER);

		position.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				boolean update = false;
				if (position.getValueIsAdjusting())
				{
					if (realTimeUpdate.isSelected())
					{
						update = true;
					}
				} else
				{
					update = true;
				}

				if (update)
				{
					updateImage();

				}

			}
		});

		changeBackgroundButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(null, backgroundColor);

			}
		});
	}

	public void setImage(BufferedImage img)
	{
		orignalPanel.setImage(img);
		updateImage();
	}

	public void updateImage()
	{
		resultPanel.setImage(ImageOperations.getRotatedImageFull(orignalPanel
				.getImage(), Math.toRadians(getRotation()), backgroundColor
				.getColor()));
	}

	public double getRotation()
	{
		return position.getValue() / (double) sliderAccuracy;
	}

	public void setRotation(double rotation)
	{
		position.setValue((int) (rotation * sliderAccuracy));
	}

	public BufferedImage getResultImage()
	{
		return resultPanel.getImage();
	}

	public static void main(String input[])
	{
		ImageRotatePanel rot = new ImageRotatePanel();
		rot.setImage(ImageOperations.getBi(300));
		FrameFactroy.getFrame(rot);
	}
}
