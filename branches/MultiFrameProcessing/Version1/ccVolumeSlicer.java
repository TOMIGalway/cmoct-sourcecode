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
package com.joey.software.MultiThreadCrossCorrelation;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.DynamicRangeImage;

public class ccVolumeSlicer extends JPanel
{
	JComboBox sliceAxis = new JComboBox(new String[] { "XY", "XZ", "ZY" });

	CrossCorrelationDataset data = null;

	float[][] floatHold;

	byte[][] structHold;

	DynamicRangeImage flowImage = new DynamicRangeImage();

	DynamicRangeImage structImage = new DynamicRangeImage();

	SpinnerNumberModel sliceSpinnerModel = new SpinnerNumberModel(0, 0, 1, 1);

	JSpinner sliceSpinner = new JSpinner(sliceSpinnerModel);

	JTabbedPane viewTab = new JTabbedPane();

	public ccVolumeSlicer()
	{
		createJPanel();
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());

		JPanel holer = new JPanel(new GridLayout(1, 2));
		holer.add(sliceAxis);
		holer.add(sliceSpinner);
		add(holer, BorderLayout.NORTH);

		viewTab.addTab("Flow", flowImage);
		viewTab.addTab("Struct", structImage);

		add(viewTab, BorderLayout.CENTER);

		sliceAxis.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				axisSwitch();
				updateSlice();
			}
		});
		sliceSpinner.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateSlice();
			}
		});

		viewTab.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				// TODO Auto-generated method stub
				updateSlice();
			}
		});
	}

	/**
	 * This function should be called when the
	 * axis is switched. It will ensure that all
	 * storage arrays are the correct size.
	 */
	public void axisSwitch()
	{
		if (sliceAxis.getSelectedIndex() == 0)// XY
		{
			floatHold = new float[data.getSizeX()][data.getSizeY()];
			structHold = new byte[data.getSizeX()][data.getSizeY()];
			sliceSpinnerModel.setMaximum(data.getSizeZ());
			sliceSpinnerModel.setMinimum(0);
			sliceSpinnerModel.setValue(0);
		} else if (sliceAxis.getSelectedIndex() == 1)// XZ
		{
			floatHold = new float[data.getSizeX()][data.getSizeZ()];
			structHold = new byte[data.getSizeX()][data.getSizeZ()];
			sliceSpinnerModel.setMaximum(data.getSizeY());
			sliceSpinnerModel.setMinimum(0);
			sliceSpinnerModel.setValue(0);
		} else if (sliceAxis.getSelectedIndex() == 2)// YZ
		{
			floatHold = new float[data.getSizeZ()][data.getSizeY()];
			structHold = new byte[data.getSizeZ()][data.getSizeY()];
			sliceSpinnerModel.setMaximum(data.getSizeX());
			sliceSpinnerModel.setMinimum(0);
			sliceSpinnerModel.setValue(0);
		}

		flowImage.setBlockUpdateRange(true);
		flowImage.setDataFloat(floatHold);
		flowImage.setBlockUpdateRange(false);

		structImage.setBlockUpdateRange(true);
		structImage.setDataByte(structHold);
		structImage.setBlockUpdateRange(false);

	}

	public void updateSlice()
	{
		if (!data.isCrossCorrRawInMemory())
		{
			return;
		}

		if (sliceAxis.getSelectedIndex() == 0)// XY
		{
			int zP = (Integer) sliceSpinnerModel.getValue();
			if (viewTab.getSelectedIndex() == 0)// Flow
			{
				for (int xP = 0; xP < data.getSizeX(); xP++)
				{
					for (int yP = 0; yP < data.getSizeY(); yP++)
					{
						floatHold[xP][yP] = (data.crossCorrRawData[zP][xP][yP] / ((float) Short.MAX_VALUE));
					}
				}
			} else
			// struct
			{
				for (int xP = 0; xP < data.getSizeX(); xP++)
				{
					for (int yP = 0; yP < data.getSizeY(); yP++)
					{
						structHold[xP][yP] = (data.structData[zP][xP][yP]);
					}
				}
			}
		} else if (sliceAxis.getSelectedIndex() == 1)// XZ
		{
			int yP = (Integer) sliceSpinnerModel.getValue();
			if (viewTab.getSelectedIndex() == 0)// Flow
			{
				for (int xP = 0; xP < data.getSizeX(); xP++)
				{
					for (int zP = 0; zP < data.getSizeZ(); zP++)
					{
						floatHold[xP][zP] = (data.crossCorrRawData[zP][xP][yP] / ((float) Short.MAX_VALUE));

					}
				}
			} else
			{
				for (int xP = 0; xP < data.getSizeX(); xP++)
				{
					for (int zP = 0; zP < data.getSizeZ(); zP++)
					{
						structHold[xP][zP] = data.structData[zP][xP][yP];
					}
				}
			}
		} else if (sliceAxis.getSelectedIndex() == 2)// YZ
		{
			int xP = (Integer) sliceSpinnerModel.getValue();
			if (viewTab.getSelectedIndex() == 0)// Flow
			{
				for (int yP = 0; yP < data.getSizeY(); yP++)
				{
					for (int zP = 0; zP < data.getSizeZ(); zP++)
					{
						floatHold[zP][yP] = (data.crossCorrRawData[zP][xP][yP] / ((float) Short.MAX_VALUE));

					}
				}
			} else
			{
				for (int yP = 0; yP < data.getSizeY(); yP++)
				{
					for (int zP = 0; zP < data.getSizeZ(); zP++)
					{
						structHold[zP][yP] = data.structData[zP][xP][yP];
					}
				}
			}
		}

		if (viewTab.getSelectedIndex() == 0)
		{
			flowImage.updateImagePanel();
		} else
		{
			structImage.updateImagePanel();
		}
	}

	public void setData(CrossCorrelationDataset data)
	{
		this.data = data;
		axisSwitch();// This ensure correct size
						// on data holders
	}

	public static void main(String input[])
	{
		FrameFactroy.getFrame(new ccVolumeSlicer());
	}

}
