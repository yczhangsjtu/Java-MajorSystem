package com.editor;

import java.awt.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;
import javax.imageio.ImageIO;

public class MapEditor extends JFrame
{
	Canvas canvas;
	public static void main(String[] args)
	{
		new MapEditor("Map Editor");
	}

	public MapEditor(String s)
	{
		super(s);
		canvas = new Canvas("map.txt");
		canvas.setFocusable(true);
		canvas.setSize(new Dimension(800,700));
		
		add(canvas);
		setLayout(new BorderLayout());
		setLocation(200, 0);
		setSize(800,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
}

class Canvas extends JPanel
{
	int mapWidth = 100;
	int mapHeight = 100;
	int gridSize = 7;
	int toolSize = 50;
	int pen = 0;
	int pensize = 1;
	int map[][] = new int[mapWidth][mapHeight];
	Image groundImage[] = new Image[3];
	Image sizeImage[] = new Image[5];
	ToolBox groundTools[] = new ToolBox[3];
	ToolBox sizeTools[] = new ToolBox[5];

	public Canvas(String mapfile)
	{
		try
		{
			groundImage[0] = ImageIO.read(new File("resource/images/ground/ground0.jpg"));
			groundImage[1] = ImageIO.read(new File("resource/images/ground/ground1.jpg"));
			groundImage[2] = ImageIO.read(new File("resource/images/ground/ground2.jpg"));
			sizeImage[0] = ImageIO.read(new File("resource/images/icon/size1.jpg"));
			sizeImage[1] = ImageIO.read(new File("resource/images/icon/size2.jpg"));
			sizeImage[2] = ImageIO.read(new File("resource/images/icon/size3.jpg"));
			sizeImage[3] = ImageIO.read(new File("resource/images/icon/size4.jpg"));
			sizeImage[4] = ImageIO.read(new File("resource/images/icon/size5.jpg"));
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
		for(int i = 0; i < 3; i++)
			groundTools[i] = new ToolBox(1,i,groundImage[i],i,-1);
		for(int i = 0; i < 5; i++)
			sizeTools[i] = new ToolBox(1,i,sizeImage[i],-1,i+1);
	}

	public void paint(Graphics g)
	{
		for(int x = 0; x < mapWidth; x++)
		{
			for(int y = 0; y < mapHeight; y++)
			{
				int k = map[x][y];
				int X = x*gridSize + toolSize*2, Y = y*gridSize;
				g.drawImage(groundImage[k],X,Y,gridSize,gridSize,null);
			}
		}
	}
}

class ToolBox{
	int size = 50;
	int X,Y,V1,V2;
	Image image;
	public ToolBox(int x, int y, Image img, int v1, int v2)
	{
		X = x;
		Y = y;
		image = img;
		V1 = v1;
		V2 = v2;
	}
	public void draw(Graphics g)
	{
		g.drawImage(image,X*size,Y*size,size,size,null);
	}
	public boolean inBox(int x, int y)
	{
		int x0 = X*size, y0 = Y*size;
		int x1 = x0+size, y1 = y0+size;
		return x >= x0 && x <= x1 && y >= y0 && y <= y1;
	}
	public int getV(int k)
	{
		if(k == 0) return V1;
		return V2;
	}
}
