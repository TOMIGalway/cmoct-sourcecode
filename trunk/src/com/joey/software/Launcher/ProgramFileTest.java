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
package com.joey.software.Launcher;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;

public class ProgramFileTest
{
	public static void main(String input[]) throws IOException,
			ClassNotFoundException, UnsupportedLookAndFeelException
	{
		SettingsLauncher.setActivePath(MainLauncher.class);

		LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();

		NimRODLookAndFeel lf = new NimRODLookAndFeel();

		UIManager.setLookAndFeel(lf);
		for (int i = 0; i < laf.length; i++)
		{
			System.out.println("LAF [" + i + "] : " + laf[i].getClassName());
		}
	}

}
