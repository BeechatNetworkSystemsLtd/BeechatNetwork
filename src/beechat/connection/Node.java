package beechat.connection;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.exceptions.XBeeException;

import java.security.GeneralSecurityException;


/**
 * Abstract representation of a remote node and its information.
 */
public class Node {

    final private DigiMeshDevice LOCAL_DEVICE;
    final private RemoteDigiMeshDevice REMOTE_DEVICE;
    ChatForm chatForm;


    KeyHandler keyHandler;

    public DigiMeshDevice getLOCAL_DEVICE() {
        return LOCAL_DEVICE;
    }

    public RemoteDigiMeshDevice getREMOTE_DEVICE() {
        return REMOTE_DEVICE;
    }


    @Override
    public String toString() {
        return this.LOCAL_DEVICE.getNodeID() + " connected to " + this.REMOTE_DEVICE.getNodeID();
    }

    public Node(DigiMeshDevice localDevice, RemoteDigiMeshDevice remoteDevice) {
        this.LOCAL_DEVICE = localDevice;
        this.REMOTE_DEVICE = remoteDevice;
        this.chatForm = new ChatForm(this);
        this.keyHandler = new KeyHandler(this);


    }

    void receiveMessage(byte[] data) {

        try {
            byte[] decrypted = keyHandler.decrypt(data);
            this.chatForm.addMessage(this.REMOTE_DEVICE.getNodeID(), new String(decrypted));

        } catch (GeneralSecurityException e) {
            System.err.println("Warning: Could not decrypt message from " + REMOTE_DEVICE.getNodeID());
        }

    }

    void sendMessage(String message) {

        try {

            this.LOCAL_DEVICE.sendData(this.REMOTE_DEVICE, this.keyHandler.encrypt(message.getBytes()));

        } catch (GeneralSecurityException e) {
            System.err.println("Could not encrypt message: " + e.getMessage());
        } catch (XBeeException e) {
            System.err.println("Could not send message: " + e.getMessage());
        }
    }

}
