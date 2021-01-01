/*
 * This file is part of BeeChat.
 *
 *     BeeChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BeeChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BeeChat.  If not, see <https://www.gnu.org/licenses/>.
 */



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