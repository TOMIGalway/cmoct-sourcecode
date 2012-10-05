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

public class FaxModeType
{ // FAXMODE_
	public static final int CLASSIC = 0x0000; /* default, include RTC */

	public static final int NORTC = 0x0001; /* no RTC at end of data */

	public static final int NOEOL = 0x0002; /* no EOL code at end of row */

	public static final int BYTEALIGN = 0x0004; /* byte align row */

	public static final int WORDALIGN = 0x0008; /* word align row */

	public static final int CLASSF = 0x0001; // NORTC /* TIFF Class F */
}
