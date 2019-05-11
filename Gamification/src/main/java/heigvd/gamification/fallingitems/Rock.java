package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 * This class represent a rock falling from the sky.
 * If the user touch it, the maxspeed of the wall is decreased
 * 
 * @author jimmy
 */
public class Rock extends FallingItem {
    
    public Rock() throws IOException {
        super("/assets/background/big_rock.png", 3, 0.1);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return maxSpeed / 4;
    }
}
