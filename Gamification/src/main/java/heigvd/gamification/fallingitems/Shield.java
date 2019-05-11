package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class Shield extends FallingItem {

    public Shield() throws IOException {
        super("/assets/background/shield.png", 3, 0.1, false);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return maxSpeed;
    }
    
    @Override
    public boolean giveSield() {
        return true;
    }
    
}
