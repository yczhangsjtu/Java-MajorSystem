package com;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.imageio.ImageIO;

public class MapContainer{
	int gridSize = 50;
	int mapWidth = 100;
	int mapHeight = 100;
	int viewOffsetX = 0;
	int viewOffsetY = 0;
	int windowWidth = 800;
	int windowHeight = 600;
	Image groundImage[] = new Image[3];
	Toolkit tk = Toolkit.getDefaultToolkit();
	int map[][] = new int[mapWidth][mapHeight];
	UnitContainer uc;
	CharacterContainer cc;
	public MapContainer(String mapfile, String unitfile)
	{
		groundImage[0] = tk.createImage("resource/images/ground/ground0.jpg");
		groundImage[1] = tk.createImage("resource/images/ground/ground1.jpg");
		groundImage[2] = tk.createImage("resource/images/ground/ground2.jpg");
		try
		{
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
		uc.readFromFile("resource/unit/"+unitfile);
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
				g.drawImage(groundImage[k],X,Y,gridSize,gridSize,null);
			}
		}
		uc.draw(g);
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
}
