import listeners.BCDiscoveryListener;
import util.Constants;

public class Main extends Constants {

    public static void main(String[] args){

        network.addDiscoveryListener(new BCDiscoveryListener()); // Add the custom discovery listener

        network.startDiscoveryProcess();
        menu.setStatusLabel("Discovering devices, please wait...");

        menu.showWindow(); // Execution is passed over to the window class

    }

}
