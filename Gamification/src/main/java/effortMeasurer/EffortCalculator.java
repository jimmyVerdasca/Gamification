package effortMeasurer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * abstract class to implement if we want to use a different kind of detector
 * 
 * @author jimmy
 */
public abstract class EffortCalculator extends TimerTask {
    
    private double effort = 0;
    private final Object lock = new Object();

    
    //More measure means less impact of the new measures.
    private final int LENGTH_AVERAGE_LIST;
    /*Value max expected for a maximum effort, should be updated by
    the child-classes depending of the detector types and effort of the user*/
    private final double EXPECTED_MAX_AVERAGE;
    private final int DELAY_BETWEEN_MEASURES;

    /**
     * constructor build the list of "current measures".
     * @param expectedMaxAverage
     * @param lengthDataList
     * @param delayBetweenMeasures
     */
    public EffortCalculator(double expectedMaxAverage, int lengthDataList, int delayBetweenMeasures) {
        EXPECTED_MAX_AVERAGE = expectedMaxAverage;
        LENGTH_AVERAGE_LIST = lengthDataList;
        DELAY_BETWEEN_MEASURES = delayBetweenMeasures;
    }
    
    /**
     * overridable methode to set the relative detector on.
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
    public final void setEffort(double effort) {
        synchronized (lock) {
            this.effort = effort;
        }
    }

    public final double getEXPECTED_MAX_AVERAGE() {
        return EXPECTED_MAX_AVERAGE;
    }

    public final int getLENGTH_AVERAGE_LIST() {
        return LENGTH_AVERAGE_LIST;
    }

    public int getDELAY_BETWEEN_MEASURES() {
        return DELAY_BETWEEN_MEASURES;
    }
}
