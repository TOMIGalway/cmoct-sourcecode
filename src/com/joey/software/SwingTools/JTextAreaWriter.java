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
package com.joey.software.SwingTools;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.joey.software.framesToolkit.FrameFactroy;


public class JTextAreaWriter extends Writer
{
	StringBuilder currentLine = new StringBuilder();

	OutputStreamDirector outputStream = new OutputStreamDirector(this);

	JTextArea textArea = new JTextArea();
	{
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
	}

	public JTextArea getTextArea()
	{
		return textArea;
	}

	public OutputStream getOutputStream()
	{
		return outputStream;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException
	{
//		if(currentLine.length() != 0)
//		{
//			textArea.setText(textArea.getText().substring(currentLine.length()+1, textArea.getText().length()));
//		}
//		for (int i = off; i < len; i++)
//		{
//			if (cbuf[i] == '\n' || cbuf[i] == '\r')
//			{
//				textArea.setText(currentLine.toString() + cbuf[i]
//						+ textArea.getText());
//				currentLine.setLength(0);
//			} else
//			{
//				currentLine.append(cbuf[i]);
//			}
//		}
//		// TODO Auto-generated method stub
//		if (currentLine.length() != 0)
//		{
//			textArea.setText(currentLine + "\n" + textArea.getText());
//		}
		textArea.append(new String(cbuf, off, len));
	}

	@Override
	public void flush() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub

	}

	public static void main(String input[]) throws InterruptedException
	{
		JTextAreaWriter write = new JTextAreaWriter();
		System.setOut(new PrintStream(write.getOutputStream()));

		FrameFactroy.getFrame(new JScrollPane(write.getTextArea()));

		Thread.sleep(1000);
		System.out.println("Hello");
		System.out.println("Text");
	}
}

class OutputStreamDirector extends OutputStream
{
	JTextAreaWriter rst = null;

	public OutputStreamDirector(JTextAreaWriter write)
	{
		rst = write;
	}

	@Override
	public void write(int b) throws IOException
	{
		rst.append((char) b);
	}

}
