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
package com.joey.software.examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.StackConverter;
import ij3d.Content;
import ij3d.Image3DUniverse;

import javax.media.j3d.Transform3D;

public class Apply_Transformation implements PlugIn {

	  public static void main(String[] args) {
		    new ij.ImageJ();
		    IJ.runPlugIn("ij3d.examples.Apply_Transformation", "");
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

		    // Add the image as an isosurface
		    Content c = univ.addVoltex(imp);
		    sleep(5);

		    // Create a new Transform3D object
		    Transform3D t3d = new Transform3D();

		    // Make it a 45 degree rotation around the local y-axis
		    t3d.rotY(45 * Math.PI / 180);

		    // Apply the transformation to the Content. This concatenates
		    // the previous present transformation with the specified one
		    c.applyTransform(t3d);
		    sleep(5);

		    // Apply it again: this gives a 90 degree rotation in
		    // summary.
		    c.applyTransform(t3d);
		    sleep(5);

		    // setTransform() does not concatenate, but sets the specified
		    // transformation:
		    c.setTransform(t3d);
		    sleep(5);

		    // reset the transformation to the identity
		    t3d.setIdentity();
		    c.setTransform(t3d);
		    sleep(5);

		    // remove all contents and close the universe
		    univ.removeAllContents();
		    univ.close();
	  }

	  private static void sleep(int sec) {
		    try {
				Thread.sleep(sec * 1000);
		    } catch(InterruptedException e) {
				System.out.println(e.getMessage());
		    }
	  }
}
