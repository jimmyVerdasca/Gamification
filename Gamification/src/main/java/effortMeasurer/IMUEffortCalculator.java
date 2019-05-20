package effortMeasurer;

import imu.BluetoothIMUAPI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

/**
 * Class using the Inertial Movement Unit
 * to detect the acceleration and calcul the effort
 * 
 * @author jimmy
 */
public class IMUEffortCalculator extends EffortCalculator {

    private final BluetoothIMUAPI imu;
    private final Object lock = new Object();
    private final LinkedList<Double> speedAverage;
    
    /**
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public IMUEffortCalculator() 
            throws IOException, FileNotFoundException, ParseException {
        super(25, 100, 10);
        imu = new BluetoothIMUAPI();
        imu.configure();
        imu.startCapture();
        speedAverage = new LinkedList();
        for (int i = 0; i < getLENGTH_AVERAGE_LIST(); i++) {
            speedAverage.add(0.0);
        }
    }

    /**
     * Add a 0 entry in the measure list of the parent.
     */
    @Override
    public void run() {
        double[][] pair;
        double newValue;
        try {
            pair = imu.registerDatasIncoming();
            newValue = pair[0][0] + pair[1][0] + pair[2][0];
            synchronized(lock) {
                speedAverage.remove();
                speedAverage.add(Math.abs(newValue));
            }
        } catch (IOException ex) {
            Logger.getLogger(IMUEffortCalculator.class.getName()).log(Level.SEVERE, null, ex);
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
