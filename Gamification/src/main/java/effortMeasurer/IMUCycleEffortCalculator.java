package effortMeasurer;

import imu.BluetoothIMUAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 *
 * @author jimmy
 */
public class IMUCycleEffortCalculator extends EffortCalculator {

    private final BluetoothIMUAPI imu;
    private final double[] accelerationMeasures;
    private int index = 0;
    private boolean running = false;
    
    public IMUCycleEffortCalculator() throws IOException, FileNotFoundException, ParseException {
        super(0.006, 100, 0);
        imu = new BluetoothIMUAPI();
        imu.configure();
        accelerationMeasures = new double[getLENGTH_AVERAGE_LIST()];
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            accelerationMeasures[i] = getEXPECTED_MAX_AVERAGE() / 2;
        }
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
    
    private int modulo(int a, int mod) {
        int result = (a) % mod;
        if (result < 0) {
            result += mod;
        }
        return result;
    }

    @Override
    public void run() {
        double newValue = 0.0;
        long[] timeMeasure = new long[accelerationMeasures.length];
        int id = 0;
        int indexBefore = 0;
        double treshold = 23;
        double frequence = 0;
        double nbCycle = 0;
        boolean isUpTreshold = false;
        
        while (running) {
            try {
                newValue = imu.registerDatasIncoming()[2][0];
            } catch (IOException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
            timeMeasure[id] = System.currentTimeMillis();
            id = modulo(id + 1, timeMeasure.length);
            accelerationMeasures[index] = newValue;
            index = (index + 1) % accelerationMeasures.length;
            // calcul the cycles numbers
            nbCycle = 0;
            for (double accelerationMeasure : accelerationMeasures) {
                if (!isUpTreshold && accelerationMeasure > treshold) {
                    nbCycle++;
                    isUpTreshold = true;
                } else if (accelerationMeasure < treshold) {
                    isUpTreshold = false;
                }
            }
            // calcul the cycle (only if array timeMeasure has been filled once)
            if (timeMeasure[id] != 0) {
                indexBefore = modulo(id - 1, timeMeasure.length);
                frequence = nbCycle / (timeMeasure[indexBefore] - timeMeasure[id]);
            } else {
                frequence = 0;
            }
            setEffort(frequence / getEXPECTED_MAX_AVERAGE());
            Thread.yield();
        }
    }
}
