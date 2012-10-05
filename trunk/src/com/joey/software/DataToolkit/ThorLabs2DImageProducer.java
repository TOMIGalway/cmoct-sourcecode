package com.joey.software.DataToolkit;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.joey.software.binaryTools.BinaryToolkit;
import com.joey.software.drawingToolkit.GraphicsToolkit;
import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.stringToolkit.StringOperations;


@Deprecated
public class ThorLabs2DImageProducer extends ImageProducer
{

	public void test()
	{
		class ImageSaverTool
		{

			public void saveImage(final BufferedImage img, final String output)

			{
				// final BufferedImage imgRot = ImageOperations
				// .getRotatedImage(img, 1);
				try
				{
					ImageIO.write(img, "PNG", new File(output));
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		ImageSaverTool tool = new ImageSaverTool();
		File f = FileSelectionField.getUserFile();
		if (f != null)
		{
			try
			{
				String base = f.getParent() + "\\";
				System.out.println(base);
				String end = ".png";
				int output = 0;
				ImagePanel pOrg = new ImagePanel();
				ImagePanel pAvg = new ImagePanel();

				JPanel p = new JPanel(new GridLayout(2, 1));
				p.add(pOrg);
				p.add(pAvg);
				FrameFactroy.getFrame(p);
				ThorLabs2DImageProducer producer = new ThorLabs2DImageProducer(
						f);

				JSpinner count = new JSpinner(new SpinnerNumberModel(1, 1,
						producer.getSizeZ(), 1));
				JOptionPane
						.showMessageDialog(null, count, "How many to average", JOptionPane.PLAIN_MESSAGE);

				int avgNum = (Integer) count.getValue();

				Vector<BufferedImage> data = new Vector<BufferedImage>();

				int[][] vals = new int[producer.getSizeX()][producer.getSizeY()];
				for (int i = 0; i < producer.getSizeZ(); i++)
				{
					BufferedImage img = producer.getImage(i);
					img = ImageOperations.getRotatedImage(img, 1);
					pOrg.setImage(img);

					if (avgNum == 1)
					{
						tool.saveImage(img, base
								+ StringOperations.getNumberString(5, i) + end);
					} else
					{
						data.add(img);

						if (data.size() >= avgNum
								|| i == producer.getSizeZ() - 1)
						{
							BufferedImage avg = ImageOperations
									.getAverageData(data, vals);
							pAvg.setImage(avg);
							data.clear();
							tool.saveImage(avg, base
									+ StringOperations.getNumberString(5, i)
									+ end);
						}

					}

				}
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "There was a problem");
				e.printStackTrace();
			}
		}
	}

	String fileId;

	int totalFrames;

	int sizeZ;

	int sizeX;

	int sizeY;

	int num3D;

	int frmLenBytes;

	int frmPixels;

	byte[] pxlHolder = new byte[0];

	File f;

	double scale = 1;

	public ThorLabs2DImageProducer(File fileSource) throws IOException
	{
		setFile(fileSource);
	}

	public void setFile(File f) throws IOException
	{
		this.f = f;
		byte[] data = new byte[36];
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		in.read(data, 0, 16);
		fileId = (byteToString(data, 16));

		in.read(data, 0, 4);
		sizeZ = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeX = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		sizeY = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		totalFrames = BinaryToolkit.readFlippedInt(data, 0);

		in.read(data, 0, 4);
		num3D = BinaryToolkit.readFlippedInt(data, 0);

		in.skip(4 * 118);

		frmLenBytes = 40 + sizeX * sizeY; // 40 bytes is the length of sub
		// hearder of the image frame
		frmPixels = sizeX * sizeY;
	}

	@Override
	public BufferedImage getImage(int pos) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		in.skip(getFileOffset(pos));

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}
		byte[] data = new byte[4];
		in.read(data);
		int val = com.joey.software.binaryTools.BinaryToolkit.readFlippedInt(data, 0);

		in.skip(36);
		// Frame Info
		in.read(pxlHolder);
		return (getImage(pxlHolder, sizeY, sizeX));
	}

	public void getImageData(int frameNum, byte data[], int pos)
			throws IOException
	{

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		in.skip(getFileOffset(frameNum));

		in.skip(40);
		// Frame Info
		in.read(data, pos, frmPixels);
	}

	/**
	 * The data is stored in the form
	 * data[x* wide*high + high * x + y]
	 * @param frameNum
	 * @param data
	 * @param pos
	 * @throws IOException
	 */
	public void getImageData(int frameNum, int data[], int pos)
			throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		in.skip(getFileOffset(frameNum));

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		in.skip(40);
		// Frame Info
		in.read(pxlHolder);

		for (int i = 0; i < frmPixels; i++)
		{
			if (pxlHolder[i] < 0)
			{
				data[i + pos] = 256 + pxlHolder[i];
			} else
			{
				data[i + pos] = pxlHolder[i];
			}
		}
	}

	/**
	 * 
	 * @param pos
	 * @param data
	 *            data[X-Data][Y-data]
	 * @throws IOException
	 */
	@Override
	public void getImage(int pos, byte[][] data) throws IOException
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));

		in.skip(getFileOffset(pos));

		if (pxlHolder.length != sizeX * sizeY)
		{
			pxlHolder = new byte[frmPixels];
		}

		in.skip(36);
		// Frame Info
		in.read(pxlHolder);
		getImage(pxlHolder, data);
	}

	private int getFileOffset(int pos)
	{
		int base = 512;
		return base+(pos) * frmLenBytes;
	}

	public static void getImage(byte[] data, byte[][] out)
	{
		for (int x = 0; x < out.length; x++)
		{
			for (int y = 0; y < out[0].length; y++)
			{
				out[x][y] = data[x + y * out.length];
			}
		}
	}

	public static BufferedImage getImage(byte[] data, int wide, int high)
	{
		BufferedImage img = ImageOperations.getBi(high, wide);
		for (int x = 0; x < wide; x++)
		{
			for (int y = 0; y < high; y++)
			{
				img
						.setRGB(y, x, NativeDataSet.getByteToRGB(data[x + y
								* wide]));
			}
		}
		return img;
	}

	@Override
	public int getImageCount()
	{
		// TODO Auto-generated method stub
		return sizeZ;
	}

	public String byteToString(byte[] data, int count)
	{
		StringBuilder rst = new StringBuilder();
		for (int i = 0; i < count; i++)
		{
			rst.append((char) data[i]);
		}
		return rst.toString();
	}

	public int getSizeZ()
	{
		return sizeZ;
	}

	public void setSizeZ(int sizeZ)
	{
		this.sizeZ = sizeZ;
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(int sizeX)
	{
		this.sizeX = sizeX;
	}

	public int getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(int sizeY)
	{
		this.sizeY = sizeY;
	}

	public static void dmain(String input[]) throws IOException,
			InterruptedException
	{
//		File f = new File(
//				"D:\\Current Analysis\\Project Data\\Correlation\\clearing\\clearedskin - 4x4_3D_000.IMG");
		
		File f= FileSelectionField.getUserFile();
		ThorLabs2DImageProducer dat = new ThorLabs2DImageProducer(f);

		byte[][][] frame = new byte[dat.sizeZ][dat.sizeX][dat.sizeY];

		dat.getData(frame, new StatusBarPanel());

		ImagePanel p = new ImagePanel();

		FrameFactroy.getFrame(p);

		byte[] data = new byte[dat.sizeX * dat.sizeY];

		BufferedImage img = ImageOperations
				.pixelsToImage(data, dat.sizeX, dat.sizeY);

		p.setImage(img);

		for (int i = 0; i < frame.length; i++)
		{
			System.out.println("" + i);
			for (int x = 0; x < dat.sizeX; x++)
			{
				for (int y = 0; y < dat.sizeY; y++)
				{
					data[y*dat.sizeX+x]=frame[i][x][y];
				}
			}
			p.setImage(img);
			p.repaint();
			Thread.sleep(10);

		}

	}

	public static void main(String input[])
	{
		int inWide= 1024;
		int inHigh = 640;
		BufferedImage inScreen = ImageOperations.getBi(inWide, inHigh);
		
		Graphics2D g1 = inScreen.createGraphics();
		g1.setColor(Color.WHITE);
		g1.drawOval(10, 10, inWide-20,inHigh-20);
		
		
		int outWide= 1024;
		int outHigh = 720;
		BufferedImage outScreen = ImageOperations.getBi(outWide, outHigh);
		Graphics2D g2 = outScreen.createGraphics();
		GraphicsToolkit.setRenderingQuality(g2, GraphicsToolkit.HIGH_QUALITY);
		g2.drawImage(inScreen, 0,0,outWide, outHigh, null);
		
//		float posX = 0;
//		float posY = 0;
//		
//		int inX = 0;
//		int inY = 0;
//		for(int x = 0; x < outWide; x++)
//		{
//			posX = (x)/(outWide-1f);
//			inX = (int)Math.round((inWide-1)*posX);
//			for(int y= 0; y < outHigh;y++)
//			{
//				posY = (y)/(outHigh-1f);
//				inY = (int)Math.round((inHigh-1)*posY);
//				
//				outScreen.setRGB(x, y, inScreen.getRGB(inX, inY));
//			}
//		}
		
		FrameFactroy.getFrame(inScreen, outScreen);
	}
	public void getData(byte[][][] data, StatusBarPanel status)
			throws IOException
	{
		RandomAccessFile in = new RandomAccessFile(f, "r");

		if (pxlHolder.length != data[0].length * data[0][0].length)
		{
			pxlHolder = new byte[data[0].length * data[0][0].length];
		}

		
		// Frame Info

		status.setMaximum(data.length);
		for (int x = 0; x < data.length; x++)
		{
			in.seek(getFileOffset(x+1)+36);
			status.setValue(x);

			// Frame Info
			in.read(pxlHolder);
			for (int y = 0; y < data[0].length; y++)
			{
				System
						.arraycopy(pxlHolder, y * data[0][0].length, data[x][y], 0, data[0][0].length);
			}
		}
	}
}
