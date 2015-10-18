package com;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.TreeSet;
import javax.imageio.ImageIO;

public class MapContainer{
	int gridSize = 50;
	int miniSize = 2;
	int miniPad = 10;
	int mapWidth = 100;
	int mapHeight = 100;
	int imgWidth = gridSize * mapWidth;
	int imgHeight = gridSize * mapHeight;
	int miniWidth = miniSize * mapWidth;
	int miniHeight = miniSize * mapHeight;
	int viewOffsetX = 0;
	int viewOffsetY = 0;
	int windowWidth = 800;
	int windowHeight = 600;
	int clock = 0;
	int map[][] = new int[mapWidth][mapHeight];
	int groundTypeNum = 4;
	boolean miniLeft = true;
	Unit units[][] = new Unit[mapWidth][mapHeight];
	LinkedList<String> instructions = new LinkedList<String>();
	UnitContainer uc;
	CharacterContainer cc;
	Image groundImage[] = new Image[groundTypeNum];
	public MapContainer(String mapfile)
	{
		try
		{
			for(int i = 0; i < groundTypeNum; i++)
				groundImage[i] = ImageIO.read(new File("resource/images/ground/ground"+i+".jpg"));
			BufferedReader br = new BufferedReader(
					new FileReader("resource/map/"+mapfile));
			for(int y = 0; y < mapHeight; y++)
			{
				String line = br.readLine();
				String [] s = line.trim().split(" ");
				for(int x = 0; x < mapWidth; x++)
					map[x][y] = Integer.parseInt(s[x]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		uc = new UnitContainer();
		cc = new CharacterContainer();
		cc.getImageFromUnitContainer(uc);
	}

	public void draw(Graphics g)
	{
		adaptViewOffset();
		for(int x = 0; x < mapWidth; x++)
		{
			for(int y = 0; y < mapHeight; y++)
			{
				int k = map[x][y];
				int X = x*gridSize - viewOffsetX, Y = y*gridSize - viewOffsetY;
				if(X + gridSize >= 0 && X <= windowWidth &&
				   Y + gridSize >= 0 && Y <= windowHeight)
			    {
					g.drawImage(groundImage[k],X,Y,gridSize,gridSize,null);
				}
			}
		}
		uc.draw(g,viewOffsetX,viewOffsetY,clock);
		drawMiniMap(g,miniLeft);
		uc.drawMini(g,miniSize,miniPad,miniWidth,windowWidth,miniLeft);
	}

	public void drawMiniMap(Graphics g, boolean left)
	{
		int x0 = miniPad, y0 = miniPad;
		if(!left) x0 = windowWidth - miniWidth - miniPad;
		for(int x = 0; x < mapWidth; x++)
		{
			for(int y = 0; y < mapHeight; y++)
			{
				int k = map[x][y];
				int X = x*miniSize+x0, Y = y*miniSize+y0;
				g.drawImage(groundImage[k],X,Y,miniSize,miniSize,null);
			}
		}
		g.setColor(Color.BLACK);
		int miniOffX = viewOffsetX * miniWidth / imgWidth;
		int miniOffY = viewOffsetY * miniHeight / imgHeight;
		int miniWindowWidth = windowWidth * miniWidth / imgWidth;
		int miniWindowHeight = windowHeight * miniHeight / imgHeight;
		g.drawRect(x0+miniOffX,y0+miniOffY,miniWindowWidth,miniWindowHeight);
	}

	public void focus(int x, int y)
	{
		viewOffsetX = x-windowWidth/2;
		viewOffsetY = y-windowHeight/2;
		adaptViewOffset();
		int X = x - viewOffsetX, Y = y - viewOffsetY;
		miniLeft = X > windowWidth - miniWidth - miniPad - gridSize
				&& X < windowWidth - miniPad && Y > miniPad - gridSize
				&& Y < miniPad + miniHeight;
	}

	public void focus(Unit unit)
	{
		int x = unit.getPixelX(clock), y = unit.getPixelY(clock);
		focus(x,y);
	}

	public void focus(String id)
	{
		Unit unit = uc.getUnitById(id);
		focus(unit);
	}

	public void tick()
	{
		clock++;
		if(clock >= 10)
		{
			clock = 0;
			update();
		}
	}

	public void executeInstructions()
	{
		while(instructions.size() > 0)
		{
			String instruction = instructions.get(0);
			if(!execute(instruction)) break;
			instructions.remove(0);
		}
	}

	public void addInstruction(String instruction)
	{
		instructions.add(instruction);
	}

	public boolean execute(String instruction)
	{
		String []tokens = instruction.trim().split(" ");
		if(tokens[0].equals("move"))
		{
			String target = tokens[1];
			String direction = tokens[2];
			cc.move(target,direction,this);
			return true;
		}
		else if(tokens[0].equals("addaction"))
		{
			String target = tokens[1];
			String action = tokens[2];
			uc.addAction(target,action);
		}
		return true;
	}

	public void update()
	{
		uc.update(this);
		executeInstructions();
	}

	public boolean isAvailable(int x, int y)
	{
		if(x < 0 || x >= mapWidth) return false;
		if(y < 0 || y >= mapHeight) return false;
		if(map[x][y] > 2) return false;
		if(units[x][y] != null) return false;
		return true;
	}

	public Unit getUnitByPosition(int x, int y)
	{
		return units[x][y];
	}

	public void setUnitByPosition(Unit unit, int x, int y)
	{
		units[x][y] = unit;
	}

	void adaptViewOffset()
	{
		int maxOffsetX = imgWidth - windowWidth;
		int maxOffsetY = imgHeight - windowHeight;
		if(viewOffsetX < 0) viewOffsetX = 0;
		if(viewOffsetX > maxOffsetX) viewOffsetX = maxOffsetX;
		if(viewOffsetY < 0) viewOffsetY = 0;
		if(viewOffsetY > maxOffsetY) viewOffsetY = maxOffsetY;
	}

	Unit getUnitById(String unitId)
	{
		return uc.getUnitById(unitId);
	}

	public void readUnitFile(String filename)
	{
		uc.readFromFile("resource/unit/"+filename,cc,this);
		for(String id: uc.getAllUnitsId())
		{
			Unit unit = uc.getUnitById(id);
			int x = unit.getX(), y = unit.getY();
			if(isAvailable(x,y))
				units[x][y] = unit;
		}
	}

	public void readInstructionFile(String filename)
	{
		try
		{
			BufferedReader br = new BufferedReader(
					new FileReader("resource/instruction/"+filename));
			String line = br.readLine();
			while(line != null)
			{
				instructions.add(line);
				line = br.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public LinkedList<Point> findPath(Point p1, Point p2)
	{
		LinkedList<Point> path = new LinkedList<Point>();
		if(p1.equals(p2)) return path;
		TreeSet<Node> heap = new TreeSet<Node>();
		TreeSet<Node> visited = new TreeSet<Node>();
		heap.add(new Node(p1,0));
		heap.first().setHCost(distance(p1,p2));
		while(!heap.isEmpty())
		{
			Node f = heap.first();
			if(visited.contains(f)) return null;
			heap.remove(f);
			TreeSet<Node> neighbors = getNeighbors(f,p2);
			if(neighbors.isEmpty()) return null;
			for(Node n : neighbors)
			{
				if(n.at(p2))
				{
					Node p = n;
					while(p != null && !p.at(p1))
					{
						path.addFirst(p.getPoint());
						p = p.getPrev();
					}
					return path;
				}
				n.setHCost(distance(n.getPoint(),p2));
				heap.add(n);
			}
		}
		return null;
	}
	int distance(Point p1, Point p2)
	{
		return Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
	}
	TreeSet<Node> getNeighbors(Node n, Point p2)
	{
		TreeSet<Node> neighbors = new TreeSet<Node>();
		for(Node nn : n.getNeighbors())
		{
			if(isAvailable(nn.getX(),nn.getY()) || nn.at(p2))
				neighbors.add(nn);
		}
		return neighbors;
	}
	boolean reachTarget(TreeSet<Node> set, Point p2)
	{
		for(Node nn : set)
		{
			if(nn.at(p2)) return true;
		}
		return false;
	}
}

class Node implements Comparable{
	Point point;
	int pcost, hcost;
	Node prev;
	public Node(Point p, int c)
	{
		point = p;
		pcost = c;
		hcost = 0;
		prev = null;
	}
	public boolean lessThan(Node n)
	{
		if(getCost() < n.getCost()) return true;
		if(getCost() > n.getCost()) return false;
		if(pcost < n.pcost) return true;
		if(pcost > n.pcost) return false;
		if(point.x < n.getX()) return true;
		if(point.x > n.getX()) return false;
		return point.y < n.getY();
	}
	public boolean equalTo(Node n)
	{
		return pcost == n.pcost && hcost == n.hcost;
	}
	public boolean equals(Node n)
	{
		return point.equals(n.point) && pcost == n.pcost && hcost == n.hcost;
	}
	public int compareTo(Object n)
	{
		if(lessThan((Node)n)) return -1;
		if(equals((Node)n)) return 0;
		return 1;
	}
	public boolean at(Point p)
	{
		return point.equals(p);
	}
	public int distance(Node n)
	{
		return Math.abs(point.x-n.getX()) + Math.abs(point.y-n.getY());
	}
	public Point getPoint()
	{
		return point;
	}
	public int getX()
	{
		return point.x;
	}
	public int getY()
	{
		return point.y;
	}
	public int getCost()
	{
		return pcost + hcost;
	}
	public void update(int c)
	{
		pcost = c;
	}
	public void setHCost(int h)
	{
		hcost = h;
	}
	public void setPrev(Node n)
	{
		prev = n;
	}
	public Node getPrev()
	{
		return prev;
	}
	public Node north()
	{
		Node n = new Node(new Point(getX(),getY()-1),pcost+1);
		n.setPrev(this);
		return n;
	}
	public Node south()
	{
		Node n = new Node(new Point(getX(),getY()+1),pcost+1);
		n.setPrev(this);
		return n;
	}
	public Node west()
	{
		Node n = new Node(new Point(getX()-1,getY()),pcost+1);
		n.setPrev(this);
		return n;
	}
	public Node east()
	{
		Node n = new Node(new Point(getX()+1,getY()),pcost+1);
		n.setPrev(this);
		return n;
	}
	TreeSet<Node> getNeighbors()
	{
		TreeSet<Node> neighbors = new TreeSet<Node>();
		neighbors.add(north());
		neighbors.add(south());
		neighbors.add(east());
		neighbors.add(west());
		return neighbors;
	}
}
