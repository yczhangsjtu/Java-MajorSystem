package com;

import java.awt.*;
import javax.swing.*;

import com.Canvas;

public class Main extends JFrame
{
	Canvas canvas;
	public static void main(String[] args)
	{
		new Main("Java Memo");
	}

	public Main(String s)
	{
		super(s);
		canvas = new Canvas();
		canvas.setFocusable(true);
		canvas.setSize(new Dimension(800,600));
		
		add(canvas);
		setLayout(new BorderLayout());
		setLocation(200, 100);
		setSize(800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
}
