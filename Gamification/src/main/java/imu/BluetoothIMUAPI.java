package imu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class reads the IMUConfig to search a particular IMU
 * through his friendly name
 * Then offers several methods to connect and communicate with an IMU
 * 
 * @author jimmy
 */
public final class BluetoothIMUAPI {
    BluetoothPairing imuHandler;
    
    private final byte ACQUITTEMENT = (byte) 0xff;
    private final byte START_CAPTURE = (byte) 0x07;
    private final byte STOP_CAPTURE = (byte) 0x20;
    // 1byte packet type + 3byte timestamp + 3x2byte Analog Accel + 3x2byte MPU9150 gyro
    private final int FRAME_SIZE = 16;
    
    private final LinkedList<Integer> datasRegistre;
    private double[][] accel_b;
    private double[][] accel_k;
    private double[][] accel_r;
    private double[][] gyro_b;
    private double[][] gyro_k;
    private double[][] gyro_r;
    
    private double[][] accel_r_k;
    private double[][] gyro_r_k;
    
    private byte[] buffer;
    private double[][] accelMesure = new double[3][1];
    private ByteBuffer bb = ByteBuffer.allocate(2);

    /**
     * constructor
     * 
     * @throws FileNotFoundException read the IMUConfig.properties file to
     *      know the friendly name of the imu and his service/protocole number
     * @throws IOException if we can't read the MUConfig file
     * @throws ParseException if the file is missbuilded
     */
    public BluetoothIMUAPI() throws FileNotFoundException, IOException, ParseException {
        this.datasRegistre = new LinkedList<>();
        FileInputStream input = new FileInputStream("src/main/java/imu/IMUConfig.properties");
	Properties prop = new Properties();
        prop.load(input);
        byte uuid = Byte.parseByte(prop.getProperty("serviceID"));
        importCalibration();
        imuHandler = new BluetoothPairing(prop.getProperty("deviceName"), uuid);
        imuHandler.connect();
        bb.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    /**
     * set up the IMU
     * 
     * @throws IOException if the messages of configuration can't be sent.
     */
    public void configure() throws IOException {
        //Selecting Shimmer accel and gyro
        System.out.println("configure1");
        imuHandler.sendMessage(new byte[]{(byte)0x08, (byte)0x40, (byte)0x10, (byte)0x00}, ACQUITTEMENT);
        //Setting accel range to 4g
        System.out.println("configure2");
        imuHandler.sendMessage(new byte[]{(byte)0x09, (byte)0x01,}, ACQUITTEMENT);
        //Setting gyro range to 500dps
        System.out.println("configure3");
        imuHandler.sendMessage(new byte[]{(byte)0x49, (byte)0x01,}, ACQUITTEMENT);
        //Setting Shimmer sampling rate to 51.2Hz
        System.out.println("configure4");
        imuHandler.sendMessage(new byte[]{(byte)0x05, (byte)256, (byte)0x02}, ACQUITTEMENT);
    }
    
    /**
     * ask the imu to start sending us measure of accelerometer a gyrometer
     * 
     * @throws IOException if we can't send the message
     */
    public void startCapture() throws IOException {
        imuHandler.sendMessage(new byte[]{START_CAPTURE}, ACQUITTEMENT);
        System.out.println("capture started");
    }
    
    /**
     * say to the imu to stop measure datas
     * 
     * @throws IOException if we can't send the message
     */
    public void stopCapture() throws IOException {
        imuHandler.sendMessage(new byte[]{STOP_CAPTURE}, ACQUITTEMENT);
    }
    
    /**
     * after starting the capture, we can call this method to read
     * the acceleration and gyroscope measures
     * 
     * @throws IOException if we can't read bytes from the IMU
     */
    public double[][] registerDatasIncoming() throws IOException {
        buffer = imuHandler.readBytes(FRAME_SIZE);
        
        bb.put(buffer[10]);
        bb.put(buffer[11]);
        accelMesure[0][0] = bb.getShort(0) - accel_b[0][0];
        bb.clear();
        
        bb.put(buffer[12]);
        bb.put(buffer[13]);
        accelMesure[1][0] = bb.getShort(0) - accel_b[1][1];
        bb.clear();
        
        bb.put(buffer[14]);
        bb.put(buffer[15]);
        accelMesure[2][0] = bb.getShort(0) - accel_b[2][2];
        bb.clear();
        
        accelMesure = Util.multiplyMatrices(accel_r_k, accelMesure);
        return accelMesure;
    }
    
    /**
     * read the calibration.json file to set the calibration of the IMU
     * 
     * @throws FileNotFoundException if we don't find the calibration file
     * @throws IOException if we can't read the calibration file
     * @throws ParseException if there is a json error in the calibration file
     */
    public void importCalibration() throws FileNotFoundException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/main/java/imu/calibration.json");
        JSONObject calibration = (JSONObject)(jsonParser.parse(reader));
        
        JSONObject gyroscope = (JSONObject)(calibration.get("Gyro 500dps"));
        gyro_b = Util.oneDArrayTo2DArray(Util.jsonArrayToArray((JSONArray)(gyroscope.get("b"))), 3, 3);
        gyro_k = Util.oneDArrayTo2DArray(Util.jsonArrayToArray((JSONArray)(gyroscope.get("k"))), 3, 3);
        gyro_r = Util.oneDArrayTo2DArray(Util.jsonArrayToArray((JSONArray)(gyroscope.get("r"))), 3, 3);
        
        JSONObject acceleration = (JSONObject)(calibration.get("Accel 4.0g"));
        accel_b = Util.oneDArrayTo2DArray(Util.jsonArrayToArray((JSONArray)(acceleration.get("b"))), 3, 3);
        accel_k = Util.oneDArrayTo2DArray(Util.jsonArrayToArray((JSONArray)(acceleration.get("k"))), 3, 3);
        accel_r = Util.oneDArrayTo2DArray(Util.jsonArrayToArray((JSONArray)(acceleration.get("r"))), 3, 3);
        
        accel_r_k = Util.multiplyMatrices(Util.invertMatrix(accel_r), Util.invertMatrix(accel_k));
        gyro_r_k = Util.multiplyMatrices(Util.invertMatrix(gyro_r), Util.invertMatrix(gyro_k));
    }
}
