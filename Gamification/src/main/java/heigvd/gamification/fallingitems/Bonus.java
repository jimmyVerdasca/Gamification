package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class Bonus extends FallingItem {

    public Bonus() throws IOException {
        super("../../assets/background/powerup.png", 0, 0.15, false);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return (int) (maxSpeed * 1.5);
    }
}
