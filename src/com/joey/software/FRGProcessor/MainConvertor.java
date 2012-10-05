package com.joey.software.FRGProcessor;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.joey.software.SwingTools.ActivityPanel;
import com.joey.software.framesToolkit.FrameFactroy;


public class MainConvertor
{
	public static void main(String input[])
	{
		JPanel p = new JPanel(new BorderLayout());
		p.add(new ImageExporter(), BorderLayout.NORTH);

		FrameFactroy.getFrame(p);
	}
}

class DropPanel
{
	JCheckBox showEdit = new JCheckBox();
}

class ImageExporter extends JPanel implements Runnable
{
	JButton cancelButton = new JButton("Stop");

	JProgressBar progress = new JProgressBar();

	ActivityPanel active = new ActivityPanel("Running", 100);

	public ImageExporter()
	{
		createJPanel();
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		add(progress, BorderLayout.CENTER);
		add(active, BorderLayout.WEST);
		add(cancelButton, BorderLayout.EAST);

	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub

	}

}
