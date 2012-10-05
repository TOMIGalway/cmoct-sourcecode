package com.joey.software.Tools;

import com.joey.software.imageToolkit.DynamicRangeImage;

public interface FRG_ViewerListner
{

	public void AScanChanged(DynamicRangeImage src, float[] dataF, int marker);
}
