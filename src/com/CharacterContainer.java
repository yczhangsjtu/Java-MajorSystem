package com;

import java.util.TreeMap;
import java.util.Set;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.UnitContainer;
import com.MapContainer;

public class CharacterContainer extends UnitContainer{
	TreeMap<String,Character> characters;
	public static int imageNum = 5;
	Image characterImage[][][] = new Image[imageNum][4][4];
	public CharacterContainer()
	{
		characters = new TreeMap<String,Character>();
		for(int i = 0; i < imageNum; i++)
		{
			characterImage[i] = getCharacterImage(
				getUnitImage("resource/images/unit/unit"+i+".png"));
		}
	}
	public void addCharacter(String characterId, Character c)
	{
		characters.put(characterId,c);
		//super.addUnit(characterId,c);
	}
	public void addCharacter(String characterId, int x, int y)
	{
		int k = getImageIndex(characterId);
		Character c = new Character(characterId,x,y,characterImage[k]);
		addCharacter(characterId,c);
	}
	public Character getCharacterById(String characterId)
	{
		return characters.get(characterId);
	}
	public void addUnit(String id, Unit unit)
	{
		if(unit instanceof Character)
			addCharacter(id,(Character)unit);
	}
	public void removeUnit(String id)
	{
		characters.put(id,null);
	}
	public Unit getUnitById(String id)
	{
		return characters.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return characters.keySet();
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
	public int getImageIndex(String str)
	{
		return hash(str)%imageNum;
	}
	public Image[][] getCharacterImage(BufferedImage image)
	{
		Image result[][] = new Image[4][4];
		int width = image.getWidth()/4;
		int height = image.getHeight()/4;
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				result[i][j] = image.getSubimage(j*width,i*height,width,height);
		return result;
	}
}
