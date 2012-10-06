/*******************************************************************************
 * This is a class to dynamically load teh correct (currently for windows only)
 * version of java3d dlls 
 * 
 * Copyright (c) 2012 joey.enfield.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     joey.enfield - initial API and implementation
 ******************************************************************************/
package com.joey.software.j3dTookit;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public class J3DDynamicLibLoader {

	private final static String libDir="./dll/";
	
	private J3DDynamicLibLoader(){		
	}
	
	private static String getLibPath(){
		boolean x64 = System.getProperty("sun.arch.data.model").toString().contains("64");
		if(x64){
			return new File(libDir+"x64/").getAbsolutePath();
		}else{
			return new File(libDir+"x32/").getAbsolutePath();
		}
	}
	
	public static void loadDlls() throws Exception{
		String path = getLibPath();
		System.out.println(path);
		addLibraryPath(path);
	}
	/**
	* Adds the specified path to the java library path
	*
	* @param pathToAdd the path to add
	* @throws Exception
	*/
	private static void addLibraryPath(String pathToAdd) throws Exception{
	    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
	    usrPathsField.setAccessible(true);
	 
	    //get array of paths
	    final String[] paths = (String[])usrPathsField.get(null);
	 
	    //check if the path to add is already present
	    for(String path : paths) {
	        if(path.equals(pathToAdd)) {
	            return;
	        }
	    }
	 
	    //add the new path
	    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
	    newPaths[newPaths.length-1] = pathToAdd;
	    usrPathsField.set(null, newPaths);
	    System.out.println(newPaths);
	}
	
	public static void main(String input[]){
		
	}
}
