package effortMeasurer;

import Program.Movement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * abstract class to implement if we want to use different kinds of detector
 * 
 * It offers the setEffort and getEffort method so that detectors can
 * thread safely set the effort and others classes can read the value.
 * 
 * Effort is a value between 0 and MAX_REACHED. If a new higher value comes,
 * MAX_REACHED become this new value. This value is stocked in database.
 * 
 * Furthermore 1 is a special value considered as the perfect effort
 * for the current sportsman. And 0 is when he does nothing.
 * 
 * @author jimmy
 */
public abstract class EffortCalculator extends TimerTask {
    
    private double effort = 0;
    private final Object lock = new Object();

    
    /**
     * More measures means less impact of the new measures.
     * Less measures means less precision.
     */
    private final int LENGTH_AVERAGE_LIST;
    
    /**
     * Value expected for an optimal effort, should be updated by
     * the child-classes depending of the detector types and effort of the user
     */
    private double freqAtVMASpeed;
    private double targetPercentEffort;
    private double[] freqAtVMAByMovements;
    
    /**
     * percent maximum relative to freqAtVMASpeed ever reached.
     */
    private double[] MAX_REACHED;
    
    /**
     * observable inner class to let others class reach effort
     */
    private EffortObservable obs;
    
    private Movement currentMovement;
    

    /**
     * constructor build the list of "current measures".
     * get the MAX_REACHED in database. or create it.
     * 
     * @param freqAtVMAByMovement Values expected for an optimal effort by kind
     *                           of movement.
     * @param currentMovement the movement with wich we start.
     * @param lengthDataList size of the list that will be created by
     *                       child-classes.
     */
    public EffortCalculator(double[] freqAtVMAByMovement, Movement currentMovement, int lengthDataList) {
        this.currentMovement = currentMovement;
        freqAtVMASpeed = freqAtVMAByMovement[currentMovement.ordinal()];
        freqAtVMAByMovements = freqAtVMAByMovement;
        targetPercentEffort = 0.8;
        LENGTH_AVERAGE_LIST = lengthDataList;
        obs = new EffortObservable();
        MAX_REACHED = new double[freqAtVMAByMovement.length];
        for (int i = 0; i < freqAtVMAByMovement.length; i++) {
            MAX_REACHED[i] = 1.5;
        }
    }

    /**
     * set the percent of freq at VMA speed we are targeting right now.
     * For example if we are training endurance we want 60% of the VMA.
     * Then we call setTargetPercentEffort(0.6).
     * As default we want 0.8 of the VMA.
     * 
     * @param targetPercentEffort the new percent we want to set
     */
    public void setTargetPercentEffort(double targetPercentEffort) {
        this.targetPercentEffort = targetPercentEffort;
    }

    /**
     * Overridable method to set the relative detector on.
     * Care to not call twice.
     * 
     * Call super if you override.
     */
    public void start() {
        new Timer().schedule(this ,1000);
    }

    /**
     * return the effort thread safely
     * 
     * @return the effort thread safely
     */
    public final double getEffort() {
        double temp;
        synchronized (lock) {
            temp = effort;
        }
        return temp;
    }

    /**
     * set the effort thread safely
     * 
     * @param effort the new effort value
     */
    protected final void setEffort(double effort) {
        if (effort > MAX_REACHED[currentMovement.ordinal()]) {
            MAX_REACHED[currentMovement.ordinal()] = effort;
        } 
        synchronized (lock) {
            this.effort = effort;
        }
        obs.update();
    }

    /**
     * return the MAX_REACHED value ever.
     * 
     * @return the MAX_REACHED value ever.
     */
    public double getMAX_REACHED() {
        return MAX_REACHED[currentMovement.ordinal()];
    }
    
    /**
     * return the freq expected at VMA speed
     * 
     * @return the freq expected at VMA speed
     */
    public final double getFreqAtVMASpeed() {
        return freqAtVMASpeed;
    }
    
    /**
     * Return the current frequence targetted relative to the frequence at Max
     * Anaerobie speed and the percent of this speed we want to reach.
     * 
     * @return the current frequence targetted.
     */
    public double getCurrentFreqTargetted() {
        return freqAtVMASpeed * targetPercentEffort;
    }

    /**
     * return the size of the child-class data list.
     * 
     * @return the size of the child-class data list.
     */
    public final int getLENGTH_AVERAGE_LIST() {
        return LENGTH_AVERAGE_LIST;
    }
    
    /**
     * add an observer of the effort
     * 
     * @param o the new observer to notify later.
     */
    public void addObserver(Observer o) {
        obs.addObserver(o);
    }
    

    public synchronized void setFreqAtVMA(Movement movement) {
        currentMovement = movement;
        freqAtVMASpeed = freqAtVMAByMovements[movement.ordinal()];
    }
    
    public void stop() {
        return;
    }

    public synchronized double getFreqAtVMASpeedOfMovement(Movement movement) {
        return freqAtVMAByMovements[movement.ordinal()];
    }
    
    /**
     * observable inner class to let classes reach the effort
     */
    public class EffortObservable extends Observable {
        
        /**
         * method to call when we change the effort value to
         * ensure that all observers are notified.
         */
        protected void update() {
            setChanged();
            notifyObservers();
        }
        
        /**
         * return the effort value.
         * 
         * @return the effort value.
         */
        public double getEffort() {
            return EffortCalculator.this.getEffort();
        }
        
        /**
         * Some observers will need to reach the MAX_REACHED too.
         * to be able to scale the effort.
         * 
         * @return the MAX_REACHED value.
         */
        public double getMAX_REACHED() {
            return EffortCalculator.this.getMAX_REACHED();
        }
        
        public double getFreqAtVMA() {
            return EffortCalculator.this.getFreqAtVMASpeed();
        }

        public double getCurrentFreqTargetted() {
            return EffortCalculator.this.getCurrentFreqTargetted();
        }
    }
}
