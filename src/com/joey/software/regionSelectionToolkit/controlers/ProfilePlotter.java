package com.joey.software.regionSelectionToolkit.controlers;


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.fileToolkit.ImageFileSelector;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.SwingToolkit;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.mathsToolkit.MathsToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;


public class ProfilePlotter extends LineControler
{
	public static final int PLOT_GRAY = 0;

	public static final int PLOT_RGB = 1;

	public static final int PLOT_RED = 2;

	public static final int PLOT_GREEN = 3;

	public static final int PLOT_BLUE = 4;

	ArrayList<Float> redValue = new ArrayList<Float>();

	ArrayList<Float> greenValue = new ArrayList<Float>();

	ArrayList<Float> blueValue = new ArrayList<Float>();

	Point2D.Float redData[] = new Point2D.Float[0];

	Point2D.Float greenData[] = new Point2D.Float[0];

	Point2D.Float blueData[] = new Point2D.Float[0];

	Point2D.Double lastP1 = new Point2D.Double();

	Point2D.Double lastP2 = new Point2D.Double();

	double lastScale = 0;

	JButton saveOutput = new JButton("Save Output");

	JSpinner yAxisHigh = new JSpinner(new SpinnerNumberModel(100.0, 0, 100000,
			1));

	JSpinner xAxisNum = new JSpinner(new SpinnerNumberModel(100, 1, 100000, 1));

	JSpinner avgNum = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));

	JSpinner offset = new JSpinner(new SpinnerNumberModel(0.0, -1000, 1000, 1));

	JPanel toolPanel = new JPanel(new BorderLayout());

	JSlider alpha = new JSlider(0, 1000, 1000);

	JCheckBox gradientBack = new JCheckBox("Gradient");

	JCheckBox boxBack = new JCheckBox("Box", true);

	boolean forceUpdate = false;

	FileSelectionField outputFile = new FileSelectionField();

	JCheckBox showRed = new JCheckBox("Red", true);

	JCheckBox showGreen = new JCheckBox("Green", true);

	JCheckBox showBlue = new JCheckBox("Blue", true);

	public ProfilePlotter(ROIPanel panel)
	{
		super(panel);

		createToolPanel();
	}

	public JPanel getToolPanel()
	{
		return toolPanel;
	}

	public void saveOutput()
	{
		if (outputFile.getUserChoice())
		{
			try
			{
				PrintWriter out = new PrintWriter(outputFile.getFile());

				out.printf("Point A :,%f,%f\n", lastP1.x, lastP1.y);
				out.printf("Point B :,%f,%f\n", lastP2.x, lastP2.y);

				out.printf("Red, green, blue\n");
				for (int i = 0; i < redValue.size(); i++)
				{
					out.printf("%f, %f, %f\n", redValue.get(i), greenValue
							.get(i), blueValue.get(i));
				}
				out.close();
				out.flush();
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void createToolPanel()
	{
		outputFile.setType(FileSelectionField.TYPE_SAVE_FILE);
		outputFile.setExtensions(new String[]
		{ "csv:CSV File .csv" }, true, true);

		JPanel back = new JPanel(new GridLayout(1, 2));
		back.add(boxBack);
		back.add(gradientBack);

		JPanel show = new JPanel(new GridLayout(1, 3));
		show.add(showRed);
		show.add(showGreen);
		show.add(showBlue);

		JPanel pan = new JPanel(new GridLayout(8, 1));
		pan.add(alpha);
		pan.add(SwingToolkit.getLabel(show, "Show : ", 60));
		pan.add(SwingToolkit.getLabel(avgNum, "Avg Num : ", 60));
		pan.add(SwingToolkit.getLabel(xAxisNum, "Total Num : ", 60));
		pan.add(SwingToolkit.getLabel(yAxisHigh, "Y High: ", 60));
		pan.add(SwingToolkit.getLabel(offset, "Offset: ", 60));
		pan.add(SwingToolkit.getLabel(back, "Background: ", 60));
		pan.add(SwingToolkit.getLabel(saveOutput, "Save Output: ", 60));
		toolPanel.setLayout(new BorderLayout());
		toolPanel.add(pan, BorderLayout.NORTH);

		offset.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{

				forceUpdate = true;
				panel.repaint();
			}
		});
		alpha.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{

				forceUpdate = true;
				panel.repaint();
			}
		});

		boxBack.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				forceUpdate = true;
				panel.repaint();

			}
		});

		showRed.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				forceUpdate = true;
				panel.repaint();

			}
		});
		showGreen.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				forceUpdate = true;
				panel.repaint();

			}
		});
		showBlue.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				forceUpdate = true;
				panel.repaint();

			}
		});
		gradientBack.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				forceUpdate = true;
				panel.repaint();

			}
		});
		saveOutput.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				saveOutput();
			}
		});
		xAxisNum.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				forceUpdate = true;
				panel.repaint();
			}
		});

		yAxisHigh.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				forceUpdate = true;
				panel.repaint();
			}
		});
		avgNum.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				forceUpdate = true;
				panel.repaint();
			}
		});
	}

	@Override
	public synchronized void draw(Graphics2D g1)
	{
		Graphics2D g = (Graphics2D) g1.create();
		drawLines = false;
		// TODO Auto-generated method stub

		updateProfile();
		Point2D.Double p1 = points.get(0);
		Point2D.Double p2 = points.get(1);

		// if (p2.x < p1.x)
		// {
		// p1 = points.get(1);
		// p2 = points.get(0);
		// }

		Stroke oldStroke = g.getStroke();
		RenderingHints oldHints = g.getRenderingHints();

		GraphicsToolkit.setRenderingQuality(g, GraphicsToolkit.HIGH_QUALITY);

		g.setStroke(new BasicStroke((float) (1f / panel.getScale())));
		double Length = ((Double) yAxisHigh.getValue());

		g.setColor(panel.getHighlightColor());
		Point2D.Double dP1 = new Point2D.Double(p1.y - p2.y, p2.x - p1.x);// Looks
		// funny
		// but
		// correct.
		MathsToolkit.normalise(dP1);
		Point2D.Double val = MathsToolkit.scale(dP1, Length);

		// Offset for boundary
		Point2D.Double off = MathsToolkit.scale(dP1, -(Double) offset
				.getValue()
				/ 256 * Length);

		GeneralPath redPath = new GeneralPath();
		GeneralPath greenPath = new GeneralPath();
		GeneralPath bluePath = new GeneralPath();

		for (int i = 0; i < redData.length; i++)
		{
			if (i == 0)
			{
				redPath.moveTo(redData[i].x + off.x, redData[i].y + off.y);
				greenPath
						.moveTo(greenData[i].x + off.x, greenData[i].y + off.y);
				bluePath.moveTo(blueData[i].x + off.x, blueData[i].y + off.y);
			} else
			{
				redPath.lineTo(redData[i].x + off.x, redData[i].y + off.y);
				greenPath
						.lineTo(greenData[i].x + off.x, greenData[i].y + off.y);
				bluePath.lineTo(blueData[i].x + off.x, blueData[i].y + off.y);
			}
		}

		float alp = SwingToolkit.getSliderPos(alpha);
		if ((1 - alp) > 0.001)
		{
			g.setComposite(AlphaComposite
					.getInstance(AlphaComposite.SRC_OVER, alp));
		}

		if (gradientBack.isSelected() || boxBack.isSelected())
		{
			GeneralPath path = new GeneralPath();
			path.moveTo(p1.x + off.x, p1.y + off.y);
			path.lineTo((p1.x + val.x) + off.x, (p1.y + val.y) + off.y);
			path.lineTo((p2.x + val.x) + off.x, (p2.y + val.y) + off.y);
			path.lineTo((p2.x) + off.x, (p2.y) + off.y);
			path.closePath();

			if (gradientBack.isSelected())
			{
				GradientPaint grad = new GradientPaint((float) (p1.x + off.x),
						(float) (p1.y + off.y), Color.BLACK, (float) (p1.x
								+ val.x + off.x),
						(float) (p1.y + val.y + off.y), Color.WHITE);
				g.setPaint(grad);
				g.fill(path);
			}

			if (boxBack.isSelected())
			{
				g.setPaint(Color.RED);
				g.draw(path);

			}
		}

		if (showRed.isSelected())
		{
			g.setColor(Color.RED);
			g.draw(redPath);
		}

		if (showGreen.isSelected())
		{
			g.setColor(Color.GREEN);
			g.draw(greenPath);
		}

		if (showBlue.isSelected())
		{
			g.setColor(Color.blue);
			g.draw(bluePath);
		}
		// g.setClip(oringalClip);
		g.setStroke(oldStroke);
		g.setRenderingHints(oldHints);

		super.draw(g1);
	}

	public synchronized void updateProfile()
	{

		if (redValue.size() != (Integer) xAxisNum.getValue())
		{
			redValue.clear();
			greenValue.clear();
			blueValue.clear();

			redData = new Point2D.Float[(Integer) xAxisNum.getValue()];
			greenData = new Point2D.Float[(Integer) xAxisNum.getValue()];
			blueData = new Point2D.Float[(Integer) xAxisNum.getValue()];

			for (int i = 0; i < (Integer) xAxisNum.getValue(); i++)
			{
				redValue.add(0.0f);
				greenValue.add(0.0f);
				blueValue.add(0.0f);

				redData[i] = new Point2D.Float();
				greenData[i] = new Point2D.Float();
				blueData[i] = new Point2D.Float();
			}
			forceUpdate = true;
		}
		Point2D.Double p1 = points.get(0);
		Point2D.Double p2 = points.get(1);

		// if (p2.x < p1.x)
		// {
		// p1 = points.get(1);
		// p2 = points.get(0);
		// }

		if (lastP1.distance(p1) < 0.000001 && lastP2.distance(p2) < 0.000001
				&& Math.abs(lastScale - panel.getScale()) < 0.001
				&& !forceUpdate)
		{
			return;
		}
		forceUpdate = false;
		lastP1 = p1;
		lastP2 = p2;
		lastScale = panel.getScale();
		Point2D.Double dP1 = new Point2D.Double(p1.y - p2.y, p2.x - p1.x);

		int projectNum = (Integer) avgNum.getValue();
		float[] redHolder = new float[projectNum * 2 + 1];
		float[] greenHolder = new float[projectNum * 2 + 1];
		float[] blueHolder = new float[projectNum * 2 + 1];

		int count = 0;
		for (int xPos = 0; xPos < (Integer) xAxisNum.getValue(); xPos++)
		{
			float pos = xPos / ((float) (Integer) xAxisNum.getValue());

			Point2D.Double p = MathsToolkit.getLinePoint(p1, p2, pos);
			MathsToolkit.normalise(dP1);
			count = 0;
			for (int pro = -projectNum; pro <= projectNum || pro == 0; pro++)
			{
				int x = (int) (p.x + dP1.x * pro);
				int y = (int) (p.y + dP1.y * pro);

				Color c;
				if (x > 0 && x < getPanel().getImage().getWidth() && y > 0
						&& y < getPanel().getImage().getHeight())
				{
					c = new Color(getPanel().getImage().getRGB(x, y));
				} else
				{
					c = new Color(0, 0, 0);
				}

				redHolder[count] = c.getRed() / 256f;
				greenHolder[count] = c.getGreen() / 256f;
				blueHolder[count] = c.getBlue() / 256f;
				count++;

			}
			redValue.set(xPos, getStats(redHolder));
			greenValue.set(xPos, getStats(greenHolder));
			blueValue.set(xPos, getStats(blueHolder));

			dP1 = MathsToolkit.scale(dP1, (Double) yAxisHigh.getValue());

			redData[xPos].x = (float) (p.x + dP1.x * redValue.get(xPos));
			redData[xPos].y = (float) (p.y + dP1.y * redValue.get(xPos));

			greenData[xPos].x = (float) (p.x + dP1.x * greenValue.get(xPos));
			greenData[xPos].y = (float) (p.y + dP1.y * greenValue.get(xPos));

			blueData[xPos].x = (float) (p.x + dP1.x * blueValue.get(xPos));
			blueData[xPos].y = (float) (p.y + dP1.y * blueValue.get(xPos));
		}

	}

	public float getStats(float[] data)
	{

		return DataAnalysisToolkit.getAveragef(data);
	}

	public static void main(String input[]) throws IOException
	{
		final ROIPanel panel = new ROIPanel(false);
		ProfilePlotter plot = new ProfilePlotter(panel);

		plot.setPanel(panel);
		plot.setListening(true);

		BufferedImage img = ImageOperations.getBi(1500, 1500);
		Graphics2D g = img.createGraphics();

		g.setPaint(new GradientPaint(100, 0, new Color(1f, 0, 0), img
				.getWidth(), 0, new Color(0, 0, 0)));
		g.fillRect(100, 0, img.getWidth(), img.getHeight() / 3);

		g.setPaint(new GradientPaint(100, 0, new Color(0, 1f, 0), img
				.getWidth(), 0, new Color(0, 0, 0)));
		g
				.fillRect(100, img.getHeight() / 3, img.getWidth(), img
						.getHeight() / 3);

		g.setPaint(new GradientPaint(100, 0, new Color(0f, 0, 1f), img
				.getWidth(), 0, new Color(0, 0, 0)));
		g.fillRect(100, img.getHeight() / 3 * 2, img.getWidth(), img
				.getHeight() / 3);

		// BufferedImage img = ImageFileSelector.getUserImage();

		// img = ImageOperations.cloneImage(img);
		// panel.setImage(ImageFileSelector.getUserImage());
		// ImageOperations.fillWithRandomColors(img);

		panel.setImage(img);

		JPanel holder = new JPanel();
		panel.putIntoPanel(holder);

		JButton loadImage = new JButton("Load");

		loadImage.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				try
				{
					BufferedImage img = ImageFileSelector.getUserImage();

					panel.setImage(ImageOperations.cloneImage(img));
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JPanel tool = new JPanel(new BorderLayout());
		tool.add(loadImage, BorderLayout.NORTH);
		tool.add(plot.getToolPanel(), BorderLayout.CENTER);
		JSplitPane split = new JSplitPane();
		split.setLeftComponent(tool);
		split.setRightComponent(holder);

		FrameFactroy.getFrame(split);

	}
}
