package com;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedList;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import javax.imageio.ImageIO;

import com.Memo;
import com.QuizLibrary;
import com.Character;

public class Canvas extends JPanel implements ActionListener
{
	MapContainer map;
	Team team;
	Timer timer;
	Random rand = new Random();
	static String hero = "0";
	String message = null;
	Pattern followAction = Pattern.compile("addaction (\\d+) follow_"+hero);
	Pattern unfollowAction = Pattern.compile("addaction (\\d+) unfollow");
	Pattern answerQuiz = Pattern.compile("answer quiz (\\d+) correctly");
	Memo memo;
	QuizLibrary quizlib;
	int clock = 0;
	int score = 0;
	AI ai;
	public Canvas()
	{
		map = new MapContainer("map.txt");
		team = new Team();
		ai = new AI(map);
		readSaveFile("resource/save/save.txt");
		team.addMember(map.getCharacterById(hero));
		timer = new Timer(50,this);
		timer.setRepeats(true);
		timer.setActionCommand("tick");
		timer.start();
		rand = new Random();
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
		if(!map.tick()) return;
		ai.tick();
		map.focus(""+hero);
		clock++;
		handleConversation();
		if(clock % 100 == 0)
		{
			addRandomMoney();
			addRandomQuiz();
			addRandomCharacter();
		}
		if(clock % 1000 == 0)
		{
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
				if(map.isEmpty(x,y))
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
				if(map.isEmpty(x,y))
					map.addQuiz(n,x,y);
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
				Character c = map.getCharacterById(m1.group(1));
				if(c != null)
				{
					c.setImageIndex(map.getCharacterById(hero).getImageIndex());
				}
			}
			else if(m2.find())
			{
				if(team.cover(m2.group(1),map))
				{
					map.solveQuiz(m2.group(1));
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
				Character c = map.getCharacterById(m3.group(1));
				if(c != null) c.setImageIndex(map.getCharacterById(hero).getImageIndex());
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
				else if(cmd.equals("gather"))
					ai.setGatherPoints(major);
				else if(cmd.equals("attack"))
					ai.setAttacking(major);
				s = br.readLine();
			}
			map.setUnitMap();
			map.update();
			map.uc.executeAllActions(map);
			for(String id: map.getAllCharactersId())
			{
				Character ch = map.getCharacterById(id);
				int myIndex = map.getCharacterById(hero).getImageIndex();
				if(ch != null && ch.target != null
					&& ch.target.getUnitId().equals(hero) &&
					ch.getImageIndex() == myIndex)
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
			for(int i = 0; i < CharacterContainer.imageNum; i++)
				bw.write("gather:"+i+" "+ai.gatherPoints[i].x+" "+ai.gatherPoints[i].y+"\n");
			for(int i = 0; i < CharacterContainer.imageNum; i++)
				bw.write("attack:"+i+" "+ai.attacking[i]+"\n");
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

	public int distance(Unit u1, Unit u2)
	{
		return distance(u1.getPoint(),u2.getPoint());
	}

	public int distance(Point p1, Point p2)
	{
		return Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
	}

	public int distance(int x, int y, Point p)
	{
		return Math.abs(x-p.x)+Math.abs(y-p.y);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		map.draw(g);
		Character c = map.getCharacterById(hero);
		int myIndex = c.getImageIndex();
		g.setFont(new Font("TimesRoman",Font.PLAIN,12));
		g.setColor(Color.WHITE);
		if(message != null) g.drawString(message,0,20);
		g.drawString("Money: " + map.getCharacterById(hero).getMoney(),0,40);
		g.drawString("Team: " + team.toString(),0,60);
		g.drawString("Score: " + score,0,80);
		g.drawString("Position: " + map.getCharacterById(hero).getX()+","+
			map.getCharacterById(hero).getY(),0,100);
		g.drawString("Enemy: ",0,120);
		if(ai.attacking[myIndex] != -1)
		{
			g.drawImage(map.cc.characterImage[ai.attacking[myIndex]][0][0],
			50,100,30,30,null);
			if(ai.enemyX >= 0 && ai.enemyY >= 0)
				g.drawString(ai.enemyX + "," + ai.enemyY,100,120);
		}
		for(int i = 0; i < CharacterContainer.imageNum; i++)
		{
			g.drawImage(map.cc.characterImage[i][0][0],i*50,map.windowHeight-50,30,30,null);
			g.drawString(""+ai.numbers[i],i*50,map.windowHeight);
		}
	}
}
