import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.models.XBeeMessage;

import java.util.ArrayList;

/**
 * Class representing a conversation with an individual node (user) on the network.
 * Exchanged information persists within memory until the program exits.
 * Only one instance of this class should exist for each node on the network.
 */
public class Conversation {
    // STOPSHIP: 5/19/20 Finish everything else to set up a connection, then implement this!
    // TODO: 5/19/20 Add ability for user to manually clear cache of messages/data, to save memory

    RemoteDigiMeshDevice device;

    /**
     * Messages from the local user are stored in dimension 0. Messages received from the remote user are stored in
     * dimension 1.
     */
    ArrayList<XBeeMessage>[] messages = new ArrayList[1];

    /**
     * Engage a new conversation with a remote user.
     * @param remoteDevice The remote node (user) to engage the conversation with.
     */
    Conversation(RemoteDigiMeshDevice remoteDevice){

    }


}
