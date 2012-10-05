package com.joey.software.Presentation;


import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.stringToolkit.StringOperations;


public class DataMIPViewer
{
	public static void main(String input[]) throws IOException
	{
		File[] fileData = new File[100];

		for (int i = 0; i < 100; i++)
		{
			moveMIPtoTest(new File(
					"D:\\Share\\RH P\\Attempt 1 - Full Occuluson\\data_3D_"
							+ StringOperations.getNumberString(3, i) + ".IMG"), 3,3,30,false,new File("c:\\test\\"+i+".png"));
		}

	}
	
	public static void moveMIPtoTest(File img,int kerX, int kerY, int threshold, boolean aligned, File rst) throws IOException
	{
		
		
			String[] data = FileOperations.splitFile(img);

			// Create Output Location
			String savePath = data[0] + "\\" + data[1] + "\\" + (2 * kerX + 1) + "-"
					+ (2 * kerY + 1) + "-" + threshold + " - " + (aligned ? " " : "Not ")
					+ "Aligned" + "\\mip.png";
			
			File src = new File(savePath);
			
			ImageOperations.saveImage(ImageIO.read(src), rst);
	}
}
