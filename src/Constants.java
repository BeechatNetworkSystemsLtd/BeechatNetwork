
import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;

import javax.swing.*;
import java.io.FileInputStream;
import java.util.Properties;


public class Constants {

    // Default values until properties file is read and constants are established.
    // TODO: 5/15/20 Do this with Properties.default methods, not manually

    private static String port = "/dev/ttyUSB0";
    private static int baud_Rate = 9600;
    private static int discovery_timeout = 10;


    static {
        try {
            // Load the properties file.
            Properties properties = new Properties();
            properties.load(new FileInputStream("configuration.properties"));

            // Replace the default values with the values located in the properties file
            port = properties.getProperty("port");
            baud_Rate = Integer.parseInt(properties.getProperty("baud_rate"));
            discovery_timeout = Integer.parseInt(properties.getProperty("discovery_timeout"));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to read configuration file. \n" +
                            "Ensure that \"configuration.properties\" is present in the same directory (folder) as " +
                            "\"BeeChatNetwork.jar\", and is properly formatted." +
                            "\nProceeding with default values:" +
                            "\nPort: " + port +
                            "\nBaud rate: " + baud_Rate +
                            "\nDiscovery timeout: " + discovery_timeout +
                            "\n\nError message: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();

            // Execution can usually continue normally using default values
        }
    }

    // TODO: 5/19/20 Use properties-default system/methods rather than this. (see line 17)

    /**
     * Port where the XBee device is located
     *
     */
    public static final String PORT = port;


    /**
     * Baud rate to be used for both input and output.
     *
     */
    public static final int BAUD_RATE = baud_Rate;

    /**
     * Discovery timeout. Determines how long the network discovery process should last.
     * @see BCDiscoveryListener
     */
    public static final int DISCOVERY_TIMEOUT = discovery_timeout;



    public static XBeeDevice device;

    static {
        device = new DigiMeshDevice(PORT, BAUD_RATE);
        try {
            device.open();

        } catch (XBeeException e) {

            JOptionPane.showMessageDialog(null, "An exception has occurred while opening " +
                            "connection interface with local device.\n" + e.getMessage() + "\nDo you have permission" +
                    " to access " + port + "?\nIs your module connected?\nIs your module properly configured?",
                    "Error", JOptionPane.WARNING_MESSAGE);

            e.printStackTrace();
            System.exit(1);
        }
    }

    public static XBeeNetwork digiMeshNetwork = device.getNetwork();

    public static Menu menu = new Menu();

}
