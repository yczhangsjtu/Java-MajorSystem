package com;

import java.util.Random;
import java.util.TreeSet;
import java.util.ArrayList;
import java.awt.Point;

public class AI {
	MapContainer map;
	Random rand = new Random();
	Point gatherPoints[];
	boolean extinct[];
	int attacking[];
	int numbers[];
	int enemyX = -1, enemyY = -1;
	int numOfCountries = CharacterContainer.imageNum;
	int heroIndex;
	String directions[] = {"left","right","up","down"};
	ArrayList<TreeSet<String>> countries =
		new ArrayList<TreeSet<String>>(numOfCountries);
	ArrayList<TreeSet<String>> troops =
		new ArrayList<TreeSet<String>>(numOfCountries);
	Character backToCountry[] = new Character[numOfCountries];
	Point troopPoint[] = new Point[numOfCountries];
	int clock = 0;
	String hero = Canvas.hero;
	public AI(MapContainer m)
	{
		map = m;
		gatherPoints = new Point[numOfCountries];
		attacking = new int[numOfCountries];
		numbers = new int[numOfCountries];
		extinct = new boolean[numOfCountries];
		troopPoint = new Point[numOfCountries];
		for(int i = 0; i < numOfCountries; i++)
		{
			countries.add(null);
			troops.add(null);
		}
	}

	public void resetCountries()
	{
		Character chero = map.getCharacterById(hero);
		heroIndex = chero.getImageIndex();
		for(int i = 0; i < numOfCountries; i++)
			countries.set(i,new TreeSet<String>());
		for(String id: map.getAllCharactersId())
		{
			Character c = map.getCharacterById(id);
			if(c == null) continue;
			int k = c.getImageIndex();
			countries.get(k).add(id);
			if(c.target != null)
			{
				String tid = c.target.getUnitId();
				if(map.getUnitById(tid) == null)
				{
					c.target = null;
					c.dest = null;
				}
			}
		}
		for(int i = 0; i < numOfCountries; i++)
		{
			numbers[i] = countries.get(i).size();
			extinct[i] = numbers[i] == 0;
		}
	}

	public void buildTroops()
	{
		for(int i = 0; i < numOfCountries; i++)
			buildTroop(i);
	}

	public void buildTroop(int index)
	{
		if(troops.get(index) == null)
			troops.set(index,new TreeSet<String>());
		TreeSet<String> troop = troops.get(index);
		ArrayList<String> deads = new ArrayList<String>();
		for(String id: troop)
		{
			Character c = map.getCharacterById(id);
			if(c == null) {deads.add(id);continue;}
			if(c.getImageIndex() != index) deads.add(id);
		}
		for(int i = 0; i < deads.size(); i++)
			troop.remove(deads.get(i));
		for(String id: countries.get(index))
		{
			if(id.equals(hero)) continue;
			if(troop.size() >= 10) break;
			if(troop.contains(id)) continue;
			Character c = map.getCharacterById(id);
			if(c == null) continue;
			troop.add(id);
		}
	}

	public void moveTroops()
	{
		for(int i = 0; i < numOfCountries; i++)
			moveTroop(i);
	}

	public void moveTroop(int index)
	{
		if(troops.get(index) == null) return;
		for(String id: troops.get(index))
		{
			if(id.equals(hero)) continue;
			Character c = map.getCharacterById(id);
			if(c == null) continue;
    		if(index == heroIndex && c.target != null &&
				c.target.getUnitId().equals(hero)) continue;
			c.clearFollow();
			c.setDest(troopPoint[index]);
		}
	}

	public void resetTroopPoints()
	{
		for(int i = 0; i < numOfCountries; i++)
			resetTroopPoint(i);
	}

	public void resetTroopPoint(int index)
	{
		if(attacking[index] == -1) return;
		if(troopPoint[index] == null)
			troopPoint[index] = gatherPoints[index];
		Character enemy = findNearestEnemy(troopPoint[index],attacking[index]);
		if(enemy == null) return;
		troopPoint[index] = enemy.getPoint();
	}

	public void tick()
	{
		Character chero = map.getCharacterById(hero);
		clock++;
		if(clock % 100 == 0)
		{
			resetCountries();
			buildTroops();
			randomMove();
			gather();
			attack();
			//resetTroopPoints();
			callBackTroops();
			moveTroops();
			findNearestEnemies();
			getNearestEnemyPos();
		}
		if(clock % 1000 == 0)
		{
			randomMoveGatherPoints();
		}
	}

	public void randomMoveGatherPoints()
	{
		for(int i = 0; i < numOfCountries; i++)
		{
			gatherPoints[i].x += rand.nextInt(3);
			gatherPoints[i].y += rand.nextInt(3);
			gatherPoints[i].x -= 1;
			gatherPoints[i].y -= 1;
			if(gatherPoints[i].x < 0) gatherPoints[i].x = 0;
			if(gatherPoints[i].y < 0) gatherPoints[i].y = 0;
			if(gatherPoints[i].x >= map.mapWidth)
				gatherPoints[i].x = map.mapWidth-1;
			if(gatherPoints[i].y >= map.mapHeight)
				gatherPoints[i].y = map.mapHeight-1;
		}
	}

	public void getNearestEnemyPos()
	{
		Character chero = map.getCharacterById(hero);
		Character enemy = findNearestEnemy(chero,attacking[chero.getImageIndex()]);
		if(enemy != null)
		{
			enemyX = enemy.getX();
			enemyY = enemy.getY();
		}
	}

	public void findNearestEnemies()
	{
		for(int i = 0; i < numOfCountries; i++)
		{
			int dist = -1;
			int X = gatherPoints[i].x;
			int Y = gatherPoints[i].y;
			Point p = new Point(X,Y);
			for(int j = 0; j < numOfCountries; j++)
			{
				if(j == i) continue;
				if(extinct[j])
				{
					continue;
				}
				if(attacking[j] == i)
				{
					attacking[i] = j;
					break;
				}
				int x = gatherPoints[j].x;
				int y = gatherPoints[j].y;
				if(distance(x,y,p) < dist || dist == -1)
				{
					dist = distance(x,y,p);
					attacking[i] = j;
				}
			}
			if(attacking[i] != -1 && extinct[attacking[i]]) 
				attacking[i] = -1;
		}
	}

	public Character findNearestEnemy(Point p, int enemyIndex)
	{
		Character enemy = null;
		int dist = -1;
		for(String id: countries.get(enemyIndex))
		{
			Character character = map.getCharacterById(id);
			if(character == null) continue;
			int ndist = distance(p,character.getPoint());
			if(ndist < dist || dist == -1)
			{
				dist = ndist;
				enemy = character;
			}
		}
		return enemy;
	}

	public Character findNearestEnemy(Character ch, int enemyIndex)
	{
		if(ch == null) return null;
		Character enemy = null;
		int dist = -1;
		for(String id: map.getAllCharactersId())
		{
			if(id.equals(ch.getUnitId())) continue;
			Character character = map.getCharacterById(id);
			if(character == null) continue;
			if(character.getImageIndex() == enemyIndex)
			{
				int ndist = distance(ch,character);
				if(ndist < dist || dist == -1)
				{
					dist = ndist;
					enemy = character;
				}
			}
		}
		return enemy;
	}

	public void randomMove()
	{
		for(String id: map.getAllCharactersId())
		{
			if(id.equals(hero)) continue;
			if(rand.nextInt(100) > 60)
			{
				Character character = map.getCharacterById(id);
				if(character != null && character.dest == null)
					character.move(directions[rand.nextInt(4)],map);
			}
		}
	}

	public boolean attack(Character from, Character to)
	{
		Character chero = map.getCharacterById(hero);
		map.fires.add(new Fire(to.getX(),to.getY(),rand.nextInt(10)+10));
		if(to == chero) return false;
		if(distance(from,to) == 1)
		{
			if(rand.nextInt(100) > 50)
			{
				if(rand.nextInt(100) > 50)
					to.setImageIndex(from.getImageIndex());
				else
					map.removeUnit(to);
				return true;
			}
			if(rand.nextInt(100) > 50)
			{
				if(rand.nextInt(100) > 50)
					from.setImageIndex(to.getImageIndex());
				else
					map.removeUnit(from);
				return true;
			}
		}
		return false;
	}

	public void attack()
	{
		for(String id: map.getAllCharactersId())
		{
			if(id.equals(hero)) continue;
			Character character = map.getCharacterById(id);
			if(character == null) continue;
			int x = character.getX(), y = character.getY();
			int from = character.getImageIndex();
			int to = attacking[from];
			if(to == -1) continue;
			Unit t = null;
			t = map.getUnitByPosition(x-1,y);
			if(t != null && t instanceof Character)
			{
				Character tc = (Character)t;
				if(tc.getImageIndex() == to)
					if(attack(character,tc)) continue;
			}
			t = map.getUnitByPosition(x+1,y);
			if(t != null && t instanceof Character)
			{
				Character tc = (Character)t;
				if(tc.getImageIndex() == to)
					if(attack(character,tc)) continue;
			}
			t = map.getUnitByPosition(x,y-1);
			if(t != null && t instanceof Character)
			{
				Character tc = (Character)t;
				if(tc.getImageIndex() == to)
					if(attack(character,tc)) continue;
			}
			t = map.getUnitByPosition(x,y+1);
			if(t != null && t instanceof Character)
			{
				Character tc = (Character)t;
				if(tc.getImageIndex() == to)
					if(attack(character,tc)) continue;
			}
		}
	}

	public void callBackTroops()
	{
		for(int i = 0; i < numOfCountries; i++)
			callBackTroop(i);
	}

	public void callBackTroop(int index)
	{
		troopPoint[index] = gatherPoints[index];
	}

	public void gather()
	{
		for(int i = 0; i < numOfCountries; i++)
			gather(i);
	}

	public boolean outOfCountry(Character ch)
	{
		if(ch == null) return false;
		int index = ch.getImageIndex();
		return distance(ch.getPoint(),gatherPoints[index])>15;
	}

	public void gather(int index)
	{
		TreeSet<String> troop = troops.get(index);
		Character chero = map.getCharacterById(hero);
		if(backToCountry[index] != null)
		{
			if( (troop != null && troop.contains(backToCountry[index].getUnitId())) ||
				backToCountry[index].target != null ||
				!outOfCountry(backToCountry[index]) ||
				backToCountry[index] == chero); 
				backToCountry[index] = null;
		}
		if(backToCountry[index] == null)
		{
			for(String id: countries.get(index))
			{
				if(id.equals(hero)) continue;
				Character character = map.getCharacterById(id);
				if(character == null) continue;
				if(outOfCountry(character) && character.target == null)
				{
					backToCountry[index] = character;
					continue;
				}
			}
		}
		if(backToCountry[index] != null)
		{
			if(backToCountry[index].dest == null ||
				distance(backToCountry[index].dest,gatherPoints[index])>10)
			{
				int destX = gatherPoints[index].x;
				int destY = gatherPoints[index].y;
				backToCountry[index].setDest(destX+rand.nextInt(20)-10,
					destY+rand.nextInt(20)-10);
			}
		}
	}

	public void setGatherPoints(String s)
	{
		String ss[] = s.trim().split(" ");
		if(ss.length != 3) return;
		int i = Integer.parseInt(ss[0]);
		int x = Integer.parseInt(ss[1]);
		int y = Integer.parseInt(ss[2]);
		gatherPoints[i] = new Point(x,y);
	}

	public void setAttacking(String s)
	{
		String ss[] = s.trim().split(" ");
		if(ss.length != 2) return;
		int i = Integer.parseInt(ss[0]);
		int j = Integer.parseInt(ss[1]);
		attacking[i] = j;
	}

	public int distance(Unit u1, Unit u2)
	{
		return distance(u1.getPoint(),u2.getPoint());
	}

	public int distance(Point p1, Point p2)
	{
		return Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
	}

	public int distance(int x, int y, Point p)
	{
		return Math.abs(x-p.x)+Math.abs(y-p.y);
	}
}
