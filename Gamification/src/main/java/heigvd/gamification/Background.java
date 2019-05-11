package heigvd.gamification;

import java.io.IOException;
/**
 *
 * @author jimmy
 */
public class Background extends WallObject {
    
    /**
     * simple constructor that build the background
     * on the top left of the window
     * 
     * @throws IOException if we can't find the image
     */
    public Background() throws IOException {
        this(0,0);
    }
 
    /**
     * constructor positionning the background
     * 
     * @param x start horizontal position
     * @param y start vertical position
     * @throws IOException if we can't find the image
     */
    public Background(int x, int y) throws IOException {
        super("/assets/background/background_rock.png", x, y);
    }
    
    /**
     * move the background down or line it up to simulate infinite scrolling down
     * @param increment step range of each call.
     */
    public void incrementY(int increment) {
        this.y += increment;
 
        // Check to see if the image has gone off stage left
        if (this.y >= image.getHeight()) {
 
            // If it has, line it back up so that its bot edge is
            // lined up to the top side of the other background image
            this.y = this.y - image.getHeight() * 2;
        }
    }
 
}
