package com.joey.software.imageToolkit.imageWarping;

import java.awt.BorderLayout;
import java.awt.Shape;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.regionSelectionToolkit.controlers.PolygonControler;
import com.joey.software.regionSelectionToolkit.controlers.ROIControler;


public class PositionSetter extends JPanel implements ROIPanelListner
{
	private ImageData data;

	ROIPanel panel;

	double scaleX = 1;

	double scaleY = 1;

	JPanel mainHolder = new JPanel();

	JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	public PositionSetter()
	{
		this(new ImageData());
	}

	public void setScaleImage(boolean scale)
	{
		mainHolder.removeAll();
		mainHolder.setLayout(new BorderLayout());
		mainHolder.setBorder(BorderFactory.createTitledBorder("Image"));
		if (scale == true)
		{
			mainHolder.add(scroll);
			scroll.setViewportView(panel);
		} else
		{
			mainHolder.add(panel, BorderLayout.CENTER);
		}
		mainHolder.repaint();
	}

	public PositionSetter(ImageData data)
	{
		createJPanel();
		setData(data);
	}

	public void updateImage()
	{
		panel.setImage(getData().getImg());
	}

	public void createJPanel()
	{
		panel = new ROIPanel(false, ROIPanel.TYPE_POLYGON);

		getPolygon().setMaxPoints(2);
		panel.addROIPanelListner(this);

		setLayout(new BorderLayout());
		add(mainHolder, BorderLayout.CENTER);
		setScaleImage(false);
	}

	public PolygonControler getPolygon()
	{
		ROIControler control = panel.getControler();
		if (control instanceof PolygonControler)
		{
			return ((PolygonControler) control);
		}
		return null;
	}

	public void setData(ImageData data)
	{
		this.data = data;
		panel.setImage(data.getImg());
		getPolygon().points.add(data.getP1());
		getPolygon().points.add(data.getP2());
	}

	@Override
	public void regionAdded(Shape region)
	{

	}

	@Override
	public void regionChanged()
	{
		getData().setP1(getPolygon().points.get(0));
		getData().setP2(getPolygon().points.get(1));
	}

	@Override
	public void regionRemoved(Shape region)
	{

	}

	public ImageData getData()
	{
		return data;
	}

	public ROIPanel getPanel()
	{
		return panel;
	}

}
