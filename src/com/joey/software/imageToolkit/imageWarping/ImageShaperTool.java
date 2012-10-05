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
package com.joey.software.imageToolkit.imageWarping;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTabbedPane;

import com.joey.software.drawingToolkit.DrawTools;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.imageToolkit.ImageOperations;
import com.joey.software.imageToolkit.ImagePanel;
import com.joey.software.mathsToolkit.DataAnalysisToolkit;


public class ImageShaperTool
{

	/**
	 * This will return a scaled and rotated version of d2 such that the points
	 * in the two images aligne with each other. The image that is returned will
	 * contain the full image in d2 and area's outside the image data will be
	 * completly transparent
	 * 
	 * @param d1
	 * @param d2
	 * @param p1
	 * @return
	 */
	public static BufferedImage getProcessedImage(ImageData d1, ImageData d2, Point2D.Double imageCorner, Color bg)
	{
		boolean debug = false;

		// Get Translation
		double tX = d2.p1.x - d1.p1.x;
		double tY = d2.p1.y - d1.p1.y;

		// Get Vectors for angle measurement
		double aX = d1.p2.x - d1.p1.x;
		double aY = d1.p2.y - d1.p1.y;
		double aS = d1.getLength();

		double bX = d2.p2.x - d2.p1.x;
		double bY = d2.p2.y - d2.p1.y;
		double bS = d2.getLength();

		double r = -Math.atan2((aX * bY - aY * bX), (aX * bX + aY * bY));

		// Get Scale
		double S = (aS / bS);

		AffineTransform result = new AffineTransform();

		if (!debug)
		{

			result.translate(-tX, -tY);

			// Origin Move
			AffineTransform orginMove = AffineTransform
					.getTranslateInstance(-d1.p1.x, -d1.p1.y);
			orginMove.concatenate(result);
			result = orginMove;

			AffineTransform rot = AffineTransform.getRotateInstance(r);
			rot.concatenate(result);
			result = rot;

			AffineTransform scale = AffineTransform.getScaleInstance(S, S);
			scale.concatenate(result);
			result = scale;

			AffineTransform orginBack = AffineTransform
					.getTranslateInstance(d1.p1.x, d1.p1.y);
			orginBack.concatenate(result);

			result = orginBack;

			BufferedImage img = getTransformedImage(result, d2.getImg(), imageCorner, bg);
			return img;
		} else
		{
			/**
			 * Only used to debug the data
			 */
			Graphics2D g = null;
			ImagePanel[] panels = new ImagePanel[6];
			// Create the transform

			BufferedImage axes = ImageOperations.getBi(600, 600);
			ImageOperations.setImage(Color.BLACK, axes);
			g = axes.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(300, 300));
			DrawTools.drawCross(g, new Point(0, 0), 600, 0, Color.white);

			g.drawImage(d2.getImg(true), result, null);
			panels[0] = new ImagePanel(ImageOperations.cloneImage(axes));

			result.translate(-tX, -tY);

			ImageOperations.setImage(Color.BLACK, axes);
			g = axes.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(300, 300));
			DrawTools.drawCross(g, new Point(0, 0), 600, 0, Color.white);
			g.drawImage(d2.getImg(), result, null);
			panels[1] = new ImagePanel(ImageOperations.cloneImage(axes));

			// Origin Move
			AffineTransform orginMove = AffineTransform
					.getTranslateInstance(-d1.p1.x, -d1.p1.y);
			orginMove.concatenate(result);
			result = orginMove;

			ImageOperations.setImage(Color.BLACK, axes);
			g = axes.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(300, 300));
			DrawTools.drawCross(g, new Point(0, 0), 600, 0, Color.white);
			g.drawImage(d2.getImg(true), result, null);
			panels[2] = new ImagePanel(ImageOperations.cloneImage(axes));

			AffineTransform rot = AffineTransform.getRotateInstance(r);
			rot.concatenate(result);
			result = rot;

			ImageOperations.setImage(Color.BLACK, axes);
			g = axes.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(300, 300));
			DrawTools.drawCross(g, new Point(0, 0), 600, 0, Color.white);
			g.drawImage(d2.getImg(true), result, null);
			panels[3] = new ImagePanel(ImageOperations.cloneImage(axes));

			AffineTransform scale = AffineTransform.getScaleInstance(S, S);
			scale.concatenate(result);
			result = scale;

			ImageOperations.setImage(Color.BLACK, axes);
			g = axes.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(300, 300));
			DrawTools.drawCross(g, new Point(0, 0), 600, 0, Color.white);
			g.drawImage(d2.getImg(true), result, null);
			panels[4] = new ImagePanel(ImageOperations.cloneImage(axes));

			AffineTransform orginBack = AffineTransform
					.getTranslateInstance(d1.p1.x, d1.p1.y);
			orginBack.concatenate(result);
			result = orginBack;

			// Back from orignin
			ImageOperations.setImage(Color.BLACK, axes);
			g = axes.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(300, 300));
			DrawTools.drawCross(g, new Point(0, 0), 600, 0, Color.white);

			BufferedImage img = getTransformedImage(result, d2.getImg(), imageCorner, bg);
			// g.setTransform(AffineTransform.getTranslateInstance(300, 300));

			g.drawImage(d1.getImg(true), 0, 0, null);
			g.setComposite(AlphaComposite
					.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g.drawImage(d2.getImg(true), result, null);
			DrawTools.drawCross(g, imageCorner, 1000, 0, Color.RED);
			panels[5] = new ImagePanel(ImageOperations.cloneImage(axes));

			// System.out.printf("Vector - A[%3.3f,%3.3f] size[%3.3f]\n", aX,
			// aY,
			// aS);
			// System.out.printf("Vector - B[%3.3f,%3.3f] size[%3.3f]\n", bX,
			// bY,
			// bS);
			// System.out.printf("T[%3.3f,%3.3f]\nR[%3.3f]\nS[%3.3f]\n", tX,tY,
			// Math.toDegrees(r), S);
			// System.out.printf("S[%3.3f,%3.3f] - R[%3.3f,%3.3f]", pS.x, pS.y,
			// pR.x, pR.y);

			JTabbedPane tabs = new JTabbedPane();
			tabs.addTab("Initial", panels[0]);
			tabs.addTab("T Even", panels[1]);
			tabs.addTab("T Orgin", panels[2]);
			tabs.addTab("Rot", panels[3]);
			tabs.addTab("scale", panels[4]);
			tabs.addTab("T back", panels[5]);

			FrameFactroy.getFrame(tabs).setBounds(800, 0, 800, 800);
			return img;
		}

	}

	public static BufferedImage getTransformedImage(AffineTransform tra, BufferedImage img, Color bg)
	{
		return getTransformedImage(tra, img, new Point2D.Double(), bg);
	}

	/**
	 * This transfrom an image with the give translation. The function will also
	 * however move the viewing windows by a certain translation. The final
	 * transfromation that was applied to the transfrom is given in rst.
	 * 
	 * @param tra
	 *            - Translation to be done to the image
	 * @param img
	 *            - image to be translated
	 * @param rst
	 *            - This is the transfrom that is applied to the image
	 * @return
	 */
	public static BufferedImage getTransformedImage(AffineTransform tra, BufferedImage img, Point2D.Double corner, Color bg)
	{
		double[] src = new double[]
		{ 0, 0, img.getWidth(), 0, img.getWidth(), img.getHeight(), 0,
				img.getHeight() };
		double[] dst = new double[8];

		tra.transform(src, 0, dst, 0, 4);

		double[] xData = DataAnalysisToolkit
				.getRange(dst[0], dst[2], dst[4], dst[6]);
		double[] yData = DataAnalysisToolkit
				.getRange(dst[1], dst[3], dst[5], dst[7]);

		int wide = (int) (xData[1] - xData[0]);
		int high = (int) (yData[1] - yData[0]);

		if (wide == 0 || high == 0)
		{
			return ImageOperations.getBi(1);
		}
		AffineTransform rst = new AffineTransform();

		rst.translate(-xData[0], -yData[0]);
		rst.concatenate(tra);

		try
		{
			corner.x = xData[0];
			corner.y = yData[0];
			// tra.transform(corner, corner);
			// rst.inverseTransform(corner, corner);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedImage rstImg = new BufferedImage(wide, high,
				BufferedImage.TYPE_INT_ARGB);

		ImageOperations.setImage(bg, rstImg);
		Graphics2D g = rstImg.createGraphics();
		// g.setComposite(AlphaComposite
		// .getInstance(AlphaComposite.SRC, 0f));
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, wide, high);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g.drawImage(img, rst, null);

		return rstImg;
	}

	public static void mainD(String input[])
			throws NoninvertibleTransformException
	{
		AffineTransform tran = new AffineTransform();
		tran.translate(10, 10);
		tran.rotate(Math.toRadians(90));

		Point p = new Point();
		Point f = new Point();
		Point r = new Point();

		tran.transform(p, f);
		tran.inverseTransform(p, r);

		System.out.println(p);

		System.out.println(r);

		System.out.println(f);
	}

	public static void main(String input[]) throws IOException
	{
		Point2D.Double a1 = new Point2D.Double(50, 50);
		Point2D.Double a2 = new Point2D.Double(50, 150);

		Point2D.Double b1 = new Point2D.Double(100, 100);
		Point2D.Double b2 = new Point2D.Double(200, 150);

		File f1 = new File("c:\\test\\img1.jpg");
		File f2 = new File("c:\\test\\img2.jpg");
		createImage(f1, 250, 300);
		createImage(f2, 250, 300);

		ImageData d1 = new ImageData(f1, a1, a2);
		ImageData d2 = new ImageData(f2, b1, b2);

		Point2D.Double p = new Point2D.Double();
		BufferedImage img = getProcessedImage(d1, d2, p, Color.white);

		d1.unloadImage();
		d2.unloadImage();
		FrameFactroy.getFrame(img, d1.getImg(true), d2.getImg(true));
		System.out.println(p);
	}

	public static void createImage(File f, int wide, int high)
			throws IOException
	{
		BufferedImage img = ImageOperations.getBi(wide, high);
		ImageOperations.fillWithRandomColorSquares(3, 3, img);
		ImageIO.write(img, "jpg", f);
	}
}
