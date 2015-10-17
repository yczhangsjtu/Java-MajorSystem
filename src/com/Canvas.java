package com;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;

import com.Memo;
import com.Character;

public class Canvas extends JPanel implements ActionListener
{
	Memo memo = new Memo();
	MapContainer map;
	Timer timer;
	public Canvas()
	{
		map = new MapContainer("map.txt");
		map.readUnitFile("units.txt");
		map.readInstructionFile("instruction.txt");
		timer = new Timer(50,this);
		timer.setRepeats(true);
		timer.setActionCommand("tick");
		timer.start();
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_LEFT) /*Left*/
				{
					map.addInstruction("move 0 left");
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) /*Up*/
				{
					map.addInstruction("move 0 up");
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) /*Right*/
				{
					map.addInstruction("move 0 right");
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) /*Down*/
				{
					map.addInstruction("move 0 down");
				}
				else if(e.getKeyCode() == KeyEvent.VK_SPACE)
				{
				}
				repaint();
			}
		});
		repaint();
	}

	public void actionPerformed(ActionEvent ae)
	{
		String cmd = ae.getActionCommand();
		if(cmd.equals("tick"))
		{
			map.tick();
			map.focus("0");
		}
		repaint();
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		map.draw(g);
	}
}
