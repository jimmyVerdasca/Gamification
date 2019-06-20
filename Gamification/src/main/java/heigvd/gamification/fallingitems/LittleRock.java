package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 * This class represent a rock falling from the sky.
 * If the user touch it, the maxspeed of the wall is decreased
 * 
 * @author jimmy
 */
public class LittleRock extends FallingItem {
    /**
     * Constructor
     * 
     * @throws IOException if we can't read the image File.
     */
    public LittleRock() throws IOException {
        super("/assets/background/small_rock.png", "bananaSlap.wav", 3, 0.15);
    }
    /**
     * Reaction that modifie the received integer. Used when we collide.
     * 
     * @param maxSpeed current value.
     * @return the entry value modified.
     */
    @Override
    public int setMaxSpeed(int maxSpeed) {
        return (int)(maxSpeed * 0.5);
    }
}
