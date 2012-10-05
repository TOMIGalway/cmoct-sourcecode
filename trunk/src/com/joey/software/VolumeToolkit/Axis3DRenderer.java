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

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.View;

public class Axis3DRenderer extends AxisRenderer
{
	Texture3DVolume texVol;

	public Axis3DRenderer(View view, Context context, Volume vol)
	{
		super(view, context, vol);
		System.out.println("Axis3Drenderer - Setting UP");
		texVol = new Texture3DVolume(context, vol);
	}

	boolean updating = false;

	@Override
	void update()
	{

		synchronized (texVol.lock)
		{
			updating = true;
			System.out.println("Axis3Drenderer - Update");
			int texVolUpdate = texVol.update();

			switch (texVolUpdate)
			{
				case TextureVolume.RELOAD_NONE:
					fullReloadNeeded = false;
					tctReloadNeeded = false;
					break;
				case TextureVolume.RELOAD_VOLUME:
					fullReloadNeeded = true;
					tctReloadNeeded = false;
					break;
				case TextureVolume.RELOAD_CMAP:
					fullReloadNeeded = false;
					tctReloadNeeded = true;
					break;
			}

			updateDebugAttrs();

			if (fullReloadNeeded)
			{
				try
				{
					fullReload();
				} catch (Exception e)
				{
					e.printStackTrace();
					System.out
							.println("Axis3DRenderer : Error update() - fullReload()");
				}
			}
			if (tctReloadNeeded)
			{
				try
				{
					tctReload();
				} catch (Exception e)
				{
					e.printStackTrace();
					System.out
							.println("Axis3DRenderer : Error update() - tctReload()");
				}
			}
		}
	}

	void fullReload()
	{
		synchronized (texVol.lock)
		{

			System.out.println("Axis3Drenderer - Full Reload : ["
					+ Thread.currentThread().getName() + "]");
			clearData();

			if (volume.hasData())
			{
				tctReload();
				loadQuads();
			}

			setWhichChild();

			fullReloadNeeded = false;
		}
	}

	void tctReload()
	{
		System.out.println("Axis3Drenderer - tctReload ["
				+ Thread.currentThread().getName() + "]");
		if (texVol.useTextureColorTable)
		{
			texAttr.setTextureColorTable(texVol.texColorMap);
		} else
		{
			texAttr.setTextureColorTable(null);
		}
		tctReloadNeeded = false;
	}

	void loadQuads()
	{
		System.out.println("Axis3Drenderer - loadQuads ["
				+ Thread.currentThread().getName() + "]");
		loadAxis(Z_AXIS);
		loadAxis(Y_AXIS);
		loadAxis(X_AXIS);
	}

	private void loadAxis(int axis)
	{
		System.out.println("Axis3Drenderer - Load Axis :["
				+ Thread.currentThread().getName() + "]");
		OrderedGroup frontGroup = null;
		OrderedGroup backGroup = null;
		int rSize = 0;

		switch (axis)
		{
			case Z_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Z_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Z_AXIS][BACK]);
				rSize = volume.zDim;

				setCoordsZ();
				break;
			case Y_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Y_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Y_AXIS][BACK]);
				rSize = volume.yDim;
				setCoordsY();
				break;
			case X_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[X_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[X_AXIS][BACK]);
				rSize = volume.xDim;
				setCoordsX();
				break;
		}

		System.out.print("Data R Size : " + rSize + " - ");
		for (int i = 0; i < rSize; i++)
		{
			switch (axis)
			{
				case Z_AXIS:
					setCurCoordZ(i);
					break;
				case Y_AXIS:
					setCurCoordY(i);
					break;
				case X_AXIS:
					setCurCoordX(i);
					break;
			}

			Appearance a = new Appearance();
			a.setMaterial(m);
			a.setTransparencyAttributes(t);
			a.setTextureAttributes(texAttr);
			a.setTexture(texVol.getTexture());

			a.setTexCoordGeneration(texVol.getTexGen());
			if (dbWriteEnable == false)
			{
				RenderingAttributes r = new RenderingAttributes();
				r.setDepthBufferWriteEnable(dbWriteEnable);
				a.setRenderingAttributes(r);
			}
			a.setPolygonAttributes(p);

			QuadArray quadArray = new QuadArray(4, GeometryArray.COORDINATES);
			quadArray.setCoordinates(0, quadCoords);

			Shape3D frontShape = new Shape3D(quadArray, a);

			System.out.print(i + "..");
			BranchGroup frontShapeGroup = new BranchGroup();
			frontShapeGroup.setCapability(BranchGroup.ALLOW_DETACH);
			frontShapeGroup.addChild(frontShape);
			frontGroup.addChild(frontShapeGroup);

			Shape3D backShape = new Shape3D(quadArray, a);

			BranchGroup backShapeGroup = new BranchGroup();
			backShapeGroup.setCapability(BranchGroup.ALLOW_DETACH);
			backShapeGroup.addChild(backShape);
			backGroup.insertChild(backShapeGroup, 0);
		}
	}
}
