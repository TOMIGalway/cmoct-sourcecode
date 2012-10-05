package com.joey.software.testing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.networkToolkit.ClientConnection;
import com.joey.software.networkToolkit.ClientServer;
import com.joey.software.networkToolkit.ClientServerInterface;
import com.joey.software.networkToolkit.ClientServerJPanel;
import com.joey.software.networkToolkit.SocketConnection;
import com.joey.software.networkToolkit.SocketConnectionJPanel;


public class NetworkTest
{
	public static void main(String input[]) throws IOException
	{
		// Create the server
		int serverPort = 5000;
		int maxClients = 100;
		JFrame f = testServerApplication(serverPort, maxClients);
		connectToServer(serverPort);
	}

	public static JFrame testServerApplication(int serverPort, int maxClients)
			throws IOException
	{

		ClientServer server = createServer(serverPort, maxClients);
		ClientServerJPanel serverControlPanel = new ClientServerJPanel(server);
		ClientServerClientsPanel serverClientPanel = new ClientServerClientsPanel(
				server);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(serverControlPanel, BorderLayout.WEST);
		mainPanel.add(serverClientPanel, BorderLayout.CENTER);
		return FrameFactroy.getFrame(false, mainPanel);
	}

	public static void connectToServer(int port)
	{
		while (true)
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try
			{
				Socket socket = new Socket("localhost", port);
				SocketConnection connection = new SocketConnection(socket);
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	public static ClientServer createServer(int serverPort, int maxClients)
			throws IOException
	{
		ClientServer server = new ClientServer(serverPort, maxClients);
		return server;
	}

}

class ClientServerClientsPanel extends JPanel implements ClientServerInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1820253508087549881L;

	ClientServer server;

	Vector<SocketConnectionJPanel> panels = new Vector<SocketConnectionJPanel>();

	JPanel clientPanel = new JPanel();

	JPanel mainPanel = new JPanel();

	public ClientServerClientsPanel(ClientServer server)
	{
		setServer(server);
		createPanel();
		clientsChanged();
	}

	public void createPanel()
	{
		mainPanel = new JPanel(new BorderLayout());
		clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tmp.add(clientPanel);
		JScrollPane scroll = new JScrollPane(tmp,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.add(scroll, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
	}

	public synchronized void updatePanelsData()
	{
		panels.clear();
		Iterator clients = server.getClients().iterator();
		while (clients.hasNext())
		{

			SocketConnectionJPanel panel = new SocketConnectionJPanel(
					((ClientConnection) clients.next()).getSocketConnection());
			panel.setBorder(BorderFactory
					.createTitledBorder("Client Connection"));
			panels.add(panel);
		}

	}

	public synchronized void updatePanels()
	{

		clientPanel.removeAll();
		for (SocketConnectionJPanel panel : panels)
		{
			clientPanel.add(panel);
		}
		mainPanel.validate();
	}

	@Override
	public synchronized void clientsChanged()
	{
		updatePanelsData();
		updatePanels();
	}

	@Override
	public void serverStarted()
	{
	}

	@Override
	public void serverStoped()
	{
	}

	@Override
	public void settingChanged()
	{
	}

	private ClientServer getServer()
	{
		return server;
	}

	private void setServer(ClientServer server)
	{
		this.server = server;
		server.addClientServerInterface(this);
	}

}
