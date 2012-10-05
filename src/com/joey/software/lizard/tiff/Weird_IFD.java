/*******************************************************************************
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
package com.joey.software.lizard.tiff;

import com.joey.software.lizard.util.MemoryFileInputFilter;

/*******************************************************************************
 * { 1, "DIN" }, { 2, "Check Serial Num" }, { 3, "Processing Date" }, { 4,
 * "Check Amount" }, { 5, "Transaction Code" }, { 6, "Account Number" }, { 7,
 * "Transit Transit" }, { 8, "Auxiliary On Us" }, { 9, "Position 44" }, { 10,
 * "Processing Date" }, { 11, "User Area" }, { 0, "User ID" }, { 1, "Site ID" },
 * { 2, "Capture Start" }, { 3, "Capture PID" }, { 4, "Document Num" }, { 5,
 * "Migration Date" }, { 6, "Micro Film Seq" }, { 7, "Item Seq" }, { 8, "Tracer
 * Number" }, { 9, "Side ID" }, //0=front 1=back { 10, "Zone List" }, { 11,
 * "Next Level 1 IFD offset" },
 ******************************************************************************/

class Weird_IFD extends IFD
{

	@Override
	public String toString()
	{
		String s;
		int i;
		s = "    BancTec IFD Entry Count: " + count + "\n";
		for (i = 0; i < count; i++)
		{
			s += "\tTag: " + entries[i].tag.Value() + ", "
					+ entries[i].type.toString() + ", "
					+ entries[i].sizeOfData() + "bytes\n\t\t";

			if (entries[i].isOffset())
			{
				int len = Math.min(entries[i].dataArray.length, 256);

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
			entries[i].read(in, offset);
		}
		offset = in.readInt();
	}

}
