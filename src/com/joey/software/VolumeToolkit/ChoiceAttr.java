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
 *      @(#)ChoiceAttr.java 1.4 00/09/20 15:47:50
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

public class ChoiceAttr extends Attr
{
	public int value;

	int initValue;

	String[] valueLabels;

	String[] valueNames;

	protected ChoiceAttr(String label, String[] initValueLabels, int initValue)
	{
		super(label);
		this.initValue = value = initValue;
		valueLabels = initValueLabels;
		valueNames = new String[valueLabels.length];
		for (int i = 0; i < valueLabels.length; i++)
		{
			valueNames[i] = toName(valueLabels[i]);
		}
	}

	@Override
	public String toString()
	{
		return name + " " + valueNames[value];
	}

	@Override
	public void set(String stringValue)
	{
		// TODO: worry about getting the next word?
		for (int i = 0; i < valueNames.length; i++)
		{
			if (stringValue.equals(valueNames[i]))
			{
				value = i;
			}
		}
	}

	@Override
	void reset()
	{
		value = initValue;
	}

	public int getValue()
	{
		return value;
	}
}
