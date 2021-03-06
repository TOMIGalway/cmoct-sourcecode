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
package com.joey.software.TransferFunctions;

import javax.vecmath.Vector4f;

public class TransferControlPoint
{
	public Vector4f Color;

	public int IsoValue;

	// / <summary>
	// / Constructor for color control points.
	// / Takes rgb color components that specify the color at the supplied
	// isovalue.
	// / </summary>
	// / <param name="x"></param>
	// / <param name="y"></param>
	// / <param name="z"></param>
	// / <param name="isovalue"></param>
	public TransferControlPoint(float r, float g, float b, int isovalue)
	{
		Color.x = r;
		Color.y = g;
		Color.z = b;
		Color.w = 1.0f;
		IsoValue = isovalue;
	}

	// / <summary>
	// / Constructor for alpha control points.
	// / Takes an alpha that specifies the aplpha at the supplied isovalue.
	// / </summary>
	// / <param name="alpha"></param>
	// / <param name="isovalue"></param>
	public TransferControlPoint(float alpha, int isovalue)
	{
		Color.x = 0.0f;
		Color.y = 0.0f;
		Color.z = 0.0f;
		Color.w = alpha;
		IsoValue = isovalue;
	}
}
