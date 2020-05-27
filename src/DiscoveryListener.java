import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;

import javax.swing.*;

public class DiscoveryListener implements IDiscoveryListener {

    DigiMeshNetwork network;
    DigiMeshDevice localDevice;

    JFrame frame = new JFrame("Please wait...");

    DiscoveryListener(DigiMeshNetwork network, DigiMeshDevice localDevice){
        this.network = network;
        this.localDevice = localDevice;
        System.out.println("Starting discovery process, please wait...");

        frame.add(new JLabel("Discovering devices in network.\nThis should take about 15 seconds"));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

    }

    @Override
    public void deviceDiscovered(RemoteXBeeDevice remoteXBeeDevice) {
        System.out.println("Discovered device " + remoteXBeeDevice.getNodeID());
        frame.add(new JLabel("Discovered device: " + remoteXBeeDevice.getNodeID()));
        network.addRemoteDevice(remoteXBeeDevice);
    }

    @Override
    public void discoveryError(String s) {
        frame.add(new JLabel("Experienced an error: " + s));
        System.err.println(s);
    }

    @Override
    public void discoveryFinished(String s) {
        System.out.println("Discovery finished. Message: " + s);
        new NetworkView(network, localDevice);
    }
}
