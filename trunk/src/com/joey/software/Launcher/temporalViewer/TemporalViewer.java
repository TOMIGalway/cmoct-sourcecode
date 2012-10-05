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
package com.joey.software.Launcher.temporalViewer;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import com.joey.software.Arrays.ArrayToolkit;
import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.Presentation.ReactiveHyperimeaTool;
import com.joey.software.VideoToolkit.CompoentRecorder;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.volumeTools.OCTVolumeDivider;
import com.sun.awt.AWTUtilities;


public class TemporalViewer
{
	public static void main(String[] input) throws IOException, AWTException,
			InterruptedException
	{
		testCloseTime();
	}

	public static void testCloseTime() throws AWTException, IOException, InterruptedException
	{
		int sizeX = 1024;
		int sizeY = 1024;
		int sizeZ = 512;
		
		byte[][][] dataA = new byte[sizeX][sizeY][sizeZ];
		byte[][][] dataB = new byte[sizeX][sizeY][sizeZ];
		byte[][][] liveData = new byte[sizeX][sizeY][sizeZ];
		
		OCTVolumeDivider divide = new OCTVolumeDivider();

		ArrayToolkit.fillRandom(dataA);
		ArrayToolkit.fillRandom(dataB);
		
		JFrame main = FrameFactroy.getFrame(divide);
		ArrayToolkit.clone(dataA, liveData);
		divide.setData(liveData);
	
		JFrame block= new JFrame();
		block.setBackground(Color.RED);
		block.setUndecorated(true);
		AWTUtilities.setWindowOpacity(block, 0.1f);
		
		Robot robot = new Robot();
		
		FrameWaitForClose.showWaitFrame();
		final CompoentRecorder recorder = divide.getVolumeViewer().getViewPanel().getRecorder();
		recorder.setPause(true);
		recorder.startRecording("c:\\test\\", "movie.avi", 15);
		
		main.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				super.windowClosed(e);
				try
				{
					recorder.stopRecording();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		for (int i = 0; i < 10; i++)
		{
			
			ArrayToolkit.clone(i%2==0?dataA:dataB,liveData);
			
			//block.setBounds(main.getBounds());	
			//block.setVisible(true);
			recorder.setPause(true);
			divide.updateData();
			Thread.sleep(500);
			//block.setVisible(false);
			recorder.setPause(false);
		//	FrameWaitForClose.showWaitFrame();
			
		}
		recorder.stopRecording();
		
	}

	public void runProgram() throws Exception
	{
		int kerX = 3;
		int kerY = 3;
		int threshold = 30;
		boolean aligned = true;

		final Vector<File[]> dataHolder = new Vector<File[]>();

		JPanel pan = new JPanel(new BorderLayout());

		final DefaultListModel modelList = new DefaultListModel();
		final JList fileList = new JList(modelList);

		pan.add(new JScrollPane(fileList));
		FileDrop drop = new FileDrop(pan, new FileDrop.Listener()
		{

			@Override
			public void filesDropped(File[] files)
			{
				for (File f : files)
				{
					modelList.add(0, f.toString());
					dataHolder.add(new File[] { f });
				}
			}
		});
		JFrame wait = FrameFactroy.getFrame(pan);
		FrameWaitForClose c = new FrameWaitForClose(wait);
		c.waitForClose();

		OCTVolumeDivider divide = new OCTVolumeDivider();
		FrameFactroy.getFrame(divide);

		byte[][][] data = null;

		for (int i = 0; i < dataHolder.size(); i++)
		{
			File file = dataHolder.get(i)[0];

			ThorlabsIMGImageProducer imgLoader = new ThorlabsIMGImageProducer(
					file, true);
			ImageFileProducer imageLoader = new ImageFileProducer(
					ReactiveHyperimeaTool
							.getFlowImageFiles(imgLoader, kerX, kerY, threshold, aligned));

			if (i == 0)
			{
				data = imageLoader.createDataHolder();
				imageLoader.getData(data, null);
				divide.setData(data);
				FrameWaitForClose.showWaitFrame();
			}
			imageLoader.getData(data, null);

			System.out.println("Starting Update");
			divide.updateData();
			System.out.println("Finished Update");
			// Thread.sleep(1000);
			// ImageOperations.saveImage(divide.getSnapshot(),
			// "c:\\test\\frame"
			// + i + ".png");
		}
	}
}
