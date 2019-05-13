package heigvd.gamification;

import heigvd.gamification.fallingitems.BigRock;
import heigvd.gamification.fallingitems.Bonus;
import heigvd.gamification.fallingitems.FallingItem;
import heigvd.gamification.fallingitems.LittleRock;
import heigvd.gamification.fallingitems.Rock;
import heigvd.gamification.fallingitems.Shield;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class containing the game logic
 * 
 * each object of the game are contained here
 * @author jimmy
 */
public class GameEngine {
    
    //Two copies of the background image to scroll
    private Background backOne;
    private Background backTwo;
    private Character character;
    private final FallingItem[] obstacles;
    private int currentObstacleIndex = 0;
    private Class<? extends FallingItem>[] possiblesObstables = new Class[]{
        Rock.class,
        BigRock.class,
        LittleRock.class,
        Bonus.class,
        Shield.class
    };
    
    //Current wallspeed
    private int speed = 0;
    
    private final int MAX_NB_OBSTACLES = 20;
    private final int ABSOLUTE_MAX_SPEED = 50;
    private final int MAX_SPEED = 30;
    private final int MAX_CURRENT_SPEED_STEP = 2;
    private final int MAX_SPEED_STEP = 2;
    private final int MS_BETWEEN_MAX_SPEED_UPDATES = 300;
    private int maxCurrentSpeed;
    
    private final int CHARACTER_Y_DECALAGE = 100;
    private final int CHARACTER_MAX_SPEED = 20;
    private final int WALL_WIDTH = 576;
 
    private Random rand = new Random();
    private boolean isShieldActivated;
    
    /**
     * constructor of the game
     * create the wall, and the character
     */
    public GameEngine() {
        super();
        maxCurrentSpeed = MAX_SPEED;
        try {
            backOne = new Background();
            backTwo = new Background(0, backOne.getImageHeight());
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            character = new Character(WALL_WIDTH); //wall width 
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        obstacles = new FallingItem[MAX_NB_OBSTACLES];
        
        // update the max current speed each 50 milliseconds
        new java.util.Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    updateMaxCurrentSpeed();
                }
        } ,0,MS_BETWEEN_MAX_SPEED_UPDATES);
    }
    
    public void updateMaxCurrentSpeed() {
        if (MAX_CURRENT_SPEED_STEP > Math.abs(MAX_SPEED - maxCurrentSpeed)) {
            maxCurrentSpeed = MAX_SPEED;
        } else if (maxCurrentSpeed < MAX_SPEED) {
            maxCurrentSpeed += MAX_CURRENT_SPEED_STEP;
        } else if (maxCurrentSpeed > MAX_SPEED) {
            maxCurrentSpeed -= MAX_CURRENT_SPEED_STEP;
        }
    }
    
    /**
     * increment the position of the backgrounds relatively to the current speed
     */
    public void downBackground() {
        backOne.incrementY(speed);
        backTwo.incrementY(speed);
    }
    
    public void downObstacles() {
        for (FallingItem obstacle : obstacles) {
            if (obstacle !=  null) {
                obstacle.move(speed);
                obstacle.updateSpeed();
            }
        }
    }
    
    public void addObstacle() {
        int randObstacleIndex = rand.nextInt(possiblesObstables.length);
        FallingItem newObstacle;
        try {
            newObstacle = (FallingItem)possiblesObstables[randObstacleIndex].newInstance();
            obstacles[currentObstacleIndex] = newObstacle;
            obstacles[currentObstacleIndex].setX(rand.nextInt(WALL_WIDTH - obstacles[currentObstacleIndex].getImageWidth()));
            currentObstacleIndex = ++currentObstacleIndex % MAX_NB_OBSTACLES;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * set the speed to a percentage [0 to 1] of the max speed
     * If the value is higher to 1 still calculate a value between 0 to 1
     * 
     * @param percent of the maximum speed
     * @param maxPossible
     * @throws IllegalArgumentException if the percent is negative
     */
    public void setSpeed(double percent, double maxPossible) {
        int newStep;
        if (percent < 0) {
            throw new IllegalArgumentException("effort can't be negative");
        } else if (percent > 1) {
            newStep = (int)(maxCurrentSpeed * (1 - (percent - 1) / (maxPossible - 1))) - speed;
        } else {
            newStep = (int)(percent * maxCurrentSpeed) - speed;
        }
        
        if (newStep > 0) {
            speed += Math.min(newStep, MAX_SPEED_STEP);
        } else {
            speed += Math.max(newStep, -MAX_SPEED_STEP);
        }
    }

    /**
     * return the current speed of the wall
     * 
     * @return the current speed of the wall
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * return the character
     * 
     * @return the character
     */
    public Character getCharacter() {
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
        if(!isShieldActivated || isShieldActivated && !obstacle.getIS_NEGATIVE()) {
            int temp = obstacle.setMaxSpeed(maxCurrentSpeed);
            if (ABSOLUTE_MAX_SPEED < temp) {
                maxCurrentSpeed = ABSOLUTE_MAX_SPEED;
            } else if (-ABSOLUTE_MAX_SPEED > temp) {
                maxCurrentSpeed = -ABSOLUTE_MAX_SPEED;
            } else {
                maxCurrentSpeed = temp;
            }
        }
        if (obstacle.giveSield()) {
            isShieldActivated = true;
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    deactivateShield();
                }
            } ,7000);
        }
    }
    
    public void deactivateShield() {
        isShieldActivated = false;
    }

    public int getCHARACTER_MAX_SPEED() {
        return CHARACTER_MAX_SPEED;
    }

    public boolean isIsShieldActivated() {
        return isShieldActivated;
    }

    public FallingItem[] getObstacles() {
        return obstacles;
    }

    public Background getBackOne() {
        return backOne;
    }

    public Background getBackTwo() {
        return backTwo;
    }
}
