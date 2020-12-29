import com.digi.xbee.api.DigiMeshDevice
import com.digi.xbee.api.DigiMeshNetwork
import com.digi.xbee.api.RemoteXBeeDevice
import com.digi.xbee.api.listeners.IDiscoveryListener
import src.Resources


fun main(args: Array<String>) {
    print("Device: /dev/ttyUSB");
    val number = readLine()
    Resources.device = DigiMeshDevice("/dev/ttyUSB$number", 230400)
    Resources.device.open()
    Resources.network = Resources.device.network as DigiMeshNetwork

    Resources.network.addDiscoveryListener(object : IDiscoveryListener {
        override fun deviceDiscovered(device: RemoteXBeeDevice) {
            println("Discovered $device")
        }
        override fun discoveryError(error: String) {
            System.err.println("Error during discovery: $error")
        }
        override fun discoveryFinished(message: String?) {
            println("Discovery finished. Message: $message")
            Resources.updateConversations()
            val target = Resources.network.devices[0]
            print("Message to send to ${target.nodeID}: ")
            val text = readLine()!!
            Resources.conversations[target]!!.sendText(text)
        }
    })
    Resources.network.startDiscoveryProcess()
}
