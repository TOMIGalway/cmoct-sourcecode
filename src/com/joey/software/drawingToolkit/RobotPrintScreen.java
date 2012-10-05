package com.joey.software.drawingToolkit;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.joey.software.framesToolkit.FrameFactroy;


public class RobotPrintScreen
{
	public static void main(String input[]) throws AWTException
	{
		JButton button = new JButton("Grab Screen");
		button.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					ImageIO.write(getScreenShot(), "png", new File(
							"c:\\test\\out.png"));
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (AWTException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		FrameFactroy.getFrame(button);
	}

	public static BufferedImage getScreenShot() throws AWTException
	{

		Robot r = new Robot();
		BufferedImage img = r.createScreenCapture(GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds());
		return img;
	}
}
