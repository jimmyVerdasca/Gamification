package heigvd.gamification;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * abstraction of the game object's
 * they all possess an image and a position on the screen
 * 
 * @author jimmy
 */
public abstract class WallObject {
    
    protected BufferedImage image;
    protected int x;
    protected int y;
    protected String path;

    /**
     * constructor
     * 
     * @param imagePath path where is store de image of the object
     * @param x horizontal position of the object
     * @param y vertical position of the object
     */
    public WallObject(String imagePath, int x, int y) throws IOException {
        this.image = ImageIO.read(Background.class.getResource(imagePath));
        this.x = x;
        this.y = y;
        this.path = imagePath;
    }
    
    /**
     * return the height of the image
     * 
     * @return the height of the image
     */
    public int getImageHeight() {
        return image.getHeight();
    }

    /**
     * return the width of the image
     * 
     * @return the width of the image
     */
    public int getImageWidth() {
        return image.getWidth();
    }
    
    /**
     * all the object can be drawn on the screen
     * 
     * @param window where the item will be drawn
     * @param decalageX horizontal decalage to smooth
     *      the display depending on the current speed
     * @param decalageY vertical decalage to smooth
     *      the display depending on the current speed
     */
    public void draw(Graphics window, int decalageX, int decalageY) {
        window.drawImage(image, x + decalageX, y + decalageY, image.getWidth(), image.getHeight(), null);
    }
}
