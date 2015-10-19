package com;

import java.awt.Image;

public class Money extends Jewel{
	public Money(String id)
	{
		super(id);
	}
	public Money(String id, int x, int y, Image img)
	{
		super(id,x,y,img);
	}
	public String toString()
	{
		return "unit:"+getUnitId()+" Money "+x+" "+y+"\n";
	}
}
