package com;

import java.awt.Image;

public class Quiz extends Unit{
	String code;
	public Quiz(String id)
	{
		super("quiz"+id);
		code = id;
	}
	public Quiz(String id, int x, int y, Image img)
	{
		super("quiz"+id,x,y);
		code = id;
		image = img;
	}
	public String getCode()
	{
		return code;
	}
	public String toString()
	{
		return "unit:"+getCode()+" Quiz "+x+" "+y+"\n";
	}
}
