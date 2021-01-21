package input;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextureLoader
{
	public static BufferedImage[] sprites;

	public TextureLoader()
	{
		BufferedImage sheet = loadPngFromFile(new File("res/sprites.png"));
		sprites = extractSprites(sheet, 6, 2);
	}

	public BufferedImage[] extractSprites(BufferedImage sheet, int w, int h)
	{
		BufferedImage[] sprites = new BufferedImage[w * h];

		int sheetW = sheet.getWidth();
		int sheetH = sheet.getHeight();
		int xScale = sheetW / w;
		int yScale = sheetH / h;

		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				BufferedImage sprite = new BufferedImage(xScale, yScale, BufferedImage.TYPE_INT_ARGB);
				sprite.getGraphics().drawImage(sheet, 0, 0, xScale, yScale, x * xScale, y * yScale, x * xScale + xScale, y * yScale + yScale, null);
				sprites[y * w + x] = sprite;
			}
		}
		return sprites;
	}

	public BufferedImage loadPngFromFile(File file)
	{
		try
		{
			return ImageIO.read(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
