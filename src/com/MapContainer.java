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
import java.util.Set;
import java.util.TreeSet;
import java.util.Random;
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
	LinkedList<Fire> fires;
	Image fireImage;
	UnitContainer uc;
	CharacterContainer cc;
	JewelContainer jc;
	QuizContainer qc;
	TransportContainer tc;
	OracleContainer oc;
	Image groundImage[] = new Image[groundTypeNum];
	Conversation conversation = null;
	String conversationResult = null;
	Random rand;
	public MapContainer(String mapfile)
	{
		fires = new LinkedList<Fire>();
		try
		{
			for(int i = 0; i < groundTypeNum; i++)
				groundImage[i] = ImageIO.read(new File("resource/images/ground/ground"+i+".jpg"));
			BufferedReader br = new BufferedReader(
					new FileReader("resource/map/"+mapfile));
			fireImage = ImageIO.read(new File("resource/images/effects/fire.png"));
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
		jc = new JewelContainer();
		qc = new QuizContainer();
		tc = new TransportContainer();
		oc = new OracleContainer();
		rand = new Random();
	}

	public void clear()
	{
		uc.clear();
		cc.clear();
		jc.clear();
		qc.clear();
		for(int x = 0; x < mapWidth; x++)
			for(int y = 0; y < mapHeight; y++)
				units[x][y] = null;
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
		uc.draw(g,viewOffsetX,viewOffsetY,clock,this);
		for(Fire fire: fires)
		{
			g.drawImage(fireImage,fire.x*gridSize-viewOffsetX,fire.y*gridSize-viewOffsetY,gridSize,gridSize,null);
		}
		drawMiniMap(g,miniLeft);
		uc.drawMini(g,miniSize,miniPad,miniWidth,windowWidth,miniLeft);
		if(isTalking())
		{
			conversation.draw(g,viewOffsetX,viewOffsetY,clock);
		}
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

	public boolean tick()
	{
		if(isTalking()) return false;
		clock++;
		if(clock >= 10)
		{
			clock = 0;
			update();
		}
		for(Fire fire: fires)
		{
			fire.countDown();
		}
		return true;
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
		LinkedList<Fire> newFires = new LinkedList<Fire>();
		for(Fire fire: fires)
			if(fire.live()) newFires.add(fire);
		fires = newFires;
	}

	public void talk(Character character, Unit unit)
	{
		conversation = new Conversation(character,unit,this);
	}

	public boolean isTalking()
	{
		if(conversation != null)
		{
			if(conversation.isFinished())
			{
				conversationResult = conversation.getResult();
				conversation = null;
			}
			else
				return true;
		}
		return false;
	}

	public boolean isAvailable(int x, int y)
	{
		if(x < 0 || x >= mapWidth) return false;
		if(y < 0 || y >= mapHeight) return false;
		if(map[x][y] > 2) return false;
		if(units[x][y] != null && units[x][y].ocupySpace()) return false;
		return true;
	}

	public boolean isEmpty(int x, int y)
	{
		if(x < 0 || x >= mapWidth) return false;
		if(y < 0 || y >= mapHeight) return false;
		if(map[x][y] > 2) return false;
		if(units[x][y] != null) return false;
		return true;
	}

	public void addRandomMoney()
	{
		int x = rand.nextInt(100);
		int y = rand.nextInt(100);
		if(isEmpty(x,y)) addMoney(x,y);
	}

	public void addMoney(int x, int y)
	{
		String id = "money"+(x * 100 + y);
		jc.addMoney(id,x,y);
		uc.addUnit(id,jc.getUnitById(id));
		units[x][y] = jc.getUnitById(id);
	}

	public void addCharacter(String id, int x, int y)
	{
		cc.addCharacter(id,x,y);
		uc.addUnit(id,cc.getUnitById(id));
		units[x][y] = cc.getUnitById(id);
	}

	public void addQuiz(String id, int x, int y)
	{
		qc.addQuiz(id,x,y);
		Quiz qz = qc.getQuizById(id);
		uc.addUnit(qz.getUnitId(),qz);
		units[x][y] = qz;
	}

	public Unit getUnitByPosition(int x, int y)
	{
		if(x >= 0 && y >= 0 && x < mapWidth && y < mapHeight)
			return units[x][y];
		return null;
	}

	public void removeUnit(int x, int y)
	{
		Unit unit = units[x][y];
		if(unit != null)
		{
			String id = units[x][y].getUnitId();
			uc.removeUnit(id);
			if(unit instanceof Character) cc.removeUnit(id);
			if(unit instanceof Jewel) jc.removeUnit(id);
			if(unit instanceof Quiz) qc.removeUnit(id);
			units[x][y] = null;
		}
	}

	public void removeUnit(Unit unit)
	{
		if(unit != null)
		{
			int x = unit.getX();
			int y = unit.getY();
			removeUnit(x,y);
		}
	}

	public void removeUnit(String id)
	{
		removeUnit(uc.getUnitById(id));
	}

	public void setUnitByPosition(Unit unit, int x, int y)
	{
		units[x][y] = unit;
	}

	public void removeQuiz(String id)
	{
		Quiz qz = qc.getQuizById(id);
		if(qz != null) removeUnit(qz.getUnitId());
	}

	public void solveQuiz(String id)
	{
		Quiz qz = qc.getQuizById(id);
		if(qz != null) qz.setSolved(true,this);
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

	Character getCharacterById(String characterId)
	{
		return cc.getCharacterById(characterId);
	}

	public Set<String> getAllCharactersId()
	{
		return cc.getAllUnitsId();
	}

	CharacterContainer getCharacterContainer()
	{
		return cc;
	}

	public Set<String> getAllUnitsId()
	{
		return uc.getAllUnitsId();
	}

	public Quiz getQuizById(String id)
	{
		return qc.getQuizById(id);
	}

	public void setUnitMap()
	{
		for(String id: getAllUnitsId())
		{
			Unit unit = uc.getUnitById(id);
			if(unit != null)
			{
				int x = unit.getX(), y = unit.getY();
				if(isEmpty(x,y)) units[x][y] = unit;
			}
		}
	}

	public int getWindowWidth()
	{
		return windowWidth;
	}

	public int getWindowHeight()
	{
		return windowHeight;
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
		int value[][] = new int[mapWidth][mapHeight];
		for(int i = 0; i < mapWidth; i++)
			for(int j = 0; j < mapHeight; j++)
				value[i][j] = -1;
		heap.add(new Node(p1,0));
		heap.first().setHCost(distance(p1,p2));
		while(!heap.isEmpty())
		{
			Node f = heap.first();
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
				int v = value[n.getX()][n.getY()];
				if(v == -1 || n.getCost() < v)
				{
					Node m = new Node(n.getPoint(),v);
					value[n.getX()][n.getY()] = n.getCost();
					heap.remove(m);
					heap.add(n);
				}
			}
		}
		return null;
	}
	int distance(Point p1, Point p2)
	{
		return Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
	}
	int distance(Node n, Point p2)
	{
		return distance(n.getPoint(),p2);
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
		if(point.x < n.getX()) return true;
		if(point.x > n.getX()) return false;
		return point.y < n.getY();
	}
	public boolean equalTo(Node n)
	{
		return getCost() == n.getCost();
	}
	public boolean equals(Node n)
	{
		return point.equals(n.point) && getCost() == n.getCost();
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
