package com;

public class Unit{
	String unitId;
	int x, y;
	public Unit(String id)
	{
		unitId = id;
		x = y = 0;
	}

	public String getUnitId()
	{
		return unitId;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
}
