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
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture2D;
import javax.media.j3d.View;

public class Axis2DRenderer extends AxisRenderer
{

	Texture2DVolume texVol;

	Shape3D[][] slicesFront = new Shape3D[3][3];

	Shape3D[][] slicesBack = new Shape3D[3][3];

	BranchGroup[][] sliceFrontHold = new BranchGroup[3][3];

	BranchGroup[][] sliceBackHold = new BranchGroup[3][3];

	public Axis2DRenderer(View view, Context context, Volume vol)
	{
		super(view, context, vol);
		texVol = new Texture2DVolume(context, vol);
	}

	@Override
	void update()
	{
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
				System.out
						.println("Axis2DRenderer : Error update() - fullReload()");
			}
		}
		if (tctReloadNeeded)
		{
			try
			{
				tctReload();
			} catch (Exception e)
			{
				System.out
						.println("Axis2DRenderer : Error update() - tctReload()");
			}
		}
	}

	void fullReload()
	{
		clearData();

		if (volume.hasData())
		{
			loadSlices();
			loadQuads();
			tctReload();

		}

		setWhichChild();

		fullReloadNeeded = false;
	}

	void tctReload()
	{
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
		loadAxis(Z_AXIS);
		loadAxis(Y_AXIS);
		loadAxis(X_AXIS);
	}

	public void loadSlices()
	{
		loadSlice(Z_AXIS, Z_AXIS, texVol.volume.vol.useZSlice);
		loadSlice(X_AXIS, Z_AXIS, texVol.volume.vol.useZSlice);
		loadSlice(Y_AXIS, Z_AXIS, texVol.volume.vol.useZSlice);

		loadSlice(Z_AXIS, X_AXIS, texVol.volume.vol.useXSlice);
		loadSlice(X_AXIS, X_AXIS, texVol.volume.vol.useXSlice);
		loadSlice(Y_AXIS, X_AXIS, texVol.volume.vol.useXSlice);

		loadSlice(Z_AXIS, Y_AXIS, texVol.volume.vol.useYSlice);
		loadSlice(X_AXIS, Y_AXIS, texVol.volume.vol.useYSlice);
		loadSlice(Y_AXIS, Y_AXIS, texVol.volume.vol.useYSlice);

	}

	public void loadSlice(int axis, int slice, boolean show)
	{

		OrderedGroup frontGroup = null;
		OrderedGroup backGroup = null;
		TexCoordGeneration tg = null;
		switch (axis)
		{
			case Z_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Z_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Z_AXIS][BACK]);

				break;
			case Y_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Y_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Y_AXIS][BACK]);

				break;
			case X_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[X_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[X_AXIS][BACK]);

				setCoordsX();
				break;
		}

		if (slicesFront[axis][slice] != null)
		{
			frontGroup.removeChild(sliceFrontHold[axis][slice]);
			slicesFront[axis][slice] = null;
			sliceFrontHold[axis][slice] = null;
		}
		if (slicesBack[axis][slice] != null)
		{
			backGroup.removeChild(sliceBackHold[axis][slice]);
			slicesBack[axis][slice] = null;
			sliceBackHold[axis][slice] = null;
		}
		if (!show)
		{
			return;
		}

		Texture2D tex = null;
		if (slice == Z_AXIS)
		{
			setCoordsZ();
			int pos = texVol.zPos;
			setCurCoordZ(pos);
			tex = texVol.zSlice;
		} else if (slice == X_AXIS)
		{
			setCoordsX();
			int pos = texVol.xPos;
			setCurCoordX(pos);
			tex = texVol.xSlice;
			tg = texVol.xTg;

		} else if (slice == Y_AXIS)
		{
			setCoordsY();
			tg = texVol.yTg;
			int pos = texVol.yPos;
			setCurCoordY(pos);
			tex = texVol.ySlice;

		}

		Appearance a = new Appearance();
		// a.setMaterial(m);
		// a.setTransparencyAttributes(t);
		a.setTextureAttributes(texAttr);
		a.setTexture(tex);

		a.setTexCoordGeneration(tg);
		if (dbWriteEnable == false)
		{
			RenderingAttributes r = new RenderingAttributes();
			r.setDepthBufferWriteEnable(dbWriteEnable);
			a.setRenderingAttributes(r);
		}
		a.setPolygonAttributes(p);
		QuadArray quadArray = new QuadArray(4, GeometryArray.COORDINATES);
		quadArray.setCoordinates(0, quadCoords);

		slicesFront[axis][slice] = new Shape3D(quadArray, a);
		sliceFrontHold[axis][slice] = new BranchGroup();
		sliceFrontHold[axis][slice].setCapability(BranchGroup.ALLOW_DETACH);
		sliceFrontHold[axis][slice].addChild(slicesFront[axis][slice]);
		frontGroup.addChild(sliceFrontHold[axis][slice]);

		slicesBack[axis][slice] = new Shape3D(quadArray, a);
		sliceBackHold[axis][slice] = new BranchGroup();
		sliceBackHold[axis][slice].setCapability(BranchGroup.ALLOW_DETACH);
		sliceBackHold[axis][slice].addChild(slicesBack[axis][slice]);
		backGroup.insertChild(sliceBackHold[axis][slice], 0);

	}

	private void loadAxis(int axis)
	{
		int rSize = 0; // number of tex maps to create
		OrderedGroup frontGroup = null;
		OrderedGroup backGroup = null;
		Texture2D[] textures = null;
		TexCoordGeneration tg = null;
		switch (axis)
		{
			case Z_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Z_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Z_AXIS][BACK]);
				rSize = volume.zDim;
				textures = texVol.zTextures;
				tg = texVol.zTg;
				setCoordsZ();
				break;
			case Y_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Y_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[Y_AXIS][BACK]);
				rSize = volume.yDim;
				textures = texVol.yTextures;
				tg = texVol.yTg;
				setCoordsY();
				break;
			case X_AXIS:
				frontGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[X_AXIS][FRONT]);
				backGroup = (OrderedGroup) axisSwitch
						.getChild(axisIndex[X_AXIS][BACK]);
				rSize = volume.xDim;
				textures = texVol.xTextures;
				tg = texVol.xTg;
				setCoordsX();
				break;
		}

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

			if (textures[i] == null)
			{
				return;
			}
			Texture2D tex = textures[i];

			Appearance a = new Appearance();
			a.setMaterial(m);
			a.setTransparencyAttributes(t);
			a.setTextureAttributes(texAttr);
			a.setTexture(tex);

			a.setTexCoordGeneration(tg);
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

	public void updateSlices()
	{
		texVol.loadTextureSlices();
		loadSlices();
	}
}
