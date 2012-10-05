package com.joey.software.framesToolkit;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextfieldTools
{

	public static void setPreventCharacters(JTextField field, String string)
	{
		setPreventCharacters(field, string.toCharArray());
	}

	public static void setPreventCharacters(JTextField field, final char[] invalid)
	{
		PlainDocument doc = new PlainDocument()
		{
			char[] invalidChars = invalid;

			@Override
			public void insertString(int offs, String str, AttributeSet a)
			{
				try
				{
					if (isValid(str))
					{
						super.insertString(offs, str, a);
					} else
					{
						return;
					}
				} catch (BadLocationException e)
				{
					return;
				}
			}

			public boolean isValid(String str)
			{
				for (char c : invalidChars)
				{
					char[] data = str.toCharArray();
					for (char d : data)
					{
						if (c == d)
						{
							return false;
						}
					}
				}
				return true;
			}
		};

		field.setDocument(doc);
	}

	public static void main(String input[])
	{
		JTextField field = new JTextField(5);

		String data = "@!\\/";
		JTextfieldTools.setPreventCharacters(field, data.toCharArray());

		JPanel p = new JPanel();
		p.add(field);

		FrameFactroy.getFrame(p);

	}
}
