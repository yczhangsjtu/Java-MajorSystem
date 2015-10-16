package com;

import java.awt.Graphics;

public class Unit{
	String unitId;
	int x, y;
	public static int width = 50, height = 50;
	public Unit(String id)
	{
		unitId = id;
		x = y = 0;
	}

	public Unit(String id, int X, int Y)
	{
		unitId = id;
		x = X;
		y = Y;
	}

	public void draw(Graphics g)
	{
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
