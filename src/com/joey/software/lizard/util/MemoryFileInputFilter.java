package com.joey.software.lizard.util;

/**
 * <b>MemoryFileInputFilter</b> allows you to read from a byte array as if it
 * was a byte array. It has built in support for intel vs. motorola byte order
 * (hi to lo / lo to hi). Copyright 1997 By BancTec. GNU Public license Applys.
 * 
 * @author Liz Marks
 * @version %I%, %G%
 * @see java.io.RandomAccessFile
 */
public class MemoryFileInputFilter
{
	private byte bytesArray[];

	private boolean isIntel;

	private int currentPos;

	public static final int BYTE_SIZE = 1;

	public static final int SHORT_SIZE = 2;

	public static final int INT_SIZE = 4;

	public static final int LONG_SIZE = 8;

	/**
	 * Creates a new byte array input filter that reads data from the specified
	 * byte array in the specified byte order.
	 * 
	 * @param bytesArray
	 *            the byte array to read from.
	 * @param isIntel
	 *            the byte order to use.
	 */
	public MemoryFileInputFilter(byte[] bytesArray, boolean isIntel)
	{
		this.bytesArray = bytesArray;
		this.isIntel = isIntel;
		this.currentPos = 0;
	}

	/**
	 * Creates a new byte array input filter that reads data from the specified
	 * byte array. <i>the default byte order is motorola (ie lo to hi)</i>
	 * 
	 * @param bytesArray
	 *            the byte array to read from.
	 */
	public MemoryFileInputFilter(byte[] bytesArray)
	{
		this.bytesArray = bytesArray;
		this.isIntel = false;
		this.currentPos = 0;
	}

	/**
	 * Returns the current index in the byte array.
	 * 
	 * @return the current index.
	 */
	public long getFilePointer()
	{
		return currentPos;
	}

	/**
	 * Returns the current byte order.
	 * 
	 * @return true if intel (hi to lo), false if motorola (lo to hi).
	 */
	public boolean isIntel()
	{
		return isIntel;
	}

	/**
	 * Set the current byte order.
	 * 
	 * @param isIntel
	 *            true if intel (hi to lo), false if motorola (lo to hi).
	 */
	public void setByteOrder(boolean isIntel)
	{
		this.isIntel = isIntel;
	}

	/**
	 * Reads a boolean from this byte array. This method reads a single byte
	 * from the byte array. A value of 0 represents false. Any other value
	 * represents true.
	 * 
	 * @return the boolean value read.
	 */
	public boolean readBoolean()
	{
		return (readByte() != 0);
	}

	/**
	 * Reads a Unicode character from this byte array. The method reads 2 bytes
	 * from this byte array in the proper byte order. If the two bytes read, in
	 * the proper byte order, are <code>b1</code> and <code>b2</code>, where
	 * each of the two values is between <code>0</code> and <code>255</code>,
	 * inclusive, then the result is equal to: <blockquote>
	 * 
	 * <pre>
	 * (char) ((b1 &lt;&lt; 8) | b2)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the next two bytes of this byte array, interpreted as a Unicode
	 *         character.
	 */
	public char readChar()
	{
		char c = 0;
		short l = readUnsignedByte();
		short h = readUnsignedByte();

		if (isIntel)
			c = (char) (((h & 0xff) << 8) + (l & 0xff));
		else
			c = (char) (((l & 0xff) << 8) + (h & 0xff));

		return c;
	}

	/**
	 * Reads an unsigned 8-bit number from this byte array. This method reads a
	 * byte from this byte array and returns that byte.
	 * 
	 * @return the next byte of this byte array, interpreted as an unsigned
	 *         8-bit number.
	 */
	public short readUnsignedByte()
	{
		short b = (short) (bytesArray[currentPos] & 0xff);
		currentPos += BYTE_SIZE;
		return b;
	}

	/**
	 * Reads a signed 8-bit value from this byte array. This method reads a byte
	 * from the byte array. If the byte read is <code>b</code>, where
	 * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>, then the result is:
	 * <blockquote>
	 * 
	 * <pre>
	 * (byte) (b)
	 * </pre>
	 * 
	 * </blockquote>
	 */
	public byte readByte()
	{
		byte b = bytesArray[currentPos];
		currentPos += BYTE_SIZE;
		return b;
	}

	/**
	 * Reads a signed 16-bit number from this byte array. The method reads 2
	 * bytes from this byte array in the proper byte order. If the two bytes
	 * read, in the proper byte order, are <code>b1</code> and <code>b2</code>,
	 * where each of the two values is between <code>0</code> and
	 * <code>255</code>, inclusive, then the result is equal to: <blockquote>
	 * 
	 * <pre>
	 * (short) ((b1 &lt;&lt; 8) | b2)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the next two bytes of this byte array, interpreted as a signed
	 *         16-bit number.
	 */
	public short readShort()
	{
		short s = 0;
		short l = readUnsignedByte();
		short h = readUnsignedByte();

		if (isIntel)
			s = (short) (((h & 0xff) << 8) + (l & 0xff));
		else
			s = (short) (((l & 0xff) << 8) + (h & 0xff));

		return s;
	}

	/**
	 * Reads an unsigned 16-bit number from this byte array. The method reads 2
	 * bytes from this byte array in the proper byte order. If the two bytes
	 * read, in the proper byte order, are <code>b1</code> and <code>b2</code>,
	 * where each of the two values is between <code>0</code> and
	 * <code>255</code>, inclusive, then the result is equal to: <blockquote>
	 * 
	 * <pre>
	 * (b1 &lt;&lt; 8) | b2
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the next two bytes of this byte array, interpreted as an unsigned
	 *         16-bit number.
	 */
	public int readUnsignedShort()
	{
		int s = 0;
		short l = readUnsignedByte();
		short h = readUnsignedByte();

		if (isIntel)
			s = (((h & 0xff) << 8) + (l & 0xff));
		else
			s = (((l & 0xff) << 8) + (h & 0xff));

		return s;
	}

	/**
	 * Reads a signed 32-bit integer from this byte array. This method reads 4
	 * bytes from the byte array. If the bytes read, in the proper byte order,
	 * are <code>b1</code>, <code>b2</code>, <code>b3</code>, and
	 * <code>b4</code>, where
	 * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>, then
	 * the result is equal to: <blockquote>
	 * 
	 * <pre>
	 * (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the next four bytes of this byte array, interpreted as an int.
	 */
	public long readInt()
	{
		long i = 0;
		short b1 = readUnsignedByte();
		short b2 = readUnsignedByte();
		short b3 = readUnsignedByte();
		short b4 = readUnsignedByte();

		if (isIntel)
			i = (((b4 & 0xff) << 24) + ((b3 & 0xff) << 16) + ((b2 & 0xff) << 8) + (b1 & 0xff));
		else
			i = (((b1 & 0xff) << 24) + ((b2 & 0xff) << 16) + ((b3 & 0xff) << 8) + (b4 & 0xff));
		return i;
	}

	/**
	 * Reads a signed 64-bit integer from this byte array. This method reads 8
	 * bytes from the byte array. If the bytes read, in the proper byte order,
	 * are <code>b1</code>, <code>b2</code>, <code>b3</code>, <code>b4</code>,
	 * <code>b5</code>, <code>b6</code>, <code>b7</code>, and <code>b8,</code>
	 * where: <blockquote>
	 * 
	 * <pre>
	 *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * then the result is equal to:
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * ((long) b1 &lt;&lt; 56) + ((long) b2 &lt;&lt; 48) + ((long) b3 &lt;&lt; 40) + ((long) b4 &lt;&lt; 32)
	 * 		+ ((long) b5 &lt;&lt; 24) + ((long) b6 &lt;&lt; 16) + ((long) b7 &lt;&lt; 8) + b8
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the next eight bytes of this byte array, interpreted as a long.
	 */
	public long readLong()
	{
		long i = 0;
		short b1 = readUnsignedByte();
		short b2 = readUnsignedByte();
		short b3 = readUnsignedByte();
		short b4 = readUnsignedByte();
		short b5 = readUnsignedByte();
		short b6 = readUnsignedByte();
		short b7 = readUnsignedByte();
		short b8 = readUnsignedByte();

		if (isIntel)
			i = ((long) b8 << 56) + ((long) b7 << 48) + ((long) b6 << 40)
					+ ((long) b5 << 32) + ((long) b4 << 24) + ((long) b3 << 16)
					+ ((long) b2 << 8) + b1;
		else
			i = ((long) b1 << 56) + ((long) b2 << 48) + ((long) b3 << 40)
					+ ((long) b4 << 32) + ((long) b5 << 24) + ((long) b6 << 16)
					+ ((long) b7 << 8) + b8;

		return i;
	}

	/**
	 * Reads a <code>float</code> from this byte array. This method reads an
	 * <code>int</code> value as if by the <code>readInt</code> method and then
	 * converts that <code>int</code> to a <code>float</code> using the
	 * <code>intBitsToFloat</code> method in class <code>Float</code>.
	 * 
	 * @return the next four bytes of this byte array, interpreted as a float.
	 */
	public float readFloat()
	{
		return Float.intBitsToFloat((int) readInt());
	}

	/**
	 * Reads a <code>double</code> from this byte array. This method reads an
	 * <code>long</code> value as if by the <code>readLong</code> method and
	 * then converts that <code>long</code> to a <code>double</code> using the
	 * <code>longBitsToDouble</code> method in class <code>Double</code>.
	 * 
	 * @return the next eight bytes of this byte array, interpreted as a double.
	 */
	public double readDouble()
	{
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * Reads a byte from the byte array.
	 * 
	 * @return the next byte of data, or -1 if the end of the file is reached.
	 */
	public int read()
	{
		return (currentPos < bytesArray.length ? readByte() : -1);
	}

	/**
	 * Reads up to b.length bytes of data from this byte array into an array of
	 * bytes.
	 * 
	 * @param b
	 *            the buffer into which the data is copied
	 * @return the total number of bytes copied into the buffer, or -1 if there
	 *         is no more data because the end of this byte array has been
	 *         reached.
	 */
	public int read(byte b[])
	{
		return read(b, 0, b.length);
	}

	/**
	 * Reads up to len bytes of data from this byte array (after incrementing
	 * the current index by off) into an array of bytes.
	 * 
	 * @param b
	 *            the buffer into which the data is copied
	 * @param off
	 *            the start offset of the data.
	 * @param len
	 *            the maximum number of bytes copied.
	 * @return the total number of bytes copied into the buffer, or -1 if there
	 *         is no more data because the end of this byte array has been
	 *         reached.
	 */
	public int read(byte b[], int off, int len)
	{
		currentPos = +off;
		if (currentPos + len < bytesArray.length)
		{
			System.arraycopy(bytesArray, currentPos, b, 0, len);
			return len;
		} else if (currentPos < bytesArray.length)
		{
			len = bytesArray.length - currentPos;
			System.arraycopy(bytesArray, currentPos, b, 0, len);
			return len;
		} else
			return -1;
	}

	/**
	 * Reads up to b.length bytes of data from this byte array into an array of
	 * bytes.
	 * 
	 * @param b
	 *            the buffer into which the data is copied
	 * @return the total number of bytes copied into the buffer, or -1 if there
	 *         is no more data because the end of this byte array has been
	 *         reached.
	 */
	public void readFully(byte[] b)
	{
		readFully(b, currentPos, b.length);
	}

	/**
	 * Reads up to len bytes of data from this byte array (after incrementing
	 * the current index by off) into an array of bytes.
	 * 
	 * @param b
	 *            the buffer into which the data is copied
	 * @param off
	 *            the start offset of the data.
	 * @param len
	 *            the maximum number of bytes copied.
	 * @return the total number of bytes copied into the buffer, or -1 if there
	 *         is no more data because the end of this byte array has been
	 *         reached.
	 */
	public void readFully(byte[] b, int off, int len)
	{
		System.arraycopy(bytesArray, off, b, 0, len);
		currentPos += len;
	}

	/**
	 * Reads up to len bytes of data from this byte array into an array of
	 * bytes. If our byte array file is in motorola format or the typeSize < 2
	 * then it just does a standard array copy for len bytes.
	 * 
	 * @param b
	 *            the buffer into which the data is copied
	 * @param typeSize
	 *            the size of data type (ie SHORT_SIZE, ...).
	 * @param len
	 *            the maximum number of bytes copied.
	 * @return the total number of bytes copied into the buffer, or -1 if there
	 *         is no more data because the end of this byte array has been
	 *         reached.
	 */
	public void readProperly(byte[] bytes, int typeSize, int len)
	{
		if (!isIntel || typeSize < 2)
			readFully(bytes, currentPos + 0, len);
		else
		{
			int n = 0;
			while (n < len)
			{
				switch (typeSize)
				{
					case SHORT_SIZE:
					{

						bytes[n + 1] = bytesArray[currentPos];
						currentPos += BYTE_SIZE;
						bytes[n] = bytesArray[currentPos];
						currentPos += BYTE_SIZE;
						break;
					}
					case INT_SIZE:
					{
						bytes[n + 3] = readByte();
						bytes[n + 2] = readByte();
						bytes[n + 1] = readByte();
						bytes[n] = readByte();
						break;
					}
					case LONG_SIZE:
					{
						bytes[n + 7] = readByte();
						bytes[n + 6] = readByte();
						bytes[n + 5] = readByte();
						bytes[n + 4] = readByte();
						bytes[n + 3] = readByte();
						bytes[n + 2] = readByte();
						bytes[n + 1] = readByte();
						bytes[n] = readByte();
						break;
					}
				}// switch
				n += typeSize;
			}// while
		}// else
	}// readProperly

	/**
	 * Set the current position in the byte array.
	 * 
	 * @param isIntel
	 *            true if intel (hi to lo), false if motorola (lo to hi).
	 */
	public void seek(long pos)
	{
		currentPos = (int) pos;
	}

	/**
	 * Returns the length in the byte array.
	 * 
	 * @return the length of the byte array.
	 */
	public long length()
	{
		return bytesArray.length;
	}

	/**
	 * Resets the byte array to null (ie. closes it).
	 */
	public void close()
	{
		bytesArray = null;
	}
}