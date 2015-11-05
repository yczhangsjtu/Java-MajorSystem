package com;

public class Fire
{
	int x,y,l;
	public Fire(int X, int Y, int L)
	{
		x = X;
		y = Y;
		l = L;
	}
	public void countDown()
	{
		if(l > 0) l--;
	}
	public boolean live()
	{
		return l > 0;
	}
}
