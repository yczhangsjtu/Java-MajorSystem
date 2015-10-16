package com;

import java.awt.Image;
import java.awt.Graphics;

import com.Unit;
import com.MapContainer;

public class Character extends Unit{
	String characterId;
	Image images[][];
	enum State {Standing, Moving}
	enum Dir {Up, Down, Left, Right}
	State state = State.Standing;
	Dir direction = Dir.Down;
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
	public void draw(Graphics g, int viewOffsetX, int viewOffsetY, int clock)
	{
		int X = x*width-viewOffsetX;
		int Y = y*height-viewOffsetY;
		int i = 0;
		int j = 0;
		if(direction == Dir.Down) i = 0;
		if(direction == Dir.Left) i = 1;
		if(direction == Dir.Right) i = 2;
		if(direction == Dir.Up) i = 3;
		if(state == State.Moving)
		{
			j = clock%4;
			if(direction == Dir.Down) Y += clock*height/10;
			if(direction == Dir.Left) X -= clock*height/10;
			if(direction == Dir.Right) X += clock*height/10;
			if(direction == Dir.Up) Y -= clock*height/10;
		}
		g.drawImage(images[i][j],X,Y,width,height,null);
	}
	public void update(MapContainer map)
	{
		if(state == State.Moving)
		{
			map.setUnitByPosition(null,x,y);
			if(direction == Dir.Down) y++;
			if(direction == Dir.Left) x--;
			if(direction == Dir.Right) x++;
			if(direction == Dir.Up) y--;
			state = State.Standing;
			map.setUnitByPosition(this,x,y);
		}
		super.update(map);
	}
	public boolean act(String action, MapContainer map)
	{
		String []tokens = action.trim().split(" ");
		if(tokens[0].equals("moveleft"))
			return moveLeft(map);
		else if(tokens[0].equals("moveright"))
			return moveRight(map);
		else if(tokens[0].equals("moveup"))
			return moveUp(map);
		else if(tokens[0].equals("movedown"))
			return moveDown(map);
		return true;
	}
	public boolean moveLeft(MapContainer map)
	{
		if(map.isAvailable(x-1,y))
		{
			direction = Dir.Left;
			state = State.Moving;
			return true;
		}
		return false;
	}
	public boolean moveRight(MapContainer map)
	{
		if(map.isAvailable(x+1,y))
		{
			direction = Dir.Right;
			state = State.Moving;
			return true;
		}
		return false;
	}
	public boolean moveUp(MapContainer map)
	{
		if(map.isAvailable(x,y-1))
		{
			direction = Dir.Up;
			state = State.Moving;
			return true;
		}
		return false;
	}
	public boolean moveDown(MapContainer map)
	{
		if(map.isAvailable(x,y+1))
		{
			direction = Dir.Down;
			state = State.Moving;
			return true;
		}
		return false;
	}
}
