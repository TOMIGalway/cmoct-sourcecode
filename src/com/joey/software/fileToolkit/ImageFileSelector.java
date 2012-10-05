package com.joey.software.fileToolkit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;


public class ImageFileSelector
{
	private ImageFileSelector()
	{

	}

	static ImageFileFilter filter = new ImageFileFilter();

	static JFileChooser fc = new JFileChooser();

	/**
	 * This is a simple funciton that will display a JFilechoser that only
	 * allows the user to select image files The user must chose a file. If the
	 * user cancels out the file that's returned in null
	 * 
	 * @return The selected file by the user, else it returns null
	 */
	public static File getUserImageFile(JFrame owner)
	{
		try
		{
			return getUserImageFile(owner, false)[0];
		} catch (NullPointerException e)
		{
			return null;
		}

	}

	public static File getUserImageFile()
	{
		return getUserImageFile(new JFrame());
	}

	public static File[] getUserImageFile(boolean selectMultiple)
	{
		return getUserImageFile(new JFrame(), selectMultiple);
	}

	public static File[] getUserImageFile(JFrame owner, boolean selectMultiple)
	{
		fc.setFileFilter(filter);
		fc.addChoosableFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setMultiSelectionEnabled(selectMultiple);
		// Add the preview pane.
		fc.setAccessory(new ImagePreview(fc));

		// Show it.
		int returnVal = fc.showDialog(owner, "Attach");

		// Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			if (selectMultiple)
			{
				return fc.getSelectedFiles();
			} else
			{
				File[] result =
				{ fc.getSelectedFile() };
				return result;
			}

		} else
		{
			return null;
		}
	}

	public static BufferedImage getUserImage() throws IOException
	{
		File f = FileSelectionField.getUserOpenImageFile();

		return ImageIO.read(f);
	}
	
	public static void main(String input[]) throws IOException
	{
		File f = new File("C:\\Users\\joey.enfield\\Desktop\\Mucosa000.tif");
		BufferedImage img = ImageIO.read(f);
		
		if(img == null)
		{
			System.err.print("Error");
			return;
		}
		FrameFactroy.getFrame(img);
	}

}
