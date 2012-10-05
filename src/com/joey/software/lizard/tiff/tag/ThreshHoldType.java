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

public class ThreshHoldType
{ // THRESHHOLD_
	public static final int BILEVEL = 1; /* b&w art scan */

	public static final int HALFTONE = 2; /* or dithered scan */

	public static final int ERRORDIFFUSE = 3; /* usually floyd-steinberg */
}
