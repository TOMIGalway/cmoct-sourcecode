package com.joey.software.VolumeToolkit;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import com.joey.software.VideoToolkit.CompoentRecorder;
import com.joey.software.framesToolkit.FrameFactroy;
import com.joey.software.framesToolkit.StatusBarPanel;


public class VolumeViewerPanel extends JPanel implements ActionListener,
		ItemListener, Externalizable
{
	private static final long serialVersionUID = 1L;

	VolRend render = new VolRend(false, false);

	// parameters settable by setting corresponding property
	boolean debug = false;

	boolean timing = false;

	int guiTypdsse;

	JPanel controlPanel = new JPanel();

	JFrame colorMapFrame = new JFrame("Color Map");

	JButton updateCmap = null;

	JFrame videoFrame = new JFrame("Recording Controls");

	String save = "Save Settings...";

	String restore = "Restore Settings...";

	String loadData = "Load Data File...";

	String cmapEdit = "Edit Colormap";

	String annoEdit = "Edit Annotations";

	String rotControl = "Rotation Controler";

	String videoControl = "Show Video Control";

	String exit = "Exit";

	File settingsFile = null;

	String dataSuffix = "vol";

	String sessionSuffix = "vrs";

	ExtensionFileFilter sessionFileFilter;

	ExtensionFileFilter dataFileFilter;

	SegyCmapEditDialog segyCmapEditDialog;

	AnnotationsEditDialog annoEditDialog;

	JButton showControls = new JButton("Controls");

	CompoentRecorder recorder;

	RotationControlPanel rotationControl = new RotationControlPanel(this);

	public VolumeViewerPanel()
	{
		setDoubleBuffered(true);
		render.initContext(getCodeBase());
		createJPanel();
		createControls();

		render.setVolumeFile(new VolArrayFile(new byte[][][]
		{
		{
		{ (byte) 1 } } }, 1f, 1f, 1f));

	}

	public void setData(VolumeViewerPanel data)
	{
		// Copy Transform
		render.getRotationAttr().set(data.render.getRotationAttr().getValue());
		render.getScaleAttr().set(data.render.getScaleAttr().getValue());
		render.getTranslationAttr().set(data.render.getTranslationAttr()
				.getValue());

		// Copy CMAP data
		UserChoiceColorMap cmap = (UserChoiceColorMap) data.render
				.getColorModeAttr().colormaps[0];
		((UserChoiceColorMap) render.getColorModeAttr().colormaps[0])
				.setData(cmap);

		render.getAnnotationsAttr().setValue(data.render.getAnnotationsAttr()
				.isValue());
		// render.displayAxisAttr.value = data.render.displayAxisAttr.value;
		render.perspectiveAttr.setValue(data.render.perspectiveAttr.isValue());
		render.coordSysAttr.setValue(data.render.coordSysAttr.isValue());

		render.rendererAttr.value = data.render.rendererAttr.value;

		render.texColorMapAttr.setValue(data.render.texColorMapAttr.isValue());
		render.getColorModeAttr().value = data.render.getColorModeAttr().value;

		render.restoreXform();
	}

	public VolRend getRender()
	{
		return render;
	}

	public void createControls()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(19, 1));
		panel.setBorder(BorderFactory.createTitledBorder("Display Settings"));

		JButton videoControlButton = new JButton(videoControl);
		videoControlButton.setName(videoControl);
		videoControlButton.addActionListener(this);
		panel.add(videoControlButton);

		JButton rotationControlButton = new JButton(rotControl);
		rotationControlButton.setName(rotControl);
		rotationControlButton.addActionListener(this);
		panel.add(rotationControlButton);

		JButton cmapEditButton = new JButton(cmapEdit);
		cmapEditButton.setName(cmapEdit);
		cmapEditButton.addActionListener(this);
		panel.add(cmapEditButton);
		// // save
		// panel.add(new JLabel("Session"));
		// JButton saveButton = new JButton("Save Settings");
		// saveButton.setName(save);
		// saveButton.addActionListener(this);
		// panel.add(saveButton);
		//
		// // restore
		// JButton restoreButton = new JButton("Restore Settings");
		// restoreButton.setName(restore);
		// restoreButton.addActionListener(this);
		// panel.add(restoreButton);

		// // load data
		// panel.add(new JLabel("Data"));
		// JButton loadButton = new JButton(loadData);
		// loadButton.setName(loadData);
		// loadButton.addActionListener(this);
		// panel.add(loadButton);

		// // Make the buttons the same size
		// Dimension minSize = saveButton.getMinimumSize();
		// Dimension maxSize = new Dimension(Short.MAX_VALUE, minSize.height);
		// saveButton.setMaximumSize(maxSize);
		// restoreButton.setMaximumSize(maxSize);
		// loadButton.setMaximumSize(maxSize);

		// AttrComponent cm = new JPanelChoice(this, panel, render
		// .getColorModeAttr());

		// AttrComponent tc = new JPanelToggle(this, panel,
		// render.texColorMapAttr);

		// AttrComponent dm = new JPanelChoice(this, panel,
		// render.rendererAttr);
		// if (debug)
		// {
		// AttrComponent da = new JPanelChoiceAxis(this, panel,
		// render.displayAxisAttr);
		// AttrComponent dw = new JPanelToggle(this, panel,
		// render.axisDepthWriteAttr);
		// }

		// JButton annoEditButton = new JButton(annoEdit);
		// annoEditButton.setName(annoEdit);
		// annoEditButton.addActionListener(this);
		// panel.add(annoEditButton);

		// use this to put these items at the bottom of the panel
		panel.add(Box.createGlue());
		panel.add(new JLabel("View Options"));
		// AttrComponent db = new JPanelToggle(this, panel,
		// render.doubleBufferAttr);
		AttrComponent pr = new JPanelToggle(this, panel, render.perspectiveAttr);
		AttrComponent cs = new JPanelToggle(this, panel, render.coordSysAttr);
		AttrComponent vb = new JPanelToggle(this, panel, render
				.getAnnotationsAttr());

		controlPanel.setLayout(new BorderLayout());
		controlPanel.add(panel, BorderLayout.NORTH);
	}

	public void createJPanel()
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("3D Render"));
		add(render.getCanvas(), BorderLayout.CENTER);
	}

	URL getCodeBase()
	{
		String directory = System.getProperty("user.dir");
		String separator = System.getProperty("file.separator");
		URL codebase = null;
		try
		{
			if (directory.startsWith("/"))
			{ // fix UNIX case
				directory = "/" + directory;
			}
			String urlString = "file:/" + directory + separator;
			codebase = new URL(urlString);
		} catch (MalformedURLException exx)
		{
			System.out.println("codebase URL error");
			System.out.println(exx.getMessage());
		}
		return codebase;
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		String name = ((Component) e.getItemSelectable()).getName();
		boolean value = (e.getStateChange() == ItemEvent.SELECTED);
		ToggleAttr attr = (ToggleAttr) render.context.getAttr(name);
		attr.set(value);
		render.update();
	}

	public void showControls()
	{
		JFrame dialog = new JFrame();
		dialog.setTitle("Settings");
		dialog.setSize(400, 800);
		dialog.setLocationRelativeTo(this);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(controlPanel);
		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String name = ((Component) e.getSource()).getName();
		String value = e.getActionCommand();
		if (e.getSource() == showControls)
		{
			showControls();
		} else if (name == exit)
		{
			System.exit(0);
		} else if (name == save)
		{
			// need to recreate chooser each time to rescan for files
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileFilter(sessionFileFilter);
			chooser.setDialogTitle("Save Vol Render Settings");
			if (settingsFile != null)
			{
				chooser.setSelectedFile(settingsFile);
			}
			int retval = chooser.showSaveDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION)
			{
				settingsFile = chooser.getSelectedFile();
				String filename = settingsFile.getName();
				if (!filename.endsWith("." + sessionSuffix))
				{
					filename = filename + "." + sessionSuffix;
				}
				render.context.save(filename, null);
			}
		} else if (name == restore)
		{
			// need to recreate chooser each time to rescan for files
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileFilter(sessionFileFilter);
			chooser.setDialogTitle("Restore Vol Render Settings");
			if (settingsFile != null)
			{
				chooser.setSelectedFile(settingsFile);
			}
			int retval = chooser.showOpenDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION)
			{
				settingsFile = chooser.getSelectedFile();
				String filename = settingsFile.getName();
				if (!filename.endsWith("." + sessionSuffix))
				{
					filename = filename + "." + sessionSuffix;
				}
				render.restoreContext(filename);
			}
		} else if (name == loadData)
		{
			// need to recreate chooser each time to rescan for files
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileFilter(dataFileFilter);
			chooser.setDialogTitle("Load Vol Render Data File");
			int retval = chooser.showOpenDialog(this);
			if (retval == JFileChooser.APPROVE_OPTION)
			{
				String filename = chooser.getSelectedFile().getAbsolutePath();

				if (!filename.endsWith("." + dataSuffix))
				{
					filename = filename + "." + dataSuffix;
				}
				System.out.println("filename : " + filename);
				render.dataFileAttr.set(filename);
				render.update();
			}
		} else if (name == cmapEdit)
		{
			editCmap();
		} else if (name == annoEdit)
		{
			if (annoEditDialog == null)
			{
				annoEditDialog = new AnnotationsEditDialog(null, render);
			}
			annoEditDialog.show();
		} else if (name == rotControl)
		{
			rotationControl.showControl();
		} else if (name == videoControl)
		{
			try
			{
				showVideoControler();
			} catch (AWTException e1)
			{
				JOptionPane
						.showMessageDialog(null, "Error : "
								+ e1.getLocalizedMessage(), "Error Showing Controls", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		} else
		{
			System.out.println("action: set attr " + name + " to value "
					+ value);
			render.context.getAttr(name).set(value);
			render.update();
		}
	}

	public void showVideoControler() throws AWTException
	{

		

		videoFrame.setSize(400, 150);
		videoFrame.getContentPane().setLayout(new BorderLayout());
		videoFrame.getContentPane().add(getRecorder().getControler());
		videoFrame.setVisible(true);

	}

	/**
	 * This should be called to update the cMap data it was mainly made for use
	 * with userChoiceColorMap to change the volume data when the volume was
	 * changed
	 */
	public void updateCmap(StatusBarPanel status)
	{
		Colormap cmap = render.getColorModeAttr().getColormap();
		if (cmap instanceof SegyColormap)
		{
			SegyColormap segyCmap = (SegyColormap) cmap;
			segyCmap.updateMapping();
		} else if (cmap instanceof UserChoiceColorMap)
		{
			UserChoiceColorMap map = (UserChoiceColorMap) cmap;
			map.updateMap();
			map.updateVolumeData(getRender().volume.vol.fileData, status);

		}
		render.update();
	}

	public void editCmap()
	{
		editCmap(true, null);
	}

	public void editCmap(boolean show, StatusBarPanel status)
	{
		System.out.println("Edit CMap - Show[" + show + "]");

		Colormap cmap = render.getColorModeAttr().getColormap();
		if (cmap instanceof SegyColormap)
		{
			SegyColormap segyCmap = (SegyColormap) cmap;
			if (segyCmapEditDialog == null)
			{
				segyCmapEditDialog = new SegyCmapEditDialog(null, render,
						segyCmap);
			}
			segyCmapEditDialog.setAlwaysOnTop(true);
			if (show)
			{
				segyCmapEditDialog.show();
			}
		} else if (cmap instanceof UserChoiceColorMap)
		{
			UserChoiceColorMap map = (UserChoiceColorMap) cmap;
			map.updateVolumeData(getRender().volume.vol.fileData, status);

			updateCmapInputPanel(map, status);
			if (show)
			{
				colorMapFrame.setVisible(true);
			}

		}
	}

	public void updateCmapInputPanel(UserChoiceColorMap map, final StatusBarPanel status)
	{
		if (updateCmap == null)
		{
			colorMapFrame.setSize(400, 600);
			updateCmap = new JButton("Update");
			class MapUpdateListner implements ActionListener
			{
				VolRend rend;

				public MapUpdateListner(VolRend rend)
				{
					this.rend = rend;
				}

				@Override
				public void actionPerformed(ActionEvent e)
				{
					System.gc();
					UserChoiceColorMap map = (UserChoiceColorMap) render
							.getColorModeAttr().getColormap();
					map.updateMap();
					map
							.updateVolumeData(getRender().volume.vol.fileData, status);

					rend.update();
				}
			}

			updateCmap.addActionListener(new MapUpdateListner(render));
		}

		colorMapFrame.getContentPane().removeAll();
		System.out.println("Adding Stuff");
		colorMapFrame.getContentPane().setLayout(new BorderLayout());
		colorMapFrame.getContentPane()
				.add(map.getUserSelectionPanel(), BorderLayout.CENTER);
		colorMapFrame.getContentPane().add(updateCmap, BorderLayout.SOUTH);
		colorMapFrame.validate();
		System.out.println("All Done...");
	}

	public void setData(byte[][][] data)
	{
		VolArrayFile vol = new VolArrayFile(data, 1, 1, 1);
		getRender().setVolumeFile(vol);
	}
	
	public void updateData()
	{
		getRender().update();
	}
	
	public static void main(String[] input)
	{
		VolumeViewerPanel view = new VolumeViewerPanel();

		int size = 256;

		double[] min = new double[]
		{ 0.1 * size, 0.5 * size, 0.8 * size };
		double[] max = new double[]
		{ 0.2 * size, 0.6 * size, 0.9 * size };
		byte[][][] data = new byte[size][size][size];

		for (int x = 0; x < size; x++)
		{
			for (int y = 0; y < size; y++)
			{
				for (int z = 0; z < size; z++)
				{
					data[x][y][z] = 0;
					for (int i = 0; i < min.length; i++)
					{
						if (x > min[i] && x < max[i])
							if (y > min[i] && y < max[i])
								if (z > min[i] && z < max[i])
									data[x][y][z] = (byte) 255;
					}
				}
			}
		}

		VolArrayFile vol = new VolArrayFile(data, 1, 1, 1);
		view.getRender().setVolumeFile(vol);
		JPanel pa = new JPanel();
		pa.setLayout(new BorderLayout());
		pa.add(view, BorderLayout.CENTER);
		pa.add(view.getControlPanel(), BorderLayout.WEST);

		JFrame f = FrameFactroy.getFrame(pa);

		f.setVisible(true);
	}

	public JPanel getControlPanel()
	{
		return controlPanel;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		// double[] transformData = (double[]) in.readObject();
		// Transform3D transform = new Transform3D(transformData);
		// render.setXForm(transform);

		Quat4d rot = (Quat4d) in.readObject();
		render.getRotationAttr().set(rot);

		double dat = in.readDouble();
		render.getScaleAttr().set(dat);

		Vector3d vec = (Vector3d) in.readObject();
		render.getTranslationAttr().set(vec);

		// Copy CMAP data
		render.getColorModeAttr().colormaps[0] = (UserChoiceColorMap) in
				.readObject();

		render.getAnnotationsAttr().setValue(in.readBoolean());
		// render.displayAxisAttr.value = in.readInt();
		render.perspectiveAttr.setValue(in.readBoolean());
		render.coordSysAttr.setValue(in.readBoolean());

		render.rendererAttr.value = in.readInt();
		render.texColorMapAttr.setValue(in.readBoolean());
		render.getColorModeAttr().value = in.readInt();

		reloadColorMap(null);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		double[] transformData = new double[16];
		// Copy Transform
		// Transform3D transform = render.getTransform();
		// transform.get(transformData);
		//		
		//		
		// out.writeObject(transformData);

		out.writeObject(render.getRotationAttr().getValue());
		out.writeDouble(render.getScaleAttr().getValue());
		out.writeObject(render.getTranslationAttr().getValue());

		// Copy CMAP data
		UserChoiceColorMap cmap = (UserChoiceColorMap) render
				.getColorModeAttr().colormaps[0];
		out.writeObject(cmap);

		out.writeBoolean(render.getAnnotationsAttr().isValue());
		// out.writeInt(render.displayAxisAttr.value);
		out.writeBoolean(render.perspectiveAttr.isValue());
		out.writeBoolean(render.coordSysAttr.isValue());

		out.writeInt(render.rendererAttr.value);

		out.writeBoolean(render.texColorMapAttr.isValue());
		out.writeInt(render.getColorModeAttr().value);

	}

	public void reloadColorMap(StatusBarPanel status)
	{
		System.out.println("ReloadColorMap - VolumeViewerPanel");
		editCmap(false, status);
	}

	public CompoentRecorder getRecorder() throws AWTException
	{
		if (recorder == null)
		{
			recorder = new CompoentRecorder(render.canvas);
		}
		return recorder;
	}
}
