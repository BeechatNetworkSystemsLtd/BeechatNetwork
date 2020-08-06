package beechat;

import beechat.connection.BackgroundDataListener;
import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import beechat.connection.Node;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    /**
     * A list of the devices with which this node has accepted a beechat.connection to.
     * @see BackgroundDataListener
     */
    public static HashMap<XBee64BitAddress, Node> knownDevices = new HashMap<>();



    public static void main(String[] args) throws XBeeException {

        Security.addProvider(new BouncyCastleProvider());

        // Standard discovery listener.
        class DiscoveryListener implements IDiscoveryListener {

            final DigiMeshDevice localDevice;

            DiscoveryListener(DigiMeshDevice localDevice) {
                this.localDevice = localDevice;
            }

            @Override
            public void deviceDiscovered(RemoteXBeeDevice remoteXBeeDevice) {
                System.out.println("Discovered " + remoteXBeeDevice);

            }

            @Override
            public void discoveryError(String s) {
                System.err.println("Error during network discovery: " + s);
            }

            /*
            Once the discovery process is finished, display the results to the user.
            Additionally, bring up the NetworkView, and add the beechat.connection.BackgroundDataListener to listen for
            new connection requests.
             */
            @Override
            public void discoveryFinished(String s) {

                if (s == null) {
                    System.out.println("Network discovery finished with no messages.");
                } else {
                    System.out.println("Network discovery finished. Message: " + s);
                }


                localDevice.addDataListener(new BackgroundDataListener(localDevice));
                new NetworkView(localDevice);

            }
        }

        DigiMeshDevice localDevice = new DigiMeshDevice(args[0], 9600);
        localDevice.open();

        DigiMeshNetwork network = (DigiMeshNetwork) localDevice.getNetwork();
        network.addDiscoveryListener(new DiscoveryListener(localDevice));
        network.startDiscoveryProcess();

    }

}
