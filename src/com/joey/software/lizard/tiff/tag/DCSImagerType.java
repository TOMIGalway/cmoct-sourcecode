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
package com.joey.software.lizard.tiff.tag;

public class DCSImagerType
{ // DCSIMAGERMODEL_ && DCSIMAGERFILTER_
	public static final int M3 = 0; /* M3 chip (1280 x 1024) */

	public static final int M5 = 1; /* M5 chip (1536 x 1024) */

	public static final int M6 = 2; /* M6 chip (3072 x 2048) */

	public static final int IR = 0; /* infrared filter */

	public static final int MONO = 1; /* monochrome filter */

	public static final int CFA = 2; /* color filter array */

	public static final int OTHER = 3; /* other filter */
}
