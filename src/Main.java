
public class Main extends Constants {

    public static void main(String[] args){

        digiMeshNetwork.addDiscoveryListener(new BCDiscoveryListener()); // Add the custom discovery listener

        digiMeshNetwork.startDiscoveryProcess();
        menu.setStatusLabel("Discovering devices, please wait...");

        menu.showWindow(); // Execution is passed over to the window class
        // STOPSHIP: 5/19/20

    }

}
