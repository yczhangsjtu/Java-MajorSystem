package com;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.TreeMap;
import java.util.Set;

public class QuizLibrary
{
	TreeMap<String,String> numToQuiz = new TreeMap<String,String>();
	public QuizLibrary()
	{
		try
		{
			BufferedReader br = new BufferedReader(
						new FileReader("resource/quizes.txt"));
			String line = br.readLine();
			while(line!=null)
			{
				String [] tokens = line.trim().split(":");
				numToQuiz.put(tokens[0],tokens[1]);
				line = br.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getHint(String num)
	{
		return numToQuiz.get(num);
	}

	public Set<String> getNums()
	{
		return numToQuiz.keySet();
	}
}
