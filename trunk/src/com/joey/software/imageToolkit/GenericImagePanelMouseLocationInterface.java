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
package com.joey.software.imageToolkit;

import java.awt.Color;
import java.awt.Point;

public class GenericImagePanelMouseLocationInterface implements ImagePanelMouseLocationInterface
{
	@Override
	public void updateMouseLocationSting(ImagePanel panel, Point p)
	{
		if(panel.image == null)
		{
			return;
		}
		if (p.x >= 0 && p.y >= 0 && p.x < panel.image.getWidth()
				&& p.y < panel.image.getHeight())
		{
			if (panel.useDimensions)
			{
				float posX = p.x / (float) panel.image.getWidth();
				float posY = p.y / (float) panel.image.getHeight();

				double oldValueX = panel.imageDimensionWide.getValue();
				double oldValueY = panel.imageDimensionHigh.getValue();

				panel.imageDimensionWide.setValue(oldValueX * posX, false);
				panel.imageDimensionHigh.setValue(oldValueY * posY, false);

				panel.mouseLocationData.setText(p.x + " "
						+ panel.imageDimensionWide.getPrefixCode() + ", " + p.y + " "
						+ panel.imageDimensionHigh.getPrefixCode());

				panel.imageDimensionWide.setValue(oldValueX, false);
				panel.imageDimensionHigh.setValue(oldValueY, false);

			} else
			{
				panel.mouseLocationData.setText(p.x + " , " + p.y);
			}
			
			if (panel.isShowRGBValueOnMouseMove())
			{
				
				Color c = new Color(panel.getImage().getRGB(p.x, p.y));
				panel.rgbValue.setText("["+c.getAlpha()+"," + c.getRed() + "," + c.getGreen() + ","
						+ c.getBlue() + "]");
			}
		} else
		{
			panel.mouseLocationData.setText("- , -");
			if (panel.isShowRGBValueOnMouseMove())
			{
				panel.rgbValue.setText("[-,-,-,-]");
			}
		}
	}
}
