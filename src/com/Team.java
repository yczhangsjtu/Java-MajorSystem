package com;

import java.util.TreeSet;

public class Team{
	TreeSet<Character> members;
	public Team()
	{
		members = new TreeSet<Character>();
	}
	public void addMember(Character character)
	{
		if(character != null) members.add(character);
	}
	public void addMember(String id, CharacterContainer cc)
	{
		Character character = cc.getCharacterById(id);
		if(character != null) members.add(character);
	}
	public void addMember(String id, MapContainer cc)
	{
		addMember(id,cc.getCharacterContainer());
	}
	public boolean containMember(Character character)
	{
		return members.contains(character);
	}
	public boolean containMember(String id, CharacterContainer cc)
	{
		Character character = cc.getCharacterById(id);
		if(character == null) return false;
		return members.contains(character);
	}
	public boolean containMember(String id, MapContainer cc)
	{
		return containMember(id,cc.getCharacterContainer());
	}
	public void removeMember(Character character)
	{
		members.remove(character);
	}
	public void removeMember(String id, CharacterContainer cc)
	{
		Character character = cc.getCharacterById(id);
		if(character != null) members.remove(character);
	}
	public void removeMember(String id, MapContainer cc)
	{
		removeMember(id,cc.getCharacterContainer());
	}
	public boolean cover(String s, CharacterContainer cc)
	{
		int t = 0;
		while(t < s.length())
		{
			String u;
			if(t + 3 <= s.length())
				u = s.substring(t,t+3);
			else
				u = s.substring(t);
			if(!containMember(u,cc)) return false;
			t += 3;
		}
		return true;
	}
	public boolean cover(String s, MapContainer map)
	{
		return cover(s,map.getCharacterContainer());
	}
	public String toString()
	{
		String s = "";
		for(Character c : members)
			s += c.getUnitId() + " ";
		return s.trim();
	}
}
