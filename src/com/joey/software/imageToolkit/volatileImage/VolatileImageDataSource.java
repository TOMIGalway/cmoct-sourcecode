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
package com.joey.software.imageToolkit.volatileImage;

import java.awt.image.VolatileImage;

/**
 * This is a interface that can be used for volatile images. It is usefull for
 * storeing the volatile image data for when the content of the volatile image
 * is lost
 * 
 * @author joey.enfield
 * 
 */
public interface VolatileImageDataSource
{
	public abstract VolatileImage getImageData();
}
