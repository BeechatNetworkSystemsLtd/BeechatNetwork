import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;



public class DataReceiveListener implements IDataReceiveListener {

    DigiMeshNetwork network;
    DigiMeshDevice localDevice;
    ConversationForm conversationForm;


    /**
     * A data listener. Use this on a DigiMesh Device to listen for incoming data within range.
     * @param network network to perform discovery on.
     * @param localDevice local DigiMesh device
     * @see IDataReceiveListener
     */
    DataReceiveListener(DigiMeshNetwork network, DigiMeshDevice localDevice, ConversationForm conversationForm){
        this.network = network;
        this.localDevice = localDevice;
        this.conversationForm = conversationForm;
    }
    @Override
    public void dataReceived(XBeeMessage xBeeMessage) {
        System.out.println(xBeeMessage.getDataString());
        conversationForm.receiveMessage(xBeeMessage);
    }
}
