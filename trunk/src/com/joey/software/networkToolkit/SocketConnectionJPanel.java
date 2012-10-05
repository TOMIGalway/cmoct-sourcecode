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

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.joey.software.framesToolkit.SwingToolkit;


public class SocketConnectionJPanel extends JPanel implements
		SocketConnectionInterface
{
	SocketConnection socketConnection;

	JLabel hostNameData = new JLabel();

	JLabel hostPortData = new JLabel();

	JLabel connectionStateData = new JLabel();

	JButton disconnect = new JButton("Disconnect");

	public SocketConnectionJPanel(SocketConnection socketConnection)
	{
		super();
		setSocketConnection(socketConnection);
		createJPanel();
		updateData();
	}

	public void createJPanel()
	{
		setLayout(new GridLayout(4, 1));

		add(SwingToolkit.getLabel(hostNameData, "Address : ", 60));
		add(SwingToolkit.getLabel(hostPortData, "Port : ", 60));
		add(SwingToolkit.getLabel(connectionStateData, "State : ", 60));
		add(disconnect);

	}

	public void updateData()
	{
		hostNameData.setText(socketConnection.getSocket().getInetAddress()
				.getHostAddress());
		hostPortData.setText(socketConnection.getSocket().getPort() + "");
		if (socketConnection.getSocket().isClosed())
		{
			connectionStateData.setText("Closed");
		} else
		{
			connectionStateData.setText("Open");
		}
		validate();
	}

	public SocketConnection getSocketConnection()
	{
		return socketConnection;
	}

	private void setSocketConnection(SocketConnection socketConnection)
	{
		this.socketConnection = socketConnection;
		socketConnection.addSocketConnectionInterface(this);
	}

	@Override
	public void socketDisconnected()
	{
		updateData();
	}
}
