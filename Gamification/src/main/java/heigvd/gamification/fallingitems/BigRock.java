package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 * This class represent a rock falling from the sky.
 * If the user touch it, the maxspeed of the wall is decreased
 * 
 * @author jimmy
 */
public class BigRock extends FallingItem {

    /**
     * Constructor
     * 
     * @throws IOException if we can't read the image File.
     */
    public BigRock() throws IOException {
        super("/assets/background/big_rock2.png", "bananaSlap.wav", 3, 0.1);
    }

    /**
     * Reaction that modifie the received integer. Used when we collide.
     * 
     * @param maxSpeed current value.
     * @return the entry value modified.
     */
    @Override
    public int setMaxSpeed(int maxSpeed) {
        return (int) (maxSpeed * -0.3);
    }
}
