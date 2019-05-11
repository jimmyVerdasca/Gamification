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

/**
 * Handler of one bluetooth service
 * 
 * @author jimmy
 */
public class BluetoothPairing {
    
    private final String name;
    
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private StreamConnection clientStream;
    private final BluetoothServicesDiscovery bluetoothDiscovery = new BluetoothServicesDiscovery();
    private final UUID uuid;

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
        boolean found = false;
        RemoteDevice rd = null;
        while(!found) {
            for (RemoteDevice remoteDevice : bluetoothDiscovery.devicesFound) {
                if(remoteDevice.getFriendlyName(false).equals(name)
                        || remoteDevice.getBluetoothAddress().equals(name)) {
                    found =  true;
                    rd = remoteDevice;
                    System.out.println("trouvé " + rd.getFriendlyName(false));
                }
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
     *send a message that expect an acquittement
     * 
     * @param data
     * @param acquittement
     * @throws java.io.IOException
     */
    public void sendMessage(byte[] data, byte acquittement) throws IOException {
        sendMessage(data);
        waitAck(acquittement);
    }
    
    /**
     * send a message
     * 
     * @param data bytes to send
     * @throws java.io.IOException if fails to send
     */
    public void sendMessage(byte[] data) throws IOException {
        dataOut.write(data);
    }
    
    /**
     * loop reading the inputStream until we read the expexted acquittement
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
        dataIn.read(datas, 0, nbBytesToRead);
        return datas;
    }
}
