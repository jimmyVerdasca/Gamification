package heigvd.gamification;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * class containing the game logic
 * 
 * each object of the game are contained here
 * @author jimmy
 */
public class GameEngine extends JPanel {
 
    private final Object lock = new Object();
    //Game display where we draw objects of the game
    private BufferedImage back;
    //Two copies of the background image to scroll
    private Background backOne;
    private Background backTwo;
    private Character character;
    private final FallingItem[] obstacles;
    private int currentObstacleIndex = 0;
    private Class<? extends FallingItem>[] possiblesObstables = new Class[]{
        Rock.class,
        LittleRock.class
    };
    
    //Current wallspeed
    private int speed = 5;
    
    private final int MAX_SPEED = 30;
    private final int Character_Y_DECALAGE = 100;
    private final int WINDOW_CENTER;
    private final int WALL_HEIGHT;
    private final int WALL_WIDTH;
    private final int MIN_X;
    private final int MAX_NB_OBSTACLES = 3;
 
    /*
    Current interpolation to know where to draw the game objects
    depending of their own current speed and position
    */
    private float interpolation;
    Random rand = new Random();
    
    /**
     * constructor of the game
     * create the wall, and the character
     */
    public GameEngine() {
        super();
        WINDOW_CENTER = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
        WALL_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
        try {
            backOne = new Background();
            backTwo = new Background(0, backOne.getImageHeight());
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        WALL_WIDTH = 576;
        try {
            character = new Character(WALL_WIDTH / 2, WALL_HEIGHT - Character_Y_DECALAGE, WALL_WIDTH); //48 is character image width
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        MIN_X = WINDOW_CENTER - backOne.getImageWidth() / 2;
        
        obstacles = new FallingItem[MAX_NB_OBSTACLES];
        setVisible(true);
    }
    
    
    /**
     * call the paint method
     * 
     * @param window graphics where is drawn the game
     */
    @Override
    public void update(Graphics window) {
        paint(window);
    }
 
    /**
     * paint the game in his current state + the interpolation that smooth the
     * visuel effect
     * 
     * @param window graphics where is drawn the game
     */
    @Override
    public void paint(Graphics window) {
        Graphics2D twoD = (Graphics2D)window;
 
        if (back == null)
            back = (BufferedImage)(createImage(getWidth(), getHeight()));
 
        // Create a buffer to draw to
        Graphics buffer = back.createGraphics();
 
        // draw the game object's considering their position and interpolation
        int interpolationBackgrounds = (int) (speed * interpolation);
        backOne.draw(buffer, MIN_X, interpolationBackgrounds);
        backTwo.draw(buffer, MIN_X, interpolationBackgrounds);
        int interpolationCharacterX = (int)(character.getSpeed() * interpolation);
        character.draw(buffer, MIN_X + interpolationCharacterX, 0);
        for (FallingItem obstacle : obstacles) {
            if (obstacle != null && obstacle.getY() < WALL_HEIGHT) {
                int interpolationObstacle = (int) (obstacle.getSpeed() * interpolation);
                obstacle.draw(buffer, MIN_X, interpolationObstacle);
            }
        }
        
        // Draw the image onto the window
        twoD.drawImage(back, null, 0, 0);
    }
    
    /**
     * increment the position of the backgrounds relatively to the current speed
     */
    public void downBackground() {
        backOne.incrementY(speed);
        backTwo.incrementY(speed);
    }
    
    public void downObstacles() {
        synchronized(lock) {
            for (FallingItem obstacle : obstacles) {
                if (obstacle !=  null) {
                    obstacle.move(speed);
                    obstacle.updateSpeed();
                }
            }
        }
    }
    
    public void addObstacle() {
        int randObstacleIndex = rand.nextInt(possiblesObstables.length);
        FallingItem newObstacle;
        try {
            newObstacle = (FallingItem)possiblesObstables[randObstacleIndex].newInstance();
            synchronized(lock) {
                obstacles[currentObstacleIndex] = newObstacle;
            }
            obstacles[currentObstacleIndex].setX(rand.nextInt(WALL_WIDTH - obstacles[currentObstacleIndex].getImageWidth()));
            currentObstacleIndex = ++currentObstacleIndex % MAX_NB_OBSTACLES;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * set the interpolation to call before a repaint
     * 
     * @param interp the new interpolation
     */
    public void setInterpolation(float interp)
    {
       interpolation = interp;
    }
    
    /**
     * set the speed to a percentage [0 to 1] of the max speed
     * If the value is higher to 1 still calculate a value between 0 to 1
     * 
     * @param percent of the maximum speed
     * @throws IllegalArgumentException if the percent is negative
     */
    public void setSpeed(double percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("effort can't be negative");
        } else if (percent > 1) {
            speed = (int)(MAX_SPEED / percent);
        } else {
            speed = (int)(percent * MAX_SPEED);
        }
    }

    /**
     * return the current speed of the wall
     * 
     * @return the current speed of the wall
     */
    int getSpeed() {
        return speed;
    }

    /**
     * return the character
     * 
     * @return the character
     */
    Character getCharacter() {
        return character;
    }

    /**
     * check if a collision append
     * resolt it if necessary.
     * We chack only the items that are in the vertical
     * range to possibly collide with the character.
     * 
     */
    public void checkCollide() {
        FallingItem obstacle;
        synchronized(lock) {
            for (int i = 0; i < obstacles.length; i++) {
                obstacle = obstacles[i];
                if (obstacle != null && obstacle.getY() + obstacle.getImageHeight() > character.getY() && obstacle.getY() < character.getY() + character.getImageHeight()) {
                    if(isReallyColliding(obstacle)) {
                        obstacles[i] = null;
                        resolveCollide(obstacle);
                    }
                }
            }
        }
    }

    /**
     * check if an obstacle in the character's range, collide
     * For simplification purpose
     * the falling items are considered as rectangles.
     * 
     * @param obstacle
     * @return 
     */
    private boolean isReallyColliding(FallingItem obstacle) {
        if (character.getX() < obstacle.getX() + obstacle.getImageWidth() &&
            character.getX() + character.getImageWidth() > obstacle.getX() &&
            character.getY() < obstacle.getY() + obstacle.getImageHeight() &&
            character.getY() + character.getImageHeight() > obstacle.getY()) {
            return true;
        } else {
            return false;
        }
    }

    private void resolveCollide(FallingItem obstacle) {
        
    }
}