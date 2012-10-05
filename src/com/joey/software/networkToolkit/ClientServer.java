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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import com.joey.software.networkToolkit.NetworkMessage.NetworkMessageSorter;


public class ClientServer implements Runnable
{
	/**
	 * This is the message sorter for all message recieved by the server
	 */
	NetworkMessageSorter messageSorter;

	/**
	 * Stores all the client connections
	 */
	Vector<ClientConnection> clients = new Vector<ClientConnection>();

	/**
	 * This is the
	 */
	Vector<ClientServerInterface> clientServerInterfaces = new Vector<ClientServerInterface>();

	/**
	 * Stores the server name
	 */

	String serverName = "Server";

	/**
	 * This flags if the server is curently accepting connections
	 */
	boolean acceptingConnections = true;

	/**
	 * This flags if the server is still alive.
	 */
	boolean serverAlive = true;

	/**
	 * This is the serversocket for the server
	 */
	ServerSocket server;

	/**
	 * This is the threadgroup for all the clientConnections
	 */
	ThreadGroup serverThreadGroup = new ThreadGroup(
			"Server Client Connections Threads");

	/**
	 * This is the owner thread of the server
	 */
	Thread ownerThread;

	/**
	 * This is the port number that the server is accepting connections on
	 */
	int serverPort = 5000;

	/**
	 * This is the number of connection attempts since the server was started
	 */
	int numConnectionAttemps = 0;

	/**
	 * This is the maxium number of connections that the server will accecpt
	 */
	int maxConnections = 10;

	/**
	 * This is the system time that the server was started at
	 */
	long serverStartTime = -1;

	/**
	 * This will create a new client server. It will NOT start the server
	 * running however, That should be done by calling the startServer()
	 * function.
	 * 
	 * @param serverPort
	 * @param maxConnections
	 * @param sorter
	 * @throws IOException
	 */
	public ClientServer(int serverPort, int maxConnections, NetworkMessageSorter sorter, String serverName)
			throws IOException
	{
		super();
		setMessageSorter(sorter);
		setMaxConnections(maxConnections);
		setServerPort(serverPort);
		setServerName(serverName);
		setAcceptingConnections(false);
		setServerAlive(false);
	}

	/**
	 * This will create a new client server. It will NOT start the server
	 * running however, That should be done by calling the startServer()
	 * function.
	 * 
	 * @param serverPort
	 * @param maxConnections
	 * @throws IOException
	 */
	public ClientServer(int serverPort, int maxConnections) throws IOException
	{
		this(serverPort, maxConnections, new NetworkMessageSorter(),
				"Client Server");
	}

	/**
	 * This will start the server running. It will create a new socket server
	 * and a new owner thread of the server. It will also reset the number of
	 * connections to the server and also set the start time of the server to
	 * the current time
	 * 
	 * @throws IOException
	 */
	public synchronized void startServer() throws IOException
	{
		// Create the socket server
		server = new ServerSocket(getServerPort());
		server.setReuseAddress(true);

		// Set accepting connections
		setAcceptingConnections(true);

		// Set server as alive
		setServerAlive(true);

		// Create the control Thread
		ownerThread = new Thread(this);
		ownerThread.start();

		// Reset number connection attemps
		setNumConnectionAttemps(0);

		// Set the start time
		setServerStartTime(System.currentTimeMillis());
		notifyServerStarted();
	}

	/**
	 * This will attempt to stop the server running. It frist stops the server
	 * from acceping new clients. It then sets the server ns not alive. It then
	 * creates a connection to the socketServer. this will cause the run method
	 * to eixt. It then closes all open connections and the closes the socket
	 * server
	 * 
	 * @throws IOException
	 */
	public synchronized void stopServer() throws IOException
	{

		// First stop the server from accepting connections
		setAcceptingConnections(false);
		setServerAlive(false);

		// Remove all the connected Clients

		closeAllConnections();

		// Stop the socket server
		getSocketServer().close();
		notifyServerStoped();
	}

	/**
	 * This will send a message to the client with the given index (the index is
	 * the index of the vector that the client is located)
	 * 
	 * @param index
	 * @param message
	 */
	public void sendMessageToClient(int index, String message)
	{
		synchronized (clients)
		{
			clients.get(index).getSocketConnection().sendData(message);
		}
	}

	/**
	 * This will send a messge to all the clients that are currently connected
	 * 
	 * @param message
	 */
	public void sendMessageToAllClients(String message)
	{
		synchronized (clients)
		{
			for (ClientConnection client : clients)
			{
				client.getSocketConnection().sendData(message);
			}
		}
	}

	/**
	 * This will check to see if the server is current full.
	 * 
	 * @return
	 */
	public boolean isServerFull()
	{
		return clients.size() > maxConnections;
	}

	/**
	 * This will close all open clients.
	 * 
	 * @throws IOException
	 */
	public void closeAllConnections() throws IOException
	{
		synchronized (clients)
		{
			for (ClientConnection client : clients)
			{
				client.closeConnection();
			}
			notifyClientsChanged();
		}
	}

	/**
	 * 
	 * @param client
	 */
	public void addClient(ClientConnection client)
	{
		synchronized (clients)
		{
			clients.add(client);
			notifyClientsChanged();
		}
	}

	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	public void removeClient(int index) throws IOException
	{
		synchronized (clients)
		{
			removeClient(clients.get(index));
		}
	}

	/**
	 * 
	 * @param c
	 * @return
	 * @throws IOException
	 */
	public void removeClient(ClientConnection c) throws IOException
	{
		synchronized (clients)
		{
			c.closeConnection();
			clients.remove(c);
			notifyClientsChanged();
		}
	}

	/**
	 * 
	 */
	@Override
	public void run()
	{
		while (serverAlive)
		{
			try
			{
				// Get a connection

				Socket clientSocket = server.accept();
				increaseNumConnectionAttemps();
				clientSocket.setReuseAddress(true);
				clientSocket.setKeepAlive(false);

				// Attempt to add client
				if (isServerFull())
				{// If server is full notify the client

					SocketConnection connection = new SocketConnection(
							clientSocket);
					connection.sendData("\\pServer is currently Full");
					connection.closeConnection();
				} else if (isAcceptingConnections())
				{// Check if the server is accepting connecctinos

					ClientConnection client = new ClientConnection(this,
							clientSocket);
					addClient(client);

				} else
				{// IF not accepting clients tell the client
					SocketConnection connection = new SocketConnection(
							clientSocket);
					connection
							.sendData("\\PServer is currently not accepting connections");
					connection.closeConnection();
				}
				notifySettingChanged();
			} catch (IOException e)
			{
				if (isServerAlive())
				{
					e.printStackTrace();
				}
			}
		}

		// Try and close any remainging connectinos
		try
		{
			closeAllConnections();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAcceptingConnections()
	{
		return acceptingConnections;
	}

	/**
	 * 
	 * @return
	 */
	public Vector<ClientConnection> getClients()
	{
		return clients;
	}

	/**
	 * 
	 * @return
	 */
	public NetworkMessageSorter getMessageSorter()
	{
		return messageSorter;
	}

	/**
	 * 
	 * @return
	 */
	public String getServerName()
	{
		return serverName;
	}

	/**
	 * 
	 * @param acceptingConnections
	 */
	public void setAcceptingConnections(boolean acceptingConnections)
	{
		this.acceptingConnections = acceptingConnections;
		notifySettingChanged();
	}

	/**
	 * 
	 * @param messageSorter
	 */
	public void setMessageSorter(NetworkMessageSorter messageSorter)
	{
		this.messageSorter = messageSorter;
	}

	/**
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName)
	{
		this.serverName = serverName;
		notifySettingChanged();
	}

	/**
	 * 
	 * @return
	 */
	private ServerSocket getSocketServer()
	{
		return server;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isServerAlive()
	{
		return serverAlive;
	}

	/**
	 * 
	 * @param serverAlive
	 */
	private void setServerAlive(boolean serverAlive)
	{
		this.serverAlive = serverAlive;
		notifySettingChanged();
	}

	/**
	 * 
	 * @return
	 */
	public ThreadGroup getServerThreadGroup()
	{
		return serverThreadGroup;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxConnections()
	{
		return maxConnections;
	}

	/**
	 * 
	 * @param maxConnections
	 */
	public void setMaxConnections(int maxConnections)
	{
		this.maxConnections = maxConnections;
		notifySettingChanged();
	}

	/**
	 * 
	 * 
	 */
	private void increaseNumConnectionAttemps()
	{
		numConnectionAttemps++;
		notifySettingChanged();
	}

	/**
	 * 
	 * @return
	 */
	public int getServerPort()
	{
		return serverPort;
	}

	/**
	 * 
	 * @param serverPort
	 */
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
		notifySettingChanged();
	}

	/**
	 * 
	 * @return
	 */
	public int getNumConnectionAttemps()
	{
		return numConnectionAttemps;
	}

	/**
	 * 
	 * @param numConnectionAttemps
	 */
	private void setNumConnectionAttemps(int numConnectionAttemps)
	{
		this.numConnectionAttemps = numConnectionAttemps;
		notifySettingChanged();
	}

	/**
	 * 
	 * @return
	 */
	public long getServerStartTime()
	{
		return serverStartTime;
	}

	/**
	 * 
	 * @param serverStartTime
	 */
	public void setServerStartTime(long serverStartTime)
	{
		this.serverStartTime = serverStartTime;
		notifySettingChanged();
	}

	/**
	 * This add another client server interface to the client server.
	 * 
	 * @param clientServerInterface
	 */
	public void addClientServerInterface(ClientServerInterface clientServerInterface)
	{
		this.clientServerInterfaces.add(clientServerInterface);
	}

	public void removeClientServerInterface(ClientServerInterface clientServerInterface)
	{
		this.clientServerInterfaces.remove(clientServerInterface);
	}

	private void notifyClientsChanged()
	{
		for (ClientServerInterface i : clientServerInterfaces)
		{
			i.clientsChanged();
		}
	}

	private void notifySettingChanged()
	{
		for (ClientServerInterface i : clientServerInterfaces)
		{
			i.settingChanged();
		}
	}

	private void notifyServerStoped()
	{
		for (ClientServerInterface i : clientServerInterfaces)
		{
			i.serverStoped();
		}
	}

	private void notifyServerStarted()
	{
		for (ClientServerInterface i : clientServerInterfaces)
		{
			i.serverStarted();
		}
	}
}
