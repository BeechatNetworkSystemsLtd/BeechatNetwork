package beechat;

import beechat.Main;
import com.digi.xbee.api.*;
import beechat.connection.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * A UI view of the local network.
 * Users can view all the nodes on the network, as well as initiate conversation with them.
 */
public class NetworkView {
    private JPanel panel;
    private JButton startConversationButton;
    private JLabel deviceIDLabel;
    private JList<RemoteDigiMeshDevice> list;
    private JFrame frame;

    /**
     * Create and display a new NetworkView.
     * @param localDevice the local device in use
     */
    NetworkView(DigiMeshDevice localDevice) {

        DigiMeshNetwork network = (DigiMeshNetwork) localDevice.getNetwork();

        // Set up UI elements
        Vector<RemoteDigiMeshDevice> listData = new Vector<>();
        for (RemoteXBeeDevice device : network.getDevices()) {
            listData.add((RemoteDigiMeshDevice) device);
        }
        list.setListData(listData);

        deviceIDLabel.setText("You are: " + localDevice.getNodeID() + "(" + localDevice.get64BitAddress() + ")");

        // When pressed, initiate a key exchange and start a conversation with the selected node.
        startConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (list.isSelectionEmpty()) { // If the user forgot to select a remote node, notify them and do nothing.
                    JOptionPane.showMessageDialog(null,
                            "Please select a remote node from the list, " +
                                    "or await a beechat.connection from another user.",
                            "No user selected", JOptionPane.INFORMATION_MESSAGE);

                } else { // If the user has selected a node from the list
                    RemoteDigiMeshDevice selectedDevice = list.getSelectedValue();
                    System.out.println("Selected " + selectedDevice + ", attempting beechat.connection...");

                    Main.knownDevices.put(selectedDevice.get64BitAddress(),
                            new Node(localDevice, selectedDevice));
                }

            }
        });


        // Add the UI elements to the frame and display it
        frame = new JFrame("BeeChat Network");
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}
