package listeners;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import util.Constants;

import javax.swing.JOptionPane;

/**
 * Custom discovery listener. Use addDiscoveryListener(new listeners.BCDiscoveryListener) whenever setting up a new network.
 */
public class BCDiscoveryListener extends Constants implements IDiscoveryListener {

    @Override
    public void deviceDiscovered(RemoteXBeeDevice remoteXBeeDevice) {
        System.out.println(timestamp() + "discovered device " + remoteXBeeDevice.getNodeID());

    }




    @Override
    public void discoveryError(String s) {
        String errorMessage = "An error has occurred while performing network discovery: " + s;
        JOptionPane.showMessageDialog(null, errorMessage);
        System.err.println(timestamp() + errorMessage); // Execution will usually continue, but there may be errors
    }




    @Override
    public void discoveryFinished(String s) {
        String errorMessage = "Network discovery has finished with an error: " + s;

        if (s == null){
            // Discovery was successful and without errors
            JOptionPane.showMessageDialog(null, "Network discovery complete!");

            System.out.println(timestamp() + "Discovery process finished. " +
                    network.getNumberOfDevices() + " devices discovered.");

        } else {
            // Discovery experienced an error
            JOptionPane.showMessageDialog(null, errorMessage);

            System.err.println(timestamp() + "Discovery process finished with an error: " + s +
                    network.getNumberOfDevices() + " devices discovered.");
        }

        menu.setNetworkInformationTable(Constants.network);
        menu.setStatusLabel(Constants.network.getNumberOfDevices() + " devices discovered.");

    }
}
