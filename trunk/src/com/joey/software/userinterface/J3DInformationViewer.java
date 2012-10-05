package com.joey.software.userinterface;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.Vector;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.VirtualUniverse;

import com.joey.software.framesToolkit.ZebraJTable;


public class J3DInformationViewer
{
	public static void main(String input[])
	{
		VirtualUniverse vu = new VirtualUniverse();

		GraphicsConfigTemplate3D tmpl = new GraphicsConfigTemplate3D();
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getBestConfiguration(tmpl);

		Canvas3D can = new Canvas3D(config);

		Map m = can.queryProperties();

		Vector<Object> owner = new Vector<Object>();
		owner.addAll(m.keySet());

		ZebraJTable table = new ZebraJTable();
	}
}
