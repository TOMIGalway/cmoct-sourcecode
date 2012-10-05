package com.joey.software.lizard.tiff;

import com.joey.software.lizard.tiff.tag.UnisysIXPSTags;
import com.joey.software.lizard.util.MemoryFileInputFilter;

/*******************************************************************************
 * { 1, "Version" }, { 2, "SIDI" }, { 10, "Processing Date" }, { 11, "cdline and
 * amt id" }, { 12, "Amount" }, { 101, "DIN" }, { 102, "Check Serial Number" },
 * { 516, "Account Number" }, { 517, "Aux on Us" }, { 518, "Position 44" }, {
 * 519, "Routing and Transit" }, { 520, "TranCode" }, { 32000, "IXPS User Area"
 * },
 ******************************************************************************/

class UnisysIXPS_IFD extends IFD
{
	long userAreaIFD = 0;

	@Override
	public String toString()
	{
		String s;
		int i;
		s = "    Unisys IXPS IFD Entry Count: " + count + "\n";
		for (i = 0; i < count; i++)
		{
			UnisysIXPSTags ut = new UnisysIXPSTags(entries[i].tag.Value());
			s += "\tTag: " + ut.toString();

			if (entries[i].isOffset())
			{
				int len = Math.min(entries[i].dataArray.length, 256);
				// s += ", Offset: 0x" + Long.toHexString(entries[i].value) + ",
				// " + entries[i].sizeOfData() + "bytes";

				if (entries[i].type.isAscii())
				{
					String sz = new String(entries[i].dataArray); // jdk1.1
					// allows
					// String(byte[])
					s += sz + "\n";
				}
			} else
				s += entries[i].value + "\n";
		}
		return s;
	}

	@Override
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
			if (entries[i].tag.equals(UnisysIXPSTags.USER))
				userAreaIFD = entries[i].value;
		}
		offset = in.readInt();
	}

}