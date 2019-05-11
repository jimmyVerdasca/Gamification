package heigvd.gamification;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * class representing the modele of the character in game
 *
 * @author jimmy
 */
public class Character extends WallObject {
    
    private final int MAX_X;
    private final int MIN_X;
    private int currentSpeed;
    private final BufferedImage imageShield;
    private final int SHIELD_GAP;
    
    /**
     * constructor
     * 
     * @param x current x position
     * @param y current y position
     * @param maxX maximum horizontal position possible (0 is the minimum)
     * @throws IOException 
     */
    public Character(int x, int y, int maxX) throws IOException {
        super("/assets/character/astronaut.png", x, y);
        imageShield = ImageIO.read(Background.class.getResource("/assets/character/shield.png"));
        SHIELD_GAP = (imageShield.getWidth() - image.getWidth()) / 2;
        MAX_X =  maxX - getImageWidth();
        MIN_X = 0;
        
    }

    /**
     * return the current horizontal position
     * @return  the current horizontal position
     */
    public int getX() {
        return x;
    }
    
    public void draw(Graphics window, int decalageX, int decalageY, boolean withShield) {
        super.draw(window, decalageX, decalageY);
        if (withShield) {
            window.drawImage(imageShield, x + decalageX  - SHIELD_GAP, y + decalageY + (image.getHeight() / 2) - (imageShield.getHeight() / 2), imageShield.getWidth(), imageShield.getHeight(), null);
        }
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
        if (x + currentSpeed < MIN_X) {
            x = MIN_X;
            currentSpeed = 0;
        } else if (x + currentSpeed > MAX_X) {
            x = MAX_X;
            currentSpeed = 0;
        } else {
            x = x + currentSpeed;
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
