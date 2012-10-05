package com.joey.software.realTimeCompiler.test;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class LimitedLinesDocument extends DefaultStyledDocument
{
	private static final String EOL = "\n";

	private int maxLines;

	public LimitedLinesDocument(int maxLines)
	{
		this.maxLines = maxLines;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet attribute)
			throws BadLocationException
	{
		if (!EOL.equals(str)
				|| occurs(getText(0, getLength()), EOL) < maxLines - 1)
		{
			super.insertString(offs, str, attribute);
		}
	}

	public static int occurs(String str, String subStr)
	{
		int occurrences = 0;
		int fromIndex = 0;

		while (fromIndex > -1)
		{
			fromIndex = str.indexOf(subStr, occurrences == 0 ? fromIndex
					: fromIndex + subStr.length());
			if (fromIndex > -1)
			{
				occurrences++;
			}
		}

		return occurrences;
	}
}
