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
package com.joey.software.movementToolkit;

public interface MovementPanelInterface
{
	public void moveUpPressed(MovementPanel owner);

	public void moveDownPressed(MovementPanel owner);

	public void moveLeftPressed(MovementPanel owner);

	public void moveRightPressed(MovementPanel owner);

	public void removePressed(MovementPanel owner);

	public void scaleUpPressed(MovementPanel owner);

	public void scaleDownPressed(MovementPanel owner);
}
