/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heigvd.gamification;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author jimmy
 */
public class ScrollingBackground extends JPanel {
 
    // Two copies of the background image to scroll
    private Background backOne;
    private Background backTwo;
    private Character character;
    private final int characterYDecalage = 100;
    private BufferedImage back;
    private int fullSpeed = 30;
    private int speed = 5;
 
    private float interpolation;
    private int decalage;
    
    public ScrollingBackground() {
        try {
            backOne = new Background();
            backTwo = new Background(0, backOne.getImageHeight());
            character = new Character(312 / 2, getHeight() - characterYDecalage, 312);
        } catch (IOException ex) {
            Logger.getLogger(ScrollingBackground.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(true);
    }
    
    
 
    @Override
    public void update(Graphics window) {
        paint(window);
    }
 
    public void paint(Graphics window) {
        Graphics2D twoD = (Graphics2D)window;
 
        if (back == null)
            back = (BufferedImage)(createImage(getWidth(), getHeight()));
 
        // Create a buffer to draw to
        Graphics buffer = back.createGraphics();
 
        // Put the two copies of the background image onto the buffer
        decalage = (int) (speed * interpolation);
        backOne.draw(buffer, getWidth() / 2,decalage);
        backTwo.draw(buffer, getWidth() / 2,decalage);
        character.draw(buffer, getWidth() / 2 - backOne.getImageWidth() / 2 + character.getImageWidth() / 2 + character.getX(), getHeight() - characterYDecalage);
        
        // Draw the image onto the window
        twoD.drawImage(back, null, 0, 0);
    }
    
    public void downBackground() {
        backOne.incrementY(speed);
        backTwo.incrementY(speed);
    }
    
    public void setInterpolation(float interp)
    {
       interpolation = interp;
    }

    public int getFullSpeed() {
        return fullSpeed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    int getSpeed() {
        return speed;
    }

    Character getCharacter() {
        return character;
    }
}
