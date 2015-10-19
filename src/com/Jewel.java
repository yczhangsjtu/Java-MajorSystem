package com;

import java.awt.Image;

public class Jewel extends Unit{
	public Jewel(String id)
	{
		super(id);
		image = null;
	}
	public Jewel(String id, int x, int y, Image img)
	{
		super(id,x,y);
		image = img;
	}
	public boolean ocupySpace()
	{
		return false;
	}
	public String toString()
	{
		return null;
	}
}
