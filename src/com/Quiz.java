package com;

import java.awt.Image;

public class Quiz extends Unit{
	String code;
	boolean solved = false;
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
		String ifsolved = "";
		if(solved) ifsolved = " solved";
		return "unit:"+getCode()+" Quiz "+x+" "+y+ifsolved+"\n";
	}
	public void setSolved(boolean s, MapContainer map)
	{
		if(s && map.units[x][y] == this)
			map.units[x][y] = null;
		if(s) x=y=-1;
		solved = s;
	}
}
