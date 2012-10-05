package com.joey.software.imageToolkit;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
 // Inner class is used to hold an image while on the clipboard.
  public class ImageSelection
    implements Transferable 
  {
    // the Image object which will be housed by the ImageSelection
    private Image image;

    public ImageSelection(Image image) {
      this.image = image;
    }

    // Returns the supported flavors of our implementation
    @Override
	public DataFlavor[] getTransferDataFlavors() 
    {
      return new DataFlavor[] {DataFlavor.imageFlavor};
    }
    
    // Returns true if flavor is supported
    @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) 
    {
      return DataFlavor.imageFlavor.equals(flavor);
    }

    // Returns Image object housed by Transferable object
    @Override
	public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException,IOException
    {
      if (!DataFlavor.imageFlavor.equals(flavor)) 
      {
        throw new UnsupportedFlavorException(flavor);
      }
      // else return the payload
      return image;
    }
  }
