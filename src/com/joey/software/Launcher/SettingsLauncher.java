package com.joey.software.Launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;

import com.joey.software.LAFTools.EditableLAFControler;
import com.joey.software.stringToolkit.StringOperations;


/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SettingsLauncher
{
	static String path = null;

	static private JPanel jPanel1;

	static private JLabel jLabel1;

	static HashMap<Integer, int[]> ramSizeData = new HashMap<Integer, int[]>();

	static HashMap<Integer, String> ramName = new HashMap<Integer, String>();

	static HashMap<Integer, Integer> vRamSizeData = new HashMap<Integer, Integer>();

	static HashMap<Integer, String> vRamName = new HashMap<Integer, String>();

	public static void createFieldData()
	{
		// Ram Size
		ramName.put(0, "256 Mb");
		ramSizeData.put(0, new int[]
		{ 100, 110, 150 });

		ramName.put(1, "512 Mb");
		ramSizeData.put(1, new int[]
		{ 150, 200, 300 });

		ramName.put(2, "768 Mb");
		ramSizeData.put(2, new int[]
		{ 200, 250, 500 });

		ramName.put(3, "1 Gb");
		ramSizeData.put(3, new int[]
		{ 300, 500, 1000 });

		ramName.put(4, "> 1 Gb Mb");
		ramSizeData.put(4, new int[]
		{ 300, 500, 1000 });

		ramName.put(5, "Manual");
		ramSizeData.put(5, new int[]
		{ -1, -1, -1 });

		// Video Ram Size
		vRamName.put(0, "32 Mb");
		vRamSizeData.put(0, 30 * 1024 * 1024);

		vRamName.put(1, "64 Mb");
		vRamSizeData.put(1, 60 * 1024 * 1024);

		vRamName.put(2, "128 Mb");
		vRamSizeData.put(2, 124 * 1024 * 1024);

		vRamName.put(3, "256 Mb");
		vRamSizeData.put(3, 254 * 1024 * 1024);

		vRamName.put(4, "512 Mb");
		vRamSizeData.put(4, 510 * 1024 * 1024);

		vRamName.put(5, "Manual");
		vRamSizeData.put(5, -1);
	}

	public static void main(String input[]) throws HeadlessException,
			IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException
	{

		SettingsLauncher.setActivePath(MainLauncher.class);

		createFieldData();
		// Test if administrator access is needed
		if (!MainLauncher.checkReadWriteAccess())
		{
			JOptionPane
					.showMessageDialog(null, "Please launch the program using Edit Settings (Elevated)");
			return;
		}

		MainLauncher.setLAF();

		int labelSize = 120;
		JComboBox ramSize = new JComboBox(ramName.values()
				.toArray(new String[0]));

		JLabel headRam = new JLabel("Ram : ");
		JPanel panelRam = new JPanel(new BorderLayout());
		panelRam.add(headRam, BorderLayout.WEST);
		panelRam.add(ramSize, BorderLayout.CENTER);

		JComboBox videoCardSize = new JComboBox(vRamName.values()
				.toArray(new String[0]));
		JLabel headVid = new JLabel("Video Memory : ");
		JPanel panelVid = new JPanel(new BorderLayout());
		panelVid.add(headVid, BorderLayout.WEST);
		panelVid.add(videoCardSize, BorderLayout.CENTER);

		headVid.setPreferredSize(new Dimension(labelSize, 0));
		headVid.setHorizontalAlignment(SwingConstants.RIGHT);

		headRam.setPreferredSize(new Dimension(labelSize, 0));
		headRam.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel elevatedLabel = new JLabel("Elevated : ");
		elevatedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		elevatedLabel.setPreferredSize(new Dimension(labelSize, 0));

		JCheckBox elevated = new JCheckBox();

		JPanel elevatedPanel = new JPanel(new BorderLayout());
		elevatedPanel.add(elevatedLabel, BorderLayout.WEST);
		elevatedPanel.add(elevated, BorderLayout.CENTER);

		JLabel textureNPOTLabel = new JLabel("Allow NPO Texture : ");
		textureNPOTLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		textureNPOTLabel.setPreferredSize(new Dimension(labelSize, 0));

		JCheckBox textureNPOT = new JCheckBox();

		JPanel textureNPOTPanel = new JPanel(new BorderLayout());
		textureNPOTPanel.add(textureNPOTLabel, BorderLayout.WEST);
		textureNPOTPanel.add(textureNPOT, BorderLayout.CENTER);

		EditableLAFControler LAFPanel = new EditableLAFControler(getPath()
				+ "\\LAF\\");

		JLabel label = new JLabel("Theme :");
		label.setPreferredSize(new Dimension(labelSize, 0));
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		JPanel look = new JPanel(new BorderLayout());
		look.add(LAFPanel, BorderLayout.CENTER);
		look.add(label, BorderLayout.WEST);

		JPanel panel = new JPanel(new GridLayout(2, 1));
		GridLayout panelLayout = new GridLayout(6, 1);
		panelLayout.setHgap(5);
		panelLayout.setColumns(1);
		panelLayout.setRows(6);
		panelLayout.setVgap(5);
		panel.setLayout(panelLayout);
		{
			jPanel1 = new JPanel();
			panel.add(jPanel1);
			{
				jLabel1 = new JLabel();
				jPanel1.add(jLabel1);
				jLabel1.setText("Please enter the following information");
			}
		}
		panel.add(panelRam);
		panel.add(panelVid);
		panel.add(elevatedPanel);
		panel.add(textureNPOTPanel);
		panel.add(look);

		panel.setBorder(BorderFactory.createTitledBorder(""));

		LAFPanel.view = panel;
		// Set Data from the file
		try
		{
			elevated.setSelected(readElevated(getPath() + "settings.dat"));
			ramSize.setSelectedIndex(readRamIndex(getPath() + "settings.dat"));
			videoCardSize.setSelectedIndex(readVideoIndex(getPath()
					+ "settings.dat"));
			textureNPOT
					.setSelected(readTextureNPOT(getPath() + "settings.dat"));
			LAFPanel.setLAF(readLAF(getPath() + "settings.dat"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		JPanel panelHolder = new JPanel(new BorderLayout());
		panelHolder.setPreferredSize(new Dimension(600, 400));
		panelHolder.add(panel, BorderLayout.NORTH);

		if (JOptionPane
				.showConfirmDialog(null, panelHolder, "Settings", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
		{
			int xmn = 1;
			int xms = 1;
			int xmx = 1;

			int vram = 0;
			boolean noddraw = true;

			/**
			 * Determine Videocardsize
			 */
			if (videoCardSize.getSelectedIndex() == vRamSizeData.size() - 1)
			{
				JSpinner vRamByte = new JSpinner(new SpinnerNumberModel(
						readVRam(getPath() + "settings.dat"), 1024,
						1024 * 1024 * 1024, 1024 * 1024));
				JOptionPane
						.showMessageDialog(null, vRamByte, "Enter Video Ram in bytes", JOptionPane.QUESTION_MESSAGE);
				vram = (Integer) vRamByte.getValue();
			} else
			{
				vram = vRamSizeData.get(videoCardSize.getSelectedIndex());
			}

			/**
			 * Determine Ram Size
			 */
			// Determine Ram Size
			if (ramSize.getSelectedIndex() == ramSizeData.size() - 1)
			{

				JSpinner xmnVal = new JSpinner(new SpinnerNumberModel(
						readXMN(getPath() + "settings.dat"), 1, 1024, 1));

				JSpinner xmsVal = new JSpinner(new SpinnerNumberModel(
						readXMS(getPath() + "settings.dat"), 1, 1024, 1));

				JSpinner xmxVal = new JSpinner(new SpinnerNumberModel(
						readXMX(getPath() + "settings.dat"), 1, 1024, 1));

				JPanel labels = new JPanel(new GridLayout(3, 1));
				labels.add(new JLabel("Xmn :"));
				labels.add(new JLabel("Xms :"));
				labels.add(new JLabel("Xmx :"));

				JPanel data = new JPanel(new GridLayout(3, 1));
				data.add(xmnVal);
				data.add(xmsVal);
				data.add(xmxVal);

				JPanel holder = new JPanel(new GridLayout(1, 2));
				holder.add(labels);
				holder.add(data);

				JOptionPane
						.showMessageDialog(null, holder, "Enter Advanced Ram Settings Mb", JOptionPane.QUESTION_MESSAGE);
				xms = (Integer) xmsVal.getValue();
				xmn = (Integer) xmnVal.getValue();
				xmx = (Integer) xmxVal.getValue();
			} else
			{
				int[] dat = ramSizeData.get(ramSize.getSelectedIndex());

				xmn = dat[0];
				xms = dat[1];
				xmx = dat[2];
			}

			try
			{
				saveSettings(LAFPanel.getLAF(), ramSize.getSelectedIndex(), videoCardSize
						.getSelectedIndex(), xmn, xms, xmx, vram, elevated
						.isSelected(), textureNPOT.isSelected(), getPath()
						+ "settings.dat");
				writeLauncher(xmn, xms, xmx, vram, noddraw, new File(getPath()
						+ "launch.bat"), elevated.isSelected());
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane
						.showMessageDialog(null, "Error writing launcher :"
								+ e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void writeLicience(String licienceFile) throws IOException
	{
		File f = new File(getPath() + licienceFile);

		DataOutputStream out = new DataOutputStream(new FileOutputStream(f));

		// Block 1
		int randBlock1 = 10 + (int) (1000 * Math.random());
		out.writeInt(randBlock1);
		for (int i = 0; i < randBlock1; i++)
		{
			out.writeInt((int) (30000 * Math.random()));
		}

		// Last Launch
		out.writeLong(System.currentTimeMillis());

		// Block 2
		int randBlock2 = 10 + (int) (1000 * Math.random());
		out.writeInt(randBlock2);
		for (int i = 0; i < randBlock2; i++)
		{
			out.writeInt((int) (30000 * Math.random()));
		}
	}

	public static long readLastDate(String licienceFile) throws IOException
	{
		File f = new File(getPath() + licienceFile);

		DataInputStream in = new DataInputStream(new FileInputStream(f));

		// Block 1
		int randBlock1 = in.readInt();
		for (int i = 0; i < randBlock1; i++)
		{
			in.readInt();
		}
		return in.readLong();
	}

	public static void testLicience(String licienceFile) throws IOException
	{
		File f = new File(getPath() + licienceFile);

		if (!f.exists())
		{
			//JOptionPane
			//		.showMessageDialog(null, "There was and error reading the licience file\nThe program will now exit", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("J3D error NF");
			System.exit(0);
		}

		long lastLong = readLastDate(licienceFile);

		GregorianCalendar last = new GregorianCalendar();
		last.setTimeInMillis(lastLong);

		GregorianCalendar today = new GregorianCalendar();
		today.setTimeInMillis(System.currentTimeMillis());

		if (today.before(last))
		{
			//JOptionPane
			//		.showMessageDialog(null, "It appears that the date and time on the computer is incorrect\nThe program will now exit", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("J3D error B");
			System.exit(0);
		}

		GregorianCalendar end = new GregorianCalendar(2011, Calendar.JANUARY,
				30);

		if (today.after(end))
		{
			System.out.println("J3D error A");
			//JOptionPane
			//		.showMessageDialog(null, "This trial version has expired\nThe program will now exit", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		long mills_per_day = 1000 * 60 * 60 * 24;

		long day_diff = (today.getTimeInMillis() - end.getTimeInMillis())
				/ mills_per_day;

		day_diff = Math.abs(day_diff);
		if (day_diff <= 1)
		{
		//	JOptionPane.showMessageDialog(null, "There is " + day_diff
		//			+ " day remaining in this trial version");
		} else
		{
		//	JOptionPane.showMessageDialog(null, "There is " + day_diff
		//			+ " days remaining in this trial version");
		}
	}

	public static void saveSettings(int LAF, int ramIndex, int videoIndex, int xmn, int xms, int xmx, int vram, boolean elevated, boolean allowNPOTTexture, String name)
			throws IOException
	{
		DataOutputStream out = new DataOutputStream(new FileOutputStream(name));
		for (int i = 0; i < 1000; i++)
		{
			out.writeInt((int) Math.random());
		}
		out.writeInt(xmn);
		out.writeInt(xms);
		out.writeInt(xmx);
		out.writeInt(vram);
		out.writeBoolean(elevated);
		out.writeInt(ramIndex);
		out.writeInt(videoIndex);
		out.writeBoolean(allowNPOTTexture);
		out.writeInt(LAF);
		for (int i = 0; i < 1000; i++)
		{
			out.writeInt((int) Math.random());
		}
		out.flush();
		out.close();

	}

	public static int readLAF(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		int val;

		in.readInt();
		in.readInt();
		in.readInt();
		in.readInt();
		in.readBoolean();
		in.readInt();
		in.readInt();
		in.readBoolean();
		val = in.readInt();
		in.close();
		return val;
	}

	public static boolean readTextureNPOT(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		boolean val;

		in.readInt();
		in.readInt();
		in.readInt();
		in.readInt();
		in.readBoolean();
		in.readInt();
		in.readInt();
		val = in.readBoolean();
		in.close();
		return val;
	}

	public static int readVideoIndex(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		int val;

		in.readInt();
		in.readInt();
		in.readInt();
		in.readInt();
		in.readBoolean();
		in.readInt();
		val = in.readInt();
		in.close();
		return val;
	}

	public static int readRamIndex(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		int val;

		in.readInt();
		in.readInt();
		in.readInt();
		in.readInt();
		in.readBoolean();
		val = in.readInt();
		in.readInt();
		in.close();
		return val;
	}

	public static boolean readElevated(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		boolean val;

		in.readInt();
		in.readInt();
		in.readInt();
		in.readInt();
		val = in.readBoolean();
		in.close();
		return val;
	}

	public static int readXMX(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		int val;

		in.readInt();
		in.readInt();
		val = in.readInt();
		in.readInt();
		in.close();
		return val;
	}

	public static int readXMS(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		int val;

		in.readInt();
		val = in.readInt();
		in.readInt();
		in.readInt();
		in.close();
		return val;
	}

	public static int readXMN(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		int val;

		val = in.readInt();
		in.readInt();
		in.readInt();
		in.readInt();
		in.close();
		return val;
	}

	public static int readVRam(String name) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(name));

		for (int i = 0; i < 1000; i++)
		{
			in.readInt();
		}
		in.readInt();
		in.readInt();
		in.readInt();
		int val = in.readInt();

		in.close();
		return val;

	}

	public static void writeLauncher(int xmn, int xms, int xmx, int ram, boolean noddraw, File norm, boolean elavated)
			throws IOException
	{
		String test;

		if (elavated)
		{
			test = "cd \"" + getPath()
					+ "\"\nstart Elevate javaw -Dsun.java2d.noddraw=" + noddraw
					+ " -Xmn" + xmn + "M -Xms" + xms + "M -Xmx" + xmx
					+ "M -cp \"" + getPath()
					+ "LAF\\NimrodLAF\\nimrodlf.jar\" -jar \"" + getPath()
					+ "program.dll\" %1";
		} else
		{
			test = "cd \"" + getPath()
					+ "\"\nstart javaw -Dsun.java2d.noddraw=" + noddraw
					+ " -Xmn" + xmn + "M -Xms" + xms + "M -Xmx" + xmx
					+ "M -cp \"" + getPath()
					+ "LAF\\NimrodLAF\\nimrodlf.jar\" -jar \"" + getPath()
					+ "program.dll\" %1";
		}

		PrintWriter out;
		out = new PrintWriter(norm);
		out.write(test);
		out.flush();
		out.close();
	}

	public static String getPath()
	{
		if (path == null)
		{
			path = System.getProperty("user.dir");
		}

		return path;
	}

	/**
	 * This function will change the active path to the folder that the current
	 * Class
	 * 
	 * @param class1
	 * @return
	 */
	public static String setActivePath(Class class1)
	{
		try
		{
			File f = new File(class1.getProtectionDomain().getCodeSource()
					.getLocation().getFile());

			String path = f.getParent().toString() + "\\";

			path = StringOperations.replace(path, "%20", " ");
			System.setProperty("user.dir", path);
			return f.toString();
		} catch (Exception e)
		{
			JOptionPane
					.showMessageDialog(null, "Failed Setting the Active Directory :"
							+ e.toString());
		}

		return null;
	}
}
