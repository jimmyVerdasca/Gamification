package heigvd.gamification;

import collide.Collider;
import java.io.IOException;

/**
 * This class represent a rock falling from the sky.
 * If the user touch it, the maxspeed of the wall is decreased
 * 
 * @author jimmy
 */
public class Rock extends FallingItem {
    
    public Rock() throws IOException {
        super("../../assets/background/big_rock.png", 0, new Collider(), 0.1);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return maxSpeed / 2;
    }
}
