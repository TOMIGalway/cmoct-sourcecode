package com.joey.software.examples;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.process.StackConverter;
import ij3d.Content;
import ij3d.Image3DUniverse;
import voltex.VoltexGroup;

public class Volume_Rendering implements PlugIn {

	  public static void main(String[] args) {
		    new ij.ImageJ();
		    IJ.runPlugIn("ij3d.examples.Volume_Rendering", "");
	  }

	  @Override
	public void run(String arg) {

		    // Open an image
		    String path = "/home/bene/PhD/brains/template.tif";
		    ImagePlus imp = IJ.openImage(path);
		    new StackConverter(imp).convertToGray8();

		    // Create a universe and show it
		    Image3DUniverse univ = new Image3DUniverse();
		    univ.show();
		    univ.rotateY(30 * Math.PI / 180);

		    // Add the image as a volume
		    Content c = univ.addVoltex(imp);
		    sleep(5);

		    // Retrieve the VoltexGroup
		    VoltexGroup voltex = (VoltexGroup)c.getContent();
		    
		    // Define a ROI
		    Roi roi = new OvalRoi(240, 220, 70, 50);
		    
		    // Define a fill color
		    byte fillValue = (byte)100;
		    
		    // Fill the part of the volume which results from the
		    // projection of the polygon onto the volume:
		    voltex.fillRoi(univ.getCanvas(), roi, fillValue);
		    sleep(5);
		    
		    // This can be optimally used for deleting some parts of
		    // the volume:
		    fillValue = (byte)0;
		    roi.setLocation(150, 150);
		    voltex.fillRoi(univ.getCanvas(), roi, fillValue);
		    sleep(5);
		    
		    // The internal image is changed, too:
		    imp.show();
	  }

	  private static void sleep(int sec) {
		    try {
				Thread.sleep(sec * 1000);
		    } catch(InterruptedException e) {
				System.out.println(e.getMessage());
		    }
	  }
}
