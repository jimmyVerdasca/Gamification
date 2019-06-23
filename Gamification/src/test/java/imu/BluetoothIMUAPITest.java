package imu;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * To run the test firstly connect the shimmer3 to the computer,
 * you can follow the chapter "How to pair your device" of this tutorial for it:
 * https://www.tobiipro.com/learn-and-support/learn/
 * steps-in-an-eye-tracking-study/setup/setting-up-shimmer-gsr-in-pro-lab/
 * 
 * @author jimmy
 */
public class BluetoothIMUAPITest {
    
    private BluetoothIMUAPI imuAPI;
    
    @Before
    public void setUp() 
            throws IOException, FileNotFoundException, ParseException {
        imuAPI = new BluetoothIMUAPI();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * test trying to set up the imu, launching the data registration,
     * registring 100 data and then stop the capture.
     * 
     * If the test fails  at the configuring phase,
     * be sure the device is connected by bluetooth to the computer.
     */
    @Test
    public void testIFBluetoothIMUIsDetected() {
        try {
            System.out.println("configuring");
            imuAPI.configure();
            System.out.println("launching capture");
            imuAPI.startCapture();
            System.out.println("capture 100 data");
            int i = 0;
            while(i++ < 100) {
                imuAPI.registerDataIncoming();
            }
            System.out.println("stop capture");
            imuAPI.stopCapture();
            assert(true);
        } catch (IOException ex) {
            assert(false);
        }
    }
    
}
