package src

import com.digi.xbee.api.RemoteDigiMeshDevice
import com.digi.xbee.api.listeners.IDataReceiveListener
import com.digi.xbee.api.models.XBeeMessage
import java.io.ByteArrayOutputStream
import java.util.logging.Logger

class Conversation(val device: RemoteDigiMeshDevice) { // TODO: 12/26/20 Document this 

    val logger = Logger.getLogger("Conversation with $device")

    /*
    TODO
    interface OnTextReceived
    interface OnFileReceived
    interface OnVoiceReceived
    or maybe a single generic interface which uses the header to handle data?
     */

    init {
        Resources.device.addDataListener { message -> // TODO: 12/28/20 Can probably be condensed down to a single, global listener
            if (message.device as RemoteDigiMeshDevice == device) {
                if (message.data[0].toInt() == 0) {
                    logger.info("Got key request, handling...")
                    receiveData(message)
                }
            }
        }
    }

    private fun receiveData(message: XBeeMessage) {
        assert(message.data[0].toInt() == 0 && message.data[1].toInt() == 0)

        val keySet = KeySet(); logger.info("Generated keyset, sending public key...")
        keySet.createSecret(message.data.copyOfRange(2, message.data.size))
        BeeChatTransmission(1, keySet.keyPair.public.encoded, device).send()

        val messageStream = ByteArrayOutputStream()
        Resources.device.addDataListener(object : IDataReceiveListener {
            override fun dataReceived(message: XBeeMessage) {
                if (message.device == device) {
                    logger.info("Writing bytes to buffer...")
                    messageStream.writeBytes(message.data.copyOfRange(2, message.data.size))

                    if (message.data[1].toInt() == 0) {
                        logger.info("Attempting to decrypt...")
                        val unencryptedData = keySet.decryptData(messageStream.toByteArray())
                        println(unencryptedData.decodeToString()) // TODO: 12/28/20 delet dis
                        // TODO: 12/28/20 Activate the interface
                        Resources.device.removeDataListener(this)
                    }
                }
            }
        }); logger.info("Added temporary listener...")


    }

    fun sendText(text: String, /* TODO: some interface maybe? */) {

        val keySet = KeySet(); logger.info("Generated keyset, sending public key...")
        BeeChatTransmission(0, keySet.keyPair.public.encoded, device).send()

        Resources.device.addDataListener(object : IDataReceiveListener {
            override fun dataReceived(message: XBeeMessage) {
                if (message.device as RemoteDigiMeshDevice == device) {
                    logger.info("Temporary listener received message...")

                    assert(message.data[0].toInt() == 1 && message.data[1].toInt() == 0)

                    keySet.createSecret(message.data.copyOfRange(2, message.data.size))
                    val encryptedData = keySet.encryptData(text.encodeToByteArray())
                    logger.info("Have encrypted data, preparing to send!")
                    BeeChatTransmission(2, encryptedData, device).send(); logger.info("Sending encrypted data...")

                    logger.info("Done! Removing temporary listener...");//Resources.device.removeDataListener(this)
                }
            }
        }); logger.info("Added temporary listener for text send action...")
    }

}

