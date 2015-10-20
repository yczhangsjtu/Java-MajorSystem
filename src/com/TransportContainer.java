package com;

import java.util.TreeMap;
import java.util.Set;
import java.awt.Image;

public class TransportContainer extends UnitContainer{
	TreeMap<String,Transport> transports;
	Image transportImage;
	public TransportContainer()
	{
		transports = new TreeMap<String,Transport>();
		transportImage = getUnitImage("resource/images/unit/transport.png");
	}
	public void addTransport(String id, int x, int y, int tx, int ty)
	{
		Transport transport = new Transport(id,x,y,tx,ty,transportImage);
		transports.put(id,transport);
	}
	public void addTransport(String id, Transport transport)
	{
		transports.put(id,transport);
	}
	public Transport getTransportById(String id)
	{
		return transports.get(id);
	}
	public void addUnit(String id, Unit unit)
	{
		if(unit instanceof Transport)
			addTransport(id,(Transport)unit);
	}
	public void removeUnit(String id)
	{
		transports.put(id,null);
	}
	public Unit getUnitById(String id)
	{
		return transports.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return transports.keySet();
	}
}
