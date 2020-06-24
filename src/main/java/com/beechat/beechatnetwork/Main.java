package com.beechat.beechatnetwork;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, JsonException, IOException {
        // TODO: 5/30/20 Check if folders exist before creating them
        String configfilesLocation = System.getProperty("user.dir");
        String generatorsLocation = configfilesLocation;
        String publickeysLocation = configfilesLocation;
        String privatekeysLocation = configfilesLocation;
        System.out.println(configfilesLocation);

        if (new FileExists().getBoolean(configfilesLocation + "/contacts.json").equals(false)) {
            new CreateFile(configfilesLocation + "/contacts.json", false);
        }

        if (new FileExists().getBoolean(configfilesLocation + "/mygenerator.pem").equals(false)
                && new FileExists().getBoolean(configfilesLocation + "/myrivatekey.pem").equals(false)
                && new FileExists().getBoolean(configfilesLocation + "/mypublickey.pem").equals(false)) {
            new GenKeys(0, "", configfilesLocation, generatorsLocation, publickeysLocation, privatekeysLocation);
            System.out.println("Keys generated.");
        }

        try {
            DigiMeshDevice localDevice = new DigiMeshDevice("/dev/ttyUSB0", 9600);
            // TODO: 5/28/20 Allow the user to change these values, such as a configuration
            // (.properties) file

            localDevice.open();

            DigiMeshNetwork digiMeshNetwork = (DigiMeshNetwork) localDevice.getNetwork();
            digiMeshNetwork.addDiscoveryListener(new DiscoveryListener(digiMeshNetwork, localDevice));
            digiMeshNetwork.startDiscoveryProcess(); // Execution is handed off to the DiscoveryListener

            // System.out.println("Starting DataListener.");
            // DataReceiveListener listener = new DataReceiveListener((DigiMeshNetwork)
            // localDevice.getNetwork(),localDevice, null);
            // localDevice.addDataListener(listener);
            // System.out.println("Started DataListener.");

        } catch (XBeeException e) {
            System.err.println("An error has occurred. " + e.getMessage());
            if (e.getMessage().contains("Permission")) { // If the error is a permission denied error
                System.err.println("Make sure this user has permission to access the USB device file.");
                System.err.println("Fix: \"sudo chown -R <username> /dev/\"");
                // FIXME: 5/28/20 Running the above command may cause a security vulnerability.
                // There is probably a
                // FIXME: 5/28/20 better, more concise command for doing this.
            }
        }

    }

}
