package heigvd.gamification;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
/**
 *
 * @author jimmy
 */
public class Background extends WallObject {
    
    public Background() throws IOException {
        this(0,0);
    }
 
    public Background(int x, int y) throws IOException {
        super(ImageIO.read(Background.class.getResource("../../assets/background/background.png")), x, y);
    }
    
    /**
     * Method that draws the image onto the Graphics object passed
     * @param window
     */
    public void draw(Graphics window, int decalageX, int decalageY) {
        window.drawImage(image, x + decalageX - image.getWidth() / 2, y + decalageY, image.getWidth(), image.getHeight(), null);
    }
    
    public void incrementY(int increment) {
        this.y += increment;
 
        // Check to see if the image has gone off stage left
        if (this.y >= image.getHeight()) {
 
            // If it has, line it back up so that its left edge is
            // lined up to the right side of the other background image
            this.y = this.y - image.getHeight() * 2;
        }
    }
 
}
