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
import java.awt.Color;
import javax.imageio.ImageIO;

import com.Unit;

public class UnitContainer{
	TreeMap<String,Unit> units;
	public static int imageNum = 4;
	public enum ImageType {Character}
	BufferedImage images[] = new BufferedImage[imageNum];
	ImageType imageTypes[] = new ImageType[imageNum];
	public UnitContainer()
	{
		for(int i = 0; i < imageNum; i++)
			images[i] = getUnitImage("resource/images/unit/unit"+i+".png");
		imageTypes[0] = ImageType.Character;
		imageTypes[1] = ImageType.Character;
		imageTypes[2] = ImageType.Character;
		imageTypes[3] = ImageType.Character;
		units = new TreeMap<String,Unit>();
	}
	public void readFromFile(String filename, CharacterContainer cc)
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
					int k = hash(ss[0])%imageNum;
					cc.addCharacter(ss[0],x,y,k);
					units.put(ss[0],cc.getUnitById(ss[0]));
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
	public void drawMini(Graphics g, int miniSize, int miniPad, int miniWidth, int windowWidth, boolean left)
	{
		int x0 = miniPad, y0 = miniPad;
		if(!left) x0 = windowWidth - miniWidth - miniPad;
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			int x = unit.getX()*miniSize+x0;
			int y = unit.getY()*miniSize+y0;
			g.setColor(Color.BLUE);
			g.fillRect(x,y,miniSize,miniSize);
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
	public void addUnit(String id, Unit unit)
	{
		units.put(id,unit);
	}
	public Unit getUnitById(String id)
	{
		return units.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return units.keySet();
	}
	public BufferedImage getImage(int i)
	{
		return images[i];
	}
	public ImageType getImageType(int i)
	{
		return imageTypes[i];
	}
	public void addAction(String id, String action)
	{
		units.get(id).addAction(action);
	}
	BufferedImage getUnitImage(String filename)
	{
		try
		{
			BufferedImage image = ImageIO.read(new File(filename));
			return image;
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
