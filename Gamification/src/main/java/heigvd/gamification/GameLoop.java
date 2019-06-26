package heigvd.gamification;

import Program.AbstractProgram;
import effortMeasurer.EffortCalculator;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import menu.GamePanel;
import sound.SoundPlayer;

/**
 * Game loop with position prediction from current speed
 * It allows items of the game to be displayed smoothly
 */
public final class GameLoop implements Observer
{
    /**
     * GameEngine to update the state of the game.
     */
    private final GameEngine gameEngine;
    
    /**
     * Detector to receive input from the player.
     */
    private final EffortCalculator effortCalculator;
    
    /**
     * Current state of the loop.
     */
    private boolean running = false;
    
    /**
     * Number of time the monitor refresh in one second.
     */
    final double GAME_HERTZ = 30.0;
    
    /**
     * number of ns in a second.
     */
    final int ONE_NS = 1000000000;

    /**
     * Calculate how many ns each frame should take for our target game hertz.
     */
    final double TIME_BETWEEN_UPDATES = ONE_NS / GAME_HERTZ;

    /**
     * At the very most we will update the game this many times before a new
     * render.
     * If you're worried about visual hitches more than perfect timing, set this
     * to 1.
     */
    final int MAX_UPDATES_BEFORE_RENDER = 1;

    /**
     * If we are able to get as high as this FPS, don't render again.
     */
    final double TARGET_FPS = 60;
    
    /**
     * Time expected between each render.
     */
    final double TARGET_TIME_BETWEEN_RENDERS = ONE_NS / TARGET_FPS;
    
    /**
     * View of the game, where we display the game state.
     */
    private final GamePanel gamePanel;
    
    /**
     * Input class where we receive the end of the game.
     */
    private final AbstractProgram program;
    
    /**
     * Musique player to start when we start the game.
     */
    private SoundPlayer soundPlayer;
    
    /**
     * constructor launching the game view,
     * the game modeles and the effort calculator.
     * 
     * @param gameEngine GameEngine to update the state of the game.
     * @param effortCalculator Detector to receive the effort of the player.
     * @param gamePanel View of the game, where we display the game state.
     * @param program Type of workout to receive the end input.
     * @throws java.io.IOException If we can't load the slider image
     */
    public GameLoop(
            GameEngine gameEngine,
            EffortCalculator effortCalculator,
            GamePanel gamePanel,
            AbstractProgram program) throws IOException
    {
        this.gameEngine = gameEngine;
        this.effortCalculator = effortCalculator;
        this.gamePanel = gamePanel;
        this.program = program;
        program.addObserver(this);
        soundPlayer = new SoundPlayer();
    }
    
    //Starts a new thread and runs the game loop in it.
    public void runGameLoop()
    {
        running = true;
        Thread loop = new Thread()
        {
            @Override
            public void run()
            {
                gameLoop();
            }
        };
        loop.setPriority(10);
        loop.start();
        try {
            soundPlayer.playSound("motivational.wav", true);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        }
        program.startProgram();
    }
    
    //Only run this in another Thread!
    private void gameLoop()
    {
        //Store the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last render time.
        double lastRenderTime;
        //Store current FPS.
        int lastSecondTime = (int)(lastUpdateTime / ONE_NS);
        long updateTotal = 0;
        int nextObstacleCreationFrame = 100;
        boolean shouldAddObstacle;
        Random rand = new Random();
        final int DELAY_BETWEEN_OBSTACLES = 23;
        final int VARIATION_BETWEEN_OBSTACLES = 80;
        
        while (running)
        {
            double now = System.nanoTime();
            int updateCount = 0;
            
            //Do as many game updates as we need to, potentially playing catchup.
            while ( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
            {
                shouldAddObstacle = updateTotal % nextObstacleCreationFrame == 0;
                updateGame(shouldAddObstacle);
                if (shouldAddObstacle) {
                    nextObstacleCreationFrame = rand.nextInt(VARIATION_BETWEEN_OBSTACLES) + DELAY_BETWEEN_OBSTACLES;
                }
                lastUpdateTime += TIME_BETWEEN_UPDATES;
                updateCount++;
                updateTotal++;
            }
            
            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
            {
                lastUpdateTime = now - TIME_BETWEEN_UPDATES;
            }
            
            //Render. To do so, we need to calculate interpolation for a smooth render.
            float interpolation = Math.min(1.0f, (float)((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
            drawGame(interpolation);
            lastRenderTime = now;
            
            //Update the frames we got.
            int thisSecond = (int) (lastUpdateTime / ONE_NS);
            if (thisSecond > lastSecondTime)
            {
                lastSecondTime = thisSecond;
            }
            
            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
            {
                Thread.yield();
                
                //This stops the app from consuming all the CPU. It makes this slightly less accurate, but is worth it.
                //You can remove this block and it will still work (better), your CPU just climbs on certain OSes.
                //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                /*try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }*/
                
                now = System.nanoTime();
            }
        }
    }
    
    /**
     * Increment the state of the game.
     * As if we did +1s to the world of them game.
     */
    private void updateGame(boolean addingNextObstacle)
    {
        gameEngine.setSpeed(effortCalculator.getEffort(), effortCalculator.getMAX_REACHED());
        gameEngine.downBackground();
        gameEngine.downObstacles();
        gameEngine.getCharacter().move();
        gameEngine.getCharacter().downVelocity();
        if (addingNextObstacle) {
            gameEngine.addObstacle();
        }
        gameEngine.checkCollide();
    }
    
    /**
     * display the game with his current state + the interpolation between
     * the current state and the next state
     * 
     * @param interpolation value between 0 and 1 depending
     *      how far we are to the next iteration
     */
    private void drawGame(float interpolation)
    {
        gamePanel.setInterpolation(interpolation);
        gamePanel.repaint();
    }
    
    

    /**
     * Quit properly the GameLoop.
     */
    public void stop() {
        running = false;
    }

    /**
     * Reaction if the program send the input "end of workout".
     * We quit the GameLoop.
     * 
     * @param o instance sending the event
     * @param o1 param relative to the event sent
     */
    @Override
    public void update(Observable o, Object o1) {
        if(!program.getIsRunning()) {
            stop();
        } else {
            gameEngine.setMode(Mode.values()[program.getIntensity().ordinal()]);
        }
    }
}