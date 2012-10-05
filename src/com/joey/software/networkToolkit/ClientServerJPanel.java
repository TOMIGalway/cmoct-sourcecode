package com.joey.software.networkToolkit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ClientServerJPanel extends JPanel implements
		ClientServerInterface, ActionListener, ChangeListener
{
	ClientServer server;

	JTextField serverNameData = new JTextField(8);

	JLabel serverRunningData = new JLabel("A");

	JLabel serverStartTimeData = new JLabel("B");

	JLabel aceptingConnectionsData = new JLabel("C");

	JLabel numClientsData = new JLabel("D");

	JLabel connectionAttempsData = new JLabel("E");

	JSpinner serverPortData = new JSpinner(new SpinnerNumberModel(1300, 1,
			65535, 1));

	JSpinner maxClientsData = new JSpinner(new SpinnerNumberModel(100, 0,
			10000, 1));

	JButton runningButton = new JButton("Start Sever Running");

	JButton acceptConnectionButton = new JButton("Yes");

	JButton disconnectAllButton = new JButton("Disconnect All Clients");

	public ClientServerJPanel(ClientServer server)
	{
		setServer(server);
		createJPanel();
		updateSettingsData();
	}

	public void createJPanel()
	{
		/*
		 * Create all the labels
		 */
		JLabel serverNameLabel = new JLabel("Server Name:");
		JLabel serverRunningLabel = new JLabel("Current Running:");
		JLabel serverStartTimeLabel = new JLabel("Start Time:");
		JLabel aceptingConnectionsLabel = new JLabel("Accepting Connections:");
		JLabel numClientsLabel = new JLabel("Currently Connected:");
		JLabel connectionAttempsLabel = new JLabel("Connection Attemps:");

		JLabel serverPortLabel = new JLabel("Server Port:");
		JLabel maxClientsLabel = new JLabel("Max Clients:");

		/*
		 * Create the infor panels
		 */
		JPanel serverInfoDataPanel = new JPanel();
		JPanel serverInfoLabelsPanel = new JPanel();
		serverInfoLabelsPanel.setLayout(new BoxLayout(serverInfoLabelsPanel,
				BoxLayout.Y_AXIS));
		serverInfoDataPanel.setLayout(new BoxLayout(serverInfoDataPanel,
				BoxLayout.Y_AXIS));

		/**
		 * Add server name
		 */
		JPanel serverNameLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		serverNameLabelPanel.add(serverNameLabel);
		serverInfoLabelsPanel.add(serverNameLabelPanel);
		JPanel serverNameDataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		serverNameDataPanel.add(serverNameData);
		serverInfoDataPanel.add(serverNameDataPanel);

		/**
		 * Add the server Running info
		 */
		JPanel serverRunningLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		serverRunningLabelPanel.add(serverRunningLabel);
		serverInfoLabelsPanel.add(serverRunningLabelPanel);
		JPanel serverRunningDataPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		serverRunningDataPanel.add(serverRunningData);
		serverInfoDataPanel.add(serverRunningDataPanel);

		/**
		 * Add the server start time
		 */
		JPanel serverStartTimeLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		serverStartTimeLabelPanel.add(serverStartTimeLabel);
		serverInfoLabelsPanel.add(serverStartTimeLabelPanel);
		JPanel serverStartTimeDataPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		serverStartTimeDataPanel.add(serverStartTimeData);
		serverInfoDataPanel.add(serverStartTimeDataPanel);

		/**
		 * Add the server acepting connection info
		 */
		JPanel aceptingConnectionsLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		aceptingConnectionsLabelPanel.add(aceptingConnectionsLabel);
		serverInfoLabelsPanel.add(aceptingConnectionsLabelPanel);
		JPanel aceptingConnectionsDataPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		aceptingConnectionsDataPanel.add(aceptingConnectionsData);
		serverInfoDataPanel.add(aceptingConnectionsDataPanel);

		/**
		 * Add the num clients bit
		 */
		JPanel numClientsLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		numClientsLabelPanel.add(numClientsLabel);
		serverInfoLabelsPanel.add(numClientsLabelPanel);
		JPanel numClientsDataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numClientsDataPanel.add(numClientsData);
		serverInfoDataPanel.add(numClientsDataPanel);

		/**
		 * Add the num connections attemps
		 */
		JPanel connectionAttempsLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		connectionAttempsLabelPanel.add(connectionAttempsLabel);
		serverInfoLabelsPanel.add(connectionAttempsLabelPanel);
		JPanel connectionAttempsDataPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		connectionAttempsDataPanel.add(connectionAttempsData);
		serverInfoDataPanel.add(connectionAttempsDataPanel);

		/**
		 * Add the server port numer
		 */
		JPanel serverPortLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		serverPortLabelPanel.add(serverPortLabel);
		serverInfoLabelsPanel.add(serverPortLabelPanel);
		JPanel serverPortDataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		serverPortDataPanel.add(serverPortData);
		serverInfoDataPanel.add(serverPortDataPanel);

		/**
		 * max clients
		 */
		JPanel maxClientsLabelPanel = new JPanel(new FlowLayout(
				FlowLayout.RIGHT));
		maxClientsLabelPanel.add(maxClientsLabel);
		serverInfoLabelsPanel.add(maxClientsLabelPanel);
		JPanel maxClientsDataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxClientsDataPanel.add(maxClientsData);
		serverInfoDataPanel.add(maxClientsDataPanel);

		serverInfoLabelsPanel.add(new JPanel());
		serverInfoDataPanel.add(new JPanel());

		/*
		 * Create the info panel
		 */
		JPanel serverInfoPanel = new JPanel();
		serverInfoPanel.setLayout(new BorderLayout());

		serverInfoPanel.add(serverInfoLabelsPanel, BorderLayout.WEST);
		serverInfoPanel.add(serverInfoDataPanel, BorderLayout.CENTER);

		/*
		 * Create the cont rols panel
		 */
		JPanel controlButtonsPanel = new JPanel();
		controlButtonsPanel.setLayout(new GridLayout(3, 1));
		controlButtonsPanel.add(runningButton);
		controlButtonsPanel.add(acceptConnectionButton);
		controlButtonsPanel.add(disconnectAllButton);

		JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		temp.add(serverInfoPanel);
		JScrollPane scrollPane = new JScrollPane(temp,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		setLayout(new BorderLayout());

		add(scrollPane, BorderLayout.CENTER);
		add(controlButtonsPanel, BorderLayout.SOUTH);

		/**
		 * Regester all listners
		 */
		acceptConnectionButton.addActionListener(this);
		disconnectAllButton.addActionListener(this);
		runningButton.addActionListener(this);
		maxClientsData.addChangeListener(this);
		serverPortData.addChangeListener(this);
		serverNameData.addActionListener(this);

	}

	public static void main(String input[]) throws IOException
	{
		ClientServerJPanel panel = new ClientServerJPanel(new ClientServer(
				5000, 200));

		JFrame frame = new JFrame("Image test");
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel);

		frame.setVisible(true);
	}

	public void updateSettingsData()
	{

		if (server.isServerAlive())
		{
			serverRunningData.setText("Yes");
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy hh:mm");
			serverStartTimeData.setText(format.format(new Date(server
					.getServerStartTime())));
			serverPortData.setEnabled(false);
			serverNameData.setEnabled(false);
			runningButton.setText("Stop Server Running");
		} else
		{
			serverRunningData.setText("No");
			serverStartTimeData.setText("----------------");
			serverPortData.setEnabled(true);
			serverNameData.setEnabled(true);
			runningButton.setText("Start Server Running");
		}

		if (server.isAcceptingConnections())
		{
			aceptingConnectionsData.setText("Yes");
			acceptConnectionButton.setText("Stop Accepting Connections");
		} else
		{
			aceptingConnectionsData.setText("No");
			acceptConnectionButton.setText("Start Accepting Connections");
		}
		serverNameData.setText(server.getServerName());
		numClientsData.setText("" + server.getClients().size());
		connectionAttempsData.setText("" + server.getNumConnectionAttemps());

		serverPortData.setValue(server.getServerPort());
		maxClientsData.setValue(server.getMaxConnections());
	}

	public ClientServer getServer()
	{
		return server;
	}

	private void setServer(ClientServer server)
	{
		this.server = server;
		server.addClientServerInterface(this);
	}

	@Override
	public void clientsChanged()
	{
		settingChanged();
	}

	@Override
	public void serverStarted()
	{
		settingChanged();

	}

	@Override
	public void serverStoped()
	{
		settingChanged();

	}

	@Override
	public void settingChanged()
	{
		updateSettingsData();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == acceptConnectionButton)
		{
			server.setAcceptingConnections(!server.isAcceptingConnections()
					&& server.isServerAlive());
		} else if (e.getSource() == serverNameData)
		{
			server.setServerName(serverNameData.getText());
		} else if (e.getSource() == disconnectAllButton)
		{
			try
			{
				server.closeAllConnections();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == runningButton)
		{
			if (server.isServerAlive())
			{
				try
				{
					server.stopServer();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else
			{
				try
				{
					server.startServer();
				} catch (IOException e1)
				{
					if (e1 instanceof BindException)
					{
						JOptionPane
								.showMessageDialog(this, "Could not open server on the port", "Error Starting Server", JOptionPane.ERROR_MESSAGE);
					} else
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}

		updateSettingsData();
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == maxClientsData)
		{
			server.setMaxConnections((Integer) (maxClientsData.getValue()));
		} else if (e.getSource() == serverPortData)
		{
			server.setServerPort((Integer) serverPortData.getValue());
		}

	}

}
