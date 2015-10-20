package com;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

import com.Memo;
import com.QuizLibrary;
import com.Character;

public class Canvas extends JPanel implements ActionListener
{
	MapContainer map;
	Team team;
	Timer timer;
	Random rand;
	String hero = "0";
	String directions[] = new String[4];
	String message = null;
	Pattern followAction = Pattern.compile("addaction (\\d+) follow_"+hero);
	Pattern unfollowAction = Pattern.compile("addaction (\\d+) unfollow");
	Pattern answerQuiz = Pattern.compile("answer quiz (\\d+) correctly");
	Memo memo;
	QuizLibrary quizlib;
	int clock = 0;
	int score = 0;
	public Canvas()
	{
		map = new MapContainer("map.txt");
		team = new Team();
		readSaveFile("resource/save/save.txt");
		team.addMember(map.getCharacterById(hero));
		timer = new Timer(50,this);
		timer.setRepeats(true);
		timer.setActionCommand("tick");
		timer.start();
		rand = new Random();
		directions[0] = "left";
		directions[1] = "right";
		directions[2] = "up";
		directions[3] = "down";
		memo = new Memo();
		quizlib = new QuizLibrary();
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
				else if(e.getKeyCode() == KeyEvent.VK_S &&
					KeyEvent.getKeyModifiersText(e.getModifiers()).contains("Ctrl"))
					saveToFile("save.txt");
				else if((e.getKeyCode() >= KeyEvent.VK_A
					&& e.getKeyCode() <= KeyEvent.VK_Z) ||
					(e.getKeyCode() >= KeyEvent.VK_0
					&& e.getKeyCode() <= KeyEvent.VK_9) ||
					e.getKeyCode() == KeyEvent.VK_MINUS)
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
		clock++;
		handleConversation();
		if(clock % 100 == 0)
		{
			addRandomCharacter();
			addRandomMoney();
		}
		if(clock % 100 == 0)
		{
			addRandomQuiz();
		}
		repaint();
	}

	public void addRandomCharacter()
	{
		int x = rand.nextInt(100);
		int y = rand.nextInt(100);
		for(String w : memo.getNums())
		{
			if(map.getCharacterById(w) == null)
			{
				if(map.isAvailable(x,y))
					map.addCharacter(w,x,y);
				break;
			}
		}
	}

	public void addRandomMoney()
	{
		map.addRandomMoney();
	}

	public void addRandomQuiz()
	{
		int x = rand.nextInt(100);
		int y = rand.nextInt(100);
		for(String n : quizlib.getNums())
		{
			if(map.getQuizById(n) == null)
			{
				if(map.isAvailable(x,y))
					map.addQuiz(n,quizlib.getHint(n),x,y);
				break;
			}
		}
	}

	public void handleConversation()
	{
		if(map.conversationResult != null)
		{
			Matcher m1 = followAction.matcher(map.conversationResult);
			Matcher m2 = answerQuiz.matcher(map.conversationResult);
			Matcher m3 = unfollowAction.matcher(map.conversationResult);
			if(m1.find())
			{
				map.execute(map.conversationResult);
				team.addMember(m1.group(1),map);
			}
			else if(m2.find())
			{
				if(team.cover(m2.group(1),map))
				{
					map.removeQuiz(m2.group(1));
					score+=100;
				}
				else
				{
					message = "What a pity, you don't have enough team members.";
				}
			}
			else if(m3.find())
			{
				map.execute(map.conversationResult);
				team.removeMember(m3.group(1),map);
			}
			map.conversationResult = null;
		}
	}

	public void readSaveFile(String filename)
	{
		try
		{
			map.clear();
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String s = br.readLine();
			while(s != null)
			{
				String []ss = s.split(":");
				if(ss.length < 2)
				{
					s = br.readLine();
					continue;
				}
				String cmd = ss[0];
				String major = ss[1];
				if(cmd.equals("unit"))
					map.uc.execute(major,map.cc,map.jc,map.qc,map.tc,map.oc,map);
				else if(cmd.equals("hero"))
					hero = major;
				else if(cmd.equals("score"))
					score = Integer.parseInt(major);
				else if(cmd.equals("instruction"))
					map.addInstruction(major);
				else if(cmd.equals("clock"))
					clock = Integer.parseInt(major);
				s = br.readLine();
			}
			map.setUnitMap();
			map.update();
			map.uc.executeAllActions(map);
			for(String id: map.getAllCharactersId())
			{
				Character ch = map.getCharacterById(id);
				if(ch != null && ch.target != null
					&& ch.target.getUnitId().equals(hero))
					team.addMember(ch);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void saveToFile(String filename)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(
					new FileWriter("resource/save/"+filename));
			bw.write("hero:"+hero+"\n");
			bw.write("score:"+score+"\n");
			bw.write("clock:"+clock+"\n");
			for(String id: map.getAllUnitsId())
			{
				Unit unit = map.getUnitById(id);
				if(unit != null) bw.write(unit.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(bw != null){
					bw.close();
				}
				message = "Successfully saved to " + filename;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		map.draw(g);
		g.setFont(new Font("TimesRoman",Font.PLAIN,12));
		g.setColor(Color.WHITE);
		if(message != null) g.drawString(message,0,20);
		g.drawString("Money: " + map.getCharacterById(hero).getMoney(),0,40);
		g.drawString("Team: " + team.toString(),0,60);
		g.drawString("Score: " + score,0,80);
		g.drawString("Position: " + map.getCharacterById(hero).getX()+","+
			map.getCharacterById(hero).getY(),0,100);
	}
}
