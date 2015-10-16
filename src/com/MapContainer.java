package com;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.LinkedList;
import javax.imageio.ImageIO;

public class MapContainer{
	int gridSize = 50;
	int mapWidth = 100;
	int mapHeight = 100;
	int viewOffsetX = 0;
	int viewOffsetY = 0;
	int windowWidth = 800;
	int windowHeight = 600;
	int clock = 0;
	int map[][] = new int[mapWidth][mapHeight];
	Unit units[][] = new Unit[mapWidth][mapHeight];
	LinkedList<String> instructions = new LinkedList<String>();
	UnitContainer uc;
	CharacterContainer cc;
	Image groundImage[] = new Image[3];
	public MapContainer(String mapfile)
	{
		try
		{
			groundImage[0] = ImageIO.read(new File("resource/images/ground/ground0.jpg"));
			groundImage[1] = ImageIO.read(new File("resource/images/ground/ground1.jpg"));
			groundImage[2] = ImageIO.read(new File("resource/images/ground/ground2.jpg"));
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
		int imgWidth = gridSize * mapWidth;
		int imgHeight = gridSize * mapHeight;
		int maxOffsetX = imgWidth - windowWidth;
		int maxOffsetY = imgHeight - windowHeight;
		if(viewOffsetX < 0) viewOffsetX = 0;
		if(viewOffsetX > maxOffsetX) viewOffsetX = maxOffsetX;
		if(viewOffsetY < 0) viewOffsetY = 0;
		if(viewOffsetY > maxOffsetX) viewOffsetY = maxOffsetY;
	}

	public void readUnitFile(String filename)
	{
		uc.readFromFile("resource/unit/"+filename);
		cc.getFromUnitContainer(uc);
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
}
