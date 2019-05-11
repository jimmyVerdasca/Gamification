package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class BigRock extends FallingItem {

    public BigRock() throws IOException {
        super("/assets/background/big_rock2.png", 3, 0.1);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return (int) (maxSpeed * -0.3);
    }
}
