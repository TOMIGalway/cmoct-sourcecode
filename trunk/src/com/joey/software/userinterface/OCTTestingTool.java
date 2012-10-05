package com.joey.software.userinterface;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;


public class OCTTestingTool
{
	public static void main(String input[]) throws Exception
	{
		// MemoryUsagePanel.getMemoryUsagePanel(1000, 100).setVisible(true);
		final JFrame f = FrameFactroy.getFrame(false);

		OwnerInterface owner = new OwnerInterface()
		{

			@Override
			public void setJMenuBar(JMenuBar menu)
			{
				f.setJMenuBar(menu);
			}

		};

		OCTProgram program = new OCTProgram(owner);

		f.setTitle(OCTProgram.TITLE);
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(program, BorderLayout.CENTER);
		f.setSize(1024, 800);
		f.setVisible(true);

		program.loadSetPressed();
	}

	public static void startProgram() throws IOException
	{
		OCTDataViewPanel view = OCTDataViewPanel.getUserSelection(null);
		JFrame f = FrameFactroy.getFrame(view);
		f.setSize(1024, 800);
	}

	public static void startProgramFixed() throws IOException
	{
		File dataFile = new File("C:\\test\\raw.dat");
		File previewFile = new File("C:\\test\\prv.dat");
		NativeDataSet octData = new NativeDataSet(dataFile, previewFile);

		final OCTDataViewPanel initial = new OCTDataViewPanel(octData,
				"Micro Needle");
		JFrame f = FrameFactroy.getFrame(initial);
		f.setSize(1024, 800);
	}

	public static void multiplePanel() throws IOException
	{

		File dataFile = new File("C:\\test\\raw.dat");
		File previewFile = new File("C:\\test\\prv.dat");
		NativeDataSet octData = new NativeDataSet(dataFile, previewFile);

		final OCTDataViewPanel initial = new OCTDataViewPanel(octData,
				"Micro Needle");

		final FileSelectionField out = new FileSelectionField();
		out.setType(FileSelectionField.TYPE_SAVE_FILE);
		// OCTDataViewPanel view = getUserSelection(null);

		final JFrame f = new JFrame(OCTDataViewPanel.tabNameField.getText());
		f.setLayout(new BorderLayout());
		f.getContentPane().add(initial);
		f.setTitle(OCTDataViewPanel.tabNameField.getText());
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				super.windowClosing(e);

				File outFile = new File("c:\\test\\test.ddd");
				try
				{
					ObjectOutputStream output = new ObjectOutputStream(
							new FileOutputStream(outFile));
					output.writeObject(initial);
					output.close();
					System.out.println("Written");
				} catch (FileNotFoundException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try
				{
					ObjectInputStream input = new ObjectInputStream(
							new FileInputStream(outFile));
					OCTDataViewPanel finalData = (OCTDataViewPanel) input
							.readObject();
					FrameFactroy.getFrame(finalData);
				} catch (FileNotFoundException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e3)
				{
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			}
		});
		f.setSize(800, 600);
		f.setVisible(true);

	}

}
