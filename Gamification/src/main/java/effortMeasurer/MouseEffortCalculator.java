package effortMeasurer;

import heigvd.gamification.GameEngine;
import java.awt.AWTException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Robot;
import java.util.LinkedList;

/**
 * Mouse Effort implementation for the mouse vertical movement detection
 * 
 * @author jimmy
 */
public final class MouseEffortCalculator extends EffortCalculator implements MouseMotionListener {

    //used to know the difference of position between each measures
    private int oldY = -1;
    private final Object lock = new Object();
    //to replace mouse easily
    private final Robot robot;
    private final GameEngine sb;
    private final LinkedList<Double> speedAverage;

    /**
     * constructor we need the GameEngine to simplify the code
     * 
     * @param sb JFrame where the mouse will fly over.
     * @throws AWTException if the Robot launch fails
     */
    public MouseEffortCalculator(GameEngine sb) throws AWTException {
        super(100, 100, 10);
        /**
         * for clearer code, for the MouseEffort we keep track of the GameEngine
         * but only to be able to add/remove himself as mouse listener.
         */
        
        speedAverage = new LinkedList();
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            speedAverage.add(0.0);
        }
        this.sb = sb;
        robot = new Robot();
        replace();
    }
    
    @Override
    public void start() {
        super.start();
        sb.addMouseMotionListener(this);
    }
    
    /**
     * obligatory override for the MouseMotionListener but unused for us
     * @param me mouse event properties
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        
    }

    /**
     * detect the mouse movement and keep track of it in the speedAverage list
     * then replace the mouse in his original position.
     * @param me mouse event properties
     */
    @Override
    public void mouseMoved(MouseEvent me) {
        int newY = me.getY();
        
        if (oldY == -1) {
            oldY = newY;
            return;
        }
        synchronized(lock) {
            speedAverage.remove();
            speedAverage.add((double)(Math.abs(newY - oldY)));
        }
        oldY = newY;
        replace();
    }
    
    /**
     * since the measures are with the mouse, we need to put back the mouse in
     * the center of the window, so that if the mouse is put in a one direction
     * material, we can still keep track of the movement.
     * Unfortunately, each time we replace the mouse, we also need to deactivate
     * the listener to not enter in an infinite loop. In counter-part we lose
     * some measures.
     */
    public void replace() {
        sb.removeMouseMotionListener(this);
        robot.mouseMove(200, 200);
        oldY = -1;
        sb.addMouseMotionListener(this);
    }
    
    /**
     * Add a 0 entry in the measure list of the parent to simulate a brake.
     * 
     * Set the effort with an average of the speed list measure from mouse
     */
    @Override
    public void run() {
        synchronized(lock) {
            speedAverage.remove();
            speedAverage.add(0.0);
        }
        
        double average = 0;
        synchronized(lock) {
            for (double speed : speedAverage) {
                average += speed;
            }
        }
        setEffort((average / getLENGTH_AVERAGE_LIST()) / getEXPECTED_MAX_AVERAGE());
    }
}
