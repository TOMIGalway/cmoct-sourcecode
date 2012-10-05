package com.joey.software.framesToolkit;

import java.io.File;

public interface DropTextFieldListner 
{
	public boolean acceptFile(File f);
	public File getSuitableFile(File f);
}
