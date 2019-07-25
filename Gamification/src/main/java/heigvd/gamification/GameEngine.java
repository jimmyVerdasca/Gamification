package heigvd.gamification;

import heigvd.gamification.fallingitems.BigRock;
import heigvd.gamification.fallingitems.Bonus;
import heigvd.gamification.fallingitems.FallingItem;
import heigvd.gamification.fallingitems.LittleRock;
import heigvd.gamification.fallingitems.Rock;
import heigvd.gamification.fallingitems.Shield;
import heigvd.gamification.rules.RulesManager;
import heigvd.gamification.rules.RulesName;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import sound.SoundPlayer;

/**
 * class containing the game logic for a solo game
 * 
 * each object of the game are contained here
 * @author jimmy
 */
public class GameEngine {
    
    private final int NB_PLAYERS;

    public List<RulesName> getMedalsWon() {
        return ruleManager.getRulesCleared();
    }

    private class SIDE_Obstacle {
        boolean isLeft;
        Integer indexObstacles;

        private SIDE_Obstacle(boolean isLeft, int obstacleIndex) {
            this.isLeft = isLeft;
            this.indexObstacles = obstacleIndex;
        }

        private int getIndex() {
            return indexObstacles;
        }

        private boolean getIsLeft() {
            return isLeft;
        }
     }
    private final ArrayList<SIDE_Obstacle>[] zigzagLogic;
    private final int GAP_BETWEEN_PLAYERS = 100;
    
    /**
     * First copy of the background to scroll infinitely vertically.
     */
    private Background backOne;
    
    /**
     * Second copy of the background to scroll infinitely vertically.
     */
    private Background backTwo;
    
    /**
     * Solo caracter of the game moving at left and right.
     */
    private Character[] character;
    
    /**
     * Circular buffer containing up to MAX_NB_OBSTACLES FallingItem.
     * A new one destroy an old one.
     */
    private final FallingItem[] obstacles;
    
    /**
     * Current index value in obstacles. Is a modulo MAX_NB_OBSTACLES.
     */
    private int currentObstacleIndex = 0;
    
    /**
     * Array containing the types of obstacle that tthe GameEngine is allowed to
     * instanciate.
     */
    private Class<? extends FallingItem>[] possiblesObstables = new Class[]{
        Rock.class,
        BigRock.class,
        LittleRock.class,
        Bonus.class,
        Shield.class
    };
    
    /**
     * Used to play sounds, should be in client side at the end.
     */
    private SoundPlayer soundPlayer;
    
    /**
     * Background is moved relatively to the speed variable.
     */
    private int speed = 0;
    
    /**
     * Current score (distance covered by the background).
     */
    private long score = 0;
    
    /**
     * Maximum number of FailingItem simultaneously into the background.
     */
    private final int MAX_NB_OBSTACLES = 20;
    
    /**
     * Limit maximum value for maxCurrentSpeed.
     */
    private final int ABSOLUTE_MAX_SPEED = 35;
    
    /**
     * Value that maxCurrentSpeed approche permanetly with step of
     * MAX_CURRENT_SPEED_STEP. With it a speed boost will slowly decrease and a 
     * collision will recover from speed loss slowly.
     */
    private final int MAX_SPEED = 25;
    
    /**
     * speed of approche of maxCurrentSpeed to MAX_SPEED.
     */
    private final int MAX_CURRENT_SPEED_STEP = 2;
    
    /**
     * speed maximum difference between two setSpeed call. as setSpeed is only
     * called at each frame, it's the maximum step of speed between two frames.
     */
    private final int MAX_SPEED_STEP = 3;
    
    /**
     * Time elapse between each approche of maxCurrentSpeed to MAX_SPEED
     */
    private final int MS_BETWEEN_MAX_SPEED_UPDATES = 300;
    
    /**
     * Current limit maximum value for speed.
     */
    private int maxCurrentSpeed;
    
    /**
     * max move speed of the character
     */
    private final int CHARACTER_MAX_SPEED = 20;
    
    /**
     * This variable should be in client side. But before we should replace the
     * charactere coordinate system from 0-WALL_WIDTH to 0-100%.
     */
    protected final int WALL_WIDTH = 576;
 
    /**
     * used to randomize the FailingItem start position.
     */
    private Random rand = new Random();
    
    /**
     * Current state of the shield.
     */
    private boolean isShieldActivated;
    
    /**
     * Inner class allowing to observe the score of the game.
     */
    private ScoreObservable obs;
    private final EndObservable endObs;
    
    
    private Mode currentMode;
    private RulesManager ruleManager;
    private final int WALL_HEIGHT;
    private long timeExcellentStart;
    
    /**
     * constructor of the game
     * create the wall and the character. Plus a new thread to force
     * maxCurrentSpeed to permanently approche MAX_SPEED.
     */
    public GameEngine(Mode firstMode) {
        this(firstMode, 1);
        
    }
    
    
    public GameEngine(Mode firstMode, int nbPlayers) {
        super();
        NB_PLAYERS = nbPlayers;
        currentMode = firstMode;
        soundPlayer = new SoundPlayer();
        maxCurrentSpeed = MAX_SPEED;
        WALL_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
        try {
            backOne = new Background(0,0, firstMode);
            backTwo = new Background(0, backOne.getImageHeight(), firstMode);
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        character = new Character[NB_PLAYERS];
        try {
            for (int i = 0; i < NB_PLAYERS; i++) {
                character[i] = new Character(WALL_WIDTH); //wall width 
                character[i].setY(character[i].getY() - i * GAP_BETWEEN_PLAYERS);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(GameEngine.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        ruleManager = new RulesManager();
        
        obstacles = new FallingItem[MAX_NB_OBSTACLES];
        
        zigzagLogic = new ArrayList[NB_PLAYERS]; 
        for (int i = 0; i < NB_PLAYERS; i++) { 
            zigzagLogic[i] = new ArrayList<SIDE_Obstacle>(); 
        } 
        
        /**
         * update the max current speed each MS_BETWEEN_MAX_SPEED_UPDATES
         * milliseconds
         */
        new java.util.Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    updateMaxCurrentSpeed();
                }
        } ,0,MS_BETWEEN_MAX_SPEED_UPDATES);
        obs = new ScoreObservable();
        endObs = new EndObservable();
    }
    
    /**
     * One step to force maxCurrentSpeed to approche MAX_SPEED by step of size
     * MAX_CURRENT_SPEED_STEP.
     */
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
     * Increment the position of the backgrounds relatively to the current speed
     * and update the score relatively to the increment.
     */
    public void downBackground() {
        backOne.incrementY(speed, currentMode);
        backTwo.incrementY(speed, currentMode);
        incrementScore(speed);
        ruleManager.addObjectif(RulesName.RUN_N_PIXELS, speed);
    }
    
    /**
     * Set the score and notify observers.
     * @param increment score to add.
     */
    public void incrementScore(long increment) {
        score += increment;
        obs.update();
    }
    
    /**
     * One step of moving FallintItem.
     */
    public void downObstacles() {
        for (int i = 0; i < obstacles.length; i++) {
            if (obstacles[i] !=  null) {
                obstacles[i].move(speed);
                obstacles[i].updateSpeed();
                if(obstacles[i].getY() > WALL_HEIGHT) {
                    if(obstacles[i].getIS_NEGATIVE() == true) {
                        ruleManager.addObjectif(RulesName.AVOID_ROCKS);
                    }
                    obstacles[i] = null;
                }
            }
        }
    }
    
    /**
     * add randomly an obstacle at the top of the wall. Taken randomly in the
     * possibleObstacles.
     * And add the new instance in the circular buffer obstacles.
     */
    public void addObstacle() {
        int randObstacleIndex = rand.nextInt(possiblesObstables.length);
        FallingItem newObstacle;
        try {
            newObstacle = (FallingItem)possiblesObstables[randObstacleIndex].newInstance();
            if (obstacles[currentObstacleIndex] != null) {
                ruleManager.addObjectif(RulesName.AVOID_ROCKS);
            }
            obstacles[currentObstacleIndex] = newObstacle;
            obstacles[currentObstacleIndex].setX(rand.nextInt(WALL_WIDTH - obstacles[currentObstacleIndex].getImageWidth()));
            currentObstacleIndex = ++currentObstacleIndex % MAX_NB_OBSTACLES;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * set the speed to a percentage [0 to 1] of the max speed
     * If the value is higher to 1 still calculate a value between 0 to 1 by
     * mapping 1-maxPossible to 1-0.
     * 
     * But the speed can't change more than MAX_SPEED_STEP at each call.
     * 
     * @param percent of the maximum speed
     * @param maxPossible is the maxPercent ever reached.
     * @throws IllegalArgumentException if the percent is negative
     */
    public void setSpeed(double percent, double maxPossible) {
        int newStep;
        if (percent < 0) {
            throw new IllegalArgumentException("effort can't be negative");
        } else if (percent > 1) {
            // avec ralentissement
            //newStep = (int)(maxCurrentSpeed * (1 - (percent - 1) / (maxPossible - 1))) - speed;
            newStep = (int)(maxCurrentSpeed * (1 + (percent - 1) / (maxPossible - 1))) - speed;
            possiblesObstables[3] = BigRock.class;
            possiblesObstables[4] = BigRock.class;
            ruleManager.resetObjectif(RulesName.EXCELLENT_EFFORT);
            timeExcellentStart = 0;
        } else {
            newStep = (int)(percent * maxCurrentSpeed) - speed;
            possiblesObstables[3] = Bonus.class;
            possiblesObstables[4] = Shield.class;
            if (percent >= 0.8 && timeExcellentStart == 0) {
                timeExcellentStart = System.currentTimeMillis();
            } else if (percent >= 0.8) {
                ruleManager.setObjectif(RulesName.EXCELLENT_EFFORT, (int)((System.currentTimeMillis() - timeExcellentStart)/ 1000));
            } else {
                ruleManager.resetObjectif(RulesName.EXCELLENT_EFFORT);
                timeExcellentStart = 0;
            }
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
    public Character[] getCharacter() {
        return character;
    }

    /**
     * check if a collision append 
     * resolve it if necessary.
     */
    public void checkCollide() {
        FallingItem obstacle;
        for (int i = 0; i < obstacles.length; i++) {
            obstacle = obstacles[i];
            for (int j = 0; j < NB_PLAYERS; j++) {
                if (obstacle != null && obstacle.getY() + obstacle.getImageHeight() > character[j].getY() && obstacle.getY() < character[j].getY() + character[j].getImageHeight()) {
                    if(isReallyColliding(obstacle, character[j])) {
                        obstacles[i] = null;
                        resolveCollide(obstacle, j);
                    } else {
                        addZigZag(j, character[j].getX(), i, obstacle.getX());
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
     * @param obstacle FallingItem currently evaluated.
     * @return true if the character collide this obstacle, false otherwise.
     */
    private boolean isReallyColliding(FallingItem obstacle, Character character) {
        if (character.getX() < obstacle.getX() + obstacle.getImageWidth() &&
            character.getX() + character.getImageWidth() > obstacle.getX() &&
            character.getY() < obstacle.getY() + obstacle.getImageHeight() &&
            character.getY() + character.getImageHeight() > obstacle.getY()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Resolve a collision between the character and an obstalce.
     * The obstacle is destroyed.
     * Each type of Falling item has his own behaviour with collision.
     * Rocks decrease the maxCurrentSpeed more or less depending of their type.
     * Shield activate the shield for an amount of time.
     * Bonus increase the maxCurrentSpeed.
     * 
     * Launch a sound effect.
     * 
     * @param obstacle instance that we resolve the collision.
     */
    private void resolveCollide(FallingItem obstacle, int characterIndex) {
        if(!isShieldActivated || isShieldActivated && !obstacle.getIS_NEGATIVE()) {
            if (obstacle.getIS_NEGATIVE()) {
                ruleManager.resetObjectif(RulesName.AVOID_ROCKS);
                zigzagLogic[characterIndex].clear();
                ruleManager.setObjectif(RulesName.ZIGZAG_ROCKS, getMaxZigZag());
            }
            int temp = obstacle.setMaxSpeed(maxCurrentSpeed);
            if(temp > maxCurrentSpeed && maxCurrentSpeed > MAX_SPEED) {
                ruleManager.addObjectif(RulesName.DOUBLE_SPEED_BOOST);
            }
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
            possiblesObstables[4] = Bonus.class;
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    deactivateShield();
                }
            } ,7000);
        }
        
        if (isShieldActivated && obstacle.getIS_NEGATIVE()) {
            ruleManager.addObjectif(RulesName.DESTROY_ROCKS);
            zigzagLogic[characterIndex].clear();
            ruleManager.setObjectif(RulesName.ZIGZAG_ROCKS, getMaxZigZag());
        }
        
        if (obstacle.getIS_NEGATIVE() && obstacle.getSoundPath() != null) {
            try {
                soundPlayer.playSound(obstacle.getSoundPath(), false);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private void addZigZag(int characterIndex, int xCharacter, int obstacleIndex, int xObstacle) {
        if (obstacles[obstacleIndex] != null && obstacles[obstacleIndex].getIS_NEGATIVE()) {
            boolean isLeft = xCharacter < xObstacle;
            if (zigzagLogic[characterIndex].isEmpty()) {
                zigzagLogic[characterIndex].add(new SIDE_Obstacle(isLeft, obstacleIndex));
            } else if (zigzagLogic[characterIndex].get(zigzagLogic[characterIndex].size() - 1).getIndex() != obstacleIndex) {
                if (zigzagLogic[characterIndex].get(zigzagLogic[characterIndex].size() - 1).getIsLeft() == isLeft) {
                    zigzagLogic[characterIndex].clear();
                } else {
                    zigzagLogic[characterIndex].add(new SIDE_Obstacle(isLeft, obstacleIndex));
                }
            }
            ruleManager.setObjectif(RulesName.ZIGZAG_ROCKS, getMaxZigZag());
        }
    }
    
    private int getMaxZigZag() {
        int max = 0;
        for (int i = 0; i < NB_PLAYERS; i++) {
            if(zigzagLogic[i].size() > max) {
                max = zigzagLogic[i].size();
            }
        }
        return max;
    }
    
    /**
     * Indicate to the GameEngine tht the shield is deactivated.
     */
    public void deactivateShield() {
        isShieldActivated = false;
        possiblesObstables[4] = Shield.class;
    }

    /**
     * Return the character max possible speed.
     * 
     * @return the character max possible speed.
     */
    public int getCHARACTER_MAX_SPEED() {
        return CHARACTER_MAX_SPEED;
    }

    /**
     * Return the state of the shield.
     * 
     * @return the state of the shield.
     */
    public boolean isIsShieldActivated() {
        return isShieldActivated;
    }

    /**
     * Return the obstacles.
     * 
     * @return the obstacles.
     */
    public FallingItem[] getObstacles() {
        return obstacles;
    }

    /**
     * Return the first part of the background.
     * 
     * @return the first part of the background.
     */
    public Background getBackOne() {
        return backOne;
    }

    /**
     * Return the second part of the background.
     * 
     * @return the second part of the background.
     */
    public Background getBackTwo() {
        return backTwo;
    }
    
    /**
     * return the current mode we are playing on.
     * 
     * @return the current mode we are playing on.
     */
    public Mode getMode() {
        return currentMode;
    }

    public int getNB_PLAYERS() {
        return NB_PLAYERS;
    }
    
    
    
    /**
     * set a new mode on. The behaviour will change as soon as possible
     * 
     * @param newMode the new mode we will play soon
     */
    public void setMode(Mode newMode) {
        currentMode = newMode;
    }
    
    /**
     * Add an observer for the score.
     * @param o instance of the new observer to add
     */
    public void addScoreObserver(Observer o) {
        obs.addObserver(o);
    }
    
    public void stop() {
        soundPlayer.stop();
        endObs.update();
    }

    public void addEndObserver(Observer o) {
        endObs.addObserver(o);
    }

    public long getScore() {
        return score;
    }

    public RulesManager getRuleManager() {
        return ruleManager;
    }
    
    /**
     * Inner class allowing to observe the score of the game.
     */
    public class ScoreObservable extends Observable {
        
        /**
         * notify observers that the score has been modified
         */
        protected void update() {
            setChanged();
            notifyObservers();
        }
        
        /**
         * Return the score.
         * 
         * @return the score.
         */
        public long getScore() {
            return GameEngine.this.getScore();
        }
    }
    
    /**
     * Inner class allowing to get notified when the game finishes
     */
    public class EndObservable extends Observable {
        
        /**
         * notify observers that the game is finish
         */
        protected void update() {
            setChanged();
            notifyObservers();
        }
    }
}
