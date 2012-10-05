package com.joey.software.VolumeToolkit;

public class IntAttr extends Attr
{
	int value;

	int initValue;

	IntAttr(String label, int initValue)
	{
		super(label);
		this.initValue = initValue;
		this.value = initValue;
	}

	@Override
	public void set(String stringValue)
	{
		int newValue = Integer.valueOf(stringValue).intValue();
		set(newValue);
	}

	public void set(int newValue)
	{
		value = newValue;
	}

	@Override
	public void reset()
	{
		value = initValue;
	}

	@Override
	public String toString()
	{
		return name + " " + numFormat.format(value);
	}

	public int getValue()
	{
		return value;
	}
}
