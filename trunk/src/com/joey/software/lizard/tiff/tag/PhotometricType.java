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

public class PhotometricType
{ // PHOTOMETRIC_
	public static final int MINISWHITE = 0; /* min value is white */

	public static final int MINISBLACK = 1; /* min value is black */

	public static final int RGB = 2; /* RGB color model */

	public static final int PALETTE = 3; /* color map indexed */

	public static final int MASK = 4; /* $holdout mask */

	public static final int SEPARATED = 5; /* !color separations */

	public static final int YCBCR = 6; /* !CCIR 601 */

	public static final int CIELAB = 8; /* !1976 CIE L*a*b* */
}
