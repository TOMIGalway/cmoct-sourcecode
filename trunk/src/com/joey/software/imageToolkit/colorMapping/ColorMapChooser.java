package com.joey.software.imageToolkit.colorMapping;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;

public class ColorMapChooser extends JPanel implements ActionListener
{
	static int mapWide = 300;

	static int mapHigh = 10;

	public ColorMap[] maps;
	{
		maps = new ColorMap[ColorMap.TOTAL_MAPS];
		for (int i = 0; i < maps.length; i++)

		{
			maps[i] = ColorMap.getColorMap(i);
		}
	}

	int selectedIndex = 0;

	JComboBox choice;

	ImagePanel picture;

	Vector<ColorMapChangeListner> listner = new Vector<ColorMapChangeListner>();

	JButton saveLinearToClipboard = new JButton("To Clip");
	public ColorMapChooser()
	{
		super(new BorderLayout());

		choice = new JComboBox(maps);
		choice.addActionListener(this);
		// Group the radio buttons.

		// Set up the picture label.
		picture = new ImagePanel(ColorMapTools
				.getSampleMap(maps[selectedIndex], mapWide, mapHigh),
				ImagePanel.TYPE_FIT_IMAGE_TO_PANEL);

		// The preferred size is hard-coded to be the width of the
		// widest image and the height of the tallest image.
		// A real program would compute this.
		picture.setPreferredSize(new Dimension(mapWide, mapHigh));

		// Put the radio buttons in a column in a panel.

		JPanel choiceHolder = new JPanel(new BorderLayout(5,5));
		choiceHolder.add(choice, BorderLayout.NORTH);
		choiceHolder.setPreferredSize(new Dimension(200, 50));
		add(choiceHolder, BorderLayout.WEST);
		add(picture, BorderLayout.CENTER);
		add(saveLinearToClipboard, BorderLayout.SOUTH);
		
		saveLinearToClipboard.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ImageOperations.sendImageToClipBoard(ColorMapTools
				.getSampleMap(maps[selectedIndex], 512, 256));
				
			}
		});
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	public void addColorMapChangeListner(ColorMapChangeListner list)
	{
		listner.add(list);
	}

	public void removeAllColorMapChangeListner()
	{
		listner.clear();
	}

	public void removeColorMapChangeListner(ColorMapChangeListner list)
	{
		listner.remove(list);
	}

	public void notifyColorMapChange()
	{
		for (ColorMapChangeListner l : listner)
		{
			l.mapChanged(this);
		}
	}

	// This will return a float[] rangeing from 0 - 1 for the red data.
	public float[] getRedData()
	{
		float[] data = new float[256];
		getRedData(data);
		return data;
	}

	public void getRedData(float[] data)
	{
		ColorMap map = getSelectedMap();
		for (int i = 0; i < data.length; i++)
		{
			data[i] = map.getColor(i).getRed() / 255.0f;
		}
	}

	public float[] getGreenData()
	{
		float[] data = new float[256];
		getGreenData(data);
		return data;
	}

	public void getGreenData(float[] data)
	{
		ColorMap map = getSelectedMap();
		for (int i = 0; i < data.length; i++)
		{
			data[i] = map.getColor(i).getGreen() / 255.0f;
		}
	}

	public float[] getBlueData()
	{
		float[] data = new float[256];
		getBlueData(data);
		return data;
	}

	public void getBlueData(float[] data)
	{
		ColorMap map = getSelectedMap();
		for (int i = 0; i < data.length; i++)
		{
			data[i] = map.getColor(i).getBlue() / 255.0f;
		}
	}

	public void getData(float[] red, float[] green, float[] blue)
	{
		ColorMap map = getSelectedMap();
		for (int i = 0; i < 256; i++)
		{
			red[i] = map.getColor(i).getRed() / 255.0f;
			green[i] = map.getColor(i).getGreen() / 255.0f;
			blue[i] = map.getColor(i).getBlue() / 255.0f;
		}
	}

	public ColorMap getSelectedMap()
	{
		return maps[getSelectedIndex()];
	}

	/** Listens to the radio buttons. */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == choice)
		{
			if (selectedIndex != choice.getSelectedIndex())
			{
				selectedIndex = choice.getSelectedIndex();
				picture.setImage(ColorMapTools
						.getSampleMap(maps[selectedIndex], mapWide, mapHigh));
				notifyColorMapChange();
			}
		}

	}

	public int getSelectedIndex()
	{
		return selectedIndex;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Create and set up the window.
		JFrame frame = new JFrame("ColorMapChooser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = new ColorMapChooser();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

}
