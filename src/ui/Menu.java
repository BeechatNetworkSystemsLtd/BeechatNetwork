package ui;

import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeNetwork;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Menu {
    private JTable messagesTable;
    private JTextField messageField;
    private JButton sendButton;
    private JTable networkInformationTable;
    private JPanel mainPanel;
    private JScrollPane messagesPane;
    private JLabel statusLabel;
    private JButton button1;
    private JButton sendFileButton;

    public Menu() {

        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {




            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = fileChooser.showOpenDialog(null);
                
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                }

                // TODO: 5/23/20 Send selectedFile to the active node 
                
            }
        });
    }

    /**
     * Set up the window.
     */
    public void showWindow(){
        JFrame frame = new JFrame("BeeChat");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Set the status label located at the top-right of the panel.
     * @param text Text to be displayed
     */
    public void setStatusLabel(String text){
        statusLabel.setText(text);
    }


    /**
     * Set the information to display in the right-side table (NetworkInformationTable).
     * @param networkInformation the XBeeNetwork whose information is to be displayed
     */
    public void setNetworkInformationTable(XBeeNetwork networkInformation){
        // FIXME: 5/19/20 Currently only node IDs are displayed, need to add more.
        String[] titles = {"ID"};

        List<RemoteXBeeDevice> devices = networkInformation.getDevices();

        String[][] data = new String[1][devices.size()]; // JTables only support primitive arrays

        for (int i = 0; i < devices.size() ; i++) { // Iterate through the List and add information to the array
            data[0][i] = devices.get(i).getNodeID();
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, titles); // Create a new TableModel with the information
        networkInformationTable.setModel(tableModel); // Set the table's model to the new model above

    }


}
