package com;

import java.awt.Image;

public class Quiz extends Unit{
	String hint;
	public Quiz(String id, String h)
	{
		super(id);
		hint = h.replaceAll("_"," ");
	}
	public Quiz(String id, String h, int x, int y, Image img)
	{
		super(id,x,y);
		hint = h.replaceAll("_"," ");
		image = img;
	}
	public String getHint()
	{
		return hint;
	}
	public String toString()
	{
		return "unit:"+getUnitId()+" Quiz "+hint.replaceAll(" ","_")+" "+x+" "+y+"\n";
	}
}
