package beechat.connection;

import beechat.Main;
import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import beechat.connection.Node;

/**
 * Listens for incoming beechat.connection requests, and handles them appropriately.
 */
public class BackgroundDataListener implements IDataReceiveListener {

    DigiMeshDevice localDevice;

    public BackgroundDataListener(DigiMeshDevice localDevice){
        this.localDevice = localDevice;
    }

    @Override
    public void dataReceived(XBeeMessage xBeeMessage) {

        RemoteDigiMeshDevice device = (RemoteDigiMeshDevice) xBeeMessage.getDevice();

        if (!Main.knownDevices.containsKey(device.get64BitAddress())) {
            System.out.println("Starting new connection with " + device);
            Main.knownDevices.put(device.get64BitAddress(), new Node(this.localDevice, device));
        }


        byte[] data = xBeeMessage.getData();

        System.out.println("Received " + data.length + " bytes from " + device);

        Node targetNode = Main.knownDevices.get(device.get64BitAddress());

        if (!targetNode.keyHandler.hasKey) {
            System.out.println("Processing public key...");
            targetNode.keyHandler.receiveKey(data);
        } else {
            targetNode.receiveMessage(data);
        }

    }
}
