import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class NetworkView {
    private JPanel panel;
    private JButton startConversationButton;
    private JTable table;
    private JLabel deviceIDLabel;

    public NetworkView(DigiMeshNetwork network, DigiMeshDevice device) {


        deviceIDLabel.setText(deviceIDLabel.getText().concat(device.getNodeID()));

        Object[][] tableData = new Object[network.getNumberOfDevices()][2];

        for (int i = 0; i < network.getNumberOfDevices(); i++) {
            tableData[i][0] = network.getDevices().get(i).getNodeID();
            tableData[i][1] = network.getDevices().get(i).get64BitAddress().toString();
        }

        String[] titles = {"ID", "64 Bit Address"};

        DefaultTableModel tableModel = new DefaultTableModel(tableData, titles);

        table.setModel(tableModel);


        JFrame frame = new JFrame("Network View");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        startConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConversationForm(
                        (RemoteDigiMeshDevice) network.getDevice(String.valueOf(table.getValueAt(
                                table.getSelectedRow(),1))), network);
            }
        });



    }
}
