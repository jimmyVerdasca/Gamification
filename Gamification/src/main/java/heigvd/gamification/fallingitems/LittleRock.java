package heigvd.gamification.fallingitems;

import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class LittleRock extends FallingItem {
    
    public LittleRock() throws IOException {
        super("/assets/background/small_rock.png", 3, 0.15);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return (int)(maxSpeed * 0.5);
    }
}
