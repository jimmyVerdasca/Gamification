/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heigvd.gamification;

import collide.Collider;
import java.io.IOException;

/**
 *
 * @author jimmy
 */
public class LittleRock extends FallingItem {
    
    public LittleRock() throws IOException {
        super("../../assets/background/small_rock.png", 0, new Collider(), 0.15);
    }

    @Override
    public int setMaxSpeed(int maxSpeed) {
        return (int)(maxSpeed * 0.8);
    }
}
