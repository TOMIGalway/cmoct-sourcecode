package com.joey.software.testing;


import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class ImageTest
{
	public static void main(String input[])
	{
		BufferedImage img = ImageOperations.getBi(100, 100);

		ImageOperations.setImage(Color.BLACK, img);
		// ImageOperations.fillWithRandomColorSquares(10, 10, img);
		ImagePanel panel = new ImagePanel(img);

		panel.setPanelType(ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);

		JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().setFullScreenWindow(frame);

	}

	public static void maindd(String input[])
	{
		BufferedImage image1 = new BufferedImage(5, 5,
				BufferedImage.TYPE_4BYTE_ABGR);

		// ImageOperations.fillWithRandomColorSquares(3, 3, image1);
		ImageOperations.setImage(Color.RED, image1);
		BufferedImage image2 = ImageOperations.cloneImage(image1);

		ImagePanel image1Panel = new ImagePanel(image1,
				ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);
		ImagePanel image2Panel = new ImagePanel(image2,
				ImagePanel.TYPE_SCALE_IMAGE_TO_PANEL);

		image1Panel.setBorder(BorderFactory.createTitledBorder("Image 1"));
		image2Panel.setBorder(BorderFactory.createTitledBorder("Image 2"));

		JFrame frame = new JFrame("Image test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),
				BoxLayout.Y_AXIS));
		frame.getContentPane().add(image1Panel);
		frame.getContentPane().add(image2Panel);

		frame.setVisible(true);

	}
}
