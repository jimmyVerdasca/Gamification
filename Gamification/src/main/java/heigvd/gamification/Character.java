package heigvd.gamification;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author jimmy
 */
public class Character extends WallObject {
    
    private final int maxX;
    
    public Character(int x, int y, int maxX) throws IOException {
        super(ImageIO.read(Background.class.getResource("../../assets/character/astronaut.png")), x, y);
        
        this.maxX =  maxX - getImageWidth();
    }

    @Override
    public void draw(Graphics window, int decalageX, int decalageY) {
        window.drawImage(image, x + decalageX - image.getWidth() / 2, y + decalageY, image.getWidth(), image.getHeight(), null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    void moveLeft() {
        x -= 5;
        if (x < 0) {
            x = 0;
        }
    }

    void moveRight() {
        x += 5;
        if (x > maxX) {
            x = maxX;
        }
    }
}
