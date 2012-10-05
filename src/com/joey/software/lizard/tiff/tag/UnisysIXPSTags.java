package com.joey.software.lizard.tiff.tag;

public class UnisysIXPSTags
{

	public static final int VERSION = 1; /* Version */

	public static final int SIDI = 2; /* SIDI */

	public static final int DATE = 10; /* Processing Date */

	public static final int CDLINE = 11; /* cdline and amt id */

	public static final int AMOUNT = 12; /* Amount */

	public static final int DIN = 101; /* DIN */

	public static final int SERIAL = 102; /* Check Serial Number */

	public static final int ACCOUNT = 516; /* Account Number */

	public static final int AUXONUS = 517; /* Aux on Us */

	public static final int POS44 = 518; /* Position 44 */

	public static final int TRANSIT = 519; /* Routing and Transit */

	public static final int TRANCODE = 520; /* TranCode */

	public static final int USER = 32000; /* IXPS User Area */

	int id;

	public UnisysIXPSTags(int i)
	{
		id = i;
	}

	@Override
	public String toString()
	{
		String sz;
		switch (id)
		{
			case VERSION:
				sz = "Version             ";
				break;
			case SIDI:
				sz = "SIDI                ";
				break;
			case CDLINE:
				sz = "cdline and amt id   ";
				break;
			case DIN:
				sz = "DIN                 ";
				break;
			case SERIAL:
				sz = "Check Serial Number ";
				break;
			case DATE:
				sz = "Processing Date     ";
				break;
			case AMOUNT:
				sz = "Check Amount        ";
				break;
			case TRANCODE:
				sz = "Transaction Code    ";
				break;
			case ACCOUNT:
				sz = "Account Number      ";
				break;
			case TRANSIT:
				sz = "Transit Transit     ";
				break;
			case AUXONUS:
				sz = "Auxiliary On Us     ";
				break;
			case POS44:
				sz = "Position 44         ";
				break;
			case USER:
				sz = "User Area           ";
				break;
			default:
				sz = "Unknown Unisys IXPS tag: " + id;
				break;
		}
		return sz;
	}

}
