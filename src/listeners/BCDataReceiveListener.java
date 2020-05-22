package listeners;

import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

import java.util.Arrays;

public class BCDataReceiveListener implements IDataReceiveListener {
    @Override
    public void dataReceived(XBeeMessage xBeeMessage) {
        System.out.println("Message received from " + xBeeMessage.getDevice().getNodeID() + ": "
                + Arrays.toString(xBeeMessage.getData()));


    }

}
