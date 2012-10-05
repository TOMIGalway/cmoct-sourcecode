package com.joey.software.j3dTookit;

import java.awt.BorderLayout;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.Vector;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.ZebraJTable;


public class J3DGraphicsDevicePanel
{
	public static void main(String[] args)
	{
		Vector<Object> names = new Vector<Object>();
		Vector<Object> data = new Vector<Object>();

		VirtualUniverse vu = new VirtualUniverse();
		Map m1 = VirtualUniverse.getProperties();

		Object[] keys = m1.keySet().toArray();
		for (Object o : keys)
		{
			names.add(o);
			data.add(m1.get(o));
		}

		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();

		/*
		 * We need to set this to force choosing a pixel format that support the
		 * canvas.
		 */
		// template.setStereo(template.PREFERRED);
		// template.setSceneAntialiasing(template.PREFERRED);

		GraphicsConfiguration config = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getBestConfiguration(template);

		Map m2 = new Canvas3D(config).queryProperties();

		keys = m2.keySet().toArray();
		for (Object o : keys)
		{
			names.add(o);
			data.add(m2.get(o));
		}

		Object[][] tdat = new Object[names.size()][2];
		for (int i = 0; i < names.size(); i++)
		{
			tdat[i][0] = names.get(i);
			tdat[i][1] = data.get(i);
		}

		ZebraJTable table = new ZebraJTable(tdat, new String[]
		{ "Properity", "Value" });
		table.setAutoCreateRowSorter(true);
		JPanel pan = new JPanel();
		pan.add(table.getTableHeader(), BorderLayout.NORTH);
		pan.add(new JScrollPane(table), BorderLayout.CENTER);

		FrameFactroy.getFrame(pan);
	}

	public static void mainO(String[] args)
	{
		VirtualUniverse vu = new VirtualUniverse();
		Map vuMap = VirtualUniverse.getProperties();

		System.out.println("version = " + vuMap.get("j3d.version"));
		System.out.println("vendor = " + vuMap.get("j3d.vendor"));
		System.out.println("specification.version = "
				+ vuMap.get("j3d.specification.version"));
		System.out.println("specification.vendor = "
				+ vuMap.get("j3d.specification.vendor"));
		System.out.println("renderer = " + vuMap.get("j3d.renderer") + "\n");

		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();

		/*
		 * We need to set this to force choosing a pixel format that support the
		 * canvas.
		 */
		template.setStereo(GraphicsConfigTemplate.PREFERRED);
		template.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);

		GraphicsConfiguration config = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getBestConfiguration(template);

		Map c3dMap = new Canvas3D(config).queryProperties();

		if (true)
		{
			Object[] keys = c3dMap.keySet().toArray();
			for (Object o : keys)
			{
				System.out.print(o);
				System.out.println("\t:\t" + c3dMap.get(o));
			}
			return;
		}
		System.out
				.println("Renderer version = " + c3dMap.get("native.version"));
		System.out.println("doubleBufferAvailable = "
				+ c3dMap.get("doubleBufferAvailable"));
		System.out
				.println("stereoAvailable = " + c3dMap.get("stereoAvailable"));
		System.out.println("sceneAntialiasingAvailable = "
				+ c3dMap.get("sceneAntialiasingAvailable"));
		System.out.println("sceneAntialiasingNumPasses = "
				+ c3dMap.get("sceneAntialiasingNumPasses"));
		System.out.println("textureColorTableSize = "
				+ c3dMap.get("textureColorTableSize"));
		System.out.println("textureEnvCombineAvailable = "
				+ c3dMap.get("textureEnvCombineAvailable"));
		System.out.println("textureCombineDot3Available = "
				+ c3dMap.get("textureCombineDot3Available"));
		System.out.println("textureCombineSubtractAvailable = "
				+ c3dMap.get("textureCombineSubtractAvailable"));
		System.out.println("texture3DAvailable = "
				+ c3dMap.get("texture3DAvailable"));
		System.out.println("textureCubeMapAvailable = "
				+ c3dMap.get("textureCubeMapAvailable"));
		System.out.println("textureSharpenAvailable = "
				+ c3dMap.get("textureSharpenAvailable"));
		System.out.println("textureDetailAvailable = "
				+ c3dMap.get("textureDetailAvailable"));
		System.out.println("textureFilter4Available = "
				+ c3dMap.get("textureFilter4Available"));
		System.out.println("textureAnisotropicFilterDegreeMax = "
				+ c3dMap.get("textureAnisotropicFilterDegreeMax"));
		System.out.println("textureBoundaryWidthMax = "
				+ c3dMap.get("textureBoundaryWidthMax"));
		System.out
				.println("textureWidthMax = " + c3dMap.get("textureWidthMax"));
		System.out.println("textureHeightMax = "
				+ c3dMap.get("textureHeightMax"));
		System.out.println("textureLodOffsetAvailable = "
				+ c3dMap.get("textureLodOffsetAvailable"));
		System.out.println("textureLodRangeAvailable = "
				+ c3dMap.get("textureLodRangeAvailable"));
		System.out.println("textureUnitStateMax = "
				+ c3dMap.get("textureUnitStateMax"));
		System.out.println("compressedGeometry.majorVersionNumber = "
				+ c3dMap.get("compressedGeometry.majorVersionNumber"));
		System.out.println("compressedGeometry.minorVersionNumber = "
				+ c3dMap.get("compressedGeometry.minorVersionNumber"));
		System.out.println("compressedGeometry.minorMinorVersionNumber = "
				+ c3dMap.get("compressedGeometry.minorMinorVersionNumber"));

		System.exit(0);
	}
}