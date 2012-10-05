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
public abstract class NetworkMessageDestination
{
	NetworkMessageDestinationInterface messageDestination;

	public NetworkMessageDestination(NetworkMessageDestinationInterface messageDestination)
	{
		setMessageDestination(messageDestination);
	}

	/**
	 * 
	 * @param message
	 */
	public void messageRecieved(String message)
	{
		messageDestination.messageRecieved(message);
	}

	public abstract boolean isDestination(String message);

	/**
	 * @return the messageDestination
	 */
	private NetworkMessageDestinationInterface getMessageDestination()
	{
		return messageDestination;
	}

	/**
	 * @param messageDestination
	 *            the messageDestination to set
	 */
	private void setMessageDestination(NetworkMessageDestinationInterface messageDestination)
	{
		this.messageDestination = messageDestination;
	}

}
