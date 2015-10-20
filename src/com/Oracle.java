package com;

import java.awt.Image;

public class Oracle extends Unit{
	public Oracle(String id, String h)
	{
		super(id);
	}
	public Oracle(String id, int x, int y, Image img)
	{
		super(id,x,y);
		image = img;
	}
	public String toString()
	{
		return "unit:"+getUnitId()+" Oracle "+x+" "+y+"\n";
	}
}
