package effortMeasurer;

import imu.BluetoothIMUAPI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;
import static util.ArrayUtil.findIndexOfMaxIn;

/**
 *
 * @author jimmy
 */
public class IMUCycleEffortCalculator extends EffortCalculator {

    private final BluetoothIMUAPI imu;
    
    private final double[] accelerationMeasures;
    private final long[] timeMeasure;
    private int currentIndex = 0;
    
    private boolean running = false;
    
    private final ArrayList<Integer> passingThresholdData;
    
    private int currentAxe = 1;
    private boolean isUpTreshold = false;
    private final double TRESHOLD = 18;
    
    public IMUCycleEffortCalculator() throws IOException, FileNotFoundException, ParseException {
        super(0.00000001, 100, 0);
        imu = new BluetoothIMUAPI();
        imu.configure();
        accelerationMeasures = new double[getLENGTH_AVERAGE_LIST()];
        timeMeasure = new long[getLENGTH_AVERAGE_LIST()];
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            accelerationMeasures[i] = getEXPECTED_MAX_AVERAGE() / 2;
        }
        passingThresholdData = new ArrayList<>(getLENGTH_AVERAGE_LIST() / 2);
    }
    
    /**
     * methode that try to calculate the nbCycle in each axes
     * and return the axe where he found the most cycles.
     */
    public int getBestAxe() {
        int temp = this.currentAxe;
        double[] nbCycleFound = new double[3];
        for (int i = 0; i < 3; i++) {
            try {
                this.wait(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
            setCurrentAxe(i);
            nbCycleFound[i] = passingThresholdData.size();
        }
        setCurrentAxe(temp);
        return findIndexOfMaxIn(nbCycleFound);
    }
    
    @Override
    public void start() {
        super.start();
        running = true;
        try {
            imu.startCapture();
        } catch (IOException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        running = false;
        try {
            imu.stopCapture();
        } catch (IOException ex) {
            Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCurrentAxe(int currentAxe) {
        this.currentAxe = currentAxe;
    }
    
    /**
     * infinite loop that update the effort of the super class by 
     * 1) asking a new value to the IMU
     * 2) updating the state of this class (nbCycle and frequence)
     * 3) calculating the effort and setting it
     */
    @Override
    public void run() {
        long time = System.nanoTime();
        long time2;
        while (running) {
            try {
                // TODO fixer le timestamp reçu par imu et remplacer System.nanoTime() par pair.getValue();
                addValue(imu.registerDatasIncoming()[currentAxe][0], System.nanoTime());
                setEffort(getFrequence() / getEXPECTED_MAX_AVERAGE());
                Thread.yield();
            } catch (IOException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * update the frequence value and recalculate the number of cycle before
     */
    private double getFrequence() {
        // calcul the frequence (only if array timeMeasure has been filled once)
        if (passingThresholdData.size() > 1) {
            return ((double)getNbCycle()) / (timeMeasure[passingThresholdData.get(passingThresholdData.size() - 1)] - timeMeasure[passingThresholdData.get(0)]);
        } else {
            return 0;
        }
    }
    
    private int getNbCycle() {
        return passingThresholdData.size();
    }
    
    /**
     * add a new entry to the accelerationMeasure
     * and put his timestamp in timeMeasure at the same index
     * @param newValue the new value
     */
    private void addValue(double newValue, long timestamp) {
        // if the index that we will erase is in the arraylist, we remove it
        if(passingThresholdData.size() > 0 && passingThresholdData.get(0) == currentIndex) {
            passingThresholdData.remove(0);
        }
        
        // if the new value pass the threshold we add the index at the end of the arrayList
        if (!isUpTreshold && Math.abs(newValue) > TRESHOLD) {
            passingThresholdData.add(currentIndex);
            isUpTreshold = true;
        } else if (Math.abs(newValue) < TRESHOLD) {
            isUpTreshold = false;
        }
        
        timeMeasure[currentIndex] = timestamp;
        accelerationMeasures[currentIndex] = newValue;
        currentIndex = (currentIndex + 1) % accelerationMeasures.length;
    }
}
