package com.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
	int canvasWidth = mapWidth*gridSize;
	int canvasHeight = mapHeight*gridSize;
	int toolSize = 50;
	int ground = 0;
	int pensize = 1;
	int map[][] = new int[mapWidth][mapHeight];
	int groundNum = 4;
	int sizeNum = 5;
	Image groundImage[] = new Image[groundNum];
	Image sizeImage[] = new Image[sizeNum];
	Image saveImage;
	ToolBox groundTools[] = new ToolBox[groundNum];
	ToolBox sizeTools[] = new ToolBox[sizeNum];
	ToolBox saveTool;
	String mapfilename;

	public Canvas(String mapfile)
	{
		mapfilename = mapfile;
		try
		{
			for(int i = 0; i < groundNum; i++)
				groundImage[i] = ImageIO.read(new File("resource/images/ground/ground"+i+".jpg"));
			for(int i = 0; i < sizeNum; i++)
				sizeImage[i] = ImageIO.read(new File("resource/images/icon/size"+(i+1)+".jpg"));
			saveImage = ImageIO.read(new File("resource/images/icon/save.jpg"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		readFromFile(mapfile);
		for(int i = 0; i < groundNum; i++)
			groundTools[i] = new ToolBox(1,i,groundImage[i],i);
		for(int i = 0; i < sizeNum; i++)
			sizeTools[i] = new ToolBox(0,i,sizeImage[i],i+1);
		saveTool = new ToolBox(1,sizeNum+1,saveImage,0);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				int x = e.getX();
				int y = e.getY();
				for(int i = 0; i < groundNum; i++)
				{
					if(groundTools[i].inBox(x,y))
					{
						ground = groundTools[i].getV();
						return;
					}
				}
				for(int i = 0; i < sizeNum; i++)
				{
					if(sizeTools[i].inBox(x,y))
					{
						pensize = sizeTools[i].getV();
						return;
					}
				}
				if(saveTool.inBox(x,y))
				{
					saveToFile(mapfilename);
				}
				paintGround(x,y);
				repaint();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent e)
			{
				int x = e.getX();
				int y = e.getY();
				paintGround(x,y);
				repaint();
			}
		});
	}

	public void paintGround(int x, int y)
	{
		if(x > toolSize * 2 && x < toolSize * 2 + canvasWidth)
		{
			if(y > 0 && y < canvasHeight)
			{
				x -= toolSize * 2;
				x /= gridSize;
				y /= gridSize;
				for(int i = 0; i < pensize; i++)
					for(int j = 0; j < pensize; j++)
					{
						int X = x-pensize/2+i;
						int Y = y-pensize/2+j;
						if(X >= 0 && X < mapWidth && Y >= 0 && Y < mapHeight)
							map[X][Y] = ground;
					}
			}
		}
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
		for(int i = 0; i < groundNum; i++)
			groundTools[i].draw(g);
		for(int i = 0; i < sizeNum; i++)
			sizeTools[i].draw(g);
		saveTool.draw(g);
	}

	public void readFromFile(String filename)
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(
					new FileReader("resource/map/"+filename));
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
		finally
		{
			try
			{
				if(br != null)
					br.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void saveToFile(String filename)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(
					new FileWriter("resource/map/"+filename));
			for(int y = 0; y < mapHeight; y++)
			{
				String line = "";
				for(int x = 0; x < mapWidth; x++)
				{
					line += map[x][y] + " ";
				}
				line = line.trim();
				bw.write(line+"\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(bw != null){
					bw.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

class ToolBox{
	int size = 50;
	int X,Y,V;
	Image image;
	public ToolBox(int x, int y, Image img, int v)
	{
		X = x;
		Y = y;
		image = img;
		V = v;
	}
	public void draw(Graphics g)
	{
		g.drawImage(image,X*size,Y*size,size,size,null);
		g.drawRect(X*size,Y*size,size,size);
	}
	public boolean inBox(int x, int y)
	{
		int x0 = X*size, y0 = Y*size;
		int x1 = x0+size, y1 = y0+size;
		return x >= x0 && x <= x1 && y >= y0 && y <= y1;
	}
	public int getV()
	{
		return V;
	}
}
