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

public class IntAttr extends Attr
{
	int value;

	int initValue;

	IntAttr(String label, int initValue)
	{
		super(label);
		this.initValue = initValue;
		this.value = initValue;
	}

	@Override
	public void set(String stringValue)
	{
		int newValue = Integer.valueOf(stringValue).intValue();
		set(newValue);
	}

	public void set(int newValue)
	{
		value = newValue;
	}

	@Override
	public void reset()
	{
		value = initValue;
	}

	@Override
	public String toString()
	{
		return name + " " + numFormat.format(value);
	}

	public int getValue()
	{
		return value;
	}
}
