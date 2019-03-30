/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heigvd.gamification;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author jimmy
 */
public abstract class WallObject {
    
    protected BufferedImage image;
    protected int x;
    protected int y;

    public WallObject(BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }
    
    public int getImageHeight() {
        return image.getHeight();
    }

    int getImageWidth() {
        return image.getWidth();
    }
    
    public abstract void draw(Graphics window, int decalageX, int decalageY);
}
