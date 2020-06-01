import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.github.cliftonlabs.json_simple.JsonException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, JsonException, IOException {
        //TODO: 5/30/20 Check if folders exist before creating them
        String configfilesLocation = System.getProperty("user.dir");
        String generatorsLocation = configfilesLocation;
        String publickeysLocation = configfilesLocation;
        String privatekeysLocation = configfilesLocation;
        System.out.println(configfilesLocation);

        //TODO: 5/30/20 Check if static keys exist before creating them
        new GenKeys(0,"", configfilesLocation,generatorsLocation,publickeysLocation,privatekeysLocation);
        System.out.println("Keys generated.");

        //TEST
        new GetSharedSecret("",configfilesLocation+"/myprivatekey.pem",configfilesLocation+"/publickey2.pem");
        new CreateFile(configfilesLocation+"/sharedsecret");
        System.out.println(new FileToString().get(configfilesLocation+"/sharedsecret"));
        //TEST

        try {
            DigiMeshDevice localDevice = new DigiMeshDevice("/dev/ttyUSB0", 9600);
            // TODO: 5/28/20 Allow the user to change these values, such as a configuration (.properties) file

            localDevice.open();

            DigiMeshNetwork digiMeshNetwork = (DigiMeshNetwork) localDevice.getNetwork();
            digiMeshNetwork.addDiscoveryListener(new DiscoveryListener(digiMeshNetwork, localDevice));
            digiMeshNetwork.startDiscoveryProcess(); // Execution is handed off to the DiscoveryListener

        } catch (XBeeException e){
            System.err.println("An error has occurred. " + e.getMessage());
            if (e.getMessage().contains("Permission")){ // If the error is a permission denied error
                System.err.println("Make sure this user has permission to access the USB device file.");
                System.err.println("Fix: \"sudo chown -R <username> /dev/\"");
                // FIXME: 5/28/20 Running the above command may cause a security vulnerability. There is probably a
                // FIXME: 5/28/20 better, more concise command for doing this.
            }
        }


    }


}
