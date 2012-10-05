package com.joey.software.interfaces;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.threadToolkit.Task;
import com.joey.software.threadToolkit.TaskMaster;


public class CrossCorrProgramMultipleFiles
{
	public static void main(String input[]) throws IOException
	{
		final File[] file = getFiles();
		int parallel = 2;

		TaskMaster master = new TaskMaster(parallel, parallel * 2);
		
		final CrossCorrProgram main = new CrossCorrProgram();
		main.loadData(file[0]);
		FrameWaitForClose c = new FrameWaitForClose(FrameFactroy.getFrame(main));
		c.waitForClose();
		
		master.start();
		for (int i = 0; i < file.length; i++)
		{
			final int frameNum = i;
			Task t = new Task()
			{
				int pos = frameNum;

				CrossCorrProgram program = new CrossCorrProgram();

				@Override
				public void doTask()
				{
					JFrame f = FrameFactroy.getFrame(program);
					System.out.println("Starting : "+file[pos]);
					f.setTitle(file[pos].toString());
					program.backgroundThreshold
							.setValue(main.backgroundThreshold.getValue());
					program.corFrameNum.setValue(main.corFrameNum.getValue());
					program.corKerSizeX.setSelectedIndex(main.corKerSizeX
							.getSelectedIndex());
					program.corKerSizeY.setSelectedIndex(main.corKerSizeY
							.getSelectedIndex());
					program.currentFrame.setValue(main.currentFrame.getValue());
					program.flowMax.setValue(main.flowMax.getValue());
					program.flowMin.setValue(main.flowMin.getValue());
					program.processingMode.setSelectedIndex(main.processingMode
							.getSelectedIndex());
					try
					{
						program.loadData(file[pos]);
						program.crossCorrTool.doFullVolume();
					} catch (Exception e)
					{
						JOptionPane
								.showMessageDialog(null, "Processing Failed\n"
										+ program.file.toString());
						e.printStackTrace();
					}
					program = null;
					System.out.println("Finished : "+file[pos]);
					f.setVisible(false);
				}
			};
			
			
			master.addTask(t);
		}
	}

	public static File[] getFiles()
	{
		JPanel panel = new JPanel();
		final Vector<File> file = new Vector<File>();
		final DefaultListModel model = new DefaultListModel();
		final JList list = new JList(model);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(list);
		panel.setLayout(new BorderLayout());
		panel.add(scroll);
		FileDrop drop = new FileDrop(panel, new FileDrop.Listener()
		{

			@Override
			public void filesDropped(File[] files)
			{
				// TODO Auto-generated method stub
				for (File f : files)
				{
					file.add(f);
					model.addElement(f.toString());
				}
			}
		});
		JFrame f = FrameFactroy.getFrame(panel);
		f.setTitle("Drag and drop files here");
		FrameWaitForClose close = new FrameWaitForClose(f);
		close.waitForClose();

		return file.toArray(new File[0]);
	}
}
