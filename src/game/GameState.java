package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import input.TextureLoader;
import rules.PieceData;
import util.Tile;

public class GameState
{
	public static final int OUT_OF_BOUND = -2, EMPTY = -1, KING = 0, QUEEN = 1, BISHOP = 2, KNIGHT = 3, ROOK = 4, PAWN = 5, COUNT = 6;
	public static final int w = 8, h = 8;

	public HashMap<Integer, Integer> pieces;
	public boolean[] hasMoved;

	public boolean checkmated;
	public boolean winner;
	public boolean stalemate;
	public boolean calculatedScore;
	public int score;

	public GameState()
	{
		resetPieces();
	}

	public GameState(GameState state)
	{
		pieces = new HashMap<Integer, Integer>();
		pieces.putAll(state.pieces);

		checkmated = state.checkmated;
		winner = state.checkmated;
		stalemate = state.stalemate;
		calculatedScore = state.calculatedScore;
		score = state.score;

		hasMoved = new boolean[state.hasMoved.length];
		for (int i = 0; i < state.hasMoved.length; i++)
		{
			hasMoved[i] = state.hasMoved[i];
		}
	}

	public void addPiece(int x, int y, int ID)
	{
		int key = y * w + x;
		pieces.put(key, ID);
	}

	public void addPiece(int x, int y, int type, boolean color)
	{
		addPiece(x, y, type + (color ? COUNT : 0));
	}

	public void resetPieces()
	{
		hasMoved = new boolean[w * h];
		pieces = new HashMap<Integer, Integer>();
		addPiece(0, 0, ROOK, false);
		addPiece(1, 0, KNIGHT, false);
		addPiece(2, 0, BISHOP, false);
		addPiece(3, 0, QUEEN, false);
		addPiece(4, 0, KING, false);
		addPiece(5, 0, BISHOP, false);
		addPiece(6, 0, KNIGHT, false);
		addPiece(7, 0, ROOK, false);

		for (int x = 0; x < w; x++)
			addPiece(x, 1, PAWN, false);

		addPiece(0, 7, ROOK, true);
		addPiece(1, 7, KNIGHT, true);
		addPiece(2, 7, BISHOP, true);
		addPiece(3, 7, QUEEN, true);
		addPiece(4, 7, KING, true);
		addPiece(5, 7, BISHOP, true);
		addPiece(6, 7, KNIGHT, true);
		addPiece(7, 7, ROOK, true);

		for (int x = 0; x < w; x++)
			addPiece(x, 6, PAWN, true);
	}

	public int isKingDead()
	{
		boolean blackKing = false;
		boolean whiteKing = false;
		for (Integer ID : pieces.values())
		{
			if (ID == KING)
				whiteKing = true;
			else if (ID == KING + COUNT)
			{
				blackKing = true;
			}
		}
		if (!whiteKing)
			return 1;
		if (!blackKing)
			return 2;
		return 0;
	}

	public int isKingDead(ArrayList<GameState> next)
	{
		if (next.size() == 0)
			return 3;

		return isKingDead();
	}

	public void setStalemate()
	{
		stalemate = true;
	}

	public int calculateScore()
	{

		if (stalemate)
			return 0;

		if (checkmated)
		{
			return winner ? -100000 : 100000;
		}

		if (!calculatedScore)
		{
			calculatedScore = true;
			int score = 0;

			Iterator<Entry<Integer, Integer>> it = pieces.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<Integer, Integer> pair = it.next();
				int key = pair.getKey();
				int x = key % GameState.w;
				int y = (key - x) / GameState.w;
				int ID = pair.getValue();

				score += PieceData.isWhite(ID) ? PieceData.getPoints(ID) : -PieceData.getPoints(ID);
			}
			this.score = score;
			return score;
		}
		return score;
	}

	public boolean hasPieceMoved(int x, int y)
	{
		int key = y * w + x;
		return hasMoved[key];
	}

	public int getPiece(int x, int y)
	{
		if (x < 0 || y < 0 || x >= w || y >= h)
		{
			return OUT_OF_BOUND;
		}
		int key = y * w + x;
		if (!pieces.containsKey(key))
			return EMPTY;
		return pieces.get(key);
	}

	public GameState getMove(int x, int y, int x2, int y2)
	{
		GameState copy = new GameState(this);
		copy.move(x, y, x2, y2);
		return copy;
	}

	public void doCastle(boolean turn, boolean kingSide)
	{
		if (turn ? (kingSide ? doCastle(4, 0, 7, 0) : doCastle(4, 0, 0, 0)) : (kingSide ? doCastle(4, 7, 7, 7) : doCastle(4, 7, 0, 7)))
			;
	}

	public boolean doCastle(int kx, int ky, int rx, int ry)
	{
		if (rx > kx)
		{
			move(kx, ky, kx + 2, ky);
			move(rx, ry, rx - 2, ry);
		} else
		{
			move(kx, ky, kx - 2, ky);
			move(rx, ry, rx + 3, ry);
		}
		return false;
	}

	public boolean canDoCastle(boolean turn, boolean kingSide)
	{
		return turn ? (kingSide ? canDoCastle(4, 0, 7, 0, turn) : canDoCastle(4, 0, 0, 0, turn)) : (kingSide ? canDoCastle(4, 7, 7, 7, turn) : canDoCastle(4, 7, 0, 7, turn));
	}

	public boolean canDoCastle(int kx, int ky, int rx, int ry, boolean player)
	{
		if (ky != ry)
			return false;

		if (PieceData.getType(getPiece(kx, ky)) != KING || PieceData.getType(getPiece(rx, ry)) != ROOK)
			return false;

		if (hasPieceMoved(kx, ky) || hasPieceMoved(rx, ry))
			return false;

		if (rx > kx)
		{
			for (int i = kx + 1; i < rx; i++)
			{
				int key = ky * w + i;
				if (pieces.containsKey(key))
					return false;
			}

			if (isInCheck(player))
				return false;

			for (int i = kx + 1; i <= kx + 2; i++)
			{
				if (getMove(kx, ky, i, ky).isInCheck(player))
					return false;
			}
		} else
		{
			for (int i = kx - 1; i > rx; i--)
			{
				int key = ky * w + i;
				if (pieces.containsKey(key))
					return false;
			}

			if (isInCheck(player))
				return false;

			for (int i = kx - 1; i >= kx - 2; i--)
			{
				if (getMove(kx, ky, i, ky).isInCheck(player))
					return false;
			}
		}
		return true;
	}

	public boolean isInCheck(boolean player)
	{
		Iterator<Entry<Integer, Integer>> it = pieces.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Integer, Integer> pair = it.next();
			int key = pair.getKey();
			int x = key % GameState.w;
			int y = (key - x) / GameState.w;
			int ID = pair.getValue();
			if (ID != GameState.EMPTY && PieceData.isWhite(ID) == !player)
			{
				for (Tile tile : PieceData.reachable(new Tile(x, y), this))
				{
					if (getMove(x, y, tile.x, tile.y).isKingDead() > 0)
						return true;
				}
			}
		}
		return false;
	}

	public void move(int x1, int y1, int x2, int y2)
	{
		int key = y1 * w + x1;
		hasMoved[key] = true;
		pieces.put(y2 * w + x2, pieces.get(key));
		pieces.remove(key);
		calculatedScore = false;
	}

	public void setCheckmated(boolean loser)
	{
		checkmated = true;
		this.winner = !loser;
	}

	public void renderBoard(Graphics2D g, int scale)
	{
		Color DARKEST_COLOR = new Color(25, 12, 5);
		Color DARKER_COLOR = new Color(50, 25, 10);
		Color DARK_COLOR = new Color(100, 50, 20);
		Color LIGHT_COLOR = new Color(200, 100, 40);

		g.setColor(DARKER_COLOR);
		g.fillRect(-scale / 2, -scale / 2, scale * w + scale, scale * h + scale);
		g.setColor(DARKEST_COLOR);
		g.fillRect(-scale / 10, -scale / 10, scale * w + (scale / 10) * 2, scale * h + (scale / 10) * 2);

		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				g.setColor((x + y) % 2 == 0 ? DARK_COLOR : LIGHT_COLOR);
				g.fillRect(x * scale, (h - y - 1) * scale, scale, scale);
			}
		}
		g.setFont(new Font("Railway", Font.BOLD, 24));
		String[] letters = new String[] { "A", "B", "C", "D", "E", "F", "G", "H" };
		g.setColor(LIGHT_COLOR);

		int textHeight = g.getFontMetrics().getHeight();

		for (int x = 0; x < w; x++)
		{
			String text = letters[x];
			int textWidth = g.getFontMetrics().stringWidth(text);
			g.drawString(text, x * scale + scale / 2 - textWidth / 2, -scale / 4 + textHeight / 4);
		}
		for (int x = 0; x < w; x++)
		{
			String text = letters[x];
			int textWidth = g.getFontMetrics().stringWidth(text);
			g.drawString(letters[x], x * scale + scale / 2 - textWidth / 2, scale * h + scale / 4 + textHeight / 4);
		}
		for (int y = 0; y < h; y++)
		{
			String text = h - y + "";
			int textWidth = g.getFontMetrics().stringWidth(text);
			g.drawString(text, 0 - scale / 4 - textWidth / 2, y * scale + scale / 2 + textHeight / 4);
		}
		for (int y = 0; y < h; y++)
		{
			String text = h - y + "";
			int textWidth = g.getFontMetrics().stringWidth(text);
			g.drawString(text, scale * w + scale / 4 - textWidth / 2, y * scale + scale / 2 + textHeight / 4);
		}
	}

	public void renderPieces(Graphics2D g, int scale)
	{
		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				if (getPiece(x, y) != EMPTY)
					g.drawImage(TextureLoader.sprites[getPiece(x, y)], x * scale, (h - y - 1) * scale, scale, scale, null);
			}
		}
	}

	@Override
	public String toString()
	{
		String s = "";
		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{
				s += " " + getPiece(x, y);
			}
			s += "\n";
		}
		return s;
	}
}
