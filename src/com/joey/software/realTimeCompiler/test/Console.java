package com.joey.software.realTimeCompiler.test;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Console extends JPanel
{
	PipedInputStream piOut;

	PipedInputStream piErr;

	PipedOutputStream poOut;

	PipedOutputStream poErr;

	JTextArea textArea = new JTextArea();

	public Console() 
	{
		// Set up System.out
		piOut = new PipedInputStream();
		try
		{
			poOut = new PipedOutputStream(piOut);
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.setOut(new PrintStream(poOut, true));

		// Set up System.err
		piErr = new PipedInputStream();
		try
		{
			poErr = new PipedOutputStream(piErr);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.setErr(new PrintStream(poErr, true));

		// Add a scrolling text area
		textArea.setEditable(false);
		setLayout(new BorderLayout());
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		

		// Create reader threads
		new ReaderThread(piOut).start();
		new ReaderThread(piErr).start();
	}

	class ReaderThread extends Thread
	{
		PipedInputStream pi;

		ReaderThread(PipedInputStream pi)
		{
			this.pi = pi;
		}

		@Override
		public void run()
		{
			final byte[] buf = new byte[1024];
			try
			{
				while (true)
				{
					final int len = pi.read(buf);
					if (len == -1)
					{
						break;
					}
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							textArea.append(new String(buf, 0, len));

							// Make sure the last
							// line is always
							// visible
							textArea.setCaretPosition(textArea.getDocument()
									.getLength());

							// Keep the text area
							// down to a certain
							// character size
							int idealSize = 10000;
							int maxExcess = 5000;
							int excess = textArea.getDocument().getLength()
									- idealSize;
							if (excess >= maxExcess)
							{
								textArea.replaceRange("", 0, excess);
							}
						}
					});
				}
			} catch (IOException e)
			{
			}
		}
	}
}
