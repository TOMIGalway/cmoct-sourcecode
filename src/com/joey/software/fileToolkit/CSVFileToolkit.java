package com.joey.software.fileToolkit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import com.joey.software.mathsToolkit.DataAnalysisToolkit;
import com.joey.software.stringToolkit.StringOperations;


public class CSVFileToolkit
{
	static JFileChooser choser = new JFileChooser();

	/**
	 * This will get all the data form a csv string data. It will first pad the
	 * data so that even blank elements are included
	 * 
	 * @param csvData
	 * @return
	 */
	public static String[][] getData(String csvData)
	{
		padData(csvData);
		String[] lineData = StringOperations.splitString(csvData, '\n');
		String[][] result = new String[lineData.length][];

		for (int i = 0; i < lineData.length; i++)
		{
			result[i] = StringOperations.splitString(lineData[i], ',');
		}

		return result;
	}

	public static void writeCSVData(File f, String data) throws IOException
	{
		PrintWriter out = new PrintWriter(f);
		out.print(data);
		out.flush();
		out.close();
	}

	public static String[][] getData(File f) throws IOException
	{
		String read = readData(f);

		return getData(read);
	}

	public static double[][] getDoubleData(File f) throws IOException
	{
		String[][] data = getData(f);
		return getDoubleData(data);
	}

	public static double[][] getDoubleData(String[][] input)
	{
		return getDoubleData(input, 0, input.length, 0, input[0].length);
	}

	public static double[][] getDoubleData(String[][] input, int xMin, int xMax, int yMin, int yMax)
	{
		int xSize = xMax - xMin;
		int ySize = yMax - yMin;

		double[][] rst = new double[xSize][ySize];

		for (int x = xMin; x < xMax; x++)
		{
			for (int y = yMin; y < yMax; y++)
			{
				try
				{
					rst[x - xMin][y - yMin] = Double.parseDouble(input[x][y]);
				} catch (Exception e)
				{
					// System.out.printf("Error[%d,%d] - %s\n", x, y,
					// input[x][y]);
					rst[x - xMin][y - yMin] = 0;
				}
			}
		}

		return rst;
	}

	public static int[][] getIntegerData(File f) throws IOException
	{
		String[][] data = getData(f);
		return getIntegerData(data);
	}

	public static int[][] getIntegerData(String[][] input)
	{
		return getIntegerData(input, 0, input.length, 0, input[0].length);
	}

	public static int[][] getIntegerData(String[][] input, int xMin, int xMax, int yMin, int yMax)
	{
		int xSize = xMax - xMin;
		int ySize = yMax - yMin;

		int[][] rst = new int[xSize][ySize];

		for (int x = xMin; x < xMax; x++)
		{
			for (int y = yMin; y < yMax; y++)
			{
				try
				{
					rst[x - xMin][y - yMin] = Integer.parseInt(input[x][y]);
				} catch (Exception e)
				{
					// System.out.printf("Error[%d,%d] - %s\n", x, y,
					// input[x][y]);
					rst[x - xMin][y - yMin] = 0;
				}
			}
		}

		return rst;
	}

	public static String readData(File f) throws IOException
	{
		BufferedInputStream input = new BufferedInputStream(
				new FileInputStream(f), 1024 * 1024);
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		StringBuilder build = new StringBuilder();

		String line = in.readLine();
		while (line != null)
		{
			build.append(line);
			build.append("\n");
			line = in.readLine();
		}
		return build.toString();
	}

	public static String getCSVDataRow(Object[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (Object o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append(",");
			}
			result.append(o.toString());
		}

		return result.toString();
	}

	public static String getCSVDataRow(int[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (int o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append(",");
			}
			result.append(o);
		}

		return result.toString();
	}

	public static String getCSVDataRow(double[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (double o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append(",");
			}
			result.append(o);
		}

		return result.toString();
	}

	public static String getCSVDataRow(float[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (float o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append(",");
			}
			result.append(o);
		}

		return result.toString();
	}

	public static String getCSVColumn(Object[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (Object o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append("\n");
			}
			result.append(o.toString());
		}

		return result.toString();
	}

	public static String getCSVColumn(float[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (float o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append("\n");
			}
			result.append(o);
		}

		return result.toString();
	}

	public static String getCSVColumn(int[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (int o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append("\n");
			}
			result.append(o);
		}

		return result.toString();
	}

	public static String getCSVColumn(double[] data)
	{
		StringBuilder result = new StringBuilder();

		boolean first = true;
		for (double o : data)
		{
			if (first)
			{
				first = false;
			} else
			{
				result.append("\n");
			}
			result.append(o);
		}

		return result.toString();
	}

	/**
	 * This function will output the given string to a user selected csv file
	 * 
	 * @param data
	 *            - String to be written to a .csv file
	 */
	public static void outputDataToCSVFile(String data)
	{
		try
		{

			choser.setFileFilter(new FileFilter()
			{

				@Override
				public boolean accept(File f)
				{
					// TODO Auto-generated method stub
					return f.getName().endsWith(".csv") || f.isDirectory();
				}

				@Override
				public String getDescription()
				{
					// TODO Auto-generated method stub
					return "CSV File";
				}
			});
			int returnVal = choser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = choser.getSelectedFile();
				/**
				 * Add .csv file to the file name
				 */
				if (!file.getName().toLowerCase().endsWith(".csv"))
				{
					file = new File(file.getAbsolutePath() + ".csv");
				}

				if (file.exists())
				{
					int choice = JOptionPane
							.showConfirmDialog(null, "The file already exists, Would you like to overwrite it?", "File Exists", JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.NO_OPTION)
					{
						return;
					}
				} else
				{
					file.createNewFile();
				}

				FileWriter outputFileStream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(outputFileStream);
				out.write(data);
				out.close();

			}
		} catch (IOException excption)
		{
			JOptionPane.showMessageDialog(null, excption.getMessage());
		}
	}

	/**
	 * This function will pad data so that there is an even number of "," in
	 * each line ie The String<Br>
	 * Hello,How,Are<Br>
	 * How,<BR>
	 * 
	 * Becomes Hello,How,Are How,,
	 * 
	 * @param input
	 * @return
	 */
	public static String padData(String input)
	{
		StringBuilder result = new StringBuilder();
		int maxData = 0;

		String[] line = StringOperations.splitString(input, '\n');

		for (int i = 0; i < line.length; i++)
		{
			String[] section = StringOperations.splitString(line[i], ',');
			if (section.length > maxData)
			{
				maxData = section.length;
			}
		}

		for (int i = 0; i < line.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			String[] lineData = StringOperations.splitString(line[i], ',');
			for (int y = 0; y < maxData; y++)
			{
				if (y != 0)
				{
					result.append(",");
				}
				if (y < lineData.length)
				{
					result.append(lineData[y]);
				}

			}

		}

		return result.toString();
	}

	/**
	 * This function will add a given string on to the Before of each line in a
	 * csv data. ie the,end<BR>
	 * is,now<BR>
	 * padded with X becomes
	 * 
	 * Xthe,end<BR>
	 * Xis,now<Br>
	 * 
	 * @param lineData
	 *            - The CSV Data
	 * @param data
	 *            - The data to pad
	 * @return
	 */
	public static String addBeforeLines(String lineData, String data)
	{
		StringBuffer result = new StringBuffer();
		String[] line = StringOperations.splitString(lineData, '\n');
		for (int i = 0; i < line.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			result.append(data);
			result.append(line[i]);

		}
		return result.toString();
	}

	/**
	 * This function will add a given string on to the END of each line in a csv
	 * data. ie the,end<BR>
	 * is,now<BR>
	 * padded with X becomes
	 * 
	 * the,endX<BR>
	 * is,nowX<Br>
	 * 
	 * @param lineData
	 *            - The CSV Data
	 * @param data
	 *            - The data to pad
	 * @return
	 */
	public static String addAfterLines(String lineData, String data)
	{
		StringBuffer result = new StringBuffer();
		String[] line = StringOperations.splitString(lineData, '\n');
		for (int i = 0; i < line.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			result.append(line[i]);
			result.append(data);

		}
		return result.toString();
	}

	/**
	 * This will return a trimmed data (it will remove any blank rows or cols.
	 * The data will be padded first
	 * 
	 * @param data
	 * @return
	 */
	public static String getTrimmedData(String data)
	{
		data = padData(data);
		String[][] cells = getData(data);

		boolean rowClear[] = new boolean[cells.length];
		boolean colClear[] = new boolean[cells[0].length];

		for (int i = 0; i < rowClear.length; i++)
		{
			rowClear[i] = true;
		}

		for (int i = 0; i < colClear.length; i++)
		{
			colClear[i] = true;
		}

		for (int row = 0; row < cells.length; row++)
		{
			for (int col = 0; col < cells[0].length; col++)
			{
				String val = cells[row][col];
				if (val.length() != 0)
				{
					rowClear[row] = false;
					colClear[col] = false;
				}
			}
		}

		int rowCount = 0;
		for (int i = 0; i < rowClear.length; i++)
		{
			if (!rowClear[i])
			{
				rowCount++;
			}
		}

		int colCount = 0;

		for (int i = 0; i < colClear.length; i++)
		{
			if (!colClear[i])
			{
				colCount++;
			}

		}

		String[][] result = new String[rowCount][colCount];

		int rowIndex = 0;
		for (int row = 0; row < cells.length; row++)
		{
			if (!rowClear[row])
			{
				int colIndex = 0;
				for (int col = 0; col < cells[0].length; col++)
				{
					if (!colClear[col])
					{
						result[rowIndex][colIndex] = cells[row][col];
						colIndex++;
					}
				}
				rowIndex++;
			}
		}
		return getCSVData(result);
	}

	public static void main(String input[]) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		String data = "";
		data += "Testing,,B1,,,,,,,,,,,hello\n";
		data += ",,\n";
		data += "A2,,B2\n";

		System.out.println("Before");
		printCSVData(data);

		data = padData(data);
		System.out.println("After");
		fancyPrintCSVData(data);

		data = "Test";
		System.out.println(makeSize(data, 15, '0'));

	}

	/**
	 * @toDo Fix this to be faster This will return the number of rows in a csv
	 *       data set
	 * 
	 * @param data
	 * @return
	 */

	public static int getRowNum(String data)
	{
		int row = StringOperations.splitString(data, '\n').length;
		return row;
	}

	/**
	 * This function will get the max number of colums in a set of csv data
	 * 
	 * @param data
	 * @return
	 */
	public static int getColNum(String data)
	{
		int maxData = 0;
		String[] line = StringOperations.splitString(data, '\n');
		for (int i = 0; i < line.length; i++)
		{
			String[] section = StringOperations.splitString(line[i], ',');
			if (section.length > maxData)
			{
				maxData = section.length;
			}
		}

		return maxData;
	}

	/**
	 * This will join two sets of data together.
	 * 
	 * data2 is added to the right of data 1.
	 * 
	 * A set of comma's added to the start of data 1 no matter what!
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static String joinDataRight(String data1, String data2)
	{
		StringBuffer result = new StringBuffer();

		// Pad the data
		data1 = padData(data1);
		data2 = padData(data2);

		// Add comma's to the end of the first set of data
		data1 = addAfterLines(data1, ",");

		/*
		 * Split up each line in the data
		 */
		String[] line1 = StringOperations.splitString(data1, '\n');
		String[] line2 = StringOperations.splitString(data2, '\n');

		/*
		 * Get the size of the data
		 */
		int col1 = getColNum(data1);
		int col2 = getColNum(data2);
		int size = DataAnalysisToolkit.getMax(line1.length, line2.length);

		for (int i = 0; i < size; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}

			// Add line 1 to the result
			if (i < line1.length)
			{
				result.append(line1[i]);
			} else
			{
				result.append(getBlankData(1, col1 - 1));
			}

			// Add line 2 to the result
			if (i < line2.length)
			{
				result.append(line2[i]);
			} else
			{
				result.append(getBlankData(1, col2));
			}

		}
		return result.toString();
	}

	/**
	 * this will create a blank set of data
	 * 
	 * @param rowNum
	 * @param colNum
	 * @return
	 */
	public static String getBlankData(int rowNum, int colNum)
	{
		StringBuffer result = new StringBuffer();

		for (int row = 0; row < rowNum; row++)
		{
			if (row != 0)
			{
				result.append("\n");
			}
			for (int col = 0; col < colNum; col++)
			{
				result.append(",");
			}

		}
		return result.toString();
	}

	/**
	 * This test to see if each line in the csv data ends in a comma
	 * 
	 * @param csvData
	 * @return
	 */
	public static boolean checkEndsComma(String csvData)
	{
		String[] line = StringOperations.splitString(csvData, '\n');
		for (int i = 0; i < line.length; i++)
		{
			String lineData = line[i];
			if (!lineData.endsWith(","))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @param csvData
	 * @return
	 */
	public static boolean checkStartsComma(String csvData)
	{
		String[] line = StringOperations.splitString(csvData, '\n');
		for (int i = 0; i < line.length; i++)
		{
			String lineData = line[i];
			if (!lineData.startsWith(","))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * This will print out all the data in a csv data set
	 * 
	 * @param csvData
	 */
	public static void printCSVData(String csvData)
	{
		String[][] split = getData(csvData);
		for (int i = 0; i < split.length; i++)
		{
			for (int y = 0; y < split[i].length; y++)
			{
				System.out.print("\"");
				System.out.print(split[i][y]);
				System.out.print("\"\t");
			}
			System.out.print("\n");
		}
	}

	public static void fancyPrintCSVData(String csvData)
	{
		String[][] split = getData(csvData);
		int maxSize[] = new int[split[0].length];
		for (int i = 0; i < split.length; i++)
		{
			for (int y = 0; y < split[i].length; y++)
			{
				if (split[i][y].length() > maxSize[y])
				{
					maxSize[y] = split[i][y].length();
				}
			}
		}

		for (int i = 0; i < split.length; i++)
		{
			for (int y = 0; y < split[i].length; y++)
			{
				System.out
						.print(makeSize(split[i][y], maxSize[y], '0') + " | ");
			}
			System.out.println();
		}

	}

	public static String makeSize(String src, int size, char pad)
	{
		StringBuilder rst = new StringBuilder(src);

		if (rst.length() < size)
		{
			for (int i = 0; i <= (size - rst.length() + 1); i++)
			{
				rst.append(pad);
			}
		}
		return rst.toString();
	}

	public static String getCSVData(Object[][] data)
	{
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			for (int j = 0; j < data[i].length; j++)
			{
				if (j != 0)
				{
					result.append(",");
				}
				result.append(data[i][j]);
			}
		}

		return result.toString();
	}

	public static String getCSVData(double[][] data)
	{
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			for (int j = 0; j < data[i].length; j++)
			{
				if (j != 0)
				{
					result.append(",");
				}
				result.append(data[i][j]);
			}
		}

		return result.toString();
	}

	public static String getCSVData(float[][] data)
	{
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			for (int j = 0; j < data[i].length; j++)
			{
				if (j != 0)
				{
					result.append(",");
				}
				result.append(data[i][j]);
			}
		}

		return result.toString();
	}
	public static String transposeData(String input)
	{
		return getCSVData(transposeData(getData(input)));
	}

	public static String[][] transposeData(String input[][])
	{
		String[][] rst = new String[input[0].length][input.length];
		for (int i = 0; i < input.length; i++)
		{
			for (int j = 0; j < input[i].length; j++)
			{
				rst[j][i] = input[i][j];
			}
		}
		return rst;
	}

	public static String getCSVData(int[][] data)
	{
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.length; i++)
		{
			if (i != 0)
			{
				result.append("\n");
			}
			for (int j = 0; j < data[i].length; j++)
			{
				if (j != 0)
				{
					result.append(",");
				}
				result.append(data[i][j]);
			}
		}

		return result.toString();
	}

	/**
	 * This will get the data at a given element of a csv data set
	 * 
	 * @param csvData
	 * @param row
	 * @param col
	 * @return
	 */
	public static String getData(String csvData, int row, int col)
	{
		String[][] data = getData(csvData);
		return data[row][col];
	}

}
