package com;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import com.Memo;
import com.QuizLibrary;

public class Conversation{
	Character from;
	Unit to;
	public enum State {question,choose,answer,result,finished}
	State state = State.question;
	String answer = "";
	String result = null;
	String question = "";
	int choice = 0;
	boolean enoughMoney = false;
	Memo memo = new Memo();
	QuizLibrary quizlib = new QuizLibrary();
	MapContainer map;
	public Conversation(Character character, Unit unit, MapContainer m)
	{
		from = character;
		to = unit;
		map = m;
		if(to instanceof Character)
		{
			question = "My code is " + to.getUnitId() + ", what's my name?";
			state = State.question;
		}
		else if(to instanceof Quiz)
		{
			question = quizlib.getHint(((Quiz)to).getCode());
			state = State.question;
		}
		else if(to instanceof Oracle)
		{
			question = "What do you want to ask?";
			state = State.question;
		}
		else
			state = State.finished;
	}
	public boolean isFinished()
	{
		return state == State.finished;
	}
	public void press(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_SPACE)
		{
			if(state == State.question)
			{
				if(to instanceof Character || to instanceof Quiz
					|| to instanceof Oracle)
					state = State.choose;
			}
			else if(state == State.answer)
			{
				if(to instanceof Character)
				{
					String correct = memo.getWord(to.getUnitId());
					if(answer.equals(correct))
						result = "That's right.";
					else
						result = "Sorry, guess again.";
					state = State.result;
				}
				else if(to instanceof Quiz)
				{
					if(answer.equals(((Quiz)to).getCode()))
						result = "That's right.";
					else
						result = "Sorry, guess again.";
					state = State.result;
				}
				else if(to instanceof Oracle)
				{
					Character c = map.getCharacterById(answer);
					if(c == null)
						result = "Sorry, no such person.";
					else
					{
						if(choice == 0)
							result = "He is at " + c.getX() + "," + c.getY();
						else
							result = "His name is " + memo.getWord(c.getUnitId());
					}
					state = State.result;
				}
			}
			else if(state == State.result)
			{
				if(to instanceof Character)
				{
					state = State.finished;
					if(result.equals("That's right."))
					{
						if(((Character)to).target == from)
							result = "addaction "+to.getUnitId()+" unfollow";
						else
							result = "addaction "+to.getUnitId()+" follow_"
								+from.getUnitId();
					}
					else
						result = null;
				}
				else if(to instanceof Quiz)
				{
					state = State.finished;
					if(result.equals("That's right."))
						result = "answer quiz " + ((Quiz)to).getCode() + " correctly";
					else
						result = null;
				}
				else
				{
					state = State.finished;
					result = null;
				}
			}
			else if(state == State.choose)
			{
				if(to instanceof Quiz || to instanceof Character)
				{
					if(choice == 0)
						state = State.answer;
					else if(choice == 1)
					{
						state = State.finished;
						result = null;
					}
					else if(choice == 2)
					{
						state = State.result;
						enoughMoney = from.getMoney() > 0;
						if(enoughMoney)
						{
							from.decreaseMoney();
							if(to instanceof Quiz)
								result = ((Quiz)to).getCode();
							else if(to instanceof Character)
								result = memo.getWord(to.getUnitId());
						}
						else
							result = "Sorry, you don't have enough money.";
					}
				}
				else if(to instanceof Oracle)
				{
					if(choice == 0 || choice == 1) state = State.answer;
					else if(choice == 2)
					{
						state = State.finished;
						result = null;
					}
				}
			}
		}
		else if((keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z)
			|| (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9)
			|| keyCode == KeyEvent.VK_MINUS)
		{
			if(state != State.answer) return;
			String modifiers = KeyEvent.getKeyModifiersText(e.getModifiers());
			if((keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z)
			 && !modifiers.contains("Shift")) keyCode += 32;
			answer += (char) keyCode;
		}
		else if(keyCode == KeyEvent.VK_BACK_SPACE)
		{
			if(state != State.answer) return;
			if(answer.length() > 0) answer = answer.substring(0,answer.length()-1);
		}
		else if(keyCode == KeyEvent.VK_UP)
		{
			if(state == State.choose)
			{
				choice--;
				if(choice < 0) choice = 0;
			}
		}
		else if(keyCode == KeyEvent.VK_DOWN)
		{
			if(state == State.choose)
			{
				choice++;
				if(choice > 2) choice = 2;
			}
		}
	}
	public String getResult()
	{
		return result;
	}
	public void draw(Graphics g, int viewOffsetX, int viewOffsetY, int clock)
	{
		if(isFinished()) return;
		int X,Y;
		X = to.getPixelX(clock)-viewOffsetX;
		Y = to.getPixelY(clock)-viewOffsetY+from.height;
		if(X + 300 > map.getWindowWidth()) X -= 250;
		if(Y + 90 > map.getWindowHeight()) Y -= 140;
		if(state == State.question || state == State.result)
		{
			if(to instanceof Character || to instanceof Quiz || to instanceof Oracle)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(X,Y,300,60);
				g.setColor(Color.BLACK);
				g.drawRect(X,Y,300,60);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman",Font.PLAIN,12));
				if(state == State.question) drawString(g,question,X,Y+20);
				else if(state == State.result) drawString(g,result,X,Y+20);
			}
		}
		else if(state == State.answer)
		{
			if(to instanceof Character || to instanceof Quiz || to instanceof Oracle)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(X,Y,300,30);
				g.setColor(Color.BLACK);
				g.drawRect(X,Y,300,30);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman",Font.PLAIN,12));
				g.drawString(answer,X,Y+20);
			}
		}
		else if(state == State.choose)
		{
			if(to instanceof Quiz || to instanceof Character)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(X,Y,300,70);
				g.setColor(Color.BLACK);
				g.drawRect(X,Y,300,70);
				g.setColor(Color.BLUE);
				g.fillRect(X,Y+choice*20+5,300,20);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman",Font.PLAIN,12));
				g.drawString("I know.",X,Y+20);
				g.drawString("I don't know.",X,Y+40);
				g.drawString("I want to know.",X,Y+60);
			}
			else if(to instanceof Oracle)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(X,Y,300,70);
				g.setColor(Color.BLACK);
				g.drawRect(X,Y,300,70);
				g.setColor(Color.BLUE);
				g.fillRect(X,Y+choice*20+5,300,20);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman",Font.PLAIN,12));
				g.drawString("I want to find ",X,Y+20);
				g.drawString("I want to know the name of ",X,Y+40);
				g.drawString("No, thanks.",X,Y+60);
			}
		}
	}

	public int drawString(Graphics g, String str, int x, int y)
	{
		String ss[] = str.split("&");
		for(int i = 0; i < ss.length; i++)
			g.drawString(ss[i],x,y+i*20);
		return ss.length;
	}
}
