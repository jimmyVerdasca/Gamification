package sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author jimmy
 */
public class SoundPlayer {
    
    private Clip clip;

    public SoundPlayer() throws LineUnavailableException {
        
    }
    
    
    
    public synchronized void playSound(final String fileName, boolean loop) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(SoundPlayer.class.getResource("/sounds/" + fileName));
        clip.open(inputStream);
        if (!loop) {
            clip.start();
        } else {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
