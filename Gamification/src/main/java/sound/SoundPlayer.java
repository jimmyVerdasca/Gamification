package sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Simple sound lector that allow to read a song file. (only one)
 * The reading can be in a loop or not.
 * 
 * @author jimmy
 */
public class SoundPlayer {
    
    /**
     * sound system manager
     */
    private Clip clip;

    /**
     * methode allowing to play a file song contained in the folder "/sounds/"
     * 
     * @param fileName name of the sound file
     * @param loop read infinitely the sound if true
     * @throws UnsupportedAudioFileException if the format is not supported
     * @throws IOException if we don't find the file in the "sound" folder
     * @throws LineUnavailableException if an error occure while reading
     */
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
