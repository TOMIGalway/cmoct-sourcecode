package com.joey.software.DataToolkit;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JOptionPane;

import com.joey.software.framesToolkit.FrameWaitForClose;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.userinterface.OCTDataCreatorPanel;
import com.joey.software.userinterface.VersionManager;


public class TiffDataSet extends NativeDataSet
{

	private static final long serialVersionUID = VersionManager.VERSION_1;

	File tiffFile;

	TiffVolumeProducer loader = new TiffVolumeProducer();

	/**
	 * This is a variable to store the tiff header. This is important as the
	 * header from native data set can be reverted...
	 */
	VolumeHeaderTiffData tiffHeader;

	public TiffDataSet() throws IOException
	{
		super();
		allowFullResolutin = false;
	}

	public TiffDataSet(File tiff) throws IOException
	{
		this();
		setTiffFile(tiff);
	}

	public void setTiffFile(File tiff) throws IOException
	{
		this.tiffFile = tiff;
		if (!tiffFile.exists())
		{
			JOptionPane
					.showMessageDialog(null, "Could not find the Tif Files\n File : "
							+ tiffFile.toString(), "Error Loading DataSet", JOptionPane.ERROR_MESSAGE);
			return;
		}
		tiffHeader = new VolumeHeaderTiffData(tiff);
		header = tiffHeader;
	}

	@Override
	public VolumeHeaderData getHeader()
	{
		header = tiffHeader;
		return super.getHeader();
	}

	@Override
	public void reloadData(StatusBarPanel status)
	{
		loaded = true;

		try
		{
			byte[][][] data = loader.getData(tiffFile, status);
			previewSizeX = data.length;
			previewSizeY = data[0].length;
			previewSizeZ = data[0][0].length;
			setPreviewData(data, 1, 1, 1);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAllowFullResolution()
	{
		allowFullResolutin = false;
		return allowFullResolutin;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		System.out.println("Saving Tiff");
		out.writeObject(tiffFile);
		super.writeExternal(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		tiffFile = (File) in.readObject();
		super.readExternal(in);

		setTiffFile(tiffFile);
	}

	public File getTiffFile()
	{
		return tiffFile;
	}

	public static void main(String input[]) throws IOException
	{
		TiffDataSet data = new TiffDataSet(new File(
				"C:\\Users\\joey.enfield\\Desktop\\Shave [Old Blade].tif"));

		OCTDataCreatorPanel pan = new OCTDataCreatorPanel();
		pan.setInputData(data);

		FrameWaitForClose wait = new FrameWaitForClose(pan);
		pan.setVisible(true);
		wait.waitForClose();

		if (pan.getLastWorked())
		{
			JOptionPane.showMessageDialog(null, "It Worked");
		} else
		{
			JOptionPane.showMessageDialog(null, "It Failed");
		}

		pan.setVisible(true);
	}
}
