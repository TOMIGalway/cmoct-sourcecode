package com.joey.software.VolumeToolkit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Timer;

import javax.media.j3d.Transform3D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;

public class RotationControlPanel extends JPanel implements ChangeListener
{
	JFrame f = new JFrame("Rot Control");

	int steps = 1000;

	int rotSteps = 1000;

	int ffps = 40;

	float rotInc = 1f / ffps;

	RotationControl control = new RotationControl(this, ffps);

	JSlider xSlide = new JSlider(-steps, steps, 0);

	JSlider ySlide = new JSlider(-steps, steps, 0);

	JSlider zSlide = new JSlider(-steps, steps, 0);

	JSlider wManualSlide = new JSlider(-steps, steps, 0);

	JSlider wAutomaticSlide = new JSlider(-rotSteps, rotSteps, 0);

	boolean allowUpdate = true;

	VolumeViewerPanel panel;

	JButton grabDataButton = new JButton("Get Current Value");

	Timer timer = new Timer(true);

	JTabbedPane tab = new JTabbedPane();

	Object lock = new Object();

	Hashtable<Integer, Component> label = new Hashtable<Integer, Component>();
	{
		label.put(0, new JLabel("Stop"));
		label.put(rotSteps, new JLabel("Fast"));
		label.put(-rotSteps, new JLabel("Fast"));
		wAutomaticSlide.setMajorTickSpacing(rotSteps);
	}

	public RotationControlPanel(VolumeViewerPanel pan)
	{
		this.panel = pan;
		createJPanel();
	}

	public void createJPanel()
	{
		JPanel axisSlidePanel = new JPanel(new GridLayout(3, 1));
		axisSlidePanel.add(xSlide);
		axisSlidePanel.add(ySlide);
		axisSlidePanel.add(zSlide);

		JPanel axisNamePanel = new JPanel(new GridLayout(3, 1));
		axisNamePanel.add(new JLabel("X :"));
		axisNamePanel.add(new JLabel("Y :"));
		axisNamePanel.add(new JLabel("Z :"));

		JPanel axesPanel = new JPanel();
		axesPanel.setLayout(new BorderLayout());
		axesPanel.add(axisNamePanel, BorderLayout.WEST);
		axesPanel.add(axisSlidePanel, BorderLayout.CENTER);

		JPanel autoRotControlPanel = new JPanel(new BorderLayout());
		autoRotControlPanel.add(new JLabel("Speed :"), BorderLayout.WEST);
		autoRotControlPanel.add(wAutomaticSlide, BorderLayout.CENTER);

		JPanel manualRotControlPanel = new JPanel(new BorderLayout());
		manualRotControlPanel.add(new JLabel("Rot :"), BorderLayout.WEST);
		manualRotControlPanel.add(wManualSlide, BorderLayout.CENTER);
		manualRotControlPanel.add(grabDataButton, BorderLayout.SOUTH);

		tab.addTab("Automatic", autoRotControlPanel);
		tab.addTab("Manual", manualRotControlPanel);

		setLayout(new BorderLayout());
		add(axesPanel, BorderLayout.NORTH);
		add(tab, BorderLayout.CENTER);
		add(new JPanel(), BorderLayout.SOUTH);
		xSlide.addChangeListener(this);
		ySlide.addChangeListener(this);
		zSlide.addChangeListener(this);
		wAutomaticSlide.addChangeListener(this);
		wManualSlide.addChangeListener(this);

		wAutomaticSlide.setSnapToTicks(true);
		int major = 1;
		int minor = 20;

		xSlide.setMajorTickSpacing(steps / major);
		ySlide.setMajorTickSpacing(steps / major);
		zSlide.setMajorTickSpacing(steps / major);
		wManualSlide.setMajorTickSpacing(steps / major);

		xSlide.setMinorTickSpacing(steps / minor);
		ySlide.setMinorTickSpacing(steps / minor);
		zSlide.setMinorTickSpacing(steps / minor);
		wManualSlide.setMinorTickSpacing(steps / minor);

		xSlide.setPaintTicks(true);
		ySlide.setPaintTicks(true);
		zSlide.setPaintTicks(true);
		wManualSlide.setPaintTicks(true);

		// xSlide.setSnapToTicks(true);
		// ySlide.setSnapToTicks(true);
		// zSlide.setSnapToTicks(true);
		// wManualSlide.setSnapToTicks(true);

		wAutomaticSlide.setLabelTable(label);
		wAutomaticSlide.setMinorTickSpacing(1);
		wAutomaticSlide.setPaintLabels(true);
		wAutomaticSlide.setPaintTicks(true);

		grabDataButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				copyData();
			}
		});
	}

	public void copyData()
	{
		Quat4d dat = panel.getRender().getRotationAttr().getValue();

		// Transform3D rot = new Transform3D();
		// rot.setRotation(dat);

		AxisAngle4d rot = new AxisAngle4d();
		rot.set(dat);

		int rotX = (int) (dat.x * steps);
		int rotY = (int) (dat.y * steps);
		int rotZ = (int) (dat.z * steps);
		int rotW = (int) (dat.w * steps);

		setAllowUpdate(false);
		xSlide.setValue(rotX);
		ySlide.setValue(rotY);
		zSlide.setValue(rotZ);
		wManualSlide.setValue(rotW);
		setAllowUpdate(true);
	}

	public void updateRotation()
	{
		double xRot = xSlide.getValue() / (double) (steps);
		double yRot = ySlide.getValue() / (double) (steps);
		double zRot = zSlide.getValue() / (double) (steps);
		double wRot = wManualSlide.getValue() / (double) (steps);

		wRot *= 3.14;
		// System.out.println(steps);
		// System.out.println(xSlide.getValue() + " - " + xRot);
		// System.out.println(ySlide.getValue() + " - " + yRot);
		// System.out.println(zSlide.getValue() + " - " + zRot);
		// System.out.println(wManualSlide.getValue() + " - " + wRot);

		Transform3D tra = new Transform3D();
		AxisAngle4d rotation = new AxisAngle4d();
		rotation.setX(xRot);
		rotation.setY(yRot);
		rotation.setZ(zRot);
		rotation.setAngle(wRot);

		tra.set(rotation);

		Quat4d dat = new Quat4d();
		tra.get(dat);
		// System.out.println("\nSetting Values\n" + dat.x);
		// System.out.println(dat.y);
		// System.out.println(dat.z);
		// System.out.println(dat.w);
		panel.getRender().getRotationAttr().set(tra);
		panel.getRender().restoreXform();

	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == wAutomaticSlide)
		{
			float num = wAutomaticSlide.getMaximum();
			setRotationSpeed(wAutomaticSlide.getValue() / num * rotInc);
		}
		if (isAllowUpdate())
		{
			if (tab.getSelectedIndex() == 0)
			{

			} else if (tab.getSelectedIndex() == 1)
			{
				wAutomaticSlide.setValue(0);
				setRotationSpeed(0);
			}
			updateRotation();
		}
	}

	/**
	 * This will set a timer to rotation the given object at the given speed
	 * (speed= rot/sec)
	 * 
	 * @param speed
	 */
	private void setRotationSpeed(float speed)
	{
		if (speed == 0)
		{
			control.stopCycle();
		} else
		{
			control.setCycleIncrement(speed);
			control.startCycle();
		}
	}

	public void showControl()
	{
		f.setLayout(new BorderLayout());
		f.add(this, BorderLayout.CENTER);
		f.setSize(300, 300);
		f.setVisible(true);
		f.addWindowListener(new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				wAutomaticSlide.setValue(0);
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				wAutomaticSlide.setValue(0);
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	public boolean isAllowUpdate()
	{
		return allowUpdate;
	}

	public void setAllowUpdate(boolean allowUpdate)
	{
		this.allowUpdate = allowUpdate;
	}
}

class RotationControl implements Runnable
{
	Thread t = new Thread(this);

	RotationControlPanel owner;

	float increment = 1;

	boolean forward = true;

	long delay = 0;

	boolean finish = false;

	boolean running = false;

	Object lock = new Object();

	public RotationControl(RotationControlPanel own, int updatesPerSec)
	{
		this.owner = own;
		delay = 1000 / updatesPerSec;
		t.start();
	}

	public void setCycleIncrement(float val)
	{
		if (val == 0)
		{
			finish = true;
		}
		if (val < 0)
		{
			forward = false;
			val *= -1;
		} else
		{
			forward = true;
		}
		increment = val;
	}

	public void startCycle()
	{
		synchronized (lock)
		{
			running = true;
			lock.notifyAll();
		}
	}

	public void stopCycle()
	{
		running = false;
	}

	public void increment()
	{
		JSlider slide = owner.wManualSlide;

		int val = slide.getValue();
		int num = slide.getMaximum();

		float pos = (float) val / (float) num;

		float inc = increment;

		if (forward)
		{
			pos += inc;
		} else
		{
			pos -= inc;
		}

		if (pos > 1)
		{
			pos = -1;
		}

		if (pos < -1)
		{
			pos = 1;
		}

		int newVal = (int) (num * pos);

		// System.out
		// .printf("\n##########\nPos:%4.4f\nInc:%4.4f\nOld Val:%d\nTot:%d\nNew Val:%d\n",
		// pos,inc, val, num, newVal);
		slide.setValue(newVal);
	}

	@Override
	public void run()
	{
		while (!finish)
		{
			if (running)
			{
				increment();
				try
				{
					Thread.sleep(delay);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				synchronized (lock)
				{
					try
					{
						lock.wait();
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

}
