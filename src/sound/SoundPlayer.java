package sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer
{
	public Clip moveSounds;

	public SoundPlayer()
	{
		AudioInputStream audioIn;
		try
		{
			audioIn = AudioSystem.getAudioInputStream(new File("res/move.wav"));
			moveSounds = AudioSystem.getClip();

			moveSounds.open(audioIn);

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	public void playMoveSound()
	{
		int length = moveSounds.getFrameLength();
		int rPos = (int) (Math.random() * 13) * (length / 13);
		moveSounds.setFramePosition(rPos);
		moveSounds.start();
		while (true)
			if (moveSounds.getFramePosition() > rPos + length / 26)
			{
				moveSounds.stop();
				break;
			}
	}
}
