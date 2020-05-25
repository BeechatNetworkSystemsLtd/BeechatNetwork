package listeners;

import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import util.Constants;

public class BCDataReceiveListener extends Constants implements IDataReceiveListener {
    @Override
    public void dataReceived(XBeeMessage xBeeMessage) {
        System.out.println(timestamp() + " received message from " + xBeeMessage.getDevice().getNodeID() + ": "
        + xBeeMessage.getDataString());

        // What should happen now?

    }

}
