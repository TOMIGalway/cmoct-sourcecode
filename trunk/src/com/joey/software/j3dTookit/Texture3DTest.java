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
package com.joey.software.j3dTookit;

/*
 *      @(#)Texture3DTest.java 1.1 99/01/05 16:14:11
 *
 * Copyright (c) 1996-1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

/**
 * Draws two squares using a 3D texture map.  The first square uses explicit
 * texture coordinates.  The second uses texture coordinate generation to 
 * set up texture coordinates which line up with the first square.
 */

/*
 * Modified by N. Vaidya to demo Texture3D Memory Leak
 */

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent3D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;


import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.memoryToolkit.MemoryMonitor;
import com.sun.j3d.utils.universe.SimpleUniverse;


public class Texture3DTest extends JFrame
{

	BranchGroup objRoot;

	Texture3D tex;

	public BranchGroup createSceneGraph()
	{

		// Create the root of the branch graph
		objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);

		// Create a transform group to center the object
		TransformGroup objOrient = new TransformGroup();
		Transform3D orient = new Transform3D();
		orient.set(new Vector3d(-0.25, -0.0, -0.0), 0.5);
		objOrient.setTransform(orient);
		objRoot.addChild(objOrient);

		// Create a transform group node and initialize it to the identity.
		// Enable the TRANSFORM_WRITE capability so that our behavior code
		// can modify it at runtime. Add it to the root of the subgraph.
		//
		TransformGroup objTrans = new TransformGroup();

		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objOrient.addChild(objTrans);

		//
		// Create a 3D texture
		//
		int width = 256;
		int height = 256;
		int depth = 256;

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		int[] nBits =
		{ 8, 8, 8, 8 };
		ComponentColorModel colorModel = new ComponentColorModel(cs, nBits,
				true, true, Transparency.TRANSLUCENT, 0);
		WritableRaster raster = colorModel
				.createCompatibleWritableRaster(width, height);

		BufferedImage bImage = new BufferedImage(colorModel, raster, true, null);

		byte[] byteData = ((DataBufferByte) raster.getDataBuffer()).getData();

		ImageComponent3D pArray = new ImageComponent3D(
				ImageComponent.FORMAT_RGBA, width, height, depth);

		// set up a volume with the color intensities corresponding to the
		// s,t,r values: s = red, t = green, r == blue
		for (int k = 0; k < depth; k++)
		{
			for (int j = 0; j < height; j++)
			{
				for (int i = 0; i < width; i++)
				{

					double s = (double) i / width;
					double t = (double) j / height;
					double r = (double) k / depth;

					// Note: Java3D flips the s coordinate to match the 2D
					// image sematics, which put s=0 at the "top" of the image.
					// Since most 3D data puts the origin at the lower left
					// corner, we flip the "s" coordinate
					s = 1.0 - s;

					int index = ((j * width) + i) * 3;
					byteData[index] = (byte) (255 * s);
					byteData[index + 1] = (byte) (255 * t);
					byteData[index + 2] = (byte) (255 * r);
				}
			}
			pArray.set(k, bImage);
		}

		tex = new Texture3D(Texture.BASE_LEVEL, Texture.RGB, width, height,
				depth);
		tex.setImage(0, pArray);
		tex.setEnable(true);
		tex.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		tex.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		tex.setBoundaryModeS(Texture.CLAMP);
		tex.setBoundaryModeT(Texture.CLAMP);
		tex.setBoundaryModeR(Texture.CLAMP);

		// turn off face culling and lighting so we an see just the texture
		PolygonAttributes p = new PolygonAttributes();
		p.setCullFace(PolygonAttributes.CULL_NONE);
		Material m = new Material();
		m.setLightingEnable(false);

		// Create two squares, one with texture coordinates, and the
		// other with generated texture coordinates

		Point3f[] coords = new Point3f[4];
		coords[0] = new Point3f(0.0f, 0.0f, 0.0f);
		coords[1] = new Point3f(1.0f, 1.0f, 0.0f);
		coords[2] = new Point3f(1.0f, 1.0f, 1.0f);
		coords[3] = new Point3f(0.0f, 0.0f, 1.0f);

		// Note that the texture coordinates match the coords: s=x, t=y, r=z
		Point3f[] texCoords = new Point3f[4];
		texCoords[0] = coords[0];
		texCoords[1] = coords[1];
		texCoords[2] = coords[2];
		texCoords[3] = coords[3];

		QuadArray coordsSquare = new QuadArray(4, GeometryArray.COORDINATES
				| GeometryArray.TEXTURE_COORDINATE_3);
		coordsSquare.setCoordinates(0, coords);
		coordsSquare.setTextureCoordinates(0, texCoords);

		// create an appearance with the texture but no tex coord gen
		Appearance coordsAppearance = new Appearance();
		coordsAppearance.setTexture(tex);
		coordsAppearance.setMaterial(m);
		coordsAppearance.setPolygonAttributes(p);

		Shape3D coordsShape = new Shape3D(coordsSquare, coordsAppearance);

		objTrans.addChild(coordsShape);

		// Now the square with generated tex coords. This crosses the first
		// square, but it has texture coordinates which match up with the
		// the first quad

		// First the shape...
		Point3f[] genCoords = new Point3f[4];
		genCoords[0] = new Point3f(1.0f, 0.0f, 0.0f);
		genCoords[1] = new Point3f(1.0f, 0.0f, 1.0f);
		genCoords[2] = new Point3f(0.0f, 1.0f, 1.0f);
		genCoords[3] = new Point3f(0.0f, 1.0f, 0.0f);

		QuadArray genSquare = new QuadArray(4, GeometryArray.COORDINATES);
		genSquare.setCoordinates(0, genCoords);

		// setup the tex coord gen. This is just s = x, t = y, r = z
		TexCoordGeneration tg = new TexCoordGeneration();
		tg.setFormat(TexCoordGeneration.TEXTURE_COORDINATE_3);
		tg.setPlaneS(new Vector4f(1.0f, 0.0f, 0.0f, 0.0f));
		tg.setPlaneT(new Vector4f(0.0f, 1.0f, 0.0f, 0.0f));
		tg.setPlaneR(new Vector4f(0.0f, 0.0f, 1.0f, 0.0f));

		// create an appearance with the texture and tex coord gen
		Appearance genAppearance = new Appearance();
		genAppearance.setTexture(tex);
		genAppearance.setTexCoordGeneration(tg);
		genAppearance.setMaterial(m);
		genAppearance.setPolygonAttributes(p);

		Shape3D genShape = new Shape3D(genSquare, genAppearance);

		objTrans.addChild(genShape);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);

		// Create a new Behavior object that will perform the desired
		// operation on the specified transform object and add it into the
		// scene graph.
		//
		Transform3D yAxis = new Transform3D();
		Alpha rotorAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000,
				0, 0, 0, 0, 0);
		RotationInterpolator rotator = new RotationInterpolator(rotorAlpha,
				objTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);
		objTrans.addChild(rotator);

		tex.setCapability(Texture.ALLOW_IMAGE_WRITE);
		// Have Java 3D perform optimizations on this scene graph.
		objRoot.compile();

		return objRoot;
	}

	public Texture3DTest()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel cp = new JPanel();
		cp.setLayout(new BorderLayout());
		Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		c.setSize(300, 300);
		cp.add(c, BorderLayout.CENTER);

		MemoryMonitor memMon = new MemoryMonitor();
		memMon.setVisible(true);
		memMon.surf.setVisible(true);
		memMon.surf.start();
		memMon.surf.setUpdateRate(30);

		FrameFactroy.getFrame(memMon);

		JButton b = new JButton("Flush");
		b.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (objRoot != null)
					objRoot.detach();
				objRoot = null;

				tex.setImage(0, null);
				tex = null;
				System.gc();
				System.gc();
				System.gc();
				System.gc();
				System.gc();
				System.gc();
				System.gc();
			}
		});

		JPanel sp = new JPanel();
		sp.setLayout(new FlowLayout(FlowLayout.RIGHT));
		sp.add(memMon);
		sp.add(b);
		cp.add(sp, BorderLayout.SOUTH);

		// Create a simple scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph();
		SimpleUniverse u = new SimpleUniverse(c);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();

		u.addBranchGraph(scene);

		this.add(cp);
		this.pack();
		this.setVisible(true);
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new Texture3DTest();
			}
		});
	}

}
