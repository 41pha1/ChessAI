package ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import game.GameState;
import rules.PieceData;
import util.Tile;

public class Brain
{
	public int startDepth;

	public GameState getMove(GameState current, int depth, boolean turn)
	{
		startDepth = depth;
		return getBestMove(current, depth, turn);
	}

	public GameState getBestMove(GameState current, int depth, boolean turn)
	{
		if (depth == 0)
		{
			return current;
		}
		ArrayList<GameState> states = getAllGameStates(current, turn, depth >= startDepth - 2);

		// if (current.isKingDead() > 0)
		// {
		// return current;
		// }

		if (states.size() == 0)
		{
			if (current.isInCheck(turn))
			{
				current.setCheckmated(!turn);
				return current;
			}
			current.setStalemate();
			return current;
		}

		ArrayList<Integer> scores;
		scores = new ArrayList<Integer>();

		GameState bestState = null;

		int bestScore = turn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		for (GameState state : states)
		{
			GameState best = getBestMove(state, depth - 1, !turn);
			int score = best.calculateScore() + (turn ? depth : -depth);
			scores.add(score);

			if (turn ? score > bestScore : score < bestScore)
			{
				bestScore = score;
				bestState = best;
			}
		}

		if (bestState == null)
		{
			return current;
		}
		if (depth == startDepth)
		{
			// System.out.println(bestScore + ", " + current.calculateScore());
			int n = states.size();
			ArrayList<GameState> bestStates = new ArrayList<GameState>();
			for (int i = 0; i < n; i++)
			{
				if (scores.get(i) == bestScore)
				{
					bestStates.add(states.get(i));
				}
			}
			return bestStates.get((int) (Math.random() * bestStates.size()));
		}
		return bestState;
	}

	public ArrayList<GameState> getAllGameStates(GameState state, boolean turn, boolean topLevel)
	{
		ArrayList<GameState> next = new ArrayList<GameState>();

		Iterator<Entry<Integer, Integer>> it = state.pieces.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Integer, Integer> pair = it.next();
			int key = pair.getKey();
			int x = key % GameState.w;
			int y = (key - x) / GameState.w;
			int ID = pair.getValue();
			if (ID != GameState.EMPTY && PieceData.isWhite(ID) == turn)
			{
				for (Tile tile : PieceData.reachable(new Tile(x, y), state))
				{
					GameState nextState = state.getMove(x, y, tile.x, tile.y);
					if (topLevel)
					{
						if (nextState.isInCheck(turn))
							continue;
					}
					next.add(nextState);
					checkForPromotion(next, nextState, tile.x, tile.y, ID, turn);
				}
			}
		}
		addCastleMoves(next, state, turn);
		return next;
	}

	public void checkForPromotion(ArrayList<GameState> states, GameState state, int x, int y, int type, boolean player)
	{
		if (PieceData.getType(type) == GameState.PAWN && (y == GameState.h - 1 || y == 0))
		{
			states.remove(state);
			state.addPiece(x, y, GameState.BISHOP + (player ? 0 : GameState.COUNT));
			states.add(state);
			GameState next = new GameState(state);
			next = new GameState(state);
			next.addPiece(x, y, GameState.ROOK + (player ? 0 : GameState.COUNT));
			states.add(next);
			next = new GameState(state);
			next.addPiece(x, y, GameState.KNIGHT + (player ? 0 : GameState.COUNT));
			states.add(next);
			next = new GameState(state);
			next.addPiece(x, y, GameState.QUEEN + (player ? 0 : GameState.COUNT));
			states.add(next);
		}
	}

	public void addCastleMoves(ArrayList<GameState> states, GameState last, boolean turn)
	{
		if (last.canDoCastle(turn, false))
		{
			GameState next = new GameState(last);
			next.doCastle(turn, false);
			states.add(next);
		}
		if (last.canDoCastle(turn, true))
		{
			GameState next = new GameState(last);
			next.doCastle(turn, true);
			states.add(next);
		}
	}
}
