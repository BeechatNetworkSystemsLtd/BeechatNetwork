package com.beechat.beechatnetwork;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBeeMessage;
import com.github.cliftonlabs.json_simple.JsonException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * This class forms the UI which is used to control BeeChat, as well as handling
 * all related operations. The interface looks and operates similarly to a
 * traditional internet message (IM) direct chat.
 */
public class ConversationForm {
    // TODO: 5/28/20 Encryption, key exchanges, etc
    // STOPSHIP: 5/28/20 This class does basically nothing at this point, until this
    // program can actually communicate.

    DigiMeshDevice localDevice;
    RemoteDigiMeshDevice remoteDevice;
    DigiMeshNetwork network;

    /**
     * The list of messages with the connected node.
     */
    HashMap<LocalTime, XBeeMessage> messages = new HashMap<LocalTime, XBeeMessage>();

    private JTextArea textArea;
    private JPanel panel;
    private JTextField textField;
    private JButton sendButton;

    /**
     * Create the form and display it.
     * 
     * @param remoteDevice device this instance is communicating with
     * @param network      the network this local node is connected to
     */
    ConversationForm(RemoteDigiMeshDevice remoteDevice, DigiMeshNetwork network, DigiMeshDevice localDevice) {

        this.remoteDevice = remoteDevice;
        this.network = network;
        this.localDevice = localDevice;

        DataReceiveListener listener = new DataReceiveListener(network, localDevice, this);
        localDevice.addDataListener(listener);

        textArea.setText(
                "Starting conversation with " + remoteDevice.getNodeID() + " at " + LocalTime.now().toString());

        // Set up and display the GUI.
        JFrame frame = new JFrame("Conversation with " + remoteDevice.getNodeID());
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 275);
        frame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            /**
             * Send the user's message contained in the text field to the remote user.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // FIXME: 5/28/20 TimeoutException whenever trying to send a long string
                // sendMessage(textField.getText());
                LocalTime time = LocalTime.now();
                if (textField.getText().contains("sendmyinfo")) {
                    try {
                        // new AddContact(remoteDevice.getNodeID(),textField.getText().substring
                        // (textField.getText().lastIndexOf("sendmyinfo")+10,textField.getText().length()),
                        // "","");

                        new SendMyInfo("", remoteDevice.getNodeID(),
                                new FileToString().get(System.getProperty("user.dir") + "/mypublickey.pem"),
                                new FileToString().get(System.getProperty("user.dir") + "/mygenerator.pem"),
                                remoteDevice, network, localDevice);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else {
                    new SendMessage(textField.getText(), remoteDevice, network, localDevice,
                            System.getProperty("user.dir"));
                    textArea.append("\n<<" + time.toString() + ">>: " + textField.getText());

                }

                textField.setText("");
            }
        });
    }

    /**
     * STANDARD Send a message to the remote user, and display it on the text area.
     * An XBeeMessage is constructed from the message string and the remote node
     * associated with this class. It is then added to the messages HashMap.
     * 
     * @param message the text to send
     * @see XBeeMessage
     */
    void sendMessage(String message) throws XBeeException {
        XBeeMessage xBeeMessage = new XBeeMessage(remoteDevice, message.getBytes());

        LocalTime time = LocalTime.now();
        messages.put(time, xBeeMessage);
        localDevice.sendData(xBeeMessage.getDevice(), xBeeMessage.getData()); // TODO: 5/28/20 Encrypt this before
                                                                              // sending

        textArea.append("\n<<" + time.toString() + ">>: " + message);
    }

    /**
     * Send an already constructed XBeeMessage to the remote node.
     * 
     * @param message the message to send.
     * @see XBeeMessage
     */
    void sendMessage(XBeeMessage message) {
        LocalTime time = LocalTime.now();
        messages.put(time, message);

        textArea.append("\n<<" + time.toString() + ">>: " + message.getDataString());
    }

    // Is this used at all?
    /**
     * When a message is received, it is sent to this method. It is then added to
     * the list of messages and displayed on the message list. FIXME: This should
     * probably be called by an IDataReceiveListener, right?
     * 
     * @param message the message to process
     * @see XBeeMessage
     */
    void receiveMessage(String message) {
        LocalTime time = LocalTime.now();
        // messages.put(time, message);
        // TODO: Getting an error above

        textArea.append("\n[[" + time.toString() + "]]: " + message);
    }

    // TODO: 5/28/20 Key exchange method and stuff?

}
