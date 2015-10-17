package com;

import com.MapContainer;
import java.awt.Graphics;
import java.util.LinkedList;

abstract class Unit{
	String unitId;
	LinkedList<String> actions;
	int x, y;
	public static int width = 50, height = 50;
	public Unit(String id)
	{
		unitId = id;
		x = y = 0;
		actions = new LinkedList<String>();
	}

	public Unit(String id, int X, int Y)
	{
		unitId = id;
		x = X;
		y = Y;
		actions = new LinkedList<String>();
	}

	public void draw(Graphics g, int viewOffsetX, int viewOffsetY, int clock)
	{
	}

	public void update(MapContainer map)
	{
		if(actions.size() > 0)
		{
			if(act(actions.get(0), map))
				actions.remove(0);
		}
	}

	public boolean act(String action, MapContainer map)
	{
		return true;
	}

	public void addAction(String action)
	{
		actions.add(action);
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

	public int getPixelX(int clock)
	{
		return x * width;
	}

	public int getPixelY(int clock)
	{
		return y * height;
	}

}
