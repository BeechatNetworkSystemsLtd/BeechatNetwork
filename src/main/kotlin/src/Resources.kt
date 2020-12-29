package src

import com.digi.xbee.api.DigiMeshDevice
import com.digi.xbee.api.DigiMeshNetwork
import com.digi.xbee.api.RemoteDigiMeshDevice
import com.digi.xbee.api.RemoteXBeeDevice
import com.digi.xbee.api.listeners.IDataReceiveListener
import com.digi.xbee.api.listeners.IDiscoveryListener
import com.digi.xbee.api.models.XBeeMessage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Security
import java.security.spec.X509EncodedKeySpec
import javax.swing.JFrame

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