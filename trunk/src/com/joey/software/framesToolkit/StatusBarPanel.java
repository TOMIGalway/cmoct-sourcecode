package com.joey.software.framesToolkit;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class StatusBarPanel extends JPanel
{
	JProgressBar progressBar = new JProgressBar();

	JTextField textField = new JTextField();

	JTextArea errorArea = new JTextArea();

	public StatusBarPanel()
	{
		super(true);
		createPanel();
		reset();
	}

	public JPanel getErrorPanel()
	{
		JScrollPane scroll = new JScrollPane(errorArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scroll);
		return panel;
	}

	public void createPanel()
	{

		setLayout(new BorderLayout());
		add(textField, BorderLayout.CENTER);
		add(progressBar, BorderLayout.EAST);

		textField.setEditable(false);
	}

	public void setStatusMessage(String message)
	{
		textField.setText(message);
		repaint();
	}

	@Override
	public void repaint()
	{
		super.repaint();
//		Graphics g = getGraphics();
////		if (g != null)
////		{
////			g = g.create();
////		} else
////		{
////			return;
////		}
//		update(g);
	}

	public void setErrorMessage(String message)
	{
		errorArea.setText(message);
		repaint();
	}

	public void appendErrorMessage(String message)
	{
		errorArea.append("\n" + message);
		validate();
		update(getGraphics());
	}

	public void reset()
	{
		textField.setText("");
		errorArea.setText("");
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(100);
		repaint();
	}

	public int getMaximum()
	{
		return progressBar.getMaximum();
	}

	public int getMinimum()
	{
		return progressBar.getMinimum();
	}

	public int getValue()
	{
		return progressBar.getValue();
	}

	public void setMaximum(int n)
	{
		progressBar.setMaximum(n);

	}

	public void setMinimum(int n)
	{
		progressBar.setMinimum(n);
	}

	public void setValue(int n)
	{
		progressBar.setValue(n);
		repaint();
	}

	public static void main(String input[]) throws InterruptedException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		StatusBarPanel status = new StatusBarPanel();

		JTextArea text = new JTextArea();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(status, BorderLayout.SOUTH);
		mainPanel.add(text, BorderLayout.NORTH);

		FrameFactroy.getFrame(mainPanel);

		for (int i = 0; i < 100; i++)
		{
			status.setStatusMessage("Value is : " + i);
			status.setValue(i);
			Thread.sleep(400);
		}

	}

	public JTextArea getErrorArea()
	{
		return errorArea;
	}

	public JFrame showInFrame(String title)
	{
		JFrame f = new JFrame(title);
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(this);
		f.setSize(600,100);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		return f;
	}
}