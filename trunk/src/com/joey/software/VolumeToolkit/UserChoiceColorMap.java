package com.joey.software.VolumeToolkit;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.VolumeToolkit.Transferfunctions.GradientToolkit;
import com.joey.software.VolumeToolkit.Transferfunctions.TransferFunctionPanel;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.imageToolkit.colorMapping.ColorMapChangeListner;
import com.joey.software.imageToolkit.colorMapping.ColorMapChooser;
import com.joey.software.imageToolkit.histogram.HistogramPanel;
import com.joey.software.imageToolkit.histogram.HistogramSelectionPanel;
import com.joey.software.imageToolkit.histogram.HistogramSelectionPanelListener;


/**
 */
public class UserChoiceColorMap extends Colormap implements Externalizable,
		ActionListener, ColorMapChangeListner, HistogramSelectionPanelListener
{
	private static final long serialVersionUID = 1L;

	HistogramSelectionPanel redHist = new HistogramSelectionPanel();

	HistogramSelectionPanel greenHist = new HistogramSelectionPanel();

	HistogramSelectionPanel blueHist = new HistogramSelectionPanel();

	HistogramSelectionPanel alphaColorHist = new HistogramSelectionPanel();

	HistogramSelectionPanel grayHist = new HistogramSelectionPanel();

	HistogramSelectionPanel alphaGrayHist = new HistogramSelectionPanel();

	ColorMapChooser colorChoicePanel = new ColorMapChooser();

	JTabbedPane histColorTypePanel = new JTabbedPane();

	JTabbedPane mapTypePanel = new JTabbedPane();

	TransferFunctionPanel gradientFunction = new TransferFunctionPanel();

	JPanel selectionPanel = null;

	JButton saveButton = new JButton("Save");

	JButton loadButton = new JButton("Load");

	ImagePanel pan = new ImagePanel(new BufferedImage(256, 2,
			BufferedImage.TYPE_INT_ARGB));

	JSpinner dataPoint = new JSpinner(new SpinnerNumberModel(256, 2,
			Integer.MAX_VALUE, 5));

	int[] histVolumeData = new int[256];

	int[][] histGradData = new int[256][256];

	FileSelectionField loadMapPanel = new FileSelectionField();

	FileSelectionField saveMapPanel = new FileSelectionField();

	public UserChoiceColorMap()
	{
		redHist.setHistogramListner(this);
		greenHist.setHistogramListner(this);
		blueHist.setHistogramListner(this);
		alphaColorHist.setHistogramListner(this);
		alphaGrayHist.setHistogramListner(this);
		grayHist.setHistogramListner(this);

		dataPoint.setValue(redHist.getDataPoints());
		getUserSelectionPanel();
		colorChoicePanel.addColorMapChangeListner(this);

		pan.setPanelType(ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);
		updateImagePanel();
		// ImageOperations.fillWithRandomColors(pan.getImage());
		// FrameFactroy.getFrame(pan);
	}

	@Override
	public int getRGBAGradColor(int val, int grad)
	{
		// TODO Auto-generated method stub
		return gradientFunction.getARGB(val, grad);
	}

	public static void main(String[] input)
	{
		UserChoiceColorMap map = new UserChoiceColorMap();
		FrameFactroy.getFrame(map.getUserSelectionPanel());
	}

	public void updateImagePanel()
	{

		for (int i = 0; i < pan.getImage().getWidth(); i++)
		{
			Color c;

			if (histColorTypePanel.getSelectedIndex() == 0)
			{
				c = new Color(redHist.getSelectionValue(i), greenHist
						.getSelectionValue(i), blueHist.getSelectionValue(i));
			} else
			{

				c = new Color(grayHist.getSelectionValue(i), grayHist
						.getSelectionValue(i), grayHist.getSelectionValue(i));

			}
			pan.getImage().setRGB(i, 0, c.getRGB());

			if (histColorTypePanel.getSelectedIndex() == 0)
			{
				c = new Color(alphaColorHist.getSelectionValue(i),
						alphaColorHist.getSelectionValue(i), alphaColorHist
								.getSelectionValue(i));
			} else
			{
				c = new Color(alphaGrayHist.getSelectionValue(i), alphaGrayHist
						.getSelectionValue(i), alphaGrayHist
						.getSelectionValue(i));
			}
			pan.getImage().setRGB(i, 1, c.getRGB());

			// System.out.println(c);

		}
		pan.repaint();
	}

	public void updateDataPoints(int value)
	{
		alphaColorHist.setDataPoints(value);
		alphaGrayHist.setDataPoints(value);
		grayHist.setDataPoints(value);
		redHist.setDataPoints(value);
		greenHist.setDataPoints(value);
		blueHist.setDataPoints(value);

	}

	public void updateVolumeData(byte[][][] data, StatusBarPanel status)
	{
		if (status != null)
		{
			status.setStatusMessage("Processing Data for Mapping");
		}
		HistogramPanel.getHistogramData(data, histVolumeData, status);
		redHist.setData(histVolumeData);
		blueHist.setData(histVolumeData);
		greenHist.setData(histVolumeData);
		grayHist.setData(histVolumeData);
		alphaGrayHist.setData(histVolumeData);
		alphaColorHist.setData(histVolumeData);

		GradientToolkit.getGradientFunctionFAST(data, this.histGradData);
		gradientFunction.setBackgroundImage(this.histGradData);
		if (status != null)
		{
			status.reset();
		}
	}

	public JPanel getUserSelectionPanel()
	{
		if (selectionPanel == null)
		{
			JPanel result = new JPanel(new BorderLayout());

			JPanel redPanel = new JPanel(new BorderLayout());
			JPanel greenPanel = new JPanel(new BorderLayout());
			JPanel bluePanel = new JPanel(new BorderLayout());
			JPanel grayPanel = new JPanel(new BorderLayout());
			JPanel alphaColorPanel = new JPanel(new BorderLayout());
			JPanel alphaGrayPanel = new JPanel(new BorderLayout());

			redPanel.setBorder(BorderFactory.createTitledBorder("Red Plane"));
			greenPanel.setBorder(BorderFactory
					.createTitledBorder("Green Plane"));
			bluePanel.setBorder(BorderFactory.createTitledBorder("Blue Plane"));
			alphaColorPanel.setBorder(BorderFactory
					.createTitledBorder("Alpha Plane"));
			alphaGrayPanel.setBorder(BorderFactory
					.createTitledBorder("Alpha Plane"));
			grayPanel.setBorder(BorderFactory.createTitledBorder("GrayPlane"));

			redPanel.add(redHist);
			greenPanel.add(greenHist);
			bluePanel.add(blueHist);
			alphaColorPanel.add(alphaColorHist);

			JPanel colorHolderPanel = new JPanel(new GridLayout(3, 1));
			colorHolderPanel.add(redPanel);
			colorHolderPanel.add(greenPanel);
			colorHolderPanel.add(bluePanel);

			JPanel colorHistHolderPanel = new JPanel(new BorderLayout());
			colorHistHolderPanel.add(alphaColorPanel);

			JSplitPane colorSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			colorSplit.setLeftComponent(colorHolderPanel);
			colorSplit.setRightComponent(colorHistHolderPanel);
			colorSplit.setDividerLocation(0.75);
			colorSplit.setOneTouchExpandable(true);

			alphaGrayPanel.add(alphaGrayHist);
			grayPanel.add(grayHist);

			JPanel grayHolderPanel = new JPanel(new GridLayout(2, 1));
			grayHolderPanel.add(grayPanel);
			grayHolderPanel.add(alphaGrayPanel);

			histColorTypePanel.addTab("Color", colorSplit);
			histColorTypePanel.add("Gray Scale", grayHolderPanel);

			JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
			buttonPanel.add(loadButton);
			buttonPanel.add(saveButton);
			buttonPanel.add(dataPoint);

			dataPoint.addChangeListener(new ChangeListener()
			{

				@Override
				public void stateChanged(ChangeEvent e)
				{
					updateDataPoints((Integer) dataPoint.getValue());

				}
			});
			buttonPanel.setBorder(BorderFactory.createTitledBorder(""));

			JPanel previewHolder = new JPanel(new BorderLayout());
			previewHolder.setBorder(BorderFactory.createTitledBorder(""));
			previewHolder.add(pan);
			previewHolder.setPreferredSize(new Dimension(100, 30));

			JPanel top = new JPanel(new BorderLayout());
			top.add(buttonPanel, BorderLayout.NORTH);
			top.add(colorChoicePanel, BorderLayout.CENTER);
			// top.add(previewHolder, BorderLayout.SOUTH);

			result.add(top, BorderLayout.NORTH);
			result.add(histColorTypePanel, BorderLayout.CENTER);
			result.add(previewHolder, BorderLayout.SOUTH);
			result.setBorder(BorderFactory.createTitledBorder(""));

			mapTypePanel.addTab("1D-Histogram", result);
			mapTypePanel.addTab("2D-Histogram + Grad", gradientFunction);

			selectionPanel = new JPanel(new BorderLayout());
			selectionPanel.setBorder(BorderFactory.createTitledBorder(""));
			selectionPanel.add(mapTypePanel, BorderLayout.CENTER);

			loadButton.addActionListener(this);
			saveButton.addActionListener(this);
		}
		return selectionPanel;
	}

	@Override
	public void mapChanged(ColorMapChooser chooser)
	{

		histColorTypePanel.setSelectedIndex(0);

		float[] red = new float[256];
		float[] green = new float[256];
		float[] blue = new float[256];
		chooser.getData(red, green, blue);
		redHist.setValues(red);
		greenHist.setValues(green);
		blueHist.setValues(blue);
		updateMap();
	}

	public void setData(UserChoiceColorMap map)
	{
		redHist.setPanelData(map.redHist);
		greenHist.setPanelData(map.greenHist);
		blueHist.setPanelData(map.blueHist);
		alphaColorHist.setPanelData(map.alphaColorHist);
		alphaGrayHist.setPanelData(map.alphaGrayHist);
		grayHist.setPanelData(map.grayHist);
		dataPoint.setValue(redHist.getDataPoints());
		if (selectionPanel != null)
		{
			selectionPanel.repaint();
		}
		updateImagePanel();
	}

	public void setData(File f) throws Exception
	{
		setData(loadMap(f));

	}

	public static UserChoiceColorMap loadMap(File f) throws Exception
	{
		System.out.println("File : " + f);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		UserChoiceColorMap c = (UserChoiceColorMap) in.readObject();
		return c;
	}

	public static void saveMap(UserChoiceColorMap map, File f) throws Exception
	{
		if (!f.exists())
		{
			f.createNewFile();
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(map);

	}

	public void updateMap()
	{
		updateImagePanel();
		if (mapTypePanel.getSelectedIndex() == 0)
		{
			if (histColorTypePanel.getSelectedIndex() == 0)
			{
				mapType = TYPE_HIST_MAP_RGBA;
				bytesPerVoxel = 4;
				alphaColorHist.updateSelectionData();
				redHist.updateSelectionData();
				greenHist.updateSelectionData();
				blueHist.updateSelectionData();
				for (int i = 0; i < 256; i++)
				{
					int alpha = (int) (255 * alphaColorHist
							.getSelectionValue(i));
					int red = (int) (255 * redHist.getSelectionValue(i));
					int green = (int) (255 * greenHist.getSelectionValue(i));
					int blue = (int) (255 * blueHist.getSelectionValue(i));
					// System.out.printf("\n[%d] -> [%d,%d,%d,%d]",i, alpha,
					// red,
					// green, blue);
					colorMapping[i] = (alpha & 0xFF) << 24 | (red & 0xFF) << 16
							| (green & 0xFF) << 8 | (blue & 0xFF);
				}
			} else if (histColorTypePanel.getSelectedIndex() == 1)
			{
				alphaGrayHist.updateSelectionData();
				grayHist.updateSelectionData();
				mapType = TYPE_HIST_MAP_GA;
				bytesPerVoxel = 2;
				for (int i = 0; i < 256; i++)
				{
					int alpha = (int) (255 * alphaGrayHist.getSelectionValue(i));
					int red = (int) (255 * grayHist.getSelectionValue(i));
					int green = (int) (255 * grayHist.getSelectionValue(i));
					int blue = (int) (255 * grayHist.getSelectionValue(i));

					// System.out.printf("\n[%d] -> [%d,%d,%d,%d]",i, alpha,
					// red,
					// green, blue);
					colorMapping[i] = (alpha & 0xFF) << 24 | (red & 0xFF) << 16
							| (green & 0xFF) << 8 | (blue & 0xFF);
				}
			}
		} else
		{
			gradientFunction.updateTransferData();
			mapType = TYPE_GRAD_RGBA;
			bytesPerVoxel = 4;
		}

		editId = (int) ((Math.random()) * Integer.MAX_VALUE);
	}

	/**
	 * This will get a string for a non volume color map(my map). the boolean
	 * direct map is used as there are two possible outputs direct map -> the
	 * data is outpted so that it can be printed to the screeen (direct ->
	 * false) this give a string ("0,-23232\n..... with the \n embeded as \\n
	 * 
	 * @param directMap
	 * @return
	 */
	public String getTextDataColorMap(boolean directMap)
	{
		updateMap();
		String output = "";
		if (directMap == false)
		{
			output += "\"";
		}
		alphaColorHist.updateSelectionData();
		redHist.updateSelectionData();
		greenHist.updateSelectionData();
		blueHist.updateSelectionData();
		for (int i = 0; i < 256; i++)
		{
			if (i != 0)
			{
				if (directMap)
				{
					output += "\n";
				} else
				{
					output += "\\n";
				}
			}
			int alpha = (int) (255 * alphaColorHist.getSelectionValue(i));
			int red = (int) (255 * redHist.getSelectionValue(i));
			int green = (int) (255 * greenHist.getSelectionValue(i));
			int blue = (int) (255 * blueHist.getSelectionValue(i));
			// System.out.printf("\n[%d] -> [%d,%d,%d,%d]",i, alpha, red,
			// green, blue);
			Color c = new Color(red, green, blue);

			output += (i + "," + c.getRGB());
		}

		if (directMap == false)
		{
			output += "\"";
		}
		return output;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		int val = in.readInt();
		redHist.setPanelData((HistogramSelectionPanel) in.readObject());
		greenHist.setPanelData((HistogramSelectionPanel) in.readObject());
		blueHist.setPanelData((HistogramSelectionPanel) in.readObject());
		grayHist.setPanelData((HistogramSelectionPanel) in.readObject());
		alphaColorHist.setPanelData((HistogramSelectionPanel) in.readObject());
		alphaGrayHist.setPanelData((HistogramSelectionPanel) in.readObject());
		dataPoint.setValue(redHist.getDataPoints());

		histColorTypePanel.setSelectedIndex(val);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		updateMap();
		out.writeInt(histColorTypePanel.getSelectedIndex());
		out.writeObject(redHist);
		out.writeObject(greenHist);
		out.writeObject(blueHist);
		out.writeObject(grayHist);
		out.writeObject(alphaColorHist);
		out.writeObject(alphaGrayHist);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == saveButton)
		{
			saveMapPanel.setExtensions(new String[]
			{ "map:Color Map (*.map)" }, true, true);

			saveMapPanel.setLabelText("Output : ");
			int val = JOptionPane
					.showConfirmDialog(null, saveMapPanel, "Select Map File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (val == JOptionPane.OK_OPTION)
			{
				try
				{
					saveMap(this, saveMapPanel.getFile());
				} catch (Exception e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error Loading Color Map : "
									+ e1, "Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == loadButton)
		{
			loadMapPanel.setExtensions(new String[]
			{ "map:Color Map (*.map)" }, true, true);
			loadMapPanel.setLabelText("Input : ");
			int val = JOptionPane
					.showConfirmDialog(null, loadMapPanel, "Select Map File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (val == JOptionPane.OK_OPTION)
			{
				try
				{
					setData(loadMapPanel.getFile());
				} catch (Exception e1)
				{
					JOptionPane
							.showMessageDialog(null, "Error Saving Color Map : "
									+ e1, "Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == dataPoint)
		{

		}

	}

	@Override
	public void histogramChanged()
	{
		updateImagePanel();
	}

}