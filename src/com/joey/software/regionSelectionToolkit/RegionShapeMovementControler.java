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
package com.joey.software.regionSelectionToolkit;

import com.joey.software.movementToolkit.MovementPanel;
import com.joey.software.movementToolkit.ShapeMovementControler;

public class RegionShapeMovementControler extends ShapeMovementControler
{
	ROIControlPanel panel;

	public RegionShapeMovementControler(ROIControlPanel panel)
	{
		this.panel = panel;
	}

	@Override
	public void moveDownPressed(MovementPanel owner)
	{
		// TODO Auto-generated method stub
		super.moveDownPressed(owner);
		panel.getPanel().shapeChanged();
	}

	@Override
	public void moveLeftPressed(MovementPanel owner)
	{
		// TODO Auto-generated method stub
		super.moveLeftPressed(owner);
		panel.getPanel().shapeChanged();
	}

	@Override
	public void moveRightPressed(MovementPanel owner)
	{
		// TODO Auto-generated method stub
		super.moveRightPressed(owner);
		panel.getPanel().shapeChanged();
	}

	@Override
	public void moveUpPressed(MovementPanel owner)
	{
		// TODO Auto-generated method stub
		super.moveUpPressed(owner);
		panel.getPanel().shapeChanged();
	}

	@Override
	public void scaleDownPressed(MovementPanel owner)
	{
		// TODO Auto-generated method stub
		super.scaleDownPressed(owner);
		panel.getPanel().shapeChanged();
	}

	@Override
	public void scaleUpPressed(MovementPanel owner)
	{
		// TODO Auto-generated method stub
		super.scaleUpPressed(owner);
		panel.getPanel().shapeChanged();
	}

	@Override
	public void removePressed(MovementPanel owner)
	{
		super.removePressed(owner);
		panel.removeCurrent();
		panel.getPanel().shapeChanged();
	}

}
