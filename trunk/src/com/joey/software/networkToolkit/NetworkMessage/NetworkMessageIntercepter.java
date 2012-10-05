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
package com.joey.software.networkToolkit.NetworkMessage;

public class NetworkMessageIntercepter extends NetworkMessageDestination
{

	public NetworkMessageIntercepter(NetworkMessageDestinationInterface messageDestination)
	{
		super(messageDestination);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isDestination(String message)
	{
		// TODO Auto-generated method stub
		return true;
	}

}
