package players;

import java.awt.Graphics2D;

import ai.Brain;
import game.GameState;

public class AIPlayer extends Player
{
	public Brain brain;
	public int depth;

	public AIPlayer(boolean player, int depth)
	{
		super(player);

		brain = new Brain();
		this.depth = depth;
	}

	@Override
	public boolean doMove(GameState game)
	{
		game.pieces = brain.getMove(game, depth, player).pieces;
		return true;
	}

	@Override
	public void showInfo(Graphics2D g, GameState game)
	{

	}
}
