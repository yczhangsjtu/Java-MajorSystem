package com;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Iterator;

import com.Unit;
import com.MapContainer;

public class Character extends Unit{
	String characterId;
	Image images[][];
	enum State {Standing, Moving}
	enum Dir {Up, Down, Left, Right}
	State state = State.Standing;
	Dir direction = Dir.Down;
	LinkedList<Point> pathToDest;
	Unit target;
	Point dest;
	int money = 0;
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
	public int getPixelX(int clock)
	{
		if(state == State.Moving)
		{
			if(direction == Dir.Down) return super.getPixelX(clock);
			if(direction == Dir.Left) return super.getPixelX(clock) - clock*width/10;
			if(direction == Dir.Right) return super.getPixelX(clock) + clock*width/10;
			if(direction == Dir.Up) return super.getPixelX(clock);
		}
		return super.getPixelX(clock);
	}
	public int getPixelY(int clock)
	{
		if(state == State.Moving)
		{
			if(direction == Dir.Down) return super.getPixelY(clock) + clock*height/10;
			if(direction == Dir.Left) return super.getPixelY(clock);
			if(direction == Dir.Right) return super.getPixelY(clock);
			if(direction == Dir.Up) return super.getPixelY(clock) - clock*height/10;
		}
		return super.getPixelY(clock);
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
		int xx = x, yy = y;
		if(state == State.Moving)
		{
			if(direction == Dir.Down) yy++;
			if(direction == Dir.Left) xx--;
			if(direction == Dir.Right) xx++;
			if(direction == Dir.Up) yy--;
			state = State.Standing;
			if(map.isAvailable(xx,yy))
			{
				if(map.getUnitByPosition(xx,yy) instanceof Money)
				{
					map.removeUnit(xx,yy);
					money++;
				}
				map.setUnitByPosition(null,x,y);
				map.setUnitByPosition(this,xx,yy);
				x = xx;
				y = yy;
			}
		}
		if(target != null) setDest(target.getX(),target.getY());
		if(dest != null) gotoDest(map);
		super.update(map);
	}
	public boolean act(String action, MapContainer map)
	{
		String []tokens = action.trim().split("_");
		if(tokens[0].equals("moveleft"))
			return moveLeft(map);
		else if(tokens[0].equals("moveright"))
			return moveRight(map);
		else if(tokens[0].equals("moveup"))
			return moveUp(map);
		else if(tokens[0].equals("movedown"))
			return moveDown(map);
		else if(tokens[0].equals("goto"))
		{
			int X = Integer.parseInt(tokens[1]);
			int Y = Integer.parseInt(tokens[2]);
			setDest(X,Y);
		}
		else if(tokens[0].equals("follow"))
		{
			follow(map.getUnitById(tokens[1]));
		}
		else if(tokens[0].equals("checkpos"))
		{
			int X = Integer.parseInt(tokens[1]);
			int Y = Integer.parseInt(tokens[2]);
			return x == X && y == Y;
		}
		return true;
	}
	public boolean moveLeft(MapContainer map)
	{
		direction = Dir.Left;
		if(map.isAvailable(x-1,y))
		{
			state = State.Moving;
			return true;
		}
		state = State.Standing;
		return false;
	}
	public boolean moveRight(MapContainer map)
	{
		direction = Dir.Right;
		if(map.isAvailable(x+1,y))
		{
			state = State.Moving;
			return true;
		}
		state = State.Standing;
		return false;
	}
	public boolean moveUp(MapContainer map)
	{
		direction = Dir.Up;
		if(map.isAvailable(x,y-1))
		{
			state = State.Moving;
			return true;
		}
		state = State.Standing;
		return false;
	}
	public boolean moveDown(MapContainer map)
	{
		direction = Dir.Down;
		if(map.isAvailable(x,y+1))
		{
			state = State.Moving;
			return true;
		}
		state = State.Standing;
		return false;
	}
	public boolean goTo(MapContainer map, int x1, int y1)
	{
		if(pathToDest == null || pathToDest.isEmpty() ||
			!pathToDest.getLast().equals(new Point(x1,y1)))
			pathToDest = map.findPath(new Point(x,y),new Point(x1,y1));
		if(pathToDest != null && !pathToDest.isEmpty())
		{
			Point p = pathToDest.get(0);
			if(p.equals(new Point(x,y)))
			{
				pathToDest.removeFirst();
				if(pathToDest.isEmpty()) return true;
				p = pathToDest.get(0);
			}
			boolean moveSucceed = false;
			if(p.x == x + 1 && p.y == y) moveSucceed = moveRight(map);
			else if(p.x == x - 1 && p.y == y) moveSucceed = moveLeft(map);
			else if(p.x == x && p.y == y + 1) moveSucceed = moveDown(map);
			else if(p.x == x && p.y == y - 1) moveSucceed = moveUp(map);
			else{
				LinkedList<Point> path = map.findPath(new Point(x,y),p);
				if(path != null && !path.isEmpty())
				{
					path.removeLast();
					pathToDest.addAll(0,path);
					if(p.x == x + 1 && p.y == y) moveSucceed = moveRight(map);
					else if(p.x == x - 1 && p.y == y) moveSucceed = moveLeft(map);
					else if(p.x == x && p.y == y + 1) moveSucceed = moveDown(map);
					else if(p.x == x && p.y == y - 1) moveSucceed = moveUp(map);
				}
			}
			if(!moveSucceed) pathToDest = null;
			return moveSucceed;
		}
		return false;
	}
	public void follow(Unit unit)
	{
		target = unit;
	}
	public void clearFollow()
	{
		target = null;
	}
	public void setDest(int X, int Y)
	{
		dest = new Point(X,Y);
	}
	public void clearDest()
	{
		dest = null;
	}
	public void gotoDest(MapContainer map)
	{
		goTo(map,dest.x,dest.y);
	}
}
