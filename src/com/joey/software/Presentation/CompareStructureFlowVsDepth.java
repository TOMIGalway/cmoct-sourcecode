package com.joey.software.Presentation;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.plottingToolkit.PlotingToolkit;


public class CompareStructureFlowVsDepth
{

	public static void main(String input[]) throws IOException
	{

		
		ThorlabsIMGImageProducer struct = new ThorlabsIMGImageProducer(
				FileSelectionField.getUserFile());
		int structX = struct.getSizeX();
		int structY = struct.getSizeY();
		int structZ = struct.getSizeZ();

		ImageFileProducer flow = new ImageFileProducer(
				ImageFileSelectorPanel.getUserSelection());
		BufferedImage tmp = flow.getImage(0);
		int flowX = tmp.getWidth();
		int flowY = tmp.getHeight();
		int flowZ = flow.getImageCount();

		float[] structDepthData = new float[structY];
		float[] flowDepthData = new float[flowY];

		byte[][][] structData = struct.createDataHolder();
		byte[][][] flowData = new byte[flowZ][flowX][flowY];

		StatusBarPanel status = new StatusBarPanel();
		FrameFactroy.getFrame(status);
		// Load Struct
		struct.getData(structData, status);
		// Load Flow
		flow.getData(flowData, status);

		getDepthData(structData, structDepthData);
		getDepthData(flowData, flowDepthData);

		FrameFactroy
				.getFrame(getPlotPanel(structDepthData, flowDepthData, 1));
		System.out.println(structX + " , " + structY + " , " + structZ);
		System.out.println(flowX + " , " + flowY + " , " + flowZ);
	}

	public static void getDepthData(byte[][][] data, float[] hold)
	{
		int sizeX = data[0].length;
		int sizeY = data[0][0].length;
		int sizeZ = data.length;

		for (int y = 0; y < sizeY; y++)
		{
			hold[y] = 0;
			for (int x = 0; x < sizeX; x++)
			{
				for (int z = 0; z < sizeZ; z++)
				{
					hold[y] += b2i(data[z][x][y]);
				}
			}
			hold[y] /= sizeX * sizeZ;
		}
	}

	public static int b2i(byte v)
	{
		return v >= 0 ? v : v + 255;
	}

	public static JPanel getPlotPanel(float[] struct, float[] flow, float depthScale)
	{
		return PlotingToolkit
				.getChartPanel(new float[][] { struct, flow }, new String[] {
						"Struct", "Flow" }, "", "", "");
	}
}
