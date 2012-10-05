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

public class CleanFaxDataType
{ // CLEANFAXDATA_
	public static final int CLEAN = 0; /* no errors detected */

	public static final int REGENERATED = 1; /* receiver regenerated lines */

	public static final int UNCLEAN = 2; /* uncorrected errors exist */
}
