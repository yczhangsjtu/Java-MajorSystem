package com;

import java.util.TreeMap;
import java.awt.Image;

public class JewelContainer extends UnitContainer{
	TreeMap<String,Jewel> Jewels;
	Image moneyImage;
	public JewelContainer()
	{
		Jewels = new TreeMap<String,Jewel>();
		moneyImage = getUnitImage("resource/images/unit/money.png");
	}
	public void addMoney(String id, int x, int y)
	{
		Money money = new Money(id,x,y,moneyImage);
		Jewels.put(id,money);
		super.addUnit(id,money);
	}
}
