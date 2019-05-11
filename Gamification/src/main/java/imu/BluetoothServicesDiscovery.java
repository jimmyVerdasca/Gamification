package imu;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 * class using the bluecove API to discover the bluetooth devices and services
 * reachable currently.
 * 
 * @author jimmy
 */
public class BluetoothServicesDiscovery implements DiscoveryListener {

    LocalDevice localDevice;
    DiscoveryAgent agent;
    Set<RemoteDevice> devicesFound;
    Set<String> servicesURL;
    
    /**
     * Constructor
     */
    public BluetoothServicesDiscovery() {
        devicesFound = new HashSet<>();
        servicesURL = new HashSet<>();
        try {
            System.setProperty("bluecove.stack", "winsock");
            localDevice = LocalDevice.getLocalDevice();
            agent = localDevice.getDiscoveryAgent();
            
        } catch (BluetoothStateException ex) {
            Logger.getLogger(BluetoothServicesDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
    
    /**
     * Launch the research of device and lock the instance
     * until the full result is received.
     * 
     * @throws javax.bluetooth.BluetoothStateException
     */
    public void searchDevicesAvailable() throws BluetoothStateException {
        agent.startInquiry(DiscoveryAgent.GIAC, this);
        try {
            synchronized(this){
                this.wait();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Launch the research of service in a specified device
     * 
     * You can find the mains uuid numbers at :
     * http://www.bluecove.org/bluecove/apidocs/javax/bluetooth/UUID.html
     * 
     * For the shimmer3 that we are using in this project,
     * we use the RFCOMM protocole with uuid 0x0003.
     * 
     * @param uuidService UUID number of the service 
     * @param deviceName device that we inspect if the service is available
     * @throws javax.bluetooth.BluetoothStateException
     */
    public void searchServicesAvailable(UUID[] uuidService, RemoteDevice deviceName) throws BluetoothStateException {
        agent.searchServices(null, uuidService, deviceName, this);
        try {
            synchronized(this){
                this.wait();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * When the research of device is launched,
     * if a device is discovered the class is notified with this method.
     * 
     * @param btDevice bluetooth device found
     * @param dc helper class to determine the service class
     *      of the device found.
     */
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass dc) {
        String name;
        try {
            name = btDevice.getFriendlyName(false);
        } catch (IOException e) {
            name = btDevice.getBluetoothAddress();
        }
        devicesFound.add(btDevice);
    }

    /**
     * When the research of service is launched,
     * if a service is discovered the class is notified with this method.
     * 
     * @param transID the transaction ID of the service search that is posting
     * @param servicesDiscovered list of services found during the search
     */
    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servicesDiscovered) {
        for (ServiceRecord serviceRecord : servicesDiscovered) {
            servicesURL.add(serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
        }
    }

    /**
     * the research of service is finish. We can unlock the class.
     * 
     * @param transID the transaction ID of the service search that is posting
     * @param respCode status response of the research of services
     */
    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        synchronized (this) {
           this.notify();
       }
    }

    /**
     * the research of devices is finish. We can unlock the class.
     * 
     * @param discType status response of the research of devices
     */
    @Override
    public void inquiryCompleted(int discType) {
        synchronized(this){
            this.notify();
        }
    }

    /**
     * Return the list of devices name currently found.
     * 
     * @return the list of devices name currently found.
     */
    public Set<RemoteDevice> getDevicesFound() {
        return devicesFound;
    }

    /**
     * Return the list of services URL currently found.
     * 
     * @return the list of services URL currently found.
     */
    public Set<String> getServicesURL() {
        return servicesURL;
    }
}
