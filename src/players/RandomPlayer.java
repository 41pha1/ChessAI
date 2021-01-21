package players;

import java.awt.Graphics2D;
import java.util.ArrayList;

import ai.Brain;
import game.GameState;

public class RandomPlayer extends Player
{
	public Brain brain;

	public RandomPlayer(boolean player)
	{
		super(player);

		brain = new Brain();
	}

	@Override
	public boolean doMove(GameState game)
	{
		ArrayList<GameState> available = brain.getAllGameStates(game, player, true);
		game.pieces = available.get((int) (Math.random() * available.size())).pieces;
		return true;
	}

	@Override
	public void showInfo(Graphics2D g, GameState game)
	{

	}
}
