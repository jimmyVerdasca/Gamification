package imu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import util.DataFileUtil;

/**
 * Handler of one bluetooth device/service
 * 
 * @author jimmy
 */
public class BluetoothPairing {
    
    /**
     * name of the device handled
     */
    private final String name;
    
    /**
     * Service uuid that we will communicate with.
     */
    private final UUID uuid;
    
    /**
     * Output stream pc to device.
     */
    private DataOutputStream dataOut;
    
    /**
     * Input stream device to pc.
     */
    private DataInputStream dataIn;
    
    /**
     * Connection where we get the streams.
     */
    private StreamConnection clientStream;
    
    /**
     * Class handling service/device discovery.
     */
    private final BluetoothServicesDiscovery bluetoothDiscovery =
            new BluetoothServicesDiscovery();
    

    /**
     * constructor using the bluetooth service discovery to get the streams with
     * which we will be able to send message to the device we are pairing
     * 
     * @param name friendly name of the device we are pairing
     * @param uuid service uuid with which we will discuss
     * @throws IOException if the connection fails
     */
    public BluetoothPairing(String name, byte uuid) throws IOException {
        this.name = name;
        this.uuid = new UUID(uuid);
        
    }
    
    /**
     * try to connect to the device and service specified at construction
     * 
     * @throws IOException depending of the error type
     * can be device or service not found or simply streams not able to connect
     */
    public void connect() throws IOException {
        bluetoothDiscovery.searchDevicesAvailable();
        RemoteDevice rd = null;
        for (RemoteDevice remoteDevice : bluetoothDiscovery.devicesFound) {
            if(remoteDevice.getFriendlyName(false).equals(name)
                    || remoteDevice.getBluetoothAddress().equals(name)) {
                rd = remoteDevice;
                System.out.println("trouvé " + rd.getFriendlyName(false));
            }
        }
        UUID[] uuidSet = new UUID[1];
        uuidSet[0] = uuid;
        bluetoothDiscovery.searchServicesAvailable(uuidSet, rd);
        for (String service : bluetoothDiscovery.servicesURL) {
            System.out.println("services " + service.getBytes());
            clientStream = (StreamConnection) Connector.open(service);
        }
        if(clientStream != null) {
            dataOut = clientStream.openDataOutputStream();
            dataIn = clientStream.openDataInputStream();
        } else {
            throw new IOException("service " + uuid + " not found");
        }
    }
    
    /**
     * close all connections properly
     */
    public void disconnect() {
        try {
            dataOut.close();
            dataIn.close();
            clientStream.close();
        } catch (IOException ex) {
            Logger.getLogger(BluetoothPairing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Send a message that expect an acquittement.
     * 
     * @param data buffer to send
     * @param acquittement expected aquittement to receive as response by the
     *                     Bluetooth device.
     * @throws java.io.IOException If an error occur while sending the packet.
     */
    public void sendMessage(byte[] data, byte acquittement) throws IOException {
        sendMessage(data);
        waitAck(acquittement);
    }
    
    /**
     * send a message with 0 controls.
     * 
     * @param data bytes to send
     * @throws java.io.IOException if fails to send
     */
    public void sendMessage(byte[] data) throws IOException {
        dataOut.write(data);
    }
    
    /**
     * loop reading the inputStream until we read the expected acquittement
     * 
     * @param acquittementExpected value of the acquittement expected
     */
    private void waitAck(byte acquittementExpected) {
        byte answer = (byte) 0x00;
        byte ack = (byte) acquittementExpected;
        do {
            try {
                answer = dataIn.readByte();
            } catch (IOException ex) {
                Logger.getLogger(BluetoothServicesDiscovery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while (answer != ack);
    }
    
    /**
     * read an amount of bytes from the dataIn stream
     * 
     * @param nbBytesToRead number of bytes we want to receive from the stream.
     * @return the datas red
     * @throws IOException if an IO error occure
     */
    public byte[] readBytes(int nbBytesToRead) throws IOException {
        byte[] datas = new byte[nbBytesToRead];
        
        int bytesReadSoFar = 0;
        
        while (bytesReadSoFar < nbBytesToRead) {
            byte[] dataTemp =  new byte[nbBytesToRead - bytesReadSoFar];
            int tempBytesReadSoFar = dataIn.read(dataTemp, 0, nbBytesToRead - bytesReadSoFar);
            System.arraycopy(dataTemp, 0, datas, bytesReadSoFar, dataTemp.length);
            bytesReadSoFar += tempBytesReadSoFar;
        }
        //dataIn.read(datas, 0, nbBytesToRead);
        return datas;
    }
}
