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
package com.joey.software.networkToolkit;

import java.io.IOException;
import java.net.Socket;

import com.joey.software.networkToolkit.NetworkMessage.NetworkMessageSorter;


public class ClientConnection implements SocketConnectionInterface
{
	SocketConnection socketConnection;

	String clientName = "Client";

	ClientServer server;

	public ClientConnection(ClientServer server, Socket socket, NetworkMessageSorter messageSorter)
			throws IOException
	{
		super();
		setServer(server);
		setSocketConnection(new SocketConnection(socket, messageSorter));
	}

	public ClientConnection(ClientServer server, Socket socket)
			throws IOException
	{
		this(server, socket, server.getMessageSorter());
	}

	public ClientConnection(ClientServer server, Socket socket, NetworkMessageSorter messageSorter, String clientName)
			throws IOException
	{
		this(server, socket, messageSorter);
		setClientName(clientName);
	}

	public ClientConnection(ClientServer server, Socket socket, String clientName)
			throws IOException
	{
		this(server, socket);
		setClientName(clientName);
	}

	public void closeConnection() throws IOException
	{
		socketConnection.closeConnection();
	}

	public String getClientName()
	{
		return clientName;
	}

	public ClientServer getServer()
	{
		return server;
	}

	public SocketConnection getSocketConnection()
	{
		return socketConnection;
	}

	public void setClientName(String clientName)
	{
		this.clientName = clientName;
	}

	@Override
	public synchronized void socketDisconnected()
	{
		try
		{
			server.removeClient(this);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socketConnection = null;

	}

	private void setServer(ClientServer server)
	{
		this.server = server;
	}

	private void setSocketConnection(SocketConnection socketConnection)
	{
		this.socketConnection = socketConnection;

		socketConnection.addSocketConnectionInterface(this);

		Thread clientThread = new Thread(server.getServerThreadGroup(),
				getSocketConnection(), "ClientConnection Thead of serverGroup"
						+ server.getServerThreadGroup().activeCount());
		clientThread.start();
	}

}
