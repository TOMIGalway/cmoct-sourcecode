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
package com.joey.software.dft.util;

/**
 * Package an point on a graph (e.g., x and y values)
 */
public class point
{
	public point(float aix, float why)
	{
		x = aix;
		y = why;
	}

	public float x, y;
}
