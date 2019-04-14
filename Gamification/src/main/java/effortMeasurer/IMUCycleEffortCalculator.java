package effortMeasurer;

import effortMeasurer.fourrier.FFTbase;
import imu.BluetoothIMUAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;
import util.DataFileUtil;

/**
 *
 * @author jimmy
 */
public class IMUCycleEffortCalculator extends EffortCalculator {

    private final BluetoothIMUAPI imu;
    private final double[] accelerationMeasures;
    private final Object lock = new Object();
    private final int BIAS = 65;
    private int index = 0;
    
    public IMUCycleEffortCalculator() throws IOException, FileNotFoundException, ParseException {
        super(0.02, 64, 0);
        imu = new BluetoothIMUAPI();
        imu.configure();
        imu.startCapture();
        accelerationMeasures = new double[getLENGTH_AVERAGE_LIST()];
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            accelerationMeasures[i] = getEXPECTED_MAX_AVERAGE() / 2;
        }
    }

    @Override
    public void run() {
        double newValue = 0.0;
        double[] fftResult;
        long[] timeMeasure = new long[accelerationMeasures.length];
        int id = 0;
        int indexBefore = 0;
        double seuil = 20;
        double frequence = 0;
        double nbHalfCycle = 0;
        boolean isUpMeasure = false;
        long timeElapsedBetweenFirstAndLastMeasure = 0;
        
        while (true) {
            try {
                newValue = imu.registerDatasIncoming()[2][0];
                timeMeasure[id] = System.currentTimeMillis();
                id = (id + 1) % timeMeasure.length;
                if (id < 0) {
                    id = timeMeasure.length + id;
                }

            } catch (IOException ex) {
                Logger.getLogger(IMUCycleEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
            synchronized(lock) {
                accelerationMeasures[index] = newValue;
            }
            index = (index + 1) % accelerationMeasures.length;
            // calcul the cycles average speed
            nbHalfCycle = 0;
            for (double accelerationMeasure : accelerationMeasures) {
                if (!isUpMeasure && accelerationMeasure > seuil) {
                    nbHalfCycle++;
                } else if (isUpMeasure && accelerationMeasure < seuil) {
                    nbHalfCycle++;
                }
            }
            if (timeMeasure[id] != 0) {
                indexBefore = (id - 1) % timeMeasure.length;
                if (indexBefore < 0) {
                    indexBefore = timeMeasure.length + indexBefore;
                }
                timeElapsedBetweenFirstAndLastMeasure = timeMeasure[indexBefore] - timeMeasure[id];
                frequence = nbHalfCycle / timeElapsedBetweenFirstAndLastMeasure;
            } else {
                frequence = 0;
            }
            setEffort(frequence / getEXPECTED_MAX_AVERAGE());
        }
    }
    
}
