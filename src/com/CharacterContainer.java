package com;

import java.util.TreeMap;

import com.UnitContainer;
import com.MapContainer;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CharacterContainer extends UnitContainer{
	TreeMap<String,Character> characters;
	int imageNum = 2;
	Image characterImage[][][] = new Image[imageNum][4][4];
	public CharacterContainer()
	{
		characters = new TreeMap<String,Character>();
	}
	public void getImageFromUnitContainer(UnitContainer uc)
	{
		for(int i = 0; i < imageNum; i++)
		{
			if(uc.getImageType(i) == UnitContainer.ImageType.Character)
				characterImage[i] = getCharacterImage(uc.getImage(i));
			else
				characterImage[i] = null;
		}
	}
	public void addCharacter(String characterId, Character c)
	{
		characters.put(characterId,c);
		super.addUnit(characterId,c);
	}
	public void addCharacter(String characterId, int x, int y, int k)
	{
		Character c = new Character(characterId,x,y,characterImage[k]);
		addCharacter(characterId,c);
	}
	public Character getCharacterById(String characterId)
	{
		return characters.get(characterId);
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
