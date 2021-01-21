package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseMotionListener, MouseListener
{
	public static int x, y;
	public static boolean left;

	@Override
	public void mouseDragged(MouseEvent e)
	{
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			left = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			left = false;
	}
}
