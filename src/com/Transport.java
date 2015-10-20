package com;

import java.awt.Point;
import java.awt.Image;

public class Transport extends Unit{
	String id;
	int targetX, targetY;
	public Transport(String id, int x, int y, int tx, int ty, Image img)
	{
		super(id,x,y);
		targetX = tx;
		targetY = ty;
		image = img;
	}
	public int getTargetX()
	{
		return targetX;
	}
	public int getTargetY()
	{
		return targetY;
	}
	public Point getTarget()
	{
		return new Point(targetX,targetY);
	}
	public boolean ocupySpace()
	{
		return false;
	}
	public String toString()
	{
		return "unit:"+getUnitId()+" Transport "
			+x+" "+y+" "+targetX+" "+targetY+"\n";
	}
}
