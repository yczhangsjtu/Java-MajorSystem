package com;

import java.awt.Image;
import java.awt.Graphics;

import com.Unit;

public class Character extends Unit{
	String characterId;
	Image images[][];
	public Character(String id)
	{
		super(id);
		characterId = id;
		images = null;
	}
	public Character(String id, int x, int y, Image imgs[][])
	{
		super(id,x,y);
		characterId = id;
		images = imgs;
	}
	public void draw(Graphics g, int viewOffsetX, int viewOffsetY)
	{
		g.drawImage(images[0][0],x*width-viewOffsetX,y*height-viewOffsetY,width,height,null);
	}
}
