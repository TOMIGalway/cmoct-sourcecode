package com.joey.software.Presentation;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.Projections.surface.SurfaceFinderTool;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.toolkit.VolumeInputSelectorPanel;


public class OpticalClearingccOCTAnalysis
{
	public static void main(String input[]) throws IOException
	{
		// compareDatasets();
		generateAScanData();
	}

	public static void compareDatasets() throws IOException
	{
		final StatusBarPanel status = new StatusBarPanel();

		final byte[][][] dataA = VolumeInputSelectorPanel
				.getUserVolumeData(status);
		final byte[][][] flowA = VolumeInputSelectorPanel
				.getUserVolumeData(status);
		final float[][] mapA = SurfaceFinderTool
				.loadSurfaceMap(FileSelectionField.getUserFile());

		final byte[][][] dataB = VolumeInputSelectorPanel
				.getUserVolumeData(status);
		final byte[][][] flowB = VolumeInputSelectorPanel
				.getUserVolumeData(status);
		final float[][] mapB = SurfaceFinderTool
				.loadSurfaceMap(FileSelectionField.getUserFile());

		final float[][] sliceA = new float[Math
				.max(dataA[0].length, flowA == null ? 0 : flowA[0].length)][Math
				.max(dataA.length, flowA == null ? 0 : flowA.length)];
		final float[][] sliceB = new float[Math
				.max(dataB[0].length, flowB == null ? 0 : flowB[0].length)][Math
				.max(dataB.length, flowB == null ? 0 : flowB.length)];
		final DynamicRangeImage sliceAPanel = new DynamicRangeImage(sliceA);
		final DynamicRangeImage sliceBPanel = new DynamicRangeImage(sliceB);

		final JSpinner depth = new JSpinner();
		final JSpinner avg = new JSpinner();
		final JComboBox method = new JComboBox(new String[] { "Avg", "Max",
				"Min", "Sum" });
		final JComboBox mode = new JComboBox(new String[] { "Struct", "Flow" });

		final ChangeListener change = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (mode.getSelectedIndex() == 0)
				{
					SurfaceFinderTool
							.getYProjectionSlice(dataA, mapA, sliceA, (Integer) depth
									.getValue(), (Integer) depth.getValue()
									+ (Integer) avg.getValue(), method
									.getSelectedIndex());
					SurfaceFinderTool
							.getYProjectionSlice(dataB, mapB, sliceB, (Integer) depth
									.getValue(), (Integer) depth.getValue()
									+ (Integer) avg.getValue(), method
									.getSelectedIndex());
				} else
				{
					SurfaceFinderTool
							.getYProjectionSlice(flowA == null ? dataA : flowA, mapA, sliceA, (Integer) depth
									.getValue(), (Integer) depth.getValue()
									+ (Integer) avg.getValue(), method
									.getSelectedIndex());
					SurfaceFinderTool
							.getYProjectionSlice(flowB == null ? dataB : flowB, mapB, sliceB, (Integer) depth
									.getValue(), (Integer) depth.getValue()
									+ (Integer) avg.getValue(), method
									.getSelectedIndex());
				}
				sliceAPanel.updateImagePanel();
				sliceBPanel.updateImagePanel();
			}
		};

		final ActionListener action = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				change.stateChanged(null);
			}
		};

		depth.addChangeListener(change);
		avg.addChangeListener(change);
		method.addActionListener(action);
		mode.addActionListener(action);

		FrameFactroy.getFrame(new JComponent[] {
				SwingToolkit.getLabel(depth, "Position : ", 100),
				SwingToolkit.getLabel(avg, "Avg Num : ", 100),
				SwingToolkit.getLabel(method, "Projection : ", 100),
				SwingToolkit.getLabel(mode, "Mode : ", 100) }, 1, 4);
		FrameFactroy.getFrame(sliceAPanel, sliceBPanel);

	}

	public static void generateAScanData() throws IOException
	{
		final StatusBarPanel status = new StatusBarPanel();

		final byte[][][] struct = VolumeInputSelectorPanel
				.getUserVolumeData(status);
		final byte[][][] flow = VolumeInputSelectorPanel
				.getUserVolumeData(status);

		final int sizeX = struct[0].length;
		final int sizeZ = struct.length;
		final int sizeY = struct[0][0].length;

		int offset = 51;
		int vesselDensityThreshold = 50;
		int startY = 0;
		int numY = 512;

		final float[][] surfaceMap = SurfaceFinderTool
				.loadSurfaceMap(FileSelectionField.getUserFile());

		final float[][] sliceStruct = new float[sizeX][sizeZ];
		final float[][] sliceFlow = new float[sizeX][sizeZ];
		float[] depthDataStruct = new float[sizeY];
		float[] depthDataFlow = new float[sizeY];
		float[] depthDataVessel = new float[sizeY];

		// TODO Auto-generated method stub
		int count = 0;

		DynamicRangeImage structImage = new DynamicRangeImage(sliceStruct);
		DynamicRangeImage flowImage = new DynamicRangeImage(sliceFlow);
		FrameFactroy.getFrame(structImage, flowImage);
		for (int y = startY; y < startY + numY; y++)
		{
			depthDataVessel[y] = 0;

			depthDataStruct[y] = 0;
			depthDataFlow[y] = 0;
			count = 0;
			SurfaceFinderTool
					.getYProjectionSlice(struct, surfaceMap, sliceStruct, y
							- offset, y + 1 - offset, SurfaceFinderTool.TYPE_AVERAGE);
			SurfaceFinderTool
					.getYProjectionSlice(flow, surfaceMap, sliceFlow, y
							- offset, y + 1 - offset, SurfaceFinderTool.TYPE_AVERAGE);

			structImage.updateImagePanel();
			flowImage.updateImagePanel();
			for (int x = 0; x < struct[0].length; x++)
			{
				for (int z = 0; z < struct.length; z++)
				{
					count++;
					depthDataStruct[y] += sliceStruct[x][z];
					depthDataFlow[y] += sliceFlow[x][z];

					if (sliceFlow[x][z] > vesselDensityThreshold)
					{
						depthDataVessel[y]++;
					}
				}
			}
			depthDataStruct[y] /= count;
			depthDataFlow[y] /= count;
			depthDataVessel[y] /= sizeX*sizeZ;

		}
		FrameFactroy.getFrame(depthDataFlow);
		FrameFactroy.getFrame(depthDataStruct);
		FrameFactroy.getFrame(depthDataVessel);
		System.out.println("Depth,Struct, Flow");
		for (int i = 0; i < 512; i++)
		{
			System.out.println(i + "," + depthDataStruct[i] + ","
					+ depthDataFlow[i] + "," + depthDataVessel[i]);
		}
	}

}
