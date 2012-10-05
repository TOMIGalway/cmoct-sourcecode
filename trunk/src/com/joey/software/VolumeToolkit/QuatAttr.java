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
package com.joey.software.VolumeToolkit;

/*
 *      @(#)QuatAttr.java 1.4 00/09/20 15:47:51
 *
 * Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4d;

public class QuatAttr extends Attr
{
	Quat4d value = new Quat4d();

	Quat4d initValue = new Quat4d();

	QuatAttr(String label, Quat4d initValue)
	{
		super(label);
		this.initValue.set(initValue);
		this.value.set(initValue);
	}

	@Override
	public void set(String stringValue)
	{
		int index;
		String doubleString;
		index = stringValue.indexOf('(');
		stringValue = stringValue.substring(index + 1);
		index = stringValue.indexOf(',');
		doubleString = stringValue.substring(0, index);
		double x = Double.valueOf(doubleString).doubleValue();
		stringValue = stringValue.substring(index + 1);
		index = stringValue.indexOf(',');
		doubleString = stringValue.substring(0, index);
		double y = Double.valueOf(doubleString).doubleValue();
		stringValue = stringValue.substring(index + 1);
		index = stringValue.indexOf(',');
		doubleString = stringValue.substring(0, index);
		double z = Double.valueOf(doubleString).doubleValue();
		stringValue = stringValue.substring(index + 1);
		index = stringValue.indexOf(')');
		doubleString = stringValue.substring(0, index);
		double w = Double.valueOf(doubleString).doubleValue();
		set(new Quat4d(x, y, z, w));
	}

	public void set(Quat4d newValue)
	{
		value.set(newValue);
	}

	public void set(Transform3D xform)
	{
		xform.get(value);
		// System.out.println("rotation = " + this);
	}

	@Override
	public void reset()
	{
		value.set(initValue);
	}

	@Override
	public String toString()
	{
		return name + " (" + numFormat.format(value.x) + ", "
				+ numFormat.format(value.y) + ", " + numFormat.format(value.z)
				+ ", " + numFormat.format(value.w) + ")";
	}

	public Quat4d getValue()
	{
		return new Quat4d(value);
	}

}
