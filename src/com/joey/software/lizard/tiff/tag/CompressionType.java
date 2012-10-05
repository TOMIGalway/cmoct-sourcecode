package com.joey.software.lizard.tiff.tag;

public class CompressionType
{ // COMPRESSION_
	public int id;

	public static final int NONE = 1; /* dump mode */

	public static final int CCITTRLE = 2; /* CCITT modified Huffman RLE */

	public static final int CCITTFAX3 = 3; /* CCITT Group 3 fax encoding */

	public static final int CCITTFAX4 = 4; /* CCITT Group 4 fax encoding */

	public static final int LZW = 5; /* Lempel-Ziv & Welch */

	public static final int OJPEG = 6; /* !6.0 JPEG */

	public static final int JPEG = 7; /* %JPEG DCT compression */

	public static final int NEXT = 32766; /* NeXT 2-bit RLE */

	public static final int CCITTRLEW = 32771; /* #1 w/ word alignment */

	public static final int PACKBITS = 32773; /* Macintosh RLE */

	public static final int THUNDERSCAN = 32809; /* ThunderScan RLE */

	/* codes 32895-32898 are reserved for ANSI IT8 TIFF/IT <dkelly@etsinc.com) */
	public static final int IT8CTPAD = 32895; /* IT8 CT w/padding */

	public static final int IT8LW = 32896; /* IT8 Linework RLE */

	public static final int IT8MP = 32897; /* IT8 Monochrome picture */

	public static final int IT8BL = 32898; /* IT8 Binary line art */

	/* compression codes 32908-32911 are reserved for Pixar */
	public static final int PIXARFILM = 32908; /* Pixar companded 10bit LZW */

	public static final int PIXARLOG = 32909; /* Pixar companded 11bit ZIP */

	public static final int DEFLATE = 32946; /* Deflate compression */

	/* compression code 32947 is reserved for Oceana Matrix <dev@oceana.com> */
	public static final int DCS = 32947; /* Kodak DCS encoding */

	public static final int JBIG = 34661; /* ISO JBIG */

	public CompressionType()
	{
		id = NONE;
	}

	public CompressionType(int n)
	{
		id = n;
	}

	@Override
	public String toString()
	{
		switch (id)
		{
			case NONE:
				return "NONE";
			case CCITTRLE:
				return "CCITT RLE";
			case CCITTFAX3:
				return "CCITT FAX 3";
			case CCITTFAX4:
				return "CCITT FAX 4";
			case LZW:
				return "LZW";
			case OJPEG:
				return "old JPEG";
			case JPEG:
				return "new JPEG";
			case NEXT:
				return "NeXT";
			case CCITTRLEW:
				return "CCITT RLE/W";
			case PACKBITS:
				return "PACKBITS";
			case THUNDERSCAN:
				return "ThunderScan";
			default:
				return "other";
		}
	}

	public static String toString(int n)
	{
		switch (n)
		{
			case NONE:
				return "NONE";
			case CCITTRLE:
				return "CCITT RLE";
			case CCITTFAX3:
				return "CCITT FAX 3";
			case CCITTFAX4:
				return "CCITT FAX 4";
			case LZW:
				return "LZW";
			case OJPEG:
				return "old JPEG";
			case JPEG:
				return "new JPEG";
			case NEXT:
				return "NeXT";
			case CCITTRLEW:
				return "CCITT RLE/W";
			case PACKBITS:
				return "PACKBITS";
			case THUNDERSCAN:
				return "ThunderScan";
			default:
				return Integer.toHexString(n);
		}
	}
}