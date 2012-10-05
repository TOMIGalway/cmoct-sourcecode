package com.joey.software.memoryToolkit;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * A demo application showing a dynamically updated chart that displays the
 * current JVM memory usage.
 * <p>
 * IMPORTANT NOTE: THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE. DO
 * NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!!
 */
public class MemoryUsagePanel extends JPanel
{
	/** Time series for total memory used. */
	private TimeSeries total;

	/** Time series for free memory. */
	private TimeSeries free;

	private TimeSeries used;

	private TimeSeries max;

	DataGenerator gen;

	public static JFrame getMemoryUsagePanel(int dataNum, int delay)
	{
		final MemoryUsagePanel u = new MemoryUsagePanel(dataNum, delay);
		JFrame f = new JFrame("Memory Usage");
		f.setLayout(new BorderLayout());
		f.getContentPane().add(u, BorderLayout.CENTER);
		f.setSize(800, 400);
		f.setVisible(true);

		final JButton runButton = new JButton("Start");
		runButton.addActionListener(new ActionListener()
		{

			boolean running = false;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (running)
				{
					running = false;
					u.stopUpdating();
					runButton.setText("Start");
				} else
				{
					running = true;
					u.startUpdating();
					runButton.setText("Stop");
				}
			}
		});

		JButton garbage = new JButton("Run GC");
		garbage.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.gc();
			}
		});

		JPanel temp = new JPanel(new GridLayout(1, 1));

		temp.add(runButton);
		temp.add(garbage);

		f.getContentPane().add(temp, BorderLayout.SOUTH);

		return f;
	}

	/**
	 * Creates a new application.
	 * 
	 * @param historyCount
	 *            the history count (in milliseconds).
	 */
	public MemoryUsagePanel(int historyCount, int interval)
	{
		super(new BorderLayout());
		// create two series that automatically discard data more than 30
		// seconds old...
		this.total = new TimeSeries("Total Memory", Millisecond.class);
		this.total.setMaximumItemCount(historyCount);
		this.free = new TimeSeries("Free Memory", Millisecond.class);
		this.free.setMaximumItemCount(historyCount);
		this.used = new TimeSeries("Used Memory", Millisecond.class);
		this.used.setMaximumItemCount(historyCount);
		this.max = new TimeSeries("Used Memory", Millisecond.class);
		this.max.setMaximumItemCount(historyCount);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.total);
		dataset.addSeries(this.free);
		dataset.addSeries(this.used);
		dataset.addSeries(this.max);

		DateAxis domain = new DateAxis("Time");
		NumberAxis range = new NumberAxis("Memory");

		domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

		range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.green);
		renderer.setSeriesPaint(2, Color.black);

		renderer.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JFreeChart chart = new JFreeChart("JVM Memory Usage", new Font(
				"SansSerif", Font.BOLD, 24), plot, true);
		chart.setBackgroundPaint(Color.white);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(4, 4, 4, 4), BorderFactory
				.createLineBorder(Color.black)));
		add(chartPanel);

		gen = new DataGenerator(interval);

	}

	public void startUpdating()
	{
		gen.start();
	}

	public void stopUpdating()
	{
		gen.stop();
	}

	/**
	 * Adds an observation to the otal memory time series.
	 * 
	 * @param y
	 *            the total memory used.
	 */
	private void addTotalObservation(double y)
	{
		this.total.add(new Millisecond(), y);
	}

	/**
	 * Adds an observation to the free memory time series.
	 * 
	 * @param y
	 *            the free memory.
	 */
	private void addFreeObservation(double y)
	{
		this.free.add(new Millisecond(), y);
	}

	private void addUsedObservation(double y)
	{
		this.used.add(new Millisecond(), y);
	}

	private void addMaxObservation(double y)
	{
		this.max.add(new Millisecond(), y);
	}

	/**
	 * The data generator.
	 */
	class DataGenerator extends Timer implements ActionListener
	{
		boolean running = true;

		/**
		 * Constructor.
		 * 
		 * @param interval
		 *            the interval (in milliseconds)
		 */
		DataGenerator(int interval)
		{
			super(interval, null);
			addActionListener(this);
		}

		/**
		 * Adds a new free/total memory reading to the dataset.
		 * 
		 * @param event
		 *            the action event.
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			long f = Runtime.getRuntime().freeMemory() / 1024;
			long t = Runtime.getRuntime().totalMemory() / 1024;
			long m = Runtime.getRuntime().maxMemory() / 1024;
			long u = t - f;
			long g = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().getAvailableAcceleratedMemory();
			addTotalObservation(t);
			addFreeObservation(f);
			addUsedObservation(u);
			addMaxObservation(m);
		}
	}

	/**
	 * Entry point for the sample application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Memory Usage Demo");
		MemoryUsagePanel panel = new MemoryUsagePanel(500, 20);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setBounds(200, 120, 600, 280);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		panel.startUpdating();

		byte[][][] data = new byte[1024][1024][10];
	}
}