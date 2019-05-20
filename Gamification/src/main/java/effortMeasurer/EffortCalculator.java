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
    private double EXPECTED_MAX_AVERAGE;
    private double MAX_REACHED;
    private final int DELAY_BETWEEN_MEASURES;
    private EffortObservable obs;
    private String pathConfig = "src/main/java/effortMeasurer/CycleEffortConfig.properties";
    private String MAX_REACHED_NAME_PROPERTY = "MAX_REACHED";

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
        if (effort > MAX_REACHED) {
            MAX_REACHED = effort;
        } 
        synchronized (lock) {
            this.effort = effort;
        }
        obs.update();
    }

    public double getMAX_REACHED() {
        return MAX_REACHED;
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
    
    public void addObserver(Observer o) {
        obs.addObserver(o);
    }
    
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
    
    public class EffortObservable extends Observable {
        protected void update() {
            setChanged();
            notifyObservers();
        }
        
        public double getEffort() {
            return EffortCalculator.this.getEffort();
        }
        
        public double getMAX_REACHED() {
            return EffortCalculator.this.getMAX_REACHED();
        }
    }
}
