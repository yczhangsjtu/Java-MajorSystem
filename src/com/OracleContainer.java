package com;

import java.util.TreeMap;
import java.util.Set;
import java.awt.Image;

public class OracleContainer extends UnitContainer{
	TreeMap<String,Oracle> oracles;
	Image oracleImage;
	public OracleContainer()
	{
		oracles = new TreeMap<String,Oracle>();
		oracleImage = getUnitImage("resource/images/unit/oracle.png");
	}
	public void addOracle(String id, int x, int y)
	{
		Oracle oracle = new Oracle(id,x,y,oracleImage);
		oracles.put(id,oracle);
	}
	public void addOracle(String id, Oracle oracle)
	{
		oracles.put(id,oracle);
	}
	public Oracle getOracleById(String id)
	{
		return oracles.get(id);
	}
	public void addUnit(String id, Unit unit)
	{
		if(unit instanceof Oracle)
			addOracle(id,(Oracle)unit);
	}
	public void removeUnit(String id)
	{
		oracles.put(id,null);
	}
	public Unit getUnitById(String id)
	{
		return oracles.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return oracles.keySet();
	}
}
