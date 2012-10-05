package com.joey.software.volumeTools;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.j3dTookit.J3DTool;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;


/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class OCTVolumeSizeControler extends JPanel
{
	public static final File QUALITY_FILE = new File("settings\\render.dat");

	public static final int MODE_AUTO = 0;

	public static final int MODE_MANUAL = 1;

	public static final int TYPE_MAXSIZE = 0;

	public static final int TYPE_MEMORY = 1;

	JTabbedPane tabs = new JTabbedPane();

	private JTextField autoSizeXValueField = new JTextField();

	private JTextField autoSizeYValueField = new JTextField();

	private JTextField autoSizeZValueField = new JTextField();

	private JTextField autoSizeXMaxedField = new JTextField();

	private JTextField autoSizeYMaxedField = new JTextField();

	private JTextField autoSizeZMaxedField = new JTextField();

	private int xReduce = 50;

	private int yReduce = 50;

	private int zReduce = 50;

	float xScale = 1;

	float yScale = 1;

	float zScale = 1;

	JSlider xScaleSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 100);

	JSlider yScaleSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 100);

	JSlider zScaleSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 100);

	private JSpinner xSizeSpinner = new JSpinner(new SpinnerNumberModel(
			xReduce, 1, Integer.MAX_VALUE, 1));

	private JSpinner ySizeSpinner = new JSpinner(new SpinnerNumberModel(
			yReduce, 1, Integer.MAX_VALUE, 1));

	private JSpinner zSizeSpinner = new JSpinner(new SpinnerNumberModel(
			zReduce, 1, Integer.MAX_VALUE, 1));

	/**
	 * Scale Data
	 */
	private JSpinner xScaleSpinner = new JSpinner(new SpinnerNumberModel(1., 0,
			1000, 0.1));

	private JSpinner yScaleSpinner = new JSpinner(new SpinnerNumberModel(1., 0,
			1000, 0.1));

	private JSpinner zScaleSpinner = new JSpinner(new SpinnerNumberModel(1., 0,
			1000, 0.1));

	private JTextField xRealSize = new JTextField(4);

	private JTextField yRealSize = new JTextField(4);

	private JTextField zRealSize = new JTextField(4);

	private JButton xCopyData = new JButton("SET");

	private JButton yCopyData = new JButton("SET");

	private JButton zCopyData = new JButton("SET");

	private JButton copyData = new JButton("SET ALL");

	private OCTVolumeViewer owner;

	private int xTrueSize = 0;

	private int yTrueSize = 0;

	private int zTrueSize = 0;

	int qualityCalculatorType = TYPE_MAXSIZE;

	private JPanel jPanel3;

	private JPanel jPanel2;

	private JPanel settingsPanel;

	private JPanel jPanel1;

	private JPanel highResField;

	private JPanel qualityPanel;

	private JPanel datPan;

	private JPanel autoPanel;

	long videoMemSize = 0;

	JPanel qualitySlider = new JPanel(new BorderLayout());

	JSlider quality = new JSlider(0, 9);

	Hashtable<Integer, JLabel> names = new Hashtable<Integer, JLabel>();

	/*
	 * This will store the max size for each quality for each axis This is
	 * stored as 3doubles
	 */
	Hashtable<Integer, int[]> maxSizeData = new Hashtable<Integer, int[]>();

	public OCTVolumeSizeControler(OCTVolumeViewer owner)
	{
		this.owner = owner;
		createJPanel();
		loadQualitySettings();
	}

	public int getRenderQuality()
	{
		return quality.getValue();
	}

	public void setRenderQuality(int val)
	{
		quality.setValue(val);
	}

	public void setPanelModle(int mode)
	{
		tabs.setSelectedIndex(mode);
	}

	public int getPanelMode()
	{
		return tabs.getSelectedIndex();
	}

	public void setVideoMemorySize(long bytes)
	{
		this.videoMemSize = bytes;
		setQualityCalculatorType(TYPE_MEMORY);
	}

	public void setQualityCalculatorType(int type)
	{
		this.qualityCalculatorType = type;
	}

	public void update()
	{

		if (tabs.getSelectedIndex() == MODE_AUTO)
		{
			System.out.println("Updating Size :" + quality.getValue());

			int xVal = 1;
			int yVal = 1;
			int zVal = 1;

			/*
			 * The following bit of code will attempt to get the correct
			 * rendering size for the current data set
			 * 
			 * To do this the X,Y,Z data is sorted assending
			 */

			if (qualityCalculatorType == TYPE_MAXSIZE)
			{
				int[] dataSize = new int[]
				{ xTrueSize, yTrueSize, zTrueSize };
				int[] rendSize = maxSizeData.get(quality.getValue());

				int[] oDataSize = DataAnalysisToolkit
						.sortAssDataSet(dataSize, true);
				DataAnalysisToolkit.sortAssDataSet(rendSize, false);

				dataSize[oDataSize[0]] = rendSize[0];
				dataSize[oDataSize[1]] = rendSize[1];
				dataSize[oDataSize[2]] = rendSize[2];

				xVal = dataSize[0];
				yVal = dataSize[1];
				zVal = dataSize[2];

				xReduce = Math.min(xTrueSize, xVal);
				yReduce = Math.min(yTrueSize, yVal);
				zReduce = Math.min(zTrueSize, zVal);

				xScale = (float) xTrueSize / (float) xReduce;
				yScale = (float) yTrueSize / (float) yReduce;
				zScale = (float) zTrueSize / (float) zReduce;
			} else if (qualityCalculatorType == TYPE_MEMORY)
			{
				float qual = (quality.getValue() + 1.f) / quality.getMaximum();

				long[] in = new long[]
				{ xTrueSize, yTrueSize, zTrueSize };
				long[] out = new long[3];

				int bytesPerVoxel = 4;
				if (owner.panel.getRender().getVolume().getMap() != null)
				{
					try
					{
						bytesPerVoxel = owner.panel.getRender().getVolume()
								.getMap().getBytesPerVoxel();
					} catch (Exception e)
					{

					}
				}
				long mem = J3DTool
						.getBestTextureSize2D(in, out, (long) (videoMemSize * qual), bytesPerVoxel);

				xReduce = (int) out[0];
				yReduce = (int) out[1];
				zReduce = (int) out[2];

				xScale = (float) xTrueSize / (float) xReduce;
				yScale = (float) yTrueSize / (float) yReduce;
				zScale = (float) zTrueSize / (float) zReduce;

				System.out
						.printf("\n\nRender Size :[%d,%d,%d] - MEM[%d]\n\n", xReduce, yReduce, zReduce, mem);
			}

			autoSizeXValueField.setText("" + xReduce);
			autoSizeYValueField.setText("" + yReduce);
			autoSizeZValueField.setText("" + zReduce);

			autoSizeXMaxedField.setText(""
					+ (Math.abs(xReduce - xTrueSize) < 5));
			autoSizeYMaxedField.setText(""
					+ (Math.abs(yReduce - yTrueSize) < 5));
			autoSizeZMaxedField.setText(""
					+ (Math.abs(zReduce - zTrueSize) < 5));

		} else if (tabs.getSelectedIndex() == MODE_MANUAL)
		{
			xReduce = (Integer) xSizeSpinner.getValue();
			yReduce = (Integer) ySizeSpinner.getValue();
			zReduce = (Integer) zSizeSpinner.getValue();

			xScale = xScaleSlider.getValue() / 100.0f;
			yScale = yScaleSlider.getValue() / 100.0f;
			zScale = zScaleSlider.getValue() / 100.0f;
		}

		xScale *= (Double) xScaleSpinner.getValue();
		yScale *= (Double) yScaleSpinner.getValue();
		zScale *= (Double) zScaleSpinner.getValue();
	}

	static int minIndex(int... data)
	{
		int index = 0;
		int min = data[index];

		for (int i = 0; i < data.length; i++)
		{
			if (data[i] < min)
			{
				index = i;
			}
		}
		return index;
	}

	public void setCleared()
	{
		xReduce = 1;
		yReduce = 1;
		zReduce = 1;

		xScale = 1;
		yScale = 1;
		zScale = 1;
	}

	public void setXTrueSize(int trueSize)
	{
		xTrueSize = trueSize;
		xRealSize.setText("" + (trueSize));
	}

	public void setYTrueSize(int trueSize)
	{
		yTrueSize = trueSize;
		yRealSize.setText("" + (trueSize));
	}

	public void setZTrueSize(int trueSize)
	{
		zTrueSize = trueSize;
		zRealSize.setText("" + (trueSize));
	}

	public void createJPanel()
	{
		JPanel scaleHolder = new JPanel();
		scaleHolder.setBorder(BorderFactory.createTitledBorder("Scale View"));
		scaleHolder.setLayout(new BoxLayout(scaleHolder, BoxLayout.Y_AXIS));
		scaleHolder.add(xScaleSlider);
		scaleHolder.add(yScaleSlider);
		scaleHolder.add(zScaleSlider);

		JPanel sizeHolder = new JPanel();
		sizeHolder.setLayout(new GridLayout(3, 1));
		sizeHolder.add(xSizeSpinner);
		sizeHolder.add(ySizeSpinner);
		sizeHolder.add(zSizeSpinner);

		JPanel realHolder = new JPanel();
		realHolder.setLayout(new GridLayout(3, 1));
		realHolder.add(xRealSize);
		realHolder.add(yRealSize);
		realHolder.add(zRealSize);

		JPanel setHolder = new JPanel();
		setHolder.setLayout(new GridLayout(3, 1));
		setHolder.add(xCopyData);
		setHolder.add(yCopyData);
		setHolder.add(zCopyData);

		xRealSize.setEditable(false);
		yRealSize.setEditable(false);
		zRealSize.setEditable(false);

		JPanel reduceHolder = new JPanel(new BorderLayout());
		reduceHolder.setBorder(BorderFactory.createTitledBorder("Data Size"));
		reduceHolder.add(realHolder, BorderLayout.WEST);
		reduceHolder.add(sizeHolder, BorderLayout.CENTER);
		reduceHolder.add(setHolder, BorderLayout.EAST);
		reduceHolder.add(copyData, BorderLayout.SOUTH);

		JPanel holder = new JPanel();
		holder.setBorder(BorderFactory.createTitledBorder("Render Settings"));
		holder.setLayout(new BorderLayout());
		// holder.add(scaleHolder);
		holder.add(reduceHolder, BorderLayout.NORTH);

		JPanel autoPan = new JPanel();
		autoPan.setLayout(new BorderLayout());

		quality.setMajorTickSpacing(1);
		quality.setSnapToTicks(true);
		quality.setPaintTicks(true);
		quality.setPaintLabels(true);

		autoSizeXValueField.setEditable(false);
		autoSizeXMaxedField.setEditable(false);
		autoSizeYValueField.setEditable(false);
		autoSizeYMaxedField.setEditable(false);
		autoSizeZValueField.setEditable(false);
		autoSizeZMaxedField.setEditable(false);

		tabs.addTab("Auto", autoPan);
		{
			jPanel1 = new JPanel();
			BorderLayout jPanel1Layout = new BorderLayout();
			autoPan.add(jPanel1, BorderLayout.NORTH);
			// jPanel1.setPreferredSize(new java.awt.Dimension(269, 254));
			jPanel1.setLayout(jPanel1Layout);
			{
				autoPanel = new JPanel();
				jPanel1.add(autoPanel, BorderLayout.NORTH);
				BorderLayout autoPanelLayout = new BorderLayout();
				autoPanel.setLayout(autoPanelLayout);
				{
					settingsPanel = new JPanel();
					BorderLayout settingsPanelLayout = new BorderLayout();
					settingsPanel.setLayout(settingsPanelLayout);
					autoPanel.add(settingsPanel, BorderLayout.CENTER);
					// settingsPanel.setPreferredSize(new
					// java.awt.Dimension(269, 155));
					settingsPanel.setBorder(BorderFactory
							.createTitledBorder("Current Settings"));
					{
						highResField = new JPanel();
						settingsPanel.add(highResField, BorderLayout.SOUTH);
						BorderLayout highResFieldLayout = new BorderLayout();
						highResField.setLayout(highResFieldLayout);
						// highResField.setPreferredSize(new
						// java.awt.Dimension(269, 22));

					}
					{
						datPan = new JPanel();
						settingsPanel.add(datPan, BorderLayout.CENTER);
						BorderLayout datPanLayout = new BorderLayout();
						datPan.setLayout(datPanLayout);
						datPan.setBorder(BorderFactory.createTitledBorder(""));
						{
							jPanel2 = new JPanel();
							GridLayout jPanel2Layout = new GridLayout(3, 1);
							jPanel2Layout.setHgap(5);
							jPanel2Layout.setVgap(5);
							jPanel2Layout.setColumns(1);
							jPanel2Layout.setRows(3);
							jPanel2.setLayout(jPanel2Layout);
							datPan.add(jPanel2, BorderLayout.WEST);

							{
								jPanel2.add(new JLabel("Size X :"));
							}
							{
								jPanel2.add(new JLabel("Size Y :"));
							}
							{
								jPanel2.add(new JLabel("Size Z :"));
							}
						}
						{
							jPanel3 = new JPanel();
							GridLayout jPanel3Layout = new GridLayout(3, 2);
							jPanel3Layout.setHgap(5);
							jPanel3Layout.setVgap(5);
							jPanel3Layout.setColumns(2);
							jPanel3Layout.setRows(3);
							jPanel3.setLayout(jPanel3Layout);
							datPan.add(jPanel3, BorderLayout.CENTER);
							// jPanel3.setPreferredSize(new
							// java.awt.Dimension(247, 97));
							jPanel3.add(autoSizeXValueField);
							jPanel3.add(autoSizeXMaxedField);
							jPanel3.add(autoSizeYValueField);
							jPanel3.add(autoSizeYMaxedField);
							jPanel3.add(autoSizeZValueField);
							jPanel3.add(autoSizeZMaxedField);
						}

					}
				}
				{
					qualityPanel = new JPanel();
					autoPanel.add(qualityPanel, BorderLayout.NORTH);
					BorderLayout qualityPanelLayout = new BorderLayout();
					qualityPanel.setLayout(qualityPanelLayout);
					qualityPanel.setBorder(BorderFactory
							.createTitledBorder("Render Quality"));
					qualityPanel.add(quality, BorderLayout.CENTER);
				}
			}
		}
		tabs.addTab("Manual", holder);

		JPanel scalePanel = new JPanel(new GridLayout(1, 3));
		scalePanel.setBorder(BorderFactory.createTitledBorder("Scale"));
		scalePanel.add(xScaleSpinner);
		scalePanel.add(yScaleSpinner);
		scalePanel.add(zScaleSpinner);

		JPanel mainholder = new JPanel(new BorderLayout());
		mainholder.setBorder(BorderFactory
				.createTitledBorder("Render Settings"));
		mainholder.add(tabs, BorderLayout.CENTER);
		mainholder.add(scalePanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		this.add(mainholder, BorderLayout.NORTH);

		ChangeListener change = new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (e.getSource() instanceof JSlider)
				{
					JSlider slide = (JSlider) e.getSource();
					if (!slide.getValueIsAdjusting())
					{
						owner.updateVolumeData();
					}
				} else if (e.getSource() instanceof JSpinner)
				{
					owner.updateVolumeData();
				}
			}

		};

		quality.addChangeListener(change);
		xSizeSpinner.addChangeListener(change);
		ySizeSpinner.addChangeListener(change);
		zSizeSpinner.addChangeListener(change);
		xScaleSlider.addChangeListener(change);
		yScaleSlider.addChangeListener(change);
		zScaleSlider.addChangeListener(change);

		xScaleSpinner.addChangeListener(change);
		yScaleSpinner.addChangeListener(change);
		zScaleSpinner.addChangeListener(change);

		xCopyData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				copySizeX();

			}
		});

		copyData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out
						.println("############################\n#####About to copy Size######\n#################");
				copySize();

			}
		});
		yCopyData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				copySizeY();

			}
		});

		zCopyData.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				copySizeZ();

			}
		});
	}

	public void loadQualitySettings()
	{
		try
		{
			throw new Exception();
		} catch (Exception e)
		{
			// e.printStackTrace();
			qualityCalculatorType = TYPE_MAXSIZE;
			names.put(0, new JLabel("Low"));
			names.put(3, new JLabel("Medium"));
			names.put(6, new JLabel("High"));
			names.put(9, new JLabel("Max"));

			int i = 0;
			for (; i < 6; i++)
			{
				float p = i / 5.0f;

				maxSizeData.put(i, new int[]
				{ (int) (64 + (128 - 64) * p), (int) (64 + (128 - 64) * p),
						(int) (64 + (128 - 64) * p) });
			}
			for (; i < 10; i++)
			{
				float p = i / 9.0f;

				maxSizeData
						.put(i, new int[]
						{ (int) (64 + (256 - 64) * p),
								(int) (64 + (256 - 64) * p), 128 });

			}
		}

		quality.setLabelTable(names);
		quality.setMaximum(maxSizeData.size() - 1);
	}

	public void copySizeX()
	{
		copySizeX(true);
	}

	public void copySizeX(boolean update)
	{
		owner.setAllowUpdate(false);
		int val = Integer.parseInt(xRealSize.getText());
		xSizeSpinner.setValue(val);
		owner.setAllowUpdate(true);
		if (update)
		{
			owner.updateVolumeData();
		}

	}

	public void copySizeY()
	{
		copySizeY(true);
	}

	public void copySizeY(boolean update)
	{
		owner.setAllowUpdate(false);
		int val = Integer.parseInt(yRealSize.getText());
		ySizeSpinner.setValue(val);
		owner.setAllowUpdate(true);
		if (update)
		{
			owner.updateVolumeData();
		}
	}

	public void copySizeZ()
	{
		copySizeZ(true);
	}

	public void copySizeZ(boolean update)

	{
		owner.setAllowUpdate(false);
		int val = Integer.parseInt(zRealSize.getText());
		zSizeSpinner.setValue(val);
		owner.setAllowUpdate(true);
		if (update)
		{
			owner.updateVolumeData();
		}
	}

	public void copySize()
	{
		copySize(true);
	}

	public void copySize(boolean update)
	{
		copySizeX(false);
		copySizeY(false);
		copySizeZ(false);
		owner.setAllowUpdate(true);
		System.out
				.println("About to update Volume(copySize : octVolumeSizeControer) ["
						+ Thread.currentThread().getName() + "]");
		owner.updateVolumeData();
	}

	public int getXSize()
	{
		return (Integer) xSizeSpinner.getValue();
	}

	public int getYSize()
	{
		return (Integer) ySizeSpinner.getValue();
	}

	public int getZSize()
	{
		return (Integer) zSizeSpinner.getValue();
	}

	public void setXSize(int size)
	{
		xSizeSpinner.setValue(size);
	}

	public void setYSize(int size)
	{
		ySizeSpinner.setValue(size);
	}

	public void setZSize(int size)
	{
		zSizeSpinner.setValue(size);
	}

	public int getXReduce()
	{
		return xReduce;
	}

	public int getYReduce()
	{
		return yReduce;
	}

	public int getZReduce()
	{
		return zReduce;
	}

	public float getXScale()
	{
		return xScale;
	}

	public float getYScale()
	{
		return yScale;
	}

	public float getZScale()
	{
		return zScale;
	}

	public int getXScaleSliderValue()
	{
		return xScaleSlider.getValue();
	}

	public int getYScaleSliderValue()
	{
		return yScaleSlider.getValue();
	}

	public int getZScaleSliderValue()
	{
		return zScaleSlider.getValue();
	}

	public void setXScaleSliderValue(int value)
	{
		xScaleSlider.setValue(value);
	}

	public void setYScaleSliderValue(int value)
	{
		yScaleSlider.setValue(value);
	}

	public void setZScaleSliderValue(int value)
	{
		zScaleSlider.setValue(value);
	}
}
