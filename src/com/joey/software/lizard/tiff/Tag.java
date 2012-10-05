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

import java.io.IOException;

import com.joey.software.lizard.util.MemoryFileInputFilter;
import com.joey.software.lizard.util.MotorolaIntelInputFilter;


/*
 * NB: In the comments below,
 *  - items marked with a + are obsoleted by revision 5.0,
 *  - items marked with a ! are introduced in revision 6.0.
 *  - items marked with a % are introduced post revision 6.0.
 *  - items marked with a $ are obsoleted by revision 6.0.
 */

/*
 * TIFF Tag Definitions.
 */

public class Tag
{

	public int id;

	public static final int MINIMUM = 254;

	public static final int MAXIMUM_STANDARD = 532;

	public static final int MINIMUM_PRIVATE = 32768;

	public static final int SUBFILETYPE = 254; /* subfile data descriptor */

	public static final int OSUBFILETYPE = 255; /* +kind of data in subfile */

	public static final int IMAGEWIDTH = 256; /* image width in pixels */

	public static final int IMAGELENGTH = 257; /* image height in pixels */

	public static final int BITSPERSAMPLE = 258; /* bits per channel (sample) */

	public static final int COMPRESSION = 259; /* data compression technique */

	/* 260 & 261 are undefined */
	public static final int PHOTOMETRIC = 262; /* photometric interpretation */

	public static final int THRESHHOLDING = 263; /*
												 * +thresholding used on data
												 */

	public static final int CELLWIDTH = 264; /* +dithering matrix width */

	public static final int CELLLENGTH = 265; /* +dithering matrix height */

	public static final int FILLORDER = 266; /* data order within a byte */

	/* 267 & 268 are undefined */
	public static final int DOCUMENTNAME = 269; /* name of doc. image is from */

	public static final int IMAGEDESCRIPTION = 270; /* info about image */

	public static final int MAKE = 271; /* scanner manufacturer name */

	public static final int MODEL = 272; /* scanner model name/number */

	public static final int STRIPOFFSETS = 273; /* offsets to data strips */

	public static final int ORIENTATION = 274; /* +image orientation */

	/* 275 & 276 are undefined */
	public static final int SAMPLESPERPIXEL = 277; /* samples per pixel */

	public static final int ROWSPERSTRIP = 278; /* rows per strip of data */

	public static final int STRIPBYTECOUNTS = 279; /* bytes counts for strips */

	public static final int MINSAMPLEVALUE = 280; /* +minimum sample value */

	public static final int MAXSAMPLEVALUE = 281; /* +maximum sample value */

	public static final int XRESOLUTION = 282; /* pixels/resolution in x */

	public static final int YRESOLUTION = 283; /* pixels/resolution in y */

	public static final int PLANARCONFIG = 284; /* storage organization */

	public static final int PAGENAME = 285; /* page name image is from */

	public static final int XPOSITION = 286; /* x page offset of image lhs */

	public static final int YPOSITION = 287; /* y page offset of image lhs */

	public static final int FREEOFFSETS = 288; /* +byte offset to free block */

	public static final int FREEBYTECOUNTS = 289; /* +sizes of free blocks */

	public static final int GRAYRESPONSEUNIT = 290; /*
													 * $gray scale curve
													 * accuracy
													 */

	public static final int GRAYRESPONSECURVE = 291; /*
													 * $gray scale response
													 * curve
													 */

	public static final int GROUP3OPTIONS = 292; /* 32 flag bits */

	public static final int GROUP4OPTIONS = 293; /* 32 flag bits */

	/* 294 & 295 are undefined */
	public static final int RESOLUTIONUNIT = 296; /* units of resolutions */

	public static final int PAGENUMBER = 297; /* page numbers of multi-page */

	/* 298 & 299 are undefined */
	public static final int COLORRESPONSEUNIT = 300; /* $color curve accuracy */

	public static final int TRANSFERFUNCTION = 301; /* !colorimetry info */

	/* 302, 303, 304 are undefined */
	public static final int SOFTWARE = 305; /* name & release */

	public static final int DATETIME = 306; /* creation date and time */

	/* 307 - 314 are undefined */
	public static final int ARTIST = 315; /* creator of image */

	public static final int HOSTCOMPUTER = 316; /* machine where created */

	public static final int PREDICTOR = 317; /* prediction scheme w/ LZW */

	public static final int WHITEPOINT = 318; /* image white point */

	public static final int PRIMARYCHROMATICITIES = 319; /*
														 * !primary
														 * chromaticities
														 */

	public static final int COLORMAP = 320; /* RGB map for pallette image */

	public static final int HALFTONEHINTS = 321; /* !highlight+shadow info */

	public static final int TILEWIDTH = 322; /* !rows/data tile */

	public static final int TILELENGTH = 323; /* !cols/data tile */

	public static final int TILEOFFSETS = 324; /* !offsets to data tiles */

	public static final int TILEBYTECOUNTS = 325; /* !byte counts for tiles */

	public static final int BADFAXLINES = 326; /* lines w/ wrong pixel count */

	public static final int CLEANFAXDATA = 327; /* regenerated line info */

	public static final int CONSECUTIVEBADFAXLINES = 328; /*
														 * max consecutive bad
														 * lines
														 */

	/* 329 is undefined */
	public static final int SUBIFD = 330; /* subimage descriptors */

	/* 331 is undefined */
	public static final int INKSET = 332; /* !inks in separated image */

	public static final int INKNAMES = 333; /* !ascii names of inks */

	public static final int NUMBEROFINKS = 334; /* !number of inks */

	public static final int DOTRANGE = 336; /* !0% and 100% dot codes */

	public static final int TARGETPRINTER = 337; /* !separation target */

	public static final int EXTRASAMPLES = 338; /* !info about extra samples */

	public static final int SAMPLEFORMAT = 339; /* !data sample format */

	public static final int SMINSAMPLEVALUE = 340; /* !variable MinSampleValue */

	public static final int SMAXSAMPLEVALUE = 341; /* !variable MaxSampleValue */

	/* 342-346 are undefined */
	public static final int JPEGTABLES = 347; /* %JPEG table stream */

	/* 348-511 are undefined */

	/*
	 * Tags 512-521 are obsoleted by Technical Note #2 which specifies a revised
	 * JPEG-in-TIFF scheme.
	 */
	public static final int JPEGPROC = 512; /* !JPEG processing algorithm */

	public static final int JPEGIFOFFSET = 513; /* !pointer to SOI marker */

	public static final int JPEGIFBYTECOUNT = 514; /* !JFIF stream length */

	public static final int JPEGRESTARTINTERVAL = 515; /*
														 * !restart interval
														 * length
														 */

	/* 316 is undefined */
	public static final int JPEGLOSSLESSPREDICTORS = 517; /*
														 * !lossless proc
														 * predictor
														 */

	public static final int JPEGPOINTTRANSFORM = 518; /*
													 * !lossless point transform
													 */

	public static final int JPEGQTABLES = 519; /* !Q matrice offsets */

	public static final int JPEGDCTABLES = 520; /* !DCT table offsets */

	public static final int JPEGACTABLES = 521; /* !AC coefficient offsets */

	/* 322-328 are undefined */
	public static final int YCBCRCOEFFICIENTS = 529; /*
													 * !RGB -> YCbCr transform
													 */

	public static final int YCBCRSUBSAMPLING = 530; /*
													 * !YCbCr subsampling
													 * factors
													 */

	public static final int YCBCRPOSITIONING = 531; /* !subsample positioning */

	public static final int REFERENCEBLACKWHITE = 532; /* !colorimetry info */

	/* tags 32952-32956 are private tags registered to Island Graphics */
	public static final int REFPTS = 32953; /* image reference points */

	public static final int REGIONTACKPOINT = 32954; /*
													 * region-xform tack point
													 */

	public static final int REGIONWARPCORNERS = 32955; /* warp quadrilateral */

	public static final int REGIONAFFINE = 32956; /* affine transformation mat */

	/* tags 32995-32999 are private tags registered to SGI */
	public static final int MATTEING = 32995; /* $use ExtraSamples */

	public static final int DATATYPE = 32996; /* $use SampleFormat */

	public static final int IMAGEDEPTH = 32997; /* z depth of image */

	public static final int TILEDEPTH = 32998; /* z depth/data tile */

	/* tags 33300-33309 are private tags registered to Pixar */
	/*
	 * PIXAR_IMAGEFULLWIDTH and PIXAR_IMAGEFULLLENGTH are set when an image has
	 * been cropped out of a larger image. They reflect the size of the original
	 * uncropped image. The XPOSITION and YPOSITION can be used to determine the
	 * position of the smaller image in the larger one.
	 */
	public static final int PIXAR_IMAGEFULLWIDTH = 33300; /*
														 * full image size in x
														 */

	public static final int PIXAR_IMAGEFULLLENGTH = 33301; /*
															 * full image size
															 * in y
															 */

	/* tag 33405 is a private tag registered to Eastman Kodak */
	public static final int WRITERSERIALNUMBER = 33405; /* device serial number */

	/* tag 33432 is listed in the 6.0 spec w/ unknown ownership */
	public static final int COPYRIGHT = 33432; /* copyright string */

	/* tag 33881, 33882, and 33884 are private tags registered to Unisys */

	public static final int UNISYS_ISIS_IFD = 33881; /* offset to ISIS IFD */

	public static final int UNISYS_SIDE = 33882; /*
												 * check side, 1=front 2=back
												 */

	public static final int UNISYS_IXPS_IFD = 33884; /* offset to IXPS IFD */

	public static final int WEIRD = 34975;

	/*
	 * tag 33881, 33882, and 33884 are private tags registered to Unisys 33881 -
	 * Unisys ISIS Document IFD { 1, "DIN" }, { 2, "Check Serial Num" }, { 3,
	 * "Processing Date" }, { 4, "Check Amount" }, { 5, "Transaction Code" }, {
	 * 6, "Account Number" }, { 7, "Transit Transit" }, { 8, "Auxiliary On Us"
	 * }, { 9, "Position 44" }, { 10, "Processing Date" }, { 11, "User Area" },
	 * { 0, "User ID" }, { 1, "Site ID" }, { 2, "Capture Start" }, { 3,
	 * "Capture PID" }, { 4, "Document Num" }, { 5, "Migration Date" }, { 6,
	 * "Micro Film Seq" }, { 7, "Item Seq" }, { 8, "Tracer Number" }, { 9,
	 * "Side ID" }, //0=front 1=back { 10, "Zone List" }, { 11,
	 * "Next Level 1 IFD offset" },
	 * 
	 * 33882 - Unisys Side (1=front 2=back) 33884 - Unisys IXPS Document IFD {
	 * 1, "Version" }, { 2, "SIDI" }, { 10, "Processing Date" }, { 11, "cdline
	 * and amt id" }, { 12, "Amount" }, { 101, "DIN" }, { 102, "Check Serial
	 * Number" }, { 516, "Account Number" }, { 517, "Aux on Us" }, { 518,
	 * "Position 44" }, { 519, "Routing and Transit" }, { 520, "TranCode" }, {
	 * 32000, "IXPS User Area" },
	 */

	/* 34016-34029 are reserved for ANSI IT8 TIFF/IT <dkelly@etsinc.com) */
	public static final int IT8SITE = 34016; /* site name */

	public static final int IT8COLORSEQUENCE = 34017; /*
													 * color seq. [RGB,CMYK,etc]
													 */

	public static final int IT8HEADER = 34018; /* DDES Header */

	public static final int IT8RASTERPADDING = 34019; /*
													 * raster scanline padding
													 */

	public static final int IT8BITSPERRUNLENGTH = 34020; /*
														 * # of bits in int run
														 */

	public static final int IT8BITSPEREXTENDEDRUNLENGTH = 34021;/*
																 * # of bits in
																 * long run
																 */

	public static final int IT8COLORTABLE = 34022; /* LW colortable */

	public static final int IT8IMAGECOLORINDICATOR = 34023; /*
															 * BP/BL image color
															 * switch
															 */

	public static final int IT8BKGCOLORINDICATOR = 34024; /*
														 * BP/BL bg color switch
														 */

	public static final int IT8IMAGECOLORVALUE = 34025; /*
														 * BP/BL image color
														 * value
														 */

	public static final int IT8BKGCOLORVALUE = 34026; /* BP/BL bg color value */

	public static final int IT8PIXELINTENSITYRANGE = 34027; /*
															 * MP pixel
															 * intensity value
															 */

	public static final int IT8TRANSPARENCYINDICATOR = 34028; /*
															 * HC transparency
															 * switch
															 */

	public static final int IT8COLORCHARACTERIZATION = 34029; /*
															 * color character.
															 * table
															 */

	/* tags 34232-34236 are private tags registered to Texas Instruments */
	public static final int FRAMECOUNT = 34232; /* Sequence Frame Count */

	/* tag 34750 is a private tag registered to Adobe? */
	public static final int ICCPROFILE = 34675; /* ICC profile data */

	/* tag 34750 is a private tag registered to Pixel Magic */
	public static final int JBIGOPTIONS = 34750; /* JBIG options */

	/* tags 34908-34914 are private tags registered to SGI */
	public static final int FAXRECVPARAMS = 34908; /*
													 * encoded Class 2 ses.
													 * parms
													 */

	public static final int FAXSUBADDRESS = 34909; /* received SubAddr string */

	public static final int FAXRECVTIME = 34910; /* receive time (secs) */

	/* tag 65535 is an undefined tag used by Eastman Kodak */
	public static final int DCSHUESHIFTVALUES = 65535; /*
														 * hue shift correction
														 * data
														 */

	/*
	 * The following are ``pseudo tags'' that can be used to control
	 * codec-specific functionality. These tags are not written to file. Note
	 * that these values start at 0xffff+1 so that they'll never collide with
	 * Aldus-assigned tags.
	 * 
	 * If you want your private pseudo tags ``registered'' (i.e. added to this
	 * file), send mail to sam@sgi.com with the appropriate C definitions to
	 * add.
	 */
	public static final int FAXMODE = 65536; /* Group 3/4 format control */

	public static final int JPEGQUALITY = 65537; /* Compression quality level */

	/* Note: quality level is on the IJG 0-100 scale. Default value is 75 */
	public static final int JPEGCOLORMODE = 65538; /*
													 * Auto RGB<=>YCbCr convert?
													 */

	public static final int JPEGTABLESMODE = 65539; /* What to put in JPEGTables */

	/* Note: default is JPEGTABLESMODE_QUANT | JPEGTABLESMODE_HUFF */
	public static final int FAXFILLFUNC = 65540; /* G3/G4 fill function */

	public static final int PIXARLOGDATAFMT = 65549; /*
													 * PixarLogCodec I/O data sz
													 */

	/* 65550-65556 are allocated to Oceana Matrix <dev@oceana.com> */
	public static final int DCSIMAGERTYPE = 65550; /* imager model & filter */

	public static final int DCSINTERPMODE = 65551; /* interpolation mode */

	public static final int DCSBALANCEARRAY = 65552; /* color balance values */

	public static final int DCSCORRECTMATRIX = 65553; /*
													 * color correction values
													 */

	public static final int DCSGAMMA = 65554; /* gamma value */

	public static final int DCSTOESHOULDERPTS = 65555; /* toe & shoulder points */

	public static final int DCSCALIBRATIONFD = 65556; /* calibration file desc */

	/* Note: quality level is on the ZLIB 1-9 scale. Default value is -1 */
	public static final int ZIPQUALITY = 65557; /* compression quality level */

	public static final int PIXARLOGQUALITY = 65558; /*
													 * PixarLog uses same scale
													 */

	/* 65559 is allocated to Oceana Matrix <dev@oceana.com> */
	public static final int DCSCLIPRECTANGLE = 65559; /*
													 * area of image to acquire
													 */

	/** ******************** METHODS ********************** */
	public Tag()
	{
		id = 0;
	}

	public Tag(int n)
	{
		id = n;
		if (!IsStandard())
		{
			System.out.println("WARNING: Non-Standard Tag " + n);
		}
	}

	public int Value()
	{
		return id;
	}

	public boolean IsStandard()
	{

		boolean isValid = (id >= MINIMUM && id <= MAXIMUM_STANDARD);

		isValid = isValid && (id < 260 || id > 261);
		isValid = isValid && (id < 267 || id > 268);
		isValid = isValid && (id < 275 || id > 276);
		isValid = isValid && (id < 294 || id > 295);
		isValid = isValid && (id < 298 || id > 299);
		isValid = isValid && (id < 302 || id > 304);
		isValid = isValid && (id < 307 || id > 314);
		isValid = isValid && (id != 329 && id != 331);
		isValid = isValid && (id < 342 || id > 346);
		isValid = isValid && (id < 348 || id > 511);

		return isValid;
	}

	public boolean IsPrivate()
	{
		return (id >= MINIMUM_PRIVATE);
	}

	public boolean IsOffsetTag()
	{
		return (id == STRIPOFFSETS || id == TILEOFFSETS || id == JPEGTABLES || id == JPEGIFOFFSET);
	}

	public boolean equals(int n)
	{
		return id == n;
	}

	public void write(MotorolaIntelInputFilter out) throws IOException
	{
		out.writeShort(id);
	}

	public void read(MemoryFileInputFilter in)
	{
		id = in.readUnsignedShort();
	}

	@Override
	public String toString()
	{
		// return Integer.toString(id); //toHexString(id);
		String sz = "";
		switch (id)
		{
			case SUBFILETYPE:
				sz = "SUBFILETYPE      ";
				break;
			case OSUBFILETYPE:
				sz = "OSUBFILETYPE     ";
				break;
			case IMAGEWIDTH:
				sz = "IMAGEWIDTH       ";
				break;
			case IMAGELENGTH:
				sz = "IMAGELENGTH      ";
				break;
			case BITSPERSAMPLE:
				sz = "BITSPERSAMPLE    ";
				break;
			case COMPRESSION:
				sz = "COMPRESSION      ";
				break;
			case PHOTOMETRIC:
				sz = "PHOTOMETRIC      ";
				break;
			case THRESHHOLDING:
				sz = "THRESHHOLDING    ";
				break;
			case CELLWIDTH:
				sz = "CELLWIDTH        ";
				break;
			case CELLLENGTH:
				sz = "CELLLENGTH       ";
				break;
			case FILLORDER:
				sz = "FILLORDER        ";
				break;
			case DOCUMENTNAME:
				sz = "DOCUMENTNAME     ";
				break;
			case IMAGEDESCRIPTION:
				sz = "IMAGEDESCRIPTION ";
				break;
			case MAKE:
				sz = "MAKE             ";
				break;
			case MODEL:
				sz = "MODEL            ";
				break;
			case STRIPOFFSETS:
				sz = "STRIPOFFSETS     ";
				break;
			case ORIENTATION:
				sz = "ORIENTATION      ";
				break;
			case SAMPLESPERPIXEL:
				sz = "SAMPLESPERPIXEL  ";
				break;
			case ROWSPERSTRIP:
				sz = "ROWSPERSTRIP     ";
				break;
			case STRIPBYTECOUNTS:
				sz = "STRIPBYTECOUNTS  ";
				break;
			case MINSAMPLEVALUE:
				sz = "MINSAMPLEVALUE   ";
				break;
			case MAXSAMPLEVALUE:
				sz = "MAXSAMPLEVALUE   ";
				break;
			case XRESOLUTION:
				sz = "XRESOLUTION      ";
				break;
			case YRESOLUTION:
				sz = "YRESOLUTION      ";
				break;
			case PLANARCONFIG:
				sz = "PLANARCONFIG     ";
				break;
			case PAGENAME:
				sz = "PAGENAME         ";
				break;
			case XPOSITION:
				sz = "XPOSITION        ";
				break;
			case YPOSITION:
				sz = "YPOSITION        ";
				break;
			case FREEOFFSETS:
				sz = "FREEOFFSETS      ";
				break;
			case FREEBYTECOUNTS:
				sz = "FREEBYTECOUNTS   ";
				break;
			case GRAYRESPONSEUNIT:
				sz = "GRAYRESPONSEUNIT ";
				break;
			case GRAYRESPONSECURVE:
				sz = "GRAYRESPONSECURVE ";
				break;
			case GROUP3OPTIONS:
				sz = "GROUP3OPTIONS    ";
				break;
			case GROUP4OPTIONS:
				sz = "GROUP4OPTIONS    ";
				break;
			case RESOLUTIONUNIT:
				sz = "RESOLUTIONUNIT   ";
				break;
			case PAGENUMBER:
				sz = "PAGENUMBER       ";
				break;
			case COLORRESPONSEUNIT:
				sz = "COLORRESPONSEUNIT ";
				break;
			case TRANSFERFUNCTION:
				sz = "TRANSFERFUNCTION ";
				break;
			case SOFTWARE:
				sz = "SOFTWARE         ";
				break;
			case DATETIME:
				sz = "DATETIME         ";
				break;
			case ARTIST:
				sz = "ARTIST           ";
				break;
			case HOSTCOMPUTER:
				sz = "HOSTCOMPUTER     ";
				break;
			case PREDICTOR:
				sz = "PREDICTOR        ";
				break;
			case WHITEPOINT:
				sz = "WHITEPOINT       ";
				break;
			case PRIMARYCHROMATICITIES:
				sz = "PRIMARYCHROMATICITIES ";
				break;
			case COLORMAP:
				sz = "COLORMAP         ";
				break;
			case HALFTONEHINTS:
				sz = "HALFTONEHINTS    ";
				break;
			case TILEWIDTH:
				sz = "TILEWIDTH        ";
				break;
			case TILELENGTH:
				sz = "TILELENGTH       ";
				break;
			case TILEOFFSETS:
				sz = "TILEOFFSETS      ";
				break;
			case TILEBYTECOUNTS:
				sz = "TILEBYTECOUNTS   ";
				break;
			case BADFAXLINES:
				sz = "BADFAXLINES      ";
				break;
			case CLEANFAXDATA:
				sz = "CLEANFAXDATA     ";
				break;
			case CONSECUTIVEBADFAXLINES:
				sz = "CONSECUTIVEBADFAXLINES ";
				break;
			case INKSET:
				sz = "INKSET           ";
				break;
			case INKNAMES:
				sz = "INKNAMES         ";
				break;
			case DOTRANGE:
				sz = "DOTRANGE         ";
				break;
			case TARGETPRINTER:
				sz = "TARGETPRINTER    ";
				break;
			case EXTRASAMPLES:
				sz = "EXTRASAMPLES     ";
				break;
			case SAMPLEFORMAT:
				sz = "SAMPLEFORMAT     ";
				break;
			case SMINSAMPLEVALUE:
				sz = "SMINSAMPLEVALUE  ";
				break;
			case SMAXSAMPLEVALUE:
				sz = "SMAXSAMPLEVALUE  ";
				break;
			case JPEGPROC:
				sz = "JPEGPROC         ";
				break;
			case JPEGIFOFFSET:
				sz = "JPEGIFOFFSET     ";
				break;
			case JPEGIFBYTECOUNT:
				sz = "JPEGIFBYTECOUNT  ";
				break;
			case JPEGRESTARTINTERVAL:
				sz = "JPEGRESTARTINTERVAL ";
				break;
			case JPEGLOSSLESSPREDICTORS:
				sz = "JPEGLOSSLESSPREDICTORS ";
				break;
			case JPEGPOINTTRANSFORM:
				sz = "JPEGPOINTTRANSFORM ";
				break;
			case JPEGQTABLES:
				sz = "JPEGQTABLES      ";
				break;
			case JPEGDCTABLES:
				sz = "JPEGDCTABLES     ";
				break;
			case JPEGACTABLES:
				sz = "JPEGACTABLES     ";
				break;
			case YCBCRCOEFFICIENTS:
				sz = "YCBCRCOEFFICIENTS ";
				break;
			case YCBCRSUBSAMPLING:
				sz = "YCBCRSUBSAMPLING ";
				break;
			case YCBCRPOSITIONING:
				sz = "YCBCRPOSITIONING ";
				break;
			case REFERENCEBLACKWHITE:
				sz = "REFERENCEBLACKWHITE ";
				break;
			case REFPTS:
				sz = "REFPTS      		";
				break;
			case REGIONTACKPOINT:
				sz = "REGIONTACKPOINT   ";
				break;
			case REGIONWARPCORNERS:
				sz = "REGIONWARPCORNERS ";
				break;
			case REGIONAFFINE:
				sz = "REGIONAFFINE      ";
				break;
			case MATTEING:
				sz = "MATTEING      	";
				break;
			case DATATYPE:
				sz = "DATATYPE      	";
				break;
			case IMAGEDEPTH:
				sz = "IMAGEDEPTH      	";
				break;
			case TILEDEPTH:
				sz = "TILEDEPTH      	";
				break;
			case PIXAR_IMAGEFULLWIDTH:
				sz = "PIXAR_IMAGEFULLWIDTH ";
				break;
			case PIXAR_IMAGEFULLLENGTH:
				sz = "PIXAR_IMAGEFULLLENGTH ";
				break;
			case WRITERSERIALNUMBER:
				sz = "WRITERSERIALNUMBER ";
				break;
			case COPYRIGHT:
				sz = "COPYRIGHT      	";
				break;
			case IT8SITE:
				sz = "IT8SITE      		";
				break;
			case IT8COLORSEQUENCE:
				sz = "IT8COLORSEQUENCE  ";
				break;
			case IT8HEADER:
				sz = "IT8HEADER      	";
				break;
			case IT8RASTERPADDING:
				sz = "IT8RASTERPADDING  ";
				break;
			case IT8BITSPERRUNLENGTH:
				sz = "IT8BITSPERRUNLENGTH ";
				break;
			case IT8BITSPEREXTENDEDRUNLENGTH:
				sz = "IT8BITSPEREXTENDEDRUNLENGTH ";
				break;
			case IT8COLORTABLE:
				sz = "IT8COLORTABLE     ";
				break;
			case IT8IMAGECOLORINDICATOR:
				sz = "IT8IMAGECOLORINDICATOR ";
				break;
			case IT8BKGCOLORINDICATOR:
				sz = "IT8BKGCOLORINDICATOR ";
				break;
			case IT8IMAGECOLORVALUE:
				sz = "IT8IMAGECOLORVALUE ";
				break;
			case IT8BKGCOLORVALUE:
				sz = "IT8BKGCOLORVALUE  ";
				break;
			case IT8PIXELINTENSITYRANGE:
				sz = "IT8PIXELINTENSITYRANGE ";
				break;
			case IT8TRANSPARENCYINDICATOR:
				sz = "IT8TRANSPARENCYINDICATOR ";
				break;
			case IT8COLORCHARACTERIZATION:
				sz = "IT8COLORCHARACTERIZATION ";
				break;
			case FRAMECOUNT:
				sz = "FRAMECOUNT      	";
				break;
			case ICCPROFILE:
				sz = "ICCPROFILE      	";
				break;
			case JBIGOPTIONS:
				sz = "JBIGOPTIONS      	";
				break;
			case FAXRECVPARAMS:
				sz = "FAXRECVPARAMS     ";
				break;
			case FAXSUBADDRESS:
				sz = "FAXSUBADDRESS     ";
				break;
			case FAXRECVTIME:
				sz = "FAXRECVTIME      	";
				break;
			case DCSHUESHIFTVALUES:
				sz = "DCSHUESHIFTVALUES ";
				break;

			case UNISYS_ISIS_IFD:
				sz = "UNISYS_ISIS_IFD";
				break;
			case UNISYS_SIDE:
				sz = "UNISYS_SIDE";
				break;
			case UNISYS_IXPS_IFD:
				sz = "UNISYS_IXPS_IFD";
				break;
			case WEIRD:
				sz = "BANCTEC_IFD";
				break;

			default:
				sz = "Unknown Tag #" + id + " ";
				break;
		}
		return sz;
	}
}
