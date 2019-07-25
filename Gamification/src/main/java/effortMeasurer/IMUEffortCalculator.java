package effortMeasurer;

import Program.Movement;
import imu.BluetoothIMUAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 * Naïv implementation of the EffortCalculator that
 * detects accelerations with a Shimmer3 accelerometer.
 * 
 * The effort is the average acceleration from the x last acceleration sent by
 * the Shimmer3 accelerometer.
 * 
 * @author jimmy
 */
public class IMUEffortCalculator extends EffortCalculator {

    /**
     * Shimmer3 accelerometer handler
     */
    private final BluetoothIMUAPI imu;
    
    /**
     * circular buffer containing the acceleration of the sportsman
     */
    private final LinkedList<Double> accList;
    
    /**
     * constructor
     * 
     * @throws IOException If we can't reach the Shimmer3 accelerometer.
     * @throws FileNotFoundException If the Shimmer3 API has not found the
     *                               calibration file.
     * @throws ParseException If there is an parsing error in the calibration
     *                        file.
     */
    public IMUEffortCalculator(Movement movement) 
            throws IOException, FileNotFoundException, ParseException {
        super(new double[]{25, 25, 25}, movement, 100);
        imu = new BluetoothIMUAPI();
        imu.configure();
        imu.startCapture();
        accList = new LinkedList();
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            accList.add(0.0);
        }
    }

    /**
     * Read datas incoming from the IMU and update the effort in the
     * super-class.
     */
    @Override
    public void run() {
        double[][] pair;
        double newValue;
        try {
            pair = imu.registerDataIncoming();
            newValue = pair[0][0] + pair[1][0] + pair[2][0];
            accList.remove();
            accList.add(Math.abs(newValue));
        } catch (IOException ex) {
            Logger.getLogger(IMUEffortCalculator.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        double average = 0;
        for (double speed : accList) {
            average += speed;
        }
        setEffort((average / getLENGTH_AVERAGE_LIST()) / getCurrentFreqTargetted());
    }
}
