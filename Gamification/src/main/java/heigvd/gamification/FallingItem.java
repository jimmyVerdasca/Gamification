package heigvd.gamification;

import collide.Collider;
import java.io.IOException;
import java.util.Random;

/**
 * This class represent an obstacle falling from the sky in the game
 * If the player touch it, the max speed will be modified for a small time
 * 
 * @author jimmy
 */
public abstract class FallingItem extends WallObject {
    
    private Collider collider;
    private double acceleration;
    private double speed = 0;
    
    public FallingItem(String imagePath, int x, Collider collider, double accelearation) throws IOException {
        super(imagePath, x, -150); // all falling item appears at top => -50
        this.collider = collider;
        this.acceleration = accelearation;
    }
    
    public void move(int yMovement) {
        y += speed + yMovement;
    }
    
    public void updateSpeed() {
        speed += acceleration;
    }
    
    public abstract int setMaxSpeed(int maxSpeed);

    double getSpeed() {
        return speed;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    
    
}
