import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;

import javax.swing.*;
import java.awt.*;

public class DiscoveryListener implements IDiscoveryListener {

    DigiMeshNetwork network;
    DigiMeshDevice localDevice;

    JFrame frame = new JFrame("Please wait...");

    DiscoveryListener(DigiMeshNetwork network, DigiMeshDevice localDevice){
        this.network = network;
        this.localDevice = localDevice;

        System.out.println("Starting discovery process, please wait...");

        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(300,400));
        frame.add(new JLabel("Discovering devices in network.\nThis should only take a moment."),
                BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

    }

    @Override
    public void deviceDiscovered(RemoteXBeeDevice remoteXBeeDevice) {
        System.out.println("Discovered device " + remoteXBeeDevice.getNodeID());
        frame.add(new JLabel("Discovered device: " + remoteXBeeDevice.getNodeID()));
        frame.pack();
        network.addRemoteDevice(remoteXBeeDevice);
    }

    @Override
    public void discoveryError(String s) {
        frame.add(new JLabel("Experienced an error: " + s));
        frame.pack();
        System.err.println(s);
    }

    @Override
    public void discoveryFinished(String s) {

        frame.dispose();

        if (s == null) {
            JOptionPane.showMessageDialog(null, "Done! " + network.getNumberOfDevices() +
                    " devices found.", "Discovery finished", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Done! " + network.getNumberOfDevices() +
                    " devices found.\n" + s, "Discovery Finished", JOptionPane.INFORMATION_MESSAGE);
        }

        new NetworkView(network, localDevice);
    }
}
