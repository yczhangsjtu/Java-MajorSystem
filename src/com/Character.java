package com;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Iterator;

import com.Unit;
import com.MapContainer;

public class Character extends Unit{
	String characterId;
	Image images[][];
	int imageIndex;
	enum State {Standing, Moving}
	enum Direction {Up, Down, Left, Right}
	State state = State.Standing;
	Direction direction = Direction.Down;
	LinkedList<Point> pathToDest;
	Unit target;
	Point dest;
	int money = 0;
	public Character(String id)
	{
		super(id);
		characterId = id;
		images = null;
		imageIndex = CharacterContainer.getImageIndex(id);
	}
	public Character(String id, int x, int y, Image imgs[][])
	{
		super(id,x,y);
		characterId = id;
		images = imgs;
		imageIndex = CharacterContainer.getImageIndex(id);
	}
	public Character(String id, int x, int y, int index)
	{
		super(id,x,y);
		characterId = id;
		imageIndex = index;
		images = CharacterContainer.characterImage[index];
	}
	public void setImageIndex(int index)
	{
		imageIndex = index % CharacterContainer.imageNum;
		images = CharacterContainer.characterImage[imageIndex];
	}
	public int getImageIndex()
	{
		return imageIndex;
	}
	public int getPixelX(int clock)
	{
		if(state == State.Moving)
		{
			if(direction == Direction.Down) return super.getPixelX(clock);
			if(direction == Direction.Left) return super.getPixelX(clock) - clock*width/10;
			if(direction == Direction.Right) return super.getPixelX(clock) + clock*width/10;
			if(direction == Direction.Up) return super.getPixelX(clock);
		}
		return super.getPixelX(clock);
	}
	public int getPixelY(int clock)
	{
		if(state == State.Moving)
		{
			if(direction == Direction.Down) return super.getPixelY(clock) + clock*height/10;
			if(direction == Direction.Left) return super.getPixelY(clock);
			if(direction == Direction.Right) return super.getPixelY(clock);
			if(direction == Direction.Up) return super.getPixelY(clock) - clock*height/10;
		}
		return super.getPixelY(clock);
	}
	public void draw(Graphics g, int viewOffsetX, int viewOffsetY, int clock, MapContainer map)
	{
		int X = x*width-viewOffsetX;
		int Y = y*height-viewOffsetY;
		int i = 0;
		int j = 0;
		if(direction == Direction.Down) i = 0;
		if(direction == Direction.Left) i = 1;
		if(direction == Direction.Right) i = 2;
		if(direction == Direction.Up) i = 3;
		if(state == State.Moving)
		{
			j = clock%4;
			if(direction == Direction.Down) Y += clock*height/10;
			if(direction == Direction.Left) X -= clock*height/10;
			if(direction == Direction.Right) X += clock*height/10;
			if(direction == Direction.Up) Y -= clock*height/10;
		}
		if(X > map.windowWidth || X + width < 0 || Y > map.windowHeight || Y + height < 0)
			return;
		g.drawImage(images[i][j],X,Y,width,height,null);
		g.setColor(Color.WHITE);
		g.drawString(getUnitId(),X+20,Y+height+20);
	}
	public void update(MapContainer map)
	{
		int xx = x, yy = y;
		if(state == State.Moving)
		{
			if(direction == Direction.Down) yy++;
			if(direction == Direction.Left) xx--;
			if(direction == Direction.Right) xx++;
			if(direction == Direction.Up) yy--;
			state = State.Standing;
			if(map.isAvailable(xx,yy))
			{
				Unit unit = map.getUnitByPosition(xx,yy);
				if(unit instanceof Money)
				{
					map.removeUnit(xx,yy);
					money++;
				}
				else if(unit instanceof Transport)
				{
					int x1 = ((Transport)unit).getTargetX();
					int y1 = ((Transport)unit).getTargetY();
					if(map.isAvailable(x1,y1))
					{
						xx = x1;
						yy = y1;
						if(map.getUnitByPosition(x1,y1) instanceof Money)
						{
							map.removeUnit(x1,y1);
							money++;
						}
					}
					else
					{
						xx = x;
						yy = y;
					}
				}
				map.setUnitByPosition(null,x,y);
				map.setUnitByPosition(this,xx,yy);
				x = xx;
				y = yy;
			}
		}
		if(target != null) setDest(target,map);
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
			if(X < 0) X = 0;
			if(Y < 0) Y = 0;
			if(X >= map.mapWidth) X = map.mapWidth - 1;
			if(Y >= map.mapHeight) Y = map.mapHeight - 1;
			setDest(X,Y);
		}
		else if(tokens[0].equals("follow"))
		{
			follow(map.getUnitById(tokens[1]));
		}
		else if(tokens[0].equals("unfollow"))
		{
			clearFollow();
		}
		else if(tokens[0].equals("checkpos"))
		{
			int X = Integer.parseInt(tokens[1]);
			int Y = Integer.parseInt(tokens[2]);
			return x == X && y == Y;
		}
		return true;
	}
	public boolean move(String direction, MapContainer map)
	{
		if(direction.equals("left")) return moveLeft(map);
		if(direction.equals("right")) return moveRight(map);
		if(direction.equals("up")) return moveUp(map);
		if(direction.equals("down")) return moveDown(map);
		return false;
	}
	public boolean moveLeft(MapContainer map)
	{
		direction = Direction.Left;
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
		direction = Direction.Right;
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
		direction = Direction.Up;
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
		direction = Direction.Down;
		if(map.isAvailable(x,y+1))
		{
			state = State.Moving;
			return true;
		}
		state = State.Standing;
		return false;
	}
	public Direction getDirection()
	{
		return direction;
	}
	public boolean faceLeft()
	{
		return direction == Direction.Left;
	}
	public boolean faceRight()
	{
		return direction == Direction.Right;
	}
	public boolean faceUp()
	{
		return direction == Direction.Up;
	}
	public boolean faceDown()
	{
		return direction == Direction.Down;
	}
	public void setMoney(int m)
	{
		money = m;
	}
	public int getMoney()
	{
		return money;
	}
	public void decreaseMoney()
	{
		if(money > 0) money--;
	}
	public boolean goTo(MapContainer map, int x1, int y1)
	{
		if(x == x1 && y == y1) return true;
		if(x1 == x + 1 && y1 == y) return moveRight(map);
		else if(x1 == x - 1 && y1 == y) return moveLeft(map);
		else if(x1 == x && y1 == y + 1) return moveDown(map);
		else if(x1 == x && y1 == y - 1) return moveUp(map);
		if(pathToDest == null || pathToDest.isEmpty() ||
			!pathToDest.getLast().equals(new Point(x1,y1)) ||
			distance(pathToDest.getFirst(),new Point(x,y))>1)
			pathToDest = map.findPath(new Point(x,y),new Point(x1,y1));
		if(pathToDest != null && !pathToDest.isEmpty())
		{
			Point p = pathToDest.getFirst();
			if(p.equals(new Point(x,y)))
			{
				pathToDest.removeFirst();
				if(pathToDest.isEmpty()) return true;
				p = pathToDest.getFirst();
			}
			boolean moveSucceed = false;
			if(p.x == x + 1 && p.y == y) moveSucceed = moveRight(map);
			else if(p.x == x - 1 && p.y == y) moveSucceed = moveLeft(map);
			else if(p.x == x && p.y == y + 1) moveSucceed = moveDown(map);
			else if(p.x == x && p.y == y - 1) moveSucceed = moveUp(map);
			//if(!moveSucceed) pathToDest = null;
			return moveSucceed;
		}
		return false;
	}
	public int distance(Point p1, Point p2)
	{
		return Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
	}
	public int distance(Unit unit)
	{
		return distance(getPoint(),unit.getPoint());
	}
	public void follow(Unit unit)
	{
		target = unit;
	}
	public void clearFollow()
	{
		target = null;
		dest = null;
		state = State.Standing;
	}
	public void setDest(Point p)
	{
		dest = p;
	}
	public void setDest(int X, int Y)
	{
		if(X == -1 || Y == -1)
			dest = null;
		else
			dest = new Point(X,Y);
	}
	public Point getBack(MapContainer map)
	{
		int X=-1,Y=-1;
		if(direction == Direction.Left){X=x+1;Y=y;}
		if(direction == Direction.Right){X=x-1;Y=y;}
		if(direction == Direction.Up){X=x;Y=y+1;}
		if(direction == Direction.Down){X=x;Y=y-1;}
		return new Point(X,Y);
	}
	public void setDest(Character character, MapContainer map)
	{
		if(character != null)
		{
			if(getPoint().equals(character.getBack(map)))
			{
				setDest(null);
				if(character.faceLeft()) moveLeft(map);
				if(character.faceRight()) moveRight(map);
				if(character.faceUp()) moveUp(map);
				if(character.faceDown()) moveDown(map);
			}
		}
		setDest(character.getBack(map));
	}
	public void setDest(Unit unit, MapContainer map)
	{
		if(unit instanceof Character)
			setDest((Character)unit,map);
		else
			setDest(unit.getPoint());
	}
	public void clearDest()
	{
		dest = null;
	}
	public void gotoDest(MapContainer map)
	{
		if(dest != null && x == dest.x && y == dest.y) dest = null;
		if(dest != null) goTo(map,dest.x,dest.y);
	}
	public String toString()
	{
		String s = "unit:";
		s += characterId + " Character ";
		s += x + " " + y + " " + money + " " + imageIndex + "\n";
		if(dest != null)
			s += "instruction:addaction "+characterId+" goto_"+dest.x
				+"_"+dest.y+"\n";
		if(target != null)
			s += "instruction:addaction "+characterId+" follow_"
				+target.getUnitId()+"\n";
		return s;
	}

	public void drawMini(Graphics g, int x, int y, int size)
	{
		g.drawImage(images[0][0],x,y,size,size,null);
	}
}
