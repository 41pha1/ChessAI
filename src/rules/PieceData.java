package rules;

import java.util.ArrayList;

import game.GameState;
import util.Tile;

public class PieceData
{
	public static int[] xoffs = new int[] { -1, 0, 1, -1, 1, -1, 0, 1 };
	public static int[] yoffs = new int[] { -1, -1, -1, 0, 0, 1, 1, 1 };
	public static int[] bishopMoves = new int[] { 0, 2, 5, 7 };
	public static int[] rookMoves = new int[] { 1, 3, 4, 6 };
	public static int[] knightXs = new int[] { -2, -1, 1, 2, -2, -1, 1, 2 };
	public static int[] knightYs = new int[] { -1, -2, -2, -1, 1, 2, 2, 1 };

	public static ArrayList<Tile> reachable(Tile pos, GameState board)
	{
		int ID = board.getPiece(pos.x, pos.y);
		boolean isWhite = isWhite(ID);

		switch (getType(ID))
		{
			case GameState.KING:
				return kingMoves(pos, board, isWhite);
			case GameState.QUEEN:
				return queenMoves(pos, board, isWhite);
			case GameState.KNIGHT:
				return knightMoves(pos, board, isWhite);
			case GameState.PAWN:
				return pawnMoves(pos, board, isWhite);
			case GameState.ROOK:
				return rookMoves(pos, board, isWhite);
			case GameState.BISHOP:
				return bishopMoves(pos, board, isWhite);
			default:
				return null;
		}
	}

	public static int getPoints(int ID)
	{
		switch (getType(ID))
		{
			case GameState.KING:
				return 1000;
			case GameState.QUEEN:
				return 88;
			case GameState.KNIGHT:
				return 32;
			case GameState.PAWN:
				return 10;
			case GameState.ROOK:
				return 51;
			case GameState.BISHOP:
				return 33;
			default:
				return 0;
		}
	}

	public static boolean consider(int x, int y, GameState board, ArrayList<Tile> tiles, boolean isWhite)
	{
		int ID = board.getPiece(x, y);
		if ((ID == GameState.EMPTY || isWhite(ID) != isWhite) && ID != GameState.OUT_OF_BOUND)
			tiles.add(new Tile(x, y));
		return ID == GameState.EMPTY;
	}

	public static int getType(int ID)
	{
		return ID % GameState.COUNT;
	}

	public static boolean isWhite(int ID)
	{
		return ID < GameState.COUNT;
	}

	public static ArrayList<Tile> kingMoves(Tile pos, GameState board, boolean isWhite)
	{
		ArrayList<Tile> moves = new ArrayList<Tile>();
		for (int i = 0; i < 8; i++)
		{
			int x = pos.x + xoffs[i];
			int y = pos.y + yoffs[i];
			consider(x, y, board, moves, isWhite);
		}
		return moves;
	}

	public static ArrayList<Tile> queenMoves(Tile pos, GameState board, boolean isWhite)
	{
		ArrayList<Tile> moves = new ArrayList<Tile>();
		for (int i = 0; i < 8; i++)
		{
			int x = pos.x + xoffs[i];
			int y = pos.y + yoffs[i];

			while (true)
			{
				if (!consider(x, y, board, moves, isWhite))
					break;

				x += xoffs[i];
				y += yoffs[i];
			}

		}
		return moves;
	}

	public static ArrayList<Tile> bishopMoves(Tile pos, GameState board, boolean isWhite)
	{
		ArrayList<Tile> moves = new ArrayList<Tile>();

		for (int i = 0; i < 4; i++)
		{
			int x = pos.x + xoffs[bishopMoves[i]];
			int y = pos.y + yoffs[bishopMoves[i]];

			while (true)
			{
				if (!consider(x, y, board, moves, isWhite))
					break;

				x += xoffs[bishopMoves[i]];
				y += yoffs[bishopMoves[i]];
			}
		}
		return moves;
	}

	public static ArrayList<Tile> rookMoves(Tile pos, GameState board, boolean isWhite)
	{
		ArrayList<Tile> moves = new ArrayList<Tile>();

		for (int i = 0; i < 4; i++)
		{
			int x = pos.x + xoffs[rookMoves[i]];
			int y = pos.y + yoffs[rookMoves[i]];

			while (true)
			{
				if (!consider(x, y, board, moves, isWhite))
					break;

				x += xoffs[rookMoves[i]];
				y += yoffs[rookMoves[i]];
			}
		}
		return moves;
	}

	public static ArrayList<Tile> pawnMoves(Tile pos, GameState board, boolean isWhite)
	{
		ArrayList<Tile> moves = new ArrayList<Tile>();

		if (isWhite)
		{
			if (pos.y == 1)
			{
				if (board.getPiece(pos.x, pos.y + 1) == GameState.EMPTY)
				{
					int ID = board.getPiece(pos.x, pos.y + 2);
					if (ID == GameState.EMPTY)
						moves.add(new Tile(pos.x, pos.y + 2));
				}
			}
			int ID = board.getPiece(pos.x, pos.y + 1);
			if (ID == GameState.EMPTY)
				moves.add(new Tile(pos.x, pos.y + 1));

			ID = board.getPiece(pos.x - 1, pos.y + 1);
			if (ID != GameState.OUT_OF_BOUND && !isWhite(ID))
				moves.add(new Tile(pos.x - 1, pos.y + 1));

			ID = board.getPiece(pos.x + 1, pos.y + 1);
			if (ID != GameState.OUT_OF_BOUND && !isWhite(ID))
				moves.add(new Tile(pos.x + 1, pos.y + 1));
		} else
		{
			if (pos.y == GameState.h - 2)
			{
				if (board.getPiece(pos.x, pos.y - 1) == GameState.EMPTY)
				{
					int ID = board.getPiece(pos.x, pos.y - 2);
					if (ID == GameState.EMPTY)
						moves.add(new Tile(pos.x, pos.y - 2));
				}
			}
			int ID = board.getPiece(pos.x, pos.y - 1);
			if (ID == GameState.EMPTY)
				moves.add(new Tile(pos.x, pos.y - 1));

			ID = board.getPiece(pos.x - 1, pos.y - 1);
			if (ID != GameState.OUT_OF_BOUND && ID != GameState.EMPTY && isWhite(ID))
				moves.add(new Tile(pos.x - 1, pos.y - 1));

			ID = board.getPiece(pos.x + 1, pos.y - 1);
			if (ID != GameState.OUT_OF_BOUND && ID != GameState.EMPTY && isWhite(ID))
				moves.add(new Tile(pos.x + 1, pos.y - 1));
		}

		return moves;
	}

	public static ArrayList<Tile> knightMoves(Tile pos, GameState board, boolean isWhite)
	{
		ArrayList<Tile> moves = new ArrayList<Tile>();
		for (int i = 0; i < 8; i++)
		{
			int x = pos.x + knightXs[i];
			int y = pos.y + knightYs[i];

			consider(x, y, board, moves, isWhite);
		}
		return moves;
	}
}
