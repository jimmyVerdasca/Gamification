package joyconController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

/**
 * <b>Main class of the library. This class will be your joycon</b>
 * <p>
 * To use it, just create a new Joycon object</p>
 * <p>
 * You can check the example to learn how to use this library</p>
 *
 * @author goupil
 * @version 1.0
 */
public class Joycon {

    private Object lock = new Object();
    protected boolean j_Open;
    protected JoyconListener j_Listener;
    private HidDeviceInfo joyconInfo;
    private HidDevice joycon;
    private LeftTranslator leftTranslator;
    private RightTranslator rightTranslator;
    private JoyconStickCalc calculator;
    private int[] factory_stick_cal = new int[18];
    private int[] stick_cal_x_l = new int[3];
    private int[] stick_cal_y_l = new int[3];
    private int[] stick_cal_x_r = new int[3];
    private int[] stick_cal_y_r = new int[3];
    

    /**
     * <b>Constructor of the Joycon.</b>
     *
     * <p>
     * To specify a correct identifier just use
     * <b>JoyconConstant.JOYCON_LEFT</b> to use the left Joycon or
     * <b>JoyconConstant.JOYCON_RIGHT</b></p>
     *
     *
     * @param joyconId Identifier of the joycon you'll use
     */
    public Joycon(short joyconId) {
        if ((joyconId == JoyconConstant.JOYCON_LEFT) || (joyconId == JoyconConstant.JOYCON_RIGHT)) {
            initialize(joyconId);
        } else {
            System.out.println("Wrong joycon id!\nPlease use 'JoyconConstant.JOYCON_RIGHT' or 'JoyconConstant.JOYCON_LEFT'");
        }
    }

    /**
     * <b>Set the listener of the Joycon to handle his input</b>
     *
     * @param li The listener, specify null to remove it
     */
    public void setListener(JoyconListener li) {
        j_Listener = li;
    }

    /**
     * <b>Close the connection with the Joycon</b>
     *
     * @return True or false if closed correctly
     */
    public boolean close() {
        boolean isClosed = false;
        try {
            joycon.close();
            isClosed = true;
        } catch (IllegalStateException e) {
            System.out.println("Error while closing conection to the joycon!");
            isClosed = false;
        }
        return isClosed;
    }

    private void initialize(short joyconId) {
        joyconInfo = null;
        joycon = null;
        calculator = new JoyconStickCalc();
        leftTranslator = new LeftTranslator(calculator, stick_cal_x_l, stick_cal_y_l);
        rightTranslator = new RightTranslator(calculator, stick_cal_x_r, stick_cal_y_r);
        System.out.println("Listing Hid devices...");
        List<HidDeviceInfo> list = PureJavaHidApi.enumerateDevices();
        for (HidDeviceInfo info : list) {
            if (info.getManufacturerString() != null && (info.getManufacturerString().equals(JoyconConstant.MANUFACTURER)) && (info.getVendorId() == JoyconConstant.VENDOR_ID) && (info.getProductId() == joyconId)) {
                System.out.println("Found a Nintendo gear!\nConecting...");
                joyconInfo = info;
            } else if (info.getManufacturerString() == null) {
                joyconInfo = info;
            }
        }
        if (joyconInfo != null) {
            try {
                joycon = PureJavaHidApi.openDevice(joyconInfo);
                System.out.print("Connected to Joy-Con ");
                if (joyconInfo.getProductId() == JoyconConstant.JOYCON_LEFT) {
                    System.out.println("Left!");
                } else if (joyconInfo.getProductId() == JoyconConstant.JOYCON_RIGHT) {
                    System.out.println("Right!");
                }

                byte ids = 1;
                byte[] datat;

                //Set the joycon user light to blinking
                datat = new byte[16];
                datat[9] = 0x30;
                datat[10] = (byte) 1; // lights player i = 2^i
                joycon.setOutputReport(ids, datat, 16);

                joycon.setInputReportListener(new InputReportListener() {

                    private float horizontal = 0f;
                    private float vertical = 0f;

                    @Override
                    public void onInputReport(HidDevice source, byte id, byte[] data, int len) {
                        //Input code case
                        if (id == 48) {
                            HashMap<String, Boolean> newInputs = new HashMap<>();
                            float horizontal = 0f;
                            float vertical = 0f;
                            byte battery = 0;
                            if (joyconInfo.getProductId() == JoyconConstant.JOYCON_LEFT) {
                                leftTranslator.translate(data);
                                newInputs = leftTranslator.getInputs();
                                horizontal = leftTranslator.getHorizontal();
                                vertical = leftTranslator.getVertical();
                                battery = leftTranslator.getBattery();
                            } else if (joyconInfo.getProductId() == JoyconConstant.JOYCON_RIGHT) {
                                rightTranslator.translate(data);
                                newInputs = rightTranslator.getInputs();
                                horizontal = rightTranslator.getHorizontal();
                                vertical = rightTranslator.getVertical();
                                battery = rightTranslator.getBattery();
                            }
                            if (j_Listener != null) {
                                double[] accelX = new double[3];
                                ByteBuffer buffer = ByteBuffer.allocate(2);
                                buffer.order(ByteOrder.LITTLE_ENDIAN);
                                // there is 3 report of acceleration by transmission, at byte 14, 26 and 38 (2 byte long)
                                // others axes are at byte number 10,12,22,24,34 and 36
                                for (int i = 0; i < 3; i++) {
                                    buffer.put(data[(i * 12) + 14]);
                                    buffer.put(data[(i * 12) + 15]);
                                    accelX[i] = buffer.getShort(0) * 0.000244f;
                                    buffer.clear();
                                }
                                j_Listener.handleNewInput(new JoyconEvent(newInputs, accelX, horizontal, vertical, battery));
                                this.horizontal = horizontal;
                                this.vertical = vertical;
                            }
                        } else if (id == 33) {
                            if (data[12] == -112) {
                                for (int i = 19; i < 37; i++) {
                                    int c;
                                    byte b = data[i];
                                    if (b < 0) {
                                        c = b + 256;
                                    } else {
                                        c = b;
                                    }
                                    factory_stick_cal[i - 19] = c;
                                }
                            }
                        }
                    }
                });
                joycon.setDeviceRemovalListener(new DeviceRemovalListener() {
                    @Override
                    public void onDeviceRemoval(HidDevice source) {
                        System.out.println("Joy-Con disconnected!");
                    }
                });

                //Get joystick calibration info
                datat = new byte[16];
                datat[9] = 0x10;
                datat[10] = 0x3D;
                datat[11] = 0x60;
                datat[14] = 0x12;
                joycon.setOutputReport(ids, datat, 16);
                
                Thread.sleep(45);

                if (joyconInfo.getProductId() == JoyconConstant.JOYCON_LEFT) {
                    stick_cal_x_l[1] = (factory_stick_cal[4] << 8) & 0xF00 | factory_stick_cal[3];
                    stick_cal_y_l[1] = (factory_stick_cal[5] << 4) | (factory_stick_cal[4] >> 4);
                    stick_cal_x_l[0] = stick_cal_x_l[1] - ((factory_stick_cal[7] << 8) & 0xF00 | factory_stick_cal[6]);
                    stick_cal_y_l[0] = stick_cal_y_l[1] - ((factory_stick_cal[8] << 4) | (factory_stick_cal[7] >> 4));
                    stick_cal_x_l[2] = stick_cal_x_l[1] + ((factory_stick_cal[1] << 8) & 0xF00 | factory_stick_cal[0]);
                    stick_cal_y_l[2] = stick_cal_y_l[1] + ((factory_stick_cal[2] << 4) | (factory_stick_cal[2] >> 4));
                } else if (joyconInfo.getProductId() == JoyconConstant.JOYCON_RIGHT) {
                    stick_cal_x_r[1] = (factory_stick_cal[10] << 8) & 0xF00 | factory_stick_cal[9];
                    stick_cal_y_r[1] = (factory_stick_cal[11] << 4) | (factory_stick_cal[10] >> 4);
                    stick_cal_x_r[0] = stick_cal_x_r[1] - ((factory_stick_cal[13] << 8) & 0xF00 | factory_stick_cal[12]);
                    stick_cal_y_r[0] = stick_cal_y_r[1] - ((factory_stick_cal[14] << 4) | (factory_stick_cal[13] >> 4));
                    stick_cal_x_r[2] = stick_cal_x_r[1] + ((factory_stick_cal[16] << 8) & 0xF00 | factory_stick_cal[15]);
                    stick_cal_y_r[2] = stick_cal_y_r[1] + ((factory_stick_cal[17] << 4) | (factory_stick_cal[16] >> 4));
                }

                //Set to normal input mode
                datat = new byte[16];
                datat[9] = 0x40;
                datat[10] = 0x01;
                joycon.setOutputReport(ids, datat, 16);
                
                Thread.sleep(45);
                
                // activate imu
                datat = new byte[16];
                datat[9] = 0x03;
                datat[10] = 0x30;
                joycon.setOutputReport(ids, datat, 16);
            } catch (IOException ex) {
                System.out.println("Error while opening connection to the Joy-Con!\nPlease try to close all software that could communicate with it and retry.");
            } catch (InterruptedException ex) {
                System.out.println("Error, the Joy-Con is not connected anymore!");
            }
        } else {
            System.out.println("No Joy-Con was found :(\nTry to conect a Joy-Con and launch the software again.");
        }
    }
    
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private byte[] getMacAdress() {
        InetAddress ip;
        byte[] mac = null;
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            mac = network.getHardwareAddress();
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(Joycon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mac;
    }
}
