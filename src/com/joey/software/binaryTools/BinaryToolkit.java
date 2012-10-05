package com.joey.software.binaryTools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.joey.software.framesToolkit.FileSelectionField;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.SwingToolkit;


public class BinaryToolkit
{
	static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
			(byte) 'd', (byte) 'e', (byte) 'f' };

	public static void binaryFileTool(File f) throws IOException
	{
		binaryFileTool(f, "");
	}

	public static void binaryFileTool(File f, String title) throws IOException
	{

		final RandomAccessFile in = new RandomAccessFile(f, "r");
		final JSpinner readPos;
		if (in.length() >= Integer.MAX_VALUE)
		{
			readPos = new JSpinner(new SpinnerNumberModel(0, 0,
					Integer.MAX_VALUE - 10, 10));
		} else
		{
			readPos = new JSpinner(new SpinnerNumberModel(0, 0,
					(int) in.length(), 10));
		}
		final JSpinner byteReadLength = new JSpinner(new SpinnerNumberModel(1,
				1, Integer.MAX_VALUE, 10));
		final JSpinner byteDispLength = new JSpinner(new SpinnerNumberModel(1,
				1, Integer.MAX_VALUE, 1));

		final JSpinner step;

		if (in.length() >= Integer.MAX_VALUE)
		{
			step = new JSpinner(new SpinnerNumberModel(1, 0,
					Integer.MAX_VALUE - 10, 1));
		} else
		{
			step = new JSpinner(new SpinnerNumberModel(1, 0, (int) in.length(),
					1));
		}

		step.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				((SpinnerNumberModel) readPos.getModel())
						.setStepSize((Integer) step.getValue());

			}
		});
		final JButton updateButton = new JButton("Update");
		final JTextArea dataArea = new JTextArea();
		final JTextArea byteArea = new JTextArea();

		Font font = new Font("Monospaced", Font.PLAIN, 13);
		dataArea.setFont(font);
		byteArea.setFont(font);

		final JCheckBox getBoolean = new JCheckBox("Boolean", true);
		final JCheckBox getByte = new JCheckBox("Byte", true);
		final JCheckBox getChar = new JCheckBox("Char", true);
		final JCheckBox getShort = new JCheckBox("Short", true);
		final JCheckBox getInt = new JCheckBox("Int", true);
		final JCheckBox getLong = new JCheckBox("Long", true);
		final JCheckBox getFloat = new JCheckBox("Float", true);
		final JCheckBox getDouble = new JCheckBox("Double", true);
		final JCheckBox getUTF = new JCheckBox("UTF", false);
		final JCheckBox getLine = new JCheckBox("Line", false);

		final ChangeListener change = new ChangeListener()
		{
			byte[] data = new byte[10];

			@Override
			public void stateChanged(ChangeEvent e)
			{
				int pos = (Integer) readPos.getValue();
				int size = (Integer) byteReadLength.getValue();
				int dataWide = (Integer) byteDispLength.getValue();

				String dataResult;

				try
				{
					dataResult = getDataFromFile(in, pos, getByte.isSelected(), getChar
							.isSelected(), getBoolean.isSelected(), getShort
							.isSelected(), getInt.isSelected(), getLong
							.isSelected(), getFloat.isSelected(), getDouble
							.isSelected(), getUTF.isSelected(), getLine
							.isSelected());
				} catch (Exception e1)
				{
					dataResult = "Error : " + e1;
				}
				dataArea.setText(dataResult);

				String binaryResult;
				try
				{
					if (data.length != size)
					{
						data = new byte[size];
					}
					in.seek(pos);
					in.read(data);

					binaryResult = printData(data, dataWide, pos);
				} catch (Exception e2)
				{
					binaryResult = "Error : " + e2;
				}
				byteArea.setText(binaryResult + "\n");
			}

		};

		readPos.addChangeListener(change);
		byteDispLength.addChangeListener(change);
		byteReadLength.addChangeListener(change);
		getBoolean.addChangeListener(change);
		getByte.addChangeListener(change);
		getChar.addChangeListener(change);
		getShort.addChangeListener(change);
		getInt.addChangeListener(change);
		getLong.addChangeListener(change);
		getFloat.addChangeListener(change);
		getDouble.addChangeListener(change);
		getUTF.addChangeListener(change);
		getLine.addChangeListener(change);

		int textSize = 100;
		JPanel controlPanel = new JPanel(new GridLayout(5, 3));
		controlPanel
				.add(SwingToolkit.getLabel(new JTextField("" + in.length()), "Size :", textSize));
		controlPanel.add(getBoolean);
		controlPanel.add(getLong);

		controlPanel
				.add(SwingToolkit.getLabel(readPos, "Position :", textSize));
		controlPanel.add(getByte);
		controlPanel.add(getFloat);

		controlPanel.add(SwingToolkit.getLabel(step, "Step :", textSize));
		controlPanel.add(getChar);
		controlPanel.add(getDouble);

		controlPanel.add(SwingToolkit
				.getLabel(byteReadLength, "Byte2show :", textSize));
		controlPanel.add(getShort);
		controlPanel.add(getUTF);

		controlPanel.add(SwingToolkit
				.getLabel(byteDispLength, "Byte2Disp :", textSize));
		controlPanel.add(getInt);
		controlPanel.add(getLine);

		JScrollPane dataScroll = new JScrollPane(dataArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane byteScroll = new JScrollPane(byteArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(400);
		split.setLeftComponent(dataScroll);
		split.setRightComponent(byteScroll);

		JPanel controlerHolder = new JPanel(new BorderLayout());
		controlerHolder.add(controlPanel);

		JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		temp.add(controlerHolder);

		JPanel finalPanel = new JPanel(new BorderLayout());
		finalPanel.add(temp, BorderLayout.NORTH);
		finalPanel.add(split, BorderLayout.CENTER);

		JFrame frame = FrameFactroy.getFrame(finalPanel);
		frame.setTitle(title);

	}

	public static String getDataFromFile(RandomAccessFile file, long pos)
			throws IOException
	{
		return getDataFromFile(file, pos, true, true, true, true, true, true, true, true, true, true);
	}

	public static String getDataFromFile(RandomAccessFile file, long pos, boolean getByte, boolean getChar, boolean getBoolean, boolean getShort, boolean getInt, boolean getLong, boolean getFloat, boolean getDouble, boolean getUTF, boolean getLine)
	{
		StringBuilder out = new StringBuilder();
		try
		{
			file.seek(pos);
		} catch (Exception e1)
		{
			out.append("\nError Reading : Seek -" + e1);
		}

		if (getBoolean)
		{
			try
			{
				file.seek(pos);
				out.append("\nBool\t: " + file.readBoolean());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Boolean - " + e1);
			}
		}
		if (getByte)
		{
			try
			{
				file.seek(pos);
				out.append("\nByte\t: " + file.readByte());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Byte - " + e1);
			}
		}

		if (getChar)
		{
			try
			{
				file.seek(pos);
				out.append("\nChar\t: " + file.readChar());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Char - " + e1);
			}
		}
		if (getShort)
		{
			try
			{
				file.seek(pos);
				out.append("\nShort\t: " + file.readShort());

			} catch (Exception e1)
			{
				out.append("\nError Reading : Short -" + e1);
			}
		}

		if (getInt)
		{
			try
			{
				file.seek(pos);
				out.append("\nInt\t: " + file.readInt());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Integer - " + e1);
			}

			try
			{
				file.seek(pos);
				byte[] data = new byte[36];
				file.read(data);
				out.append("\nFiplled Int : " + readFlippedInt(data, 0));
			} catch (Exception e1)
			{
				out.append("\nError reading : Flipped Integer " + e1);
			}
		}

		if (getLong)
		{
			try
			{
				file.seek(pos);
				out.append("\nLong\t: " + file.readLong());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Long - " + e1);
			}
		}

		if (getFloat)
		{
			try
			{
				file.seek(pos);
				out.append("\nFloat\t: " + file.readFloat());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Float - " + e1);
			}
		}

		if (getDouble)
		{
			try
			{
				file.seek(pos);
				out.append("\nDouble\t: " + file.readDouble());
			} catch (Exception e1)
			{
				out.append("\nError Reading : Double - " + e1);
			}

			try
			{
				file.seek(pos);

				file.seek(pos);
				byte[] data = new byte[36];
				file.read(data);
				out.append("\nFiplled Double: " + readFlippedDouble(data, 0));
			} catch (Exception e1)
			{
				out.append("\nError Reading : Double - " + e1);
			}
		}

		if (getUTF)
		{
			try
			{
				file.seek(pos);
				String utfData = file.readUTF();

				out.append("\nUTF Str\t: " + utfData);
				out.append("\nUTF Sze\t: " + utfData.length());
				out.append("\n");
			} catch (Exception e1)
			{
				out.append("\nError Reading : UTF String -" + e1);
			}
		}

		if (getLine)
		{
			try
			{
				file.seek(pos);

				String utfData = file.readLine();
				// String utfData =
				// "Disabled Currently - BinaryToolkit";

				out.append("\n\\n Str\t: " + utfData);
				out.append("\n\\n Sze\t: " + utfData.length());
				out.append("\n");
			} catch (Exception e1)
			{
				out.append("\nError Reading : \\n String -" + e1);
			}
		}
		return out.toString();
	}
	
	public static String printData(byte[] data, int col, int pos)
			throws UnsupportedEncodingException
	{

		StringBuilder out = new StringBuilder();

		StringBuilder charData = new StringBuilder();
		StringBuilder hexData = new StringBuilder();
		StringBuilder binaryData = new StringBuilder();

		charData.append(String.format("[%d]\t", 0));
		for (int i = 0; i < data.length; i++)
		{

			charData.append(String.format("%4d", data[i]));
			charData.append(" ");

			hexData.append(getHexString(data[i]));
			hexData.append(" ");

			binaryData.append(getBinaryString(data[i]));
			binaryData.append(" ");

			if (((i + 1) % col == 0) || (i == data.length - 1))
			{

				// fill out end of data set if not
				// full
				if ((i == data.length - 1) && ((i + 1) % col != 0))
				{

					int cnt = i + 1;
					do
					{
						charData.append(String.format("%4d", 0));
						charData.append(" ");

						hexData.append(getHexString((byte) 0));
						hexData.append(" ");

						binaryData.append(getBinaryString((byte) 0));
						binaryData.append(" ");

					} while (!(++cnt % col == 0));
				}
				out.append(charData.toString());
				out.append(" | ");
				out.append(hexData.toString());
				out.append(" | ");
				out.append(binaryData.toString());
				out.append("\n");

				charData.setLength(0);
				charData.append(String.format("[%d]\t", i + 2+pos));
				hexData.setLength(0);
				binaryData.setLength(0);
			}
		}

		return out.toString();
	}

	public static void main(String input[])
			throws UnsupportedEncodingException, Exception
	{
		binaryFileTool(FileSelectionField.getUserFile());
	}

	public static String getBinaryString(byte b)
	{
		return getBinaryString(b, Byte.SIZE);
	}

	public static String getBinaryString(long b)
	{
		return getBinaryString(b, Long.SIZE);
	}

    public static String getBinaryString(int b)
    {
        return getBinaryString(b, Integer.SIZE);
    }
	public static String getBinaryString(long b, int bits)
	{
		StringBuilder result = new StringBuilder();

		{
			for (int i = bits - 1; i >= 0; i--)
			{
				if (((b) & (1 << i)) != 0)
				{
					result.append("1");
				} else
				{
					result.append("0");
				}
			}
		}

		return result.toString();
	}

	/**
	 * This will get a string of the hexadecimal
	 * string of data
	 * 
	 * @param raw
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getHexString(byte b)
			throws UnsupportedEncodingException
	{
		byte[] hex = new byte[2];
		int v = b & 0xFF;
		hex[0] = HEX_CHAR_TABLE[v >>> 4];
		hex[1] = HEX_CHAR_TABLE[v & 0xF];
		return new String(hex, "ASCII");
	}

	public static byte getHex2Byte(char val)
	{
		for (int i = 0; i < HEX_CHAR_TABLE.length; i++)
		{
			if (HEX_CHAR_TABLE[i] == val)
			{
				return (byte) i;
			}
		}
		throw new InvalidParameterException("Not a hex value");
	}

	public static void write(byte[] bytes, boolean b, int pos)
	{
		bytes[pos] = (byte) (b ? 1 : 0);
	}

	public static void write(byte[] bytes, short s, int pos)
	{
		bytes[pos] = (byte) ((s >>> 8) & 0xFF);
		bytes[pos + 1] = (byte) ((s >>> 0) & 0xFF);
	}

	public static void write(byte[] bytes, int i, int pos)
	{
		bytes[pos] = (byte) ((i >>> 24) & 0xFF);
		bytes[pos + 1] = (byte) ((i >>> 16) & 0xFF);
		bytes[pos + 2] = (byte) ((i >>> 8) & 0xFF);
		bytes[pos + 3] = (byte) ((i >>> 0) & 0xFF);
	}

	public static void write(byte[] bytes, long l, int pos)
	{
		bytes[pos] = (byte) ((l >>> 56) & 0xFF);
		bytes[pos + 1] = (byte) ((l >>> 48) & 0xFF);
		bytes[pos + 2] = (byte) ((l >>> 40) & 0xFF);
		bytes[pos + 3] = (byte) ((l >>> 32) & 0xFF);
		bytes[pos + 4] = (byte) ((l >>> 24) & 0xFF);
		bytes[pos + 5] = (byte) ((l >>> 16) & 0xFF);
		bytes[pos + 6] = (byte) ((l >>> 8) & 0xFF);
		bytes[pos + 7] = (byte) ((l >>> 0) & 0xFF);
	}

	public static void write(byte[] bytes, float f, int pos)
	{
		write(bytes, Float.floatToIntBits(f), pos);
	}

	public static void write(byte[] bytes, double d, int pos)
	{
		write(bytes, Double.doubleToLongBits(d), pos);
	}

	public static boolean readBoolean(byte[] bytes, int pos)
	{
		return bytes[pos] == 1;
	}

	public static short readShort(byte[] bytes, int pos)
	{
		int b1 = bytes[pos];
		int b2 = bytes[pos + 1];

		if (b1 < 0)
		{
			b1 = 128 - b1;
		}
		if (b2 < 0)
		{
			b2 = 128 - b2;
		}

		return (short) (((b1 << 8)) | ((b2 & 0xff)));
	}

	public static short readShortN(byte[] bytes, int pos)
	{
		int b1 = bytes[pos];
		int b2 = bytes[pos + 1];
		return (short) (((b1 << 8)) | ((b2 & 0xff)));
	}

	public static short readFlippedShort(byte[] bytes, int pos)
	{
		return (short) (((bytes[pos + 1] << 8)) | ((bytes[pos] & 0xff)));
	}

	public static int readInt(byte[] bytes, int pos)
	{
		return ((0xff & bytes[pos + 3]) | (0xff & bytes[pos + 2]) << 8
				| (0xff & bytes[pos + 1]) << 16 | (bytes[pos]) << 24);
	}

	public static int readFlippedInt(byte[] bytes, int pos)
	{
		return ((0xff & bytes[pos]) | (0xff & bytes[pos + 1]) << 8
				| (0xff & bytes[pos + 2]) << 16 | (bytes[pos + 3]) << 24);
	}

	public static long readLong(byte[] bytes, int pos)
	{
		return ((0xff & bytes[pos + 7]) | (0xff & bytes[pos + 6]) << 8
				| (0xff & bytes[pos + 5]) << 16
				| (long) (0xff & bytes[pos + 4]) << 24
				| (long) (0xff & bytes[pos + 3]) << 32
				| (long) (0xff & bytes[pos + 2]) << 40
				| (long) (0xff & bytes[pos + 1]) << 48 | (long) (0xff & bytes[pos]) << 56);
	}

	public static float readFloat(byte[] bytes, int pos)
	{
		return Float.intBitsToFloat(readInt(bytes, pos));
	}

	public static float readFlippedFloat(byte[] bytes, int pos)
	{
		return Float.intBitsToFloat(readFlippedInt(bytes, pos));
	}
	
	public static double readFlippedDouble(byte[] bytes, int pos)
	{
		return Double.longBitsToDouble(readLong(new byte[] { bytes[pos + 7],
				bytes[pos + 6], bytes[pos + 5], bytes[pos + 4], bytes[pos + 3],
				bytes[pos + 2], bytes[pos + 1], bytes[pos] }, 0));
	}

	public static double readDouble(byte[] bytes, int pos)
	{
		return Double.longBitsToDouble(readLong(bytes, pos));
	}
}