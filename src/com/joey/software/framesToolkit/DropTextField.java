package com.joey.software.framesToolkit;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.joey.software.fileToolkit.dragAndDrop.FileDrop;


public class DropTextField extends JTextField
{
	FileDrop drop;

	// DropTarget target;
	//
	// public DropTextField()
	// {
	// target = new DropTarget(this, this);
	// }

	DropTextFieldListner field = null;

	public DropTextField()
	{
		new FileDrop(null, this, /* dragBorder, */
		new FileDrop.Listener()
		{

			@Override
			public void filesDropped(java.io.File[] files)
			{
				for (int i = 0; i < files.length; i++)
				{
					try
					{
						if (field != null)
						{
							if(field.acceptFile(files[i]))
							{
								setText(field.getSuitableFile(files[i]).getCanonicalPath());
							}
						}
						else
						{
							setText(files[i].getCanonicalPath());
						}
						return;
					} // end try
					catch (java.io.IOException e)
					{
					}
				} // end for: through each dropped
					// file
			} // end filesDropped
		})
		{
			
		}; // end FileDrop.Listener
	}

	public void addListner(DropTextFieldListner list)
	{
		field = list;
	}

	// public void dragEnter(DropTargetDragEvent
	// dtde)
	// {
	//
	// }
	//
	// public void dragExit(DropTargetEvent dte)
	// {
	//
	// }
	//
	// public void dragOver(DropTargetDragEvent
	// dtde)
	// {
	//
	// }
	//
	// public void
	// dropActionChanged(DropTargetDragEvent dtde)
	// {
	//
	// }
	//
	// public void drop(DropTargetDropEvent dtde)
	// {
	//
	// try
	// {
	// // Ok, get the dropped object and try to
	// figure out what it is
	// Transferable tr = dtde.getTransferable();
	// DataFlavor[] flavors =
	// tr.getTransferDataFlavors();
	// for (int i = 0; i < flavors.length; i++)
	// {
	// // Check for file lists specifically
	// if (flavors[i].isFlavorJavaFileListType())
	// {
	// // Great! Accept copy drops...
	// dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	//
	// // And add the list of file names to our
	// text area
	// java.util.List list = (java.util.List) tr
	// .getTransferData(flavors[i]);
	// if (list.size() > 0)
	// {
	//
	// setText(list.get(0).toString());
	// }
	//
	// // If we made it this far, everything
	// worked.
	// dtde.dropComplete(true);
	// return;
	// }
	// // Ok, is it another Java object?
	// else if
	// (flavors[i].isFlavorSerializedObjectType())
	// {
	// dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	//
	// Object o = tr.getTransferData(flavors[i]);
	// setText(o.toString());
	// dtde.dropComplete(true);
	// return;
	// }
	// // How about an input stream?
	// // else if
	// (flavors[i].isRepresentationClassInputStream())
	// // {
	// //
	// dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	// // ta.setText("Successful text drop.\n\n");
	// // ta
	// // .read(new
	// InputStreamReader((InputStream) tr
	// // .getTransferData(flavors[i])),
	// "from system clipboard");
	// // dtde.dropComplete(true);
	// // return;
	// // }
	// }
	// // Hmm, the user must not have dropped a
	// file list
	// dtde.rejectDrop();
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// dtde.rejectDrop();
	// }
	// }

	public static void main(String args[])
	{
		DropTextField drop = new DropTextField();

		JPanel p = new JPanel(new BorderLayout());

		p.add(new JLabel("Data : "), BorderLayout.WEST);
		p.add(drop, BorderLayout.CENTER);
		p.add(new JButton("Set"), BorderLayout.EAST);

		FrameFactroy.getFrame(p);

	}
}