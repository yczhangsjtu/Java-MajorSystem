package com;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.Memo;
import com.Character;

public class Canvas extends JPanel implements ActionListener
{
	MapContainer map;
	Team team;
	Timer timer;
	Random rand;
	String hero = "0";
	String directions[] = new String[4];
	Pattern followAction = Pattern.compile("addaction (\\d+) follow_"+hero);
	int clock = 0;
	public Canvas()
	{
		map = new MapContainer("map.txt");
		map.readUnitFile("units.txt");
		map.readInstructionFile("instruction.txt");
		timer = new Timer(50,this);
		timer.setRepeats(true);
		timer.setActionCommand("tick");
		timer.start();
		rand = new Random();
		directions[0] = "left";
		directions[1] = "right";
		directions[2] = "up";
		directions[3] = "down";
		team = new Team();
		team.addMember(map.getCharacterById(hero));
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_LEFT) /*Left*/
				{
					if(map.isTalking()) map.conversation.press(e);
					else map.addInstruction("move "+hero+" left");
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) /*Up*/
				{
					if(map.isTalking()) map.conversation.press(e);
					else map.addInstruction("move "+hero+" up");
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) /*Right*/
				{
					if(map.isTalking()) map.conversation.press(e);
					else map.addInstruction("move "+hero+" right");
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) /*Down*/
				{
					if(map.isTalking()) map.conversation.press(e);
					else map.addInstruction("move "+hero+" down");
				}
				else if(e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					if(map.isTalking()) map.conversation.press(e);
					else
					{
						Character h = map.getCharacterById(hero);
						Unit t = null;
						if(h != null)
						{
							int x = h.getX(), y = h.getY();
							if(h.faceLeft()) t = map.getUnitByPosition(x-1,y);
							else if(h.faceRight()) t = map.getUnitByPosition(x+1,y);
							else if(h.faceUp()) t = map.getUnitByPosition(x,y-1);
							else if(h.faceDown()) t = map.getUnitByPosition(x,y+1);
							if(t != null)
							{
								map.talk(h,t);
							}
						}
					}
				}
				else if(e.getKeyCode() >= KeyEvent.VK_A
					&& e.getKeyCode() <= KeyEvent.VK_Z)
				{
					if(map.isTalking()) map.conversation.press(e);
				}
				else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				{
					if(map.isTalking()) map.conversation.press(e);
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
			tick();
		}
		repaint();
	}

	public void tick()
	{
		map.tick();
		map.focus(""+hero);
		if(map.conversationResult != null)
		{
			map.execute(map.conversationResult);
			Matcher m = followAction.matcher(map.conversationResult);
			if(m.find())
			{
				team.addMember(m.group(1),map);
			}
			map.conversationResult = null;
		}
		clock++;
		repaint();
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		map.draw(g);
	}
}
