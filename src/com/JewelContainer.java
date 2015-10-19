package com;

import java.util.TreeMap;
import java.awt.Image;

public class JewelContainer extends UnitContainer{
	TreeMap<String,Jewel> jewels;
	Image moneyImage;
	public JewelContainer()
	{
		jewels = new TreeMap<String,Jewel>();
		moneyImage = getUnitImage("resource/images/unit/money.png");
	}
	public void addMoney(String id, int x, int y)
	{
		Money money = new Money(id,x,y,moneyImage);
		jewels.put(id,money);
		super.addUnit(id,money);
	}
}
