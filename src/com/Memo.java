package com;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.TreeMap;
import java.util.Set;

class Memo {
	TreeMap<String,String> numToWord = new TreeMap<String,String>();
	TreeMap<String,String> wordToNum = new TreeMap<String,String>();
	public Memo()
	{
		try
		{
			BufferedReader br = new BufferedReader(
						new FileReader("resource/word.txt"));
			String line = br.readLine();
			while(line!=null)
			{
				String [] tokens = line.trim().split(" ");
				numToWord.put(tokens[0],tokens[1]);
				wordToNum.put(tokens[1],tokens[0]);
				line = br.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getWord(String num)
	{
		return numToWord.get(num);
	}

	public String getNum(String word)
	{
		return wordToNum.get(word);
	}

	public Set<String> getWords()
	{
		return wordToNum.keySet();
	}

	public Set<String> getNums()
	{
		return numToWord.keySet();
	}
}
