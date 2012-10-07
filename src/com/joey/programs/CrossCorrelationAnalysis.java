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

import com.joey.software.Launcher.MainLauncher;
import com.joey.software.MultiThreadCrossCorrelation.CrossCorrProgram;
import com.joey.software.j3dTookit.J3DDynamicLibLoader;

public class CrossCorrelationAnalysis {
	public static void main(String input[]) throws Exception{

		/*
		 * If you keep getting out of memory errors add
		 * 
		 * Go to the menu : Run->Run Configurations
		 * Click Arguments tab and in VM add
		 * -Xmn100M -Xms500M -Xmx1024M (Add more to XMX if you have enough RAM)
		 * 
		 */
		
		
		/**
		 * This is a nice piece of code to automaically load the correct
		 * library (x32 or x64) as required. 
		 * 
		 * This should be added to the start of any program that uses Java3d
		 */
		J3DDynamicLibLoader.loadDlls();
		
		//This sets the look of feels
		try{
			MainLauncher.setLAF();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//I didnt want to rewite the main class so i just call it from here. 
		CrossCorrProgram.main(input);
	}
}
