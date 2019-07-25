package heigvd.gamification;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 * Class that modelize an image of the background.
 * 
 * @author jimmy
 */
public class Background extends WallObject {
    
    private BufferedImage[] imageMode = new BufferedImage[] {
        ImageIO.read(Background.class.getResource("/assets/background/background_rock.png")),
        ImageIO.read(Background.class.getResource("/assets/background/background_grass.png")),
        ImageIO.read(Background.class.getResource("/assets/background/background_see.png")),
        ImageIO.read(Background.class.getResource("/assets/background/background.png"))
    };
    
    /**
     * simple constructor that build the background
     * on the top left of the window
     * 
     * @throws IOException if we can't find the image
     */
    public Background() throws IOException {
        this(0,0, Mode.WALL);
    }
 
    /**
     * constructor positionning the background
     * 
     * @param x start horizontal position
     * @param y start vertical position
     * @param mode wich type of background to draw
     * @throws IOException if we can't find the image
     */
    public Background(int x, int y, Mode mode) throws IOException {
        super(x, y);
        super.image = imageMode[mode.ordinal()];
    }
    
    /**
     * move the background down or line it up to simulate infinite scrolling down
     * @param increment step range of each call.
     * @param currentMode wich type of background to draw
     */
    public void incrementY(int increment, Mode currentMode) {
        this.y += increment;
 
        // Check to see if the image has gone off stage left
        if (this.y >= image.getHeight()) {
 
            // If it has, line it back up so that its bot edge is
            // lined up to the top side of the other background image
            setMode(currentMode);
            this.y = this.y - image.getHeight() * 2;
        }
    }

    private void setMode(Mode newMode) {
        super.image = imageMode[newMode.ordinal()];
    }
}
