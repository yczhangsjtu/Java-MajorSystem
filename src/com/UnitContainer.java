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
import java.awt.Point;
import javax.imageio.ImageIO;

import com.Unit;

public class UnitContainer{
	TreeMap<String,Unit> units;
	public UnitContainer()
	{
		units = new TreeMap<String,Unit>();
	}
	public void clear()
	{
		units = new TreeMap<String,Unit>();
	}
	public void execute(String s, CharacterContainer cc,
		JewelContainer jc, QuizContainer qc, TransportContainer tc,
		OracleContainer oc, MapContainer map)
	{
		s = s.trim();
		String []ss = s.split(" ");
		if(ss.length < 2) return;
		if(ss[1].equals("Character"))
		{
			int x,y,m=0;
			if(ss.length < 4) return;
			x = Integer.parseInt(ss[2]);
			y = Integer.parseInt(ss[3]);
			if(ss.length > 4) m = Integer.parseInt(ss[4]);
			int k = cc.getImageIndex(ss[0]);
			if(map.isEmpty(x,y))
			{
				cc.addCharacter(ss[0],x,y);
				cc.getCharacterById(ss[0]).setMoney(m);
				units.put(ss[0],cc.getUnitById(ss[0]));
			}
		}
		else if(ss[1].equals("Money"))
		{
			if(ss.length < 4) return;
			int x = Integer.parseInt(ss[2]);
			int y = Integer.parseInt(ss[3]);
			if(map.isEmpty(x,y))
			{
				jc.addMoney(ss[0],x,y);
				units.put(ss[0],jc.getUnitById(ss[0]));
			}
		}
		else if(ss[1].equals("Quiz"))
		{
			if(ss.length < 4) return;
			int x = Integer.parseInt(ss[2]);
			int y = Integer.parseInt(ss[3]);
			if(map.isEmpty(x,y))
			{
				qc.addQuiz(ss[0],x,y);
				Quiz qz = qc.getQuizById(ss[0]);
				units.put(qz.getUnitId(),qz);
			}
		}
		else if(ss[1].equals("Transport"))
		{
			if(ss.length < 6) return;
			int x = Integer.parseInt(ss[2]);
			int y = Integer.parseInt(ss[3]);
			int tx = Integer.parseInt(ss[4]);
			int ty = Integer.parseInt(ss[5]);
			if(map.isEmpty(x,y))
			{
				tc.addTransport(ss[0],x,y,tx,ty);
				Transport tp = tc.getTransportById(ss[0]);
				units.put(tp.getUnitId(),tp);
			}
		}
		else if(ss[1].equals("Oracle"))
		{
			if(ss.length < 4) return;
			int x = Integer.parseInt(ss[2]);
			int y = Integer.parseInt(ss[3]);
			if(map.isEmpty(x,y))
			{
				oc.addOracle(ss[0],x,y);
				Oracle or = oc.getOracleById(ss[0]);
				units.put(or.getUnitId(),or);
			}
		}
	}
	public void executeAllActions(MapContainer map)
	{
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			if(unit != null)
			{
				unit.executeAllActions(map);
			}
		}
	}
	public void draw(Graphics g, int viewOffsetX, int viewOffsetY, int clock)
	{
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			if(unit != null) unit.draw(g, viewOffsetX, viewOffsetY, clock);
		}
	}
	public void drawMini(Graphics g, int miniSize, int miniPad, int miniWidth,
		int windowWidth, boolean left)
	{
		int x0 = miniPad, y0 = miniPad;
		if(!left) x0 = windowWidth - miniWidth - miniPad;
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			if(unit != null)
			{
				int x = unit.getX()*miniSize+x0;
				int y = unit.getY()*miniSize+y0;
				if(unit instanceof Character)
					g.setColor(Color.BLUE);
				else if(unit instanceof Jewel)
					g.setColor(Color.YELLOW);
				else if(unit instanceof Quiz)
					g.setColor(Color.RED);
				else if(unit instanceof Transport)
					g.setColor(Color.GREEN);
				else if(unit instanceof Oracle)
					g.setColor(Color.BLACK);
				g.fillRect(x,y,miniSize,miniSize);
			}
		}
	}
	public void update(MapContainer map)
	{
		for(String id: getAllUnitsId())
		{
			Unit unit = units.get(id);
			if(unit != null) unit.update(map);
		}
	}
	public void addUnit(String id, Unit unit)
	{
		units.put(id,unit);
	}
	public void removeUnit(String id)
	{
		units.put(id,null);
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
