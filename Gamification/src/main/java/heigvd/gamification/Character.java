package heigvd.gamification;

import java.io.IOException;

/**
 * class representing the modele of the character in game
 *
 * @author jimmy
 */
public class Character extends WallObject {
    
    private final int MAX_X;
    private final int MIN_X;
    private int currentSpeed;
    
    /**
     * constructor
     * 
     * @param x current x position
     * @param y current y position
     * @param maxX maximum horizontal position possible (0 is the minimum)
     * @throws IOException 
     */
    public Character(int x, int y, int maxX) throws IOException {
        super("../../assets/character/astronaut.png", x, y);
        
        this.MAX_X =  maxX - getImageWidth();
        MIN_X = 0;
    }

    /**
     * return the current horizontal position
     * @return  the current horizontal position
     */
    public int getX() {
        return x;
    }

    /**
     * return the current vertical position
     * @return  the current vertical position
     */
    public int getY() {
        return y;
    }

    /**
     * move the character relatively to his speed
     */
    public void move() {
        x += currentSpeed;
        if (x < MIN_X) {
            x = MIN_X;
        } else if (x > MAX_X) {
            x = MAX_X;
        }
    }
    
    /**
     * set the horizontal speed of the character
     * positive speed for right
     * negative speed for left
     * 
     * @param newSpeed 
     */
    public void setSpeed(int newSpeed) {
        currentSpeed = newSpeed;
    }

    /**
     * slow down the character speed
     * care if we change -- or ++ to a -= or += higer than one,
     * there will be issues.
     */
    public void downVelocity() {
        if(currentSpeed > 0) {
            currentSpeed--;
        } else if (currentSpeed < 0) {
            currentSpeed++;
        }
    }

    /**
     * return the horizontal speed of the character
     * 
     * @return the horizontal speed of the character
     */
    int getSpeed() {
        return currentSpeed;
    }
}
