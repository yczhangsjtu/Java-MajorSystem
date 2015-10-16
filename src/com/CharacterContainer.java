package com;

import java.util.TreeMap;

import com.UnitContainer;
import com.MapContainer;

public class CharacterContainer extends UnitContainer{
	TreeMap<String,Character> characters;
	public CharacterContainer()
	{
		characters = new TreeMap<String,Character>();
	}
	public void getFromUnitContainer(UnitContainer uc)
	{
		for(String id: uc.getAllUnitsId())
		{
			Unit unit = uc.getUnitById(id);
			if(unit instanceof Character)
				characters.put(id,(Character)unit);
		}
	}
	public void move(String target, String direction, MapContainer map)
	{
		Character character = characters.get(target);
		if(direction.equals("left"))
			character.moveLeft(map);
		if(direction.equals("right"))
			character.moveRight(map);
		if(direction.equals("up"))
			character.moveUp(map);
		if(direction.equals("down"))
			character.moveDown(map);
	}
}
