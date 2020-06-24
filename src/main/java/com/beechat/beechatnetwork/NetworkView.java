package com.beechat.beechatnetwork;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * A UI view of the local network. Users can view all the nodes on the network,
 * as well as initiate conversation with them.
 */
public class NetworkView {
    private JPanel panel;
    private JButton startConversationButton;
    private JTable table;
    private JLabel deviceIDLabel;

    /**
     * Construct and display a new NetworkView. There should only be one NetworkView
     * active at a time.
     * 
     * @param network the local network
     * @param device  the local device
     */
    public NetworkView(DigiMeshNetwork network, DigiMeshDevice device) {

        deviceIDLabel.setText(deviceIDLabel.getText().concat(device.getNodeID()));

        Object[][] tableData = new Object[network.getNumberOfDevices()][2]; // JTable only supports primitive arrays.
                                                                            // There is probably a better way of doing
                                                                            // this but... :)

        for (int i = 0; i < network.getNumberOfDevices(); i++) {
            tableData[i][0] = network.getDevices().get(i).getNodeID();
            tableData[i][1] = network.getDevices().get(i).get64BitAddress().toString();
        }

        String[] titles = { "ID", "64 Bit Address" };

        DefaultTableModel tableModel = new DefaultTableModel(tableData, titles);
        table.setModel(tableModel);

        // Construct the GUI frame
        JFrame frame = new JFrame("Network View");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        startConversationButton.addActionListener(new ActionListener() {
            /**
             * Start a new conversation with the node selected from the table. TODO: Check
             * if a conversation is already actively with the node, and if so; notify the
             * user and do not open another ConversationForm window.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConversationForm(
                        (RemoteDigiMeshDevice) network
                                .getDevice(String.valueOf(table.getValueAt(table.getSelectedRow(), 0))),
                        network, device);
            }
        });

    }

}
