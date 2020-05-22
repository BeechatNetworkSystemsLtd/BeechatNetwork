package listeners;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import util.Constants;
import javax.swing.JOptionPane;

/**
 * Custom discovery listener. Use addDiscoveryListener(new listeners.BCDiscoveryListener) whenever setting up a new network.
 */
public class BCDiscoveryListener implements IDiscoveryListener {

    @Override
    public void deviceDiscovered(RemoteXBeeDevice remoteXBeeDevice) {
        System.out.println("Device discovered! " + remoteXBeeDevice.getNodeID());
        System.out.println();

    }

    @Override
    public void discoveryError(String s) {
        String errorMessage = "An error has occurred while performing network discovery: " + s;
        JOptionPane.showMessageDialog(null, errorMessage);
        System.out.println(errorMessage); // Execution will continue, but there may be errors
    }

    @Override
    public void discoveryFinished(String s) {
        String errorMessage = "Network discovery has finished with an error: " + s;

        if (s == null){
            JOptionPane.showMessageDialog(null, "Network discovery complete!");
        } else {
            JOptionPane.showMessageDialog(null, errorMessage);
        }

        Constants.menu.setNetworkInformationTable(Constants.digiMeshNetwork);
        Constants.menu.setStatusLabel(Constants.digiMeshNetwork.getNumberOfDevices() + " devices discovered.");

    }
}
