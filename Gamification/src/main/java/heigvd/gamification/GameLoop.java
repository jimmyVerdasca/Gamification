package heigvd.gamification;

import effortMeasurer.EffortCalculator;
import effortMeasurer.IMUEffortCalculator;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 * Game loop with position prediction from current speed
 * It allows items of the game to be displayed smoothly
 */
public final class GameLoop extends JFrame
{
    
    private GameEngine back;
    private EffortCalculator ec;
    private boolean running = false;
    private int fps = 60;
    private int frameCount = 0;
    
    
    //Number of time the monitor refresh in one second
    final double GAME_HERTZ = 30.0;
    final int ONE_NS = 1000000000;

    //Calculate how many ns each frame should take for our target game hertz.
    final double TIME_BETWEEN_UPDATES = ONE_NS / GAME_HERTZ;

    //At the very most we will update the game this many times before a new render.
    //If you're worried about visual hitches more than perfect timing, set this to 1.
    final int MAX_UPDATES_BEFORE_RENDER = 5;

    //If we are able to get as high as this FPS, don't render again.
    final double TARGET_FPS = 60;
    final double TARGET_TIME_BETWEEN_RENDERS = ONE_NS / TARGET_FPS;
    
    /**
     * constructor launching the game view,
     * the game modeles and the effort calculator
     */
    public GameLoop()
    {
        super("Fixed Timestep Game Loop Test");
        
        //launch the effort calculator
        try {
            ec = new IMUEffortCalculator();
            ec.start();
        } catch (IOException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //launch the view
        Container cp = getContentPane();
        BorderLayout bl = new BorderLayout();
        cp.setLayout(bl);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        
        //create game modeles
        back = new GameEngine();
        CharacterController cc = new CharacterController(back);
        
        bl.addLayoutComponent(back, BorderLayout.CENTER);
        setVisible(true);
        cp.add(back, BorderLayout.CENTER);
        
        //launch the game
        runGameLoop();
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
        
        while (running)
        {
            double now = System.nanoTime();
            int updateCount = 0;
            
            //Do as many game updates as we need to, potentially playing catchup.
            while ( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
            {
                boolean shouldAddObstacle = updateTotal % 100 == 0;
                updateGame(shouldAddObstacle);
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
                fps = frameCount;
                frameCount = 0;
                lastSecondTime = thisSecond;
            }
            
            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
            {
                Thread.yield();
                
                //This stops the app from consuming all the CPU. It makes this slightly less accurate, but is worth it.
                //You can remove this block and it will still work (better), your CPU just climbs on certain OSes.
                //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt(); // Here!
                    throw new RuntimeException(e);
                }
                
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
        back.setSpeed(ec.getEffort());
        back.downBackground();
        back.downObstacles();
        back.getCharacter().move();
        back.getCharacter().downVelocity();
        if (addingNextObstacle) {
            back.addObstacle();
        }
        back.checkCollide();
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
        back.setInterpolation(interpolation);
        back.repaint();
    }
    
    /**
     * entry point of the program
     * 
     * @param args currently no needs arguments
     */
    public static void main(String[] args)
    {
        GameLoop gl = new GameLoop();
    }
}