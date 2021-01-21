package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import ai.Brain;
import main.Main;
import players.AIPlayer;
import players.HumanPlayer;
import players.Player;
import sound.SoundPlayer;

public class Simulation
{
	public static final boolean WHITE = true, BLACK = false;
	public static final int NORMAL = 3, HARD = 4, EXTREME = 5;
	public GameState game;
	public Brain brain;
	public SoundPlayer player;

	public boolean turn = WHITE;

	public Player white;
	public Player black;

	public int size = 800;
	public int scale;

	public String GameResult;
	public boolean GameEnded;

	public Simulation()
	{
		player = new SoundPlayer();
		game = new GameState();
		brain = new Brain();
		scale = Math.min(size / GameState.w, size / GameState.h);

		int recursionDepth = getDifficulty();
		boolean color = getColor();

		white = color ? new HumanPlayer(WHITE, size, scale) : new AIPlayer(WHITE, recursionDepth);
		black = !color ? new HumanPlayer(BLACK, size, scale) : new AIPlayer(BLACK, recursionDepth);

		// white = new AIPlayer(WHITE, HARD);
		// black = new AIPlayer(BLACK, EXTREME);
	}

	public boolean getColor()
	{
		Object[] options =
		{ "Weiß", "Schwarz" };
		return 0 == JOptionPane.showOptionDialog(Main.frame, "Wähle deine Farbe aus", "Eingabe", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
	}

	public int getDifficulty()
	{
		Object[] options =
		{ "Normal", "Schwer", "Extrem" };
		return 3 + JOptionPane.showOptionDialog(Main.frame, "Wähle die Schwierigkeit aus", "Eingabe", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
	}

	public void update()
	{
		if (turn == WHITE)
		{
			turn = !white.doMove(game);

			if (turn == BLACK)
			{
				Main.frame.repaint();
				player.playMoveSound();
			}
		} else
		{
			turn = black.doMove(game);
			if (turn == WHITE)
			{
				Main.frame.repaint();
				player.playMoveSound();
			}
		}
		checkGameEnd();
	}

	public void checkGameEnd()
	{
		boolean stalemate = true;
		for (GameState state : brain.getAllGameStates(game, turn, false))
		{
			if (!state.isInCheck(turn))
				stalemate = false;
		}
		boolean check = game.isInCheck(turn);
		boolean checkmate = stalemate && check;

		if (game.isKingDead() > 0 || stalemate)
		{
			if (checkmate)
				GameResult = (turn ? "SCHACHMATT W" : "SCHACHMATT S");
			else
				GameResult = "PATT";

			GameEnded = true;

			System.out.println(GameResult);

			Main.frame.repaint();

			try
			{
				Thread.sleep(15000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			GameEnded = false;
			game.resetPieces();
			turn = WHITE;
		}
	}

	public void render(Graphics2D g, int width, int height)
	{
		g.translate(width / 2 - size / 2, height / 2 - size / 2);
		game.renderBoard(g, scale);
		white.showInfo(g, game);
		black.showInfo(g, game);
		game.renderPieces(g, scale);

		if (GameEnded)
		{
			g.translate(-width / 2 + size / 2, -height / 2 + size / 2);
			g.setColor(new Color(0, 0, 0, 200));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Impact", Font.PLAIN, 60));

			g.drawString(GameResult, width / 2 - g.getFontMetrics().stringWidth(GameResult) / 2, height / 2);
		}
	}
}
