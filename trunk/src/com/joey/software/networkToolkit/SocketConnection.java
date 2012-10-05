package com.joey.software.networkToolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import com.joey.software.networkToolkit.NetworkMessage.NetworkMessageSorter;


public class SocketConnection implements Runnable
{
	Socket socket;

	boolean socketClosed = false;

	PrintWriter out;

	BufferedReader in;

	NetworkMessageSorter messageSorter;

	Vector<SocketConnectionInterface> socketClients = new Vector<SocketConnectionInterface>();

	public SocketConnection(Socket socket, NetworkMessageSorter messageSorter)
			throws IOException
	{
		setSocket(socket);
		setMessageSorter(messageSorter);
	}

	public SocketConnection(Socket socket) throws IOException
	{
		this(socket, new NetworkMessageSorter());
	}

	public NetworkMessageSorter getMessageSorter()
	{
		return messageSorter;
	}

	public void sendData(String data)
	{
		out.println(data);
		out.flush();
	}

	public Socket getSocket()
	{
		return socket;
	}

	public boolean isConnected()
	{
		return !socket.isClosed();
	}

	public void setSocket(Socket socket) throws IOException
	{
		this.socket = socket;
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void setMessageSorter(NetworkMessageSorter sorter)
	{
		this.messageSorter = sorter;
	}

	public void closeConnection() throws IOException
	{
		socket.close();
	}

	public void addSocketConnectionInterface(SocketConnectionInterface client)
	{
		socketClients.add(client);
	}

	public void removeSocketConnectionInterface(SocketConnectionInterface client)
	{
		socketClients.remove(client);
	}

	private synchronized void disconnectionDetected()
	{
		for (SocketConnectionInterface client : socketClients)
		{
			client.socketDisconnected();
		}
		// Clear all interfaces
		socketClients.clear();

		// Set all things to null
		this.in = null;
		this.out = null;
	}

	@Override
	public void run()
	{
		while (!socket.isClosed())
		{
			try
			{
				String input = in.readLine();
				if (input != null)
				{
					messageSorter.sortMessage(input);
				} else
				{
					socket.close();
				}
			} catch (IOException e)
			{
			}

		}
		disconnectionDetected();
	}
}
