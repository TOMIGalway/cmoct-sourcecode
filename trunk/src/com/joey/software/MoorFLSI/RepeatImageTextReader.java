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
package com.joey.software.MoorFLSI;


import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.joey.software.VideoToolkit.BufferedImageStreamToAvi;
import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.fileToolkit.CSVFileToolkit;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.DynamicRangeImage;
import com.joey.software.plottingToolkit.PlotingToolkit;
import com.joey.software.regionSelectionToolkit.ROIPanel;
import com.joey.software.regionSelectionToolkit.ROIPanelListner;
import com.joey.software.stringToolkit.StringOperations;


public class RepeatImageTextReader extends JPanel
{
	DynamicRangeImage image = new DynamicRangeImage();

	JSpinner currentValue = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));

	LinkedHashMap<Integer, short[][]> imageData = new LinkedHashMap<Integer, short[][]>();

	LinkedHashMap<Integer, Date> imageTime = new LinkedHashMap<Integer, Date>();

	int high;

	int wide;

	int currentImage = 0;

	JFreeChart chart = ChartFactory.createXYLineChart("Stat Data", // Title
	"X - Axis", // X-Axis label
	"Y - Axis", // Y-Axis label
	new XYSeriesCollection(), // Dataset
	PlotOrientation.VERTICAL, true, // Show legend
	true, true);

	ChartPanel chartPanel = new ChartPanel(chart);

	JTextArea dataField = new JTextArea();

	JFrame staticsHolderFrame = new JFrame("Stats");

	JButton outputImages = new JButton("Export Images");

	public RepeatImageTextReader() throws IOException
	{
		createJPanel();
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		add(currentValue, BorderLayout.NORTH);
		add(image, BorderLayout.CENTER);

		currentValue.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				setCurrentImage((Integer) currentValue.getValue());
			}
		});

		// Create the text output panel
		JPanel outputPanel = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(dataField);
		outputPanel.add(scrollPane, BorderLayout.CENTER);

		JTabbedPane tabPanel = new JTabbedPane();
		tabPanel.addTab("Graph", chartPanel);
		tabPanel.addTab("Data", outputPanel);
		staticsHolderFrame.getContentPane().setLayout(new BorderLayout());
		staticsHolderFrame.getContentPane().removeAll();
		staticsHolderFrame.getContentPane().add(tabPanel, BorderLayout.CENTER);
		staticsHolderFrame.getContentPane()
				.add(outputImages, BorderLayout.SOUTH);

		// Add region panel to image
		image.getImage().setControler(ROIPanel.TYPE_RECTANGLE);
		image.getImage().setAllowMultipleROI(false);
		image.getImage().addROIPanelListner(new ROIPanelListner()
		{

			@Override
			public void regionAdded(Shape region)
			{

				updateData();
			}

			@Override
			public void regionChanged()
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void regionRemoved(Shape region)
			{
				// TODO Auto-generated method stub

			}
		});

		outputImages.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveImages();
			}
		});
	}

	public void saveImages()
	{
		try
		{
			BufferedImageStreamToAvi videoOut = new BufferedImageStreamToAvi(
					image.getImage().getImage().getWidth(), image.getImage()
							.getImage().getHeight(), 2, "c:\\test\\images\\",
					"video.avi", true, true);

			for (int i = 0; i < imageData.size(); i++)
			{
				setCurrentImage(i);
				BufferedImage rst = image.getImage().getImage();

				try
				{
					videoOut.pushImage(rst);
					ImageIO.write(rst, "PNG", new File(
							"c:\\test\\images\\image"
									+ StringOperations.getNumberString(3, i)
									+ ".png"));
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			videoOut.finaliseVideo();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showStaticsWindow()
	{
		staticsHolderFrame.setVisible(true);
	}

	public void updateData()
	{
		try
		{
			Rectangle rec = (Rectangle) image.getImage().getRegions().get(0);

			int max[] = new int[imageData.size()];
			int min[] = new int[imageData.size()];
			float avg[] = new float[imageData.size()];
			float std[] = new float[imageData.size()];
			for (int i = 0; i < imageData.size(); i++)
			{
				max[i] = imageData.get(i)[rec.x][rec.y];
				min[i] = imageData.get(i)[rec.x][rec.y];
				long val = 0;
				long count = 0;

				/*
				 * Determine max/min and average
				 */
				for (int x = rec.x; x < rec.x + rec.width; x++)
				{
					for (int y = rec.y; y < rec.y + rec.height; y++)
					{
						val += imageData.get(i)[x][y];
						count++;
						if (max[i] < imageData.get(i)[x][y])
						{
							max[i] = imageData.get(i)[x][y];
						}
						if (min[i] > imageData.get(i)[x][y])
						{
							min[i] = imageData.get(i)[x][y];
						}
					}
				}
				avg[i] = (float) val / (float) count;

				/*
				 * Determin other stats
				 */

				float varSqr = 0;

				for (int x = rec.x; x < rec.x + rec.width; x++)
				{
					for (int y = rec.y; y < rec.y + rec.height; y++)
					{
						float d = imageData.get(i)[x][y];
						varSqr += (d - avg[i]) * (d - avg[i]);
					}
				}
				varSqr /= imageData.size();

				std[i] = (float) Math.sqrt(varSqr);
			}

			float[] xData = getTimeDifference();

			XYSeriesCollection datCol = PlotingToolkit
					.getCollection(xData, avg, "Data");

			chart.getXYPlot().setDataset(0, datCol);

			String avgCol = CSVFileToolkit.getCSVColumn(avg);
			String maxCol = CSVFileToolkit.getCSVColumn(max);
			String minCol = CSVFileToolkit.getCSVColumn(min);
			String stdCol = CSVFileToolkit.getCSVColumn(std);
			String timeCol = CSVFileToolkit.getCSVColumn(imageTime.values()
					.toArray());
			String minTimeCol = CSVFileToolkit.getCSVColumn(xData);

			String result = "";
			result = CSVFileToolkit.joinDataRight(result, timeCol);
			result = CSVFileToolkit.joinDataRight(result, minTimeCol);
			result = CSVFileToolkit.joinDataRight(result, avgCol);
			result = CSVFileToolkit.joinDataRight(result, maxCol);
			result = CSVFileToolkit.joinDataRight(result, minCol);
			result = CSVFileToolkit.joinDataRight(result, stdCol);

			result = CSVFileToolkit.getTrimmedData(result);

			XYLineAndShapeRenderer render = new XYLineAndShapeRenderer(false,
					true);

			chart.getXYPlot().setRenderer(render);
			dataField.setText(result);

			System.out
					.println("\n\n\n##########################################################################################\n##########################################################################################\n\n\n\n\n");
			System.out.println(result);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public float[] getTimeDifference()
	{
		Date firstTime = imageTime.get(currentValue.getValue());
		float[] result = new float[imageTime.size()];

		for (int i = 0; i < imageTime.size(); i++)
		{
			long diff = imageTime.get(i).getTime() - firstTime.getTime();
			result[i] = diff / 1000.0f / 60.0f;
		}
		return result;

	}

	public void exportvideo()
	{

	}

	public void setCurrentImage(int index)
	{
		float min = (float) image.getMinValue();
		float max = (float) image.getMaxValue();
		image.setDataShort(imageData.get(index));
		image.repaint();
		image.setMinValue(min);
		image.setMaxValue(max);
	}

	public void saveData(File f) throws IOException
	{
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(f)));

		out.writeInt(imageData.size());
		out.writeInt(wide);
		out.writeInt(high);

		for (int i = 0; i < imageData.size(); i++)
		{
			System.out.println(i);
			out.writeLong(imageTime.get(i).getTime());
			for (int x = 0; x < wide; x++)
			{
				for (int y = 0; y < high; y++)
				{
					out.writeShort(imageData.get(i)[x][y]);
				}
			}
		}

		out.close();

	}

	public void loadData(File f, boolean clearData) throws IOException
	{
		BufferedInputStream inReader = new BufferedInputStream(
				new FileInputStream(f), 1000000);// RandomAccessFile in = new
		// RandomAccessFile(f, "r");
		DataInputStream in = new DataInputStream(inReader);
		int tot = in.readInt();
		wide = in.readInt();
		high = in.readInt();

		short[][][] holder = new short[tot][wide][high];

		byte[] inputHolder = new byte[high * 2];
		for (int i = 0; i < tot; i++)
		{
			Date d = new Date(in.readLong());
			for (int x = 0; x < wide; x++)
			{
				in.read(inputHolder);
				for (int y = 0; y < high; y++)
				{
					holder[i][wide - 1 - x][high - y - 1] = BinaryToolkit
							.readShort(inputHolder, y * 2);
				}
				// for (int y = 0; y < high; y++)
				// {
				// holder[i][x][y] = in.readShort();
				// }
			}
			addData(d, holder[i]);
		}
		((SpinnerNumberModel) currentValue.getModel()).setMaximum(imageData
				.size() - 1);
		in.close();
		setCurrentImage(0);
		image.updateMaxMin();
	}

	public void addData(Date time, short[][] data)
	{
		imageData.put(imageData.size(), data);
		imageTime.put(imageTime.size(), time);
		System.out.println("Img,Tim : " + imageData.size() + ","
				+ imageTime.size());
	}

	public void loadTextData(File file)
	{
		try
		{
			RandomAccessFile in = new RandomAccessFile(file, "r");

			// Skip header
			in.readLine();
			in.readLine();
			in.readLine();

			// Skip Subject Information
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			String startTimeInput = in.readLine();
			String commentsInput = in.readLine();

			String data = in.readLine();
			while (!data.startsWith("2) System Configuration"))
			{
				commentsInput += data;
				data = in.readLine();
			}
			// System configuration

			// in.readLine();
			in.readLine();
			String timeCounstantInput = in.readLine();
			String cameraGainInput = in.readLine();
			String exposureTimeInput = in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			String resolutionInput = in.readLine();

			// Time Data
			in.readLine();
			String timeDataInput = in.readLine();
			String totalImagesInput = in.readLine();
			in.readLine();
			in.readLine();
			// in.readLine();
			// System.out.println(in.readLine());
			// in.readLine();

			// Parse important Size

			high = (new Scanner(resolutionInput.split(":")[1])).nextInt();
			wide = (new Scanner(resolutionInput.split(",")[1])).nextInt();
			int tot = 1;
			try
			{
				tot = (new Scanner(totalImagesInput.split(":")[1])).nextInt();
			} catch (Exception e)
			{

			}
			System.out.println(wide + "," + high);
			// Parse timeInformation
			SimpleDateFormat format = new SimpleDateFormat(
					"hh:mm:ss (dd/MM/yy)");
			Date startTime = null;
			try
			{
				startTime = format.parse(startTimeInput.split(": ")[1]);
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String[] frameTimeData = timeDataInput.split("information:")[1]
					.split(",");

			Date[] timeInfo = new Date[tot];
			for (int i = 0; i < frameTimeData.length - 1; i++)
			{
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(startTime);
				String dat = (frameTimeData[i]);
				String[] timeVals = dat.split(":");

				int hour = Integer.parseInt(StringOperations
						.removeNonNumber(timeVals[0]));
				int min = Integer.parseInt(StringOperations
						.removeNonNumber(timeVals[1]));
				int sec = Integer.parseInt(StringOperations
						.removeNonNumber(timeVals[2]));
				int msec = Integer.parseInt(StringOperations
						.removeNonNumber(timeVals[3]));

				cal.add(Calendar.HOUR_OF_DAY, hour);
				cal.add(Calendar.MINUTE, min);
				cal.add(Calendar.SECOND, sec);
				cal.add(Calendar.MILLISECOND, msec);
				timeInfo[i] = cal.getTime();
			}

			// Parse Image Data
			/*
			 * Close Random access file and switch to scanner first store pos
			 * then move to correct point.
			 */
			long pos = in.getFilePointer();
			in.close();

			FileInputStream fIn = new FileInputStream(file);
			fIn.skip(pos);

			BufferedInputStream bIn = new BufferedInputStream(fIn);
			Scanner sIn = new Scanner(bIn);

			short[][][] holder = new short[tot][wide][high];

			JFrame f = new JFrame();
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			StatusBarPanel stat = new StatusBarPanel();
			stat.setMaximum(high);
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(stat, BorderLayout.CENTER);
			f.setSize(200, 60);
			f.setVisible(true);

			for (int i = 0; i < tot; i++)
			{
				// Skip over the heading values
				stat.setStatusMessage("Loading " + i + " of " + tot);
				sIn.useDelimiter("\n");
				sIn.next();
				sIn.next();
				sIn.next();
				if (i != 0)
				{
					sIn.next();
				}
				sIn.reset();
				for (int y = 0; y < high; y++)
				{
					stat.setValue(y);
					sIn.nextInt();
					for (int x = 0; x < wide; x++)
					{
						holder[i][x][y] = sIn.nextShort();
					}

				}
				addData(timeInfo[i], holder[i]);
			}

			// FrameFactroy.getFrame(new DynamicRangeImage(data[0]));
			// Start Image Data

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadTextDataFluxSingle(File file)
	{
		try
		{
			RandomAccessFile in = new RandomAccessFile(file, "r");

			// Skip header
			in.readLine();
			in.readLine();
			in.readLine();

			// Skip Subject Information
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			String startTimeInput = in.readLine();
			String commentsInput = in.readLine();

			String data = in.readLine();
			while (!data.startsWith("2) System Configuration"))
			{
				commentsInput += data;
				data = in.readLine();
			}
			// System configuration

			// in.readLine();
			in.readLine();
			String timeCounstantInput = in.readLine();
			String cameraGainInput = in.readLine();
			String exposureTimeInput = in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			String resolutionInput = in.readLine();

			// in.readLine();
			// System.out.println(in.readLine());
			// in.readLine();

			// Parse important Size

			high = (new Scanner(resolutionInput.split(":")[1])).nextInt();
			wide = (new Scanner(resolutionInput.split(",")[1])).nextInt();
			int tot = 1;
			while (!data.startsWith("3) Flux Image Data"))
			{
				System.out.println(data);
				data = in.readLine();
			}
			in.readLine();
			// Parse Image Data
			/*
			 * Close Random access file and switch to scanner first store pos
			 * then move to correct point.
			 */
			long pos = in.getFilePointer();
			in.close();

			FileInputStream fIn = new FileInputStream(file);
			fIn.skip(pos);

			BufferedInputStream bIn = new BufferedInputStream(fIn);
			Scanner sIn = new Scanner(bIn);

			short[][] holder = new short[wide][high];

			JFrame f = new JFrame();
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			StatusBarPanel stat = new StatusBarPanel();
			stat.setMaximum(high);
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(stat, BorderLayout.CENTER);
			f.setSize(200, 60);
			f.setVisible(true);

			// Skip over the heading values

		

			sIn.reset();
			
			for (int y = 0; y < high; y++)
			{
				System.out.println(sIn.nextInt());
				try
				{
					for (int x = 0; x < wide; x++)
					{
						holder[x][y] = sIn.nextShort();
					}
				} catch (Throwable e)
				{

				}

			}
			addData(new Date(), holder);

			FrameFactroy.getFrame(new DynamicRangeImage(holder));
			// Start Image Data

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String input[]) throws IOException
	{
		RepeatImageTextReader r = new RepeatImageTextReader();
		// try
		// {
		//
		// r
		// .loadTextData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 2\\mainData 1 (RFlux).txt"));
		//
		// r
		// .loadTextData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 2\\mainData 2 (RFlux).txt"));

		// r
		// .loadTextData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 1\\mouse restore 1 (RFlux).txt"));
		//
		// r
		// .loadTextData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 1\\mouse restore 2 (RFlux).txt"));
		//
		// r
		// .loadTextData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 1\\mouse restore 3 (RFlux).txt"));
		// } catch (Exception e)
		// {
		// for (int i = 0; i < 100; i++)
		// {
		// java.awt.Toolkit.getDefaultToolkit().beep();
		// }
		// System.exit(0);
		// }

		// r
		// .loadData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\Data\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 1\\rawData.flx"),
		// false);
		// r
		// .loadData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\Data\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 2\\rawData.flx"),
		// false);

		r
				.loadTextDataFluxSingle(new File(
						"I:\\High Res Video 2001 (Flux).txt"));
		// r
		// .loadData(new File(
		// "C:\\Users\\joey.enfield\\Desktop\\St Louis\\AR Pam\\Hymodynamic Monitor\\Run 1\\data.flx"));
		FrameFactroy.getFrame(r);
		r.showStaticsWindow();
	}
}
