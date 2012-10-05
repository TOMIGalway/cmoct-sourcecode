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

public class UnisysISISTags
{
	public static final int DIN = 1; /* DIN */

	public static final int SERIAL = 2; /* Check Serial Num */

	public static final int DATE = 3; /* Processing Date */

	public static final int AMOUNT = 4; /* Check Amount */

	public static final int TRANCODE = 5; /* Transaction Code */

	public static final int ACCOUNT = 6; /* Account Number */

	public static final int TRANSIT = 7; /* Transit Transit */

	public static final int AUXONUS = 8; /* Auxiliary On Us */

	public static final int POS44 = 9; /* Position 44 */

	public static final int DATE2 = 10; /* Processing Date */

	public static final int USER = 11; /* User Area */

	int id;

	public UnisysISISTags(int i)
	{
		id = i;
	}

	@Override
	public String toString()
	{
		String sz;
		switch (id)
		{
			case DIN:
				sz = "DIN";
				break;
			case SERIAL:
				sz = "Check Serial Num";
				break;
			case DATE:
				sz = "Processing Date";
				break;
			case AMOUNT:
				sz = "Check Amount";
				break;
			case TRANCODE:
				sz = "Transaction Code";
				break;
			case ACCOUNT:
				sz = "Account Number";
				break;
			case TRANSIT:
				sz = "Transit Transit";
				break;
			case AUXONUS:
				sz = "Auxiliary On Us";
				break;
			case POS44:
				sz = "Position 44";
				break;
			case DATE2:
				sz = "Processing Date";
				break;
			case USER:
				sz = "User Area";
				break;
			default:
				sz = "Unknown Unisys ISIS tag: " + id;
				break;
		}
		return sz;
	}

}
