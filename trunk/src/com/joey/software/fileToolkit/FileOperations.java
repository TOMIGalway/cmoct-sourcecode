package com.joey.software.fileToolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.InvalidParameterException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.joey.software.framesToolkit.FileSelectionField;


public class FileOperations
{
	public static void main(String input[]) throws IOException
	{
		FileSelectionField saveFolder = new FileSelectionField();
		saveFolder.setFormat(FileSelectionField.FORMAT_FOLDERS);
		saveFolder.getUserChoice();
		String[] data = splitFile(saveFolder.getFile());
		System.out.println(data[0]);
		System.out.println(data[1]);
		System.out.println(data[2]);
	}

	public static File[] getMultipleFiles(String path)
	{
		JFileChooser choose = new JFileChooser();
		if(path!=null)choose.setCurrentDirectory(new File(path));
		choose.setMultiSelectionEnabled(true);
		
		
		choose.showOpenDialog(null);
		return choose.getSelectedFiles();
	}
	public static String readFile(String file) throws IOException
	{
		StringBuilder rst = new StringBuilder();

		FileReader input = new FileReader(file);
		BufferedReader in = new BufferedReader(input);

		String data = in.readLine();
		while (data != null)
		{
			rst.append(data + "\n");
			data = in.readLine();
		}

		return rst.toString();
	}

	/**
	 * This function will ensure that the current
	 * file directory exists
	 * 
	 * @param f
	 */
	public static void ensureDirStruct(File f)
	{
		File path = new File(f.getParent());
		path.mkdirs();
	}

	/**
	 * This will split a file into 3 different
	 * compoents (if available)<br>
	 * (1) -> Folder<br>
	 * (2) -> FileName<br>
	 * (3) -> Extension<br>
	 * <br>
	 * input -> c:\test\test\filename.ext<br>
	 * output -> <br>
	 * [1] c:\test\test\<br>
	 * [2] filename<br>
	 * [3] ext<br>
	 * 
	 * @param f
	 * @return
	 */
	public static String[] splitFile(File file)
	{
		String path = "";
		String name = "";
		String ext = "";

		if (file.isDirectory())
		{
			path = file.toString();
		} else
		{
			// Parse out path
			int pos = file.toString().lastIndexOf("\\");
			if (pos > 0)
			{
				// Split out fileName
				path = file.toString().substring(0, pos + 1);
				String fileName = file.toString().substring(pos + 1, file
						.toString().length());

				int dPos = fileName.lastIndexOf(".");
				if (dPos > 0)
				{
					name = fileName.substring(0, dPos);
					ext = fileName.substring(dPos + 1, fileName.length());
				} else
				{
					name = fileName;
				}

			} else
			{
				String fileName = file.toString();

				int dPos = fileName.lastIndexOf(".");
				if (dPos > 0)
				{
					name = fileName.substring(0, dPos);
					ext = fileName.substring(dPos + 1, fileName.length());
				} else
				{
					name = fileName;
				}
			}
		}
		return new String[] { path, name, ext };
	}

	/**
	 * This function will rename the current file
	 * type. type -> "frg" not .frg
	 * 
	 * @param file
	 * @param type
	 * @return
	 */
	public static File renameFileType(File file, String type)
	{
		if (file.isDirectory())
		{
			throw new InvalidParameterException(
					"Cannot change Extension of directory");
		}
		try
		{
			int pos = file.toString().lastIndexOf("\\");
			if (pos > 0 && pos < file.toString().length() - 1)
			{
				// Split out fileName
				String path = file.toString().substring(0, pos + 1);
				String name = file.toString().substring(pos + 1, file
						.toString().length());

				int dPos = name.lastIndexOf(".");
				if (dPos > 0)
				{
					System.out.println(path + "\n" + name + "");
					String newFile = name.substring(0, dPos) + "." + type;
					System.out.println(newFile);
					return new File(path + newFile);
				}

			} else
			{
				System.out.println(pos);
			}
		} catch (Exception e)
		{
			System.out.println("Error Renaming file:" + e);
			e.printStackTrace();
		}
		return new File(file.toString() + "." + type);

	}

	/**
	 * This will return the given files
	 * extendsion. It will include the . i.e
	 * test.data -> .data
	 * 
	 * @param f
	 * @return
	 */
	public static String getExtension(File f)
	{

		int index = f.getName().lastIndexOf(".");
		if (index < 0)
		{
			return "";
		}
		return f.getName().substring(index);
	}

	public static void copyFile(File in, File out) throws IOException
	{
		if (!out.exists())
		{
			out.createNewFile();
		}
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try
		{
			// magic number for Windows, 64Mb -
			// 32Kb)
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = 0;
			while (position < size)
			{
				position += inChannel
						.transferTo(position, maxCount, outChannel);
			}
		} catch (IOException e)
		{
			throw e;
		} finally
		{
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/**
	 * This will try to create a sequence of files
	 * from a start file and a end file. <BR>
	 * Files should be in the format
	 * fileName001.jpg<BR>
	 * It expects that the files are in the same
	 * directory.<BR>
	 * Provided that the start and end file
	 * provide valid data it will store the file
	 * list in result. If the function finds a
	 * problem with the sequence it will return
	 * false.
	 * 
	 * @param start
	 * @param end
	 * @param result
	 * @return
	 */
	public static boolean createFileSequence(File start, File end, Vector<File> result)
			throws InvalidFileSequenceException
	{

		if (start.getParent().toLowerCase()
				.equals(end.getParent().toLowerCase()))
		{
			String s1 = start.getName().toLowerCase();
			String s2 = end.getName().toLowerCase();
			boolean firstDigitFound = false;
			int firstDigitIndex = 0;
			int lastDigitIndex = 0;

			if (s1.length() == s2.length())
			{
				for (int i = s1.length() - 1; i >= 0; i--)
				{
					if (Character.isDigit(s1.charAt(i)))
					{
						if (!firstDigitFound)
						{
							firstDigitFound = true;
							firstDigitIndex = i;
						} else
						{
							if (firstDigitFound)
							{
								lastDigitIndex = i;
							}
						}
					}
				}
				if (!firstDigitFound)
				{
					throw new InvalidFileSequenceException(
							"No digits were found to sequence in the filename :"
									+ s1);
				}

				String numString1 = s1
						.substring(lastDigitIndex, firstDigitIndex + 1);
				String numString2 = s2
						.substring(lastDigitIndex, firstDigitIndex + 1);

				int num1 = Integer.parseInt(numString1);
				int num2 = Integer.parseInt(numString2);

				if (num1 > num2)
				{
					int tmp = num1;
					num1 = num2;
					num2 = tmp;
				}

				for (int fileIndex = num1; fileIndex <= num2; fileIndex++)
				{
					String fileIndexString = String.valueOf(fileIndex);
					while (fileIndexString.length() < numString1.length())
					{
						fileIndexString = "0" + fileIndexString;
					}
					String fileName = s1.substring(0, lastDigitIndex)
							+ fileIndexString
							+ s1.substring(firstDigitIndex + 1);
					File resultFile = new File(start.getParent() + "\\"
							+ fileName);
					if (!resultFile.exists())
					{
						throw new InvalidFileSequenceException(
								"Could not find the file:\n" + resultFile);
					}
					result.add(resultFile);
				}

			} else
			{
				throw new InvalidFileSequenceException(
						"The fileNames are not in a valid format.\n" + s1
								+ "\n" + s2);
			}
		} else
		{
			throw new InvalidFileSequenceException(
					"The files are not in the same directory.\n" + start + "\n"
							+ end);
		}
		return true;
	}

	public static boolean confirmOverwrite(File f)
	{
		if(f.exists())
		{
			if(JOptionPane.showConfirmDialog(null, "The File : "+f.toString()+" \n Already exists, Are you sure you want to overwrite", "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
			{
				return false;
			}
		}
		return true;
	}
}
