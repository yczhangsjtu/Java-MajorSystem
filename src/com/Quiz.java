package com;

import java.awt.Image;

public class Quiz extends Unit{
	String hint;
	String code;
	public Quiz(String id, String h)
	{
		super("quiz"+id);
		code = id;
		hint = h.replaceAll("_"," ");
	}
	public Quiz(String id, String h, int x, int y, Image img)
	{
		super("quiz"+id,x,y);
		hint = h.replaceAll("_"," ");
		code = id;
		image = img;
	}
	public String getHint()
	{
		return hint;
	}
	public String getCode()
	{
		return code;
	}
	public String toString()
	{
		return "unit:"+getCode()+" Quiz "+hint.replaceAll(" ","_")+" "+x+" "+y+"\n";
	}
}
