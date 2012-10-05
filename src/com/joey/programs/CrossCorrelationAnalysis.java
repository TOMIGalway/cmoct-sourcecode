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
package com.joey.programs;

import java.io.IOException;

import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.MultiThreadCrossCorrelation.CrossCorrProgram;

public class CrossCorrelationAnalysis {
	public static void main(String input[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, InterruptedException{
		CrossCorrProgram.main(input);
	}
}
