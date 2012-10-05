package com.joey.software.userinterface;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.joey.software.DataToolkit.NativeDataSet;
import com.joey.software.framesToolkit.StatusBarPanel;
import com.joey.software.mainProgram.OCTAnalysis;
import com.joey.software.mainProgram.OCTExperimentData;
import com.joey.software.mainProgram.OCTViewDataHolder;
import com.joey.software.sliceTools.OCTSliceViewerDataHolder;
import com.joey.software.volumeTools.OCTVolumeDividerDataHolder;


public class VersionManager
{
	public static final long VERSION_1 = 1L;

	public static final long VERSION_2 = 2L;

	public static final long VERSION_3 = 3L;

	public static final long VERSION_4 = 4L;

	public static final long VERSION_5 = 5L;

	private static final long serialVersionUID = VERSION_5;

	/**
	 * this are used to store what is being currently loaded (this is used in
	 * sub classes that are loaded to determine the current version
	 */
	private static long currentLoadingVersion = VERSION_5;

	private static long currentSavingVersion = VERSION_5;

	public static long getCurrentVersion()
	{
		return serialVersionUID;
	}

	public static long getCurrentLoadingVersion()
	{
		return currentLoadingVersion;
	}

	public static long getCurrentSavingVersion()
	{
		return currentSavingVersion;
	}

	public static void saveData(OCTAnalysis owner, File f, StatusBarPanel status)
			throws IOException
	{
		saveData(owner, f, status, getCurrentVersion());
	}

	public static void saveData(OCTAnalysis owner, File f, StatusBarPanel status, long version)
			throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		currentSavingVersion = version;
		switch ((int) version)
		{
			case (int) VERSION_1:
			{
				System.out.println("Saving Data");
				out.writeInt((int) getCurrentVersion());
				out.writeInt(owner.getExpData().size());

				for (int i = 0; i < owner.getExpData().size(); i++)
				{
					System.out.println("Saving : " + i);
					OCTExperimentData data = owner.getExpData().get(i);
					// Experiment Name
					out.writeUTF(data.getTitle());
					// OCT Dataset
					out.writeObject(data.getData());

					// Number of Views
					out.writeInt(data.getViews().size());

					for (int j = 0; j < data.getViews().size(); j++)
					{
						// Each View
						OCTViewDataHolder view = data.getViews().get(j);

						out.writeUTF(view.getName());
						out.writeObject(view.getSliceData());
						out.writeObject(view.getVolumeData());
						System.out.println(view.getVolumeData().rot);
					}
				}
				System.out.println("OK");
				break;
			}
			case (int) VERSION_2:
			{
				System.out.println("Saving Data : Version 2");
				out.writeInt((int) version);
				out.writeInt(owner.getExpData().size());

				for (int i = 0; i < owner.getExpData().size(); i++)
				{
					System.out.println("Saving : " + i);
					OCTExperimentData data = owner.getExpData().get(i);
					// Experiment Name
					out.writeUTF(data.getTitle());
					// OCT Dataset
					out.writeObject(data.getData());

					// Number of Views
					out.writeInt(data.getViews().size());

					for (int j = 0; j < data.getViews().size(); j++)
					{
						// Each View
						OCTViewDataHolder view = data.getViews().get(j);

						out.writeUTF(view.getName());
						out.writeObject(view.getSliceData());
						out.writeObject(view.getVolumeData());

						// Write measure data
						Point2D.Double p1x = view.getSliceData().p1x;
						Point2D.Double p2x = view.getSliceData().p2x;

						Point2D.Double p1y = view.getSliceData().p1y;
						Point2D.Double p2y = view.getSliceData().p2y;

						Point2D.Double p1z = view.getSliceData().p1z;
						Point2D.Double p2z = view.getSliceData().p2z;

						out.writeObject(p1x);
						out.writeObject(p2x);

						out.writeObject(p1y);
						out.writeObject(p2y);

						out.writeObject(p1z);
						out.writeObject(p2z);
					}
				}
				System.out.println("OK");
				break;
			}
			case (int) VERSION_3:
			{
				// System.out.println("Saving Data : Version 3");
				out.writeInt((int) version);
				out.writeInt(owner.getExpData().size());

				for (int i = 0; i < owner.getExpData().size(); i++)
				{
					// System.out.println("Saving : " + i);
					OCTExperimentData data = owner.getExpData().get(i);
					// Experiment Name
					out.writeUTF(data.getTitle());
					// OCT Dataset

					out.writeObject(data.getData());

					// Write power for backward compabability
					out.writeInt(data.getData().getPowerX());
					out.writeInt(data.getData().getPowerY());
					out.writeInt(data.getData().getPowerZ());

					// Number of Views
					out.writeInt(data.getViews().size());

					for (int j = 0; j < data.getViews().size(); j++)
					{
						// Each View
						OCTViewDataHolder view = data.getViews().get(j);

						out.writeUTF(view.getName());
						out.writeObject(view.getSliceData());
						out.writeObject(view.getVolumeData());

						// Write measure data
						Point2D.Double p1x = view.getSliceData().p1x;
						Point2D.Double p2x = view.getSliceData().p2x;

						Point2D.Double p1y = view.getSliceData().p1y;
						Point2D.Double p2y = view.getSliceData().p2y;

						Point2D.Double p1z = view.getSliceData().p1z;
						Point2D.Double p2z = view.getSliceData().p2z;

						out.writeObject(p1x);
						out.writeObject(p2x);

						out.writeObject(p1y);
						out.writeObject(p2y);

						out.writeObject(p1z);
						out.writeObject(p2z);
					}
				}
				// System.out.println("OK");
				break;
			}
			case (int) VERSION_4:
			{
				// System.out.println("Saving Data : Version 3");
				out.writeInt((int) version);
				out.writeInt(owner.getExpData().size());

				for (int i = 0; i < owner.getExpData().size(); i++)
				{
					// System.out.println("Saving : " + i);
					OCTExperimentData data = owner.getExpData().get(i);
					// Experiment Name
					out.writeUTF(data.getTitle());
					// OCT Dataset

					out.writeObject(data.getData());

					// Write power for backward compabability
					out.writeInt(data.getData().getPowerX());
					out.writeInt(data.getData().getPowerY());
					out.writeInt(data.getData().getPowerZ());

					// Number of Views
					out.writeInt(data.getViews().size());

					for (int j = 0; j < data.getViews().size(); j++)
					{
						// Each View
						OCTViewDataHolder view = data.getViews().get(j);

						out.writeUTF(view.getName());
						out.writeObject(view.getSliceData());
						out.writeObject(view.getVolumeData());

						// Write measure data
						Point2D.Double p1x = view.getSliceData().p1x;
						Point2D.Double p2x = view.getSliceData().p2x;

						Point2D.Double p1y = view.getSliceData().p1y;
						Point2D.Double p2y = view.getSliceData().p2y;

						Point2D.Double p1z = view.getSliceData().p1z;
						Point2D.Double p2z = view.getSliceData().p2z;

						out.writeObject(p1x);
						out.writeObject(p2x);

						out.writeObject(p1y);
						out.writeObject(p2y);

						out.writeObject(p1z);
						out.writeObject(p2z);
					}
				}
				// System.out.println("OK");
				break;
			}
			case (int) VERSION_5:
			{
				// System.out.println("Saving Data : Version 3");
				out.writeInt((int) version);
				out.writeInt(owner.getExpData().size());

				for (int i = 0; i < owner.getExpData().size(); i++)
				{
					// System.out.println("Saving : " + i);
					OCTExperimentData data = owner.getExpData().get(i);
					// Experiment Name
					out.writeUTF(data.getTitle());
					// OCT Dataset

					out.writeObject(data.getData());

					// Write power for backward compabability
					out.writeInt(data.getData().getPowerX());
					out.writeInt(data.getData().getPowerY());
					out.writeInt(data.getData().getPowerZ());

					// Number of Views
					out.writeInt(data.getViews().size());

					for (int j = 0; j < data.getViews().size(); j++)
					{
						// Each View
						OCTViewDataHolder view = data.getViews().get(j);

						out.writeUTF(view.getName());
						out.writeObject(view.getSliceData());
						out.writeObject(view.getVolumeData());

						// Write measure data
						Point2D.Double p1x = view.getSliceData().p1x;
						Point2D.Double p2x = view.getSliceData().p2x;

						Point2D.Double p1y = view.getSliceData().p1y;
						Point2D.Double p2y = view.getSliceData().p2y;

						Point2D.Double p1z = view.getSliceData().p1z;
						Point2D.Double p2z = view.getSliceData().p2z;

						out.writeObject(p1x);
						out.writeObject(p2x);

						out.writeObject(p1y);
						out.writeObject(p2y);

						out.writeObject(p1z);
						out.writeObject(p2z);

						System.out.println("\n\n\n\nSaving Data");
						// Write Area Data
						out.writeObject(view.getSliceData().xArea);
						out.writeObject(view.getSliceData().yArea);
						out.writeObject(view.getSliceData().zArea);

						// Write Path Data
						out.writeObject(view.getSliceData().xPath);
						out.writeObject(view.getSliceData().yPath);
						out.writeObject(view.getSliceData().zPath);

					}
				}
				// System.out.println("OK");
				break;
			}
		}

		// try and close the file
		try
		{
			out.close();
		} catch (Exception e)
		{
			// This doesn't matter
		}
	}

	public static boolean loadingData = false;

	public static void loadData(final OCTAnalysis owner, final File f, final StatusBarPanel status)
	{

		System.out.println("Loading");
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				loadingData = true;
				try
				{
					ObjectInputStream in = new ObjectInputStream(
							new FileInputStream(f));

					int version = in.readInt();
					currentLoadingVersion = version;
					System.out.println("Version : " + version);
					if (version == (int) VERSION_1)
					{ // Version 1.0
						status.setStatusMessage("Starting Loading Version 1");
						int expCount = in.readInt();

						status.setStatusMessage("Loading Data");
						status.setMaximum(expCount);
						for (int i = 0; i < expCount; i++)
						{
							try
							{
								status.setValue(i);
								String name = in.readUTF();

								NativeDataSet data = (NativeDataSet) in
										.readObject();
								data.unloadData();
								OCTExperimentData exp = new OCTExperimentData(
										owner, data, name);

								// Number of Views
								int viewCount = in.readInt();

								for (int j = 0; j < viewCount; j++)
								{
									String viewName = in.readUTF();
									OCTSliceViewerDataHolder slice = (OCTSliceViewerDataHolder) in
											.readObject();
									OCTVolumeDividerDataHolder vol = (OCTVolumeDividerDataHolder) in
											.readObject();

									Point2D.Double p1x = new Point2D.Double();
									Point2D.Double p2x = new Point2D.Double();

									Point2D.Double p1y = new Point2D.Double();
									Point2D.Double p2y = new Point2D.Double();

									Point2D.Double p1z = new Point2D.Double();
									Point2D.Double p2z = new Point2D.Double();

									slice.p1x = p1x;
									slice.p2x = p2x;

									slice.p1y = p1y;
									slice.p2y = p2y;

									slice.p1z = p1z;
									slice.p2z = p2z;

									OCTViewDataHolder view = new OCTViewDataHolder(
											exp, viewName, slice, vol);
									exp.addView(view);
								}
								System.out.println("Loading Data");
								owner.addExperiment(exp);
							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}

						status.reset();

					} else if (version == (int) VERSION_2)
					{ // Version 2.0
						int expCount = in.readInt();

						status.setStatusMessage("Loading Data version 2");
						status.setMaximum(expCount);
						for (int i = 0; i < expCount; i++)
						{
							// System.out.println("Loading Data : "+i);
							try
							{
								status.setValue(i);
								String name = in.readUTF();
								// System.out.println("Reading");
								NativeDataSet data = (NativeDataSet) in
										.readObject();
								System.out.println("Unloading Data");
								data.unloadData();
								// System.out.println("Reading Experiment");
								OCTExperimentData exp = new OCTExperimentData(
										owner, data, name);

								// Number of Views
								int viewCount = in.readInt();

								for (int j = 0; j < viewCount; j++)
								{
									String viewName = in.readUTF();
									OCTSliceViewerDataHolder slice = (OCTSliceViewerDataHolder) in
											.readObject();
									OCTVolumeDividerDataHolder vol = (OCTVolumeDividerDataHolder) in
											.readObject();

									Point2D.Double p1x = (Double) in
											.readObject();
									Point2D.Double p2x = (Double) in
											.readObject();

									Point2D.Double p1y = (Double) in
											.readObject();
									Point2D.Double p2y = (Double) in
											.readObject();

									Point2D.Double p1z = (Double) in
											.readObject();
									Point2D.Double p2z = (Double) in
											.readObject();

									slice.p1x = p1x;
									slice.p2x = p2x;

									slice.p1y = p1y;
									slice.p2y = p2y;

									slice.p1z = p1z;
									slice.p2z = p2z;

									OCTViewDataHolder view = new OCTViewDataHolder(
											exp, viewName, slice, vol);
									// System.out.println("Reading View");
									exp.addView(view);
								}
								// System.out.println("Loading Data");
								owner.addExperiment(exp);
							} catch (Exception e)
							{
								System.out.println("Error : "
										+ e.getLocalizedMessage());
								e.printStackTrace();
							}
						}

						status.reset();

					} else if (version == (int) VERSION_3)
					{ // Version 3.0
						int expCount = in.readInt();

						status.setStatusMessage("Loading Data version 3");
						status.setMaximum(expCount);
						for (int i = 0; i < expCount; i++)
						{
							// System.out.println("Loading Data : "+i);
							try
							{
								status.setValue(i);
								String name = in.readUTF();
								// System.out.println("Reading");
								NativeDataSet data = (NativeDataSet) in
										.readObject();

								data.setPowerX(in.readInt());
								data.setPowerY(in.readInt());
								data.setPowerZ(in.readInt());

								System.out.println("Unloading Data");
								data.unloadData();
								// System.out.println("Reading Experiment");
								OCTExperimentData exp = new OCTExperimentData(
										owner, data, name);

								// Number of Views
								int viewCount = in.readInt();

								for (int j = 0; j < viewCount; j++)
								{
									String viewName = in.readUTF();
									OCTSliceViewerDataHolder slice = (OCTSliceViewerDataHolder) in
											.readObject();
									OCTVolumeDividerDataHolder vol = (OCTVolumeDividerDataHolder) in
											.readObject();

									Point2D.Double p1x = (Double) in
											.readObject();
									Point2D.Double p2x = (Double) in
											.readObject();

									Point2D.Double p1y = (Double) in
											.readObject();
									Point2D.Double p2y = (Double) in
											.readObject();

									Point2D.Double p1z = (Double) in
											.readObject();
									Point2D.Double p2z = (Double) in
											.readObject();

									slice.p1x = p1x;
									slice.p2x = p2x;

									slice.p1y = p1y;
									slice.p2y = p2y;

									slice.p1z = p1z;
									slice.p2z = p2z;

									OCTViewDataHolder view = new OCTViewDataHolder(
											exp, viewName, slice, vol);
									// System.out.println("Reading View");
									exp.addView(view);
								}
								// System.out.println("Loading Data");
								owner.addExperiment(exp);
							} catch (Exception e)
							{
								System.out.println("Error : "
										+ e.getLocalizedMessage());
								e.printStackTrace();
							}
						}

						status.reset();

					} else if (version == (int) VERSION_4)
					{ // Version 3.0
						int expCount = in.readInt();

						status.setStatusMessage("Loading Data version 3");
						status.setMaximum(expCount);
						for (int i = 0; i < expCount; i++)
						{
							// System.out.println("Loading Data : "+i);
							try
							{
								status.setValue(i);
								String name = in.readUTF();
								// System.out.println("Reading");
								NativeDataSet data = (NativeDataSet) in
										.readObject();

								data.setPowerX(in.readInt());
								data.setPowerY(in.readInt());
								data.setPowerZ(in.readInt());

								System.out.println("Unloading Data");
								data.unloadData();
								// System.out.println("Reading Experiment");
								OCTExperimentData exp = new OCTExperimentData(
										owner, data, name);

								// Number of Views
								int viewCount = in.readInt();

								for (int j = 0; j < viewCount; j++)
								{
									String viewName = in.readUTF();
									OCTSliceViewerDataHolder slice = (OCTSliceViewerDataHolder) in
											.readObject();
									OCTVolumeDividerDataHolder vol = (OCTVolumeDividerDataHolder) in
											.readObject();

									Point2D.Double p1x = (Double) in
											.readObject();
									Point2D.Double p2x = (Double) in
											.readObject();

									Point2D.Double p1y = (Double) in
											.readObject();
									Point2D.Double p2y = (Double) in
											.readObject();

									Point2D.Double p1z = (Double) in
											.readObject();
									Point2D.Double p2z = (Double) in
											.readObject();

									slice.p1x = p1x;
									slice.p2x = p2x;

									slice.p1y = p1y;
									slice.p2y = p2y;

									slice.p1z = p1z;
									slice.p2z = p2z;

									OCTViewDataHolder view = new OCTViewDataHolder(
											exp, viewName, slice, vol);
									// System.out.println("Reading View");
									exp.addView(view);
								}
								// System.out.println("Loading Data");
								owner.addExperiment(exp);
							} catch (Exception e)
							{
								System.out.println("Error : "
										+ e.getLocalizedMessage());
								e.printStackTrace();
							}
						}

						status.reset();

					} else if (version == (int) VERSION_5)
					{ // Version 3.0
						int expCount = in.readInt();

						status.setStatusMessage("Loading Data version 3");
						status.setMaximum(expCount);
						for (int i = 0; i < expCount; i++)
						{
							// System.out.println("Loading Data : "+i);
							try
							{
								status.setValue(i);
								String name = in.readUTF();
								// System.out.println("Reading");
								NativeDataSet data = (NativeDataSet) in
										.readObject();

								data.setPowerX(in.readInt());
								data.setPowerY(in.readInt());
								data.setPowerZ(in.readInt());

								System.out.println("Unloading Data");
								data.unloadData();
								// System.out.println("Reading Experiment");
								OCTExperimentData exp = new OCTExperimentData(
										owner, data, name);

								// Number of Views
								int viewCount = in.readInt();

								for (int j = 0; j < viewCount; j++)
								{
									String viewName = in.readUTF();
									OCTSliceViewerDataHolder slice = (OCTSliceViewerDataHolder) in
											.readObject();
									OCTVolumeDividerDataHolder vol = (OCTVolumeDividerDataHolder) in
											.readObject();

									Point2D.Double p1x = (Double) in
											.readObject();
									Point2D.Double p2x = (Double) in
											.readObject();

									Point2D.Double p1y = (Double) in
											.readObject();
									Point2D.Double p2y = (Double) in
											.readObject();

									Point2D.Double p1z = (Double) in
											.readObject();
									Point2D.Double p2z = (Double) in
											.readObject();

									slice.p1x = p1x;
									slice.p2x = p2x;

									slice.p1y = p1y;
									slice.p2y = p2y;

									slice.p1z = p1z;
									slice.p2z = p2z;

									System.out
											.println("\n\n\n\nReading Stuff\n\n\n");
									slice.xArea = (Vector<Double>) in
											.readObject();
									slice.yArea = (Vector<Double>) in
											.readObject();
									slice.zArea = (Vector<Double>) in
											.readObject();

									slice.xPath = (Vector<Double>) in
											.readObject();
									slice.yPath = (Vector<Double>) in
											.readObject();
									slice.zPath = (Vector<Double>) in
											.readObject();

									OCTViewDataHolder view = new OCTViewDataHolder(
											exp, viewName, slice, vol);
									// System.out.println("Reading View");
									exp.addView(view);
								}
								// System.out.println("Loading Data");
								owner.addExperiment(exp);
							} catch (Exception e)
							{
								System.out.println("Error : "
										+ e.getLocalizedMessage());
								e.printStackTrace();
							}
						}

						status.reset();

					}

					in.close();
				} catch (Exception e)
				{
					JOptionPane
							.showMessageDialog(null, "Error loading data", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("Error : " + e.getLocalizedMessage());
					e.printStackTrace();
				}

				loadingData = false;
				System.out.println("Done Loading");
			}
		};
		t.start();

	}
}
