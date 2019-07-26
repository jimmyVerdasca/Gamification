package menu;

import components.EndPopupDialogBox;
import effortMeasurer.EffortCalculator;
import heigvd.gamification.Background;
import heigvd.gamification.GameEngine;
import heigvd.gamification.fallingitems.FallingItem;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Panel where is drawn the game state (Background, falling items, character)
 * 
 * @author jimmy
 */
public class GamePanel extends JPanel implements Observer {

    private Background backOne;
    private Background backTwo;
    //Game display where we draw objects of the game
    private BufferedImage back;
    private GameEngine gameEngine;
    /*
    Current interpolation to know where to draw the game objects
    depending of their own current speed and position
    */
    private float interpolation;
    
    private final int WINDOW_CENTER;
    private final int WALL_HEIGHT;
    private int MIN_X;
    private EndPopupDialogBox endBox = null;
    private final Menu window;
    
    public GamePanel(JFrame window, GameEngine gameEngine, EffortCalculator effortCalculator) {
        this.window = (Menu)window;
        this.gameEngine = gameEngine;
        backOne = gameEngine.getBackOne();
        backTwo = gameEngine.getBackTwo();
        
        WINDOW_CENTER = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
        WALL_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
        MIN_X = 0;
        gameEngine.addEndObserver(this);
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
 
        if (back == null) {
            back = (BufferedImage)(createImage(getWidth(), getHeight()));
            MIN_X = (getWidth() - backOne.getImageWidth()) / 2;
        }
 
        // Create a buffer to draw to
        Graphics buffer = back.createGraphics();
 
        // draw the game object's considering their position and interpolation
        int interpolationBackgrounds = (int) (gameEngine.getSpeed() * interpolation);
        int interpolationCharacterX;
        backOne.draw(buffer, MIN_X, interpolationBackgrounds);
        backTwo.draw(buffer, MIN_X, interpolationBackgrounds);
        for (heigvd.gamification.Character character : gameEngine.getCharacter()) {
            interpolationCharacterX = (int)(character.getSpeed() * interpolation);
            character.draw(buffer, MIN_X + interpolationCharacterX, 0, gameEngine.isIsShieldActivated());
            character.draw(buffer, MIN_X + interpolationCharacterX, 0, gameEngine.isIsShieldActivated());
        }
        for (FallingItem obstacle : gameEngine.getObstacles()) {
            if (obstacle != null && obstacle.getY() < WALL_HEIGHT) {
                int interpolationObstacle = (int) (interpolationBackgrounds + (obstacle.getSpeed() + 1/2.0 * obstacle.getAcceleration() * Math.pow(interpolation, 2)));
                obstacle.draw(buffer, MIN_X, interpolationObstacle);
            }
        }

        // Draw the image onto the window
        twoD.drawImage(back, null, 0, 0);
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

    @Override
    public void update(java.util.Observable o, Object o1) {
        if (endBox == null) {
            endBox = new EndPopupDialogBox(window, gameEngine.getScore(), gameEngine.getMedalsWon());
        }
    }
}
