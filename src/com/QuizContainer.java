package com;

import java.util.TreeMap;
import java.util.Set;
import java.awt.Image;

public class QuizContainer extends UnitContainer{
	TreeMap<String,Quiz> quizes;
	Image quizImage;
	public QuizContainer()
	{
		quizes = new TreeMap<String,Quiz>();
		quizImage = getUnitImage("resource/images/unit/quiz.png");
	}
	public void addQuiz(String id, int x, int y)
	{
		Quiz quiz = new Quiz(id,x,y,quizImage);
		quizes.put(id,quiz);
	}
	public void addQuiz(String id, Quiz quiz)
	{
		quizes.put(id,quiz);
	}
	public Quiz getQuizById(String id)
	{
		return quizes.get(id);
	}
	public void addUnit(String id, Unit unit)
	{
		if(unit instanceof Quiz)
			addQuiz(id,(Quiz)unit);
	}
	public void removeUnit(String id)
	{
		quizes.put(id,null);
	}
	public Unit getUnitById(String id)
	{
		return quizes.get(id);
	}
	public Set<String> getAllUnitsId()
	{
		return quizes.keySet();
	}
}
