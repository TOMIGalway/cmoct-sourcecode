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

/**
 * @description<BR> This is an abstract class that is used as the destination of
 *                  network message
 * @author Joey Enfield
 * 
 * @date 02/01/2007
 * 
 * @version 1.0
 * 
 * @versionHistory <BR>
 *                 <table>
 *                 <tr>
 *                 <td>1.0</td>
 *                 <td>Initial version</td>
 *                 </tr>
 *                 </table>
 */
public class NetworkTaggedMessageDestination extends NetworkMessageDestination
{
	/**
	 * This is the tag that all messages destined for the class must begin with
	 */
	String messageTag;

	public NetworkTaggedMessageDestination(String messageTag, NetworkMessageDestinationInterface messageDestination)
	{
		super(messageDestination);
		setMessageTag(messageTag);
	}

	public void setMessageTag(String tag)
	{
		this.messageTag = tag;
	}

	public String getMessageTag()
	{
		return messageTag;
	}

	@Override
	public void messageRecieved(String message)
	{
		super.messageRecieved(message.substring(getMessageTag().length()));
	}

	@Override
	public boolean isDestination(String message)
	{
		return message.startsWith(getMessageTag());
	}
}
