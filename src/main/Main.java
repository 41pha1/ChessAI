package main;

import game.Simulation;

public class Main
{
	public static Frame frame;
	public static Simulation simulation;
	public static int FPS = 60;

	public static void main(String[] args)
	{
		simulation = new Simulation();
		frame = new Frame(1000, 1000);

		long lf = System.nanoTime();

		while (true)
		{
			if (System.nanoTime() - lf > 1000000000 / FPS)
			{
				lf = System.nanoTime();

				frame.repaint();
				simulation.update();
			}
		}
	}
}
