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

class PackbitsImage extends RawImage
{ // after unpacking, treat as a raw image
	public PackbitsImage(IFD ifd)
	{
		super(ifd);
	}

	/*
	 * public Image getImage() { if (!CanDecodeImage()) return null;
	 * 
	 * if (imageBytes == null && imageStrips != null) { int i, n, len; for
	 * (i=0,len=0; i<imageStrips.length; i++) len+=imageStrips[i].length;
	 * imageBytes = new byte [len]; for (i=0,n=0; i<imageStrips.length; i++) {
	 * System.arraycopy(imageStrips[i], 0, imageBytes, n,
	 * imageStrips[i].length); n += imageStrips[i].length; } imageStrips = null;
	 * }
	 * 
	 * if (!DecodeImage()) return null;
	 * 
	 * return super.getImage(); }
	 */
	@Override
	public Object getImageProducer()
	{
		if (!CanDecodeImage())
			return null;

		if (imageBytes == null && imageStrips != null)
		{
			int i, n, len;
			for (i = 0, len = 0; i < imageStrips.length; i++)
				len += imageStrips[i].length;
			imageBytes = new byte[len];
			for (i = 0, n = 0; i < imageStrips.length; i++)
			{
				System
						.arraycopy(imageStrips[i], 0, imageBytes, n, imageStrips[i].length);
				n += imageStrips[i].length;
			}
			imageStrips = null;
		}

		if (!DecodeImage())
			return null;

		return super.getImageProducer();
	}

	boolean DecodeImage()
	{
		int expectedCount = imageWidth * imageHeight;
		byte unPacked[] = new byte[expectedCount];
		long n;
		byte b;
		int packedCount = imageBytes.length;
		int i = 0, j = 0;

		// System.out.println("Decode Image - packed bits\n\texpected
		// count="+expectedCount);
		// System.out.println("\tpacked count="+packedCount);

		// loop until you get the number of unpacked bytes you are expecting
		while (packedCount > 0 && (long) expectedCount > 0)
		{
			n = imageBytes[i++];
			packedCount--;
			/*
			 * Watch out for compilers that don't sign extend chars...
			 */
			if (n >= 128)
				n -= 256;

			if (n < 0)
			{ /* replicate next byte -n+1 times */
				if (n == -128) /* nop */
					continue;
				n = -n + 1;
				expectedCount -= n;
				b = imageBytes[i++];
				packedCount--;
				while (n-- > 0)
				{
					unPacked[j++] = b;
					// System.out.print(".");
				}
			} else
			{ /* copy next n+1 bytes literally */
				// System.out.println("copy next "+n+"+1 bytes literally");
				System.arraycopy(imageBytes, i, unPacked, j, (int) ++n);
				j += n;
				expectedCount -= n;
				i += n;
				packedCount -= n;
			}
		}

		imageBytes = new byte[unPacked.length];
		System.arraycopy(unPacked, 0, imageBytes, 0, unPacked.length);

		// System.out.println("\n\texpected count="+expectedCount);
		// System.out.println("\tpacked count="+packedCount);

		// lies all lies, worked just fine even with 75600 left in expectedCount
		// if (expectedCount > 0) {
		// System.out.println("PackBitsDecode: Not enough data for scanline");
		// return (false);
		// }

		/* check for buffer overruns? */
		return (true);
	}
}
