package com.joey.software.Tools;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.DataToolkit.ThorLabs2DImageProducer;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageFileSaver;
import com.joey.software.stringToolkit.StringOperations;


public class IMGExporter
{
	public static void main(String input[]) throws IOException
	{
		File f = FileSelectionField.getUserFile();

		ThorLabs2DImageProducer img = new ThorLabs2DImageProducer(f);

		String base = f.getParent() + "\\img";

		String end = ".bmp";

		JSpinner startNum = new JSpinner(new SpinnerNumberModel(0, 0, img
				.getImageCount(), 1));
		JSpinner endNum = new JSpinner(new SpinnerNumberModel(img
				.getImageCount(), 0, img.getImageCount(), 1));

		JPanel startPanel = new JPanel(new BorderLayout());
		startPanel.add(new JLabel("Start Pos : "), BorderLayout.WEST);
		startPanel.add(startNum, BorderLayout.CENTER);

		JPanel endPanel = new JPanel(new BorderLayout());
		endPanel.add(new JLabel("End Pos : "), BorderLayout.WEST);
		endPanel.add(endNum, BorderLayout.CENTER);

		JPanel holder = new JPanel(new GridLayout(2, 1));
		// holder.setPreferredSize(new Dimension(300,300));
		holder.add(startPanel);
		holder.add(endPanel);

		if (JOptionPane.showConfirmDialog(null, holder) != JOptionPane.OK_OPTION)
		{
			return;
		}
		int startVal = (Integer) startNum.getValue();
		int endVal = (Integer) endNum.getValue();

		ImageFileSaver saveTool = new ImageFileSaver(3, 10);

		StatusBarPanel stat = new StatusBarPanel();
		stat.setMaximum(endVal - startVal);

		JFrame frame = new JFrame("Progress");
		frame.setSize(500, 60);
		frame.setVisible(true);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(stat, BorderLayout.CENTER);
		for (int i = startVal; i < endVal; i++)
		{
			stat.setValue(i - startVal);
			BufferedImage rst = img.getImage(i);
			String fileName = base + StringOperations.getNumberString(5, i)
					+ end;
			saveTool.addData(new File(fileName), rst);
		}
		frame.setVisible(false);
		System.exit(0);
	}
}
