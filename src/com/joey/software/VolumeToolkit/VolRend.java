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
package com.joey.software.VolumeToolkit;

/*
 *	%Z%%M% %I% %E% %U%
 *
 * Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.Dimension;
import java.net.URL;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.media.j3d.WakeupOr;
import javax.swing.JOptionPane;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * The base class for VolRend applets and
 * applications. Sets up the basic scene graph
 * structure and processes changes to the
 * attributes
 */
public class VolRend implements VolRendListener, MouseBehaviorCallback
{

	//
	static final int POST_AWT_CHANGE = 1;

	// parameters settable by setting
	// corresponding property
	boolean timing = false;

	boolean debug = false;

	Volume volume;

	boolean loaded = true;

	Renderer renderer;

	Annotations annotations;

	View view; // primary view for renderers

	Renderer[] renderers;

	UpdateBehavior updateBehavior;

	Switch coordSwitch;

	TransformGroup objectGroup;

	TransformGroup centerGroup;

	Transform3D centerXform = new Transform3D();

	Vector3d centerOffset = new Vector3d(-0.5, -0.5, -0.5);

	Group staticAttachGroup;

	Group dynamicAttachGroup;

	Switch staticBackAnnotationSwitch;

	Switch dynamicBackAnnotationSwitch;

	Switch staticFrontAnnotationSwitch;

	Switch dynamicFrontAnnotationSwitch;

	Canvas3D canvas;

	SimpleUniverse universe;

	Context context;

	StringAttr dataFileAttr;

	ToggleAttr doubleBufferAttr;

	public ToggleAttr coordSysAttr;

	private ToggleAttr annotationsAttr;

	ToggleAttr plusXBoxAttr;

	ToggleAttr plusYBoxAttr;

	ToggleAttr plusZBoxAttr;

	ToggleAttr minusXBoxAttr;

	ToggleAttr minusYBoxAttr;

	ToggleAttr minusZBoxAttr;

	StringAttr plusXImageAttr;

	StringAttr plusYImageAttr;

	StringAttr plusZImageAttr;

	StringAttr minusXImageAttr;

	StringAttr minusYImageAttr;

	StringAttr minusZImageAttr;

	public ToggleAttr perspectiveAttr;

	public ColormapChoiceAttr colorModeAttr;

	public ToggleAttr texColorMapAttr;

	IntAttr segyMinAlphaAttr;

	IntAttr segyMaxAlphaAttr;

	public ChoiceAttr rendererAttr;

	private VectorAttr translationAttr;

	private QuatAttr rotationAttr;

	private DoubleAttr scaleAttr;

	CoordAttr volRefPtAttr;

	ChoiceAttr displayAxisAttr;

	ToggleAttr axisDepthWriteAttr;

	boolean restorePending;

	int volEditId = -1;

	ScreenSizeCalculator calculator = new ScreenSizeCalculator();

	public VolRend(boolean timing, boolean debug)
	{

		if (timing)
		{
			canvas = new TimingCanvas3D(
					SimpleUniverse.getPreferredConfiguration(), this);
		} else
		{
			canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		}
		canvas.setMinimumSize(new Dimension(1,1));
	}

	public Canvas3D getCanvas()
	{
		return canvas;
	}

	public void setVolumeFile(VolFile vol)
	{

		if (renderer instanceof Axis2DRenderer)
		{
			Axis2DRenderer rend = ((Axis2DRenderer) renderer);
			synchronized (rend.texVol.lock)
			{
				volume.setVolumeFile(vol);
				rend.texVol.update();
			}
		} else if (renderer instanceof Axis3DRenderer)
		{
			Axis3DRenderer rend = ((Axis3DRenderer) renderer);
			synchronized (rend.texVol.lock)
			{
				volume.setVolumeFile(vol);
				rend.texVol.update();
			}
		}
		update();
	}

	void setupScene()
	{

		// Setup the graphics
		// Create a simple scene and attach it to
		// the virtual universe
		BranchGroup scene = createSceneGraph();
		universe = new SimpleUniverse(canvas);

		// This will move the ViewPlatform back a
		// bit so the
		// objects in the scene can be viewed.
		universe.getViewingPlatform().setNominalViewingTransform();

		// get the primary view
		view = universe.getViewer().getView();

		// switch to a parallel projection, which
		// is faster for texture mapping
		view.setProjectionPolicy(View.PARALLEL_PROJECTION);
		view.setBackClipDistance(10000);

		universe.addBranchGraph(scene);

		canvas.setDoubleBufferEnable(true);

		// setup the renderers
		renderers = new Renderer[1];
		renderers[0] = new Axis2DRenderer(view, context, volume);
		// renderers[1] = new Axis3DRenderer(view,
		// context, volume);
		// renderers[2] = new
		// SlicePlane3DRenderer(view, context,
		// volume);
		// renderers[3] = new
		// SlicePlane2DRenderer(view, context,
		// volume);

		renderer = renderers[rendererAttr.getValue()];

		// Add the volume to the scene
		clearAttach();
		renderer.attach(dynamicAttachGroup, staticAttachGroup);

		// Set up the annotations
		annotations = new Annotations(view, context, volume);
		annotations
				.attach(dynamicFrontAnnotationSwitch, staticFrontAnnotationSwitch);
		annotations
				.attachBack(dynamicBackAnnotationSwitch, staticBackAnnotationSwitch);
	}

	void update()
	{
		updateBehavior.postId(POST_AWT_CHANGE);
	}

	void initContext(URL codebase)
	{
		// Attribute stuff

		context = new Context(codebase);
		dataFileAttr = new StringAttr("Data File", null);
		context.addAttr(dataFileAttr);

		segyMinAlphaAttr = new IntAttr("Segy Min Alpha", 85);
		context.addAttr(segyMinAlphaAttr);
		segyMaxAlphaAttr = new IntAttr("Segy Max Alpha", 170);
		context.addAttr(segyMaxAlphaAttr);
		String[] colorModes = new String[1];
		colorModes[0] = "Standard Mapping";
		// colorModes[1] = "Intensity";
		// colorModes[2] = "Intensity Cmap";
		// colorModes[3] = "Segy Cmap";

		Colormap[] colormaps = new Colormap[4];
		colormaps[0] = new UserChoiceColorMap();
		// colormaps[1] = null;
		// colormaps[2] = new LinearColormap();
		// colormaps[3] = new
		// SegyColormap(segyMinAlphaAttr,
		// segyMaxAlphaAttr);

		setColorModeAttr(new ColormapChoiceAttr("Color Mode", colorModes,
				colormaps, 0));
		context.addAttr(getColorModeAttr());
		texColorMapAttr = new ToggleAttr("Tex Color Map", false);
		context.addAttr(texColorMapAttr);

		String[] rendererNames = new String[1];
		rendererNames[0] = "Axis Volume 2D Textures";
		// rendererNames[1] =
		// "Axis Volume 3D Texture";
		// rendererNames[2] =
		// "Slice Plane 3D Texture";
		// rendererNames[3] =
		// "Slice Plane 2D Textures";
		rendererAttr = new ChoiceAttr("Renderer", rendererNames, 0);
		context.addAttr(rendererAttr);

		doubleBufferAttr = new ToggleAttr("Double Buffer", true);
		context.addAttr(doubleBufferAttr);

		coordSysAttr = new ToggleAttr("Coord Sys", true);
		context.addAttr(coordSysAttr);

		setAnnotationsAttr(new ToggleAttr("Bounding Box", true));
		context.addAttr(getAnnotationsAttr());

		plusXBoxAttr = new ToggleAttr("Plus X Box", true);
		context.addAttr(plusXBoxAttr);

		plusYBoxAttr = new ToggleAttr("Plus Y Box", true);
		context.addAttr(plusYBoxAttr);

		plusZBoxAttr = new ToggleAttr("Plus Z Box", true);
		context.addAttr(plusZBoxAttr);

		minusXBoxAttr = new ToggleAttr("Minus X Box", true);
		context.addAttr(minusXBoxAttr);

		minusYBoxAttr = new ToggleAttr("Minus Y Box", true);
		context.addAttr(minusYBoxAttr);

		minusZBoxAttr = new ToggleAttr("Minus Z Box", true);
		context.addAttr(minusZBoxAttr);

		plusXImageAttr = new StringAttr("Plus X Image", "");
		context.addAttr(plusXImageAttr);

		plusYImageAttr = new StringAttr("Plus Y Image", "");
		context.addAttr(plusYImageAttr);

		plusZImageAttr = new StringAttr("Plus Z Image", "");
		context.addAttr(plusZImageAttr);

		minusXImageAttr = new StringAttr("Minus X Image", "");
		context.addAttr(minusXImageAttr);

		minusYImageAttr = new StringAttr("Minus Y Image", "");
		context.addAttr(minusYImageAttr);

		minusZImageAttr = new StringAttr("Minus Z Image", "");
		context.addAttr(minusZImageAttr);

		perspectiveAttr = new ToggleAttr("Perspective", false);
		context.addAttr(perspectiveAttr);

		setTranslationAttr(new VectorAttr("Translation", new Vector3d()));
		context.addAttr(getTranslationAttr());

		setRotationAttr(new QuatAttr("Rotation", new Quat4d()));
		context.addAttr(getRotationAttr());

		setScaleAttr(new DoubleAttr("Scale", 1.0));
		context.addAttr(getScaleAttr());

		volRefPtAttr = new CoordAttr("Vol Ref Pt", new Point3d());
		context.addAttr(volRefPtAttr);

		// initialize the volume
		volume = new Volume(context);

		// initialize the scene graph
		setupScene();

		if (debug)
		{
			String[] displayAxis = new String[7];
			displayAxis[0] = "Auto";
			displayAxis[1] = "-X";
			displayAxis[2] = "+X";
			displayAxis[3] = "-Y";
			displayAxis[4] = "+Y";
			displayAxis[5] = "-Z";
			displayAxis[6] = "+Z";
			displayAxisAttr = new ChoiceAttr("Display Axis", displayAxis, 0);
			context.addAttr(displayAxisAttr);
			axisDepthWriteAttr = new ToggleAttr("Depth Write", true);
			context.addAttr(axisDepthWriteAttr);
		}

	}

	private void doUpdate()
	{
		if (restorePending)
		{
			return; // we will get called again
					// after the restore is
					// complete
		}
		canvas.stopRenderer();
		canvas.setDoubleBufferEnable(doubleBufferAttr.getValue());
		if (coordSysAttr.getValue())
		{
			coordSwitch.setWhichChild(Switch.CHILD_ALL);
		} else
		{
			coordSwitch.setWhichChild(Switch.CHILD_NONE);
		}
		if (getAnnotationsAttr().getValue())
		{
			staticBackAnnotationSwitch.setWhichChild(Switch.CHILD_ALL);
			dynamicBackAnnotationSwitch.setWhichChild(Switch.CHILD_ALL);
			staticFrontAnnotationSwitch.setWhichChild(Switch.CHILD_ALL);
			dynamicFrontAnnotationSwitch.setWhichChild(Switch.CHILD_ALL);
		} else
		{
			staticBackAnnotationSwitch.setWhichChild(Switch.CHILD_NONE);
			dynamicBackAnnotationSwitch.setWhichChild(Switch.CHILD_NONE);
			staticFrontAnnotationSwitch.setWhichChild(Switch.CHILD_NONE);
			dynamicFrontAnnotationSwitch.setWhichChild(Switch.CHILD_NONE);
		}
		if (perspectiveAttr.getValue())
		{
			view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		} else
		{
			view.setProjectionPolicy(View.PARALLEL_PROJECTION);
		}
		if (renderer != renderers[rendererAttr.getValue()])
		{
			// TODO: renderer.clear();
			// TODO: handle gui
			try
			{
				System.out.println("Clearing Attached : VolRend-doUpdate()");
				clearAttach();

			} catch (Exception e)
			{
				e.printStackTrace();
			}
			renderer = renderers[rendererAttr.getValue()];
			renderer.attach(dynamicAttachGroup, staticAttachGroup);
		}
		try
		{
			renderer.update();
			annotations.update();
		} catch (Exception e)
		{

			e.printStackTrace();
		} catch (OutOfMemoryError e)
		{
			JOptionPane
					.showMessageDialog(null, "Ran out of memory!", "Render Error", JOptionPane.ERROR_MESSAGE);
		}
		int newVolEditId;
		if ((newVolEditId = volume.update()) != volEditId)
		{
			updateCenter(volume.minCoord, volume.maxCoord);
			newVolEditId = volEditId;
		}
		eyePtChanged();
		canvas.startRenderer();

	}

	private class UpdateBehavior extends Behavior
	{
		WakeupCriterion criterion[] = { new WakeupOnBehaviorPost(null,
				POST_AWT_CHANGE) };

		WakeupCondition conditions = new WakeupOr(criterion);

		Object lock = new Object();

		boolean updating = false;

		@Override
		public void initialize()
		{
			wakeupOn(conditions);
		}

		@Override
		public void processStimulus(Enumeration criteria)
		{
			synchronized (lock)
			{


			try
			{

				// Do the update
				doUpdate();

				wakeupOn(conditions);
			} catch (Throwable t)
			{
				System.out.println("Saved : ");
				t.printStackTrace();
			}

				updating = false;
				lock.notifyAll();
			}
		}

		public void startWaitForUpdate()
		{
			synchronized (lock)
			{
				updating = true;
			}
		}
		public void waitForUpdateFinish()
		{

			synchronized (lock)
			{
				if (updating)
				{
					try
					{
						lock.wait();
					} catch (InterruptedException e)
					{
						// TODO Auto-generated
						// catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void waitForUpdate()
	{
		updateBehavior.waitForUpdateFinish();
	}

	void restoreContext(String filename)
	{
		restorePending = true;
		context.restore(filename);
		restorePending = false;
		restoreXform();
		update();
	}

	void updateCenter(Point3d minCoord, Point3d maxCoord)
	{

		centerOffset.x = -(maxCoord.x - minCoord.x) / 2.0;
		centerOffset.y = -(maxCoord.y - minCoord.y) / 2.0;
		centerOffset.z = -(maxCoord.z - minCoord.z) / 2.0;

		if (Double.isNaN(centerOffset.x) || Double.isInfinite(centerOffset.x))
		{
			centerOffset.x = 0;
		}
		if (Double.isNaN(centerOffset.y) || Double.isInfinite(centerOffset.y))
		{
			centerOffset.y = 0;
		}
		if (Double.isNaN(centerOffset.z) || Double.isInfinite(centerOffset.z))
		{
			centerOffset.z = 0;
		}
		System.out.println(centerOffset);
		centerXform.setTranslation(centerOffset);
		centerGroup.setTransform(centerXform);
	}

	BranchGroup createSceneGraph()
	{
		Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
		Vector3f lDir1 = new Vector3f(0.0f, 0.0f, 1.0f);
		Color3f alColor = new Color3f(1.0f, 1.0f, 1.0f);

		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create a transform group to scale the
		// whole scene
		TransformGroup scaleGroup = new TransformGroup();
		Transform3D scaleXform = new Transform3D();
		double scale = 1;
		scaleXform.setScale(scale);
		scaleGroup.setTransform(scaleXform);
		objRoot.addChild(scaleGroup);

		// Create the static ordered group
		OrderedGroup scaleOGroup = new OrderedGroup();
		scaleGroup.addChild(scaleOGroup);

		// Create the static annotation group
		staticBackAnnotationSwitch = new Switch();
		staticBackAnnotationSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		staticBackAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
		staticBackAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
		staticBackAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		scaleOGroup.addChild(staticBackAnnotationSwitch);

		// Create the static attachment group
		staticAttachGroup = new Group();
		staticAttachGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		staticAttachGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		staticAttachGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		scaleOGroup.addChild(staticAttachGroup);

		// Create a TG at the origin
		objectGroup = new TransformGroup();

		// Enable the TRANSFORM_WRITE capability
		// so that our behavior code
		// can modify it at runtime. Add it to the
		// root of the subgraph.
		//
		objectGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objectGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scaleOGroup.addChild(objectGroup);

		// Create the static annotation group
		staticFrontAnnotationSwitch = new Switch();
		staticFrontAnnotationSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		staticFrontAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
		staticFrontAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
		staticFrontAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		scaleOGroup.addChild(staticFrontAnnotationSwitch);
		// added after objectGroup so it shows up
		// in front

		// Create the transform group node and
		// initialize it center the
		// object around the origin
		centerGroup = new TransformGroup();
		updateCenter(new Point3d(0.0, 0.0, 0.0), new Point3d(1.0, 1.0, 1.0));
		centerGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objectGroup.addChild(centerGroup);

		// Set up the annotation/volume/annotation
		// sandwitch
		OrderedGroup centerOGroup = new OrderedGroup();
		centerGroup.addChild(centerOGroup);

		// create the back dynamic annotation
		// point
		dynamicBackAnnotationSwitch = new Switch(Switch.CHILD_ALL);
		dynamicBackAnnotationSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		dynamicBackAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
		dynamicBackAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
		dynamicBackAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		centerOGroup.addChild(dynamicBackAnnotationSwitch);

		// create the dynamic attachment point
		dynamicAttachGroup = new Group();
		dynamicAttachGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		dynamicAttachGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		dynamicAttachGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		centerOGroup.addChild(dynamicAttachGroup);

		// create the front dynamic annotation
		// point
		dynamicFrontAnnotationSwitch = new Switch(Switch.CHILD_ALL);
		dynamicFrontAnnotationSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		dynamicFrontAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_READ);
		dynamicFrontAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_WRITE);
		dynamicFrontAnnotationSwitch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		centerOGroup.addChild(dynamicFrontAnnotationSwitch);

		// Create the annotations
		Annotations annotations = new Annotations(view, context, volume);
		annotations
				.attachBack(dynamicBackAnnotationSwitch, staticBackAnnotationSwitch);
		annotations
				.attach(dynamicFrontAnnotationSwitch, staticFrontAnnotationSwitch);

		coordSwitch = new Switch(Switch.CHILD_ALL);
		coordSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		coordSwitch.addChild(new CoordSys(0.2, new Vector3d(-0.1, -0.1, -0.1)));
		centerGroup.addChild(coordSwitch);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				10000000.0);

		FirstFramesBehavior ff = new FirstFramesBehavior(this);
		ff.setSchedulingBounds(bounds);
		objRoot.addChild(ff);

		updateBehavior = new UpdateBehavior();
		updateBehavior.setSchedulingBounds(bounds);
		objRoot.addChild(updateBehavior);

		// ExitKeyBehavior eb = new
		// ExitKeyBehavior();
		// eb.setSchedulingBounds(bounds);
		// objRoot.addChild(eb);

		BranchGroup trueRoot = new BranchGroup();
		TransformGroup trueTransform = new TransformGroup();

		trueTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		trueTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		trueRoot.addChild(trueTransform);
		trueTransform.addChild(objRoot);

		// MouseTranslate mt = new
		// MouseTranslate();
		// mt.setupCallback(this);
		// mt.setTransformGroup(trueTransform);
		// mt.setSchedulingBounds(bounds);
		// trueRoot.addChild(mt);

		MouseZoom mz = new MouseZoom();
		mz.setupCallback(this);
		mz.setTransformGroup(trueTransform);
		mz.setSchedulingBounds(bounds);
		trueRoot.addChild(mz);

		MouseRotate mr = new MouseRotate();
		mr.setupCallback(this);
		mr.setTransformGroup(trueTransform);
		mr.setSchedulingBounds(bounds);
		mr.setFactor(0.007);
		trueRoot.addChild(mr);

		// trueTransform.addChild(objRoot);
		// trueRoot.addChild(trueTransform);
		return trueRoot;
	}

	private void clearAttach()
	{
		while (staticAttachGroup.numChildren() > 0)
		{
			System.out.println(staticAttachGroup.getChild(0));
			staticAttachGroup.removeChild(0);
		}
		while (dynamicAttachGroup.numChildren() > 0)
		{
			System.out.println(dynamicAttachGroup.getChild(0));
			dynamicAttachGroup.removeChild(0);
		}

	}

	@Override
	public void transformChanged(int type, Transform3D xform)
	{
		renderer.transformChanged(type, xform);
		renderer.eyePtChanged();
		annotations.eyePtChanged();
		getScaleAttr().set(xform);
		getTranslationAttr().set(xform);
		getRotationAttr().set(xform);
	}

	public void restoreXform()
	{
		Transform3D xform = new Transform3D(getRotationAttr().getValue(),
				getTranslationAttr().getValue(), getScaleAttr().getValue());
		objectGroup.setTransform(xform);
		renderer.eyePtChanged();
	}

	// public void setXForm(Transform3D trans)
	// {
	// centerGroup.setTransform(trans);
	// transformChanged(0, trans);
	// renderer.eyePtChanged();
	// }
	//
	// public Transform3D getTransform()
	// {
	// Transform3D t1 = new Transform3D();
	// centerGroup.getTransform(t1);
	// return t1;
	// }
	@Override
	public void eyePtChanged()
	{
		renderer.eyePtChanged();
	}

	@Override
	public double calcRenderSize()
	{
		if (!centerGroup.isLive())
		{
			return 0.0;
		} else
		{
			return renderer.calcRenderSize(calculator, canvas);
		}
	}

	// Have main starup VolRendEdit for
	// compatiblity
	public static void main(String[] args)
	{
		VolRendEdit vol = new VolRendEdit(args);

	}

	public SimpleUniverse getUniverse()
	{
		return universe;
	}

	public Renderer getRenderer()
	{
		return renderer;
	}

	public Volume getVolume()
	{
		return volume;
	}

	public void setRotationAttr(QuatAttr rotationAttr)
	{
		this.rotationAttr = rotationAttr;
	}

	public QuatAttr getRotationAttr()
	{
		return rotationAttr;
	}

	public void setScaleAttr(DoubleAttr scaleAttr)
	{
		this.scaleAttr = scaleAttr;
	}

	public DoubleAttr getScaleAttr()
	{
		return scaleAttr;
	}

	public void setTranslationAttr(VectorAttr translationAttr)
	{
		this.translationAttr = translationAttr;
	}

	public VectorAttr getTranslationAttr()
	{
		return translationAttr;
	}

	public void setColorModeAttr(ColormapChoiceAttr colorModeAttr)
	{
		this.colorModeAttr = colorModeAttr;
	}

	public ColormapChoiceAttr getColorModeAttr()
	{
		return colorModeAttr;
	}

	public void setAnnotationsAttr(ToggleAttr annotationsAttr)
	{
		this.annotationsAttr = annotationsAttr;
	}

	public ToggleAttr getAnnotationsAttr()
	{
		return annotationsAttr;
	}

	public void startWaitForUpdate()
	{
		updateBehavior.startWaitForUpdate();
	}
}
