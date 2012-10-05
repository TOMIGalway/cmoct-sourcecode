package com.joey.software.DataToolkit;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JOptionPane;

import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.userinterface.VersionManager;


public class IMGDataSet extends NativeDataSet
{

	private static final long serialVersionUID = VersionManager.VERSION_1;

	File imgFile;

	ThorLabs2DImageProducer loader = null;
	/**
	 * This is a variable to store the tiff header. This is important as the
	 * header from native data set can be reverted...
	 */
	VolumeHeaderIMGData imgHeader;

	public IMGDataSet() throws IOException
	{
		super();
		allowFullResolutin = false;
	}

	public IMGDataSet(File img) throws IOException
	{
		this();
		setIMGFile(img);
	}

	public void setIMGFile(File img) throws IOException
	{
		this.imgFile = img;
		if (!imgFile.exists())
		{
			JOptionPane
					.showMessageDialog(null, "Could not find the Tif Files\n File : "
							+ imgFile.toString(), "Error Loading DataSet", JOptionPane.ERROR_MESSAGE);
			return;
		}
		imgHeader = new VolumeHeaderIMGData(img);
		loader = new ThorLabs2DImageProducer(img);
		header = imgHeader;
	}

	@Override
	public VolumeHeaderData getHeader()
	{
		header = imgHeader;
		return super.getHeader();
	}

	@Override
	public void reloadData(StatusBarPanel status)
	{
		loaded = true;

		try
		{
			setPreviewData(new byte[0][0][0]);
			byte[][][] data = new byte[loader.getSizeZ()][loader.getSizeX()][loader.getSizeY()];
			loader.getData(data, status);
			previewSizeX = data.length;
			previewSizeY = data[0].length;
			previewSizeZ = data[0][0].length;
			setPreviewData(data, 1, 1, 1);
			
			System.out.println(loader.getSizeX()+","+loader.getSizeY()+","+loader.getSizeZ());
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
		out.writeObject(imgFile);
		super.writeExternal(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		imgFile = (File) in.readObject();
		super.readExternal(in);

		setIMGFile(imgFile);
	}

	public File getIMGFile()
	{
		return imgFile;
	}

	public static void main(String input[]) throws IOException
	{
		File f = new File("D:\\Current Analysis\\Project Data\\Correlation\\clearing\\good experiment\\Before clearing_3D_000.IMG");
		StatusBarPanel status = new StatusBarPanel();
		IMGDataSet data = new IMGDataSet(f);
		data.reloadData(status);
	}
}
