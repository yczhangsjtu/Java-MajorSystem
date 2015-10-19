package com;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import com.Memo;

public class Conversation{
	Character from;
	Unit to;
	public enum State {question,answer,result,finished}
	State state = State.question;
	String answer = "";
	String result = null;
	String question = "";
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
			if(state == State.question) state = State.answer;
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
			}
		}
		else if(keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z)
		{
			String modifiers = KeyEvent.getKeyModifiersText(e.getModifiers());
			if(!modifiers.contains("Shift")) keyCode += 32;
			answer += (char) keyCode;
		}
		else if(keyCode == KeyEvent.VK_BACK_SPACE)
		{
			if(answer.length() > 0) answer = answer.substring(0,answer.length()-1);
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
			if(to instanceof Character)
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
			if(to instanceof Character)
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
	}
}
