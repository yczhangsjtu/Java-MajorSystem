package com;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import com.Memo;

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
	public Conversation(Character character, Unit unit)
	{
		from = character;
		to = unit;
		if(to instanceof Character)
		{
			question = "My code is " + to.getUnitId() + ", what's my name?";
			state = State.question;
		}
		else if(to instanceof Quiz)
		{
			question = ((Quiz)to).getHint();
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
				if(to instanceof Character)
					state = State.choose;
				else if(to instanceof Quiz)
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
					if(answer.equals(to.getUnitId()))
						result = "That's right.";
					else
						result = "Sorry, guess again.";
					state = State.result;
				}
			}
			else if(state == State.result)
			{
				if(to instanceof Character)
				{
					state = State.finished;
					if(result.equals("That's right."))
						result = "addaction "+to.getUnitId()+" follow_"
							+from.getUnitId();
					else
						result = null;
				}
				else if(to instanceof Quiz)
				{
					state = State.finished;
					if(result.equals("That's right."))
						result = "answer quiz " + to.getUnitId() + " correctly";
					else
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
								result = to.getUnitId();
							else
								result = memo.getWord(to.getUnitId());
						}
						else
							result = "Sorry, you don't have enough money.";
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
			if(state == State.choose && (to instanceof Quiz || to instanceof Character))
			{
				choice--;
				if(choice < 0) choice = 0;
			}
		}
		else if(keyCode == KeyEvent.VK_DOWN)
		{
			if(state == State.choose && (to instanceof Quiz || to instanceof Character))
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
		if(state == State.question || state == State.result)
		{
			X = to.getPixelX(clock)-viewOffsetX;
			Y = to.getPixelY(clock)-viewOffsetY+from.height;
			if(to instanceof Character || to instanceof Quiz)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(X,Y,300,30);
				g.setColor(Color.BLACK);
				g.drawRect(X,Y,300,30);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman",Font.PLAIN,12));
				if(state == State.question) g.drawString(question,X,Y+20);
				else if(state == State.result) g.drawString(result,X,Y+20);
			}
		}
		else if(state == State.answer)
		{
			X = from.getPixelX(clock)-viewOffsetX;
			Y = from.getPixelY(clock)-viewOffsetY+from.height;
			if(to instanceof Character || to instanceof Quiz)
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
			X = from.getPixelX(clock)-viewOffsetX;
			Y = from.getPixelY(clock)-viewOffsetY+from.height;
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
		}
	}
}
