package com;

import java.util.Set;
import java.util.TreeMap;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.ImageIO;

import com.Unit;

public class UnitContainer{
	TreeMap<String,Unit> units;
	Image characterImage[][][] = new Image[2][4][4];
	public UnitContainer()
	{
		characterImage[0] = getUnitImage("resource/images/unit/unit0.png");
		characterImage[1] = getUnitImage("resource/images/unit/unit1.png");
		units = new TreeMap<String,Unit>();
	}
	public void readFromFile(String filename)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String s = br.readLine();
			while(s != null)
			{
				s = s.trim();
				String []ss = s.split(" ");
				if(s.startsWith("#"))
				{
					s = br.readLine();
					continue;
				}
				if(ss[1].equals("Character"))
				{
					int x = Integer.parseInt(ss[2]);
					int y = Integer.parseInt(ss[3]);
					int k = hash(ss[0])%2;
					units.put(ss[0],new Character(ss[0],x,y,characterImage[k]));
				}
				s = br.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void draw(Graphics g, int viewOffsetX, int viewOffsetY, int clock)
	{
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			unit.draw(g, viewOffsetX, viewOffsetY, clock);
		}
	}
	public void update(MapContainer map)
	{
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			unit.update(map);
		}
	}
	public Unit getUnitById(String id)
	{
		return units.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return units.keySet();
	}
	public void addAction(String id, String action)
	{
		units.get(id).addAction(action);
	}
	Image[][] getUnitImage(String filename)
	{
		try
		{
			BufferedImage big = ImageIO.read(new File(filename));
			Image result[][] = new Image[4][4];
			int width = big.getWidth();
			int height = big.getHeight();
			if(width < 100 && height < 100)
			{
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						result[i][j] = big;
			}
			else
			{
				width /= 4;
				height /= 4;
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						result[i][j] = big.getSubimage(j*width,i*height,width,height);
			}
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	int hash(String str)
	{
		int s = 0;
		for(int i = 0; i < str.length(); i++)
		{
			int c = str.codePointAt(i);
			s += c;
		}
		return s;
	}
}
