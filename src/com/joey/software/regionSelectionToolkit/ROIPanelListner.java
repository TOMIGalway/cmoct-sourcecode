package com.joey.software.regionSelectionToolkit;

import java.awt.Shape;

public interface ROIPanelListner
{
	public void regionChanged();

	public void regionAdded(Shape region);

	public void regionRemoved(Shape region);
}
