package heigvd.gamification.fallingitems;

import heigvd.gamification.WallObject;
import java.io.IOException;

/**
 * This class represent an obstacle falling from the sky in the game
 * If the player touch it, the max speed will be modified for a small time
 * 
 * @author jimmy
 */
public abstract class FallingItem extends WallObject {
    
    private double acceleration;
    private double speed = 0;
    private final boolean IS_NEGATIVE;
    private final String soundPath;
    
    public FallingItem(String imagePath, String soundPath, int x, double acceleration) throws IOException {
        this(imagePath, soundPath, x, acceleration, true);
    }
    
    public FallingItem(String imagePath, String soundPath, int x, double acceleration, boolean isNegative) throws IOException {
        super(imagePath, x, -150); // all falling item appears at top => -150
        this.acceleration = acceleration;
        this.IS_NEGATIVE = isNegative;
        this.soundPath = soundPath;
    }
    
    public boolean getIS_NEGATIVE() {
        return IS_NEGATIVE;
    }
    
    public void move(int yMovement) {
        y += speed + yMovement;
    }
    
    public void updateSpeed() {
        speed += acceleration;
    }
    
    public abstract int setMaxSpeed(int maxSpeed);
    
    
    public boolean giveSield() {
        return false;
    }

    public double getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getAcceleration() {
        return acceleration;
    }
    
    public String getSoundPath() {
        return soundPath;
    }
    
}
