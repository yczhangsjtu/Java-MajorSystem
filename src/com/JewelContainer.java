package com;

import java.util.TreeMap;
import java.util.Set;
import java.awt.Image;

public class JewelContainer extends UnitContainer{
	TreeMap<String,Jewel> jewels;
	Image moneyImage;
	int maxSize = 20;
	public JewelContainer()
	{
		jewels = new TreeMap<String,Jewel>();
		moneyImage = getUnitImage("resource/images/unit/money.png");
	}
	public void addMoney(String id, int x, int y)
	{
		if(jewels.size() >= maxSize) return;
		Money money = new Money(id,x,y,moneyImage);
		jewels.put(id,money);
	}
	public void addJewel(String id, Jewel jewel)
	{
		if(jewels.size() >= maxSize) return;
		jewels.put(id,jewel);
	}
	public void addUnit(String id, Unit unit)
	{
		if(unit instanceof Jewel)
			addJewel(id,(Jewel)unit);
	}
	public void removeUnit(String id)
	{
		jewels.put(id,null);
	}
	public Unit getUnitById(String id)
	{
		return jewels.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return jewels.keySet();
	}
}
