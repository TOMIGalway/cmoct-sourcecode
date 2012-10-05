package com.joey.software.lizard.tiff;

import java.awt.Image;
import java.io.IOException;

import com.joey.software.lizard.tiff.tag.CompressionType;
import com.joey.software.lizard.util.MemoryFileInputFilter;
import com.joey.software.lizard.util.MotorolaIntelInputFilter;


class IFD
{

	public int count; // count of entries

	public IFDEntry entries[]; // entries

	public long offset; // offset to next IFD, must be zeros if last one

	CodedImage cImg;

	IFD unisys;

	public IFD()
	{
		init();
	}

	void init()
	{
		count = 0;
		entries = null;
		offset = 0;
		cImg = null;
		unisys = null;
	}

	IFDEntry GetEntry(int id)
	{
		for (int i = 0; i < count; i++)
		{
			if (entries[i].tag.equals(id))
				return entries[i];
		}
		return null;
	}

	int GetFieldValue(int id)
	{
		for (int i = 0; i < count; i++)
		{
			if (entries[i].tag.equals(id))
				return (int) entries[i].value;
		}
		return 0;
	}

	int GetCompressionType()
	{
		return GetFieldValue(Tag.COMPRESSION);
	}

	int size()
	{
		return (2 + IFDEntry.SIZE * count + 4); // count + tags + offset
	}

	@Override
	public String toString()
	{
		String s;
		int i;
		s = "IFD Entry Count: " + count + "\n";
		for (i = 0; i < count; i++)
		{
			if (!entries[i].tag.equals(Tag.UNISYS_ISIS_IFD)
					&& !entries[i].tag.equals(Tag.WEIRD)
					&& !entries[i].tag.equals(Tag.UNISYS_IXPS_IFD))
			{
				s += "\t" + entries[i].toString() + "\n";
			} else
			{
				s += "\tTag: " + entries[i].tag.toString() + "\n";
			}
		}

		if (unisys != null)
		{
			s += unisys.toString();
		}

		s += "\t\tNext Offset: " + offset + "\n";
		return s;
	}

	public Image getImage()
	{
		return cImg.getImage();
	}

	public Object getImageProducer()
	{
		return cImg.getImageProducer();
	}

	public void write(MotorolaIntelInputFilter out) throws IOException
	{
		int i, nStripOffsets = 0, nStripByteCounts = 0;
		long nextIFD = offset;

		if (count == 0 || cImg == null)
			return;

		long offset = out.getFilePointer();

		offset += size();

		out.writeShort(count);

		for (i = 0; i < count; i++)
		{

			if (entries[i].tag.equals(Tag.STRIPOFFSETS))
			{
				nStripOffsets = i;
				entries[i].count = 1;
				entries[i].value = offset;
				offset += cImg.imageBytes.length;
			} else if (entries[i].tag.equals(Tag.STRIPBYTECOUNTS))
			{
				nStripByteCounts = i;
				entries[i].count = 1;
				entries[i].value = cImg.imageBytes.length;
			}

			if (entries[i].isOffset())
			{
				entries[i].value = offset;
				offset += entries[i].sizeOfData();
			}

			entries[i].writeEntry(out);
		}

		out.writeInt((int) nextIFD);

		for (i = 0; i < count; i++)
		{
			if (entries[i].isOffset())
				entries[i].writeData(out);
		}

		writeImageBytes(out, nStripOffsets, nStripByteCounts);
	}

	public void writeImageBytes(MotorolaIntelInputFilter out, int nOffsets, int nCounts)
			throws IOException
	{
		int len = 0, i = 0;
		// get image size
		if (entries[nCounts].isOffset())
		{
			System.out.println("No I won't write out in strips for now");
		} else
		{ // just one strip
			len = (int) entries[nCounts].value;
			out.seek(entries[nOffsets].value);
			out.write(cImg.imageBytes, 0, len);
		}
	}

	public void read(MemoryFileInputFilter in)
	{
		int i, nStripOffsets = 0, nStripByteCounts = 0;
		String s;
		int compType = 0; // compression type

		count = in.readUnsignedShort();
		entries = new IFDEntry[count];

		for (i = 0; i < count; i++)
		{
			entries[i] = new IFDEntry();
			entries[i].read(in);

			if (entries[i].tag.equals(Tag.COMPRESSION))
				compType = (int) entries[i].value;

			if (entries[i].tag.equals(Tag.STRIPBYTECOUNTS))
				nStripByteCounts = i;

			if (entries[i].tag.equals(Tag.STRIPOFFSETS))
				nStripOffsets = i;

			if (entries[i].tag.equals(Tag.UNISYS_ISIS_IFD))
			{
				unisys = new UnisysISIS_IFD();
				unisys.offset = entries[i].value;
			}

			if (entries[i].tag.equals(Tag.WEIRD))
			{
				unisys = new Weird_IFD();
				unisys.offset = entries[i].value;
			}

			if (entries[i].tag.equals(Tag.UNISYS_IXPS_IFD))
			{
				unisys = new UnisysIXPS_IFD();
				unisys.offset = entries[i].value;
			}

		}
		offset = in.readInt();

		switch (compType)
		{
			case CompressionType.NONE:
				cImg = new RawImage(this);
				break;
			case CompressionType.CCITTFAX3:
				cImg = new CCITTG3Image(this);
				break;
			case CompressionType.CCITTFAX4:
				cImg = new CCITTG4Image(this);
				break;
			case CompressionType.OJPEG:
			case CompressionType.JPEG:
				cImg = new JPEGImage(this);
				break;
			case CompressionType.PACKBITS:
				cImg = new PackbitsImage(this);
				break;

			/*******************************************************************
			 * don't do these types yet case CompressionType.CCITTRLE: case
			 * CompressionType.CCITTRLEW: cImg = new CCITTImage(this); break;
			 * case CompressionType.LZW: cImg = new LZWImage(this); break; case
			 * CompressionType.NEXT: cImg = new NeXTImage(this); break; case
			 * CompressionType.THUNDERSCAN: cImg = new ThunderScanImage(this);
			 * break; don't do these types yet
			 */

			default:
				cImg = new CodedImage(this);
				break;
		}

		readImageBytes(in, nStripOffsets, nStripByteCounts);
		if (unisys != null)
		{
			in.seek(unisys.offset);
			unisys.read(in);
		}
	}

	public void readImageBytes(MemoryFileInputFilter in, int nOffsets, int nCounts)
	{
		int i, len = 0;

		// get image size
		if (nCounts != 0 && entries[nCounts].isOffset())
		{

			long count = entries[nCounts].count;
			long countArray[] = new long[(int) count];

			cImg.imageStrips = new byte[(int) count][];

			// first jump through the array of byte counts to get total size
			in.seek(entries[nCounts].value);
			for (i = 0, len = 0; i < count; i++)
			{
				countArray[i] = in.readInt();
				len += countArray[i];
			}

			// now jump through getting the offsets
			long offsetArray[] = new long[(int) count];
			in.seek(entries[nOffsets].value);
			for (i = 0; i < count; i++)
			{
				offsetArray[i] = in.readInt();
			}

			// finally jump through the offset array and read the strips
			int n = 0;

			for (i = 0; i < count; i++)
			{
				cImg.imageStrips[i] = new byte[(int) countArray[i]];
				in.seek(offsetArray[i]);
				in.readFully(cImg.imageStrips[i]);
				n += countArray[i];
			}
		} else
		{ // just one strip
			long offset = entries[nOffsets].value;
			len = (nCounts > 0 ? (int) entries[nCounts].value : (int) (in
					.length() - offset));
			cImg.imageBytes = new byte[len];
			in.seek(offset);
			in.readFully(cImg.imageBytes);
		}

	}

}