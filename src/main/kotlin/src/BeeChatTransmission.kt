package src

import com.digi.xbee.api.RemoteDigiMeshDevice
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.logging.Logger

class BeeChatTransmission(header: Int, data: ByteArray, val target: RemoteDigiMeshDevice) {

    val logger = Logger.getLogger("Transmission to $target")

    val byteArrays = ArrayList<ByteArray>()
    init {
        logger.info("Initializing...")
        var numberOfMessages = data.size / 70
        logger.info("Expecting to send ${numberOfMessages + 1} message(s)...")
        assert(numberOfMessages <= Byte.MAX_VALUE.toInt()) // TODO: 12/26/20 Add actual error handling

        val dataStream = ByteArrayInputStream(data)

        while (dataStream.available() > 0) {
            val message = ByteArrayOutputStream()
            message.write(header); message.write(numberOfMessages); numberOfMessages--
            message.writeBytes(dataStream.readNBytes(70))
            byteArrays.add(message.toByteArray())
        }
    }

    fun send() {
        for (message in byteArrays) {
            logger.info("Sending ${message.size} bytes to $target...")
            Resources.device.sendData(target, message)
        }
    }

}