package com;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;

import com.Memo;
import com.Character;

public class Canvas extends JPanel
{
	Memo memo = new Memo();
	MapContainer map;
	public Canvas()
	{
		map = new MapContainer("map.txt","units.txt");
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_LEFT) /*Left*/
				{
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) /*Up*/
				{
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) /*Right*/
				{
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) /*Down*/
				{
				}
				else if(e.getKeyCode() == KeyEvent.VK_SPACE)
				{
				}
				repaint();
			}
		});
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		map.draw(g);
	}
}
