package com.beechat.beechatnetwork;

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
    // TODO: 5/28/20 Add a countdown to show how many seconds are left until
    // discovery is finished

    /**
     * A discovery listener. Use this on a DigiMeshNetwork to discover any DigiMesh
     * devices within range.
     * 
     * @param network     network to perform discovery on.
     * @param localDevice local DigiMesh device
     * @see IDiscoveryListener
     */
    DiscoveryListener(DigiMeshNetwork network, DigiMeshDevice localDevice) {
        this.network = network;
        this.localDevice = localDevice;

        System.out.println("Starting discovery process, please wait...");

        // Set up a GUI panel to inform the user of the discovery process
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(300, 400));
        frame.setBackground(Color.black);
        frame.setForeground(Color.white);
        frame.add(new JLabel("Discovering devices in network.\nPlease wait..."), BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

    }

    /**
     * Is called when a device is discovered. Adds the device to the local network,
     * and notifies the user.
     * 
     * @param remoteXBeeDevice
     */
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

        if (s == null) {
            JOptionPane.showMessageDialog(null, "Done! " + network.getNumberOfDevices() + " devices found.",
                    "Discovery finished", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Done! " + network.getNumberOfDevices() + " devices found.\n" + s,
                    "Discovery Finished", JOptionPane.INFORMATION_MESSAGE);
        }

        new NetworkView(network, localDevice);

        frame.dispose();

    }
}
