package com.joey.software.MultiThreadCrossCorrelation;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.MultiThreadCrossCorrelation.threads.CrossCorrelationMaster;
import com.joey.software.fileToolkit.dragAndDrop.FileDrop;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.FrameWaitForClose;


public class PerformanceTesting
{
	public static void main(String input[]) throws IOException
	{
	
		System.out.println(Runtime.getRuntime().availableProcessors());
		
		run();

	}
	
	public static void run() throws IOException
	{
		
		PrintStream out = new PrintStream(new File("result.csv"));
		out.printf("Path,sizeX,sizeY,sizeZ,Cores,Ker X,KerY,Total Time, Allocate Memory, Read Data To Memory, Create Directories, Processing\n");
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
					modelList.add(dataHolder.size(), f.toString());
					dataHolder.add(new File[] { f });
				}
			}
		});
		JFrame wait = FrameFactroy.getFrame(pan);
		FrameWaitForClose c = new FrameWaitForClose(wait);
		c.waitForClose();


		int threshold = -1;
		
		float[] times = new float[4];
		for(int i =0; i < dataHolder.size(); i++)
		{			
			//Select File
			File f= dataHolder.get(i)[0];
			ThorlabsIMGImageProducer inputData = new ThorlabsIMGImageProducer(f);
			inputData.printSize();
			System.out.println("Starting : "+f.toString());
			for(int core = 1; core <= 2*Runtime.getRuntime().availableProcessors(); core++)
			{
				System.out.println("\tS-Cores [ "+core+"]");
				for(int ker = 1; ker <=5; ker++)
				{
					int kerX = 2*ker+1;
					int kerY = 2*ker+1;
					System.out.println("\t\tS-Ker [ "+kerX+"x"+kerY+"]");
					
					CrossCorrelationMaster master = new CrossCorrelationMaster(core);
					CrossCorrelationDataset task = new CrossCorrelationDataset(null);

					task.setData(inputData);
					task.setSaveMIP(false);
					task.setSaveRawMIP(false);
					task.setSaveFlow(false);
					task.setSaveStruct(false);
					task.setCrossCorrRawInMemory(false);
					task.setCrossCorrByteinMemory(false);
					task.setDataInMemory(true);
					task.setMIPInMemory(true);
					task.setImageAlign(false);

					task.setMaxPosMIP(512);
					task.setMinPosMIP(0);

					task.setCrossCorrMin(0.6f);
					task.setCrossCorrMax(-0.6f);

					task.setKerX(kerX);
					task.setKerY(kerY);
					task.setThreshold(threshold);
					task.setInterlace(1);
					
					master.setCurrentTask(task);
								
					System.out.println("\t\t\t Starting");
					long run = System.currentTimeMillis();
					master.processDataSet(true, false, true, times);
					long time = System.currentTimeMillis()-run;
					System.out.println("\t\t\t Finished");
					System.out.println("\t\tE-Ker [ "+kerX+"x"+kerY+"]");
					
					out.printf(" \"%s\",%d,%d,%d,%d,%d,%d,%d,%f,%f,%f,%f\n", f.getPath(),inputData.getSizeX(), inputData.getSizeY(), inputData.getSizeZ(),core,kerX,kerY, time, times[0],times[1],times[2],times[3]);
					out.flush();
				}
				System.out.println("\tE-Cores [ "+core+"]");
			}
			System.out.println("Finished : "+f.toString());
			out.flush();
			out.close();
		}
	}
}
