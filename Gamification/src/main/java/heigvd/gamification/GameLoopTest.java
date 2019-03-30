package heigvd.gamification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLoopTest extends JFrame implements ActionListener
{
   
   private ScrollingBackground back;
   private boolean running = false;
   private boolean paused = false;
   private int fps = 60;
   private int frameCount = 0;
   
   public GameLoopTest()
   {
      super("Fixed Timestep Game Loop Test");
      Container cp = getContentPane();
      BorderLayout bl = new BorderLayout();
      cp.setLayout(bl);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
      this.setUndecorated(true);
 
      back = new ScrollingBackground();
      CharacterController cc = new CharacterController(back);
       try {
           EffortCalculator ec = new EffortCalculator(back);
       } catch (AWTException ex) {
           Logger.getLogger(GameLoopTest.class.getName()).log(Level.SEVERE, null, ex);
       }
      
      ((Component)back).setFocusable(true);
      bl.addLayoutComponent(back, BorderLayout.CENTER);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
    
      cp.add(back, BorderLayout.CENTER);

      running = true;
      runGameLoop();
   }
   
   public static void main(String[] args)
   {
      GameLoopTest glt = new GameLoopTest();
      glt.setVisible(true);
   }
   
   
   public void actionPerformed(ActionEvent e)
   {
      Object s = e.getSource();
      if (1 == 2)
      {
         running = !running;
         if (running)
         {
            runGameLoop();
         }
         else
         {
             
         }
      }
      else if (1 == 2)
      {
        paused = !paused;
         if (paused)
         {
             
         }
         else
         {
             
         }
      }
      else if (1 == 2)
      {
         System.exit(0);
      }
   }
   
   //Starts a new thread and runs the game loop in it.
   public void runGameLoop()
   {
      Thread loop = new Thread()
      {
         public void run()
         {
            gameLoop();
         }
      };
      loop.start();
   }
   
   //Only run this in another Thread!
   private void gameLoop()
   {
      //This value would probably be stored elsewhere.
      final double GAME_HERTZ = 30.0;
      //Calculate how many ns each frame should take for our target game hertz.
      final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
      //At the very most we will update the game this many times before a new render.
      //If you're worried about visual hitches more than perfect timing, set this to 1.
      final int MAX_UPDATES_BEFORE_RENDER = 5;
      //We will need the last update time.
      double lastUpdateTime = System.nanoTime();
      //Store the last time we rendered.
      double lastRenderTime = System.nanoTime();
      
      //If we are able to get as high as this FPS, don't render again.
      final double TARGET_FPS = 60;
      final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
      
      //Simple way of finding FPS.
      int lastSecondTime = (int) (lastUpdateTime / 1000000000);
      
      while (running)
      {
         double now = System.nanoTime();
         int updateCount = 0;
         
         if (!paused)
         {
             //Do as many game updates as we need to, potentially playing catchup.
            while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
            {
               updateGame();
               lastUpdateTime += TIME_BETWEEN_UPDATES;
               updateCount++;
            }
   
            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
            {
               lastUpdateTime = now - TIME_BETWEEN_UPDATES;
            }
         
            //Render. To do so, we need to calculate interpolation for a smooth render.
            float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
            drawGame(interpolation);
            lastRenderTime = now;
         
            //Update the frames we got.
            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime)
            {
               //System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
               fps = frameCount;
               frameCount = 0;
               lastSecondTime = thisSecond;
            }
         
            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
            {
               Thread.yield();
            
               //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
               //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
               //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
               try {Thread.sleep(1);} catch(Exception e) {} 
            
               now = System.nanoTime();
            }
         }
      }
   }
   
   private void updateGame()
   {
      back.downBackground();
   }
   
   private void drawGame(float interpolation)
   {
      back.setInterpolation(interpolation);
      back.repaint();
   }
   
}