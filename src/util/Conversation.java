package util;

import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.models.XBeeMessage;

import java.util.ArrayList;

/**
 * This class represents all communications with a specific node.
 */
public class Conversation {
    RemoteDigiMeshDevice remoteDevice;
    ArrayList<XBeeMessage> messages = new ArrayList<XBeeMessage>();




}
