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

public class OrientationType
{ // ORIENTATION_
	public static final int TOPLEFT = 1; /* row 0 top, col 0 lhs */

	public static final int TOPRIGHT = 2; /* row 0 top, col 0 rhs */

	public static final int BOTRIGHT = 3; /* row 0 bottom, col 0 rhs */

	public static final int BOTLEFT = 4; /* row 0 bottom, col 0 lhs */

	public static final int LEFTTOP = 5; /* row 0 lhs, col 0 top */

	public static final int RIGHTTOP = 6; /* row 0 rhs, col 0 top */

	public static final int RIGHTBOT = 7; /* row 0 rhs, col 0 bottom */

	public static final int LEFTBOT = 8; /* row 0 lhs, col 0 bottom */
}
