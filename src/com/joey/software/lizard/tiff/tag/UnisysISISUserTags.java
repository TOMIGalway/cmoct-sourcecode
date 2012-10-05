package com.joey.software.lizard.tiff.tag;

public class UnisysISISUserTags
{
	public static final int USERID = 0; /* User ID */

	public static final int SITEID = 1; /* Site ID */

	public static final int START = 2; /* Capture Start */

	public static final int PID = 3; /* Capture PID */

	public static final int DOCID = 4; /* Document Num */

	public static final int MIGRATION = 5; /* Migration Date */

	public static final int MICROFILM = 6; /* Micro Film Seq */

	public static final int ITEMSEQ = 7; /* Item Seq */

	public static final int TRACER = 8; /* Tracer Number */

	public static final int SIDEID = 9; /* Side ID */// 0=front 1=back

	public static final int ZONELIST = 10; /* Zone List */

	public static final int NEXTIFD = 11; /* Next Level 1 IFD offset */

	int id;

	public UnisysISISUserTags(int i)
	{
		id = i;
	}

	@Override
	public String toString()
	{
		String sz;
		switch (id)
		{
			case USERID:
				sz = "User ID";
				break;
			case SITEID:
				sz = "Site ID";
				break;
			case START:
				sz = "Capture Start";
				break;
			case PID:
				sz = "Capture PID";
				break;
			case DOCID:
				sz = "Document Num";
				break;
			case MIGRATION:
				sz = "Migration Date";
				break;
			case MICROFILM:
				sz = "Micro Film Seq";
				break;
			case ITEMSEQ:
				sz = "Item Seq";
				break;
			case TRACER:
				sz = "Tracer Number";
				break;
			case SIDEID:
				sz = "Side ID";
				break; // 0=front 1=back
			case ZONELIST:
				sz = "Zone List";
				break;
			case NEXTIFD:
				sz = "Next Level 1 IFD offset";
				break;
			default:
				sz = "Unknown Unisys ISIS User tag";
				break;
		}
		return sz;
	}
}
