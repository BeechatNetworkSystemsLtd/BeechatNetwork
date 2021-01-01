package src

import com.digi.xbee.api.DigiMeshDevice
import com.digi.xbee.api.DigiMeshNetwork
import com.digi.xbee.api.RemoteDigiMeshDevice
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

object Resources {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    lateinit var device: DigiMeshDevice
    lateinit var network: DigiMeshNetwork

    val conversations = HashMap<RemoteDigiMeshDevice, Conversation>()

    fun updateConversations() {
        conversations.clear()
        for (device in network.devices) {
            conversations[device as RemoteDigiMeshDevice] = Conversation(device as RemoteDigiMeshDevice)
        }
    }

}