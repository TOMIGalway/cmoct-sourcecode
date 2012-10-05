package com.joey.software.fileToolkit.dragAndDrop;

import javax.swing.JFrame;

/**
 * A simple example showing how to use {@link FileDrop}
 * 
 * @author Robert Harder, rob@iharder.net
 */
public class Example
{

	/** Runs a sample program that shows dropped files */
	public static void main(String[] args)
	{
		System.out.println("HEre");
		javax.swing.JFrame frame = new javax.swing.JFrame("FileDrop");
		// javax.swing.border.TitledBorder dragBorder = new
		// javax.swing.border.TitledBorder( "Drop 'em" );
		final javax.swing.JTextArea text = new javax.swing.JTextArea();
		frame
				.getContentPane()
				.add(new javax.swing.JScrollPane(text), java.awt.BorderLayout.CENTER);

		new FileDrop(System.out, text, /* dragBorder, */
		new FileDrop.Listener()
		{
			@Override
			public void filesDropped(java.io.File[] files)
			{
				for (int i = 0; i < files.length; i++)
				{
					try
					{
						text.append(files[i].getCanonicalPath() + "\n");
					} // end try
					catch (java.io.IOException e)
					{
					}
				} // end for: through each dropped file
			} // end filesDropped
		}); // end FileDrop.Listener

		System.out.println("hELLO");
		frame.setBounds(100, 100, 300, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	} // end main

}
