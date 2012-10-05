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
package com.joey.software.j3dTookit;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class Axis3D
{
	public static BranchGroup createAxis()
	{

		BranchGroup axisBG = new BranchGroup();

		// create line for X axis
		LineArray axisXLines = new LineArray(2, GeometryArray.COORDINATES
				| GeometryArray.COLOR_3);
		axisBG.addChild(new Shape3D(axisXLines));

		axisXLines.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
		axisXLines.setCoordinate(1, new Point3f(1.0f, 0.0f, 0.0f));

		Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
		Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
		Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);

		// create line for Y axis
		LineArray axisYLines = new LineArray(2, GeometryArray.COORDINATES
				| GeometryArray.COLOR_3);
		axisBG.addChild(new Shape3D(axisYLines));

		axisYLines.setCoordinate(0, new Point3f(0.0f, -1.0f, 0.0f));
		axisYLines.setCoordinate(1, new Point3f(0.0f, 1.0f, 0.0f));

		axisYLines.setColor(0, green);
		axisYLines.setColor(1, blue);

		// create line for Z axis
		Point3f z1 = new Point3f(0.0f, 0.0f, -1.0f);
		Point3f z2 = new Point3f(0.0f, 0.0f, 1.0f);

		LineArray axisZLines = new LineArray(10, GeometryArray.COORDINATES
				| GeometryArray.COLOR_3);
		axisBG.addChild(new Shape3D(axisZLines));

		axisZLines.setCoordinate(0, z1);
		axisZLines.setCoordinate(1, z2);
		axisZLines.setCoordinate(2, z2);
		axisZLines.setCoordinate(3, new Point3f(0.1f, 0.1f, 0.9f));
		axisZLines.setCoordinate(4, z2);
		axisZLines.setCoordinate(5, new Point3f(-0.1f, 0.1f, 0.9f));
		axisZLines.setCoordinate(6, z2);
		axisZLines.setCoordinate(7, new Point3f(0.1f, -0.1f, 0.9f));
		axisZLines.setCoordinate(8, z2);
		axisZLines.setCoordinate(9, new Point3f(-0.1f, -0.1f, 0.9f));

		Color3f colors[] = new Color3f[9];

		colors[0] = new Color3f(0.0f, 1.0f, 1.0f);
		for (int v = 0; v < 9; v++)
		{
			colors[v] = red;
		}

		axisZLines.setColors(1, colors);

		return axisBG;
	}
}
