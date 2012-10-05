package com.joey.software.clipboardToolkit;

import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;

import com.joey.software.drawingToolkit.RobotPrintScreen;
import com.joey.software.framesToolkit.FrameFactroy;


public final class ClipboardToolkit implements ClipboardOwner
{

	public static void main(String... aArguments) throws AWTException
	{
		JButton action = new JButton("GRAB");
		action.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					saveScreenShotToClipboard();
				} catch (AWTException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		FrameFactroy.getFrame(action);

	}

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	@Override
	public void lostOwnership(Clipboard aClipboard, Transferable aContents)
	{
		// do nothing
	}

	public static void saveScreenShotToClipboard() throws AWTException
	{
		final BufferedImage image = RobotPrintScreen.getScreenShot();
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new Transferable()
				{
					@Override
					public DataFlavor[] getTransferDataFlavors()
					{
						return new DataFlavor[]
						{ DataFlavor.imageFlavor };
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor flavor)
					{
						return DataFlavor.imageFlavor
								.equals(DataFlavor.imageFlavor);
					}

					@Override
					public Object getTransferData(DataFlavor flavor)
							throws UnsupportedFlavorException, IOException
					{
						if (!DataFlavor.imageFlavor.equals(flavor))
						{
							throw new UnsupportedFlavorException(flavor);
						}
						return image;
					}
				}, null);

	}

	/**
	 * Place a String on the clipboard, and make this class the owner of the
	 * Clipboard's contents.
	 */
	public void setClipboardContents(String aString)
	{
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	public static void sendTextToClipboard(String data)
	{
		ClipboardToolkit tra = new ClipboardToolkit();
		tra.setClipboardContents(data);
	}

	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty
	 *         String.
	 */
	public String getClipboardContents()
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText)
		{
			try
			{
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex)
			{
				// highly unlikely since we are using a standard DataFlavor
				System.out.println(ex);
				ex.printStackTrace();
			} catch (IOException ex)
			{
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}
}
