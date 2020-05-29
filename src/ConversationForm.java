import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.models.XBeeMessage;

import javax.swing.*;
import java.util.ArrayList;

public class ConversationForm {

    RemoteDigiMeshDevice remoteDevice;
    DigiMeshNetwork network;

    ArrayList<XBeeMessage> messages = new ArrayList<XBeeMessage>();
    private JTextArea textArea;
    private JPanel panel;
    private JTextField textField;
    private JButton Send;

    ConversationForm(RemoteDigiMeshDevice remoteDevice, DigiMeshNetwork network){
        this.remoteDevice = remoteDevice;
        this. network = network;



    }

}
