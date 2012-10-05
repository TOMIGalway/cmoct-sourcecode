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
package com.joey.software.ErrorReporting;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorReport
{
	public static final int ERROR_LEVEL_DETAILED = 0;

	public static final int ERROR_LEVEL_BASIC = 1;

	private static int errorLevel = ERROR_LEVEL_DETAILED;

	public static void reportError(Throwable e)
	{
		reportError(null, e);
	}

	public static void reportError(Component parent, Throwable e)
	{
		if (e instanceof OutOfMemoryError)
		{
			reportOutOfMemoryError(parent, (OutOfMemoryError) e);
		}
		else
		{
			reportGenericError(parent, e);
		}
	}

	public static void reportGenericError(Component parent, Throwable e)
	{
		JOptionPane.showMessageDialog(parent, new JScrollPane(new JTextArea(StackTraceUtil.getStackTrace(e))), "Error!", JOptionPane.ERROR_MESSAGE);
	}
	public static void reportOutOfMemoryError(Component parent,OutOfMemoryError e)
	{
		if(errorLevel == ERROR_LEVEL_DETAILED)
		{
			JOptionPane.showMessageDialog(parent, new JScrollPane(new JTextArea(StackTraceUtil.getStackTrace(e))), "Out of Memory!", JOptionPane.ERROR_MESSAGE);
		}
	}
}
