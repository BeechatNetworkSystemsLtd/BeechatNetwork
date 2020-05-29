import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import java.security.*;


public class Main {

    public static void main(String[] args) throws XBeeException {

        DigiMeshDevice localDevice = new DigiMeshDevice("/dev/ttyUSB0", 9600);
        localDevice.open();

        DigiMeshNetwork digiMeshNetwork = (DigiMeshNetwork) localDevice.getNetwork();
        digiMeshNetwork.addDiscoveryListener(new DiscoveryListener(digiMeshNetwork, localDevice));
        digiMeshNetwork.startDiscoveryProcess();



    }


}
