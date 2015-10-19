package com;

import java.util.TreeMap;
import java.awt.Image;

public class QuizContainer extends UnitContainer{
	TreeMap<String,Quiz> quizes;
	Image quizImage;
	public QuizContainer()
	{
		quizes = new TreeMap<String,Quiz>();
		quizImage = getUnitImage("resource/images/unit/quiz.png");
	}
	public void addQuiz(String id, String hint, int x, int y)
	{
		Quiz quiz = new Quiz(id,hint,x,y,quizImage);
		quizes.put(id,quiz);
		super.addUnit(id,quiz);
	}
}
