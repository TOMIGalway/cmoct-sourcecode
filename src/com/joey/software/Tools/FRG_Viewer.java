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
package com.joey.software.Tools;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.DataToolkit.DrgRawImageProducer;
import com.joey.software.DataToolkit.ImageFileProducer;
import com.joey.software.DataToolkit.ImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsFRGImageProducer;
import com.joey.software.DataToolkit.thorlabs.ThorlabsIMGImageProducer;
import com.joey.software.fileToolkit.FileOperations;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ImageFileSelectorPanel;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.stringToolkit.StringOperations;


public class FRG_Viewer extends JPanel implements ChangeListener,
		ROIPanelListner
{
	public ImageProducer getFrg()
	{
		return frg;
	}

	public static final int TYPE_FRG = 0;

	public static final int TYPE_IMG_THOR = 1;

	public static final int TYPE_IMAGE_SERIES = 2;

	public static final int TYPE_DRGRAW = 3;

	int type = TYPE_FRG;

	DynamicRangeImage srcPanel = new DynamicRangeImage();

	DynamicRangeImage magPanel = new DynamicRangeImage();


	ImageFileSelectorPanel imagePanel = new ImageFileSelectorPanel();

	

	ImageProducer frg;

	JSpinner currentImage;

	JSpinner averageNumber = new JSpinner(new SpinnerNumberModel(0, -1000,
			10000, 1));

	JSpinner rotation = new JSpinner(new SpinnerNumberModel(0, -4, +4, 1));

	JTextField totalImages = new JTextField(5);

	JButton loadFRG = new JButton("FRG");

	JButton loadIMG = new JButton("IMG");

	JButton loadDRGRAW = new JButton("DRGRAW");

	JButton loadImage = new JButton("Img Series");

	JTabbedPane tabHolder = new JTabbedPane();

	int lastTabPos = 0;

	FRG_ViewerListner listner;

	StatusBarPanel status = new StatusBarPanel();

	public FRG_Viewer()
	{
		createJPanel();
		srcPanel.getImage().addROIPanelListner(this);
		srcPanel.getMinJSlider().addChangeListener(this);
		srcPanel.getMaxJSlider().addChangeListener(this);

		setViewType(DynamicRangeImage.TYPE_POINT_PROFILE);
	}

	public void setFrg(ThorlabsFRGImageProducer frg)
	{
		this.frg = frg;
	}

	public void setViewType(int type)
	{
		srcPanel.setPanelMode(type);
		
		magPanel.setPanelMode(type);
		

	}

	public static void main(String input[]) throws IOException
	{

		final File fI = FileSelectionField.getUserFile();

		final int start = 300;
		final int end = 330;

		final ThorlabsFRGImageProducer img = new ThorlabsFRGImageProducer(
				fI);
		img.getUserInputs();

		StatusBarPanel stat = new StatusBarPanel();
		JFrame fram = FrameFactroy.getFrame(stat);
		stat.setMaximum(end - start);
		for (int i = start; i < end; i++)
		{
			stat.setValue(i - start);
			String name = fI.toString()
					+ StringOperations.getNumberString(3, i) + ".png";
			BufferedImage dat = img.getImage(i);

			ImageIO.write(dat, "PNG", new File(name));
		}
		fram.setVisible(false);

	}

	public DynamicRangeImage getCurrentView()
	{
		return (DynamicRangeImage) tabHolder.getSelectedComponent();
	}

	public void createJPanel()
	{
		tabHolder.addTab("SRC Image", srcPanel);
		tabHolder.addTab("Mag", magPanel);
	

		// tabHolder.addTab("Hilbert Real", hilRealPanel);
		// tabHolder.addTab("Hilbert Imaginary", hilImaginaryPanel);
		// tabHolder.addTab("Hilbert Mag", hilMagPanel);
		// tabHolder.addTab("Hilbert Phase", hilPhasePanel);

		tabHolder.addChangeListener(this);

		currentImage = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

		totalImages.setEditable(false);

		JPanel loadPanel = new JPanel(new FlowLayout());
		loadPanel.add(loadFRG);
		loadPanel.add(loadIMG);
		loadPanel.add(loadDRGRAW);
		loadPanel.add(loadImage);

		JPanel settingPanel = new JPanel(new FlowLayout());
		settingPanel.add(new JLabel("  Current Image : "));
		settingPanel.add(currentImage);
		settingPanel.add(totalImages);
		settingPanel.add(new JLabel("  Rotation :"));
		settingPanel.add(rotation);
		settingPanel.add(new JLabel("  Averages :"));
		settingPanel.add(averageNumber);

		JPanel controlPanel = new JPanel(new GridLayout(2, 1));
		controlPanel.add(loadPanel);
		controlPanel.add(settingPanel);

		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.NORTH);
		add(tabHolder, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);
		currentImage.addChangeListener(this);

		rotation.addChangeListener(this);
		averageNumber.addChangeListener(this);

		loadFRG.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setFile(FileSelectionField.getUserFile(new String[]
					{ ".frg:FRG File(.frg)" }));
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

		loadIMG.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setFile(FileSelectionField.getUserFile(new String[]
					{ ".img:Thorlabs IMG File(.img)" }));
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

		loadDRGRAW.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setFile(FileSelectionField.getUserFile(new String[]
					{ ".drgraw:Rawfile(.drgraw)", ".raw:RAWFile(.raw)" }));
				} catch (Exception e1)
				{
					JOptionPane.showMessageDialog(null, "Error : " + e1);
					e1.printStackTrace();
				}
			}
		});

		loadImage.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{

				imagePanel.setPreferredSize(new Dimension(600, 480));

				if (JOptionPane.showConfirmDialog(null, imagePanel) == JOptionPane.OK_OPTION)
				{
					type = TYPE_IMAGE_SERIES;
					frg = new ImageFileProducer(imagePanel.getFiles());

					totalImages.setText("" + frg.getImageCount());
					((SpinnerNumberModel) currentImage.getModel())
							.setMaximum(frg.getImageCount());
					((SpinnerNumberModel) currentImage.getModel()).setValue(0);
					// Fix Error in input data
					updateCurrentPanel();
				}

			}
		});
		tabHolder.setSelectedIndex(1);
	}

	public void setFile(File f) throws IOException
	{

		if (f == null)
		{
			frg = null;
			return;
		}
		if (FileOperations.getExtension(f).equalsIgnoreCase(".frg"))
		{

			type = TYPE_FRG;
			ThorlabsFRGImageProducer frg = new ThorlabsFRGImageProducer(f);
			frg.loadSpecData(0);
			frg.setUseWindowing(false);
			((SpinnerNumberModel) currentImage.getModel()).setMaximum(frg
					.getImageCount());

			srcPanel.setDataFloat(frg.specData);		
			magPanel.setDataFloat(frg.magnitude);
			
			this.frg = frg;
			totalImages.setText("" + frg.getImageCount());
			((SpinnerNumberModel) currentImage.getModel()).setMaximum(frg
					.getImageCount());
			((SpinnerNumberModel) currentImage.getModel()).setValue(0);
			// Fix Error in input data
			updateCurrentPanel();
		} else if (FileOperations.getExtension(f).equalsIgnoreCase(".img"))
		{
			ThorlabsIMGImageProducer frg = new ThorlabsIMGImageProducer(f);

			type = TYPE_IMG_THOR;
			((SpinnerNumberModel) currentImage.getModel()).setMaximum(frg
					.getImageCount());

			srcPanel.setData(frg.getImage(1));
			
			magPanel.setDataFloat(new float[][]
			{
			{ 1 } });
			
			

			this.frg = frg;
			totalImages.setText("" + frg.getImageCount());
			((SpinnerNumberModel) currentImage.getModel()).setMaximum(frg
					.getImageCount());
			((SpinnerNumberModel) currentImage.getModel()).setValue(0);
			// Fix Error in input data

			updateCurrentPanel();
		} else
		{
			type = TYPE_DRGRAW;

			DrgRawImageProducer frg = new DrgRawImageProducer(f);
			frg.getUserChoceAxis();

			((SpinnerNumberModel) currentImage.getModel()).setMaximum(frg
					.getImageCount());

			totalImages.setText("" + frg.getImageCount());
			((SpinnerNumberModel) currentImage.getModel()).setMaximum(frg
					.getImageCount());
			((SpinnerNumberModel) currentImage.getModel()).setValue(0);

			this.frg = frg;
		}
		try
		{
			JFrame jf = (JFrame) (this.getTopLevelAncestor());
			jf.setTitle("OCT Analysis : " + f.toString());

		} catch (Exception e)
		{
			// Not important
		}

	}

	public int getImageCount()
	{
		return frg.getImageCount();
	}

	public void setImagePos(int pos) throws IOException
	{
		if (type == TYPE_FRG)
		{
			ThorlabsFRGImageProducer frg = (ThorlabsFRGImageProducer) this.frg;
			frg.loadSpecData(pos);

			if (tabHolder.getSelectedIndex() <= 5)
			{
				frg.processSpecData();
			} 
		} else
		{
			int avg = (Integer) averageNumber.getValue();
			BufferedImage img;

			if (avg == 0)
			{
				img = frg.getImage(pos);
			} else
			{
				if (avg > 0)
				{
					avg += 1;
				} else
				{
					avg -= 1;
				}
				img = frg
						.getImageProject(pos, avg, ImageOperations.PROJECT_TYPE_AVERAGE, ImageOperations.PLANE_GRAY, status);
			}
			img = ImageOperations.getRotatedImage(img, (Integer) rotation
					.getValue());
			srcPanel.setData(img);
			magPanel.setData(img);
		}

		DynamicRangeImage p = (DynamicRangeImage) tabHolder
				.getSelectedComponent();

		p.updateImagePanel();
	}

	public DynamicRangeImage getSrcPanel()
	{
		return srcPanel;
	}

	public DynamicRangeImage getMagPanel()
	{
		return magPanel;
	}


	
	public void updateCurrentPanel()
	{
		try
		{
			if (frg == null)
			{
				return;
			}

			setImagePos((Integer) currentImage.getValue());
			updateAScan();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void updateAScan()
	{

		if (listner == null)
		{
			return;
		}

		DynamicRangeImage pan = (DynamicRangeImage) tabHolder
				.getSelectedComponent();

		Point2D.Double p = pan.getMarkerPosition();

		try
		{
			listner.AScanChanged(pan, pan.getScanDataFloat(), (int) p.y);

		} catch (Exception e)
		{
			System.out.println("Error : " + e);
			e.printStackTrace();
		}
	}

	public void setListner(FRG_ViewerListner listner)
	{
		this.listner = listner;
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		// Detect tab change.
		if (e.getSource() == tabHolder)
		{
			DynamicRangeImage newPan = (DynamicRangeImage) tabHolder
					.getSelectedComponent();
			DynamicRangeImage oldPan = (DynamicRangeImage) tabHolder
					.getComponentAt(lastTabPos);

			oldPan.getImage().removeROIPanelListner(this);
			oldPan.getMinJSlider().removeChangeListener(this);
			oldPan.getMaxJSlider().removeChangeListener(this);

			newPan.getImage().addROIPanelListner(this);
			newPan.getMinJSlider().addChangeListener(this);
			newPan.getMaxJSlider().addChangeListener(this);
			updateCurrentPanel();

			lastTabPos = tabHolder.getSelectedIndex();
		} else if (e.getSource() == currentImage || e.getSource() == rotation
				|| e.getSource() == averageNumber)
		{
			updateCurrentPanel();
		} else if (e.getSource() instanceof JSlider)
		{
			updateAScan();
		}
	}

	@Override
	public void regionAdded(Shape region)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void regionChanged()
	{
		updateAScan();
	}

	@Override
	public void regionRemoved(Shape region)
	{
		// TODO Auto-generated method stub

	}
}
