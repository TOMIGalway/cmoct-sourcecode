package com.joey.software.mathsToolkit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class NumberDimension extends JPanel implements Externalizable
{
	public static final int POWER_FEMTO = -6;

	public static final int POWER_PICO = -5;

	public static final int POWER_NANO = -4;

	public static final int POWER_MICRO = -3;

	public static final int POWER_MILLI = -2;

	public static final int POWER_CENTA = -1;

	public static final int POWER_UNITY = 0;

	public static final int POWER_KILO = 1;

	public static final int POWER_MEGA = 2;

	public static final int POWER_GEGA = 3;

	public static final int POWER_TERA = 4;

	private static final long serialVersionUID = 1L;

	LinkedHashMap<Integer, String> prefexs = new LinkedHashMap<Integer, String>();

	LinkedHashMap<Integer, Double> values = new LinkedHashMap<Integer, Double>();

	JSpinner number = new JSpinner(new SpinnerNumberModel(1.0,
			-Double.MAX_VALUE, Double.MAX_VALUE, 0.1));

	PrefexModel model = new PrefexModel(prefexs, values);

	JComboBox prefex = new JComboBox(model);

	String unit = "";

	int minPower = POWER_NANO;

	int maxPower = POWER_KILO;

	boolean ignorePowerChange = true;

	boolean numberEditable = true;

	double value = 1;

	boolean changeListnerBlock = false;

	Vector<ChangeListener> listners = new Vector<ChangeListener>();

	float power = 1;

	public NumberDimension()
	{
		this("");
	}

	public NumberDimension(String unit)
	{
		setUnit(unit);
		createJPanel();
	}

	/**
	 * Val is in Meteres
	 * 
	 * @param val
	 * @param power
	 * @return
	 */
	public static double getValue(double val, int power)
	{
		NumberDimension d = new NumberDimension("");
		d.setValue(val, true);
		d.setPrefex(power, true);
		return d.getValueUsingPrefix();
	}

	public static double getValueUnity(double val, int power)
	{
		NumberDimension d = new NumberDimension("");
		d.setValue(val, true);
		d.setPrefex(power, false);
		return d.getValue();
	}

	/**
	 * Val is in meteres
	 * 
	 * @param val
	 * @param power
	 * @param unit
	 * @return
	 */
	public static String getValue(double val, int power, String unit)
	{
		NumberDimension d = new NumberDimension(unit);
		d.setValue(val, true);
		d.setPrefex(power, true);

		double rst = d.getValueUsingPrefix();

		double abs = Math.abs(rst);

		DecimalFormat format = new DecimalFormat();
		if (abs < 1)
		{
			format.setMaximumIntegerDigits(1);
			format.setMaximumFractionDigits(4);
		} else if (abs < 100)
		{
			format.setMaximumIntegerDigits(2);
			format.setMaximumFractionDigits(3);
		} else if (abs < 1000)
		{
			format.setMaximumIntegerDigits(3);
			format.setMaximumFractionDigits(2);
		} else if (abs < 10000)
		{
			format.setMaximumIntegerDigits(4);
			format.setMaximumFractionDigits(1);
		} else
		{
			format = new DecimalFormat();
			format.setMaximumFractionDigits(0);
		}
		String unitVal = d.prefexs.get(power);
		return format.format(rst) + " " + unitVal;
	}

	public void addChangeListner(ChangeListener listner)
	{
		listners.add(listner);
	}

	public void removeChangeListner(ChangeListener listner)
	{
		listners.remove(listner);
	}

	public void blockChangeListner(boolean block)
	{
		changeListnerBlock = block;
	}

	public void setNumberEditable(boolean allow)
	{
		this.numberEditable = allow;
		number.setEnabled(false);
	}

	/**
	 * This will set the current value to the given size
	 * 
	 * @param val
	 */
	public void setValue(double val, boolean resetPower)
	{
		value = val;
		updateValueField();
		if (resetPower)
		{
			if (!model.setSelectedPrefex(POWER_UNITY))
			{
				model.setSelectedPrefex(minPower);
			}

		}
	}

	public void setValue(NumberDimension num)
	{
		setValue(num.getValue(), true);
		setPrefex(num.getPrefex(), true);

	}

	@Override
	public String toString()
	{
		StringBuilder rst = new StringBuilder();

		rst.append("NumberDimension : ");
		rst.append("Value [");
		rst.append(getValue());
		rst.append("] - Display Power : " + getPrefex());
		return rst.toString();
	}

	public void setPrefex(int powVal)
	{
		setPrefex(powVal, true);
	}

	public void setPrefex(int powVal, boolean updateValue)
	{
		boolean last = ignorePowerChange;
		setIgnorePowerChange(!updateValue);
		model.setSelectedPrefex((powVal));
		prefex.setSelectedItem(model.getSelectedItem());
		prefex.validate();
		prefex.repaint();
		setIgnorePowerChange(last);
	}

	public void notifyChange()
	{
		if (changeListnerBlock == true)
		{
			return;
		}

		for (ChangeListener l : listners)
		{
			ChangeEvent change = new ChangeEvent(this);
			l.stateChanged(change);
		}
	}

	public static void main(String input[])
	{

		System.out.println(NumberDimension.getValue(1, POWER_MILLI, "m"));

	}

	public void setPrefexPower(float power)
	{
		this.power = power;
		setUnit(unit);
	}

	public JSpinner getJSpinner()
	{
		return number;
	}

	/**
	 * This will return the current true value
	 * i.e if this is 1000 um 
	 * this will return 1000e-6;
	 * @return
	 */
	public double getValue()
	{
		return value;
	}

	public int getPrefex()
	{
		return model.getSelectedPrefex();
	}

	public String getPrefixCode()
	{
		return prefexs.get(getPrefex());
	}
	public void setNumberformat(String format)
	{
		number.setEditor(new JSpinner.NumberEditor(number, format));
	}

	public void createJPanel()
	{
		setNumberformat("#,###,###,###,###.###############");
		setLayout(new BorderLayout());
		add(number, BorderLayout.CENTER);
		add(prefex, BorderLayout.EAST);

		prefex.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (ignorePowerChange)
				{
					grabValueField();
				} else
				{
					updateValueField();
				}
				notifyChange();
			}
		});

		number.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				grabValueField();
				notifyChange();
			}
		});
	}

	/**
	 * This will get the current value using the current previx set in the 
	 * sytem. 
	 * i.e if the value is 10e-3 and the dimsneion is set to
	 * mm, this will return 10 mm. 
	 * @return
	 */
	public double getValueUsingPrefix()
	{
		double val = getValue();

		try
		{
			val /= values.get(model.getSelectedPrefex());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return val;
	}

	public void updateValueField()
	{
		double val = getValue();

		try
		{
			val /= values.get(model.getSelectedPrefex());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		// ignoreValueChange = true;
		number.setValue(val);
		// ignoreValueChange = false;
	}

	/**
	 * This is set if the nubmer shouldn't change when the power is switched
	 * 
	 * @param ignore
	 */
	public void setIgnorePowerChange(boolean ignore)
	{
		ignorePowerChange = ignore;
	}

	public void grabValueField()
	{
		double num = (Double) number.getValue();
		try
		{
			num *= values.get(model.getSelectedPrefex());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		value = num;
	}

	/**
	 * This will set the current unit for SI units
	 * 
	 * i.e if unit is kg
	 * 
	 * the data will be mKg, MKg ect...
	 * 
	 * @param unit
	 */
	public void setUnit(String unit)
	{
		this.unit = unit;

		int old = model.getSelectedPrefex();

		prefexs.clear();
		values.clear();

		if (minPower <= POWER_FEMTO && maxPower >= POWER_FEMTO)
		{
			prefexs.put(POWER_FEMTO, "f" + unit);
			values.put(POWER_FEMTO, Math.pow(1e-15, power));
		}
		if (minPower <= POWER_PICO && maxPower >= POWER_PICO)
		{
			prefexs.put(POWER_PICO, "p" + unit);
			values.put(POWER_PICO, Math.pow(1e-12, power));
		}
		if (minPower <= POWER_NANO && maxPower >= POWER_NANO)
		{
			prefexs.put(POWER_NANO, "n" + unit);
			values.put(POWER_NANO, Math.pow(1e-9, power));
		}
		if (minPower <= POWER_MICRO && maxPower >= POWER_MICRO)
		{
			prefexs.put(POWER_MICRO, "u" + unit);
			values.put(POWER_MICRO, Math.pow(1e-6, power));
		}
		if (minPower <= POWER_MILLI && maxPower >= POWER_MILLI)
		{
			prefexs.put(POWER_MILLI, "m" + unit);
			values.put(POWER_MILLI, Math.pow(1e-3, power));
		}
		if (minPower <= POWER_CENTA && maxPower >= POWER_CENTA)
		{
			prefexs.put(POWER_CENTA, "c" + unit);
			values.put(POWER_CENTA, Math.pow(1e-2, power));
		}
		if (minPower <= POWER_UNITY && maxPower >= POWER_UNITY)
		{
			prefexs.put(POWER_UNITY, unit);
			values.put(POWER_UNITY, Math.pow(1.0, power));
		}
		if (minPower <= POWER_KILO && maxPower >= POWER_KILO)
		{
			prefexs.put(POWER_KILO, "k" + unit);
			values.put(POWER_KILO, Math.pow(1e3, power));
		}
		if (minPower <= POWER_MEGA && maxPower >= POWER_MEGA)
		{
			prefexs.put(POWER_MEGA, "M" + unit);
			values.put(POWER_MEGA, Math.pow(1e6, power));
		}
		if (minPower <= POWER_GEGA && maxPower >= POWER_GEGA)
		{
			prefexs.put(POWER_GEGA, "G" + unit);
			values.put(POWER_GEGA, Math.pow(1e9, power));
		}
		if (minPower <= POWER_TERA && maxPower >= POWER_TERA)
		{
			prefexs.put(POWER_TERA, "T" + unit);
			values.put(POWER_TERA, Math.pow(1e12, power));
		}
		model.setSelectedPrefex(old);

	}

	/**
	 * This will set the min power that is to be displayed in the drop down list
	 * 
	 * @see POWER_UNIT....
	 * @param minPower
	 */
	public void setMinPower(int minPower)
	{
		this.minPower = minPower;
		setUnit(unit);
	}

	/**
	 * This will set the max power that is to be displayed in the drop down list
	 * 
	 * @see POWER_UNIT....
	 * @param maxPower
	 */
	public void setMaxPower(int maxPower)
	{
		this.maxPower = maxPower;
		setUnit(unit);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		number.setValue(in.readDouble());

		setUnit(in.readUTF());
		model.setSelectedIndex(in.readInt());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeDouble((Double) number.getValue());
		out.writeUTF(unit);
		out.writeInt(model.index);

	}

	public void setDigits(int i)
	{
		number.setPreferredSize(new Dimension(i * 5, 0));
	}

}

class PrefexModel extends DefaultComboBoxModel
{
	LinkedHashMap<Integer, String> prefexs;

	LinkedHashMap<Integer, Double> values;

	int index = 0;

	public PrefexModel(LinkedHashMap<Integer, String> pre, LinkedHashMap<Integer, Double> val)
	{
		prefexs = pre;
		values = val;
	}

	@Override
	public Object getElementAt(int index)
	{
		return prefexs.values().toArray()[index];
	}

	@Override
	public int getSize()
	{
		// TODO Auto-generated method stub
		return prefexs.size();
	}

	@Override
	public void setSelectedItem(Object anObject)
	{
		Object[] pre = prefexs.values().toArray();
		for (int i = 0; i < pre.length; i++)
		{
			if (pre[i].equals(anObject))
			{
				index = i;
			}
		}
	}

	public void setSelectedIndex(int index)
	{
		this.index = index;
	}

	@Override
	public Object getSelectedItem()
	{
		if (prefexs.size() == 0)
		{
			return "";
		}
		return getElementAt(index);
	}

	public int getSelectedPrefex()
	{
		if (index < prefexs.size())
		{
			return prefexs.keySet().toArray(new Integer[0])[index];
		}
		return 0;

	}

	/**
	 * This will try and set the selected previex using a POWER_XXXX prefix.
	 * 
	 * If the prefix isn't there it will return 0 and set the index = 0;
	 * 
	 * @param powerPrefex
	 * @return
	 */
	public boolean setSelectedPrefex(int powerPrefex)
	{
		Object[] pre = prefexs.keySet().toArray();
		for (int i = 0; i < pre.length; i++)
		{
			if (pre[i].equals(powerPrefex))
			{
				index = i;
				return true;
			}
		}
		index = 0;
		return false;
	}

}
