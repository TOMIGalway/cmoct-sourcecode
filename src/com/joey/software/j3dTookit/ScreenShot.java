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
 * Based upon OffScreenCanvas3D source code from
 *  book Java 3D Jump-Start, by AARON E. WALSH and DOUG GEHRINGER
 *  http://www.web3dbooks.com/java3d/jumpstart/Java3DExplorer.html
 * 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Screen3D;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import com.joey.software.VolumeToolkit.VolumeViewerPanel;
import com.joey.software.framesToolkit.FrameFactroy;
import com.sun.j3d.utils.universe.SimpleUniverse;


/**
 * OffScreenCanvas3D is used to do off screen rendering for screen captures. The
 * images for the book were produced using this class. Several changes were made
 * on top of "Java3D Jump Start" book.
 * 
 * <pre>
 *  usage:   
 *     ScreenShot screenShot = new ScreenShot(canvas,universe,1.0f);
 *     JPanel control = screenShot.getControlPanel();
 *     this.add(control,&quot;South&quot;);
 *     // you can also put the control in a floating Frame;
 *     Frame f = new Frame(&quot;ScreenShot Control&quot;);
 *      f.setSize(240,200);
 *      f.add(control);
 *      f.pack();
 *      f.setVisible(true);
 * 
 * </pre>
 */
public class ScreenShot extends Canvas3D
{
	private Canvas3D canvas;

	private SimpleUniverse universe;

	private Dimension dim;

	private float offScreenScale = 1.0f;

	private int counter = 1;

	private String imageType = "jpg";

	private JPanel controlPanel = null;

	public static void main(String input[])
	{
		VolumeViewerPanel panel = new VolumeViewerPanel();

		ScreenShot shoot = new ScreenShot(panel.getRender().getCanvas(), panel
				.getRender().getUniverse(), 1);

		// OR you can also put the
		// control in a floating Frame;
		Frame f = new Frame("ScreenShot Control");
		f.setSize(240, 200);
		f.add(shoot.getControlPanel());
		f.pack();
		f.setVisible(true);

		FrameFactroy.getFrame(panel);
	}

	public ScreenShot(Canvas3D canvas, SimpleUniverse universe, float scale)
	{
		super(canvas.getGraphicsConfiguration(), true);
		this.canvas = canvas;
		this.universe = universe;
		universe.getViewer().getView().addCanvas3D(this);
		setOffScreenScale(offScreenScale);
	}

	public BufferedImage doRender(int width, int height)
	{
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		return doRender(image);
	}

	/**
	 * render a Image from canvas
	 * 
	 * @param width
	 *            Image width
	 * @param height
	 *            Image height
	 * @return the image
	 */
	public BufferedImage doRender(BufferedImage image)
	{
		ImageComponent2D buffer = new ImageComponent2D(
				ImageComponent.FORMAT_RGB, image);
		// buffer.setYUp(true);

		if (getOffScreenBuffer() == null)
		{
			setOffScreenBuffer(buffer);

			renderOffScreenBuffer();
			waitForOffScreenRendering();
		}
		return getOffScreenBuffer().getImage();
	}

	/**
	 * save a image from current Canvas3D view
	 * 
	 * @param filename
	 * @param width
	 * @param height
	 */
	public void snapImageFile(String filename)
	{
		BufferedImage bImage = doRender(dim.width, dim.height);
		try
		{
			FileOutputStream fos = new FileOutputStream(filename + "."
					+ imageType);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			ImageIO.write(bImage, imageType, bos);

			bos.flush();
			fos.close();
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}

	/**
	 * set the size of the off-screen canvas based on a scale of the on-screen
	 * size
	 * 
	 * @param scale
	 *            to be applied.
	 */
	public void setOffScreenScale(float offScreenScale)
	{
		// set the size of the off-screen canvas based on a scale
		// of the on-screen size
		this.offScreenScale = offScreenScale;
		Screen3D sOn = canvas.getScreen3D();
		Screen3D sOff = this.getScreen3D();

		this.dim = sOn.getSize();
		dim.width *= offScreenScale;
		dim.height *= offScreenScale;
		sOff.setSize(dim);
		sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth()
				* offScreenScale);
		sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight()
				* offScreenScale);
	}

	/**
	 * return the scale applied
	 * 
	 * @return
	 */
	public float getOffScreenScale()
	{
		return this.offScreenScale;
	}

	/**
	 * remove this canvas from universe
	 */
	public void removeCanvas()
	{
		universe.getViewer().getView().removeCanvas3D(this);
	}

	/**
	 * return a clone of current screen dimension
	 * 
	 * @return Dimension of this screen
	 */
	public Dimension getDimension()
	{
		return (Dimension) this.dim.clone();
	}

	JTextField filenameTF;

	JTextField scaleTF;

	JLabel sizeLabel;

	/**
	 * creates and return a Control panel for this offscreen renderer example:
	 * 
	 * <pre>
	 * usage:   
	 *    ScreenShot screenShot = new ScreenShot(canvas,u,1.0f);
	 *     JPanel control = screenShot.getControlPanel();
	 *     this.add(control,&quot;South&quot;);
	 *     
	 *     // you can also put the control in a floating Frame;
	 *     Frame f = new Frame(&quot;ScreenShot Control&quot;);
	 *      f.setSize(240,200);
	 *      f.add(control);
	 *      f.pack();
	 *      f.setVisible(true);
	 * &lt;pre&gt;
	 * &#064;return a panel to control this renderer
	 * 
	 */
	public JPanel getControlPanel()
	{
		if (controlPanel == null)
		{
			controlPanel = new JPanel();
			JButton btSave = new JButton("Save");
			JLabel label2 = new JLabel("filename");
			JLabel label3 = new JLabel("Scale");
			sizeLabel = new JLabel("ScreenShot - " + dim.width + " x "
					+ dim.height);
			filenameTF = new JTextField("screenshot_1." + imageType);
			scaleTF = new JTextField("1.0f", 4);

			filenameTF
					.setToolTipText("<HTML>Supports several image file formats,"
							+ " as<br>   .png, .jpg, .bmp, etc.");
			scaleTF.setToolTipText("set scale based upon your video screen.");
			btSave.setToolTipText("Save image on disk.");

			btSave.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					System.gc();
					String name = filenameTF.getText();
					// check if imageType changed
					imageType = name.substring(name.lastIndexOf(".") + 1, name
							.length());

					name = name.substring(0, name.lastIndexOf("."));
					snapImageFile(name);
					counter++;
					int pos = name.lastIndexOf("_");
					if (pos > 0)
						name = name.substring(0, pos) + "_" + counter;
					else
					{
						name = name + "_" + counter;
					}
					filenameTF.setText(name + "." + imageType);
					System.gc();
				}
			}); // btSave listener

			scaleTF.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					String scale = scaleTF.getText();
					float temp = offScreenScale;
					try
					{
						offScreenScale = Float.parseFloat(scale);
						setOffScreenScale(offScreenScale);
						sizeLabel.setText("ScreenShot - " + dim.width + " x "
								+ dim.height);
					} catch (Exception ex)
					{
						offScreenScale = temp;
						scaleTF.setText(offScreenScale + "");
					}
				}
			});

			controlPanel.setLayout(new BorderLayout());
			controlPanel.add(sizeLabel, "North");

			JPanel pan2 = new JPanel();
			pan2.setLayout(new GridLayout(2, 2, 2, 2));

			pan2.add(label2);
			pan2.add(filenameTF);
			pan2.add(label3);
			pan2.add(scaleTF);

			controlPanel.add(pan2, "Center");
			controlPanel.add(btSave, "South");
		}
		return controlPanel;
	}

}
