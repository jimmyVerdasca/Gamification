package effortMeasurer;

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
    private double EXPECTED_MAX_AVERAGE;
    
    /**
     * percent maximum relative to EXPECTED_MAX_AVERAGE ever reached.
     */
    private double MAX_REACHED;
    
    /**
     * observable inner class to let others class reach effort
     */
    private EffortObservable obs;
    
    /**
     * "database" where is store MAX_REACHED
     */
    private String pathConfig = "src/main/java/effortMeasurer/CycleEffortConfig.properties";
    private String MAX_REACHED_NAME_PROPERTY = "MAX_REACHED";

    /**
     * constructor build the list of "current measures".
     * get the MAX_REACHED in database. or create it.
     * 
     * @param expectedMaxAverage Value expected for an optimal effort.
     * @param lengthDataList size of the list that will be created by
     *                       child-classes.
     */
    public EffortCalculator(double expectedMaxAverage, int lengthDataList) {
        EXPECTED_MAX_AVERAGE = expectedMaxAverage;
        LENGTH_AVERAGE_LIST = lengthDataList;
        obs = new EffortObservable();
        
        try {
            FileInputStream input = new FileInputStream(pathConfig);
            Properties prop = new Properties();
            prop.load(input);
            MAX_REACHED = Double.parseDouble(prop.getProperty(MAX_REACHED_NAME_PROPERTY));
        } catch(IOException ex) {
            MAX_REACHED = expectedMaxAverage;
        }
    }
    
    /**
     * overridable method to set the relative detector on.
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
        if (effort > MAX_REACHED) {
            MAX_REACHED = effort;
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
        return MAX_REACHED;
    }
    
    /**
     * return the optimale effort value.
     * 
     * @return the optimale effort value.
     */
    public final double getEXPECTED_MAX_AVERAGE() {
        return EXPECTED_MAX_AVERAGE;
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
    
    /**
     * overridable method that Stop the workout detection properly.
     * Stock the MAX_REACHED in database.
     * 
     * Call super if you override.
     */
    public void stop() {
        FileOutputStream output =  null;
        try {
            File file = new File(pathConfig);
            output = new FileOutputStream(file, false);
            if (!file.exists()) {
                file.createNewFile();
            }
            output.write((MAX_REACHED_NAME_PROPERTY + "=" + Double.toString(MAX_REACHED)).getBytes());
            output.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    }
}
