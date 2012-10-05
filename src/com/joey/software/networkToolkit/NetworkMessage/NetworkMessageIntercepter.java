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
