package players;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import game.GameState;
import input.Mouse;
import main.Main;
import rules.PieceData;
import util.Tile;

public class HumanPlayer extends Player
{
	public Tile selected;
	public boolean selecting;
	public int size, scale;

	public HumanPlayer(boolean player, int size, int scale)
	{
		super(player);
		this.size = size;
		this.scale = scale;
	}

	@Override
	public boolean doMove(GameState game)
	{
		if (Mouse.left)
		{
			Mouse.left = false;
			int pickX = (-Main.frame.width / 2 + size / 2 + Mouse.x) / scale;
			int pickY = GameState.h - (-Main.frame.height / 2 + size / 2 + Mouse.y) / scale - 1;

			int ID = game.getPiece(pickX, pickY);
			if (selecting && ID != GameState.OUT_OF_BOUND)
			{
				for (Tile t : getReachable(game))
				{
					if (t.x == pickX && t.y == pickY)
					{
						if (!doCastleAttempt(game, selected.x, selected.y, pickX, pickY))
						{
							game.move(selected.x, selected.y, pickX, pickY);

							Iterator<Entry<Integer, Integer>> it = game.pieces.entrySet().iterator();
							while (it.hasNext())
							{
								Entry<Integer, Integer> pair = it.next();
								int key = pair.getKey();
								int x = key % GameState.w;
								int y = (key - x) / GameState.w;
								int type = pair.getValue();

								if (PieceData.getType(type) == GameState.PAWN && (y == GameState.h - 1 || y == 0))
								{
									game.addPiece(x, y, GameState.QUEEN + (player ? 0 : GameState.COUNT));
								}
							}
						}
						selecting = false;
						return true;
					}
				}
				if (!(ID == GameState.EMPTY || ID == GameState.OUT_OF_BOUND) && PieceData.isWhite(ID) == player)
				{
					selecting = true;
					selected = new Tile(pickX, pickY);
				} else
				{
					selecting = false;
				}
				return false;
			}
			if (!(ID == GameState.EMPTY || ID == GameState.OUT_OF_BOUND) && PieceData.isWhite(ID) == player)
			{
				selecting = true;
				selected = new Tile(pickX, pickY);
			} else
			{
				selecting = false;
			}
		}
		return false;
	}

	public boolean doCastleAttempt(GameState state, int x1, int y1, int x2, int y2)
	{
		if (PieceData.getType(state.getPiece(x1, y1)) != GameState.KING)
			return false;
		if (Math.abs(x1 - x2) != 2)
			return false;
		state.doCastle(player, x2 > x1);
		return true;
	}

	public ArrayList<Tile> getReachable(GameState game)
	{
		ArrayList<Tile> moves = PieceData.reachable(selected, game);

		for (int i = 0; i < moves.size(); i++)
		{
			Tile t = moves.get(i);
			if (game.getMove(selected.x, selected.y, t.x, t.y).isInCheck(player))
			{
				moves.remove(i);
				i--;
			}
		}
		if (PieceData.getType(game.getPiece(selected.x, selected.y)) == GameState.KING)
		{
			if (game.canDoCastle(player, true))
				moves.add(new Tile(selected.x + 2, selected.y));
			if (game.canDoCastle(player, false))
				moves.add(new Tile(selected.x - 2, selected.y));
		}
		return moves;
	}

	@Override
	public void showInfo(Graphics2D g, GameState game)
	{
		if (selecting)
		{
			g.setColor(new Color(0, 0, 100, 150));
			for (Tile t : getReachable(game))
			{
				g.fillRect(t.x * scale, (GameState.h - t.y - 1) * scale, scale, scale);
			}
		}
	}
}
