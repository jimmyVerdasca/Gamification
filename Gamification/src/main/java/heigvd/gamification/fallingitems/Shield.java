package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class Shield extends FallingItem {
    /**
     * Constructor
     * 
     * @throws IOException if we can't read the image File.
     */
    public Shield() throws IOException {
        super("/assets/background/shield.png", "bananaSlap.wav", 3, 0.1, false);
    }

    /**
     * Reaction that modifie the received integer. Used when we collide.
     * 
     * @param maxSpeed current value.
     * @return the entry value modified.
     */
    @Override
    public int setMaxSpeed(int maxSpeed) {
        return maxSpeed;
    }
    
    /**
     * Shield is the only FallingItem that gives a Shield
     * @return true.
     */
    @Override
    public boolean giveSield() {
        return true;
    }
    
}
