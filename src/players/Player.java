package players;

import java.awt.Graphics2D;

import game.GameState;

public abstract class Player
{
	boolean player;

	public Player(boolean player)
	{
		this.player = player;
	}

	public abstract boolean doMove(GameState game);

	public abstract void showInfo(Graphics2D g, GameState game);
}
