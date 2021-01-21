package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import input.Mouse;
import input.TextureLoader;

public class Frame extends JFrame
{
	private static final long serialVersionUID = 1L;
	public int width, height;
	public BufferedImage screen;

	public Frame(int w, int h)
	{
		width = w;
		height = h;
		screen = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		new TextureLoader();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(w, h);
		addMouseMotionListener(new Mouse());
		addMouseListener(new Mouse());
		setResizable(false);
		setVisible(true);
	}

	@Override
	public void paint(Graphics fin)
	{
		Graphics2D g = screen.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.GRAY);
		g.fillRect(0, 0, width, height);

		Main.simulation.render(g, width, height);

		fin.drawImage(screen, 0, 0, width, height, null);
	}
}
